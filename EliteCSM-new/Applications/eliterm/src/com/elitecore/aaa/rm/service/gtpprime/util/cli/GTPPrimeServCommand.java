/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceCounterListener;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeServCommand extends EliteBaseCommand {

	private final static String HELP = "?";
	private final static String COUNTERS = "-c";
	private final static String COUNTERS_DETAIL = "-counter";

	@Override
	public String execute(String parameter) {

		String response = "--";
		if (parameter != null && parameter.trim().length() > 0){

			StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
			String commandParameters=null;	

			if (tokenizer.hasMoreTokens())
				parameter= tokenizer.nextToken();

			if (tokenizer.hasMoreTokens())
				commandParameters= tokenizer.nextToken();

			if(parameter.trim().length() > 0 ){				
				if(parameter.equalsIgnoreCase(COUNTERS)|| parameter.equalsIgnoreCase(COUNTERS_DETAIL)){
					if(commandParameters!=null){
						response = displayGTPPCounters(commandParameters);
					}else{
						response = displayGTPPCounters("total");
					}
				} else if (parameter.equalsIgnoreCase(HELP) || parameter.equalsIgnoreCase(HELP_OPTION)){
					response = getHelp().toString();	
				} else {
					response = getErrorMsg();
				}
			}
		}else{ // if parameter is null;
			StringWriter stringWriter = new StringWriter();
			stringWriter.append("Required parameter missing.");
			stringWriter.append("\n");
			stringWriter.append(getHelp().toString());
			response = stringWriter.toString();			
		}

		return response;
	}

	@Override
	public String getCommandName() {
		return "gtppserv";
	}

	@Override
	public String getDescription() {
		return "Displays GTP' service detail as per option.";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'gtppserv':{'-c':{},'-counter':{},'-help':{}}}";
	}

	private String displayGTPPCounters(String commandParameter){

		StringBuilder responseBuilder = new StringBuilder();

		if (commandParameter.equalsIgnoreCase("total")){
			responseBuilder.append("\n    GTP' Service Request Summary");
			responseBuilder.append("\n------------------------------------");
			responseBuilder.append("\n Total Request                                :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRequestReceived());
			responseBuilder.append("\n Total Echo Request                           :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestReceived());
			responseBuilder.append("\n Total Node Alive Request                     :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestReceived());
			responseBuilder.append("\n Total Redirection Request                    :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionRequestReceived());
			responseBuilder.append("\n Total Data Record Transfer Request           :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferRequestReceived());
			responseBuilder.append("\n Total Redirection Response (Success)         :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionResponseSent());
			responseBuilder.append("\n Total Redirection Response (Failure)         :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionFailureResponseSent());
			responseBuilder.append("\n Total Echo Response                          :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoResponseSent());
			responseBuilder.append("\n Total Node Alive Response                    :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveResponseSent());
			responseBuilder.append("\n Total Data Record Transfer Response(Success) :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferResponseSent());
			responseBuilder.append("\n Total Data Record Transfer Response(Failure) :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferFailureResponseSent());
			responseBuilder.append("\n Total Version Not Supported Response         :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalVersionNotSupportedResponseSent());
			responseBuilder.append("\n Total Invalid Client Request                 :" + GTPPrimeServiceCounterListener.getInvalidClientRequestReceived());
			responseBuilder.append("\n Total Dropped Echo Request                   :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestDropped());
			responseBuilder.append("\n Total Dropped Node Alive Request             :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestDropped());
			responseBuilder.append("\n Total Dropped Redirection Request            :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionRequestDropped());
			responseBuilder.append("\n Total Dropped Data transfer Request          :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferRequestDropped());
			responseBuilder.append("\n Total Dropped Request                        :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDroppedRequest());
			responseBuilder.append("\n Total Malformed Request Packet               :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalMalformedRequestPacketReceived());
			responseBuilder.append("\n Service Up Time                              :" + GTPPrimeServiceCounterListener.getServiceUpTime());
			responseBuilder.append("\n");
			responseBuilder.append("\n Total Echo Request Sent                      :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestSent());
			responseBuilder.append("\n Total Echo Request Retry                     :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestRetry());
			responseBuilder.append("\n Total Node Alive Request Sent                :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestSent());
			responseBuilder.append("\n Total Node Alive Request Retry               :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestRetry());
			responseBuilder.append("\n Total Echo Response Received                 :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoResponseReceived());
			responseBuilder.append("\n Total Malformed Echo Response Received       :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalMalformedEchoResponseReceived());
			responseBuilder.append("\n Total Node Alive Response Received           :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveResponseReceived());
			responseBuilder.append("\n Total Malformed Node Alive Response Received :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalMalformedNodeAliveResponseReceived());
		} else {

			try {
				InetAddress.getAllByName(commandParameter);
			} catch (UnknownHostException e) {
				responseBuilder.append(getErrorMsg().toString());
				return responseBuilder.toString();
			}
			responseBuilder.append("\n    GTP' Service Request Summary");
			responseBuilder.append("\n------------------------------------");
			responseBuilder.append("\n Echo Request                           :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestReceived(commandParameter));
			responseBuilder.append("\n Node Alive Request                     :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestReceived(commandParameter));
			responseBuilder.append("\n Redirection Request                    :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionRequestReceived(commandParameter));
			responseBuilder.append("\n Data Record Transfer Request           :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferRequestReceived(commandParameter));
			responseBuilder.append("\n Redirection Response (Success)         :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionResponseSent(commandParameter));
			responseBuilder.append("\n Redirection Response (Failure)         :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionFailureResponseSent(commandParameter));
			responseBuilder.append("\n Echo Response                          :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoResponseSent(commandParameter));
			responseBuilder.append("\n Node Alive Response            		  :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveResponseSent(commandParameter));
			responseBuilder.append("\n Data Record Transfer Response(Success) :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferResponseSent(commandParameter));
			responseBuilder.append("\n Data Record Transfer Response(Failure) :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferFailureResponseSent(commandParameter));
			responseBuilder.append("\n Version Not Supported Response         :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalVersionNotSupportedResponseSent(commandParameter));
			responseBuilder.append("\n Dropped Echo Request                   :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestDropped(commandParameter));
			responseBuilder.append("\n Dropped Node Alive Request             :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestDropped(commandParameter));
			responseBuilder.append("\n Dropped Redirection Request            :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalRedirectionRequestDropped(commandParameter));
			responseBuilder.append("\n Dropped Data transfer Request          :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDataRecordTransferRequestDropped(commandParameter));
			responseBuilder.append("\n Dropped Request                        :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalDroppedRequest(commandParameter));
			responseBuilder.append("\n Total Malformed Request Packet         :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalMalformedRequestPacketReceived(commandParameter));
			responseBuilder.append("\n ");
			responseBuilder.append("\n Echo Request Sent                      :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestSent(commandParameter));
			responseBuilder.append("\n Echo Request Retry                     :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoRequestRetry(commandParameter));
			responseBuilder.append("\n Node Alive Request Sent                :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestSent(commandParameter));
			responseBuilder.append("\n Node Alive Request Retry               :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveRequestRetry(commandParameter));
			responseBuilder.append("\n Echo Response Received                 :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalEchoResponseReceived(commandParameter));
			responseBuilder.append("\n Malformed Echo Response Received       :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalMalformedEchoResponseReceived(commandParameter));
			responseBuilder.append("\n Node Alive Response Received           :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalNodeAliveResponseReceived(commandParameter));
			responseBuilder.append("\n Malformed Node Alive Response Received :" + GTPPrimeServiceCounterListener.getGTPPrimeTotalMalformedNodeAliveResponseReceived(commandParameter));
		}

		return responseBuilder.toString();

	}

	private StringWriter getHelp(){

		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] ={
				COUNTERS
		};

		String paramDesc[] ={
				"Displays GTP' Service counters.",
		};

		out.println("Usage : gtppserv <options>");
		out.println("Possible options"  );
		for(int i=0;i<paramDesc.length;i++){
			out.println("    " + fillChar(paramName[i],52)  +  paramDesc[i]   );
		}

		out.close();		
		return stringWriter;	
	}

	private String getErrorMsg(){		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Invalid Parameter or Parameter format is not correct.");
		stringBuilder.append("\n");
		stringBuilder.append("Try 'gtppserv ?'.");

		return stringBuilder.toString();

	}

}