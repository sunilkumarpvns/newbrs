
package com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Depends;

@XmlRootElement(name = "credit-control-policy")
@Depends(field = "defaultResponseBehaviorArgument", dependsOn = "defaultResponseBehaviour" , message = "")
public class CreditControlPolicyData extends BaseData implements Differentiable{

	private java.lang.String policyId;
	
	@NotEmpty(message = "Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	private String description;
	
	@NotEmpty(message = "Ruleset must be specified")
	private String ruleSet;
	private java.lang.Long orderNumber;
	
	@NotEmpty(message = "Status must be specified")
	private String status;
	private String script;
	
	@Valid
	private List<CreditControlDriverRelationData> driverList;
	
	private Set<CreditControlDriverRelationData> ccPolicyDriverRelDataSet;
	private String auditUId;
	private Set<CCResponseAttributes>  ccResponseAttributesSet;
	
	@NotEmpty(message = "Session Management must be specified")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid value of Session Management. Value could be 'True' or 'False'.")
	private String sessionManagement;
	
	private List<CCPolicyPluginConfig> ccPolicyPluginConfigList;
	
	@Valid
	private List<CCPolicyPluginConfig> prePlugins;
	@Valid
	private List<CCPolicyPluginConfig> postPlugins;
	
	@NotEmpty(message = "Default Response Behaviour must be specified")
	@Pattern(regexp = "REJECT|DROP|HOTLINE", message = "Invalid value of Default Response Behaviour. Value could be 'REJECT' or 'DROP' or 'HOTLINE'.")
	private String defaultResponseBehaviour;
	
	private String  defaultResponseBehaviorArgument;
	
	public CreditControlPolicyData() {
		description = RestUtitlity.getDefaultDescription();
		this.driverList = new ArrayList<CreditControlDriverRelationData>();
		this.ccPolicyPluginConfigList = new ArrayList<CCPolicyPluginConfig>();
		this.prePlugins = new ArrayList<CCPolicyPluginConfig>();
		this.postPlugins = new ArrayList<CCPolicyPluginConfig>();
	}
	
	@XmlTransient
	public Set<CreditControlDriverRelationData> getCcPolicyDriverRelDataSet() {
		return ccPolicyDriverRelDataSet;
	}

	public void setCcPolicyDriverRelDataSet(
			Set<CreditControlDriverRelationData> ccPolicyDriverRelDataSet) {
		this.ccPolicyDriverRelDataSet = ccPolicyDriverRelDataSet;
	}

	@XmlTransient
	public java.lang.String getPolicyId(){
		return policyId;
	}

	public void setPolicyId(java.lang.String policyId) {
		this.policyId = policyId;
	}

	@Override
	@XmlElement(name = "name")
	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "description")
	public String getDescription(){
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name = "ruleset")
	public String getRuleSet(){
		return ruleSet;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	@XmlTransient
	public java.lang.Long getOrderNumber(){
		return orderNumber;
	}

	public void setOrderNumber(java.lang.Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
	public String getStatus(){
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElementWrapper(name = "driver-details")
	@XmlElement(name = "driver-detail")
	public List<CreditControlDriverRelationData> getDriverList() {
		return driverList;
	}

	public void setDriverList(List<CreditControlDriverRelationData> driverList) {
		this.driverList = driverList;
	}

	@XmlElement(name = "driver-script")
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("PolicyId :" + policyId);
		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("RuleSet :" + ruleSet);
		writer.println("Session Management : " + sessionManagement);
		writer.println("Order Number :" + orderNumber);
		writer.println("Status :" + status);
		writer.println("Script :" + script);
		writer.println("DriverList :" + driverList);
		writer.println("Cc Policy Driver RelData Set :" + ccPolicyDriverRelDataSet);
		
		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Active", status);
		object.put("RuleSet", ruleSet);
		object.put("Session Management", sessionManagement);
		object.put("Default Response Behaviour", defaultResponseBehaviour);
		object.put("Default Response Behaviour Argument", defaultResponseBehaviorArgument);
		if(ccPolicyDriverRelDataSet!=null){
			JSONArray array = new JSONArray();
			for (CreditControlDriverRelationData element : ccPolicyDriverRelDataSet) {
				if(element.getDriverData()!=null && element.getDriverData().getName()!=null && element.getWeightage()!=null){
					array.add(element.getDriverData().getName() + "-W-" + element.getWeightage());
				}
			}
			object.put("Driver", array);
		}
		
		JSONObject responseObject = new JSONObject();	
		
		if( ccResponseAttributesSet != null ){
			JSONArray array = new JSONArray();
				for (CCResponseAttributes element : ccResponseAttributesSet) {
					array.add(element.toJson());
				}
			responseObject.put("Response Attributes", array);
		}
		
		object.put("Response Attributes Mappings",responseObject);
		object.put("Driver Script", script);
		
		if(ccPolicyPluginConfigList != null){
            JSONArray pre_Array = new  JSONArray();
            JSONArray post_Array = new  JSONArray();
            for(CCPolicyPluginConfig element : ccPolicyPluginConfigList){
                if(element.getPluginName().isEmpty() == false ){
                    if( element.getPluginType().equals(PolicyPluginConstants.IN_PLUGIN))
                        pre_Array.add(element.toJson());
                    else if( element.getPluginType().equals(PolicyPluginConstants.OUT_PLUGIN))
                        post_Array.add(element.toJson());
                }
            }
            object.put("Pre Plugin Details", pre_Array);
            object.put("Post Plugin Details", post_Array);
        }
		return object;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlElementWrapper(name = "command-code-wise-response-attributes") 
	@XmlElement(name = "command-code-wise-response-attribute", type = CCResponseAttributes.class)
	public Set<CCResponseAttributes> getCcResponseAttributesSet() {
		return ccResponseAttributesSet;
	}

	public void setCcResponseAttributesSet(Set<CCResponseAttributes> ccResponseAttributesSet) {
		this.ccResponseAttributesSet = ccResponseAttributesSet;
	}

	@XmlElement(name = "session-management")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getSessionManagement() {
		return sessionManagement;
	}

	public void setSessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
	}

	@XmlTransient
	public List<CCPolicyPluginConfig> getCcPolicyPluginConfigList() {
		return ccPolicyPluginConfigList;
	}

	public void setCcPolicyPluginConfigList(List<CCPolicyPluginConfig> ccPolicyPluginConfigList) {
		this.ccPolicyPluginConfigList = ccPolicyPluginConfigList;
	}

	@XmlElementWrapper(name = "pre-plugins")
	@XmlElement(name = "pre-plugin")
	public List<CCPolicyPluginConfig> getPrePlugins() {
		return prePlugins;
	}

	@XmlElementWrapper(name = "post-plugins")
	@XmlElement(name = "post-plugin")
	public List<CCPolicyPluginConfig> getPostPlugins() {
		return postPlugins;
	}
	
	@XmlElement(name = "default-response-behaviour-argument")
	public String getDefaultResponseBehaviorArgument() {
		return defaultResponseBehaviorArgument;
	}

	public void setDefaultResponseBehaviorArgument(String defaultResponseBehaviorArgument) {
		this.defaultResponseBehaviorArgument = defaultResponseBehaviorArgument;
	}

	@XmlElement(name = "default-response-behaviour")
	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}

	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}
}