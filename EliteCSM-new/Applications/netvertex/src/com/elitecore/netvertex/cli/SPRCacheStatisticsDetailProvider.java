package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.util.CacheStatistics;


/**
 * <PRE>
 * {@link SPRCacheStatisticsDetailProvider} provides counters of SPR cache in CLI.
 * 
 * Command: "show statistics cache spr-cache"
 * </PRE>
 * @author chetan.sankhala
 */
public class SPRCacheStatisticsDetailProvider extends DetailProvider{

	private HashMap<String ,DetailProvider> detailProviderMap;
	private CacheStatistics statistics;
	private static final String KEY = "spr-cache";
	private static final String HELP = "-help";
	
	public SPRCacheStatisticsDetailProvider(CacheStatistics statistics) {
		this.detailProviderMap = new HashMap<String, DetailProvider>(8);
		this.statistics = statistics;
	}
	
	@Override
	public String execute(String[] parameters) {
		if (parameters.length > 0) {
			if("?".equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)){
				return getHelpMsg();
			}
			
			if (detailProviderMap.containsKey(parameters[0])) {
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			}
			
			return "Invalid Option: " + parameters[0] + getHelpMsg();
		}
		
		return getCacheStatistics() + getCounterDiscription();
	}

	private String getCacheStatistics() {
		
		
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
		dataRecord1[2] = "Total-Cache-Hit     : ";
		dataRecord1[3] = "" + statistics.getHitCount();
		dataRecord1[4] = "Total-Cache-Miss    : ";
		dataRecord1[5] = "" + statistics.getMissCount();
		
		String[] dataRecord2 = new String[6];
		dataRecord2[0] = "Total-Load-Count    : ";
		dataRecord2[1] = "" + statistics.getLoadCount();
		dataRecord2[2] = "Average-Load-Penalty : ";
		dataRecord2[3] = "" + statistics.getAverageLoadPanelty();
		dataRecord2[4] = "Total-Eviction-Count : ";
		dataRecord2[5] = "" + statistics.getEvictionCount();
		
		int column0 = findMaxLength(dataRecord[0].length(), dataRecord1[0].length() , dataRecord2[0].length());
		int column1 = findMaxLength(dataRecord[1].length(), dataRecord1[1].length() , dataRecord2[1].length());
		int column2 = findMaxLength(dataRecord[2].length(), dataRecord1[2].length() , dataRecord2[2].length());
		int column3 = findMaxLength(dataRecord[3].length(), dataRecord1[3].length() , dataRecord2[3].length());
		int column4 = findMaxLength(dataRecord[4].length(), dataRecord1[4].length() , dataRecord2[4].length());
		int column5= findMaxLength(dataRecord[5].length(), dataRecord1[5].length() , dataRecord2[5].length());
		
		String[] header = {"","","","","",""};
		int[] width = {column0,column1,column2,column3,column4,column5};
		int[] column_alignment = {TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT};
		TableFormatter formatter = new TableFormatter(header, width, column_alignment ,TableFormatter.NO_BORDERS);
		
		formatter.addRecord(dataRecord);
		formatter.addNewLine();
		formatter.addRecord(dataRecord1);
		formatter.addNewLine();
		formatter.addRecord(dataRecord2);
		formatter.addNewLine();
		
		return formatter.getFormattedValues();
	}
	
	private String getCounterDiscription(){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println(" Description of counters");
		out.println(StringUtility.fillChar("",60, '-'));
		out.println(" Total-Cache-Count    : Total number of account data in cache");
		out.println(" Total-Cache-Request  : Total request for cache");
		out.println(" Total-Cache-Hit      : Account data found from cache ");
		out.println(" Total-Cache-Miss     : Account data not found from cache");
		out.println(" Total-Load-Count     : Total number of call to database for fetching account data");
		out.println(" Average-Load-Penalty : Average of total cache loading time(ms)");
		out.println(" Total-Eviction-Count : Total number of time cache evicted");
		out.close();
		return writer.toString();
	}

	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println(" Description: Display SPR cache statistics");
		for(String provider : detailProviderMap.keySet()){
			out.println("    " +provider);
		}
		out.println();
		out.close();
		return writer.toString();
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("'"+KEY+"':{'"+HELP+"':{}");
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
	public String getDescription(){
		return "Display SPR cache statistics";	
	}
}
