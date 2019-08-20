package com.elitecore.netvertex.core.roaming;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MCCMNCGroup implements ToStringable{
	
	private  String name;
	private String brand;
	private String description;
	private Map<String, MCCMNCEntry> mccMncEntries;

	public MCCMNCGroup(String name, String brand, String description, Map<String, MCCMNCEntry> mccmncEntities) {
		this.name = name;
		this.brand = brand;
		this.description = description;
		this.mccMncEntries = mccmncEntities;
	}

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}
	
	public String getDescription() {
		return description;
	}

	public Set<Entry<String,MCCMNCEntry>> getMCCMNCEntities() {
		return mccMncEntries.entrySet();
	}
	
	public MCCMNCEntry getMCCMNCEntities(String mccmnc) {
		return mccMncEntries.get(mccmnc);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mccMncEntries == null) ? 0 : mccMncEntries.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		try {
			MCCMNCGroup other = (MCCMNCGroup) obj;
			if (mccMncEntries == null) {
				return other.mccMncEntries == null;
			}

			return mccMncEntries.equals(other.mccMncEntries);
		} catch (ClassCastException ex) {
			LogManager.ignoreTrace(ex);
			return false;
		}

	}
	
	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- MCC-MNC Group -- ");
		builder.newline();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Brand", brand)
				.append("Description", description)
				.appendChildObject("MCC-MNC Entities", mccMncEntries.values());
	}
}
