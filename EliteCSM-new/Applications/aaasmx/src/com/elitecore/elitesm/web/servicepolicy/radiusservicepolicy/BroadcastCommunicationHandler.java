package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class BroadcastCommunicationHandler {
	private Long broadcastCommId;
	private List<BroadcastCommunication> broadcastCommunication;
	
	public Long getBroadcastCommId() {
		return broadcastCommId;
	}
	public void setBroadcastCommId(Long broadcastCommId) {
		this.broadcastCommId = broadcastCommId;
	}
	public List<BroadcastCommunication> getBroadcastCommunication() {
		return broadcastCommunication;
	}
	public void setBroadcastCommunication(
			List<BroadcastCommunication> broadcastCommunication) {
		this.broadcastCommunication = broadcastCommunication;
	}
	
	@Override
	public String toString() {
		return "BroadcastCommunicationHandler [broadcastCommId="
				+ broadcastCommId + ", broadcastCommunication="
				+ broadcastCommunication + "]";
	}
	
}
