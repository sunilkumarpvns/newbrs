package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;


import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewRadiusPolicyCheckItemForm extends BaseWebForm{

	private String strRadiusPolicyId;
	private String status;	
	private List lstCheckItem;
	private String checkItem;
	
	
	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}
	public String getRadiusPolicyId(){
		return strRadiusPolicyId;
	}
	public void setRadiusPolicyId(String strRadiusPolicyId){
		this.strRadiusPolicyId=strRadiusPolicyId;
	}

	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
		this.status=status;
	}

	public List getCheckItems(){
		return lstCheckItem;
	}
	public void setCheckItems(List lstCheckItem){
		this.lstCheckItem=lstCheckItem;
	}


	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request){
		ActionErrors errors=super.validate(mapping, request);
		return errors;
	}


}
