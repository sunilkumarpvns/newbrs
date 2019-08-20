package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.serverx.alert.IAlertData;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.alerts.NetVertexAlertManager;

public class AlertStatisticsDetailProvider extends DetailProvider{

	private static final String ALERT = "alert";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static final String[] headerShow = {"Alert Name", "Total", "Today", "Current Week", "Current Month"};
	private static final int[] width = {33, 19, 19, 19, 19};
	private static final int[] alignment = {TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.LEFT};
	private final NetVertexAlertManager alertManager;
	
	public AlertStatisticsDetailProvider(NetVertexAlertManager alertManager){
		
		this.alertManager = alertManager;
		detailProviderMap = new HashMap<String, DetailProvider>();
	}
	
	@Override
	public String execute(String[] parameters) {

		if (parameters.length == 0) {
			
			if (checkIfAnyAlertGenerated() == false) {
				return "\nNo alert(s) generated";
			} else {
				return getAlertStatistics();
			}
		}
		
		if (isHelpSymbol(parameters[0])) {
			return getHelpMsg();
		}
		
		if (detailProviderMap.containsKey(parameters[0])) {
			return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
		}
		
		return " Invalid Option: " + parameters[0] + getHelpMsg();
	}

	private boolean checkIfAnyAlertGenerated() {
		
		for (Alerts alert : Alerts.values()) {

			IAlertData alertData = alertManager.getAlertData(alert.id());
			if (alertData.getStatistics().getTotalCounter() > 0) {
				return true;
			}
		}
		return false;
	}

	private String getAlertStatistics() {

		TableFormatter formatter = new TableFormatter(headerShow, width, alignment, TableFormatter.OUTER_BORDER);
		for (Alerts alert : Alerts.values()) {

			IAlertData alertData = alertManager.getAlertData(alert.id());
			if (alertData != null && Strings.isNullOrBlank(alert.id()) == false && (alertData.getStatistics().getTotalCounter() > 0)) {
				formatter.addRecord(new String[] { alert.getDisplayName(),
								String.valueOf(alertData.getStatistics().getTotalCounter()),
								String.valueOf(alertData.getStatistics().getDailyCounter()),
								String.valueOf(alertData.getStatistics().getWeeklyCounter()),
								String.valueOf(alertData.getStatistics().getMonthlyCounter()) });
			}
		}

		return formatter.getFormattedValues();

	}
	@Override
	public String getHelpMsg() {

		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" Description : " + getDescription());
		out.println(" Usage : " + ALERT + " <option>");
		if (detailProviderMap.size() > 0) {
    		out.println(" Possible Options : ");
    		out.println();
    		for (Map.Entry<String, DetailProvider> entry : detailProviderMap.entrySet()) {
    			out.println(" 	" + entry.getKey() + " : "
    					+ entry.getValue().getDescription());
    		}
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getKey() {
		return ALERT;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getDescription() {
		return "Displays alert statistics based on daily, weekly, monthly time periods.";
	}
}
