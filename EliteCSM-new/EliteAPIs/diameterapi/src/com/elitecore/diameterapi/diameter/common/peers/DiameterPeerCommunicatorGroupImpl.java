package com.elitecore.diameterapi.diameter.common.peers;

import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class DiameterPeerCommunicatorGroupImpl extends ESCommunicatorGroupImpl<DiameterPeerCommunicator> implements DiameterPeerCommunicatorGroup{

	private LogicalExpression ruleSet;
	
	public DiameterPeerCommunicatorGroupImpl(LogicalExpression ruleSet, LoadBalancerType loadBalancerType) {
		super(loadBalancerType);
		this.ruleSet = ruleSet;
	}
	
	public DiameterPeerCommunicatorGroupImpl(LogicalExpression ruleSet,  
			LoadBalancerType loadBalancerType, boolean checkAlive) {
		super(loadBalancerType , checkAlive);
		this.ruleSet = ruleSet;
	}
		
	@Override
	public boolean assignGroup(DiameterPacket diameterPacket) {
		ValueProvider valueProvider = new DiameterAVPValueProvider(diameterPacket);
		if (ruleSet != null){
			return ruleSet.evaluate(valueProvider);
		} else {
			return true;
		}
	}

	@Override
	public String getNextPeer() {
		DiameterPeerCommunicator communicator = getCommunicator();
		if (communicator != null){
			return communicator.getName();
		}
		return null;
	}

	@Override
	public String getSecondaryPeer(DiameterPeerCommunicator... ignoreCommunicatorList) {
		DiameterPeerCommunicator secondaryCommunicator = getSecondaryCommunicator(ignoreCommunicatorList);
		if(secondaryCommunicator != null){
			return secondaryCommunicator.getName();
		}
		return null;
	}
}
