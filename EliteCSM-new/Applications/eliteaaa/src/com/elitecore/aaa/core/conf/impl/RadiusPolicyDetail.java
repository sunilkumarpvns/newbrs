package com.elitecore.aaa.core.conf.impl;

import static com.elitecore.commons.base.Strings.padStart;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class RadiusPolicyDetail {
	private boolean rejectOnRejectItemNotFound;
	private boolean rejectOnCheckItemNotFound = true;
	private boolean acceptOnPolicyOnFound;
	
	public RadiusPolicyDetail(){
		// Required by Jaxb.
	}
	
	@XmlElement(name = "reject-on-reject-item-not-found", type = boolean.class,defaultValue = "true")
	public boolean isRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}
	public void setRejectOnRejectItemNotFound(boolean rejectOnRejectNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectNotFound;
	}

	@XmlElement(name = "reject-on-check-item-not-found", type = boolean.class)
	public boolean isRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}
	
	public void setRejectOnCheckItemNotFound(boolean rejectOnCheckNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckNotFound;
	}
	
	@XmlElement(name = "accept-on-policy-not-found", type = boolean.class)
	public boolean isAcceptOnPolicyOnFound() {
		return acceptOnPolicyOnFound;
	}
	
	public void setAcceptOnPolicyOnFound(boolean acceptOnPolicyOnFound) {
		this.acceptOnPolicyOnFound = acceptOnPolicyOnFound;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(padStart("Radius Policy Details", 10, ' '));
		out.println(format("%-30s: %s", "Reject on Check Item not found",
				isRejectOnCheckItemNotFound()));
		out.println(format("%-30s: %s", "Reject on Reject Item not found",
				isRejectOnRejectItemNotFound()));
		out.println(format("%-30s: %s", "Accept on Policy not found",
				isAcceptOnPolicyOnFound()));
		out.close();
		return writer.toString();
	}
}
