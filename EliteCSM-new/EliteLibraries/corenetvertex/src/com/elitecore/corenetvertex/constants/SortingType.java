package com.elitecore.corenetvertex.constants;

/**
 * This enum will define sorting type for file location. It can be Ascending or descending
 * @author Ajay Pandey
 *
 */

public enum SortingType {
	
	ASCENDING("Ascending"),
	DESCENDING("Descending");
	
    private  String value;
	
	private SortingType(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}
	
}
