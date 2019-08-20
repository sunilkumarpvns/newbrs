package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "time-base-policy")
@XmlType(propOrder = {"monthOfYear", "dayOfMonth", "dayOfWeek", "timePeriod"})
@ValidObject
public class RadiusPolicyTimePeriod extends BaseData implements Differentiable, Validator {
	
	private String timePeriodId;
	@Pattern(regexp = RestValidationMessages.REGEX_MOY , message = RestValidationMessages.MESSAGE_MOY)
	private String monthOfYear;
	@Pattern(regexp = RestValidationMessages.REGEX_DOM , message = RestValidationMessages.MESSAGE_DOM)
	private String dayOfMonth;
	@Pattern(regexp = RestValidationMessages.REGEX_DOW, message = RestValidationMessages.MESSAGE_DOW)
	private String dayOfWeek;
	@Pattern(regexp = RestValidationMessages.REGEX_TIME_PERIOD, message = RestValidationMessages.MESSAGE_TIME_PERIOD)
	private String timePeriod;
	private String radiusPolicyId;
	private Integer orderNumber;
	
	@XmlTransient
	public String getTimePeriodId() {
		return timePeriodId;
	}
	public void setTimePeriodId(String timePeriodId) {
		this.timePeriodId = timePeriodId;
	}
	@XmlElement(name = "month-of-year")
	public String getMonthOfYear() {
		return monthOfYear;
	}
	public void setMonthOfYear(String monthOfYear) {
		this.monthOfYear = monthOfYear;
	}
	@XmlElement(name = "date-of-month")
	public String getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	@XmlElement(name = "day-of-week")
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	@XmlElement(name = "time-duration")
	public String getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	@XmlTransient
	public String getRadiusPolicyId() {
		return radiusPolicyId;
	}
	public void setRadiusPolicyId(String radiusPolicyId) {
		this.radiusPolicyId = radiusPolicyId;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Month of Year", monthOfYear);
		object.put("Day of Month", dayOfMonth);
		object.put("Day of Week", dayOfWeek);
		object.put("Time Peroid", timePeriod);
		return object;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		if (Strings.isNullOrEmpty(monthOfYear) && Strings.isNullOrEmpty(dayOfMonth) && Strings.isNullOrEmpty(dayOfWeek) && Strings.isNullOrEmpty(timePeriod)) {
			RestUtitlity.setValidationMessage(context, "Atleast one among month-of-year, date-of-month, day-of-week or time-duration must be specified.");
			isValid = false;
		}

		return isValid;
	}
}
