package com.elitecore.elitesm.datamanager.diameter.prioritytable.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.Length;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.diameterapi.core.common.transport.priority.Priority;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.DiameterSessionTypes;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StringValidatorAdaptor;
import com.elitecore.elitesm.ws.rest.adapter.prioritytable.DiameterSessionAdapter;
import com.elitecore.elitesm.ws.rest.adapter.prioritytable.PriorityAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "priority-entry")
@XmlType(propOrder = { "applicationId", "commandCode", "ipAddress", "diameterSession", "priority" })
@ValidObject
public class PriorityTableData implements Differentiable,Validator{
	
	private String priorityTableId;
	
	@Length(max = 200, message = "Length of Application Id must not greater than 200.")
	@NotNull(message = "Application Id must be specified.")
	private String applicationId;
	
	@Length(max = 200, message = "Length of Command Code must not greater than 200.")
	private String commandCode;
	
	@Length(max = 1000, message = "Length of IP Address must not greater than 1000.")
	@Pattern(regexp = RestValidationMessages.IPV4_IPV6_REGEX , message = "Enter Valid IP Address.")
	private String ipAddress;

	@NotNull(message = "Diameter Session must be specified. Value could be 'All' or 'New' or 'Existing'.")
	private Integer diameterSession;
	
	@NotNull(message = "Priority must be specified. Value could be 'High' or 'Medium' or 'Low'.")
	private Integer priority;
	
	private Integer orderNumber;
	
	@XmlTransient
	public String getPriorityTableId() {
		return priorityTableId;
	}
	public void setPriorityTableId(String priorityTableId) {
		this.priorityTableId = priorityTableId;
	}
	
	@XmlElement(name = "application-id")
	@XmlJavaTypeAdapter(StringValidatorAdaptor.class)
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	@XmlElement(name = "command-code")
	@XmlJavaTypeAdapter(StringValidatorAdaptor.class)
	public String getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	
	@XmlElement(name = "ip-address")
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@XmlElement(name = "diameter-session")
	@XmlJavaTypeAdapter(DiameterSessionAdapter.class)
	public Integer getDiameterSession() {
		return diameterSession;
	}
	public void setDiameterSession(Integer diameterSession) {
		this.diameterSession = diameterSession;
	}
	
	@XmlElement(name = "priority")
	@XmlJavaTypeAdapter(PriorityAdapter.class)
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	@Override
	public JSONObject toJson() {
		
		JSONObject innerObject = new JSONObject();
		
		innerObject.put("Command Code", commandCode);
		innerObject.put("IP Address", ipAddress);
		innerObject.put("Diameter Session", getDiameterSessionValue(diameterSession));
		innerObject.put("Priority", getPriorityFromValue(priority));
		innerObject.put("applicationId", applicationId);
		
		return innerObject;
	}
	
	private String  getPriorityFromValue(int priorityVal) {
		return Priority.fromPriority(priorityVal).priority;
	}
	
	private String getDiameterSessionValue(int diameterSessionVal) {
		return DiameterSessionTypes.fromVal(diameterSessionVal).type;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		
		if(Strings.isNullOrBlank(applicationId) == false){
			if(applicationId.equalsIgnoreCase("INVALID")){
				RestUtitlity.setValidationMessage(context, "Invalid Application Id, Only numeric value are allowed");
				isValid = false;
			}
		}
		
		if(Strings.isNullOrBlank(commandCode) == false){
			if(commandCode.equalsIgnoreCase("INVALID")){
				RestUtitlity.setValidationMessage(context, "Invalid Command Code, Only numeric value are allowed");
				isValid = false;
			}
		}
		return isValid;
	}
}
