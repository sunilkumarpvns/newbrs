package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class CreateDatabaseSubscriberProfileFieldForm extends BaseDictionaryForm {
	private Long dbAuthId;
	private String action;
	private String fieldName;
	private String fieldType;
	private long fieldLength;
	
	public Long getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(Long dbAuthId) {
		this.dbAuthId = dbAuthId;
	}
	public String getAction(){
		return action;
	}
	public void setAction(String action){
		this.action=action;
	}
	public String getFieldName(){
		return fieldName;
	}
	public void setFieldName(String fieldName){
		this.fieldName=fieldName;
	}
	public String getFieldType(){
		return fieldType;
	}
	public void setFieldType(String fieldType){
		this.fieldType=fieldType;
	}
	public long getFieldLength(){
		return fieldLength;
	}
	public void setFieldLength(long fieldLength){
		this.fieldLength=fieldLength;
	}
}
