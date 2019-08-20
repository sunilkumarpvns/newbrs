package com.elitecore.core.driverx.cdr.data;

public class DBFieldMapping {

	private String dbField;
	private String key;
	private int dataType;
	private String defaultValue;

	public DBFieldMapping(String dbField, String key, int dataType, String defaultVaule) {
		this.dbField = dbField;
		this.key = key;
		this.dataType = dataType;
		this.defaultValue = defaultVaule;
	}

	public String getDBField() {
		return dbField;
	}

	public String getKey() {
		return key;
	}

	public int getDataType() {
		return dataType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
