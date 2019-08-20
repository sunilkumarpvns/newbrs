package com.elitecore.coregtp.commons.util.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dhaval.jobanputra
 *
 */
public enum GTPPrimeConstants{

	// packet types
	ECHO_REQUEST (1),
	ECHO_RESPONSE (2),
	VERSION_NOT_SUPPORTED (3),
	NODE_ALIVE_REQUEST (4),
	NODE_ALIVE_RESPONSE (5),
	REDIRECTION_REQUEST (6),
	REDIRECTION_RESPONSE (7),
	DATA_RECORD_TRANSFER_REQUEST (240),
	DATA_RECORD_TRANSFER_RESPONSE (241);



	public final int typeID;

	public static final GTPPrimeConstants[] types = values();
	private static final Map<Integer , GTPPrimeConstants> map;

	static {
		map = new HashMap<Integer,GTPPrimeConstants>();
		for ( GTPPrimeConstants type : types){
			map.put(type.typeID, type);
		}

	}

	GTPPrimeConstants (int type){
		this.typeID = type;
	}

	public int getTypeID (){
		return typeID;
	}

	public static GTPPrimeConstants fromTypeID (int type){
		return map.get(type);
	}
	public boolean isValid (int value){
		return map.containsKey(value);
	}
}