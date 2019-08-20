package com.elitecore.netvertex.core.driver.cdr.conf;

import java.util.List;

import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;

public interface DBCDRDriverConfiguration extends DriverConfiguration {
	
	public int getDBQueryTimeout();
	public int getMaxQueryTimeoutCount();
	public String getDBDatasourceId();
	public String getTableName();
	public String getIdentityField();
	public String getSequenceName();
	public boolean isStoreAllCDRs();
	
	public String getSessionIdField();
	public String getReportingType();
	public String getInputOctetsField();
	public String getOutputOctetsField();
	public String getTotalOctetsField();
	public String getUsageTimeField();
	public String getUsageKeyField();
	public String getCreateDateField();
	public String getLastModifiedDateField();
	public String getTimestampField();
	
	public boolean isBatchUpdate();
	public int getBatchSize();
	public int getBatchUpdateInterval();
	public int getBatchUpdateQueryTimeout();
	public List<DBFieldMapping> getDBFieldMappings();

}
