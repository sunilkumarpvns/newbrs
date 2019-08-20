package com.elitecore.netvertexsm.datamanager.core.util;

import java.util.ArrayList;
import java.util.List;

public class CSVData {
	
	private String[] fieldNames;
	private List<CSVRecordData> records = new ArrayList<CSVRecordData>();
	
	public String[] getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}
	public List<CSVRecordData> getRecords() {
		return records;
	}
	
}
