package com.elitecore.netvertexsm.web.gateway.gateway.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;

public class RadiusGatewayForm extends ActionForm{

	private static final long serialVersionUID = 1L;
	private long gatewayId;
	private String gatewayName;
	private String connectionUrl;
	private String policyEnforcementMethodName;		
	private long gatewayProfileId;
//	Radius Properties
	private String sharedSecret;
	
	private String action;
	private Long minLocalPort;
	private List<GatewayProfileData> gatewayProfileList;	
	
	public List<GatewayProfileData> getGatewayProfileList() {
		return gatewayProfileList;
	}
	public void setGatewayProfileList(List<GatewayProfileData> gatewayProfileList) {
		this.gatewayProfileList = gatewayProfileList;
	}
	public long getGatewayProfileId() {
		return gatewayProfileId;
	}
	public void setGatewayProfileId(long gatewayProfileId) {
		this.gatewayProfileId = gatewayProfileId;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action){
		this.action=action;
	}
	public Long getMinLocalPort() {
		return minLocalPort;
	}
	public void setMinLocalPort(Long minLocalPort) {
		this.minLocalPort = minLocalPort;
	}
	 
	public String getPolicyEnforcementMethodName() {
		return policyEnforcementMethodName;
	}
	public void setPolicyEnforcementMethodName(String policyEnforcementMethodName) {
		this.policyEnforcementMethodName = policyEnforcementMethodName;
	}		
}
