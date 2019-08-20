package com.elitecore.netvertexsm.util.driver.cdr;

import java.util.List;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.driverx.cdr.BaseDBCDRDriver;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.util.BoDServiceRequest;
import com.elitecore.netvertexsm.util.driver.cdr.conf.DBCDRDriverConfiguration;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.datasource.database.DBDataSourceImpl;

public class NVSMDBCDRDriver extends BaseDBCDRDriver {

	private static final String MODULE = "NVSM-DBCDRD";


	private static final int CONNECTION_READ_TIMEOUT = 60 * 1000;
	private DBDataSource dataSource;
	private DBCDRDriverConfiguration dbCDRDriverConf;
	
	public NVSMDBCDRDriver(DBCDRDriverConfiguration dbCDRDriverConf, TaskScheduler taskScheduler) {
		super(taskScheduler);
		this.dbCDRDriverConf = dbCDRDriverConf;
	}
	
	public void init() throws DriverInitializationFailedException {
		try {
			super.init();
		} catch (InitializationFailedException e) {
			throw new DriverInitializationFailedException("Error while initialising DB CDR driver. Reason: " + e.getMessage(), e);
		}
	}
	
	@Override
	public String getParameterValue(ServiceRequest serviceRequest, String key, String defaultValue) {
		String value = ((BoDServiceRequest) serviceRequest).getAttribute(key);
		return value == null ? defaultValue : value;
	}
	
	@Override
	public long getDriverInstanceId() {
		return dbCDRDriverConf.getDriverInstanceId();
	}

	@Override
	public String getDriverInstanceUUID() {
		return null;
	}
	
	@Override
	public int getDriverType() {
		return DriverTypes.DB_CDR_DRIVER;
	}

	@Override
	public String getDriverName() {
		return dbCDRDriverConf.getDriverName();
	}
	
	public String getTableName() {
		return dbCDRDriverConf.getTableName();
	}
	
	public List<DBFieldMapping> getDBFieldMappings() {
		return dbCDRDriverConf.getDBFieldMappings();
	}

	@Override
	public boolean isStoreAllCDRs() {
	   return dbCDRDriverConf.isStoreAllCDRs();
	}

	@Override
	public DBDataSource getDataSource() {
		if(dataSource == null) {
			try {
				DatabaseDSBLManager dsblManager = new DatabaseDSBLManager();
				DatabaseDSData dsData = dsblManager.getDatabaseDS((long)dbCDRDriverConf.getDBDatasourceId());
				dataSource = new DBDataSourceImpl("NVSM", dsData.getName(), dsData.getConnectionUrl(), dsData.getUserName(), dsData.getPassword(), (int)dsData.getMinimumPool(), (int)dsData.getMaximumPool(),DBDataSourceImpl.DEFAULT_STATUS_CHECK_DURATION,CONNECTION_READ_TIMEOUT);

			} catch (DataManagerException e) {
				Logger.logError(MODULE, "Error while initializing DBDatasource. Reason: " + e.getMessage());
				Logger.logTrace(MODULE, e);
			}
		}
		return dataSource;
	}

	@Override
	public String getIdentityField() {
		return dbCDRDriverConf.getIdentityField();
	}

	@Override
	public String getSequenceName() {
		return dbCDRDriverConf.getSequenceName();
	}

	@Override
	public String getSessionIdField() {
		return dbCDRDriverConf.getSessionIdField();
	}

	@Override
	public String getCreateDateField() {
		return dbCDRDriverConf.getCreateDateField();
	}
	
	@Override
	public String getTimestampField() {
		return dbCDRDriverConf.getTimestampField();
	}

	@Override
	public String getLastModifiedDateField() {
		return dbCDRDriverConf.getLastModifiedDateField();
	}

	@Override
	public int getQueryTimeout() {
		return dbCDRDriverConf.getQueryTimeout();
	}

	@Override
	public int getMaxQueryTimeoutCount() {
		return dbCDRDriverConf.getMaxQueryTimeoutCount();
	}
	
	@Override
	public String getIdentityFieldValue(ServiceRequest request) {
		return null;
	}

	@Override
	public String getName() {
		return dbCDRDriverConf.getDriverName();
	}

	@Override
	public String getTypeName() {
		return "DB_CDR_DRIVER";
	}

	@Override
	public boolean isBatchUpdate() {
		return dbCDRDriverConf.isBatchUpdate();
	}

	@Override
	public int getBatchSize() {
		return dbCDRDriverConf.getBatchSize();
	}

	@Override
	public int getBatchUpdateInterval() {
		return dbCDRDriverConf.getBatchUpdateInterval();
	}

	@Override
	public int getBatchQueryTimeOut() {
		return dbCDRDriverConf.getQueryTimeout();
	}
}