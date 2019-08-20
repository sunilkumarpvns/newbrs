package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.service.notification.NotificationServiceStatisticsProvider;

public class NotificationServiceEmailStatisticsDetailProvider extends DetailProvider {
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private NotificationServiceStatisticsProvider notificationServiceStatisticsProvider;
	
	public NotificationServiceEmailStatisticsDetailProvider(NotificationServiceStatisticsProvider notificationServiceStatisticsProvider) {
		this.detailProviderMap = new HashMap<String, DetailProvider>(1);
		this.notificationServiceStatisticsProvider = notificationServiceStatisticsProvider;
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length > 0){
			if(isHelpSymbol(parameters[0])){
				return getHelpMsg();
			}
		}
		
		return getNotificationerviceStatistics();
	}

	private String getNotificationerviceStatistics() {
		
		String[] dataRecord2 = new String[6];
		dataRecord2[0] = "Email-Proce           :";
		dataRecord2[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailProcessed());
		dataRecord2[2] = "Email-Sent           :";
		dataRecord2[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailSuccess());
		dataRecord2[4] = "Email-Failure           :";
		dataRecord2[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailFailures());
		
		// ==== Today 
		String[] dataRecord5 = new String[6];
		dataRecord5[0] = "Email-Proce-Today     :";
		dataRecord5[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailProcessedToday());
		dataRecord5[2] = "Email-Sent-Today     :";
		dataRecord5[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailSuccessToday());
		dataRecord5[4] = "Email-Failure-Today     :";
		dataRecord5[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailFailuresToday());
		
		// yesterday
		String[] dataRecord8 = new String[6];
		dataRecord8[0] = "Email-Proce-Yesterday :";
		dataRecord8[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailProcessedYesterday());
		dataRecord8[2] = "Email-Sent-Yesterday :";
		dataRecord8[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailSuccessYesterday());
		dataRecord8[4] = "Email-Failure-Yesterday :";
		dataRecord8[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailFailuresYesterday());
		
		int column0 = findMaxLength(dataRecord2[0].length(), dataRecord5[0].length() ,
					dataRecord8[0].length());

		int column1 = findMaxLength(dataRecord2[1].length() , dataRecord5[1].length() ,
				 dataRecord8[1].length());
		
		int column2 = findMaxLength(dataRecord2[2].length() ,dataRecord5[2].length() ,
				dataRecord8[2].length());
		
		int column3 = findMaxLength(dataRecord2[3].length(), dataRecord5[3].length() ,
				dataRecord8[3].length());
		
		int column4 = findMaxLength(dataRecord2[4].length() , dataRecord5[4].length() ,
				dataRecord8[4].length());
		
		int column5= findMaxLength(dataRecord2[5].length() , dataRecord5[5].length() ,
				dataRecord8[5].length());
		
		String[] header = {"","","","","",""};
		int[] width = {column0,column1,column2,column3,column4,column5};
		int[] column_alignment = {TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT};
		TableFormatter formatter = new TableFormatter(header, width, column_alignment ,TableFormatter.NO_BORDERS);
		
		formatter.addRecord(dataRecord2);
		formatter.addNewLine();
		formatter.addRecord(dataRecord5);
		formatter.addNewLine();
		formatter.addRecord(dataRecord8);
		formatter.addNewLine();
		return formatter.getFormattedValues();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("  Description: Display Email Notification Statistics");
		out.println();
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "email";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}
