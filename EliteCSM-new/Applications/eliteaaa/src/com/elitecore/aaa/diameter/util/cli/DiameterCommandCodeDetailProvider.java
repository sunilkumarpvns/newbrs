package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.mibs.statistics.CCCounterTuple;
import com.elitecore.diameterapi.mibs.statistics.CounterTuple;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;

public class DiameterCommandCodeDetailProvider extends DetailProvider{

	private static final String key  = "cmd";
	private static final String HELP  = "-help";

	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticsProvider diameterStatisticProvider;
	private static final int TABLE_WIDTH = 76;

	public DiameterCommandCodeDetailProvider(DiameterStatisticsProvider diameterStatisticProvider) {
		this.diameterStatisticProvider = diameterStatisticProvider;
		detailProviderMap = new HashMap<String ,DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters == null || parameters.length == 0) {
			return getCommandCodeUsage();
		}

		if (isHelpSymbol(parameters[0])){
			return getCommandCodeUsage();
		}
		
		int iCommandCode = -1;
		try {
			iCommandCode = Integer.parseInt(parameters[0]);
		} catch (NumberFormatException e) {
			CommandCode commandCode = CommandCode.fromDisplayName(parameters[0]);
			if (commandCode != null) {
				iCommandCode = commandCode.code;
			}
		}

		if (iCommandCode == -1) {
			return "Invalid Command-Code";
		}

		TableFormatter formatter = new TableFormatter( new String[]{}, new int[]{TABLE_WIDTH}, TableFormatter.NO_BORDERS);

		Map<Integer, CounterTuple> stackStats = diameterStatisticProvider.getStackStatistics().getCommandCodeCountersMap();

		CounterTuple tuple = stackStats.get(iCommandCode);
		if(	tuple != null ){
			formatter.addNewLine();
			formatter.add("Command-Code = " + CommandCode.getDisplayName(iCommandCode)+"("+ iCommandCode + ")", TableFormatter.CENTER);
			formatter.addNewLine();
			addFormatedRecord(iCommandCode, tuple,formatter);
			formatter.add(getLegend());
		} else {
			formatter.add("No Statistics Available", TableFormatter.LEFT);
		}

		return formatter.getFormattedValues();
	}

	private void addCCFormatedRecord(int commandCode, CCCounterTuple tuple, TableFormatter output) {
		TableFormatter formatter1 = new TableFormatter(new String[]{
				"CMD", "R-Rx",  "A-Tx", "R-Tx", "A-Rx", "R-Rt", "R-To"}, 
				new int[]{5, 10, 10, 10, 10, 10, 10} , 
				new int[]{TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT,
				TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT},
				TableFormatter.ONLY_HEADER_LINE);

		TableFormatter formatter2 = new TableFormatter(new String[]{
				"CMD", "R-Dr", "A-Dr", "A-Un", "R-Du", "A-Du", "Mf-Msg", "R-Pn"}, 
				new int[]{5, 9, 9, 8, 8, 8, 8, 8} , 
				new int[]{TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT,
				TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT},
				TableFormatter.ONLY_HEADER_LINE);

		formatter1.addRecord(new String[]{
				"Total", 
				getDisplayValue(tuple.getRequestInCount(), 10), 
				getDisplayValue(tuple.getAnswerOutCount(), 10),  
				getDisplayValue(tuple.getRequestOutCount(), 10), 
				getDisplayValue(tuple.getAnswerInCount(), 10), 
				getDisplayValue(tuple.getRequestsRetransmittedCount(), 10),  
				getDisplayValue(tuple.getTimeoutRequestStatistics(), 10),     });
		
		formatter1.addRecord(new String[]{
				"CC-I", 
				getDisplayValue(tuple.getInitialRequestRx(), 10), 
				getDisplayValue(tuple.getInitialAnswerTx(), 10),  
				getDisplayValue(tuple.getInitialRequestTx(), 10), 
				getDisplayValue(tuple.getInitialAnswerRx(), 10), 
				"0", 
				"0",});
		
		formatter1.addRecord(new String[]{
				"CC-U", 
				getDisplayValue(tuple.getUpdateRequestRx(), 10), 
				getDisplayValue(tuple.getUpdateAnswerTx(), 10),  
				getDisplayValue(tuple.getUpdateRequestTx(), 10), 
				getDisplayValue(tuple.getUpdateAnswerRx(), 10), 
				"0", 
				"0",});
		
		formatter1.addRecord(new String[]{
				"CC-T", 
				getDisplayValue(tuple.getTerminateRequestRx(), 10), 
				getDisplayValue(tuple.getTerminateAnswerTx(), 10),  
				getDisplayValue(tuple.getTerminateRequestTx(), 10), 
				getDisplayValue(tuple.getTerminateAnswerRx(), 10), 
				"0", 
				"0",});
		
		formatter1.addRecord(new String[]{
				"CC-O", 
				getDisplayValue(tuple.getOtherRequestRx(), 10), 
				getDisplayValue(tuple.getOtherAnswerTx(), 10),  
				getDisplayValue(tuple.getOtherRequestTx(), 10), 
				getDisplayValue(tuple.getOtherAnswerRx(), 10), 
				"0", 
				"0",});

		formatter2.addRecord(new String[]{
				"Total", 
				getDisplayValue(tuple.getRequestDroppedCount(), 9), 
				getDisplayValue(tuple.getAnswerDroppedCount(), 9), 
				getDisplayValue(tuple.getUnknownHbHAnswerDroppedCount(), 8), 
				getDisplayValue(tuple.getDuplicateRequestCount(), 8), 
				getDisplayValue(tuple.getDuplicateEtEAnswerCount(), 8), 
				getDisplayValue(tuple.getMalformedPacketReceivedCount(), 8),
				getDisplayValue(tuple.getPendingRequestCount(), 8)  });

		output.add("Counter Details", TableFormatter.CENTER);
		output.add(formatter1.getFormattedValues());
		output.addNewLine();
		output.add("Error Message Details", TableFormatter.CENTER);
		output.add(formatter2.getFormattedValues());
		output.addNewLine();
	}
	
	private void addFormatedRecord(int commandCode, CounterTuple tuple, TableFormatter output) {

		if (tuple instanceof CCCounterTuple) {
			addCCFormatedRecord(commandCode, (CCCounterTuple) tuple, output);
			return;
		}
		
		TableFormatter formatter1 = new TableFormatter(new String[]{
				"CMD", "R-Rx",  "A-Tx", "R-Tx", "A-Rx", "R-Rt", "R-To"}, 
				new int[]{5, 10, 10, 10, 10, 10, 10} , 
				new int[]{TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT,
				TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT},
				TableFormatter.ONLY_HEADER_LINE);

		TableFormatter formatter2 = new TableFormatter(new String[]{
				"CMD", "R-Dr", "A-Dr", "A-Un", "R-Du", "A-Du", "Mf-Msg", "R-Pn"}, 
				new int[]{5, 9, 9, 8, 8, 8, 8, 8} , 
				new int[]{TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT,
				TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT},
				TableFormatter.ONLY_HEADER_LINE);

		formatter1.addRecord(new String[]{
				"Total", 
				getDisplayValue(tuple.getRequestInCount(), 10), 
				getDisplayValue(tuple.getAnswerOutCount(), 10),  
				getDisplayValue(tuple.getRequestOutCount(), 10), 
				getDisplayValue(tuple.getAnswerInCount(), 10), 
				getDisplayValue(tuple.getRequestsRetransmittedCount(), 10),  
				getDisplayValue(tuple.getTimeoutRequestStatistics(), 10),     });

		formatter2.addRecord(new String[]{
				"Total", 
				getDisplayValue(tuple.getRequestDroppedCount(), 9), 
				getDisplayValue(tuple.getAnswerDroppedCount(), 9), 
				getDisplayValue(tuple.getUnknownHbHAnswerDroppedCount(), 8), 
				getDisplayValue(tuple.getDuplicateRequestCount(), 8), 
				getDisplayValue(tuple.getDuplicateEtEAnswerCount(), 8), 
				getDisplayValue(tuple.getMalformedPacketReceivedCount(), 8),
				getDisplayValue(tuple.getPendingRequestCount(), 8)  });

		output.add("Counter Details", TableFormatter.CENTER);
		output.add(formatter1.getFormattedValues());
		output.addNewLine();
		output.add("Error Message Details", TableFormatter.CENTER);
		output.add(formatter2.getFormattedValues());
		output.addNewLine();

	}

	private String getDisplayValue(long counter, int wrapDigts) {
		if(counter > (Math.pow(10, wrapDigts)-1))
			return (counter/1000) + "k";
		return String.valueOf(counter);
	}

	private String getLegend(){
		StringBuilder responseBuilder = new StringBuilder();

		responseBuilder.append(StringUtility.fillChar("", TABLE_WIDTH+2, '-'));
		responseBuilder.append("\n  Rx  : Received     ||   Tx  : Transmitted   ||   Rt  : Retransmitted"); 
		responseBuilder.append("\n  To  : Timeout      ||   Dr  : Dropped       ||   Un  : Unknown");
		responseBuilder.append("\n  Du  : Duplicate    ||   Mf  : Malformed     ||   Pn  : Pending" );
		responseBuilder.append("\n" + StringUtility.fillChar("", TABLE_WIDTH+2, '-') + "\n");
		return responseBuilder.toString();
	}

	@Override
	public String getHelpMsg() {
		return StringUtility.fillChar(getKey(), 20, ' ') + " : " + getDescription() + "\n";
	}
	
	public String getCommandCodeUsage() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage       : show diameter statistic cmd [<options>]");
		responseBuilder.append("\nDescription : "+getDescription() +"\n\n");
		responseBuilder.append("-----------------------------Possible Options:----------------------------------\n");
		responseBuilder.append(StringUtility.fillChar("<Command-Code>", 20, ' ')+" : Displays Command-Code wise Statistics of Stack.\n");
		responseBuilder.append(StringUtility.fillChar(HELP, 20, ' ')+" : Displays this message.\n");
		return responseBuilder.toString();
	}

	public String getDescription(){
		return "Displays Diameter Command Code Statistics.";
	}
	
	@Override
	public String getHotkeyHelp() {
		StringBuilder hotKeyHelp = new StringBuilder();
		hotKeyHelp.append(key+":{");
		CommandCode[] commandCodes = CommandCode.VALUES;
		for (int i=0 ; i<commandCodes.length ; i++) {
			hotKeyHelp.append(commandCodes[i].displayName+":{}, ");
		}
		String strHotKeyHelp = hotKeyHelp.substring(0, hotKeyHelp.length() -2) + ", "+HELP+":{}}"; 
		return strHotKeyHelp;
	}
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

}
