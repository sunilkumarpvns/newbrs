package com.elitecore.aaa.core.util.cli;

import java.util.HashMap;

import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.core.util.cli.cmd.DetailProvider;

public class ClearWimaxKeysCommand extends DetailProvider {

	private static final String PARAM_DHCP = "-dhcp";
	private static final String PARAM_HA = "-ha";
	private final KeyManager keyManager;
	private HashMap<String, DetailProvider> detailProviderMap;

	public ClearWimaxKeysCommand(KeyManager keyManager) {
		this.keyManager = keyManager;
		this.detailProviderMap = new HashMap<String, DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters.length != 2) {
			return getHelpMsg();
		}
		
		String out;
		if (PARAM_HA.equalsIgnoreCase(parameters[0])) {
			if (keyManager.removeHAKey(parameters[1])) {
				out = "HA key with IP: " + parameters[1] + " removed";
			} else {
				out = "HA key with IP: " + parameters[1] + " is not found";
			}
		} else if (PARAM_DHCP.equalsIgnoreCase(parameters[0])) {
			if (keyManager.removeDHCPKey(parameters[1])) {
				out = "DHCP key with IP: " + parameters[1] + " removed";
			} else {
				out = "DHCP key with IP: " + parameters[1] + " is not found";
			}
		} else {
			out = getHelpMsg();
		}
			
		return out;
	}

	@Override
	public String getKey() {
		return "wimax";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return this.detailProviderMap;
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage:\n");
		responseBuilder.append("    clear keys wimax <options>\n");
		responseBuilder.append("Possible Options:\n");
		responseBuilder.append("    -ha    <IP-Address>       : Removes HA key details for provided IP-Address\n");
		responseBuilder.append("    -dhcp  <IP-Address>       : Removes DHCP keys details for provided IP-Address\n");
		return responseBuilder.toString();
	}
	
	@Override
	public String getHotkeyHelp() {
		return "'wimax':{'-ha':{},'-dhcp':{}}";
	}
	
	@Override
	public String getDescription() {
		return "removes wimax keys created in the system";
	}

}
