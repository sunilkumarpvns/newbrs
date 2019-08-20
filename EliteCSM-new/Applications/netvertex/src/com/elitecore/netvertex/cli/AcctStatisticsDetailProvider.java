package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.gateway.radius.snmp.acct.AcctServerClientStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.acct.AcctServerCounters.RadiusAcctClientEntry;
import com.elitecore.netvertex.gateway.radius.snmp.acct.AcctServerStatisticsProvider;

public class AcctStatisticsDetailProvider extends DetailProvider {

	private HashMap<String ,DetailProvider> detailProviderMap;
	
	private AcctServerClientStatisticsProvider acctServerClientStatisticsProvider;
	private AcctServerStatisticsProvider acctServerStatisticsProvider;

	public AcctStatisticsDetailProvider(AcctServerStatisticsProvider acctServerStatisticsProvider, 
			AcctServerClientStatisticsProvider acctServerClientStatisticsProvider){
		this.acctServerStatisticsProvider = acctServerStatisticsProvider;
		this.acctServerClientStatisticsProvider = acctServerClientStatisticsProvider;
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
		out.println("  Description: "+" Display Radius Accounting Statistics");
		out.println();
		out.println("  Possible Options : ");
		out.println("   <Gateway Ip>     It displays Accounting Statistics for Gateway");
		return writer.toString();
	}
	
	private String getOptions(){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println("" + StringUtility.fillChar("",96, '-'));
		out.println("  Rx : Received   || Tx : Transmitted  || Dr : Dropped");
		out.println("  To : Timeouts   || Pn : Pending      || Du : Duplicate");
		out.println("" + StringUtility.fillChar("",96, '-'));
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "acct";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	private String getRadiusStatistics() {
		
		String[] header = {"","","","","",""};
		int[] width = {19,5,28,5,18,4};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);
		
		String[] data = new String[6];
		data[0] = "Accounting-Req-Rx";
		data[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalRequests());
		data[2] = "Accounting-Resp-Tx";
		data[3] = ": " + String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalResponses());
		data[4] = "";
		data[5] = "";
		formatter.addRecord(data);
		
		data[0] = "Accounting-Start-Rx";
		data[1] = ": " + String.valueOf(acctServerStatisticsProvider.getAccStartReqCntr());
		data[2] = "Accounting-Interim-Update-Rx";
		data[3] = ": " + String.valueOf(acctServerStatisticsProvider.getAccIntrUpdateReqCntr());
		data[4] = "Accounting-Stop-Rx";
		data[5] = ": " + String.valueOf(acctServerStatisticsProvider.getAccStopReqCntr());
		
		formatter.addRecord(data);
		
		String[] heade2 = {"",""};
		int[] width2 = {24,7};
		TableFormatter formatter2 = new TableFormatter(heade2, width2, TableFormatter.NO_BORDERS);
		formatter2.add(StringUtility.fillChar("",37, '-') + " General Statistics " + StringUtility.fillChar("",38, '-'));
		formatter2.addNewLine();
		formatter2.addNewLine();
		
		String[] data2 = new String[2];
		
		data2[0] = "Accounting-Du-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalDupRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Dr-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalPacketsDropped());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Invalid-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalInvalidRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Malformed-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalMalformedRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Bad-Auth-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalBadAuthenticators());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Unknown-Type";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalUnknownTypes());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-No-Record";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalNoRecords());
		formatter2.addRecord(data2);
		formatter2.addNewLine();
		return formatter.getFormattedValues() +  formatter2.getFormattedValues() + getOptions();
	}
	
	private String getRadiusStatistics(String gatewayIp) {
		
		RadiusAcctClientEntry clietEntry = acctServerClientStatisticsProvider.getClientEntry(gatewayIp); 
		
		if(clietEntry == null){
			return "RADIUS client: " + gatewayIp + " does not exist"; 
		}
		
		
		String[] header = {"","","","","",""};
		int[] width = {19,5,28,5,18,4};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);
		
		String[] data = new String[6];
		data[0] = "Accounting-Req-Rx";
		data[1] = ": " +String.valueOf(clietEntry.getReqCntr());
		
		data[2] = "Accounting-Resp-Tx";
		data[3] = ": " + String.valueOf(clietEntry.getResCntr());
		
		data[4] = "";
		data[5] = "";
		formatter.addRecord(data);
		
		data[0] = "Accounting-Start-Rx";
		data[1] = ": " + String.valueOf(clietEntry.getStartReqCntr());
		
		data[2] = "Accounting-Interim-Update-Rx";
		data[3] = ": " + String.valueOf(clietEntry.getIntrUpdateReqCntr());
		
		data[4] = "Accounting-Stop-Rx";
		data[5] = ": " + String.valueOf(clietEntry.getStopReqCntr());
		
		formatter.addRecord(data);
		
		String[] heade2 = {"",""};
		int[] width2 = {24,7};
		
		TableFormatter formatter2 = new TableFormatter(heade2, width2, TableFormatter.NO_BORDERS);
		formatter2.add(StringUtility.fillChar("",37, '-') + " General Statistics " + StringUtility.fillChar("",38, '-'));
		formatter2.addNewLine();
		formatter2.addNewLine();
		
		String[] data2 = new String[2];
		
		data2[0] = "Accounting-Du-Req";
		data2[1] = ": " +String.valueOf(clietEntry.getDupReqCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Dr-Req";
		data2[1] = ": " +String.valueOf(clietEntry.getPackDropCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Malformed-Req";
		data2[1] = ": " +String.valueOf(clietEntry.getRadMalformedReqCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Bad-Auth-Req";
		data2[1] = ": " +String.valueOf(clietEntry.getBadAuthenticatorCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-Unknown-Type";
		data2[1] = ": " +String.valueOf(clietEntry.getUnknownTypeCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "Accounting-No-Record";
		data2[1] = ": " +String.valueOf(clietEntry.getNoRecordCntr());
		formatter2.addRecord(data2);
		
		formatter2.addNewLine();
		return formatter.getFormattedValues() +  formatter2.getFormattedValues() + getOptions();
	}
}