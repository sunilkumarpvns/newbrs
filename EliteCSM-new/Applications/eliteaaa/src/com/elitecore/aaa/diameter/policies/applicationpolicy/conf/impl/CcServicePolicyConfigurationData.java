package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.CcServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.RejectBehavior;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType.DefaultResponseBehaviorTypeAdapter;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.commons.config.BooleanAdapter;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlType(propOrder={})
public class CcServicePolicyConfigurationData implements CcServicePolicyConfiguration {


	private String policyId;
	private String name="";
	private String ruleSet = "";
	private Map<String,Integer>driverInstanceIdsMap;
	private List<PrimaryDriverDetail> driverList;
	private String driverScript;
	private ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributesList;
	private Map<Integer, AdditionalResponseAttributes> commandCodeResponseAttributesMap;
	private Boolean sessionManagementEnabled = false;

	private List<PluginEntryDetail> prePlugins;
	private List<PluginEntryDetail> postPlugins;

	private DefaultResponseBehaviorType responseBehaviorType = DefaultResponseBehaviorType.REJECT;
	private String responseBehaviorParameter = String.valueOf(RejectBehavior.DEFAULT_RESULT_CODE.code); 
	
	public CcServicePolicyConfigurationData() {
		this.driverInstanceIdsMap = new HashMap<String, Integer>();
		this.driverList = new ArrayList<PrimaryDriverDetail>();
		this.commandCodeResponseAttributesList = new ArrayList<CommandCodeResponseAttribute>();
		this.commandCodeResponseAttributesMap = new HashMap<Integer, AdditionalResponseAttributes>();
		
		this.prePlugins = new ArrayList<PluginEntryDetail>();
		this.postPlugins = new ArrayList<PluginEntryDetail>();
	}
	

	@Override
	@XmlTransient
	public Map<String, Integer> getDriverInstanceIdMap() {		
		return driverInstanceIdsMap;
	}
	
	public void  setDriverInstanceIdMap(Map<String, Integer> driverInstanceIdsMap) {		
		this.driverInstanceIdsMap = driverInstanceIdsMap;
	}

	@Override
	@XmlElement(name="name",type=String.class,defaultValue="")
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@XmlElement(name="ruleset",type=String.class,defaultValue="")
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleset) {
		this.ruleSet = ruleset;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- Cc Policy Configuration -- ");
		out.println();		
		out.println("    Name                          = " + name);
		out.println("    Policy Id  				   = " + policyId);
		out.println("    Rule Set   				   = " + ruleSet);
		out.println("    Default response behavior    = " + responseBehaviorType);
		out.println("    Response behavior Parameter  = " + responseBehaviorParameter);
		out.println("    ");
		out.close();
		return stringBuffer.toString();
	}

	@Override
	@XmlElement(name="id",type=String.class)
	public String getId() {
		return policyId;
	}
	public void setId(String id) {
		this.policyId = id;
	}
	
	@XmlElementWrapper(name="driver-group")
	@XmlElement(name="driver")
	public List<PrimaryDriverDetail> getDriverList() {
		return driverList;
	}

	public void setDriverList(List<PrimaryDriverDetail> driverList) {
		this.driverList = driverList;
	}

	@Override
	@XmlElement(name = "driver-script", type = String.class)
	public String getDriverScript() {
		return this.driverScript;
	}
	
	public void setDriverScript(String driverScript){
		this.driverScript = driverScript;
	}

	@Override
	@XmlElementWrapper(name = "command-code-wise-response-attribute-list")
	@XmlElement(name = "command-code-wise-response-attribute")
	public ArrayList<CommandCodeResponseAttribute> getCommandCodeResponseAttributesList() {
		return commandCodeResponseAttributesList;
	}

	public void setCommandCodeResponseAttributesList(
			ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributesList) {
		this.commandCodeResponseAttributesList = commandCodeResponseAttributesList;
	}
	
	@Override
	@XmlTransient
	public Map<Integer, AdditionalResponseAttributes> getCommandCodeResponseAttributesMap() {
		return commandCodeResponseAttributesMap;
	}

	public void setCommandCodeResponseAttributesMap(
			Map<Integer, AdditionalResponseAttributes> commandCodeResponseAttributesMap) {
		this.commandCodeResponseAttributesMap = commandCodeResponseAttributesMap;
	}
	
	@Override
	@XmlJavaTypeAdapter(value = BooleanAdapter.class)
	@XmlElement(name = "session-management")
	public Boolean isSessionManagementEnabled() {
		return sessionManagementEnabled;
	}
	
	public void setSessionManagementEnabled(Boolean sessionManagementEnabled) {
		this.sessionManagementEnabled  = sessionManagementEnabled;
	}
	
	@XmlElementWrapper(name = "in-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPrePlugins() {
		return prePlugins;
	}
	
	public void setPrePlugins(List<PluginEntryDetail> prePlugins) {
		this.prePlugins = prePlugins;
	}
	
	@XmlElementWrapper(name = "out-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPostPlugins() {
		return postPlugins;
	}
	
	public void setPostPlugins(List<PluginEntryDetail> postPlugins) {
		this.postPlugins = postPlugins;
	}

	@Override
	@XmlElement(name = "default-response-behavior-type", type = DefaultResponseBehaviorType.class)
	@XmlJavaTypeAdapter(value = DefaultResponseBehaviorTypeAdapter.class)
	public DefaultResponseBehaviorType getDefaultResponseBehaviorType() {
		return responseBehaviorType;
	}

	public void setResponseBehaviorType(
			DefaultResponseBehaviorType responseBehaviorType) {
		this.responseBehaviorType = responseBehaviorType;
	}
	
	@Override
	@XmlElement(name = "default-response-behavior-parameter")
	@Nullable
	public String getDefaultResponseBehaviorParameter() {
		return responseBehaviorParameter;
	}

	public void setResponseBehaviorParameter(String responseBehaviorParameter) {
		this.responseBehaviorParameter = responseBehaviorParameter;
	}

}