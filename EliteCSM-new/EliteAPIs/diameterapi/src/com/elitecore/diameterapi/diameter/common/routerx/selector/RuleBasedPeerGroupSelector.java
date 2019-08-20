package com.elitecore.diameterapi.diameter.common.routerx.selector;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.data.PeerGroup;
import com.elitecore.diameterapi.diameter.common.data.PeerInfo;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerCommunicatorGroup;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerCommunicatorGroupImpl;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class RuleBasedPeerGroupSelector implements PeerCommunicatorGroupSelector {

	private static final String MODULE = "RULE-BSD-PEER-GRP-SELECTOR";
	private List<DiameterPeerCommunicatorGroup> communicatorGroups;
	private RouterContext diameterRouterContext;
	private List<String> peers;
	private List<PeerGroupImpl> peerGroupList;
	
	public RuleBasedPeerGroupSelector(List<PeerGroupImpl> peerGroupList, 
			final RouterContext diameterRouterContext) {
		
		this.peerGroupList = peerGroupList;
		this.communicatorGroups = new ArrayList<DiameterPeerCommunicatorGroup>();
		this.diameterRouterContext = diameterRouterContext;
		this.peers = new ArrayList<String>();
	}
	
	/**
	 * 	Initializes Peer Communicator Groups of Peer Groups in Routing Entry Data 
	 *   
	 * @param peerGroupList is the List of Peer Groups.
	 * @param addlistener true if you want to register Peer Listener for Routing Entry Peer Groups.
	 * @throws InitializationFailedException when rule compilation fails 
	 */
	@Override
	public void init(boolean addlistener) throws InitializationFailedException {

		if (peerGroupList == null) {
			if (LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "No Peer Groups Found.");
			}
			return;
		}
		for (PeerGroup peerGroup : peerGroupList){

			String peerGroupAdvancedCondition = peerGroup.getAdvancedConditionStr();
			LogicalExpression logicalExpForPeergroup = null;

			if(peerGroupAdvancedCondition != null  && peerGroupAdvancedCondition.trim().length() > 0 ){
				Compiler compiler = Compiler.getDefaultCompiler();
				try {
					logicalExpForPeergroup =  compiler.parseLogicalExpression(peerGroupAdvancedCondition);
				} catch (InvalidExpressionException e) {
					throw new InitializationFailedException(e);
				}
			}
			DiameterPeerCommunicatorGroup communicatorGroup = new DiameterPeerCommunicatorGroupImpl(logicalExpForPeergroup, 
					LoadBalancerType.ROUND_ROBIN, addlistener);

			List<PeerInfoImpl> peerInfos = peerGroup.getPeerList();
			for (PeerInfo peerInfo : peerInfos){
				DiameterPeerCommunicator peerCommunicator = diameterRouterContext.getPeerCommunicator(peerInfo.getPeerName());
				if (peerCommunicator != null) {
					communicatorGroup.addCommunicator(peerCommunicator, peerInfo.getLoadFactor());
					peers.add(peerCommunicator.getName());
				}
			}

			//Adding Peer Comm Groups to its List
			communicatorGroups.add(communicatorGroup);
		}	
	}
	
	@Override
	public DiameterPeerCommunicatorGroup select(DiameterRequest diameterRequest) {

		if (LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Fetching Rule-based Peer Group");
		}
		
		for(int i = 0 ; i < communicatorGroups.size() ; i++) {
			if (communicatorGroups.get(i).assignGroup(diameterRequest)) {
				return communicatorGroups.get(i);
			}
		}
		return null;
	}

	@Override
	public List<String> peers() {
		return peers;
	}

}
