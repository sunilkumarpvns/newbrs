package com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.CommandCodeResponseAttribute;

public class TGPPAAAPolicyData extends BaseData implements Serializable,Differentiable{

	private static final long serialVersionUID = 1L;
	private String tgppAAAPolicyId;
	private String name;
	private String description;
	private String status;
	private String ruleset;
	private String sessionManagement;
	private String cui;
	private String userIdentity;
	private byte[] tgppAAAPolicyXml;
	private Long orderNumber;
	private String auditUid;
	private List<CommandCodeResponseAttribute> commandCodeResponseAttributesList;
	private String defaultResponseBehaviorArgument;
	private String defaultResponseBehaviour;

	public String getTgppAAAPolicyId() {
		return tgppAAAPolicyId;
	}

	public void setTgppAAAPolicyId(String tgppAAAPolicyId) {
		this.tgppAAAPolicyId = tgppAAAPolicyId;
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

	public String getSessionManagement() {
		return sessionManagement;
	}

	public void setSessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
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

	public byte[] getTgppAAAPolicyXml() {
		return tgppAAAPolicyXml;
	}

	public void setTgppAAAPolicyXml(byte[] tgppAAAPolicyXml) {
		this.tgppAAAPolicyXml = tgppAAAPolicyXml;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String toString() {
		
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
	
		out.println(repeat("-", 70));
		out.println(padStart("TGPP AAA policy (Basic Details): " + getName(), 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "TGPP AAA Policy Id ",getTgppAAAPolicyId()));
		out.println(format("%-30s: %s", "Name", getName()));
		out.println(format("%-30s: %s", "Description", getDescription()));
		out.println(format("%-30s: %s", "Status", getStatus()));
		out.println(format("%-30s: %s", "Ruleset", getRuleset()));
		out.println(format("%-30s: %s", "Session Management", getSessionManagement()));
		out.println(format("%-30s: %s", "CUI", getCui()));
		out.println(format("%-30s: %s", "Order Number ",getOrderNumber()));
		out.println(format("%-30s: %s", "TGPP AAA XML", getTgppAAAPolicyXml()));
		
		out.close();
		return writer.toString();
	}
	
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Policy Name", name);
		if("CST01".equals(status)){
			object.put("Active", true);
		}else{
			object.put("Active", false);
		}
		object.put("Session Management", sessionManagement);
		object.put("Description", description);
		object.put("Ruleset", ruleset);
		object.put("User Identity", userIdentity);
		object.put("Cui", cui);
		object.put("Default Response Behaviour Argument", defaultResponseBehaviorArgument);
		object.put("Default Response Behaviour", defaultResponseBehaviour);
		return object;
	}

	public String getRuleset() {
		return ruleset;
	}

	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public List<CommandCodeResponseAttribute> getCommandCodeResponseAttributesList() {
		return commandCodeResponseAttributesList;
	}

	public void setCommandCodeResponseAttributesList(
			List<CommandCodeResponseAttribute> commandCodeResponseAttributesList) {
		this.commandCodeResponseAttributesList = commandCodeResponseAttributesList;
	}

	public String getDefaultResponseBehaviorArgument() {
		return defaultResponseBehaviorArgument;
	}

	public void setDefaultResponseBehaviorArgument(String defaultResponseBehaviorArgument) {
		this.defaultResponseBehaviorArgument = defaultResponseBehaviorArgument;
	}

	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}

	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}
}
