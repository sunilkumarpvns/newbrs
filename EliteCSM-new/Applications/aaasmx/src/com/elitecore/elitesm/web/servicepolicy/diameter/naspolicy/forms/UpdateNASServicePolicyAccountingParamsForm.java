package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctPluginConfig;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateNASServicePolicyAccountingParamsForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String nasPolicyId;
	private String action;
	private List<NASPolicyAcctDriverRelData> driversList;
	private String[] selecteddriverIds;
	private String prePlugins;
	private String postPlugins;
	private String acctScript;
	private String auditUId;
	private String name;
	private String acctPrePluginJson;
	private String acctPostPluginJson;
	private List<NASPolicyAcctPluginConfig> nasPolicyAcctPluginConfigList;
	private List<ScriptInstanceData> driverScriptList;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getNasPolicyId() {
		return nasPolicyId;
	}
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
	}
	public List<NASPolicyAcctDriverRelData> getDriversList() {
		return driversList;
	}
	public void setDriversList(List<NASPolicyAcctDriverRelData> driversList) {
		this.driversList = driversList;
	}
	public String[] getSelecteddriverIds() {
		return selecteddriverIds;
	}
	public void setSelecteddriverIds(String[] selecteddriverIds) {
		this.selecteddriverIds = selecteddriverIds;
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
	public String getAcctScript() {
		return acctScript;
	}
	public void setAcctScript(String acctScript) {
		this.acctScript = acctScript;
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
	public String getAcctPrePluginJson() {
		return acctPrePluginJson;
	}
	public void setAcctPrePluginJson(String acctPrePluginJson) {
		this.acctPrePluginJson = acctPrePluginJson;
	}
	public String getAcctPostPluginJson() {
		return acctPostPluginJson;
	}
	public void setAcctPostPluginJson(String acctPostPluginJson) {
		this.acctPostPluginJson = acctPostPluginJson;
	}
	public List<NASPolicyAcctPluginConfig> getNasPolicyAcctPluginConfigList() {
		return nasPolicyAcctPluginConfigList;
	}
	public void setNasPolicyAcctPluginConfigList(
			List<NASPolicyAcctPluginConfig> nasPolicyPluginConfigList) {
		this.nasPolicyAcctPluginConfigList = nasPolicyPluginConfigList;
	}
	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}
	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}
}
