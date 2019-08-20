package com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms;

import java.sql.Timestamp;
import java.text.ParseException;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateConcurrentLoginPolicyStatusForm extends BaseWebForm {

	
	private String concurrentLoginId;
	private String status;
	private Timestamp statusChangeDate;
	private String reason;	
	private String action;	
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat("dd-MMM-yyyy");
	private String auditUId;
	
	public String getStrStatusChangeDate(){
		return formatter.format(statusChangeDate);
	}
	
	public void setStrStatusChangeDate(String strStatusChangeDate){
		try{
			this.statusChangeDate=new Timestamp(formatter.parse(strStatusChangeDate).getTime());
		}catch(ParseException e){
			e.printStackTrace();
		}
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getConcurrentLoginId() {
		return concurrentLoginId;
	}
	public void setConcurrentLoginId(String concurrentLoginId) {
		this.concurrentLoginId = concurrentLoginId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	
	
	
}
