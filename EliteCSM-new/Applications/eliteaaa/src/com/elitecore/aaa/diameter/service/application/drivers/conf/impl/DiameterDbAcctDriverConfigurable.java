package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.core.conf.impl.DBAccountingDriverDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverCDRDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverCDRTimestampDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverInterimCDRDetails;
import com.elitecore.aaa.core.conf.impl.DBAcctDriverMandatoryFieldDetails;
import com.elitecore.aaa.core.conf.impl.DatabaseDSConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.DbFiledMapping;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.TGPPServerPolicyConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
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
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData;

@XmlType(propOrder={})
@XmlRootElement(name = "db-acct-drivers")
@ConfigurationProperties(moduleName = "NAS_DB_ACCT_DRIVER", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "db-acct-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterDbAcctDriverConfigurable extends Configurable{

	private List<NasDbAcctDriverConfigurationImpl> nasDbAcctDriverConfigurationImplDetailsList;
	@XmlTransient
	private Map<String, DBAcctDriverConfiguration> nasDBAcctDriverMap;
	private static final String MODULE="NAS_DB_ACCT_DRIVER";
	private static final String QUERY_FOR_FIELD_MAPPING = "SELECT * FROM tblmopendbacctfieldmap WHERE OPENDBID=?";

	private static final int DEFAULT_QUERY_TIMEOUT = 100;

	public DiameterDbAcctDriverConfigurable(){
		this.nasDbAcctDriverConfigurationImplDetailsList =  new ArrayList<NasDbAcctDriverConfigurationImpl>();
		this.nasDBAcctDriverMap = new LinkedHashMap<String, DBAcctDriverConfiguration>();
	}

	@XmlElement(name = "db-acct-driver")
	public List<NasDbAcctDriverConfigurationImpl> getDbAcctdriverDetails() {
		return nasDbAcctDriverConfigurationImplDetailsList;
	}

	public void  setDbAcctdriverDetails(List<NasDbAcctDriverConfigurationImpl> NasDbAcctDriverConfigurationImplDetails) {
		this.nasDbAcctDriverConfigurationImplDetailsList = NasDbAcctDriverConfigurationImplDetails;
	}

	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement psFields = null;
		ResultSet rsFields = null;

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		try {


			connection = EliteAAADBConnectionManager.getInstance().getConnection();

			AAAServerConfigurable serverConfigurable = getConfigurationContext().get(AAAServerConfigurable.class);

			String querya;
			if (isTGPPServerEnabled(serverConfigurable)) {
				TGPPServerPolicyConfigurable policyConfigurable = getConfigurationContext().get(TGPPServerPolicyConfigurable.class);

				if (policyConfigurable.getSelectedDriverIds().isEmpty() == false) {
					querya = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.NAS_OPENDB_ACCT_DRIVER.value +"' AND ("
							+ "DRIVERINSTANCEID IN ("
							+ Strings.join(",", policyConfigurable.getSelectedDriverIds(), Strings.WITHIN_SINGLE_QUOTES) + ") "
							+ "OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
							"(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyacctdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
							"(select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
							"(select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
							"(select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")))";
				} else {
					querya = getQueryWithoutTGPPDrivers();
				}
			} else {
				querya = getQueryWithoutTGPPDrivers();
			}


			List<NasDbAcctDriverConfigurationImpl> tempDBAcctDriverList = new ArrayList<NasDbAcctDriverConfigurationImpl>();
			Map<String, DBAcctDriverConfiguration> tempDBAcctDriverMap = new LinkedHashMap<String, DBAcctDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(querya); //NOSONAR - Reason: SQL binding mechanisms should be used

			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				ps = connection.prepareStatement(getQueryForDBAcctDriver());

				psFields = connection.prepareStatement(QUERY_FOR_FIELD_MAPPING);

				ps.setString(1,driverInstanceId);
				ps.setString(2,driverInstanceId);
				ps.setString(3,driverInstanceId);

				setQueryTimeout(ps,100);
				rs = ps.executeQuery();
				if(rs.next()){
					NasDbAcctDriverConfigurationImpl nasDBAcctDriverConfigurationImpl = new NasDbAcctDriverConfigurationImpl();
					nasDBAcctDriverConfigurationImpl.setDriverInstanceId(driverInstanceId);

					String dbId = rs.getString("OPENDBID");

					String driverName ="";
					if(rs.getString("DRIVER_NAME")!=null){
						driverName = rs.getString("DRIVER_NAME");
						nasDBAcctDriverConfigurationImpl.setDriverName(driverName);
					}

					psFields.setString(1, dbId);
					setQueryTimeout(psFields,100);
					rsFields = psFields.executeQuery();
					List<DbFiledMapping> dbFiledMappingList = new ArrayList<DbFiledMapping>();

					while(rsFields.next()){
						DbFiledMapping dbFiledMapping = new DbFiledMapping();
						dbFiledMapping.setAttributeId(rsFields.getString("ATTRIBUTEIDS"));
						dbFiledMapping.setDataType(rsFields.getString("DATATYPE"));
						dbFiledMapping.setDbField(rsFields.getString("DBFIELD"));
						dbFiledMapping.setDefaultValue(rsFields.getString("DEFAULTVALUE"));

						dbFiledMapping.setUseDictionaryValue(Boolean.parseBoolean(rsFields.getString("USE_DICTIONARY_VALUE")));
						List<String> attributeIDList = getAttributeIDList(dbFiledMapping.getAttributeId());
						dbFiledMapping.setAttributeIDList(attributeIDList);

						dbFiledMappingList.add(dbFiledMapping);
					}
					nasDBAcctDriverConfigurationImpl.setDbFiledMappingList(dbFiledMappingList);

					DBAccountingDriverDetails dbAccountingDriverDetails = nasDBAcctDriverConfigurationImpl.getDbAccountingDriverDetails();

					DBAcctDriverCDRDetails dbAcctDriverCDRDetails = nasDBAcctDriverConfigurationImpl.getDbAcctDriverCDRDetails();
					DBAcctDriverInterimCDRDetails dbAcctDriverInterimCDRDetails = nasDBAcctDriverConfigurationImpl.getDbAcctDriverInterimCDRDetails();
					DBAcctDriverMandatoryFieldDetails dbAcctDriverMandatoryFieldDetails = nasDBAcctDriverConfigurationImpl.getDbAcctDriverMandatoryFieldDetails();
					DBAcctDriverCDRTimestampDetails dbAcctDriverCDRTimestampDetails = nasDBAcctDriverConfigurationImpl.getDbAcctDriverCDRTimestampDetails();

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
						dbAccountingDriverDetails.setDbQueryTimeout(Numbers.parseInt(rs.getString("DB_QUERY_TIMEOUT").trim(),nasDBAcctDriverConfigurationImpl.getDbQueryTimeout()));

					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Timeout Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("MAX_QUERY_TIMEOUT_COUNT")!=null && rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim().length()>0){
						dbAccountingDriverDetails.setMaxQueryTimeoutCount(Numbers.parseInt(rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim(), nasDBAcctDriverConfigurationImpl.getMaxQueryTimeoutCount()));
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
					tempDBAcctDriverList.add(nasDBAcctDriverConfigurationImpl);
					tempDBAcctDriverMap.put(driverInstanceId, nasDBAcctDriverConfigurationImpl);
				}
			}
			this.nasDbAcctDriverConfigurationImplDetailsList = tempDBAcctDriverList;
			this.nasDBAcctDriverMap = tempDBAcctDriverMap;
		} finally{
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(rsFields);
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(psFields);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(connection);
		}
	}

	private String getQueryWithoutTGPPDrivers() {
		String querya;
		querya = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.NAS_OPENDB_ACCT_DRIVER.value +"' AND (DRIVERINSTANCEID IN" +
				"(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				"(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyacctdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				"(select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				"(select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
				"(select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")))";
		return querya;
	}

	private boolean isTGPPServerEnabled(AAAServerConfigurable serverConfigurable) {
		return serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID) !=null
				&& serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID);
	}

	@DBReload
	public void reloadDiameterDBAcctDriverConfiguration()throws Exception {

		int size = this.nasDbAcctDriverConfigurationImplDetailsList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			NasDbAcctDriverConfigurationImpl nasDbAcctDriverConfigurationImpl = nasDbAcctDriverConfigurationImplDetailsList.get(i);
			queryBuilder.append("'" + nasDbAcctDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + nasDbAcctDriverConfigurationImplDetailsList.get(size - 1).getDriverInstanceId() + "')");
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
				NasDbAcctDriverConfigurationImpl nasDbAcctDriverConfigurationImpl = (NasDbAcctDriverConfigurationImpl) this.nasDBAcctDriverMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));
				String driverInstanceId = nasDbAcctDriverConfigurationImpl.getDriverInstanceId();

				ps = conn.prepareStatement(getReloadQueryForDBAcctDriver());
				String driverName = nasDbAcctDriverConfigurationImpl.getDriverName();


				ps.setString(1,driverInstanceId);
				ps.setString(2,driverInstanceId);

				setQueryTimeout(ps,100);
				rs = ps.executeQuery();
				if(rs.next()){

					DBAccountingDriverDetails dbAccountingDriverDetails = nasDbAcctDriverConfigurationImpl.getDbAccountingDriverDetails();

					if(rs.getString("DB_QUERY_TIMEOUT")!=null && rs.getString("DB_QUERY_TIMEOUT").trim().length()>0){
						dbAccountingDriverDetails.setDbQueryTimeout(Numbers.parseInt(rs.getString("DB_QUERY_TIMEOUT").trim(),nasDbAcctDriverConfigurationImpl.getDbQueryTimeout()));

					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Timeout Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("MAX_QUERY_TIMEOUT_COUNT")!=null && rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim().length()>0){
						dbAccountingDriverDetails.setMaxQueryTimeoutCount(Numbers.parseInt(rs.getString("MAX_QUERY_TIMEOUT_COUNT").trim(), nasDbAcctDriverConfigurationImpl.getMaxQueryTimeoutCount()));
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
	public void postReadProcessing() {
		if(this.nasDbAcctDriverConfigurationImplDetailsList!=null){
			int numOfDrivers = nasDbAcctDriverConfigurationImplDetailsList.size();
			NasDbAcctDriverConfigurationImpl diameterDBAcctDriverConfigurationImpl;
			for(int i=0;i<numOfDrivers;i++){
				diameterDBAcctDriverConfigurationImpl = nasDbAcctDriverConfigurationImplDetailsList.get(i);
				setValidFieldMapping(diameterDBAcctDriverConfigurationImpl);
				diameterDBAcctDriverConfigurationImpl.setMandatorydbFieldList(getMandatorydbFieldList(diameterDBAcctDriverConfigurationImpl));
				generateQueries(diameterDBAcctDriverConfigurationImpl,getConfiguredFieldsString(diameterDBAcctDriverConfigurationImpl),getConfiguredValuesString(diameterDBAcctDriverConfigurationImpl));
				DBAccountingDriverDetails dbAccountingDriverDetails = diameterDBAcctDriverConfigurationImpl.getDbAccountingDriverDetails();
				if(dbAccountingDriverDetails!=null){
					int dbQueryTimeout = diameterDBAcctDriverConfigurationImpl.getDbQueryTimeout();
					if(dbQueryTimeout > 0 && dbQueryTimeout < 10){
						dbQueryTimeout = 10;
						dbAccountingDriverDetails.setDbQueryTimeout(dbQueryTimeout);
					}
				}
			}

		}

	}


	private void setValidFieldMapping(NasDbAcctDriverConfigurationImpl diameterDBAcctDriverConfigurationImpl) {
		List<DbFiledMapping>  fieldMappingList =diameterDBAcctDriverConfigurationImpl.getDbFiledMappingList();
		if(Collectionz.isNullOrEmpty(fieldMappingList) == false){
			List<DbFiledMapping> validMappingList = new ArrayList<DbFiledMapping>();
			DbFiledMapping dbFiledMapping ;
			for(int i=0;i<fieldMappingList.size();i++){
				dbFiledMapping = fieldMappingList.get(i);
				if(isValidDbFieldMapping(dbFiledMapping)){
					validMappingList.add(dbFiledMapping);
				}
			}
			diameterDBAcctDriverConfigurationImpl.setDbFiledMappingList(validMappingList);
		}

	}

	private void generateQueries(NasDbAcctDriverConfigurationImpl nasDBAcctDriverConfigurationImpl,String strFields, String strValues) {

		String cdrTableName  = nasDBAcctDriverConfigurationImpl.getCdrTableName();
		String interimTableName  = nasDBAcctDriverConfigurationImpl.getInterimTableName();
		boolean storeAllCDR = nasDBAcctDriverConfigurationImpl.getStoreAllCDR();

		if(cdrTableName!=null && cdrTableName.trim().length()>0 && storeAllCDR){
			nasDBAcctDriverConfigurationImpl.setIsStoreStartRecordEnabled(true);
		}
		if(interimTableName != null && interimTableName.trim().length()>0){
			nasDBAcctDriverConfigurationImpl.setIsStoreUpdateRecordEnabled(true);
		}

		String cdrIdDbField = nasDBAcctDriverConfigurationImpl.getCdrIdDbField();
		String cdrIdSeqName = nasDBAcctDriverConfigurationImpl.getCdrIdSeqName();

		DBDataSource datasource = getConfigurationContext().get(DatabaseDSConfigurationImpl.class).getDataSourceByName(nasDBAcctDriverConfigurationImpl.getDsName());
		String connectionURL = datasource.getConnectionURL();
		DBVendors dbVendor = null;
		try{
			dbVendor = DBVendors.fromUrl(connectionURL);
		}catch(DatabaseTypeNotSupportedException e){
			LogManager.getLogger().error(MODULE, "Database type not supported for Diameter DB-acct-driver: " + e.getMessage());
			LogManager.ignoreTrace(e);
		}

		if(cdrIdDbField != null && cdrIdSeqName != null && dbVendor != null) {
			String strCDRInsertQuery = "INSERT INTO " + nasDBAcctDriverConfigurationImpl.getCdrTableName() +   " ( " + cdrIdDbField + " , "+ strFields + " ) values ( "+ dbVendor.getVendorSpecificSequenceSyntax(cdrIdSeqName) + ", " +strValues + " ) ";
			nasDBAcctDriverConfigurationImpl.setCDRInsertQuery(strCDRInsertQuery);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "CDR_ID_FIELD_NAME or CDR_ID_SEQUENCE_NAME field not found");
		}
		String interimCdrIdDbField = nasDBAcctDriverConfigurationImpl.getInterimCdrIdDbField();
		String interimCdrIdSeqName = nasDBAcctDriverConfigurationImpl.getInterimCdrIdSeqName();

		if(interimCdrIdDbField != null && interimCdrIdSeqName != null && dbVendor != null) {
			String  strCDRInterimInsertQuery = "INSERT INTO " + nasDBAcctDriverConfigurationImpl.getInterimTableName() +   " ( " + interimCdrIdDbField + " , "+ strFields + " ) values ( " + dbVendor.getVendorSpecificSequenceSyntax(interimCdrIdSeqName) + ", " + strValues + " ) ";
			String strCDRInterimUpdateQuery = "UPDATE " + nasDBAcctDriverConfigurationImpl.getInterimTableName() + " SET " + getConfiguredStringForUpdate(strFields, strValues) + "WHERE " + nasDBAcctDriverConfigurationImpl.getIntrimCDRAcctSessionId() + " = ?";
			String strCDRInterimDeleteQuery = "DELETE FROM " + nasDBAcctDriverConfigurationImpl.getInterimTableName() + " WHERE " + nasDBAcctDriverConfigurationImpl.getIntrimCDRAcctSessionId() + " = ?";

			nasDBAcctDriverConfigurationImpl.setCDRInterimInsertQuery(strCDRInterimInsertQuery);
			nasDBAcctDriverConfigurationImpl.setCDRInterimUpdateQuery(strCDRInterimUpdateQuery);
			nasDBAcctDriverConfigurationImpl.setCDRInterimDeleteQuery(strCDRInterimDeleteQuery);
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "INTRIEM_CDR_ID_FIELD_NAME or INTRIEM_CDR_ID_SEQUENCE_NAME field not found");

		}


	}

	private String getConfiguredValuesString(NasDbAcctDriverConfigurationImpl nasDBAcctDriverConfigurationImpl) {

		String strValues = " ";
		List<String> mandatorydbFieldList = nasDBAcctDriverConfigurationImpl.getMandatorydbFieldList();
		for(int i=0;i<mandatorydbFieldList.size();i++) {
			if(!mandatorydbFieldList.isEmpty() && mandatorydbFieldList != null) {
				if(i == mandatorydbFieldList.size()-1)
					strValues = strValues + "? ";
				else
					strValues = strValues + "?, ";
			}
		}
		List<DbFiledMapping> dbFiledMappings =  nasDBAcctDriverConfigurationImpl.getDbFiledMappingList();
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

	private String getConfiguredFieldsString(NasDbAcctDriverConfigurationImpl nasDBAcctDriverConfigurationImpl) {
		String strConfigureFields = "";
		List<String> mandatorydbFieldList = nasDBAcctDriverConfigurationImpl.getMandatorydbFieldList();
		if(!mandatorydbFieldList.isEmpty()) {
			for(int i=0;i < mandatorydbFieldList.size();i++) {
				if(i == mandatorydbFieldList.size()-1)
					strConfigureFields = strConfigureFields + mandatorydbFieldList.get(i);
				else
					strConfigureFields = strConfigureFields + mandatorydbFieldList.get(i) + ", ";
			}
		}
		List<DbFiledMapping> dbFiledMappings =  nasDBAcctDriverConfigurationImpl.getDbFiledMappingList();
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

	private void setQueryTimeout(PreparedStatement preparedStatement, int timeout) throws SQLException {
		if(timeout < DEFAULT_QUERY_TIMEOUT)
			timeout=DEFAULT_QUERY_TIMEOUT;
		preparedStatement.setQueryTimeout(timeout);
	}
	private List<String> getMandatorydbFieldList(NasDbAcctDriverConfigurationImpl setDBAcctDriverConfigurationImpl) {
		List<String> mandatorydbFieldList = new ArrayList<String>();
		if(setDBAcctDriverConfigurationImpl.getCallStartFieldName() != null && setDBAcctDriverConfigurationImpl.getCallStartFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(setDBAcctDriverConfigurationImpl.getCallStartFieldName());
		}

		if(setDBAcctDriverConfigurationImpl.getCallEndFieldName() != null && setDBAcctDriverConfigurationImpl.getCallEndFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(setDBAcctDriverConfigurationImpl.getCallEndFieldName());
		}

		if(setDBAcctDriverConfigurationImpl.getCreateDateFieldName() != null && setDBAcctDriverConfigurationImpl.getCreateDateFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(setDBAcctDriverConfigurationImpl.getCreateDateFieldName());
		}

		if(setDBAcctDriverConfigurationImpl.getLastModifiedDateFieldName() != null && setDBAcctDriverConfigurationImpl.getLastModifiedDateFieldName().trim().length() > 0) {
			mandatorydbFieldList.add(setDBAcctDriverConfigurationImpl.getLastModifiedDateFieldName());
		}

		if(setDBAcctDriverConfigurationImpl.getEnebled() && setDBAcctDriverConfigurationImpl.getDbDateField()!= null && setDBAcctDriverConfigurationImpl.getDbDateField().trim().length() > 0) {
			mandatorydbFieldList.add(setDBAcctDriverConfigurationImpl.getDbDateField());
		}
		return mandatorydbFieldList;
	}
	private boolean isValidDbFieldMapping(DbFiledMapping dbFiledMapping) {
		return Collectionz.isNullOrEmpty(dbFiledMapping.getAttributeIDList()) == false &&
				Strings.isNullOrBlank(dbFiledMapping.getDataType()) == false &&
				Strings.isNullOrBlank(dbFiledMapping.getDbField()) == false;
	}
	@PostWrite
	public void postWriteProcessing(){
		//IGNORED
	}

	@PostReload
	public void postReloadProcessing(){
		//IGNORED
	}

	private List<String> getAttributeIDList(String strAttributeId) {

		List<String> attributeList = new ArrayList<String>();
		if(strAttributeId!=null && strAttributeId.trim().length()>0){
			StringTokenizer attributeTokens = new StringTokenizer(strAttributeId,",;");
			while(attributeTokens.hasMoreTokens()) {
				String attributeId = attributeTokens.nextToken();
				AttributeData tempAttributeId = DiameterDictionary.getInstance().getAttributeId(attributeId);
				if(tempAttributeId != null){
					attributeList.add(tempAttributeId.getAVPId());
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Attribute id: "+ attributeId+ " not found in dictionary.");
					}
				}
			}
		}
		return attributeList;
	}

	private String getReadQueryForNasServicePolicyConfiguration() {

		DiameterNasServiceConfigurable diameterNasServiceConfigurable = getConfigurationContext().get(DiameterNasServiceConfigurable.class);
		if(diameterNasServiceConfigurable == null) {
			return "''";
		}
		List<String> servicePolicies = diameterNasServiceConfigurable.getServicePolicies();
		if(Collectionz.isNullOrEmpty(servicePolicies) == true || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE +
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+")";

	}

	private String getReadQueryForEapServicePolicyConfiguration() {

		DiameterEAPServiceConfigurable diameterEAPServiceConfigurable = getConfigurationContext().get(DiameterEAPServiceConfigurable.class);
		if(diameterEAPServiceConfigurable == null) {
			return "''";
		}
		List<String> servicePolicies =  diameterEAPServiceConfigurable.getServicePolicies();
		if(Collectionz.isNullOrEmpty(servicePolicies) == true || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE +
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ";

	}
}
