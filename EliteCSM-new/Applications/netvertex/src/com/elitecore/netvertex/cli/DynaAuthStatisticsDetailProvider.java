package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientCounters;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientServerStatisticsProvider;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientStatisticsProvider;

public class DynaAuthStatisticsDetailProvider extends DetailProvider {

	private DynaAuthClientStatisticsProvider dynaAuthClientStatisticsProvider;
	private DynaAuthClientServerStatisticsProvider dynaAuthClientServerStatisticsProvider;

	private HashMap<String ,DetailProvider> detailProviderMap;

	public DynaAuthStatisticsDetailProvider(DynaAuthClientStatisticsProvider dynaAuthClientStatisticsProvider , 
			DynaAuthClientServerStatisticsProvider dynaAuthClientServerStatisticsProvider){
		this.dynaAuthClientStatisticsProvider = dynaAuthClientStatisticsProvider;
		this.dynaAuthClientServerStatisticsProvider = dynaAuthClientServerStatisticsProvider;
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
		out.print("  Description: "+" Display Radius Dynamic-Authrization Statistics");
		out.println();
		out.println("  Possible Options : ");
		out.println("   <Gateway Ip>     It displays Dynamic-Authrization Statistics for Gateway");
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "dynaauth";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	private String getRadiusStatistics() {

		String[] header = {"","","","","",""};
		int[] width = {18,7,18,7,17,7};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);

		String[] data = new String[6];

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
		return formatter.getFormattedValues() +  formatter2.getFormattedValues() + getOptions();
	}

	private String getRadiusStatistics(String gatewayIp) {
		
		DynaAuthClientCounters.DynAuthServerEntry serverEntry = dynaAuthClientServerStatisticsProvider.getClientEntry(gatewayIp);
		
		if(serverEntry == null){
			return "RADIUS client: " + gatewayIp + " does not exist";
		}

		String[] header = {"","","","","",""};
		int[] width = {18,7,18,7,17,7};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.NO_BORDERS);

		String[] data = new String[6];

		data[0] = "CoA-Req-Tx";
		data[1] = ": " +String.valueOf(serverEntry.getCoAReqCntr());
		data[2] = "CoA-Ack-Rx";
		data[3] = ": " + String.valueOf(serverEntry.getCoAAckCntr());
		data[4] = "CoA-NAck-Rx";
		data[5] = ": " + String.valueOf(serverEntry.getDynAuthCoANakCntr());
		formatter.addRecord(data);

		data[0] = "Disc-Req-Tx";
		data[1] = ": " +String.valueOf(serverEntry.getDisReqCntr());
		data[2] = "Disc-Ack-Rx";
		data[3] = ": " + String.valueOf(serverEntry.getDisAckCntr());
		data[4] = "Disc-NAck-Rx";
		data[5] = ": " + String.valueOf(serverEntry.getDisNakCntr());
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

		data2[0] = "CoA-Rt-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoARetransmissions(gatewayIp));
		data2[2] = "Disc-Rt-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconRetransmissions(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-Pn-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoAPendingRequests(gatewayIp));
		data2[2] = "Disc-Pn-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconPendingRequests(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-To-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoATimeouts(gatewayIp));
		data2[2] = "Disc-To-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconTimeouts(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-Dr-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoAPacketsDropped(gatewayIp));
		data2[2] = "Disc-Dr-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconPacketsDropped(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-Auth-only-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoAAuthOnlyRequest(gatewayIp));
		data2[2] = "Disc-Auth-only-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconAuthOnlyRequests(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-Nack-Auth-Only-Req";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoANakAuthOnlyRequest(gatewayIp));
		data2[2] = "Disc-Nack-Auth-Only-Req";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconNakAuthOnlyRequest(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-Nack-Sess-No-Ctx-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoANakSessNoContext(gatewayIp));
		data2[2] = "Disc-Nack-Sess-No-Ctx-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconNakSessNoContext(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-Malformed-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientMalformedCoAResponses(gatewayIp));
		data2[2] = "Disc-Malformed-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconMalformedResponses(gatewayIp));
		formatter2.addRecord(data2);

		data2[0] = "CoA-Bad-Auth-Resp";
		data2[1] = ": " +String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientCoABadAuthenticators(gatewayIp));
		data2[2] = "Disc-Bad-Auth-Resp";
		data2[3] = ": " + String.valueOf(dynaAuthClientServerStatisticsProvider.getRadiusDynAuthClientDisconBadAuthenticators(gatewayIp));
		formatter2.addRecord(data2);

		formatter2.addNewLine();
		formatter2.addNewLine();
		return formatter.getFormattedValues() +  formatter2.getFormattedValues() + getOptions();
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
}