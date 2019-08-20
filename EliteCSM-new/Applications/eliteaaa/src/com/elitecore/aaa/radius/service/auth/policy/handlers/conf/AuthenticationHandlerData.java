package com.elitecore.aaa.radius.service.auth.policy.handlers.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.subscriber.conf.SubscriberProfileRespositoryDetailsAware;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.AuthenticationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerDataSupport;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "authentication-handler")
public class AuthenticationHandlerData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, SubscriberProfileRespositoryDetailsAware {
	private static final char COMMA = ',';
	private ArrayList<Integer> authMethodHandlerTypes;
	private String digestConfigId;
	private String eapConfigId;
	private String userName;
	/* This is available only when advanced expression is configured */
	@Nullable private String userNameExpression;
	private String userNameResponseAttributeStr;
	
	/* Transient properties */
	private RadiusSubscriberProfileRepositoryDetails userProfileRepoDetails;
	private List<String> userNameResponseAttributes;
	
	public AuthenticationHandlerData() {
		authMethodHandlerTypes = new ArrayList<Integer>();
		userNameResponseAttributes = new ArrayList<String>(0);
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
	public List<Integer> getSupportedMethods() {		
		return this.authMethodHandlerTypes;
	}

	@XmlElement(name = "user-name",type = String.class)
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

	@XmlTransient
	public List<String> getUserNameRespAttrList() {
		return userNameResponseAttributes;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return new AuthenticationHandler(serviceContext, this);
	}

	@XmlTransient
	public RadiusSubscriberProfileRepositoryDetails getUserProfileRepoDetails() {
		return userProfileRepoDetails;
	}
	
	@Override
	public void setSubscriberProfileRepositoryDetails(RadiusSubscriberProfileRepositoryDetails details) {
		this.userProfileRepoDetails = details;
	}
	
	@Override
	public void postRead() {
		super.postRead();
		postReadProcessingForUsernameResponseAttributes();
	}

	private void postReadProcessingForUsernameResponseAttributes() {
		if(userNameResponseAttributeStr != null) {
			userNameResponseAttributes = Strings.splitter(COMMA).trimTokens().split(userNameResponseAttributeStr);
		}
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Authentication Handler | Enabled: %s", 10, ' '), isEnabled()));
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

		jsonObject.put("Enabled", isEnabled());
		
		if(Collectionz.isNullOrEmpty(authMethodHandlerTypes) == false){
			List<String> authMethodList = new ArrayList<String>();
			String authMethodString = "";
			for( Integer method : authMethodHandlerTypes ){
				switch (method) {
				case AuthMethods.PAP:
					authMethodList.add("PAP");
					break;
				case AuthMethods.CHAP:
					authMethodList.add("CHAP");
					break;
				case AuthMethods.EAP:
					authMethodList.add("EAP");
					break;
				case AuthMethods.DIGEST:
					authMethodList.add("DIGEST");
					break;
				case AuthMethods.PROXY:
					authMethodList.add("PROXY");
					break;
				default:
					break;
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
}
