package com.elitecore.aaa.diameter.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;

public abstract class SendPacketCommand extends EliteBaseCommand {
	
	private final static String COMMAND_CODE          = "cc";
	private final static String PEER                  = "peer";
	private final static String ATTRIBUTES            = "avps";
	private final static String REQUEST               = "request";
	private final static String ANSWER                = "answer";
	private final static String APPLICATION_ID        = "appid";
	private final static String HOP_BY_HOP            = "hid";
	private final static String END_TO_END            = "eid";
	private final static String DIAMTER               = "diameter";	
	
	@Override
	public String execute(String parameter) {
		if(parameter != null && parameter.trim().length() > 0){
			parameter = parameter.trim();
			String [] parameters = parameter.split(" ");
				
			if(parameters.length == 1 && parameter.equals("?"))
				return DIAMTER;
			else if(parameter.contains("?"))
				return getHelpMsg().toString();
			
			int commandCode = -1,hopbyhop=-1,endtoend=-1;
			long applicationId = -1;
			String destinationPeer=null;
			boolean isRequest = true;
			ArrayList<IDiameterAVP> diameterAVPs = null;
			
			try{
	
				for(int index = 0; index < parameters.length ; index ++){
					if(PEER.equalsIgnoreCase(parameters[index])){
						destinationPeer =  parameters[++index];
					}else if(COMMAND_CODE.equalsIgnoreCase(parameters[index])){
						commandCode =  getIntVal(parameters[++index],COMMAND_CODE);
					}else if(ATTRIBUTES.equalsIgnoreCase(parameters[index])){
						try{
							ArrayList<IDiameterAVP> tempDiameterAVPs = DiameterUtility
									.getDiameterAttributes( parameters[++index], new ValueProvider() {
										
										@Override
										public String getStringValue(String identifier) {
											return identifier;
										}
									});
							if(tempDiameterAVPs == null || tempDiameterAVPs.isEmpty())
								throw new Exception();
							diameterAVPs =  tempDiameterAVPs;
						}catch(Exception ex){
							return ATTRIBUTES+" is not properly specified"; 
						}
						
					}else if(APPLICATION_ID.equalsIgnoreCase(parameters[index])){
						applicationId = getLongVal(parameters[++index],APPLICATION_ID);
					}else if(REQUEST.equalsIgnoreCase(parameters[index])){
						isRequest = true;
					}else if(ANSWER.equalsIgnoreCase(parameters[index])){
						isRequest = false;
					}else if(END_TO_END.equalsIgnoreCase(parameters[index])){
						endtoend = getIntVal(parameters[++index],END_TO_END);
					}else if(HOP_BY_HOP.equalsIgnoreCase(parameters[index])){
						hopbyhop = getIntVal(parameters[++index],HOP_BY_HOP);
					}
				}
				
				
				//check for mandatory bit
				if(destinationPeer == null || commandCode < 0 || applicationId < 0 || diameterAVPs == null){
					StringBuffer sttBfr = new StringBuffer();
					sttBfr.append("mandatory attribute is missing");
					sttBfr.append(getHelpMsg());
					return sttBfr.toString();
				}
				
				//check for Hop_by_Hop and End_to_End identifier when request bit is false 
				if((!isRequest) && (hopbyhop < 0 || endtoend < 0)){
					StringBuffer sttBfr = new StringBuffer();
					sttBfr.append("hopbyhop and endtoend identifier is mandatory for sending diameter answer");
					sttBfr.append(getHelpMsg());
					return sttBfr.toString();
				}	
				
				if (isRequest) {
					DiameterRequest request = createDiameterRequest(applicationId, commandCode, hopbyhop, endtoend, diameterAVPs);
					sendDiameterRequest(request, destinationPeer);
					return printSuccessMessage(request);
				} else {
					DiameterAnswer answer = createDiameterAnswer(applicationId, commandCode, hopbyhop, endtoend, diameterAVPs);
					sendDiameterAnswer(answer, destinationPeer);
					return printSuccessMessage(answer);
				}
				
			}catch(IndexOutOfBoundsException ex){
				return "Invalid argument " + getHelpMsg();
			}catch(NullPointerException ex){
				return "Invalid argument " + getHelpMsg();
			}catch(Exception ex){
				return ex.getMessage() + getHelpMsg();
			}
		}else{
			return "missing argument " + getHelpMsg();
		}
	}

	private String printSuccessMessage(DiameterPacket packet) {
		StringBuffer sttBfr = new StringBuffer();
		sttBfr.append("diameter packet sent successfully");
		sttBfr.append("diameter packet ");
		sttBfr.append(packet.toString());
		return sttBfr.toString();
	}

	@Override
	public String getCommandName() {
		return "sendpacket";
	}

	@Override
	public String getDescription() {
		return "Send Diameter Request/Answer to Peer";
	}

	@Override
	public String getHotkeyHelp() {
		
		return "{'"+getCommandName() +"':{'"+DIAMTER+"':{'-help':{}}}}";
	}
	
	private DiameterRequest createDiameterRequest(long applicationId,int commandCode, int endtoend, int hopbyhop,ArrayList<IDiameterAVP> diameterAVPs) throws Exception{
		DiameterRequest request = new DiameterRequest();
		request.setRequestingHost(Parameter.getInstance().getOwnDiameterIdentity());
		request.setRequestBit();
		setHeader(applicationId, commandCode, endtoend, hopbyhop, diameterAVPs, request);
		return request;
	}
	
	private DiameterAnswer createDiameterAnswer(long applicationId,int commandCode, int endtoend, int hopbyhop,ArrayList<IDiameterAVP> diameterAVPs) throws Exception{
		DiameterAnswer answer = new DiameterAnswer();
		setHeader(applicationId, commandCode, endtoend, hopbyhop, diameterAVPs, answer);
		return answer;
	}

	private void setHeader(long applicationId, int commandCode, int endtoend, int hopbyhop,
			ArrayList<IDiameterAVP> diameterAVPs, DiameterPacket packet) {
		packet.setCommandCode(commandCode);
		packet.setApplicationID(applicationId);
		packet.setProxiableBit();
		packet.addAvps(diameterAVPs);
		if(endtoend > 0)
			packet.setEnd_to_endIdentifier(endtoend);
		if(hopbyhop > 0)
			packet.setHop_by_hopIdentifier(hopbyhop);
	}
	

	public String getHelpMsg(){
		return getHelp().append(getUsageExample()).toString();
	}

	private StringWriter getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		
		String paramName[] ={DIAMTER, REQUEST + "|" + ANSWER,PEER + " <destination peer>",APPLICATION_ID + " <application id>",COMMAND_CODE+ " <command code>",END_TO_END+ " <end_by_end id>",HOP_BY_HOP+ " <hop_by_hop id>",ATTRIBUTES+ " <diameter avps>" };
		
		String paramDesc[] ={"Specify Application level protocol for sending packet",
				"Specify whether send packet is request or answer (option default is "+REQUEST+")",
				"Specify peer identity of diameter packet ",
				"Specify application id of diameter packet",
				"Specify command code of diameter packet",
				"Specify End to End identifier of diameter packet(mendetory for diameter answer)",
				"Specify Hop by Hop identifier of diameter packet(mendetory for diameter answer)",
				"Specify attribute of diameter packet"};
		
		
		out.println();
		for(int i=0;i<paramDesc.length;i++){
			out.println("	" + fillChar(paramName[i],40)  +  paramDesc[i] );
		}
		out.println();
		
		getUsageExample();
		
		out.close();		
		return stringWriter;	
	}
	
	private String getUsageExample(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println("Usage : ");
		out.println();
		out.println("For Sending Diameter Request : ");
		out.println(getCommandName() +" "+ DIAMTER +" "+ REQUEST +" " + PEER + " eliteaaa.elitecore.com " + APPLICATION_ID +  " 16777238 " + COMMAND_CODE + " 272 "+ ATTRIBUTES +" 0:1=name,10415:1016='{'10415:1028'='9';'10415:1025'='232323'}'");
		out.println();
		out.println("For Sending Diameter Answer : ");
		out.println(getCommandName() +" "+ DIAMTER +" "+ ANSWER +" " + PEER + " eliteaaa.elitecore.com " + APPLICATION_ID +  " 16777238 " + COMMAND_CODE + " 272 "+ HOP_BY_HOP +" 136546 " + END_TO_END +" 165987 "+ ATTRIBUTES +" 0:1=name,10415:1016={'10415:1028'='9';'10415:1025'='232323';'10415:1026'='232323'}");
		out.close();		
		return stringWriter.toString();
	}

	
	private int getIntVal(String val, String name) throws Exception{
		try{
			return Integer.parseInt(val);
		}catch(NumberFormatException ex){
			throw new Exception(name + " should be number");
		}
	}
	
	private long getLongVal(String val, String name) throws Exception{
		try{
			return Long.parseLong(val);
		}catch(NumberFormatException ex){
			throw new Exception(name + " should be number");
		}
	}
	
	public abstract void sendDiameterRequest(DiameterRequest request, String destinationPeer) throws Exception;
	public abstract void sendDiameterAnswer(DiameterAnswer answer, String destinationPeer) throws Exception;

}
