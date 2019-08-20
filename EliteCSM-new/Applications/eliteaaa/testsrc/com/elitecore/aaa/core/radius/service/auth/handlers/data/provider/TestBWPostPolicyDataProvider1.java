package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;

public class TestBWPostPolicyDataProvider1 extends TestBWDataProviderSupport {
	
	public static Object[] provide_dataFor_UserShouldBeBlackListed_WithSingleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_1() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldBeBlackListed_WithSingleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_2() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa1");

		return $(list, newRequest);
	}
	
	
	public static Object[] provide_dataFor_UserShouldBeBlackListed_WithMultipleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_1() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:2", "eliteaaa"));
		list.add(bl("0:1", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa1");

		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldBeBlackListed_WithMultipleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_2() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:2", "eliteaaa1"));
		list.add(bl("0:1", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa1");

		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldBeBlackListed_WhenAttributeInRequestMatchesProvidedExpression() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa*"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa1");

		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldBeBlackListed_WhenAnyAttributeOfMultipleOccurancesFromRequestMatchesAnyOfConfiguredValues() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(bl("0:1", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "0:1", "eliteaaa4");
		add(newRequest, "0:1", "eliteaaa3");
		add(newRequest, "0:1", "eliteaaa2");
		add(newRequest, "0:1", "eliteaaa1");

		return $(list, newRequest);
	}
	
}