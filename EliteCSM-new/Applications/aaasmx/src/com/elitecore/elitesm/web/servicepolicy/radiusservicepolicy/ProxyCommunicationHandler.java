package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class ProxyCommunicationHandler {
	private Long proxyCommId;
	private List<ProxyCommunicationData> proxyCommunication;
	
	public Long getProxyCommId() {
		return proxyCommId;
	}
	public void setProxyCommId(Long proxyCommId) {
		this.proxyCommId = proxyCommId;
	}
	public List<ProxyCommunicationData> getProxyCommunication() {
		return proxyCommunication;
	}
	public void setProxyCommunication(List<ProxyCommunicationData> proxyCommunication) {
		this.proxyCommunication = proxyCommunication;
	}
}
