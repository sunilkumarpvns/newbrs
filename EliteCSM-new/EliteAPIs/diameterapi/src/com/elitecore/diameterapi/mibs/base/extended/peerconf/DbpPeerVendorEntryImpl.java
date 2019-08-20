package com.elitecore.diameterapi.mibs.base.extended.peerconf;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPeerVendorEntryMBean;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerVendorId;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerVendorRowStatus;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerVendorStorageType;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;

public class DbpPeerVendorEntryImpl implements DbpPeerVendorEntryMBean {
	
	private String hostIdentity;
	private static final String MODULE = "DBP-PEER-VENDOR-ENTRY";
	private DiameterConfigProvider diameterConfigProvider;
	private long index;
	private DiameterBasePeerVendorTable dbpPeerVendorEntry;
	
	
	public DbpPeerVendorEntryImpl(int index, String hostIdentity, 
			DiameterConfigProvider diameterConfigProvider, 
			DiameterBasePeerVendorTable dbpPeervendorEntries) {
		
		this.index = index;
		this.hostIdentity = hostIdentity;
		this.diameterConfigProvider = diameterConfigProvider;
		this.dbpPeerVendorEntry = dbpPeervendorEntries;
	}
	
	@Override
	public EnumDbpPeerVendorRowStatus getDbpPeerVendorRowStatus(){
		try{
			return new EnumDbpPeerVendorRowStatus(dbpPeerVendorEntry.getDbpPeerVendorRowStatus());
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer vendor row status for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPeerVendorRowStatus();
		}
	}

	@Override
	public void setDbpPeerVendorRowStatus(EnumDbpPeerVendorRowStatus x){
		//no need to handle
	}

	@Override
	public EnumDbpPeerVendorStorageType getDbpPeerVendorStorageType(){
		try{
			return new EnumDbpPeerVendorStorageType(dbpPeerVendorEntry.getDbpPeerVendorStorageType());
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer vendor storage type for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPeerVendorStorageType();
		}
	}

	@Override
	public void setDbpPeerVendorStorageType(EnumDbpPeerVendorStorageType x){
		//no need to handle
	}

	@Override
	public void checkDbpPeerVendorStorageType(EnumDbpPeerVendorStorageType x){
		//no need to handle
	}

	@Override
	public EnumDbpPeerVendorId getDbpPeerVendorId()  {
		return new EnumDbpPeerVendorId(dbpPeerVendorEntry.getDbpPeerVendorId());
	}

	@Override
	public void setDbpPeerVendorId(EnumDbpPeerVendorId x){
		//no need to handle
	}

	@Override
	public void checkDbpPeerVendorId(EnumDbpPeerVendorId x){
		//no need to handle
	}

	@Override
	public Long getDbpPeerVendorIndex()  {
		return index;
	}

	@Override
	public Long getDbpPeerIndex()  {
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return peerConfig.getDbpPeerIndex();
	}
	
	private DiameterPeerConfig getDiameterPeerConfig(){
		DiameterPeerConfig peerConfig = diameterConfigProvider.getPeerConfig(hostIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Base Peer Config not found for peer:" + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Peer Config not found for peer:" + hostIdentity);
		}
		return peerConfig;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.PEER_VENDOR_TABLE + hostIdentity + "-" + getDiameterPeerConfig().getPeerIpAddresses() + "-" + getDbpPeerVendorId();
	}

}
