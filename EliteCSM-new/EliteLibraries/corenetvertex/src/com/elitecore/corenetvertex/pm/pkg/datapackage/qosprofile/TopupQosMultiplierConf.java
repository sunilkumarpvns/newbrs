package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TopupQosMultiplierConf implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Double sessionMultiplier;
	private Map<String, Double> serviceToMultiplier;
	
	public TopupQosMultiplierConf(Double sessionMultiplier) {
		
		this.sessionMultiplier = sessionMultiplier;
		this.serviceToMultiplier = new HashMap<String, Double>();
	}
	
	public TopupQosMultiplierConf addServiceMultiplier(String serviceId, double multiplierValue) {
		this.serviceToMultiplier.put(serviceId, multiplierValue);
		return this;
	}

	public Double getSessionMultiplier() {
		return sessionMultiplier;
	}

	public Map<String, Double> getServiceToMultiplier() {
		return serviceToMultiplier;
	}
	
}
