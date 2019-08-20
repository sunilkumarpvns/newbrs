package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.DBAcctDriverConfiguration;

public abstract class DbAcctDriverConfigurationImpl implements DBAcctDriverConfiguration{

	private String driverInstanceId;
	private String driverName="";

	private DBAccountingDriverDetails dbAccountingDriverDetails;
	private DBAcctDriverCDRDetails dbAcctDriverCDRDetails;
	private DBAcctDriverInterimCDRDetails dbAcctDriverInterimCDRDetails;
	private DBAcctDriverMandatoryFieldDetails dbAcctDriverMandatoryFieldDetails;
	private DBAcctDriverCDRTimestampDetails dbAcctDriverCDRTimestampDetails;


	private List<DbFiledMapping> dbFiledMappingList;	
	private List<String> mandatorydbFieldList;
	private String strCDRInsertQuery;
	private String strCDRInterimInsertQuery;
	private String strCDRInterimUpdateQuery;
	private String strCDRInterimDeleteQuery;

	private boolean isStoreStartRecordEnabled;
	private boolean isStoreUpdateRecordEnabled;
	
	public DbAcctDriverConfigurationImpl() {
		this.dbFiledMappingList = new ArrayList<DbFiledMapping>();
		this.dbAccountingDriverDetails = new DBAccountingDriverDetails();
		this.dbAcctDriverCDRDetails = new DBAcctDriverCDRDetails();
		this.dbAcctDriverInterimCDRDetails = new DBAcctDriverInterimCDRDetails();
		this.dbAcctDriverMandatoryFieldDetails = new DBAcctDriverMandatoryFieldDetails();
		this.dbAcctDriverCDRTimestampDetails = new DBAcctDriverCDRTimestampDetails();
	}

	
	@XmlElement(name="id",type=String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	@XmlTransient
	public String getDsType() {
		return dbAccountingDriverDetails.getDsType();
	}

	@XmlTransient
	public String getCdrTableName() {
		return dbAcctDriverCDRDetails.getCdrTableName();
	}

	@XmlTransient
	public String getInterimTableName() {
		return dbAcctDriverInterimCDRDetails.getInterimTableName();
	}

	@Override
	public boolean getStoreAllCDR() {
		return dbAcctDriverCDRDetails.getIsStoreAllCDR();
	}
	
	@XmlTransient
	public boolean getStoreStopRec() {
		return dbAcctDriverCDRDetails.getIsStoreStopRec();
	}

	@XmlTransient
	public boolean getStoreInterimRec() {
		return dbAcctDriverInterimCDRDetails.getIsStoreInterimRec();
	}

	@XmlTransient
	public boolean getRemoveInterimOnStop() {
		return dbAcctDriverInterimCDRDetails.getIsRemoveInterimOnStop();
	}


	@XmlTransient
	public int getDbQueryTimeout() {
		return dbAccountingDriverDetails.getDbQueryTimeout();
	}

	@XmlTransient
	public int getMaxQueryTimeoutCount() {
		return dbAccountingDriverDetails.getMaxQueryTimeoutCount();
	}

	@XmlTransient
	public String getMultivalDelimeter() {
		return dbAccountingDriverDetails.getMultivalDelimeter();
	}

	@XmlTransient
	public String getDbDateField() {
		return dbAcctDriverCDRTimestampDetails.getDbDateField();
	}

	@XmlTransient
	public boolean getEnebled() {
		return dbAcctDriverCDRTimestampDetails.getIsEnabled();
	}

	@XmlTransient
	public String getCdrIdDbField() {
		return dbAcctDriverCDRDetails.getCdrIdDbField();
	}

	@XmlTransient
	public String getCdrIdSeqName() {
		return dbAcctDriverCDRDetails.getCdrIdSeqName();
	}

	@XmlTransient
	public String getInterimCdrIdDbField() {
		return dbAcctDriverInterimCDRDetails.getInterimCdrIdDbField();
	}

	@XmlTransient
	public String getInterimCdrIdSeqName() {
		return dbAcctDriverInterimCDRDetails.getInterimCdrIdSeqName();
	}

	@XmlTransient
	public String getCallStartFieldName() {
		return dbAcctDriverMandatoryFieldDetails.getCallStartFieldName();
	}

	@XmlTransient
	public String getCallEndFieldName() {
		return dbAcctDriverMandatoryFieldDetails.getCallEndFieldName();
	}

	@XmlTransient
	public String getCreateDateFieldName() {
		return dbAcctDriverMandatoryFieldDetails.getCreateDateFieldName();
	}

	@XmlTransient
	public String getLastModifiedDateFieldName() {
		return dbAcctDriverMandatoryFieldDetails.getLastModifiedDateFieldName();
	}
	
	@XmlTransient
	public String getIntrimCDRAcctSessionId() {
		return "acct_session_id";
	}

	@XmlElementWrapper(name="db-acct-driver-mapping-details")
	@XmlElement(name="mapping")
	public List<DbFiledMapping> getDbFiledMappingList() {
		return dbFiledMappingList;
	}
	public void setDbFiledMappingList(List<DbFiledMapping> dbFiledMappingList) {
		this.dbFiledMappingList = dbFiledMappingList;
	}

	@XmlTransient
	public List<String> getMandatorydbFieldList() {
		return mandatorydbFieldList;
	}
	
	public void setMandatorydbFieldList(List<String> mandatorydbFieldList) {
		this.mandatorydbFieldList = mandatorydbFieldList;
	}

	@XmlTransient
	public String getDsName(){
		return this.dbAccountingDriverDetails.getDsName();
	}
	
	@XmlTransient
	public String getCDRInsertQuery() {		
		return this.strCDRInsertQuery;
	}

	@XmlTransient
	public String getCDRInterimInsertQuery() {		
		return this.strCDRInterimInsertQuery;
	}

	@XmlTransient
	public String getCDRInterimUpdateQuery() {
		return this.strCDRInterimUpdateQuery;
	}

	@XmlTransient
	public String getCDRInterimDeleteQuery() {
		return this.strCDRInterimDeleteQuery;
	}
	
	@XmlTransient
	public boolean isStoreStartRecordEnabled() {
		return this.isStoreStartRecordEnabled;
	}

	@XmlTransient
	public boolean isStoreUpdateRecordEnabled() {
		return this.isStoreUpdateRecordEnabled;
	}
	
	public void setIsStoreStartRecordEnabled(boolean isStoreStartRecordEnabled) {		
		this.isStoreStartRecordEnabled = isStoreStartRecordEnabled;
	}
	
	public void setIsStoreUpdateRecordEnabled(boolean isStoreUpdateRecordEnabled) {		
		this.isStoreUpdateRecordEnabled = isStoreUpdateRecordEnabled;
	}
	
	public void setCDRInsertQuery(String strCDRInsertQuery) {		
		this.strCDRInsertQuery = strCDRInsertQuery;
	}

	public void setCDRInterimInsertQuery(String strCDRInterimInsertQuery) {		
		this.strCDRInterimInsertQuery = strCDRInterimInsertQuery;
	}

	public void setCDRInterimUpdateQuery(String strCDRInterimUpdateQuery) {
		this.strCDRInterimUpdateQuery = strCDRInterimUpdateQuery;
	}

	public void setCDRInterimDeleteQuery(String strCDRInterimDeleteQuery) {
		this.strCDRInterimDeleteQuery = strCDRInterimDeleteQuery;
	}

	@Override
	@XmlElement(name="driver-name",type=String.class)
	public String getDriverName() {		
		return driverName;
	}
	
	public void setDriverName(String driverName) {		
		this.driverName = driverName;
	}
	
	@XmlElement(name="db-accounting-driver-details")
	public DBAccountingDriverDetails getDbAccountingDriverDetails() {
		return dbAccountingDriverDetails;
	}


	public void setDbAccountingDriverDetails(
			DBAccountingDriverDetails dbAccountingDriverDetails) {
		this.dbAccountingDriverDetails = dbAccountingDriverDetails;
	}

	@XmlElement(name="cdr-details")
	public DBAcctDriverCDRDetails getDbAcctDriverCDRDetails() {
		return dbAcctDriverCDRDetails;
	}


	public void setDbAcctDriverCDRDetails(
			DBAcctDriverCDRDetails dbAcctDriverCDRDetails) {
		this.dbAcctDriverCDRDetails = dbAcctDriverCDRDetails;
	}
	
	@XmlElement(name="interim-details")
	public DBAcctDriverInterimCDRDetails getDbAcctDriverInterimCDRDetails() {
		return dbAcctDriverInterimCDRDetails;
	}


	public void setDbAcctDriverInterimCDRDetails(
			DBAcctDriverInterimCDRDetails dbAcctDriverInterimCDRDetails) {
		this.dbAcctDriverInterimCDRDetails = dbAcctDriverInterimCDRDetails;
	}

	@XmlElement(name="mandatory-field-details")
	public DBAcctDriverMandatoryFieldDetails getDbAcctDriverMandatoryFieldDetails() {
		return dbAcctDriverMandatoryFieldDetails;
	}


	public void setDbAcctDriverMandatoryFieldDetails(
			DBAcctDriverMandatoryFieldDetails dbAcctDriverMandatoryFieldDetails) {
		this.dbAcctDriverMandatoryFieldDetails = dbAcctDriverMandatoryFieldDetails;
	}


	@XmlElement(name="cdr-timestamp-details")
	public DBAcctDriverCDRTimestampDetails getDbAcctDriverCDRTimestampDetails() {
		return dbAcctDriverCDRTimestampDetails;
	}


	public void setDbAcctDriverCDRTimestampDetails(
			DBAcctDriverCDRTimestampDetails dbAcctDriverCDRTimestampDetails) {
		this.dbAcctDriverCDRTimestampDetails = dbAcctDriverCDRTimestampDetails;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		 this.driverInstanceId =  driverInstanceId;
	}

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- Acct Open Db Driver Configuration -- ");
		out.println();
		out.println("     MANDATORY_DB_FIELDS_MAP -> ");
		out.println();
		out.println("--------------------------------");
		out.println("    LAST_MODIFIED_DATE = " + getLastModifiedDateFieldName());
		out.println("    CALL_START_FIELD_NAME = " + getCallStartFieldName());
		out.println("    INTERIM_CDR_ID_SEQUENCE_NAME = " + getInterimCdrIdSeqName());
		out.println("    INTERIM_CDR_ID_FIELD_NAME = " + getInterimCdrIdDbField());
		out.println("    CDR_ID_SEQUENCE_NAME = " + getCdrIdSeqName());
		out.println("    CDR_ID_FIELD_NAME = " + getCdrIdDbField());
		out.println("    CALL_END_FIELD_NAME = " + getCallEndFieldName());
		out.println("    CREATE_DATE_FIELD_NAME " + getCreateDateFieldName());
		out.println("--------------------------------");
		out.println();
		out.println("--------------------------------");
		out.println("    INTERIM_TABLENAME = " + getInterimTableName());
		out.println("    DATASOURCE_SCANTIME = " );
		out.println("    MULTIVALUE_DELIMETER = " + getMultivalDelimeter());
		out.println();
		out.println("    CDR_TABLENAME = " + getCdrTableName());
		out.println("    DB_QUERY_TIMEOUT = " + getDbQueryTimeout());
		out.println("    STORE_INTERIM_RECORD = " + getStoreInterimRec());
		out.println("    STORE_ALL_CDR = " + getStoreAllCDR());
		out.println("    STORE_STOP_RECORD = " + getStoreStopRec());
		out.println("    DATASOURCE_TYPE = " + getDsType());
		out.println("--------------------------------");		
		out.println("    ");

		out.close();
		return stringBuffer.toString();
	}
}
