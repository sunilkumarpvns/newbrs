package com.elitecore.netvertex.core.roaming;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class MCCMNCEntry implements ToStringable {
	private String mccmncID;
	private String mcc;
	private String mnc;
	private String operator;
	private String brand;
	private String technology;
	private String networkName;
	private String country;
	private String mccmnc;
	public MCCMNCEntry(String mccmncID, String mcc, String mnc, String operator,
			String networkName, String country, String brand, String technology) {
		this.mccmncID = mccmncID;
		this.mcc = mcc;
		this.mnc = mnc;
		this.operator = operator;
		this.networkName = networkName;
		this.country = country;
		this.brand = brand;
		this.technology = technology;
		this.mccmnc = mcc + "" + mnc;
	}
	public String getBrand() {
		return brand;
	}
	public String getTechnology() {
		return technology;
	}
	
	public String getMCCMNCID() {
		return mccmncID;
	}
	
	public String getMCC() {
		return mcc;
	}
	public String getMNC() {
		return mnc;
	}
	public String getOperator() {
		return operator;
	}
	public String getNetworkName() {
		return networkName;
	}
	public String getCountry() {
		return country;
	}
	
	public String getMCCMNC(){
		return mccmnc;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof MCCMNCEntry)) {
			return false;
		}

		MCCMNCEntry mccmncEntry = (MCCMNCEntry) obj;

		return this.mccmnc.equals(mccmncEntry.mccmnc);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mccmnc == null) ? 0 : mccmnc.hashCode());
		return result;
	}

	
	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- MCC-MNC Entity -- ");
		builder.newline();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.incrementIndentation()
				.append("MCCMNCID", mccmncID)
				.append("MCC", mcc)
				.append("MNC", mnc)
				.append("Brand", brand)
				.append("Operator", operator)
				.append("Technology", technology)
				.append("NetworkName", networkName)
				.append("Country", country);
	}
}
