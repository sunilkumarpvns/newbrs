package com.elitecore.diameterapi.diameter;

import static com.elitecore.commons.base.Equality.areEqual;

import com.elitecore.diameterapi.diameter.common.packet.*;
import com.elitecore.diameterapi.diameter.common.util.constant.AVPType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * A static factory method class for all Diameter Matchers. These matchers can be used in test cases
 * for fluent assertions that are readable.
 *
 * @see Matcher
 * @author narendra.pathai
 *
 */
public class DiameterMatchers {

	/**
	 * A static factory method class for all Diameter Packet Matchers. These matchers can be used in test cases
	 * for fluent assertions that are readable.
	 *
	 * @see Matcher
	 * @author narendra.pathai
	 *
	 */
	public static class PacketMatchers {

		/**
		 * @param resultCode a non-null result code value to be matched
		 * @return a matcher that matches if a diamter answer contains the {@code resultCode}.
		 */
		public static Matcher<DiameterAnswer> hasResultCode(final ResultCode resultCode) {
			return new TypeSafeDiagnosingMatcher<DiameterAnswer>() {

				@Override
				public void describeTo(Description description) {
					description.appendText("an answer with result code ").appendValue(resultCode.code);
				}

				@Override
				protected boolean matchesSafely(DiameterAnswer answer,
												Description description) {
					IDiameterAVP resultCodeAVP = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
					if (resultCodeAVP == null) {
						description.appendText(" was not found in answer.");
						return false;
					}
					if (!(resultCodeAVP.getInteger() == resultCode.code)) {
						description.appendText(" was answer with result code ").appendValue((int) resultCodeAVP.getInteger());
						return false;
					}
					return true;
				}
			};
		}

		/**
		 * @param packetWithExpectedHeader a packet whose header is expected.
		 * @return a matcher that matches header of a diameter packet with header of {@code packetWithExpectedHeader}.
		 */
		public static Matcher<DiameterPacket> hasHeaderOf(final DiameterPacket packetWithExpectedHeader) {
			return new TypeSafeDiagnosingMatcher<DiameterPacket>() {

				@Override
				public void describeTo(Description description) {
					description.appendText("Header with H2H ")
							.appendValue(packetWithExpectedHeader.getHop_by_hopIdentifier())
							.appendText(" E2E ").appendValue(packetWithExpectedHeader.getEnd_to_endIdentifier())
							.appendText(" App-Id ").appendValue(packetWithExpectedHeader.getApplicationID())
							.appendText(" CommandCode ").appendValue(packetWithExpectedHeader.getCommandCode());
				}

				@Override
				protected boolean matchesSafely(DiameterPacket actualPacket,
												Description description) {
					boolean equal = areEqual(packetWithExpectedHeader.getApplicationID(), actualPacket.getApplicationID())
							&& areEqual(packetWithExpectedHeader.getHop_by_hopIdentifier(), actualPacket.getHop_by_hopIdentifier())
							&& areEqual(packetWithExpectedHeader.getEnd_to_endIdentifier(), actualPacket.getEnd_to_endIdentifier())
							&& areEqual(packetWithExpectedHeader.getCommandCode(), actualPacket.getCommandCode());

					if (!equal) {
						description.appendText(" was Header with H2H ")
								.appendValue(packetWithExpectedHeader.getHop_by_hopIdentifier())
								.appendText(" E2E ").appendValue(packetWithExpectedHeader.getEnd_to_endIdentifier())
								.appendText(" App-Id ").appendValue(packetWithExpectedHeader.getApplicationID())
								.appendText(" CommandCode ").appendValue(packetWithExpectedHeader.getCommandCode());
						return false;
					}
					return true;
				}
			};
		}
	}

	public static class DiameterRequestMatcher {

		public static Matcher<? super DiameterRequest> containsAttribute(String attributeId, String value) {
			return new DiameterRequestMatchers.ContainsAttributeMatcher(attributeId, value);
		}
	}

	public static class AvpMatcher {

		public static Matcher<? super IDiameterAVP> containsAttribute(String attributeId, String value) {
			return new com.elitecore.diameterapi.diameter.common.packet.AvpMatchers.ContainsAttributeMatcher(attributeId, value);
		}

		public static Matcher<? super IDiameterAVP> containsAttribute(String attributeId, long value) {
			return new com.elitecore.diameterapi.diameter.common.packet.AvpMatchers.ContainsAttributeMatcher(attributeId, value);
		}

		public static Matcher<? super IDiameterAVP> ofType(AVPType avpType) {
			return new AvpMatchers.AvpOfType(avpType);
		}
	}

	public static class DiameterResponseMatcher {

		public static Matcher<? super DiameterAnswer> containsAttribute(String attributeId, String value) {
			return new DiameterResponseMatchers.ContainsAttributeMatcher(attributeId, value);
		}
	}
}
