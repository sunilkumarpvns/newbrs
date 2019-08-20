package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class MediaFlowRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.SUBSCRIBER_ID.typeID,getTLV(is));
		addField(RDRConstants.PACKAGE_ID.typeID,getTLV(is));
		addField(RDRConstants.SERVICE_ID.typeID,getTLV(is));
		addField(RDRConstants.PROTOCOL_ID.typeID,getTLV(is));
		
		addField(RDRConstants.DESTINATION_IP.typeID,getTLV(is));
		addField(RDRConstants.DESTINATION_PORT.typeID,getTLV(is));
		
		addField(RDRConstants.SOURCE_IP.typeID,getTLV(is));
		addField(RDRConstants.SOURCE_PORT.typeID,getTLV(is));
		
		addField(RDRConstants.INITIATING_SIDE.typeID,getTLV(is));
		
		addField(RDRConstants.ZONE_ID.typeID,getTLV(is));
		addField(RDRConstants.FLAVOR_ID.typeID,getTLV(is));
		addField(RDRConstants.DOMAIN.typeID,getTLV(is));
		addField(RDRConstants.USER_AGENT.typeID,getTLV(is));

		addField(RDRConstants.START_TIME.typeID,getTLV(is));
		addField(RDRConstants.REPORT_TIME.typeID,getTLV(is));
		
		addField(RDRConstants.DURATION_SECONDS.typeID,getTLV(is));
		
		addField(RDRConstants.UPSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_VOLUME.typeID,getTLV(is));
		
		addField(RDRConstants.IP_PROTOCOL.typeID,getTLV(is));
		addField(RDRConstants.FLOW_TYPE.typeID,getTLV(is));
		addField(RDRConstants.SESSION_ID.typeID,getTLV(is));
		
		addField(RDRConstants.UPSTREAM_JITTER.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_JITTER.typeID,getTLV(is));
		
		addField(RDRConstants.UPSTREAM_PACKET_LOSS.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_PACKET_LOSS.typeID,getTLV(is));
		
		addField(RDRConstants.UPSTREAM_PAYLOAD_TYPE.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_PAYLOAD_TYPE.typeID,getTLV(is));
	}
}
