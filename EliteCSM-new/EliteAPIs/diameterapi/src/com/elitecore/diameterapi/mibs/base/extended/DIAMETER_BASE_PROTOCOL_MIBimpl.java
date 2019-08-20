package com.elitecore.diameterapi.mibs.base.extended;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.base.autogen.DIAMETER_BASE_PROTOCOL_MIB;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPeerInfoMBean;
import com.elitecore.diameterapi.mibs.base.autogen.DbpRealmCfgsMBean;
import com.elitecore.diameterapi.mibs.base.autogen.DbpRealmStatsMBean;
import com.elitecore.diameterapi.mibs.base.autogen.EliteDSCMBean;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpAppAdvFromPeerTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpAppAdvToPeerTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpLocalApplTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPeerIpAddrTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPeerTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPeerVendorTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpPerPeerInfoTable;
import com.elitecore.diameterapi.mibs.base.autogen.TableDbpRealmMessageRouteTable;
import com.elitecore.diameterapi.mibs.base.extended.elitedsc.EliteDSCImpl;
import com.elitecore.diameterapi.mibs.base.extended.localcfgs.DbpAppAdvToPeerEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.localcfgs.DbpLocalApplEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.localcfgs.DbpLocalCfgsImpl;
import com.elitecore.diameterapi.mibs.base.extended.localcfgs.TableDbpAppAdvToPeerTableImpl;
import com.elitecore.diameterapi.mibs.base.extended.localcfgs.TableDbpLocalApplTableImpl;
import com.elitecore.diameterapi.mibs.base.extended.localstats.DbpLocalStatsImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.DbpAppAdvFromPeerEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.DbpPeerCfgsImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.DbpPeerEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.DbpPeerIpAddrEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.DbpPeerVendorEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.TableDbpAppAdvFromPeerTableImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.TableDbpPeerIpAddrTableImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerconf.TableDbpPeerTableImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerstats.DbpPeerInfoImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerstats.DbpPerPeerInfoEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.peerstats.TableDbpPerPeerInfoTableImpl;
import com.elitecore.diameterapi.mibs.base.extended.realmconf.DbpRealmCfgsImpl;
import com.elitecore.diameterapi.mibs.base.extended.realmstats.DbpRealmMessageRouteEntryImpl;
import com.elitecore.diameterapi.mibs.base.extended.realmstats.DbpRealmStatsImpl;
import com.elitecore.diameterapi.mibs.base.extended.realmstats.TableDbpRealmMessageRouteTableImpl;
import com.elitecore.diameterapi.mibs.config.DiameterConfiguration;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerAppAdvTable;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerIpAddressTable;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatistic;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.elitecore.diameterapi.mibs.statistics.RealmIdentifier;
import com.sun.management.snmp.SnmpStatusException;

public class DIAMETER_BASE_PROTOCOL_MIBimpl extends DIAMETER_BASE_PROTOCOL_MIB implements Observer{
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE ="DIA-BASE-PROTOCOL-MIB";
	private DbpLocalCfgsImpl dbpLocalCfgs;
	private DbpLocalStatsImpl dbpLocalStats;
	private DbpPeerCfgsImpl dbpPeerCfgs;
	private DbpPeerInfoMBean dbpPeerInfo; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	private DbpRealmCfgsMBean dbpRealmCfgs; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	private DbpRealmStatsMBean dbpRealmStats; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	private TableDbpRealmMessageRouteTable tableDbpRealmMessageRouteTable;
	private TableDbpAppAdvFromPeerTable tableDbpAppAdvFromPeerTable;
	private TableDbpPeerTable tableDbpPeerTable;
	private TableDbpPeerIpAddrTable tableDbpPeerIpAddrTable;
	private TableDbpPeerVendorTable tableDbpPeerVendorTable;
	private TableDbpPerPeerInfoTable tableDbpPerPeerInfoTable;
	private EliteDSCMBean eliteDSC; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	transient private DiameterStatisticListener diameterStatisticListener;
	private TableDbpLocalApplTable tableDbpLocalApplTable;
	private TableDbpAppAdvToPeerTable tableDbpAppAdvToPeerTable;

	private static AtomicInteger realmMIBIndexAllocator = new AtomicInteger(1);
	private static AtomicInteger peerIPAddrMIBIndexAllocator = new AtomicInteger(1);
	private static AtomicInteger peerVendorMIBIndexAllocator = new AtomicInteger(1);
	
	public DIAMETER_BASE_PROTOCOL_MIBimpl(DiameterStackContext diameterStackContext, DiameterStatisticListener diameterStatisticListener) {
		
		this.diameterStatisticListener = diameterStatisticListener;
		dbpLocalCfgs = new DbpLocalCfgsImpl();
		dbpLocalStats = new DbpLocalStatsImpl(diameterStatisticListener.getDiameterStatisticProvider());
		dbpPeerCfgs = new DbpPeerCfgsImpl();
		dbpPeerInfo = new DbpPeerInfoImpl();
		dbpRealmCfgs = new DbpRealmCfgsImpl();
		dbpRealmStats = new DbpRealmStatsImpl();
		eliteDSC = new EliteDSCImpl(diameterStackContext , diameterStatisticListener.getDiameterStatisticProvider());
	}
	
	@Override
	public void populate(MBeanServer server, ObjectName name) throws Exception {
		super.populate(server, name);
		
		this.tableDbpAppAdvFromPeerTable = new TableDbpAppAdvFromPeerTableImpl(this, server);
		this.tableDbpPeerTable = new TableDbpPeerTableImpl(this,server);
		this.tableDbpPeerIpAddrTable = new TableDbpPeerIpAddrTableImpl(this,server);
		this.tableDbpPeerVendorTable = new TableDbpPeerVendorTable(this, server);
		this.tableDbpPerPeerInfoTable = new TableDbpPerPeerInfoTableImpl(this,server);
		this.tableDbpRealmMessageRouteTable = new TableDbpRealmMessageRouteTableImpl(this,server);
		this.tableDbpLocalApplTable = new TableDbpLocalApplTableImpl(this, server);
		this.tableDbpAppAdvToPeerTable = new TableDbpAppAdvToPeerTableImpl(this, server);
		registerSNMPPeerEntries();
		registerSNMPRealmEntries();
		registerSNMPLocalAppEntries();
		diameterStatisticListener.addStatisticObserver(this);
		diameterStatisticListener.addConfigurationObserver(this);
		((DbpRealmStatsImpl)dbpRealmStats).setTableDbpRealmMessageRouteTable(tableDbpRealmMessageRouteTable);
		((DbpPeerInfoImpl)dbpPeerInfo).setTableDbpPerPeerInfoTable(tableDbpPerPeerInfoTable);
		
		//Providing Inner Peer Tables
		dbpPeerCfgs.setTableDbpAppAdvFromPeerTable(tableDbpAppAdvFromPeerTable);
		dbpPeerCfgs.setTableDbpPeerIpAddrTable(tableDbpPeerIpAddrTable);
		dbpPeerCfgs.setTableDbpPeerTable(tableDbpPeerTable);
		dbpPeerCfgs.setTableDbpPeerVendorTable(tableDbpPeerVendorTable);
		
		//Providing Inner tables to Local Peer (Diameter Stack)
		dbpLocalCfgs.setTableDbpLocalApplTable(tableDbpLocalApplTable);
		dbpLocalCfgs.setTableDbpAppAdvToPeerTable(tableDbpAppAdvToPeerTable);
		
	}

	private void registerSNMPLocalAppEntries() {
		DiameterBasePeerAppAdvTable[] supportedApplications = createApplicationEntries(diameterStatisticListener.getDiameterStatisticProvider().getSupportedApplicationIdentifiers());
		for(int i = 0 ; i < supportedApplications.length ; i++) {
			try {
				DbpLocalApplEntryImpl dbpLocalApplEntry = new DbpLocalApplEntryImpl(supportedApplications[i]);
				tableDbpLocalApplTable.addEntry(dbpLocalApplEntry, new ObjectName(dbpLocalApplEntry.getObjectName()));
			} catch (SnmpStatusException e) {
				LogManager.getLogger().error(MODULE, "Local App entry for Application: " + supportedApplications[i].getAppAdvName() + 
						" not added, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MalformedObjectNameException e) {
				LogManager.getLogger().error(MODULE, "Local App entry for Application: " + supportedApplications[i].getAppAdvName() + 
						" not added, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

	private void registerSNMPRealmEntries(){
		Map<RealmIdentifier, GroupedStatistics> realmStatistics = diameterStatisticListener.getDiameterStatisticProvider().getRealmStatsMap();

		if(realmStatistics == null || realmStatistics.isEmpty()){
			LogManager.getLogger().warn(MODULE, "No Realm Entry added in SNMP Realm Route Table. Reason: No Realm configuration found");
			return;
		}
		
		for(RealmIdentifier realm : realmStatistics.keySet()){
			addSNMPRealmEntry(realm);
		}
	}
	
	private void addSNMPRealmEntry (RealmIdentifier realmIdentifier) {
		
		DbpRealmMessageRouteEntryImpl dbpRealmMessageRouteEntry = new DbpRealmMessageRouteEntryImpl(realmMIBIndexAllocator.getAndIncrement(), 
				realmIdentifier, diameterStatisticListener.getDiameterStatisticProvider());
		try {
			tableDbpRealmMessageRouteTable.addEntry(dbpRealmMessageRouteEntry,new ObjectName(dbpRealmMessageRouteEntry.getObjectName()));
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Error while adding Realm Entry for realm(" + realmIdentifier.getRealmName() + ") in Realm Message Route Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (MalformedObjectNameException e) {
			LogManager.getLogger().error(MODULE, "Error while adding Realm Entry for realm(" + realmIdentifier.getRealmName() + ") in Realm Message Route Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		// FIXME
		//Following entries are not implemented as it is not provided in current structure but will provided in  future
		/*try {
			tableDbpRealmKnownPeersTable.addEntry(dbpPeerIpAddrEntry);
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Realm Entry for realm(" + realmIdentifier + ") not added in Realm Known Peers Table. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}*/
		
	}
	private void registerSNMPPeerEntries(){
		Map<String, DiameterPeerConfig> peerConfigs = diameterStatisticListener.getDiameterConfigProvider().getPeerConfigMap();
		
		if(peerConfigs == null || peerConfigs.isEmpty()){
			LogManager.getLogger().error(MODULE, "No Peer Entry added in SNMP Peer Table. Reason: No Peer configuration found");
			return;
		}
		
		for(String peerIdentity : peerConfigs.keySet()) {
			addSNMPPeerEntry(peerIdentity);
		}
	}
	
	private void addSNMPPeerEntry (String peerIdentity) {

		try{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Adding SNMP Entry for Peer: " + peerIdentity);
			}
			DbpPeerEntryImpl dbpPeerEntry =  new DbpPeerEntryImpl(peerIdentity, diameterStatisticListener.getDiameterConfigProvider());
			DbpPerPeerInfoEntryImpl dbpPerPeerInfoEntry = new DbpPerPeerInfoEntryImpl(peerIdentity,
					diameterStatisticListener.getDiameterStatisticProvider(), 
					diameterStatisticListener.getDiameterConfigProvider());

			try {
				tableDbpPeerTable.addEntry(dbpPeerEntry,new ObjectName(dbpPeerEntry.getObjectName()));
			} catch (SnmpStatusException e) {
				LogManager.getLogger().error(MODULE, "Error while adding Peer (" + peerIdentity + ") in Peer Table. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MalformedObjectNameException e) {
				LogManager.getLogger().error(MODULE, "Error while adding Peer (" + peerIdentity + ") in Peer Table. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

			try {
				tableDbpPerPeerInfoTable.addEntry(dbpPerPeerInfoEntry,new ObjectName(dbpPerPeerInfoEntry.getObjectName()));
			} catch (SnmpStatusException e) {
				LogManager.getLogger().error(MODULE, "Error while adding Peer Entry for peer(" + peerIdentity + ") in Peer Info Table. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}catch (MalformedObjectNameException e) {
				LogManager.getLogger().error(MODULE, "Error while adding Peer Entry for peer (" + peerIdentity + ") in Peer Info Table. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

			DiameterPeerConfig peerConfig = diameterStatisticListener.getDiameterConfigProvider().getPeerConfig(peerIdentity);
			DiameterBasePeerIpAddressTable[] dbpPeerIPAddressEntries = peerConfig.getPeerIpAddressIndex();
			for(int i = 0 ; i < dbpPeerIPAddressEntries.length ; i++) {
				try {
					DbpPeerIpAddrEntryImpl dbpPeerIpAddrEntry = new DbpPeerIpAddrEntryImpl(peerIPAddrMIBIndexAllocator.getAndIncrement(), 
							peerIdentity, diameterStatisticListener.getDiameterConfigProvider(), dbpPeerIPAddressEntries[i]);
					tableDbpPeerIpAddrTable.addEntry(dbpPeerIpAddrEntry,new ObjectName(dbpPeerIpAddrEntry.getObjectName()));
				} catch (SnmpStatusException e) {
					LogManager.getLogger().error(MODULE, "Error while adding Peer (" + peerIdentity + ") in Peer IP Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}catch (MalformedObjectNameException e) {
					LogManager.getLogger().error(MODULE, "Error while adding Peer (" + peerIdentity + ") in Peer IP Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			DiameterBasePeerAppAdvTable[] dbpAppAdvFromPeerEntries = createApplicationEntries(peerConfig.getDbpAppAdvFromPeer());
			for(int i = 0 ; i < dbpAppAdvFromPeerEntries.length ; i++) {
				try {
					DbpAppAdvFromPeerEntryImpl dbpAppAdvFromPeerEntry = new DbpAppAdvFromPeerEntryImpl(
							peerIdentity, diameterStatisticListener.getDiameterConfigProvider(), dbpAppAdvFromPeerEntries[i]);
					tableDbpAppAdvFromPeerTable.addEntry(dbpAppAdvFromPeerEntry, new ObjectName(dbpAppAdvFromPeerEntry.getObjectName()));
				} catch (SnmpStatusException e) {
					LogManager.getLogger().error(MODULE, "Peer Application Entry for peer(" + peerIdentity + 
							") not added in Peer App Adv From Peer Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				} catch (MalformedObjectNameException e) {
					LogManager.getLogger().error(MODULE, "Peer Application Entry for peer(" + peerIdentity + 
							") not added in Peer App Adv From Peer Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}

			DiameterBasePeerVendorTable[] dbpPeervendorEntries = peerConfig.getDbpPeerVendorTable();
			for(int i = 0 ; i < dbpPeervendorEntries.length ; i++) {
				try {
					DbpPeerVendorEntryImpl dbpPeerVendorEntry = new DbpPeerVendorEntryImpl(peerVendorMIBIndexAllocator.getAndIncrement(), 
							peerIdentity, diameterStatisticListener.getDiameterConfigProvider(),dbpPeervendorEntries[i]);
					tableDbpPeerVendorTable.addEntry(dbpPeerVendorEntry, new ObjectName(dbpPeerVendorEntry.getObjectName()));
				} catch (SnmpStatusException e) {
					LogManager.getLogger().error(MODULE, "Peer Vendor Entry for peer(" + peerIdentity + ") not added in Peer Vendor Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				} catch (MalformedObjectNameException e) {
					LogManager.getLogger().error(MODULE, "Peer Vendor Entry for peer(" + peerIdentity + 
							") not added in Peer Vendor Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}

			DiameterBasePeerAppAdvTable[] dbpAppAdvToPeerEntries = createApplicationEntries(peerConfig.getDbpAppAdvToPeer());
			for(int i = 0 ; i < dbpAppAdvToPeerEntries.length ; i++) {
				try {
					DbpAppAdvToPeerEntryImpl dbpAppAdvToPeerEntry = new DbpAppAdvToPeerEntryImpl(peerIdentity, 
							diameterStatisticListener.getDiameterConfigProvider(), dbpAppAdvToPeerEntries[i]);
					tableDbpAppAdvToPeerTable.addEntry(dbpAppAdvToPeerEntry, new ObjectName(dbpAppAdvToPeerEntry.getObjectName()));
				} catch (SnmpStatusException e) {
					LogManager.getLogger().error(MODULE, "Entry for Application: " + dbpAppAdvToPeerEntries[i].getAppAdvName() + 
							", peer(" + peerIdentity + ") not added in App Adv To Peer Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				} catch (MalformedObjectNameException e) {
					LogManager.getLogger().error(MODULE, "Entry for Application: " + dbpAppAdvToPeerEntries[i].getAppAdvName() + 
							", peer(" + peerIdentity + ") not added in App Adv To Peer Table. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}catch (Throwable e){
			LogManager.getLogger().error(MODULE, "Error Adding SNMP entry for Peer: " + peerIdentity+ ", Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
	private DiameterBasePeerAppAdvTable[] createApplicationEntries(
			Set<ApplicationEnum> applications) {
		
		Map<Long, DiameterBasePeerAppAdvTable > entries = new HashMap<Long, DiameterBasePeerAppAdvTable>();
		for(ApplicationEnum entry: applications) {
			DiameterBasePeerAppAdvTable mapEntry = entries.get(entry.getApplicationId());
			if(mapEntry == null) {
				entries.put(entry.getApplicationId(), new DiameterBasePeerAppAdvTable(
						entry.getVendorId(), entry.getApplicationId(), entry.getApplicationType()));
				
			} else if(isApplicationEntryUpdatable(entry, mapEntry)) {
				mapEntry.setDbpAppAdvFromPeerType(ServiceTypes.BOTH);
				entries.put(mapEntry.getDbpAppId(), mapEntry);
			}
		}
		return entries.values().toArray(new DiameterBasePeerAppAdvTable[entries.size()]);
	}

	private boolean isApplicationEntryUpdatable(
			ApplicationEnum entry,
			DiameterBasePeerAppAdvTable mapEntry) {
		return (mapEntry.getDbpAppAdvFromPeerType() == ServiceTypes.AUTH.code && entry.getApplicationType() == ServiceTypes.ACCT) ||
				(mapEntry.getDbpAppAdvFromPeerType() == ServiceTypes.ACCT.code && entry.getApplicationType() == ServiceTypes.AUTH);
	}

	@Override
	@Table(name = "")
	protected Object createDbpRealmStatsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return dbpRealmStats;
	}

	@Override
	protected Object createDbpRealmCfgsMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return dbpRealmCfgs;
	}

	@Override
	@Table(name = "")
	protected Object createDbpPeerInfoMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return dbpPeerInfo;
	}

	@Override
	protected Object createDbpPeerCfgsMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return dbpPeerCfgs;
	}

	@Override
	@Table(name = "dbpLocalStats")
	protected Object createDbpLocalStatsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return dbpLocalStats;
	}

	@Override
	protected Object createDbpLocalCfgsMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return dbpLocalCfgs;
	}

	@Override
	protected Object createDbpNotifCfgsMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		// TODO will replace by implemented class
		return super.createDbpNotifCfgsMBean(groupName, groupOid, groupObjname, server);
	}
	
	@Override
	protected Object createEliteDSCMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return eliteDSC;
	}

	@Override
	public void update(Observable o, Object arg) {
		
		if ((o instanceof DiameterStatistic) && arg instanceof RealmIdentifier) {
			addSNMPRealmEntry((RealmIdentifier)arg);
			return;
		}
		if ((o instanceof DiameterConfiguration) && arg instanceof String) {
			addSNMPPeerEntry((String)arg);
			return;
		}
		
	}

	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.BASE_DIAMETER_MIB +",name="+ name);
	}
}
