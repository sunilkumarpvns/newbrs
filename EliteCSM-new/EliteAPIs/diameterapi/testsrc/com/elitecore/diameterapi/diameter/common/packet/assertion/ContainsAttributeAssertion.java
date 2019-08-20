package com.elitecore.diameterapi.diameter.common.packet.assertion;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.Matcher;

import com.elitecore.diameterapi.diameter.DiameterMatchers;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

@XmlRootElement(name = "contains-attribute")
public class ContainsAttributeAssertion implements PacketAssertion {

	private String id;
	private String value;
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlAttribute(name = "value")
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Matcher<? super DiameterRequest> createRequestMatcher() {
		return DiameterMatchers.DiameterRequestMatcher.containsAttribute(id, value);
	}

	@Override
	public Matcher<? super DiameterAnswer> createResponseMatcher() {
		return DiameterMatchers.DiameterResponseMatcher.containsAttribute(id, value);
	}
}
