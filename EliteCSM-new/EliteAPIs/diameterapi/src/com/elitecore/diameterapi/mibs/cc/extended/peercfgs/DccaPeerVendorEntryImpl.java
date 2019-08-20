package com.elitecore.diameterapi.mibs.cc.extended.peercfgs;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPeerVendorEntry;
import com.elitecore.diameterapi.mibs.cc.autogen.EnumDccaPeerVendorRowStatus;
import com.elitecore.diameterapi.mibs.cc.autogen.EnumDccaPeerVendorStorageType;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;

public class DccaPeerVendorEntryImpl extends DccaPeerVendorEntry {

	private static final String MODULE = "DCC-PEER-VENDOR-ENTRY";
	transient private DiameterBasePeerVendorTable peerVendorTable;
	transient private DiameterConfigProvider diameterConfigProvider;
	private String hostIdentity;


	public DccaPeerVendorEntryImpl(DiameterBasePeerVendorTable peerVendorTable,
			DiameterConfigProvider diameterConfigProvider, String hostIdentity) {
		this.peerVendorTable = peerVendorTable;
		this.diameterConfigProvider = diameterConfigProvider;
		this.hostIdentity = hostIdentity;
	}

	@Override
	public EnumDccaPeerVendorRowStatus getDccaPeerVendorRowStatus(){
		try{
			return new EnumDccaPeerVendorRowStatus(peerVendorTable.getDbpPeerVendorRowStatus());
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating PeerVendorRowStatus for peer("+peerVendorTable.getDbpPeerVendorId()+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}

		return new EnumDccaPeerVendorRowStatus();
	}

	@Override
	public void setDccaPeerVendorRowStatus(EnumDccaPeerVendorRowStatus x){
	}

	@Override
	public EnumDccaPeerVendorStorageType getDccaPeerVendorStorageType(){
		try{
			return new EnumDccaPeerVendorStorageType(peerVendorTable.getDbpPeerVendorRowStatus());
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating PeerVendorStorageType for peer("+peerVendorTable.getDbpPeerVendorId()+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}

		return new EnumDccaPeerVendorStorageType();
	}

	@Override
	public void setDccaPeerVendorStorageType(EnumDccaPeerVendorStorageType x){
	}

	@Override
	public void checkDccaPeerVendorStorageType(EnumDccaPeerVendorStorageType x){
	}

	@Override
	public Long getDccaPeerVendorId(){
		try{
			Long.parseLong(peerVendorTable.getDbpPeerVendorId());
		}catch (NumberFormatException  e){
			LogManager.getLogger().error(MODULE, "Error while providing Dcca Peer Vendor Id for peer("+peerVendorTable.getDbpPeerVendorId()+"). Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return 0L;
	}

	@Override
	public void setDccaPeerVendorId(Long x){
	}

	@Override
	public void checkDccaPeerVendorId(Long x){
	}

	@Override
	public Long getDccaPeerVendorIndex(){
		return (long) peerVendorTable.getDbpPeerVendorIndex();
	}

	@Override
	public Long getDccaPeerIndex(){
		return getDiameterPeerConfig().getDbpPeerIndex();
	}
	
	private DiameterPeerConfig getDiameterPeerConfig(){
		DiameterPeerConfig peerConfig = diameterConfigProvider.getPeerConfig(hostIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Peer Config not found for peer: " + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.CCSTATISTICSNOTFOUND, MODULE, "Peer Config not found for peer: " + hostIdentity);
		}
		return peerConfig;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.CCA_PEER_VENDOR_TABLE + hostIdentity + "-" + getDiameterPeerConfig().getPeerIpAddresses() + "-" + getDccaPeerVendorId();
	}
}