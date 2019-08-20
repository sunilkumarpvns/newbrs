package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.util.constants.RadiusServicePolicyConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder = {})
public class RadiusPolicyDetail {
	private String rejectOnRejectItemNotFound;
	private String rejectOnCheckItemNotFound = "true";
	private String acceptOnPolicyOnFound;
	
	public RadiusPolicyDetail(){
		this.rejectOnRejectItemNotFound = RadiusServicePolicyConstants.REJECTONREJECTITEMNOTFOUND;
		this.rejectOnCheckItemNotFound = RadiusServicePolicyConstants.REJECTONCHECKITEMNOTFOUND;
		this.acceptOnPolicyOnFound = RadiusServicePolicyConstants.ACCEPTONPOLICYONFOUND;
	}
	
	@XmlElement(name = "reject-on-reject-item-not-found", type = String.class,defaultValue = "true")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid value of Reject On Reject Item Not Found in Authrorization Handler. It could be 'true' or 'false'")
	public String getRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}
	public void setRejectOnRejectItemNotFound(String rejectOnRejectNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectNotFound.toLowerCase();
	}

	@XmlElement(name = "reject-on-check-item-not-found", type = String.class)
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid value of Reject On Check Item Not Found in Authrorization Handler. It could be 'true' or 'false'")
	public String getRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}
	
	public void setRejectOnCheckItemNotFound(String rejectOnCheckNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckNotFound.toLowerCase();
	}
	
	@XmlElement(name = "accept-on-policy-not-found", type = String.class)
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid value of Accept On Policy Not Found in Authrorization Handler. It could be 'true' or 'false'")
	public String getAcceptOnPolicyOnFound() {
		return acceptOnPolicyOnFound;
	}
	
	public void setAcceptOnPolicyOnFound(String acceptOnPolicyOnFound) {
		this.acceptOnPolicyOnFound = acceptOnPolicyOnFound.toLowerCase();
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(padStart("Radius Policy Details", 10, ' '));
		out.println(format("%-30s: %s", "Reject on Check Item not found",
				getRejectOnCheckItemNotFound()));
		out.println(format("%-30s: %s", "Reject on Reject Item not found",
				getRejectOnRejectItemNotFound()));
		out.println(format("%-30s: %s", "Accept on Policy not found",
				getAcceptOnPolicyOnFound()));
		out.close();
		return writer.toString();
	}
}
