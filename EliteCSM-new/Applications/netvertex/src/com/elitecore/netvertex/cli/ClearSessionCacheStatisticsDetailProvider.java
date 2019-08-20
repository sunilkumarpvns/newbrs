package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.util.CacheStatistics;

public class ClearSessionCacheStatisticsDetailProvider extends DetailProvider{

	private HashMap<String ,DetailProvider> detailProviderMap;
	private CacheStatistics sessionCacheStatistics;
	private static final String HELP = "-help";
	private String key;

	public ClearSessionCacheStatisticsDetailProvider(String key,CacheStatistics cacheStatistics) {
		this.key = key;
		this.detailProviderMap = new HashMap<String, DetailProvider>();
		this.sessionCacheStatistics = cacheStatistics;
	}
	
	@Override
	public String execute(String[] parameters) {
		if (parameters.length > 0) {

			if ("?".equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)) {
				return getHelpMsg();
			}
			
			return " Invalid Option: " + parameters[0] + getHelpMsg();
		}

		return "Session cache statistics successfully cleared" + getCacheStatistics();
	}

	private String getCacheStatistics() {
		
		sessionCacheStatistics.clear();
		String[] dataRecord = new String[6];
		dataRecord[0] = "Total-Cache-Count   : ";
		dataRecord[1] = "" + sessionCacheStatistics.getCacheCount();
		dataRecord[2] = "";
		dataRecord[3] = "";
		dataRecord[4] = "";
		dataRecord[5] = "";

		String[] dataRecord1 = new String[6];
		dataRecord1[0] = "Total-Cache-Request : ";
		dataRecord1[1] = "" + sessionCacheStatistics.getRequestCount();
		dataRecord1[2] = "Total-Cache-Hit      : ";
		dataRecord1[3] = "" + sessionCacheStatistics.getHitCount();
		dataRecord1[4] = "Total-Cache-Miss     : ";
		dataRecord1[5] = "" + sessionCacheStatistics.getMissCount();

		String[] dataRecord2 = new String[6];
		dataRecord2[0] = "Total-Load-Count    : ";
		dataRecord2[1] = "" + sessionCacheStatistics.getLoadCount();
		dataRecord2[2] = "Average-Load-Panelty : ";
		dataRecord2[3] = "" + sessionCacheStatistics.getAverageLoadPanelty();
		dataRecord2[4] = "Total-Eviction-Count : ";
		dataRecord2[5] = "" + sessionCacheStatistics.getEvictionCount();

		int column0 = findMaxLength(dataRecord[0].length(),
				dataRecord1[0].length(), dataRecord2[0].length());
		int column1 = findMaxLength(dataRecord[1].length(),
				dataRecord1[1].length(), dataRecord2[1].length());
		int column2 = findMaxLength(dataRecord1[2].length(),
				dataRecord2[2].length());
		int column3 = findMaxLength(dataRecord1[3].length(),
				dataRecord2[3].length());
		int column4 = findMaxLength(dataRecord1[4].length(),
				dataRecord2[4].length());
		int column5 = findMaxLength(dataRecord1[5].length(),
				dataRecord2[5].length());

		String[] header = { "", "", "", "", "", "" };
		int[] width = { column0, column1, column2, column3, column4, column5 };
		int[] columnAlignment = { TableFormatter.LEFT, TableFormatter.RIGHT,
				TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.LEFT,
				TableFormatter.RIGHT };
		TableFormatter formatter = new TableFormatter(header, width,
				columnAlignment, TableFormatter.NO_BORDERS);

		formatter.addNewLine();
		formatter.addRecord(dataRecord);
		formatter.addNewLine();
		formatter.addRecord(dataRecord1);
		formatter.addNewLine();
		formatter.addRecord(dataRecord2);
		formatter.addNewLine();

		return formatter.getFormattedValues();
	}

	@Override
	public String getHelpMsg() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println(" Description: " + getDescription());
		out.println(" Usage : " + key);
		out.println(" Possible Options : ");
		out.println();
		for (Map.Entry<String, DetailProvider> provider : detailProviderMap.entrySet()) {
			out.println(" 	" + provider + " : "
					+ provider.getValue().getDescription());
		}
		return writer.toString();
	}

	@Override
	public String getKey() {
		return key;
	
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
		
	}

	@Override
	public String getDescription() {
		return "Clears session cache statistics";
	}
}
