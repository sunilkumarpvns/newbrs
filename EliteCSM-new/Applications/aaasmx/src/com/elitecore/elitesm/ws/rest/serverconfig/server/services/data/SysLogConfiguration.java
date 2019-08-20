package com.elitecore.elitesm.ws.rest.serverconfig.server.services.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "syslog")
@XmlType(propOrder = {"hostIpAddress", "facility"})
@ValidObject
public class SysLogConfiguration implements Validator{
	
	private String hostIpAddress;
	
	@NotEmpty(message = "Facility must be specified. It can be -None-, AUTH, KERN, USER, MAIL, DAEMON, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6 or LOCAL7")
	private String facility;
	
	public SysLogConfiguration(String hostIpAddress, String facility) {
		this.hostIpAddress =hostIpAddress;
		if (Strings.isNullOrEmpty(facility) == false) {
			this.facility = facility;
		}
	}
	public SysLogConfiguration() {
		hostIpAddress = new String();
		facility = new String();
	}
	
	@XmlElement(name = "facility")
	public String getFacility() {
		return facility;
	}
	
	public void setFacility(String facility){
		this.facility = facility;
	}
	
	@XmlElement(name = "address")
	public String getHostIpAddress() {
		return hostIpAddress;
	}
	
	public void setHostIpAddress(String hostIpAddress){
		this.hostIpAddress = hostIpAddress;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Address  = " + getHostIpAddress());
		out.println("Facility = " + getFacility());
		out.close();
		return stringBuffer.toString();
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		AlertListenerBLManager alertListenerBLManager =new AlertListenerBLManager();
		if (RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(facility) == false && RestValidationMessages.NONE.equalsIgnoreCase(facility) == false) {
			try {
				List<String> sysLogNames = alertListenerBLManager.getSysLogNames();
				if (Strings.isNullOrEmpty(facility) == false && sysLogNames.contains(facility) == false) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid value of Facility (Sys Log Configuration). It can be -None-, AUTH, KERN, USER, MAIL, DAEMON, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6 or LOCAL7");
				}

			} catch(Exception e) {
				RestUtitlity.setValidationMessage(context, "Unable to get value of facility in Sys Log Configuration");
				isValid = false;
			}
		}

		return isValid;
	}

}
