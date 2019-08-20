/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Raghu G
 *  Last Modified October 4, 2008
 */

/*
 * PackageDefinitionData.java
 * 
 * This class is an POJO class used to represent an package definition data.
 * It contains declaration for all attributes with appropriate set and get methods.
 * 
 */

package com.elitecore.classicrating.datamanager.packages;

import com.elitecore.classicrating.datamanager.rating.PrefixGroupDefinitionData;


public class PackageDefinitionData {

	private int packageID;
	
	private String packageName;
	private String packageType;
	private String unitType;
	
	private double minCap;
	private double maxCap;
	
	// added for slab definition
	
	private SlabDefinitionData slabDefinitionData;
	private PrefixGroupDefinitionData prefixGroupDefinitionData;
	
	public PackageDefinitionData() {

	}
	public String toString(){
		String returnString = new String("Package Definition Data = [ "+
										 "Max Cap = " + maxCap + ", " +
										 "Min Cap = " + minCap + ", " +
										 "Package Name = " + packageName + ", " +
										 "Package Type = " + packageType + ", " +
										 "Unit Type = " + unitType + ", ");
		if(slabDefinitionData != null)
			returnString += slabDefinitionData + ", ";
		else
			returnString += "Slab Definition Data = [ ], ";
		
		if(prefixGroupDefinitionData != null)
			returnString += prefixGroupDefinitionData + " ]";
		else
			returnString += "Prefix Group Definition Data = [ ] ]";
		
		return returnString;
	}
	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public void setMinCap(double minCap) {
		this.minCap = minCap;
	}

	public void setMaxCap(double maxCap) {
		this.maxCap = maxCap;
	}

	public int getPackageID() {
		return packageID;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getPackageType() {
		return packageType;
	}

	public String getUnitType() {
		return unitType;
	}

	public double getMinCap() {
		return minCap;
	}

	public double getMaxCap() {
		return maxCap;
	}
	
	public void setSlabDefinitionData(SlabDefinitionData slabDefinitionData){
		this.slabDefinitionData = slabDefinitionData;
	}
	
	public SlabDefinitionData getSlabDefinitionData(){
		return slabDefinitionData;
	}
	
	public void setPrefixGroupDefinitionData(PrefixGroupDefinitionData prefixGroupDefinitionData){
		this.prefixGroupDefinitionData = prefixGroupDefinitionData;
	}
	
	public PrefixGroupDefinitionData getPrefixGroupDefinitionData(){
		return prefixGroupDefinitionData;
	}
}
