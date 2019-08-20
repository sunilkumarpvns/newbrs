/**
 * 
 */
package com.elitecore.coregtp.commons.util.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dhaval.jobanputra
 *
 */
public enum GTPPrimeElementTypeConstants {
	
	CAUSE(1),
	RECOVERY(14),
	PACKET_TRANSFER_COMMAND(126),
	CHARGING_ID(127),
	SEQUENCE_NUMBER_OF_REALSED_PACKETS (249),
	SEQUENCE_NUMBER_OF_CANCELLED_PACKETS (250),
	NODE_ADDRES(251),
	DATA_RECORD_PACKET(252),
	REQUESTS_RESPONDED(253),
	ADDRESS_OF_RECOMMENDED_NODE(254);
	
	public final int typeID;

	public static final GTPPrimeElementTypeConstants[] types = values();
	private static final Map<Integer , GTPPrimeElementTypeConstants> map;

	static {
		map = new HashMap<Integer,GTPPrimeElementTypeConstants>();
		for ( GTPPrimeElementTypeConstants type : types){
			map.put(type.typeID, type);
		}
	}

	GTPPrimeElementTypeConstants (int type){
		this.typeID = type;
	}

	public int getTypeID (){
		return typeID;
	}

	public static GTPPrimeElementTypeConstants fromTypeID (int type){
		return map.get(type);
	}
	public boolean isValid (int value){
		return map.containsKey(value);
	}
	
	
}
