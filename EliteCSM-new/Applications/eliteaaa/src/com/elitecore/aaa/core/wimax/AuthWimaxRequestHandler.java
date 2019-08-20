package com.elitecore.aaa.core.wimax;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.wimax.keys.DhcpKeys;
import com.elitecore.aaa.core.wimax.keys.HAKeyDetails;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreradius.util.wimax.WiMAXUtility;

public class AuthWimaxRequestHandler implements WimaxRequestHandler {

	private static final String MODULE = "AUTH-WIMAX-REQUEST_HANDLER";
	private EAPSessionManager eapSessionManager;
	private KeyManager keyManager;
	private SPIKeyConfiguration spiKeyConfiguration;
	
	public AuthWimaxRequestHandler(EAPSessionManager eapSessionManager, KeyManager keyManager, 
			SPIKeyConfiguration spiKeyConfiguration) {
		this.eapSessionManager = eapSessionManager;
		this.keyManager = keyManager;
		this.spiKeyConfiguration = spiKeyConfiguration;
	}
	
	@Override
	public void handleRequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse,WimaxSessionData wimaxSessionData) throws AuthorizationFailedException {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Handling Wimax Request for the user authenticated by TLS or TTLS");
		}
		String ha_ip_address = wimaxSessionData.getHA_IP_MIP4();
		((WimaxSessionDataImpl)wimaxSessionData).setHA_IP_MIP4("");
		String eapIdentity = wimaxRequest.getEAP().getEapIdentity();
		
		String str_dhcp_ip_address = wimaxSessionData.getDHCPV4_SERVER();
		((WimaxSessionDataImpl)wimaxSessionData).setDHCPV4_SERVER(str_dhcp_ip_address);
		
		
		createAndSetHA_KEYS_AND_CONTEXT(wimaxRequest, wimaxResponse, wimaxSessionData, ha_ip_address);
		byte[] eapKeyData = wimaxRequest.getEAP().getMSK();
		
			if(eapKeyData != null && eapIdentity != null){
				setWimaxParameters(wimaxSessionData, eapIdentity, eapKeyData, wimaxResponse,wimaxRequest);
			}

		if(str_dhcp_ip_address != null){			
			createAndSetDHCP_KEYS_AND_CONTEXT(wimaxSessionData, wimaxRequest, wimaxResponse, str_dhcp_ip_address);
			addDHCPRKKeyAndContext(wimaxSessionData, wimaxRequest, wimaxResponse);			
		}			
		addHA_RK_Key_andContext(wimaxSessionData, wimaxRequest, wimaxResponse, ha_ip_address);			
		addWimaxKeys(wimaxSessionData, wimaxRequest, wimaxResponse);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Response packet after adding WiMAX specific parameters: " + wimaxResponse);
		}
		
	}

	@Override
	public boolean isEligible(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		return eapSessionManager.isSessionExist(wimaxRequest.getEAP().getSessionID());
	}

	protected void createAndSetHA_KEYS_AND_CONTEXT(WimaxRequest wimaxRequest,WimaxResponse wimaxResponse, WimaxSessionData wimaxSessionData,String str_ha_ip_address) throws AuthorizationFailedException{
		String ha_ip_address = "0.0.0.0";
		
		HAKeyDetails haKeyDetails;
		if (str_ha_ip_address != null) {			
			ha_ip_address = str_ha_ip_address;
		}
		
		((WimaxSessionDataImpl)wimaxSessionData).setHA_IP_MIP4(ha_ip_address);		

		try {
			haKeyDetails = keyManager.getOrCreateHaKeyDetails(ha_ip_address, wimaxRequest, wimaxResponse);
		} catch (Exception e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Could not create HA keys for HA IP Address: " + ha_ip_address);
			}
			throw new AuthorizationFailedException(AuthReplyMessageConstant.WIMAX_PROCESSING_FAILED);
		}
		
		((WimaxSessionDataImpl)wimaxSessionData).setHA_RK_KEY(haKeyDetails.getHa_rk_key());
		((WimaxSessionDataImpl)wimaxSessionData).setHA_RK_SPI(haKeyDetails.getHa_rk_spi());
		((WimaxSessionDataImpl)wimaxSessionData).setHA_RK_Generation_Time_In_Millis(haKeyDetails.getHa_rk_genereation_time_in_millis());
		long remaining_lifetime_in_seconds = haKeyDetails.getHa_rk_lifetime_in_seconds() 
				- haKeyDetails.getElapsedTimeInSeconds(wimaxRequest.getRequestReceivedTimeInMillis()); 
		((WimaxSessionDataImpl)wimaxSessionData).setHA_RK_Lifetime_In_Seconds(remaining_lifetime_in_seconds);
	}
	
	protected void setWimaxParameters(WimaxSessionData wimaxSessionData,String eapIdentity, byte[] eapKeyData,WimaxResponse wimaxResponse,WimaxRequest wimaxRequest){
		byte[] MSK = new byte[64];
		byte[] EMSK = new byte[64];
		
		if(eapKeyData != null){
			System.arraycopy(eapKeyData, 0, MSK, 0, MSK.length);
			System.arraycopy(eapKeyData, MSK.length, EMSK, 0, EMSK.length);
		}
		
		((WimaxSessionDataImpl)wimaxSessionData).setMSK(MSK);
		((WimaxSessionDataImpl)wimaxSessionData).setEMSK(EMSK);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
			LogManager.getLogger().trace(MODULE, "MSK : " + Utility.bytesToHex(MSK));
			LogManager.getLogger().trace(MODULE, "EMSK : " + Utility.bytesToHex(EMSK));
		}
		
		
		byte[] mip_rk = WiMAXUtility.generateMIP_RK_Key(EMSK);
		String mn_nai = eapIdentity;
		if(mn_nai == null)
			mn_nai = "";
		
		long mn_ha_mip4_spi = -1;
		byte[] mn_ha_mip4_key = null;
		if(wimaxResponse.getHA().getMN_HA_MIP4_SPI() != null){
			mn_ha_mip4_spi = wimaxResponse.getHA().getMN_HA_MIP4_SPI();
		}
		
		Map<String,Map<String,String>> spiGroupMap = getSPIGroupMap();
		
		if(spiGroupMap != null){
			String str_ha_ip_address = wimaxSessionData.getHA_IP_MIP4();
			Map<String,String> spiKeyMap = (HashMap<String,String>) spiGroupMap.get(str_ha_ip_address);
			if(spiKeyMap != null){
				if(mn_ha_mip4_spi != -1){
					if(spiKeyMap.get(String.valueOf(mn_ha_mip4_spi)) != null){
						mn_ha_mip4_key = ((String)spiKeyMap.get(String.valueOf(mn_ha_mip4_spi))).getBytes();
					} else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "No key found for SPI " + mn_ha_mip4_spi + ", generating as per standard.");
						mn_ha_mip4_key = WiMAXUtility.generateMN_HA_PMIP4_KEY(mip_rk, mn_nai, wimaxSessionData.getHaIpAddressInBytes());
					}
				} else{
					mn_ha_mip4_spi = Long.parseLong((String)spiKeyMap.keySet().iterator().next());
					mn_ha_mip4_key = ((String)spiKeyMap.get(String.valueOf(mn_ha_mip4_spi))).getBytes();
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No SPI-Key configuration exists for HA-IP-Address " + str_ha_ip_address + ", generating key and/or SPI as per standard.");
				if(mn_ha_mip4_spi == -1)
					mn_ha_mip4_spi = WiMAXUtility.generateMN_HA_PMIP_SPI(mip_rk);
				mn_ha_mip4_key = WiMAXUtility.generateMN_HA_PMIP4_KEY(mip_rk, mn_nai, wimaxSessionData.getHaIpAddressInBytes());
			}
		} else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "No SPI-Key configuration exists, generating key and/or SPI as per standard.");
			if(mn_ha_mip4_spi == -1)
				mn_ha_mip4_spi = WiMAXUtility.generateMN_HA_PMIP_SPI(mip_rk);
			mn_ha_mip4_key = WiMAXUtility.generateMN_HA_PMIP4_KEY(mip_rk, mn_nai, wimaxSessionData.getHaIpAddressInBytes());
		}

		((WimaxSessionDataImpl)wimaxSessionData).setMN_HA_SPI(mn_ha_mip4_spi);
		((WimaxSessionDataImpl)wimaxSessionData).setMN_HA_MIP4_KEY(mn_ha_mip4_key);
		
		byte[] fa_rk_key = WiMAXUtility.generateFA_RK_KEY(mip_rk);
		((WimaxSessionDataImpl)wimaxSessionData).setFA_RK_KEY(fa_rk_key);
		
		long fa_rk_spi = WiMAXUtility.generateMN_HA_CMIP_SPI(mip_rk);
		((WimaxSessionDataImpl)wimaxSessionData).setFA_RK_SPI(fa_rk_spi);
		
		((WimaxSessionDataImpl)wimaxSessionData).setDefaultSessionTimeoutInSeconds(wimaxResponse.getSessionTimeoutInSeconds()); 
		
		//adding the CUI value in WiMAX session
		((WimaxSessionDataImpl)wimaxSessionData).setCUI(wimaxRequest.getCUI());
	}
	
	private Map<String,Map<String,String>> getSPIGroupMap(){
		return spiKeyConfiguration != null ? spiKeyConfiguration.getSPIgroupMap() : null;
	}
	
	protected void addHA_RK_Key_andContext(WimaxSessionData wimaxSessionData,WimaxRequest wimaxRequest, WimaxResponse wimaxResponse,String str_ha_ip_address) {
		if(wimaxResponse.getHA().getHA_IP_MIP4() == null && str_ha_ip_address != null ){
			wimaxResponse.getHA().setHA_IP_MIP4(wimaxSessionData.getHA_IP_MIP4());
		}
		
		//HA_RK_KEY
		wimaxResponse.getHA().setHA_RK_KEY(wimaxSessionData.getHA_RK_KEY());
		//HA_RK_SPI
		wimaxResponse.getHA().setHA_RK_SPI(wimaxSessionData.getHA_RK_SPI());
		//HA_RK_LIFETIME
		wimaxResponse.getHA().setHA_RK_LIFETIME(wimaxSessionData.getHA_RK_Lifetime_In_Seconds());
		
	}

	protected void addWimaxKeys(WimaxSessionData wimaxSessionData, WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		//MSK
		wimaxResponse.setMSK(wimaxSessionData.getMSK());
		//		Adding Session-Timeout in repsonse with system level default-session-timeout
		if(wimaxResponse.getSessionTimeoutInSeconds() == null){ 
			wimaxResponse.setSessionTimeoutInSeconds(wimaxSessionData.getDefaultSessionTimeoutInSeconds());
		}
		//MN_HA_MIP4_KEY
		wimaxResponse.getHA().setMN_HA_MIP4_KEY(wimaxSessionData.getMN_HA_MIP4_KEY());
		
		//MN_HA_MIP4_SPI
		wimaxResponse.getHA().setMN_HA_MIP4_SPI(wimaxSessionData.getMN_HA_MIP4_SPI());
		
//		 FA_RK_KEY
		wimaxResponse.getFA().setFA_RK_KEY(wimaxSessionData.getFA_RK_KEY());
		
//		 FA_RK_SPI (value equals MN_HA_CMIP4_SPI)
		wimaxResponse.getFA().setFA_RK_SPI(wimaxSessionData.getFA_RK_SPI());
		
	}
	
	protected void createAndSetDHCP_KEYS_AND_CONTEXT(WimaxSessionData wimaxSessionData, WimaxRequest wimaxRequest,WimaxResponse wimaxResponse, String str_dhcp_ip_address) throws AuthorizationFailedException {

		String dhcp_ip_address = null;
		DhcpKeys dhcpKeyDetails = null;

		dhcp_ip_address = str_dhcp_ip_address;
		((WimaxSessionDataImpl)wimaxSessionData).setDHCPV4_SERVER(dhcp_ip_address);			

		// handling the asn request for providing dhcp-rk.			
		long requestReceivedInMilis = wimaxRequest.getRequestReceivedTimeInMillis();					
		try {
			dhcpKeyDetails = keyManager.getOrCreateDhcpKeyDetails(dhcp_ip_address,requestReceivedInMilis);
		} catch (Exception e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Could not create DHCP keys for DHCP Ip Address: " + dhcp_ip_address);
			}
			throw new AuthorizationFailedException(AuthReplyMessageConstant.WIMAX_PROCESSING_FAILED);
		}
		((WimaxSessionDataImpl)wimaxSessionData).setDhcp_rk(dhcpKeyDetails.getDhcp_rk());
		((WimaxSessionDataImpl)wimaxSessionData).setDhcp_rk_id(dhcpKeyDetails.getDhcp_rk_id());
		((WimaxSessionDataImpl)wimaxSessionData).setDhcp_rk_lifetime_in_seconds(dhcpKeyDetails.getRemainingLifetimeInSeconds(requestReceivedInMilis));
		((WimaxSessionDataImpl)wimaxSessionData).setDhcp_rk_generation_time_in_millis(dhcpKeyDetails.getDhcp_rk_genereation_time_in_millis());

	}

	protected void addDHCPRKKeyAndContext(WimaxSessionData wimaxSessionData,WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		if(wimaxResponse.getDHCP().getDHCP_Server() == null){
			wimaxResponse.getDHCP().setDHCP_Server(wimaxSessionData.getDHCPV4_SERVER());
		}
		 // DHCP_RK
		wimaxResponse.getDHCP().setDHCP_RK(wimaxSessionData.getDhcp_rk());
	//	 DHCP_RK_ID
		wimaxResponse.getDHCP().setDHCP_RK_KEY_ID(wimaxSessionData.getDhcp_rk_id());
	//	 DHCP_RK_Lifetime
		wimaxResponse.getDHCP().setDHCP_RK_LIFETIME(wimaxSessionData.getDhcp_rk_lifetime_in_seconds());
		
	}

}
