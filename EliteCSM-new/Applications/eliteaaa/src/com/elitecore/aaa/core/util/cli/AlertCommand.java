package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.alert.IAlertData;
import com.elitecore.core.util.cli.TableFormatter;

public abstract class AlertCommand extends EliteBaseCommand{
	
	private static final String COUNTER_SHORTHAND = "c";
	private static final String COUNTERS = "counters";
	private String[] header = {"Alert Name", "Number Of Listener Configured", "Number Of Alerts Generated"};
	private int[] width = {20, 29, 26};
	private int[] alignment = {TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT};
	
	@Override
	public String execute(String parameter) {
		if(parameter != null && parameter.trim().length() > 0 ){
			String [] params = parameter.split(" ");
			if(COUNTER_SHORTHAND.equalsIgnoreCase(params[0]) || COUNTERS.equalsIgnoreCase(params[0])){
				if(params.length == 2 && CSV.equalsIgnoreCase(params[1])){
					TableFormatter formatter = new TableFormatter(header, width, 
							TableFormatter.CSV,TableFormatter.COLUMN_SEPARATOR_COMMA);
					return alertSummary(formatter);
				}
				TableFormatter formatter = new TableFormatter(header, width, alignment, TableFormatter.OUTER_BORDER);
				return alertSummary(formatter);
			}
		}
		return getHelpMessege();
	}

	private String alertSummary(TableFormatter formatter) {
		Alerts alerts[] = Alerts.VALUES;
		int size = alerts.length;
		IAlertData alertData;
		for(int cnt = 0; cnt < size; cnt++) {
			alertData = getSystemAlertData(alerts[cnt].alertId);
			if(alertData != null && alertData.getAlertId() != null && alertData.getAlertId().trim().length() > 0){
				formatter.addRecord(new String[]{alerts[cnt].getDisplayName(), 
						String.valueOf(alertData.getAlertProcessorsList().size()), String.valueOf(alertData.getStatistics().getTotalCounter())});
			}
		}
		return formatter.getFormattedValues();
	}
	@Override
	public String getCommandName() {
		return "alert";
	}

	@Override
	public String getDescription() {
		return "Display alert information";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'alert':{'"+COUNTER_SHORTHAND+"':{'"+CSV+"':{}},'"+COUNTERS+"':{'"+CSV+"':{}}}}";
	}
	private String getHelpMessege(){
		StringWriter writer = new StringWriter();
		PrintWriter buffer = new PrintWriter(writer);
		
		buffer.println("Usage: " + getCommandName() + " <option>");
		buffer.println();
		buffer.println("Possible options");
		buffer.append(fillChar("{ " +COUNTER_SHORTHAND+ " | "+COUNTERS+"}", 30));
		buffer.append(": "+ getDescription());
		buffer.println();
		buffer.append(fillChar("{ " +COUNTER_SHORTHAND+ " | "+COUNTERS+"} "+ CSV, 30));
		buffer.append(": "+ getDescription() +" in CSV Format");
		buffer.println();
		buffer.close();
		return writer.toString();
	}
	public abstract IAlertData getSystemAlertData(String alertId) ;
	

}
