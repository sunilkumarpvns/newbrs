package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;

public class ViewCDRRecordsForm extends BaseWebForm{
	private String sessionId;
	private String name;
	private Long netServerId;
	private String netServerType;
	private String action;
	private Date callStartDate;
	private Date callEndDate;
	private List CDRRecordsList;
	private String errorCode;
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat(ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT));
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(Long netServerId) {
		this.netServerId = netServerId;
	}
	public String getNetServerType() {
		return netServerType;
	}
	public void setNetServerType(String netServerType) {
		this.netServerType = netServerType;
	}
	public List getCDRRecordsList() {
		return CDRRecordsList;
	}
	public void setCDRRecordsList(List recordsList) {
		CDRRecordsList = recordsList;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Date getCallEndDate() {
		return callEndDate;
	}
	public void setCallEndDate(Date callEndDate) {
		this.callEndDate = callEndDate;
	}
	public Date getCallStartDate() {
		return callStartDate;
	}
	public void setCallStartDate(Date callStartDate) {
		this.callStartDate = callStartDate;
	}
	public String getStrCallStartDate(){
		if(callStartDate != null)
			return formatter.format(callStartDate);
		else 
			return "";
	}
	public void setStrCallStartDate(String strCallStartDate){
		try{
			if(strCallStartDate != null && !strCallStartDate.equalsIgnoreCase(""))
			this.callStartDate=new Timestamp(formatter.parse(strCallStartDate).getTime());
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
	public String getStrCallEndDate(){
		if(callEndDate != null)
			return formatter.format(callEndDate);
		else
			return "";
	}
	public void setStrCallEndDate(String strCallEndDate){
		try {
			if(strCallEndDate != null && !strCallEndDate.equalsIgnoreCase(""))
			this.callEndDate = new Timestamp(formatter.parse(strCallEndDate).getTime());
		} catch (ParseException exp) {
			exp.printStackTrace();
		}
	}
}
