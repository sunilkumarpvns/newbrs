package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthPluginConfig;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class UpdateNASServicePolicyAuthenticationParamsForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;

	private String nasPolicyId;
	
	private int caseSensitiveUserIdentity;
	private String multipleUserIdentity;
	private Boolean stripUserIdentity;
	private String realmPattern;
	private String realmSeparator;
	private Boolean trimUserIdentity;
	private Boolean trimPassword;	
	
	private String cui;
	private String cuiResponseAttributes;
	private String userNameAttribute;
	private String userNameResonseAttributes;
	private List<Long> authMethods;
	
	private String action;
	
	private String[] selectedAuthMethods;
	private List<NASPolicyAuthDriverRelData> driversList;
	private List<AuthMethodTypeData>methodTypeList;
	private String[] selectedAuthMethodTypes;
		
	private String[] selecteddriverIds;
	private String[] selectedAdditionalDriverIds;
	
	private String prePlugins;
	private String postPlugins;
	private String authScript;
	private String auditUId;
	private String name;
	private String anonymousProfileIdentity;
	
	private String authPrePluginJson;
	private String authPostPluginJson;
	
	private List<NASPolicyAuthPluginConfig> nasPolicyAuthPluginConfigList;
	private List<ScriptInstanceData> driverScriptList;
	
	public String getNasPolicyId() {
		return nasPolicyId;
	}
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String[] getSelectedAuthMethods() {
		return selectedAuthMethods;
	}
	public void setSelectedAuthMethods(String[] selectedAuthMethods) {
		this.selectedAuthMethods = selectedAuthMethods;
	}
	public List<NASPolicyAuthDriverRelData> getDriversList() {
		return driversList;
	}
	public void setDriversList(List<NASPolicyAuthDriverRelData> driversList) {
		this.driversList = driversList;
	}
	public List<AuthMethodTypeData> getMethodTypeList() {
		return methodTypeList;
	}
	public void setMethodTypeList(List<AuthMethodTypeData> methodTypeList) {
		this.methodTypeList = methodTypeList;
	}
	public Boolean getStripUserIdentity() {
		return stripUserIdentity;
	}
	public void setStripUserIdentity(Boolean stripUserIdentity) {
		this.stripUserIdentity = stripUserIdentity;
	}
	public Boolean getTrimUserIdentity() {
		return trimUserIdentity;
	}
	public void setTrimUserIdentity(Boolean trimUserIdentity) {
		this.trimUserIdentity = trimUserIdentity;
	}
	public Boolean getTrimPassword() {
		return trimPassword;
	}
	public void setTrimPassword(Boolean trimPassword) {
		this.trimPassword = trimPassword;
	}
	public String[] getSelectedAuthMethodTypes() {
		return selectedAuthMethodTypes;
	}
	public void setSelectedAuthMethodTypes(String[] selectedAuthMethodTypes) {
		this.selectedAuthMethodTypes = selectedAuthMethodTypes;
	}
	
	public String[] getSelecteddriverIds() {
		return selecteddriverIds;
	}
	public void setSelecteddriverIds(String[] selecteddriverIds) {
		this.selecteddriverIds = selecteddriverIds;		
	}
	
	public String[] getSelectedAdditionalDriverIds() {
		return selectedAdditionalDriverIds;
	}
	public void setSelectedAdditionalDriverIds(String[] selectedAdditionalDriverIds) {
		this.selectedAdditionalDriverIds = selectedAdditionalDriverIds;
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
	public String getAuthScript() {
		return authScript;
	}
	public void setAuthScript(String authScript) {
		this.authScript = authScript;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}
	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}
	public List<NASPolicyAuthPluginConfig> getNasPolicyAuthPluginConfigList() {
		return nasPolicyAuthPluginConfigList;
	}
	public void setNasPolicyAuthPluginConfigList(
			List<NASPolicyAuthPluginConfig> nasPolicyPluginConfigList) {
		this.nasPolicyAuthPluginConfigList = nasPolicyPluginConfigList;
	}
	public String getAuthPrePluginJson() {
		return authPrePluginJson;
	}
	public void setAuthPrePluginJson(String authPrePluginJson) {
		this.authPrePluginJson = authPrePluginJson;
	}
	public String getAuthPostPluginJson() {
		return authPostPluginJson;
	}
	public void setAuthPostPluginJson(String authPostPluginJson) {
		this.authPostPluginJson = authPostPluginJson;
	}
	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}
	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}
}
