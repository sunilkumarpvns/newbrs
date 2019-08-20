package com.elitecore.elitesm.web.core.system.moduledetails;

import javax.servlet.http.HttpServletRequest;


public interface ModuleDetailsMethods {
	
	public String handleRadiusSessionManager(String instanceId, HttpServletRequest request) throws Exception;
	public String handleDatabaseDatasource(String instanceId, HttpServletRequest request)throws Exception;
	public String handleExtendedRadius(String instanceId, HttpServletRequest request)throws Exception;
	public String handleLDAPDatasource(String instanceId, HttpServletRequest request)throws Exception;
	public String handleTrustedClient(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDiameterPeerProfile(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDiameterPeer(String instanceId, HttpServletRequest request)throws Exception;
	public String handleIMSIBasedRoutingTable(String instanceId, HttpServletRequest request)throws Exception;
	public String handleMSISDNBasedRoutingTable(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDiameterSessionManager(String instanceId, HttpServletRequest request)throws Exception;
	public String handleTranslationMapping(String instanceId, HttpServletRequest request)throws Exception;
	public String handleCopyPacketMappingConfig(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDiameterRoutingConfig(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDigestConfig(String instanceId, HttpServletRequest request)throws Exception;
	public String handleAlertConfiguration(String instanceId, HttpServletRequest request)throws Exception;
	public String handleEAPConfiguration(String instanceId, HttpServletRequest request)throws Exception;
	public String handleRoutingTableConfiguration(String instanceId, HttpServletRequest request)throws Exception;
	public String handleServerCertificate(String instanceId, HttpServletRequest request)throws Exception;
	public String handleRadiusESIGroup(String instanceId, HttpServletRequest request) throws Exception;
	/* Service Driver - Radius */
	public String handleDBAuthDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleHTTPAuthDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleLDAPAuthDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleMapGWAuthDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleHSSDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleUserFileAuthDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleWebserviceAuthDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleCrestelRatingTranslationDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleCrestelChargingDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDiameterChargingDriver(String instanceId, HttpServletRequest request)throws Exception;
	
	/* Radius Accounting Driver */
	public String handleClassicCSVDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDBAcctDriver(String instanceId, HttpServletRequest request)throws Exception;
	public String handleDetailLocalDriver(String instanceId, HttpServletRequest request)throws Exception;
}
