package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;

public class TestBWPostPolicyDataProvider5 extends
		TestBWDataProviderSupport {

	public static Object[] provide_dataFor_UserShouldBeBlocked_WhenAttributeInRequestIsInBlackListButNotInWhiteList_1() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa"));
		list.add(wl("0:2", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldBeBlocked_WhenAttributeInRequestIsInBlackListButNotInWhiteList_2() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa"));
		list.add(bl("0:1", "eliteaaa1"));
		list.add(wl("0:1", "eliteaaa1"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldBeBlocked_WhenAnyOccuranceOfMultipleAttributeInRequestIsInBlackListButNotInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa2"));
		list.add(bl("0:1", "eliteaaa"));
		list.add(wl("0:1", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa1");
		add(newRequest, "0:1", "eliteaaa2");
		add(newRequest, "0:1", "eliteaaa3");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldBeBlocked_WhenAttributeInRequestMatchesWithExpressionInBlackListButNotInWhiteList() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa*"));
		list.add(wl("0:1", "eliteaaa1*"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa");
		
		return $(list, newRequest);
	}
}
