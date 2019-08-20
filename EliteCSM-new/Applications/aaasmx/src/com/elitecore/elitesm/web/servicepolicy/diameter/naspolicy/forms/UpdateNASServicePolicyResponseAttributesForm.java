package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import java.util.Set;

import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateNASServicePolicyResponseAttributesForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String nasPolicyId;
	private String action;
	private Set<NASResponseAttributes> nasResponseAttributesList;
	private String auditUId;
	private String name;

	public String getNasPolicyId() {
		return nasPolicyId;
	}
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Set<NASResponseAttributes> getNasResponseAttributesList() {
		return nasResponseAttributesList;
	}
	public void setNasResponseAttributesList(
			Set<NASResponseAttributes> nasResponseAttributesList) {
		this.nasResponseAttributesList = nasResponseAttributesList;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
