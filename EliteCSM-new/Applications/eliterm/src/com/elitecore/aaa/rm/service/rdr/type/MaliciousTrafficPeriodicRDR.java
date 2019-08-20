package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class MaliciousTrafficPeriodicRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.ATTACK_ID.typeID,getTLV(is));
		addField(RDRConstants.SUBSCRIBER_ID.typeID,getTLV(is));
		
		
		addField(RDRConstants.ATTACK_IP.typeID,getTLV(is));
		
		addField(RDRConstants.OTHER_IP.typeID,getTLV(is));
		addField(RDRConstants.PORT_NUMBER.typeID,getTLV(is));

		addField(RDRConstants.ATTACK_TYPE.typeID,getTLV(is));
		addField(RDRConstants.SIDE.typeID,getTLV(is));
		addField(RDRConstants.IP_PROTOCOL.typeID,getTLV(is));
		
		addField(RDRConstants.CONFIGURED_DURATION.typeID,getTLV(is));
		addField(RDRConstants.DURATION.typeID,getTLV(is));
		addField(RDRConstants.END_TIME.typeID,getTLV(is));
		
		
		addField(RDRConstants.ATTACKS.typeID,getTLV(is));
		
		addField(RDRConstants.MALICIOUS_SESSIONS.typeID,getTLV(is));
	}
}
