package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.clients.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.config.core.UserDefined;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {})
@XmlRootElement(name = "clients")
public class ClientsConfigurationData{
	
	
	@Valid
	private List<ClientDetail> clientsList;

	public ClientsConfigurationData(){
		clientsList = new ArrayList<ClientDetail>();
	}
	
	@XmlElement(name = "client")
	@Size(min=1,message="Atleast one Client Detail must be specified")
	public List<ClientDetail> getClients() {
		return clientsList;
	}

	public void setClients(List<ClientDetail> clients) {
		this.clientsList = clients;
	}

	@XmlType(propOrder = {"strClientIp","strSharedSecret","lTimeout","profileName","strFramedPoolName"})
	@ValidObject
	public static class ClientDetail implements UserDefined,Validator{
		
		private String strClientIp = null;
		
		@Pattern(regexp=RestValidationMessages.REGEX_VALUE, message = "please enter valid Shared Secret")
		private String strSharedSecret = null;
		
		@Pattern(regexp=RestValidationMessages.REGEX_VALUE, message = "please enter valid Framed Pool Name")
		private String strFramedPoolName=null;
		
		@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC,message="please enter valid Request Expiry Time")
		private String lTimeout = "8000";
		
		@NotEmpty(message ="Profile Name must be specified" )
		@Pattern(regexp=RestValidationMessages.REGEX_VALUE,message="please enter valid Profile Name")
		private String profileName;
		
		@XmlElement(name = "profile-name", type = String.class)
		public String getProfileName() {
			return profileName;
		}
		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}
		
		@XmlElement(name = "client-ip", type = String.class)
		public String getStrClientIp() {
			return strClientIp;
		}
		public void setStrClientIp(String strClientIp) {
			this.strClientIp = strClientIp;
		}
		
		@XmlElement(name = "shared-secret", type = String.class)
		public String getStrSharedSecret() {
			return strSharedSecret;
		}
		public void setStrSharedSecret(String strSharedSecret) {
			this.strSharedSecret = strSharedSecret;
		}
		
		@XmlElement(name = "framed-pool-name", type = String.class)
		public String getStrFramedPoolName() {
			return strFramedPoolName;
		}
		public void setStrFramedPoolName(String strFramedPoolName) {
			this.strFramedPoolName = strFramedPoolName;
		}
		
		@XmlElement(name = "request-expiry-time", type = String.class)
		public String getlTimeout() {
			return lTimeout;
		}
		public void setlTimeout(String lTimeout) {
			this.lTimeout = lTimeout;
		}
		
		@Override
		public boolean validate(ConstraintValidatorContext context) {
			
			boolean isValid = true ;
			if(Strings.isNullOrBlank(profileName) == false){
				ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
				try{
					clientProfileBLManager.getClientProfileDataByName(profileName);
				}catch(Exception e){
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Configured "+profileName+" Trusted Client Profile does not exists");
				}
				
			}
			
			return isValid;
		}
		
	}
}

