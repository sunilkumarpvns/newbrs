package com.elitecore.elitesm.web.core.system.referencialdata.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.certificate.ServerCertificateBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.logger.Logger;

/** This DAO is used for Audit purpose only, where any entity is referential or foreign key 
    at that time it will fetch name of entity */

public class EliteSMReferencialDAO {

	private static final String MODULE = "EliteSMReferencialDAO";
	
	/** Fetching Database Data source Details */
	public static String fetchDatabaseDatasourceData(String databaseDatasourceId) {
		String datasourceName = "";
		try{
			if(Strings.isNullOrBlank(databaseDatasourceId) == false){
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				IDatabaseDSData databaseDsdata = databaseDSBLManager.getDatabaseDSDataById(databaseDatasourceId);
				datasourceName = databaseDsdata.getName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return datasourceName;
	}

	/** Fetching EAP Configuration Details */
	public static String fetchEAPConfigurationDetails(String eapConfigId) {
		String eapConfigurationName = "";
		try{
			if(Strings.isNullOrBlank(eapConfigId) == false){
				EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
				EAPConfigData eapConfigData = new EAPConfigData();
				eapConfigData = eapConfigBLManager.getEapConfigurationDataById(eapConfigId);
				eapConfigurationName = eapConfigData.getName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return eapConfigurationName;
	}
	
	
	/** Fetching Server Certificate Details */
	public static String fetchServerCertificateDetails(String serverCertificateId) {
		String serverCertificateName = "";
		try{
			if(Strings.isNullOrBlank(serverCertificateId) == false){
				ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
				ServerCertificateData serverCertificateData = serverCertificateBLManager.getServerCertificateById(serverCertificateId);
				serverCertificateName = serverCertificateData.getServerCertificateName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return serverCertificateName;
	}

	/** Fetching CipherSuit List based of ciphersuite digit*/
	public static String fetchCipherSuitList(String ciphersuiteList) {
		String strCipherSuit="";
		
		if(ciphersuiteList !=null){
			String cipherSuitString = ciphersuiteList;
			String[] cipherSuiteArray=cipherSuitString.split(",");
			List<String> cipherSuitValueStrings = new ArrayList<String>() ;
			Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
			for (int i = 0; i < cipherSuiteArray.length; i++) {
					if(cipherSuiteArray[i] != null || cipherSuiteArray[i] != ""){
						for(CipherSuites cipherSuites2:cipherSuites){
							CipherSuites strCipherSuitesName=CipherSuites.fromCipherCode(cipherSuites2.code);
							String code=String.valueOf(strCipherSuitesName.code);
							if(code.equals(cipherSuiteArray[i])){
								cipherSuitValueStrings.add(strCipherSuitesName.name());
								if(strCipherSuit == ""){
									strCipherSuit=strCipherSuitesName.name();
								}else{
									strCipherSuit=strCipherSuit+","+strCipherSuitesName.name();
								}
							}
						}
					}
				}
		}
		
		return strCipherSuit;
	}

	/** Fetching Translation Mapping Details */
	public static String fetchTranslationMappingData( String translationMapConfigId) {
		String translationMappingName = "-";
		try{
			if(Strings.isNullOrBlank(translationMapConfigId) == false){
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				TranslationMappingConfData translationMappingData = translationMappingConfBLManager.getTranslationMappingConfData(translationMapConfigId);
				translationMappingName = translationMappingData.getName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return translationMappingName;
	}
	
	/** Fetching Client Type Details */
	public static String fetchClientTypeData( Long clientTypeId ){
		String clientTypeName = "-";
		try{
			if( clientTypeId != null &&  clientTypeId > 0 ){
				ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
				ClientTypeData clientTypeData = clientProfileBLManager.getClientTypeData(clientTypeId);
				clientTypeName = clientTypeData.getClientTypeName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return clientTypeName;
	}
	
	/** Fetching Vendor Details */
	public static String fetchVendorData( String vendorId ){
		String vendorName = "-";
		try{
			if( Strings.isNullOrBlank(vendorId) == false){
				ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
				VendorData vendorData = clientProfileBLManager.getVendorData(vendorId);
				vendorName = vendorData.getVendorName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return vendorName;
	}
	
	/** Fetching Diameter Peer Profile details */
	public static String fetchDiameterPeerProfileData( String peerProfileId ){
		String diameterPeerProfileName = "";
		try{
			if(Strings.isNullOrBlank(peerProfileId) == false){
				DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();
				
				DiameterPeerProfileData diameterPeerProfileData = blManager.getDiameterPeerProfileById(peerProfileId);

				diameterPeerProfileName = diameterPeerProfileData.getProfileName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return diameterPeerProfileName;
	}
	
	/** Fetching Diameter Routing Table details */
	public static String fetchDiameterRoutingTableData( String diameterRoutingId ){
		String diameterRoutingtableName = "";
		try{
			if(Strings.isNullOrBlank(diameterRoutingId) == false){
				
				DiameterRoutingConfBLManager blManager = new DiameterRoutingConfBLManager();
				DiameterRoutingTableData diameterRoutingTableData = blManager.getDiameterRoutingTableData(diameterRoutingId);
				diameterRoutingtableName = diameterRoutingTableData.getRoutingTableName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return diameterRoutingtableName;
	}
	
	/** Fetching Copy Packet Mapping details */
	public static String fetchCopypacketMappingData( String copyPacketMapConfigId) {
		String copyPacketMappingName = "-";
		try{
			if(Strings.isNullOrBlank(copyPacketMapConfigId) == false){
				CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
				CopyPacketTranslationConfData copyPacketMappingData = blManager.getCopyPacketTransMapConfigData(copyPacketMapConfigId);
				copyPacketMappingName = copyPacketMappingData.getName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return copyPacketMappingName;
	}
	
	/** Fetch IMSI Based Routing Table Data */
	public static String fetchIMSIBasedRoutingTableData( String routingTableId) {
		String routingTableName = "-";
		try{
			if(Strings.isNullOrBlank(routingTableId) == false){
				IMSIBasedRoutingTableBLManager blManager = new IMSIBasedRoutingTableBLManager();
				IMSIBasedRoutingTableData imsiBasedRoutingTableData = blManager.getImsiBasedRoutingTableData(routingTableId);
				routingTableName = imsiBasedRoutingTableData.getRoutingTableName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return routingTableName;
	}
	
	/** Fetch MSISDN Based Routing Table Data */
	public static String fetchMSISDNBasedRoutingTableData( String routingTableId) {
		String routingTableName = "-";
		try{
			if(Strings.isNullOrBlank(routingTableId) == false){
				MSISDNBasedRoutingTableBLManager blManager = new MSISDNBasedRoutingTableBLManager();
				MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = blManager.getMSISDNBasedRoutingTableData(routingTableId);
				routingTableName = msisdnBasedRoutingTableData.getRoutingTableName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return routingTableName;
	}
	
	/** Fetch Diameter Peer Data */
	public static String fetchDiameterPeerData( String peerId) {
		String peerName = "-";
		try{
			if(Strings.isNullOrBlank(peerId) == false){
				DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
				DiameterPeerData diameterPeerData = diameterPeerBLManager.getDiameterPeerById(peerId);
				peerName = diameterPeerData.getName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return peerName;
	}

	/** Fetch Ldap datasource data */
	public static String fetchLDAPDatasourceData(String ldapDsId) {
		String ldapDsName = "-";
		try{
			if(Strings.isNullOrBlank(ldapDsId) == false){
				LDAPDatasourceBLManager ldapDsBLManager = new LDAPDatasourceBLManager();
				ILDAPDatasourceData ldapDatasourceDataDetails = ldapDsBLManager.getLDAPDatabaseDataById(ldapDsId);
				ldapDsName = ldapDatasourceDataDetails.getName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return ldapDsName;
	}

	/** Fetch Alert Type data */
	public static String fetchAlertTypeData(String typeId) {
		String alertTypeName = "-";
		try{
			if( typeId != null ){
				AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
				AlertTypeData alertTypeData = alertListenerBLManager.getAlertTypeData(typeId);
				alertTypeName = alertTypeData.getName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return alertTypeName;
	}
	
	public static String getAccessGroupNameByActionId(String actionId) {
		String groupName = "";
			AccessGroupBLManager accessGroupBlManager = new AccessGroupBLManager();
			try {
				 groupName = accessGroupBlManager.getAccessGroupName(actionId);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (DataManagerException e) {
				e.printStackTrace();
			}
		return groupName;
	}
	
	/** Fetch Diameter Peer Group Data */
	public static String fetchDiameterPeerGroupData( String peerGroupId) {
		String peerGroupName = "-";
		try{
			if(Strings.isNullOrBlank(peerGroupId) == false){
				DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
				DiameterPeerGroup diameterPeerGroup = diameterPeerGroupBLManager.getDiameterPeerGroupById(peerGroupId);
				peerGroupName = diameterPeerGroup.getPeerGroupName();
			}
		}catch( Exception e ){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			e.printStackTrace();
		}
		return peerGroupName;
	}
}
