package com.elitecore.diameterapi.mibs.custom.extended;

import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.DIAMETER_STACK_MIB;
import com.elitecore.diameterapi.mibs.custom.autogen.TableAppStatisticsTable;
import com.elitecore.diameterapi.mibs.custom.autogen.TableAppWiseStatisticsTable;
import com.elitecore.diameterapi.mibs.custom.autogen.TableCommandCodeStatisticsTable;
import com.elitecore.diameterapi.mibs.custom.autogen.TablePeerInfoTable;
import com.elitecore.diameterapi.mibs.custom.autogen.TablePeerIpAddrTable;
import com.elitecore.diameterapi.mibs.custom.autogen.TableResultCodeStatisticsTable;
import com.elitecore.diameterapi.mibs.custom.utility.DiameterStatisticsEvents;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatistic;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.sun.management.snmp.SnmpOid;

public class DIAMETER_STACK_MIBImpl extends DIAMETER_STACK_MIB implements Observer {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "DIAMETER-STACK-MIB";

	private final transient DiameterStatisticListener diameterStatisticListener;


	// MBean Implementation for MIB Groups.
	
	private final DiameterStackInfo diameterStackInfo;
	private final StackStatisticsMbeanImpl stackStatisticsMbean;
	private final AppStatsMBeanImpl applicationStatisticsMBean;
	private final PeerStatisticsMBeanImpl peerStatisticsMBean;
	private final ConnectionStatisticsMBeanImpl connectionStatisticsMBean;
	private final ApplicationWiseStatsMBeanImpl applicationWiseStatsMBean;
	private final CommandCodeWiseStatisticsMBeanImpl commandCodeWiseStatsMBean;
	private final ResultCodeWiseStatisticsMBeanImpl resultCodeWiseStatsMBean;
	
	// Tables of DIAMETER-STACK.mib

	private TableAppStatisticsTable applicationStatisticsTable;
	private TablePeerInfoTable peerInfoTable;
	private TablePeerIpAddrTable peerIpAddrTable;
	private TableAppWiseStatisticsTable applicationToPeerStatisticsTable;
	private TableCommandCodeStatisticsTable appToPeerCCWStatisticsTable;
	private TableResultCodeStatisticsTable appToPeerRCWStatisticsTable;

	private transient SNMPTableFactory snmpTableFactory;
	
	private static final AtomicLong IP_ADDRESS_INDEX = new AtomicLong(0);
	
	public DIAMETER_STACK_MIBImpl(DiameterStatisticListener diameterStatisticListener, Status status) {
		this.diameterStatisticListener = diameterStatisticListener;
		this.diameterStackInfo = new DiameterStackInfo(status,diameterStatisticListener.getDiameterStatisticResetter());
		this.stackStatisticsMbean = new StackStatisticsMbeanImpl(diameterStatisticListener.getDiameterStatisticProvider());
		this.applicationStatisticsMBean = new AppStatsMBeanImpl();
		this.peerStatisticsMBean = new PeerStatisticsMBeanImpl();
		this.connectionStatisticsMBean = new ConnectionStatisticsMBeanImpl();
		this.applicationWiseStatsMBean = new ApplicationWiseStatsMBeanImpl();
		this.commandCodeWiseStatsMBean = new CommandCodeWiseStatisticsMBeanImpl();
		this.resultCodeWiseStatsMBean = new ResultCodeWiseStatisticsMBeanImpl();
	}

	public DIAMETER_STACK_MIBImpl(DiameterStatisticListener diameterStatisticListener, Status status
			, SNMPTableFactory snmpTableFactory){
		this(diameterStatisticListener, status);
		this.snmpTableFactory = snmpTableFactory;
	}
	
	

	@Override
	public void populate(MBeanServer server, ObjectName name) throws Exception {
		super.populate(server, name);

		if(snmpTableFactory==null){
			snmpTableFactory = new SNMPTableFactoryImpl(server);
		}
		applicationStatisticsTable = snmpTableFactory.createAppStatisticTable(this);

		peerInfoTable = new TablePeerInfoTableImpl(this, server);
		peerIpAddrTable = new TablePeerIpAddrTableImpl(this, server);
		applicationToPeerStatisticsTable = new TableAppWiseStatisticsTableImpl(this, server);
		appToPeerCCWStatisticsTable = new TableCommandCodeStatisticsTableImpl(this, server);
		appToPeerRCWStatisticsTable = new TableResultCodeStatisticsTableImpl(this, server);
		
		this.diameterStatisticListener.addStatisticObserver(this);
		this.diameterStatisticListener.addConfigurationObserver(this);
		
		registerSNMPPeerEntries();
		registerApplicationEntries();
		
		// set all table
		this.applicationStatisticsMBean.setApplicationStatisticsTable(this.applicationStatisticsTable);
		this.peerStatisticsMBean.setPeerInfoTable(this.peerInfoTable);
		this.connectionStatisticsMBean.setPeerIPAddressTable(this.peerIpAddrTable);
		this.applicationWiseStatsMBean.setApplicationWiseToPeerStatisticsTable(this.applicationToPeerStatisticsTable);
		this.commandCodeWiseStatsMBean.setAppToPeerCCWStatisticsTable(this.appToPeerCCWStatisticsTable);
		this.resultCodeWiseStatsMBean.setAppToPeerRCWStatisticsTable(this.appToPeerRCWStatisticsTable);
		
	}
	
	private void registerSNMPPeerEntries() {
		Map<String, DiameterPeerConfig> peerConfigs = this.diameterStatisticListener.
				getDiameterConfigProvider().getPeerConfigMap();
		
		if (Maps.isNullOrEmpty(peerConfigs)) {
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No peer entry added in peer table, " +
						"Reason: no peer configuration found");
				return;
			}
		}
		
		for(String peerIdentity : peerConfigs.keySet()) {
			addSNMPPeerEntry(peerIdentity);
		}
	}

	private void registerApplicationEntries() {
		Map<ApplicationStatsIdentifier, GroupedStatistics> applicationMap = 
				this.diameterStatisticListener.getDiameterStatisticProvider().getApplicationMap();
		
		if (Maps.isNullOrEmpty(applicationMap)) {
			return;
		}

		/**
		 * Don't add stat entry if same application id is already added
		 * START - NETVERTEX-3491
		 */

		Set<Long> applicationIds = new HashSet<Long>();
		for (ApplicationStatsIdentifier keySet : applicationMap.keySet()) {
			if(applicationIds.add(keySet.getApplicationId())) {
				addAppStatsEntry(keySet);
			} else{
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "registerApplicationEntries -> Duplicate entry found for application: " + keySet);
				}
			}
		}
		/**
		 * END - NETVERTEX-3491
		 */
	}

	private void addSNMPPeerEntry(String peerIdentity) {
		addPeerEntry(peerIdentity);
	}

	private void addAppStatsEntry(ApplicationStatsIdentifier applicationStatsIdentifier) {
		AppStatisticsEntryMBeanImpl appStatsEntry = new AppStatisticsEntryMBeanImpl(
				applicationStatsIdentifier, diameterStatisticListener);
		try {
			applicationStatisticsTable.addEntry(appStatsEntry,new ObjectName(appStatsEntry.getObjectName()));
			SnmpOid compositeIndex = applicationStatisticsTable.buildOidFromIndexVal(appStatsEntry.getApplicationID());
			appStatsEntry.setCompositeIndex(compositeIndex.toString());
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Entry for application: "+ applicationStatsIdentifier.getApplicationId() + 
						" successfully added into application statistics table.");
			}
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while adding application statistics" +
					" for application: "+ applicationStatsIdentifier.getApplicationId() + 
					" in application statistics table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	private void addPeerEntry(String peerIdentity) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Adding snmp entry for peer: " + peerIdentity);
		}
		PeerInfoEntryMbeanImpl peerEntry =  new PeerInfoEntryMbeanImpl(peerIdentity,
				diameterStatisticListener.getDiameterConfigProvider());

		try {

			peerInfoTable.addEntry(peerEntry,new ObjectName(peerEntry.getObjectName()));
			SnmpOid indexValue = peerInfoTable.buildOidFromIndexVal(peerEntry.getPeerIndex());
			peerEntry.setIndexValue(indexValue.toString());
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Entry for peer: " + peerIdentity + 
						" added successfullly into peer table.");
			}
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while adding peer: " + peerIdentity + 
					" in peer table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		PeerIpAddrEntryMBeanImpl ipAddrEntry = new PeerIpAddrEntryMBeanImpl(peerIdentity,
				IP_ADDRESS_INDEX.incrementAndGet(),diameterStatisticListener.getDiameterConfigProvider());
		
		try {
			peerIpAddrTable.addEntry(ipAddrEntry,new ObjectName(ipAddrEntry.getObjectName()));
			SnmpOid compositeIndex = peerIpAddrTable.buildOidFromIndexVal(ipAddrEntry.getPeerIndex(), ipAddrEntry.getPeerIpAddressIndex());
			ipAddrEntry.setCompositeIndex(compositeIndex.toString());
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while adding peer ip address entry " +
					"for peer: " + peerIdentity + " in peer ip address table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof DiameterStatistic == false) {
			return;
		}
		
		if (arg instanceof DiameterStatisticsEvents) {
			
			DiameterStatisticsEvents diameterStatisticsEvents = (DiameterStatisticsEvents) arg;
			
			switch (diameterStatisticsEvents.getType()) {
			
			case DiameterStatisticsEvents.APPLICATION_TO_PEER_CC_WISE:
				addAppToPeerCommandCodeWiseEntry(diameterStatisticsEvents);
				break;
				
			case DiameterStatisticsEvents.APPLICATION_TO_PEER:
				addAppToPeerEntry(diameterStatisticsEvents);
				break;
			
			case DiameterStatisticsEvents.APPLICATION_TO_PEER_RC_WISE:
				addAppToPeerResultCodeWiseEntry(diameterStatisticsEvents);
				break;
			}
		} else if (arg instanceof ApplicationStatsIdentifier) {
			addAppStatsEntry((ApplicationStatsIdentifier)arg);
		} else if (arg instanceof String) {
			
			Map<String, DiameterPeerConfig> peerConfigMap = diameterStatisticListener.getDiameterConfigProvider().getPeerConfigMap();
			DiameterPeerConfig ccPeerConfig = peerConfigMap.get(arg);
			if (ccPeerConfig != null ) {
				addPeerEntry((String)arg);
			} else {
				LogManager.getLogger().error(MODULE, "Dynamic Peer Entry for peer(" + arg + ") not added in peer table. Reason: Peer Configuration not found");
			}
		}
	}


	private void addAppToPeerResultCodeWiseEntry(
			DiameterStatisticsEvents diameterStatisticsEvents) {
		
		ResultCodeStatisticsEntryMbeanImpl rcwPeerEntry = new ResultCodeStatisticsEntryMbeanImpl(
				diameterStatisticsEvents.getApplicationIdentifier(),
				diameterStatisticsEvents.getPeerIdentity(),
				diameterStatisticsEvents.getResultCode(),
				diameterStatisticListener);
		try {
			
			appToPeerRCWStatisticsTable.addEntry(rcwPeerEntry,new ObjectName(rcwPeerEntry.getObjectName()));
			SnmpOid compositeIndex = appToPeerRCWStatisticsTable.buildOidFromIndexVal(rcwPeerEntry.getPeerIndex(), 
					rcwPeerEntry.getApplicationID(), rcwPeerEntry.getResultCode());
			rcwPeerEntry.setCompositeIndex(compositeIndex.toString());
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Entry for result code: "+ diameterStatisticsEvents.getResultCode() + 
						" successfully added for peer: " +diameterStatisticsEvents.getPeerIdentity()+ " into result code wise statistic table.");
			}
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while adding result code entry" +
					" for result code: "+ diameterStatisticsEvents.getResultCode() + 
					" in result code wise statistic table for peer: " +diameterStatisticsEvents.getPeerIdentity()+ ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		
	}

	private void addAppToPeerCommandCodeWiseEntry(DiameterStatisticsEvents diameterStatisticsEvents) {
		CommandCodeWiseStatisticsEntryMBeanImpl ccwPeerEntry = new CommandCodeWiseStatisticsEntryMBeanImpl(
				diameterStatisticsEvents.getApplicationIdentifier(),
				diameterStatisticsEvents.getPeerIdentity(),
				diameterStatisticsEvents.getCommandCode()
				,diameterStatisticListener);
		try {
			
			appToPeerCCWStatisticsTable.addEntry(ccwPeerEntry,new ObjectName(ccwPeerEntry.getObjectName()));
			SnmpOid compositeIndex = appToPeerCCWStatisticsTable.buildOidFromIndexVal(ccwPeerEntry.getPeerIndex(), ccwPeerEntry.getApplicationID(), ccwPeerEntry.getCommandCode());
			ccwPeerEntry.setCompositeIndex(compositeIndex.toString());
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Entry for command code: "+ diameterStatisticsEvents.getCommandCode() + 
						" successfully added for peer: " +diameterStatisticsEvents.getPeerIdentity()+ " into command code wise statistic table.");
			}
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while adding command code entry" +
					" for command code: "+ diameterStatisticsEvents.getCommandCode() + 
					" in command code wise statistic table for peer: " +diameterStatisticsEvents.getPeerIdentity()+ ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	private void addAppToPeerEntry(DiameterStatisticsEvents diameterStatisticsEvents) {

		AppWiseStatisticsEntryMBeanImpl appToPeerEntry = new AppWiseStatisticsEntryMBeanImpl(
				diameterStatisticsEvents.getApplicationIdentifier(),
				diameterStatisticsEvents.getPeerIdentity()
				,diameterStatisticListener);
		try {
			
			applicationToPeerStatisticsTable.addEntry(appToPeerEntry,new ObjectName(appToPeerEntry.getObjectName()));
			SnmpOid compositeIndex = applicationToPeerStatisticsTable.buildOidFromIndexVal(appToPeerEntry.getPeerIndex(), appToPeerEntry.getApplicationID());
			appToPeerEntry.setCompositeIndex(compositeIndex.toString());
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Entry for application: "+ 
						diameterStatisticsEvents.getApplicationIdentifier().getApplicationId() + 
						" successfully added into application wise statistic table.");
			}
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while adding application wise statistics entry" +
					" for application: "+ diameterStatisticsEvents.getApplicationIdentifier().getApplicationId() + 
					" in application wise statistic table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	/**
	 * Implementation of the group defined in DIAMETER-STACK.mib
	 * include diameterStack, stackStats, appStats, peerStats,
	 * connectionStats, appWiseStats, commandCodeWiseStats. 
	 */
	
	@Override
	@Table(name = "diameterStack")
	protected Object createDiameterStackMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return diameterStackInfo;
	}

	@Override
	@Table(name = "stackStatistics")
	protected Object createStackStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return stackStatisticsMbean;
	}
	
	@Override
	@Table(name = "")
	protected Object createAppStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return applicationStatisticsMBean;
	}
	
	@Override
	@Table(name = "")
	protected Object createPeerStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return peerStatisticsMBean;
	}
	
	@Override
	@Table(name = "")
	protected Object createConnectionStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return connectionStatisticsMBean;
	}
	
	@Override
	@Table(name = "")
	protected Object createApplicationWiseStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return applicationWiseStatsMBean;
	}
	
	@Override
	@Table(name = "")
	protected Object createCommandCodeWiseStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return commandCodeWiseStatsMBean;
	}
	
	@Override
	@Table(name = "")
	protected Object createResultCodeWiseStatisticsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return resultCodeWiseStatsMBean;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.DIAMETER_STACK_MIB + ",name="+name);
	}
}