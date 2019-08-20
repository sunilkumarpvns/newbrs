package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Set;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.base.data.CommonStatusData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name="radius-policy")
@XmlType(propOrder = {"name", "description", "commonStatusId", "checkItem", "addItem", "rejectItem", "replyItem", "radiusPolicyTimePeriodList"})
@ValidObject
public class RadiusPolicyData extends BaseData implements IRadiusPolicyData, Differentiable, Validator {
	
	@NotEmpty(message = "Radius Policy Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	private String description;
	private Timestamp createDate;
	private Timestamp lastUpdated;
	private String lastModifiedByStaffId;
	private String radiusPolicyId;
	@NotEmpty(message = "Radius policy status must be specified. Value could be 'ACTIVE' or 'INACTIVE'")
	@Contains(allowedValues = {BaseConstant.SHOW_STATUS_ID, BaseConstant.HIDE_STATUS_ID}, isNullable = true, invalidMessage = "Invalid status value. It can be 'ACTIVE' or 'INACTIVE' only.")
	private String commonStatusId;
	private String systemGenerated;
	private String createdByStaffId;
	private Timestamp statusChangeDate;	
	private String editable;	
	private String checkItem;
	private String rejectItem;
	private String replyItem;
	private String addItem;
	private CommonStatusData commonStatus;
	@Valid
	private List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodList;
	private String auditUId;
	
	public RadiusPolicyData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlTransient
	public String getRadiusPolicyId() {
		return radiusPolicyId;
	}
	public void setRadiusPolicyId(String radiusPolicyId) {
		this.radiusPolicyId = radiusPolicyId;
	}
	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	@XmlTransient
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) { 
		this.createdByStaffId = createdByStaffId;
	}
	@XmlTransient
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	@XmlTransient
	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}

	@XmlTransient
	public CommonStatusData getCommonStatus() {
		return commonStatus;
	}
	public void setCommonStatus(CommonStatusData commonStatus) {
		this.commonStatus = commonStatus;
	}
	@XmlTransient
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@XmlElement(name = "check-item-expression")
	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;

	}
	@XmlElement(name = "reject-item-expression")
	public String getRejectItem() {
		return rejectItem;
	}
	public void setRejectItem(String rejectItem) {
		this.rejectItem = rejectItem;
	}
	@XmlElement(name = "add-item-expression")
	public String getAddItem() {
		return addItem;
	}
	public void setAddItem(String addItem) {
		this.addItem = addItem;
	}
	@XmlElement(name = "reply-item-expression")
	public String getReplyItem() {
		return replyItem;
	}
	public void setReplyItem(String replyItem) {
		this.replyItem = replyItem;
	}
	@XmlElement(name = "time-base-policy")
	public List<RadiusPolicyTimePeriod> getRadiusPolicyTimePeriodList() {
		return radiusPolicyTimePeriodList;
	}
	public void setRadiusPolicyTimePeriodList(
			List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodList) {
		this.radiusPolicyTimePeriodList = radiusPolicyTimePeriodList;
	}
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();

		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("Create Date :" + createDate);
		writer.println("Last Updated :" + lastUpdated);
		writer.println("Last ModifiedBy StaffId :" + lastModifiedByStaffId);
		writer.println("Radius Policy Id :" + radiusPolicyId);
		writer.println("Common Status Id :" + commonStatusId);
		writer.println("System Generated :" + systemGenerated);
		writer.println("CreatedBy Staff Id :" + createdByStaffId);
		writer.println("Status Change Date :" + statusChangeDate);
		writer.println("Editable :" + editable);
		writer.println("Check Item :" + checkItem);
		writer.println("Reject Item :" + rejectItem);
		writer.println("Reply Item :" + replyItem);
		writer.println("Add Item :" + addItem);
		writer.println("Common Status :" + commonStatus);
		writer.println("Radius Policy Time PeriodSet :" + radiusPolicyTimePeriodList);

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
		object.put("Active", (commonStatusId.equals(BaseConstant.HIDE_STATUS_ID))?"InActive":"Active");
		object.put("Check Item Expressions", checkItem);
		object.put("Add Item Expressions", addItem);
		object.put("Reject Item Expressions", rejectItem);
		object.put("Reply Item Expressions", replyItem);
		if(radiusPolicyTimePeriodList!=null){
			JSONArray array = new JSONArray();
			for (RadiusPolicyTimePeriod element : radiusPolicyTimePeriodList) {
				array.add(element.toJson());
			}
			object.put("Time Base Condition", array);
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		if (Strings.isNullOrEmpty(this.checkItem) && Strings.isNullOrEmpty(this.addItem) && Strings.isNullOrEmpty(this.rejectItem) && Strings.isNullOrEmpty(this.replyItem) ) {
			RestUtitlity.setValidationMessage(context, "configure atleast one field among Check Item Expressions, Add Item Expressions, Reject Item Expressions and Reply Item Expressions.");
			return false;
		}
		return true;
	}
}
