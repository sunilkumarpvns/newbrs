package com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;

/**
 * 
 * Diameter Peer Name To Id Adapter do conversion of Diameter Peer Name to Diameter Peer Id and vice versa. <br>
 * It takes Diameter Peer Name as input and gives Diameter Peer Id as output in unmarshal. <br>
 * It takes Diameter Peer Id as input and gives Diameter Peer Name as output in marshal. <br>
 * For invalid value of input, it gives null. 
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <peer>peer1</peer>
 * 
 * }
 * 
 * than output is :
 * 101
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class DiameterPeerNameToIdAdapter extends XmlAdapter<String, String> {
	
	DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();

	@Override
	public String unmarshal(String diameterPeerName) throws Exception {
		
		String diameterPeerId = null;
		
		if (Strings.isNullOrBlank(diameterPeerName) == false) {
			try {
				diameterPeerId = diameterPeerBLManager.getDiameterPeerIdByName(diameterPeerName);
			} catch (Exception e) {
				diameterPeerId = null;
			}
		}
		
		return diameterPeerId;
	}

	@Override
	public String marshal(String diameterPeerId) throws Exception {
		
		String diameterPeerName;
		diameterPeerName = diameterPeerBLManager.getDiameterPeerNameById(diameterPeerId);
		return diameterPeerName;
		
	}
	
}
