package com.elitecore.core.imdg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.imdg.autogen.IMDG_MIBOidTable;
import com.elitecore.core.imdg.config.IMDGConfiguration;
import com.elitecore.core.imdg.impl.IMDG_MIBImpl;
import com.elitecore.core.imdg.impl.TableMapStatisticsTableImpl;
import com.elitecore.core.imdg.impl.TableMembersDetailTableImpl;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.cli.cmd.ImdgDetailProvider;
import com.elitecore.core.util.cli.cmd.ImdgMapStatisticsDetailProvider;
import com.elitecore.core.util.cli.cmd.ImdgStatisticsProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.cli.cmd.ShowCommand;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MemberAttributeConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.sun.management.snmp.SnmpStatusException;

/**
 * 
 * @author malav
 *
 */
public class HazelcastImdgInstance {


	private static final String MODULE = "IMDG";
	public static final String MEMBER_NAME = "memberName";

	private final IMDGConfiguration configuration;
	private final Config config;
	private final List<EventListner> listeners;
	private final String serverInstanceName;
	private final Map<String, String> defaultProperties;

	private HazelcastInstance hazelcastInstance;

	private IMDG_MIBImpl imdgMIBImpl;

	private  ServerContext context;

	private ClusterMembershipListener clusterMembershipListener;
	private NodeLifeCycleListener nodeLifeCycleListener;
	private ClusterMigrationListener clusterMigrationListener;

	public HazelcastImdgInstance(ServerContext context, IMDGConfiguration configuration, String serverInstanceName) {
		this.context = context;
		this.configuration = configuration;
		this.serverInstanceName = serverInstanceName;
		this.listeners = new ArrayList<EventListner>();
		this.config = new Config();

		this.nodeLifeCycleListener = new NodeLifeCycleListener(context);
		this.config.addListenerConfig(new ListenerConfig(nodeLifeCycleListener));

		this.clusterMembershipListener = new ClusterMembershipListener(context);
		this.config.addListenerConfig(new ListenerConfig(clusterMembershipListener));

		this.clusterMigrationListener = new ClusterMigrationListener(context);
		this.config.addListenerConfig(new ListenerConfig(clusterMigrationListener) );

		this.defaultProperties = new HashMap<String, String>(5);
		this.defaultProperties.put("hazelcast.phone.home.enabled", "false");
		this.defaultProperties.put("hazelcast.slow.operation.detector.stacktrace.logging.enabled", "true");
		this.defaultProperties.put("hazelcast.slow.operation.detector.threshold.millis", "5000");
		this.defaultProperties.put("hazelcast.jmx", "true");
	}

	public void start() throws ImdgInstanceFailedException {
		try {
			config.setInstanceName(serverInstanceName);
			
			MemberAttributeConfig memberAttributeConfig = new MemberAttributeConfig();
			memberAttributeConfig.setStringAttribute(MEMBER_NAME, serverInstanceName);
			config.setMemberAttributeConfig(memberAttributeConfig);

			
			String ownHost = configuration.getMemberData().get(serverInstanceName);
			config.getNetworkConfig().getInterfaces().setEnabled(true);
			config.getNetworkConfig().getInterfaces().addInterface(ownHost);

			GroupConfig groupConfig = config.getGroupConfig();
			groupConfig.setName(configuration.getGroupName());
			groupConfig.setPassword(configuration.getPassword());

			Properties properties = configuration.getProperties();
			for (Entry<String, String> entry : this.defaultProperties.entrySet()) {
				if (properties.containsKey(entry.getKey()) == false) {
					properties.put(entry.getKey(), entry.getValue());
				}
			}
			config.setProperties(properties);

			ManagementCenterConfig managementCenterConfig = config.getManagementCenterConfig();
			managementCenterConfig.setEnabled(configuration.isMancenterEnabled());
			managementCenterConfig.setUrl(configuration.getUrl());

			config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
			config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);

			TcpIpConfig tcpIpConfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
			tcpIpConfig.setEnabled(true);
			tcpIpConfig.setMembers(configuration.getMemberIps());

			config.getNetworkConfig().setPortCount(configuration.getPortCount());
			config.getNetworkConfig().setPortAutoIncrement(true);
			config.getNetworkConfig().setPort(configuration.getStartPort());
			config.getNetworkConfig().setOutboundPortDefinitions(configuration.getOutBoundPorts());

			InMemoryFormat inMemoryFormat = getInMemoryFormat();
			setFormatToMap(inMemoryFormat);
			setFormatToReplicatedMap(inMemoryFormat);
			setFormatToRingBuffer(inMemoryFormat);

			hazelcastInstance = Hazelcast.newHazelcastInstance(config);

			generateStartEvent();
			registerImdgCommand();
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "IMDG succesfully started");
			}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "IMDG instance can not be started. Reason:" + e.getMessage());
			throw new ImdgInstanceFailedException(e);

		}
	}

	private void registerImdgCommand() {
		ImdgMapStatisticsDetailProvider mapststisticsDetailProvider = new ImdgMapStatisticsDetailProvider();
		registerMapDetailProvider(mapststisticsDetailProvider);
		ImdgStatisticsProvider imdgStatisticsProvider = new ImdgStatisticsProvider();

		ImdgDetailProvider imdgDetailProvider = new ImdgDetailProvider();
		try {
			imdgStatisticsProvider.registerDetailProvider(mapststisticsDetailProvider);
			imdgDetailProvider.registerDetailProvider(imdgStatisticsProvider);
			ShowCommand.registerDetailProvider(imdgDetailProvider);
		} catch (RegistrationFailedException e) {
			LogManager.getLogger().error(MODULE, e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	private void registerMapDetailProvider(ImdgMapStatisticsDetailProvider mapststisticsDetailProvider) {
		Collection<String> values = hazelcastInstance.getConfig().getMapConfigs().keySet();
		for(String mapName : values) {
			mapststisticsDetailProvider.registerMapDetailProvider(hazelcastInstance.getMap(mapName));
		}
	}

	public void initIMGDMIB() {
		try {
			imdgMIBImpl = new IMDG_MIBImpl(hazelcastInstance.getCluster() , configuration.getGroupName());
			imdgMIBImpl.init();
			context.getSNMPAgent().registerMib(imdgMIBImpl);
		} catch (IllegalAccessException e) {
			LogManager.getLogger().error(MODULE, "Failed to initialize IMDG_MIB, Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	private void setFormatToRingBuffer(InMemoryFormat inMemoryFormat) {
		Collection<RingbufferConfig> values = config.getRingbufferConfigs().values();
		for (RingbufferConfig ringbufferConfig : values) {
			ringbufferConfig.setInMemoryFormat(inMemoryFormat);
		}
	}

	private void setFormatToReplicatedMap(InMemoryFormat inMemoryFormat) {
		Collection<ReplicatedMapConfig> replicatedMaps = config.getReplicatedMapConfigs().values();
		for (ReplicatedMapConfig mapConfig : replicatedMaps) {
			mapConfig.setInMemoryFormat(inMemoryFormat);
		}
	}

	private void setFormatToMap(InMemoryFormat inMemoryFormat) {
		Collection<MapConfig> mapConfigs = config.getMapConfigs().values();
		for (MapConfig mapConfig : mapConfigs) {
			mapConfig.setInMemoryFormat(inMemoryFormat);
		}
	}

	private InMemoryFormat getInMemoryFormat() {
		InMemoryFormat inMemoryFormat = InMemoryFormat.valueOf(configuration.getInMemoryFormat());
		if (inMemoryFormat == null) {
			LogManager.getLogger().warn(MODULE, "Setting default in-memory-format(BINARY), Reason: in-memory-format(" + configuration.getInMemoryFormat() + ") is not valid");
			inMemoryFormat = InMemoryFormat.BINARY;
		}
		return inMemoryFormat;
	}

	private void generateStartEvent() {
		for (EventListner listener : this.listeners) {
			listener.onStartUp(hazelcastInstance);
		}
	}

	public void addMapConfig(MapConfig mapConfig,
			EventListner startInstanceListner) {
		this.listeners.add(startInstanceListner);
		config.addMapConfig(mapConfig);
	}

	public <T extends DataSerializableFactory> void addSerializableFactory(int factoryId, T factory) {
		config.getSerializationConfig().addDataSerializableFactory(factoryId, factory);
	}

	public void stop() {
		if (hazelcastInstance != null) {
			hazelcastInstance.shutdown();
		}
	}

	public void generatIMDGStats() {
		registerSnmpOidTable();
		generateMapStatistics();

		NotificationDetail notificationDetail = generateMemberDetailTable();
		clusterMembershipListener.setNotificationDetail(notificationDetail);
	}

	private NotificationDetail generateMemberDetailTable() {
		TableMembersDetailTableImpl memberDetailTable = new TableMembersDetailTableImpl(imdgMIBImpl);
		try {
			memberDetailTable.addMemebersToTableEntries(configuration.getMemberData(), hazelcastInstance.getName());
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Error while adding Entry for imdg  in member detail table. Reason: "+e.getMessage());
			LogManager.getLogger().trace(e);
		}

		return  new NotificationDetail(memberDetailTable);
	}

	private void registerSnmpOidTable() {
		context.getSNMPAgent().registetSnmpTrapTable(new IMDG_MIBOidTable());
	}

	private void generateMapStatistics() {
		TableMapStatisticsTableImpl mapStatisticsTable = new TableMapStatisticsTableImpl(imdgMIBImpl);
		Collection<String> values = hazelcastInstance.getConfig().getMapConfigs().keySet();
		int index = 0; 
		for(String mapName : values) {
			try {
				mapStatisticsTable.addMapStatisticsToTableEntry(hazelcastInstance.getMap(mapName), ++index);
			} catch (SnmpStatusException e) {
				LogManager.getLogger().error(MODULE, "Error while adding Entry for imdg  in mapStatistics table. Reason: "+e.getMessage());
				LogManager.getLogger().trace(e);
			}		
		}
	}

}