package com.elitecore.elitesm.ws.rest.adapter.diameterpeers;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;

/**
 * 
 * Peer Profile Name From Id Adapter do conversion of Peer Profile Name to Peer Profile Id and vice versa. <br>
 * It takes Peer Profile Name as input and gives Peer Profile Id as output in unmarshal. <br>
 * It takes Peer Profile Id as input and gives Peer Profile Name as output in marshal. <br>
 * For input value is not specified than it gives -1. <br>
 * For invalid value of input, it gives -2. 
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <profile-name>diameter_peer_profile</profile-name>
 * 
 * }
 * 
 * than output is :
 * 1
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class PeerProfileNameFromIdAdapter  extends XmlAdapter<String, String> {
	
	DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();

	@Override
	public String unmarshal(String peerProfileName) throws Exception {
		
		String peerProfileId = "-1L";
		
		if (Strings.isNullOrBlank(peerProfileName) == false) {
			
			try {

				peerProfileId = diameterPeerProfileBLManager.getPeerProfileIdByPeerProfileName(peerProfileName);

			} catch (DataManagerException dme) {
				peerProfileId = "-2L";
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		return peerProfileId;
		
	}

	@Override
	public String marshal(String peerProfileId) throws Exception {
		
		String peerProfileName;
		
		peerProfileName=diameterPeerProfileBLManager.getDiameterPeerProfileById(peerProfileId).getProfileName();
		
		return peerProfileName;
		
	}

}
