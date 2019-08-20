package com.elitecore.netvertex.cli;

import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.util.Cache;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ClearSPRCacheDetailProvider extends DetailProvider{

	private HashMap<String ,DetailProvider> detailProviderMap;
	@Nonnull private Cache<String, SPRInfo> cache;
	private static final String KEY = "spr-cache";
	private static final String HELP = "-help";
	private static final String ALL = "-all";
	private static final String IDLE = "-idle";
	
	public ClearSPRCacheDetailProvider(Cache<String, SPRInfo> cache) {
		this.detailProviderMap = new HashMap<String, DetailProvider>();
		this.cache = cache;
	}
	
	@Override
	public String execute(String[] parameters) {
		if (parameters.length > 0) {

			if ("?".equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)) {
				return getHelpMsg();
			}
			
			if (ALL.equalsIgnoreCase(parameters[0])) {
				int count = cache.flush();
				return "Total " + count + " spr cache flushed" + getCacheStatistics() ;
			}
			
			if (IDLE.equalsIgnoreCase(parameters[0])) {
				int count = cache.evict();
				return "Total " + count + " spr cache flushed" + getCacheStatistics() ;
			}
			
			return " Invalid Option: " + parameters[0] + getHelpMsg();
		}

		return getHelpMsg();
	}

	private String getCacheStatistics() {
		
		String[] dataRecord = new String[6];
		dataRecord[0] = "Total-Cache-Count   : ";
		dataRecord[1] = Long.toString(cache.statistics().getCacheCount());
		dataRecord[2] = "";
		dataRecord[3] = "";
		dataRecord[4] = "";
		dataRecord[5] = "";

		String[] dataRecord1 = new String[6];
		dataRecord1[0] = "Total-Cache-Request : ";
		dataRecord1[1] = Long.toString(cache.statistics().getRequestCount());
		dataRecord1[2] = "Total-Cache-Hit      : ";
		dataRecord1[3] = Long.toString(cache.statistics().getHitCount());
		dataRecord1[4] = "Total-Cache-Miss     : ";
		dataRecord1[5] = Long.toString(cache.statistics().getMissCount());

		String[] dataRecord2 = new String[6];
		dataRecord2[0] = "Total-Load-Count    : ";
		dataRecord2[1] = Long.toString(cache.statistics().getLoadCount());
		dataRecord2[2] = "Average-Load-Panelty : ";
		dataRecord2[3] = Long.toString(cache.statistics().getAverageLoadPanelty());
		dataRecord2[4] = "Total-Eviction-Count : ";
		dataRecord2[5] = Long.toString(cache.statistics().getEvictionCount());

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
		IndentingWriter out = new IndentingPrintWriter(writer);
		out.println();
		out.println(" Description: " + getDescription());
		out.println(" Usage : " + KEY);
		out.println(" Possible Options : ");
		out.incrementIndentation();
		out.println(" " + ALL + " : flush all spr cache and statistics");
		out.println(" " + IDLE + " : flush idle spr cache");
		
		for (Map.Entry<String, DetailProvider> provider : detailProviderMap.entrySet()) {
			out.println(" " + provider.getKey() + " : "
					+ provider.getValue().getDescription());
		}
		
		out.decrementIndentation();
		out.close();
		return writer.toString();
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("'"+KEY+"':{'"+HELP+"':{},'" +ALL +"':{},'" +IDLE +"':{}");
		for(DetailProvider provider : detailProviderMap.values()){
				out.print("," + provider.getHotkeyHelp());
		}
		out.print("}");
		return writer.toString() ;
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
		return "Clears spr cache and statistics";
	}
}
