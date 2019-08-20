package com.elitecore.netvertex.core.conf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.db.EliteDBConnectionProperty;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.filelocation.FileLocationData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverprofile.OfflineRncServerProfileData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.conf.impl.DiameterStackConfigurationFactory;
import com.elitecore.netvertex.core.conf.impl.NetvertexServerInstanceConfigurationImpl;
import com.elitecore.netvertex.core.conf.impl.NetvertexServerInstanceFactory;
import com.elitecore.netvertex.core.conf.impl.RadiusListenerConfigurationFactory;
import com.elitecore.netvertex.core.conf.impl.ScriptDataFactory;
import com.elitecore.netvertex.core.conf.impl.base.BaseConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterStackConfigurationImpl;
import com.elitecore.netvertex.gateway.file.FileGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.impl.RadiusListenerConfigurationImpl;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.offlinernc.conf.impl.OfflineRnCServiceConfigurationImpl;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.conf.impl.PCRFServiceConfigurationImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.ServerGroups.OFFLINE_RNC;
import static com.elitecore.corenetvertex.constants.ServerGroups.PCC;
import static java.util.Comparator.comparingInt;

/**
 * Created by harsh on 6/20/16.
 */
public class NetvertexServerGroupConfigurable extends BaseConfigurationImpl {

	private static final String MODULE = "NET-SERV-GRP-CONFIGURABLE";
    @Nullable
    private NetvertexServerGroupConfiguration netvertexServerGroupConfiguration;
	private SessionFactory sessionFactory;
	@Nonnull
	private NetvertexServerInstanceFactory netvertexServerInstanceFactory;
	private DiameterStackConfigurationFactory diameterStackFactory;
	private RadiusListenerConfigurationFactory radiusListenerFactory;
	private NotificationServiceConfigurationFactory notificationServiceConfigurationFactory;
	private ScriptDataFactory scriptDataFactory;

	public NetvertexServerGroupConfigurable(ServerContext serverContext, SessionFactory sessionFactory) {
        super(serverContext);
		this.sessionFactory = sessionFactory;
		this.netvertexServerInstanceFactory = new NetvertexServerInstanceFactory();
		this.diameterStackFactory = new DiameterStackConfigurationFactory();
		this.radiusListenerFactory = new RadiusListenerConfigurationFactory();
		this.notificationServiceConfigurationFactory = new NotificationServiceConfigurationFactory();
		this.scriptDataFactory = new ScriptDataFactory();
    }
    
    @Override
    public void readConfiguration() throws LoadConfigurationException {
    	
        getLogger().info(MODULE, "Read configuration for netvertex server group started");

        if(Strings.isNullOrBlank(getServerContext().getServerInstanceId())) {
            throw new LoadConfigurationException("Server instance id not configured");
        }

        Session session = null;
        try {
        	session = sessionFactory.openSession();
        	List<ServerGroupData> serverGroupDatas = HibernateReader.readAll(ServerGroupData.class, session);
        	
        	ServerGroupData currentServerGroupData = null;
        	for (ServerGroupData serverGroupData : serverGroupDatas) {
        		for(ServerInstanceData serverInstanceData : serverGroupData.getServerInstances()) {
        			if(serverInstanceData.getId().equals(getServerContext().getServerInstanceId())) {
        				currentServerGroupData = serverGroupData;
        				break;
        			}
        		}
        		
        		if (currentServerGroupData != null) {
        			break;
        		}
        	}
        	
        	
        	if (currentServerGroupData == null) {
        		throw new LoadConfigurationException("No server group found for server instance id: " + getServerContext().getServerInstanceId());
        	}
    		
    		List<NetvertexServerGroupInstanceConfiguration> netvertexServerGroupInstanceConfigurations = new ArrayList<NetvertexServerGroupInstanceConfiguration>();
    		
    		if (Collectionz.isNullOrEmpty(currentServerGroupData.getServerInstances())) {
    			throw new LoadConfigurationException("No server instance configured in server group: " + currentServerGroupData.getName());
    		}

    		List<ServerProfileData> serverProfileDatas = new ArrayList<>();
    		List<OfflineRncServerProfileData> offlineRnCServerProfileDatas = new ArrayList<>();
			
    		if (PCC.getValue().equals(currentServerGroupData.getServerGroupType())) {
				serverProfileDatas = HibernateReader.readAll(ServerProfileData.class, session);
				offlineRnCServerProfileDatas.add(OfflineRncServerProfileData.disabled());
			} else {
				offlineRnCServerProfileDatas = HibernateReader.readAll(OfflineRncServerProfileData.class, session);
				serverProfileDatas.add(ServerProfileData.disabled());
			}

			
			if ((PCC.getValue().equals(currentServerGroupData.getServerGroupType()) && (Collectionz.isNullOrEmpty(serverProfileDatas))) || 
					(OFFLINE_RNC.getValue().equals(currentServerGroupData.getServerGroupType()) && Collectionz.isNullOrEmpty(offlineRnCServerProfileDatas))) {
				throw new LoadConfigurationException("No server profile found");
			}

			ServerProfileData serverProfileData = serverProfileDatas.get(0);
			OfflineRncServerProfileData offlineRncServerProfileData = offlineRnCServerProfileDatas.get(0);

			NetvertexServerGroupInstanceConfiguration runningServerInstance = null;

			Collections.sort(currentServerGroupData.getServerInstances(), comparingInt(getInstanceWeight()));

			boolean isPrimaryServer = true;

    		for(ServerInstanceData serverInstanceData : currentServerGroupData.getServerInstances()) {
    		
				NetvertexServerGroupInstanceConfiguration serverInstanceGroupConfiguration = createServerInstanceGroupConfiguration(serverInstanceData,
						isPrimaryServer,
						serverProfileData,
						offlineRncServerProfileData,
						getFileLocationData(serverInstanceData.getFileLocation(),session),
						currentServerGroupData.getServerGroupType());
				if (serverInstanceData.getId().equals(getServerContext().getServerInstanceId())) {
					runningServerInstance = serverInstanceGroupConfiguration;
				}
				netvertexServerGroupInstanceConfigurations.add(serverInstanceGroupConfiguration);
				isPrimaryServer = false;
    		}


            NetvertexServerGroupInstanceConfiguration primaryInstance = netvertexServerGroupInstanceConfigurations.get(0);
            NetvertexServerGroupInstanceConfiguration secondaryInstance = null;
            if(netvertexServerGroupInstanceConfigurations.size() > 1) {
                secondaryInstance = netvertexServerGroupInstanceConfigurations.get(1);
            }

            if(runningServerInstance == null) {
            	throw new LoadConfigurationException("Running server instance not found");
			}



			DatabaseData databaseData = currentServerGroupData.getDatabaseData();
			DBDataSource sessionDatasource = getDataSource(databaseData);

			DatabaseData notificationDatabaseData = currentServerGroupData.getNotificationDataSourceData();
			DBDataSource notificationDataSource = null;
			if(Objects.isNull(notificationDatabaseData) == false){
				notificationDataSource = getDataSource(notificationDatabaseData);
			}

			if(primaryInstance == runningServerInstance) {
				if(getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Running Instance is primary");
				}
			} else {
				if(getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE,"Running Instance is secondary");
				}
			}

			this.netvertexServerGroupConfiguration = new NetvertexServerGroupConfiguration(currentServerGroupData.getId(),
					currentServerGroupData.getName()
            		, currentServerGroupData.getSessionSynchronization(),
					primaryInstance,
					secondaryInstance,
					runningServerInstance,
					sessionDatasource,
					notificationDataSource);
    	        	
        } finally {
        	HibernateConfigurationUtil.closeQuietly(session);
        }
        
    }

	private DBDataSourceImpl getDataSource(DatabaseData databaseData) {

		return new DBDataSourceImpl(databaseData.getId(),
				databaseData.getName(),
				databaseData.getConnectionUrl(),
				databaseData.getUserName(),
				databaseData.getPassword(),
				databaseData.getMinimumPool(),
				databaseData.getMaximumPool(),
				databaseData.getStatusCheckDuration(),
				EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue,
				databaseData.getQueryTimeout());
	}

	private ToIntFunction<ServerInstanceData> getInstanceWeight() {
		return data -> data.getServerGroupServerInstanceRelData().getServerWeightage();
	}

	private NetvertexServerGroupInstanceConfiguration createServerInstanceGroupConfiguration(ServerInstanceData serverInstanceData,
			boolean isPrimaryServer,
			@Nullable ServerProfileData serverProfileData,
			@Nullable OfflineRncServerProfileData offlineRncServerProfileData,
			List<FileLocationData> fileLocationDatas, String serverGroupType) {
		
		NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration = null;
		DiameterStackConfigurationImpl diameterListenerConf = null;
		RadiusListenerConfigurationImpl radiusGatewayEventListnerConf = null;
		PCRFServiceConfiguration pcrfServiceConfiguration = null;
		NotificationServiceConfigurationImpl notificationServiceConfiguration= null;
		OfflineRnCServiceConfigurationImpl offlineRnCServiceConfiguration = null;
		List<FileGatewayConfiguration> listOfFileGatewayConfigurations = new ArrayList<>();
		
		if (PCC.getValue().equals(serverGroupType) && serverProfileData != null) {
			
			netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, serverProfileData,scriptDataFactory);
			diameterListenerConf = diameterStackFactory.create(serverInstanceData, serverProfileData);
			radiusGatewayEventListnerConf = radiusListenerFactory.create(serverInstanceData, serverProfileData);
			pcrfServiceConfiguration = new PCRFServiceConfigurationImpl(serverProfileData.getPcrfServiceQueueSize(), serverProfileData.getPcrfServiceMinThreads(), serverProfileData.getPcrfServiceMaxThreads(), serverProfileData.getPcrfServiceWorkerThreadPriority());
			notificationServiceConfiguration = notificationServiceConfigurationFactory.create(serverInstanceData, serverProfileData);

			offlineRnCServiceConfiguration = new OfflineRnCServiceConfigurationImpl();
		} else if (OFFLINE_RNC.getValue().equals(serverGroupType) && offlineRncServerProfileData != null) {
			
			netvertexServerInstanceConfiguration = netvertexServerInstanceFactory.create(serverInstanceData, offlineRncServerProfileData,scriptDataFactory);
			diameterListenerConf = new DiameterStackConfigurationImpl();
			radiusGatewayEventListnerConf = new RadiusListenerConfigurationImpl();
			pcrfServiceConfiguration = new PCRFServiceConfigurationImpl();
			notificationServiceConfiguration = new NotificationServiceConfigurationImpl(0, 0, 0, null, null);
			offlineRnCServiceConfiguration = new OfflineRnCServiceConfigurationImpl(offlineRncServerProfileData,
					getServerContext().getServerHome() + File.separatorChar + "intermediate");
			
			for (FileLocationData fileLocationData : fileLocationDatas) {
				FileGatewayConfiguration fileGatewayConfiguration = new FileGatewayConfiguration(fileLocationData);
				listOfFileGatewayConfigurations.add(fileGatewayConfiguration);
			}
		} else {
			throw new AssertionError("Unknown serverGroupType value: " + serverGroupType);
		}

        return new NetvertexServerGroupInstanceConfiguration(serverInstanceData.getId(), netvertexServerInstanceConfiguration,
				diameterListenerConf,
				radiusGatewayEventListnerConf,
				pcrfServiceConfiguration,
				notificationServiceConfiguration, offlineRnCServiceConfiguration,
				listOfFileGatewayConfigurations,
				isPrimaryServer);
	}

	private List<FileLocationData> getFileLocationData(String fileLocation, Session session) {
		if (fileLocation == null || fileLocation.length() == 0) {
			return null;
		}
		List<String> fileLocations = Arrays.asList(fileLocation.split(","));
		List<FileLocationData> fileLocationDatas = HibernateReader.readAll(FileLocationData.class, session);
		Collectionz.filter(fileLocationDatas, fileLocationData -> fileLocations.contains(fileLocationData.getId()));
		
		return fileLocationDatas;
	}

	public int parseInt(Integer configuredValue, int defaultVal) {
        return configuredValue == null ? defaultVal : configuredValue.intValue();
    }

	@Nullable
    public NetvertexServerGroupConfiguration getNetvertexServerGroupConfiguration() {
        return netvertexServerGroupConfiguration;
    }
}
