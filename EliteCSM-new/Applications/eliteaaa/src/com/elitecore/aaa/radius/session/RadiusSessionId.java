package com.elitecore.aaa.radius.session;

import static com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants.THREEGPP2_CORRELATION_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.base.Strings;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;

public class  RadiusSessionId {

	public static final String MODULE = "RAD-SESS-ID";
	
	private static final String ATTRIBUTE_SEPARATOR = ";|,";
	public static final String RADIUS_SESSION_ID = "radius-session-id";
	
	private static List<String> sessionAttributes;
	private static String sessionAttributeStr = "";
	
	public static final List<String> DEFAULT_SESSION_ATTRIBUTE_LIST = Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(
			RadiusConstants.WIMAX_VENDOR_ID + ":" + WimaxAttrConstants.AAA_SESSION_ID.getIntValue(),
			RadiusConstants.VENDOR_3GPP2_ID + ":" + THREEGPP2_CORRELATION_ID)));

	private static void parseAttributesForRadiusSessionId() {
		sessionAttributes = new ArrayList<>();
		sessionAttributeStr = System.getProperty(RADIUS_SESSION_ID);

		if (Strings.isNullOrBlank(sessionAttributeStr) == false) {
			String[] tokens = sessionAttributeStr.split(ATTRIBUTE_SEPARATOR);

			for (String id : tokens) {
				if (Strings.isNullOrBlank(id) == false) {
					sessionAttributes.add(id.trim());
				}
			}	
		} else {
			sessionAttributeStr = "";
		}
	}
	
	public static String sessionId(RadServiceRequest radiusServiceRequest) {
		IRadiusAttribute radiusAttr=null;
		
		if (sessionAttributeStr.equals(System.getProperty(RADIUS_SESSION_ID)) == false) {
			parseAttributesForRadiusSessionId();
		}
		
		for (String attributeId : DEFAULT_SESSION_ATTRIBUTE_LIST) {
			radiusAttr= radiusServiceRequest.getRadiusAttribute(attributeId);
			if (radiusAttr != null) {
				return radiusAttr.getStringValue();
			}
		}

		StringBuilder radiusSessionId = new StringBuilder();
		for (String attributeId : sessionAttributes) {
			radiusAttr= radiusServiceRequest.getRadiusAttribute(attributeId);
			if (radiusAttr != null) {
				radiusSessionId.append(";")
					.append(radiusAttr.getStringValue());
			}
		}
		
		return radiusSessionId.length() == 0 ? null : radiusSessionId.deleteCharAt(0).toString();
	}
	
}
