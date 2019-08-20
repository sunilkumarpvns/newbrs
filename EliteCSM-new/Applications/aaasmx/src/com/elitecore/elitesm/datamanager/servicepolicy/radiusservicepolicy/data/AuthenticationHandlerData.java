package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
@XmlRootElement(name = "authentication-handler")
@XmlType(propOrder = {"supportedMethods", "eapConfigId", "digestConfigId",
		"userName", "userNameExpression", "userNameResponseAttributeStr"})
public class AuthenticationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData,AcctServicePolicyHandlerData, SubscriberProfileRespositoryDetailsAware, Validator {
	
	@Size(min = 1, message ="At least one supported authentication method is mandatory")
	private List<String> supportedMethods;
	
	private String digestConfigId;
	private String eapConfigId;
	
	private String userName;
	/* This is available only when advanced expression is configured */
	private String userNameExpression;
	private String userNameResponseAttributeStr;
	
	
	public AuthenticationHandlerData() {
		supportedMethods = new ArrayList<String>();
		userName = AAAServerConstants.NONE;
	}

	@XmlElement(name = "digest-config")
	public String getDigestConfigId() {
		return digestConfigId;
	}

	public void setDigestConfigId(String digestConfigId) {
		this.digestConfigId = digestConfigId;
	}

	@XmlElement(name = "eap-config")
	public String getEapConfigId() {
		return eapConfigId;
	}

	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}

	@XmlElementWrapper(name ="supported-methods")
	@XmlElement(name = "method")
	public List<String> getSupportedMethods() {
		return supportedMethods;
	}
	
	public void setSupportedMethods(List<String> authMethodHandlerTypes) {
		this.supportedMethods = authMethodHandlerTypes;
	}

	@XmlElement(name = "user-name",type = String.class)
	@Pattern(regexp = "NONE|Authenticated-Username|CUI|Request|Advanced", 
			message = "Supported values of User Name are NONE, Authenticated-Username, CUI, Request and Advanced only")
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement(name = "username-expression", type = String.class)
	public String getUserNameExpression() {
		return userNameExpression;
	}

	public void setUserNameExpression(String userNameExpression) {
		this.userNameExpression = userNameExpression;
	}

	@XmlElement(name = "user-name-response-attribute")
	public String getUserNameResponseAttributeStr() {
		return userNameResponseAttributeStr;
	}

	public void setUserNameResponseAttributeStr(String userNameResponseAttributeStr) {
		this.userNameResponseAttributeStr = userNameResponseAttributeStr;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return null;
	}
	
	@Override
	public void setSubscriberProfileRepositoryDetails(RadiusSubscriberProfileRepositoryDetails details) {

	}
	
	@Override
	public void postRead() {
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Authentication Handler | Enabled: %s", 10, ' '), getEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Supported Methods", getSupportedMethods()));
		out.println(format("%-30s: %s", "Digest Configuration Id", getDigestConfigId()));
		out.println(format("%-30s: %s", "EAP Configuration Id", getEapConfigId()));
		out.println(format("%-30s: %s", "Username", 
				getUserName() != null ? getUserName() : ""));
		out.println(format("%-30s: %s", "Advanced Username Expression", 
				getUserNameExpression() != null ? getUserNameExpression() : ""));
		out.println(format("%-30s: %s", "Username Response Attributes", 
				getUserNameResponseAttributeStr() != null ? getUserNameResponseAttributeStr() : ""));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}


	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new  JSONObject();

		jsonObject.put("Enabled", getEnabled());
		
		if(Collectionz.isNullOrEmpty(supportedMethods) == false){
			List<String> authMethodList = new ArrayList<String>();
			String authMethodString = "";
			for(String method : supportedMethods ) {
				if ("PAP".equalsIgnoreCase(method)) {
					authMethodList.add("PAP");
				} else if ("CHAP".equalsIgnoreCase(method)) {
					authMethodList.add("CHAP");
				} else if ("EAP".equalsIgnoreCase(method)) {
					authMethodList.add("EAP");
				} else if ("DIGEST".equalsIgnoreCase(method)) {
					authMethodList.add("DIGEST");
				} else if ("PROXY".equalsIgnoreCase(method)) {
					authMethodList.add("PROXY");
				}
			}
				
			if( Collectionz.isNullOrEmpty(authMethodList) == false){
				authMethodString = authMethodList.toString().replaceAll("[\\s\\[\\]]", "");
				jsonObject.put("Supported Authentication Methods", authMethodString);
			}
			
			jsonObject.put("Digest Config",digestConfigId);
			jsonObject.put("EAP Config",eapConfigId);
			
			if ( Strings.isNullOrEmpty(userName) == false )
				jsonObject.put("Username", userName);

			if( Strings.isNullOrEmpty(userNameExpression) == false )
				jsonObject.put("Advanced Username Expression", userNameExpression);

			if( Strings.isNullOrEmpty(userNameResponseAttributeStr) == false )
				jsonObject.put("User Name Response Attributes", userNameResponseAttributeStr);
		}
		return jsonObject;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		for (String authMethod : getSupportedMethods()) {
			
			boolean isValidSupportedMethod = false;
			if ("PAP".equalsIgnoreCase(authMethod)
					|| "CHAP".equalsIgnoreCase(authMethod)
					|| "EAP".equalsIgnoreCase(authMethod)
					|| "DIGEST".equalsIgnoreCase(authMethod)) {
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
			
			if ("DIGEST".equalsIgnoreCase(authMethod)) {
				if (Strings.isNullOrBlank(getDigestConfigId())) {
					RestUtitlity.setValidationMessage(context, "Digest Configuration in Authentication Handler must be specified");
					isValid = false;
				} else {
					try {
						DigestConfBLManager digestConfigBLManager = new DigestConfBLManager();
						digestConfigBLManager.getDigestConfigDataByName(getDigestConfigId());
					} catch (Exception exception) {
						RestUtitlity.setValidationMessage(context, "Invalid Digest Configuration in Authentication Handler");
						isValid = false;
					}
				}
			}
		}
		
		if (AAAServerConstants.ADVANCED.equalsIgnoreCase(userName) && Strings.isNullOrBlank(getUserNameExpression())) {
			RestUtitlity.setValidationMessage(context, "Advanced Username Expression must be specified When Username attribute value is 'Advanced' in Authenication handler");
			isValid = false;
		}
		
		
		return isValid;
	}
}