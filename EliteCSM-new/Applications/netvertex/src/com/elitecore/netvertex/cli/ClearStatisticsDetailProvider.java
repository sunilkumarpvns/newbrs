package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClearStatisticsDetailProvider extends DetailProvider{

	private static final String STATISTICS = "statistics";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static final String HELP = "-help";
	private static ClearStatisticsDetailProvider clearStatisticsDetailProvider;
	private ClearStatisticsDetailProvider(){
		detailProviderMap = new HashMap<String, DetailProvider>();
	}
	
	static {
		clearStatisticsDetailProvider = new ClearStatisticsDetailProvider();
	}
	
	public static ClearStatisticsDetailProvider getInstance() {
		
		return clearStatisticsDetailProvider;
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
		out.println(" Usage : " + STATISTICS + " <option>");
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
		return STATISTICS;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getDescription() {
		return "Clears statistics based on given parameter";
	}
}
