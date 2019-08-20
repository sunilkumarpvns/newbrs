package com.elitecore.commons.kpi.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class KpiUtil {
	private static final String MODULE = "KPI_UTIL";
	private static final String METHODNAME_STARTS_WITH_GETENTRIES = "getEntries";
	
	public static Object[] getEntries(Object clientBasedTableFromAccessMethod) {
		
		if(clientBasedTableFromAccessMethod == null) {
			throw new IllegalArgumentException("client based table from access method can not be null");
		}
		
		Object[] entries = new Object[0];

		Method[] methods = clientBasedTableFromAccessMethod.getClass().getMethods();
		for (Method method : methods) {
			
			try {
				if(METHODNAME_STARTS_WITH_GETENTRIES.equals(method.getName())) {
					entries = (Object[]) method.invoke(clientBasedTableFromAccessMethod);
					break;
				}

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
		}
		return entries;
	}
	
}
