package com.elitecore.diameterapi.diameter.common.packet.assertion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

@XmlRootElement(name = "not")
public class NotAssertion implements PacketAssertion {

	@XmlElementRefs({
		@XmlElementRef(name = "contains-attribute", type = ContainsAttributeAssertion.class),
		@XmlElementRef(name = "not", type = NotAssertion.class)
	})
	
	private List<PacketAssertion> assertions = new ArrayList<PacketAssertion>();

	
	public List<PacketAssertion> getAssertions() {
		return assertions;
	}

	@Override
	public Matcher<? super DiameterRequest> createRequestMatcher() {
		ArrayList<Matcher<? super DiameterRequest>> matchers = new ArrayList<Matcher<? super DiameterRequest>>(); 
		for (PacketAssertion assertion : assertions) {
			matchers.add(CoreMatchers.not(assertion.createRequestMatcher()));
		}
		return CoreMatchers.allOf(matchers);
	}

	@Override
	public Matcher<? super DiameterAnswer> createResponseMatcher() {
		ArrayList<Matcher<? super DiameterAnswer>> matchers = new ArrayList<Matcher<? super DiameterAnswer>>(); 
		for (PacketAssertion assertion : assertions) {
			matchers.add(CoreMatchers.not(assertion.createResponseMatcher()));
		}
		return CoreMatchers.allOf(matchers);
	}
}
