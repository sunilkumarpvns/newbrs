package com.elitecore.diameterapi.diameter.common.packet.assertion;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.Matcher;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

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

	public Matcher<? super DiameterRequest> createRequestMatcher() {
		return requestAssertion.create();
	}
	
	public Matcher<? super DiameterAnswer> createResponseMatcher() {
		return responseAssertion.create();
	}
}
