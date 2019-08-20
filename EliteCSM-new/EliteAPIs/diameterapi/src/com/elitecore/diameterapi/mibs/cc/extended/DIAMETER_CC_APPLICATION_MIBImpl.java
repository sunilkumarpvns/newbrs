package com.elitecore.diameterapi.mibs.cc.extended;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.cc.autogen.DIAMETER_CC_APPLICATION_MIB;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaHostCfgsMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPeerCfgsMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPeerStatsMBean;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaHostIpAddrTable;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaPeerTable;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaPeerVendorTable;
import com.elitecore.diameterapi.mibs.cc.autogen.TableDccaPerPeerStatsTable;
import com.elitecore.diameterapi.mibs.cc.extended.hostcfgs.DccaHostCfgsImpl;
import com.elitecore.diameterapi.mibs.cc.extended.hostcfgs.DccaHostIpAddrEntryImpl;
import com.elitecore.diameterapi.mibs.cc.extended.hostcfgs.TableDccaHostIpAddrTableImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peercfgs.DccaPeerCfgsImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peercfgs.DccaPeerEntryImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peercfgs.DccaPeerVendorEntryImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peercfgs.TableDccaPeerTableImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peercfgs.TableDccaPeerVendorTableImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peerstats.DccaPeerStatsImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peerstats.DccaPerPeerStatsEntryImpl;
import com.elitecore.diameterapi.mibs.cc.extended.peerstats.TableDccaPerPeerStatsTableImpl;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatistic;
import com.sun.management.snmp.SnmpStatusException;

public class DIAMETER_CC_APPLICATION_MIBImpl extends DIAMETER_CC_APPLICATION_MIB implements Observer {

	private static final String MODULE = "DIA-CC-APP-MIB";
	private DccaPeerStatsMBean dccaPeerStatsMBean; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	private DccaPeerCfgsMBean dccaPeerCfgsMBean; //NOSONAR
	private DccaHostCfgsMBean dccaHostCfgsMBean; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable

	private TableDccaPeerTable dccaPeerTable;
	private TableDccaPeerVendorTable dccaPeerVendorTable;
	private TableDccaPerPeerStatsTable dccaPerPeerStatsTable;
	private TableDccaHostIpAddrTable hostIpAddrTable;
	transient private DiameterStatisticListener diameterStatisticListener;	
	
	public DIAMETER_CC_APPLICATION_MIBImpl(DiameterStatisticListener diameterStatisticListener) {
		this.diameterStatisticListener = diameterStatisticListener;
		this.dccaPeerStatsMBean = new DccaPeerStatsImpl();
		this.dccaPeerCfgsMBean = new DccaPeerCfgsImpl();
		this.dccaHostCfgsMBean = new DccaHostCfgsImpl();
	}

	@Override
	public void populate(MBeanServer server, ObjectName name) throws Exception {
		super.populate(server, name);
		this.dccaPeerTable = new TableDccaPeerTableImpl(this,server);
		this.dccaPeerVendorTable = new TableDccaPeerVendorTableImpl(this,server);
		this.dccaPerPeerStatsTable = new TableDccaPerPeerStatsTableImpl(this,server);
		this.hostIpAddrTable = new TableDccaHostIpAddrTableImpl(this,server);
		registerHostIpTableEntries();
		registerPeerTableEntries();
		diameterStatisticListener.addStatisticObserver(this);
		((DccaPeerStatsImpl)dccaPeerStatsMBean).setTableDccaPerPeerStatsTable(dccaPerPeerStatsTable);
	}
	
	
	private void registerHostIpTableEntries() {

		Map<String, DiameterPeerConfig>  ccHostConfigMap = diameterStatisticListener.getDiameterConfigProvider().getPeerConfigMap();
		if(ccHostConfigMap == null){
			LogManager.getLogger().error(MODULE, "HostIpAddr Entry not added in DccaHostIpAddrTable. Reason: No Host Configuration found");
			return;
		}

		for(DiameterPeerConfig ccPeerConfig : ccHostConfigMap.values()){
			addHostIPTableEntry(ccPeerConfig.getPeerId());
		}
	}

	private void addHostIPTableEntry(String peerIdentity) {
		try {
			DccaHostIpAddrEntryImpl ccaHostIpAddrEntry = new DccaHostIpAddrEntryImpl(peerIdentity, diameterStatisticListener.getDiameterConfigProvider());
			this.hostIpAddrTable.addEntry(ccaHostIpAddrEntry,new ObjectName(ccaHostIpAddrEntry.getObjectName()));
		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Error while adding HostIpAddr Entry for Peer: "+peerIdentity+" in DccaHostIpAddrTable. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}catch (MalformedObjectNameException e) {
			LogManager.getLogger().error(MODULE, "Error while adding HostIpAddr Entry for Peer: "+peerIdentity+" in DccaHostIpAddrTable. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	private void registerPeerTableEntries(){

		Map<String, DiameterPeerConfig> peerConfigMap = diameterStatisticListener.getDiameterConfigProvider().getPeerConfigMap();
		if(peerConfigMap == null){
			LogManager.getLogger().error(MODULE, "Peer Entry not added in DccaPeerTable. Reason: No Peer Config found");
			return;
		}
		for(DiameterPeerConfig ccPeerConfig : peerConfigMap.values()){
			addPeerTableEntry(ccPeerConfig);
		}
	}

	private void addPeerTableEntry(DiameterPeerConfig ccPeerConfig){
		try {

			DccaPeerEntryImpl dccaPeerEntry = new DccaPeerEntryImpl(ccPeerConfig.getPeerId(), diameterStatisticListener.getDiameterConfigProvider());
			dccaPeerTable.addEntry(dccaPeerEntry,new ObjectName(dccaPeerEntry.getObjectName()));
			int vendorIndex = 1;
			for(DiameterBasePeerVendorTable basePeerVendorTable  : ccPeerConfig.getDbpPeerVendorTable() ){
				try{
					DccaPeerVendorEntryImpl ccaPeerVendorEntry = new DccaPeerVendorEntryImpl(basePeerVendorTable, diameterStatisticListener.getDiameterConfigProvider(), ccPeerConfig.getPeerId());
					dccaPeerVendorTable.addEntry(ccaPeerVendorEntry,new ObjectName(ccaPeerVendorEntry.getObjectName()));
				} catch (SnmpStatusException e) {
					LogManager.getLogger().error(MODULE, "Error while adding PeerVendor Entry for peer(" + ccPeerConfig.getPeerId() + ") in DccaPeerVendorTable. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}catch (MalformedObjectNameException e) {
					LogManager.getLogger().error(MODULE, "Error while adding PeerVendor Entry for peer(" + ccPeerConfig.getPeerId() + ") in DccaPeerVendorTable. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
				vendorIndex++;
			}

			try {
				DccaPerPeerStatsEntryImpl ccaPeerStatsEntry = new DccaPerPeerStatsEntryImpl(ccPeerConfig.getPeerId(), diameterStatisticListener.getDiameterStatisticProvider(), diameterStatisticListener.getDiameterConfigProvider());
				dccaPerPeerStatsTable.addEntry(ccaPeerStatsEntry,new ObjectName(ccaPeerStatsEntry.getObjectName()));
			} catch (SnmpStatusException e) {
				LogManager.getLogger().error(MODULE, "Error while adding PerPeerStats Entry for peer(" + ccPeerConfig.getPeerId() + ") in DccaPerPeerStatsTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}catch (MalformedObjectNameException e) {
				LogManager.getLogger().error(MODULE, "Error while adding PerPeerStats Entry for peer(" + ccPeerConfig.getPeerId() + ") in DccaPerPeerStatsTable. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

		} catch (SnmpStatusException e) {
			LogManager.getLogger().error(MODULE, "Error while adding Peer Entry for peer(" + ccPeerConfig.getPeerId() + ") in DccaPeerTable. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}catch (MalformedObjectNameException e) {
			LogManager.getLogger().error(MODULE, "Error while adding Peer Entry for peer(" + ccPeerConfig.getPeerId() + ") in DccaPeerTable. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	@Override
	@Table(name = "")
	protected Object createDccaPeerStatsMBean(String groupName,
			String groupOid, ObjectName groupObjname, MBeanServer server) {
		return dccaPeerStatsMBean;
	}

	@Override
	protected Object createDccaPeerCfgsMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return dccaPeerCfgsMBean;
	}

	@Override
	protected Object createDccaHostCfgsMBean(String groupName, String groupOid,
			ObjectName groupObjname, MBeanServer server) {
		return dccaHostCfgsMBean;
	}

	@Override
	public void update(Observable o, Object arg) {
		if ((o instanceof DiameterStatistic) == false) {
			return;
		}
		
		if (arg instanceof String) {
			
			Map<String, DiameterPeerConfig> peerConfigMap = diameterStatisticListener.getDiameterConfigProvider().getPeerConfigMap();
			DiameterPeerConfig ccPeerConfig = peerConfigMap.get(arg);
			if (ccPeerConfig != null ) {
				addPeerTableEntry(ccPeerConfig);
				addHostIPTableEntry(ccPeerConfig.getPeerId());
			} else {
				LogManager.getLogger().error(MODULE, "Dyanmic Peer Entry for peer(" + arg + ") not added in DccaPeerTable. Reason: CC Peer Configuration not found");
			}
		}
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,String defaultName) throws MalformedObjectNameException {
		return new ObjectName(SnmpAgentMBeanConstant.DIAMETER_CC_MIB +",name="+ name);
	}
}
