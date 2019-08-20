package com.elitecore.aaa.core.util.cli;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;

public class ClearKeysCommand extends DetailProvider {

	private static final String KEY = "keys";
	private HashMap<String, DetailProvider> detailProviderMap;

	public ClearKeysCommand() {
		detailProviderMap = new HashMap<String, DetailProvider>();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter writer = new StringWriter();
		IndentingPrintWriter out = new IndentingPrintWriter(writer);
		out.println();

		out.println(StringUtility.fillChar(" Description", 12) + " : " +  getDescription());
		out.println(StringUtility.fillChar(" Usage", 12) + " : " + KEY);

		if (detailProviderMap.isEmpty()) {
			out.close();
			return writer.toString();
		}

		out.println(" Possible Options:");
		out.println();
		out.incrementIndentation();
		for (String provider : detailProviderMap.keySet()) {
			out.println(provider + " : " + detailProviderMap.get(provider).getDescription());
		}
		out.decrementIndentation();
		out.close();
		return writer.toString();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return this.detailProviderMap;
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters.length == 0 || isHelpSymbol(parameters[0])) {
			return getHelpMsg();
		}
		
		if (detailProviderMap.containsKey(parameters[0])) {
			return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters, 1, parameters.length));
		}

		return " Invalid Option: " + parameters[0] + getHelpMsg();
	}

	@Override
	public String getDescription() {
		return "clears keys created in system";
	}
}
