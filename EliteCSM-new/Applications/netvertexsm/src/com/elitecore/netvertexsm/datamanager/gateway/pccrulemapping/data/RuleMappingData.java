package com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;

public class RuleMappingData {

	private long ruleMappingId;
	private String name;
	private String description;
	private List<PCCRuleMappingData> pccRuleMappingList;
	private Set<PCCRuleMappingData> pccRuleMappingSet;
	public long getRuleMappingId() {
		return ruleMappingId;
	}
	public void setRuleMappingId(long ruleMappingId) {
		this.ruleMappingId = ruleMappingId;
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
	public List<PCCRuleMappingData> getPccRuleMappingList() {
		return pccRuleMappingList;
	}
	public void setPccRuleMappingList(List<PCCRuleMappingData> pccRuleMappingList) {
		this.pccRuleMappingList = pccRuleMappingList;
	}
	public Set<PCCRuleMappingData> getPccRuleMappingSet() {
		return pccRuleMappingSet;
	}
	public void setPccRuleMappingSet(Set<PCCRuleMappingData> pccRuleMappingSet) {
		this.pccRuleMappingSet = pccRuleMappingSet;
	}
	
}
