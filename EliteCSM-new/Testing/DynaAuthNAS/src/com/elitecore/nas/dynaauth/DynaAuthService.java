package com.elitecore.nas.dynaauth;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;


public class DynaAuthService {
	
	private InetAddress bindAddress;
	private DatagramSocket serverSocket;
	private static final int MAX_PACKET_SIZE = 65535;
    private static final int MAX_RESPONSE_TIME_MS = 100;
    private boolean ackMode = true;
    
	private void startService() {
        try {
        	bindAddress =  InetAddress.getByName("0.0.0.0");
        }catch (Exception e) {
        	e.printStackTrace();
        }
        try {
			
        	serverSocket = new DatagramSocket(4799, bindAddress);
			
        	serverSocket.setReceiveBufferSize(100000);

    		serverSocket.setSendBufferSize(100000);
    		
		} catch (SocketException e) {
            System.out.println("Problem starting Dyna Auth Service, reason: " + e.getMessage());
            e.printStackTrace();
		}

		DatagramPacket udpPacket = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
		while(true){
            try {
                serverSocket.receive(udpPacket);
                byte[] rawPacketBytes = new byte[udpPacket.getLength()];
                System.arraycopy(udpPacket.getData(), 0, rawPacketBytes, 0, rawPacketBytes.length);

                RadiusPacket radiusPacket = new RadiusPacket();
                radiusPacket.setBytes(rawPacketBytes);
                handleRequest(radiusPacket, udpPacket.getAddress(), udpPacket.getPort());
            }catch (Exception exp) {
            	exp.printStackTrace();
            }
            
		}
	}
	
	
	private void handleRequest(RadiusPacket radiusRequestPacket, InetAddress srcAddress, int srcPort){
		System.out.println("Request Received : " + radiusRequestPacket);
		byte [] responseBytes = null;
		RadiusPacket radiusResponsePacket = new RadiusPacket();
		radiusResponsePacket.setIdentifier(radiusRequestPacket.getIdentifier());
		if(radiusRequestPacket.getPacketType() == RadiusConstants.COA_REQUEST_MESSAGE){
			if(ackMode){
				radiusResponsePacket.setPacketType(RadiusConstants.COA_ACK_MESSAGE);
				IRadiusAttribute vsaattribute = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,252);
				vsaattribute.setStringValue("nisarg@test.com");
				IRadiusAttribute vsaattribute1 = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,250);
				vsaattribute1.setStringValue("S192.168.60.15");
				IRadiusAttribute vsaattribute2 = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,250);
				vsaattribute2.setStringValue("$MA000c.2997.dad1");
				IRadiusAttribute vsaattribute3 = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,1);
				vsaattribute3.setStringValue("accounting-list=WEB_LOGON_ACCT");
				IRadiusAttribute vsaattribute4 = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				vsaattribute4.setStringValue("000C.2997.DAD1");
				
				radiusResponsePacket.addAttribute(vsaattribute);
				radiusResponsePacket.addAttribute(vsaattribute1);
				radiusResponsePacket.addAttribute(vsaattribute2);
				IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
				attribute.setStringValue("Authentication Success");
				radiusResponsePacket.addAttribute(attribute);	
				attribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
				attribute.setStringValue("600");
				radiusResponsePacket.addAttribute(attribute);				
				radiusResponsePacket.addAttribute(vsaattribute3);				
				radiusResponsePacket.addAttribute(vsaattribute4);
			}else
				radiusResponsePacket.setPacketType(RadiusConstants.COA_NAK_MESSAGE);
			
		}else if(radiusRequestPacket.getPacketType() == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
			if(ackMode){
				radiusResponsePacket.setPacketType(RadiusConstants.DISCONNECTION_ACK_MESSAGE);
				
				IRadiusAttribute vsaattribute = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,252);
				vsaattribute.setStringValue("nisarg@test.com");
				IRadiusAttribute vsaattribute1 = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,250);
				vsaattribute1.setStringValue("S192.168.60.15");
				IRadiusAttribute vsaattribute2 = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,250);
				vsaattribute2.setStringValue("$MA000c.2997.dad1");
				IRadiusAttribute vsaattribute3 = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID,1);
				vsaattribute3.setStringValue("accounting-list=WEB_LOGON_ACCT");
				IRadiusAttribute vsaattribute4 = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				vsaattribute4.setStringValue("000C.2997.DAD1");
				
				radiusResponsePacket.addAttribute(vsaattribute);
				radiusResponsePacket.addAttribute(vsaattribute1);
				radiusResponsePacket.addAttribute(vsaattribute2);
				IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
				attribute.setStringValue("Authentication Success");
				radiusResponsePacket.addAttribute(attribute);	
				attribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
				attribute.setStringValue("600");
				radiusResponsePacket.addAttribute(attribute);				
				radiusResponsePacket.addAttribute(vsaattribute3);				
				radiusResponsePacket.addAttribute(vsaattribute4);
			}else
				radiusResponsePacket.setPacketType(RadiusConstants.DISCONNECTION_NAK_MESSAGE);
			
		}
		
		radiusResponsePacket.refreshPacketHeader();
		radiusResponsePacket.refreshInfoPacketHeader();
		radiusResponsePacket.setAuthenticator(RadiusUtility.generateRFC2865ResponseAuthenticator(radiusResponsePacket, radiusRequestPacket.getAuthenticator(), "secret"));
		
		System.out.println("Response Send : " + radiusResponsePacket);
		responseBytes = radiusResponsePacket.getBytes();
		
        DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);
        responsePacket.setAddress(srcAddress);
        responsePacket.setPort(srcPort);
        try {
			serverSocket.send(responsePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	private void SetAckMode(boolean b) {
		// TODO Auto-generated method stub
		ackMode = b;
	}


	public static void main(String[] args) {
		try {
			Dictionary.getInstance().loadDictionary(new File("../../Applications/eliteaaa/dictionary/radius"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DynaAuthService dynaAuthService = new DynaAuthService();
		dynaAuthService.SetAckMode(true);
		dynaAuthService.startService();
		
		
	}


}