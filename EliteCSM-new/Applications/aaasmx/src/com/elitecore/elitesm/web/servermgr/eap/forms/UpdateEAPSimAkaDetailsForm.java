package com.elitecore.elitesm.web.servermgr.eap.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateEAPSimAkaDetailsForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private Long configId;
	private Long datasource=0L;
	private Long numberOfTriplets=3L;
	private String localHostId="aaa-ulticom";
	private int localHostPort;
	private String localHostIp="127.0.0.1";
	private String remoteHostId="aaa-ulticom";
	private int remoteHostPort=10090;
	private String remoteHostIp="127.0.0.1";
	private Integer eapAuthType;
	private String eapAuthTypeAction;
	private Long eapId;
	private String action;
	private String pseudonymSupport;
	
	public String getPseudonymSupport() {
		return pseudonymSupport;
	}
	public void setPseudonymSupport(String pseudonymSupport) {
		this.pseudonymSupport = pseudonymSupport;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Long getConfigId() {
		return configId;
	}
	public void setConfigId(Long configId) {
		this.configId = configId;
	}
	
	public Long getDatasource() {
		return datasource;
	}
	public void setDatasource(Long datasource) {
		this.datasource = datasource;
	}
	public Long getNumberOfTriplets() {
		return numberOfTriplets;
	}
	public void setNumberOfTriplets(Long numberOfTriplets) {
		this.numberOfTriplets = numberOfTriplets;
	}
	public String getLocalHostId() {
		return localHostId;
	}
	public void setLocalHostId(String localHostId) {
		this.localHostId = localHostId;
	}
	public Integer getLocalHostPort() {
		return localHostPort;
	}
	public void setLocalHostPort(Integer localHostPort) {
		this.localHostPort = localHostPort;
	}
	public String getLocalHostIp() {
		return localHostIp;
	}
	public void setLocalHostIp(String localHostIp) {
		this.localHostIp = localHostIp;
	}
	public String getRemoteHostId() {
		return remoteHostId;
	}
	public void setRemoteHostId(String remoteHostId) {
		this.remoteHostId = remoteHostId;
	}
	public Integer getRemoteHostPort() {
		return remoteHostPort;
	}
	public void setRemoteHostPort(Integer remoteHostPort) {
		this.remoteHostPort = remoteHostPort;
	}
	public String getRemoteHostIp() {
		return remoteHostIp;
	}
	public void setRemoteHostIp(String remoteHostIp) {
		this.remoteHostIp = remoteHostIp;
	}
	public Integer getEapAuthType() {
		return eapAuthType;
	}
	public void setEapAuthType(Integer eapAuthType) {
		this.eapAuthType = eapAuthType;
	}
	public Long getEapId() {
		return eapId;
	}
	public void setEapId(Long eapId) {
		this.eapId = eapId;
	}
	public String getEapAuthTypeAction() {
		return eapAuthTypeAction;
	}
	public void setEapAuthTypeAction(String eapAuthTypeAction) {
		this.eapAuthTypeAction = eapAuthTypeAction;
	}
		
}
