package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class PrimaryDriverRelData {
	private String driverInstanceId;
	private int weightage;
	
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	public int getWeightage() {
		return weightage;
	}

	public void setWeightage(int weightage) {
		this.weightage = weightage;
	}

	@Override
	public String toString() {
		return "----------------PrimaryDriverRelData------------------------\n  driverInstanceId = "
				+ driverInstanceId
				+ "\n  weightage = "
				+ weightage
				+ "\n----------------PrimaryDriverRelData-------------------------\n";
	}
}
