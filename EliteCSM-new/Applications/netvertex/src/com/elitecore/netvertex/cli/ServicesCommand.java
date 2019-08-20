package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.netvertex.cli.data.ServiceDataProvider;

public class ServicesCommand extends EliteBaseCommand {
	
	private ServiceDataProvider dataProvider;
	private SimpleDateFormat sdf = null;
	
	private final static int SERVICE_NAME_WIDTH=20;
	private final static int START_DATE_WIDTH=25;
	private final static int STATUS_WIDTH=12;
	private final static int REMARKS_WIDTH=35;
	
	int[] width= {SERVICE_NAME_WIDTH
			,START_DATE_WIDTH
			,STATUS_WIDTH
			,REMARKS_WIDTH};
	
	String[] header={"Service Name"
			,"Start Date"
			,"Status"
			,"Remarks"};
	
	public ServicesCommand(ServiceDataProvider dataProvider) {
		this.dataProvider = dataProvider;
		this.sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	}
	
	public String execute(String parameter) {
		
		TableFormatter formatter = null;
		if (parameter == null || parameter.trim().length() == 0) {
			formatter=new TableFormatter(header, width, TableFormatter.OUTER_BORDER);
			return getServiceSummary(formatter);
		}
		
		parameter = parameter.trim();
		if(CSV.equalsIgnoreCase(parameter)){
			formatter=new TableFormatter(header, width, 
					TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
			return getServiceSummary(formatter);
		}
		return getHelp();
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

	private String getServiceSummary(TableFormatter formatter){
		String[] data=new String[width.length];
		if (dataProvider.getServiceDescriptionList().size() > 0) {
			for (ServiceDescription baseService : dataProvider.getServiceDescriptionList()) {

				data[0] = baseService.getName();
				data[1] = baseService.getStartDate() == null ? " --- " : sdf.format(baseService.getStartDate());
				data[2] = baseService.getStatus();
				data[3] = baseService.getRemarks() == null ? " --- " : baseService.getRemarks();

				formatter.addRecord(data);
			}
		} else {
			formatter.add("No Services are initialized.", TableFormatter.CENTER);
		}
		return formatter.getFormattedValues();
	}

	
	public String getCommandName() {
		return "services";
	}

	public String getDescription() {
		return "Displays current status of configured services.";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'services':{'"+CSV+"':{}}}";
	}
}
