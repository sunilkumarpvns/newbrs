package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class QuotaBreachRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.QUOTA_MODEL_TYPE.typeID,getTLV(is));
		addField(RDRConstants.RDR_REASON.typeID,getTLV(is));
		
		addField(RDRConstants.SUBSCRIBER_ID.typeID,getTLV(is));

		addField(RDRConstants.PACKAGE_ID.typeID,getTLV(is));
		addField(RDRConstants.ADDITIONAL_INFO.typeID,getTLV(is));
		addField(RDRConstants.END_TIME.typeID,getTLV(is));
		addField(RDRConstants.BUCKET_ID.typeID,getTLV(is));
		addField(RDRConstants.BUCKET_TYPE.typeID,getTLV(is));
		addField(RDRConstants.UNIT_AMOUNT_IN.typeID,getTLV(is));
		addField(RDRConstants.UNIT_AMOUNT_OUT.typeID,getTLV(is));
		
		addField(RDRConstants.BUCKET_SIZE_IN.typeID,getTLV(is));
		addField(RDRConstants.BUCKET_SIZE_OUT.typeID,getTLV(is));
	}
}
