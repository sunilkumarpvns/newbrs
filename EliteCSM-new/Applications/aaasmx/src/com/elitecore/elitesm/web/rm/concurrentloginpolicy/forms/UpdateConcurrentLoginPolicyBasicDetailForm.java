package com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateConcurrentLoginPolicyBasicDetailForm extends BaseWebForm{
	private String name;
	private String strDescription;
	private String concurrentLoginPolicy;
	private String maxLogin; // Limited or Unlimited
	private int login;
	private String concurrentLoginPolicyMode;
	private String status;
	private String description;
	private String action;
	private String attribute;
	private List<DictionaryParameterDetailData> dictionaryParameterDetailList;
	private String auditUId;
	private String ConcurrentLoginId;
	
	public String getConcurrentLoginId() {
		return ConcurrentLoginId;
	}
	public void setConcurrentLoginId(String concurrentLoginId) {
		ConcurrentLoginId = concurrentLoginId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getConcurrentLoginPolicy() {
		return concurrentLoginPolicy;
	}
	public void setConcurrentLoginPolicy(String concurrentLoginPolicy) {
		this.concurrentLoginPolicy = concurrentLoginPolicy;
	}
	public String getConcurrentLoginPolicyMode() {
		return concurrentLoginPolicyMode;
	}
	public void setConcurrentLoginPolicyMode(String concurrentLoginPolicyMode) {
		this.concurrentLoginPolicyMode = concurrentLoginPolicyMode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLogin() {
		return login;
	}
	public void setLogin(int login) {
		this.login = login;
	}
	public String getMaxLogin() {
		return maxLogin;
	}
	public void setMaxLogin(String maxLogin) {
		this.maxLogin = maxLogin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStrDescription() {
		return strDescription;
	}
	public void setStrDescription(String strDescription) {
		this.strDescription = strDescription;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public List<DictionaryParameterDetailData> getDictionaryParameterDetailList() {
		return dictionaryParameterDetailList;
	}
	public void setDictionaryParameterDetailList(
			List<DictionaryParameterDetailData> dictionaryParameterDetailList) {
		this.dictionaryParameterDetailList = dictionaryParameterDetailList;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	
	
}
