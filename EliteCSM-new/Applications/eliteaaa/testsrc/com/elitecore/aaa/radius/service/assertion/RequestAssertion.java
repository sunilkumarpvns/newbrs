package com.elitecore.aaa.radius.service.assertion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import com.elitecore.aaa.radius.service.RadServiceRequest;

@XmlRootElement(name = "request")
public class RequestAssertion {

	@XmlElementRefs({
		@XmlElementRef(name = "contains-attribute", type = ContainsAttributeAssertion.class),
		@XmlElementRef(name = "not", type = NotAssertion.class),
		@XmlElementRef(name = "packet-type", type = PacketTypeAssertion.class)
	})
	
	private List<PacketAssertion> assertions = new ArrayList<PacketAssertion>();

	public List<PacketAssertion> getAssertions() {
		return assertions;
	}
	
	public Matcher<RadServiceRequest> create() {
		ArrayList<Matcher<? super RadServiceRequest>> matchers = new ArrayList<Matcher<? super RadServiceRequest>>(); 
		for (PacketAssertion assertion : assertions) {
			matchers.add(assertion.createRequestMatcher());
		}
		return CoreMatchers.allOf(matchers);
	}
}
