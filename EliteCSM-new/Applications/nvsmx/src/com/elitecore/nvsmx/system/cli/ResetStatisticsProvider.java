package com.elitecore.nvsmx.system.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;

public class ResetStatisticsProvider extends DetailProvider {

	private static final String KEY = "ws";
	private static final String STATISTICS = "statistics";
	private HashMap<String, DetailProvider> detailProviderMap;
	private static ResetStatisticsProvider statisticsDetailProvider;
	
	static {
		statisticsDetailProvider = new ResetStatisticsProvider();
	}

	
	private ResetStatisticsProvider(){
		this.detailProviderMap = new HashMap<String, DetailProvider>(1);
	}

	public static ResetStatisticsProvider getInstance(){
		return statisticsDetailProvider;
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length >0  ){

			if (isHelpSymbol(parameters[0])) {
				return getHelpMsg();
			}
			return resetStatistics(parameters[0]);
			
		}
		return getHelpMsg();
	}

	
	private String resetStatistics(String value) {
		 
		if(STATISTICS.equalsIgnoreCase(value)){
			String response = WebServiceStatisticsManager.getInstance().resetAllStatistics();
			if(response!=null && response.equalsIgnoreCase("success")){
				return "WebService statistics Reset successfully";
			}else{
				return "Failed to reset WebService statistics";
			}
		}else{
			return "Invalid option provided." + getHelpMsg();
		}
	 
}
	
	@Override
	public String getHelpMsg() {
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" "+KEY+" <option>");
		out.println(" Description : Reset statistics of all WebServices");
		out.println(" Possible Options : ");
		out.println();
		out.println(" " + STATISTICS + " ");
		out.close();
		return stringWriter.toString();
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("'" + KEY + "':{'" + HELP_OPTION + "':{},'" + STATISTICS + "':{}");
		
		for (DetailProvider provider : detailProviderMap.values()) {
			out.print("," + provider.getHotkeyHelp());
		}
		
		out.print("}");
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
}
