package com.elitecore.aaa.core.radius.service.auth.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWDataProviderSupport;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostProfileDataProvider1;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostProfileDataProvider2;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostProfileDataProvider3;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostProfileDataProvider4;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostProfileDataProvider5;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostProfileDataProvider6;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostProfileDataProvider7;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.handlers.PostProfileValidator;

@RunWith(JUnitParamsRunner.class)
public class PostProfileValidatorTest {

	private PostProfileValidator postProfileValidator;

	@Before
	public void setUp() {
		postProfileValidator = new PostProfileValidator();
	}
	
	@Test
	public void testIsBlockedUser_ShouldNotBlackListAnyUser_WhenAttributeDetailsAreNotProvided() throws UnknownHostException {
		postProfileValidator.init(Collections.<AttributeDetails>emptyList());
		
		assertFalse(postProfileValidator.isBlockedUser(TestBWDataProviderSupport.newRequest()));
	}
	
	@Test
	public void testIsBlockedUser_ShouldNotBlockAnyUser_WhenVendorIdOfAttributeInBlackListIsOtherThan21067() throws UnknownHostException {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(TestBWDataProviderSupport.bl("0:1", "eliteaaa"));
		
		RadAuthRequest newRequest = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest, "0:1", "eliteaaa");
		
		
		postProfileValidator.init(list);
		
		assertFalse(postProfileValidator.isBlockedUser(newRequest));
	}
	
	@Test
	public void testIsBlockedUser_ShouldBlock_WhenVendorIdOfAttributeInBlackListIsStandardAndAttributeIdIsCUI() throws UnknownHostException {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(TestBWDataProviderSupport.bl("0:89", "eliteaaa"));
		
		RadAuthRequest newRequest = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest, "0:89", "eliteaaa");
		
		
		postProfileValidator.init(list);
		
		assertTrue(postProfileValidator.isBlockedUser(newRequest));
	}
	
	@Test
	@Parameters(source = {TestBWPostProfileDataProvider1.class})
	public void testIsBlockedUser__WithBlackListOnly_ShouldBlockUser_IfRequestContainsAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postProfileValidator.init(attributeDetails);
		
		assertTrue(postProfileValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostProfileDataProvider2.class})
	public void testIsBlockedUser__WithBlackListOnly_ShouldNotBlockUser_IfRequestDoesNotContainAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postProfileValidator.init(attributeDetails);
		
		assertFalse(postProfileValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostProfileDataProvider3.class})
	public void testIsBlockedUser__WithWhiteListOnly_ShouldBlockUser_IfRequestDoesNotContainsAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postProfileValidator.init(attributeDetails);
		
		assertTrue(postProfileValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostProfileDataProvider4.class})
	public void testIsBlockedUser__WithWhiteListOnly_ShouldNotBlockUser_IfRequestContainsAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postProfileValidator.init(attributeDetails);
		
		assertFalse(postProfileValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostProfileDataProvider5.class})
	public void testIsBlockedUser__WithBlackAndWhiteList_ShouldBlockUser_IfAttributeInRequestIsInBlackListButNotInWhiteList(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postProfileValidator.init(attributeDetails);
		
		assertTrue(postProfileValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostProfileDataProvider6.class})
	public void testIsBlockedUser__WithBlackAndWhiteList_ShouldNotBlockUser_IfAttributeInRequestIsInBlackListAndInWhiteList(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postProfileValidator.init(attributeDetails);
		
		assertFalse(postProfileValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostProfileDataProvider7.class})
	public void testIsBlockedUser__WithBlackAndWhiteList_ShouldNotBlockUser_IfConfiguredTimeHasPassedForAttribute_ThanSkipThatAttributeInValidation(
			List<AttributeDetails> attributeDetails, RadAuthRequest request, boolean expected) {
		postProfileValidator.init(attributeDetails);
		
		assertEquals(expected, postProfileValidator.isBlockedUser(request));
	}
	
	@Test
	public void testIsBlockedUser__WithBlackList_ShouldNotBlockAnyUser_IfConfiguredTimeHasPassedForAttribute_ThanSkipThatAttributeInValidation() {
		
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(TestBWDataProviderSupport.bly("21067:125", "eliteaaa*"));

		postProfileValidator.init(list);
		
		RadAuthRequest newRequest1 = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest1, "21067:125", "eliteaaa1");
		
		RadAuthRequest newRequest2 = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest2, "21067:125", "eliteaaa2");
		
		assertFalse(postProfileValidator.isBlockedUser(newRequest1));
		assertFalse(postProfileValidator.isBlockedUser(newRequest2));
	}
}
