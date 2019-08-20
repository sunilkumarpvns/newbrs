package com.elitecore.commons.kpi.data;

public class Value {
	
	private String columnName;
	private Object val;
	private int type;
	
	public Value(Object val, String columnName, int type) {
		this.val = val;
		this.columnName = columnName;
		this.type = type;
	}

	public Object getValue() {
		return val;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public int getType() {
		return type;
	}
}
