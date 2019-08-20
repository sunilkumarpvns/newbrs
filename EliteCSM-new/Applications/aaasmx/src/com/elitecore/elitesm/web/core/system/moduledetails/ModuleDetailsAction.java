package com.elitecore.elitesm.web.core.system.moduledetails;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.certificate.ServerCertificateBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertFileListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTrapListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogAlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMConfigInstanceData;
import com.elitecore.elitesm.util.constants.AlertListenerConstant;
import com.elitecore.elitesm.util.constants.CertificateRemarks;
import com.elitecore.elitesm.util.constants.EliteViewCommonConstant;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.util.exception.InvalidPrivateKeyException;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModuleDetailsAction extends BaseDispatchAction implements ModuleDetailsMethods{
	private static final String MODULE = ModuleDetailsAction.class.getSimpleName();
	
	public ActionForward getModuleDetails(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered getModuleDetails() method of " + this.getClass());
		try {
			response.setContentType("application/json");
			
			String instance_Id = request.getParameter("instance_Id");
			String module_Name = request.getParameter("module_Name");
			String moduleJson = "";
			
			if( module_Name.equals(EliteViewCommonConstant.DATABASE_DATASOURCE) ){
				moduleJson = handleDatabaseDatasource(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.SESSION_MANAGER)){
				moduleJson = handleRadiusSessionManager(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.EXTENDED_RADIUS)){
				moduleJson = handleExtendedRadius(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.LDAP_DATASOURCE) ){
				moduleJson = handleLDAPDatasource(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.TRUSTED_CLIENT_PROFILE) ){
				moduleJson = handleTrustedClient(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DIAMETER_PEER_PROFILE)){
				moduleJson = handleDiameterPeerProfile(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DIAMETER_PEER) ){
				moduleJson = handleDiameterPeer(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.IMSI_BASED_ROUTING_TABLE)){
				moduleJson = handleIMSIBasedRoutingTable(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.MSISDN_BASED_ROUTING_TABLE)){
				moduleJson = handleMSISDNBasedRoutingTable(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DIAMETER_SESSION_MANAGER)){
				moduleJson = handleDiameterSessionManager(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.COPY_PACKET_CONFIG)){
				moduleJson = handleCopyPacketMappingConfig(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.TRANSLATION_MAPPING)){
				moduleJson = handleTranslationMapping(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DIAMETER_ROUTING_CONFIG)){
				moduleJson = handleDiameterRoutingConfig(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DIGEST_CONFIGURATION)){
				moduleJson = handleDigestConfig(instance_Id, request);
			}else if ( module_Name.equals(EliteViewCommonConstant.ALERT_CONFIGURATION)){
				moduleJson = handleAlertConfiguration(instance_Id, request);
			}else if ( module_Name.equals(EliteViewCommonConstant.EAP_CONFIGURATION)){
				moduleJson = handleEAPConfiguration(instance_Id, request);
			}else if ( module_Name.equals(EliteViewCommonConstant.DIAMETER_ROUTING_TABLE)){
				moduleJson = handleRoutingTableConfiguration(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DB_AUTH_DRIVER)){
				moduleJson = handleDBAuthDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.HTTP_AUTH_DRIVER)){
				moduleJson = handleHTTPAuthDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.LDAP_AUTH_DRIVER)){
				moduleJson = handleLDAPAuthDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.MAP_GW_AUTH_DRIVER)){
				moduleJson = handleMapGWAuthDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.HSS_AUTH_DRIVER)){
				moduleJson = handleHSSDriver(instance_Id, request);
			}else if ( module_Name.equals(EliteViewCommonConstant.USER_FILE_AUTH_DRIVER)){
				moduleJson = handleUserFileAuthDriver(instance_Id, request);
			}else if ( module_Name.equals(EliteViewCommonConstant.WEB_SERVICE_AUTH_DRIVER)){
				moduleJson = handleWebserviceAuthDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.CLASSIC_CSV_ACCT_DRIVER)){
				moduleJson = handleClassicCSVDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DB_ACCT_DRIVER)){
				moduleJson = handleDBAcctDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DETAIL_LOCAL_ACCT_DRIVER)){
				moduleJson = handleDetailLocalDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.CRESTEL_RATING_TRANSLATION_DRIVER)){
				moduleJson = handleCrestelRatingTranslationDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.CRESTEL_CHARGING_DRIVER)){
				moduleJson = handleCrestelChargingDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DIAMETER_CHARGING_DRIVER)){
				moduleJson = handleDiameterChargingDriver(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.SERVER_CERTIFICATE)){
				moduleJson = handleServerCertificate(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.RADIUS_ESI_GROUP)){
				moduleJson = handleRadiusESIGroup(instance_Id, request);
			}else if( module_Name.equals(EliteViewCommonConstant.DIAMETER_PEER_GROUP)){
				moduleJson = handleDiameterPeerGroup(instance_Id, request);
			}
			
			response.getWriter().write(moduleJson);
			return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return mapping.findForward(null);
	}
	
	private String handleDiameterPeerGroup(String instance_Id, HttpServletRequest request) {
		Logger.logDebug(MODULE, "Entered handleDiameterPeerGroup() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Diameter Peer Group.");
			
			DiameterPeerGroupBLManager blManager = new  DiameterPeerGroupBLManager();
			
			DiameterPeerGroup diameterPeerGroup = blManager.getDiameterPeerGroupById(instance_Id);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			
			if( Strings.isNullOrBlank(diameterPeerGroup.getGeoRedunduntGroup()) == false ){
				DiameterPeerGroup diameterPeerGroupData = blManager.getDiameterPeerGroupById(diameterPeerGroup.getGeoRedunduntGroup());
				
				List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
				
				NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
				nestedObjectDetails.setKey("Geo Redundant Group");
				nestedObjectDetails.setModuleId(diameterPeerGroupData.getPeerGroupId());
				nestedObjectDetails.setModuleInstanceName(diameterPeerGroupData.getPeerGroupName());
				nestedObjectDetails.setModuleName(EliteViewCommonConstant.DIAMETER_PEER_GROUP);
				
				nestedObjectDetailsList.add(nestedObjectDetails);
				moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			} else {
				diameterPeerGroup.setGeoRedunduntGroup("NONE");
			}
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String diameterPeerGroupDataJson = gson.toJson(diameterPeerGroup);
			
			moduleDetails.setJsonObject(diameterPeerGroupDataJson.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DIAMETER_PEER_GROUP+diameterPeerGroup.getPeerGroupId());
			
			String diameterPeerGroupJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Diameter Peer Group : " + diameterPeerGroupJson.toString());
			return diameterPeerGroupJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while fetching Diameter Peer Group information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleRadiusSessionManager(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleRadiusSessionManager() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Session Manager.");
			SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
			ISessionManagerInstanceData sessionManagerInstanceData = sessionManagerBLManager.getSessionManagerDataById(instanceId);
			SMConfigInstanceData smConfigInstanceData=(SMConfigInstanceData)sessionManagerInstanceData.getSmConfigInstanceData();
		
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			IDatabaseDSData dataBaseDsData = databaseDSBLManager.getDatabaseDSDataById(smConfigInstanceData.getDatabaseDatasourceId());
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Datasource");
			nestedObjectDetails.setModuleId(smConfigInstanceData.getDatabaseDatasourceId());
			nestedObjectDetails.setModuleInstanceName(dataBaseDsData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.DATABASE_DATASOURCE);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			 
			/* convert object to json format */
			String smConfigInstanceDataJson = gson.toJson(smConfigInstanceData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(smConfigInstanceDataJson);
			
			if( smConfigInstanceData.getBehaviour() != 0){
				if(smConfigInstanceData.getBehaviour() == 1){
					jsonObject.remove("Behavior");
					jsonObject.put("Behavior", "Acct");
				}else if( smConfigInstanceData.getBehaviour() == 2){
					jsonObject.remove("Behavior");
					jsonObject.put("Behavior", "Auth");
				}
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_SESSION_MANAGER_ADVANCED_DETAILS+smConfigInstanceData.getSmInstanceId());
			
			String json = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Session Manager: " + json.toString());
			return json.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDatabaseDatasource(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDatabaseDatasource() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Database Datasource.");
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			IDatabaseDSData dataBaseDsData = databaseDSBLManager.getDatabaseDSDataById(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String dataBaseDsDataJson = gson.toJson(dataBaseDsData);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(dataBaseDsDataJson.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DATABASE_DATAOURCE_DETAILS+dataBaseDsData.getDatabaseId());
			
			String json = new Gson().toJson(moduleDetails);
			return json.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleExtendedRadius(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleExtendedRadius() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of External System interface.");
			
			ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
			ESITypeAndInstanceData esiTypeAndInstanceData = esiBLManager.getESIInstanceDetail(instanceId);
		
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			/* convert object to json format */
			String esiTypeAndInsanceDataJson = gson.toJson(esiTypeAndInstanceData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(esiTypeAndInsanceDataJson);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_EXTERNAL_SYSTEM_DETAILS+esiTypeAndInstanceData.getEsiInstanceId());
			
			String esiJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of External System Interface : " + esiJson.toString());
			return esiJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleLDAPDatasource(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleLDAPDatasource() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Ldap datasource details.");
			LDAPDatasourceBLManager blManager = new LDAPDatasourceBLManager();
			LDAPDatasourceData ldapDatasourceData = (LDAPDatasourceData) blManager.getLDAPDatabaseDataById(instanceId);
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			/* convert object to json format */
			String ldapDatasourceDataJson = gson.toJson(ldapDatasourceData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(ldapDatasourceDataJson);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_LDAP_DATASOURCE+ldapDatasourceData.getLdapDsId());
			
			String ldapJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Ldap datasource data : " + ldapJson.toString());
			return ldapJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleTrustedClient(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleTrustedClient() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Trusted Client Profile details.");
			
			ClientProfileBLManager blManager = new ClientProfileBLManager();
			RadiusClientProfileData radiusClientProfileData = new RadiusClientProfileData();
			
			radiusClientProfileData = blManager.getClientProfileDataById(instanceId);
			
			String vendorInstanceId=radiusClientProfileData.getVendorInstanceId();
			Long clientTypeId=radiusClientProfileData.getClientTypeId();

			VendorData vendorData=blManager.getVendorData(vendorInstanceId);
			ClientTypeData clientTypeData=blManager.getClientTypeData(clientTypeId);	

			radiusClientProfileData.setVendorData(vendorData);
			radiusClientProfileData.setClientTypeData(clientTypeData);
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			/* convert object to json format */
			String ldapDatasourceDataJson = gson.toJson(radiusClientProfileData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(ldapDatasourceDataJson);
			
			if( radiusClientProfileData.getVendorData() != null){
				jsonObject.remove("Vendor Name");
				jsonObject.put("Vendor Name",radiusClientProfileData.getVendorData().getVendorName());
			}else{
				jsonObject.put("Vendor Name","");
			}
			
			if( radiusClientProfileData.getClientTypeData() != null){
				jsonObject.remove("Client Type");
				jsonObject.put("Client Type",radiusClientProfileData.getClientTypeData().getClientTypeName());
			}else{
				jsonObject.put("Client Type","");
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_TRUSTED_CLIENT_PROFILE+radiusClientProfileData.getProfileId());
			
			String trustedProfileJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Trusted Client Profile data : " + trustedProfileJson.toString());
			return trustedProfileJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDiameterPeerProfile(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDiameterPeerProfile() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Diameter peer Profile details.");
			
			DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();
			
			DiameterPeerProfileData diameterPeerProfileData = blManager.getDiameterPeerProfileById(instanceId);

			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			/* convert object to json format */
			String diameterPeerProfileDataJson = gson.toJson(diameterPeerProfileData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(diameterPeerProfileDataJson);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DIAMETER_PEER_PROFILE+diameterPeerProfileData.getPeerProfileId());
			
			String peerProfileJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Ldap datasource data : " + peerProfileJson.toString());
			return peerProfileJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDiameterPeer(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDiameterPeer() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Diameter Peer.");
			
			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
			DiameterPeerData diameterPeerData = diameterPeerBLManager.getDiameterPeerById(instanceId);
		
			DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();

			DiameterPeerProfileData diameterPeerProfileData = blManager.getDiameterPeerProfileById(diameterPeerData.getPeerProfileId());

			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Profile Name");
			nestedObjectDetails.setModuleId(diameterPeerProfileData.getPeerProfileId());
			nestedObjectDetails.setModuleInstanceName(diameterPeerProfileData.getProfileName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.DIAMETER_PEER_PROFILE);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			 
			/* convert object to json format */
			String diameterPeerDataJson = gson.toJson(diameterPeerData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(diameterPeerDataJson);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DIAMETER_PEER+diameterPeerData.getPeerId());
			
			String json = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Diameter Peer: " + json.toString());
			return json.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleIMSIBasedRoutingTable(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleIMSIBasedRoutingTable() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of IMSI Based Routing table data.");
			
			IMSIBasedRoutingTableBLManager blManager = new  IMSIBasedRoutingTableBLManager();
			IMSIBasedRoutingTableData imsiBasedRoutingTableData = blManager.getImsiBasedRoutingTableData(instanceId);
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			/* convert object to json format */
			String imsiBasedRoutingTableDataJson = gson.toJson(imsiBasedRoutingTableData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(imsiBasedRoutingTableDataJson);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_IMSI_BASED_ROUTING_TABLE+imsiBasedRoutingTableData.getRoutingTableId());
			
			String imsiBasedJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of IMSI based Routing table data : " + imsiBasedJson.toString());
			return imsiBasedJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleMSISDNBasedRoutingTable(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleMSISDNBasedRoutingTable() method of " + this.getClass());
		try {
			
			Logger.logInfo(MODULE, "View data of MSISDN Based Routing table data.");
			MSISDNBasedRoutingTableBLManager blManager = new  MSISDNBasedRoutingTableBLManager();
			MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = blManager.getMSISDNBasedRoutingTableData(instanceId);
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			/* convert object to json format */
			String msisdnBasedRoutingTableDataJson = gson.toJson(msisdnBasedRoutingTableData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(msisdnBasedRoutingTableDataJson);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_MSISDN_BASED_ROUTING_TABLE+msisdnBasedRoutingTableData.getRoutingTableId());
			
			String msisdnBasedJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of MSISDN based Routing table data : " + msisdnBasedJson.toString());
			return msisdnBasedJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDiameterSessionManager(String instanceId, HttpServletRequest request)throws Exception {
		Logger.logDebug(MODULE, "Entered handleDiameterSessionManager() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Diameter Session Manager data.");
			
			DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
			DiameterSessionManagerData diameterSessionManagerData = blManager.getDiameterSessionManagerDataById(instanceId);
		
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			IDatabaseDSData dataBaseDsData = databaseDSBLManager.getDatabaseDSDataById(diameterSessionManagerData.getDatabaseDatasourceId());
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			/* convert object to json format */
			String diameterSessionManagerDataJson = gson.toJson(diameterSessionManagerData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(diameterSessionManagerDataJson);
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Datasource");
			nestedObjectDetails.setModuleId(diameterSessionManagerData.getDatabaseDatasourceId());
			nestedObjectDetails.setModuleInstanceName(dataBaseDsData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.DATABASE_DATASOURCE);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DIAMETER_SESSION_MANAGER+diameterSessionManagerData.getSessionManagerId());
			
			String json = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Diameter Session Manager: " + json.toString());
			return json.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleTranslationMapping(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleTranslationMapping() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Translation Mapping data.");
			TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
			
			TranslationMappingConfData translationMappingConfData = blManager.getTranslationMappingConfData(instanceId);
		
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			String translationMappingType = translationMappingConfData.getTranslatorTypeTo().getName() +"-"+translationMappingConfData.getTranslatorTypeFrom().getName();
			
			/* convert object to json format */
			String translationMappingDataJson = gson.toJson(translationMappingConfData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(translationMappingDataJson);
			jsonObject.put("Translation Type", translationMappingType);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(null);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_TRANSLATION_MAPPING+translationMappingConfData.getTranslationMapConfigId());
			
			String json = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Translation Mapping Data : " + json.toString());
			return json.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleCopyPacketMappingConfig(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleCopyPacketMappingConfig() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Copy Packet data.");
			CopyPacketTransMapConfBLManager confBLManager = new  CopyPacketTransMapConfBLManager();
			CopyPacketTranslationConfData copyPacketTranslationConfData = confBLManager.getCopyPacketTransMapConfigData(instanceId);
		
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String translationMappingType = copyPacketTranslationConfData.getTranslatorTypeTo().getName() +"-"+copyPacketTranslationConfData.getTranslatorTypeFrom().getName();
			
			/* convert object to json format */
			String copypacketMappingDataJson = gson.toJson(copyPacketTranslationConfData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(copypacketMappingDataJson);
			jsonObject.put("Translation Type", translationMappingType);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(null);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_COPY_PACKET_CONFIG+copyPacketTranslationConfData.getCopyPacketTransConfId());
			
			String json = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Copy Packet Configuration : " + json.toString());
			return json.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDiameterRoutingConfig(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDiameterRoutingConfig() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Diameter Routing Config data.");
			
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			DiameterRoutingConfData diameterRoutingConfData = diameterRoutingConfBLManager.getDiameterRoutingConfData(instanceId);
		
			/* Diameter Routing Table Data */
			DiameterRoutingTableData diameterRoutingTableData =  diameterRoutingConfBLManager.getDiameterRoutingTableData(diameterRoutingConfData.getRoutingTableId());
			
			/* Translation Mapping Configuration*/
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData translationMappingConfData = translationMappingConfBLManager.getTranslationMappingConfData(diameterRoutingConfData.getTransMapConfId());
	
			/* Copy Packet Configuration*/
			CopyPacketTransMapConfBLManager copyPacketTransMapConfBLManager = new CopyPacketTransMapConfBLManager();
			CopyPacketTranslationConfData copyPacketTranslationConfData = copyPacketTransMapConfBLManager.getCopyPacketTransMapConfigData(diameterRoutingConfData.getCopyPacketMapId());
	
			/* IMSI Based Routing Table*/
			IMSIBasedRoutingTableBLManager imsiBasedRoutingTableBLManager = new  IMSIBasedRoutingTableBLManager();
			IMSIBasedRoutingTableData imsiBasedRoutingTableData = new IMSIBasedRoutingTableData();
			
			/* MSISDN Based Routing Table*/
			MSISDNBasedRoutingTableBLManager msisdnBasedRoutingTableBLManager = new  MSISDNBasedRoutingTableBLManager();
			MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = new  MSISDNBasedRoutingTableData();
			
			/* Checking for Translation mapping configured or copy packet configured */
			if(diameterRoutingConfData.getTransMapConfId() != null){
				translationMappingConfData = translationMappingConfBLManager.getTranslationMappingConfData(diameterRoutingConfData.getTransMapConfId());
			}else if( diameterRoutingConfData.getCopyPacketMapId() != null){
				copyPacketTranslationConfData = copyPacketTransMapConfBLManager.getCopyPacketTransMapConfigData(diameterRoutingConfData.getCopyPacketMapId());
			}
			
			/* Checking for IMSI or MSISDN Configuration */
			if(diameterRoutingConfData.getImsiBasedRoutingTableId() != null){
				imsiBasedRoutingTableData = imsiBasedRoutingTableBLManager.getImsiBasedRoutingTableData(diameterRoutingConfData.getImsiBasedRoutingTableId());
			}
			
			if( diameterRoutingConfData.getMsisdnBasedRoutingTableId() != null){
				msisdnBasedRoutingTableData = msisdnBasedRoutingTableBLManager.getMSISDNBasedRoutingTableData(diameterRoutingConfData.getMsisdnBasedRoutingTableId());
			}
			
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			/* convert object to json format */
			String copypacketMappingDataJson = gson.toJson(diameterRoutingConfData);
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(copypacketMappingDataJson);
			
			/* Create link for Diameter Routing table */
			jsonObject.put("Routing Table Name", diameterRoutingTableData.getRoutingTableName());
			NestedObjectDetails nestedObjectRoutingtable = new NestedObjectDetails();
			nestedObjectRoutingtable.setKey("Routing Table Name");
			nestedObjectRoutingtable.setModuleId(diameterRoutingTableData.getRoutingTableId());
			nestedObjectRoutingtable.setModuleInstanceName(diameterRoutingTableData.getRoutingTableName());
			nestedObjectRoutingtable.setModuleName(EliteViewCommonConstant.DIAMETER_ROUTING_TABLE);
			nestedObjectDetailsList.add(nestedObjectRoutingtable);
			
			if( diameterRoutingConfData.getSubsciberMode().equals(CommonConstants.IMSI_MSISDN)){
				
				if( imsiBasedRoutingTableData != null){
					
					jsonObject.put("Subscriber Routing 1", imsiBasedRoutingTableData.getRoutingTableName());
					
					NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
					nestedObjectDetails.setKey("Subscriber Routing 1");
					nestedObjectDetails.setModuleId(imsiBasedRoutingTableData.getRoutingTableId());
					nestedObjectDetails.setModuleInstanceName(imsiBasedRoutingTableData.getRoutingTableName());
					nestedObjectDetails.setModuleName(EliteViewCommonConstant.IMSI_BASED_ROUTING_TABLE);
					nestedObjectDetailsList.add(nestedObjectDetails);
				}else{
					jsonObject.put("Subscriber Routing 1", "");
				}
				
				if(msisdnBasedRoutingTableData != null){
					jsonObject.put("Subscriber Routing 2", msisdnBasedRoutingTableData.getRoutingTableName());
					
					NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
					nestedObjectDetails.setKey("Subscriber Routing 2");
					nestedObjectDetails.setModuleId(msisdnBasedRoutingTableData.getRoutingTableId());
					nestedObjectDetails.setModuleInstanceName(msisdnBasedRoutingTableData.getRoutingTableName());
					nestedObjectDetails.setModuleName(EliteViewCommonConstant.MSISDN_BASED_ROUTING_TABLE);
					
					nestedObjectDetailsList.add(nestedObjectDetails);
					
				}else{
					jsonObject.put("Subscriber Routing 2", "");
				}
				
			}else if( diameterRoutingConfData.getSubsciberMode().equals(CommonConstants.MSISDN_IMSI)){
				
				if( imsiBasedRoutingTableData != null){
					jsonObject.put("Subscriber Routing 2", imsiBasedRoutingTableData.getRoutingTableName());
					
					NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
					nestedObjectDetails.setKey("Subscriber Routing 2");
					nestedObjectDetails.setModuleId(imsiBasedRoutingTableData.getRoutingTableId());
					nestedObjectDetails.setModuleInstanceName(imsiBasedRoutingTableData.getRoutingTableName());
					nestedObjectDetails.setModuleName(EliteViewCommonConstant.IMSI_BASED_ROUTING_TABLE);
					
					nestedObjectDetailsList.add(nestedObjectDetails);
				}else{
					jsonObject.put("Subscriber Routing 2", "");
				}
				
				if(msisdnBasedRoutingTableData != null){
					jsonObject.put("Subscriber Routing 1", msisdnBasedRoutingTableData.getRoutingTableName());
					
					NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
					nestedObjectDetails.setKey("Subscriber Routing 1");
					nestedObjectDetails.setModuleId(msisdnBasedRoutingTableData.getRoutingTableId());
					nestedObjectDetails.setModuleInstanceName(msisdnBasedRoutingTableData.getRoutingTableName());
					nestedObjectDetails.setModuleName(EliteViewCommonConstant.MSISDN_BASED_ROUTING_TABLE);
					
					nestedObjectDetailsList.add(nestedObjectDetails);
				}else{
					jsonObject.put("Subscriber Routing 1", "");
				}
			}
			
			if( translationMappingConfData != null ){
				
				jsonObject.put("Translation Mapping", translationMappingConfData.getName()); /* Replace Translation mapping with Translation mapping*/
				
				NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
				nestedObjectDetails.setKey("Translation Mapping");
				nestedObjectDetails.setModuleId(translationMappingConfData.getTranslationMapConfigId());
				nestedObjectDetails.setModuleInstanceName(translationMappingConfData.getName());
				nestedObjectDetails.setModuleName(EliteViewCommonConstant.TRANSLATION_MAPPING);
				
				nestedObjectDetailsList.add(nestedObjectDetails);
				
			}else if( copyPacketTranslationConfData != null ){
				
				jsonObject.put("Translation Mapping", copyPacketTranslationConfData.getName()); /* Replace Translation mapping with copy packet*/
				
				NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
				nestedObjectDetails.setKey("Translation Mapping");
				nestedObjectDetails.setModuleId(copyPacketTranslationConfData.getCopyPacketTransConfId());
				nestedObjectDetails.setModuleInstanceName(copyPacketTranslationConfData.getName());
				nestedObjectDetails.setModuleName(EliteViewCommonConstant.COPY_PACKET_CONFIG);
				
				nestedObjectDetailsList.add(nestedObjectDetails);
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DIAMETER_ROUTING_CONFIG+diameterRoutingConfData.getRoutingConfigId());
			
			String json = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Diameter Routing Configuration : " + json.toString());
			return json.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDigestConfig(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDigestConfig() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Digest Configuration .");
			
			DigestConfBLManager digestConfBLManager = new DigestConfBLManager();
			DigestConfigInstanceData digestConfigInstanceData = new DigestConfigInstanceData();
			digestConfigInstanceData = digestConfBLManager.getDigestConfigDataById(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String digestConfigDataJson = gson.toJson(digestConfigInstanceData);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(digestConfigDataJson.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DIGEST_CONFIGURATION+digestConfigInstanceData.getDigestConfId());
			
			String digestConfigJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Digest Configuration : " + digestConfigJson.toString());
			return digestConfigJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleAlertConfiguration(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleAlertConfiguration() method of " + this.getClass());
		try {
			
			Logger.logInfo(MODULE, "View data of Alert Listener .");
			
			AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
			AlertListenerData alertListenerData = new AlertListenerData();
			
			alertListenerData.setListenerId(instanceId);
			alertListenerData = alertListenerBLManager.getAlertListenerDataById(instanceId);
			
			AlertFileListenerData alertFileListenerData = null;
			AlertTrapListenerData alertTrapListenerData  = null;
			SYSLogAlertListenerData alertSysLogAlertListenerData  = null;
			String alertListenerDataJson = null;
			
			if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(alertListenerData.getTypeId())){
				alertFileListenerData = (AlertFileListenerData) alertListenerData.getAlertListener();
			}else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(alertListenerData.getTypeId())){
				alertTrapListenerData = (AlertTrapListenerData) alertListenerData.getAlertListener();
			}else if(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(alertListenerData.getTypeId())){
				alertSysLogAlertListenerData = (SYSLogAlertListenerData) alertListenerData.getAlertListener();
			}
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			
			if( alertFileListenerData != null ){
				alertListenerDataJson = gson.toJson(alertFileListenerData);
			}else if( alertTrapListenerData != null ){
				alertListenerDataJson = gson.toJson(alertTrapListenerData);
			}else if( alertSysLogAlertListenerData != null){
				alertListenerDataJson = gson.toJson(alertSysLogAlertListenerData);
			}
			
			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(alertListenerDataJson);
			
			JSONObject listenerObject = new JSONObject();
			listenerObject.put("Name", alertListenerData.getName());
			listenerObject.put("Listener Type", alertListenerData.getAlertListenerTypeData().getTypeName());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    listenerObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(listenerObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_ALERT_CONFIGURATION+alertListenerData.getListenerId());
			
			String digestConfigJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Digest Configuration : " + digestConfigJson.toString());
			return digestConfigJson.toString();
			
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleEAPConfiguration(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleEAPConfiguration() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of EAP Configuration .");
			
			EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
			EAPConfigData eapConfigData = new EAPConfigData();
			eapConfigData = eapConfigBLManager.getEapConfigurationDataById(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String eapConfigDataJson = gson.toJson(eapConfigData);
			
			String labelDefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(eapConfigData.getDefaultNegiotationMethod());
			String strEnabledAuthMethods = EAPConfigUtils.convertEnableAuthMethodToLabelString(eapConfigData.getEnabledAuthMethods());
			
			JSONObject jsonObject = new JSONObject();
			jsonObject = JSONObject.fromObject(eapConfigDataJson);
			jsonObject.put("Default Negotiation Method", labelDefaultNegotiationMethod);
			jsonObject.put("Enabled Auth Methods", strEnabledAuthMethods);
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(jsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_EAP_CONFIGURATION+eapConfigData.getEapId());
			
			String digestConfigJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of EAP Configuration : " + digestConfigJson.toString());
			return digestConfigJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleRoutingTableConfiguration(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleRoutingTableConfiguration() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Routing Table Configuration .");
			
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			DiameterRoutingTableData diameterRoutingTableData =diameterRoutingConfBLManager.getDiameterRoutingTableData(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String diameterRoutingTableDataJson = gson.toJson(diameterRoutingTableData);

			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(diameterRoutingTableDataJson.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_DIAMETER_ROUTING_TABLE+diameterRoutingTableData.getRoutingTableId());
			
			String diameterRoutingTableJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Routing Table Configuration : " + diameterRoutingTableJson.toString());
			return diameterRoutingTableJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDBAuthDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDBAuthDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of DB Auth Driver");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(instanceId);
			
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			IDatabaseDSData dataBaseDsData = databaseDSBLManager.getDatabaseDSDataById(dbAuthDriverData.getDatabaseId());
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Datasource");
			nestedObjectDetails.setModuleId(dataBaseDsData.getDatabaseId());
			nestedObjectDetails.setModuleInstanceName(dataBaseDsData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.DATABASE_DATASOURCE);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String radiusDbAuthDriverDataJson = gson.toJson(dbAuthDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(radiusDbAuthDriverDataJson);
			
			JSONObject radiusDbAuthJsonObject = new JSONObject();
			radiusDbAuthJsonObject.put("Instance Name", driverInstanceData.getName());
			radiusDbAuthJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    radiusDbAuthJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusDbAuthJsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String radiusDbAuthDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of DB Auth Driver Configuration : " + radiusDbAuthDriverJson.toString());
			return radiusDbAuthDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;	
	}

	@Override
	public String handleHTTPAuthDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleHTTPAuthDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of HTTP Auth Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			HttpAuthDriverData httpAuthDriverData = driverBLManager.getHttpDriverByDriverInstanceId(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String httpAuthDriverDataJson = gson.toJson(httpAuthDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(httpAuthDriverDataJson);
			
			JSONObject radiusHttpAuthJsonObject = new JSONObject();
			radiusHttpAuthJsonObject.put("Instance Name", driverInstanceData.getName());
			radiusHttpAuthJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    radiusHttpAuthJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusHttpAuthJsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String httpAuthDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of HTTP Auth Driver Configuration : " + httpAuthDriverJson.toString());
			return httpAuthDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleLDAPAuthDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleLDAPAuthDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of LDAP Auth Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			LDAPAuthDriverData ldapAuthDriverData = driverBLManager.getLdapDriverByDriverInstanceId(instanceId);
			
			LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
			LDAPDatasourceData ldapDatasourceData = (LDAPDatasourceData) ldapDatasourceBLManager.getLDAPDatabaseDataById(ldapAuthDriverData.getLdapDsId());
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Database Name");
			nestedObjectDetails.setModuleId(ldapDatasourceData.getLdapDsId());
			nestedObjectDetails.setModuleInstanceName(ldapDatasourceData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.LDAP_DATASOURCE);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String ldapAuthDriverDataJson = gson.toJson(ldapAuthDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(ldapAuthDriverDataJson);
			
			JSONObject radiusLDAPAuthJsonObject = new JSONObject();
			radiusLDAPAuthJsonObject.put("Instance Name", driverInstanceData.getName());
			radiusLDAPAuthJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    radiusLDAPAuthJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusLDAPAuthJsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String ldapAuthDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of LDAP Auth Driver Configuration : " + ldapAuthDriverJson.toString());
			return ldapAuthDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleMapGWAuthDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleMapGWAuthDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Map Gw Auth Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			MappingGatewayAuthDriverData mapgwAuthDriver = driverBLManager.getMappingGWDataByDriverInstanceId(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String mapGwAuthDriverDataJson = gson.toJson(mapgwAuthDriver);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(mapGwAuthDriverDataJson);
			
			JSONObject radiusMapGwAuthJsonObject = new JSONObject();
			radiusMapGwAuthJsonObject.put("Instance Name", driverInstanceData.getName());
			radiusMapGwAuthJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    radiusMapGwAuthJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusMapGwAuthJsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String httpAuthDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Map Gw Auth Driver Configuration : " + httpAuthDriverJson.toString());
			return httpAuthDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleHSSDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleHSSDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of HSS Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			HssAuthDriverData hssAuthDriverData = driverBLManager.getHSSDriverData(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String hssAuthDriverDataJson = gson.toJson(hssAuthDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(hssAuthDriverDataJson);
			
			JSONObject radiusHssAuthJsonObject = new JSONObject();
			radiusHssAuthJsonObject.put("Instance Name", driverInstanceData.getName());
			radiusHssAuthJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    radiusHssAuthJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusHssAuthJsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String radiusHSSDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of HSS Auth Driver Configuration : " + radiusHSSDriverJson.toString());
			return radiusHSSDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleUserFileAuthDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleUserFileAuthDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of User file Auth Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			UserFileAuthDriverData userFileAuthDriver = driverBLManager.getUserFileDriverByDriverInstanceId(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String userfileAuthDriverDataJson = gson.toJson(userFileAuthDriver);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(userfileAuthDriverDataJson);
			
			JSONObject radiusUserFileAuthJsonObject = new JSONObject();
			radiusUserFileAuthJsonObject.put("Instance Name", driverInstanceData.getName());
			radiusUserFileAuthJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    radiusUserFileAuthJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusUserFileAuthJsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String radiusUserfileAuthDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of User file Auth Driver Configuration : " + radiusUserfileAuthDriverJson.toString());
			return radiusUserfileAuthDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleWebserviceAuthDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleWebserviceAuthDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Web Service Auth Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			WebServiceAuthDriverData webServiceAuthDriver = driverBLManager.getWebServiceDriverByDriverInstanceId(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String webserviceAuthDriverDataJson = gson.toJson(webServiceAuthDriver);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(webserviceAuthDriverDataJson);
			
			JSONObject radiusWebServiceAuthJsonObject = new JSONObject();
			radiusWebServiceAuthJsonObject.put("Instance Name", driverInstanceData.getName());
			radiusWebServiceAuthJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    radiusWebServiceAuthJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusWebServiceAuthJsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String radiusWebServiceAuthDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Web Service Auth Driver Configuration : " + radiusWebServiceAuthDriverJson.toString());
			return radiusWebServiceAuthDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleClassicCSVDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleClassicCSVDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Classic CSV Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			ClassicCSVAcctDriverData classicCSVAcctDriverData = driverBLManager.getClassicCsvDriverByDriverInstanceId(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String classocCSVDriverDataJson = gson.toJson(classicCSVAcctDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(classocCSVDriverDataJson);
			
			JSONObject classicCSVDriverJsonObject = new JSONObject();
			classicCSVDriverJsonObject.put("Instance Name", driverInstanceData.getName());
			classicCSVDriverJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    classicCSVDriverJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(classicCSVDriverJsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String classicCSVDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Classic CSV Driver Configuration : " + classicCSVDriverJson.toString());
			return classicCSVDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDBAcctDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDBAcctDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of DB Acct Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			DBAcctDriverData dbAcctDriverData = driverBLManager.getDbAcctDriverByDriverInstanceId(instanceId);
			
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			IDatabaseDSData dataBaseDsData = databaseDSBLManager.getDatabaseDSDataById(dbAcctDriverData.getDatabaseId());
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Database Datasource");
			nestedObjectDetails.setModuleId(dataBaseDsData.getDatabaseId());
			nestedObjectDetails.setModuleInstanceName(dataBaseDsData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.DATABASE_DATASOURCE);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String dbAcctDataJson = gson.toJson(dbAcctDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(dbAcctDataJson);
			
			JSONObject dbAcctDataJsonObject = new JSONObject();
			dbAcctDataJsonObject.put("Instance Name", driverInstanceData.getName());
			dbAcctDataJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    dbAcctDataJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(dbAcctDataJsonObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String classicCSVDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of DB Acct Driver Configuration : " + classicCSVDriverJson.toString());
			return classicCSVDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDetailLocalDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleDetailLocalDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Detail Local Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			DetailLocalAcctDriverData detailLocalAcctdriver = driverBLManager.getDetailLocalDriverByDriverInstanceId(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String detailLocalJson = gson.toJson(detailLocalAcctdriver);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(detailLocalJson);
			
			JSONObject detailLocalJsonObject = new JSONObject();
			detailLocalJsonObject.put("Instance Name", driverInstanceData.getName());
			detailLocalJsonObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    detailLocalJsonObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(detailLocalJsonObject.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String detailLocalCSVDriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Detail Local Driver Configuration : " + detailLocalCSVDriverJson.toString());
			return detailLocalCSVDriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleCrestelRatingTranslationDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleCrestelOCSV2TranslationDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Crestel OCSv2 Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			CrestelRatingDriverData crestelRatingDriverData = driverBLManager.getCrestelRatingDriverData(driverInstanceData.getDriverInstanceId());
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData translationMappingConfData= translationMappingConfBLManager.getTranslationMappingConfData(crestelRatingDriverData.getTransMapConfId());
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Translation Mapping Configuration");
			nestedObjectDetails.setModuleId(translationMappingConfData.getTranslationMapConfigId());
			nestedObjectDetails.setModuleInstanceName(translationMappingConfData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.TRANSLATION_MAPPING);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String crestelRatingJson = gson.toJson(crestelRatingDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(crestelRatingJson);
			
			JSONObject crestelRatingObject = new JSONObject();
			crestelRatingObject.put("Instance Name", driverInstanceData.getName());
			crestelRatingObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    crestelRatingObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(crestelRatingObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String crestelOCSv2DriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Crestel OCSv2 Driver Configuration : " + crestelOCSv2DriverJson.toString());
			return crestelOCSv2DriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleCrestelChargingDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleCrestelChargingDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Crestel Charging Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			CrestelChargingDriverData crestelChargingDriverData = driverBLManager.getCrestelChargingDriverData(instanceId);
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData translationMappingConfData= translationMappingConfBLManager.getTranslationMappingConfData(crestelChargingDriverData.getTransMapConfId());
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Translation Mapping Configuration");
			nestedObjectDetails.setModuleId(translationMappingConfData.getTranslationMapConfigId());
			nestedObjectDetails.setModuleInstanceName(translationMappingConfData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.TRANSLATION_MAPPING);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String crestelChargingDriverJson = gson.toJson(crestelChargingDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(crestelChargingDriverJson);
			
			JSONObject crestelChargingObject = new JSONObject();
			crestelChargingObject.put("Instance Name", driverInstanceData.getName());
			crestelChargingObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    crestelChargingObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(crestelChargingObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String crestelOCSv2DriverJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Crestel Charging Driver Configuration : " + crestelOCSv2DriverJson.toString());
			return crestelOCSv2DriverJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleDiameterChargingDriver(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleCrestelChargingDriver() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Crestel Charging Driver .");
			
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instanceId);
			DiameterChargingDriverData diameterChargingDriverData = driverBLManager.getDiameterChargingDataByDriverInstanceId(instanceId);
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData translationMappingConfData= translationMappingConfBLManager.getTranslationMappingConfData(diameterChargingDriverData.getTransMapConfId());
			
			List<NestedObjectDetails> nestedObjectDetailsList = new ArrayList<NestedObjectDetails>();
			
			NestedObjectDetails nestedObjectDetails = new NestedObjectDetails();
			nestedObjectDetails.setKey("Translation Mapping Configuration");
			nestedObjectDetails.setModuleId(translationMappingConfData.getTranslationMapConfigId());
			nestedObjectDetails.setModuleInstanceName(translationMappingConfData.getName());
			nestedObjectDetails.setModuleName(EliteViewCommonConstant.TRANSLATION_MAPPING);
			
			nestedObjectDetailsList.add(nestedObjectDetails);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String diameterChargingDriverJson = gson.toJson(diameterChargingDriverData);

			JSONObject jsonObject = new  JSONObject();
			jsonObject = JSONObject.fromObject(diameterChargingDriverJson);
			
			JSONObject crestelChargingObject = new JSONObject();
			crestelChargingObject.put("Instance Name", driverInstanceData.getName());
			crestelChargingObject.put("Description", driverInstanceData.getDescription());
			
			for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    crestelChargingObject.put(key, jsonObject.get(key));
			}
			
			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(crestelChargingObject.toString());
			moduleDetails.setNestedObjectDetailsList(nestedObjectDetailsList);
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_DRIVER+driverInstanceData.getDriverInstanceId());
			
			String diameterChargingDriverDataJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Diameter Charging Driver Configuration : " + diameterChargingDriverDataJson.toString());
			return diameterChargingDriverDataJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}

	@Override
	public String handleServerCertificate(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleServerCertificate() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Server Certificate ");
			ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();	
			ServerCertificateData serverCertificateData = serverCertificateBLManager.view(instanceId);
			setServerCertificateDetail(serverCertificateData);
			setPrivateKeyDetail(serverCertificateData);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String serverCertificateJson = gson.toJson(serverCertificateData);

			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(serverCertificateJson.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_SERVER_CERTIFICATE+serverCertificateData.getServerCertificateId());
			
			String diameterChargingDriverDataJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Server Certificate Configuration : " + diameterChargingDriverDataJson.toString());
			return diameterChargingDriverDataJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}
	
	@Override
	public String handleRadiusESIGroup(String instanceId, HttpServletRequest request) throws Exception {
		Logger.logDebug(MODULE, "Entered handleRadiusESIGroup() method of " + this.getClass());
		try {
			Logger.logInfo(MODULE, "View data of Radius ESI Group .");
			
			RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();
			
			RadiusESIGroupData radiusESIGroupData = radiusESIGroupBLManager.getRadiusESIGroupById(instanceId);
			
			/* convert object to json format */
			Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
			String radiusESIGroupDataJson = gson.toJson(radiusESIGroupData);

			ModuleDetails moduleDetails = new ModuleDetails();
			moduleDetails.setJsonObject(radiusESIGroupDataJson.toString());
			moduleDetails.setViewAdvancedDetailsLink(request.getContextPath()+EliteViewCommonConstant.VIEW_RADIUS_ESI_GROUP+radiusESIGroupData.getId());
			
			String radiusESIGroupDataTableJson = new Gson().toJson(moduleDetails);
			Logger.logInfo(MODULE, "JSON String of Radius ESI Group : " + radiusESIGroupDataTableJson.toString());
			return radiusESIGroupDataTableJson.toString();
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting Radius ESI Group information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
		
	}
	
	/**
	 * Set the Public Certificate Details to show details on JSP Page.
	 * @param data
	 * @throws Exception
	 */
	private void setServerCertificateDetail(ServerCertificateData data) throws InvalidPrivateKeyException{
		try {
			byte[] fileData=data.getCertificate();
			if(fileData!=null){

				ByteArrayInputStream bais=new ByteArrayInputStream(fileData);
				CertificateFactory cf;

				cf = CertificateFactory.getInstance("X.509");

				Collection<? extends Certificate> certList=cf.generateCertificates(bais);		

				if(certList!=null){
					for(Certificate certificate : certList) {

						X509Certificate x509Cert=(X509Certificate)certificate;		

						//Version
						data.setVersion(x509Cert.getVersion()+"");	

						//Serial No
						if(x509Cert.getSerialNumber().toString()!=null){
							data.setSerialNo(x509Cert.getSerialNumber().toString());	
						}
						//Signature Algorithm
						if(x509Cert.getSigAlgName()!=null){
							data.setSignatureAlgo(x509Cert.getSigAlgName());	
						}
						//Issuer
						if(x509Cert.getIssuerX500Principal().toString()!=null){
							data.setIssuer(x509Cert.getIssuerX500Principal().toString());				
						}
						Map<String,String> oidMap=new HashMap<String,String>();
						oidMap.put("TE","2.5.4.15");

						//Subject
						if(x509Cert.getSubjectX500Principal().toString()!=null){
							data.setSubject(x509Cert.getSubjectX500Principal().toString());	
						}
						//x509Cert.getExtensionValue("");
						SimpleDateFormat simpleDate=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");

						//Valid Form
						if(x509Cert.getNotBefore().toString()!=null){
							data.setValidFrom(simpleDate.format(x509Cert.getNotBefore()).toString());	
						}
						//Valid To Date
						if(x509Cert.getNotAfter().toString()!=null){
							data.setValidTo(simpleDate.format(x509Cert.getNotAfter()).toString());	
						}
						//Public Key
						if(x509Cert.getPublicKey().toString()!=null){
							data.setPublicKey(x509Cert.getPublicKey().toString());	
						}
						//Basic Constraint
						data.setBasicConstraint(x509Cert.getBasicConstraints()+"");

						//Subject Unique ID			
						if(x509Cert.getSubjectUniqueID()!=null){
							data.setSubjectUniqueID(x509Cert.getSubjectUniqueID().toString());
						}

						//Key Usage
						boolean[] b1=x509Cert.getKeyUsage();
						if(b1!=null){
							int i=0;
							if(x509Cert.getKeyUsage()!=null){
								String keyUsage="";	
								for(Boolean b :x509Cert.getKeyUsage()){
									if(b==true){
										switch(i){
										case 0:keyUsage=keyUsage+" Digital Signature,"; break;
										case 1:keyUsage=keyUsage+" Non Repudiation,"; break;
										case 2:keyUsage=keyUsage+" Key Encipherment,"; break;
										case 3:keyUsage=keyUsage+" Data Encipherment,"; break;
										case 4:keyUsage=keyUsage+" Key Agreement,"; break;	
										case 5:keyUsage=keyUsage+" Key Certificate Sign,"; break;
										case 6:keyUsage=keyUsage+" CRL Sign,"; break;
										case 7:keyUsage=keyUsage+" Encipher Only,"; break;
										case 8:keyUsage=keyUsage+" Decipher Only,"; break;					
										}
									}	
									i++;
								}
								keyUsage=keyUsage.substring(0,keyUsage.length()-1);
								data.setKeyUsage(keyUsage);
							}
						}

						//Subject Alternative Name
						if(x509Cert.getSubjectAlternativeNames()!=null){
							String subjectAltName = "";
							for(List<?> x:x509Cert.getSubjectAlternativeNames()){
								String subName=x.toString();
								subjectAltName=subjectAltName+subName.substring(subName.indexOf(",")+1, subName.length()-1)+",";					
							}
							subjectAltName=subjectAltName.substring(0, subjectAltName.length()-1);
							data.setSubjectAltName(subjectAltName);
						}

						//Issuer Alternative Name
						if(x509Cert.getIssuerAlternativeNames()!=null){
							String issuerAltName = "";
							for(List<?> x:x509Cert.getIssuerAlternativeNames()){
								String issuerName=x.toString();
								issuerAltName=issuerAltName+issuerName.substring(issuerName.indexOf(",")+1, issuerName.length()-1)+",";					
							}
							issuerAltName=issuerAltName.substring(0, issuerAltName.length()-1);
							data.setIssuerAltName(issuerAltName);
						}
						
						if(data.getSubject() != null){
							
							if (data.getSubject() != null) { 
								 String[] subjectDetail = data.getSubject().split(",");
								 	for (String str : subjectDetail) {
								 			if (str.contains("CN=")) { 
								 				String strSubjectName=str.split("CN=")[1];
								 				data.setStrSubjectName(strSubjectName);
								 			} 
								 	}
								} 
							
						}
						
						if(data.getIssuer() != null){
							if (data.getIssuer() != null) { 
								 String[] subjectDetail = data.getIssuer().split(",");
								 	for (String str : subjectDetail) {
								 			if (str.contains("CN=")) { 
								 				String strIssuerName=str.split("CN=")[1];
								 				data.setStrIssuerName(strIssuerName);
								 			} 
								 	}
								} 
						}
						break;
					}		
				}
			}
		}catch (Exception e) {
			throw new InvalidPrivateKeyException("Invalid Public Certificate", CertificateRemarks.INVALID_CERTIFICATE);
		}
	}

	/**
	 * Set the Private key Details to show details on JSP page.
	 * @param data
	 * @throws Exception
	 */
	private void setPrivateKeyDetail(ServerCertificateData data) throws InvalidPrivateKeyException{

		if(data==null){
			throw new InvalidPrivateKeyException("Invalid PrivateKey", CertificateRemarks.INVALID_PRIVATE_KEY);
		}
		byte[] keyData=data.getPrivateKey();
		if(keyData==null){
			throw new InvalidPrivateKeyException("Invalid PrivateKey", CertificateRemarks.INVALID_PRIVATE_KEY);
		}
		try {	

			PrivateKey privateKey;
			if(data.getPrivateKeyPassword()!=null && data.getPrivateKeyPassword().length() > 0){

				javax.crypto.EncryptedPrivateKeyInfo encPriKey;

				encPriKey = new javax.crypto.EncryptedPrivateKeyInfo(keyData);

				/* Decrypt  password */
				String decryptedPassword = PasswordEncryption.getInstance().decrypt(data.getPrivateKeyPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				data.setPrivateKeyPassword(decryptedPassword);
				
				Cipher cipher = Cipher.getInstance(encPriKey.getAlgName());
				PBEKeySpec pbeKeySpec = new PBEKeySpec(data.getPrivateKeyPassword().toCharArray());
				SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
				cipher.init(Cipher.DECRYPT_MODE,secKeyFactory.generateSecret(pbeKeySpec),encPriKey.getAlgParameters());
				PKCS8EncodedKeySpec keySpec = encPriKey.getKeySpec(cipher);

				KeyFactory keyFactory = KeyFactory.getInstance(data.getPrivateKeyAlgorithm());
				privateKey = keyFactory.generatePrivate(keySpec);					

			}else{
				ByteArrayInputStream bais=new ByteArrayInputStream(keyData);
				byte[] keyByte=new byte[keyData.length];
				bais.read(keyByte,0,bais.available());
				bais.close();
				PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyByte);				
				KeyFactory kf=KeyFactory.getInstance(data.getPrivateKeyAlgorithm());
				privateKey=kf.generatePrivate(keySpec);

			}
			data.setPrivateKeyData(privateKey.toString());

		} catch (Exception e) {
			throw new InvalidPrivateKeyException("Invalid PrivateKey", CertificateRemarks.INVALID_PRIVATE_KEY);
		}
	}
	
	public ActionForward getDriverTypeByInstanceId(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered getDriverTypeByInstanceId() method of " + this.getClass());
		try {
			response.setContentType("text/html");
			String instance_Id = request.getParameter("instance_Id");
			Logger.logInfo(MODULE, "Instance Id :-" + instance_Id);
			String driverModule = "";
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(instance_Id);
			DriverTypeData driverTypeData = driverBLManager.getDriverTypeDataById(driverInstanceData.getDriverTypeId());
			
			if("OPENDB_AUTH_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_DB_AUTH_DRIVER".equals(driverTypeData.getAlias()) ){
				driverModule = EliteViewCommonConstant.DB_AUTH_DRIVER;
			}else if("OPENDB_ACCT_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_DB_ACCT_DRIVER".equals(driverTypeData.getAlias()) ){
				driverModule = EliteViewCommonConstant.DB_ACCT_DRIVER;
			}else if("LDAP_AUTH_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_LDAP_AUTH_DRIVER".equals(driverTypeData.getAlias()) ){
				driverModule = EliteViewCommonConstant.LDAP_AUTH_DRIVER;
			}else if("USERFILE_AUTH_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_USERFILE_AUTH_DRIVER".equals(driverTypeData.getAlias()) ){
				driverModule = EliteViewCommonConstant.USER_FILE_AUTH_DRIVER;
			}else if("DETAIL_LOCAL_ACCT_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_DETAIL_LOCAL_ACCT_DRIVER".equals(driverTypeData.getAlias()) ){
				driverModule = EliteViewCommonConstant.DETAIL_LOCAL_ACCT_DRIVER;
			}else if("CLASSIC_CSV_ACCT_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_CLASSIC_CSV_ACCT_DRIVER".equals(driverTypeData.getAlias()) || "RM_CLASSIC_CSV_ACCT_DRIVER".equals(driverTypeData.getAlias())){
				driverModule = EliteViewCommonConstant.CLASSIC_CSV_ACCT_DRIVER;
			}else if("MAPGW_AUTH_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_MAP_GATEWAY_AUTH_DRIVER".equals(driverTypeData.getAlias()) ){
				driverModule = EliteViewCommonConstant.MAP_GW_AUTH_DRIVER;
			}else if("CRESTEL_RATING_TRANSLATION_DRIVER".equals(driverTypeData.getAlias()) || "CRESTEL_OCS-V2_DRIVER".equals(driverTypeData.getAlias()) || "CRESTEL_OCS-V2_TRANSLATION_DRIVER".equals(driverTypeData.getAlias())){
				driverModule = EliteViewCommonConstant.CRESTEL_RATING_TRANSLATION_DRIVER;
			}else if("DIAMETER_CHARGING_DRIVER".equals(driverTypeData.getAlias())){
				driverModule = EliteViewCommonConstant.DIAMETER_CHARGING_DRIVER;
			}else if("WEB_SERVICE_AUTH_DRIVER".equals(driverTypeData.getAlias())){
				driverModule = EliteViewCommonConstant.WEB_SERVICE_AUTH_DRIVER;
			}else if("CRESTEL_CHARGING_DRIVER".equals(driverTypeData.getAlias())){
				driverModule = EliteViewCommonConstant.CRESTEL_CHARGING_DRIVER;
			}else if("RAD_HSS_AUTH_DRIVER".equals(driverTypeData.getAlias()) || "DIA_HSS_AUTH_DRIVER".equals(driverTypeData.getAlias())){
				driverModule = EliteViewCommonConstant.HSS_AUTH_DRIVER;
			}else if("HTTP_AUTH_DRIVER".equals(driverTypeData.getAlias()) || "DIAMETER_HTTP_AUTH_DRIVER".equals(driverTypeData.getAlias())){
				driverModule = EliteViewCommonConstant.HTTP_AUTH_DRIVER;
			}
			Logger.logInfo(MODULE, "configured module name is : " +driverModule );
			response.getWriter().write(driverModule);
		}catch (Exception e) {
			Logger.logError(MODULE,"Error while getting configuration information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return mapping.findForward(null);
	}
}
