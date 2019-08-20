package com.elitecore.aaa.core.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class CacheParameters{
	
	private String primaryKeyColumn = "ID";
	private String sequenceName = "SEQ_RADIUSCUSTOMER";
	
	public CacheParameters(){
		//required by Jaxb.
	}
	
	@XmlElement(name = "primary-key-column",type = String.class)
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}
	
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	@XmlElement(name = "sequence-name",type = String.class)
	public String getSequenceName() {
		return sequenceName;
	}
}