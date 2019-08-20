package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.gateway.radius.snmp.acct.AcctServerStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.auth.AuthServerStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientStatisticsProvider;

public class RadiusStatisticsDetailProvider extends DetailProvider {

	private HashMap<String ,DetailProvider> detailProviderMap;
	
	private AcctServerStatisticsProvider acctServerStatisticsProvider;
	private AuthServerStatisticsProvider authServerStatisticsProvider;
	private DynaAuthClientStatisticsProvider dynaAuthClientStatisticsProvider;

	public RadiusStatisticsDetailProvider(AcctServerStatisticsProvider acctServerStatisticsProvider, 
			DynaAuthClientStatisticsProvider dynaAuthClientStatisticsProvider , 
			AuthServerStatisticsProvider authServerStatisticsProvider){
		this.acctServerStatisticsProvider = acctServerStatisticsProvider;
		this.authServerStatisticsProvider = authServerStatisticsProvider;
		this.dynaAuthClientStatisticsProvider = dynaAuthClientStatisticsProvider;
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length > 0){
			if("?".equals(parameters[0])){
				return getHelpMsg();
			}
			
			if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			}
			
			return "Invalid Option: " + parameters[0] + getHelpMsg();
		}
		return getRadiusStatistics() + getOptions();
	}

	private String getRadiusStatistics() {
		
		String[] header = {"","","","","",""};
		int[] width = {18,7,18,7,17,7};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);
		
		String[] data = new String[6];
		data[0] = "Acct-Req-Rx";
		data[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalRequests());
		data[2] = "Acct-Resp-Tx";
		data[3] = ": " + String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalResponses());
		data[4] = "";
		data[5] = "";
		formatter.addRecord(data);
		
		data[0] = "Access-Req-Rx";
		data[1] = ": " +String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessRequests());
		data[2] = "Access-Accept-Tx";
		data[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessAccepts());
		data[4] = "Access-Reject-Tx";
		data[5] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessRejects());
		formatter.addRecord(data);
		
		data[0] = "CoA-Req-Tx";
		data[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoAReqCntr());
		data[2] = "CoA-Ack-Rx";
		data[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getCoAAckCntr());
		data[4] = "CoA-NAck-Rx";
		data[5] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getCoANakCntr());
		formatter.addRecord(data);
		
		data[0] = "Disc-Req-Tx";
		data[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getDisReqCntr());
		data[2] = "Disc-Ack-Rx";
		data[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisAckCntr());
		data[4] = "Disc-NAck-Rx";
		data[5] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisNackCntr());
		formatter.addRecord(data);
		
		String[] heade2 = {"","","",""};
		int[] width2 = {25,7,32,7};
		int[] column_alignment2 = {TableFormatter.LEFT , TableFormatter.LEFT, TableFormatter.LEFT , TableFormatter.LEFT 
				, TableFormatter.LEFT , TableFormatter.LEFT};
		int format2 = TableFormatter.NO_BORDERS;
		TableFormatter formatter2 = new TableFormatter(heade2, width2, column_alignment2, format2);
		formatter2.add(StringUtility.fillChar("",37, '-') + " General Statistics " + StringUtility.fillChar("",37, '-'));
		formatter2.addNewLine();
		formatter2.addNewLine();
		
		String[] data2 = new String[4];
		
		data2[0] = "Acct-Du-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalDupRequests());
		data2[2] = "Access-Du-Req";
		data2[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalDupAccessRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Acct-Dr-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalPacketsDropped());
		data2[2] = "Access-Dr-Req";
		data2[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalPacketsDropped());
		formatter2.addRecord(data2);
		
		data2[0] = "Acct-Invalid-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalInvalidRequests());
		data2[2] = "Access-Invalid-Req";
		data2[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalInvalidRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Acct-Malformed-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalMalformedRequests());
		data2[2] = "Access-Malformed-Req";
		data2[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalMalformedAccessRequests());
		formatter2.addRecord(data2);
		
		data2[0] = "Acct-Bad-Auth-Req";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalBadAuthenticators());
		data2[2] = "Access-Bad-Auth-Req";
		data2[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalBadAuthenticators());
		formatter2.addRecord(data2);
		
		data2[0] = "Acct-Unknown-Type";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalUnknownTypes());
		data2[2] = "Access-Unknown-Type";
		data2[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalUnknownTypes());
		formatter2.addRecord(data2);
		
		data2[0] = "Acct-No-Record";
		data2[1] = ": " +String.valueOf(acctServerStatisticsProvider.getRadiusAccServTotalNoRecords());
		data2[2] = "Access-Challenges-Tx";
		data2[3] = ": " + String.valueOf(authServerStatisticsProvider.getRadiusAuthServTotalAccessChallenges());
		formatter2.addRecord(data2);
		
		formatter2.addNewLine();
		formatter2.addNewLine();
		
		data2[0] = "CoA-Rt-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoARetraCntr());
		data2[2] = "Disc-Rt-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisReqCntr());
		formatter2.addRecord(data2);

		data2[0] = "CoA-Pn-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoAPenReqCntr());
		data2[2] = "Disc-Pn-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisPenReqCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "CoA-To-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoATimeoutCntr());
		data2[2] = "Disc-To-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisTimeoutCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "CoA-Dr-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoAPackDropCntr());
		data2[2] = "Disc-Dr-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisPackDropCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "CoA-Auth-only-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoAAuthOnlyReqCntr());
		data2[2] = "Disc-Auth-only-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisAuthOnlyReqCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "CoA-Nack-Auth-Only-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoANakAuthOnlyReqCntr());
		data2[2] = "Disc-Nack-Auth-Only-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisNackAuthOnlyReqCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "CoA-Nack-Sess-No-Ctx-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoANakSessNoCtxCntr());
		data2[2] = "Disc-Nack-Sess-No-Ctx-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisNackSessNoCtxCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "CoA-Malformed-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getMalformedCoAResCntr());
		data2[2] = "Disc-Malformed-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getMalformedDisResCntr());
		formatter2.addRecord(data2);
		
		data2[0] = "CoA-Bad-Auth-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientStatisticsProvider.getCoABadAuthenticatorCntr());
		data2[2] = "Disc-Bad-Auth-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientStatisticsProvider.getDisBadAuthenticatorCntr());
		formatter2.addRecord(data2);
		
		formatter2.addNewLine();
		formatter2.addNewLine();
		return formatter.getFormattedValues() +  formatter2.getFormattedValues();
	}
	
	private String getOptions(){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println("" + StringUtility.fillChar("",95, '-'));
		out.println("  Rx : Received   || Tx : Transmitted  || Dr : Dropped   || Rt : Retransmitted");
		out.println("  To : Timeouts   || Pn : Pending      || Du : Duplicate");
		out.println("" + StringUtility.fillChar("",95, '-'));
		return writer.toString();
	}

	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("  Description: "+" Display Radius Listener Statistics");
		out.println();
		out.println("  Possible Options : ");
		for(String provider : detailProviderMap.keySet()){
			out.println("    " +provider);
		}
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "radius";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}