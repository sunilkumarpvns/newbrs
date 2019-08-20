package com.elitecore.aaa.core.util.cli;

import java.io.StringWriter;

import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.commons.kpi.exception.StartupFailedException;
import com.elitecore.commons.kpi.handler.MIBSerializer;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;

public class KpiServiceCommand extends EliteBaseCommand {
	private static final String KPISERVICE = "kpiservice";
	private static final String HELP = "-help";
	private static final String STOP = "-stop";
	private static final String START = "-start";
	private static final String FLUSH = "-flush";
	private MIBSerializer mibSerializer;
	private KpiConfiguration kpiConfig;
	
	public KpiServiceCommand(MIBSerializer mibSerializer, KpiConfiguration kpiConfig) {
		this.mibSerializer = mibSerializer;
		this.kpiConfig = kpiConfig;
	}
	
	@Override
	public String execute(String parameter) {
		
		String[] parseArgumentString = parseArgumentString(parameter);
		
		if(parseArgumentString == null || parseArgumentString.length != 1) {
			return KPISERVICE + ": Invalid no of arguments. " + CommonConstants.LINE_SEPARATOR + "Usage: " + getHelpMsg();
		}
		
		String responseMessage = "";
		String commandOption = parseArgumentString[0];

		if(START.equals(commandOption)) {
			responseMessage = executeKPIServiceStartCommand();
		} else if(STOP.equals(commandOption)) {
			responseMessage = executeKPIServiceStopCommand();
		} else if (FLUSH.equals(commandOption)) {
			responseMessage = executeKPIServiceFlushCommand();
		} else if(HELP.equals(commandOption)) {
			responseMessage = getHelpMsg();
		} else {
			responseMessage = generateInvalidArgumentMessage(commandOption);
		}

		return responseMessage;
	}

	private String executeKPIServiceFlushCommand() {
		return mibSerializer.flush();
	}

	private String generateInvalidArgumentMessage(String commandOption) {
		return KPISERVICE + ": Invalid argument: " + commandOption + CommonConstants.LINE_SEPARATOR + "Usage: " + getHelpMsg();
	}

	private String executeKPIServiceStopCommand() {
		mibSerializer.stop();
		return "KPI service stopped successfully";
	}

	private String executeKPIServiceStartCommand() {
		DBConnectionManager connectionManager = DBConnectionManager.getInstance(kpiConfig.getDSName());
		try {
			connectionManager.reInit();
		} catch (DatabaseInitializationException e) {
			return "Failed to start KPI Service, Reason: " + e.getMessage();
		}
		
		try {
			mibSerializer.restart();
		} catch (StartupFailedException e) {
			return "Failed to start KPI Service, Reason: " + e.getMessage();
		} catch (InitializationFailedException e) {
			return "Failed to start KPI Service, Reason: " + e.getMessage();
		}
		return "KPI Service started successfully";
	}

	@Override
	public String getCommandName() {
		return KPISERVICE;
	}

	@Override
	public String getDescription() {
		return "Controls KPI service";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'"+ KPISERVICE + "':{'-start':{},'-stop':{},'-flush':{},'-help':{}}}";	
	}
	
	@Override
	public String getHelpMsg(){
		StringWriter stringWriter = new StringWriter();
		TabbedPrintWriter out = new TabbedPrintWriter(stringWriter);
		out.println(KPISERVICE+ " <option>");
		out.println("Description : "+ getDescription());
		out.println("Possible Options : ");
		out.incrementIndentation();
		out.println("-start - start the KPI service");
		out.println("-stop  - stop the KPI service");
		out.println("-flush  - flush KPI records");
		out.close();
		return stringWriter.toString();
	}
	
}
