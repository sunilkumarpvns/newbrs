package com.elitecore.netvertex.core.driver.cdr.conf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.BaseConfConstant;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DriverType;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverData;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.conf.impl.DBCDRDriverConfigurationFactory;
import com.elitecore.netvertex.core.driver.cdr.conf.DBCDRDriverConfiguration;
import com.elitecore.netvertex.core.util.ConfigLogger;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manjil Purohit
 *
 */
public class DBCDRDriverConfigurationImpl implements DBCDRDriverConfiguration {
	
	private static final String MODULE = "DB-CDRD-CNF";

	private String driverInstanceId;
	private String driverType;

	private int dbQueryTimeout = BaseConfConstant.QUERY_TIMEOUT_SEC;
	private int maxQueryTimeoutCount = 200;
	
	private int batchSize = CommonConstants.BATCH_SIZE_MIN;
	private int batchUpdateInterval=1;
	private int batchUpdateQueryTimeout = BaseConfConstant.QUERY_TIMEOUT_SEC;
	
	private boolean isStoreAllCDRs = true;
	private boolean isBatchUpdate;
	
	private String dbDatasourceId;
	private String driverName;
	private String tableName;
	private String identityField;
	private String sequenceName;
	
	private String sessionIdField;
	private String reportingType;
	private String inputOctetsField;
	private String outputOctetsField;
	private String totalOctetsField;
	private String usedTimeField;
	private String usageKeyField;
	private String timestampField;
	
	private String createDateField;
	private String lastModifiedDateField;

	private Map<String, DBCDRDriverConfiguration> dbcdrDriverConfigurationMap;
	private List<DBFieldMapping> fieldMappings = new ArrayList<DBFieldMapping>();
	private DBCDRDriverConfigurationFactory dbCdrFactory = new DBCDRDriverConfigurationFactory();

	public DBCDRDriverConfigurationImpl(String driverInstanceId,
										String driverType,
										String dbDatasourceId,
										Integer maxQueryTimeoutCount,
										Integer batchSize,
										boolean isStoreAllCDRs,
										boolean isBatchUpdate,
										String driverName,
										String tableName,
										String identityField,
										String sequenceName,
										String sessionIdField,
										String reportingType,
										String inputOctetsField,
										String outputOctetsField,
										String totalOctetsField,
										String usedTimeField,
										String usageKeyField,
										String timestampField,
										String createDateField,
										String lastModifiedDateField,
										List<DBFieldMapping> fieldMappings) {
		this.driverInstanceId = driverInstanceId;
		this.driverType = driverType;
		this.dbDatasourceId = dbDatasourceId;
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
		this.batchSize = batchSize;
		this.isStoreAllCDRs = isStoreAllCDRs;
		this.isBatchUpdate = isBatchUpdate;
		this.driverName = driverName;
		this.tableName = tableName;
		this.identityField = identityField;
		this.sequenceName = sequenceName;
		this.sessionIdField = sessionIdField;
		this.reportingType = reportingType;
		this.inputOctetsField = inputOctetsField;
		this.outputOctetsField = outputOctetsField;
		this.totalOctetsField = totalOctetsField;
		this.usedTimeField = usedTimeField;
		this.usageKeyField = usageKeyField;
		this.timestampField = timestampField;
		this.createDateField = createDateField;
		this.lastModifiedDateField = lastModifiedDateField;
		this.fieldMappings = fieldMappings;
	}

	public void readConfiguration() throws LoadConfigurationException {
		LogManager.getLogger().info(MODULE, "Read configuration operation for  DB CDR driver configuration for DriverInstanceId: " + driverInstanceId +" started");
		Session session = null;

		Map<String, DBCDRDriverConfiguration> tempDbcdrDriverConfigurationMap = new HashMap<>(dbcdrDriverConfigurationMap);
		List<DriverData> driverDataList = HibernateReader.readAll(DriverData.class, session);

		for (DriverData driverData : driverDataList) {

			DBCDRDriverConfigurationImpl dbCdrDriverConfImpl = dbCdrFactory.create(driverData);

			tempDbcdrDriverConfigurationMap.put(driverData.getId(), dbCdrDriverConfImpl);
		}

		this.dbcdrDriverConfigurationMap = tempDbcdrDriverConfigurationMap;
		ConfigLogger.getInstance().info(MODULE, this.toString());
		LogManager.getLogger().debug(MODULE, "Read DB CDR Driver configuration completed ");
	}

	@Override
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	@Override
	public String getDriverType() {
		return driverType;
	}
	
	@Override
	public String getDBDatasourceId() {
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
	public int getBatchUpdateQueryTimeout() {
		return batchUpdateQueryTimeout;
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
	public boolean isStoreAllCDRs() {
		return isStoreAllCDRs;
	}

	@Override
	public String getSessionIdField() {
		return sessionIdField;
	}

	@Override
	public String getReportingType() {
		return reportingType;
	}
	
	@Override
	public String getInputOctetsField() {
		return inputOctetsField;
	}

	@Override
	public String getOutputOctetsField() {
		return outputOctetsField;
	}

	@Override
	public String getTotalOctetsField() {
		return totalOctetsField;
	}

	@Override
	public String getUsageTimeField() {
		return usedTimeField;
	}

	@Override
	public String getUsageKeyField() {
		return usageKeyField;
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
	public String toString(){

		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- DB CDR Driver Configuration -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.incrementIndentation();
		builder.append("Driver Name", driverName);
		builder.append("Driver Id", driverInstanceId);
		builder.append("Driver Type", driverType);
		builder.append("DB DataSource ID", dbDatasourceId);
		builder.append("DB Query Timeout", dbQueryTimeout);
		builder.append("Max Query Timeout count", maxQueryTimeoutCount);
		builder.append("Table Name", tableName);
		builder.append("Identity Field", identityField);
		builder.append("Sequence Name", sequenceName);
		builder.append("Batch Update", isBatchUpdate);
		builder.append("BatchSize", batchSize);
		builder.append("Batch Interval time", batchUpdateInterval);
		builder.append("Batch Update Query Timout", batchUpdateQueryTimeout);
		builder.append("Store all CDRs", isStoreAllCDRs);
		builder.append("Session field", sessionIdField);
		builder.append("Reporting Type", reportingType);
		builder.append("Input Octets field", inputOctetsField);
		builder.append("Output Octets field", outputOctetsField);
		builder.append("Total Octets", totalOctetsField);
		builder.append("Usage Time field", usedTimeField);
		builder.append("Usage key field", usageKeyField);
		builder.append("Creation date field", createDateField);
		builder.append("Last Modified Date field", lastModifiedDateField);
		builder.append("Time stamp field", timestampField);
		printDBFiledMappings(builder, fieldMappings);
		builder.decrementIndentation();
	}

	@Override
	public void reloadDriverConfiguration() throws LoadConfigurationException {
		LogManager.getLogger().info(MODULE, "Reload configuration operation for  DB CDR driver configuration for DriverInstanceId: " + driverInstanceId +" started");
		Session session = null;
		int batchSize = this.batchSize;
		List<DriverData> driverDataList = HibernateReader.readAll(DriverData.class, session);

		for (DriverData driverData : driverDataList) {
			DbCdrDriverData dbCdrDriverData = driverData.getDbCdrDriverData();
			int size = dbCdrDriverData.getBatchSize();
			if (size >= CommonConstants.BATCH_SIZE_MIN) {
				batchSize = size;
			} else {
				LogManager.getLogger().warn(MODULE, "Using previous value '" + this.batchSize
						+ "' for 'Batch Size'. Reason: Configured 'Batch Size' " + size
						+ " is less than minimum required value '" + CommonConstants.BATCH_SIZE_MIN + "'");
			}
		}
		this.batchSize = batchSize;

		ConfigLogger.getInstance().info(MODULE, this.toString());
		LogManager.getLogger().debug(MODULE, "Reload DB CDR Driver configuration completed ");
	}

	public void printDBFiledMappings(IndentingToStringBuilder builder, List<DBFieldMapping> dbFieldMappings) {
		builder.appendField("Field Mapping");
		builder.incrementIndentation();
		if (Collectionz.isNullOrEmpty(dbFieldMappings) == false) {
			for(DBFieldMapping dbFieldMapping:dbFieldMappings){
				printFieldMapping(builder, dbFieldMapping);
			}
		} else {
			builder.appendValue(null); //will be printed based on setNullText
		}
		builder.decrementIndentation();
	}

	public void printFieldMapping(IndentingToStringBuilder builder, DBFieldMapping dbFieldMapping) {

		builder.incrementIndentation();
		builder.append("DB Field", dbFieldMapping.getDBField());
		builder.append("Key", dbFieldMapping.getKey());
		builder.append("Data Type", dbFieldMapping.getDataType());
		builder.decrementIndentation();
	}


	public static class DBCDRDriverConfigurationBuilder {

		private String driverInstanceId;
		private String driverType;

		private int dbQueryTimeout = BaseConfConstant.QUERY_TIMEOUT_SEC;
		private int maxQueryTimeoutCount = 200;

		private int batchSize = CommonConstants.BATCH_SIZE_MIN;
		private int batchUpdateInterval=1;
		private int batchUpdateQueryTimeout = BaseConfConstant.QUERY_TIMEOUT_SEC;

		private boolean isStoreAllCDRs = true;
		private boolean isBatchUpdate;

		private String dbDatasourceId;
		private String driverName;
		private String tableName;
		private String identityField;
		private String sequenceName;

		private String sessionIdField;
		private String reportingType;
		private String inputOctetsField;
		private String outputOctetsField;
		private String totalOctetsField;
		private String usedTimeField;
		private String usageKeyField;
		private String timestampField;

		private String createDateField;
		private String lastModifiedDateField;
		private List<DBFieldMapping> fieldMappings = new ArrayList<DBFieldMapping>();


		public DBCDRDriverConfigurationBuilder(String driverInstanceId, String driverName) {
			this.driverInstanceId = driverInstanceId;
			this.driverName = driverName;
			this.driverType = DriverType.DB_CDR_DRIVER.name();
		}


		public DBCDRDriverConfigurationBuilder withDbQueryTimeout(int dbQueryTimeout) {
			this.dbQueryTimeout = dbQueryTimeout;
			return this;
		}


		public DBCDRDriverConfigurationBuilder withMaxQueryTimeoutCount(int maxQueryTimeoutCount) {
			this.maxQueryTimeoutCount = maxQueryTimeoutCount;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withBatchSize(int batchSize) {
			this.batchSize = batchSize;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withBatchUpdateInterval(int batchUpdateInterval) {
			this.batchUpdateInterval = batchUpdateInterval;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withBatchUpdateQueryTimeout(int batchUpdateQueryTimeout) {
			this.batchUpdateQueryTimeout = batchUpdateQueryTimeout;
			return this;
		}

		public DBCDRDriverConfigurationBuilder storeAllCDRs() {
			this.isStoreAllCDRs = true;
			return this;
		}
		public DBCDRDriverConfigurationBuilder batchUpdate() {
			this.isBatchUpdate = true;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withDbDataSourceId(String dbDatasourceId) {
			this.dbDatasourceId = dbDatasourceId;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withTableName(String tableName) {
			this.tableName = tableName;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withIdentityField(String identityField) {
			this.identityField = identityField;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withReportingType(String reportingType) {
			this.reportingType = reportingType;
			return this;
		}

		public DBCDRDriverConfigurationBuilder withSessionIdField(String sessionIdField) {
			this.sessionIdField = sessionIdField;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withInputOctetsField(String inputOctetsField) {
			this.inputOctetsField = sequenceName;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withOutputOctetsField(String outputOctetsField) {
			this.outputOctetsField = outputOctetsField;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withTotalOctetsField(String totalOctetsField) {
			this.totalOctetsField = totalOctetsField;
			return this;
		}
		public DBCDRDriverConfigurationBuilder withUsedTimeField(String usedTimeField) {
			this.usedTimeField = usedTimeField;
			return this;
		}

		public DBCDRDriverConfigurationBuilder withUsageKeyField(String usageKeyField) {
			this.usageKeyField = usageKeyField;
			return this;
		}

		public DBCDRDriverConfigurationBuilder withTimestampField(String timestampField) {
			this.timestampField = timestampField;
			return this;
		}

		public DBCDRDriverConfigurationBuilder withCreateDateField(String createDateField) {
			this.createDateField = createDateField;
			return this;
		}

		public DBCDRDriverConfigurationBuilder withLastModifiedDateField(String lastModifiedDateField) {
			this.lastModifiedDateField = lastModifiedDateField;
			return this;
		}

		public DBCDRDriverConfigurationBuilder withDbFieldMapping(DBFieldMapping dbFieldMapping) {
			this.fieldMappings.add(dbFieldMapping);
			return this;
		}

		public DBCDRDriverConfiguration build() {
			return new DBCDRDriverConfigurationImpl(driverInstanceId,
					driverType,
					dbDatasourceId,
					maxQueryTimeoutCount,
					batchSize,
			isStoreAllCDRs,
			isBatchUpdate,
			driverName,
			tableName,
			identityField,
			sequenceName,
			sessionIdField,
			reportingType,
			inputOctetsField,
			outputOctetsField,
			totalOctetsField,
			usedTimeField,
			usageKeyField,
			timestampField,
			createDateField,
			lastModifiedDateField,
			fieldMappings);
		}






	}
}
