package com.elitecore.aaa.core.util.cli;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.elitecore.aaa.core.wimax.keys.DhcpKeys;
import com.elitecore.aaa.core.wimax.keys.HAKeyDetails;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class ShowWimaxKeysCommand extends DetailProvider {

	private static final String PARAM_DHCP = "-dhcp";
	private static final String PARAM_HA = "-ha";
	private final KeyManager keyManager;
	private HashMap<String, DetailProvider> detailProviderMap;
	
	public ShowWimaxKeysCommand(KeyManager keyManager) {
		this.keyManager = keyManager;
		this.detailProviderMap = new HashMap<String, DetailProvider>();
	}
	
	@Override
	public String execute(String[] parameters) {
		if (parameters.length != 2) {
			return getHelpMsg();
		}
		
		if (PARAM_HA.equalsIgnoreCase(parameters[0])) {
			return getHADetails(parameters[1]);
		} else if (PARAM_DHCP.equalsIgnoreCase(parameters[0])) {
			return getDHCPDetails(parameters[1]);
		} else {
			return getHelpMsg();
		}
	}

	private String getHADetails(String haIp) {
		TableFormatter tableFormatter = new TableFormatter(new String[] {"IP", "SPI", "Root Key", "Lifetime (sec)", "Generation Time"}, new int[] {20, 20, 20, 20, 20});
		HAKeyDetails keyDetails;
		try {
			keyDetails = keyManager.getHaKeyDetails(haIp);
		} catch (Exception e) {
			return "Error in locating HA keys";
		}
		
		if (keyDetails == null) {
			return "HA key for IP: " + haIp + " not found";
		}
		tableFormatter.addRecord(new String[] {haIp, 
				String.valueOf(keyDetails.getHa_rk_spi()), 
				RadiusUtility.bytesToHex(keyDetails.getHa_rk_key()),
				remainingLifetimeInSeconds(keyDetails), 
				new Date(keyDetails.getHa_rk_genereation_time_in_millis()).toString()});

		return tableFormatter.getFormattedValues();
	}

	private String remainingLifetimeInSeconds(HAKeyDetails keyDetails) {
		long remainingLifetimeInSeconds = keyDetails.getRemainingLifetimeInSeconds(System.currentTimeMillis());
		return remainingLifetimeInSeconds > 0 ? String.valueOf(remainingLifetimeInSeconds) : "EXPIRED";
	}

	private String getDHCPDetails(String dhcpIP) {
		TableFormatter tableFormatter = new TableFormatter(new String[] {"IP", "Id", "Root Key", "Lifetime (sec)", "Generation Time"}, new int[] {20, 20, 20, 20, 20});
		List<DhcpKeys> keyDetails;
		try {
			keyDetails = keyManager.getDhcpKeyDetails(dhcpIP);
		} catch (Exception e) {
			return "Error in locating DHCP keys";
		}
		if (keyDetails == null || keyDetails.isEmpty()) {
			return "DHCP key for IP: " + dhcpIP + " not found";
		}
		long currentTimeInMillis = System.currentTimeMillis();
		for (DhcpKeys dhcpKey : keyDetails) {
			tableFormatter.addRecord(new String[] {dhcpIP, String.valueOf(dhcpKey.getDhcp_rk_id()),
					RadiusUtility.bytesToHex(dhcpKey.getDhcp_rk()), 
					remainingLifetimeInSeconds(dhcpKey, currentTimeInMillis), 
					new Date(dhcpKey.getDhcp_rk_genereation_time_in_millis()).toString()});
		}

		return tableFormatter.getFormattedValues();
	}

	private String remainingLifetimeInSeconds(DhcpKeys dhcpKey, long currentTimeInMillis) {
		return dhcpKey.isExpired(currentTimeInMillis) ? "EXPIRED" 
				: String.valueOf(dhcpKey.getRemainingLifetimeInSeconds(currentTimeInMillis));
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage:\n");
		responseBuilder.append("    show keys wimax <options>\n");
		responseBuilder.append("Possible Options:\n");
		responseBuilder.append(String.format("    %s    <IP-Address>       : Shows HA key details for provided IP-Address\n", PARAM_HA));
		responseBuilder.append(String.format("    %s  <IP-Address>       : Shows DHCP keys details for provided IP-Address\n", PARAM_DHCP));
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return "wimax";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getDescription() {
		return "displays wimax keys created in the system";
	}
}
