package com.elitecore.aaa.radius.service;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * Hamcrest matchers for {@link RadServiceRequest}.
 * 
 * @author narendra.pathai
 *
 */
public class RadServiceResponseMatchers {

	/**
	 * Hamcrest matcher that verifies if response contains given attribute with expected value
	 */
	public static class ContainsAttributeMatcher extends TypeSafeDiagnosingMatcher<RadServiceResponse> {

		private final String attributeId;
		private final Matcher<? super String> valueMatcher;

		public ContainsAttributeMatcher(String attributeId, Matcher<? super String> valueMatcher) {
			this.attributeId = attributeId;
			this.valueMatcher = valueMatcher;
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("response to contain attribute ").appendText(attributeId).appendText(" with value ")
				.appendDescriptionOf(valueMatcher);
		}

		@Override
		protected boolean matchesSafely(RadServiceResponse arg0, Description arg1) {
			IRadiusAttribute attribute = arg0.getRadiusAttribute(true, attributeId);
			if (attribute == null) {
				arg1.appendText("attribute was not found in response");
				return false;
			}

			if (!valueMatcher.matches(attribute.getStringValue())) {
				valueMatcher.describeMismatch(attribute.getStringValue(), arg1);
				return false;
			}

			return true;
		}
	}

	/**
	 * Hamcrest matcher that verifies if response is of expected packet type
	 */
	public static class PacketTypeMatcher extends TypeSafeDiagnosingMatcher<RadServiceResponse> {

		private final int packetType;

		public PacketTypeMatcher(int packetType) {
			this.packetType = packetType;
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("response with packet type ").appendValue(packetType);
		}

		@Override
		protected boolean matchesSafely(RadServiceResponse arg0, Description arg1) {
			int packetType = arg0.getPacketType();

			if (packetType != this.packetType) {
				arg1.appendText("was packet type ").appendValue(packetType);
				return false;
			}

			return true;
		}
	}
}
