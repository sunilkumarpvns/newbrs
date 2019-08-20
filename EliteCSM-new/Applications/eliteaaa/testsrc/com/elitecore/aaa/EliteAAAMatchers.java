package com.elitecore.aaa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.hamcrest.Matcher;

import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * A common entry class for all the hamcrest matchers created in eliteaaa. 
 * This class provides direct static factory methods to create matcher instances.
 * 
 * @author narendra.pathai
 *
 */
public class EliteAAAMatchers {

	/**
	 * Hamcrest matchers for {@link ApplicationResponse}.
	 */
	public static class ApplicationResponseMatchers {
		
		/**
		 * @return a matcher that checks whether application response is proxiable.
		 */
		public static Matcher<? super ApplicationResponse> isProxiable() {
			return com.elitecore.aaa.diameter.service.application.ApplicationResponseMatchers.isProxiable();
		}
	}
		
	/**
	 * Hamcrest matchers for {@link RadServiceRequest}.
	 */
	public static class RadServiceRequestMatchers {
		
		/**
		 * Hamcrest matcher that verifies if response contains given attribute with expected value
		 */
		public static Matcher<? super RadServiceRequest> containsAttribute(String attributeId, String value) {
			return new com.elitecore.aaa.radius.service.RadServiceRequestMatchers.ContainsAttributeMatcher(attributeId, value);
		}
		
		/**
		 * Hamcrest matcher that verifies if request is of expected packet type
		 */
		public static Matcher<? super RadServiceRequest> packetType(int packetType){
			return new com.elitecore.aaa.radius.service.RadServiceRequestMatchers.PacketTypeMatcher(packetType);
		}
		
		/**
		 * Hamcrest matcher that verifies if request is of expected packet type
		 */
		public static Matcher<? super RadServiceRequest> packetType(String packetType){
			return new com.elitecore.aaa.radius.service.RadServiceRequestMatchers.PacketTypeMatcher(Integer.parseInt(packetType));
		}
	}
	
	/**
	 * Hamcrest matchers for {@link RadServiceResponse}.
	 */
	public static class RadServiceResponseMatchers {
		
		/**
		 * Hamcrest matcher that verifies if response contains given attribute with expected value
		 */
		public static Matcher<? super RadServiceResponse> containsAttribute(String attributeId, String value) {
			return new com.elitecore.aaa.radius.service.RadServiceResponseMatchers.ContainsAttributeMatcher(attributeId, equalTo(value));
		}
		
		/**
		 * Hamcrest matcher that verifies if response contains given attribute and matches provided matcher
		 */
		public static Matcher<? super RadServiceResponse> containsAttribute(String attributeId, Matcher<? super String> valueMatcher) {
			return new com.elitecore.aaa.radius.service.RadServiceResponseMatchers.ContainsAttributeMatcher(attributeId, valueMatcher);
		}
		
		/**
		 * Hamcrest matcher that verifies if response contains given attribute with some value
		 */
		public static Matcher<? super RadServiceResponse> containsAttribute(String attributeId) {
			return new com.elitecore.aaa.radius.service.RadServiceResponseMatchers.ContainsAttributeMatcher(attributeId, notNullValue());
		}
		
		/**
		 * Hamcrest matcher that verifies if response is of expected packet type
		 */
		public static Matcher<? super RadServiceResponse> packetType(int packetType){
			return new com.elitecore.aaa.radius.service.RadServiceResponseMatchers.PacketTypeMatcher(packetType);
		}
		
		/**
		 * Hamcrest matcher that verifies if response is of expected packet type
		 */
		public static Matcher<? super RadServiceResponse> packetType(String packetType){
			return new com.elitecore.aaa.radius.service.RadServiceResponseMatchers.PacketTypeMatcher(Integer.parseInt(packetType));
		}
		
		/**
		 * Hamcrest matcher that verifies if response packet type is {@link RadiusConstants#ACCESS_REJECT_MESSAGE}.
		 * <pre>
		 * <code>
		 * 	assertThat(response, is(rejectMessage()));
		 * </code>
		 * </pre>
		 */
		public static Matcher<? super RadServiceResponse> rejectMessage() {
			return new com.elitecore.aaa.radius.service.RadServiceResponseMatchers.PacketTypeMatcher(RadiusConstants.ACCESS_REJECT_MESSAGE);
		}

		/**
		 * Hamcrest matcher that verifies if response packet type is {@link RadiusConstants#ACCESS_ACCEPT_MESSAGE}.
		 * <pre>
		 * <code>
		 * 	assertThat(response, is(acceptMessage()));
		 * </code>
		 * </pre>
		 */
		public static Matcher<? super RadServiceResponse> acceptMessage() {
			return new com.elitecore.aaa.radius.service.RadServiceResponseMatchers.PacketTypeMatcher(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
		}
	}
}
