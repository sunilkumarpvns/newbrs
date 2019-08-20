package com.elitecore.aaa.radius.service.assertion;

import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.Matcher;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.CoreLibMatchers;

@XmlRootElement(name = "is-dropped")
public class IsDroppedAssertion implements PacketAssertion {

	@Override
	public Matcher<? super RadServiceRequest> createRequestMatcher() {
		throw new UnsupportedOperationException("Request cannot be dropped");
	}

	@Override
	public Matcher<? super RadServiceResponse> createResponseMatcher() {
		return CoreLibMatchers.ServiceResponseMatchers.isDropped();
	}
}
