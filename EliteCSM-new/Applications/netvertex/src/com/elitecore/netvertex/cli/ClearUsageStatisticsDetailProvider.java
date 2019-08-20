package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.usagemetering.UsageMonitoringStatisticsCounter;

public class ClearUsageStatisticsDetailProvider extends DetailProvider {

	private static final String KEY = "usage";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private UsageMonitoringStatisticsCounter usageMonitoringStatisticsCounter;

	public ClearUsageStatisticsDetailProvider(UsageMonitoringStatisticsCounter usageMonitoringStatisticsCounter) {
		this.usageMonitoringStatisticsCounter = usageMonitoringStatisticsCounter;
		this.detailProviderMap = new HashMap<String, DetailProvider>(0);
	}

	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public String execute(String[] parameters) {
		if (parameters.length > 0) {

			if (HELP.equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP_OPTION)) {
				return getHelpMsg();
			}
			
			return " Invalid Option: " + parameters[0] + getHelpMsg();
		}

		usageMonitoringStatisticsCounter.reset();
		
		return "Usage statistics successfully cleared";
	}

	@Override
	public String getHelpMsg() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println(" Description: Clears usage statistics");
		out.println(" Usage : " + KEY);
		out.println();
		for (Map.Entry<String, DetailProvider> provider : detailProviderMap.entrySet()) {
			out.println(" 	" + provider + " : "
					+ provider.getValue().getDescription());
		}
		return writer.toString();}


	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
}
