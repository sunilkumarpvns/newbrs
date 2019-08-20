package com.elitecore.nvsmx.system.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatistics;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;

public class WebServiceStatisticsDetailProvider extends DetailProvider {
	
	private static final String GLOBAL_COUNTER_KEY = "global";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static final String MODULE = "WS-STATS-DETAIL-PROVIDER";
	private static final String KEY = "ws";
	private final short VALUE_WIDTH = 5;

	public WebServiceStatisticsDetailProvider() { 
		this.detailProviderMap = new HashMap<String, DetailProvider>(2);
	}

	@Override
	public String execute(String[] parameters) {
		
		if(parameters.length==0){
			return getHelpMsg();
		}
		
		if(parameters.length > 0){
			if(isHelpSymbol(parameters[0])){
				return getHelpMsg();
			} 
		}
		
		DetailProvider detailProvider = detailProviderMap.get(parameters[0]);
		if(detailProvider != null){
			parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
			return detailProvider.execute(parameters);
		}
		
		if(parameters!=null && parameters.length>0){		
			return getWebServiceStatistics(parameters[0]);
		}else{
			return getHelpMsg();
		}
	}

	private String getWebServiceStatistics(String value) {
		 
			if(GLOBAL_COUNTER_KEY.equalsIgnoreCase(value)){
				WebServiceStatistics globalWebServiceStatistics = WebServiceStatisticsManager.getInstance().getWSGlobalStatistics();
				String globalStats = getStatistics(GLOBAL_COUNTER_KEY, globalWebServiceStatistics);
				return globalStats + getOptions();
			}else{
				WebServiceStatistics webServiceStatistics = WebServiceStatisticsManager.getInstance().getWSStatistics(value);	
				 
				if(webServiceStatistics!=null){
					return getStatistics(value, webServiceStatistics) + getOptions();
				}else{
					return "No statistics Available for: "+value;
				}
			}
		 
	}

	private String getStatistics(String value, WebServiceStatistics webServiceStatistics) {
		
		String[] dataRecord1 = new String[6];
		dataRecord1[0] = "Total-Req-Rx: ";
		dataRecord1[1] = "" + webServiceStatistics.getTotalRequestCounter();
		dataRecord1[2] = "Total-Resp-Tx: ";
		dataRecord1[3] = "" + webServiceStatistics.getTotalResponseCounter();
		dataRecord1[4] = "Last-One-Minute-Req-Rx: ";
		dataRecord1[5] = ""+ webServiceStatistics.getLastMinuteRequestCounter();
		
		int totalRows = ResultCode.values().length/3;
		if(ResultCode.values().length%3!=0){
			totalRows++;
		}
		
		String[][] dataRecord5 = new String[totalRows][6];
		int index = -1 ;
		ResultCode[] values = ResultCode.values();
		int rowIndex = -1;
		long totalFailed = 0;
		while(totalRows >= 1){
			++rowIndex;
			 				
			for(int i=0; i<3; i++){
				
				ResultCode resultCode = null;
				
				String codeName = " ";
				String counterValue = " ";
				
				if(++index < values.length){
					resultCode = values[index];
					codeName = resultCode.name +"("+resultCode.code+"): ";
					long counters = webServiceStatistics.getResponseCodeCounter(resultCode);							
					counterValue = ""+counters;
					
					if(resultCode != ResultCode.SUCCESS){
						totalFailed += counters;
					}
				}
				
				
				if(i==0){
					dataRecord5[rowIndex][0] = codeName;
					dataRecord5[rowIndex][1] = counterValue; 
				} else if(i==1){
					dataRecord5[rowIndex][2] = codeName;
					dataRecord5[rowIndex][3] = counterValue; 
				} else if(i==2){
					dataRecord5[rowIndex][4] = codeName;
					dataRecord5[rowIndex][5] = counterValue; 
				}
			}
			--totalRows;
		}
 		
		int column1 = VALUE_WIDTH ;
		int column3 = VALUE_WIDTH ;
		int column5 = VALUE_WIDTH ;
		
		String[] header = {"","","","","",""};
		int[] width = {27,column1,30,column3,32,column5};
		int[] column_alignment = {TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT,
				TableFormatter.LEFT,TableFormatter.RIGHT};
		TableFormatter formatter = new TableFormatter(header, width, column_alignment ,TableFormatter.NO_BORDERS);
		
		formatter.add(" Statistics for: "+value);
		formatter.addNewLine();
		formatter.add("-------------------------------------------------------------------------------------------------------------------------");
		formatter.addNewLine();
		formatter.addRecord(dataRecord1);
		formatter.addNewLine();
		
		for(int i=0; i<=rowIndex; i++){
			formatter.addRecord(dataRecord5[i]);
		}
		
		formatter.addNewLine();
		formatter.add(" Total-Failed-Req-Rx: "+totalFailed);
		formatter.addNewLine();
		formatter.add(" TPS: ");
		formatter.add(String.valueOf(webServiceStatistics.getTPS()));
		formatter.addNewLine();
		return formatter.getFormattedValues();
	}
	
	private String getOptions(){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println("" + StringUtility.fillChar("",121, '-'));
		out.println("  Rx : Received  || Tx : Response ");
		out.println("" + StringUtility.fillChar("",121, '-'));
		return writer.toString();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		out.println("Description:  Display Web Service Statistics");
		out.println();
		out.println("To get WebService specific statistics");
		out.println("Syntax: show statistics ws WebServiceName");
		out.println("i.e: show statistics ws SubscriberProvisioningWS");
		
		out.println();
		out.println("To get WebService Method specific statistics");
		out.println("Syntax: show statistics ws WebServiceName#MethodName");
		out.println("i.e: show statistics ws SubscriberProvisioningWS#wsGetSubscriberProfileByID");
		
		out.println();
		out.println("To get Global statistics");
		out.println("i.e: show statistics ws global");

		return writer.toString();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("'"+KEY+"':{'"+HELP+"':{}");
		for(DetailProvider provider : detailProviderMap.values()){
				out.print("," + provider.getHotkeyHelp());
		}
		
		out.print(",'" + GLOBAL_COUNTER_KEY +"':{}");
		
		Set<String> cacheSet = WebServiceStatisticsManager.getInstance().getWebServicesKeysCache();
		for(String key : cacheSet){
			out.print(",'" + key+ "':{}");	
		}
		
		out.print("}");
		
		return writer.toString() ;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}