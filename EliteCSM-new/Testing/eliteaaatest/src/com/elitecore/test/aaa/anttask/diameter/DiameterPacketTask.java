package com.elitecore.test.aaa.anttask.diameter;

import org.apache.tools.ant.Task;

import com.elitecore.test.aaa.anttask.core.BaseAttributeTask;

public class DiameterPacketTask extends Task {
	private int version=1;
	private int commandFlag=128;
	private int commandCode=257;
	private int applicationId=0;
	private DiameterAvpsTask avpsTask;

	public DiameterPacketTask() {
	}

	public DiameterAvpsTask getAvps() {
		return avpsTask;
	}

	public void addAvps(DiameterAvpsTask avpsTask) {
		this.avpsTask = avpsTask;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int vsersion) {
		this.version = vsersion;
	}

	public int getCommandFlag() {
		return commandFlag;
	}

	public void setCommandFlag(int commandFlag) {
		this.commandFlag = commandFlag;
	}

	public int getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(int commandCode) {
		this.commandCode = commandCode;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	public byte[] getRequestBytes(){ return getBytes();}
	
	public byte[] getResponseBytes(){return getBytes();}
	
	private byte[] getBytes(){
	
		int currentPosition = 0;
		int totalLength = 0;
		int avpsTotalLength = 0;
		byte[] attributeBytes = new byte[4096];	
	
		for(BaseAttributeTask avpTask : this.avpsTask.getAvps()){
			byte[] temp = avpTask.getBytes();
			System.arraycopy(temp, 0, attributeBytes, currentPosition, temp.length);
			currentPosition = currentPosition + temp.length;
			avpsTotalLength = avpsTotalLength + temp.length;			
		}				
		/*Length = Version + MessageLength + CommandFlag + CommandCode + ApplicationIdentifier + hop-by-hop + end-to-end + avps */ 
		totalLength = 1 + 3 + 1 + 3 + 4 + 4 + 4 + avpsTotalLength; 
		byte[] requestPacketBytes = new byte[totalLength];
		 //+  + ApplicationIdentifier + hop-by-hop + end-to-end + avps */
		/* Version */
		 requestPacketBytes[0] = (byte)getVersion();
		 /* MessageLength */
		 requestPacketBytes[1] = (byte) (totalLength >>> 16);
		 requestPacketBytes[2] = (byte) (totalLength >>> 8);
		 requestPacketBytes[3] = (byte) (totalLength >>> 0);
		 
		 /* CommandFlag  */
		 requestPacketBytes[4] = (byte)getCommandFlag();
		 /* CommandCode */
		 requestPacketBytes[5] = (byte) (commandCode >>> 16);
		 requestPacketBytes[6] = (byte) (commandCode >>> 8);
		 requestPacketBytes[7] = (byte) (commandCode >>> 0);
		
		 
		 /* ApplicationIdentifier */
		 requestPacketBytes[8] = (byte) (applicationId >>> 24);
		 requestPacketBytes[9] = (byte) (applicationId >>> 16);
		 requestPacketBytes[10] = (byte) (applicationId >>> 8);
		 requestPacketBytes[11] = (byte) (applicationId >>> 0);
	
		 
		 /* Hop-By-Hop Identifier */
		 byte bytes[] = {0,1,1,1};		 
		 System.arraycopy(bytes, 0, requestPacketBytes, 12, 4);
		
		 /* End-To-End Identifier */
		 byte bytes2[] = {0,1,0,1};
		 System.arraycopy(bytes2, 0, requestPacketBytes, 16, 4);
		
		 /* ATTRIBUTES */
		 System.arraycopy(attributeBytes, 0, requestPacketBytes, 20, avpsTotalLength);
		 return requestPacketBytes;
	}


}
