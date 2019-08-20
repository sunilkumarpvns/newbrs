package com.elitecore.corenetvertex.constants;

public enum CommonStatusValues {

	ENABLE(1,true,"Enable","true","True"),
	DISABLE(0,false,"Disable","false","False");

	private int id;
	private boolean booleanValue;
	private String stringName;
	private String stringNameBoolean;
	private String displayBooleanValue;

	CommonStatusValues(int id, boolean booleanValue, String stringName, String stringNameBoolean, String displayBooleanValue){
		this.id = id;
		this.booleanValue = booleanValue;
		this.stringName = stringName;
		this.stringNameBoolean=stringNameBoolean;
		this.displayBooleanValue = displayBooleanValue;
	}

	public static CommonStatusValues fromValue(int id){
		if(ENABLE.id == id){
			return ENABLE;
		}else if(DISABLE.id == id){
			return DISABLE;
		}else{
			return null;
		}
	}
	public static CommonStatusValues fromBooleanValue(Boolean booleanValue){
		if(ENABLE.booleanValue == booleanValue){
			return ENABLE;
		}else if(DISABLE.booleanValue == booleanValue){
			return DISABLE;
		}else{
			return null;
		}
	}

	public static CommonStatusValues fromStringBooleanValue(String booleanValue){
		if(ENABLE.stringNameBoolean.equals(booleanValue)){
			return ENABLE;
		}else if(DISABLE.stringNameBoolean.equals(booleanValue)){
			return DISABLE;
		}else{
			return null;
		}
	}
	public int getId() {
		return id;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public String getStringName() {
		return stringName;
	}

	public String getStringNameBoolean() {
		return stringNameBoolean;
	}

	public String getDisplayBooleanValue() {
		return displayBooleanValue;
	}
}
