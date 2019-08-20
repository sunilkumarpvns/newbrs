package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@ValidObject
@XmlRootElement(name = "sys-log-listener")
@XmlType(propOrder = {"address", "facility", "repeatedMessageReduction"})
public class SYSLogAlertListenerData extends BaseAlertListener implements Validator {
	private String sysLogInstanceId;
	
	@Expose
	@SerializedName("Address")
	@NotNull(message = "Address must be specified.")
	@Pattern(regexp = RestValidationMessages.IPV4_IPV6_REGEX, message = "Enter Valid Address." )
	private String address;
	
	@Expose
	@SerializedName("Facility")
	@NotEmpty(message = "Facility must be specified. It can be AUTH, KERN, USER, MAIL, DAEMON, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6 or LOCAL7")
	private String facility;
	
	@Expose
	@SerializedName("Repeated Message Reduction")
	@NotNull(message = "Repeated Message Reduction must be specified. It can be true or false")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid Repeated Message Reduction. It can be true or false." )
	private String repeatedMessageReduction;
	
	@XmlTransient
	public String getSysLogInstanceId() {
		return sysLogInstanceId;
	}
	
	public void setSysLogInstanceId(String sysLogInstanceId) {
		this.sysLogInstanceId = sysLogInstanceId;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getFacility() {
		return facility;
	}
	
	public void setFacility(String facility) {
		this.facility = facility;
	}	
	
	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();
		object.put("Address",address);
		object.put("Facility", facility);
		if(repeatedMessageReduction.equalsIgnoreCase("true")){
			object.put("Repeated Message Reduction", "True");
		}else{
			object.put("Repeated Message Reduction", "False");
		}
		
		return object;
	}

	@XmlElement(name = "repeated-message-reduction")
	public String getRepeatedMessageReduction() {
		return repeatedMessageReduction;
	}

	public void setRepeatedMessageReduction(String repeatedMessageReduction) {
		this.repeatedMessageReduction = repeatedMessageReduction.toLowerCase();
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		AlertListenerBLManager alertListenerBLManager =new AlertListenerBLManager();
		try {
			List<String> sysLogNames = alertListenerBLManager.getSysLogNames();
			if (Strings.isNullOrEmpty(facility) == false) {
			if (sysLogNames.contains(facility) == false) {
				RestUtitlity.setValidationMessage(context, "Invalid value of Facility (Sys Log Configuration). It can be  AUTH, KERN, USER, MAIL, DAEMON, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6 or LOCAL7");
				return false;
			}
			}

		} catch(Exception e) {
			RestUtitlity.setValidationMessage(context,e.getMessage() + "");
			return false;
		}
		return true;
	}
}
