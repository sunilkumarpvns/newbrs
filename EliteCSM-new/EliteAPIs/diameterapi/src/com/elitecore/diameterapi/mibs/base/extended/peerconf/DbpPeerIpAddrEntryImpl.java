package com.elitecore.diameterapi.mibs.base.extended.peerconf;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPeerIpAddrEntry;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPeerIpAddressType;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerIpAddressTable;

public class DbpPeerIpAddrEntryImpl extends DbpPeerIpAddrEntry {

	private static final String MODULE = "DBP-PEER-IP-ADD-ENTRY";

	private static final long serialVersionUID = 1L;
	private String hostIdentity;
	transient private DiameterConfigProvider configProvider;
	private long index;
	transient private DiameterBasePeerIpAddressTable dbpPeerIPAddressEntry;
	
	public DbpPeerIpAddrEntryImpl(int index, String hostIdentity, 
			DiameterConfigProvider configProvider, 
			DiameterBasePeerIpAddressTable dbpPeerIPAddressEntries) {
		
		this.index = index;
		this.hostIdentity = hostIdentity;
		this.configProvider = configProvider;
		this.dbpPeerIPAddressEntry = dbpPeerIPAddressEntries;
	}

	@Override
	public Byte[] getDbpPeerIpAddress()  {
		String ipAddressStr =	dbpPeerIPAddressEntry.getPeerIpAddress();
		if(ipAddressStr == null || ipAddressStr.trim().length() == 0){
			LogManager.getLogger().error(MODULE, "No IP Address found from peer("+hostIdentity+") Config");
			return new Byte [0];
		}
		byte[] bs = ipAddressStr.getBytes();
		Byte[] bytes = new Byte[bs.length];
		for(int i=0; i< bs.length ; i++){
			bytes[i] = bs[i];
		}
		return bytes;
	}

	@Override
	public EnumDbpPeerIpAddressType getDbpPeerIpAddressType(){

		return new EnumDbpPeerIpAddressType(dbpPeerIPAddressEntry.getdbpPeerIpAddressType());
	}

	@Override
	public Long getDbpPeerIpAddressIndex()  {
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
		DiameterPeerConfig peerConfig = configProvider.getPeerConfig(hostIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Base Peer Config not found for peer:" + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Peer Config not found for peer:" + hostIdentity);
		}
		return peerConfig;
	}
	
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.PEER_IP_ADDR_TABLE + hostIdentity+ "-" + getDiameterPeerConfig().getPeerIpAddresses();
	}
}
