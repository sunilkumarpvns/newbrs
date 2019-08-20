package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class GenericUsageRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.GUR_TYPE.typeID,getTLV(is));
		addField(RDRConstants.LINK_ID.typeID,getTLV(is));
		addField(RDRConstants.GENERATOR_ID.typeID,getTLV(is));
		addField(RDRConstants.GLOBAL_COUNTER_ID.typeID,getTLV(is));
		addField(RDRConstants.SUBSCRIBER_COUNTER_ID.typeID,getTLV(is));
		addField(RDRConstants.PACKAGE_COUNTER_ID.typeID,getTLV(is));
		
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
		
		addField(RDRConstants.START_TIME.typeID,getTLV(is));
		addField(RDRConstants.END_TIME.typeID,getTLV(is));
		addField(RDRConstants.ACCESS_STRING.typeID,getTLV(is));
		addField(RDRConstants.INFO_STRING.typeID,getTLV(is));
		
		// 4 Future Use
		
		addField(RDRConstants.UPSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.TOTAL_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.SESSIONS.typeID,getTLV(is));
		addField(RDRConstants.SECONDS.typeID,getTLV(is));
		addField(RDRConstants.CONCURRENT_SESSIONS.typeID,getTLV(is));
		addField(RDRConstants.ACTIVE_SUBSCRIBERS.typeID,getTLV(is));
		addField(RDRConstants.TOTAL_ACTIVE_SUBSCRIBERS.typeID,getTLV(is));
		addField(RDRConstants.CONFIGURED_DURATION.typeID,getTLV(is));
		addField(RDRConstants.DURATION.typeID,getTLV(is));
		
		// 4 Future Use

	}
}
