package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;

public class StatisticsDetailProvider extends DetailProvider {

	private static final String STATISTICS = "statistics";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static StatisticsDetailProvider statisticsDetailProvider;
	
	static {
		statisticsDetailProvider = new StatisticsDetailProvider();
	}
	
	private StatisticsDetailProvider(){
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	public static StatisticsDetailProvider getInstance(){
		return statisticsDetailProvider;
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length >0  ){

			if("?".equals(parameters[0])){
				return getHelpMsg();
			}

			if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			}
		}
		return getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("   "+STATISTICS+" <option>");
		out.println(" Description : Display statistics base on given parameter");
		out.println(" Possible Options : ");
		out.println();
		for(String provider : detailProviderMap.keySet()){
			out.println(" " +provider);
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return "statistics";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}
