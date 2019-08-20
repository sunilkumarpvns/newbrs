/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyData.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.base.data.CommonStatusData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@XmlRootElement(name="authorization-policy")
@XmlType(propOrder = {"name", "description", "commonStatusId", "checkItem", "rejectItem", "replyItem", "diameterPolicyTimePeriodList"})
public class DiameterPolicyData extends BaseData implements Differentiable{

    private String diameterPolicyId;
    @NotEmpty(message = "Authorization Policy Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
    private String name;
    private String systemGenerated;
    private String description;
    private java.sql.Timestamp createDate;
    @NotEmpty(message = "Authorization policy status must be specified. Value could be 'ACTIVE' or 'INACTIVE'")
    @Contains(allowedValues = {BaseConstant.SHOW_STATUS_ID, BaseConstant.HIDE_STATUS_ID}, isNullable = true, invalidMessage = "Invalid status value. It can be 'ACTIVE' or 'INACTIVE' only.")
    private String commonStatusId;
    private java.sql.Timestamp statusChangeDate;
    private String createdByStaffId;
    private java.sql.Timestamp lastModifiedDate;
    private String lastModifiedByStaffId;
    private String editable;
    private String checkItem;
    private String replyItem;
    private String rejectItem;
    private CommonStatusData commonStatus;
    @Valid
	private List<DiameterPolicyTimePeriod> diameterPolicyTimePeriodList;
    private String auditUId;

	public DiameterPolicyData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
    @XmlTransient
    public CommonStatusData getCommonStatus() {
		return commonStatus;
	}

	public void setCommonStatus(CommonStatusData commonStatus) {
		this.commonStatus = commonStatus;
	}
	
	@XmlTransient
	public String getDiameterPolicyId(){
        return diameterPolicyId;
    }

	public void setDiameterPolicyId(String diameterPolicyId) {
		this.diameterPolicyId = diameterPolicyId;
	}

	@XmlElement(name = "name")
    public String getName(){
        return name;
    }

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
    public String getSystemGenerated(){
        return systemGenerated;
    }

	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}

	@XmlElement(name = "description")
    public String getDescription(){
        return description;
    }

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
    public java.sql.Timestamp getCreateDate(){
        return createDate;
    }

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(StatusAdapter.class)
    public String getCommonStatusId(){
        return commonStatusId;
    }

	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}

	@XmlTransient
    public java.sql.Timestamp getStatusChangeDate(){
        return statusChangeDate;
    }

	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	@XmlTransient
    public String getCreatedByStaffId(){
        return createdByStaffId;
    }

	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	@XmlTransient
    public java.sql.Timestamp getLastModifiedDate(){
        return lastModifiedDate;
    }

	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@XmlTransient
    public String getLastModifiedByStaffId(){
        return lastModifiedByStaffId;
    }

	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}

	@XmlTransient
    public String getEditable(){
        return editable;
    }

	public void setEditable(String editable) {
		this.editable = editable;
	}

	@XmlElement(name = "check-item-expression")
    public String getCheckItem(){
        return checkItem;
    }

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

	@XmlElement(name = "reply-item-expression")
    public String getReplyItem(){
        return replyItem;
    }

	public void setReplyItem(String replyItem) {
		this.replyItem = replyItem;
	}

	@XmlElement(name = "reject-item-expression")
    public String getRejectItem(){
        return rejectItem;
    }

	public void setRejectItem(String rejectItem) {
		this.rejectItem = rejectItem;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Active", (commonStatusId.equals(BaseConstant.HIDE_STATUS_ID))?"Inactive":"Active");
		object.put("Check Item Expression", checkItem);
		object.put("Reject Item Expression", rejectItem);
		object.put("Reply Item Expression", replyItem);
		if(diameterPolicyTimePeriodList!=null){
			JSONArray array = new JSONArray();
			for (DiameterPolicyTimePeriod element : diameterPolicyTimePeriodList) {
				array.add(element.toJson());
			}
			object.put("Time Base Condition", array);
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
	
	@XmlElement(name = "time-base-policy")
	public List<DiameterPolicyTimePeriod> getDiameterPolicyTimePeriodList() {
		return diameterPolicyTimePeriodList;
	}

	public void setDiameterPolicyTimePeriodList(
			List<DiameterPolicyTimePeriod> diameterPolicyTimePeriodList) {
		this.diameterPolicyTimePeriodList = diameterPolicyTimePeriodList;
	}
}
