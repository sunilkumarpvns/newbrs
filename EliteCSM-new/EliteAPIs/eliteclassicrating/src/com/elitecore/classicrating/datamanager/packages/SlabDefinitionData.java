/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author tejasmudgal
 *  Last Modified October 1, 2008
 */

package com.elitecore.classicrating.datamanager.packages;

/**
 * Base POJO class for SlabDefinition Model.
 * 
 * @author tejasmudgal
 * 
 */
public class SlabDefinitionData {

	private int packageId;

	private int unit1;

	private int unit2;

	private int unit3;

	public SlabDefinitionData() {

	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public int getUnit1() {
		return unit1;
	}

	public void setUnit1(int unit1) {
		this.unit1 = unit1;
	}

	public int getUnit2() {
		return unit2;
	}

	public void setUnit2(int unit2) {
		this.unit2 = unit2;
	}

	public int getUnit3() {
		return unit3;
	}

	public void setUnit3(int unit3) {
		this.unit3 = unit3;
	}

}
