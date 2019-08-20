package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class DiameterPeerNameAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String peerName) throws Exception {
		if(peerName.isEmpty() == false){
			try{
				DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
				DiameterPeerData diameterPeerData = diameterPeerBLManager.getDiameterPeerByName(peerName.trim());
				if(diameterPeerData == null){
					return RestValidationMessages.INVALID;
				}else{
					return diameterPeerData.getPeerUUID();
				}
			} catch(Exception e){
				return RestValidationMessages.INVALID;
			}
		} else {
			return RestValidationMessages.INVALID;
		}
	}

	@Override
	public String marshal(String peerId) throws Exception {
		if(peerId != null){
			try{
				DiameterPeerBLManager diameterPeerBlManager = new DiameterPeerBLManager();
				DiameterPeerData diameterPeerData = diameterPeerBlManager.getDiameterPeerById(peerId);
				
				if(Strings.isNullOrBlank(diameterPeerData.getName()) == false){
					return diameterPeerData.getName();
				}
				
			} catch(Exception e ) {
				return null;
			}
		}
		return null;
	}
}