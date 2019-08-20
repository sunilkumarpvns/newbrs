
package com.elitecore.core.serverx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.util.cli.CLIExitStatusCodes;
import com.elitecore.core.util.cli.cmd.JMXCommnadExecutor;
import com.elitecore.core.util.cli.parser.CLIArguments;
import com.elitecore.core.util.cli.parser.CLIArgumentsParser;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.history.FileHistory;
import jline.console.history.MemoryHistory;
import jline.console.history.PersistentHistory;
import net.sf.json.JSONObject;

public class BaseCLI {	
	private static final String MODULE = "BASE-CLI";
	private String serverHome;
	private JMXCommnadExecutor jmxCommandExecutor = null;	
	private final String historyFileName;
	private ConsoleReader consoleReader;
	private String serverName;
	private CLICommandExecutor cliCommandExecutor;
	private int defaultPort;
	private String defaultHostName;
	private PersistentHistory history;
	
	public BaseCLI(String serverHome, String serverName, int defaultPort, String defaultHostName) {		
		this.serverHome = serverHome;
		this.defaultPort = defaultPort;
		this.defaultHostName = defaultHostName;
		this.historyFileName = serverHome+ File.separator + "system" + File.separator + "_cmdhistory.dat";
		this.serverName = serverName;
	}
	
	public CLIExitStatusCodes start(String[] cliArgs) {	
		
		//Get CLI Args
		String host = null;
		int port = -1;
		List<String> cliCommandList  = null;
		FileWriter outputFile = null;
		if(cliArgs != null && cliArgs.length > 0){
			try {
				CLIArguments  cliArguments = CLIArgumentsParser.parse(cliArgs);
				host = cliArguments.getHost();
				port = cliArguments.getPort();
				cliCommandList = cliArguments.getCommandList();
				outputFile = cliArguments.getOutputFileWriter();
			} catch (ParserException e) {
				System.out.println(e.getMessage());
				return CLIExitStatusCodes.ILLEGAL_CLI_ARGUMENT;
			}
		}
		//Build Connection
		establishConnection(host, port);
		//executeCommands
		return executeCommands(outputFile, cliCommandList);
	}

	private CLIExitStatusCodes executeCommands(FileWriter outputFile,	List<String> cliCommandList) {
		if(cliCommandList != null){
			return executeCLICommnads(outputFile, cliCommandList);
		}
		executeTerminalCommands();
		return CLIExitStatusCodes.EXECUTED_SUCCESSFULLY;
	}

	private void establishConnection(String host, int port){
		if(host == null || host.length() == 0){
			host = defaultHostName;
			if(host == null || host.trim().length() == 0){
				host = "localhost";
			}
		}
		if(port == -1){
			port = getPort();
		}
		jmxCommandExecutor = new JMXCommnadExecutor(host,port);		
	}

	private CLIExitStatusCodes executeCLICommnads(FileWriter outputFile, List<String> cliCommandList) {
		cliCommandExecutor = new CLICommandExecutor(this.serverName, jmxCommandExecutor, outputFile) {
			@Override
			protected void displayCommandResult(String result) {
				System.out.println(result);
			}
		};
		CLIExitStatusCodes exitStatus = cliCommandExecutor.execute(cliCommandList);
		cliCommandExecutor.closeOutputFile();
		return exitStatus;
	}
	
	private void executeTerminalCommands() {
		//Initializing Prompt for CLI
		initTerminal();
		cliCommandExecutor = new CLICommandExecutor(this.serverName, jmxCommandExecutor) {
			@Override
			protected void displayCommandResult(String result) {
				try {
					consoleReader.println(result);
					consoleReader.flush();
					history.flush();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		};
		startTerminal();
	}

	private int getPort(){
		int port = defaultPort;
		File systemFile = new File(serverHome + "/system/_sys.init");
		if (systemFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(systemFile));
				if(reader != null){
					port =  Integer.parseInt(reader.readLine().trim());
				}
			}catch (Exception e) {
			}finally{
				if (reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						System.out.println("unable to close file, Reason: "+ e.getMessage());
					}
				}
			}
		}
		return port;
	}
	
	private void initTerminal() {
		try {
			history = createHistory();
			consoleReader = new ConsoleReader();
			consoleReader.clearScreen();
			consoleReader.setHistory(history);
			
			String commandNames = jmxCommandExecutor.retrieveCommandNames();
			List<Completer> completorList = sorting(buildCompletor(commandNames));
			for(Completer completor : completorList){
				consoleReader.addCompleter(completor);
			}
			String[] hotkeyHelp = jmxCommandExecutor.retrieveHotkeyHelp();
			List<Completer[]> completorArray = new ArrayList<Completer[]>();
			for(int i = 0 ; i < hotkeyHelp.length ; i++){
				
				completorArray.addAll(buildCompletor(hotkeyHelp[i]));
			}
			completorList = sorting(completorArray);
			for(Completer completor : completorList){
				consoleReader.addCompleter(completor);
			}
		} catch (Exception e) {
		}finally{
//			Terminal.resetTerminal();
		}
		
	}

	private PersistentHistory createHistory() {
		File historyFile = new File(historyFileName);
		try {
			return new FileHistory(historyFile);
		} catch (IOException ex) {
			LogManager.getLogger().warn(MODULE, "Using in memory history as there was error in reading history file. Reason: " 
				+ ex.getMessage() + ". History of current session will not be presisted.");
			LogManager.getLogger().trace(ex);
			return new InMemoryPersistentHistoryAdapter();
		} catch (Exception ex) {
			LogManager.getLogger().warn(MODULE, "Error in reading history file. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(ex);
			
			if (markAsCorrupt(historyFile) == false) {
				LogManager.getLogger().warn(MODULE, "Using in memory history as there was error in taking backup of corrupt history file. "
						+ "History of current session will not be presisted.");
				return new InMemoryPersistentHistoryAdapter();
			}
			
			try {
				LogManager.getLogger().info(MODULE, "Creating new history file " + historyFileName);
				return new FileHistory(new File(historyFileName));
			} catch (Exception ex1) {
				LogManager.getLogger().warn(MODULE, "Using in memory history as there was error in creating history file. Reason: " 
						+ ex1.getMessage() + ". History of current session will not be presisted.");
					LogManager.getLogger().trace(ex1);
					return new InMemoryPersistentHistoryAdapter();
			}
		}
	}

	private boolean markAsCorrupt(File historyFile) {
		LogManager.getLogger().warn(MODULE, "Marking " + historyFileName + " as corrupt overwriting previous if any.");
		File corruptHistoryFile = new File(corruptHistoryFileName());
		deleteIfExists(corruptHistoryFile);
		
		return historyFile.renameTo(corruptHistoryFile);
	}

	private String corruptHistoryFileName() {
		return historyFileName + ".corrupt";
	}

	private void deleteIfExists(File corruptHistoryFile) {
		if (corruptHistoryFile.exists()) {
			corruptHistoryFile.delete(); // NOSONAR
		}
	}

	private void startTerminal() {
		displayWelcomeMessage();
		try{
			CLIExitStatusCodes status = CLIExitStatusCodes.QUIT;
			do{			
				String promptInput = consoleReader.readLine(getConsolePrompt());
				status = cliCommandExecutor.execute(promptInput);
			
			} while(status != CLIExitStatusCodes.QUIT);
			
		}catch(IOException e){
			System.out.println(e.getMessage());
		}finally{
			cliCommandExecutor.closeOutputFile();
//			Terminal.resetTerminal();
		}
	}

	protected String getConsolePrompt(){
		return "$ ";		
	}
	
	public String getServerDescription(){
		return "";
	}	
	private void displayWelcomeMessage(){		
		System.out.println();		
		System.out.println("************************************************");
		System.out.println("*  " + StringUtility.fillChar(serverName, 42 , ' ') +   " *");
        System.out.println("*                                              *");
		System.out.println("*  Sterlite Technologies Ltd.                  *");
		System.out.println("************************************************");
		System.out.println();
		System.out.println("Enter ? or help for list of commands supported.");
		System.out.println();

	}

	public static List<Completer> sorting(List<Completer[]> listCompletor){
		List<Completer> newCompletorList = new ArrayList<Completer>();
		//sorting
		for(int i = 0 ; i < listCompletor.size() ; i++){
			for(int j = 0 ; j < i ; j++){
				if(listCompletor.get(i).length < listCompletor.get(j).length){
					Completer[] completor = listCompletor.get(i);
					listCompletor.set(i, listCompletor.get(j));					
					listCompletor.set(j, completor);
				}
			}
		}
		//arranging
		
		for(Completer[] completor : listCompletor){
			Completer argCompletor = new ArgumentCompleter(completor);
			newCompletorList.add(argCompletor);
		}
		return newCompletorList;
	}
	
	public static List<Completer[]> buildCompletor(String inputString){
		List<Completer[]> listCompletor = new ArrayList<Completer[]>();
		inputString = inputString.replaceAll("\n","\\\\n");
		JSONObject commandJSONObj = JSONObject.fromObject(inputString);
		recursion(listCompletor, commandJSONObj);
		return listCompletor;
	}

	public static void recursion(List<Completer[]> listCompletor,JSONObject jsonObj,String... parentList){
		@SuppressWarnings("rawtypes")
		Iterator itr1 = jsonObj.keys();
		List<String> optionList = new ArrayList<String>();
		while(itr1.hasNext()){
			String key = (String) itr1.next();
			optionList.add(key);
			JSONObject childObj = jsonObj.getJSONObject(key);
			if(childObj.size() > 0){
				String[] newParentList = null;
				if(parentList != null && parentList.length <= 0){
					newParentList = new String[1];
				}else{
					newParentList = new String[parentList.length + 1];
					System.arraycopy(parentList, 0, newParentList, 0, parentList.length);
				}
				newParentList[newParentList.length-1] = key;
				
				recursion(listCompletor, childObj, newParentList);
			}				
		}
		if(optionList.size() > 0 ){
			
			Completer[] completorList = new Completer[parentList.length + 2];			
			if(parentList.length > 0){
				for(int i = 0 ; i < parentList.length ; i++ ){
					completorList[i] = new StringsCompleter(parentList[i]);
				}
			}			
			String[] optionArray = new String[optionList.size()];
			optionArray = optionList.toArray(optionArray);			
			completorList[completorList.length-2] = new StringsCompleter(optionArray);
			completorList[completorList.length-1] = new NullCompleter();
			listCompletor.add(completorList);
		}
	}
	public String getServerHome(){
		return serverHome;
	}
	
	private class InMemoryPersistentHistoryAdapter extends MemoryHistory implements PersistentHistory {
		
		@Override
		public void flush() throws IOException {
			// no op
}

		@Override
		public void purge() throws IOException {
			// no op
		}
	}
}
