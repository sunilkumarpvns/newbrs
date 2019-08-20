package com.elitecore.test.aaa.anttask.diameter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;

import com.elitecore.test.aaa.anttask.core.AAATestCaseTask;

public class DiameterTestCaseTask extends AAATestCaseTask {
	
	private String destIp = "127.0.0.1";
	private int destPort = 3868;
	private Socket clientSocket;
	private DataInputStream inStream ;
    private OutputStream outStream;
    
	private List<DiameterScenarioTask> diameterScenarioTasks = new ArrayList<DiameterScenarioTask>();
	public DiameterTestCaseTask(){
		
	}
	
	private void initTask() throws BuildException {
		try {
			clientSocket = new Socket(InetAddress.getByName(getDestIp()), getDestPort());
			if(clientSocket != null){
        		inStream = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
				outStream = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
				this.clientSocket.setSendBufferSize(65536);
				this.clientSocket.setReceiveBufferSize(65536);
				System.out.println("Socket is connected with destination peer.");
        	}
			
		} catch (UnknownHostException e) {
			throw new BuildException(e);			
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}
	@Override
	public void execute() throws BuildException {
		System.out.println("Starting Diameter Test Case : ");
		System.out.println("Destination Diameter     : " + " ( IP : "+ getDestIp()+" and Port : " + getDestPort()+" )");
		initTask();
		if(isIngoreonerror())
		System.out.println("Diameter Test Case : " + getName() + " will ingore any TestCase failure");
		for(DiameterScenarioTask diameterTestCaseTask : diameterScenarioTasks){
			byte[] responsePacketBytes = null;
			try {
				responsePacketBytes = doTest(diameterTestCaseTask.getRequestPacket().getRequestBytes());
			} catch (IOException e) {
				System.out.println(diameterTestCaseTask.getId()+ " : " + diameterTestCaseTask.getDesc() + " [ Status = Failed ]");
				System.out.println(diameterTestCaseTask.getId()+ " , Reason : " + e.getMessage());
				if(!isIngoreonerror()) 
				throw new BuildException(diameterTestCaseTask.getId() + "  Status = Response not received from server, Reason : " + e.getMessage());
				return;
			}
            byte[] expectedPacketBytes = diameterTestCaseTask.getResponsePacket().getResponseBytes();
            boolean isSuccess = true;
            /*System.out.println("Received msg length: " +responsePacketBytes.length);
            System.out.println("expected length: " +expectedPacketBytes.length);*/
            if(expectedPacketBytes.length == responsePacketBytes.length){
            	for(int i=21; i < expectedPacketBytes.length ; i++) {
                    if (responsePacketBytes[i] != expectedPacketBytes[i]) {
        			    isSuccess = false;
        		    }
        	    }
            }else{
            	isSuccess = false;
            }
            
           
            if(isSuccess) {
        	    System.out.println(diameterTestCaseTask.getId() + " : "+ diameterTestCaseTask.getDesc() + " [ Status = Success ]");
            }else {
            	System.out.println(diameterTestCaseTask.getId()+ " : " + diameterTestCaseTask.getDesc() + " [ Status = Failed ]");
        	    System.out.println("Expected : " + bytesToHex(expectedPacketBytes));
        	    System.out.println("Received : " + bytesToHex(responsePacketBytes));
        	    if(!isIngoreonerror())
        	    throw new BuildException(diameterTestCaseTask.getId() + "  Status = Failed");        	    
            }
		}
	}
	
	private final byte[] doTest(byte[] requestBytes) throws IOException {
		outStream.write(requestBytes);
		outStream.flush();
		
		byte []headerBytes = new byte[20];
		byte [] avpBytes = null;
		byte []responseBytes = null;
		int totalBytes = inStream.read(headerBytes);
		if(totalBytes > 0) {
			
			//int messageLength = (0xFF) & ((headerBytes[1] << 16) | (headerBytes[2] << 8) | headerBytes[3]) ;
			
			int messageLength = headerBytes[1];
			
			messageLength = (messageLength << 8) | headerBytes[2] & 0xFF;
			messageLength = (messageLength << 8) | headerBytes[3] & 0xFF;
			
			avpBytes = new byte[messageLength - 20];
			inStream.readFully(avpBytes);
			responseBytes = new byte[messageLength];
			System.arraycopy(headerBytes, 0, responseBytes, 0, 20);
			System.arraycopy(avpBytes, 0, responseBytes, 20, messageLength-20);
		}
		return responseBytes;
	}
	
	public void addScenario(DiameterScenarioTask scenarioTask){
		if(scenarioTask != null)
			diameterScenarioTasks.add(scenarioTask);
	}

	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}

	public String getDestIp() {
		return destIp;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}

	public int getDestPort() {
		return destPort;
	}
}
