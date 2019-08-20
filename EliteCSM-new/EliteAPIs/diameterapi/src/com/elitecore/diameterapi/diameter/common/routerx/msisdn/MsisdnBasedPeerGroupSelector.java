package com.elitecore.diameterapi.diameter.common.routerx.msisdn;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.collections.Trie;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.core.systemx.esix.loadBalancer.LoadBalancer;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerCommunicatorGroup;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerCommunicatorGroupImpl;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.selector.PeerCommunicatorGroupSelector;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

/**
 * 
 * This will select {@link DiameterPeerCommunicatorGroup} based on MSISDN.
 * This maintains MSISDN Tree and based on Msisdn Attribute arrived in {@link DiameterRequest}
 * selects a {@link DiameterPeerCommunicatorGroup}.  
 * 
 * @author monica.lulla
 *
 */
public class MsisdnBasedPeerGroupSelector implements PeerCommunicatorGroupSelector {

	private static final String MODULE = "MSISDN-BSD-PEER-GRP-SELECTOR";
	private final RouterContext routerContext;
	private final List<String> peers;
	private final Trie<DiameterPeerCommunicatorGroup> msisdnTrie;

	private MsisdnBasedRoutingTableData msisdnBasedRoutingTableData;

	public MsisdnBasedPeerGroupSelector(final RouterContext routerContext, 
			MsisdnBasedRoutingTableData msisdnBasedRoutingTableData) {
		this.routerContext = routerContext;
		this.msisdnBasedRoutingTableData = msisdnBasedRoutingTableData;
		this.peers = new ArrayList<String>();
		this.msisdnTrie = new Trie<DiameterPeerCommunicatorGroup>();
	}

	public void init(boolean addlistener) throws InitializationFailedException {
		
		if (Collectionz.isNullOrEmpty(msisdnBasedRoutingTableData.getEntries())) {
			throw new InitializationFailedException(
					"No Msisdn Entries available for Msisdn-Based-Routing-Table: " + 
					msisdnBasedRoutingTableData.getName());
		}
		for (MsisdnBasedRouteEntryData msisdnBasedRouteEntryData : msisdnBasedRoutingTableData.getEntries()) {

			msisdnTrie.put(msisdnBasedRouteEntryData.getMsisdnRange(),
					createPeerGroupCommunicatorFor(msisdnBasedRouteEntryData, addlistener));
		}
	}

	private DiameterPeerCommunicatorGroup createPeerGroupCommunicatorFor(
			MsisdnBasedRouteEntryData msisdnBasedRouteEntryData, boolean addlistener) {
		
		DiameterPeerCommunicatorGroup peerCommunicatorGroup = new DiameterPeerCommunicatorGroupImpl(
				null, LoadBalancerType.ROUND_ROBIN, addlistener);
		if (msisdnBasedRouteEntryData.getPrimaryPeer() != null) {
			DiameterPeerCommunicator peerCommunicator = routerContext.getPeerCommunicator(msisdnBasedRouteEntryData.getPrimaryPeer());
			if (peerCommunicator != null) {
				peerCommunicatorGroup.addCommunicator(peerCommunicator);
				peers.add(peerCommunicator.getName());
			}
		}
		
		if (msisdnBasedRouteEntryData.getSecondaryPeer() != null) {
			DiameterPeerCommunicator peerCommunicator = routerContext.getPeerCommunicator(msisdnBasedRouteEntryData.getSecondaryPeer());
			if (peerCommunicator != null) {
				peerCommunicatorGroup.addCommunicator(peerCommunicator, LoadBalancer.SECONDARY_WEIGHT);
				peers.add(peerCommunicator.getName());
			}
		}
		return peerCommunicatorGroup;
	}

	@Override
	public DiameterPeerCommunicatorGroup select(DiameterRequest diameterRequest) {
		
		String msisdn = getMsisdn(diameterRequest); 
		if (msisdn == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Msisdn not found in request with Session-ID=" + 
						diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			return null;
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Selecting Msisdn-based entry for Msisdn: " + msisdn);
		}
		return msisdnTrie.longestPrefixKeyMatch(msisdn);
	}

	private String getMsisdn(DiameterRequest diameterRequest) {
		
		List<String> msisdnIdentityAttributes = msisdnBasedRoutingTableData.getMsisdnIdentityAttributes();
		String msisdn;
		for (int i = 0; i < msisdnIdentityAttributes.size(); i++) {
			msisdn = diameterRequest.getAVPValue(msisdnIdentityAttributes.get(i), 
					DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
			if (Strings.isNullOrBlank(msisdn) == false) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Msisdn: " + msisdn + 
							" found from Msisdn-Attribute: " + msisdnIdentityAttributes.get(i) + 
							" arrived in request with Session-ID=" + 
							diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
				return DiameterUtility.formatMsisdn(msisdn,
						msisdnBasedRoutingTableData.getMsisdnLength(),
						msisdnBasedRoutingTableData.getMcc());
			}
		}
		return null;
	}

	@Override
	public List<String> peers() {
		return peers;
	}
	
}
