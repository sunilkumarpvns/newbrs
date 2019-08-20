package com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewConcurrentLoginPolicyForm  extends BaseWebForm{

	private String strName;
	private String strDescription;
	private String strConcurrentLoginPolicy;
	private String strMaxLogin; // Limited or Unlimited
	private int ilogin;
	private String strConcurrentLoginPolicyMode;
	private String strStatus;
	private List concurrentLoginPolicyDetail = new ArrayList();
	
	public List getConcurrentLoginPolicyDetail() {
		return concurrentLoginPolicyDetail;
	}
	public void setConcurrentLoginPolicyDetail(List concurrentLoginPolicyDetail) {
		this.concurrentLoginPolicyDetail = concurrentLoginPolicyDetail;
	}
	public int getIlogin() {
		return ilogin;
	}
	public void setIlogin(int ilogin) {
		this.ilogin = ilogin;
	}
	public String getStrConcurrentLoginPolicy() {
		return strConcurrentLoginPolicy;
	}
	public void setStrConcurrentLoginPolicy(String strConcurrentLoginPolicy) {
		this.strConcurrentLoginPolicy = strConcurrentLoginPolicy;
	}
	public String getStrConcurrentLoginPolicyMode() {
		return strConcurrentLoginPolicyMode;
	}
	public void setStrConcurrentLoginPolicyMode(String strConcurrentLoginPolicyMode) {
		this.strConcurrentLoginPolicyMode = strConcurrentLoginPolicyMode;
	}
	public String getStrDescription() {
		return strDescription;
	}
	public void setStrDescription(String strDescription) {
		this.strDescription = strDescription;
	}
	public String getStrMaxLogin() {
		return strMaxLogin;
	}
	public void setStrMaxLogin(String strMaxLogin) {
		this.strMaxLogin = strMaxLogin;
	}
	public String getStrName() {
		return strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public String getStrStatus() {
		return strStatus;
	}
	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}
	
	
}
