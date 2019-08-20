package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class ZoneUsageRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.ZONE_COUNTER_ID.typeID,getTLV(is));
		addField(RDRConstants.GENERATOR_ID.typeID,getTLV(is));
		
		addField(RDRConstants.SERVICE_USAGE_COUNTER_ID.typeID,getTLV(is));

		addField(RDRConstants.CONFIGURED_DURATION.typeID,getTLV(is));
		addField(RDRConstants.DURATION.typeID,getTLV(is));
		addField(RDRConstants.END_TIME.typeID,getTLV(is));
		addField(RDRConstants.UPSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.SESSIONS.typeID,getTLV(is));
		addField(RDRConstants.SECONDS.typeID,getTLV(is));
		
		addField(RDRConstants.CONCURRENT_SESSIONS.typeID,getTLV(is));
		addField(RDRConstants.ACTIVE_SUBSCRIBERS.typeID,getTLV(is));
		addField(RDRConstants.TOTAL_ACTIVE_SUBSCRIBERS.typeID,getTLV(is));
	}
}
