package com.elitecore.elitesm.web.core.system.staff.forms;

import java.sql.Timestamp;
import java.text.ParseException;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateStaffStatusForm extends BaseWebForm{
	private String staffId;
	private String status;
	private String action;

	private Timestamp statusChangeDate;
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat("dd-MMM-yyyy");
	
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

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
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
	
}
