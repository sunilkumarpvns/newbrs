package com.elitecore.diameterapi.mibs.base.extended.peerconf;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPeerEntry;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerRowStatus;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerSecurity;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerStorageType;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerTransportProtocol;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;

public class DbpPeerEntryImpl extends DbpPeerEntry {
	
	private static final String MODULE = "DBP-PEER-ENTRY";
	private String hostIdentity;
	transient private DiameterConfigProvider configProvider;

	public DbpPeerEntryImpl(String peerIdentity, DiameterConfigProvider configProvider) {
		this.hostIdentity = peerIdentity;
		this.configProvider = configProvider;
	}

	@Override
	public EnumDbpPeerRowStatus getDbpPeerRowStatus(){
		
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return new EnumDbpPeerRowStatus();
		}
		
		try{
			return new EnumDbpPeerRowStatus(peerConfig.getPeerRowStatus().code);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer row status for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPeerRowStatus();
		}
	}

	@Override
	public void setDbpPeerRowStatus(EnumDbpPeerRowStatus x){}

	@Override
	public EnumDbpPeerStorageType getDbpPeerStorageType(){
		
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return new EnumDbpPeerStorageType();
		}
		try{
			return new EnumDbpPeerStorageType(peerConfig.getPeerStorageType().code);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer storage type for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPeerStorageType();
		}
	}

	@Override
	public void setDbpPeerStorageType(EnumDbpPeerStorageType x){}

	@Override
	public void checkDbpPeerStorageType(EnumDbpPeerStorageType x){}

	@Override
	public String getDbpPeerFirmwareRevision()  {
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return "";
		}
		
		return String.valueOf(peerConfig.getPeerFirmwareRevison());
	}

	@Override
	public EnumDbpPeerSecurity getDbpPeerSecurity()  {
		
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return new EnumDbpPeerSecurity();
		}
		
		try{
			return new EnumDbpPeerSecurity(peerConfig.getDbpPeerSecurity().protocol);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer security for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPeerSecurity();
		}
		
	}

	@Override
	public EnumDbpPeerTransportProtocol getDbpPeerTransportProtocol(){
		
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return new EnumDbpPeerTransportProtocol();
		}
		
		try{
			return new EnumDbpPeerTransportProtocol(peerConfig.getDbpPeerTransportProtocol().code);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer transport protocol for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPeerTransportProtocol();
		}
	}

	@Override
	public Long getDbpPeerPortListen() {
		
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		
		return (long) peerConfig.getDbpPeerPortListen();
	}

	@Override
	public void setDbpPeerPortListen(Long x)  {}

	@Override 	
	public void checkDbpPeerPortListen(Long x)  {}

	@Override
	public Long getDbpPeerPortConnect()  {
		
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32((long) peerConfig.getDbpPeerPortConnect());
	}

	@Override
	public String getDbpPeerId()  {
		
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return "";
		}
		
		return peerConfig.getPeerId();
	}

	@Override
	public void setDbpPeerId(String x)  {}

	@Override
	public void checkDbpPeerId(String x)  {}

	@Override
	public Long getDbpPeerIndex()  {
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerConfig.getDbpPeerIndex());
	}

	private DiameterPeerConfig getDiameterPeerConfig(){
		DiameterPeerConfig peerConfig = configProvider.getPeerConfig(hostIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Base Peer Config not found for peer:" + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Peer Config not found for peer:" + hostIdentity);
		}
		return peerConfig;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.PEER_TABLE + hostIdentity + "-" +getDiameterPeerConfig().getPeerIpAddresses();
	}
}
