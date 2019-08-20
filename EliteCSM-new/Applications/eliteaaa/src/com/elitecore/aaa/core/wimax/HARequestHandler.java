package com.elitecore.aaa.core.wimax;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.ResultCode;

public class HARequestHandler implements WimaxRequestHandler {

	private static final String MODULE = "HA-REQUEST_HANDLER";
	
	public HARequestHandler() {
	
	}
	
	@Override
	public void handleRequest(WimaxRequest request, WimaxResponse response,WimaxSessionData wimaxSessionData) {
		//setting the request type in wimax Request
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Handling Request from HA.");
		}
		addWimaxKeysForHA(wimaxSessionData, request,response);

	}

	@Override
	public boolean isEligible(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		return (wimaxRequest.getHA().getMN_HA_MIP4_SPI() != null);
	}

	private void addWimaxKeysForHA(WimaxSessionData wimaxSessionData,WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		
		if(wimaxRequest.getHA().getHA_RK_SPI() != null){
			//add HA_RK_KEY
			wimaxResponse.getHA().setHA_RK_KEY(wimaxSessionData.getHA_RK_KEY());
			// add HA_RK_SPI
			wimaxResponse.getHA().setHA_RK_SPI(wimaxSessionData.getHA_RK_SPI());
			// add HA_RK_LIFETIME
			wimaxResponse.getHA().setHA_RK_LIFETIME(wimaxSessionData.getHA_RK_Lifetime_In_Seconds());
			
		}
		//add MN_HA_MIP4_KEY
		wimaxResponse.getHA().setMN_HA_MIP4_KEY(wimaxSessionData.getMN_HA_MIP4_KEY());
		//add MN_HA_MIP4_SPI
		wimaxResponse.getHA().setMN_HA_MIP4_SPI(wimaxSessionData.getMN_HA_MIP4_SPI());
		
		//adding the value of CUI from WiMAX session
		wimaxResponse.setCUI(wimaxSessionData.getCUI());
		
		
		wimaxResponse.setFurtherProcessingRequired(true);
		wimaxResponse.setResultCode(ResultCode.AUTH_SUCCESS);
	}

}
