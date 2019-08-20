package com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
import org.hibernate.validator.constraints.Range;

import com.elitecore.aaa.util.constants.DynauthServicePolicyConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.DatabaseDatasourceNameAdapter;
import com.elitecore.elitesm.ws.rest.adapter.EligibleSessionsAdapter;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "dynauth-service-policy")
@XmlType(propOrder = {"name", "description", "status", "ruleSet", "responseAttributes", "validatePacket", 
		"eventTimestamp", "databaseDatasourceId", "tableName", "dbFailureAction", "eligibleSession", 
		"dynAuthFeildMapSet", "dynAuthNasClientDataSet"})
@ValidObject
public class DynAuthPolicyInstData extends BaseData implements  Serializable,Differentiable, Validator{
	
	private static final long serialVersionUID = 1L;
	private String dynAuthPolicyId;

	@NotEmpty(message = "Policy Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	private String description;
	private String status;
	private Long orderNumber;
	
	@NotEmpty(message = "Policy Selection Rule must be specified")
	private String ruleSet;
	private String responseAttributes;
	
	@NotEmpty(message = "Validate Packet must be specified")
	@Pattern(regexp = "true|false", message = "Invalid value of Validate Packet. Value could be 'True' or 'False'.")
	private String validatePacket;
	
	@NotNull(message = "Eligible Session must be specified. Value could be 'NONE' or 'RECENT' or 'OLDEST' or 'ALL'.")
	@Range(min = 1, max = 4, message = "Invalid value of Eligible Session. Value could be 'NONE' or 'RECENT' or 'OLDEST' or 'ALL'.")
	private Integer eligibleSession;
	
	private Long eventTimestamp;
	@NotEmpty(message = "Table Name must be specified")
	private String tableName;
	private String databaseDatasourceId;
	
	@Valid
	private Set<DynAuthFieldMapData> dynAuthFeildMapSet;
	
	@Valid
	private Set<DynAuthNasClientsData> dynAuthNasClientDataSet;
	private List<DynAuthFieldMapData> mappingList;
	private String auditUId;
	
	@Pattern(regexp = "NAK|DROP", message = "Supported DB Failure action are NAK and DROP only")
	private String dbFailureAction;
	
	public DynAuthPolicyInstData() {
		description = RestUtitlity.getDefaultDescription();
		this.eventTimestamp = DynauthServicePolicyConstants.EVENT_TIMESTAMP;
		this.mappingList = new ArrayList<DynAuthFieldMapData>();
	}
	
	@XmlTransient
	public List<DynAuthFieldMapData> getMappingList() {
		return mappingList;
	}

	public void setMappingList(List<DynAuthFieldMapData> mappingList) {
		this.mappingList = mappingList;
	}

	@XmlElementWrapper(name = "missing-attribute-field-mapping")
	@XmlElement(name = "attribute-mapping", type = DynAuthFieldMapData.class)
	public Set<DynAuthFieldMapData> getDynAuthFeildMapSet() {
		return dynAuthFeildMapSet;
	}
	
	public void setDynAuthFeildMapSet(Set<DynAuthFieldMapData> dynaAuthFeildMapSet) {
		this.dynAuthFeildMapSet = dynaAuthFeildMapSet;
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
	
	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	@XmlElement(name = "response-attributes")
	public String getResponseAttributes() {
		return responseAttributes;
	}
	
	public void setResponseAttributes(String responseAttributes) {
		this.responseAttributes = responseAttributes;
	}

	@XmlTransient
	public String getDynAuthPolicyId() {
		return dynAuthPolicyId;
	}
	
	public void setDynAuthPolicyId(String dynAuthPolicyId) {
		this.dynAuthPolicyId = dynAuthPolicyId;
	}
	
	@XmlElement(name = "eligible-session")
	@XmlJavaTypeAdapter(EligibleSessionsAdapter.class)
	public Integer getEligibleSession() {
		return eligibleSession;
	}
	
	public void setEligibleSession(Integer eligibleSession) {
		this.eligibleSession = eligibleSession;
	}
	
	@XmlElement(name = "event-timestamp")
	public Long getEventTimestamp() {
		return eventTimestamp;
	}
	
	public void setEventTimestamp(Long eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}
	
	@XmlElement(name = "table-name")
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@XmlElement(name = "database-datasource")
	@NotEmpty(message = "Database datasource must be specified")
	@XmlJavaTypeAdapter(DatabaseDatasourceNameAdapter.class)
	public String getDatabaseDatasourceId() {
		return databaseDatasourceId;
	}
	
	public void setDatabaseDatasourceId(String databaseDatasourceId) {
		this.databaseDatasourceId = databaseDatasourceId;
	}
	
	@XmlElement(name = "validate-packet")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getValidatePacket() {
		return validatePacket;
	}
	
	public void setValidatePacket(String validatePacket) {
		this.validatePacket = validatePacket;
	}

	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ DynAuthPolicyInstData --------------");
		writer.println("DynAuth PolicyId 	          :"+dynAuthPolicyId);
		writer.println("Name                          :"+name);           
		writer.println("Description                   :"+description);
		writer.println("Status                        :"+status);         
		writer.println("Order Number                   :"+orderNumber);
		writer.println("RuleSet	           		      :"+ruleSet);   
		writer.println("Response Attributes	          :"+responseAttributes);
		writer.println("Eligible Session	           	  :"+eligibleSession);
		writer.println("Table Name	           		  :"+tableName);
		writer.println("Database Datasource Id	      :"+databaseDatasourceId);
		writer.println("Event Timestamp	           	  :"+eventTimestamp);
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Status", (status.equals(BaseConstant.HIDE_STATUS_ID))? "Inactive" : "Active" );
		object.put("Description", description);
		object.put("Policy Selection Rule", ruleSet);
		object.put("Response attributes", responseAttributes);
		object.put("Validate Packet", (validatePacket.equals("true") ? "True" : "False"));
		object.put("Database Datasource", EliteSMReferencialDAO.fetchDatabaseDatasourceData(databaseDatasourceId));
		object.put("Table Name", tableName);
		object.put("Eligible Session", getEligibleSessionDetails(eligibleSession));
		object.put("Event Timestamp", eventTimestamp);
		object.put("DB Failure Action", dbFailureAction);
		
		if(Collectionz.isNullOrEmpty(dynAuthFeildMapSet) == false){
			JSONArray array = new JSONArray();
			for (DynAuthFieldMapData element : dynAuthFeildMapSet) {
				array.add(element.toJson());
			}
			object.put("DynaAuth Field Mapping", array);
		}
		
		if(Collectionz.isNullOrEmpty(dynAuthNasClientDataSet) == false){
			JSONArray array = new JSONArray();
			for(DynAuthNasClientsData elClientsData :dynAuthNasClientDataSet){
				array.add(elClientsData.toJson());
			}
			object.put("NAS Clients ", array);
		}

		return object;
	}
	
	private String getEligibleSessionDetails(Integer eligibleSessionData) {
		if( eligibleSessionData  == 1 ){
			return "None";
		}else if( eligibleSessionData == 2 ){
			return "Recent";
		}else if( eligibleSessionData == 3 ){
			return "Oldest";
		}else if( eligibleSessionData == 4 ){
			return "All";
		}
		return "-";
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlElementWrapper(name = "nas-clients")
	@XmlElement(name = "nas-client", type = DynAuthNasClientsData.class)
	public Set<DynAuthNasClientsData> getDynAuthNasClientDataSet() {
		return dynAuthNasClientDataSet;
	}

	public void setDynAuthNasClientDataSet(Set<DynAuthNasClientsData> dynAuthNasClientDataSet) {
		this.dynAuthNasClientDataSet = dynAuthNasClientDataSet;
	}

	@XmlElement(name = "db-failure-action")
	public String getDbFailureAction() {
		return dbFailureAction;
	}

	public void setDbFailureAction(String dbFailureAction) {
		this.dbFailureAction = dbFailureAction;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(RestValidationMessages.INVALID.equals(this.databaseDatasourceId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Database datasource not found");
		}
		return isValid;
	}
}