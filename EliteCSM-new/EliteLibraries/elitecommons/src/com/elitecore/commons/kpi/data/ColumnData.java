package com.elitecore.commons.kpi.data;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class ColumnData {
	
	private static final String MODULE = "COLUMN_DATA";
	private String columnName;
	private int type;
	private Method method;
	private Object annotedClass;
	
	public ColumnData(String columnName, Object annotedClass, Method method, int type) {
		this.columnName = columnName;
		this.method = method;
		this.type = type;
		this.annotedClass = annotedClass;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public Object getCurrentValue() {
		Object returnedObject = null; 
		try {
			returnedObject = method.invoke(annotedClass);
		
		} catch (IllegalArgumentException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Illegal argument passed while invoking method: " + method.getName() + "Reason: " + e.getMessage());
		} catch (IllegalAccessException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Problem in accessing method: " + method.getName() + "Reason: " + e.getMessage());
		} catch (InvocationTargetException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Problem in invoking method: " + method.getName() + "Reason: " + e.getMessage());
		}
		return returnedObject;
	}
	
	@Override
	public int hashCode() {
		return columnName.hashCode();
	}
}
