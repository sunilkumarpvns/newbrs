package com.elitecore.commons.kpi.data;


import java.util.ArrayList;
import java.util.List;

public class TableData {
	
	private List<Row> rows;
	private String tableName;
	private Object clientBasedTableFromAccessMethod;
	
	public TableData(String tableName) {
		this.tableName = tableName;
		this.rows = new ArrayList<Row>();
	}
	
	public void addRow(Row row) {
		this.rows.add(row);
	}
	
	public void addRow(List<Row> rows) {
		this.rows.addAll(rows);
	}

	public List<Row> getRows() {
		return rows;
	}

	public String getTableName() {
		return tableName;
	}

	public void setClientBasedTableFromAccessMethod(Object clientBasedTableFromAccessMethod) {
		this.clientBasedTableFromAccessMethod = clientBasedTableFromAccessMethod;
	}
	
	public Object getClientBasedTableFromAccessMethod() {
		return clientBasedTableFromAccessMethod;
	}
	
	public boolean contains(Row row) {
		return rows.contains(row);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof TableData)) {
			return false;
		}
		
		if(tableName == null || tableName.trim().isEmpty()) {
			return false;
		}
		
		return this.tableName.equalsIgnoreCase(((TableData)obj).getTableName());
	}
	
	@Override
	public int hashCode() {
		return tableName.hashCode();
	}
}
