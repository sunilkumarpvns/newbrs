package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateNASServicePolicyRFC4372CUIParamsForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	
	private String nasPolicyId;
	private String auditUId;
	private String name;
	private String cui;
	private String cuiResponseAttributes;
	private String advancedCuiExpression;	
	private String action;
	
	public String getNasPolicyId() {
		return nasPolicyId;
	}
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
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
	public String getCui() {
		return cui;
	}
	public void setCui(String cui) {
		this.cui = cui;
	}
	public String getCuiResponseAttributes() {
		return cuiResponseAttributes;
	}
	public void setCuiResponseAttributes(String cuiResponseAttributes) {
		this.cuiResponseAttributes = cuiResponseAttributes;
	}
	public String getAdvancedCuiExpression() {
		return advancedCuiExpression;
	}
	public void setAdvancedCuiExpression(String advancedCuiExpression) {
		this.advancedCuiExpression = advancedCuiExpression;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
