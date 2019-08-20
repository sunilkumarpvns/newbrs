package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClearCacheStatisticsDetailProvider extends DetailProvider{

	private static final String CACHE = "cache";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static final String HELP = "-help";
	private static ClearCacheStatisticsDetailProvider clearCacheStatisticsDetailProvider;
	private ClearCacheStatisticsDetailProvider(){
		detailProviderMap = new HashMap<String, DetailProvider>();
	}
	
	static {
		clearCacheStatisticsDetailProvider = new ClearCacheStatisticsDetailProvider();
	}
	
	public static ClearCacheStatisticsDetailProvider getInstance() {
		
		return clearCacheStatisticsDetailProvider;
	}
	@Override
	public String execute(String[] parameters) {

		if (parameters.length == 0) {
			return getHelpMsg();
		}
		
		if ("?".equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)) {
			return getHelpMsg();
		}
		
		if (detailProviderMap.containsKey(parameters[0])) {
			return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
		}
		
		return " Invalid Option: " + parameters[0] + getHelpMsg();
	}

	@Override
	public String getHelpMsg() {

		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" Description : " + getDescription());
		out.println(" Usage : " + CACHE + " <option>");
		out.println(" Possible Options : ");
		out.println();
		for (Map.Entry<String, DetailProvider> provider : detailProviderMap.entrySet()) {
			out.println(" 	" + provider.getKey() + " : "
					+ provider.getValue());
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return CACHE;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getDescription() {
		return "Clears cache statistics based on given parameter";
	}
}
