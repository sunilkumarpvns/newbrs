package com.elitecore.core.util.cli.cmd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.logmonitor.LogMonitorManager;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;

/**
 *
 * @author harsh patel
 */
public class LogMonitorCommandExt extends EliteBaseCommand {
	private static final String ADD 	= "add";
	private static final String CLEAR 	= "clear";
	private static final String LIST 	= "list";
	private static final String CLEAR_ALL 	= "clearall";
	private String[] operations = {ADD,CLEAR,LIST,CLEAR_ALL};
	private LogMonitorManager logMonitorManager;

	@VisibleForTesting
	LogMonitorCommandExt(LogMonitorManager logMonitorManager) {
		this.logMonitorManager = logMonitorManager;
	}


	public LogMonitorCommandExt() {
		this.logMonitorManager = LogMonitorManager.getInstance();
	}

	@Override
	public String execute(String parameter) {
		
		if(Strings.isNullOrBlank(parameter)){
			return getHelpMsg();
		}

		String[] parameters = Splitter.on(' ').splitToArray(parameter.trim());
				
		String val = parameters[0].trim(); 
		
		if(isHelpParameter(val) || "help".equalsIgnoreCase(val)){
			return getHelpMsg();
		}
		

		Monitor<?,?> monitor = null;
		if(parameters.length >= 2) {
			monitor = logMonitorManager.getLogMonitor(parameters[1]);
		}

		String operation = parameters[0];
		if(ADD.equalsIgnoreCase(operation)){
			
			//ex. logmonitor add
			if (isAddMonitorNotProvided(parameters)) {
				return "Monitor not provided." + getHelpMsg();
			}
			
			//ex. logmonitor add wrongMonitor
			if(monitor == null) {
				return "Invalid Monitor." + getHelpMsg();
			}

			//ex.logmonitor add monitor
			if(isAddConditionNotProvided(parameters)) {
				return "Condition not provided." + getHelpMsg();
			}
			
			String condition = null;
			
			long time = 1;// if time not specified default value(1min.) will be used
				int conditionEndIndex  = parameters.length;
				//ex.logmonitor add monitor 0:1="harsh"
				try {
					time = Long.parseLong(parameters[parameters.length - 1]);
					conditionEndIndex = parameters.length - 1;
				} catch(NumberFormatException ex){
					time = 1;
				}
			condition = Strings.join(" ", Arrays.copyOfRange(parameters, 2, conditionEndIndex));
			
			try{
				monitor.add(condition, time);
				return "Condition: " + condition + " added successfully in " + monitor.getType() + " monitor";
			}catch(Exception ex){
			    LogManager.ignoreTrace(ex);
				return "Fail to add Condition: " + condition + ". Reason: " + ex.getMessage();
            }
		}else if (CLEAR.equalsIgnoreCase(operation)) {
			
			//ex. logmonitor clear 
			if(isClearMonitorOrConditionNotProvided(parameters)) {
				return "Monitor or Condition not provided." + getHelpMsg();
			}
			
			String condition  = null;
			
			if(monitor == null) {
				if(isClearConditionProvided(parameters)) {
					condition = Strings.join(" ", Arrays.copyOfRange(parameters, 1, parameters.length));
					//ex. logmonitor clear condition
					return clear(condition);
				} else {
					return "Invalid Monitor." + getHelpMsg();
				}
			} else {
				//ex. logmonitor clear monitor condition
				if(isClearMonitorConditionProvided(parameters)) {
					condition = Strings.join(" ", Arrays.copyOfRange(parameters, 2, parameters.length));
					return clear(condition, monitor);
				} else {
					return "Condition not provided." + getHelpMsg();
				}
			}
			
		}else if (LIST.equalsIgnoreCase(operation)) {
			
			if(isListParametersExceeded(parameters)) {
				return "LIST operation must not have more than 1 parameters." + getHelpMsg();
			}
			
			if(isListMonitorProvided(parameters)) {
				if(monitor == null) {
					return "Invalid Monitor." + getHelpMsg();
				}
				return list(monitor);
			} else {
				return list();
			}
		}else if (CLEAR_ALL.equalsIgnoreCase(operation)) {
			
			if(isClearAllParametersExceeded(parameters)) {
				return "CLEARALL operation must not have more than 1 parameters." + getHelpMsg();
			}
			
			//ex. logmonitor clearall
			if (isClearAllMonitorNotProvided(parameters)) {
				return "Monitor not provided." + getHelpMsg();
			}
			
			//ex. logmonitor clear all wrongMonitor
			if(monitor == null){
				return "Invalid Monitor." + getHelpMsg();
			}
			//ex. logmonitor clearall monitor
			return monitor.clearAll();
		}else {
			return "Invalid Operation" + getHelpMsg();
		}
	}
	
	private boolean isClearAllMonitorNotProvided(String[] parameters) {
		return parameters.length == 1;
	}

	private boolean isClearAllParametersExceeded(String[] parameters) {
		return parameters.length > 2;
	}

	private boolean isListMonitorProvided(String[] parameters) {
		return parameters.length == 2 ;
	}

	private boolean isListParametersExceeded(String[] parameters) {
		return parameters.length > 2;
	}

	private boolean isClearMonitorConditionProvided(String[] parameters) {
		return parameters.length >= 3;
	}

	private boolean isClearConditionProvided(String[] parameters) {
		return parameters.length >= 2;
	}

	private boolean isAddConditionNotProvided(String[] parameters) {
		return parameters.length == 2;
	}
	
	private boolean isClearMonitorOrConditionNotProvided(String[] parameters) {
		return parameters.length == 1;
	}


	private boolean isAddMonitorNotProvided(String[] parameters) {
		return parameters.length == 1;
	}

	@Override
	public String getCommandName() {
		return "logmonitor";
	}

	@Override
	public String getDescription() {
		return "Add,Remove and View condition to get satisfied request logs in seperate .log file";
	}
	

	private String list(Monitor<?,?> monitor){
		return monitor.list();
	}
	
	private String list(){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		for(Monitor<?, ?> monitor : logMonitorManager.getRegisteredLogMonitors()){
			out.println("------" + monitor.getType() + "------");
			out.println(list(monitor));
			
		}
		
		out.println();
		return writer.toString();	
	}
	
	
	private String clear(String condition){
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println();
		for(Monitor<?, ?> monitor : logMonitorManager.getRegisteredLogMonitors()){
			out.println(clear(condition, monitor));
		}
		
		out.println();
		return writer.toString();	
	}
	
	private String clear(String condition, Monitor<?, ?> monitor){
		return monitor.clear(condition);
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("{'logmonitor':{");
		int size = logMonitorManager.getRegisteredLogMonitors().size();
		for(int j=0;j<operations.length;j++){
			out.print("'"+operations[j]+"':{");
			int i =1;
			

			for(Monitor<?, ?> provider : logMonitorManager.getRegisteredLogMonitors()){
				if(i != (size)){
					out.print(provider.getHotkeyHelp() + ",");
				}else{
					out.print(provider.getHotkeyHelp());
				}
				i++;
			}
			
			if(j!=operations.length-1){
				out.print("},");
			}else {
				out.print("}");
			}
			
		}
		
		out.print("}}");
		
		return writer.toString();	
	}

	@Override
	public String getHelpMsg(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println(" Description : "+ getDescription());
		out.println();
		out.println(" logmonitor <operation> <monitor>");
		out.println(" Possible Operations : ");
		out.print("   ");
		int i = 0;
		for(i = 0; i < operations.length-1 ; i++){
			out.print(operations[i] + ", ");
		}
		out.println(operations[i]);
		out.println();
		
		
		out.println(" Possible Monitors : ");
		Set<String> registeredMonitorTypes = logMonitorManager.getRegisteredLogMonitorTypes();
		if(registeredMonitorTypes != null && !registeredMonitorTypes.isEmpty()){
			for(String monitorType : registeredMonitorTypes){
				out.println("  " + monitorType);
			}
		}else {
			out.println(" No monitor registered");
		}
		out.println();
		
		out.println(" Usage : ");
		out.println();
		out.println(" Add Operation : ");
		out.println("  logmonitor add <monitor> <condition> [time duration(min)]");
		out.println("  Time Duration: duration untill which condtion is applicable, value less then or equal to 0 is consider as NO TIME LIMIT");
		out.println("  ex. logmonitor add radius 0:1=\"ELITECORE\" 10");
		out.println("  ex. logmonitor add radius 0:1=\"ELITECORE\"");
		
		
		out.println();
		out.println(" Clear Operation : ");
		out.println("  logmonitor clear <condition>");
		out.println("  ex. logmonitor clear 0:1=\"ELITECORE\"");
		out.println("  logmonitor clear <monitor> <condition>");
		out.println("  ex. logmonitor clear radius 0:1=\"ELITECORE\"");
		
		out.println();
		out.println(" Clearall Operation : ");
		out.println("  logmonitor clearall <monitor>");
		out.println("  ex. logmonitor clearall radius");
		
		out.println();
		out.println(" List Operation : ");
		out.println("  logmonitor list ");
		out.println("  logmonitor list <monitor>");
		out.println("  ex. logmonitor list radius");
		out.close();
		return stringWriter.toString();
	}
}
