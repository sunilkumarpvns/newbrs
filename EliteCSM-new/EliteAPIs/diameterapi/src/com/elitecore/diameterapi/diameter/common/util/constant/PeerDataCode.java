/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.util.constant;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.core.common.fsm.IStateTransitionDataCode;

/**
 * @author pulindani
 *
 */
public enum PeerDataCode implements IStateTransitionDataCode{
	DIAMETER_RECEIVED_PACKET(0),	
	DIAMETER_PACKET_TO_SEND(1),
	CONNECTION(2),
	PEER_EVENT(3),
	PEER_STATE(4),
	USER_SESSION(6),
	DISCONNECT_REASON(7),
	RESPONSE_LISTENER(8),
	;

	private static final Map<Integer,PeerDataCode> map;
	public static final PeerDataCode[] VALUES = values();

	static {
		map = new HashMap<Integer,PeerDataCode>();
		for (PeerDataCode type : VALUES) {
			map.put(type.code, type);
		}
	}

	public final int code;

	PeerDataCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

	public static PeerDataCode fromCode(int value) {
		return map.get(value);
	}

	public int getStateTransitionDataCode() {
		return this.ordinal();
	}
}
