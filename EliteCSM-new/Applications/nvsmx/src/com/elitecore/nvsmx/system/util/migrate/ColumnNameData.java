package com.elitecore.nvsmx.system.util.migrate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


public class ColumnNameData {
	
	private int index;
	private String columnName;
	
	public ColumnNameData() {}
	
	
	@XmlAttribute(name="index")
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	@XmlValue
	public String getName() {
		return columnName;
	}
	
	public void setName(String columnName) {
		this.columnName = columnName;
	}
}
