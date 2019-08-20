package com.elitecore.aaa.radius.util.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import com.elitecore.aaa.radius.util.StringAttributeParser;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.client.CommunicationException;
import com.elitecore.coreradius.client.RequestTimeoutException;
import com.elitecore.coreradius.client.base.IRadiusClient;
import com.elitecore.coreradius.client.base.RadiusClientFactory;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;


public class RadClientCommand extends EliteBaseCommand {
	private InetAddress nasIPAddress = null; 
	private int port = 0;
	private String sharedSecret = null; 
	private int requestType = 0;
	private int requestTimeout = 0;
	private static final int DEFAULT_TIMEOUT = 10000;	
	private IRadiusClient client = null;
	
	public RadClientCommand() {
	}
			
	@Override
	public String getCommandName() {
		return "radclient";
	}

	@Override
	public String getDescription() {
		return "Sends the Request to Server.";
	}
	@Override
	public String execute(String parameter) {
		String parameters[] = super.parseArgumentString(parameter);		
		StringWriter strReturnBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(strReturnBuffer);
		ArrayList<IRadiusAttribute> requestAttributes = new ArrayList<IRadiusAttribute>();
		
		if (parameters == null || parameters.length < 5) {
			return getHelp();
		} else {
			try {
				nasIPAddress = InetAddress.getByName(parameters[0]);
				port = Integer.parseInt(parameters[1]);
				sharedSecret = parameters[2];
				requestType = Integer.parseInt(parameters[3]);
				requestTimeout = Integer.parseInt(parameters[4]);
				
				// Parsing Attributes
				if (parameters.length >5){
					try{
						requestAttributes = parseAVPairString(parameters[5]);
					}catch(Exception e){
						return "Error during parsing attributes. Reason: " + e.getMessage();						
					}
				}
				
				IRadiusPacket requestPacket = null;
				IRadiusPacket responsePacket = null;
				
				// Get the actual client object by using Factory Class.
				client = RadiusClientFactory.getRadiusClient(requestType);
				
				if (client != null){
					client.setRequestType(requestType);
					client.setServerAddress(nasIPAddress);
					client.setServerPort(port);
					client.setSharedSecret(sharedSecret);
					if (requestTimeout > 0)
						client.setTimeout(requestTimeout);
					else
						client.setTimeout(DEFAULT_TIMEOUT);
			
					client.openSocket();
					requestPacket = client.createRadiusRequestPacket(requestAttributes);
					
					// Displaying Request Packet
					out.println("Request : ");
					out.println(fillChar("", 100, '-'));
					out.println("Sending " + displayPacket(requestPacket));
					
					
					out.println("Response : ");
					out.println(fillChar("", 100, '-'));
					
					// Sending/Receiving Radius Packet
					responsePacket = client.sendReceiveRadiusPacket(requestPacket);
				
					if (responsePacket == null)
						return "Problem getting reply from server.";
					else 
						out.println("Received  " + displayPacket(responsePacket));
					
					
					out.println("Showing response message from server.");
				}else{
					out.println("Unsupported message type received.");
					return strReturnBuffer.toString();
				}	
				
			} catch (IllegalArgumentException e) {
				out.println("Unparsable Parameter : " + e.getMessage());
			} catch (UnknownHostException e) {
				out.println(e.getMessage());
			} catch (CommunicationException e) {
				out.println(e.getMessage());
			} catch (RequestTimeoutException e) {
				out.println(e.getMessage());
			} catch (IOException e) {
				out.println(e.getMessage());
			}catch(Exception e){
				out.println(e.getMessage()); 
			}finally {
				try {
					requestAttributes = null;
					if (client != null){
						client.closeSocket();
					}					
				} catch (Exception ex) {
					out.println(ex.getMessage());
				}
			}
		}
		return strReturnBuffer.toString();	
	}
		
	/**
	 * Parses the AV Pair provided as argument.
	 * @param parameter
	 * @return ArrayList<IRadiusAttribute>
	 */
	private ArrayList<IRadiusAttribute> parseAVPairString(String parameter){
		ArrayList<IRadiusAttribute> attributesList = new ArrayList<IRadiusAttribute>();

		String[] attributes = ParserUtility.splitString(parameter, ',');
		for(int i=0; i<attributes.length; i++){
			IRadiusAttribute attribute = StringAttributeParser.parseString(attributes[i]);
			if(attribute != null)
				attributesList.add(attribute);
			//TODO Need to add logger when attribute is null. Currently there is no support of Logger in
			//RadClientCommand for 6.0
		}
		return  attributesList;
	}
	
	private String getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage : " + getCommandName() + " <SERVER-IP-Address> <port> <shared-secret> <message-type> <timeout(ms)> [[attr-type:]attr-id=val[,[attr-type:]attr-id=val]]\n");		
		sb.append("Description : " + getDescription());
		sb.append("Ex. : " + getCommandName() + " 127.0.0.1 1812 secret 1 3000 0:1=uname,0:2=pswrd,0:4=127.0.01,0:5=5");
		return sb.toString();
	}
	
	/**
	 * Displays the Packet.
	 * @param packet
	 */
	private String displayPacket(IRadiusPacket packet){
		StringWriter sb = new StringWriter();
		PrintWriter out = new PrintWriter(sb);
		
		out.println(RadiusPacketTypeConstant.from(packet.getPacketType())+" : "
				+ packet.getClientIP()+":"+packet.getClientPort()+" ID:"
				+ packet.getIdentifier()+" and Length:"
				+ packet.getLength() + " at "
				+ new Date().toString()
				+ packet);
		
		out.println("\t" + "Hex");
		out.println("\t" + fillChar("", 80, '-'));
		out.println(displayHexView(RadiusUtility.bytesToHex(packet.getBytes())));
		
		out.println("\t" + fillChar("", 80, '-'));
		
		out.close();
		return sb.toString();
	}
	
	/**
	 * Displays the Hex Packet in formated view.
	 * @param hexBytes
	 * @return
	 */
	private String displayHexView(String hexBytes){
		StringWriter sb = new StringWriter();
		PrintWriter out = new PrintWriter(sb);
		
		int hexStringLength = hexBytes.length();
		
		int endIndex = 80;
		for ( int startIndex = 0;startIndex < hexStringLength ; startIndex = endIndex + 1,endIndex = endIndex + 80){
			out.print("\t");
			if (endIndex < hexStringLength)
				out.println(hexBytes.substring(startIndex, endIndex));
			else
				out.print(hexBytes.substring(startIndex));
		}
		out.close();
		return sb.toString();
	}
	@Override
	public String getHotkeyHelp() {
		return "{'radclient':{'-help':{}}}";
	}
}
