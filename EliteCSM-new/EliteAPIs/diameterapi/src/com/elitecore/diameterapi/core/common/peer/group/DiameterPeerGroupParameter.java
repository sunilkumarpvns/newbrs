package com.elitecore.diameterapi.core.common.peer.group;

import java.util.Map;

import com.elitecore.core.systemx.esix.LoadBalancerType;

/**
 * Created by harsh on 6/17/15.
 */
public class DiameterPeerGroupParameter {
    private final Map<String, Integer> peers;
    private final String name;
    private final LoadBalancerType loadBalancerType;
    private final boolean isStateFull;
    private final int maxRetryCount;
    private final long transactionTimeout;
    
    public DiameterPeerGroupParameter(String name,
                                      Map<String, Integer> peers,
                                      LoadBalancerType loadBalancerType,
                                      boolean isStateFull,
                                      int maxRetryCount,
                                      long transactionTimeout
                                      ) {
        this.peers = peers;
        this.name = name;
        this.loadBalancerType = loadBalancerType;
        this.isStateFull = isStateFull;
        this.maxRetryCount = maxRetryCount <= peers.size() ? maxRetryCount : peers.size();
        this.transactionTimeout = transactionTimeout;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public long getTransactionTimeout() {
        return transactionTimeout;
    }

    public Map<String, Integer> getPeers() {
        return peers;
    }

    public LoadBalancerType getLoadBalancerType() {
        return  loadBalancerType;
    }

    public String getName() {
        return name;
    }

    public boolean isStateFull() {
        return isStateFull;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isStateFull ? 1231 : 1237);
		result = prime * result + ((loadBalancerType == null) ? 0 : loadBalancerType.hashCode());
		result = prime * result + maxRetryCount;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((peers == null) ? 0 : peers.hashCode());
		result = prime * result + (int) (transactionTimeout ^ (transactionTimeout >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiameterPeerGroupParameter other = (DiameterPeerGroupParameter) obj;
		if (isStateFull != other.isStateFull)
			return false;
		if (loadBalancerType != other.loadBalancerType)
			return false;
		if (maxRetryCount != other.maxRetryCount)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (peers == null) {
			if (other.peers != null)
				return false;
		} else if (!peers.equals(other.peers))
			return false;
		if (transactionTimeout != other.transactionTimeout)
			return false;
		return true;
	}
    
    
}
