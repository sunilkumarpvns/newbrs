package com.elitecore.elitesm.ws.rest.utility;

public class SubscriberQuery {

	private String columnName;
	private String columnValue;
	private boolean caseSensitivity;

	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public boolean isCaseSensitivity() {
		return caseSensitivity;
	}
	public void setCaseSensitivity(boolean caseSensitivity) {
		this.caseSensitivity = caseSensitivity;
	}
	public String getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
}
