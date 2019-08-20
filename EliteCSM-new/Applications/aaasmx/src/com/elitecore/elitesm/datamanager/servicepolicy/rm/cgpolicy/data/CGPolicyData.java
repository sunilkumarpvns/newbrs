package com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

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

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

@XmlRootElement(name = "charging-service-policy")
@XmlType(propOrder = {"name", "description", "status", "ruleSet", "driverList", "script"})
public class CGPolicyData extends BaseData implements Serializable,Differentiable {
	
	private static final long serialVersionUID = 1L;
	private String policyId;
	
	@NotEmpty(message = "Name must be specified")
	@Pattern(regexp = "[a-zA-Z0-9.\\-\\_]*", message = RestValidationMessages.NAME_INVALID)
    private String name;
    private String description;
    
    @NotEmpty(message = "RuleSet must be specified")
    private String ruleSet;
    private Long orderNumber;
    
    @Pattern(regexp = "CST01|CST02", message = "Status value must be ACTIVE or INACTIVE")
    private String status;
    private String script;
    @Valid
    @NotEmpty(message = "Atleast one driver must be specified")
    private List<CGPolicyDriverRelationData> driverList;
    private String auditUId;
    
    public CGPolicyData() {
    	description = RestUtitlity.getDefaultDescription();
	}
    
    @XmlElementWrapper(name = "driver-group")
    @XmlElement(name = "driver-detail")
    public List<CGPolicyDriverRelationData> getDriverList() {
		return driverList;
	}

	public void setDriverList(List<CGPolicyDriverRelationData> driverList) {
		this.driverList = driverList;
	}
	
	@XmlTransient
	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(java.lang.String policyId) {
		this.policyId = policyId;
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
	
	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(java.lang.Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(value = StatusAdapter.class)
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement(name = "script")
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
		
		writer.println("Policy Id :" + policyId);
		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("RuleSet :" + ruleSet);
		writer.println("Order Number :" + orderNumber);
		writer.println("Status :" + status);
		writer.println("Script :" + script);
		writer.println("DriverList :" + driverList);
		
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
		if(status.equals("CST01")){
			object.put("Active", "true");
		}else if(status.equals("CST02")){
			object.put("Active", "false");
		}
		
		object.put("RuleSet", ruleSet);
		if(driverList!=null){
			JSONArray array = new JSONArray();
			for (CGPolicyDriverRelationData element : driverList) {
				if(element != null){
					if(element.getDriverData()!=null && element.getDriverData().getName()!=null && element.getWeightage()!=null){
						array.add(element.getDriverData().getName() + "-W-" + element.getWeightage());
					}
				}
				
			}
			object.put("Driver", array);
		}
		object.put("Driver Script", script);
		return object;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
 }
