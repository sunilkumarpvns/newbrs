package com.elitecore.aaa.radius.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.DBAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.DBAccountingDriverDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverCDRDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverCDRTimestampDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverInterimCDRDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverMandatoryFieldDetails;
import com.elitecore.aaa.core.conf.impl.DatabaseDSConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.DbFiledMapping;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.DriverSelectQueryBuilder;
import com.elitecore.aaa.radius.drivers.conf.impl.RadDBAcctDriverConfigurationImpl;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

@XmlType(propOrder = {})
@XmlRootElement(name = "db-acct-drivers")
@ConfigurationProperties(moduleName ="RAD-DB-ACCT-CONFIGURABLE", readWith = DBReader.class, synchronizeKey = "", writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","services","acct","driver"},name = "db-acct-drivers")
public class RadDBAcctDriverConfigurable extends Configurable implements DriverConfigurable {
	
	private static final String MODULE = "RAD-DB-ACCT-CONFIGURABLE";
	
	private List<RadDBAcctDriverConfigurationImpl> radDBAcctDriverList;
	@XmlTransient
	private Map<String, DBAcctDriverConfiguration> radDBAcctDriverMap;
	
	public RadDBAcctDriverConfigurable() {
		this.radDBAcctDriverList = new ArrayList<RadDBAcctDriverConfigurationImpl>();
		this.radDBAcctDriverMap = new LinkedHashMap<String, DBAcctDriverConfiguration>();
	}

	@DBRead
	public void readDBAcctDriverConfiguration() throws Exception {

		Connection connection = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement psFields = null;
		ResultSet rsFields = null;		

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String querya =  new DriverSelectQueryBuilder(DriverTypes.RAD_OPENDB_ACCT_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();	

			List<RadDBAcctDriverConfigurationImpl> tempDBAcctDriverList = new ArrayList<RadDBAcctDriverConfigurationImpl>();
			Map<String, DBAcctDriverConfiguration> tempDBAcctDriverMap = new HashMap<String, DBAcctDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(querya);		
			
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {
				
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				
				ps = connection.prepareStatement(getQueryForDBAcctDriver());
				
				psFields = connection.prepareStatement(getQueryForFieldMapping());
						
				ps.setString(1,driverInstanceId);
				ps.setString(2,driverInstanceId);
				ps.setString(3,driverInstanceId);

				rs = ps.executeQuery();
				if(rs.next()){
					RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl = new RadDBAcctDriverConfigurationImpl();
					radDBAcctDriverConfigurationImpl.setDriverInstanceId(driverInstanceId);

					String dbId = rs.getString("OPENDBID");
					
					String driverName ="";
					if(rs.getString("DRIVER_NAME")!=null){
						driverName = rs.getString("DRIVER_NAME");
						radDBAcctDriverConfigurationImpl.setDriverName(driverName);
					}	
					
					psFields.setString(1, dbId);
					rsFields = psFields.executeQuery();
					List<DbFiledMapping> dbFiledMappingList = new ArrayList<DbFiledMapping>();
					
					while(rsFields.next()){
						DbFiledMapping dbFiledMapping = new DbFiledMapping();
						dbFiledMapping.setAttributeId(rsFields.getString("ATTRIBUTEIDS"));
						dbFiledMapping.setDataType(rsFields.getString("DATATYPE"));						
						dbFiledMapping.setDbField(rsFields.getString("DBFIELD")); 					
						dbFiledMapping.setDefaultValue(rsFields.getString("DEFAULTVALUE"));
						
						dbFiledMapping.setUseDictionaryValue(Boolean.parseBoolean(rsFields.getString("USE_DICTIONARY_VALUE")));
						
						dbFiledMappingList.add(dbFiledMapping);
					}
					radDBAcctDriverConfigurationImpl.setDbFiledMappingList(dbFiledMappingList);
					
					DBAccountingDriverDetails dbAccountingDriverDetails = radDBAcctDriverConfigurationImpl.getDbAccountingDriverDetails();
					
					DBAcctDriverCDRDetails dbAcctDriverCDRDetails = radDBAcctDriverConfigurationImpl.getDbAcctDriverCDRDetails();
					DBAcctDriverInterimCDRDetails dbAcctDriverInterimCDRDetails = radDBAcctDriverConfigurationImpl.getDbAcctDriverInterimCDRDetails();
					DBAcctDriverMandatoryFieldDetails dbAcctDriverMandatoryFieldDetails = radDBAcctDriverConfigurationImpl.getDbAcctDriverMandatoryFieldDetails();
					DBAcctDriverCDRTimestampDetails dbAcctDriverCDRTimestampDetails = radDBAcctDriverConfigurationImpl.getDbAcctDriverCDRTimestampDetails();
					
					dbAccountingDriverDetails.setDsType(rs.getString("DATASOURCE_TYPE"));				
					dbAcctDriverCDRDetails.setCdrTableName(rs.getString("CDR_TABLENAME"));
						
					dbAcctDriverInterimCDRDetails.setInterimTableName(rs.getString("INTERIM_TABLENAME"));
					
					
					if(rs.getString("STORE_ALL_CDR")!=null && rs.getString("STORE_ALL_CDR").trim().length()>0){
						dbAcctDriverCDRDetails.setIsStoreAllCDR(Boolean.parseBoolean(rs.getString("STORE_ALL_CDR").trim()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Store All CDR Parameter for Driver :"+driverName+" is not defined , using default value");
					}				
					if(rs.getString("STORE_STOP_REC")!=null && rs.getString("STORE_STOP_REC").trim().length()>0){
						dbAcctDriverCDRDetails.setIsStoreStopRec(Boolean.parseBoolean(rs.getString("STORE_STOP_REC").trim()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Store Stop Record Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("STORE_INTERIM_REC")!=null && rs.getString("STORE_INTERIM_REC").trim().length()>0){
						dbAcctDriverInterimCDRDetails.setIsStoreInterimRec(Boolean.parseBoolean(rs.getString("STORE_INTERIM_REC").trim())); 
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Store Interim Record Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("REMOVE_INTERIM_ON_STOP")!=null && rs.getString("REMOVE_INTERIM_ON_STOP").length()>0){
						dbAcctDriverInterimCDRDetails.setIsRemoveInterimOnStop(Boolean.parseBoolean(rs.getString("REMOVE_INTERIM_ON_STOP").trim()));	
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Remove Interim On Stop Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					
					if(rs.getString("DB_QUERY_TIMEOUT")!=null && rs.getString("DB_QUERY_TIMEOUT").trim().length()>0){
						dbAccountingDriverDetails.setDbQueryTimeout(Numbers.parseInt(rs.getString("DB_QUERY_TIMEOUT").trim(),radDBAcctDriverConfigurationImpl.getDbQueryTimeout()));
						
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Timeout Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("MAX_QUERY_TIMEOUT_COUNT")!=null && rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim().length()>0){
						dbAccountingDriverDetails.setMaxQueryTimeoutCount(Numbers.parseInt(rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim(), radDBAcctDriverConfigurationImpl.getMaxQueryTimeoutCount()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Maximum Query Timeout Count for Driver :"+driverName+" is not defined , using default value");
					}
					dbAccountingDriverDetails.setMultivalDelimeter(rs.getString("MULTIVAL_DELIMETER"));
					dbAcctDriverCDRTimestampDetails.setDbDateField(rs.getString("DB_DATE_FIELD"));
					if(rs.getString("ENABLED")!=null && rs.getString("ENABLED").trim().length()>0){
						dbAcctDriverCDRTimestampDetails.setIsEnabled(Boolean.parseBoolean(rs.getString("ENABLED").trim())); 
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Enabled Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("CDR_ID_DB_FIELD")!=null && rs.getString("CDR_ID_DB_FIELD").trim().length()>0){
						dbAcctDriverCDRDetails.setCdrIdDbField(rs.getString("CDR_ID_DB_FIELD"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "CDR Id Feild Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("CDR_ID_SEQ_NAME")!=null && rs.getString("CDR_ID_SEQ_NAME").trim().length()>0){
						dbAcctDriverCDRDetails.setCdrIdSeqName(rs.getString("CDR_ID_SEQ_NAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "CDR Id Sequence Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("INTERIM_CDR_ID_DB_FIELD")!=null && rs.getString("INTERIM_CDR_ID_DB_FIELD").trim().length()>0){
						dbAcctDriverInterimCDRDetails.setInterimCdrIdDbField(rs.getString("INTERIM_CDR_ID_DB_FIELD")); 
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Interim CDR Id Database Feild Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("INTERIM_CDR_ID_SEQ_NAME")!=null && rs.getString("INTERIM_CDR_ID_SEQ_NAME").trim().length()>0){
						dbAcctDriverInterimCDRDetails.setInterimCdrIdSeqName(rs.getString("INTERIM_CDR_ID_SEQ_NAME")); 
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Interim CDR Id Sequence Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("CALL_START_FIELD_NAME")!=null && rs.getString("CALL_START_FIELD_NAME").trim().length()>0){
						dbAcctDriverMandatoryFieldDetails.setCallStartFieldName(rs.getString("CALL_START_FIELD_NAME")); 
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Call Start Feild Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("CALL_END_FIELD_NAME")!=null && rs.getString("CALL_END_FIELD_NAME").trim().length()>0){
						dbAcctDriverMandatoryFieldDetails.setCallEndFieldName(rs.getString("CALL_END_FIELD_NAME")); 
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Call End Feild Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("CREATE_DATE_FIELD_NAME")!=null && rs.getString("CREATE_DATE_FIELD_NAME").trim().length()>0){
						dbAcctDriverMandatoryFieldDetails.setCreateDateFieldName(rs.getString("CREATE_DATE_FIELD_NAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Create Date Feild Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("LAST_MODIFIED_DATE_FIELD_NAME")!=null && rs.getString("LAST_MODIFIED_DATE_FIELD_NAME").trim().length()>0){
						dbAcctDriverMandatoryFieldDetails.setLastModifiedDateFieldName(rs.getString("LAST_MODIFIED_DATE_FIELD_NAME"));	
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Last Modified Date Feild Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					dbAccountingDriverDetails.setDsName(rs.getString("DATASOURCE_NAME"));
					tempDBAcctDriverList.add(radDBAcctDriverConfigurationImpl);
					tempDBAcctDriverMap.put(driverInstanceId, radDBAcctDriverConfigurationImpl);
				}
			}
			this.radDBAcctDriverList = tempDBAcctDriverList;
			this.radDBAcctDriverMap = tempDBAcctDriverMap;
		} finally{
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(rsFields);
			DBUtility.closeQuietly(psFields);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadDBAcctDriverConfiguration() throws Exception{

		int size = this.radDBAcctDriverList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl = radDBAcctDriverList.get(i);
			queryBuilder.append("'" + radDBAcctDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + radDBAcctDriverList.get(size - 1).getDriverInstanceId() + "')");
		String queryForReload = queryBuilder.toString();

		Connection conn = null;
		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			psForDriverInstanceId = conn.prepareStatement(queryForReload);
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while(rsForDriverInstanceId.next()){
				RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl = (RadDBAcctDriverConfigurationImpl) this.radDBAcctDriverMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));
				String driverInstanceId = radDBAcctDriverConfigurationImpl.getDriverInstanceId();

				ps = conn.prepareStatement(getReloadQueryForDBAcctDriver());
				String driverName = radDBAcctDriverConfigurationImpl.getDriverName();


				ps.setString(1,driverInstanceId);
				ps.setString(2,driverInstanceId);

				rs = ps.executeQuery();
				if(rs.next()){

					DBAccountingDriverDetails dbAccountingDriverDetails = radDBAcctDriverConfigurationImpl.getDbAccountingDriverDetails();

					if(rs.getString("DB_QUERY_TIMEOUT")!=null && rs.getString("DB_QUERY_TIMEOUT").trim().length()>0){
						dbAccountingDriverDetails.setDbQueryTimeout(Numbers.parseInt(rs.getString("DB_QUERY_TIMEOUT").trim(),radDBAcctDriverConfigurationImpl.getDbQueryTimeout()));

					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Timeout Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("MAX_QUERY_TIMEOUT_COUNT")!=null && rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim().length()>0){
						dbAccountingDriverDetails.setMaxQueryTimeoutCount(Numbers.parseInt(rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim(), radDBAcctDriverConfigurationImpl.getMaxQueryTimeoutCount()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Maximum Query Timeout Count for Driver :"+driverName+" is not defined , using default value");
					}

				}
			}
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(conn);
		}
	}
	
	@PostRead
	public void doProcessing() {
		
		for (RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl : radDBAcctDriverList) {

			setValidFieldMapping(radDBAcctDriverConfigurationImpl);

			radDBAcctDriverConfigurationImpl.setMandatorydbFieldList(
					getMandatorydbFieldList(radDBAcctDriverConfigurationImpl));
			
			generateQueries(radDBAcctDriverConfigurationImpl,
					getConfiguredFieldsString(radDBAcctDriverConfigurationImpl),
					getConfiguredValuesString(radDBAcctDriverConfigurationImpl));

			DBAccountingDriverDetails dbAccountingDriverDetails = radDBAcctDriverConfigurationImpl.
					getDbAccountingDriverDetails();
			
			int dbQueryTimeout = radDBAcctDriverConfigurationImpl.getDbQueryTimeout(); 
			if (dbQueryTimeout > 0 && dbQueryTimeout < 10) {
				dbQueryTimeout = 10;
				dbAccountingDriverDetails.setDbQueryTimeout(dbQueryTimeout);
			}
		}
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}

	private void setValidFieldMapping(RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl) {
		 
		List<DbFiledMapping>  fieldMappingList = radDBAcctDriverConfigurationImpl.getDbFiledMappingList();

		if (Collectionz.isNullOrEmpty(fieldMappingList)) {
			return;
		}

		List<DbFiledMapping> validMappingList = new ArrayList<DbFiledMapping>();

		for (DbFiledMapping dbFiledMapping : fieldMappingList) {

			List<String> attributeIDList = getAttributeIDList(dbFiledMapping.getAttributeId());
			dbFiledMapping.setAttributeIDList(attributeIDList);

			if (isValidDbFieldMapping(dbFiledMapping)) {
				validMappingList.add(dbFiledMapping);
			}
		}

		radDBAcctDriverConfigurationImpl.setDbFiledMappingList(validMappingList);
	}

	private void generateQueries(RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl,String strFields, String strValues) {
		
		String cdrTableName  = radDBAcctDriverConfigurationImpl.getCdrTableName();
		String interimTableName  = radDBAcctDriverConfigurationImpl.getInterimTableName();
		boolean storeAllCDR = radDBAcctDriverConfigurationImpl.getStoreAllCDR(); 
		
		if(cdrTableName!=null && cdrTableName.trim().length()>0 && storeAllCDR){
			radDBAcctDriverConfigurationImpl.setIsStoreStartRecordEnabled(true);
		}
		if(interimTableName != null && interimTableName.trim().length()>0){
			radDBAcctDriverConfigurationImpl.setIsStoreUpdateRecordEnabled(true);
		}
		
		String cdrIdDbField = radDBAcctDriverConfigurationImpl.getCdrIdDbField();
		String cdrIdSeqName = radDBAcctDriverConfigurationImpl.getCdrIdSeqName();
		
		String acctSessionIDColumn = "ACCT_SESSION_ID" ;
		
		DBDataSource datasource = (DBDataSource)getConfigurationContext().get(DatabaseDSConfigurationImpl.class).getDataSourceByName(radDBAcctDriverConfigurationImpl.getDsName());
		String connectionURL = datasource.getConnectionURL();
		DBVendors dbVendor = null;
		try {
			dbVendor = DBVendors.fromUrl(connectionURL);
		} catch (DatabaseTypeNotSupportedException e) {
			LogManager.getLogger().error(MODULE, "Database type not supported for Radius DB-acct-driver: " + e.getMessage());
		}
		
		DbFiledMapping acctSessionIDFieldMapping =  getFieldMapping(RadiusAttributeConstants.ACCT_SESSION_ID_STR,String.valueOf(RadiusAttributeConstants.ACCT_SESSION_ID),RadiusConstants.STANDARD_VENDOR_ID+":"+RadiusAttributeConstants.ACCT_SESSION_ID,radDBAcctDriverConfigurationImpl.getDbFiledMappingList());
		if(acctSessionIDFieldMapping!=null){
			acctSessionIDColumn = acctSessionIDFieldMapping.getDbField();
		}
		
		String acctUpdateDictionaryValue = null;
		DbFiledMapping acctStatusTypeColumnFieldMapping = getFieldMapping(RadiusAttributeConstants.ACCT_STATUS_TYPE_STR,String.valueOf(RadiusAttributeConstants.ACCT_STATUS_TYPE),RadiusConstants.STANDARD_VENDOR_ID+":"+RadiusAttributeConstants.ACCT_STATUS_TYPE,radDBAcctDriverConfigurationImpl.getDbFiledMappingList());
		if(acctStatusTypeColumnFieldMapping!=null){
			if(acctStatusTypeColumnFieldMapping.getUseDictionaryValue()){
				acctUpdateDictionaryValue = Dictionary.getInstance().getValueFromKey(RadiusAttributeConstants.ACCT_STATUS_TYPE, RadiusAttributeValuesConstants.INTERIM_UPDATE);
			}
			if(acctUpdateDictionaryValue==null){
				acctUpdateDictionaryValue = String.valueOf(RadiusAttributeValuesConstants.INTERIM_UPDATE);
			}
		}
		
		if(cdrIdDbField != null && cdrIdSeqName != null && dbVendor != null) {
			String strCDRInsertQuery = "INSERT INTO " + radDBAcctDriverConfigurationImpl.getCdrTableName() +   " ( " + cdrIdDbField + " , "+ strFields + " ) values ( " + dbVendor.getVendorSpecificSequenceSyntax(cdrIdSeqName) + ", " +strValues + " ) ";
			radDBAcctDriverConfigurationImpl.setCDRInsertQuery(strCDRInsertQuery);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) 
				LogManager.getLogger().debug(MODULE, "CDR_ID_FIELD_NAME or CDR_ID_SEQUENCE_NAME field not found");
		}
		String interimCdrIdDbField = radDBAcctDriverConfigurationImpl.getInterimCdrIdDbField();
		String interimCdrIdSeqName = radDBAcctDriverConfigurationImpl.getInterimCdrIdSeqName();
		
		if(interimCdrIdDbField != null && interimCdrIdSeqName != null && dbVendor != null) {
			String  strCDRInterimInsertQuery = "INSERT INTO " + radDBAcctDriverConfigurationImpl.getInterimTableName() +   " ( " + interimCdrIdDbField + " , "+ strFields + " ) values ( " + dbVendor.getVendorSpecificSequenceSyntax(interimCdrIdSeqName) + ", " + strValues + " ) ";
			String strCDRInterimUpdateQuery = "UPDATE " + radDBAcctDriverConfigurationImpl.getInterimTableName() + " SET " + getConfiguredStringForUpdate(strFields, strValues) + "WHERE " + acctSessionIDColumn + " = ?";
			
			String strCDRInterimDeleteQuery = null;
			if(acctStatusTypeColumnFieldMapping!=null){
				strCDRInterimDeleteQuery = "DELETE FROM " + radDBAcctDriverConfigurationImpl.getInterimTableName() + " WHERE " + acctSessionIDColumn + " = ?" +" AND "+acctStatusTypeColumnFieldMapping.getDbField()+"="+acctUpdateDictionaryValue;
			}else {
				strCDRInterimDeleteQuery = "DELETE FROM " + radDBAcctDriverConfigurationImpl.getInterimTableName() + " WHERE " + acctSessionIDColumn + " = ?";
			}
			
			radDBAcctDriverConfigurationImpl.setCDRInterimInsertQuery(strCDRInterimInsertQuery);
			radDBAcctDriverConfigurationImpl.setCDRInterimUpdateQuery(strCDRInterimUpdateQuery);
			radDBAcctDriverConfigurationImpl.setCDRInterimDeleteQuery(strCDRInterimDeleteQuery);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "INTRIEM_CDR_ID_FIELD_NAME or INTRIEM_CDR_ID_SEQUENCE_NAME field not found");
			
		}
	
		
	}

	private DbFiledMapping getFieldMapping(String attributeName,String attributeId,String vendorAndAttributeId,List<DbFiledMapping> fieldMappingList) {
		int size  = fieldMappingList.size();
		for(int i=0;i<size;i++){
			String attributeIdColumnValue = fieldMappingList.get(i).getAttributeId();
			if(attributeId.equalsIgnoreCase(attributeIdColumnValue) || attributeName.equalsIgnoreCase(attributeIdColumnValue) || vendorAndAttributeId.equalsIgnoreCase(attributeIdColumnValue)){
				return fieldMappingList.get(i);
			}
		}
		return null;
	}

	private String getConfiguredValuesString(RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl) {

		String strValues = " ";
		List<String> mandatorydbFieldList = radDBAcctDriverConfigurationImpl.getMandatorydbFieldList();
		for(int i=0;i<mandatorydbFieldList.size();i++) {
			if(!mandatorydbFieldList.isEmpty() && mandatorydbFieldList != null) {
				if(i == mandatorydbFieldList.size()-1)
					strValues = strValues + "? ";
				else
					strValues = strValues + "?, ";
			}
		}
		List<DbFiledMapping> dbFiledMappings =  radDBAcctDriverConfigurationImpl.getDbFiledMappingList();
		if(dbFiledMappings != null && !dbFiledMappings.isEmpty()){
			strValues = strValues + ", ";
			for(int i=0; i < dbFiledMappings.size() ; i++){
				if(i == dbFiledMappings.size()-1)
					strValues = strValues + "? ";
				else
					strValues = strValues + "?, ";
			}
			return strValues;
		}
		return strValues;
	
	}
	
	private String getConfiguredStringForUpdate(String strFields, String strValues) {
		String[] strFieldsArray = strFields.split(",");
		String[] strValuesArray = strValues.split(",");
		String strVal = "";
		for(int i=0;i < strFieldsArray.length;i++) {
			if(i == strFieldsArray.length-1)
				strVal = strVal + strFieldsArray[i] + " =" + strValuesArray[i];
			else
				strVal = strVal + strFieldsArray[i] + " =" + strValuesArray[i] + ",";
		}
		return strVal;
	}

	private String getConfiguredFieldsString(RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl) {
		String strConfigureFields = "";
		List<String> mandatorydbFieldList = radDBAcctDriverConfigurationImpl.getMandatorydbFieldList();
		if(!mandatorydbFieldList.isEmpty()) {
			for(int i=0;i < mandatorydbFieldList.size();i++) {
				if(i == mandatorydbFieldList.size()-1)
					strConfigureFields = strConfigureFields + mandatorydbFieldList.get(i);
				else
					strConfigureFields = strConfigureFields + mandatorydbFieldList.get(i) + ", ";
			}
		}
		List<DbFiledMapping> dbFiledMappings =  radDBAcctDriverConfigurationImpl.getDbFiledMappingList();		
		if(dbFiledMappings != null && !dbFiledMappings.isEmpty()){
			strConfigureFields = strConfigureFields + ", ";
			for(int i=0; i < dbFiledMappings.size() ; i++){
				if(i == dbFiledMappings.size()-1)
					strConfigureFields = strConfigureFields +  dbFiledMappings.get(i).getDbField();
				else
					strConfigureFields = strConfigureFields + dbFiledMappings.get(i).getDbField() +  ", ";
			}
			return strConfigureFields;
		} 	
		return strConfigureFields;
	}

	private String getQueryForDBAcctDriver(){
		return "SELECT A.*,B.NAME AS DATASOURCE_NAME,B.STATUSCHECKDURATION,B.TIMEOUT,C.NAME AS DRIVER_NAME FROM TBLMOPENDBACCTDRIVER A, TBLMDATABASEDS B,TBLMDRIVERINSTANCE C WHERE A.driverinstanceid=? " +
		"AND B.databasedsid = (Select databasedsid from TBLMOPENDBACCTDRIVER where driverinstanceid=?) AND C.DRIVERINSTANCEID=?";
	}
	private String getReloadQueryForDBAcctDriver(){
		return "SELECT A.DB_QUERY_TIMEOUT,A.MAX_QUERY_TIMEOUT_COUNT,B.STATUSCHECKDURATION FROM TBLMOPENDBACCTDRIVER A, TBLMDATABASEDS B WHERE A.driverinstanceid=? " +
		"AND B.databasedsid = (Select databasedsid from TBLMOPENDBACCTDRIVER where driverinstanceid=?)";
	}
	
	private String getQueryForFieldMapping(){
		return "SELECT * FROM tblmopendbacctfieldmap WHERE OPENDBID=?";
	}

	private List<String> getMandatorydbFieldList(RadDBAcctDriverConfigurationImpl radDBAcctDriverConfigurationImpl) {
		List<String> mandatorydbFieldList = new ArrayList<String>();
		if(radDBAcctDriverConfigurationImpl.getCallStartFieldName() != null && radDBAcctDriverConfigurationImpl.getCallStartFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(radDBAcctDriverConfigurationImpl.getCallStartFieldName());
		}

		if(radDBAcctDriverConfigurationImpl.getCallEndFieldName() != null && radDBAcctDriverConfigurationImpl.getCallEndFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(radDBAcctDriverConfigurationImpl.getCallEndFieldName());
		}

		if(radDBAcctDriverConfigurationImpl.getCreateDateFieldName() != null && radDBAcctDriverConfigurationImpl.getCreateDateFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(radDBAcctDriverConfigurationImpl.getCreateDateFieldName());
		}

		if(radDBAcctDriverConfigurationImpl.getLastModifiedDateFieldName() != null && radDBAcctDriverConfigurationImpl.getLastModifiedDateFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(radDBAcctDriverConfigurationImpl.getLastModifiedDateFieldName());
		}

		if(radDBAcctDriverConfigurationImpl.getEnebled() && radDBAcctDriverConfigurationImpl.getDbDateField()!= null && radDBAcctDriverConfigurationImpl.getDbDateField().trim().length() > 0) {
			mandatorydbFieldList.add(radDBAcctDriverConfigurationImpl.getDbDateField());
		}
		return mandatorydbFieldList;
	}
	private boolean isValidDbFieldMapping(DbFiledMapping dbFiledMapping) {
		if(dbFiledMapping.getAttributeIDList() != null && dbFiledMapping.getAttributeIDList().size() > 0 && 
				dbFiledMapping.getDataType() != null && dbFiledMapping.getDataType().trim().length() > 0 && 
						dbFiledMapping.getDbField() != null && dbFiledMapping.getDbField().trim().length()>0)
			return true;
		else {
			return false;
		}
	}
	
	@Override
	@XmlElement(name="db-acct-driver")
	public List<RadDBAcctDriverConfigurationImpl> getDriverConfigurationList() {
		return radDBAcctDriverList;
	}

	private List<String> getAttributeIDList(String strAttributeId) {

		List<String> attributeList = new ArrayList<String>();
		if(strAttributeId!=null && strAttributeId.trim().length()>0){
			StringTokenizer attributeTokens = new StringTokenizer(strAttributeId,",;");
			while(attributeTokens.hasMoreTokens()) {
				String attributeId = attributeTokens.nextToken();
				try {
					AttributeId tempAttributeId = Dictionary.getInstance().getAttributeId(attributeId);
					attributeList.add(tempAttributeId.getStringID());
				} catch (InvalidAttributeIdException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Attribute id: "+ attributeId+ " not found in dictionary.");
					}
				}
			}
		}
		return attributeList;
	}
}
