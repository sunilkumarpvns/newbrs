package com.elitecore.corenetvertex.spr.data.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.SpInterfaceType;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.spinterface.DbSpInterfaceData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceFieldMappingData;
import com.elitecore.corenetvertex.spr.DBSPInterfaceConfiguration;
import com.elitecore.corenetvertex.spr.data.ProfileFieldMapping;
import com.elitecore.corenetvertex.util.DataSourceProvider;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author Jay Trivedi
 *
 */

public class DBSPInterfaceConfigurationImpl implements DBSPInterfaceConfiguration {

	private static final String MODULE = "DB-SP-INTERFACE-CONF-IMPL";

	private int dbQueryTimeout;
	private long maxQueryTimeoutCount;
	private String tableName;
	private String identityField;
	private ProfileFieldMapping profileFieldMapping;
	private DBDataSource dbDataSource;
	private int statusCheckDuration;
	private int timeout;
	private String name;

	public DBSPInterfaceConfigurationImpl(int dbQueryTimeout, long maxQueryTimeoutCount, String tableName, String identityField,
										  DBDataSource dbDataSource, int statusCheckDuration, int timeout, String driverName,
			ProfileFieldMapping profileFieldMapping) {

		this.dbQueryTimeout = dbQueryTimeout;
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
		this.tableName = tableName;
		this.identityField = identityField;
		this.dbDataSource = dbDataSource;
		this.statusCheckDuration = statusCheckDuration;
		this.timeout = timeout;
		this.name = driverName;
		this.profileFieldMapping = profileFieldMapping;

	}

	public DBSPInterfaceConfigurationImpl() {
	}



	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setDbQueryTimeout(int dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}

	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}

	public void setIdentityField(String identityField) {
		this.identityField = identityField;
	}

	public void setAccountDataFieldMapping(ProfileFieldMapping accountDataFieldMapping) {
		this.profileFieldMapping = accountDataFieldMapping;
	}

	public void setStatusCheckDuration(int statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ProfileFieldMapping getProfileFieldMapping() {
		return profileFieldMapping;
	}

	@Override
	public DBDataSource getDbDataSource() {
		return dbDataSource;
	}

	@Override
	public int getDbQueryTimeout() {
		return dbQueryTimeout;
	}

	@Override
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}

	@Override
	public int getStatusCheckDuration() {
		return statusCheckDuration;
	}

	@Override
	public String getIdentityField() {
		return identityField;
	}

	@Override
	public int getTimeOut() {
		return timeout;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder =  new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public SpInterfaceType getType() {
		return SpInterfaceType.DB_SP_INTERFACE;
	}

	public static DBSPInterfaceConfiguration create(SpInterfaceData spInterfaceData, DataSourceProvider dataSourceProvider, FailReason failReason) {

		DbSpInterfaceData dbSPInterfaceData = spInterfaceData.getDbSpInterfaceData();

		DatabaseData dbDSData = null;
		ProfileFieldMapping profileFieldMapping = null;
		if (dbSPInterfaceData == null) {
			failReason.add("SP interface not found with name: " + spInterfaceData.getName());
			return null;
		}


		dbDSData = dbSPInterfaceData.getDatabaseData();
		if (dbDSData == null) {
			failReason.add("DataSource not found in SP interface: " + spInterfaceData.getName());
		}

		List<SpInterfaceFieldMappingData> dbFieldMappingDatas = dbSPInterfaceData.getSpInterfaceFieldMappingDatas();
		if (Collectionz.isNullOrEmpty(dbFieldMappingDatas)) {
			failReason.add("Field mapping not configured in SP interface: " + spInterfaceData.getName());
		}

		profileFieldMapping = new ProfileFieldMapping();
		for (int fiedlMappingIndex = 0; fiedlMappingIndex < dbFieldMappingDatas.size(); fiedlMappingIndex++) {
			SpInterfaceFieldMappingData fieldMapping = dbFieldMappingDatas.get(fiedlMappingIndex);
			profileFieldMapping.setFieldMapping(fieldMapping.getLogicalName(), fieldMapping.getFieldName());
		}


		Integer maxQueryTimeoutCount = dbSPInterfaceData.getMaxQueryTimeoutCount();
		if (maxQueryTimeoutCount == null) {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Taking 3 as maximum query timeout count. Reason: Maximum query timeout count not configured in sp interface:" + spInterfaceData.getName());
			}
			maxQueryTimeoutCount = 3;
		}

		Integer statusCheckDuration = 10;
		Integer timeout = 3;
		if (dbDSData != null) {
			statusCheckDuration = dbDSData.getStatusCheckDuration();
			if (statusCheckDuration == null) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Taking 10 as status check duration. Reason: Status check duration not configured in data source:" + dbDSData.getName());
				}
				statusCheckDuration = 10;
			}

			timeout = dbDSData.getQueryTimeout();
			if (timeout == null) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Taking 3 as timeout. Reason: Timeout not configured in data source:" + dbDSData.getName());
				}
				timeout = 3;
			}
		}


		FailReason dsFailReason = new FailReason("DB DS");
		DBDataSource dbDs = dataSourceProvider.getDBDataSource(dbSPInterfaceData.getDatabaseData(), failReason);
		failReason.addChildModuleFailReasonIfNotEmpty(dsFailReason);

		if (failReason.isEmpty() == false) {
			return null;
		}


		return new DBSPInterfaceConfigurationImpl(dbSPInterfaceData.getDatabaseData().getQueryTimeout(),
				maxQueryTimeoutCount.intValue(),
				dbSPInterfaceData.getTableName(),
				dbSPInterfaceData.getIdentityField(),
				dbDs,
				statusCheckDuration.intValue(),
				timeout.intValue(),
				spInterfaceData.getName(),
				profileFieldMapping);

	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Name", name);
		builder.append("DB DataSource", dbDataSource.getDataSourceName());
		builder.append("Table Name", tableName);
		builder.append("Identity Field", identityField);
		builder.append("Status Check Duration", statusCheckDuration);
		builder.append("Timeout", timeout);
		builder.append("DB Query Timeout", dbQueryTimeout);
		builder.append("Maximum Query Timeout Count", maxQueryTimeoutCount);
		builder.appendChildObject("Field Mapping ", profileFieldMapping);
	}
}
