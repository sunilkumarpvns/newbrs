package com.elitecore.netvertexsm.web.gateway.gateway.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;

public class DiameterGatewayForm extends ActionForm{

	private static final long serialVersionUID = 1L;
	private long gatewayId;
	private String gatewayName;
	private String connectionUrl;
	private String policyEnforcementMethodName;
	private String localAddress;	
//	Diameter Properties
	private String hostIdentity;
	private String realm;
	private Long requestTimeout;
	private Integer retransmissionCount; 
	
	private String action;
	private long gatewayProfileId;
	private long alternateHostId;
	private List<GatewayProfileData> gatewayProfileList;
	private List<DiameterProfileData> diameterProfileDataList;
	
	private List<GatewayData> alternateHostList;
	
		
	public List<DiameterProfileData> getDiameterProfileDataList() {
		return diameterProfileDataList;
	}
	public void setDiameterProfileDataList(
			List<DiameterProfileData> diameterProfileDataList) {
		this.diameterProfileDataList = diameterProfileDataList;
	}
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
	public String getLocalAddress() {
		return localAddress;
	}
	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}
	public String getHostIdentity() {
		return hostIdentity;
	}
	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
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
 
	public String getPolicyEnforcementMethodName() {
		return policyEnforcementMethodName;
	}
	public void setPolicyEnforcementMethodName(String policyEnforcementMethodName) {
		this.policyEnforcementMethodName = policyEnforcementMethodName;
	}
	public Integer getRetransmissionCount() {
		return retransmissionCount;
	}
	public void setRetransmissionCount(Integer retransmissionCount) {
		this.retransmissionCount = retransmissionCount;
	}
	public Long getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(Long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	public List<GatewayData> getAlternateHostList() {
		return alternateHostList;
	}
	public void setAlternateHostList(List<GatewayData> alternateHostList) {
		this.alternateHostList = alternateHostList;
	}
	public long getAlternateHostId() {
		return alternateHostId;
	}
	public void setAlternateHostId(long alternateHostId) {
		this.alternateHostId = alternateHostId;
	}	
	
}
