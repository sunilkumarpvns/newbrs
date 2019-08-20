package com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class AddConcurrentLoginPolicyDetailForm extends BaseWebForm {

	private List  lstAttributeValue;
	private String attributeValue;
	private String maxLogin = "Limited"; // Limited or Unlimited
	private int maxNumLogin;
	private String action;
	private int itemIndex;

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}


	public int getMaxNumLogin() {
		return maxNumLogin;
	}

	public void setMaxNumLogin(int maxNumLogin) {
		this.maxNumLogin = maxNumLogin;
	}

	public String getMaxLogin() {
		return maxLogin;
	}

	public void setMaxLogin(String maxLogin) {
		this.maxLogin = maxLogin;
	}

	public List getLstAttributeValue() {
		return lstAttributeValue;
	}

	public void setLstAttributeValue(List lstAttributeValue) {
		this.lstAttributeValue = lstAttributeValue;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}


	

	
		
	

}
