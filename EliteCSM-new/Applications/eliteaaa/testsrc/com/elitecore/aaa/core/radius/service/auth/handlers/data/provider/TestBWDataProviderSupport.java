package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.AssumptionViolatedException;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.radius.conf.impl.RadBWListConfigurable;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

public abstract class TestBWDataProviderSupport {
	
	static {
		RadiusDictionaryTestHarness.getInstance();
	}
	
	public static AttributeDetails bl(String id, String value) {
		AttributeDetails attributeDetails = new AttributeDetails();
		attributeDetails.setType(RadBWListConfigurable.BLACK);
		attributeDetails.setValue(value);
		attributeDetails.setValidity(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
		attributeDetails.setId(id);
		return attributeDetails;
	}
	
	public static AttributeDetails wl(String id, String value) {
		AttributeDetails attributeDetails = new AttributeDetails();
		attributeDetails.setType(RadBWListConfigurable.WHITE);
		attributeDetails.setValue(value);
		attributeDetails.setValidity(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));
		attributeDetails.setId(id);
		return attributeDetails;
	}
	
	public static void add(RadServiceRequest request, String id, String value) {
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(id);
		attribute.setStringValue(value);
		request.addInfoAttribute(attribute);
	}

	public static RadAuthRequest newRequest() {
		try {
			return new RadiusAuthRequestImpl(new byte[]{}, InetAddress.getLocalHost(),
					0, null, new SocketDetail(InetAddress.getLocalHost().getHostName(), 0));
		} catch (UnknownHostException e) {
			throw new AssumptionViolatedException("Localhost resolution must not fail");
		}
	}
	
	private static AttributeDetails setYesterday(AttributeDetails attributeDetails) {
		attributeDetails.setValidity(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
		return attributeDetails;
	}
	
	public static AttributeDetails bly(String id, String value) {
		return setYesterday(bl(id, value));
	}
	
	public static AttributeDetails wly(String id, String value) {
		return setYesterday(wl(id, value));
	}
}
