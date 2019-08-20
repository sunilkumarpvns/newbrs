package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;

public class TestBWPostProfileDataProvider6 extends
		TestBWDataProviderSupport {

	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAnyAttributeInRequestIsInBlackListAndInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:124", "eliteaaa"));
		list.add(wl("21067:124", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:124", "eliteaaa");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAnyAttributeInRequestIsInBlackListAndDiifferentAttributeFromTheRequestIsInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:124", "eliteaaa"));
		list.add(wl("21067:125", "eliteaaa1"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:124", "eliteaaa");
		add(newRequest, "21067:125", "eliteaaa1");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenOneOccuranceOfAttributeInBlackListAndAnotherIsInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:124", "eliteaaa"));
		list.add(wl("21067:124", "eliteaaa1"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:124", "eliteaaa");
		add(newRequest, "21067:124", "eliteaaa1");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInRequestIsInWhiteListAndMatchesWithExpressionInBlackList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:125", "eliteaaa*"));
		list.add(wl("21067:125", "eliteaaa123"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa123");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInRequestIsInBlackListAndMatchesWithExpressionInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:125", "eliteaaa123"));
		list.add(wl("21067:125", "eliteaaa*"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa123");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInRequestMatchesWithExpressionInBlackListAndInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("21067:125", "eliteaaa*"));
		list.add(wl("21067:125", "eliteaaa1*"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa123");
		
		return $(list, newRequest);
	}
}
