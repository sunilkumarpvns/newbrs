package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;

import org.apache.struts.action.ActionForm;
			 
public class MiscSearchDatabaseSubscriberProfileForm extends ActionForm {
	
	private String driverInstanceId;
	private String dbAuthId;
	private String miscAction;
	private String checkField;
	private String fieldName;
	private String pageNo;
	private String totalNumberOfRecord;
	private String totalPage;
	private String firstFieldName;
	//private String secondFieldName;
	
	private String firstFieldData;
	//private String secondFieldData;
	
	private String idFieldName;
	
	private String start;
	private String end;
	

	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(String dbAuthId) {
		this.dbAuthId = dbAuthId;
	}
	public String getCheckField(){
		return checkField;
	}
	public void setCheckField(String checkField){
		this.checkField=checkField;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getPageNo() {
		return pageNo;
	}
	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}
	public String getTotalNumberOfRecord() {
		return totalNumberOfRecord;
	}
	public void setTotalNumberOfRecord(String totalNumberOfRecord) {
		this.totalNumberOfRecord = totalNumberOfRecord;
	}
	public String getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}
	public String getMiscAction() {
		return miscAction;
	}
	public void setMiscAction(String miscAction) {
		this.miscAction = miscAction;
	}
	public String getFirstFieldData() {
		return firstFieldData;
	}
	public void setFirstFieldData(String firstFieldData) {
		this.firstFieldData = firstFieldData;
	}
	public String getFirstFieldName() {
		return firstFieldName;
	}
	public void setFirstFieldName(String firstFieldName) {
		this.firstFieldName = firstFieldName;
	}
	public String getIdFieldName() {
		return idFieldName;
	}
	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}
	/*public String getSecondFieldData() {
		return secondFieldData;
	}
	public void setSecondFieldData(String secondFieldData) {
		this.secondFieldData = secondFieldData;
	}
	public String getSecondFieldName() {
		return secondFieldName;
	}
	public void setSecondFieldName(String secondFieldName) {
		this.secondFieldName = secondFieldName;
	}*/
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
}
