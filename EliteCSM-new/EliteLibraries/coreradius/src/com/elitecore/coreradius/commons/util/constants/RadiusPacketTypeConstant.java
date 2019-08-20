package com.elitecore.coreradius.commons.util.constants;

import java.util.HashMap;
import java.util.Map;

public enum RadiusPacketTypeConstant {
	ACCESS_REQUEST(1,"ACCESS_REQUEST"),
	ACCESS_ACCEPT(2,"ACCESS_ACCEPT"),
	ACCESS_REJECT(3,"ACCESS_REJECT"),
	ACCOUNTING_REQUEST(4,"ACCOUNTING_REQUEST"),
	ACCOUNTING_RESPONSE(5,"ACCOUNTING_RESPONSE"),
	ACCOUNTING_STATUS(6,"ACCOUNTING_STATUS"),
	PASSWORD_REQUEST(7,"PASSWORD_REQUEST"),
	PASSWORD_ACCEPT(8,"PASSWORD_ACCEPT"),
	PASSWORD_REJECT(9,"PASSWORD_REJECT"),
	ACCOUNTING_MESSAGE(10,"ACCOUNTING_MESSAGE"),
	ACCESS_CHALLENGE(11,"ACCESS_CHALLENGE"),
	STATUS_SERVER(12,"STATUS_SERVER"),
	STATUS_CLIENT(13,"STATUS_CLIENT"),
	RESOURCE_FREE_REQUEST(21, "RESOURCE_FREE_REQUEST"),
	RESOURCE_FREE_RESPONSE(22, "RESOURCE_FREE_RESPONSE"),
	RESOURCE_QUERY_REQ(23,"RESOURCE_QUERY_REQ"),
	RESOURCE_QUERY_RESP(24,"RESOURCE_QUERY_RESP"),
	RESOURCE_REAUTHORIZE_REQUEST(25,"RESOURCE_REAUTHORIZE_REQUEST"),
	DISCONNECT_REQUEST(40,"DISCONNECT_REQUEST"),
	DISCONNECT_ACK(41,"DISCONNECT_ACK"),
	DISCONNECT_NAK(42,"DISCONNECT_NAK"),
	COA_REQUEST(43, "COA_REQUEST"),
	COA_ACK(44, "COA_ACK"),
	COA_NAK(45, "COA_NAK"),
	RESERVED(255,"RESERVED"),
	NAS_REBOOT_REQUEST(26, "NAS_REBOOT_REQUEST"),
	NAS_REBOOT_RESPONSE(27, "NAS_REBOOT_RESPONSE"),

	IP_ADDRESS_ALLOCATE_MESSAGE(50, "IP_ADDRESS_ALLOCATE_MESSAGE"),
	IP_ADDRESS_RELEASE_MESSAGE(51, "IP_ADDRESS_RELEASE_MESSAGE"),
	IP_UPDATE_MESSAGE(52, "IP_UPDATE_MESSAGE"),

	TEST_MESSAGE(254, "TEST_MESSAGE"),
	TIMEOUT_MESSAGE(253, "TIMEOUT_MESSAGE"),
	NO_RM_COMMUNICATION_MESSAGE(252, "NO_RM_COMMUNICATION_MESSAGE");

	public int packetTypeId;
	public String packetTypeString;

	private RadiusPacketTypeConstant(int packetTypeId, String packetTypeString) {
		this.packetTypeId = packetTypeId;
		this.packetTypeString = packetTypeString;
	}

	private static final Map<Integer,RadiusPacketTypeConstant> map;
	static {
		map = new HashMap<Integer,RadiusPacketTypeConstant>();
		for (RadiusPacketTypeConstant type : values()) {
			map.put(type.packetTypeId, type);
		}
	}

	public static RadiusPacketTypeConstant from(int packetId){
		return map.get(packetId);
	}

	@Override
	public String toString() {
		return packetTypeString;
	}
}
