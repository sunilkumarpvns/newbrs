package com.elitecore.aaa.diameter.conf.impl;

import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
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
	private long transactionTimeoutInMs = 2000;
	private String geoRedunduntGroupId;
	
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "gr-group-id")
	public String getGeoRedunduntGroupId() {
		return geoRedunduntGroupId;
	}

	public void setGeoRedunduntGroupId(String geoRedunduntGroupId) {
		this.geoRedunduntGroupId = geoRedunduntGroupId;
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

	@XmlElement(name="transaction-timeout")
	public long getTransactionTimeoutInMs() {
		return transactionTimeoutInMs;
	}

	public void setTransactionTimeoutInMs(long transactionTimeoutInMs) {
		this.transactionTimeoutInMs = transactionTimeoutInMs;
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
				getTransactionTimeoutInMs());

	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "PeerGroup Name", this.name));
		out.println(format("%-30s: %s", "Peers", peers));
		out.println(format("%-30s: %s", "Retry Limit", this.retryLimit));
		out.println(format("%-30s: %s", "Statefull", this.stateFull));
		out.println(format("%-30s: %s", "Transaction Timeout(ms)", this.transactionTimeoutInMs));
		out.println(format("%-30s: %s", "Geo Redundunt Group Id", this.geoRedunduntGroupId != null ? this.geoRedunduntGroupId : "-NA-"));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}
	
}
