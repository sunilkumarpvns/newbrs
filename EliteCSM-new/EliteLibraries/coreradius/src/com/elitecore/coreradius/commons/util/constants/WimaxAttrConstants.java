package com.elitecore.coreradius.commons.util.constants;


public enum WimaxAttrConstants implements WiMAXConstant{
	WIMAX_CAPABILITY(1),
	DEVICE_AUTHENTICATION_INDICATOR(2),
	GMT_TIMEZONE_OFFSET(3),
	AAA_SESSION_ID (4),
	MSK (5),
	HA_IP_MIP4 (6),
	HA_IP_MIP6 (7),
	DHCPV4_SERVER (8),
	DHCPV6_SERVER (9),
	MN_HA_MIP4_KEY (10),
	MN_HA_MIP4_SPI (11),
	MN_HA_MIP6_KEY (12),
	MN_HA_MIP6_SPI (13),
	FA_RK_KEY(14),
	HA_RK_KEY (15),
	HA_RK_SPI (16),
	HA_RK_LIFETIME (17),
	RRQ_HA_IP (18),
	RRQ_MN_HA_KEY (19),
	TIME_OF_DAY_TIME (20),
	SESSION_CONTINUE (21),
	BEGINNING_OF_SESSION (22),
	IP_TECHNOLOGY (23),
	HOTLINE_INDICATOR (24),
	PREPAID_INDICATOR (25),
	PDFID (26),
	SDFID (27),
	PACKET_FLOW_DESCRIPTOR (28),
	QOS_DESCRIPTOR (29),
	UPLINK_GRANTED_QOS (30),
	CONTROL_PACKETS_IN (31),
	CONTROL_OCTETS_IN (32),
	CONTROL_PACKETS_OUT (33),
	CONTROL_OCTETS_OUT (34),
	PPAC (35),
	SESSION_TERMINATION_CAPABILITY(36),
	PPAQ (37),
	PTS (38),
	ACTIVE_TIME (39),
	DHCP_RK (40),
	DHCP_RK_KEY_ID (41),
	DHCP_RK_LIFETIME (42),
	DHCPMSG_SERVER_IP (43),
	IDLE_MODE_TRANSITION (44),
	NAP_ID (45),
	BS_ID (46),
	LOCATION (47),
	ACCT_INPUT_PACKETS_GIGAWORD (48),
	ACCT_OUTPUT_PACKETS_GIGAWORD (49),
	UPLINK_FLOW_DESCRIPTION (50),
	BU_COA_IPV6 (51),
	DNS (52),
	HOTLINE_PROFILE_ID (53),
	HTTP_REDIRECTION_RULE (54),
	IP_REDIRECTION_RULE (55),
	HOTLINE_SESSION_TIMER (56),
	NSP_ID (57),
	HA_RK_KEY_REQUESTED (58),
	COUNT_TYPE (59),
	WIMAX_DM_ACTION_CODE (60),
	FA_RK_SPI (61),
	DOWNLINK_FLOW_DESCRIPTION (62),
	DOWNLINK_GRANTED_QOS (63),
	VHA_IP_MIP4 (64),
	VHA_IP_MIP6 (65),
	MN_VHA_MIP4_KEY (66),
	VHA_RK_KEY (67),
	VHA_RK_SPI (68),
	VHA_RK_LIFETIME (69);
	
	private final int value;
	WimaxAttrConstants(int value){
		this.value = value;
	}
	
	public int getIntValue() {
		return this.value;
	}
	// wimax capability
	public enum WimaxCapability implements WiMAXConstant{
		WIMAX_RELEASE (1),
		ACCOUNTING_CAPABILITIES (2),
		HOTLINING_CAPABILITIES (3),
		IDLE_MODE_NOTIFICATION_CAPABILITIES (4);
		
		private final int value;
		WimaxCapability(int value) {
			this.value = value;
		}
		
		public int getIntValue(){
			return this.value;
		}
	}
	//Time of the day
	public enum TimeOfTheDay implements WiMAXConstant{
		HOUR (1),
		MINUTE (2),
		UTC_OFFSET (3);
		
		private final int value;
		TimeOfTheDay(int value) {
			this.value = value;
		}
		
		public int getIntValue(){
			return this.value;
		}
	}

	//Packet Flow Descriptor
	
	public enum PacketFlowDescriptor implements WiMAXConstant{
		PACKET_DATA_FLOW_ID (1),
		SERVICE_DATA_FLOW_ID (2),
		SERVICE_PROFILE_ID (3),
		DIRECTION (4),
		ACTIVATION_TRIGGER (5),
		TRANSPORT_TYPE (6),
		UPLINK_QOS_ID (7),
		DOWNLINK_QOS_ID (8),
		UPLINK_CLASSIFIER (9),
		DOWNLINK_CLASSIFIER (10);
		
		private final int value;
		PacketFlowDescriptor(int value) {
			this.value = value;
		}
		
		public int getIntValue(){
			return this.value;
		}
	}
	
	//Qos descriptor
	public enum QoSDescriptor implements WiMAXConstant{
		QOS_ID (1),
		GLOBAL_SERIVCE_CLASS_NAME (2),
		SERVICE_CLASS_NAME (3),
		SCHEDULE_TYPE (4),
		TRAFFIC_PRIORITY (5),
		MAXIMUM_SUSTAINED_TRAFFIC_RATE (6),
		MINIMUM_RESERVED_TRAFFIC_RATE (7),
		MAXIMUM_TRAFFIC_BURST (8),
		TOLERATED_JITTER (9),
		MAXIMUM_LATENCY (10),
		REDUCED_RESOURCE_CODE (11),
		MEDIA_FLOW_TYPE (12),
		UNSOLICITED_GRANT_INTERVAL (13),
		SDU_SIZE (14),
		UNSOLICITED_POLLING_INTERVAL (15),
		MEDIA_FLOW_DESCRIPTION_IN_SDP_FORMAT (16);
		
		private final int value;
		QoSDescriptor(int value) {
			this.value = value;
		}
		
		public int getIntValue(){
			return this.value;
		}
	}
	//PPAC
	public enum PPAC implements WiMAXConstant{
		AVAILABLE_IN_CLIENT (1);
		
		private final int value;
		PPAC(int value) {
			this.value = value;
		}
		
		public int getIntValue(){
			return this.value;
		}
	}
	
	public enum PPAQ implements WiMAXConstant{
		QUOTA_IDENTIFIER (1),
		VOLUME_QUOTA (2),
		VOLUME_THRESHOLD (3),
		DURATION_QUOTA (4),
		DURATION_THRESHOLD (5),
		RESOURCE_QUOTA (6),
		RESOURCE_THRESHOLD (7),
		UPDATE_REASON (8),
		PREPAID_SERVER (9),
		SERVICE_ID (10),
		RATING_GROUP_ID (11),
		TERMINATION_ACTION (12),
		POOL_ID (13),
		POOL_MULTIPLIER (14),
		REQUESTED_ACTION (15),
		CHECK_BALANCE_RESULT (16),
		COST_INFORMATION_AVP (17);
		
		private final int value;
		PPAQ(int value) {
			this.value = value;
		}
		
		public int getIntValue(){
			return this.value;
		}
	}
	
	//PTS
	public enum PrepaidTariffSwitch implements WiMAXConstant{
		VOLUME_USED_AFTER_TARIFF_SWITCH (2),
		TARIFF_SWITCH_INTERVAL (3),
		TIME_INTERVAL_AFTER_TARIFF_SWITCH_INTERVAL (4);
		
		private final int value;
		PrepaidTariffSwitch(int value) {
			this.value = value;
		}
		
		public int getIntValue(){
			return this.value;
		}
	}
	
	public enum SerciceType implements WiMAXConstant{
		AUTHORIZE_ONLY(17),
		AUTHENTICATE_ONLY(8);
		
		public final int value;
		SerciceType(int value){
			this.value = value;
		}
		
		public static boolean isAuthenticateOnly(int serviceType){
			return (serviceType == AUTHENTICATE_ONLY.value);
		}
		
		public static boolean isAuthorizeOnly(int serviceType){
			return (serviceType == AUTHORIZE_ONLY.value);
		}
	}
	
	public enum ResultCode implements WiMAXConstant{
		AUTH_SUCCESS,
		AUTH_REJECT,
		AUTH_DROP,
		AUTH_CHALLENGE,
		ACCT_RESPONSE,
		ACCT_DROP
	}
	
}
