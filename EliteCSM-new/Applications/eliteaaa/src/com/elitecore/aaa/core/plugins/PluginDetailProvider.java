package com.elitecore.aaa.core.plugins;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;

public class PluginDetailProvider extends DetailProvider {

	private static PluginDetailProvider instance;
	
	private HashMap<String, DetailProvider> detailProviders = new HashMap<String, DetailProvider>();

	static {
		instance = new PluginDetailProvider();
	}
	
	public static PluginDetailProvider getInstance() {
		return instance;
	}	
	
	@Override
	public String execute(String[] parameters) {
		if (parameters.length == 0) {
			return getHelpMsg();
		}
		
		if ("?".equals(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)) {
			return getHelpMsg();
		}
		
		if (detailProviders.containsKey(parameters[0])) {
			return detailProviders.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
		}
		
		return " Invalid Option: " + parameters[0] + getHelpMsg();
	}
	
	@Override
	public String getDescription() {
		return "Shows details of plugins";
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" Description : " + getDescription());
		out.println(" Usage : " + "plugin" + " <option>");
		out.println(" Possible Options : ");
		out.println();
		for (String provider : detailProviders.keySet()) {
			out.println(" 	" + provider + " : "
					+ detailProviders.get(provider).getDescription());
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return "plugin";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviders;
	}

}
