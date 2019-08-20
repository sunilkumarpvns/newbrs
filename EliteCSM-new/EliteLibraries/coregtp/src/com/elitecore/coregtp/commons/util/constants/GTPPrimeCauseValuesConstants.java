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
public enum GTPPrimeCauseValuesConstants {
	
	SYSTEM_FAILURE(204),
	THE_TRANSMIT_BUFFERS_ARE_BECOMING_FULL(60),
	THE_RECEIVE_BUFFERS_ARE_BECOMING_FULL(61),
	ANOTHER_NODE_IS_ABOUT_TO_GO_DOWN(62),
	THIS_NODE_IS_ABOUT_TO_GO_DOWN(63),
	REQUEST_ACCEPTED(128),
	CDR_DECODING_ERROR(177),
	INVALID_MESSAGE_FORMAT(193),
	VERSION_NOT_SUPPORTED(198),
	NO_RESOURCES_AVAILABLE(199),
	SERVICE_NOT_SUPPORTED(200),
	MANDATORY_IE_INCORRECT(201),
	MANDATORY_IE_MISSING(202),
	OPTIONAL_IE_INCORRECT(203),
	REQUEST_RELATED_TO_POSSIBLY_DUPLICATED_PACKETS_ALREADY_FULFILLED(252),
	REQUEST_ALREADY_FULFILLED(253),
	SEQUENCE_NUMBERS_OF_RELEASED_OR_CANCELLED_PACKETS_IE_INCORRECT(254),
	REQUEST_NOT_FULFILLED(255);

	
	public final int typeID;

	public static final GTPPrimeCauseValuesConstants[] types = values();
	private static final Map<Integer , GTPPrimeCauseValuesConstants> map;

	static {
		map = new HashMap<Integer,GTPPrimeCauseValuesConstants>();
		for ( GTPPrimeCauseValuesConstants type : types){
			map.put(type.typeID, type);
		}

	}

	GTPPrimeCauseValuesConstants (int type){
		this.typeID = type;
	}

	public int getTypeID (){
		return typeID;
	}

	public static GTPPrimeCauseValuesConstants fromTypeID (int type){
		return map.get(type);
	}
	public boolean isValid (int value){
		return map.containsKey(value);
	}
}
