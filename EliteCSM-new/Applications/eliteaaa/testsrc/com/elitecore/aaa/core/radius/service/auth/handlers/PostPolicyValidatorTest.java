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
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostPolicyDataProvider1;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostPolicyDataProvider2;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostPolicyDataProvider3;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostPolicyDataProvider4;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostPolicyDataProvider5;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostPolicyDataProvider6;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TestBWPostPolicyDataProvider7;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.handlers.PostPolicyValidator;

@RunWith(JUnitParamsRunner.class)
public class PostPolicyValidatorTest {

	private PostPolicyValidator postPolicyValidator;

	@Before
	public void setUp() {
		postPolicyValidator = new PostPolicyValidator();
	}
	
	@Test
	public void testIsBlockedUser_ShouldNotBlackListAnyUser_WhenAttributeDetailsAreNotProvided() throws UnknownHostException {
		postPolicyValidator.init(Collections.<AttributeDetails>emptyList());
		
		assertFalse(postPolicyValidator.isBlockedUser(TestBWDataProviderSupport.newRequest()));
	}
	
	@Test
	public void testIsBlockedUser_ShouldNotBlockAnyUser_WhenVendorIdOfAttributeInBlackListIs21067() throws UnknownHostException {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(TestBWDataProviderSupport.bl("21067:125", "eliteaaa"));
		
		RadAuthRequest newRequest = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest, "21067:125", "eliteaaa");
		
		
		postPolicyValidator.init(list);
		
		assertFalse(postPolicyValidator.isBlockedUser(newRequest));
	}
	
	@Test
	public void testIsBlockedUser_ShouldNotBlock_WhenVendorIdOfAttributeInBlackListIsStandardAndAttributeIdIsCUI() throws UnknownHostException {
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(TestBWDataProviderSupport.bl("0:89", "eliteaaa"));
		
		RadAuthRequest newRequest = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest, "0:89", "eliteaaa");
		
		
		postPolicyValidator.init(list);
		
		assertFalse(postPolicyValidator.isBlockedUser(newRequest));
	}
	
	@Test
	@Parameters(source = {TestBWPostPolicyDataProvider1.class})
	public void testIsBlockedUser__WithBlackListOnly_ShouldBlockUser_IfRequestContainsAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postPolicyValidator.init(attributeDetails);
		
		assertTrue(postPolicyValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostPolicyDataProvider2.class})
	public void testIsBlockedUser__WithBlackListOnly_ShouldNotBlockUser_IfRequestDoesNotContainAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postPolicyValidator.init(attributeDetails);
		
		assertFalse(postPolicyValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostPolicyDataProvider3.class})
	public void testIsBlockedUser__WithWhiteListOnly_ShouldBlockUser_IfRequestDoesNotContainsAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postPolicyValidator.init(attributeDetails);
		
		assertTrue(postPolicyValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostPolicyDataProvider4.class})
	public void testIsBlockedUser__WithWhiteListOnly_ShouldNotBlockUser_IfRequestContainsAnyAttributeWithProvidedValue(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postPolicyValidator.init(attributeDetails);
		
		assertFalse(postPolicyValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostPolicyDataProvider5.class})
	public void testIsBlockedUser__WithBlackAndWhiteList_ShouldBlockUser_IfAttributeInRequestIsInBlackListButNotInWhiteList(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postPolicyValidator.init(attributeDetails);
		
		assertTrue(postPolicyValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostPolicyDataProvider6.class})
	public void testIsBlockedUser__WithBlackAndWhiteList_ShouldNotBlockUser_IfAttributeInRequestIsInBlackListAndInWhiteList(
			List<AttributeDetails> attributeDetails, RadAuthRequest request) {
		postPolicyValidator.init(attributeDetails);
		
		assertFalse(postPolicyValidator.isBlockedUser(request));
	}
	
	@Test
	@Parameters(source = {TestBWPostPolicyDataProvider7.class})
	public void testIsBlockedUser__WithBlackAndWhiteList_ShouldNotBlockUser_IfConfiguredTimeHasPassedForAttribute_ThanSkipThatAttributeInValidation(
			List<AttributeDetails> attributeDetails, RadAuthRequest request, boolean expected) {
		postPolicyValidator.init(attributeDetails);
		
		assertEquals(expected, postPolicyValidator.isBlockedUser(request));
	}
	
	@Test
	public void testIsBlockedUser__WithBlackList_ShouldNotBlockAnyUser_IfConfiguredTimeHasPassedForAttribute_ThanSkipThatAttributeInValidation() {
		
		List<AttributeDetails> list = new ArrayList<AttributeDetails>();
		list.add(TestBWDataProviderSupport.bly("0:1", "eliteaaa*"));

		postPolicyValidator.init(list);
		
		RadAuthRequest newRequest1 = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest1, "0:1", "eliteaaa1");
		
		RadAuthRequest newRequest2 = TestBWDataProviderSupport.newRequest();
		TestBWDataProviderSupport.add(newRequest2, "0:1", "eliteaaa2");
		
		assertFalse(postPolicyValidator.isBlockedUser(newRequest1));
		assertFalse(postPolicyValidator.isBlockedUser(newRequest2));
	}
}
