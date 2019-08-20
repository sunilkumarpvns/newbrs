package com.elitecore.diameterapi.mibs.config;

import java.util.Map;

import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;

public interface DiameterConfigProvider {

	public String getAllPeerConfigSummary();

	public String getPeerConfigSummary(String hostIdentity);

	public String getDCCPeerConfig(String hostIdentity, DiameterPeerConfig peerConfig);

	public String getAllDCCPeerConfigSummary();

	public String getDCCPeerConfig(String hostIdentity);

	public Map<String, DiameterPeerConfig> getPeerConfigMap();
	
	public DiameterPeerConfig getPeerConfig(String hostIdentity);

	public DiameterPeerState getPeerState(String hostIdentity);

}
