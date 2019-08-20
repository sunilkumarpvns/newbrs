package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.core.eap.session.EAPSessionId;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;

public class EapSessionDetailProvider extends DetailProvider{
	
	//possible params
	private static final String NAS_ID = "-nas_id";
	private static final String DEVICE_ID = "-device_id";
	private static final String SUB_ID = "-sub_id";
	private static final String COUNT = "-count";
	private static final String ALL = "-all";

	private AAAServerContext serverContext;
	public static final String MODULE = "EAP-SESSION";
	private HashMap<String ,DetailProvider> detailProviderMap;
	private static EapSessionDetailProvider eapSessionDetailProvider;

	private EapSessionDetailProvider(AAAServerContext serverContext) {
		this.serverContext = serverContext;
		detailProviderMap = new HashMap<String, DetailProvider>();
	}
	public static EapSessionDetailProvider getInstance(AAAServerContext serverContext){
		if (eapSessionDetailProvider == null ){
			synchronized (EapSessionDetailProvider.class) { //NOSONAR - Reason: Double-checked locking should not be used
				if (eapSessionDetailProvider == null){
					eapSessionDetailProvider = new EapSessionDetailProvider(serverContext);
				}
			}
		}
		return eapSessionDetailProvider;
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
			return showEapSessionsDetails();
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
				return showEapSessionsDetails();
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
					return countEAPSessions();
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

	@Override
	public String getKey() {
		return "eap";
	}

	private String countEAPSessions() {
		Map<String, IEapStateMachine> eapSessions = (serverContext).getEapSessionManager().getEapSessions();
		if (eapSessions != null && eapSessions.size() > 0) {
			return "Total Number of Active Eap Sessions : "+String.valueOf(eapSessions.size());
		}
		return "No Active Sessions Found.";
	}

	private String getTotalNoOfSubIdSessions(String strArg) {
		int counter = 0;
		Map<String, IEapStateMachine> eapSessions = (serverContext)
				.getEapSessionManager().getEapSessions();
		if (eapSessions != null && eapSessions.size() > 0) {
			for (String eapSession : eapSessions.keySet()) {
				EAPSessionId eapSessionId = EAPSessionId.valueOf(eapSession);
				if (eapSessionId == null) {
					continue;
				}
				
				if (strArg.equals(eapSessionId.getUserName())) {
					counter++;
				}
			}
		}
		if (counter == 0) {
			return "No Active Sessions Found for Sub Id : " + strArg;
		}
		return "Total Number Of Active Sessions For Sub Id : " + strArg
				+ " is " + counter;
	}

	private String getTotalNoOfNasIdSessions(String strArg) {
		int counter = 0;
		Map<String, IEapStateMachine> eapSessions = (serverContext)
				.getEapSessionManager().getEapSessions();
		if (eapSessions != null && eapSessions.size() > 0) {
			for (String eapSession : eapSessions.keySet()) {
				EAPSessionId eapSessionId = EAPSessionId.valueOf(eapSession);
				if (eapSessionId == null) {
					continue;
				}
				
				if (strArg.equals(eapSessionId.getNasIdendtifier())) {
					counter++;
				}
			}
		}
		if (counter == 0) {
			return "No Active Sessions Found for Nas Id  : " + strArg;
		}
		return "Total Number Of Active Sessions For Device Id : " + strArg+ " is " + counter;
	}

	private String getTotalNoOfDeviceIdSessions(String strArg) {
		int counter = 0;
		Map<String, IEapStateMachine> eapSessions = (serverContext)
				.getEapSessionManager().getEapSessions();
		if (eapSessions != null && eapSessions.size() > 0) {
			for (String eapSession : eapSessions.keySet()) {
				EAPSessionId eapSessionId = EAPSessionId.valueOf(eapSession);
				if (eapSessionId == null) {
					continue;
				}
				
				if (strArg.equals(eapSessionId.getCallingStationID())) {
					counter++;
				}
			}
		}
		if (counter == 0) {
			return "No Active Sessions Found for Device Id : " + strArg;
		}
		return "Total Number Of Active Sessions For Device Id : " + strArg+ " is " + counter;
	}

	private String showEapSessionsDetails() {
		Map<String, IEapStateMachine> eapSessions = (serverContext).getEapSessionManager().getEapSessions();
		StringBuilder eapSessionsStr = new StringBuilder();
		if (eapSessions != null && eapSessions.size() > 0) {
			for (String eapSession : eapSessions.keySet()) {
				EAPSessionId eapSessionId = EAPSessionId.valueOf(eapSession);
				if (eapSessionId == null) {
					continue;
				}
				eapSessionsStr.append(getStrEapSessionDetail(eapSessionId));
			}
			
			if (eapSessionsStr.length() != 0) {
				eapSessionsStr.insert(0,(StringUtility.fillChar("-", 50, '-') + "\n" + StringUtility.fillChar("", 13) 
						+ "EAP SESSION DETAILS\n" + StringUtility.fillChar("-", 50, '-')));
			}
		} else {
			return "No Active Sessions Found";
		}

		return eapSessionsStr.toString();
	}

	private String getDeviceIdSessionDetails(String strArg) {
		int sessionCount = 0;
		Map<String, IEapStateMachine> eapSessions = (serverContext)
				.getEapSessionManager().getEapSessions();
		StringBuilder eapSessionsStr = new StringBuilder();
		if (eapSessions != null && eapSessions.size() > 0) {
			eapSessionsStr.append(StringUtility.fillChar("-", 50, '-'));
			eapSessionsStr.append("\n" + StringUtility.fillChar("", 13)
					+ "EAP SESSION DETAILS\n");
			eapSessionsStr.append(StringUtility.fillChar("-", 50, '-'));
			for (String eapSession : eapSessions.keySet()) {
				EAPSessionId eapSessionId = EAPSessionId.valueOf(eapSession);
				if (eapSessionId == null) {
					continue;
				}
				
				if (strArg.equals(eapSessionId.getCallingStationID())) {
					eapSessionsStr.append(getStrEapSessionDetail(eapSessionId));
					sessionCount++;
				}
			}
			if (sessionCount == 0) {
				return "No Active Sessions Found for Device Id : " + strArg;
			}
		} else {
			return "No Active Sessions Found";
		}
		return eapSessionsStr.toString();
	}

	private String getNasIdSessionDetails(String strArg) {
		StringBuffer eapSessionsStr = new StringBuffer();
		int sessionCount = 0;
		Map<String, IEapStateMachine> eapSessions = (serverContext)
				.getEapSessionManager().getEapSessions();
		if (eapSessions != null && eapSessions.size() > 0) {
			eapSessionsStr.append(StringUtility.fillChar("-", 50, '-'));
			eapSessionsStr.append("\n" + StringUtility.fillChar("", 13)
					+ "EAP SESSION DETAILS\n");
			eapSessionsStr.append(StringUtility.fillChar("-", 50, '-'));
			
			for (String eapSession : eapSessions.keySet()) {
				EAPSessionId eapSessionId = EAPSessionId.valueOf(eapSession);
				if (eapSessionId == null) {
					continue;
				}
				
				if (strArg.equals(eapSessionId.getNasIdendtifier())) {
					eapSessionsStr.append(getStrEapSessionDetail(eapSessionId));
					sessionCount++;
				}
			}
			if (sessionCount == 0) {
				return "No Active Sessions Found for Nas Id : " + strArg;
			}
		} else {
			return "No Active Sessions Found";
		}
		return eapSessionsStr.toString();
	}

	private String getSubIdSessionDetails(String strArg) {
		StringBuffer eapSessionsStr = new StringBuffer();
		int sessionCount = 0;
		Map<String, IEapStateMachine> eapSessions = (serverContext)
				.getEapSessionManager().getEapSessions();
		if (eapSessions != null && eapSessions.size() > 0) {
			eapSessionsStr.append(StringUtility.fillChar("-", 50, '-'));
			eapSessionsStr.append("\n" + StringUtility.fillChar("", 13)
					+ "EAP SESSION DETAILS\n");
			eapSessionsStr.append(StringUtility.fillChar("-", 50, '-'));
			for (String eapSession : eapSessions.keySet()) {
				EAPSessionId eapSessionId = EAPSessionId.valueOf(eapSession);
				if (eapSessionId == null) {
					continue;
				}

				if (strArg.equals(eapSessionId.getUserName())) {
					eapSessionsStr.append(getStrEapSessionDetail(eapSessionId));
					sessionCount++;
				}
			}
			if (sessionCount == 0) {
				return "No Active Sessions Found for Sub Id : " + strArg ;
			}
		} else {
			return "No Active Sessions Found";
		}
		return eapSessionsStr.toString();
	}

	private String getStrEapSessionDetail(EAPSessionId eapSessionId) {

		StringBuilder eapSessionsStr = new StringBuilder();
		eapSessionsStr
				.append((eapSessionId.getCalledStationID() != null) ? (StringUtility
						.fillChar("\nCalledStationID ", 21)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getCalledStationID() + "\n")
						: "");
		eapSessionsStr
				.append((eapSessionId.getCallingStationID() != null) ? (StringUtility
						.fillChar("CallingStationID", 20)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getCallingStationID() + "\n")
						: "");
		eapSessionsStr
				.append((eapSessionId.getNasIdendtifier() != null) ? (StringUtility
						.fillChar("NasIdentifier", 20)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getNasIdendtifier() + "\n")
						: "");
		eapSessionsStr
				.append((eapSessionId.getNasIPAddress() != null) ? (StringUtility
						.fillChar("NasIpAddress", 20)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getNasIPAddress() + "\n")
						: "");
		eapSessionsStr
				.append((eapSessionId.getNasPort() != null) ? (StringUtility
						.fillChar("NasPort", 20)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getNasPort() + "\n") : "");
		eapSessionsStr
				.append((eapSessionId.getNasPortID() != null) ? (StringUtility
						.fillChar("NasPortID", 20)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getNasPortID() + "\n") : "");
		eapSessionsStr
				.append((eapSessionId.getOriginatingLineInfo() != null) ? (StringUtility
						.fillChar("OriginatingLineInfo", 20)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getOriginatingLineInfo() + "\n")
						: "");
		eapSessionsStr
				.append((eapSessionId.getUserName() != null) ? (StringUtility
						.fillChar("UserName", 20)
						+ StringUtility.fillChar(":", 5)
						+ eapSessionId.getUserName() + "\n") : "");
		eapSessionsStr.append(StringUtility.fillChar("-", 50, '-') + "");
		return eapSessionsStr.toString();
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println("Usage :show session eap");
		out.println();
		out.println("*** Commands used only for RADIUS EAP session related information.");
		out.println(StringUtility.fillChar("-", 150, '-'));
		out.println("Possible Options  :");
		out.println("  -all                                                Display all available Active eap Sessions details");
		out.println("  -all        {-count}                                Display total numbers of Active eap Sessions");
		out.println("  -device_id  {device_id}                             Display Session details of given eap Calling-Station-ID/Device-Id");
		out.println("  -device_id  -count            {device_id}           Display total numbers of sessions for eap given device_Id");
		out.println("  -sub_id     {Subscriber_id}                         Display Session details of given eap Subscriber-Id");
		out.println("  -sub_id     -count            {Subscriber_id}       Display total numbers of sessions for given Subscriber-Id");
		out.println("  -nas_id     {Nas-Identifier}                        Display Session details of given Nas-Identifier");
		out.println("  -nas_id     -count            {Nas-Identifier}      Display total numbers of sessions for given Nas-Identifier");
		out.println(StringUtility.fillChar("-", 150, '-'));
		out.close();
		return stringWriter.toString();
	}
}
