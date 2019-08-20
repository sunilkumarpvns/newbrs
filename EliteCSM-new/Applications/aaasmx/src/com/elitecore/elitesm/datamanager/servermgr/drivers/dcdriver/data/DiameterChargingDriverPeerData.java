package com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;



public class DiameterChargingDriverPeerData extends BaseData{
	private Long peerRelId;
	private String name;
	private Long communicationPort;
	private String attemptConnection;
	private String routingPolicyName;
	private Long watchDogInterval;
	private String transMapConfId;
	private Long realmRelId;
	private TranslationMappingConfData translationMappingConfData;
	
	public Long getRealmRelId() {
		return realmRelId;
	}

	public void setRealmRelId(Long realmRelId) {
		this.realmRelId = realmRelId;
	}

	public Long getPeerRelId() {
		return peerRelId;
	}
	
	public void setPeerRelId(Long peerRelId) {
		this.peerRelId = peerRelId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getCommunicationPort() {
		return communicationPort;
	}
	
	public void setCommunicationPort(Long communicationPort) {
		this.communicationPort = communicationPort;
	}
	
	public String getAttemptConnection() {
		return attemptConnection;
	}

	public void setAttemptConnection(String attemptConnection) {
		this.attemptConnection = attemptConnection;
	}

	public String getRoutingPolicyName() {
		return routingPolicyName;
	}
	
	public void setRoutingPolicyName(String routingPolicyName) {
		this.routingPolicyName = routingPolicyName;
	}
	
	public Long getWatchDogInterval() {
		return watchDogInterval;
	}
	
	public void setWatchDogInterval(Long watchDogInterval) {
		this.watchDogInterval = watchDogInterval;
	}
	
	public String getTransMapConfId() {
		return transMapConfId;
	}
	
	public void setTransMapConfId(String transMapConfId) {
		this.transMapConfId = transMapConfId;
	}
	
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}
	
	public void setTranslationMappingConfData(
			TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}
}
