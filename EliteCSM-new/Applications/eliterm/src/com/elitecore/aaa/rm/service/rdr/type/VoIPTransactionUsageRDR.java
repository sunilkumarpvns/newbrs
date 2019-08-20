package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class VoIPTransactionUsageRDR extends BaseRDR {
	public void read(InputStream is){
		addField(RDRConstants.SUBSCRIBER_ID.typeID,getTLV(is));
		addField(RDRConstants.PACKAGE_ID.typeID,getTLV(is));
		addField(RDRConstants.SERVICE_ID.typeID,getTLV(is));
		addField(RDRConstants.PROTOCOL_ID.typeID,getTLV(is));
		addField(RDRConstants.SKIPPED_SESSIONS.typeID,getTLV(is));
		addField(RDRConstants.SERVER_IP.typeID,getTLV(is));
		addField(RDRConstants.SERVER_PORT.typeID,getTLV(is));
		addField(RDRConstants.ACCESS_STRING.typeID,getTLV(is));
		addField(RDRConstants.INFO_STRING.typeID,getTLV(is));
		addField(RDRConstants.CLIENT_IP.typeID,getTLV(is));
		addField(RDRConstants.CLIENT_PORT.typeID,getTLV(is));
		addField(RDRConstants.INITIATING_SIDE.typeID,getTLV(is));
		addField(RDRConstants.REPORT_TIME.typeID,getTLV(is));
		addField(RDRConstants.MILLISEC_DURATION.typeID,getTLV(is));
		addField(RDRConstants.TIME_FRAME.typeID,getTLV(is));
		addField(RDRConstants.SESSION_UPSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.SESSION_DOWNSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.SUBSCRIBER_COUNTER_ID.typeID,getTLV(is));
		addField(RDRConstants.GLOBAL_COUNTER_ID.typeID,getTLV(is));
		addField(RDRConstants.PACKAGE_COUNTER_ID.typeID,getTLV(is));
		addField(RDRConstants.IP_PROTOCOL.typeID,getTLV(is));
		addField(RDRConstants.PROTOCOL_SIGNATURE.typeID,getTLV(is));
		addField(RDRConstants.ZONE_ID.typeID,getTLV(is));
		addField(RDRConstants.FLAVOR_ID.typeID,getTLV(is));
		addField(RDRConstants.FLOW_CLOSE_MODE.typeID,getTLV(is));
		
		addField(RDRConstants.APPLICATION_ID.typeID,getTLV(is));
		addField(RDRConstants.UPSTREAM_PACKET_LOSS.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_PACKET_LOSS.typeID,getTLV(is));
		addField(RDRConstants.UPSTREAM_AVERAGE_JITTER.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_AVERAGE_JITTER.typeID,getTLV(is));
		addField(RDRConstants.CALL_DESTINATION.typeID,getTLV(is));
		addField(RDRConstants.CALL_SOURCE.typeID,getTLV(is));
		addField(RDRConstants.UPSTREAM_PAYLOAD_TYPE.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_PAYLOAD_TYPE.typeID,getTLV(is));
		addField(RDRConstants.CALL_TYPE.typeID,getTLV(is));
		addField(RDRConstants.MEDIA_CHANNELS.typeID,getTLV(is));
	}
}
