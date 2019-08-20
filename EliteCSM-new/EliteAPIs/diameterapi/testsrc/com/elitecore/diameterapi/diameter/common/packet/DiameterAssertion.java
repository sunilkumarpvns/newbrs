package com.elitecore.diameterapi.diameter.common.packet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DiameterAssertion {
	
	public static PacketAssertion assertThat(DiameterPacket packet) {
		return new PacketAssertion(packet);
	}
	
	public static class PacketAssertion {
		private final DiameterPacket packet;

		public PacketAssertion(DiameterPacket packet) {
			this.packet = packet;
		}
		
		public PacketAssertion isRequest() {
			assertTrue(packet.isRequest());
			return this;
		}
		
		public PacketAssertion hasHeaderOf(DiameterPacket packet) {
			assertEquals(this.packet.getHop_by_hopIdentifier(), packet.getHop_by_hopIdentifier());
			assertEquals(this.packet.getEnd_to_endIdentifier(), packet.getEnd_to_endIdentifier());
			assertEquals(this.packet.getCommandCode(), packet.getCommandCode());
			assertEquals(this.packet.getApplicationID(), packet.getApplicationID());
			return this;
		}
		
		public PacketAssertion hasResultCode(ResultCode expectedResultCode) {
			assertNotNull(packet.getAVP(DiameterAVPConstants.RESULT_CODE));
			assertEquals(expectedResultCode.code, packet.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger());
			return this;
		}
		
		public PacketAssertion containsAVP(String avpId, String expectedValue) {
			MatcherAssert.assertThat(
					"Expected packet to contain avp: " + avpId + ", but not found", 
					packet.getAVP(avpId, true), CoreMatchers.is(CoreMatchers.notNullValue()));
			MatcherAssert.assertThat(
					"Expected packet to contain avp: " + avpId + " with value " + expectedValue +", but was: " + packet.getAVPValue(avpId, true),
					packet.getAVPValue(avpId, true), 
					CoreMatchers.is(CoreMatchers.equalTo(expectedValue)));
			
			return this;
		}
		
		public PacketAssertion doesNotContainAVP(String avpId) {
			assertNull(packet.getAVP(avpId, true));
			return this;
		}
		
		public PacketAssertion containsAVP(String avpId) {
			assertNotNull(packet.getAVP(avpId));
			return this;
		}
	}
}
