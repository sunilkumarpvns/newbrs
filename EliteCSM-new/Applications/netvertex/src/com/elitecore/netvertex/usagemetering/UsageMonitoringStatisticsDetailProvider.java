package com.elitecore.netvertex.usagemetering;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.extended.UsageMonitoringStatisticsProvider;

public class UsageMonitoringStatisticsDetailProvider extends DetailProvider {

	private HashMap<String ,DetailProvider> detailProviderMap;
	private UsageMonitoringStatisticsProvider monitoringStatisticsProvider;

	public UsageMonitoringStatisticsDetailProvider(
			UsageMonitoringStatisticsProvider monitoringStatisticsProvider) {
		this.monitoringStatisticsProvider =monitoringStatisticsProvider;
		detailProviderMap = new HashMap<String, DetailProvider>(1);
	}
	@Override
	public String execute(String[] parameters) {
		if(parameters.length > 0){
			if(isHelpSymbol(parameters[0])){
				return getHelpMsg();
			}
		}

		return getUsageStatistics();
	}

	private String getUsageStatistics() {

		String[] dataRecord1 = new String[2];
		dataRecord1[0] = "Total-Usage-Reported-In-Last-Hour     :";
		dataRecord1[1] = String.valueOf(monitoringStatisticsProvider.getTotalUsageReportedInLastHour());

		String[] dataRecord2 = new String[2];
		dataRecord2[0] = "Total-Usage-Reported-In-Day           :";
		dataRecord2[1] = String.valueOf(monitoringStatisticsProvider.getTotalUsageReportedInCurrentDay());

		String[] dataRecord3 = new String[2];
		dataRecord3[0] = "Total-Usage-Reported-In-Yesterday     :";
		dataRecord3[1] = String.valueOf(monitoringStatisticsProvider.getTotalUsageReportedInYesterday());

		String[] dataRecord4 = new String[2];
		dataRecord4[0] = "Total-Usage-Reported-In-Last-24-Hours :";
		dataRecord4[1] = String.valueOf(monitoringStatisticsProvider.getTotalUsageReportedInLast24Hours());
		
		String[] dataRecord5 = new String[2];
		dataRecord5[0] = "Reset-Time                            :";
		dataRecord5[1] = new Date(monitoringStatisticsProvider.getLastResetTimeInMillis()).toString();
		
		String[] header = {"",""};
		
		int colum1 = findMaxLength(dataRecord1[0].length() , dataRecord2[0].length() ,dataRecord3[0].length() , 
				dataRecord4[0].length(), dataRecord5[0].length());
		int colum2 = findMaxLength(dataRecord1[1].length() , dataRecord2[1].length() ,dataRecord3[1].length() , 
				dataRecord4[1].length(), dataRecord5[1].length());
		
		int[] width = {colum1 , colum2};
		int[] column_alignment = {TableFormatter.LEFT,TableFormatter.RIGHT};

		TableFormatter formatter = new TableFormatter(header, width, column_alignment ,TableFormatter.NO_BORDERS);

		formatter.addRecord(dataRecord1);
		formatter.addNewLine();
		formatter.addRecord(dataRecord2);
		formatter.addNewLine();
		formatter.addRecord(dataRecord3);
		formatter.addNewLine();
		formatter.addRecord(dataRecord4);
		formatter.addNewLine();
		formatter.addRecord(dataRecord5);
		formatter.addNewLine();
		return formatter.getFormattedValues();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("  Description: Display Usage Statistics");
		out.println();
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "usage";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}
