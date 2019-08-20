package com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class PCRFServicePolicyData extends BaseData{
	private long 	pcrfPolicyId;
	private String 	name;
	private String 	description;
	private String 	ruleset;
	private String 	status;
	private Long 	dstGatewayId;
	private String 	identityAttribute;
	private Integer orderNumber;
	private String 	sessionMgrEnabled;
	private Long 	unknownUserAction;
	private String 	pkgId;
	private String 	pkgName;
	private Long 	action;
	private String 	syMode;
	
	private Set<DriverInstanceData>  driverInstances = new HashSet<DriverInstanceData>(0);
	private List<PCRFPolicyCDRDriverRelData> 		pcrfPolicyCDRDriverRelDataList;
	private List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList;
	private GatewayData 			 gatewayData;
	private DriverInstanceData 		 driverInstanceData;
	private List<DriverInstanceData> driverInstanceList;
	
	public String getSyMode() {
		return syMode;
	}
	public void setSyMode(String syMode) {
		this.syMode = syMode;
	}
	public List<PCRFPolicyCDRDriverRelData> getPcrfPolicyCDRDriverRelDataList() {
		return pcrfPolicyCDRDriverRelDataList;
	}
	public void setPcrfPolicyCDRDriverRelDataList(
			List<PCRFPolicyCDRDriverRelData> pcrfPolicyCDRDriverRelDataList) {
		this.pcrfPolicyCDRDriverRelDataList = pcrfPolicyCDRDriverRelDataList;
	}
	public List<PCRFServicePolicySyGatewayRelData> getPcrfServicePolicySyGatewayRelDataList() {
		return pcrfServicePolicySyGatewayRelDataList;
	}
	public void setPcrfServicePolicySyGatewayRelDataList(List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList) {
		this.pcrfServicePolicySyGatewayRelDataList = pcrfServicePolicySyGatewayRelDataList;
	}
	public Long getAction() {
		return action;
	}
	public void setAction(Long action) {
		this.action = action;
	}
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
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
	public String getSessionMgrEnabled(){
		return sessionMgrEnabled;
	}
	public void setSessionMgrEnabled(String sessionMgrEnabled){
		this.sessionMgrEnabled = sessionMgrEnabled;
	}
	public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}
	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	public GatewayData getGatewayData() {
		return gatewayData;
	}
	public void setGatewayData(GatewayData gatewayData) {
		this.gatewayData = gatewayData;
	}
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}
	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
	}
	public long getPcrfPolicyId() {
		return pcrfPolicyId;
	}
	public void setPcrfPolicyId(long pcrfPolicyId) {
		this.pcrfPolicyId = pcrfPolicyId;
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
	public Long getDstGatewayId() {
		return dstGatewayId;
	}
	public void setDstGatewayId(Long dstGatewayId) {
		this.dstGatewayId = dstGatewayId;
	}
	public Set<DriverInstanceData> getDriverInstances() {
		return driverInstances;
	}
	public void setDriverInstances(Set<DriverInstanceData> driverInstances) {
		this.driverInstances = driverInstances;
	}
	public String getIdentityAttribute() {
		return identityAttribute;
	}
	public void setIdentityAttribute(String identityAttribute) {
		this.identityAttribute = identityAttribute;
	}
	

}
