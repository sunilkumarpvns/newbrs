package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class DHCPRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.CPE_MAC.typeID,getTLV(is));
		addField(RDRConstants.CMTS_IP.typeID,getTLV(is));
		
		addField(RDRConstants.ASSIGNED_IP.typeID,getTLV(is));

		addField(RDRConstants.RELEASED_IP.typeID,getTLV(is));
		addField(RDRConstants.TRANSACTION_ID.typeID,getTLV(is));
		addField(RDRConstants.MESSAGE_TYPE.typeID,getTLV(is));
		
		addField(RDRConstants.OPTION_TYPE_0.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_1.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_2.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_3.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_4.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_5.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_6.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_7.typeID,getTLV(is));

		// 
		
		addField(RDRConstants.OPTION_TYPE_0.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_1.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_2.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_3.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_4.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_5.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_6.typeID,getTLV(is));
		addField(RDRConstants.OPTION_TYPE_7.typeID,getTLV(is));
		
		addField(RDRConstants.END_TIME.typeID,getTLV(is));
	}
}
