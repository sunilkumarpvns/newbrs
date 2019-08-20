package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;


import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewRadiusPolicyRejectItemForm extends BaseWebForm{

	private String strRadiusPolicyId;
	private String status;	
	private List lstRejectItem;
	private String rejectItem;
	
	public String getRejectItem() {
		return rejectItem;
	}
	public void setRejectItem(String rejectItem) {
		this.rejectItem = rejectItem;
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

	public List getRejectItems(){
		return lstRejectItem;
	}
	public void setRejectItems(List lstRejectItem){
		this.lstRejectItem=lstRejectItem;
	}


	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request){
		ActionErrors errors=super.validate(mapping, request);
		return errors;
	}


}
