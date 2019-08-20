package com.elitecore.core.servicex;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.base.BaseEliteService;
import com.elitecore.core.util.cli.cmd.ICommand;
import jline.console.history.FileHistory;
import jline.console.history.History;

import javax.management.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * Server administration service, can be used to expose mbean service configuration,
 * cache, CLI, etc. 
 *
 */
public class EliteAdminService extends BaseEliteService {
	
	private final static String MODULE = "ADMN-SER";
	private MBeanServer mbeanServer;
	private final Map<String,ICommand>cliCommandMap;
	private final ServerContext serverContext;
	private EliteAdminServiceContext eliteAdminserviceContext;
	private EliteCliController eliteCliController;
	
	public EliteAdminService(ServerContext serverContext) {
		super(serverContext);
		this.serverContext = serverContext;
		this.cliCommandMap = new HashMap<String,ICommand>();
		
		//registering CLI commands
		addCliCommand(new HelpCommand());		
		addCliCommand(new QuestionMarkCommand());
		addCliCommand(new HistoryCommand(serverContext.getServerHome()));

		mbeanServer = ManagementFactory.getPlatformMBeanServer();
		eliteCliController = new EliteCliController();
	}


	public String executeCliCommand(String sCommand, String parameters) throws Exception{
		return eliteCliController.executeCLICommand(sCommand, parameters);
	}
	
	
	/**
	 * Attempts to start ADMIN service.
	 */
	@Override
	protected boolean startService(){
		try {
			registerMbean(eliteCliController);
			return true;
		} catch (Exception e) {			
			LogManager.getLogger().error(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE,e);			
		}
		return false;
	}
	
	
	public void registerMbeans(List<BaseMBeanController> baseMBeanImplList) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException{
		if(baseMBeanImplList != null){
			int size = baseMBeanImplList.size();
			for(int i=0;i<size;i++){
				mbeanServer.registerMBean(baseMBeanImplList.get(i), new ObjectName(baseMBeanImplList.get(i).getName()));
			}		
		}				
	}
	
	public void registerMbean(BaseMBeanController baseMBeanImpl) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException{
		String name = baseMBeanImpl.getName();
		mbeanServer.registerMBean(baseMBeanImpl, new ObjectName(name));
	}
	
	public void registerStandardMbean(StandardMBean baseMBeanImpl, String name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException{
		mbeanServer.registerMBean(baseMBeanImpl, new ObjectName(name));
	}
	
	
	@Override
	public boolean stopService() {
		if (mbeanServer != null) {
			MBeanServerFactory.releaseMBeanServer(mbeanServer);
		}
		mbeanServer = null;
		return true;
	}
	
	@Override
	protected void shutdownService() {
		
	}
	
	public void addCliCommand(List<ICommand> commandList){
		if(commandList == null)
			return;
		
		for(ICommand command : commandList){
			if(command!=null)
				cliCommandMap.put(command.getCommandName(),command);
		}
	}
	
	
	public void addCliCommand(ICommand command){		
		cliCommandMap.put(command.getCommandName(),command);
	}
	
	
	public static interface EliteCliControllerMBean {
		public String executeCLICommand(String command, String parameters) throws Exception;
		public String[] retrieveHotkeyHelp() throws Exception;
		public String retrieveCommandNames() throws Exception;
	}
	
	
	public class EliteCliController extends BaseMBeanController implements EliteCliControllerMBean {
		@Override
		public String executeCLICommand(String sCommand, String parameters) throws Exception{
			String response = "";
			ICommand command = cliCommandMap.get(sCommand);
			if (command == null){
				response = "Unknown command: " + sCommand;
			} else {
				try{
					if(parameters != null){
						parameters = parameters.trim();
					}
					response = command.execute(parameters);
				}catch(Throwable ee){
					response = "Error in executing command: " + sCommand + " " + parameters;
					LogManager.getLogger().warn(MODULE, "Error in executing command: " + sCommand + " " + parameters +". Reason: " + ee.getMessage());
					LogManager.getLogger().trace(ee);
				}
			}
			return response;
		}
		
		@Override
		public String retrieveCommandNames() throws Exception{
			String commandNames = "{'";
			for(Entry<String,ICommand> entry : cliCommandMap.entrySet()){
				commandNames = commandNames + entry.getValue().getCommandName() + "':{},'";
			}			
			
			commandNames = commandNames.substring(0, commandNames.length()-2) + "}";
			return commandNames;

		}
		@Override
		public String[] retrieveHotkeyHelp() throws Exception{
			String[] help = new String[cliCommandMap.size()];
			
			int index = 0;
			for(Entry<String,ICommand> entry : cliCommandMap.entrySet()){
				help[index++] = entry.getValue().getHotkeyHelp();
			}			
			return help;
		}
		@Override
		public String getName() {
			return MBeanConstants.CLI;
		}
	}	
	
	
	/**
	 * 
	 * Returns service identifier, "INT-ADMN"
	 *  
	 */
    @Override
	public String getServiceIdentifier() {
		return "INT-ADMN";
    }
	
	
	/**
	 * 
	 * Help CLI Command, provides list of commands and its descriptions
	 * 
	 */	
	private class HelpCommand extends EliteBaseCommand{
		
		private static final String BLANK_SPACE = "                               ";
		
		@Override
		public String execute(String parameter) {			 
			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter(stringBuffer);			
			out.println("Commands:");
			out.println();		
			Iterator<String> iterator = cliCommandMap.keySet().iterator();
			while(iterator.hasNext()){
				String commandName = iterator.next();
				ICommand command = cliCommandMap.get(commandName);
				out.println(commandName + BLANK_SPACE.substring(commandName.length()) + command.getDescription());
			}
			out.println();
			return "\n------------------------------------------------ " +
			  	   "\n             " + EliteAdminService.this.serverContext.getServerName() + 
			  	   "\n------------------------------------------------\n" +			 
			  	   "\n" + "q or quit   Exit from CLI." +		
			  	   "\n" + stringBuffer.toString();			
		}

		@Override
		public String getCommandName() {
			return "help";
		}
		@Override
		public String getDescription() {
			return "Provides list of supported commands and its descriptions.";
		}		
		@Override
		public String getHotkeyHelp(){
			return "{'?':{}}";
		}
	}
	

	private class QuestionMarkCommand extends HelpCommand{
		@Override
		public String getCommandName() {		
			return "?";
		}
	}
	
	
	private class HistoryCommand extends EliteBaseCommand {
		private final static String HISTORY_DETAILS_ACRONYM    = "-d";
		private final static String HISTORY_HEAD_LIST_ACRONYM   = "-h";
		private final static String HISTORY_TAIL_LIST_ACRONYM   = "-t";
		private final static String HISTORY_DETAILS    = "-detail";
		private final static String HISTORY_HEAD_LIST  = "-head";
		private final static String HISTORY_TAIL_LIST  = "-tail";	
		
		private String serverHome = null;
		public HistoryCommand(String serverHome){
			this.serverHome = serverHome;
		
		}
		@Override
		public String execute(String parameter) {
			File historyFile = new File(serverHome+ File.separator + "system" + File.separator + "_cmdhistory.dat");
			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter(stringBuffer);
			History history = null;
			try {
				history = new FileHistory(historyFile);
			} catch (IOException e) {
				return "Problem with reading history file";
			}
			try {
						if(parameter!=null && parameter.length() > 0){
							parameter=parameter.trim();
							StringTokenizer stk = new StringTokenizer(parameter," ");
							int tokenSize = stk.countTokens();
							if(tokenSize > 3){
								return getHelp();
							}
							if(!stk.hasMoreElements())
								return getHelp();
							parameter = stk.nextToken();
							if(parameter.equals(HISTORY_HEAD_LIST) || parameter.equals(HISTORY_HEAD_LIST_ACRONYM)){
								int index = 1;
								try{
									if(!stk.hasMoreElements())
										return getHelp();
									index = Integer.parseInt(stk.nextToken());
									if(index > history.size())
										index = history.size();
									for(int i=0;i<index;i++){
										out.println("[ " + (i+1) + " ] " + history.get(i));
									}
									out.println();
									out.println("Displays previously executed commands.");
								}catch(Exception err){
									out.println();
									out.println("Use the syntax \"history -h <position>\"" ) ;
								}
							}else if(parameter.equals(HISTORY_TAIL_LIST) || parameter.equals(HISTORY_TAIL_LIST_ACRONYM)){
								int index = 0;
								try{
									if(!stk.hasMoreElements())
										return getHelp();

									index = Integer.parseInt(stk.nextToken());
									 
									int iHistoryLength = history.size(); 
									if(iHistoryLength - index < 0)
										index=0;
									else
										index=iHistoryLength - index -1;

									for(int i=index,k=1;i<iHistoryLength-1;i++,k++){
										out.println("[ " + (k) + " ] " + history.get(i));
									}

									out.println();
									out.println("Displays previously executed commands.");
								}catch(Exception err){
									out.println("Use the syntax \"history -t <position>\"" ) ;
								}
							}else if(parameter.equals(HISTORY_DETAILS)|| parameter.equals(HISTORY_DETAILS_ACRONYM)){
								ListIterator<jline.console.history.History.Entry> cmdHistory = 
									history.entries();
								int index = 1;
								while (cmdHistory.hasNext()) {
									jline.console.history.History.Entry entry = cmdHistory.next();
									out.println("[ " + index++ + " ] " + "[ " + 
											new Date(entry.timestamp()) + " ] " + entry.value());
								}
							}else {
								if("?".equals(parameter) || HELP_OPTION.equals(parameter)){
									out.println(getHelp()) ;
								}else{
									try{
									}catch(NumberFormatException e){
										out.println("For help type \"history ?\" or \"history -help\"" ) ;
									}
								}
							}
						}else{
							int numberOfExecutedCommand = history.size();

							int numberOfcommandsToDisplay = 10;

							int fromIndex;
							if (numberOfExecutedCommand - numberOfcommandsToDisplay <= 0) {
								fromIndex = 0;
							} else {
								fromIndex = numberOfExecutedCommand - numberOfcommandsToDisplay;
							}
							
							for (int startIndex = fromIndex, displayIndex =1; startIndex < numberOfExecutedCommand ; startIndex++,displayIndex++) {
								ListIterator<jline.console.history.History.Entry> entries = history.entries(startIndex);
								if (entries.hasNext()) {
									jline.console.history.History.Entry currentEntry = entries.next();
									
									out.println("[ " + displayIndex + " ] " + "[ " + 
											new Date(currentEntry.timestamp()) + " ] " + currentEntry.value());
								}
							}
							
						}
			} catch (Exception ioe) {
				out.println("Error opening command history file.");
			}			
			return stringBuffer.toString();
		}
		
		@Override
		public String getCommandName() {
			return "history";
		}
		
		@Override
		public String getDescription() {
			return "Displays the previously executed commands";
		}
		
		private String getHelp(){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : " + getCommandName() + " [<options>] [<position>]]");
			out.println("Description : " + getDescription());
			out.println();
			out.println(fillChar("where options include:", 30));		
			out.println("    " + fillChar("<position>", 30));
			out.println("    " + fillChar("", 5) +fillChar(" Display command for specified position.", 30));
			out.println("    " + fillChar("-d | -detail", 30));
			out.println("    " + fillChar("", 5) +fillChar(" Displays detailed history ", 30));
			out.println("    " + fillChar("-h | -head <position>", 30));
			out.println("    " + fillChar("", 5) +fillChar(" Displays the first part of executed commands till specified position", 30));
			out.println("    " + fillChar("-t <position>", 30));
			out.println("    " + fillChar("", 5) +fillChar(" Displays the last part of executed commands from specified position", 30));
			out.close();
			return stringWriter.toString();
		}
		
		@Override
		public String getHotkeyHelp() {
			return "{'history':{'-detail':{},'-head':{},'-tail':{},'-help':{}}}";
		}
	}

	@Override
	public String getKey() {
		return MODULE;
	}

	@Override
	public ServiceDescription getDescription() {
		return new ServiceDescription(getServiceIdentifier(), getStatus(), 
				"N.A.", getStartDate(), getRemarks());
	}
	
	@Override
	protected ServiceContext getServiceContext() {
		return eliteAdminserviceContext;
	}

	@Override
	protected void initService() throws ServiceInitializationException {
	}


	@Override
	public void readConfiguration() throws LoadConfigurationException {
	}

	@Override
	public String getServiceName() {
		return "Admin Service";
		
	}
}
