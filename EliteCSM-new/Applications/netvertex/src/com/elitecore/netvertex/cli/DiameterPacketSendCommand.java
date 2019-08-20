package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.Parameter;

public abstract class DiameterPacketSendCommand extends DetailProvider {

	private static final  String COMMAND_CODE          = "cc";
	private static final  String PEER                  = "peer";
	private static final  String ATTRIBUTES            = "avps";
	private static final  String REQUEST               = "request";
	private static final  String ANSWER                = "answer";
	private static final  String APPLICATION_ID        = "appid";
	private static final  String HOP_BY_HOP            = "hid";
	private static final  String END_TO_END            = "eid";

	private HashMap<String ,DetailProvider> detailProvider;

	public DiameterPacketSendCommand() {
		detailProvider = new HashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {

		if(parameters.length > 0){

			if(parameters.length == 1 && "?".equals(parameters[0]))
				return getHelpMsg();

			int commandCode=-1;
			int applicationId = -1;
			int hopbyhop=-1;
			int endtoend=-1;
			String destinationPeer=null;
			boolean isRequest = true;
			ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();

			try{

				for(int index = 0; index < parameters.length ; index ++){
					if(PEER.equalsIgnoreCase(parameters[index])){
						destinationPeer =  parameters[++index];
					}else if(COMMAND_CODE.equalsIgnoreCase(parameters[index])){
						commandCode = getIntVal(parameters[++index],COMMAND_CODE);
					}else if(ATTRIBUTES.equalsIgnoreCase(parameters[index])){
						try{
							for(int i = index + 1; i < parameters.length ; i++){
								diameterAVPs.addAll(getDiameterAttributes(parameters[i]));
							}
						}catch(Exception ex){
							LogManager.ignoreTrace(ex);
							return ATTRIBUTES + " properly not specified. "; 
						}

					}else if(APPLICATION_ID.equalsIgnoreCase(parameters[index])){
						applicationId = getIntVal(parameters[++index],APPLICATION_ID);
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
				if(destinationPeer == null || commandCode < 0 || applicationId < 0 || (diameterAVPs.size()<0)){
					StringBuilder sttBfr = new StringBuilder();
					sttBfr.append("mandatory attribute is missing \n");
					sttBfr.append(getHelpMsg());
					return sttBfr.toString();
				}

				//check for Hop_by_Hop and End_to_End identifier when request bit is false 
				if(!isRequest && (hopbyhop < 0 || endtoend < 0)){
					StringBuilder sttBfr = new StringBuilder();
					sttBfr.append("hopbyhop and endtoend identifier is mandatory for sending diameter answer \n ");
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
			} catch(IndexOutOfBoundsException | NullPointerException ex){
				LogManager.ignoreTrace(ex);
				return "Invalid argument " + "\n" +getHelpMsg();
			} catch(Exception ex){
				LogManager.ignoreTrace(ex);
				return ex.getMessage() + "\n" +  getHelpMsg();
			}
		}else{
			return "    argument missing \n " + getHelpMsg();
		}
	}

	private String printSuccessMessage(DiameterPacket packet) {
		StringBuilder sttBfr = new StringBuilder();
		sttBfr.append("diameter packet sent successfully");
		sttBfr.append("diameter packet ");
		sttBfr.append(packet.toString());
		return sttBfr.toString();
	}
	
	private ArrayList<IDiameterAVP> getDiameterAttributes(String str) throws Exception {

		ArrayList<IDiameterAVP> arrayList = new ArrayList<IDiameterAVP>();
		if(str!=null) {
			String [] strs = ParserUtility.splitKeyAndValue(str);
			if(strs != null && strs.length == 3 && strs[0].trim().length()>0){
				IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(strs[0].trim());
				if(diameterAVP != null) {
					if(strs[2] !=null){
						diameterAVP.setStringValue(strs[2]);
						arrayList.add(diameterAVP);
					}
				}
			}else
				throw new Exception("Invalid argument configured : "+str);
		}
		return arrayList;
	}

	@Override
	public String getKey() {
		return "diameter";
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

		String[] paramName ={ REQUEST + "|" + ANSWER , PEER + " <destination peer>",APPLICATION_ID + " <application id>",COMMAND_CODE+ " <command code>",END_TO_END+ " <end_to_end id>",HOP_BY_HOP+ " <hop_by_hop id>",ATTRIBUTES+ " <diameter avps>" };

		String[] paramDesc ={
				"Specify whether send packet is request or answer (option default is "+REQUEST+")",
				"Specify peer identity of diameter packet ",
				"Specify application id of diameter packet",
				"Specify command code of diameter packet",
				"Specify End to End identifier of diameter packet(mandatory for diameter answer)",
				"Specify Hop by Hop identifier of diameter packet(mandatory for diameter answer)",
		"Specify attribute of diameter packet"};
		out.println();
		out.println("   Description : send Diameter Packet");
		out.println("   Options : ");
		out.println();
		for(int i=0;i<paramDesc.length;i++){
			out.printf("   %-25s %s" , paramName[i] , paramDesc[i] );
			out.println();
		}
		out.println();
		out.close();		
		return stringWriter;	
	}

	private String getUsageExample(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println("   Usage : ");
		out.println();
		out.println("   For Sending Diameter Request : ");
		out.println("   sendpacket "+getKey() + " "+ REQUEST +" " + PEER + " eliteaaa.elitecore.com " + APPLICATION_ID +  " 16777238 " + COMMAND_CODE + " 272 "+ ATTRIBUTES +" 0:1=name 0:263=sessionId 10415:1016={'10415:1028'='9';'10415:1025'='232323'}");
		out.println();
		out.println("   For Sending Diameter Answer : ");
		out.println("   sendpacket "+getKey() + " "+ ANSWER +" " + PEER + " eliteaaa.elitecore.com " + APPLICATION_ID +  " 16777238 " + COMMAND_CODE + " 272 "+ HOP_BY_HOP +" 136546 " + END_TO_END +" 165987 "+ ATTRIBUTES +" 0:1=name 0:263=sessionId 10415:1016={'10415:1028'='9';'10415:1025'='232323';'10415:1026'='232323'}");
		out.close();		
		return stringWriter.toString();
	}


	private int getIntVal(String val, String name) throws Exception{
		try{
			return Integer.parseInt(val);
		}catch(NumberFormatException ex){
			throw new Exception(name + " must be number");
		}
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProvider;
	}
	
	public abstract void sendDiameterRequest(DiameterRequest request, String destinationPeer) throws Exception;
	public abstract void sendDiameterAnswer(DiameterAnswer answer, String destinationPeer) throws Exception;
}
