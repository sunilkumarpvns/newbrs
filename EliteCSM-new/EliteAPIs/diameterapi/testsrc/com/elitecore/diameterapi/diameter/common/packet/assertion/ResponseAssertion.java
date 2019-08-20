package com.elitecore.diameterapi.diameter.common.packet.assertion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;

@XmlRootElement(name = "response")
public class ResponseAssertion {
	
	@XmlElementRefs({
		@XmlElementRef(name = "contains-attribute", type = ContainsAttributeAssertion.class),
		@XmlElementRef(name = "not", type = NotAssertion.class)
	})

	private List<PacketAssertion> assertions = new ArrayList<PacketAssertion>();
	
	public List<PacketAssertion> getAssertions() {
		return assertions;
	}
	
	public Matcher<? super DiameterAnswer> create() {
		ArrayList<Matcher<? super DiameterAnswer>> matchers = new ArrayList<Matcher<? super DiameterAnswer>>(); 
		for (PacketAssertion assertion : assertions) {
			matchers.add(assertion.createResponseMatcher());
		}
		return CoreMatchers.allOf(matchers);
	}
}
