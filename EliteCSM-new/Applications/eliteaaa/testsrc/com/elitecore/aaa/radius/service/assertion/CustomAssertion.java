package com.elitecore.aaa.radius.service.assertion;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.Matcher;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;

@XmlRootElement(name = "assert")
public class CustomAssertion {
	
	private RequestAssertion requestAssertion = new RequestAssertion();
	private ResponseAssertion responseAssertion = new ResponseAssertion();
	
	@XmlElement(name = "request")
	public RequestAssertion getRequestAssertion() {
		return requestAssertion;
	}

	public void setRequestAssertion(RequestAssertion requestAssertion) {
		this.requestAssertion = requestAssertion;
	}

	@XmlElement(name = "response")
	public ResponseAssertion getResponseAssertion() {
		return responseAssertion;
	}

	public void setResponseAssertion(ResponseAssertion responseAssertion) {
		this.responseAssertion = responseAssertion;
	}

	public Matcher<? super RadServiceRequest> createRequestMatcher() {
		return requestAssertion.create();
	}
	
	public Matcher<? super RadServiceResponse> createResponseMatcher() {
		return responseAssertion.create();
	}
}
