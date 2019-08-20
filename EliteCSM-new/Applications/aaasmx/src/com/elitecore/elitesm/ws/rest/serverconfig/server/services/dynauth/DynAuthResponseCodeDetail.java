package com.elitecore.elitesm.ws.rest.serverconfig.server.services.dynauth;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name="response-code-to-retry")
@XmlType(propOrder={"retryEnabled", "retryLimit", "responceCodeList"})
@ValidObject
public class DynAuthResponseCodeDetail  implements Validator {

	@Pattern(regexp = "true|false|True|False|TRUE|FALSE", message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "'enabled' of response code to retry." )
	private String retryEnabled;
	
	private List<String> responceCodeList;

	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Delay Between Subsequent Requests.")
	private String retryLimit;

	public DynAuthResponseCodeDetail() {
		this.retryLimit = "3";
		this.responceCodeList = new ArrayList<String>();
	}

	@XmlAttribute(name="enabled") 
	public String getRetryEnabled() {
		return retryEnabled;
	}

	public void setRetryEnabled(String retryEnabled) {
		this.retryEnabled = retryEnabled.toLowerCase();
	}

	@XmlElementWrapper(name="response-code-list")
	@XmlElement(name="response-code",type=String.class)
	public List<String> getResponceCodeList() {
		return responceCodeList;
	}

	public void setResponceCodeList(List<String> responceCodeList) {
		this.responceCodeList = responceCodeList;
	}

	@XmlElement(name="retry-limit", type=String.class)
	public String getRetryLimit() {
		return retryLimit;
	}
	public void setRetryLimit(String retryLimit) {
		this.retryLimit = retryLimit;
	}

	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Response Code Data");
		out.println("    Enabled        = " + retryEnabled);
		out.println("    Retry Limit    = " + retryLimit);
		if (Collectionz.isNullOrEmpty(responceCodeList) == false) {
			out.println("    Response Codes = " + responceCodeList);
		} else {
			out.println("No Responce Code Configured");
		}
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		java.util.regex.Pattern pattern =java.util.regex.Pattern.compile(RestValidationMessages.PARAMETER_DIGIT_REGEX);

		for (String responseCode : responceCodeList) {
			if (Strings.isNullOrEmpty(responseCode) == false) {
				if (pattern.matcher(responseCode).matches() == false) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, RestValidationMessages.PARAMETER_ERR_MESSAGE + "Response code " + responseCode + ". It should be numeric.");
					return isValid;	
				}
			} else {
				responceCodeList.remove(responseCode);
				responceCodeList.add(null);
			}
		}
		return isValid;
	}
}
