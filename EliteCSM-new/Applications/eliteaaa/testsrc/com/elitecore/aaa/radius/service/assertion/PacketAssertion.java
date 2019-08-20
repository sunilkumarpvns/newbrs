package com.elitecore.aaa.radius.service.assertion;

import org.hamcrest.Matcher;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;

public interface PacketAssertion {

	Matcher<? super RadServiceRequest> createRequestMatcher();
	Matcher<? super RadServiceResponse> createResponseMatcher();
}
