package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.netvertex.service.notification.NotificationServiceStatisticsProvider;

public class NotificationServiceAllStatisticsDetailProvider extends DetailProvider {
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private NotificationServiceStatisticsProvider notificationServiceStatisticsProvider;
	
	public NotificationServiceAllStatisticsDetailProvider(NotificationServiceStatisticsProvider notificationServiceStatisticsProvider) {
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
		
		String[] dataRecord1 = new String[6];
		dataRecord1[0] = "Notification-Proce           :";
		dataRecord1[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalNotificationProcessed());
		dataRecord1[2] = "";
		dataRecord1[3] = "";
		dataRecord1[4] = "";
		dataRecord1[5] = "";
		
		String[] dataRecord2 = new String[6];
		dataRecord2[0] = "Email-Proce                  :";
		dataRecord2[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailProcessed());
		dataRecord2[2] = "Email-Sent           :";
		dataRecord2[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailSuccess());
		dataRecord2[4] = "Email-Failure           :";
		dataRecord2[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailFailures());
		
		String[] dataRecord3 = new String[6];
		dataRecord3[0] = "SMS-Proce                    :";                                                                          
		dataRecord3[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSProcessed()); 
		dataRecord3[2] = "SMS-Sent             :";                                                                          
		dataRecord3[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSSuccess());                     
		dataRecord3[4] = "SMS-Failure             :";                                                                          
		dataRecord3[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSFailures());
		
		// ==== Today 
		
		String[] dataRecord4 = new String[6];
		dataRecord4[0] = "Notification-Proce-Today     :";
		dataRecord4[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalNotificationProcessedToday());
		dataRecord4[2] = "";
		dataRecord4[3] = "";
		dataRecord4[4] = "";
		dataRecord4[5] = "";
		
		String[] dataRecord5 = new String[6];
		dataRecord5[0] = "Email-Proce-Today            :";
		dataRecord5[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailProcessedToday());
		dataRecord5[2] = "Email-Sent-Today     :";
		dataRecord5[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailSuccessToday());
		dataRecord5[4] = "Email-Failure-Today     :";
		dataRecord5[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailFailuresToday());
		
		String[] dataRecord6 = new String[6];
		dataRecord6[0] = "SMS-Proce-Today              :";                                                                          
		dataRecord6[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSProcessedToday()); 
		dataRecord6[2] = "SMS-Sent-Today       :";                                                                          
		dataRecord6[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSSuccessToday());                     
		dataRecord6[4] = "SMS-Failure-Today       :";                                                                          
		dataRecord6[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSFailuresToday());
		
		// yesterday
		String[] dataRecord7 = new String[6];
		dataRecord7[0] = "Notification-Proce-Yesterday :";
		dataRecord7[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalNotificationProcessedYesterday());
		dataRecord7[2] = "";
		dataRecord7[3] = "";
		dataRecord7[4] = "";
		dataRecord7[5] = "";
		
		String[] dataRecord8 = new String[6];
		dataRecord8[0] = "Email-Proce-Yesterday        :";
		dataRecord8[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailProcessedYesterday());
		dataRecord8[2] = "Email-Sent-Yesterday :";
		dataRecord8[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailSuccessYesterday());
		dataRecord8[4] = "Email-Failure-Yesterday :";
		dataRecord8[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalEmailFailuresYesterday());
		
		String[] dataRecord9 = new String[6];
		dataRecord9[0] = "SMS-Proce-Yesterday          :";                                                                          
		dataRecord9[1] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSProcessedYesterday()); 
		dataRecord9[2] = "SMS-Sent-Yesterday   :";                                                                          
		dataRecord9[3] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSSuccessYesterday());                     
		dataRecord9[4] = "SMS-Failure-Yesterday   :";                                                                          
		dataRecord9[5] = String.valueOf(notificationServiceStatisticsProvider.getTotalSMSFailuresYesterday());
		
		int column0 = findMaxLength(dataRecord1[0].length() , dataRecord2[0].length() , dataRecord3[0].length() ,
					dataRecord4[0].length() , dataRecord5[0].length() , dataRecord6[0].length(),
					dataRecord7[0].length() , dataRecord8[0].length() , dataRecord9[0].length());

		int column1 = findMaxLength(dataRecord1[1].length() , dataRecord2[1].length() , dataRecord3[1].length() ,
				dataRecord4[1].length() , dataRecord5[1].length() , dataRecord6[1].length(),
				dataRecord7[1].length() , dataRecord8[1].length() , dataRecord9[1].length());
		
		int column2 = findMaxLength(dataRecord1[2].length() , dataRecord2[2].length() , dataRecord3[2].length() ,
				dataRecord4[2].length() , dataRecord5[2].length() , dataRecord6[2].length(),
				dataRecord7[2].length() , dataRecord8[2].length() , dataRecord9[2].length());
		
		int column3 = findMaxLength(dataRecord1[3].length() , dataRecord2[3].length() , dataRecord3[3].length() ,
				dataRecord4[3].length() , dataRecord5[3].length() , dataRecord6[3].length(),
				dataRecord7[3].length() , dataRecord8[3].length() , dataRecord9[3].length());
		
		int column4 = findMaxLength(dataRecord1[4].length() , dataRecord2[4].length() , dataRecord3[4].length() ,
				dataRecord4[4].length() , dataRecord5[4].length() , dataRecord6[4].length(),
				dataRecord7[4].length() , dataRecord8[4].length() , dataRecord9[4].length());
		
		int column5= findMaxLength(dataRecord1[5].length() , dataRecord2[5].length() , dataRecord3[5].length() ,
				dataRecord4[5].length() , dataRecord5[5].length() , dataRecord6[5].length(),
				dataRecord7[5].length() , dataRecord8[5].length() , dataRecord9[5].length());
		
		String[] header = {"","","","","",""};
		int[] width = {column0,column1,column2,column3,column4,column5};
		int[] column_alignment = {TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT};
		TableFormatter formatter = new TableFormatter(header, width, column_alignment ,TableFormatter.NO_BORDERS);
		
		formatter.addRecord(dataRecord1);
		formatter.addNewLine();
		formatter.addRecord(dataRecord2);
		formatter.addNewLine();
		formatter.addRecord(dataRecord3);
		formatter.addNewLine();
		formatter.addNewLine();
		
		formatter.addRecord(dataRecord4);
		formatter.addNewLine();
		formatter.addRecord(dataRecord5);
		formatter.addNewLine();
		formatter.addRecord(dataRecord6);
		formatter.addNewLine();
		formatter.addNewLine();
		
		formatter.addRecord(dataRecord7);
		formatter.addNewLine();
		formatter.addRecord(dataRecord8);
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
		out.println("  Description: Display Notification Service Statistics");
		out.println();
		return writer.toString();
	}

	@Override
	public String getKey() {
		return "all";
	}
	
	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}
