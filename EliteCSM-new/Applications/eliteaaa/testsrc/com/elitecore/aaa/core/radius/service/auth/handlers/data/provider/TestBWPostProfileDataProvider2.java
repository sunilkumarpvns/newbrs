package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;

public class TestBWPostProfileDataProvider2 extends TestBWDataProviderSupport {

	public static Object[] provide_dataFor_UserShouldNotBeBlackListed_WhenConfiguredAttributesAreNotFoundInRequest_1() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:125", "eliteaaa1"));
		list.add(bl("21067:126", "eliteaaa1"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa1");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlackListed_WhenAnyConfiguredAttributeValueDoesNotMatchWithTheSameAttribiuteFromRequest() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:125", "eliteaaa1"));
		list.add(bl("21067:125", "eliteaaa2"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa");
		
		return $(list, newRequest);
	}
	
}
