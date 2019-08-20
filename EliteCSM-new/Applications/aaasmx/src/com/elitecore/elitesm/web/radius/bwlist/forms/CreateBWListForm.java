package com.elitecore.elitesm.web.radius.bwlist.forms;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

public class CreateBWListForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private String attributeId;
	private String attributeValue;
	private String activeStatus;
	private FormFile fileUpload;
	private String inputMode;
	private Timestamp  validity;
	private String remainingTime;
	private String typeName;
	private String bwId;
	
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat(ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT));
	
	
	public String getStrValidity() {
		if(validity != null){
			return formatter.format(validity);
		}else {
			return "";
		}
	}
	public void setStrValidity(String strValidity) {
		try{
			if(strValidity == ""){
				Date now = new Date(); 
				Calendar cal = Calendar.getInstance();  
				cal.add(cal.DAY_OF_MONTH, 1);
				Date tomorrow = cal.getTime();
				DateFormat formatter1 = new SimpleDateFormat("dd-MM-yy HH:mm");
				String formattedDate = formatter1.format(now);
				formattedDate = formatter1.format(tomorrow);
				this.validity=new Timestamp(formatter1.parse(formatter1.format(tomorrow)).getTime());
			
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				java.util.Date date = sdf.parse(strValidity);
				java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				this.validity=timestamp;
			}
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
	
	public String getInputMode() {
		return inputMode;
	}
	public void setInputMode(String inputMode) {
		this.inputMode = inputMode;
	}
	public FormFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FormFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	public String getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}
	public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	public Timestamp getValidity() {
		return validity;
	}
	public void setValidity(Timestamp validity) {
		this.validity = validity;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getBwId() {
		return bwId;
	}
	public void setBwId(String bwId) {
		this.bwId = bwId;
	}
	public String getRemainingTime() {
		return remainingTime;
	}
	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}
}
