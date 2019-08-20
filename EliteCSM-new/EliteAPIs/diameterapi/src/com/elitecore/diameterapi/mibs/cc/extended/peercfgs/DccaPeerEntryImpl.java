package com.elitecore.diameterapi.mibs.cc.extended.peercfgs;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPeerEntry;
import com.elitecore.diameterapi.mibs.cc.autogen.EnumDccaPeerRowStatus;
import com.elitecore.diameterapi.mibs.cc.autogen.EnumDccaPeerStorageType;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;

public class DccaPeerEntryImpl extends DccaPeerEntry{

	private static final String MODULE = "DCC-PEER-ENTRY";
	private String peerIdentity;
	transient private DiameterConfigProvider diameterConfigProvider;

	public DccaPeerEntryImpl(String peerIdentity, DiameterConfigProvider diameterConfigProvider) {
		this.peerIdentity = peerIdentity;
		this.diameterConfigProvider = diameterConfigProvider;
	}

	@Override
	public EnumDccaPeerRowStatus getDccaPeerRowStatus(){
		DiameterPeerConfig ccPeerConfig = getPeerConfiguration();
		if(ccPeerConfig == null){
			return new EnumDccaPeerRowStatus();
		}
		try{
			return new EnumDccaPeerRowStatus(ccPeerConfig.getPeerRowStatus().code);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DccaPeerRowStatus for peer("+peerIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
		return new EnumDccaPeerRowStatus();
	}

	@Override
	public void setDccaPeerRowStatus(EnumDccaPeerRowStatus x){
	}

	@Override
	public EnumDccaPeerStorageType getDccaPeerStorageType(){

		DiameterPeerConfig ccPeerConfig = getPeerConfiguration();
		if(ccPeerConfig == null){
			return new EnumDccaPeerStorageType();
		}
		try{
			return new EnumDccaPeerStorageType(ccPeerConfig.getPeerStorageType().code);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating PeerStorageType for peer("+peerIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
		}
		return new EnumDccaPeerStorageType();
	}

	@Override
	public void setDccaPeerStorageType(EnumDccaPeerStorageType x){
	}

	@Override
	public void checkDccaPeerStorageType(EnumDccaPeerStorageType x){
	}

	@Override
	public Long getDccaPeerFirmwareRevision(){
		DiameterPeerConfig ccPeerConfig = getPeerConfiguration();
		if(ccPeerConfig == null){
			return 0L;
		}
		return (long) ccPeerConfig.getPeerFirmwareRevison();
	}

	@Override
	public void setDccaPeerFirmwareRevision(Long x){
	}

	@Override
	public void checkDccaPeerFirmwareRevision(Long x){
	}

	@Override
	public String getDccaPeerId(){
		DiameterPeerConfig ccPeerConfig = getPeerConfiguration();
		if(ccPeerConfig == null){
			return "";
		}
		return ccPeerConfig.getPeerId();
	}

	@Override
	public void setDccaPeerId(String x){
	}

	@Override
	public void checkDccaPeerId(String x){
	}

	@Override
	public Long getDccaPeerIndex(){
		DiameterPeerConfig ccPeerConfig = getPeerConfiguration();
		if(ccPeerConfig == null){
			return 0L;
		}
		return ccPeerConfig.getDbpPeerIndex();
	}

	private DiameterPeerConfig getPeerConfiguration(){
		DiameterPeerConfig ccPeerStatistic = diameterConfigProvider.getPeerConfig(peerIdentity);
		if(ccPeerStatistic == null){
			LogManager.getLogger().error(MODULE, "CC Peer Config not found for peer:" + peerIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.CCSTATISTICSNOTFOUND, MODULE, "CC Peer Config not found for peer:" + peerIdentity);
		}
		return ccPeerStatistic;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.CCA_PEER_TABLE + peerIdentity+"-"+getPeerConfiguration().getPeerIpAddresses();
	}
}