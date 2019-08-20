package com.elitecore.aaa.rm.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.elitecore.aaa.mibs.rm.ippool.server.RMIPPoolServiceMIBListener;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public abstract class IPPoolServCommand extends EliteBaseCommand {	
	
	
	private final static String SUMMARY        	      = "-s";

	private final static String THREADS_QUEUE_DETAILS = "-t";
	
	private final static String HELP           		  = "?";
	
	private final static String CLIENTSTATISTICS = "-ns";
	
	private RMIPPoolServiceMIBListener ipPoolServiceListener;
	
	public IPPoolServCommand(RMIPPoolServiceMIBListener ipPoolServiceListener) {
		this.ipPoolServiceListener = ipPoolServiceListener;
	}

	@Override
	public String execute(String parameter) {
		// TODO Auto-generated method stub
		
		String response = "--";
		
		if(parameter != null){

			StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
			String commandParameters=null;

			if (tokenizer.hasMoreTokens())
				parameter= tokenizer.nextToken();
			
			if (tokenizer.hasMoreTokens())
				commandParameters= tokenizer.nextToken();
			
			if(parameter.trim().length() > 0 ){				
				if(parameter.trim().equals(SUMMARY)){
					response = getServiceRequestSummary();					
				}else if(parameter.trim().equals(THREADS_QUEUE_DETAILS)){
					response = getServiceThreadSummary();
				}else if (parameter.trim().equals(CLIENTSTATISTICS)) {
					response  = getNasClientStatistics();
				}else if(parameter.trim().equals(HELP)){
					response = getHelp().toString();
				}else{
					response = getErrorMsg().toString();
				}				
			}else{ // if parameter's length is  = 0;
				StringWriter stringWriter = new StringWriter();
				stringWriter.append("Required parameter missing.");
				stringWriter.append("\n");
				stringWriter.append(getHelp().toString());
				response = stringWriter.toString();
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
		return "ippoolserv";
	}

	@Override
	public String getDescription() {
		return "Displays IP Pool service detail as per option.";
	}
	
	private StringWriter getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] ={
				THREADS_QUEUE_DETAILS,
				SUMMARY,
				CLIENTSTATISTICS};					 

		String paramDesc[] ={
				"Displays detail of Threads and Queue currently in used.",
				"Displays IP Pool Service summary.",
				"Displays details of Client Statistics."
		};
		
		out.println("Usage : ippoolserv <options>");
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
		stringBuilder.append("Try 'ippoolserv ?'.");
		
		return stringBuilder.toString();
		
	}

	public abstract String getServiceThreadSummary();
	
	private String getServiceRequestSummary(){
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n    IP Pool Service Request Summary");
		responseBuilder.append("\n---------------------------------------");
		responseBuilder.append("\nIP-Pool-Request           :" + ipPoolServiceListener.getRMIPPoolServTotalRequests());
		responseBuilder.append("\nIP-Pool-Response          :"+ ipPoolServiceListener.getRMIPPoolServTotalResponses());
		responseBuilder.append("\nDropped                   :" + ipPoolServiceListener.getRMIPPoolServTotalPacketsDropped());
		responseBuilder.append("\nDuplicate Request         :" + ipPoolServiceListener.getRMIPPoolServTotalDupRequests());
		responseBuilder.append("\nUnknown Type Request      :" + ipPoolServiceListener.getRMIPPoolServTotalUnknownTypes());
		responseBuilder.append("\nMalformed Request         :" + ipPoolServiceListener.getRMIPPoolServTotalMalformedRequests());
		responseBuilder.append("\nInvalid Request           :" + ipPoolServiceListener.getRMIPPoolServTotalInvalidRequests());
		responseBuilder.append("\nBad Authenticator Request :" + ipPoolServiceListener.getRMIPPoolServTotalBadAuthenticators());

		responseBuilder.append("\nDiscover Request          :" + ipPoolServiceListener.getIPAddressDiscoverTotalRequest());
		responseBuilder.append("\nDecline Response          :" + ipPoolServiceListener.getIPAddressDeclineTotalResponse());
		responseBuilder.append("\nOffer Success             :" + ipPoolServiceListener.getIPAddressOfferTotalResponse());
		responseBuilder.append("\nAllocation Request        :" + ipPoolServiceListener.getIPAddressTotalAllocationRequest());
		responseBuilder.append("\nRelease Request           :" + ipPoolServiceListener.getIPAddressTotalReleaseRequest());
		responseBuilder.append("\nUpdate Request            :" + ipPoolServiceListener.getIPAddressTotalUpdateRequest());
		return responseBuilder.toString();
	}

	/**
	 * Display the NAS Client Statistics Summary for 
	 * all the NAS Client which are interacting with
	 * IP-Pool-Service. 
	 */

	private String getNasClientStatistics(){
		if(ipPoolServiceListener != null)
			return ipPoolServiceListener.toString();
		else
			return "no data available for NAS clients.";
	}
	
	@Override
	public String getHotkeyHelp() {
		return "{'ippoolserv':{'-s':{},'-t':{},'-ns':{},'?':{}}}";
	}
}
