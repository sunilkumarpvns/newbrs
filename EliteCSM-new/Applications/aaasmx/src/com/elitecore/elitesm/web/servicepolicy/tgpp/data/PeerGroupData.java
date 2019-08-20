package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.data.PeerInfo;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;

public class PeerGroupData {

	private String id;
	private String name;
	private List<PeerInfoImpl> peers = new ArrayList<PeerInfoImpl>();
	private int retryLimit = 1;
	private boolean stateFull = true;
	
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "retry-limit")
	public int getRetryLimit() {
		return retryLimit;
	}

	public void setRetryLimit(int retryLimit) {
		this.retryLimit = retryLimit;
	}

	@XmlElement(name = "peer-info")
	public List<PeerInfoImpl> getPeers() {
		return peers;
	}

	@XmlElement(name = "statefull")
	public boolean isStateFull() {
		return stateFull;
	}

	public void setStateFull(boolean stateFull) {
		this.stateFull = stateFull;
	}
	
	public DiameterPeerGroupParameter createDiameterPeerGroupParameter() {
		Map<String, Integer> peerNameToWeightage = new LinkedHashMap<String, Integer>();
		for (PeerInfo peerInfo : getPeers()) {
			peerNameToWeightage.put(peerInfo.getPeerName(), peerInfo.getLoadFactor());
		}

		return new DiameterPeerGroupParameter(getName(), peerNameToWeightage,
				LoadBalancerType.ROUND_ROBIN, 
				isStateFull(), 
				1,
				// TODO this should be removed in case when retry is not required
				3000);

	}
}
