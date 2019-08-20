package com.elitecore.nvsmx.system.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.jmx.SubscriberImportStatistics;
import com.elitecore.corenetvertex.subscriberimport.InputType;
import com.elitecore.corenetvertex.subscriberimport.SubscriberImportParameters;
import com.elitecore.nvsmx.system.jmx.SubscriberImportController;

public class SubscriberImportCommand extends EliteBaseCommand {

	private static final String KEY = "import648Subscriber";
	private static final int INDEX_FOR_INPUT_TYPE = 1;
	private static final int INDEX_FOR_INPUT_FILE_PATH = 2;
	private static final int INDEX_FOR_PACKAGE_MAPPING_FILE_PATH = 3;
	private static final int MIN_PARAMETERS_LENGTH = 3;
	private static final String START = "-start";
	private static final String STOP = "-stop";
	private static final String STATUS = "-status";
	private static final int WIDTH_NO = 3;
	private static final int WIDTH_STATUS = 60;
	private static final int WIDTH_DATE = 30;
	
	private final HashMap<String, DetailProvider> detailProviderMap;
	private SubscriberImportController subscriberImportController;
	
	//for status history
	private int[] widthHistory = {WIDTH_NO,WIDTH_STATUS,WIDTH_DATE};
	private String[] headerHistory = {"NO","STATUS","TIMESTAMP"};

	public SubscriberImportCommand(SubscriberImportController subscriberImportController) {
		this.detailProviderMap = new HashMap<String, DetailProvider>(1);
		this.subscriberImportController = subscriberImportController;
	}

	@Override
	public String execute(String parameter) {

		String[] parameters = parameter.split(" ");

		if (detailProviderMap.containsKey(parameters[0].toLowerCase())) {
			String destArray[] = new String[parameters.length - 1];
			System.arraycopy(parameters, 1, destArray, 0, destArray.length);
			return detailProviderMap.get(parameters[0].toLowerCase()).execute(destArray);
		} else if (parameters[0].equalsIgnoreCase(START)) {
			
			/* 
			 * atleast 3 parameters need to run this command
			 * e.g. <start> <inputType> <inputFilePath> 
			 * 
			*/
			if (parameters.length < MIN_PARAMETERS_LENGTH) {
				return getHelpMsg();
			}
			
			InputType inputType = InputType.fromOption(parameters[INDEX_FOR_INPUT_TYPE]);
			
			if (inputType == null) {
				return "Invalid input type: " + parameters[INDEX_FOR_INPUT_TYPE] + getHelpMsg();
			}
			
			SubscriberImportParameters importParameters = new SubscriberImportParameters();
			importParameters.setInputType(inputType);
			importParameters.setInputFilePath(parameters[INDEX_FOR_INPUT_FILE_PATH]);
			
			if (parameters.length == INDEX_FOR_PACKAGE_MAPPING_FILE_PATH+1) {
				// package mapping is optional
				importParameters.setPackageMappingFilePath(parameters[INDEX_FOR_PACKAGE_MAPPING_FILE_PATH]);
			}
			
			return subscriberImportController.start(importParameters);

		} else if (parameters[0].equalsIgnoreCase(STOP)) {
			if (parameters.length != 1) {
				return getHelpMsg();
			}

			return stopImportProcess();
		} else if (parameters[0].equalsIgnoreCase(STATUS)) {
			if (parameters.length != 1) {
				return getHelpMsg();
			}
			
			return getFormattedStatus();
		}

		return getHelpMsg();
	}
	private String getFormattedStatus() {

		Iterator<SubscriberImportStatistics> statusHistoryIterator = subscriberImportController.status();

		TableFormatter formatter = new TableFormatter(headerHistory, widthHistory, TableFormatter.ALL_BORDER);
		if (statusHistoryIterator == null || statusHistoryIterator.hasNext() == false) {
			formatter.add("NO IMPORT HISTORY FOUND", TableFormatter.CENTER);
			return formatter.getFormattedValues();
		}

		int index = 1;
		while (statusHistoryIterator.hasNext()) {
			formatter.addRecord(formatImportStatus(statusHistoryIterator.next(), index));
			index++;
		}

		return formatter.getFormattedValues();
	}

	private String[] formatImportStatus(SubscriberImportStatistics statistics, int index) {
		
		String[] data = new String[widthHistory.length];
		data[0] = index+"";
		data[1] = getFormattedStatistics(statistics);
		data[2] = statistics.getImportStartTime().toString();
		return data;
	}

	private String getFormattedStatistics(SubscriberImportStatistics statistics) {
		return new StringBuilder("Submitted Task=").append(statistics.getSubmittedTaskCount()).append(CommonConstants.COMMA)
				.append("Success=").append(statistics.getSuccessCount()).append(CommonConstants.COMMA)
				.append("Fail=").append(statistics.getFailCount()).append(CommonConstants.COMMA)
				.append("Inprogress Task=").append(statistics.getInprogressCount()).toString();
	}

	private String stopImportProcess() {
		return subscriberImportController.stop();
	}

	@Override
	public String getCommandName() {
		return "import648Subscriber";
	}

	@Override
	public String getDescription() {
		return "import subscriber from NetVertex 6.4.8 to 6.8.0";
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("{'" + KEY + "':{'" + HELP_OPTION + "':{},");
		
		out.print("'" + START + "':{");
		
		InputType[] inputTypes = InputType.values();
		
		if (inputTypes.length > 0) {
			out.print("'" + inputTypes[0].option + "':{}");
			for (int i = 1; i < inputTypes.length; i++) {
				out.print(",'" + inputTypes[i].option + "':{}");
			}
		}

		out.print("}");
		
		out.print(",'" + STOP + "':{},'" + STATUS + "':{}");
			
		for (DetailProvider provider : detailProviderMap.values()) {
			out.print("," + provider.getHotkeyHelp());
		}
		out.print("}}");
		return writer.toString();
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		IndentingWriter out = new IndentingPrintWriter(stringWriter);
		out.println();
		out.println("   " + KEY + " <input-type> <option>");
		out.println(" Description: Import subscriber ");

		out.println(" Possible Input Type: ");
		out.incrementIndentation();
		
		for (InputType inputType: InputType.values()) {
			out.println(inputType.option);
		}

		out.decrementIndentation();
		
		out.println(" Possible Options: ");
		out.println();
		out.incrementIndentation();
		for (String provider : detailProviderMap.keySet()) {
			out.println(" " + provider + detailProviderMap.get(provider).getDescription());
		}
		out.print(StringUtility.fillChar(START, 8));
		out.println(": <subscriber-dump-file> [<package-name-mapping-file-path>] starts import subscriber task using provided input file");
		out.print(StringUtility.fillChar(STOP, 8));
		out.println(": Try to stop currently running import subscriber task");
		out.print(StringUtility.fillChar(STATUS, 8));
		out.println(": Shows statistics of last 5 import command execution");
		out.decrementIndentation();
		out.println();
		out.close();
		return stringWriter.toString();
	}
}
