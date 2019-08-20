package com.elitecore.test.aaa.anttask.radius;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SocketTestCaseTask extends Task {
	private String id;
	private String desc;
	private RadiusPacketTask requestPacketTask;
	private RadiusPacketTask responsePacketTask;
	
	@Override
	public void execute() throws BuildException {
		// TODO Auto-generated method stub
	}

	public void addRequestPacket(RadiusPacketTask packetTask){
		requestPacketTask = packetTask;
	}
	
	public void addResponsePacket(RadiusPacketTask packetTask){
		responsePacketTask = packetTask;
		responsePacketTask.setAuthenticator(requestPacketTask.getAuthenticator());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public RadiusPacketTask getRequestPacket() {
		return requestPacketTask;
	}

	public RadiusPacketTask getResponsePacket() {
		return responsePacketTask;
	}
}
