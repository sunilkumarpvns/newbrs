package com.elitecore.corenetvertex.constants;

import java.util.*;

public enum PCRFKeyConstants {
	
	
	
	
 	REQUESTED_ACTION(false, false , false, false, "RequestedAction"),
 	CONTENT_TYPE(false, false , false,false, "ContentType"),
 	PCC_RULE_LIST(false, false, false, false, "PCCRuleList"),
	AUTH_FAILED_REASON(false, false , false, false, "AuthFailedReason"),
 	RESULT_CODE(false, false , false, false, "ResultCode"),
 	REQUEST_NUMBER(false, false , false, false, "RequestNumber"),
 	PREVIOUS_REQUEST_NUMBER(false, false , false, false, "PreviousRequestNumber"),
	PROFILE(false, false , false, false, "Profile"),
	REAL_TIME_MONITOR_INSTALL(false, true, false, false, "RealTimeMonitorInstall"),	
	CHECK_BALANCE_RESULT(false, false, false, false, "CheckBalanceResult"),
	PCRF_EVENT(false, false, false, false, "PCRFEvent"),
	UNKNOWN_KEY(false, false, false, false, "UnknownKey"),
	SERVICE_EVENT(false,false,false,false,"ServiceEvent"),
	SESSION_RE_AUTH(false,false,false,false,"SessionReAuth"),
	
	
	
	
	/*
	=================KEYS RELATED TO Request==================
 	*/
 	REQ_AAMBRDL(true, true, false, false,"Req.AAMBRDL"),
 	REQ_AAMBRUL(true, true, false, false,"Req.AAMBRUL"),
 	REQ_MBRDL(true, true, false, false,"Req.MBRDL"),
 	REQ_MBRUL(true, true, false, false,"Req.MBRUL"),
 	REQ_QCI(true, true, false, false,"Req.QCI"),
 	REQ_PRIORITY_LEVEL(true, true, false, false,"Req.PriorityLevel"),
 	REQ_PREEMPTION_CAPABILITY(true, true, false, false,"Req.PEC"),
 	REQ_PREEMPTION_VULNERABILITY(true, true, false, false,"Req.PEV"),
 	QOS_UPGRADE(true, true, false, false,"QoSUpgrade"),
 	IMS_EMERGENCY_SESSION(true, false, true, false,"IMSEmergencySession"),
 	EMERGENCY_PACKAGE(true, false, true, false,"EmergencyPackage"),
 	
 	NETWORK_REQUEST_SUPPORT(true, false, true, false,"NetworkRequestSupport"),
 	SERVICE_URN(true,false,true,false,"ServiceURN"),
 	SIP_FORKING_INDICATION(true,false,true,false,"SIPForkingIndication"),
 	SERVICE_INFO_STATUS(true,false,true,false,"ServiceInfoStatus"),
	RE_AUTH_CAUSE(false,false,true,false,"ReAuthCause"),
	EVENT_TIMESTAMP(true, false, true, false, "EventTimestamp"),
	MULTIPLE_SERVICE_INDICATOR(true, false, true, false, "MultipleServiceIndicator"),
	REPORTING_REASON(false, false, false, false, "ReportingReason"),
	LRN(true, false, true, false, "LRN"),

 	/*
	=================KEYS RELATED TO Transaction==================
 	*/
 	
	TRANSACTION_ID(false, false , false, false,"TransactionId"),
	
	/*
	=================KEYS RELATED TO Media Component==================
 	*/
	MEDIA_COMPONENT_QCI(false, false , false, false,"MediaComponent.QCI"),
	MEDIA_COMPONENT_NUMBER(false, false , false, false,"MediaComponent.Number"),
	MEDIA_COMPONENT_AF_APP_IDENTIFIER(false, false , false, false,"MediaComponent.AfAppIdentifier"),
	MEDIA_COMPONENT_UPLINK_CODEC(false, false , false, false,"MediaComponent.UplinkCodec"),
	MEDIA_COMPONENT_DOWNLINK_CODEC(false, false , false, false,"MediaComponent.DownlinkCodec"),
	MEDIA_COMPONENT_FLOW_STATUS(false, false , false, false,"MediaComponent.FlowStatus"),
	MEDIA_COMPONENT_UPLINK_FLOW(false, false , false, false,"MediaComponent.UplinkFlow"),
	MEDIA_COMPONENT_DOWNLINK_FLOW(false, false , false, false,"MediaComponent.DownlinkFlow"),
	MEDIA_COMPONENT_AF_SIGNALLING_PROTOCOL(false, false , false, false,"MediaComponent.AfSingnallingProtocol"),
	MEDIA_COMPONENT_FLOW_NUMBER(false, false , false, false,"MediaComponent.FlowNumber"),
	MEDIA_COMPONENT_FLOW_USAGE(false, false , false, false,"MediaComponent.FlowUsage"),
	MEDIA_COMPONENT_MAX_REQUESTED_BW_DOWNLOAD(false, false , false, false,"MediaComponent.MaxReqBwDownload"),
	MEDIA_COMPONENT_MAX_REQUESTED_BW_UPLOAD(false, false , false, false,"MediaComponent.MaxReqBwUpload"),
	MEDIA_COMPONENT_RESERVATION_PRIORITY(false, false , false, false,"MediaComponent.ReservationPriority"),
	MEDIA_COMPONENT_RR_BW(false, false , false, false,"MediaComponent.RRBw"),
	MEDIA_COMPONENT_RS_BW(false, false , false, false,"MediaComponent.RSBw"),


	
	/*
	=================KEYS RELATED TO SY Interface==================
 	*/

	SY_COMMUNICATION(false, false , true, false,"SyCommunication"),
	SY_COUNTER_PREFIX(false,false,false,false,"PolicyCounter."),
	SY_GATEWAY_REALM(false, false, false, false, "SyGatewayRealm"),
	SY_GATEWAY_HOST_IDENTITY(false, false, false, false, "SyGatewayHostIdentity"),
	
	
	/*
	=================KEYS RELATED TO S9 Interface==================
 	*/
	
	SUBSCRIBER_ROAMING(false, true , true, true,"SubscriberRoaming"),
	LOCAL_SUBSCRIBER(false, true , true, false,"LocalSubscriber"),
 	
	/*
 	=================KEYS RELATED TO RX Session=========================
	*/
 	FLOW_USAGE(false, false, false, false,"FlowUsage"),
 	GX_ADDRESS(false, false, false, false,"Gx_ADDRESS"),
 	GX_REALM(false, false , false, false,"Gx_REALM"),
    GX_SESSION_ID(false, false, false, false, "Gx_Session_ID"), // Used in Gy also
 	AF_ACTIVE_PCC_RULE(false, false, false, false, "AFActivePCCRule"),
	PCC_ADDITIONAL_PARAMETER(false, false , false, false,"MediaComponent.AdditionalParameter"),

    /*
     =================KEYS RELATED TO Gy Session=========================
    */
    RADIUS_SESSION_ID(false, false, false, false, "Radius_Session_ID"),
	RESERVATION_REQUIRED(false, false, false , false, "ReservationRequired"),

 	
 	/*
	===============KEYS RELATED TO EXPRESSION=================
	*/

 	GATEWAY_TYPE(false, false , true, false,"GatewayType"),
 	REQUEST_TYPE(false, false , true, false, "RequestType"),
 	GATEWAY_IP(false, false , false, false, "GatewayIP"),

 /*
 	=================KEYS RELATED TO PCC AUTH RULE=========================
*/

 	QOS_PROFILE_NAME(false, true, false, false,"QoSProfile.Name"),
 	IPCAN_AAMBRDL(true, true, false, false,"IPCAN.AAMBRDL"),
 	IPCAN_AAMBRUL(true, true, false, false,"IPCAN.AAMBRUL"),
 	IPCAN_MBRDL(true, true, false, false,"IPCAN.MBRDL"),
 	IPCAN_MBRUL(true, true, false, false,"IPCAN.MBRUL"),
 	IPCAN_QCI(true, true, false, false,"IPCAN.QCI"),
 	IPCAN_PRIORITY_LEVEL(true, true, false, false,"IPCAN.PriorityLevel"),
 	IPCAN_PREEMPTION_CAPABILITY(true, true, false, false,"IPCAN.PEC"),
 	IPCAN_PREEMPTION_VULNERABILITY(true, true, false, false,"IPCAN.PEV"),

 	
 	DEFAULT_QOS_PROFILE(false, false, true, false,"DefaultQoSProfile"),
 	EXPIRED_QOS_PROFILE(false, false, true, false,"ExpiredQoSProfile"),

	IPCAN_REDIRECT_URL(true, true, false, false,"IPCAN.RedirectURL"),
/*
 	=================KEYS RELATED TO PCC RULE=========================
*/
 	PCC_RULE(false, true, false, false, "PCCRule"),
 	PCC_RULE_NAME(false, false, false, true, "PCCRule.Name"),
	PCC_RULE_PRECEDENCE(false, false, false, true, "PCCRule.Precedence"),
	PCC_RULE_CHARGING_KEY(false, false, false, true, "PCCRule.ChargingKey"),
	PCC_RULE_SPONSOR_ID(false, false, false, true, "PCCRule.SponsorID"),
	PCC_RULE_APP_SERVICE_PROVIDER_ID(false, false, false, true, "PCCRule.AppServiceProviderID"),
	PCC_RULE_SERVICE_IDENTIFIER(false, false, false, true, "PCCRule.ServiceIdentifier"),
	PCC_RULE_USAGE_METERING(false, false, false, false, "PCCRule.UsageMetering"),
	PCC_RULE_MONITORING_KEY(false, false, false, true, "PCCRule.MonitoringKey"),
	PCC_RULE_BEARER_ID(false, false, false, true, "PCCRule.BearerID"),
	PCC_RULE_QCI(false, false, false, true, "PCCRule.QCI"),
	PCC_RULE_GBRDL(false, false, false, true, "PCCRule.GBRDL"),
	PCC_RULE_GBRUL(false, false, false, true, "PCCRule.GBRUL"),
	PCC_RULE_MBRDL(false, false, false, true, "PCCRule.MBRDL"),
	PCC_RULE_MBRUL(false, false, false, true, "PCCRule.MBRUL"),
	PCC_RULE_SERVICE_DATA_FLOW(false, false, false, true,"PCCRule.ServiceDataFlow"),
	PCC_RULE_CHARGING_MODE(false, false, false, false, "PCCRule.ChargingMode"),
	PCC_RULE_ONLINE_CHARGING(false, false, false, true, "PCCRule.OnlineCharging"),
	PCC_RULE_OFFLINE_CHARGING(false, false, false, true, "PCCRule.OfflineCharging"),
	PCC_RULE_ARP(false, false, false, true, "PCCRule.ARP"),
	PCC_RULE_PRIORITY_LEVEL(false, false, false, true, "PCCRule.PriorityLevel"),
	PCC_RULE_PREEMPTION_CAPABILITY(false, false, false, true, "PCCRule.PEC"),
	PCC_RULE_PREEMPTION_VULNERABILITY(false, false, false, true, "PCCRule.PEV"),
	PCC_RULE_STATUS(false, false, false, false, "PCCRule.Status"),
	PCC_RULE_FLOW_STATUS(false, false, false, true, "PCCRule.FlowStatus"),
	PCC_RULE_FLOW_NUMBER(false, false, false, false, "PCCRule.FlowNumber"),
/*
	 ===============KEYS RELATED TO SUBSCRIBER =====================
*/
	SUB_SUBSCRIBER_IDENTITY(false, true, true, false, "Sub.SubscriberIdentity"),
	SUB_ALTERNATE_IDENTITY(false, true, true, false, "Sub.AlternateIdentity"),
 	SUB_USER_NAME(false, true, true, false, "Sub.UserName"),
 	SUB_CUSTOMER_TYPE(false, true, true, false, "Sub.CustomerType"),
 	SUB_SUBSCRIBER_STATUS(false, true, true, false, "Sub.SubscriberStatus"),
 	SUB_EXPIRY_DATE(false, true, true, false, "Sub.ExpiryDate"),
 	SUB_DATA_PACKAGE(false, true, true, false, "Sub.DataPackage"),
	SUB_PRODUCT_OFFER(false, true, true, false, "Sub.ProductOffer"),
	SUB_VOICE_RNC_PACKAGE(false, true, true, false, "Sub.VoiceRnCPackage"),
 	SUB_IMS_PACKAGE(false, true, true, false, "Sub.ImsPackage"),
 	SUB_BILLING_DATE(false, true, true, false, "Sub.BillingDate"),
 	SUB_AREA(false, true, true, false, "Sub.Area"),
 	SUB_CITY(false, true, true, false, "Sub.City"),
 	SUB_ZONE(false, true, true, false, "Sub.Zone"),
 	SUB_COUNTRY(false, true, true, false, "Sub.Country"),
 	SUB_BIRTHDATE(false, true, true, false, "Sub.Birthdate"),
 	SUB_ROLE(false, true, true, false, "Sub.Role"),
 	SUB_COMPANY(false, true, true, false, "Sub.Company"),
 	SUB_DEPARTMENT(false, true, true, false, "Sub.Department"),
 	SUB_DEVICE_TYPE(false, true, true, false, "Sub.DeviceType"),
 	SUB_FAP(false, true, true, false, "Sub.FAP"),
 	SUB_CADRE(false, true, true, false, "Sub.Cadre"),
 	SUB_ARPU(false, true, true, false, "Sub.ARPU"),
 	SUB_CUI(false, true, true, false, "Sub.CUI"),
 	SUB_AGE(false, true, true, false, "Sub.Age"),
 	SUB_IMSI(false, true, true, false, "Sub.IMSI"),
 	SUB_MSISDN(false, true, true, false, "Sub.MSISDN"),
 	SUB_IMEI(false, true, true, false, "Sub.IMEI"),
 	SUB_MAC(false, true, true, false, "Sub.MAC"),
 	SUB_EUI64(false, true, true, false, "Sub.EUI64"),
 	SUB_MODIFIED_EUI64(false, true, true, false, "Sub.MODIFIED_EUI64"),
 	SUB_ENCRYPTION_TYPE(false, true, true, false, "Sub.ENCRYPTION_TYPE"),
	SUB_ESN(false, true, true, false, "Sub.ESN"),
	SUB_MEID(false, true, true, false, "Sub.MEID"),
	SUB_PARENTID(false, true, true, false, "Sub.ParentId"),
	SUB_GROUPNAME(false, true, true, false, "Sub.GroupName"),
	SUB_EMAIL(false, true, true, false, "Sub.Email"),
	SUB_PHONE(false, true, true, false, "Sub.Phone"),
	SUB_BILLING_ACCOUNT_ID(false, true, true, false, "Sub.BillingAccount"),
	SUB_SERVICE_INSTANCE_ID(false, true, true, false, "Sub.ServiceInstance"),
	SUB_SIP_URL(false, true, true, false, "Sub.SIPURL"),
	SUB_PARAM1(false, true, true, false, "Sub.param1"),
	SUB_PARAM2(false, true, true, false, "Sub.param2"),
	SUB_PARAM3(false, true, true, false, "Sub.param3"),
	SUB_PARAM4(false, true, true, false, "Sub.param4"),
	SUB_PARAM5(false, true, true, false, "Sub.param5"),
	SUB_IS_UM_ENABLED(false, true, true, false, "Sub.IsUMEnabled"),
	SUB_PASSWORD_CHECK(false, true, true, false, "Sub.PasswordCheck"),
	SUB_SY_INTERFACE(false, true, true, false, "Sub.SyInterface"),
	SUB_PAYG_INTL_DATA_ROAMING(false, true, true, false, "Sub.PaygIntlDataRoaming"),
	SUB_UNKNOWN_USER(false, true, true, false, "Sub.UnknownUser"),
	SUB_PROFILE_EXPIRED_HOURS(false, true, true, false, "Sub.ProfileExpiredHours"),
	SUB_CALLING_STATION_ID(false, true, true, false, "Sub.CallingStationID"),
	SUB_NAS_PORT_ID(false, true, true, false, "Sub.NasPortID"),
	SUB_FRAMED_IP(false, true, true, false, "Sub.FramedIP"),
	SUB_CREATEDDATE(false, true, true, false, "Sub.CreatedDate"),
	SUB_MODIFIEDDATE(false, true, true, false, "Sub.ModifiedDate"),
	SUB_MCC_MNC(false, true, true, false, "SUB.MccMnc"),
	SUB_INTERNATIONAL_ROAMING(false, true, true, false, "Sub.InternationalRoaming"),
	SUB_NEXT_BILL_DATE(false, true, true, false, "Sub.NextBillDate"),
	SUB_BILL_CHANGE_DATE(false, true, true, false, "Sub.BillChangeDate"),

   /*
	 =================KEYS RELATED TO Subscriber Network Information=========================
	 */
    SUB_NETWORK_OPERATOR(true,true,true,false,"Sub.NetworkOperator"),
    SUB_NETWORK_BRAND(true,true,true,false,"Sub.NetworkBrand"),
	SUB_NETWORK_NAME(true,true,true,false,"Sub.NetworkName"),
	SUB_NETWORK_TECHNOLOGY(true,true,true,false,"Sub.NetworkTechnology"),
	SUB_NETWORK_COUNTRY(true,true,true,false,"Sub.NetworkCountry"),
	SUB_NETWORK_GEOGRAPHY(true,true,true,false,"Sub.NetworkGeography"),

	/*
      =================KEYS RELATED TO OPERATOR NETWORK-INFO=========================
    */
    OPERATOR_NAME(true,true,true,false,"Opr.Name"),
    OPERATOR_NETWORK_BRAND(true,true,true,false,"Opr.NetworkBrand"),
    OPERATOR_NETWORK_NAME(true,true,true,false,"Opr.NetworkName"),
	OPERATOR_NETWORK_TECHNOLOGY(true,true,true,false,"Opr.NetworkTechnology"),
	OPERATOR_NETWORK_COUNTRY(true,true,true,false,"Opr.NetworkCountry"),
	OPERATOR_NETWORK_GEOGRAPHY(true,true,true,false,"Opr.NetworkGeography"),


	/*
	 =================KEYS RELATED TO Dyna SPR Driver=========================

	DYNA_SUB_APPLIEDAUTHRULE(false, true, true, true, "DySub.APPLIEDAUTHRULE"),
	DYNA_SUB_CPEID(false, true, true, false, "DySub.CPEID"),
	DYNA_SUB_GATEWAYADDRESS(false, true, true, false, "DySub.GATEWAYADDRESS"),
	DYNA_SUB_LASTLOGOUTTIME(false, true, true, true, "DySub.LASTLOGOUTTIME"),
	DYNA_SUB_LASTLOGINTIME(false, true, true, true, "DySub.LASTLOGINTIME"),

	
	
	/*
	 =================KEYS RELATED TO Session Manager=========================
	 */
 	
 	CS_SUBSCRIPTION_ID_TYPE_MSISDN(true, true, true, false, "CS.MSISDN"),
	CS_SUBSCRIPTION_ID_TYPE_IMSI(true, true, true, false, "CS.IMSI"),
	CS_SUBSCRIPTION_ID_TYPE_SIPURI(true, true, true, false, "CS.SIPURI"),
	CS_SUBSCRIPTION_ID_TYPE_NAI(true, true, true, false, "CS.NAI"),
	CS_NAI_RELATED_USERNAME(false, true, true, false, "CS.NaiUserName"),
	CS_NAI_REALM(false, true, true, false, "CS.NaiRealm"),
	CS_NAI_WIMAX_DECORATION(false, true, true, false, "CS.WimaxDecoration"),
	CS_CORESESSION_ID(false, false, false, false, "CS.CoreSessionID"),
	CS_SESSION_ID(true, true, false, false, "CS.SessionID"),
	CS_SUBSESSION_ID(true, true, false, false, "CS.SubSessionID"),
	CS_USER_IDENTITY(false, true, false, false, "CS.UserIdentity"),
	CS_GATEWAY_ADDRESS(false, false, true, false, "CS.GatewayAddress"),
	CS_SOURCE_GATEWAY(false, false, true, false, "CS.SourceGateway"),
	CS_SESSION_MANAGER_ID(false, false, false, false, "CS.SessionManagerID"),
	CS_SESSION_IPV4(true, true, true, false, "CS.SessionIPv4"),
	CS_SESSION_IPV6(true, true, true, false, "CS.SessionIPv6"),
	CS_ACCESS_NETWORK(true, false, true, false, "CS.AccessNetwork"),
	CS_SESSION_STATE(false, false, false, false, "CS.SessionState"),
	CS_DEVICE_TYPE(true, false , true, false, "CS.DeviceType"),
	CS_DEVICE_ID(true, false, true, false, "CS.DeviceID"),
	CS_GATEWAY_REALM(true, false, true, false, "CS.GatewayRealm"),	
	CS_SESSION_TYPE(false, false, false, false, "CS.SessionType"),
	CS_MULTI_SESSION_ID(true, false, false, false, "CS.MultiSessionID"),
	CS_AF_APP_ID(true, true, true, false, "CS.AFApplicationID"),
	CS_MEDIA_TYPE(true, false, true, false, "CS.MediaType"),
	CS_FLOW_STATUS(true, false, true, false, "CS.FlowStatus"),
	CS_MRBU(true, false, false, false, "CS.MaxRequestedBWU"),
	CS_MRBD(true, false, false, false, "CS.MaxRequestedBWD"),
	CS_PDN_CONN_ID(true, false, true, false, "CS.PDNConnectionID"),
	CS_LOCATION(true, false, true, false, "CS.Location"),
	CS_CSG_INFO_REPORTING(true, false, true, false, "CS.CSGInformationReporting"),
	CS_CSG_ID(true, false, true, false, "CS.CSGID"),
	CS_CSG_ACCESS_MODE(true, false, true, false, "CS.CSGAccessMode"),
	CS_CSG_MEMBERSHIP_INDICATION(true, false, true, false, "CS.CSGMembershipIndication"),
	CS_PACKET_FILTER_INFORMATION(true, false, true, false, "CS.PacketFilterInformation"),
	CS_PACKET_FILTER_CONTENT(true, false, true, false, "CS.PacketFilterContent"),
	CS_AF_SESSION_ID(false, false, false, false, "CS.AFSessionId"),
	CS_ADD_ONS(false, false, false, false, "CS.AddOns"),
	CS_USERNAME(true, true , true, false, "CS.UserName"),
	CS_CREDIT_LIMIT(true, true , true, false, "CS.CreditLimit"),
	CS_CPEID(true, true, true, false, "CS.CPEID"),
	CS_SY_SESSION_ID(false, false, false,false, "CS.SySessionId"),
	CS_SY_GATEWAY_NAME(false, false, false,false, "CS.SyGatewayName"),
	CS_GATEWAY_NAME(false, false, false, false, "CS.GatewayName"),
	CS_ACTIVE_PCC_RULES(false, false, false, false, "CS.ActivePCCRules"),
	CS_REQ_IP_CAN_QOS(false,false,false,false,"CS.ReqIpCanQoS"),
	CS_SESSION_USAGE(false,false,false,false,"CS.Usage"),
	CS_USAGE_RESERVATION(false,false,false,false,"CS.UsageReservation"),
	CS_FLOW_DESCRIPTION(false,false,false,false,"CS.FlowDescription"),
	CS_FLOW_NUMBSER(false,false,false,false,"CS.FlowNumber"),
	CS_MEDIA_COMPONENT_NUMBER(true, false, true, false, "CS.MediaComponentNumber"),
	CS_UPLINK_FLOW(true, false, true, false, "CS.UpLink"),
	CS_DOWNLINK_FLOW(true, false, true, false, "CS.DownLink"),
	CS_CALL_DIRECTION(true, false, false, false, "CS.CallDirection"),
	CS_PACKAGE_USAGE(true, false, true, false, "CS.PackageUsage"),
	CS_CALLING_STATION_ID(true, false, true, true, "CS.CallingStationId"),
	CS_CALLED_STATION_ID(true, false, true, true, "CS.CalledStationId"),
	CS_CALLING_PREFIX(true, true , true, false, "CS.CallingPrefix"),
	SYSTEM_COUNTRY(true, true , true, false, "System.Country"),
	SYSTEM_OPERATOR(true, true , true, false, "System.Operator"),
	CS_CALLING_NETWORK_NAME(true, true , true, false, "CS.CallingNetworkName"),
	CS_CALLED_PREFIX(true, true , true, false, "CS.CalledPrefix"),
	CS_CALLED_COUNTRY(true, true , true, false, "CS.CalledCountry"),
	CS_CALLED_OPERATOR(true, true , true, false, "CS.CalledOperator"),
	CS_CALLED_NETWORK_NAME(true, true , true, false, "CS.CalledNetworkName"),
	CS_NAS_PORT_ID(true, false, true, true, "CS.NASPortId"),
	CS_ACTIVE_CHARGING_RULE_BASE_NAMES(false, false, false, false, "CS.ActiveChargingRuleBaseNames"),
	CS_QUOTA_RESERVATION(true, false, true, false, "CS.QuotaReservation"),
	CS_PCC_PROFILE_SELECTION_STATE(true, false, true, false, "CS.PCCProfilSelectionState"),
	CS_UNACCOUNTED_QUOTA(true, false, true, false, "CS.UnaccountedQuota"),
	CS_SGSN_MCC_MNC(false, false, false, false, "CS.SGSNMCCMNC"),
	CS_SERVICE(true, false, true, false, "CS.Service"),
	CS_CALLTYPE(true, false, true, false, "CS.CallType"),

	/*
	 =================KEYS RELATED TO USER-EQUIPMENT-INFO=========================
	 */
	USER_EQUIPMENT_SVN(true, false , true, false, "UserEquipment.SVN"),
	USER_EQUIPMENT_SNR(true, false , true, false, "UserEquipment.SNR"),
	USER_EQUIPMENT_TAC(true, false , true, false, "UserEquipment.TAC"),
	USER_EQUIPMENT_MAC(true, false , true, false, "UserEquipment.MAC"),
	USER_EQUIPMENT_EUI64(true, false , true, false, "UserEquipment.EUI64"),
	USER_EQUIPMENT_MODIFIED_EUI64(true, false , true, false, "UserEquipment.MODIFIED-EUI64"),

	USER_EQUIPMENT_BRAND(true, false, true, false, "UserEquipment.Brand"),
	USER_EQUIPMENT_MODEL(true, false, true, false, "UserEquipment.Model"),
	USER_EQUIPMENT_HWTYPE(true, false, true, false, "UserEquipment.HwType"),
	USER_EQUIPMENT_OS(true, false, true, false, "UserEquipment.OS"),
	USER_EQUIPMENT_ADDITIONALINFO(true, false, true, false, "UserEquipment.AdditionalInfo"),
	/*
	 =================KEYS RELATED TO TGPP-USER-LOCATION-INFO=========================
	 */
	LOCATION_TYPE(false, true, true, false, "Location.Type"),
	
	LOCATION_ID(false, true, true, false, "Location.ID"),
	LOCATION_OLD_CONGESTION_STATUS(false, true, true, false, "Location.OldCongestionStatus"),
	LOCATION_NEW_CONGESTION_STATUS(false, true, true, false, "Location.NewCongestionStatus"),
	
	LOCATION_MCC(false, true, true, false, "Location.MCC"),
	LOCATION_MNC(false, true, true, false, "Location.MNC"),
	LOCATION_LAC(false, true, true, false, "Location.LAC"),
	
	LOCATION_CI(false, true, true, false, "Location.CI"),
	
	LOCATION_SAC(false, true, true, false, "Location.SAC"),
	
	LOCATION_RAC(false, true, true, false, "Location.RAC"),

	LOCATION_TAC(false, true, true, false, "Location.TAC"), 

	LOCATION_ECGI_SPARE(false, true, true, false, "Location.ECGISPARE"), 
	LOCATION_ECGI_ECI(false, true, true, false, "Location.ECGIECI"),

	LOCATION_AREA(false, true, true, false,"Location.Area"),
	LOCATION_CITY(false, true, true, false,"Location.City"),
	LOCATION_REGION(false, true, true, false,"Location.Region"),
	LOCATION_COUNTRY(false, true, true, false,"Location.Country"),
	LOCATION_GEOGRAPHY(false, true, true, false,"Location.Geography"),
	LOCATION_PARAM1(false, true, true, false,"Location.Param1"),
	LOCATION_PARAM2(false, true, true, false,"Location.Param2"),
	LOCATION_PARAM3(false, true, true, false,"Location.Param3"),
	WIFI_SSID(false, true, true, false,"WiFi.SSID"),

     /*
	  =================KEYS RELATED TO Response=========================
	 */
	RND_MCN(false, true , false, false, "RnDMCN"),
	SESSION_RELEASE_CAUSE(true, true , true, false, "SessionReleaseCause"),
	ABORT_CAUSE(false, true, true, false, "AbortCause"),
	CISCO_PACKAGE_ID(false, true , false, false, "CiscoPackageID"),
	SBC_PORT(false, true , false, false, "SBCPort"),
	SBC_PORTI(false, true , false, false, "SBCPortI"),
	USER_PORT(false, true , false, false, "UserPort"),
	USER_PORTI(false, true , false, false, "UserPortI"),
	FLOW_NUMBER(false, true , false, false, "flowNumber"),
	FLOW_NUMBERI(false, true , false, false, "flowNumberI"),
	FLOW_STATUS(false, true , false, false, "FlowStatus"),
	PACKAGE_NAME(false, true , false, false, "PackageName"),
	
	BAL_OUTPUT_OCTETS(false, true, false, false, "BalOutputOctets"),
	BAL_INPUT_OCTETS(false, true, false, false, "BalInputOctets"),
	BAL_TOTAL_OCTETS(false, true, false, false, "BalTotalOctets"),
	BAL_TIME(false, true, false, false, "BalTime"),
	SLICE_OUTPUT_OCTETS(false, true, false, false, "SliceOutputOctets"),
	SLICE_INPUT_OCTETS(false, true, false, false, "SliceInputOctets"),
	SLICE_TOTAL_OCTETS(false, true, false, false, "SliceTotalOctets"),
	SLICE_TIME(false, true, false, false, "SliceTime"),
	SESS_GSU_TOTAL(false, true, false, false,"SessionGsuTotal"),
	SESS_GSU_DOWNLOAD(false, true, false, false,"SessionGsuDownalod"),
	SESS_GSU_UPLOAD(false, true, false, false,"SessionGsuUpload"),
	SESS_GSU_TIME(false, true, false, false,"SessionGsuTime"),
	
 	/*
	 =================KEYS RELATED TO Response=========================
	 */
 	IN_ACTIVE_PCC_RULE_NAME(true, false , false, false, "InactivePCCRuleName"),
 	
 	EVENT_TRIGGER(true, true, true, false, "EventTrigger"),
 	SESSION_TIMEOUT(false, true, false, false, "SessionTimeout"),
 	REVALIDATION_MODE(false, false, false, false, "RevalidationMode"),
 	
 	/*
	 =================KEYS RELATED TO Gateway Configuration=========================
	 */
	USAGE_REPORTING_TYPE(false, false, false, false, "UsageReportingType"),
	PCC_LEVEL_MONITORING(false, false, false, false, "ServiceLevelMonitoringSupported"),

	/*
	 =================KEYS RELATED TO CDR=========================
	 */
	CDR_USAGEKEY(false, false, false, false, "CDR.UsageKey"),
	CDR_INPUTOCTETS(false, false, false, false, "CDR.InputOctets"),
	CDR_OUTPUTOCTETS(false, false, false, false, "CDR.OutputOctets"),
	CDR_TOTALOCTETS(false, false, false, false, "CDR.TotalOctets"),
	CDR_USAGETIME(false, false, false, false, "CDR.UsageTime"),
	CDR_TIMESTAMP(false, false, false, false, "CDR.TimeStamp"),
	
	BOD_SUBSCRIBER_PACKAGE(false, false, false, false, "BOD.SubscriberPackage"),
	BOD_USERIDENTITY(false, false, false, false, "BOD.UserIdentity"),
	BOD_STARTTIME(false, false, false, false, "BOD.StartTime"),
	BOD_ENDTIME(false, false, false, false, "BOD.EndTime"),
	BOD_STATUS(false, false, false, false, "BOD.Status"),

	TARIFF_TYPE(false, false, true, false, "TariffType"),

	/**
	 =================KEYS RELATED TO PACKAGE=========================
	 */

	SUB_DATA_PACKAGE_PARAM1(true, true, false, true,"Sub.DataPackage.Param1"),
	SUB_DATA_PACKAGE_PARAM2(true, true, false, true,"Sub.DataPackage.Param2"),

	/**
	 =================KEYS RELATED TO SUBSCRIPTION NOTIFICATION=========================
	 */
	PKG_NAME(false,false,false,false,"Pkg.Name"),
	PKG_PRICE(false,false,false,false,"Pkg.Price"),

	/**
	 =================KEYS RELATED TO COST INFORMATION AVP=========================
	 */
	CI_VALUE_DIGIT(false,false,false,false,"CI.ValueDigit"),
	CI_COST_UNIT(false,false,false,false,"CI.CostUnit")
	;


	public final String val;
	public final boolean request;
	public final boolean response;
	public final boolean rule;
	public final boolean pccRule;

	private static Map<PCRFKeyType, List<PCRFKeyConstants>> valueMap;
	private static Map<String, PCRFKeyConstants> pcrfKeys;

	private PCRFKeyConstants(boolean request,boolean response, boolean rule, boolean pccRule, String val) {
		this.val = val;
		this.request = request;
		this.response = response;
		this.rule = rule;
		this.pccRule = pccRule;
	}

	public String getVal() {
		return val;
	}
	
	
	private boolean isRequestKey(){
		return request;
	}
	
	private boolean isResponseKey(){
		return response;
	}
	
	private boolean isRuleKey(){
		return rule;
	}
	
	private boolean isPCCRuleKey(){
		return pccRule;
	}

	static{
		valueMap = new EnumMap<>(PCRFKeyType.class);
		pcrfKeys = new HashMap<>();
		for(PCRFKeyType key : PCRFKeyType.values()){
			List<PCRFKeyConstants> pcrfKeys = new ArrayList<>();
			valueMap.put(key, pcrfKeys);
		}
		
		for(PCRFKeyConstants pcrfKey : PCRFKeyConstants.values()){
			if(pcrfKey.isRequestKey() && valueMap.get(PCRFKeyType.REQUEST) != null)
				valueMap.get(PCRFKeyType.REQUEST).add(pcrfKey);
			if(pcrfKey.isResponseKey() && valueMap.get(PCRFKeyType.RESPONSE) != null)
				valueMap.get(PCRFKeyType.RESPONSE).add(pcrfKey);
			if(pcrfKey.isRuleKey() && valueMap.get(PCRFKeyType.RULE) != null)
				valueMap.get(PCRFKeyType.RULE).add(pcrfKey);
			if(pcrfKey.isPCCRuleKey() && valueMap.get(PCRFKeyType.PCC_RULE) != null)
				valueMap.get(PCRFKeyType.PCC_RULE).add(pcrfKey);
			
			if(pcrfKey.val.startsWith("Sub.")){
				valueMap.get(PCRFKeyType.SUBSCRIBER_PROFILE).add(pcrfKey);
			}
			
			if(pcrfKey.val.startsWith("MediaComponent.") || pcrfKey.isRuleKey()){
				valueMap.get(PCRFKeyType.IMS_RULE).add(pcrfKey);
			}
			pcrfKeys.put(pcrfKey.getVal(), pcrfKey);
			
		}
	}
	
	public static List<PCRFKeyConstants> values(PCRFKeyType type){
		return valueMap.get(type);
	}
	
	public static PCRFKeyConstants fromKeyConstants(String keyName){
		PCRFKeyConstants pcrfKey= pcrfKeys.get(keyName);
		return pcrfKey == null ? PCRFKeyConstants.UNKNOWN_KEY : pcrfKey; 
	}
}
