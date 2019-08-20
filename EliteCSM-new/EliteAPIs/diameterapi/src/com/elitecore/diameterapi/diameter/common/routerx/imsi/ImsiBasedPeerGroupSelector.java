package com.elitecore.diameterapi.diameter.common.routerx.imsi;

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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This will select {@link DiameterPeerCommunicatorGroup} based on IMSI.
 * This maintains IMSI Tree and based on Imsi Attribute arrived in {@link DiameterRequest}
 * selects a {@link DiameterPeerCommunicatorGroup}.  
 * 
 * @author monica.lulla
 *
 */
public class ImsiBasedPeerGroupSelector implements PeerCommunicatorGroupSelector {

	private static final String MODULE = "IMSI-BSD-PEER-GRP-SELECTOR";
	private final RouterContext routerContext;
	private ImsiBasedRoutingTableData imsiTableRoutingTableData;
	private final List<String> peers;
	private final Trie<DiameterPeerCommunicatorGroup> imsiTrie;

	public ImsiBasedPeerGroupSelector(final RouterContext routerContext, 
			ImsiBasedRoutingTableData imsiBasedRoutingTableData) {
		this.routerContext = routerContext;
		this.imsiTableRoutingTableData = imsiBasedRoutingTableData;
		this.peers = new ArrayList<String>();
		this.imsiTrie = new Trie<DiameterPeerCommunicatorGroup>();
	}

	public void init(boolean addlistener) throws InitializationFailedException {
		
		if (Collectionz.isNullOrEmpty(imsiTableRoutingTableData.getEntries())) {
			throw new InitializationFailedException(
					"No Imsi Entries available for Imsi-Table: " + 
					imsiTableRoutingTableData.getName());
		}
		
		for (ImsiBasedRouteEntryData imsiBasedRouteEntryData : imsiTableRoutingTableData.getEntries()) {

			imsiTrie.put(imsiBasedRouteEntryData.getImsiRange(),
					createPeerGroupCommunicatorFor(imsiBasedRouteEntryData, addlistener));
		}
	}

	private DiameterPeerCommunicatorGroup createPeerGroupCommunicatorFor(
			ImsiBasedRouteEntryData imsiBasedRouteEntryData, boolean addlistener) {
		
		DiameterPeerCommunicatorGroup peerCommunicatorGroup = new DiameterPeerCommunicatorGroupImpl(
				null, LoadBalancerType.ROUND_ROBIN, addlistener);
		if (imsiBasedRouteEntryData.getPrimaryPeer() != null) {
			DiameterPeerCommunicator peerCommunicator = routerContext.getPeerCommunicator(imsiBasedRouteEntryData.getPrimaryPeer());
			if (peerCommunicator != null) {
				peerCommunicatorGroup.addCommunicator(peerCommunicator);
				peers.add(peerCommunicator.getName());
			}
		}
		
		if (imsiBasedRouteEntryData.getSecondaryPeer() != null) {
			DiameterPeerCommunicator peerCommunicator = routerContext.getPeerCommunicator(imsiBasedRouteEntryData.getSecondaryPeer());
			if (peerCommunicator != null) {
				peerCommunicatorGroup.addCommunicator(peerCommunicator, LoadBalancer.SECONDARY_WEIGHT);
				peers.add(peerCommunicator.getName());
			}
		}
		return peerCommunicatorGroup;
	}

	@Override
	public DiameterPeerCommunicatorGroup select(DiameterRequest diameterRequest) {
		
		String imsi = getImsi(diameterRequest); 
		if (Strings.isNullOrBlank(imsi)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Imsi not found in request with Session-ID=" + 
						diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			return null;
		}
		imsi = DiameterUtility.getIMSIFromIdentity(imsi);
		if (imsi.length() < 15 ) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Invalid IMSI: " + imsi + 
						" received in request with Session-ID=" + 
						diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) +
						", Reason: IMSI shoud be atleast 15 digit decimal value" );
			}
			return null;
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Selecting Imsi-based entry for Imsi: " + imsi);
		}
		return imsiTrie.longestPrefixKeyMatch(imsi);
	}

	private String getImsi(DiameterRequest diameterRequest) {
		
		List<String> imsiIdentityAttributes = imsiTableRoutingTableData.getImsiIdentityAttributes();
		String imsi;
		for (int i = 0; i < imsiIdentityAttributes.size(); i++) {
			imsi = diameterRequest.getAVPValue(imsiIdentityAttributes.get(i), 
					DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
			if (Strings.isNullOrBlank(imsi) == false) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Imsi: " + imsi + 
							" found from Imsi-Attribute: " + imsiIdentityAttributes.get(i) + 
							" arrived in request with Session-ID=" + 
							diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
				return imsi;
			}
		}
		return null;
	}

	@Override
	public List<String> peers() {
		return peers;
	}




}
