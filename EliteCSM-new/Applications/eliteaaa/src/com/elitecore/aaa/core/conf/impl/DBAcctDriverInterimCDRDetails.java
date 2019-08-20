package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DBAcctDriverInterimCDRDetails {
	
	private String interimTableName;
	private String interimCdrIdDbField="interimcdrid";
	private String interimCdrIdSeqName="seq_tblradiusinterimcdr";
	private boolean storeInterimRec=true;
	private boolean removeInterimOnStop;
	
	public DBAcctDriverInterimCDRDetails() {
		// TODO Auto-generated constructor stub
	}

	public void setInterimTableName(String interimTableName) {
		this.interimTableName = interimTableName;
	}

	public void setInterimCdrIdDbField(String interimCdrIdDbField) {
		this.interimCdrIdDbField = interimCdrIdDbField;
	}

	public void setInterimCdrIdSeqName(String interimCdrIdSeqName) {
		this.interimCdrIdSeqName = interimCdrIdSeqName;
	}

	public void setIsStoreInterimRec(boolean storeInterimRec) {
		this.storeInterimRec = storeInterimRec;
	}

	public void setIsRemoveInterimOnStop(boolean removeInterimOnStop) {
		this.removeInterimOnStop = removeInterimOnStop;
	}
	
	@XmlElement(name="table-name",type=String.class)
	public String getInterimTableName() {
		return interimTableName;
	}

	@XmlElement(name="identity-field",type=String.class,defaultValue="interimcdrid")
	public String getInterimCdrIdDbField() {
		return interimCdrIdDbField;
	}

	@XmlElement(name="sequence-name",type=String.class,defaultValue="seq_tblradiusinterimcdr")
	public String getInterimCdrIdSeqName() {
		return interimCdrIdSeqName;
	}

	@XmlElement(name="store-interim-record",type=boolean.class)
	public boolean getIsStoreInterimRec() {
		return storeInterimRec;
	}

	@XmlElement(name="remove-interim-on-stop",type=boolean.class)
	public boolean getIsRemoveInterimOnStop() {
		return removeInterimOnStop;
	}
}
