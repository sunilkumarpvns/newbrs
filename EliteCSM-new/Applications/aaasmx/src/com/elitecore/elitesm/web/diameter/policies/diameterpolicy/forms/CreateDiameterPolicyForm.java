/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateDiameterpolicyForm.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateDiameterPolicyForm extends BaseWebForm{

    private java.lang.Long diameterPolicyId;
    private String name;
    private String systemGenerated;
    private String description;
    private java.sql.Timestamp createDate;
    private String commonStatusId;
    private java.sql.Timestamp statusChangeDate;
    private java.lang.Long createdByStaffId;
    private java.sql.Timestamp lastModifiedDate;
    private java.lang.Long lastModifiedByStaffId;
    private String editable;
    private String checkItem;
    private String replyItem;
    private String rejectItem;
    private String action;
    private String status;
    private String[] monthOfYear;
	private String[] dayOfMonth;
	private String[] dayOfWeek;
	private String[] timePeriod;
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public java.lang.Long getDiameterPolicyId(){
        return diameterPolicyId;
    }

	public void setDiameterPolicyId(java.lang.Long diameterPolicyId) {
		this.diameterPolicyId = diameterPolicyId;
	}


    public String getName(){
        return name;
    }

	public void setName(String name) {
		this.name = name;
	}


    public String getSystemGenerated(){
        return systemGenerated;
    }

	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}


    public String getDescription(){
        return description;
    }

	public void setDescription(String description) {
		this.description = description;
	}


    public java.sql.Timestamp getCreateDate(){
        return createDate;
    }

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}


    public String getCommonStatusId(){
        return commonStatusId;
    }

	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}


    public java.sql.Timestamp getStatusChangeDate(){
        return statusChangeDate;
    }

	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}


    public java.lang.Long getCreatedByStaffId(){
        return createdByStaffId;
    }

	public void setCreatedByStaffId(java.lang.Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}


    public java.sql.Timestamp getLastModifiedDate(){
        return lastModifiedDate;
    }

	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}


    public java.lang.Long getLastModifiedByStaffId(){
        return lastModifiedByStaffId;
    }

	public void setLastModifiedByStaffId(java.lang.Long lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}


    public String getEditable(){
        return editable;
    }

	public void setEditable(String editable) {
		this.editable = editable;
	}


    public String getCheckItem(){
        return checkItem;
    }

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}


    public String getReplyItem(){
        return replyItem;
    }

	public void setReplyItem(String replyItem) {
		this.replyItem = replyItem;
	}


    public String getRejectItem(){
        return rejectItem;
    }

	public void setRejectItem(String rejectItem) {
		this.rejectItem = rejectItem;
	}

	public String[] getMonthOfYear() {
		return monthOfYear;
	}

	public void setMonthOfYear(String[] monthOfYear) {
		this.monthOfYear = monthOfYear;
	}

	public String[] getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String[] dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String[] getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String[] dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String[] getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(String[] timePeriod) {
		this.timePeriod = timePeriod;
	}
}
