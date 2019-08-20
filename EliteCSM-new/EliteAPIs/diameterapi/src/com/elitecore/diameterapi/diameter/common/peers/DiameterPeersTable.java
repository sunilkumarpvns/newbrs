package com.elitecore.diameterapi.diameter.common.peers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.diameter.common.data.PeerData;

public class DiameterPeersTable {

	private Map <String,DiameterPeer> peerMap;
	private Map<String, DiameterPeer> peerMapByName;
	private final String MODULE = "DIAMETER-PEERS-TABLE";

	public DiameterPeersTable() {
		peerMap = new LinkedHashMap<String,DiameterPeer>(1,1);
		peerMapByName = new LinkedHashMap<String, DiameterPeer>(1,1);
	}

	public Collection<DiameterPeer> getPeerList(){
		if(peerMap!=null){
			return peerMap.values();
		}else
			return null;
	}
	public DiameterPeer getPeer(String strPeerHostIdentity) {
		if (strPeerHostIdentity != null){
			return peerMap.get(strPeerHostIdentity);
		}
		return null;
	}
	
	public DiameterPeer getPeerByName(String peerName) {
		return peerMapByName.get(peerName);
	}
	
	/**
	 *  
	 * @param Host Identity or Peer Name
	 * @return PeerData
	 */
	public PeerData getPeerData(String strPeerHostIdentity) {
		DiameterPeer peer = getPeerByName(strPeerHostIdentity);
		if (peer != null){
			return peer.getPeerData();
		}

		peer = getPeer(strPeerHostIdentity);
		if (peer != null){
			return peer.getPeerData();
		}
		
		return null;
	}

	public int getPeerState(String strPeerHostIdentity) {
		DiameterPeer peer =  peerMap.get(strPeerHostIdentity); 
		if(peer != null)
			return peer.getPeerState();
		else
			return 0;
	}

	public Map<String, IStateEnum> getPeersState() {
		Map<DiameterPeer,String> tempDiameterPeerMap = new HashMap<DiameterPeer,String>();
		for(Entry<String, DiameterPeer> entry: peerMap.entrySet()){
			tempDiameterPeerMap.put(entry.getValue(),entry.getKey());
		}
		
		Map<String, IStateEnum> peersStateMap = new HashMap<String, IStateEnum>();
		for(Entry<DiameterPeer,String> entry: tempDiameterPeerMap.entrySet()){
			
			/*
			 * Priority :
			 * 	1) host-identity
			 * 	2) key
			 */
			if(entry.getKey().getHostIdentity() != null && 
					entry.getKey().getHostIdentity().isEmpty() == false){
				peersStateMap.put(entry.getKey().getHostIdentity(), entry.getKey().currentState());
			} else {
				peersStateMap.put(entry.getValue(), entry.getKey().currentState());
			}
		}
		return peersStateMap;
	}

	public void addPeer(DiameterPeer diameterPeer) {
		if(diameterPeer.getHostIdentity() != null && diameterPeer.getHostIdentity().trim().length() > 0){
			peerMap.put(diameterPeer.getHostIdentity(), diameterPeer);
		}
		
		if(diameterPeer.getRemoteInetAddress() != null && diameterPeer.getRemoteInetAddress().toString().trim().length() > 0){
			if (peerMap.put(diameterPeer.getRemoteInetAddress().getHostAddress(), diameterPeer) != null)
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Map Already contains Key :"+diameterPeer.getRemoteInetAddress().getHostAddress()+", so Overriding value");
		}
		
		if(diameterPeer.getPeerName() != null && diameterPeer.getPeerName().trim().length() > 0) {
			if (peerMapByName.put(diameterPeer.getPeerName(), diameterPeer) != null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Map already contains Key: " + diameterPeer.getPeerName() + ", so Overriding value");
			}
		}
	}
}