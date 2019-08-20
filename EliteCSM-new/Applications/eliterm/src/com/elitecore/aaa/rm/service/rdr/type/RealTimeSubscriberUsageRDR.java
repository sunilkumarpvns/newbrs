package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class RealTimeSubscriberUsageRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.SUBSCRIBER_ID.typeID,getTLV(is));
		addField(RDRConstants.PACKAGE_ID.typeID,getTLV(is));
		
		addField(RDRConstants.SERVICE_USAGE_COUNTER_ID.typeID,getTLV(is));
		addField(RDRConstants.AGGREGATION_OBJECT_ID.typeID,getTLV(is));
		addField(RDRConstants.BREACH_STATE.typeID,getTLV(is));
		
		addField(RDRConstants.REASON.typeID,getTLV(is));
		addField(RDRConstants.CONFIGURED_DURATION.typeID,getTLV(is));
		addField(RDRConstants.DURATION.typeID,getTLV(is));
		addField(RDRConstants.END_TIME.typeID,getTLV(is));
		addField(RDRConstants.UPSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.DOWNSTREAM_VOLUME.typeID,getTLV(is));
		addField(RDRConstants.SESSIONS.typeID,getTLV(is));
		addField(RDRConstants.SECONDS.typeID,getTLV(is));
	}
}
