package com.elitecore.aaa.radius.service;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * Hamcrest matchers for {@link RadServiceRequest}.
 * 
 * @author narendra.pathai
 *
 */
public class RadServiceRequestMatchers {

	/**
	 * Hamcrest matcher that verifies if request contains given attribute with expected value
	 */
	public static class ContainsAttributeMatcher extends TypeSafeDiagnosingMatcher<RadServiceRequest> {

		private final String attributeId;
		private final String value;

		public ContainsAttributeMatcher(String attributeId, String value) {
			this.attributeId = attributeId;
			this.value = value;
		}
		
		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("request to contain attribute ").appendText(attributeId).appendText(" with value ")
			.appendText(value);
		}

		@Override
		protected boolean matchesSafely(RadServiceRequest arg0, Description arg1) {
			IRadiusAttribute attribute = arg0.getRadiusAttribute(attributeId, true);
			if (attribute == null) {
				arg1.appendText("attribute was not found in request");
				return false;
			}
			
			if (!attribute.getStringValue().equals(value)) {
				arg1.appendText("was found with value ").appendText(attribute.getStringValue());
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * Hamcrest matcher that verifies if request is of expected packet type
	 */
	public static class PacketTypeMatcher extends TypeSafeDiagnosingMatcher<RadServiceRequest> {
		
		private final int packetType;
		
		public PacketTypeMatcher(int packetType) {
			this.packetType = packetType;
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("request with packet type ").appendValue(packetType);
		}

		@Override
		protected boolean matchesSafely(RadServiceRequest arg0, Description arg1) {
			int packetType = arg0.getPacketType();
			
			if (packetType != this.packetType) {
				arg1.appendText("was packet type ").appendValue(packetType);
				return false;
			}
			
			return true;
		}
	}
}
