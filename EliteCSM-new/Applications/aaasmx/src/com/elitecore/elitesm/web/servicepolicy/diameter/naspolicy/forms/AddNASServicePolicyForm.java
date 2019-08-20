package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class AddNASServicePolicyForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String ruleSet;
	
	private int caseSensitiveUserIdentity;
	private String multipleUserIdentity="0:1";
	private String stripUserIdentity="false";
	private String realmPattern="suffix";
	private String realmSeparator="@";
	private String trimUserIdentity="true";
	private String trimPassword="false";
	private String status;
	
	private String cui;
	private String cuiResponseAttributes;
	private String advancedCuiExpression;
	private String userNameAttribute;
	private String userNameResonseAttributes;
	private List<Long> authMethods;
	
	private String prePluginsList;
	private String postPluginsList;
	
	private boolean pap=false;
	private boolean chap=false;
 	
	private Integer requestType = 3;
	private String rejectOnCheckItemNotFound = "false";
	private String rejectOnRejectItemNotFound = "false";
	private String actionOnPolicyNotFound = "false";

	private Set<NASResponseAttributes> nasResponseAttributesSet;
	
	private List<GracepolicyData> gracePolicyList;
	private String wimax;
	private String gracePolicy;
	private String anonymousProfileIdentity;
	private String sessionManagement = "false";
	private String diameterConcurrency;
	private String additionalDiameterConcurrency;
	private List<DiameterConcurrencyData> diameterConcurrencyDataList;
	private Long defaultSessionTimeout = 600L;
	private	String defaultResponseBehaviourArgument;
	private String defaultResponseBehaviour;
	
	private List<ScriptInstanceData> driverScriptList;

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


	public String getRuleSet() {
		return ruleSet;
	}




	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
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




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
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


	public String getUserNameAttribute() {
		return userNameAttribute;
	}




	public void setUserNameAttribute(String userName) {
		this.userNameAttribute = userName;
	}




	public String getUserNameResonseAttributes() {
		return userNameResonseAttributes;
	}




	public void setUserNameResonseAttributes(String userNameResonseAttributes) {
		this.userNameResonseAttributes = userNameResonseAttributes;
	}




	public List<Long> getAuthMethods() {
		return authMethods;
	}




	public void setAuthMethods(List<Long> authMethods) {
		this.authMethods = authMethods;
	}




	public String getPrePluginsList() {
		return prePluginsList;
	}




	public void setPrePluginsList(String prePluginsList) {
		this.prePluginsList = prePluginsList;
	}




	public String getPostPluginsList() {
		return postPluginsList;
	}




	public void setPostPluginsList(String postPluginsList) {
		this.postPluginsList = postPluginsList;
	}

    


	public boolean isPap() {
		return pap;
	}

	public void setPap(boolean pap) {
		this.pap = pap;
	}

	public boolean isChap() {
		return chap;
	}

	public void setChap(boolean chap) {
		this.chap = chap;
	}
	
	public Integer getRequestType() {
		return requestType;
	}

	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
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

	public String getActionOnPolicyNotFound() {
		return actionOnPolicyNotFound;
	}

	public void setActionOnPolicyNotFound(String actionOnPolicyNotFound) {
		this.actionOnPolicyNotFound = actionOnPolicyNotFound;
	}

	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println("--------------------- NASServicePolicyForm ---------------------------");
		writer.println("name                        :"+name);
		writer.println("description                 :"+description);
		writer.println("ruleSet                     :"+ruleSet);
		writer.println("caseSensitiveUserIdentity   :"+caseSensitiveUserIdentity);
		writer.println("multipleUserIdentity        :"+multipleUserIdentity);
		writer.println("stripUserIdentity           :"+stripUserIdentity);
		writer.println("realmPattern                :"+realmPattern);
		writer.println("realmSeparator              :"+realmSeparator);
		writer.println("trimUserIdentity            :"+trimUserIdentity);
		writer.println("trimPassword                :"+trimPassword);
		writer.println("status                      :"+status);
		writer.println("cui                         :"+cui);
		writer.println("cuiResponseAttributes       :"+cuiResponseAttributes);
		writer.println("userName                    :"+userNameAttribute);
		writer.println("userNameResonseAttributes   :"+userNameResonseAttributes);
		writer.println("pap                         :"+isPap());
		writer.println("chap                        :"+isChap());
		writer.println("requestType                 :"+rejectOnCheckItemNotFound );
		writer.println("rejectOnCheckItemNotFound   :"+requestType );
		writer.println("rejectOnRejectItemNotFound  :"+rejectOnRejectItemNotFound);
		writer.println("actionOnPolicyNotFound      :"+actionOnPolicyNotFound );
		writer.println("-----------------------------------------------------------------------------");
		writer.close();
		return out.toString();
		
		
	}

	public Set<NASResponseAttributes> getNasResponseAttributesSet() {
		return nasResponseAttributesSet;
	}

	public void setNasResponseAttributesSet(Set<NASResponseAttributes> nasResponseAttributesSet) {
		this.nasResponseAttributesSet = nasResponseAttributesSet;
	}

	public String getWimax() {
		return wimax;
	}

	public void setWimax(String wimax) {
		this.wimax = wimax;
	}

	public List<GracepolicyData> getGracePolicyList() {
		return gracePolicyList;
	}

	public void setGracePolicyList(List<GracepolicyData> gracePolicyList) {
		this.gracePolicyList = gracePolicyList;
	}

	public String getGracePolicy() {
		return gracePolicy;
	}

	public void setGracePolicy(String gracePolicy) {
		this.gracePolicy = gracePolicy;
	}

	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}

	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}

	public String getSessionManagement() {
		return sessionManagement;
	}

	public void setSessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
	}

	public String getDiameterConcurrency() {
		return diameterConcurrency;
	}

	public void setDiameterConcurrency(String diameterConcurrency) {
		this.diameterConcurrency = diameterConcurrency;
	}

	public String getAdditionalDiameterConcurrency() {
		return additionalDiameterConcurrency;
	}

	public void setAdditionalDiameterConcurrency(
			String additionalDiameterConcurrency) {
		this.additionalDiameterConcurrency = additionalDiameterConcurrency;
	}

	public List<DiameterConcurrencyData> getDiameterConcurrencyDataList() {
		return diameterConcurrencyDataList;
	}

	public void setDiameterConcurrencyDataList(
			List<DiameterConcurrencyData> diameterConcurrencyDataList) {
		this.diameterConcurrencyDataList = diameterConcurrencyDataList;
	}

	public Long getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}

	public void setDefaultSessionTimeout(Long defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}

	public String getAdvancedCuiExpression() {
		return advancedCuiExpression;
	}

	public void setAdvancedCuiExpression(String advancedCuiExpression) {
		this.advancedCuiExpression = advancedCuiExpression;
	}

	public String getDefaultResponseBehaviourArgument() {
		return defaultResponseBehaviourArgument;
	}

	public void setDefaultResponseBehaviourArgument(String defaultResponseBehaviourArgument) {
		this.defaultResponseBehaviourArgument = defaultResponseBehaviourArgument;
	}

	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}

	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}

	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}

	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}

}
