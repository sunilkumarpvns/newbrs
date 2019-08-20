/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author tejasmudgal
 *  Last Modified October 1, 2008
 */
package com.elitecore.classicrating.datamanager.rating;

/**
 * Base POJO class for PrefixGroupDefinition Model.
 * 
 */
public class PrefixGroupDefinitionData {

	private String prefixGroupName;

	private long prefixGroupNumber;

	public PrefixGroupDefinitionData() {

	}

	public String getPrefixGroupName() {
		return prefixGroupName;
	}

	public void setPrefixGroupName(String prefixGroupName) {
		this.prefixGroupName = prefixGroupName;
	}

	public long getPrefixGroupNumber() {
		return prefixGroupNumber;
	}

	public void setPrefixGroupNumber(long prefixGroupNumber) {
		this.prefixGroupNumber = prefixGroupNumber;
	}

}
