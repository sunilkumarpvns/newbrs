package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;

/* Session Manager Command which will help to monitor session mgr. 
 * */
public class SessionMgrCommand extends EliteBaseCommand {
	AAAServerContext serverContext;
	private final static String LIST = "-list";
	private final static String FLUSH = "-f";
	private final static String VIEW = "-v";
	private final static String HELP = "-help";
	private final static String VIEWALL = "-all";
	private final static String SUMMARY = "-s";
	private Map<String, SessionManagerData> allSessionMngrConf;


	protected static final String ALIVE = "ALIVE";
	protected static final String DEAD = "DEAD";
	
	public SessionMgrCommand(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public String execute(String parameter) {
		String response = "";
		StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
		String commandParameters = null;
		if (tokenizer.hasMoreTokens()) {
			parameter = tokenizer.nextToken().trim();
			if (tokenizer.hasMoreTokens()) {
				commandParameters = tokenizer.nextToken().trim();
				if (tokenizer.hasMoreTokens()) {
					return "\nInvalid Option." + getHelp();
				}
			}
		}
		// retrieve the map of all the session managers configuration
		allSessionMngrConf = serverContext.getServerConfiguration()
				.getSessionManagerConfiguration().getSmConfigurationMap();
		// retrieve the map of active session managers configuration
		Map<String, ConcurrencySessionManager> sessionMgrMap = serverContext
				.getLocalSessionManagerMap();
		if (allSessionMngrConf != null) {
			if (parameter.length() > 0) {
				if (parameter.equals(LIST)) {
					if (commandParameters != null) {
						if (commandParameters.equals("?")) {
							response = getListOptionHelp();
						} else if(CSV.equals(commandParameters)){
							TableFormatter formatter = getTableFormatter(TableFormatter.CSV, 
									TableFormatter.COLUMN_SEPARATOR_COMMA);
							response = getSessionManagerList(formatter, sessionMgrMap,
									allSessionMngrConf);
						}else{
							response = "\nInvalid Option." + getListHelp();
						}
					} else {
						TableFormatter formatter = getTableFormatter(TableFormatter.ONLY_HEADER_LINE, 
								TableFormatter.COLUMN_SEPARATOR_PIPE);
						response = "\n" + getSessionManagerList(formatter, sessionMgrMap,
								allSessionMngrConf);
					}
				} else if (parameter.equals(VIEW)) {
					if (commandParameters != null) {
						if (commandParameters.startsWith("-")) {
							if (commandParameters.equalsIgnoreCase(VIEWALL)) {
								response = getAllSessionMgrConfig(allSessionMngrConf);
							} else {
								response = "\nInvalid Option." + getViewHelp();
							}
						} else if (commandParameters.equals("?")) {
							response = getViewOptionHelp();
						} else {
							response = getSessionMgrConfig(commandParameters,
									allSessionMngrConf);
						}
					} else {
						response = getViewHelp();
					}
				} else if (parameter.equals(FLUSH)) {
					if (sessionMgrMap != null) {
						if (commandParameters != null) {
							if (commandParameters.startsWith("-")) {
								response = "\nInvalid Option." + getFlushHelp();
							} else if (commandParameters.equals("?")) {
								response = getFlushOptionHelp();
							} else {
								response = flushSessionManager(
										commandParameters, sessionMgrMap);
							}
						} else {
							response = getFlushHelp();
						}
					} else {
						response = "\nNo Active Session Managers are available";
					}
				} else if(SUMMARY.equals(parameter)) {
					if (commandParameters != null) {
						if ("?".equals(commandParameters) || HELP.equalsIgnoreCase(commandParameters)) {
							response = getSessionManagerStatisticsHelp();
						} else {
							response = getSessionManagerStatsSummary(commandParameters,sessionMgrMap);
						}
					} else {
						response = "Invalid number of arguments specified."+getSessionManagerStatisticsHelp();
					}
				} else if (parameter.equals(HELP)) {
					if (commandParameters != null) {
						response = "\nInvalid Option." + getHelp();
					}
					response = getHelp();
				} else if (parameter.equals("?")) {
					response = getHelp();
				} else {
					response = "\nInvalid Option." + getHelp();
				}
			} else {
				response = getHelp();
			}
		} else {
			response = "\nNo Session Managers are available";
		}
		return response;
	}

	// List all the Session managers with respective Service Policy and existing
	// Status
	protected String getSessionManagerList(TableFormatter formatter,
			Map<String, ConcurrencySessionManager> sessionMgrMap,
			Map<String, SessionManagerData> allSessionMngrConf) {
		if(formatter == null){
			return "";
		}
		Set<String> allSessMgrSet = null;
		Set<String> activeSet;
		for (ConcurrencySessionManager value : sessionMgrMap.values()) {
			formatter.addRecord(new String[]{
					String.valueOf(value.getSmInstanceId()), 
					value.getSmInstanceName(), 
					value.getSMType(),
					"L".equals(value.getSMType()) ? getStatus(value.isAlive()) : "NA", 
					searchSessionMgrInPolicies(serverContext.getServerConfiguration().getRadiusServicePolicyConfiguration().getRadiusPoliciesData(), value.getSmInstanceId())});
		}
		// key set for active session managers
		activeSet = sessionMgrMap.keySet();
		allSessMgrSet = new HashSet<String>(allSessionMngrConf.keySet());
		// key set for not initialized session managers
		allSessMgrSet.removeAll(activeSet);
		for (String key : allSessMgrSet) {
			SessionManagerData config = allSessionMngrConf.get(key);
			formatter.addRecord(new String[]{
					String.valueOf(config.getInstanceId()), 
					config.getInstanceName(), 
					config.getType(), 
					"NI", 
					searchSessionMgrInPolicies(serverContext.getServerConfiguration().getRadiusServicePolicyConfiguration().getRadiusPoliciesData(), key)});
		}

		formatter.addNewLine();
		formatter.add("L-LOCAL");
		formatter.addNewLine();
		formatter.add("A-ALIVE");
		formatter.addNewLine();
		formatter.add("D-DEAD");
		formatter.addNewLine();
		formatter.add("NI-NOT INITIALIZED");
		formatter.addNewLine();
		formatter.add("NA-NOT APPLICABLE");
		formatter.addNewLine();
		return formatter.getFormattedValues();
	}

	protected TableFormatter getTableFormatter(int format, String columnSeparator) {
		String[] header = {"ID", "NAME", "TYPE", "STATUS", "POLICIES"};
		int [] width = {4, 30, 5, 7, 35};
		int [] alignment = {TableFormatter.LEFT, TableFormatter.LEFT, 
				TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.LEFT};
		return new TableFormatter(header, width, alignment, format, columnSeparator);
	}
	
	/**
	 * added to check the status of ConcurrencySessionManager 
	 * based on the DB Connectivity. 
	 * @param isAlive 
	 * @return status string
	 */
	protected String getStatus(boolean isAlive) {
		return isAlive == true ? ALIVE : DEAD;
	}
	
	// search the session manager in Acct and Auth policies and if found
	// retrieve the policy names list for that session manager
	private String searchSessionMgrInPolicies(
			List<RadiusServicePolicyData> radiusServicePolicies,
			String smInstanceID) {
		List<String> policies = new ArrayList<String>();
		if (radiusServicePolicies != null) {
			for (RadiusServicePolicyData servicePolicy : radiusServicePolicies) {
				if (servicePolicy.getSessionManagerId().equals(smInstanceID)) {
					policies.add(servicePolicy.getName());
				}
			}
		}
		if (policies.size() > 0) {

			return StringUtility.getDelimitirSeparatedString(policies, ",");
		}
		return "Not binded";
	}

	// retrieve Existing Configuration of all the Session Manager
	private String getAllSessionMgrConfig(
			Map<String, SessionManagerData> allSessionMngrConf) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		for (SessionManagerData value : allSessionMngrConf.values()) {
			out.println(value);
		}
		out.flush();
		out.close();
		return stringWriter.toString();
	}

	// retrieve Existing Configuration of Session Manager of given name
	private String getSessionMgrConfig(String parameter,
			Map<String, SessionManagerData> allSessionMngrConf) {
		String response = "\nSession Manager Not Found of a given name, either not Initialized OR Invalid Name ";
		for (SessionManagerData value : allSessionMngrConf.values()) {
			if (value.getInstanceName().equals(parameter)) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter out = new PrintWriter(stringWriter);
				out.println(value);
				out.flush();
				response = stringWriter.toString();
			}
		}
		return response;
	}

	// Forcefully flush all records of Session Manager and Queue - if Batch
	// Update Enabled
	private String flushSessionManager(String parameter,
			Map<String, ConcurrencySessionManager> sessionMgrMap) {
		String response = "\nSession Manager not found of a given name,"
				+ " either not Initialized OR Invalid Name";
		for (ConcurrencySessionManager value : sessionMgrMap.values()) {
			if(value.getSmInstanceName().equals(parameter) == false) {
				continue;
			}
			
			Optional<ConcurrencySessionManager> sessionManager = 
				serverContext.getLocalSessionManager(value.getSmInstanceId());
			
			if(sessionManager.isPresent() == false) {
				response = "\nSession Manager with name: " + parameter + " not found";
				break;
			}

			if (sessionManager.get().getSMType().equals("R")) {
				response = "\nCan not Flush the sessions because Session Manager is Remote Session Manager";
			} else {
				int result = sessionManager.get().flushAllSessions();
				if (result < 0) {
					response = "\nError in flushing sessions for Session Manager"
						+ value.getSmInstanceName();
				} else {
					response = "\nSuccessfully flushed sessions for the Session Manager : "
						+ value.getSmInstanceName();
				}
			}
			break;
		}
		return response;
	}

	@Override
	public String getCommandName() {
		return "sessionmgr";
	}

	@Override
	public String getDescription() {
		return "Lists configuration of all the session managers initialized successfully and flushes sessions.";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'sessionmgr':{'-list':{'"+CSV+"':{}},'-help':{},'-v':{'-all':{},"
				+ getSessionManagerViewHelpFormat() + "},'-f':{"
				+ getSessionManagerFlushHelpFormat() + "},'-s':{} }}";
	}

	private String getSessionManagerViewHelpFormat() {
		String option = "";
		int size = 0;
		Map<String, SessionManagerData> sessionMgrMap = serverContext
				.getServerConfiguration().getSessionManagerConfiguration()
				.getSmConfigurationMap();
		if (sessionMgrMap != null) {
			int mapSize = (sessionMgrMap.size() - 1);
			for (SessionManagerData value : sessionMgrMap.values()) {
				option = option + "'" + value.getInstanceName() + "':{}";
				if (size < mapSize) {
					option += ",";
				}
				size++;
			}
		}
		return option;
	}

	private String getSessionManagerFlushHelpFormat() {
		String option = "";
		int size = 0;
		Map<String, ConcurrencySessionManager> sessionMgrMap = serverContext
				.getLocalSessionManagerMap();
		if (sessionMgrMap != null) {
			int mapSize = (sessionMgrMap.size() - 1);
			for (ConcurrencySessionManager value : sessionMgrMap.values()) {
				option = option + "'" + value.getSmInstanceName() + "':{}";
				if (size < mapSize) {
					option += ",";
				}
				size++;
			}
		}
		return option;
	}

	public String getHelp() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] = { LIST, VIEW, FLUSH ,SUMMARY};
		String paramDesc[] = {
				"Display the List of Session Manager and existing status",
				"Displays Existing Configuration of Session Manager ",
				"Forcefully flush all records of Session Manager and Queue - if Batch Update Enabled" ,
				"Display Session Manager Statistics"};
		
		out.println("\nUsage : sessionmgr <options>");
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.println("Possible options : \n");
		for (int i = 0; i < paramDesc.length; i++) {
			out.println("    " + fillChar(paramName[i], 7) + paramDesc[i]);
		}
		out.flush();
		out.close();
		return stringWriter.toString();
	}

	protected String getListOptionHelp() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		out.println("\nUsage : sessionmgr  -list ["+CSV+"]");
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.println("Description :");
		out.println();
		out.println("	Display the List of Session Manager with respective Type, Status and its Services Policy Information");
		out.println("    Displays CSV formatted output with "+CSV+" option");
		out.flush();
		out.close();
		return stringWriter.toString();
	}

	private String getViewOptionHelp() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		String paramName[] = { "<-all>", "<session-manager-name>" };
		String paramDesc[] = {
				"Display the configuration of all the session managers ",
				"Display the configuration of session manager of given name " };
		out.println("\nUsage : sessionmgr  -v [<-all> | <session-manager-name>]");
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.println("Possible options : \n");
		for (int i = 0; i < paramDesc.length; i++) {
			out.println("    " + fillChar(paramName[i], 24) + paramDesc[i]);
		}
		out.flush();
		out.close();
		return stringWriter.toString();
	}

	private String getFlushOptionHelp() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		out.println("\nUsage : sessionmgr  -f <session-manager-name>");
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.println("Possible options : \n");
		out.println(fillChar("	<session-manager-name>", 24)
				+ "Flush the Sessions for that session manager and additionally Queue - if Batch Update is enabled ");
		out.flush();
		out.close();
		return stringWriter.toString();
	}

	private String getListHelp() {
		return "\nUsage : sessionmgr  -list ["+CSV+"] ";
	}

	private String getViewHelp() {
		return "\nUsage : sessionmgr  -v [<-all> | <session-manager-name>]";
	}

	private String getFlushHelp() {
		return "\nUsage : sessionmgr  -f <session-manager-name>";
	}

	private String getSessionManagerStatsSummary(String sessionManagerName,Map<String, ConcurrencySessionManager> sessionMgrMap) {
		
		String response = "";
		ConcurrencySessionManager sessionManager = null;
		if(sessionMgrMap != null && sessionMgrMap.size() > 0) {
			for (ConcurrencySessionManager csm : sessionMgrMap.values()) {
				if(csm.getSmInstanceName().equals(sessionManagerName)) {
					sessionManager = csm;
					break;
				}
			}
		}
		
		if(sessionManager == null) {
			response = "Session Manager: "+sessionManagerName+" was not found,"
					+ " either not Initialized OR Invalid Name";
		} else {
			response = sessionManager.toString();
		}
		return response;
	}

	private String getSessionManagerStatisticsHelp() {
		return "\nUsage : sessionmgr -s <session-manager-name>";
	}
}
