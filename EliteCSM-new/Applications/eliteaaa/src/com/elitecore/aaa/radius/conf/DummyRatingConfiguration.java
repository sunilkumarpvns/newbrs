package com.elitecore.aaa.radius.conf;

import java.util.HashMap;

public interface DummyRatingConfiguration {
	public static final String MODULE = "DUMMY RATING";
	public static final String SESSION_TIMEOUT = "session-timeout";
	public static final String CHARGING_TYPE = "charging-type";
	public static final String QUOTA_ID = "quota-id";
	public static final String QUOTA = "quota";
	public static final String THRESHOLD = "threshold";
	public static final String ITERATIONS = "iterations";
	public static final String TERMINATION_ACTION = "termination-action";
	public static final String ACTION_ON_TERMINATE_REQUEST = "action-on-terminate-request";
	public static final String QOS = "qos";
	public static final String QOSID = "qosid";
	public static final String GLOBAL_SERVICE_CLASS_NAME = "global-service-class-name";
	public static final String SERVICE_CLASS_NAME = "service-class-name";
	public static final String SCHEDULE_TYPE = "schedule-type";
	public static final String TRAFFIC_PRIORITY = "traffic-priority";
	public static final String MAX_TRAFFIC_RATE = "max-traffic-rate";
	public static final String MIN_TRAFFIC_RATE = "min-traffic-rate";
	public static final String MAX_TRAFFIC_BURST = "max-traffic-burst";
	public static final String TOLERATED_JITTER = "tolerated-jitter";
	public static final String MAX_LATENCY = "max-latency";
	public static final String REDUCED_RESOURCE_CODE = "reduced-resource-code";
	public static final String MEDIA_FLOW_TYPE = "media-flow-type";
	public static final String UNSOLICITED_GRANT_INTERVAL = "unsolicited-grant-interval";
	public static final String SDU_SIZE = "sdu-size";
	public static final String UNSOLICITED_POLLING_INTERVAL = "unsolicited-polling-interval";
	public static final String MEDIA_FLOW_DESCRIPTION = "media-flow-description";
	public static final String PACKET_FLOW_DESCRIPTOR = "packet-flow-descriptor";
	public static final String PDFID = "pdfid";
	public static final String SDFID = "sdfid";
	public static final String SERVICE_PROFILE_ID = "service-profile-id";
	public static final String DIRECTION = "direction";
	public static final String ACTIVATION_TRIGGER = "activation-trigger";
	public static final String TRANSPORT_TYPE = "transport-type";
	public static final String UPLINK_QOS_ID = "uplink-qos-id";
	public static final String DOWNLINK_QOS_ID = "downlink-qos-id";
	public static final String UPLINK_CLASSIFIER = "uplink-classifier";
	public static final String DOWNLINK_CLASSIFIER = "downlink-classifier";
	public static final int SEND_NO_QUOTA 		= 0;
	public static final int SEND_PREVIOUS_QUOTA = 1;
	
	public HashMap<Object, Object> getDummyRatingConfiguration();
}
