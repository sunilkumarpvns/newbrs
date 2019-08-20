package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType.DefaultResponseBehaviorTypeAdapter;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Depends;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "tgpp-server-policy")
@Depends(field = "defaultResponseBehaviorParameter", dependsOn = "defaultResponseBehaviorType" , message = "")
@ValidObject
@XmlType(propOrder = {"name", "description", "status", "ruleSet", "userIdentity", "sessionManagementEnabled",
     "cui", "defaultResponseBehaviorType", "defaultResponseBehaviorParameter", "commandCodeResponseAttributesList", 
     "commandCodeFlows"})
public class TGPPServerPolicyData implements DiameterServicePolicyConfiguration, Differentiable, Validator {
	
	private String id;
	
	@NotEmpty(message = "Policy Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	private String description;
	private String userIdentity;

	@NotEmpty(message = "Ruleset must be specified")
	private String ruleSet;
	
	@NotEmpty(message = "Session Management Enabled Field must be specified")
	@Pattern(regexp = "^$|true|false", message = "Invalid value of Session Management Enabled field. Value could be 'true' or 'false'.")
	private String sessionManagementEnabled;
	
	@Valid
	@NotEmpty(message = "Atleast one command code flow must be specified")
	private List<CommandCodeFlowData> commandCodeFlows = new ArrayList<CommandCodeFlowData>();
	private DefaultResponseBehaviorType defaultResponseBehaviorType = DefaultResponseBehaviorType.REJECT;
	private String defaultResponseBehaviorParameter;
	
	private List<CommandCodeResponseAttribute> commandCodeResponseAttributesList = new ArrayList<CommandCodeResponseAttribute>();
	
	private String status;
	private String cui;

	public TGPPServerPolicyData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlElement(name = "cui")
	public String getCui() {
		return cui;
	}
	
	public void setCui(String cui) {
		this.cui = cui;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleset) {
		this.ruleSet = ruleset;
	}

	@XmlElement(name = "user-identity")
	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	@XmlElementWrapper(name = "command-code-flows")
	@XmlElement(name = "command-code-flow")
	public List<CommandCodeFlowData> getCommandCodeFlows() {
		return commandCodeFlows;
	}
	
	@Override
	@XmlTransient
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	@XmlElement(name = "session-management")
	public String getSessionManagementEnabled() {
		return sessionManagementEnabled;
	}
	
	public void setSessionManagementEnabled(String sessionManagementEnabled) {
		this.sessionManagementEnabled = sessionManagementEnabled;
	}

	@Override
	@XmlElementWrapper(name = "command-code-wise-response-attribute-list")
	@XmlElement(name = "command-code-wise-response-attribute")
	public List<CommandCodeResponseAttribute> getCommandCodeResponseAttributesList() {
		return commandCodeResponseAttributesList;
	}
	

	public void setCommandCodeResponseAttributesList(
			ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributesList) {
		this.commandCodeResponseAttributesList = commandCodeResponseAttributesList;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		if(Collectionz.isNullOrEmpty(commandCodeResponseAttributesList) == false){
			JSONArray array = new JSONArray();
			for(CommandCodeResponseAttribute commandCodeResponseAttribute : commandCodeResponseAttributesList){
				array.add(commandCodeResponseAttribute.toJson());
			}
			if(array.size() > 0){
				object.put("Command Code Wise Response Attribute Entry", array);
			}
		}
		
		if(Collectionz.isNullOrEmpty(commandCodeFlows) == false){
			for(CommandCodeFlowData commandCodeFlowData : commandCodeFlows){
				object.put(commandCodeFlowData.getName(),commandCodeFlowData.toJson());
			}
		}
		return object;
	}

	@Override
	@XmlElement(name = "default-response-behavior-type")
	@XmlJavaTypeAdapter(value = DefaultResponseBehaviorTypeAdapter.class)
	public DefaultResponseBehaviorType getDefaultResponseBehaviorType() {
		return defaultResponseBehaviorType;
	}

	public void setDefaultResponseBehaviorType(DefaultResponseBehaviorType responseBehaviorType) {
		this.defaultResponseBehaviorType = responseBehaviorType;
	}
	
	@Override
	@XmlElement(name = "default-response-behavior-parameter")
	public String getDefaultResponseBehaviorParameter() {
		return defaultResponseBehaviorParameter;
	}
	
	public void setDefaultResponseBehaviorParameter(String responseBehaviorParameter) {
		this.defaultResponseBehaviorParameter = responseBehaviorParameter;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		
		if (DefaultResponseBehaviorType.REJECT != getDefaultResponseBehaviorType() &&
				DefaultResponseBehaviorType.DROP != getDefaultResponseBehaviorType() &&
						DefaultResponseBehaviorType.HOTLINE != getDefaultResponseBehaviorType()) {
			RestUtitlity.setValidationMessage(context, "Supported values for default response behaviour is REJECT, DROP and HOTLINE only");
			isValid = false;
		}
		
		if (Strings.isNullOrEmpty(this.status)) {
			RestUtitlity.setValidationMessage(context, "Status must be specified. It can be 'ACTIVE' or 'INACTIVE' only");
			isValid = false;
		} else {
			if (RestValidationMessages.ACTIVE.equalsIgnoreCase(this.status)) {
				setStatus(BaseConstant.SHOW_STATUS_ID);
			} else if (RestValidationMessages.INACTIVE.equalsIgnoreCase(this.status)) {
				setStatus(BaseConstant.HIDE_STATUS_ID);
			} else {
				RestUtitlity.setValidationMessage(context, "Specify Valid Value of Status. It can be 'ACTIVE' or 'INACTIVE' only");
				isValid = false;
			}
		}
		
		return isValid;
	}
}
