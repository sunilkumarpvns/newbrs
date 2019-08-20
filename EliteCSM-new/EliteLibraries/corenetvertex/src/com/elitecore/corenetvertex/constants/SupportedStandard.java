package com.elitecore.corenetvertex.constants;



public enum SupportedStandard {
	
	RELEASE_7("Release 7",1),
	RELEASE_8("Release 8",2),
	RELEASE_9("Release 9",3),
	CISCOSCE("Cisco SCE",4);
	
	private final String name;
	private final int id;
	
	private SupportedStandard(String name,int id) {
		this.name = name;
		this.id=id;
	}
	
	public static SupportedStandard fromValue(int id){
		if(RELEASE_9.id == id) {
			return RELEASE_9;
		} else if (RELEASE_8.id == id) {
			return RELEASE_8;
		} else if (CISCOSCE.id == id) {
			return CISCOSCE;
		} else if (RELEASE_7.id == id) {
			return RELEASE_7;
		}
		return null;
	}
		
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
