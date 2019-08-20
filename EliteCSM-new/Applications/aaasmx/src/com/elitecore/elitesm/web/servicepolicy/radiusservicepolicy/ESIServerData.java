package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;


public class ESIServerData {
	private int oNumber;
	private String esiId;
	private String loadFactor;
	
	public int getoNumber() {
		return oNumber;
	}
	public void setoNumber(int oNumber) {
		this.oNumber = oNumber;
	}
	public String getEsiId() {
		return esiId;
	}
	public void setEsiId(String esiId) {
		this.esiId = esiId;
	}
	public String getLoadFactor() {
		return loadFactor;
	}
	public void setLoadFactor(String loadFactor) {
		this.loadFactor = loadFactor;
	}

	@Override
	public String toString() {
		return "----------------ESIServerData------------------------\n  oNumber = "
				+ oNumber
				+ "\n  esiId = "
				+ esiId
				+ "\n  loadFactor = "
				+ loadFactor
				+ "\n----------------ESIServerData-------------------------\n";
	}
}
