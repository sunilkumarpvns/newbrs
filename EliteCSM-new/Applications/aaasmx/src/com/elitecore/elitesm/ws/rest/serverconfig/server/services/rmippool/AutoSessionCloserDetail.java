package com.elitecore.elitesm.ws.rest.serverconfig.server.services.rmippool;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder = {"enabled", "executionInterval", "reservationTimeoutInterval", "sessionTimeoutInterval", "maxBatchSize"})
public class AutoSessionCloserDetail {
	
	@NotEmpty(message = "Enabled must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Enabled. It can be true or false." )
	private String enabled;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Execution Interval")
	private String executionInterval;

	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Reservation Timeout Interval")
	private String reservationTimeoutInterval;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Session Timeout Interval")
	private String sessionTimeoutInterval;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Max Batch Size")
	private String maxBatchSize;
	
	
	public AutoSessionCloserDetail(){
		//required By Jaxb.
	}
	
	@XmlElement(name = "enabled")
	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled.toLowerCase();
	}

	@XmlElement(name = "execution-interval")
	public String getExecutionInterval() {
		return executionInterval;
	}

	public void setExecutionInterval(String executionInterval) {
		this.executionInterval = executionInterval;
	}

	@XmlElement(name = "reservation-timeout-interval")
	public String getReservationTimeoutInterval() {
		return reservationTimeoutInterval;
	}

	public void setReservationTimeoutInterval(String reservationTimeoutInterval) {
		this.reservationTimeoutInterval = reservationTimeoutInterval;
	}

	@XmlElement(name = "session-timeout-interval")
	public String getSessionTimeoutInterval() {
		return sessionTimeoutInterval;
	}

	public void setSessionTimeoutInterval(String sessionTimeoutInterval) {
		this.sessionTimeoutInterval = sessionTimeoutInterval;
	}

	@XmlElement(name = "max-batch-size")
	public String getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(String maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}
	
}
