package com.elitecore.netvertexsm.util.driver.cdr.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.spi.SessionImplementor;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.DataTypeConstant;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.util.driver.cdr.conf.DBCDRDriverConfiguration;
import com.elitecore.netvertexsm.util.logger.Logger;

/**
 * @author Manjil Purohit
 *
 */
public class DBCDRDriverConfigurationImpl implements DBCDRDriverConfiguration {
	
	private static final String MODULE = "DB-CDRD-CNF";
	
	private int driverInstanceId;
	private int dbQueryTimeout = 2;
	private int maxQueryTimeoutCount = 200;
	
	private int batchSize;
	private int batchUpdateInterval;
	private int queryTimeout;
	
	private boolean isBatchUpdate;
	
	private int dbDatasourceId;
	private String driverName;
	private String tableName;
	private String identityField;
	private String sequenceName;
	
	private String sessionIdField;
	private String timestampField;
	private String createDateField;
	private String lastModifiedDateField;
	
	private List<DBFieldMapping> fieldMappings;
	
	private boolean isStoreAllCDRs = true;


	public DBCDRDriverConfigurationImpl(int driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
		fieldMappings = new ArrayList<DBFieldMapping>();
	}
	
	public void readConfiguration() throws LoadConfigurationException {
		Logger.logInfo(MODULE, "Reading DB CDR driver configuration for DriverInstanceId: " + driverInstanceId);
		Connection connection = null;
		PreparedStatement preparedStatement = null, psFields = null;
		ResultSet resultSet = null, rsFields = null;
		List<DBFieldMapping> fieldMappings = new ArrayList<DBFieldMapping>();
		String tempValue;
		try {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SessionImplementor sim =(SessionImplementor)session;
			//SessionFactoryImplementor impl = (SessionFactoryImplementor)((HibernateDataSession)session).getSession().getSessionFactory();
			connection = sim.connection();//  DBConnectionManager.getInstance().getSMDatabaseConection();//        impl.getConnectionProvider().getConnection();
			if(connection == null) {
				Logger.logError(MODULE,"No connection available while reading DB CDR driver configuration");
				throw new LoadConfigurationException("Cannot establish connection to database");
			}
			preparedStatement = connection.prepareStatement(getQueryForDBCDRDriver());
			preparedStatement.setInt(1, driverInstanceId);
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
				int csvDriverId = resultSet.getInt("DBCDRDRIVERID");
				dbDatasourceId = resultSet.getInt("DATABASEDSID");
				dbQueryTimeout = resultSet.getInt("DBQUERYTIMEOUT");
				maxQueryTimeoutCount = resultSet.getInt("MAXQUERYTIMEOUTCOUNT");
				
				batchSize = resultSet.getInt("BATCHSIZE");
				batchUpdateInterval = resultSet.getInt("BATCHUPDATEINTERVAL");
				queryTimeout = resultSet.getInt("QUERYTIMEOUT");
				
				isBatchUpdate = Boolean.parseBoolean(resultSet.getString("ISBATCHUPDATE"));
				
				isStoreAllCDRs = Boolean.parseBoolean(resultSet.getString("STOREALLCDR"));
				
				tempValue = resultSet.getString("NAME");
				if(tempValue != null && !tempValue.trim().isEmpty())
					driverName =  tempValue;
				
				tempValue = resultSet.getString("TABLENAME");
				if(tempValue == null)
					throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: Table name not configured");
				tableName =  tempValue.trim();
				
				tempValue = resultSet.getString("IDENTITYFIELD");
				if(tempValue == null)
					throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: Identity field name not configured");
				identityField = tempValue.trim();
				
				tempValue = resultSet.getString("SEQUENCENAME");
				if(tempValue == null)
					throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: Sequence name not configured");
				sequenceName = tempValue.trim();
				
				tempValue = resultSet.getString("SESSIONIDFIELDNAME");
				if(tempValue == null)
					throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: SessionId field name not configured");
				sessionIdField = tempValue.trim();
				fieldMappings.add(new DBFieldMapping(sessionIdField, PCRFKeyConstants.CS_SESSION_ID.getVal(), DataTypeConstant.STRING_DATA_TYPE, null));
				
				tempValue = resultSet.getString("TIMESTAMPFIELDNAME");
				if(tempValue != null && !tempValue.trim().isEmpty()) 
					timestampField = tempValue;
				
				tempValue = resultSet.getString("CREATEDATEFIELDNAME");
				if(tempValue != null && !tempValue.trim().isEmpty())
					createDateField = tempValue;
				
				tempValue = resultSet.getString("LASTMODIFIEDFIELDNAME");
				if(tempValue != null && !tempValue.trim().isEmpty())
					lastModifiedDateField = tempValue;
				
				psFields = connection.prepareStatement(getQueryForFieldMap());
				if(psFields == null) {
					Logger.logDebug(MODULE,"PreparedStatement is null");
					throw new LoadConfigurationException("Prepared statement is null");
				}

				psFields.setInt(1, csvDriverId);
				rsFields = psFields.executeQuery();
				
				while (rsFields.next()) {
					String headerKey = rsFields.getString("DBFIELD");
					String key = rsFields.getString("PCRFKEY");
					int dataType = rsFields.getInt("DATATYPE");
					String defaultValue = rsFields.getString("DEFAULTVALUE");
					DBFieldMapping csvFieldMapping = new DBFieldMapping(headerKey, key, dataType, defaultValue);
					fieldMappings.add(csvFieldMapping);
				}
				
				this.fieldMappings = fieldMappings; 
			}
		} catch (SQLException sqlEx) {
			throw new LoadConfigurationException("Error while reading DB CDR driver configuration. Reason: " + sqlEx.getMessage() , sqlEx);
		} catch (Exception ex) {
			Logger.logError(MODULE, "Error while reading DB CDR driver = " + driverInstanceId + ". Reason: " + ex.getMessage());
			Logger.logTrace(MODULE, ex);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsFields);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psFields);
			DBUtility.closeQuietly(connection);
		}
	}

	private String getQueryForFieldMap() {
		return "SELECT * FROM TBLMDBCDRFIELDMAPPING WHERE DBCDRDRIVERID = ?";
	}

	private String getQueryForDBCDRDriver() {
		return "SELECT A.*,B.NAME FROM TBLMDBCDRDRIVER A, TBLMDRIVERINSTANCE B WHERE A.DRIVERINSTANCEID=B.DRIVERINSTANCEID AND B.DRIVERINSTANCEID=?";
	}

	@Override
	public int getDriverInstanceId() {
		return driverInstanceId;
	}

	@Override
	public int getDriverTypeId() {
		return DriverTypes.DB_CDR_DRIVER;
	}
	
	@Override
	public int getDBDatasourceId() {
		return dbDatasourceId;
	}

	@Override
	public int getDBQueryTimeout() {
		return dbQueryTimeout;
	}

	@Override
	public int getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}

	@Override
	public boolean isBatchUpdate() {
		return isBatchUpdate;
	}

	@Override
	public int getBatchSize() {
		return batchSize;
	}

	@Override
	public int getBatchUpdateInterval() {
		return batchUpdateInterval;
	}

	@Override
	public int getQueryTimeout() {
		return queryTimeout;
	}

	@Override
	public String getDriverName() {
		return driverName;
	}

	@Override
	public String getTableName() {
		return tableName;
	}
	
	@Override
	public String getIdentityField() {
		return identityField;
	}

	@Override
	public String getSequenceName() {
		return sequenceName;
	}
	
	@Override
	public String getSessionIdField() {
		return sessionIdField;
	}

	@Override
	public String getCreateDateField() {
		return createDateField;
	}

	@Override
	public String getLastModifiedDateField() {
		return lastModifiedDateField;
	}
	
	@Override
	public String getTimestampField() {
		return timestampField;
	}
	
	@Override
	public List<DBFieldMapping> getDBFieldMappings() {
		return fieldMappings;
	}

	@Override
	public boolean isStoreAllCDRs() {

		return isStoreAllCDRs;
	}

}
