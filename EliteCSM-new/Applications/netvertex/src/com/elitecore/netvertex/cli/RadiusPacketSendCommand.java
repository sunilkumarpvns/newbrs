package com.elitecore.netvertex.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.coreradius.client.CommunicationException;
import com.elitecore.coreradius.client.RequestTimeoutException;
import com.elitecore.coreradius.client.base.IRadiusClient;
import com.elitecore.coreradius.client.base.RadiusClientFactory;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;

public class RadiusPacketSendCommand extends DetailProvider {

	private HashMap<String ,DetailProvider> detailProvider;

	private static final  String PORT          = "port";
	private static final  String MESSAGE_TYPE  = "message-type";
	private static final  String TIMEOUT       = "timeout";

	private static final int DEFAULT_TIMEOUT = 10000;

	public RadiusPacketSendCommand() {
		detailProvider = new HashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {

		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);

		IRadiusClient client = null;
		if(parameters.length >= 6) {
			try {

				InetAddress nasIPAddress = InetAddress.getByName(parameters[0].trim());
				int port = getIntVal(parameters[1].trim(),PORT);
				String sharedSecret = parameters[2].trim();
				int requestType = getIntVal(parameters[3].trim(),MESSAGE_TYPE);
				int requestTimeout = getIntVal(parameters[4].trim(),TIMEOUT);


				ArrayList<IRadiusAttribute> requestAttributes = getRadiusAttrs(parameters);

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
					IRadiusPacket requestPacket = client.createRadiusRequestPacket(requestAttributes);

					// Displaying Request Packet
					out.print(StringUtility.fillChar("", 80, '-'));
					out.println();
					out.println("Sending Request: " + displayPacket(requestPacket));

					out.println(StringUtility.fillChar("", 80, '-'));
					// Sending/Receiving Radius Packet
					//FIXME Need to change blind retry count below.. -by jay
					IRadiusPacket responsePacket = client.sendReceiveRadiusPacket(requestPacket);

					if (responsePacket == null)
						return "Problem receiving response from server.";
					else 
						out.println("Response received: " + displayPacket(responsePacket));
				}else{
					out.println("Unsupported message type: " + requestType);
					return writer.toString();
				}	

			} catch (IllegalArgumentException e) {
				LogManager.ignoreTrace(e);
				out.println("Unparsable Parameter : " + e.getMessage());
			} catch (UnknownHostException e) {
				LogManager.ignoreTrace(e);
				out.println("Unknown Host : "+ e.getMessage());
			}catch (RequestTimeoutException | IOException e) {
				LogManager.ignoreTrace(e);
				out.println(" Response "+e.getMessage());
			}
			catch (CommunicationException e) {
				LogManager.ignoreTrace(e);
				out.println(e.getMessage());
			} catch(Exception e){
				LogManager.ignoreTrace(e);
				out.println(e.getMessage()); 
			}finally {
				try {	
					if (client != null){
						client.closeSocket();
					}
				} catch (Exception ex) {
					LogManager.ignoreTrace(ex);
					out.println(ex.getMessage());
				}
			}
			return writer.toString();
		}else if(parameters.length == 1 && "?".equals(parameters[0])){
			return getHelpMsg();
		}else {
			return "Argument missing \n" + getHelpMsg();
		}
	}

	@Override
	public String getHelpMsg() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("   Description : send Radius Packet");
		out.println();
		out.println("   Usage : sendpacket " + getKey() + " <Server-Ip-Address> <port> <shared-secret> <message-type> <timeout(ms)> <Radius Attributes>");
		out.println();
		out.println("   Example : sendpacket " + getKey() + " 127.0.0.1 1812 secret 1 3000 0:1=UserName 0:2=password 0:4=127.0.01");
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "radius";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProvider;
	}

	private int getIntVal(String val, String name) throws Exception{
		try{
			return Integer.parseInt(val);
		}catch(NumberFormatException ex){
			throw new Exception(name + " must be number");
		}
	}

	private ArrayList<IRadiusAttribute> getRadiusAttrs(String[] attributes) throws Exception{

		ArrayList<IRadiusAttribute> radiusAttrs = new ArrayList<IRadiusAttribute>();
		if(attributes.length >= 6){
			for(int i=5; i<attributes.length;i++){
				String str = attributes[i];
				String [] strs = ParserUtility.splitKeyAndValue(str);
				if(strs != null && strs.length == 3 && strs[0]!=null && strs[0].trim().length()>0){
					IRadiusAttribute radiusAttr = Dictionary.getInstance().getKnownAttribute(strs[0].trim());
					if(radiusAttr != null) {
						if(strs[2] != null) {
								radiusAttr.setStringValue(strs[2].trim());
								radiusAttrs.add(radiusAttr);
						}
					}else {
						throw new Exception("Unknown Attribute Id : "+strs[0]);
					}
				}else
					throw new Exception("Invalid argument provided : "+str);
			}
		}
		return radiusAttrs;
	}

	private String displayPacket(IRadiusPacket packet){
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(RadiusPacketTypeConstant.from(packet.getPacketType())+" to "
				+ packet.getClientIP()+":"+packet.getClientPort()+" ID:"
				+ packet.getIdentifier()+" and Length:"
				+ packet.getLength() + " at "
				+ new Date().toString()
				+ packet);

		out.println("\t" + "Hex");
		out.println("\t" + StringUtility.fillChar("", 76, '-'));
		out.println(displayHexView(RadiusUtility.bytesToHex(packet.getBytes())));
		out.println("\t" + StringUtility.fillChar("", 76, '-'));
		out.close();
		return writer.toString();
	}

	private String displayHexView(String hexBytes){
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);

		int hexStringLength = hexBytes.length();

		int endIndex = 77;
		for ( int startIndex = 0;startIndex < hexStringLength ; startIndex = endIndex + 1,endIndex = endIndex + 78){
			out.print("\t");
			if (endIndex < hexStringLength)
				out.println(hexBytes.substring(startIndex, endIndex));
			else
				out.print(hexBytes.substring(startIndex));
		}
		out.close();
		return writer.toString();
	}
}
