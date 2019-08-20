package com.elitecore.aaa.radius.service.assertion;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.Matcher;

import com.elitecore.aaa.EliteAAAMatchers;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;

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
	public Matcher<? super RadServiceRequest> createRequestMatcher() {
		return EliteAAAMatchers.RadServiceRequestMatchers.containsAttribute(id, value);
	}

	@Override
	public Matcher<? super RadServiceResponse> createResponseMatcher() {
		return EliteAAAMatchers.RadServiceResponseMatchers.containsAttribute(id, value);
	}
}
