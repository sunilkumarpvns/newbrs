package com.elitecore.netvertexsm.web.core.system.systemparameter.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ViewSystemParameterForm extends BaseWebForm{
	
	private String parameterId;
	private String name;
	private String alias;
	private String value;
	private String systemGenerated;
	private String action;
	List lstParameterValue = new ArrayList();
	private PasswordPolicyConfigData passwordSelectionConfigData;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List getLstParameterValue() {
		return lstParameterValue;
	}
	public void setLstParameterValue(List lstParameterValue) {
		this.lstParameterValue = lstParameterValue;
	}
	public PasswordPolicyConfigData getPasswordSelectionConfigData() {
	    return passwordSelectionConfigData;
	}
	public void setPasswordSelectionConfigData(PasswordPolicyConfigData passwordSelectionConfigData) {
	    this.passwordSelectionConfigData = passwordSelectionConfigData;
	}
}
