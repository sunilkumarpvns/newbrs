package com.elitecore.elitesm.web.dashboard.widget.json;

import java.util.ArrayList;
import java.util.List;


public class TableSchema {
	private List<String> columnHeaders = new ArrayList<String>(); 
	private List<String> rowGroupHeaders = new ArrayList<String>();
	private List<TableData> tableData  = new ArrayList<TableData>();
	private List<List<String>> firstRow = new ArrayList<List<String>>();
	
	public List<String> getColumnHeaders() {
		return columnHeaders;
	}
	public void setColumnHeaders(List<String> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}
	public List<String> getRowGroupHeaders() {
		return rowGroupHeaders;
	}
	public void setRowGroupHeaders(List<String> rowGroupHeaders) {
		this.rowGroupHeaders = rowGroupHeaders;
	}
	public List<TableData> getTableData() {
		return tableData;
	}
	public void setTableData(List<TableData> tableData) {
		this.tableData = tableData;
	}
	public List<List<String>> getFirstRow() {
		return firstRow;
	}
	public void setFirstRow(List<List<String>> firstRow) {
		this.firstRow = firstRow;
	}
	
	public TableSchema addColumnHeader(String columnHeader) {
		columnHeaders.add(columnHeader);
		return this;
	}
	
	public TableSchema addRowGroupHeaders(String rowHeader) {
		rowGroupHeaders.add(rowHeader);
		return this;
	}
	
	public TableSchema addTableData(TableData tableData) {
		this.tableData.add(tableData);
		return this;
	}
	
	public TableSchema addFirstRowData(List<String> firstRowList) {
		this.firstRow.add(firstRowList);
		return this;
	}
	
	public List<String> idList() {
		List<String> idList = new ArrayList<String>();
		for(TableData td : tableData) {
			String rowID = "";
			for(String data : td.getName()) {
				rowID += data + "_";
			}
			
			String colID = rowID;
			for(List<String> th : firstRow) {
				for(String data : th) {
					colID += data + "_";
				}
				idList.add(colID.substring(0,colID.length()-1));
				colID = rowID;
			}
			
		}
		return idList;
	}
	
}
