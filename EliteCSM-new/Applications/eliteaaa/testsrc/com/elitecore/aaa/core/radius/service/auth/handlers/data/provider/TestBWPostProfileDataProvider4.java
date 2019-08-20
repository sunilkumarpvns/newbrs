package com.elitecore.aaa.core.radius.service.auth.handlers.data.provider;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;

public class TestBWPostProfileDataProvider4 extends TestBWDataProviderSupport {

	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WithSingleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_1() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(wl("21067:125", "eliteaaa"));
		
		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa");
		
		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WithSingleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_2() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(wl("21067:125", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa1");

		return $(list, newRequest);
	}
	
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WithMultipleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_1() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(wl("21067:126", "eliteaaa"));
		list.add(wl("21067:125", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa1");

		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WithMultipleAttributeInRequest_WhenAttributeIsFoundWithValueExpected_2() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(wl("21067:126", "eliteaaa1"));
		list.add(wl("21067:125", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa1");

		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAttributeInRequestMatchesProvidedExpression() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(wl("21067:125", "eliteaaa*"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa1");

		return $(list, newRequest);
	}
	
	public static Object[] provide_dataFor_UserShouldNotBeBlocked_WhenAnyAttributeOfMultipleOccurancesFromRequestMatchesAnyOfConfiguredValues() {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(wl("21067:125", "eliteaaa1"));

		RadAuthRequest newRequest = newRequest();
		add(newRequest, "21067:125", "eliteaaa4");
		add(newRequest, "21067:125", "eliteaaa3");
		add(newRequest, "21067:125", "eliteaaa2");
		add(newRequest, "21067:125", "eliteaaa1");

		return $(list, newRequest);
	}
}