package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
@XmlType(propOrder={"peerList"})
public class SelectedPeerConfDetailData implements Validator{
	
	private List<String> peerList;
	
	public SelectedPeerConfDetailData() {
		this.peerList = new ArrayList<String>();
	}
	
	@XmlElement(name="name")
	@Size(min=1,message="Atleast one Peer must be specified")
	public List<String> getPeerList() {
		return peerList;
	}

	public void setPeerList(List<String> peerList) {
		this.peerList = peerList;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		DiameterPeerBLManager peerBLManager = new DiameterPeerBLManager();
		if(Collectionz.isNullOrEmpty(peerList) == false){
			for(String peerName : peerList){
				if(Strings.isNullOrBlank(peerName) == false && "-ALL-".equals(peerName.trim()) == false && "-ROUTING-".equals(peerName.trim()) == false){
					try{
						String peerId = peerBLManager.getDiameterPeerIdByName(peerName);
					}catch(Exception e){
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Configured  "+peerName+" Diameter Peer does not exists");
					}
				}else{
					if(Strings.isNullOrBlank(peerName)){
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Diameter Peer value must be specified");
					}
				}
			}
		}
		
		return isValid;
	}
}
