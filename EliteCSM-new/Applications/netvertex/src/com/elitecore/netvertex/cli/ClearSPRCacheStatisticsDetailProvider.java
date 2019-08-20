package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.util.CacheStatistics;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class ClearSPRCacheStatisticsDetailProvider extends DetailProvider{

	private HashMap<String ,DetailProvider> detailProviderMap;
	private CacheStatistics statistics;
	private static final String KEY = "spr-cache";
	private static final String HELP = "-help";
	
	public ClearSPRCacheStatisticsDetailProvider(CacheStatistics cacheStatistics) {
		this.detailProviderMap = new HashMap<String, DetailProvider>();
		this.statistics = cacheStatistics;
	}
	
	@Override
	public String execute(String[] parameters) {
		if (parameters.length > 0) {

			if ("?".equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)) {
				return getHelpMsg();
			}
			
			return " Invalid Option: " + parameters[0] + getHelpMsg();
		}

		return "SPR cache statistics successfully cleared" + getCacheStatistics();
	}

	private String getCacheStatistics() {
		
		statistics.clear();
		String[] dataRecord = new String[6];
		dataRecord[0] = "Total-Cache-Count   : ";
		dataRecord[1] = "" + statistics.getCacheCount();
		dataRecord[2] = "";
		dataRecord[3] = "";
		dataRecord[4] = "";
		dataRecord[5] = "";

		String[] dataRecord1 = new String[6];
		dataRecord1[0] = "Total-Cache-Request : ";
		dataRecord1[1] = "" + statistics.getRequestCount();
		dataRecord1[2] = "Total-Cache-Hit      : ";
		dataRecord1[3] = "" + statistics.getHitCount();
		dataRecord1[4] = "Total-Cache-Miss     : ";
		dataRecord1[5] = "" + statistics.getMissCount();

		String[] dataRecord2 = new String[6];
		dataRecord2[0] = "Total-Load-Count    : ";
		dataRecord2[1] = "" + statistics.getLoadCount();
		dataRecord2[2] = "Average-Load-Panelty : ";
		dataRecord2[3] = "" + statistics.getAverageLoadPanelty();
		dataRecord2[4] = "Total-Eviction-Count : ";
		dataRecord2[5] = "" + statistics.getEvictionCount();

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
		int[] columnAlignments = { TableFormatter.LEFT, TableFormatter.RIGHT,
				TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.LEFT,
				TableFormatter.RIGHT };
		TableFormatter formatter = new TableFormatter(header, width,
				columnAlignments, TableFormatter.NO_BORDERS);

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
		out.println(" Usage : " + KEY);
		out.println(" Possible Options : ");
		out.println();
		detailProviderMap.keySet().stream().map(provider -> " 	" + provider + " : "
				+ detailProviderMap.get(provider).getDescription()).forEach(out::println);
		return writer.toString();
	}

	@Override
	public String getKey() {
		return KEY;
	
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
		
	}

	@Override
	public String getDescription() {
		return "Clears spr cache statistics";
	}
}
