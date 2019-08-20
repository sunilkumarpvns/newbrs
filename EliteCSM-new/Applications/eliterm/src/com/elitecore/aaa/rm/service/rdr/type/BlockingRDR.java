package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class BlockingRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.SUBSCRIBER_ID.typeID,getTLV(is));
		addField(RDRConstants.PACKAGE_ID.typeID,getTLV(is));
		addField(RDRConstants.SERVICE_ID.typeID,getTLV(is));
		addField(RDRConstants.PROTOCOL_ID.typeID,getTLV(is));
		
		addField(RDRConstants.CLIENT_IP.typeID,getTLV(is));
		addField(RDRConstants.CLIENT_PORT.typeID,getTLV(is));
		addField(RDRConstants.SERVER_IP.typeID,getTLV(is));
		addField(RDRConstants.SERVER_PORT.typeID,getTLV(is));
		
		addField(RDRConstants.INITIATING_SIDE.typeID,getTLV(is));

		addField(RDRConstants.ACCESS_STRING.typeID,getTLV(is));
		addField(RDRConstants.INFO_STRING.typeID,getTLV(is));
		
		addField(RDRConstants.BLOCK_REASON.typeID,getTLV(is));
		addField(RDRConstants.BLOCK_RDR_COUNT.typeID,getTLV(is));
		addField(RDRConstants.REDIRECTED.typeID,getTLV(is));
		addField(RDRConstants.REPORT_TIME.typeID,getTLV(is));
	}
}
