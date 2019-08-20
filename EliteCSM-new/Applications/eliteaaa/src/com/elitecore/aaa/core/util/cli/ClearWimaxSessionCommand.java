package com.elitecore.aaa.core.util.cli;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.wimax.WimaxSessionData;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import static com.elitecore.aaa.core.wimax.WimaxSessionManager.*;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;

/**
 * 
 * @author narendra.pathai
 *
 */
public class ClearWimaxSessionCommand extends DetailProvider {
	private static final String KEY = "wimax";
	
	private static final String CUI_PARAM = "-cui";
	private static final String CALLING_STATION_ID_PARAM = "-calling_station_id";
	private static final String USERNAME_PARAM = "-username";
	private static final String AAA_ID_PARAM = "-aaa_id";
	private static final String NAS_IP_PARAM = "-nas_ip";
	private static final String NAS_ID_PARAM = "-nas_id";
	
	private HashMap<String, DetailProvider> detailProviderMap;
	@Nonnull private final WimaxSessionManager wimaxSessionManager;
	
	public ClearWimaxSessionCommand(@Nonnull WimaxSessionManager wimaxSessionManager) {
		this.wimaxSessionManager = checkNotNull(wimaxSessionManager, "wimaxSessionManager is null");
		this.detailProviderMap = new HashMap<String, DetailProvider>(0);
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters.length != 2) {
			return getHelpMsg();
		}
		
		StringBuilder out = new StringBuilder();
		if (AAA_ID_PARAM.equalsIgnoreCase(parameters[0])) {
			WimaxSessionData wimaxSession = wimaxSessionManager.removeSessionByAAASessionID(parameters[1]);
			if (wimaxSession != null) {
				out.append("WiMAX session with AAA Session ID: " + parameters[1] + " removed.\n");
				out.append(prettyPrint(wimaxSession));
			} else {
				out.append("WiMAX session with AAA Session ID: " + parameters[1] + " not found.\n");
			}
		} else if (CALLING_STATION_ID_PARAM.equalsIgnoreCase(parameters[0])) {
			WimaxSessionData wimaxSession = wimaxSessionManager.removeSessionByCallingStationID(parameters[1]);
			if (wimaxSession != null) {
				out.append("WiMAX session with Calling Station ID: " + parameters[1] + " removed.\n");
				out.append(prettyPrint(wimaxSession));
			} else {
				out.append("WiMAX session with Calling Station ID: " + parameters[1] + " not found.");
			}
		} else if (USERNAME_PARAM.equalsIgnoreCase(parameters[0])) {
			WimaxSessionData wimaxSession = wimaxSessionManager.removeSessionByUsername(parameters[1]);
			if (wimaxSession != null) {
				out.append("WiMAX session with Username: " + parameters[1] + " removed.\n");
				out.append(prettyPrint(wimaxSession));
			} else {
				out.append("WiMAX session with Username: " + parameters[1] + " not found.");
			}
		} else if (CUI_PARAM.equalsIgnoreCase(parameters[0])) {
			WimaxSessionData wimaxSession = wimaxSessionManager.removeSessionByCUI(parameters[1]);
			if (wimaxSession != null) {
				out.append("WiMAX session with CUI: " + parameters[1] + " removed.\n");
				out.append(prettyPrint(wimaxSession));
			} else {
				out.append("WiMAX session with CUI: " + parameters[1] + " not found.");
			}
		} else if (NAS_IP_PARAM.equalsIgnoreCase(parameters[0])) {
			int count = wimaxSessionManager.removeSessions(havingNasIPAddress(parameters[1]));
			if (count > 0) {
				out.append(count + " WiMAX session(s) removed with NAS IP Address: " + parameters[1] + "\n");
			} else {
				out.append("No WiMAX session with NAS IP Address: " + parameters[1] + " found.");
			}
		} else if (NAS_ID_PARAM.equalsIgnoreCase(parameters[0])) {
			int count = wimaxSessionManager.removeSessions(havingNasIdentifier(parameters[1]));
			if (count > 0) {
				out.append(count + " WiMAX session(s) removed with NAS Identifier: " + parameters[1] + "\n");
			} else {
				out.append("No WiMAX session with NAS Identifier: " + parameters[1] + " found.");
			}
		} else {
			out.append(getHelpMsg());
		}
			
		return out.toString();
	}

	private String prettyPrint(WimaxSessionData wimaxSession) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n"
				+ Strings.repeat("-", 180));
		builder.append("\n" + StringUtility.fillChar("", 13)
				+ "WIMAX SESSION DETAILS");
		builder.append("\n"
				+ Strings.repeat("-", 180));
		builder.append(wimaxSession);
		builder.append("\n"
				+ Strings.repeat("-", 180));
		
		return builder.toString();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage:\n");
		responseBuilder.append("clear session wimax <options>\n");
		responseBuilder.append(Strings.repeat("-", 150) + "\n");
		responseBuilder.append("Possible Options:\n");
		responseBuilder.append(String.format("%-20s <Value> : Removes WiMAX session for provided %s\n", CALLING_STATION_ID_PARAM, "Calling station ID"));
		responseBuilder.append(String.format("%-20s <Value> : Removes WiMAX session for provided %s\n", USERNAME_PARAM, "Username"));
		responseBuilder.append(String.format("%-20s <Value> : Removes WiMAX session for provided %s\n", CUI_PARAM, "CUI"));
		responseBuilder.append(String.format("%-20s <Value> : Removes WiMAX session for provided %s\n", AAA_ID_PARAM, "AAA Sessiom ID"));
		responseBuilder.append(String.format("%-20s <Value> : Removes WiMAX session(s) for provided %s\n", NAS_IP_PARAM, "NAS IP Address"));
		responseBuilder.append(String.format("%-20s <Value> : Removes WiMAX session(s) for provided %s\n", NAS_ID_PARAM, "NAS Identifier"));
		responseBuilder.append(Strings.repeat("-", 150) + "\n");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return this.detailProviderMap;
	}
	
	@Override
	public String getHotkeyHelp() {
		return "'wimax':{'-calling_station_id':{},'-username':{}, '-cui':{}, '-aaa_id':{}, '-nas_ip':{}, '-nas_id':{}}";
	}
	
	@Override
	public String getDescription() {
		return "removes WiMAX session based on provided key";
	}
}
