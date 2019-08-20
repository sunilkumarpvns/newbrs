package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;



import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class RadiusPolicyForm extends BaseWebForm{

	private String action;
	private String strName;
	private String strDescription;
	private String strService;
	private long createDate;
	private long dtLastUpdated;
	private String lastModifiedByStaffId;
	private String createdByStaffId;
	private String radiusPolicyId;
	private String strServiceName;
	private String strStatus ;
	private String checkItem;
	private String addItem;
	private String rejectItem;
	private String replyItem;
	private String[] monthOfYear;
	private String[] dayOfMonth;
	private String[] dayOfWeek;
	private String[] timePeriod;
	private String commonStatusId; 
	private String systemGenerated;
	private long statusChangeDate;
	private String editable;
	private String auditUId;
	
	
	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}
	public String getAddItem() {
		return addItem;
	}
	public void setAddItem(String addItem) {
		this.addItem = addItem;
	}
	public String getRejectItem() {
		return rejectItem;
	}
	public void setRejectItem(String rejectItem) {
		this.rejectItem = rejectItem;
	}
	public String getReplyItem() {
		return replyItem;
	}
	public void setReplyItem(String replyItem) {
		this.replyItem = replyItem;
	}
	public String getServiceName(){
		return strServiceName;
	}
	public void setServiceName(String strServiceName){
		this.strServiceName=strServiceName;
	}
	
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
	
	public String getService(){
		return strService;
	}
	public void setService(String strService){
		this.strService=strService;
	}
	
	public long getCreateDate(){
		return createDate;
	}
	public void setCreateDate(long createDate){
		this.createDate=createDate;
	}
	
	public long getLastUpdated(){
		return dtLastUpdated;
	}
	public void setLastUpdated(long dtLastUpdated){
		this.dtLastUpdated=dtLastUpdated;
	}
	
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
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

	public String getStrStatus(){
		return strStatus;
	}
	public void setStrStatus(String strStatus){
		this.strStatus=strStatus;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
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
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public long getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(long statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
   	
	
}
