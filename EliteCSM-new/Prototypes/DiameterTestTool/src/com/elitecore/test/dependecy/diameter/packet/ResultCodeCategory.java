package com.elitecore.test.dependecy.diameter.packet;


public enum ResultCodeCategory {

	RC1XXX(1000, "Informational", 		"1XXX"),
	RC2XXX(2000, "Success", 			"2XXX"),
	RC3XXX(3000, "Protocol Errors", 	"3XXX"),
	RC4XXX(4000, "Transient Errors", 	"4XXX"),
	RC5XXX(5000, "Permanent Failures", 	"5XXX"),
	RC6XXX(6000, "Unknown", 			"6XXX");
	
	public final int value;
	public final String category;
	public final String categoryType;
	private ResultCodeCategory(int val, String category, String categoryType){
		this.categoryType = categoryType;
		this.value = val;
		this.category = category;
	}
	
	public static boolean isSessionRemovableCategory(ResultCodeCategory category){
		switch(category){
			case RC3XXX:
			case RC5XXX:
				return true;
		}
		return false;
	}
	public static ResultCodeCategory getResultCodeCategory(long resultCode){
		if(resultCode > 999 && resultCode < 2000 )
			return ResultCodeCategory.RC1XXX;
		else if(resultCode > 1999 && resultCode < 3000)
			return ResultCodeCategory.RC2XXX;
		else if(resultCode > 2999 && resultCode < 4000)
			return ResultCodeCategory.RC3XXX;
		else if(resultCode > 3999 && resultCode < 5000)
			return ResultCodeCategory.RC4XXX;
		else if(resultCode > 4999 && resultCode < 6000)
			return ResultCodeCategory.RC5XXX;
		else 
			return ResultCodeCategory.RC6XXX;
	}
	
}
