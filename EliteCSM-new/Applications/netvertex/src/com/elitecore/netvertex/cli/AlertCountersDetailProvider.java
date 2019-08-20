package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.serverx.alert.IAlertData;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.core.alerts.Alerts;

/**
 *
 * This class provides counter details to Alert command
 * @author Jay Trivedi
 * 
 *
 */
public abstract class AlertCountersDetailProvider extends DetailProvider{

	private static final String COUNTERS = "counters";
	private static final String SHOW = "-show";
	
	private HashMap<String, DetailProvider> detailProviderMap;
	private static final String[] headerShow = {"Alert Name", "No. of Listeners", "No. of Alerts      Generated"};
	private static final String[] headerCSV = {"Alert Name", "No. of Listeners", "No. of Alerts Generated"};
	private static final int[] width = {33, 19, 19};
	private static final int[] alignment = {TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.LEFT};
	
	public AlertCountersDetailProvider() {
		detailProviderMap = new HashMap<String, DetailProvider>();
	}
	@Override
	public String execute(String[] parameters) {

		if (parameters.length == 0 || isHelpSymbol(parameters[0])) {
			return getHelpMsg();
		}

		if (CSV.equalsIgnoreCase(parameters[0])) {
			
			if (checkIfAnyAlertGenerated() == false) {
				return "\nNo alert(s) generated";
			}
			
			TableFormatter formatter = new TableFormatter(headerCSV, width, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
			return alertSummary(formatter);
		}
		
		if (SHOW.equalsIgnoreCase(parameters[0])) {
			
			if (checkIfAnyAlertGenerated() == false) {
				return "\nNo alert(s) generated";
			}
			
			TableFormatter formatter = new TableFormatter(headerShow, width, alignment, TableFormatter.OUTER_BORDER);
			return alertSummary(formatter);
		}
		
		if (detailProviderMap.containsKey(parameters[0])) {
			return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
		}
		
		return "\n Invalid Option: " + parameters[0] + getHelpMsg();
	}

	private boolean checkIfAnyAlertGenerated() {
		
		for (Alerts alert : Alerts.values()) {

			IAlertData alertData = getSystemAlertData(alert.id());
			if (alertData.getStatistics().getTotalCounter() > 0) {
				return true;
			}
		}
		return false;
	}
	
	private String alertSummary(TableFormatter formatter) {

		for (Alerts alert : Alerts.values()) {

			IAlertData alertData = getSystemAlertData(alert.id());
			if (alertData != null && Strings.isNullOrBlank(alert.id()) == false && (alertData.getStatistics().getTotalCounter() > 0)) {
				formatter.addRecord(new String[] { alert.getDisplayName(),
						String.valueOf(alertData.getAlertProcessorsList().size()), String.valueOf(alertData.getStatistics().getTotalCounter()) });
			}
		}

		return formatter.getFormattedValues();
	}
	
	@Override
	public String getHelpMsg() {

		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);
		out.println();
		out.println(" Description : " + getDescription());
		out.println(" Usage : " + COUNTERS + " <option>");
		out.println(" Possible Options : ");
		out.println();
		out.incrementIndentation();
		out.println(SHOW + " : Displays alert counters");
		out.println(CSV + "  : Displays alert counters in csv format");
		if (detailProviderMap.size() > 0) {
			out.println();
			for (Map.Entry<String, DetailProvider> entry : detailProviderMap.entrySet()) {
				out.println(" 	" + entry.getKey() + " : "
						+ entry.getValue().getDescription());
			}
		}
		out.decrementIndentation();
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer =new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		printWriter.print("'" + getKey() +"':{");
		printWriter.print("'" + SHOW + "':{},'" + CSV + "':{},'" + HELP_OPTION + "':{}");
		int size = getDetailProviderMap().size();
		int i =1;
		for(DetailProvider provider : getDetailProviderMap().values()){
			if(i != size){
				printWriter.print(provider.getHotkeyHelp() + ",");
			}else{
				printWriter.print(provider.getHotkeyHelp());
			}
			i++;
		}
		printWriter.print("}");
		return writer.toString();	
	}

	@Override
	public String getDescription() {
		return "Displays counters of generated alerts.";
	}
	
	@Override
	public String getKey() {
		return COUNTERS;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		
		return detailProviderMap;
	}

	public abstract IAlertData getSystemAlertData(String alertId) ;
}
