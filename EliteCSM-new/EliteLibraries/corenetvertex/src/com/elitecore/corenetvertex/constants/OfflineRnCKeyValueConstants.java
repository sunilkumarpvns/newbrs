package com.elitecore.corenetvertex.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OfflineRnCKeyValueConstants {

	TRAFFIC_TYPE_DIRECT_INCOMING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Direct Incoming"),
	TRAFFIC_TYPE_DIRECT_OUTGOING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Direct Outgoing"),
	TRAFFIC_TYPE_TRANSIT_INCOMING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Transit Incoming"),
	TRAFFIC_TYPE_TRANSIT_OUTGOING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Transit Outgoing"),
	TRAFFIC_TYPE_INCOMING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Incoming"),
	TRAFFIC_TYPE_OUTGOING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Outgoing"),
	TRAFFIC_TYPE_TRANSIT(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Transit"),
	TRAFFIC_TYPE_MVNO_IDD(OfflineRnCKeyConstants.TRAFFIC_TYPE, "MVNO_IDD"),
	TRAFFIC_TYPE_MVNO_DOMESTIC(OfflineRnCKeyConstants.TRAFFIC_TYPE, "MVNO_Domestic"),
	TRAFFIC_TYPE_MVNO_ROAMING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "MVNO_Roaming"),
	TRAFFIC_TYPE_CONTENT(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Content"),
	TRAFFIC_TYPE_SUBSCRIPTION(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Subscription"),
	TRAFFIC_TYPE_AUDIO(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Audio"),
	TRAFFIC_TYPE_ANSI(OfflineRnCKeyConstants.TRAFFIC_TYPE, "ANSI"),
	TRAFFIC_TYPE_NON_ANSI(OfflineRnCKeyConstants.TRAFFIC_TYPE, "NON-ANSI"),
	TRAFFIC_TYPE_INBOUND_ROAMING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Inbound Roaming"),
	TRAFFIC_TYPE_OUTBOUND_ROAMING(OfflineRnCKeyConstants.TRAFFIC_TYPE, "Outbound Roaming"),
	
	SERVICE_TYPE_FIXED(OfflineRnCKeyConstants.SERVICE_NAME, "Fixed"),
	SERVICE_TYPE_MOBILE(OfflineRnCKeyConstants.SERVICE_NAME, "Mobile"),
	SERVICE_TYPE_INTERNATIONAL(OfflineRnCKeyConstants.SERVICE_NAME, "International"),
	SERVICE_TYPE_1800(OfflineRnCKeyConstants.SERVICE_NAME, "1800"),
	SERVICE_TYPE_SPECIAL_SERVICE(OfflineRnCKeyConstants.SERVICE_NAME, "Special Service"),
	SERVICE_TYPE_SUPPORT(OfflineRnCKeyConstants.SERVICE_NAME, "Support"),
	SERVICE_TYPE_TOLL_FREE(OfflineRnCKeyConstants.SERVICE_NAME, "Toll Free"),
	
	SUB_SERVICE_TYPE_MOBILE_PREPAID(OfflineRnCKeyConstants.SERVICE_NAME, "Mobile Prepaid"),
	SUB_SERVICE_TYPE_MOBILE_POSTPAID(OfflineRnCKeyConstants.SERVICE_NAME, "Mobile Postpaid"),
	SUB_SERVICE_TYPE_VOICE_MAIL(OfflineRnCKeyConstants.SERVICE_NAME, "Voice Mail"),
	RATE_TYPE_SPECIAL_DAY(OfflineRnCKeyConstants.RATE_TYPE,"Special Day"),
	RATE_TYPE_WEEKEND_DAY(OfflineRnCKeyConstants.RATE_TYPE, "Weekend Day"),
	RATE_TYPE_OFF_PEAK_DAY(OfflineRnCKeyConstants.RATE_TYPE, "Off Peak Day"),
	RATE_TYPE_PEAK_DAY(OfflineRnCKeyConstants.RATE_TYPE, "Peak Day"),

	RATING_STREAM_TYPE_RETAIL_INTERCONNECT(OfflineRnCKeyConstants.RATING_STREAM, "0"),
	RATING_STREAM_TYPE_CARRIER_INTERCONNECT(OfflineRnCKeyConstants.RATING_STREAM, "1")
	
	;
	
	public final OfflineRnCKeyConstants key;
	public final String val;

	private OfflineRnCKeyValueConstants(OfflineRnCKeyConstants key, String val) {
		this.key = key;
		this.val = val;
	}
	
	public static List<OfflineRnCKeyValueConstants> valuesFor(OfflineRnCKeyConstants key) {
		return Arrays.stream(values())
				.filter(each -> each.key == key)
				.collect(Collectors.toList());
	}
	
}
