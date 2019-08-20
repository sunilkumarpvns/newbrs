package com.elitecore.diameterapi.mibs.cc.extended.hostcfgs;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaHostIpAddrEntry;
import com.elitecore.diameterapi.mibs.cc.autogen.EnumDccaHostIpAddrType;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerIpAddressTable;

public class DccaHostIpAddrEntryImpl extends DccaHostIpAddrEntry {

	private static final String MODULE = "DCC-HOST-IP-ADDR-ENTRY";
	private String hostIdentity;
	transient private DiameterConfigProvider configProvider;
	
	public DccaHostIpAddrEntryImpl(String diameterHostIdentity, DiameterConfigProvider configProvider) {
		this.hostIdentity = diameterHostIdentity;
		this.configProvider = configProvider;
	}

	@Override
	public Byte[] getDccaHostIpAddress(){
		DiameterPeerConfig peerConfig = getCCHostConfig();
		if(peerConfig == null){
			return new Byte [0];
		}
		DiameterBasePeerIpAddressTable[] tables =  peerConfig.getPeerIpAddressIndex();
		if (tables != null && tables.length > 0) {
			String ipAddressStr =	tables[0].getPeerIpAddress();
			if(ipAddressStr == null || ipAddressStr.trim().length() == 0){
				LogManager.getLogger().error(MODULE, "No IP Address found from diameter cc host config for peer: " + hostIdentity);
				return new Byte [0];
			}
			byte[] bs = ipAddressStr.getBytes();
			Byte[] bytes = new Byte[bs.length];
			for(int i=0; i< bs.length ; i++){
				bytes[i] = bs[i];
			}
			return bytes;
		}
		return new Byte [0];
	}

	@Override
	public EnumDccaHostIpAddrType getDccaHostIpAddrType(){

		DiameterPeerConfig peerConfig = getCCHostConfig();
		if(peerConfig == null){
			return new EnumDccaHostIpAddrType();
		}
		
		try{
			DiameterBasePeerIpAddressTable[] tables =  peerConfig.getPeerIpAddressIndex();
			if (tables != null && tables.length > 0) {
				return new EnumDccaHostIpAddrType(tables[0].getdbpPeerIpAddressType());
			} else {
				return new EnumDccaHostIpAddrType();
			}
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating HostIpAddrType for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDccaHostIpAddrType();
		}
	}

	@Override
	public Long getDccaHostIpAddrIndex(){
		DiameterPeerConfig peerConfig = getCCHostConfig();
		if(peerConfig == null){
			return 0L;
		}
		return peerConfig.getDbpPeerIndex();
	}
	
	private DiameterPeerConfig getCCHostConfig() {
		DiameterPeerConfig ccHostConfig = configProvider.getPeerConfig(hostIdentity);
		if(ccHostConfig == null){
			LogManager.getLogger().error(MODULE, "CC Host Config not found for peer:" + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.CCSTATISTICSNOTFOUND, MODULE, "CC Host Config not found for peer:" + hostIdentity);
		}
		return ccHostConfig;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.CCA_HOST_IPADDR_TABLE + hostIdentity +"-"+getCCHostConfig().getPeerIpAddresses();
	}
}