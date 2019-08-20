package com.elitecore.aaa.core.wimax;

import com.elitecore.aaa.core.wimax.keys.DhcpKeys;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.ResultCode;

public class DHCPRequestHandler implements WimaxRequestHandler {
	
	private final static String MODULE = "DHCP-REQUEST-HANDLER";
	private KeyManager keyManager;
	
	public DHCPRequestHandler(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	@Override
	public void handleRequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse,
			WimaxSessionData wimaxSessionData) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Handling Request from DHCP Server.");
		}
		DhcpKeys dhcpKeyDetails ;
		String dhcp_ip_address = getDHCP_IP_Address(wimaxRequest,wimaxResponse);	
		if(dhcp_ip_address == null || dhcp_ip_address.length()<=0){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "No NAS-IP-ADDRESS is present in the request which is mandatory for DHCP-Request so sending Access-Reject.");
		    }
			wimaxResponse.setFurtherProcessingRequired(false);
			wimaxResponse.setResponseMessage("No NAS-IP-ADDRESS is present in the request which is mandatory for DHCP-Request so sending Access-Reject.");
			wimaxResponse.setResultCode(ResultCode.AUTH_REJECT);
			return;
		}
		
		int dhcp_rk_id  = getDHCP_RK_ID(wimaxRequest, wimaxResponse);
		dhcpKeyDetails = keyManager.getDhcpKeyDetails(dhcp_ip_address, dhcp_rk_id, System.currentTimeMillis());
		
		if(dhcpKeyDetails == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "No active keys found for DHCP Server at ip address " + dhcp_ip_address  + " corrosponding to the requested id,sending Access-Reject.");
			}
			wimaxResponse.setFurtherProcessingRequired(false);
			wimaxResponse.setResponseMessage("No active keys found for DHCP Server");
			wimaxResponse.setResultCode(ResultCode.AUTH_REJECT);
			return;
		}
		addDHCPAttributes(dhcpKeyDetails, wimaxRequest, wimaxResponse);
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
			LogManager.getLogger().debug(MODULE, "Response packet after adding DHCP related attributes is " + wimaxResponse);
		}
		
		wimaxResponse.setFurtherProcessingRequired(true);
		wimaxResponse.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
		wimaxResponse.setResultCode(ResultCode.AUTH_SUCCESS);
	}

	@Override
	public boolean isEligible(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		return (wimaxRequest.getDHCP_RK_KEY_ID() != null);
	}
	
	protected int getDHCP_RK_ID(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		
		int dhcp_rk_key_id = 0;
		if(wimaxRequest.getDHCP_RK_KEY_ID() != null){
			dhcp_rk_key_id = wimaxRequest.getDHCP_RK_KEY_ID();
		}
		return dhcp_rk_key_id;
	}
	
	protected String getDHCP_IP_Address(WimaxRequest wimaxRequest,WimaxResponse wimaxResponse) {
		String nas_ip_address_value ="";
		if(wimaxRequest.getSourceInfo().getNAS_IP_Address() != null){
			nas_ip_address_value = wimaxRequest.getSourceInfo().getNAS_IP_Address();
		}
		return nas_ip_address_value;
	}
	
	protected void addDHCPAttributes(DhcpKeys dhcpKeys,WimaxRequest wimaxRequest , WimaxResponse wimaxResponse){
		//DHCP_RK
		wimaxResponse.getDHCP().setDHCP_RK(dhcpKeys.getDhcp_rk());
		
		//DHCP_RK_ID
		wimaxResponse.getDHCP().setDHCP_RK_KEY_ID(dhcpKeys.getDhcp_rk_id());
		
		//DHCP_RK_Lifetime
		long currentTimeInMillis = System.currentTimeMillis();
		
		wimaxResponse.getDHCP().setDHCP_RK_LIFETIME(dhcpKeys.getDhcp_rk_lifetime_in_seconds() - dhcpKeys.getElapsedTimeInSeconds(currentTimeInMillis));
		
	}
}
