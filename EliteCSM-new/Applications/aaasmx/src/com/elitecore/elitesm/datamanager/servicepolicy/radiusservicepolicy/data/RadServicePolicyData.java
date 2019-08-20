package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import java.io.Serializable;
import java.util.Arrays;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.BaseConstant;

public class RadServicePolicyData extends BaseData implements Serializable,Differentiable{

	private static final long serialVersionUID = 1L;
	private String radiusPolicyId;
	private String name;
	private String description;
	private String status;
	private String authMsg;
	private String acctMsg;
	private String authRuleset;
	private String acctRuleset;
	private String validatepacket;
	private String defaultAuthResBehavior;
	private String hotlinePolicy;
	private String defaultAcctResBehavior;
	private String sessionManagerId;
	private String authResponseAttributes;
	private String authAttribute;
	private String acctAttribute;
	private String cui;
	private String auditUid;
	private byte[] radiusPolicyXml;
	private Long orderNumber;
	private String userIdentity;
	private String advancedCuiExpression;
	private String acctResponseAttributes;
	
	public String getRadiusPolicyId() {
		return radiusPolicyId;
	}
	public void setRadiusPolicyId(String radiusPolicyId) {
		this.radiusPolicyId = radiusPolicyId;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAuthMsg() {
		return authMsg;
	}
	public void setAuthMsg(String authMsg) {
		this.authMsg = authMsg;
	}
	public String getAcctMsg() {
		return acctMsg;
	}
	public void setAcctMsg(String acctMsg) {
		this.acctMsg = acctMsg;
	}
	public String getAuthRuleset() {
		return authRuleset;
	}
	public void setAuthRuleset(String authRuleset) {
		this.authRuleset = authRuleset;
	}
	public String getAcctRuleset() {
		return acctRuleset;
	}
	public void setAcctRuleset(String acctRuleset) {
		this.acctRuleset = acctRuleset;
	}
	public String getValidatepacket() {
		return validatepacket;
	}
	public void setValidatepacket(String validatepacket) {
		this.validatepacket = validatepacket;
	}
	public String getDefaultAuthResBehavior() {
		return defaultAuthResBehavior;
	}
	public void setDefaultAuthResBehavior(String defaultAuthResBehavior) {
		this.defaultAuthResBehavior = defaultAuthResBehavior;
	}
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}
	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}
	public String getDefaultAcctResBehavior() {
		return defaultAcctResBehavior;
	}
	public void setDefaultAcctResBehavior(String defaultAcctResBehavior) {
		this.defaultAcctResBehavior = defaultAcctResBehavior;
	}
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}
	public String getAuthResponseAttributes() {
		return authResponseAttributes;
	}
	public void setAuthResponseAttributes(String responseAttributes) {
		this.authResponseAttributes = responseAttributes;
	}
	public String getAcctResponseAttributes() {
		return acctResponseAttributes;
	}
	public void setAcctResponseAttributes(String acctResponseAttributes) {
		this.acctResponseAttributes = acctResponseAttributes;
	}
	public String getAuthAttribute() {
		return authAttribute;
	}
	public void setAuthAttribute(String authAttribute) {
		this.authAttribute = authAttribute;
	}
	public String getAcctAttribute() {
		return acctAttribute;
	}
	public void setAcctAttribute(String acctAttribute) {
		this.acctAttribute = acctAttribute;
	}
	public String getCui() {
		return cui;
	}
	public void setCui(String cui) {
		this.cui = cui;
	}
	public String getAuditUid() {
		return auditUid;
	}
	public void setAuditUid(String auditUid) {
		this.auditUid = auditUid;
	}
	public byte[] getRadiusPolicyXml() {
		return radiusPolicyXml;
	}
	public void setRadiusPolicyXml(byte[] radiusPolicyXml) {
		this.radiusPolicyXml = radiusPolicyXml;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public String toString() {
		return "----------------RadServicePolicyData------------------------\n  radiusPolicyId = "
				+ radiusPolicyId
				+ "\n  name = "
				+ name
				+ "\n  description = "
				+ description
				+ "\n  status = "
				+ status
				+ "\n  authMsg = "
				+ authMsg
				+ "\n  acctMsg = "
				+ acctMsg
				+ "\n  authRuleset = "
				+ authRuleset
				+ "\n  acctRuleset = "
				+ acctRuleset
				+ "\n  validatepacket = "
				+ validatepacket
				+ "\n  defaultAuthResBehavior = "
				+ defaultAuthResBehavior
				+ "\n  hotlinePolicy = "
				+ hotlinePolicy
				+ "\n  defaultAcctResBehavior = "
				+ defaultAcctResBehavior
				+ "\n  sessionManagerId = "
				+ sessionManagerId
				+ "\n  authResponseAttributes = "
				+ authResponseAttributes
				+ "\n  authResponseAttributes = "
				+ acctResponseAttributes
				+ "\n  authAttribute = "
				+ authAttribute
				+ "\n  acctAttribute = "
				+ acctAttribute
				+ "\n  cui = "
				+ cui
				+ "\n  auditUid = "
				+ auditUid
				+ "\n  radiusPolicyXml = "
				+ Arrays.toString(radiusPolicyXml)
				+ "\n  orderNumber = "
				+ orderNumber
				+ "\n----------------RadServicePolicyData-------------------------\n";
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		JSONObject basicDetailObject = new JSONObject();
		
		basicDetailObject.put("Policy Name", name);
		if(BaseConstant.SHOW_STATUS_ID.equals(status)){
			basicDetailObject.put("Active", true);
		}else{
			basicDetailObject.put("Active", false);
		}
		basicDetailObject.put("Description", description);
		
		if("true".equals(authMsg) && "true".equals(acctMsg)){
			basicDetailObject.put("Radius Messages", "Authentication Message,Accounting Message");
		} else if ("true".equals(authMsg)) {
			basicDetailObject.put("Radius Messages", "Authentication Message");
		} else if ("true".equals(acctMsg)) {
			basicDetailObject.put("Radius Messages", "Accounting Message");
		} else {
			basicDetailObject.put("Radius Messages", "");
		}
		
		basicDetailObject.put("Authentication Ruleset", authRuleset);
		basicDetailObject.put("Accounting Ruleset", acctRuleset);
		basicDetailObject.put("User Identity", userIdentity);
		
		if ("true".equals(validatepacket)) {
			basicDetailObject.put("Validate Packet", true);
		} else {
			basicDetailObject.put("Validate Packet", false);
		}
		
		if ("0".equals(defaultAuthResBehavior)) {
			basicDetailObject.put("Default Authentication Response Behavior", "Reject(Default)");
		} else if ("1".equals(defaultAuthResBehavior)){
			basicDetailObject.put("Default Authentication Response Behavior", "Drop");
		} else {
			basicDetailObject.put("Default Authentication Response Behavior", "Hotline");
		}
		
		basicDetailObject.put("Hotline Policy", hotlinePolicy);
		
		if ("0".equals(defaultAcctResBehavior)) {
			basicDetailObject.put("Default Accounting Response Behavior", "Drop(Default)");
		} else {
			basicDetailObject.put("Default Accounting Response Behavior", "Response");
		}
		
		
		JSONObject sessionManagementObject = new JSONObject();
		sessionManagementObject.put("Session Manager", sessionManagerId);
		
		JSONObject rfcObject = new JSONObject();
		rfcObject.put("CUI", cui);
		rfcObject.put(" Advanced CUI Expression", advancedCuiExpression);
		rfcObject.put("CUI Response Attributes", authAttribute);
		
		rfcObject.put("CUI Attribute", acctAttribute);

		object.put("Basic Details", basicDetailObject);
		object.put("Session Management", sessionManagementObject);
		object.put("RFC-4372-CUI", rfcObject);
		return object;
		
	}
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	public String getAdvancedCuiExpression() {
		return advancedCuiExpression;
	}
	public void setAdvancedCuiExpression(String advancedCuiExpression) {
		this.advancedCuiExpression = advancedCuiExpression;
	}
}
