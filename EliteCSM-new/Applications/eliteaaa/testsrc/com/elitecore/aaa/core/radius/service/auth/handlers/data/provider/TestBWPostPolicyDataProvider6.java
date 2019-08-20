package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;

public class TestBWPostPolicyDataProvider6 extends
		TestBWDataProviderSupport {

	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAnyAttributeInRequestIsInBlackListAndInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:2", "eliteaaa"));
		list.add(wl("0:2", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:2", "eliteaaa");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAnyAttributeInRequestIsInBlackListAndDiifferentAttributeFromTheRequestIsInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:2", "eliteaaa"));
		list.add(wl("0:1", "eliteaaa1"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:2", "eliteaaa");
		add(newRequest, "0:1", "eliteaaa1");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenOneOccuranceOfAttributeInBlackListAndAnotherIsInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:2", "eliteaaa"));
		list.add(wl("0:2", "eliteaaa1"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:2", "eliteaaa");
		add(newRequest, "0:2", "eliteaaa1");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInRequestIsInWhiteListAndMatchesWithExpressionInBlackList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa*"));
		list.add(wl("0:1", "eliteaaa123"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa123");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInRequestIsInBlackListAndMatchesWithExpressionInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa123"));
		list.add(wl("0:1", "eliteaaa*"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa123");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInRequestMatchesWithExpressionInBlackListAndInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa*"));
		list.add(wl("0:1", "eliteaaa1*"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa123");
		
		return $(list, newRequest);
	}
}
