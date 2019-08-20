package com.elitecore.elitesm.web.driver.diameter.forms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctFeildMapData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateDiameterDBAcctDriverForm extends BaseWebForm{
	
	private Long openDbAcctId;	
	private String databaseId;
	private String datasourceType;
	//private Long datasourceScantime = 2L;
	private String cdrTablename = "tbldiametercdr";
	private String interimTablename="tbldiameterinterimcdr";
	private String storeStopRec;
	private String storeInterimRec;
	private String removeInterimOnStop;
	private String storeTunnelStartRec;
	private String storeTunnelStopRec;
	private String removeTunnelStopRec;
	private String storeTunnelLinkStartRec;
	private String storeTunnelLinkStopRec;
	private String removeTunnelLinkStopRec;
	private String storeTunnelRejectRec;
	private String storeTunnelLinkRejectRec;
	private Long dbQueryTimeout = 2L;
	private Long maxQueryTimeoutCount = 200L;
	private String multivalDelimeter =";";
	private String dbDateField = "PARAM_DATE1";
	private String enabled;
	private String cdrIdDbField = "cdrid";
	private String cdrIdSeqName = "seq_tbldiametercdr";
	private String interimCdrIdDbField = "interimcdrid";
	private String interimCdrIdSeqName = "seq_tbldiameterinterimcdr";
	private String callStartFieldName = "call_start";
	private String callEndFieldName = "call_end";
	private String createDateFieldName = "create_date";
	private String lastModifiedDateFieldName = "last_modified_date";
	private HashSet dbAcctFeildMapSet;
	private List databaseDSList;
	
	//driver instance related properties 
	
	private String action;
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private int itemIndex;
	
	// driver feild mapping 
	
	private Long dbAcctFeildMapId;	
	private String attributeids;
	private String dbfield;
	private String datatype;
	private String defaultvalue;
	private String useDictionaryValue;
	
	public Long getOpenDbAcctId() {
		return openDbAcctId;
	}
	public void setOpenDbAcctId(Long openDbAcctId) {
		this.openDbAcctId = openDbAcctId;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	public String getDatasourceType() {
		return datasourceType;
	}
	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}
	/*public Long getDatasourceScantime() {
		return datasourceScantime;
	}
	public void setDatasourceScantime(Long datasourceScantime) {
		this.datasourceScantime = datasourceScantime;
	}*/
	public String getCdrTablename() {
		return cdrTablename;
	}
	public void setCdrTablename(String cdrTablename) {
		this.cdrTablename = cdrTablename;
	}
	public String getInterimTablename() {
		return interimTablename;
	}
	public void setInterimTablename(String interimTablename) {
		this.interimTablename = interimTablename;
	}
	public String getStoreStopRec() {
		return storeStopRec;
	}
	public void setStoreStopRec(String storeStopRec) {
		this.storeStopRec = storeStopRec;
	}
	public String getStoreInterimRec() {
		return storeInterimRec;
	}
	public void setStoreInterimRec(String storeInterimRec) {
		this.storeInterimRec = storeInterimRec;
	}
	public String getRemoveInterimOnStop() {
		return removeInterimOnStop;
	}
	public void setRemoveInterimOnStop(String removeInterimOnStop) {
		this.removeInterimOnStop = removeInterimOnStop;
	}
	public String getStoreTunnelStartRec() {
		return storeTunnelStartRec;
	}
	public void setStoreTunnelStartRec(String storeTunnelStartRec) {
		this.storeTunnelStartRec = storeTunnelStartRec;
	}
	public String getStoreTunnelStopRec() {
		return storeTunnelStopRec;
	}
	public void setStoreTunnelStopRec(String storeTunnelStopRec) {
		this.storeTunnelStopRec = storeTunnelStopRec;
	}
	public String getRemoveTunnelStopRec() {
		return removeTunnelStopRec;
	}
	public void setRemoveTunnelStopRec(String removeTunnelStopRec) {
		this.removeTunnelStopRec = removeTunnelStopRec;
	}
	public String getStoreTunnelLinkStartRec() {
		return storeTunnelLinkStartRec;
	}
	public void setStoreTunnelLinkStartRec(String storeTunnelLinkStartRec) {
		this.storeTunnelLinkStartRec = storeTunnelLinkStartRec;
	}
	public String getStoreTunnelLinkStopRec() {
		return storeTunnelLinkStopRec;
	}
	public void setStoreTunnelLinkStopRec(String storeTunnelLinkStopRec) {
		this.storeTunnelLinkStopRec = storeTunnelLinkStopRec;
	}
	public String getRemoveTunnelLinkStopRec() {
		return removeTunnelLinkStopRec;
	}
	public void setRemoveTunnelLinkStopRec(String removeTunnelLinkStopRec) {
		this.removeTunnelLinkStopRec = removeTunnelLinkStopRec;
	}
	public String getStoreTunnelRejectRec() {
		return storeTunnelRejectRec;
	}
	public void setStoreTunnelRejectRec(String storeTunnelRejectRec) {
		this.storeTunnelRejectRec = storeTunnelRejectRec;
	}
	public String getStoreTunnelLinkRejectRec() {
		return storeTunnelLinkRejectRec;
	}
	public void setStoreTunnelLinkRejectRec(String storeTunnelLinkRejectRec) {
		this.storeTunnelLinkRejectRec = storeTunnelLinkRejectRec;
	}
	public Long getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(Long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	public String getMultivalDelimeter() {
		return multivalDelimeter;
	}
	public void setMultivalDelimeter(String multivalDelimeter) {
		this.multivalDelimeter = multivalDelimeter;
	}
	public String getDbDateField() {
		return dbDateField;
	}
	public void setDbDateField(String dbDateField) {
		this.dbDateField = dbDateField;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public String getCdrIdDbField() {
		return cdrIdDbField;
	}
	public void setCdrIdDbField(String cdrIdDbField) {
		this.cdrIdDbField = cdrIdDbField;
	}
	public String getCdrIdSeqName() {
		return cdrIdSeqName;
	}
	public void setCdrIdSeqName(String cdrIdSeqName) {
		this.cdrIdSeqName = cdrIdSeqName;
	}
	public String getInterimCdrIdDbField() {
		return interimCdrIdDbField;
	}
	public void setInterimCdrIdDbField(String interimCdrIdDbField) {
		this.interimCdrIdDbField = interimCdrIdDbField;
	}
	public String getInterimCdrIdSeqName() {
		return interimCdrIdSeqName;
	}
	public void setInterimCdrIdSeqName(String interimCdrIdSeqName) {
		this.interimCdrIdSeqName = interimCdrIdSeqName;
	}
	public String getCallStartFieldName() {
		return callStartFieldName;
	}
	public void setCallStartFieldName(String callStartFieldName) {
		this.callStartFieldName = callStartFieldName;
	}
	public String getCallEndFieldName() {
		return callEndFieldName;
	}
	public void setCallEndFieldName(String callEndFieldName) {
		this.callEndFieldName = callEndFieldName;
	}
	public String getCreateDateFieldName() {
		return createDateFieldName;
	}
	public void setCreateDateFieldName(String createDateFieldName) {
		this.createDateFieldName = createDateFieldName;
	}
	public String getLastModifiedDateFieldName() {
		return lastModifiedDateFieldName;
	}
	public void setLastModifiedDateFieldName(String lastModifiedDateFieldName) {
		this.lastModifiedDateFieldName = lastModifiedDateFieldName;
	}
	public HashSet getDbAcctFeildMapSet() {
		return dbAcctFeildMapSet;
	}
	public void setDbAcctFeildMapSet(HashSet dbAcctFeildMapSet) {
		this.dbAcctFeildMapSet = dbAcctFeildMapSet;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public List getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public Long getDbAcctFeildMapId() {
		return dbAcctFeildMapId;
	}
	public void setDbAcctFeildMapId(Long dbAcctFeildMapId) {
		this.dbAcctFeildMapId = dbAcctFeildMapId;
	}

	public String getAttributeids() {
		return attributeids;
	}
	public void setAttributeids(String attributeids) {
		this.attributeids = attributeids;
	}
	public String getDbfield() {
		return dbfield;
	}
	public void setDbfield(String dbfield) {
		this.dbfield = dbfield;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	public String getUseDictionaryValue() {
		return useDictionaryValue;
	}
	public void setUseDictionaryValue(String useDictionaryValue) {
		this.useDictionaryValue = useDictionaryValue;
	}
	
	public List<DBAcctFeildMapData> getDefaultmapping(){
		List<DBAcctFeildMapData> defaultMappingList = new ArrayList<DBAcctFeildMapData>();
		String[] dbFiledList ={"USER_NAME","NAS_IP_ADDRESS","NAS_PORT","FRAMED_IP_ADDRESS","CLASS","CALLING_STATION_ID"
				,"NAS_IDENTIFIER","ACCT_DELAY_TIME","SESSION_ID","ACCT_SESSION_TIME","ACCT_MULTI_SESSION_ID"
				,"EVENT_TIMESTAMP","NAS_PORT_ID","ORIGIN_HOST","DESTINATION_REALM","ORIGIN_REALM"
				,"ACCT_INPUT_OCTETS","ACCT_OUTPUT_OCTETS","ACCT_INPUT_PACKETS","ACCT_OUTPUT_PACKETS","ACCT_RECORD_TYPE"};	
		String[] attridList = {"0:1","0:4","0:5","0:8","0:25","0:31","0:32","0:41","0:44","0:46","0:50","0:55","0:87","0:264","0:283","0:296"
				,"0:363","0:364","0:365","0:366","0:480"};
		for(int index = 0 ; index < dbFiledList.length ; index++){
			DBAcctFeildMapData dbAcctFeildMapData = new DBAcctFeildMapData();
			dbAcctFeildMapData.setDbfield(dbFiledList[index]);
			dbAcctFeildMapData.setAttributeids(attridList[index]);
			dbAcctFeildMapData.setUseDictionaryValue("false");
			defaultMappingList.add(dbAcctFeildMapData);
		}
		return defaultMappingList;
	}

}
