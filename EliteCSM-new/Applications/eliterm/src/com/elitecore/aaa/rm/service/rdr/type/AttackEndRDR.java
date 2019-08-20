package com.elitecore.aaa.rm.service.rdr.type;

import java.io.InputStream;

import com.elitecore.aaa.rm.service.rdr.BaseRDR;
import com.elitecore.aaa.rm.service.rdr.RDRConstants;

public class AttackEndRDR extends BaseRDR {
	public void read(InputStream is){
		
		addField(RDRConstants.SUBSCRIBER_ID.typeID,getTLV(is));
		
		addField(RDRConstants.ATTACK_ID.typeID,getTLV(is));
		addField(RDRConstants.ATTACKING_IP.typeID,getTLV(is));
		
		addField(RDRConstants.ATTACKED_IP.typeID,getTLV(is));
		addField(RDRConstants.ATTACKED_PORT.typeID,getTLV(is));

		addField(RDRConstants.ATTACKING_SIDE.typeID,getTLV(is));
		
		addField(RDRConstants.IP_PROTOCOL.typeID,getTLV(is));
		addField(RDRConstants.ATTACK_TYPE.typeID,getTLV(is));
		
		addField(RDRConstants.GENERATOR_ID.typeID,getTLV(is));
		
		addField(RDRConstants.ATTACK_TIME.typeID,getTLV(is));
		addField(RDRConstants.REPORT_TIME.typeID,getTLV(is));
	}
}
