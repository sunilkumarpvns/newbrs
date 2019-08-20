package com.elitecore.aaa.radius.service.assertion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;

@XmlRootElement(name = "not")
public class NotAssertion implements PacketAssertion {

	@XmlElementRefs({
		@XmlElementRef(name = "contains-attribute", type = ContainsAttributeAssertion.class),
		@XmlElementRef(name = "not", type = NotAssertion.class),
		@XmlElementRef(name = "packet-type", type = PacketTypeAssertion.class),
		@XmlElementRef(name = "is-dropped", type = IsDroppedAssertion.class)
	})
	private List<PacketAssertion> assertions = new ArrayList<PacketAssertion>();

	
	public List<PacketAssertion> getAssertions() {
		return assertions;
	}

	@Override
	public Matcher<? super RadServiceRequest> createRequestMatcher() {
		ArrayList<Matcher<? super RadServiceRequest>> matchers = new ArrayList<Matcher<? super RadServiceRequest>>(); 
		for (PacketAssertion assertion : assertions) {
			matchers.add(CoreMatchers.not(assertion.createRequestMatcher()));
		}
		return CoreMatchers.allOf(matchers);
	}

	@Override
	public Matcher<? super RadServiceResponse> createResponseMatcher() {
		ArrayList<Matcher<? super RadServiceResponse>> matchers = new ArrayList<Matcher<? super RadServiceResponse>>(); 
		for (PacketAssertion assertion : assertions) {
			matchers.add(CoreMatchers.not(assertion.createResponseMatcher()));
		}
		return CoreMatchers.allOf(matchers);
	}
}
