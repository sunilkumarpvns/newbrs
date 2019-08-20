package com.elitecore.elitesm.ws.rest.serverconfig.server.services.radauth;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
@XmlType(propOrder = {"rrdResponseTimeEnabled", "rrdSummaryEnabled", "rrdErrorsEnabled", "rrdRejectReasonsEnabled"})
public class RRDDetail {

	@NotEmpty(message = "Summary must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Summary. It can be true or false." )
	private String rrdSummaryEnabled;

	@NotEmpty(message = "Error must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Error. It can be true or false." )
	private String rrdErrorsEnabled;

	@NotEmpty(message = "Reject With Response must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Reject With Response. It can be true or false." )
	private String rrdRejectReasonsEnabled;

	@NotEmpty(message = "Response Time must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Response Time. It can be true or false." )
	private String rrdResponseTimeEnabled;

	public RRDDetail(){
		//required by Jaxb.
	}

	@XmlElement(name = "summary")
	public String getRrdSummaryEnabled() {
		return rrdSummaryEnabled;
	}

	public void setRrdSummaryEnabled(String rrdSummaryEnabled) {
		this.rrdSummaryEnabled = rrdSummaryEnabled.toLowerCase();
	}

	@XmlElement(name = "errors")
	public String getRrdErrorsEnabled() {
		return rrdErrorsEnabled;
	}

	public void setRrdErrorsEnabled(String rrdErrorsEnabled) {
		this.rrdErrorsEnabled = rrdErrorsEnabled.toLowerCase();
	}

	@XmlElement(name = "reject-with-reasons")
	public String getRrdRejectReasonsEnabled() {
		return rrdRejectReasonsEnabled;
	}

	public void setRrdRejectReasonsEnabled(String rrdRejectReasonsEnabled) {
		this.rrdRejectReasonsEnabled = rrdRejectReasonsEnabled.toLowerCase();
	}

	@XmlElement(name = "response-time")
	public String getRrdResponseTimeEnabled() {
		return rrdResponseTimeEnabled;
	}

	public void setRrdResponseTimeEnabled(String rrdResponseTimeEnabled) {
		this.rrdResponseTimeEnabled = rrdResponseTimeEnabled.toLowerCase();
	}
}
