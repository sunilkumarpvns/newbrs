package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.service.pcrf.PCRFServiceStatisticsProvider;

public class PCRFServiceStatisticsDetailProvider extends DetailProvider {
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private PCRFServiceStatisticsProvider pcrfServiceStatisticsProvider;
	
	public PCRFServiceStatisticsDetailProvider(PCRFServiceStatisticsProvider pcrfServiceStatisticsProvider) {
		detailProviderMap = new HashMap<String, DetailProvider>(1);
		this.pcrfServiceStatisticsProvider = pcrfServiceStatisticsProvider;
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length > 0){
			if(isHelpSymbol(parameters[0])){
				return getHelpMsg();
			}
		}
		
		return getPCRFServiceStatistics();
	}

	private String getPCRFServiceStatistics() {
		
		String[] dataRecord1 = new String[6];
		dataRecord1[0] = "Total-Req-Rx : ";
		dataRecord1[1] = "" + pcrfServiceStatisticsProvider.getTotalRequests();
		dataRecord1[2] = "Total-Resp-Tx         : ";
		dataRecord1[3] = "" + pcrfServiceStatisticsProvider.getTotalResponses();
		dataRecord1[4] = "";
		dataRecord1[5] = "";
		
		String[] dataRecord2 = new String[6];
		dataRecord2[0] = "Total-Dr-Rx  : ";
		dataRecord2[1] = "" + pcrfServiceStatisticsProvider.getTotalRequestDropped();
		dataRecord2[2] = "Total-Success-Resp-Tx : ";
		dataRecord2[3] = "" + pcrfServiceStatisticsProvider.getTotalSuccessResponses();
		dataRecord2[4] = "Total-Reject-Resp-Tx : ";
		dataRecord2[5] = "" + pcrfServiceStatisticsProvider.getTotalRejectResponses();
		
		int column1 = findMaxLength(dataRecord1[1].length() , dataRecord2[1].length());
		int column3 = findMaxLength(dataRecord1[3].length() , dataRecord2[3].length());
		int column5= findMaxLength(dataRecord1[5].length() , dataRecord2[5].length());
		
		String[] header = {"","","","","",""};
		int[] width = {14,column1,23,column3,22,column5};
		int[] column_alignment = {TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT};
		TableFormatter formatter = new TableFormatter(header, width, column_alignment ,TableFormatter.NO_BORDERS);
		
		formatter.addRecord(dataRecord1);
		formatter.addRecord(dataRecord2);
		formatter.addNewLine();
		formatter.add(" Avg-Req-Processing-Time(ms) : ");
		formatter.add(String.valueOf(pcrfServiceStatisticsProvider.getAvgRequestProcessingTime()));
		formatter.add("     Avg-TPS : ");
		formatter.add(String.valueOf(pcrfServiceStatisticsProvider.getAvgTPS()));
		formatter.addNewLine();
		return formatter.getFormattedValues() + getOptions();
	}
	
	private String getOptions(){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println("" + StringUtility.fillChar("",96, '-'));
		out.println("  Rx : Received   || Tx : Transfered  || Dr : Dropped");
		out.println("" + StringUtility.fillChar("",96, '-'));
		return writer.toString();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("  Description:  Display PCRF Service Statistics");
		out.println();
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "pcrf";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}