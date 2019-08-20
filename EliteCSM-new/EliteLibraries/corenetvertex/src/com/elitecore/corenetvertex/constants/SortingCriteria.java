package com.elitecore.corenetvertex.constants;

/**
 * This enum will define sorting criteria for file location. It can be File Name Based, Last Modified or descending
 * @author Ajay Pandey
 *
 */

public enum SortingCriteria {
	
	FILE_NAME_BASED("File Name Based"),
	LAST_MODIFIED("Last Modified"),
	CREATE("Create");
	
	private String value;
	
	private SortingCriteria(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}
	

}
