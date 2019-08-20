package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerClientStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerCounters.RadiusAuthClientEntry;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerStatisticsProvider;

public class AuthStatisticsDetailProvider extends DetailProvider {

	private HashMap<String ,DetailProvider> detailProviderMap;

	private AuthServerStatisticsProvider authServerStatisticsProvider;
	private AuthServerClientStatisticsProvider authServerClientStatisticsProvider;

	public AuthStatisticsDetailProvider(AuthServerStatisticsProvider acctServerStatisticsProvider , 
			AuthServerClientStatisticsProvider authServerClientStatisticsProvider){
		this.authServerStatisticsProvider = acctServerStatisticsProvider;
		this.authServerClientStatisticsProvider = authServerClientStatisticsProvider;
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length > 0){
			if("?".equals(parameters[0])){
				return getHelpMsg();
			}
			
			return getRadiusStatistics(parameters[0]);
		}
		return getRadiusStatistics();
	}

	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("  Description: "+" Display Radius Authenticaion Statistics");
		out.println();
		out.println("  Possible Options : ");
		out.println("   <Gateway Ip>     It displays Authenticaion Statistics for Gateway");
		return writer.toString();
	}
	
	@Override
	public String getKey() {
		return "auth";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	private String getRadiusStatistics() {
		
		String[] header = {"","","","","",""};
		int[] width = {20,5,20,5,20,5};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);
		
		String[] data = new String[6];
		
		data[0] = "Access-Req-Rx";
		data[1] = ": " +String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessRequests());
		data[2] = "Access-Accept-Tx";
		data[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessAccepts());
		data[4] = "Access-Reject-Tx";
		data[5] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessRejects());
		formatter.addRecord(data);
		
		String[] heade2 = {"",""};
		int[] width2 = {24,7};
		TableFormatter formatter2 = new TableFormatter(heade2, width2, TableFormatter.NO_BORDERS);
		
		formatter2.add(StringUtility.fillChar("",37, '-') + " General Statistics " + StringUtility.fillChar("",37, '-'));
		formatter2.addNewLine();
		formatter2.addNewLine();
		
		String[] data2 = new String[2];
		
		data2[0] = "Access-Du-Req";
		data2[1] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalDupAccessRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Dr-Req";
		data2[1] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalPacketsDropped());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Invalid-Req";
		data2[1] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalInvalidRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Malformed-Req";
		data2[1] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalMalformedAccessRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Bad-Auth-Req";
		data2[1] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalBadAuthenticators());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Unknown-Type";
		data2[1] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalUnknownTypes());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Challenges-Tx";
		data2[1] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessChallenges());
		formatter2.addRecord(data2);
		
		formatter2.addNewLine();
		return formatter.getFormattedValues() +  formatter2.getFormattedValues() + getOptions();
	}
	
	private String getOptions(){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println("" + StringUtility.fillChar("",95, '-'));
		out.println("  Rx : Received   || Tx : Transmitted  || Dr : Dropped");
		out.println("  To : Timeouts   || Pn : Pending      || Du : Duplicate");
		out.println("" + StringUtility.fillChar("",95, '-'));
		return writer.toString();
	}
	
	private String getRadiusStatistics(String gatewayIp) {
		
		
		RadiusAuthClientEntry authClientEntry = authServerClientStatisticsProvider.getClientEntry(gatewayIp);
		if(authClientEntry == null){
			return "RADIUS client: " + gatewayIp + " does not exist"; 
		}
		
		String[] header = {"","","","","",""};
		int[] width = {20,5,20,5,20,5};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);
		
		String[] data = new String[6];
		
		data[0] = "Access-Req-Rx";
		data[1] = ": " +String.valueOf(authClientEntry.getReqCntr());
		data[2] = "Access-Accept-Tx";
		data[3] = ": " + String.valueOf(authClientEntry.getAccessAcceptCntr());
		data[4] = "Access-Reject-Tx";
		data[5] = ": " + String.valueOf(authClientEntry.getAccessRejectCntr());
		formatter.addRecord(data);
		
		String[] heade2 = {"",""};
		int[] width2 = {24,7};
		TableFormatter formatter2 = new TableFormatter(heade2, width2, TableFormatter.NO_BORDERS);
		formatter2.add(StringUtility.fillChar("",37, '-') + " General Statistics " + StringUtility.fillChar("",37, '-'));
		formatter2.addNewLine();
		formatter2.addNewLine();
		
		String[] data2 = new String[2];
		
		data2[0] = "Access-Du-Req";
		data2[1] = ": " + String.valueOf(authClientEntry.getDupReqCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Dr-Req";
		data2[1] = ": " + String.valueOf(authClientEntry.getPackDropCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Malformed-Req";
		data2[1] = ": " + String.valueOf(authClientEntry.getRadMalformedReqCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Bad-Auth-Req";
		data2[1] = ": " + String.valueOf(authClientEntry.getBadAuthenticatorCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Unknown-Type";
		data2[1] = ": " + String.valueOf(authClientEntry.getUnknownTypeCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Access-Challenges-Tx";
		data2[1] = ": " + String.valueOf(authClientEntry.getAccessChallengesCntr());
		formatter2.addRecord(data2);
		
		formatter2.addNewLine();
		return formatter.getFormattedValues() +  formatter2.getFormattedValues() + getOptions();
	}
}