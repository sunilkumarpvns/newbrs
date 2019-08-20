package com.elitecore.core.commons.utilx.mbean;

public interface MBeanConstants {
	
	public final static String ELITEAAA_MBEAN = "Elitecore:type=EliteAdmin,data=";
	
	public final static String CONFIGURATION = ELITEAAA_MBEAN + "Configuration";
	public final static String GATEWAYINFORMATION = ELITEAAA_MBEAN + "GatewayInformation";
	public final static String SOFTLOGOUT = ELITEAAA_MBEAN + "SoftLogout";
	public final static String CLI = ELITEAAA_MBEAN + "AAA Cli";
	public final static String LICENSE = ELITEAAA_MBEAN + "License";

	public final static String REPORTS = "Elitecore:type=Reports,data=";
	public final static String ONLINE_REPORT = REPORTS + "Online";
	public final static String OFFLINE_REPORT = REPORTS + "Offline";
	
	public final static String DETAILS = "Elitecore:type=Details,data=";
	public final static String CLIENT = DETAILS + "Client";
	public final static String SUPPORTED_RFC = DETAILS + "Supported RFC";
	public final static String RADIUS_USERFILE = DETAILS + "Radius Userfile";
	public final static String CONFIGURATION_DETAIL = DETAILS + "Configuration";
	public final static String SYSTEM = DETAILS + "System";
	public final static String SESSION_DISCONNECT = ELITEAAA_MBEAN + "Disconnect";
	public final static String SERVER_INFO =ELITEAAA_MBEAN + "ServerInfo";
	public final static String SUBSCRIBERPROFILE_DATA = ELITEAAA_MBEAN + "SubscriberProfileInfo";
	public final static String SESSION_MANAGER_INFO = ELITEAAA_MBEAN + "SessionManagerInfo";
	public final static String SESSION_INFO = ELITEAAA_MBEAN + "SessionInfo";
	public final static String LOG_MONITOR_INFO = ELITEAAA_MBEAN + "LogMonitorInfo";
	
	public final static String PROFILE = "Elitecore:type=Details,name1=profile,";
	
	public final static String DRIVER_PROFILE = PROFILE+"name2=driver,data=";
	
	public final static String POLICY_PROFILE = PROFILE+"name2=policy,data=";
	
	public final static String HTML_PROTOCOL = "Elitecore:type=Adaptor,protocol=html";
	public final static String STATISTICS_PROTOCOL = "Elitecore:type=Statistics,protocol=";
	public final static String STATISTICS_PROTOCOL_RADIUS = STATISTICS_PROTOCOL +  "radius,application=";
	public final static String STATISTICS_PROTOCOL_DIAMETER = STATISTICS_PROTOCOL +  "diameter,application=";
	public final static String STATISTICS_PROTOCOL_PCRF = STATISTICS_PROTOCOL +  "pcrf,application=";
	
	public final static String POLICYCONFIGURATION = ELITEAAA_MBEAN + "PolicyConfiguration";
	public final static String VALIDATECONDITION = ELITEAAA_MBEAN + "ValidateCondition";
	
	public final static String MISCELLANEOUS = DETAILS + "Miscellaneous";
	public final static String SEND_DIAMETER_PACKET = ELITEAAA_MBEAN + "DiameterPacketSender";
	public final static String SEND_RADIUS_PACKET = ELITEAAA_MBEAN +  "RadiusPacketSender";
	
	public final static String PACKET_UTILITY = ELITEAAA_MBEAN + "SendPacket";
	public final static String REAUTHORIZE = ELITEAAA_MBEAN + "reAuthorization";
}
