package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class AddRadiusPolicyForm extends BaseWebForm{

	private String action;
	private String strName;
	private String strDescription;
	private String strService;
	private Timestamp dtCreateDate;
	private Timestamp dtLastUpdated;
	private String strLastModifiedByStaffId;
	private long radiusPolicyId;
	private String strServiceName;
	private String strStatus ;
	private boolean checkItemForward;
	private boolean rejectItemForward;
	private boolean replyItemForward;
	private String checkItem;
	private String rejectItem;
	private String replyItem;
	
	
	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
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
	public boolean isCheckItemForward() {
		return checkItemForward;
	}
	public void setCheckItemForward(boolean checkItemForward) {
		this.checkItemForward = checkItemForward;
	}
	public boolean isRejectItemForward() {
		return rejectItemForward;
	}
	public void setRejectItemForward(boolean rejectItemForward) {
		this.rejectItemForward = rejectItemForward;
	}
	public boolean isReplyItemForward() {
		return replyItemForward;
	}
	public void setReplyItemForward(boolean replyItemForward) {
		this.replyItemForward = replyItemForward;
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
	
	public long getRadiusPolicyId(){
		return radiusPolicyId;
	}
	public void setRadiusPolicyId(long radiusPolicyId){
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
	
}
