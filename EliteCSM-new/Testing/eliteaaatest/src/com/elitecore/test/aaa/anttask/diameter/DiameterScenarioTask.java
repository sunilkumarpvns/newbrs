package com.elitecore.test.aaa.anttask.diameter;

import org.apache.tools.ant.Task;

public class DiameterScenarioTask extends Task {
	private String id = "";
	private String desc = "";
	private DiameterPacketTask requestPacketTask;
	private DiameterPacketTask responsePacketTask;
	
	public void addRequestPacket(DiameterPacketTask packetTask){
		requestPacketTask = packetTask;
	}
	
	public void addResponsePacket(DiameterPacketTask packetTask){	
		responsePacketTask = packetTask;
	}
	
	public DiameterPacketTask getRequestPacket(){
		return requestPacketTask;
	}
	public DiameterPacketTask getResponsePacket(){
		return responsePacketTask;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
