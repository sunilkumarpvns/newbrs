package com.elitecore.aaa.radius.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.policies.accesspolicy.data.AccessPolicyTime;
import com.elitecore.aaa.core.policies.accesspolicy.data.AccountAccessPolicyData;
import com.elitecore.aaa.mibs.radius.accounting.server.RadiusAcctServiceMIBListener;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.ServerContext;

public abstract class AcctServCommand extends EliteBaseCommand {	
	
	private final static String SERVICE_POLICY        = "-sp";
	
	private final static String SUMMARY        	      = "-s";

	private final static String THREADS_QUEUE_DETAILS = "-t";
	
	private final static String HELP           		  = "?";
	
	private final static String ACCESS_POLICY        = "-ap";

	private ServerContext serverContext;
	
	public AcctServCommand(ServerContext serverContext) {
		this.serverContext = serverContext;
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
				if(parameter.equalsIgnoreCase(ACCESS_POLICY)){
					Map<String,AccountAccessPolicyData> policyMap = AccessPolicyManager.getInstance().getAcccessPolicyMap();					
					if(commandParameters!=null){
						response = displayAccessPolicyDetail(policyMap,commandParameters);
					}else{
						response = displayPolicyList(policyMap,"Access");
					}					
				}else if(parameter.trim().equals(SERVICE_POLICY)){
					if(commandParameters!=null){
						response = getServicePolicyDetail(commandParameters);
					}else{
						response = getServicepolicyList();
					}
					
					
				}else if(parameter.trim().equals(SUMMARY)){
					response = getServiceRequestSummary();					
				}else if(parameter.trim().equals(THREADS_QUEUE_DETAILS)){
					response = getServiceThreadSummary();
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
		return "acctserv";
	}

	@Override
	public String getDescription() {
		return "Displays Acct service detail as per option.";
	}
	
	private StringWriter getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] ={
				THREADS_QUEUE_DETAILS,
				SUMMARY, 
				SERVICE_POLICY + " [ServicePolicyName]", 
				ACCESS_POLICY + " [PolicyName]"};					 

		String paramDesc[] ={
				"Displays detail of Threads and Queue currently in used.",
				"Displays Accounting Service summary.",
				"Displays list of Service Policy from Cache. If supplied with the Service Policy Name then, it displays the details of that Policy.",
				"Displays list of Access Policy from Cache. If supplied with the Access Policy Name then, it displays the details of that Policy.",
		};
		
		out.println("Usage : acctserv <options>");
		out.println("Possible options"  );
		//TODO: only ACCESS_POLICY,SERVICE_POLICY,THREADS_QUEUE_DETAILS and SUMMARY option is implemented
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
		stringBuilder.append("Try 'acctserv ?'.");
		
		return stringBuilder.toString();
		
	}
	
	
	private String displayPolicyList(Map<String,AccountAccessPolicyData> resultMap, String policyType){
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		Set <String> policyList= resultMap.keySet();

		if(policyList!=null && !policyList.isEmpty()){
			Iterator <String> policyListIterator= policyList.iterator();
			int iSrNo=1;
			
			out.println("\nList Of " + policyType + " Policies in Cache");
			out.println("--------------------------------");
			while(policyListIterator.hasNext()){
				out.println(iSrNo++ + ") " + policyListIterator.next());
			}
			return stringBuffer.toString();
		}else{
			return "No " + policyType + " Policy available in cache!";
		}
	}
	
	private String displayAccessPolicyDetail(Map<String,AccountAccessPolicyData> resultMap, String accessPolicyName){

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		String strStartDay=null;
		String strEndDay=null;
		AccountAccessPolicyData acctAccessPolicyData = (AccountAccessPolicyData)resultMap.get(accessPolicyName);
		if (acctAccessPolicyData!=null){
			ArrayList<AccessPolicyTime> arrAccessPolicyTime = acctAccessPolicyData.getAccessPolicyTimeSlots();
			AccessPolicyTime accessPolicyTimeSlaps=null;
			char cDefaultAccess=acctAccessPolicyData.getPolicyAccessFlag();
			out.println("**********************************************************************");
			out.println("\t\t\tAccess Policy Details");
			out.println("**********************************************************************");
			out.println("Name           :  " + acctAccessPolicyData.getAccessPolicyName());
			out.println("Default Access :  " + (cDefaultAccess=='A'?"Allowed":"Denied"));
			out.println("Description    :  " + (acctAccessPolicyData.getAccessPolicyDesc()!=null?acctAccessPolicyData.getAccessPolicyDesc():""));
			if(!arrAccessPolicyTime.isEmpty()){
				out.println("\n\n\t\t\t"+ (cDefaultAccess=='A'?"Denied":"Allowed") + " Timeslaps");
				out.println("----------------------------------------------------------------------");
				out.println("StartDay   StartTime      EndDay   EndTime   AccessStatus");
				out.println("----------------------------------------------------------------------");

				for(int i=0;i<arrAccessPolicyTime.size();i++){
					accessPolicyTimeSlaps=arrAccessPolicyTime.get(i);
					strStartDay=getWeekDayFromId(accessPolicyTimeSlaps.getStartDay());
					strEndDay=getWeekDayFromId(accessPolicyTimeSlaps.getEndDay());

					out.print("  "+ strStartDay  + "\t     "+ (accessPolicyTimeSlaps.getStartHour()+":"+accessPolicyTimeSlaps.getStartMinute()) +"\t");
					out.print("   " + strEndDay  + "\t     "+ (accessPolicyTimeSlaps.getStopHour()+":"+accessPolicyTimeSlaps.getStopMinute())+ "\t"+ (cDefaultAccess=='A'?'D':'A')+ "\n");
				}
				out.println("----------------------------------------------------------------------");
			}else{
				out.println("\nNo Timeslaps created for this policy!");
			}
			out.println("**********************************************************************");
			return stringBuffer.toString();
		}else{
			return "No such Access Policy exist!\nKindly verify the name and Retry.";
		}
	}	

	private String getWeekDayFromId(int day){
		 String strWeekDay=null;
		 switch(day){
		 case 0:
			 strWeekDay="SUN"; 
			 break;
		 case 1:
			 strWeekDay="MON"; 
			 break;
		 case 2:
			 strWeekDay="TUE"; 
			 break;
		 case 3:
			 strWeekDay="WED"; 
			 break;
		 case 4:
			 strWeekDay="THR"; 
			 break;
		 case 5:
			 strWeekDay="FRI"; 
			 break;
		 default:
			 strWeekDay="SAT"; 
		 }	
	return strWeekDay; 	
	}
	public abstract String getServiceThreadSummary();
	private String getServiceRequestSummary(){
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n    Acct Service Request Summary");
		responseBuilder.append("\n------------------------------------");
		responseBuilder.append("\nSystem MPS 				:" + serverContext.addAndGetAverageRequestCount(0));
		responseBuilder.append("\nSystem TPS                :" + serverContext.getTPSCounter());
		if(serverContext.getTPSCounter() != 0){
			responseBuilder.append("\nAvg Response Time         :" + serverContext.addTotalResponseTime(0) + "ms");
		}else{
			responseBuilder.append("\nAvg Response Time         :0ms");
		}
		responseBuilder.append("\n------------------------------------");
		responseBuilder.append("\nAccounting-Request        :" + RadiusAcctServiceMIBListener.getRadiusAccServTotalRequests());
		responseBuilder.append("\nAccounting-Response       :"+ RadiusAcctServiceMIBListener.getRadiusAccServTotalResponses());
		responseBuilder.append("\nDropped                   :" + RadiusAcctServiceMIBListener.getRadiusAccServTotalPacketsDropped());
		responseBuilder.append("\nDuplicate Request         :" + RadiusAcctServiceMIBListener.getRadiusAccServTotalDupRequests());
		responseBuilder.append("\nUnknown Type Request      :" + RadiusAcctServiceMIBListener.getRadiusAccServTotalUnknownTypes());
		responseBuilder.append("\nMalformed Access Request  :" + RadiusAcctServiceMIBListener.getRadiusAccServTotalMalformedRequests());
		responseBuilder.append("\nInvalid Request           :" + RadiusAcctServiceMIBListener.getRadiusAccServTotalInvalidRequests());
		responseBuilder.append("\nBad Authenticator Request :" + RadiusAcctServiceMIBListener.getRadiusAccServTotalBadAuthenticators());
		
		return responseBuilder.toString();
	}
	public abstract String getServicepolicyList(); 
	public abstract String getServicePolicyDetail(String commandParameters);
	@Override
	public String getHotkeyHelp() {
		return "{'acctserv':{'-sp':{},'-s':{},'-t':{},'?':{},'-ap':{}}}";
	}

}
