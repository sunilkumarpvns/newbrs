package com.elitecore.aaa.rm.policies.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.rm.policies.conf.RMChargingPolicyConfiguration;

@XmlType(propOrder={})
public class RMChargingPolicyConfigurationImpl implements RMChargingPolicyConfiguration{
	private String policyId;
	private String policyName;
	private String ruleSet;
	private Map<String, Integer> driverInstanceIdsMap;
	
	private List<PrimaryDriverDetail> driverList;
	
	//configuration for scripts
	private String driverScript;

	public RMChargingPolicyConfigurationImpl() {
		this.driverList = new ArrayList<PrimaryDriverDetail>();
		this.driverInstanceIdsMap = new HashMap<String, Integer>();
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	public void setDriverScript(String driverScript){
		this.driverScript = driverScript;
	}
	
	@Override
	@XmlElement(name = "id",type = String.class)
	public String getPolicyId() {
		return policyId;
	}

	@Override
	@XmlElement(name = "name",type = String.class)
	public String getPolicyName() {
		return policyName;
	}
	
	@Override
	@XmlTransient
	public Map<String,Integer> getDriverInstanceIdsMap() {		
		return driverInstanceIdsMap;
	}
	
	public void setDriverInstanceIdsMap(Map<String,Integer> driverInstanceIdsMap ) {		
		this.driverInstanceIdsMap=driverInstanceIdsMap;
	}

	@Override
	@XmlElement(name = "ruleset",type = String.class)
	public String getRuleSet() {
		return ruleSet;
	}
	
	@XmlElementWrapper(name = "drivergroup")
	@XmlElement(name = "driver")
	public List<PrimaryDriverDetail> getDriverList() {
		return driverList;
	}

	public void setDriverList(List<PrimaryDriverDetail> primaryList) {
		this.driverList = primaryList;
	}	
	
	@Override
	@XmlElement(name = "driver-script", type = String.class)
	public String getDriverScript() {
		return this.driverScript;
	}
}
