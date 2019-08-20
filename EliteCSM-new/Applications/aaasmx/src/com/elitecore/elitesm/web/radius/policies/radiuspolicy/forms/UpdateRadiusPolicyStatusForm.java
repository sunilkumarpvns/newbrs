package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
//import com.elitecore.radius.manager.policies.radiuspolicy.data.*;
//import com.elitecore.radius.manager.system.standardmaster.data.RadiusStandardMasterBean;
//import com.elitecore.radius.manager.system.user.data.RadiusStaffBean;
//import com.elitecore.radius.web.base.forms.BaseWebForm;

public class UpdateRadiusPolicyStatusForm extends BaseWebForm{

	private String radiusPolicyId;
	private String status;
	private Timestamp statusChangeDate;
	private String reason;	
	private String action;	
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat("dd-MMM-yyyy");
	
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

	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
		this.status=status;
	}

	public Timestamp getStatusChangeDate(){
		return statusChangeDate;
	}
	public void setStatusChangeDate(Timestamp statusChangeDate){
		this.statusChangeDate=statusChangeDate;
	}

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

	public String getReason(){
		return reason;
	}
	public void setReason(String reason){
		this.reason=reason;
	}


}
