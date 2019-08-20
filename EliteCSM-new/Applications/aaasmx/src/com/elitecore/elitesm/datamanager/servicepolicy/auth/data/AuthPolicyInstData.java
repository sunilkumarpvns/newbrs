package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class AuthPolicyInstData extends BaseData implements Serializable,Differentiable{

	private static final long serialVersionUID = 1L;
	private String authPolicyId;
	private String name;
	private String description;
	private String status;
	private Long orderNumber;
	private String ruleSet;
	private String responseAttributes;
	private String responseBehavior;
	private String hotlinePolicy;
	private Integer requestType;
	private String validateAuthPacket;
	
		
	private int caseSensitiveUserIdentity;
	private String multipleUserIdentity;
	private String stripUserIdentity;
	private String trimUserIdentity;	
	private String trimPassword;
	private String eapConfigId;
	private String realmPattern;	
	private String realmSeparator;	
	private String cui;
	private String cuiResponseAttributes;
	private String userName;
	private String userNameResonseAttributes;
	private String anonymousName;
	
	//private AuthPolicyDigestConfRelData authPolicyDigestConfRelData;
	private AuthPolicySMRelData authPolicySMRelData;
	private List<AuthPolicyAuthMethodRelData> authMethodRelDataList;
	private String gracePolicyId;
	private String digestConfigId;
	
	private String wimaxEnabled;
	private String threeGPPEnabled;

	private Long defaultSessionTimeout;
	private String rejectOnCheckItemNotFound;
	private String rejectOnRejectItemNotFound;
	private Integer actionOnPolicyNotFound;
	
	private String proxyTranslationMapConfigId;
	private String proxyScript;

	
	private List<AuthPolicyMainDriverRelData> mainDriverList;

	
	private AuthPolicyRMParamsData prepaidRMParamsData;
	private AuthPolicyRMParamsData ipPoolRMParamsData;
	private AuthPolicyRMParamsData chargingGatewayRMParamsData;
	
	private List<AuthPolicyExternalSystemRelData> proxyServerRelList;
	private List<AuthPolicyExternalSystemRelData> ipPoolServerRelList;
	private List<AuthPolicyExternalSystemRelData> prepaidServerRelList;
	private List<AuthPolicyExternalSystemRelData> chargingGatewayServerRelList;
	private List<AuthPolicyBroadcastESIRelData> broadcastingServerRelList;
	private List<AuthPolicySecDriverRelData> secondaryDriverList;
	private List<AuthPolicyAdditionalDriverRelData> additionalDriverList;
	
	// plugin related 
	private String prePlugins;
	private String postPlugins;
	private List<AuthPolicySecDriverRelData> authPolicySecDriverRelDataList;
	private String driverScript;
	private String auditUid;
	
	public List<AuthPolicySecDriverRelData> getAuthPolicySecDriverRelDataList() {
		return authPolicySecDriverRelDataList;
	}
	public void setAuthPolicySecDriverRelDataList(
			List<AuthPolicySecDriverRelData> authPolicySecDriverRelDataList) {
		this.authPolicySecDriverRelDataList = authPolicySecDriverRelDataList;
	}
	public List<AuthPolicySecDriverRelData> getSecondaryDriverList() {
		return secondaryDriverList;
	}
	public void setSecondaryDriverList(
			List<AuthPolicySecDriverRelData> secondaryDriverList) {
		this.secondaryDriverList = secondaryDriverList;
	}
	public String getValidateAuthPacket() {
		return validateAuthPacket;
	}
	public void setValidateAuthPacket(String validateAuthPacket) {
		this.validateAuthPacket = validateAuthPacket;
	}
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getRuleSet() {
		return ruleSet;
	}
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	public Integer getRequestType() {
		return requestType;
	}
	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}
	

	public int getCaseSensitiveUserIdentity() {
		return caseSensitiveUserIdentity;
	}
	public void setCaseSensitiveUserIdentity(int caseSensitiveUserIdentity) {
		this.caseSensitiveUserIdentity = caseSensitiveUserIdentity;
	}
	public String getMultipleUserIdentity() {
		return multipleUserIdentity;
	}
	public void setMultipleUserIdentity(String multipleUserIdentity) {
		this.multipleUserIdentity = multipleUserIdentity;
	}
	public String getStripUserIdentity() {
		return stripUserIdentity;
	}
	public void setStripUserIdentity(String stripUserIdentity) {
		this.stripUserIdentity = stripUserIdentity;
	}
	public String getRealmPattern() {
		return realmPattern;
	}
	public void setRealmPattern(String realmPattern) {
		this.realmPattern = realmPattern;
	}
	public String getRealmSeparator() {
		return realmSeparator;
	}
	public void setRealmSeparator(String realmSeparator) {
		this.realmSeparator = realmSeparator;
	}
	public String getTrimUserIdentity() {
		return trimUserIdentity;
	}
	public void setTrimUserIdentity(String trimUserIdentity) {
		this.trimUserIdentity = trimUserIdentity;
	}
	public String getTrimPassword() {
		return trimPassword;
	}
	public void setTrimPassword(String trimPassword) {
		this.trimPassword = trimPassword;
	}
	public String getWimaxEnabled() {
		return wimaxEnabled;
	}
	public void setWimaxEnabled(String wimaxEnabled) {
		this.wimaxEnabled = wimaxEnabled;
	}
	public String getThreeGPPEnabled() {
		return threeGPPEnabled;
	}
	public void setThreeGPPEnabled(String threeGPPEnabled) {
		this.threeGPPEnabled = threeGPPEnabled;
	}
	public String getRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}
	public void setRejectOnCheckItemNotFound(String rejectOnCheckItemNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckItemNotFound;
	}
	public String getRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}
	public void setRejectOnRejectItemNotFound(String rejectOnRejectItemNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectItemNotFound;
	}
	public Integer getActionOnPolicyNotFound() {
		return actionOnPolicyNotFound;
	}
	public void setActionOnPolicyNotFound(Integer actionOnPolicyNotFound) {
		this.actionOnPolicyNotFound = actionOnPolicyNotFound;
	}	
	public String getProxyTranslationMapConfigId() {
		return proxyTranslationMapConfigId;
	}
	public void setProxyTranslationMapConfigId(String proxyTranslationMapConfigId) {
		this.proxyTranslationMapConfigId = proxyTranslationMapConfigId;
	}
	public String getProxyScript() {
		return proxyScript;
	}
	public void setProxyScript(String proxyScript) {
		this.proxyScript = proxyScript;
	}
	public String getResponseBehavior() {
		return responseBehavior;
	}
	public void setResponseBehavior(String responseBehavior) {
		this.responseBehavior = responseBehavior;
	}
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}
	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}
	public List<AuthPolicyMainDriverRelData> getMainDriverList() {
		return mainDriverList;
	}
	public void setMainDriverList(List<AuthPolicyMainDriverRelData> mainDriverList) {
		this.mainDriverList = mainDriverList;
	}
//	public List<AuthPolicyExternalSystemRelData> getExternalSystemList() {
//		return externalSystemList;
//	}
//	public void setExternalSystemList(
//			List<AuthPolicyExternalSystemRelData> externalSystemList) {
//		this.externalSystemList = externalSystemList;
//	}
	
	
	public List<AuthPolicyExternalSystemRelData> getProxyServerRelList() {
		return proxyServerRelList;
	}
	public String getResponseAttributes() {
		return responseAttributes;
	}
	public void setResponseAttributes(String responseAttributes) {
		this.responseAttributes = responseAttributes;
	}
	public String getDigestConfigId() {
		return digestConfigId;
	}
	public void setDigestConfigId(String digestConfigId) {
		this.digestConfigId = digestConfigId;
	}
	public String getGracePolicyId() {
		return gracePolicyId;
	}
	public void setGracePolicyId(String gracePolicyId) {
		this.gracePolicyId = gracePolicyId;
	}
//	public AuthPolicyDigestConfRelData getAuthPolicyDigestConfRelData() {
//		return authPolicyDigestConfRelData;
//	}
//	public void setAuthPolicyDigestConfRelData(
//			AuthPolicyDigestConfRelData authPolicyDigestConfRelData) {
//		this.authPolicyDigestConfRelData = authPolicyDigestConfRelData;
//	}
	public void setProxyServerRelList(
			List<AuthPolicyExternalSystemRelData> proxyServerRelList) {
		this.proxyServerRelList = proxyServerRelList;
	}
	public List<AuthPolicyExternalSystemRelData> getIpPoolServerRelList() {
		return ipPoolServerRelList;
	}
	public void setIpPoolServerRelList(
			List<AuthPolicyExternalSystemRelData> ipPoolServerRelList) {
		this.ipPoolServerRelList = ipPoolServerRelList;
	}
	public List<AuthPolicyExternalSystemRelData> getPrepaidServerRelList() {
		return prepaidServerRelList;
	}
	public void setPrepaidServerRelList(
			List<AuthPolicyExternalSystemRelData> prepaidServerRelList) {
		this.prepaidServerRelList = prepaidServerRelList;
	}
	public List<AuthPolicyAuthMethodRelData> getAuthMethodRelDataList() {
		return authMethodRelDataList;
	}
	public void setAuthMethodRelDataList(
			List<AuthPolicyAuthMethodRelData> authMethodRelDataList) {
		this.authMethodRelDataList = authMethodRelDataList;
	}
	public AuthPolicySMRelData getAuthPolicySMRelData() {
		return authPolicySMRelData;
	}
	public void setAuthPolicySMRelData(AuthPolicySMRelData authPolicySMRelData) {
		this.authPolicySMRelData = authPolicySMRelData;
	}
	public Long getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}
	public void setDefaultSessionTimeout(Long defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}
	

	
	
	public String getCui() {
		return cui;
	}
	public void setCui(String cui) {
		this.cui = cui;
	}
	public String getCuiResponseAttributes() {
		return cuiResponseAttributes;
	}
	public void setCuiResponseAttributes(String cuiResponseAttributes) {
		this.cuiResponseAttributes = cuiResponseAttributes;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNameResonseAttributes() {
		return userNameResonseAttributes;
	}
	public void setUserNameResonseAttributes(String userNameResonseAttributes) {
		this.userNameResonseAttributes = userNameResonseAttributes;
	}
	
	public String getEapConfigId() {
		return eapConfigId;
	}
	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}
	
	public AuthPolicyRMParamsData getPrepaidRMParamsData() {
		return prepaidRMParamsData;
	}
	public void setPrepaidRMParamsData(AuthPolicyRMParamsData prepaidRMParamsData) {
		this.prepaidRMParamsData = prepaidRMParamsData;
	}
	public AuthPolicyRMParamsData getChargingGatewayRMParamsData() {
		return chargingGatewayRMParamsData;
	}
	public void setChargingGatewayRMParamsData(
			AuthPolicyRMParamsData chargingGatewayRMParamsData) {
		this.chargingGatewayRMParamsData = chargingGatewayRMParamsData;
	}
	public AuthPolicyRMParamsData getIpPoolRMParamsData() {
		return ipPoolRMParamsData;
	}
	
	public void setIpPoolRMParamsData(AuthPolicyRMParamsData ipPoolRMParamsData) {
		this.ipPoolRMParamsData = ipPoolRMParamsData;
	}
	public List<AuthPolicyExternalSystemRelData> getChargingGatewayServerRelList() {
		return chargingGatewayServerRelList;
	}
	public void setChargingGatewayServerRelList(
			List<AuthPolicyExternalSystemRelData> chargingGatewayServerRelList) {
		this.chargingGatewayServerRelList = chargingGatewayServerRelList;
	}
	public List<AuthPolicyBroadcastESIRelData> getBroadcastingServerRelList() {
		return broadcastingServerRelList;
	}

	public void setBroadcastingServerRelList(
			List<AuthPolicyBroadcastESIRelData> broadcastingServerRelList) {
		this.broadcastingServerRelList = broadcastingServerRelList;
	}
	public String getPrePlugins() {
		return prePlugins;
	}
	public void setPrePlugins(String prePlugins) {
		this.prePlugins = prePlugins;
	}
	public String getPostPlugins() {
		return postPlugins;
	}
	public void setPostPlugins(String postPlugins) {
		this.postPlugins = postPlugins;
	}
	public String getAnonymousName() {
		return anonymousName;
	}
	public void setAnonymousName(String anonymousName) {
		this.anonymousName = anonymousName;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AuthPolicyInstData --------------");
		writer.println("authPolicyId 	              :"+authPolicyId);
		writer.println("name                          :"+name);           
		writer.println("description                   :"+description);
		writer.println("status                        :"+status);         
		writer.println("orderNumber                   :"+orderNumber);
		writer.println("ruleSet	           		      :"+ruleSet);   
		writer.println("caseSensitiveUserIdentity     :"+caseSensitiveUserIdentity);
		writer.println("multipleUserIdentity          :"+multipleUserIdentity);
		writer.println("stripUserIdentity   	      :"+stripUserIdentity);          
		writer.println("realmPattern			      :"+realmPattern);	
		writer.println("realmSeparator			      :"+realmSeparator);	
		writer.println("trimUserIdentity		      :"+trimUserIdentity);	
		writer.println("trimPassword			      :"+trimPassword);
		writer.println("defaultSessionTimeout	      :"+defaultSessionTimeout);
		writer.println("cui	  					      :"+cui);
		writer.println("cuiResponseAttributes	      :"+cuiResponseAttributes);
		writer.println("userName	 			      :"+userName);
		writer.println("userNameResonseAttributes     :"+userNameResonseAttributes);
		writer.println("eapConfigID                   :"+eapConfigId);
		writer.println("threeGPPEnabled               : "+threeGPPEnabled);
		writer.println("wimaxEnabled                  : "+wimaxEnabled);
		writer.println("anonymousName                 : "+anonymousName);
		writer.println("DriverScript                  : "+driverScript);
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}
	public List<AuthPolicyAdditionalDriverRelData> getAdditionalDriverList() {
		return additionalDriverList;
	}
	public void setAdditionalDriverList(
			List<AuthPolicyAdditionalDriverRelData> additionalDriverList) {
		this.additionalDriverList = additionalDriverList;
	}
	public String getDriverScript() {
		return driverScript;
	}
	public void setDriverScript(String driverScript) {
		this.driverScript = driverScript;
	}
	public String getAuditUid() {
		return auditUid;
	}
	public void setAuditUid(String auditUid) {
		this.auditUid = auditUid;
	}
	
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Active", "CST01".equals(status)?"TRUE":"FALSE");
		object.put("Ruleset", ruleSet);
		object.put("Response attributes", responseAttributes);
		object.put("Default Response Behavior", responseBehavior);
		object.put("Hotline Policy", hotlinePolicy);
		object.put("Validate Packet", validateAuthPacket);

		// Authentication Parameters
		object.put("Authentication Parameters ", getAuthenticationParameters());

		// Authorization Parameters
		object.put("Authorization Parameters", getAuthorizationParameters());

		// Profile Driver
		object.put("Profile Driver", getProfileDriver());

		// IP Pool Communication Parameters
		if (ipPoolRMParamsData != null) {
			object.put("IP Pool Communication Parameters",
					getIPPoolCommunicationParameters());
		}

		// Prepaid Communication Parameters
		if (prepaidRMParamsData != null) {
			object.put("Prepaid Communication Parameters ",
					getPrepaidCommunicationParameters());
		}

		// Charging Gateway Communication Parameters
		if (chargingGatewayRMParamsData != null) {
			object.put("Charging Gateway Communication Parameters ",
					getChargingGatewayCommunicationParameters());
		}

		// Broadcasting Server Details
		if (broadcastingServerRelList != null) {
			JSONObject fields = new JSONObject();
			for (AuthPolicyBroadcastESIRelData element : broadcastingServerRelList) {
				fields.putAll(element.toJson());
			}
			object.put("Broadcasting Server", fields);
		}

		// Plugins
		object.put("Pre-plugins", prePlugins);
		object.put("Post-plugins", postPlugins);
		return object;
	}

	private JSONObject getAuthenticationParameters() {
		JSONObject object = new JSONObject();
		
		if(requestType != null){
			object.put("Request Mode", requestType);
		}
		
		JSONArray array = new JSONArray();
		if (authMethodRelDataList != null) {
			for (AuthPolicyAuthMethodRelData element : authMethodRelDataList) {
				if(element.getAuthMethodTypeId()!=null){
					array.add(String.valueOf(element.getAuthMethodTypeId()));
				}
			}
			object.put("Supported Authentication Methods", array);
		}
		
		if(eapConfigId != null){
			object.put("EAP Config", eapConfigId);
		}
		
		object.put("Digest Config", digestConfigId);
		 object.put("User Identity Attribute", multipleUserIdentity);
		object.put("Anonymous Profile Identity", anonymousName);
		object.put("Select Case", caseSensitiveUserIdentity);
		object.put("Strip User Identity", stripUserIdentity);
		object.put("Separator", realmSeparator);
		object.put("Realm Pattern", realmPattern);
		object.put("Trim User Identity", trimUserIdentity);
		object.put("Trim Password", trimPassword);
		object.put("CUI", cui);
		object.put("CUI Response Attributes", cuiResponseAttributes);
		object.put("User Name", userName);
		object.put("User Name Response Attributes", userNameResonseAttributes);
		if(gracePolicyId!= null){
			object.put("Grace Policy", gracePolicyId);
		}
		if (proxyServerRelList != null) {
			array = new JSONArray();
			for (AuthPolicyExternalSystemRelData element : proxyServerRelList) {
				if (element.getWeightage() != null
						&& element.getExternalSystemData() != null
						&& element.getExternalSystemData().getName() != null) {
					array.add(element.getExternalSystemData().getName() + "-W-"
							+ element.getWeightage().intValue());
				}
			}
			object.put("Proxy Server", array);
		}
		
		if(proxyTranslationMapConfigId != null){
			object.put("Translation Mapping", proxyTranslationMapConfigId);
		}
		
		object.put("Script", proxyScript);
		return object;
	}

	private JSONObject getAuthorizationParameters() {
		JSONObject object = new JSONObject();
		object.put("Wimax", wimaxEnabled);
		object.put("3GPP", threeGPPEnabled);
		object.put("Reject On Check Item Not Found", rejectOnCheckItemNotFound);
		object.put("Reject On Reject Item Not Found", rejectOnRejectItemNotFound);
		object.put("Accept On Policy Not Found", actionOnPolicyNotFound);
		if (authPolicySMRelData != null && authPolicySMRelData.getSessionManagerInstanceId() != null) {
			object.put("Session Manager", authPolicySMRelData.getSessionManagerInstanceId());
		}
		if(defaultSessionTimeout != null)
			object.put("Default Session Timeout", defaultSessionTimeout);
		return object;
	}

	private JSONObject getProfileDriver() {
		JSONObject object = new JSONObject();
		if (mainDriverList != null) {
			JSONArray array = new JSONArray();
			for (AuthPolicyMainDriverRelData element : mainDriverList) {
				if (element.getWeightage() != null
						&& element.getDriverData() != null
						&& element.getDriverData().getName() != null) {
					array.add(element.getDriverData().getName() + "-W-"
							+ element.getWeightage());
				}
			}
			object.put("Primary Driver", array);
		}
		if (secondaryDriverList != null) {
			JSONArray array = new JSONArray();
			for (AuthPolicySecDriverRelData element : secondaryDriverList) {
				String add = "";
				if (element.getSecondaryDriverData() != null
						&& element.getSecondaryDriverData().getName() != null) {
					add += element.getSecondaryDriverData().getName();
					if (element.getCacheDriverData() != null
							&& element.getCacheDriverData().getName() != null) {
						add += "(" + element.getCacheDriverData().getName() + ")";
					}else{
						add += "(--None--)";
					}
				}
				array.add(add);
			}
			object.put("Secondary Driver", array);
		}
		if (additionalDriverList != null && additionalDriverList.size() > 0) {
			JSONArray array = new JSONArray();
			for (AuthPolicyAdditionalDriverRelData element : additionalDriverList) {
				if (element.getDriverInstanceData() != null
						&& element.getDriverInstanceData().getName() != null) {
					array.add(element.getDriverInstanceData().getName());
				}
			}
			object.put("Additional Driver", array);
		}
		object.put("Driver Script", driverScript);
		return object;
	}

	private JSONObject getIPPoolCommunicationParameters() {
		JSONObject object = new JSONObject();
		// object.put("IP Pool Communication", ipPoolRMParamsData.ge);
		object.put("Accept On Timeout", ipPoolRMParamsData.getAcceptOnTimeout());
		object.put("RuleSet", ipPoolRMParamsData.getRuleSet());
		if (ipPoolServerRelList != null) {
			JSONArray array = new JSONArray();
			for (AuthPolicyExternalSystemRelData element : ipPoolServerRelList) {
				if (element.getWeightage() != null
						&& element.getExternalSystemData() != null
						&& element.getExternalSystemData().getName() != null) {
					array.add(element.getExternalSystemData().getName() + "-W-"
							+ element.getWeightage());
				}
			}
			object.put("IP Pool Server", array);
		}
		if (ipPoolRMParamsData.getTranslationMappingConfData() != null
				&& ipPoolRMParamsData.getTranslationMappingConfData().getName() != null) {
			object.put("Translation Mapping", ipPoolRMParamsData
					.getTranslationMappingConfData().getName());
		}
		object.put("Script", ipPoolRMParamsData.getScript());
		return object;
	}

	private JSONObject getPrepaidCommunicationParameters() {
		JSONObject object = new JSONObject();
		// object.put("Prepaid Communication", ipPoolRMParamsData.ge);
		object.put("Accept On Timeout",
				prepaidRMParamsData.getAcceptOnTimeout());
		object.put("RuleSet", prepaidRMParamsData.getRuleSet());
		if(prepaidRMParamsData.getDefaultSessionTimeout()!=null){
			object.put("Default Session Timeout",
					prepaidRMParamsData.getDefaultSessionTimeout());
		}
		object.put("Rating Class Attribute",
				prepaidRMParamsData.getRatingClassAttribute());
		// object.put("Fallback Driver", prepaidRMParamsData.get)

		if (prepaidServerRelList != null) {
			JSONArray array = new JSONArray();
			for (AuthPolicyExternalSystemRelData element : prepaidServerRelList) {
				if (element.getWeightage() != null
						&& element.getExternalSystemData() != null
						&& element.getExternalSystemData().getName() != null) {
					array.add(element.getExternalSystemData().getName() + "-W-"
							+ element.getWeightage());
				}
			}
			object.put("Prepaid Server", array);
		}
		if (prepaidRMParamsData.getTranslationMappingConfData() != null
				&& prepaidRMParamsData.getTranslationMappingConfData()
						.getName() != null) {
			object.put("Translation Mapping", prepaidRMParamsData
					.getTranslationMappingConfData().getName());
		}
		object.put("Script", prepaidRMParamsData.getScript());
		return object;
	}

	private JSONObject getChargingGatewayCommunicationParameters() {
		JSONObject object = new JSONObject();
		// object.put("Charging Gateway Communication", );
		object.put("Accept On Timeout",
				chargingGatewayRMParamsData.getAcceptOnTimeout());
		object.put("RuleSet", chargingGatewayRMParamsData.getRuleSet());
		if (chargingGatewayServerRelList != null) {
			JSONArray array = new JSONArray();
			for (AuthPolicyExternalSystemRelData element : chargingGatewayServerRelList) {
				if (element.getWeightage() != null
						&& element.getExternalSystemData() != null
						&& element.getExternalSystemData().getName() != null) {
					array.add(element.getExternalSystemData().getName() + "-W-"
							+ element.getWeightage());
				}
			}
			object.put("Charging Gateway Server", array);
		}
		if (chargingGatewayRMParamsData.getTranslationMappingConfData() != null
				&& chargingGatewayRMParamsData.getTranslationMappingConfData()
						.getName() != null) {
			object.put("Translation Mapping", chargingGatewayRMParamsData
					.getTranslationMappingConfData().getName());
		}
		object.put("Script", chargingGatewayRMParamsData.getScript());
		return object;
	}
}
