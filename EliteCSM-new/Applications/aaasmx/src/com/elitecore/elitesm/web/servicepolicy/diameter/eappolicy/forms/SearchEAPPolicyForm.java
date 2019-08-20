package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchEAPPolicyForm extends BaseWebForm {

	private java.lang.Long policyId;
    private String name;
    private String description;
    private String ruleSet;
    private java.lang.Long orderNumber;
    private String status;
    private int caseSensitiveUserIdentity;
	private String multipleUserIdentity="0:1";
	private String stripUserIdentity="false";
	private String realmPattern="suffix";
	private String realmSeparator="@";
	private String trimUserIdentity="true";
	private String trimPassword="false";
    private java.lang.Long eapConfigId;
    private String action;
    private long pageNumber;
	private long totalPages;
	private long totalRecords;
    private List searchList;
    private List<EAPConfigData> eapConfigurationList;
    private List<EAPPolicyData> searchEAPList;
    
    
    
	public long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}

	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List getSearchList() {
		return searchList;
	}

	public void setSearchList(List searchList) {
		this.searchList = searchList;
	}

	public List<EAPPolicyData> getSearchEAPList() {
		return searchEAPList;
	}

	public void setSearchEAPList(List<EAPPolicyData> searchEAPList) {
		this.searchEAPList = searchEAPList;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public java.lang.Long getPolicyId() {
		return policyId;
	}
	
	public void setPolicyId(java.lang.Long policyId) {
		this.policyId = policyId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	
	public java.lang.Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(java.lang.Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getCaseSensitiveUserIdentity() {
		return caseSensitiveUserIdentity;
	}
	
	public void setCaseSensitiveUserIdentity(int caseSensitiveUserIdentity) {
		this.caseSensitiveUserIdentity = caseSensitiveUserIdentity;
	}
	
	public String getMultipleUserIdentity() {
		return multipleUserIdentity;
	}
	
	public void setMultipleUserIdentity(String multipleUserIdentity) {
		this.multipleUserIdentity = multipleUserIdentity;
	}
	
	public String getStripUserIdentity() {
		return stripUserIdentity;
	}
	
	public void setStripUserIdentity(String stripUserIdentity) {
		this.stripUserIdentity = stripUserIdentity;
	}
	
	public String getRealmPattern() {
		return realmPattern;
	}
	
	public void setRealmPattern(String realmPattern) {
		this.realmPattern = realmPattern;
	}
	
	public String getRealmSeparator() {
		return realmSeparator;
	}
	
	public void setRealmSeparator(String realmSeparator) {
		this.realmSeparator = realmSeparator;
	}
	
	public String getTrimUserIdentity() {
		return trimUserIdentity;
	}
	
	public void setTrimUserIdentity(String trimUserIdentity) {
		this.trimUserIdentity = trimUserIdentity;
	}
	
	public String getTrimPassword() {
		return trimPassword;
	}
	
	public void setTrimPassword(String trimPassword) {
		this.trimPassword = trimPassword;
	}
	
	public java.lang.Long getEapConfigId() {
		return eapConfigId;
	}
	
	public void setEapConfigId(java.lang.Long eapConfigId) {
		this.eapConfigId = eapConfigId;
	}
	
	public List<EAPConfigData> getEapConfigurationList() {
		return eapConfigurationList;
	}
	
	public void setEapConfigurationList(List<EAPConfigData> eapConfigurationList) {
		this.eapConfigurationList = eapConfigurationList;
	} 

}
