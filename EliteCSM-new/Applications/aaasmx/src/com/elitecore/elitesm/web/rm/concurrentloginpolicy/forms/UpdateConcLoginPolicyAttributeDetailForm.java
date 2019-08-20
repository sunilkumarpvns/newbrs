package com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateConcLoginPolicyAttributeDetailForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String concurrentLoginId;
	private String name;
	private String strDescription;
	private String concurrentLoginPolicy;
	private String maxLogin; // Limited or Unlimited
	private int maxNumLogin;
	private String concurrentLoginPolicyMode;
	private String status;
	private String description;
	private List concurrentLoginPolicyDetail = new ArrayList();
	private String auditUId;

	private String attributeValue;
	private List lstAttribute;
	private String action;
	private int itemIndex;
	private int   serialNumber   ;
	
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
	public String getConcurrentLoginId() {
		return concurrentLoginId;
	}
	public void setConcurrentLoginId(String concurrentLoginId) {
		this.concurrentLoginId = concurrentLoginId;
	}
	public String getConcurrentLoginPolicy() {
		return concurrentLoginPolicy;
	}
	public void setConcurrentLoginPolicy(String concurrentLoginPolicy) {
		this.concurrentLoginPolicy = concurrentLoginPolicy;
	}
	public List getConcurrentLoginPolicyDetail() {
		return concurrentLoginPolicyDetail;
	}
	public void setConcurrentLoginPolicyDetail(List concurrentLoginPolicyDetail) {
		this.concurrentLoginPolicyDetail = concurrentLoginPolicyDetail;
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
/*	public int getLogin() {
		return login;
	}
	public void setLogin(int login) {
		this.login = login;
	}*/
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
	public int getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getMaxNumLogin() {
		return maxNumLogin;
	}
	public void setMaxNumLogin(int maxNumLogin) {
		this.maxNumLogin = maxNumLogin;
	}
	public List getLstAttribute() {
		return lstAttribute;
	}
	public void setLstAttribute(List lstAttribute) {
		this.lstAttribute = lstAttribute;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	
	
}
