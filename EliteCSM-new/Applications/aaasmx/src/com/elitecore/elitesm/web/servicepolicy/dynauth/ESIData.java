package com.elitecore.elitesm.web.servicepolicy.dynauth;


public class ESIData{
	private String esiId;
	private Long loadFactor;
	
	public String getEsiId() {
		return esiId;
	}
	public void setEsiId(String esiId) {
		this.esiId = esiId;
	}
	public Long getLoadFactor() {
		return loadFactor;
	}
	public void setLoadFactor(Long loadFactor) {
		this.loadFactor = loadFactor;
	}
	
	@Override
	public String toString() {
		return "ESIData [esiId=" + esiId + ", loadFactor=" + loadFactor + "]";
	}
	
}
