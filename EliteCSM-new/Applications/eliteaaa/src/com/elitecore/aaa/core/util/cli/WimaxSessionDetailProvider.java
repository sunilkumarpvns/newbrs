package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.wimax.WimaxSessionData;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;



public class WimaxSessionDetailProvider extends DetailProvider{
	
	//possible params
	private static final String NAS_ID = "-nas_id";
	private static final String DEVICE_ID = "-device_id";
	private static final String SUB_ID = "-sub_id";
	private static final String COUNT = "-count";
	private static final String ALL = "-all";

	
	private AAAServerContext serverContext;
	public static final String MODULE = "WiMAX-SESSION";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static  WimaxSessionDetailProvider wimaxSessionDetailProvider;

	private WimaxSessionDetailProvider(AAAServerContext serverContext) {
		this.serverContext = serverContext;
		detailProviderMap = new HashMap<String, DetailProvider>();
	}

	public static WimaxSessionDetailProvider getInstance(
			AAAServerContext serverContext) {
		if (wimaxSessionDetailProvider == null) {
			synchronized (WimaxSessionDetailProvider.class) { //NOSONAR - Reason: Double-checked locking should not be used
				if (wimaxSessionDetailProvider == null) {
					wimaxSessionDetailProvider = new WimaxSessionDetailProvider(
							serverContext);
				}
			}
		}
		return wimaxSessionDetailProvider;
	}

	public  HashMap<String ,DetailProvider> getDetailProviderMap(){
		return detailProviderMap;
	}

	@Override
	public String execute(String[] parameters) {

		int numberOfArgs = parameters.length;
		for (int i = 0; i < numberOfArgs; i++) {
			parameters[i] = parameters[i].trim();
		}

		if (numberOfArgs == 0) {
			return getWimaxSessionsDetails();
		}

		if (parameters[0].equals(NAS_ID)) {

			if (numberOfArgs == 1) {
				return getHelpMsg();
			}

			if (numberOfArgs == 2) {
				if (parameters[1].equals(COUNT) || parameters[1].equals("?")
						|| parameters[1].equals("-help")) {
					return getHelpMsg();
				}

				return getNasIdSessionDetails(parameters[1]);
			}

			if (parameters[1].equals(COUNT)) {
				if (numberOfArgs >= 4) {
					return "Invalid Option\n" + getHelpMsg();
				}
				return getTotalNoOfNasIdSessions(parameters[2]);
			} else {
				if (numberOfArgs >= 3) {
					return "Invalid Option\n" + getHelpMsg();
				}
				return getNasIdSessionDetails(parameters[1]);
			}

		} else if (parameters[0].equals(SUB_ID)) {

			if (numberOfArgs == 1) {
				return getHelpMsg();
			}

			if (numberOfArgs == 2) {
				if (parameters[1].equals(COUNT) || parameters[1].equals("?")
						|| parameters[1].equals("-help")) {
					return getHelpMsg();
				}

				return getSubIdSessionDetails(parameters[1]);
			}

			if (parameters[1].equals(COUNT)) {
				if (numberOfArgs >= 4) {
					return "Invalid Option\n" + getHelpMsg();
				}
				return getTotalNoOfSubIdSessions(parameters[2]);
			} else {
				if (numberOfArgs >= 3) {
					return "Invalid Option\n" + getHelpMsg();
				}
				return getSubIdSessionDetails(parameters[1]);
			}

		} else if (parameters[0].equals(DEVICE_ID)) {
			if (numberOfArgs == 1) {
				return getHelpMsg();
			}

			if (numberOfArgs == 2) {
				if (parameters[1].equals(COUNT) || parameters[1].equals("?")
						|| parameters[1].equals("-help")) {
					return getHelpMsg();
				}

				return getDeviceIdSessionDetails(parameters[1]);
			}

			if (parameters[1].equals(COUNT)) {
				if (numberOfArgs >= 4) {
					return "Invalid Option\n" + getHelpMsg();
				}
				return getTotalNoOfDeviceIdSessions(parameters[2]);
			} else {
				if (numberOfArgs >= 3) {
					return "Invalid Option\n" + getHelpMsg();
				}
				return getDeviceIdSessionDetails(parameters[1]);
			}
		} else if (parameters[0].equals(ALL)) {
			if (numberOfArgs == 1) {
				return getWimaxSessionsDetails();
			} else {
				if (parameters[1].equals(COUNT)) {
					if (numberOfArgs > 2) {
						if (parameters[2].equals("?")
								|| parameters[2].equals("-help")) {
							return getHelpMsg();
						} else {
							return "Invalid Option\n" + getHelpMsg();
						}
					}
					return countWimaxSessions();
				} else if (parameters[1].equals("?")
						|| parameters[1].equals("-help")) {
					return getHelpMsg();
				} else {
					return "Invalid Option\n" + getHelpMsg();
				}
			}
		} else if (parameters[0].equals("?") || parameters[0].equals("-help")) {
			return getHelpMsg();
		} else {
			return "Invalid Option\n" + getHelpMsg();
		}
	}

	private String getWimaxSessionsDetails() {
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		StringBuilder activeSessionsStr = new StringBuilder();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			activeSessionsStr.append("\n"
					+ StringUtility.fillChar("-", 180, '-'));
			activeSessionsStr.append("\n" + StringUtility.fillChar("", 13)
					+ "WIMAX SESSION DETAILS\n");
			activeSessionsStr.append(StringUtility.fillChar("-", 180, '-'));
			for (WimaxSessionData wimaxSession : wimaxSessions.values()) {
				activeSessionsStr.append(wimaxSession);
				activeSessionsStr.append(StringUtility.fillChar("-", 180, '-'));
			}
		} else {
			return "No active sessions found.";
		}
		return activeSessionsStr.toString();
	}

	private String getSubIdSessionDetails(String strArg) {
		int sessionCount = 0;
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		StringBuilder activeSessionsStr = new StringBuilder();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			activeSessionsStr.append("\n"
					+ StringUtility.fillChar("-", 180, '-'));
			activeSessionsStr.append("\n" + StringUtility.fillChar("", 13)
					+ "WIMAX SESSION DETAILS\n");
			activeSessionsStr.append(StringUtility.fillChar("-", 180, '-'));
			for (WimaxSessionData wimaxSession : wimaxSessions.values()) {
				if (strArg.equals(wimaxSession.getUsername())) {
					activeSessionsStr.append(wimaxSession);
					activeSessionsStr.append(StringUtility.fillChar("-", 180,
							'-'));
					sessionCount++;
				}
			}
			if (sessionCount == 0) {
				return "No active sessions found for Sub Id : " + strArg;
			}
		} else {
			return "No active sessions found.";
		}
		return activeSessionsStr.toString();
	}

	private String getNasIdSessionDetails(String strArg) {
		int sessionCount = 0;
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		StringBuilder activeSessionsStr = new StringBuilder();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			activeSessionsStr.append("\n"
					+ StringUtility.fillChar("-", 180, '-'));
			activeSessionsStr.append("\n" + StringUtility.fillChar("", 13)
					+ "WIMAX SESSION DETAILS\n");
			activeSessionsStr.append(StringUtility.fillChar("-", 180, '-'));
			for (WimaxSessionData wimaxSession : wimaxSessions.values()) {
				if (strArg.equals(wimaxSession.getNasIdentifier())) {
					activeSessionsStr.append(wimaxSession);
					activeSessionsStr.append(StringUtility.fillChar("-", 180,
							'-'));
					sessionCount++;
				}
			}
			if (sessionCount == 0) {
				return "No active sessions found for Nas Id : " + strArg;
			}
		} else {
			return "No active sessions found.";
		}
		return activeSessionsStr.toString();
	}

	private String getDeviceIdSessionDetails(String strArg) {
		int sessionCount = 0;
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		StringBuilder activeSessionsStr = new StringBuilder();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			activeSessionsStr.append("\n"
					+ StringUtility.fillChar("-", 180, '-'));
			activeSessionsStr.append("\n" + StringUtility.fillChar("", 13)
					+ "WIMAX SESSION DETAILS\n");
			activeSessionsStr.append(StringUtility.fillChar("-", 180, '-'));
			for (WimaxSessionData wimaxSession : wimaxSessions.values()) {
				if (strArg.equals(wimaxSession.getCallingStationId())) {
					activeSessionsStr.append(wimaxSession);
					activeSessionsStr.append(StringUtility.fillChar("-", 180,
							'-'));
					sessionCount++;
				}
			}
			if (sessionCount == 0) {
				return "No active sessions found for Device Id : " + strArg;
			}

		} else {
			return "No active sessions found.";
		}
		return activeSessionsStr.toString();
	}

	private String countWimaxSessions() {
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			return "Total Active Wimax Sessions : "+ String.valueOf(wimaxSessions.size());
		} else {
			return "No Active Sessions Found.";
		}
	}

	private String getTotalNoOfDeviceIdSessions(String strArg) {
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			int counter = 0;
			for (WimaxSessionData wimaxSession : wimaxSessions.values()) {
				if (strArg.equals(wimaxSession.getCallingStationId())) {
					counter++;
				}
			}
			if (counter > 0) {
				return "Total number of active wimax sessions for Device Id : "+ strArg + " is " + counter;
			} else {
				return "No active sessions found for Device Id : " + strArg;
			}
		} else {
			return "No active sessions found";
		}
	}

	private String getTotalNoOfNasIdSessions(String strArg) {
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			int counter = 0;
			for (WimaxSessionData wimaxSession : wimaxSessions.values()) {
				if (strArg.equals(wimaxSession.getNasIdentifier()))
				{
					counter++;
				}
			}
			if (counter > 0) {
				return "Total number of active wimax sessions for Nas Id : "+ strArg + " is " + counter;
			} else {
				return "No active sessions found for Nas Id : " + strArg;
			}
		} else {
			return "No active sessions found";
		}
	}

	private String getTotalNoOfSubIdSessions(String strArg) {
		Map<String, WimaxSessionData> wimaxSessions = serverContext
				.getWimaxSessionManager().getActiveWimaxSessions();
		if (wimaxSessions != null && wimaxSessions.size() > 0) {
			int counter = 0;
			for (WimaxSessionData wimaxSession : wimaxSessions.values()) {
				if (strArg.equals(wimaxSession.getUsername())) {
					counter++;
				}
			}
			if (counter > 0) {
				return "Total number of active wimax sessions for Sub Id  : "+ strArg + " is " + counter;
			} else {
				return "No active sessions found for Sub Id  : " + strArg;
			}
		} else {
			return "No active sessions found";
		}
	}

	@Override
	public String getKey() {
		return "wimax";
	}
	
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println("Usage :show session wimax");
		out.println(StringUtility.fillChar("-", 150, '-'));
		out.println("Possible Options  :");
		out.println("  -all                                                Display all available Active wimax Sessions details");
		out.println("  -all        {-count}                                Display total numbers of Active wimax Sessions");
		out.println("  -device_id  {device_id}                             Display Session details of given wimax Calling-Station-ID/Device-Id");
		out.println("  -device_id  {-count}          {device_id}           Display total numbers of sessions for wimax given device_id");
		out.println("  -sub_id     {Subscriber_id}                         Display Session details of given wimax Subscriber-id");
		out.println("  -device_id  {-count}          {Subscriber_id}       Display total numbers of sessions for given Subscriber-id");
		out.println("  -nas_id     {Nas-Identifier}                        Display Session details of given Nas-Identifier");
		out.println("  -nas_id     {-count}          {Nas-Identifier}      Display total numbers of sessions for given Nas-Identifier");
		out.println(StringUtility.fillChar("-", 150, '-'));
		out.close();
		return stringWriter.toString();
	}



}
