package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DBAcctDriverCDRDetails {
	
	private String cdrTableName;
	private String cdrIdDbField="cdrid";
	private String cdrIdSeqName="seq_tblradiuscdr";
	private boolean storeAllCDR=false;
	private boolean storeStopRec=true;
	
	public DBAcctDriverCDRDetails() {
		// TODO Auto-generated constructor stub
	}

	@XmlElement(name="table-name",type=String.class)
	public String getCdrTableName() {
		return cdrTableName;
	}

	public void setCdrTableName(String cdrTableName) {
		this.cdrTableName = cdrTableName;
	}

	public void setCdrIdDbField(String cdrIdDbField) {
		this.cdrIdDbField = cdrIdDbField;
	}

	public void setCdrIdSeqName(String cdrIdSeqName) {
		this.cdrIdSeqName = cdrIdSeqName;
	}

	public void setIsStoreAllCDR(boolean storeAllCDR) {
		this.storeAllCDR = storeAllCDR;
	}

	public void setIsStoreStopRec(boolean storeStopRec) {
		this.storeStopRec = storeStopRec;
	}

	@XmlElement(name="identity-field",type=String.class)
	public String getCdrIdDbField() {
		return cdrIdDbField;
	}

	@XmlElement(name="sequence-name",type=String.class)
	public String getCdrIdSeqName() {
		return cdrIdSeqName;
	}

	@XmlElement(name="store-all-cdr",type=boolean.class)
	public boolean getIsStoreAllCDR() {
		return storeAllCDR;
	}

	@XmlElement(name="store-stop-record",type=boolean.class)
	public boolean getIsStoreStopRec() {
		return storeStopRec;
	}

}
