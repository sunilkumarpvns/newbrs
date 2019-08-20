package com.elitecore.aaa.radius.service.assertion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import com.elitecore.aaa.radius.service.RadServiceResponse;

@XmlRootElement(name = "response")
public class ResponseAssertion {

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
	
	public Matcher<? super RadServiceResponse> create() {
		ArrayList<Matcher<? super RadServiceResponse>> matchers = new ArrayList<Matcher<? super RadServiceResponse>>(); 
		for (PacketAssertion assertion : assertions) {
			matchers.add(assertion.createResponseMatcher());
		}
		return CoreMatchers.allOf(matchers);
	}

}
