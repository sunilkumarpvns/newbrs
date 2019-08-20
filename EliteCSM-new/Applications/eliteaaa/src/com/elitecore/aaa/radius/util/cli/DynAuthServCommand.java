package com.elitecore.aaa.radius.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.elitecore.aaa.mibs.radius.dynauth.server.RadiusDynAuthServerMIB;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.ServerContext;

public abstract class DynAuthServCommand extends EliteBaseCommand{
	
	private final static String SUMMARY        	      = "-s";
	
	private final static String THREADS_QUEUE_DETAILS = "-t";
	
	private final static String HELP           		  = "?";
	
	private final static String SERVICE_POLICY        = "-sp";

	private ServerContext serverContext;
	
	public DynAuthServCommand(ServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	@Override
	public String execute(String parameter) {
		String response = "--";
		
		if(parameter != null){
			StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
			String commandParameters=null;	

			if (tokenizer.hasMoreTokens())
				parameter= tokenizer.nextToken().trim();
			if (tokenizer.hasMoreTokens())
				commandParameters=tokenizer.nextToken().trim();
			
			if(parameter.length() > 0 ){				
				
				if(parameter.equals(SUMMARY)){
					response = getServiceRequestSummary();					
				}else if(parameter.equals(THREADS_QUEUE_DETAILS)){
					response = getServiceThreadSummary();
				}else if(parameter.equals(SERVICE_POLICY)){
					if(commandParameters!=null){
						response = getServicePolicyDetail(commandParameters);
					}else{
						response = getServicepolicyList();
					}					
				}else if(parameter.equals(HELP)){
					response = getHelp();
				}else{
					response = getErrorMsg();
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
		return "dynauthserv";
	}

	@Override
	public String getDescription() {
		return "Displays Dynauth service detail as per option.";
	}
	
	private String getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] ={
				THREADS_QUEUE_DETAILS,
				SUMMARY, SERVICE_POLICY + " [ServicePolicyName]"};					 

		String paramDesc[] ={
				"Displays detail of Threads and Queue currently in used.",
				"Displays DynAuth Service summary.",
				"Displays list of Service Policy from Cache. If supplied with the Service Policy Name then, it displays the details of that Policy."};
		
		out.println("Usage : dynauthserv <options>");
		out.println("Possible options"  );
		for(int i=0;i<paramDesc.length;i++){
			out.println("    " + fillChar(paramName[i],52)  +  paramDesc[i]   );
		}
		out.close();		
		return stringWriter.toString();	
	}

	private String getErrorMsg(){		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Invalid Parameter or Parameter format is not correct.");
		stringBuilder.append("\n");
		stringBuilder.append("Try 'dynauthserv ?'.");
		
		return stringBuilder.toString();
		
	}
	
	public abstract String getServiceThreadSummary();
	private String getServiceRequestSummary(){
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n    DynAuth Service Request Summary");
		responseBuilder.append("\n--------------------------------------------");
		responseBuilder.append(fillChar("\nSystem MPS",33) + " : " + serverContext.addAndGetAverageRequestCount(0));
		responseBuilder.append(fillChar("\nSystem TPS",33) + " : " + serverContext.getTPSCounter());
		if(serverContext.getTPSCounter() != 0){
			responseBuilder.append(fillChar("\nAvg Response Time",33) + " : " + serverContext.addTotalResponseTime(0) + "ms");
		}else{
			responseBuilder.append(fillChar("\nAvg Response Time",33) + " : 0ms");
		}
		responseBuilder.append("\n------------------------------------");
		responseBuilder.append(fillChar("\nCOA-Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOARequests());
		responseBuilder.append(fillChar("\nDISCON-Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDisConnectRequests());
		responseBuilder.append(fillChar("\nCOA-Ack",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOAAck());
		responseBuilder.append(fillChar("\nCOA-Nck",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOANak());
		responseBuilder.append(fillChar("\nDISCON-Ack",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalConnectAck());
		responseBuilder.append(fillChar("\nDISCON-Nck",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDisConnectNak());
		responseBuilder.append(fillChar("\nCOA-Request Dropped",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOAPacketsDropped());
		responseBuilder.append(fillChar("\nDISCON-Request Dropped",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDisConnectPacketsDropped());
		responseBuilder.append(fillChar("\nDuplicate COA-Request",33)+" : " +  RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDupCOARequests());
		responseBuilder.append(fillChar("\nDuplicate DISCON-Request",33)+" : " +  RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDupDisConnectRequests());
		responseBuilder.append(fillChar("\nUnknown Type Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalUnknownTypes());
		responseBuilder.append(fillChar("\nMalformed COA-Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalMalformedCOARequests());
		responseBuilder.append(fillChar("\nMalformed DISCON-Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalMalformedDisConnectRequests());
		responseBuilder.append(fillChar("\nInvalid Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalInvalidRequests());
		responseBuilder.append(fillChar("\nBad Authenticator COA-Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalBadAuthenticatorsCOARequests());
		responseBuilder.append(fillChar("\nBad Authenticator DISCON-Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalBadAuthenticatorsDisConnectRequests());
		responseBuilder.append(fillChar("\nTotal Bad Authenticator Request",33)+" : " + RadiusDynAuthServerMIB.getRadiusDynAuthServTotalBadAuthenticatorsRequests());
		return responseBuilder.toString();
	}
	public abstract String getServicepolicyList(); 
	public abstract String getServicePolicyDetail(String commandParameters);
	@Override
	public String getHotkeyHelp() {
		return "{'dynauthserv':{'-s':{},'-t':{},'-sp':{}}}";
	}	

}
