package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.data.ServiceDataProvider;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.util.cli.TableFormatter;

public class ServicesCommand extends EliteBaseCommand {

	private ServiceDataProvider dataProvider;
	private SimpleDateFormat sdf = null;
	private AAAServerContext serverContext;
	private final String[] header = { "Service Name", "Service Address",
			"Start Date", "Status", "Remarks" };
	private final int[] alignment = new int[] { TableFormatter.LEFT,
			TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.LEFT,
			TableFormatter.LEFT };

	public ServicesCommand(ServiceDataProvider dataProvider,
			ServerContext serverContext) {
		this.dataProvider = dataProvider;
		this.sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		this.serverContext = (AAAServerContext) serverContext;

	}

	public String execute(String parameter) {

		TableFormatter formatter = null;
		if (parameter == null || parameter.trim().length() == 0) {
			formatter = new TableFormatter(header, new int[]{ 20, 25, 25, 20, 25 }, 
					alignment, TableFormatter.OUTER_BORDER);
			return getServiceSummary(formatter);
		}
		if (CSV.equalsIgnoreCase(parameter.trim())) {
			formatter = new TableFormatter(header, new int[]{ 20, 25, 25, 20, 200 },
					TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
			return getServiceSummary(formatter);
		}
		return getHelp();
	}

	private String getServiceSummary(TableFormatter formatter) {
		if (formatter != null) {
			String[] rowData = new String[5];
			for (ServiceDescription baseService : dataProvider.getServiceDataList()) {
				rowData[0] = baseService.getName();
				rowData[1] = baseService.getSocketAddress() == null ? "---" 
						: baseService.getSocketAddress();
				rowData[2] = baseService.getStartDate() == null ? " --- " : sdf
						.format(baseService.getStartDate());
				rowData[3] = baseService.getStatus();
				rowData[4] = baseService.getRemarks() == null ? " --- "
						: baseService.getRemarks();
				formatter.addRecord(rowData, alignment);
			}

			if (serverContext.getConfigurationState() == AAAConfigurationState.UNRECOVERABLE) {
				formatter.add("No services started as configuration is improper and last good configuration not found or improper",
								TableFormatter.LEFT);
			}
			if (serverContext.getConfigurationState() == AAAConfigurationState.FALLBACK_CONFIGURATION) {
				formatter.add("Note : ** represents service started with last successful configuration , verify your current configuration or contact system administrator.",
								TableFormatter.LEFT);
			}
			return formatter.getFormattedValues();
		}
		return "";
	}

	public String getCommandName() {
		return "services";
	}

	public String getDescription() {
		return "Displays current status of configured services";
	}

	private String getHelp() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " [<option>]");
		out.println("Description : " + getDescription());
		out.println();
		out.println("----------------------------:Possible Options:----------------------------");
		out.println(CSV + "		: " + getDescription() + " in CSV format");
		out.println();
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "{'services':{'-help':{},'" + CSV + "':{}}}";
	}
}
