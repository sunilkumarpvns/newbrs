package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewRadiusPolicyForm extends BaseWebForm{

	private String strName;
	private String strDescription;
	private String strCommonStatusId;	
	private Timestamp dtCreateDate;
	private Timestamp dtLastUpdated;
	private String strLastModifiedByStaffId;
	private String radiusPolicyId;
	private String systemGenerated;	
	private String createdByStaffId;
	private Timestamp statusChangeDate;
	private String editable;	
	private String action;
	
	public String getName(){
		return strName;
	}
	public void setName(String strName){
		this.strName=strName;
	}
	
	public String getDescription(){
		return strDescription;
	}
	public void setDescription(String strDescription){
		this.strDescription=strDescription;
	}
	public String getCommonStatusId(){
		return strCommonStatusId;
	}
	public void setCommonStatusId(String strCommonStatusId){
		this.strCommonStatusId=strCommonStatusId;
	}
	
	public Timestamp getCreateDate(){
		return dtCreateDate;
	}
	public void setCreateDate(Timestamp dtCreateDate){
		this.dtCreateDate=dtCreateDate;
	}
	
	public Timestamp getLastUpdated(){
		return dtLastUpdated;
	}
	public void setLastUpdated(Timestamp dtLastUpdated){
		this.dtLastUpdated=dtLastUpdated;
	}
	
	public String getLastModifiedByStaffId(){
		return strLastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String strLastModifiedByStaffId){
		this.strLastModifiedByStaffId=strLastModifiedByStaffId;
	}
	
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}

	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}
	
	public String getRadiusPolicyId(){
		return radiusPolicyId;
	}
	public void setRadiusPolicyId(String radiusPolicyId){
		this.radiusPolicyId=radiusPolicyId;
	}
	

	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request){
		ActionErrors errors=super.validate(mapping, request);
		return errors;
	}

	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action=action;
	}
}
