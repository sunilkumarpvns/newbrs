package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;


import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewRadiusPolicyReplyItemForm extends BaseWebForm{

	private String strRadiusPolicyId;
	private String status;	
	private List lstReplyItem;
	private String replyItem;
	
	public String getReplyItem() {
		return replyItem;
	}
	public void setReplyItem(String replyItem) {
		this.replyItem = replyItem;
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

	public List getReplyItems(){
		return lstReplyItem;
	}
	public void setReplyItems(List lstReplyItem){
		this.lstReplyItem=lstReplyItem;
	}


	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request){
		ActionErrors errors=super.validate(mapping, request);
		return errors;
	}


}
