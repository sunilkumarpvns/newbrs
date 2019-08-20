package com.elitecore.core.util.cli.cmd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public class DataSourceCommand extends EliteBaseCommand {

	private static final String COMMAND = "datasource";
	private static final String DESCRIPTION = "returns the status of all the datasources configured";
	private LinkedHashMap<String, DetailProvider> detailProviderMap = new LinkedHashMap<String, DetailProvider>();

	public String execute(String parameter) {
		if (parameter == null || parameter.isEmpty() || HELP.equalsIgnoreCase(parameter) || HELP_OPTION.equalsIgnoreCase(parameter)) {
			return getHelpMsg();
		}

		String[] commandParameter = parameter.split("\\s");
		if (detailProviderMap.containsKey(commandParameter[0].toLowerCase()) == false) {
			return "Invalid Arguments\n" + getHelpMsg();
		}

		String key = commandParameter[0].toLowerCase();
		if (commandParameter.length == 1) {
			return detailProviderMap.get(key).execute(null);
		} else {
			return detailProviderMap.get(key).execute(Arrays.copyOfRange(commandParameter, 1, commandParameter.length));
		}
		
	}
	@Override
	public String getCommandName() {
		return COMMAND;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	public void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException {
		if (detailprovider.getKey() == null) {
			throw new RegistrationFailedException( "Failed to register detail provider. Reason: key is not specified.");
		}

		if (detailProviderMap.containsKey(detailprovider.getKey())) {
			throw new RegistrationFailedException( "Failed to register detail provider. Reason: Show Command already contains detail provider with Key : "
							+ detailprovider.getKey());
		}

		detailProviderMap.put(detailprovider.getKey().toLowerCase(), detailprovider);
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("{'" + getCommandName() + "':{'" + HELP_OPTION + "':{}");
		for (DetailProvider detailProvider : detailProviderMap.values()) {
			out.print(",");
			out.print(detailProvider.getHotkeyHelp());
		}
		out.print("}}");
		out.close();
		return writer.toString();
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + "[<options>]");
		out.println("Description : " + getDescription());
		out.println("Possible options:\n");
		for (DetailProvider detailProvider : detailProviderMap.values()) {
			out.println(fillChar("\t" + detailProvider.getKey(), 35) + "-" + detailProvider.getDescription());
		}
		out.close();
		return stringWriter.toString();
	}
}
