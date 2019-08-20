package com.elitecore.netvertexsm.web.core.system.systemparameter.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateSystemParameterForm extends BaseWebForm{
	
	List lstParameterValue = new ArrayList();
	String action ;
	String status;
	private String passwordRange;
	private String aplhabetsRange;
	private String digitRange;
	private String speCharRange;
	private String proChar;
	private String passwordPolicyId;
	private String pwdValidity;
	private String totalHistoricalPasswords;
	private boolean changePwdOnFirstLogin;
	private PasswordPolicyConfigData passwordSelectionConfigData;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getLstParameterValue() {
		return lstParameterValue;
	}
	public void setLstParameterValue(List lstParameterValue) {
		this.lstParameterValue = lstParameterValue;
	}

	public String getParameterId(int index) {
		return String.valueOf(((SystemParameterData)lstParameterValue.get(index)).getParameterId());
	}
	public void setParameterId(int index,String parameterId) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new SystemParameterData());
		((SystemParameterData)lstParameterValue.get(index)).setParameterId(Long.valueOf(parameterId));
	}
	public String getName(int index) {
		return ((SystemParameterData)lstParameterValue.get(index)).getName();
	}
	public void setName(int index,String name) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new SystemParameterData());
		((SystemParameterData)lstParameterValue.get(index)).setName(name);
	}
	public String getAlias(int index) {
		return ((SystemParameterData)lstParameterValue.get(index)).getAlias();
	}
	public void setAlias(int index,String alias) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new SystemParameterData());
		((SystemParameterData)lstParameterValue.get(index)).setAlias(alias);
	}
	public String getValue(int index) {
		return ((SystemParameterData)lstParameterValue.get(index)).getValue();
	}
	public void setValue(int index,String value) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new SystemParameterData());
		((SystemParameterData)lstParameterValue.get(index)).setValue(value);
	}
	public String getDescription(int index) {
		return ((SystemParameterData)lstParameterValue.get(index)).getDescription();
	}
	public void setDescription(int index,String description) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new SystemParameterData());
		((SystemParameterData)lstParameterValue.get(index)).setDescription(description);
	}
	public String getSystemGenerated(int index) {
		return ((SystemParameterData)lstParameterValue.get(index)).getSystemGenerated();
	}
	public void setSystemGenerated(int index,String systemGenerated) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new SystemParameterData());
		((SystemParameterData)lstParameterValue.get(index)).setSystemGenerated(systemGenerated);
	}
	public Set getSystemParameterValuePoolData(int index) {
		return ((SystemParameterData)lstParameterValue.get(index)).getParameterDetail();
	}
	public void setSystemParameterValuePoolData(int index, Set systemParameterValuePoolData) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new SystemParameterData());
		((SystemParameterData)lstParameterValue.get(index)).setParameterDetail(systemParameterValuePoolData);
	}
	public String getPasswordRange() {
	    return passwordRange;
	}
	public void setPasswordRange(String passwordRange) {
	    this.passwordRange = passwordRange;
	}
	public String getAplhabetsRange() {
	    return aplhabetsRange;
	}
	public void setAplhabetsRange(String aplhabetsRange) {
	    this.aplhabetsRange = aplhabetsRange;
	}
	public String getDigitRange() {
	    return digitRange;
	}
	public void setDigitRange(String digitRange) {
	    this.digitRange = digitRange;
	}
	public String getSpeCharRange() {
	    return speCharRange;
	}
	public void setSpeCharRange(String speCharRange) {
	    this.speCharRange = speCharRange;
	}
	public String getProChar() {
	    return proChar;
	}
	public void setProChar(String proChar) {
	    this.proChar = proChar;
	}
	public String getPasswordPolicyId() {
	    return passwordPolicyId;
	}
	public void setPasswordPolicyId(String passwordPolicyId) {
	    this.passwordPolicyId = passwordPolicyId;
	}
	public String getPwdValidity() {
	    return pwdValidity;
	}
	public void setPwdValidity(String pwdValidity) {
	    this.pwdValidity = pwdValidity;
	}
	public String getTotalHistoricalPasswords() {
	    return totalHistoricalPasswords;
	}
	public void setTotalHistoricalPasswords(String totalHistoricalPasswords) {
	    this.totalHistoricalPasswords = totalHistoricalPasswords;
	}
	public boolean isChangePwdOnFirstLogin() {
	    return changePwdOnFirstLogin;
	}
	public void setChangePwdOnFirstLogin(boolean changePwdOnFirstLogin) {
	    this.changePwdOnFirstLogin = changePwdOnFirstLogin;
	}
	public PasswordPolicyConfigData getPasswordSelectionConfigData() {
	    return passwordSelectionConfigData;
	}
	public void setPasswordSelectionConfigData(PasswordPolicyConfigData passwordSelectionConfigData) {
	    this.passwordSelectionConfigData = passwordSelectionConfigData;
	}
		
}
