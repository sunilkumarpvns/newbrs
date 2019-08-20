package com.elitecore.test.aaa.anttask.radius;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;

import com.elitecore.test.aaa.anttask.core.AAATestCaseTask;

public class RadiusTestCaseTask extends AAATestCaseTask {

	private List<SocketTestCaseTask> socketTestList = new ArrayList<SocketTestCaseTask>();
	private DatagramSocket datagramSocket;
	public RadiusTestCaseTask(){
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			
		}
	}
	
	@Override
	public void execute() throws BuildException {
		System.out.println("Start Radius Test Case : " + getName() + " ( IP : "+ getIp()+" and Port : " + getPort() +" )");
		if(isIngoreonerror())
		System.out.println("Radius Test Case : " + getName() + " will ingore any TestCase failure");
		for(SocketTestCaseTask socketTestCaseTask : socketTestList){
			byte[] responsePacketBytes = null;
			try {
				responsePacketBytes = doTest(socketTestCaseTask.getRequestPacket().getRequestBytes());
			} catch (IOException e) {
				System.out.println(socketTestCaseTask.getId()+ " : " + socketTestCaseTask.getDesc() + " [ Status = Failed ]");
				System.out.println(socketTestCaseTask.getId()+ " , Reason : " + e.getMessage());
				if(!isIngoreonerror()) 
				throw new BuildException(socketTestCaseTask.getId() + "  Status = Response not received from server, Reason : " + e.getMessage());
				return;
			}
            byte[] expectedPacketBytes = socketTestCaseTask.getResponsePacket().getResponseBytes();
            boolean isSuccess = true;

            for(int i=0; i < expectedPacketBytes.length; i++) {
                if (responsePacketBytes[i] != expectedPacketBytes[i]) {
    			    isSuccess = false;
    		    }
    	    }
           
            if(isSuccess) {
        	    System.out.println(socketTestCaseTask.getId() + " : "+ socketTestCaseTask.getDesc() + " [ Status = Success ]");
            }else {
            	System.out.println(socketTestCaseTask.getId()+ " : " + socketTestCaseTask.getDesc() + " [ Status = Failed ]");
        	    System.out.println("Expected : " + bytesToHex(expectedPacketBytes));
        	    System.out.println("Received : " + bytesToHex(responsePacketBytes));
        	    if(!isIngoreonerror())
        	    throw new BuildException(socketTestCaseTask.getId() + "  Status = Failed");        	    
            }
		}
	}
	
	private final byte[] doTest(byte[] requestBytes) throws IOException {
        DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, InetAddress.getByName(getIp()), getPort());
        datagramSocket.send(requestPacket);        
        DatagramPacket responsePacket = new DatagramPacket(new byte[4096], 4096);
        datagramSocket.setSoTimeout(10000);
        datagramSocket.receive(responsePacket);
        return responsePacket.getData();
        
	}

	public void addTestCase(SocketTestCaseTask socketTestCaseTask) {
		if(socketTestCaseTask != null)
			socketTestList.add(socketTestCaseTask);		
	}

}
