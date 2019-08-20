package com.elitecore.elitesm.util.constants;

/**
 * @author     nayana.rathod
 * @date       17 March 2015
 * @Modulename EliteViewCommonConstant
 */

public class EliteViewCommonConstant {
	
	/** Module Specific Constants Used in view sub module details  */
	 public static final String SESSION_MANAGER = "SESSION MANAGER";   
	 public static final String DATABASE_DATASOURCE = "DATABASE DATASOURCE";
	 public static final String EXTENDED_RADIUS= "EXTENDED_RADIUS";
	 public static final String LDAP_DATASOURCE="LDAP_DATASOURCE";
	 public static final String TRUSTED_CLIENT_PROFILE = "TRUSTED_CLIENT_PROFILE";
	 public static final String DIAMETER_PEER_PROFILE = "DIAMETER_PEER_PROFILE";
	 public static final String RADIUS_ESI_GROUP = "RADIUS_ESI_GROUP";
	 public static final String DIAMETER_PEER = "DIAMETER_PEER";
	 public static final String IMSI_BASED_ROUTING_TABLE="IMSI_BASED_ROUTING_TABLE";
	 public static final String MSISDN_BASED_ROUTING_TABLE="MSISDN_BASED_ROUTING_TABLE";
	 public static final String DIAMETER_SESSION_MANAGER="DIAMETER_SESSION_MANAGER";
	 public static final String TRANSLATION_MAPPING="TRANSLATION_MAPPING";
	 public static final String COPY_PACKET_CONFIG="COPY_PACKET_CONFIG";
	 public static final String DIAMETER_ROUTING_CONFIG="DIAMETER_ROUTING_CONFIG";
	 public static final String DIGEST_CONFIGURATION="DIGEST_CONFIGURATION";
	 public static final String ALERT_CONFIGURATION="ALERT_CONFIGURATION";
	 public static final String EAP_CONFIGURATION="EAP_CONFIGURATION";
	 public static final String DIAMETER_ROUTING_TABLE="DIAMETER_ROUTING_TABLE";
	 public static final String SERVER_CERTIFICATE="SERVER_CERTIFICATE";
	 public static final String DIAMETER_PEER_GROUP="DIAMETER_PEER_GROUP";

	 /** Service Driver Constants */
	 public static final String DB_AUTH_DRIVER="DB_AUTH_DRIVER";
	 public static final String HTTP_AUTH_DRIVER="HTTP_AUTH_DRIVER";
	 public static final String LDAP_AUTH_DRIVER="LDAP_AUTH_DRIVER";
	 public static final String MAP_GW_AUTH_DRIVER="MAP_GW_AUTH_DRIVER";
	 public static final String HSS_AUTH_DRIVER="HSS_AUTH_DRIVER";
	 public static final String USER_FILE_AUTH_DRIVER="USER_FILE_AUTH_DRIVER";
	 public static final String WEB_SERVICE_AUTH_DRIVER="WEB_SERVICE_AUTH_DRIVER";
	 public static final String CLASSIC_CSV_ACCT_DRIVER="CLASSIC_CSV_ACCT_DRIVER";
	 public static final String DB_ACCT_DRIVER="DB_ACCT_DRIVER";
	 public static final String DETAIL_LOCAL_ACCT_DRIVER="DETAIL_LOCAL_ACCT_DRIVER";
	 public static final String CRESTEL_RATING_TRANSLATION_DRIVER="CRESTEL_OCSV2_TRANSLATION_DRIVER";
	 public static final String CRESTEL_CHARGING_DRIVER="CRESTEL_CHARGING_DRIVER";
	 public static final String DIAMETER_CHARGING_DRIVER="DIAMETER_CHARGING_DRIVER";
	 
	 /** View Advanced details links */
	 public static final String VIEW_SESSION_MANAGER_ADVANCED_DETAILS="/viewSessionManagerDetail.do?sminstanceid=";
	 public static final String VIEW_DATABASE_DATAOURCE_DETAILS="/viewDatabaseDS.do?databaseId=";
	 public static final String VIEW_EXTERNAL_SYSTEM_DETAILS="/viewESI.do?esiInstanceId=";
	 public static final String VIEW_LDAP_DATASOURCE="/viewLDAPDS.do?ldapDsId=";
	 public static final String VIEW_TRUSTED_CLIENT_PROFILE="/viewClientProfile.do?profileId=";
	 public static final String VIEW_RADIUS_ESI_GROUP = "/viewRadiusESIGroup.do?esiGroupId=";
	 public static final String VIEW_DIAMETER_PEER_PROFILE="/viewDiameterPeerProfile.do?peerProfileId=";
	 public static final String VIEW_DIAMETER_PEER="/viewDiameterPeer.do?peerId=";
	 public static final String VIEW_IMSI_BASED_ROUTING_TABLE="/initViewIMSIBasedRouting.do?routingTableId=";
	 public static final String VIEW_MSISDN_BASED_ROUTING_TABLE="/viewMSISDNBasedRouting.do?routingTableId=";
	 public static final String VIEW_DIAMETER_SESSION_MANAGER="/viewDiameterSessionManager.do?action=view&sessionManagerId=";
	 public static final String VIEW_TRANSLATION_MAPPING="/viewTranslationMappingConfigBasicDetail.do?translationMapConfigId=";
	 public static final String VIEW_COPY_PACKET_CONFIG="/viewCopyPacketConfigBasicDetail.do?copyPacketTransConfId=";
	 public static final String VIEW_DIAMETER_ROUTING_CONFIG="/viewDiameterRoutingTable.do?routingConfigId=";
	 public static final String VIEW_DIGEST_CONFIGURATION="/viewDigestConf.do?digestConfId=";
	 public static final String VIEW_ALERT_CONFIGURATION="/viewAlertListener.do?listenerId=";
	 public static final String VIEW_EAP_CONFIGURATION="/viewEAPConfig.do?eapId=";
	 public static final String VIEW_DIAMETER_ROUTING_TABLE="/diameterRoutingTable.do?actionType=tablewiserouting&routingTableId=";
	 public static final String VIEW_SERVER_CERTIFICATE="/serverAllCertificates.do?method=view&serverCertificateId=";
	 public static final String VIEW_DIAMETER_PEER_GROUP="/viewDiameterPeerGroup.do?peerGroupId=";

	 /** Service Driver Constants */
	 public static final String VIEW_RADIUS_DRIVER="/viewDriverInstance.do?action=view&driverInstanceId=";
	 
}
