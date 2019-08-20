package com.elitecore.diameterapi.mibs.custom.extended;

import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.EnumPeerSecurity;
import com.elitecore.diameterapi.mibs.custom.autogen.PeerInfoEntry;
import com.sun.management.snmp.SnmpStatusException;

public class PeerInfoEntryMbeanImpl extends PeerInfoEntry {
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "PEER-CONFIG";
	private final String peerIdentity;
	transient private final DiameterConfigProvider diameterConfigProvider;
	private String indexValue;

	public PeerInfoEntryMbeanImpl(String peerIdentity,DiameterConfigProvider diameterConfigProvider) {
		this.peerIdentity = peerIdentity;
		this.diameterConfigProvider = diameterConfigProvider;
	}

	private DiameterPeerConfig getDiameterPeerConfig(){
		DiameterPeerConfig peerConfig = diameterConfigProvider.getPeerConfig(peerIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Peer Configuration not found for peer: " + peerIdentity);
		}
		return peerConfig;
	}
	
	@Override
	@Column(name ="peerIdentity", type = Types.VARCHAR)
	public String getPeerIdentity() throws SnmpStatusException {
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return "NOT AVAILABLE";
		}
		
		return peerConfig.getPeerId();
	}

	@Override
	@Column(name ="peerIndex", type = Types.BIGINT)
	public Long getPeerIndex() throws SnmpStatusException {
		DiameterPeerConfig  peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return peerConfig.getDbpPeerIndex();
	}
	

	@Override
	@Column(name ="peerSecurity", type = Types.VARCHAR)
	public EnumPeerSecurity getPeerSecurity() throws SnmpStatusException {
		return new EnumPeerSecurity(getDiameterPeerConfig().getDbpPeerSecurity().protocol);
	}

	@Override
	@Column(name ="peerTransportProtocol", type = Types.VARCHAR)
	public String getPeerTransportProtocol() throws SnmpStatusException {
		return getDiameterPeerConfig().getDbpPeerTransportProtocol().protocolTypeStr;
	}

	@Override
	@Column(name ="connectionInitByPeer", type = Types.VARCHAR)
	public String getConnectionInitByPeer() throws SnmpStatusException {
		return String.valueOf(getDiameterPeerConfig().isConnectionInitiationEnabled());
	}

	@Override
	public String getPeerIndexValue() throws SnmpStatusException {
		return this.indexValue;
	}

	public void setIndexValue(String indexValue) {
		this.indexValue = indexValue;
	}
	
	public String getObjectName() {
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_INFO_TABLE 
				+ peerIdentity  + "-" + getDiameterPeerConfig().getPeerIpAddresses();
	}
}