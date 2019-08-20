package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.service.notification.NotificationServiceStatisticsProvider;

public class NotificationServiceSMSStatisticsDetailProvider extends DetailProvider {
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private NotificationServiceStatisticsProvider notificationServiceStatisticsProvider;
	
	public NotificationServiceSMSStatisticsDetailProvider(NotificationServiceStatisticsProvider notificationServiceStatisticsProvider) {
		this.detailProviderMap = new HashMap<String, DetailProvider>(1);
		this.notificationServiceStatisticsProvider = notificationServiceStatisticsProvider;
		
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters.length > 0){
			if(isHelpSymbol(parameters[0])){
				return getHelpMsg();
			}

			if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
			}
		}
		
		return getNotificationerviceStatistics();
	}

	private String getNotificationerviceStatistics() {
		
		String[] dataRecord3 = new String[6];
		dataRecord3[0] = "SMS-Proce           :";                                                                          
		dataRecord3[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSProcessed()); 
		dataRecord3[2] = "SMS-Sent           :";                                                                          
		dataRecord3[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSSuccess());                     
		dataRecord3[4] = "SMS-Failure           :";                                                                          
		dataRecord3[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSFailures());
		
		// ==== Today 
		String[] dataRecord6 = new String[6];
		dataRecord6[0] = "SMS-Proce-Today     :";                                                                          
		dataRecord6[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSProcessedToday()); 
		dataRecord6[2] = "SMS-Sent-Today     :";                                                                          
		dataRecord6[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSSuccessToday());                     
		dataRecord6[4] = "SMS-Failure-Today     :";                                                                          
		dataRecord6[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSFailuresToday());
		
		// yesterday
		String[] dataRecord9 = new String[6];
		dataRecord9[0] = "SMS-Proce-Yesterday :";                                                                          
		dataRecord9[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSProcessedYesterday()); 
		dataRecord9[2] = "SMS-Sent-Yesterday :";                                                                          
		dataRecord9[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSSuccessYesterday());                     
		dataRecord9[4] = "SMS-Failure-Yesterday :";                                                                          
		dataRecord9[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSFailuresYesterday());
		
		int column0 = findMaxLength(dataRecord3[0].length() , dataRecord6[0].length(),
					dataRecord9[0].length());

		int column1 = findMaxLength(dataRecord3[1].length() , dataRecord6[1].length(),
				dataRecord9[1].length());
		
		int column2 = findMaxLength(dataRecord3[2].length() ,
				 dataRecord6[2].length(), dataRecord9[2].length());
		
		int column3 = findMaxLength(dataRecord3[3].length() , dataRecord6[3].length(),
				dataRecord9[3].length());
		
		int column4 = findMaxLength(dataRecord3[4].length() , dataRecord6[4].length(),
				dataRecord9[4].length());
		
		int column5= findMaxLength(dataRecord3[5].length() ,dataRecord6[5].length(),
				dataRecord9[5].length());
		
		String[] header = {"","","","","",""};
		int[] width = {column0,column1,column2,column3,column4,column5};
		int[] column_alignment = {TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT};
		TableFormatter formatter = new TableFormatter(header, width, column_alignment ,TableFormatter.NO_BORDERS);
		
		formatter.addRecord(dataRecord3);
		formatter.addNewLine();
		
		formatter.addRecord(dataRecord6);
		formatter.addNewLine();
		formatter.addRecord(dataRecord9);
		formatter.addNewLine();
		return formatter.getFormattedValues();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("  Description: Display SMS Notification Statistics");
		out.println();
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "sms";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}
