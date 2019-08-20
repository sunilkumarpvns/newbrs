package com.elitecore.aaa.core.util.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.BaseESIScanCommand;

public class ESIScanCommand extends BaseESIScanCommand {
	public static final String UDP = "-udp_comm";
	private static final int[] alignment = { TableFormatter.LEFT,
			TableFormatter.LEFT, TableFormatter.LEFT };
	private static final int[] width = { 40, 35, 5 };
	private static final String[] header = { "NAME", "TYPE", "STATE" };
	
	public ESIScanCommand() {
		fillMapWithDriverTypes();
	}
	
	@Override
	public String execute(String parameter) {

		if (parameter == null) {
			return getHelp();
		}
		
		if (isHelpParameter(parameter)) {
			return getHelp();
		}
		
		if (parameter.trim().length() == 0 || parameter.equalsIgnoreCase("-all")) {
			TableFormatter formatter = new TableFormatter(header, width, alignment, TableFormatter.OUTER_BORDER);
			return getAllESInfo(formatter);
		}
		
		StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
		if (tokenizer.hasMoreTokens()) {
			parameter = tokenizer.nextToken();
		}
		
		String commandParameters = null;
		
		if (tokenizer.hasMoreTokens()) {
			commandParameters = tokenizer.nextToken();
		}
		
		if (CSV.equalsIgnoreCase(parameter) || 
				("-all".equalsIgnoreCase(parameter) && CSV.equalsIgnoreCase(commandParameters))) {
			TableFormatter formatter = new TableFormatter(header, width, 
					TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
			return getAllESInfo(formatter);
		}
		
		if (commandParameters != null && CSV.equalsIgnoreCase(commandParameters)) {
			commandParameters = null;
			TableFormatter formatter = new TableFormatter(header, width, 
					TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
			return getESInfo(parameter, commandParameters, formatter);
		}
		
		TableFormatter formatter = new TableFormatter(header, width, alignment, TableFormatter.OUTER_BORDER);
		return getESInfo(parameter, commandParameters, formatter);
	} 

	private String getAllESInfo(TableFormatter formatter) {
		for (Map<String, ESIStatus> esType : esiStatusMap.values()) {
			for (ESIStatus esStatus : esType.values()) {
				formatter.addRecord(new String[] { esStatus.getESName(),
								esStatus.getConfiguredESType(),
								esStatus.getESStatus() });
			}
		}
		
		return formatter.getFormattedValues() + getLegend();
	}
	
	private String getESInfo(String parameter, String commandParameters, TableFormatter formatter) {
		if (commandParameters != null) {
			if (esiStatusMap.get(parameter) != null) {
				ESIStatus esiStatus= esiStatusMap.get(parameter).get(commandParameters);
				if (esiStatus != null) {
					formatter.addRecord(new String[] { esiStatus.getESName(),
							esiStatus.getConfiguredESType(),
							esiStatus.getESStatus() });
				} else {
					return "ESI :" + commandParameters + "NOT FOUND";
				}
			} else {
				return "INVALID PARAMRTER: " + parameter + "\n" + getHelp();
			}
		} else {
			Map<String, ESIStatus> tmpMap = esiStatusMap.get(parameter);
			for (ESIStatus esiStatus: tmpMap.values()) {
				formatter.addRecord(new String[] { esiStatus.getESName(),
						esiStatus.getConfiguredESType(),
						esiStatus.getESStatus() });
			}
		}
		
		return formatter.getFormattedValues() + getLegend();
	}
	
	private String getLegend() {
		return new StringBuilder()
				.append(CommonConstants.LINE_SEPARATOR)
				.append("FAIL - The driver creation failed (permanent failure)")
				.append(CommonConstants.LINE_SEPARATOR).toString();
	}
	
	@Override
	public String getCommandName() {
		return "esiscan";
	}

	@Override
	public String getDescription() {
		return "Displays the details of the External Systems.";
	}

	@Override
	public String getHotkeyHelp() {
		String[] tmpDriverType = new String[DriverTypes.values().length];
		int i = 0;
		for (DriverTypes drvrType : DriverTypes.values()) {
			tmpDriverType[i] = getHotkeyPattern(buildParameterString(drvrType.name()));
			i++;
		}
		return "{'esiscan':{'" +CSV + "':{}," + StringUtility.getCommaSeparated(tmpDriverType) 
				+ ",'-udp_comm':{'" + CSV + "':{}},'-all':{'" + CSV + "':{}}}}";
	}

	private String getHotkeyPattern(String drvrType) {
		return "'" + drvrType + "':{'" + CSV + "':{}}";
	}
	
	private String getHelp(){
		StringBuilder builder = new StringBuilder();
		builder.append("Usage : " + getCommandName() + "[<options>]\n");
		builder.append("Description : " + getDescription() + "\n");
		builder.append(fillChar("\t-all", 40) + "\n");
		builder.append(fillChar("\t" + CSV, 40) + "\n");
		builder.append(fillChar("\t-udp_comm", 40) + "\n");
		for (DriverTypes drvrType: DriverTypes.values()) {
			builder.append(fillChar(
					"\t" + fillChar(buildParameterString(drvrType.name()), 30)
							+ " [" + CSV + ",<name>] ", 43));
			builder.append(": Shows details of " + drvrType.name() + "\n");
		}
		return builder.toString();
	}

	private void fillMapWithDriverTypes() {
		DriverTypes[] drvrTypes = DriverTypes.values();
		for (DriverTypes drvrType:drvrTypes) {
			esiStatusMap.put(buildParameterString(drvrType.name()),
					new HashMap<String, ESIStatus>());
		}
		esiStatusMap.put(UDP, new HashMap<String, ESIStatus>());
	}
}
