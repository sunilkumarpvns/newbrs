package com.elitecore.core.serverx;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.util.cli.CLIExitStatusCodes;
import com.elitecore.core.util.cli.CLIUtility;
import com.elitecore.core.util.cli.JMXException;
import com.elitecore.core.util.cli.cmd.JMXCommnadExecutor;

public abstract class CLICommandExecutor {
	private static final String SET_INPUT = "set input";
	private static final String SET_OUTPUT = "set output";
	private SimpleDateFormat sdf;
	private String serverName;
	private FileWriter outputFile;
	private JMXCommnadExecutor jmxExecutor;
	private static final String JMX_COMMUNICATION_ERROR_MSG = "Communication Error";

	public CLICommandExecutor(String serverName, JMXCommnadExecutor jmxExecutor) {		
		this.serverName = serverName;
		this.jmxExecutor = jmxExecutor;
		this.sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	}
	
	public CLICommandExecutor(String serverName, JMXCommnadExecutor jmxExecutor, FileWriter outputFile) {
		this(serverName, jmxExecutor);
		this.outputFile = outputFile;
	}

	protected abstract void displayCommandResult(String result);

	public CLIExitStatusCodes execute(List<String> cliCommandList) {
		CLIExitStatusCodes exitStatus = CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
		for (int i = 0; i < cliCommandList.size(); i++) {
			exitStatus = execute(cliCommandList.get(i));
			if (exitStatus == CLIExitStatusCodes.QUIT){
				return CLIExitStatusCodes.QUIT;
			}
		}
		return exitStatus;
	}

	public CLIExitStatusCodes execute(String command) {

		if(isQuitCommand(command)){
			displayCommandResult("Bye");
			return CLIExitStatusCodes.QUIT;
		}
		CLIExitStatusCodes returnStatus = CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
		command = command.trim();
		
		if(command.length() == 0 ){
			return returnStatus;
		}
		String [] args = command.split(" ");
		if(isInputFileCommand(args)){
			returnStatus = executeInputCommand(args[2]);
		}else if(isOutputFileCommand(args)){
			returnStatus = executeOutputCommand(args[2]);
		} else if(isRestartCommand(args)) {
			returnStatus = executeRestartCommand(command);
		}else{
			String result = "";
			try {
				result = executeCommand(command);
			} catch (JMXException e) { 
				result = JMX_COMMUNICATION_ERROR_MSG;
				ignoreTrace(e);
				return CLIExitStatusCodes.COMMAND_EXECUTION_ERROR;
			}finally{
				displayCommandResult(result);
				writeToOutputFile(result, command);
			}
		}
		
		return returnStatus;
	}

	private CLIExitStatusCodes executeRestartCommand(String command) {
		String result = "";
		try {
			result = executeCommand(command);
			if (result.contains(LifeCycleState.RESTART_CALLED.message)) {
				displayCommandResult(result);
				do {
					Thread.sleep(1000);
					try {
						result = jmxExecutor.retriveServerState();
					} catch (JMXException e) { 
						// Expected. Just continue processing
						result = LifeCycleState.STOPPED.message;
						ignoreTrace(e);
					}
				} while (LifeCycleState.RUNNING.message.equals(result) == false && 
						LifeCycleState.RUNNING_WITH_LAST_CONF.message.equals(result) == false); 
				result =  String.format("Server restarted successfully \n %s",executeCommand("services"));
			}
		} catch (JMXException e) { 
			result = JMX_COMMUNICATION_ERROR_MSG;
			ignoreTrace(e);
			return CLIExitStatusCodes.COMMAND_EXECUTION_ERROR;
		} catch (InterruptedException e) {
			displayCommandResult(e.getMessage());
		} finally{
			displayCommandResult(result);
			writeToOutputFile(result, command);
		}
		return CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
	}

	private boolean isRestartCommand(String[] args) {
		return "restart".equalsIgnoreCase(args[0]);
	}

	public boolean isQuitCommand(String quitStr) {
		return (quitStr == null || "quit".equalsIgnoreCase(quitStr) 
				|| "q".equalsIgnoreCase(quitStr) 
				|| "exit".equalsIgnoreCase(quitStr) 
				|| "bye".equalsIgnoreCase(quitStr));
	}

	private boolean isInputFileCommand(String[] command){
		if(command.length == 3 
				&& "set".equalsIgnoreCase(command[0]) 
				&& "input".equalsIgnoreCase(command[1]) 
				&& !"help".equalsIgnoreCase(command[2])){
			return true;
		}
		return false;
	}

	private CLIExitStatusCodes executeInputCommand(String filePath) {
		List<String> fileCommandList = null;
		try {
			fileCommandList = CLIUtility.readCommandsFromFile(filePath);
			return execute(fileCommandList);
		} catch (FileNotFoundException e) { 
			StringBuilder output = new StringBuilder();
			output.append(getCommandHeader(SET_INPUT));
			output.append(SET_INPUT + ": cannot execute: " + e.getMessage());
			displayCommandResult(output.toString());
			ignoreTrace(e);
			return CLIExitStatusCodes.FILE_ERROR;
		} catch (IOException e) { 
			StringBuilder output = new StringBuilder();
			output.append(getCommandHeader(SET_INPUT));
			output.append(SET_INPUT + ": unable to read ‘" + filePath + "’");
			displayCommandResult(output.toString());
			ignoreTrace(e);
			return CLIExitStatusCodes.IO_ERROR;
		}
	}

	private boolean isOutputFileCommand(String[] command){
		if(command.length == 3 
				&& "set".equalsIgnoreCase(command[0]) 
				&& "output".equalsIgnoreCase(command[1]) 
				&& !"help".equalsIgnoreCase(command[2])){
			return true;
		}
		return false;
	}

	private CLIExitStatusCodes executeOutputCommand(String filePath) {
		try {
			outputFile = CLIUtility.getOutputFile(filePath);
		} catch (IOException e) { 
			StringBuilder output = new StringBuilder();
			output.append(getCommandHeader(SET_OUTPUT));
			output.append(SET_OUTPUT + ": unable to write : " + e.getMessage());
			displayCommandResult(output.toString());
			ignoreTrace(e);
			return CLIExitStatusCodes.IO_ERROR;
		}
		return CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
	}

	private String executeCommand(String commandArg) throws JMXException{

		int index = commandArg.indexOf(" ");

		String strCommand = commandArg;
		String strCommadParameters = "";
		if(index != -1){
			strCommand = commandArg.substring(0, index);
			strCommadParameters = commandArg.substring(index + 1, commandArg.length());
		}
		long timeElapsed = System.currentTimeMillis();
		String response = jmxExecutor.execute(strCommand, strCommadParameters.trim());
		timeElapsed = System.currentTimeMillis() - timeElapsed;
		if(response != null){
			return response + "\nExecuted On: " + sdf.format(new Date()) + " -- Time Taken: "+ timeElapsed + "ms";
		}
		return "";
	}

	private void writeToOutputFile(String result, String commandName){
		if(outputFile == null){
			return;
		}
		StringBuilder output = new StringBuilder();
		output.append(getCommandHeader(commandName));
		output.append(result);
		try {
			outputFile.write(output.toString()+"\n");
		} catch (IOException e) { 
			output = new StringBuilder();
			output.append(getCommandHeader(SET_INPUT));
			output.append(SET_OUTPUT + ": unable to write : " + e.getMessage());
			displayCommandResult(output.toString());
			ignoreTrace(e);
		}
	}

	private String getCommandHeader(String commandName) {
		return  "\nCommand : " + commandName + 
				"\nInstance: " + serverName + "\n\n";
	}

	public void closeOutputFile() {
		FileUtil.closeQuietly(outputFile);
	}

}
