package com.elitecore.core.util.csv;

public class CSVRecordData {
	
	private int lineNumber;
	private String[] record;
	
	public CSVRecordData(int lineNumber, String[] record){
		this.lineNumber = lineNumber;
		this.record= record;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String[] getRecord() {
		return record;
	}
	public void setRecord(String[] record) {
		this.record = record;
	}
	
}
