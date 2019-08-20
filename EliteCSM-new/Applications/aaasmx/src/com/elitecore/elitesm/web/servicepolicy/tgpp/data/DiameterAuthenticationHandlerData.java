package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "authentication-handler")
@ValidObject
@XmlType(propOrder = {"supportedAuthenticationMethods", "eapConfigId"})
public class DiameterAuthenticationHandlerData extends DiameterApplicationHandlerDataSupport implements Validator {
	
	@Size(min = 1, message ="At least one supported authentication method is mandatory")
	private List<String> supportedAuthenticationMethods = new ArrayList<String>();
	private String eapConfigId;
	
	@XmlElement(name = "eap-config")
	public String getEapConfigId() {
		return eapConfigId;
	}

	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}

	@XmlElementWrapper(name = "supported-methods")
	@XmlElement(name = "method")
	public List<String> getSupportedAuthenticationMethods() {
		return supportedAuthenticationMethods;
	}
	
	public void setSupportedAuthenticationMethods(
			List<String> supportedAuthenticationMethods) {
		this.supportedAuthenticationMethods = supportedAuthenticationMethods;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Authentication Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Supported Methods", getSupportedAuthenticationMethods()));
		out.println(format("%-30s: %s", "EAP Configuration Id", getEapConfigId()));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		object.put("Enabled", getEnabled());
		
		List<String> authMethodList = new ArrayList<String>();
		for(String method : supportedAuthenticationMethods ) {
			if ("PAP".equalsIgnoreCase(method)) {
				authMethodList.add("PAP");
			} else if ("CHAP".equalsIgnoreCase(method)) {
				authMethodList.add("CHAP");
			} else if ("EAP".equalsIgnoreCase(method)) {
				authMethodList.add("EAP");
			}
		}
		
		if(Collectionz.isNullOrEmpty(authMethodList) == false){
			String authMethodString = authMethodList.toString().replaceAll("[\\s\\[\\]]", "");
			object.put("Supported Authentication Methods", authMethodString);
		}
		object.put("EAP Config", eapConfigId);

		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		for (String authMethod : getSupportedAuthenticationMethods()) {

			boolean isValidSupportedMethod = false;
			if ("PAP".equalsIgnoreCase(authMethod)
					|| "CHAP".equalsIgnoreCase(authMethod)
					|| "EAP".equalsIgnoreCase(authMethod)) {
				isValidSupportedMethod = true;
			}

			if( isValidSupportedMethod == false ){
				RestUtitlity.setValidationMessage(context, "Invalid Supported methods in Authentication handler. It could be 'PAP', 'CHAP', 'EAP' and 'DIGEST'");
				isValid = false;
			}

			if ("EAP".equalsIgnoreCase(authMethod)) {
				if (Strings.isNullOrBlank(getEapConfigId())) {
					RestUtitlity.setValidationMessage(context, "EAP Configuration must be specified when EAP authentication method is selected");
					isValid = false;
				} else {
					try {
						EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
						eapConfigBLManager.getEapConfigurationDataByName(getEapConfigId());
					} catch (Exception e) {
						e.printStackTrace();
						RestUtitlity.setValidationMessage(context, "Invalid EAP Configuration in Authentication Handler");
						isValid = false;
					}
				}
			}
		}
		return isValid;
	}
}