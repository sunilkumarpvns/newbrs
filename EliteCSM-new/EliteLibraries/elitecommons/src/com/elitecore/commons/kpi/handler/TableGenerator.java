package com.elitecore.commons.kpi.handler;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.commons.kpi.data.Row;
import com.elitecore.commons.kpi.data.TableData;
import com.elitecore.commons.kpi.util.KpiUtil;
import com.elitecore.commons.kpi.util.ReflectionUtil;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.sun.management.snmp.agent.SnmpMib;

public class TableGenerator {
	
	private static final String MODULE = "TABLE_GENERATOR";
	private static final String TABLE_NAME_WITH_ZERO_COLUMNS = "";
	
	public List<TableData> generateTables(SnmpMib snmpMib) {
		List<TableData> tables = new ArrayList<TableData>();
		/*
		 * collecting methods which starts with create in annotated class
		 */
		List<Method> methodListStartWithCreate = ReflectionUtil.getDeclaredMethodsAnnotatedWith(snmpMib.getClass(), Table.class);
		
		for (Method methodStartWithCreate : methodListStartWithCreate) {
			try {
				/*
				 * invoking create method 
				 */
				Object serviceLevelTableFromCreateMethod = methodStartWithCreate.invoke(
																						snmpMib, 
																						new String("groupName") , 
																						new String("groupOID") , 
																						snmpMib.getSnmpAdaptorName(), 
																						snmpMib.getMBeanServer());

				if(serviceLevelTableFromCreateMethod == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Improper create method: " + methodStartWithCreate.getName() + ", so skipping table generation for this method");
					continue;
				}
				
				TableData table = generateServiceLevelTable(methodStartWithCreate, serviceLevelTableFromCreateMethod);
				if(!table.getRows().isEmpty())
					tables.add(table);

				/*
				 * Fetching methods which starts with access in service level mbean class using annotation kpi.annotation.Table
				 */
				List<Method> clientBasedMethods = ReflectionUtil.getAllMethodsAnnotatedWith(serviceLevelTableFromCreateMethod.getClass(), Table.class);

				for (Method clientBasedMethod : clientBasedMethods) {
					table = generateClientBasedTables(clientBasedMethod, serviceLevelTableFromCreateMethod);
					if(table != null && !table.getRows().isEmpty())
						tables.add(table);
				}

			} catch (IllegalArgumentException e) {
				LogManager.getLogger().trace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Illegal argument passed while invoking method: " + methodStartWithCreate.getName() + "Reason: " + e.getMessage());
			} catch (IllegalAccessException e) {
				LogManager.getLogger().trace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Problem in accessing method: " + methodStartWithCreate.getName() + "Reason: " + e.getMessage());
			} catch (InvocationTargetException e) {
				LogManager.getLogger().trace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Problem in invoking method: " + methodStartWithCreate.getName() + "Reason: " + e.getMessage());
			}

		}
		return tables;
	}

	private TableData generateServiceLevelTable(Method methodStartWithCreate, Object serviceLevelTableFromCreateMethod) {
		TableData serviceLevelTable = null;
		Table serviceLevelTableAnnotation = methodStartWithCreate.getAnnotation(Table.class);

		if(TABLE_NAME_WITH_ZERO_COLUMNS.equals(serviceLevelTableAnnotation.name().trim())) {
			return new TableData(TABLE_NAME_WITH_ZERO_COLUMNS);
		}

		/*
		 * For service level table there can be only one row possible
		 */
		Row singleServiceLevelRow = generateRow(serviceLevelTableFromCreateMethod);

		serviceLevelTable = new TableData(serviceLevelTableAnnotation.name().trim());
		serviceLevelTable.addRow(singleServiceLevelRow);
		return serviceLevelTable;

	}

	private TableData generateClientBasedTables(Method clientBasedMethod, Object serviceLevelTableFromCreateMethod) {
		TableData clientBasedTable = null;
		try {
			Table clientBasedTableAnnotation = clientBasedMethod.getAnnotation(Table.class);
			/*
			 * invoking methods that starts with access
			 */
			Object clientBasedTableFromAccessMethod = clientBasedMethod.invoke(serviceLevelTableFromCreateMethod);

			List<Row> rows = generateRowsForAllClientEntries(clientBasedTableFromAccessMethod);

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, rows.size() + " client entries found for: " + clientBasedTableAnnotation.name());
			}
			
			clientBasedTable = new TableData(clientBasedTableAnnotation.name());
			clientBasedTable.setClientBasedTableFromAccessMethod(clientBasedTableFromAccessMethod);
			clientBasedTable.addRow(rows);
		} catch (IllegalArgumentException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Illegal argument passed while invoking method: " + clientBasedMethod.getName() + "Reason: " + e.getMessage());
		} catch (IllegalAccessException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Problem in accessing method: " + clientBasedMethod.getName() + "Reason: " + e.getMessage());
		} catch (InvocationTargetException e) {
			LogManager.getLogger().trace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Problem in invoking method: " + clientBasedMethod.getName() + "Reason: " + e.getMessage());
		}

		return clientBasedTable;
	}

	private List<Row> generateRowsForAllClientEntries(Object clientBasedTableFromAccessMethod) {
		List<Row> rows = new ArrayList<Row>();
		
		Object[] clientBasedEntries = KpiUtil.getEntries(clientBasedTableFromAccessMethod);
		for(int entryIndex = 0 ; entryIndex < clientBasedEntries.length ; entryIndex++) {
			Row row = generateRow(clientBasedEntries[entryIndex]);
			rows.add(row);
		}
		
		return rows;
	}
	
	private Row generateRow(Object annotedClass) {
		Row row = new Row(annotedClass);
		row.generateColumns();
		return row;
	}
}
