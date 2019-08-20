package com.elitecore.coreradius.commons.util.constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class RadiusPacketTypeConstantTest {

	@Test
	@Parameters(method = "dataFor_TestPacketTypeConstants")
	public void testOf_ShouldReturnRadiusPacketTypeConstantBasedOnPacketTypeId(int packetTypeId, String packetTypeString){
		assertNotNull(RadiusPacketTypeConstant.from(packetTypeId));
		assertEquals(packetTypeId, RadiusPacketTypeConstant.from(packetTypeId).packetTypeId);
		assertEquals(packetTypeString, RadiusPacketTypeConstant.from(packetTypeId).packetTypeString);
	}
	
	
	@Test
	@Parameters(method = "dataFor_TestPacketTypeConstants")
	public void testToString_ShouldReturnRadiusPacketTypeString(int packetTypeId, String packetTypeString){
		assertEquals(packetTypeString, RadiusPacketTypeConstant.from(packetTypeId).toString());
	}
	
	
	public static Object[][] dataFor_TestPacketTypeConstants(){
		return new Object[][]{
				{1, "ACCESS_REQUEST"},
				{2,"ACCESS_ACCEPT"},
				{3,"ACCESS_REJECT"},
				{4,"ACCOUNTING_REQUEST"},
				{5,"ACCOUNTING_RESPONSE"},
				{6,"ACCOUNTING_STATUS"},
				{7,"PASSWORD_REQUEST"},
				{8,"PASSWORD_ACCEPT"},
				{9,"PASSWORD_REJECT"},
				{10,"ACCOUNTING_MESSAGE"},
				{11,"ACCESS_CHALLENGE"},
				{12,"STATUS_SERVER"},
				{13,"STATUS_CLIENT"},
				{21, "RESOURCE_FREE_REQUEST"},
				{22, "RESOURCE_FREE_RESPONSE"},
				{23,"RESOURCE_QUERY_REQ"},
				{24,"RESOURCE_QUERY_RESP"},
				{25,"RESOURCE_REAUTHORIZE_REQUEST"},
				{26, "NAS_REBOOT_REQUEST"},
				{27, "NAS_REBOOT_RESPONSE"},
				{40,"DISCONNECT_REQUEST"},
				{41,"DISCONNECT_ACK"},
				{42,"DISCONNECT_NAK"},
				{43, "COA_REQUEST"},
				{44, "COA_ACK"},
				{45, "COA_NAK"},
				{50, "IP_ADDRESS_ALLOCATE_MESSAGE"},
				{51, "IP_ADDRESS_RELEASE_MESSAGE"},
				{52, "IP_UPDATE_MESSAGE"},
				{252, "NO_RM_COMMUNICATION_MESSAGE"},
				{253, "TIMEOUT_MESSAGE"},
				{254, "TEST_MESSAGE"},
				{255,"RESERVED"}
		};
	}
}
