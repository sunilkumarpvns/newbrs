package com.elitecore.netvertexsm.util.driver.cdr.conf;

import java.util.List;

import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.netvertexsm.util.driver.DriverConfiguration;

public interface DBCDRDriverConfiguration extends DriverConfiguration {
	
	public int getDBQueryTimeout();
	public int getMaxQueryTimeoutCount();
	public int getDBDatasourceId();
	public String getTableName();
	public String getIdentityField();
	public String getSequenceName();
	
	public String getSessionIdField();
	public String getCreateDateField();
	public String getLastModifiedDateField();
	public String getTimestampField();
	
	public boolean isBatchUpdate();
	public int getBatchSize();
	public int getBatchUpdateInterval();
	public int getQueryTimeout();
	public List<DBFieldMapping> getDBFieldMappings();
	public boolean isStoreAllCDRs();
	
}
