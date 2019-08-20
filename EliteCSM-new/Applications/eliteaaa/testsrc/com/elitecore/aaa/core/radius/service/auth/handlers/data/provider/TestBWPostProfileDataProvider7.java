package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;

public class TestBWPostProfileDataProvider7 extends TestBWDataProviderSupport {

	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInBlackListHasPastValidity() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bly("21067:124", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:124", "eliteaaa");
		
		return $(list, newRequest, false);
	}

	public static Object[] provide_dataFor_UserShouldBeBlocked_WhenAnyAttributeIsInBlackListAndInWhiteListButAttributeInWhiteListHasPastValidity() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:124", "eliteaaa"));
		list.add(wly("21067:124", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:124", "eliteaaa");
		
		return $(list, newRequest, true);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAnyAttributeIsInWhiteListAndInBlackListButAttributeInBlackListHasPastValidity() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bly("21067:124", "eliteaaa"));
		list.add(wl("21067:124", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:124", "eliteaaa");
		
		return $(list, newRequest, false);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAnyAttributeIsInWhiteListAndInBlackListAndBothHasPastValidity() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bly("21067:124", "eliteaaa"));
		list.add(wly("21067:124", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:124", "eliteaaa");
		
		return $(list, newRequest, false);
	}
}
