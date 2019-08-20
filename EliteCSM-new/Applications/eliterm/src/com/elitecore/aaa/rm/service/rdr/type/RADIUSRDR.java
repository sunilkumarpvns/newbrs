package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class RADIUSRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.SERVER_IP.typeID,getTLV(is));
		addField(RDRConstants.SERVER_PORT.typeID,getTLV(is));
		
		addField(RDRConstants.CLIENT_IP.typeID,getTLV(is));
		addField(RDRConstants.CLIENT_PORT.typeID,getTLV(is));
		
		addField(RDRConstants.INITIATING_SIDE.typeID,getTLV(is));
		
		addField(RDRConstants.RADIUS_PACKET_CODE.typeID,getTLV(is));
		addField(RDRConstants.RADIUS_ID.typeID,getTLV(is));
		
		addField(RDRConstants.ATTRIBUTE_VALUE_1.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_2.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_3.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_4.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_5.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_6.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_7.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_8.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_9.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_10.typeID,getTLV(is));
		
		addField(RDRConstants.ATTRIBUTE_VALUE_11.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_12.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_13.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_14.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_15.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_16.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_17.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_18.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_19.typeID,getTLV(is));
		addField(RDRConstants.ATTRIBUTE_VALUE_20.typeID,getTLV(is));
	}
}
