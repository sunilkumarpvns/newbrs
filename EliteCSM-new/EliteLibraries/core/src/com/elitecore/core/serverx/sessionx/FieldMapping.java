package com.elitecore.core.serverx.sessionx;

public interface FieldMapping {
	int STRING_TYPE = 0;
	int TIMESTAMP_TYPE = 1;
	String getPropertyName();
	int getType();
	String getColumnName();
	String getDefaultValue();
	String getField();
}
