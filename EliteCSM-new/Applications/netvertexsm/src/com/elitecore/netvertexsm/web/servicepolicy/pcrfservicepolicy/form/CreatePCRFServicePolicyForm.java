package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class CreatePCRFServicePolicyForm extends ActionForm {
	private String name;
	private String description;
	private String ruleset;
	private Long driverInstance;
	private String status;
	private String sessionMgrEnabled;
	private Long unknownUserAction;
	private String pkgId;
	private Long action;
	private List<PkgData> pkgDataList;
	private List<DriverInstanceData> driverInstanceList;
	private List<DriverInstanceData> cdrDriverInstanceDataList;
	private List<GatewayData> gatewayList;
	private boolean weightageSelection = true;
	private Long weightage;
	private String syMode;
	private String identityAttribute;
		
	public String getSyMode() {
		return syMode;
	}
	public void setSyMode(String syMode) {
		this.syMode = syMode;
	}
	public Long getWeightage() {
		return weightage;
	}
	public void setWeightage(Long weightage) {
		this.weightage = weightage;
	}
	
	public List<DriverInstanceData> getCdrDriverInstanceDataList() {
		return cdrDriverInstanceDataList;
	}
	public void setCdrDriverInstanceDataList(
			List<DriverInstanceData> cdrDriverInstanceDataList) {
		this.cdrDriverInstanceDataList = cdrDriverInstanceDataList;
	}
	public Long getAction() {
		return action;
	}
	public void setAction(Long action) {
		this.action = action;
	}
	public Long getUnknownUserAction() {
		return unknownUserAction;
	}
	public void setUnknownUserAction(Long unknownUserAction) {
		this.unknownUserAction = unknownUserAction;
	}
	public String getPkgId() {
		return pkgId;
	}
	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}
	public List<PkgData> getPkgDataList() {
		return pkgDataList;
	}
	public void setPkgDataList(List<PkgData> pkgDataList) {
		this.pkgDataList = pkgDataList;
	}
	public void setSessionMgrEnabled(String sessionMgrEnabled){
		this.sessionMgrEnabled = sessionMgrEnabled;
	}
	public String getSessionMgrEnabled(){
		return sessionMgrEnabled;
	}
	public boolean isWeightageSelection() {
		return weightageSelection;
	}
	public void setWeightageSelection(boolean weightageSelection) {
		this.weightageSelection = weightageSelection;
	}
	public Long getDriverInstance() {
		return driverInstance;
	}
	public void setDriverInstance(Long driverInstance) {
		this.driverInstance = driverInstance;
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
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}
	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
	}
	public List<GatewayData> getGatewayList() {
		return gatewayList;
	}
	public void setGatewayList(List<GatewayData> gatewayList) {
		this.gatewayList = gatewayList;
	}
	public String getIdentityAttribute() {
		return identityAttribute;
	}
	public void setIdentityAttribute(String identityAttribute) {
		this.identityAttribute = (identityAttribute != null ? identityAttribute.trim() : identityAttribute);
	}
	
}
