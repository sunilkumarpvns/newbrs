package com.elitecore.core.serverx.sessionx.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.CriteriaBuilder;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;

public class PostgresDialectImpl implements SQLDialect{
	private static final String PREPARED_STATEMENT_ERROR_MESSAGE = "Error while setting values to Prepared Statement, Reason: ";
	private static final String WHERE = " WHERE ";
	private Map<String,SchemaMapping> schemaMapping;
	private Map<String,DMLQueries> dmlQueryMap;
	private PostgreCriteriaBuilder criteriaBuilder;
	
	public PostgresDialectImpl(List<SchemaMapping> schemaMappingList) {
		
		schemaMapping = new HashMap<String, SchemaMapping>();
		
		for(SchemaMapping tableSchema:schemaMappingList){
			schemaMapping.put(tableSchema.getSchemaName(),tableSchema);
		}
		
		dmlQueryMap = new HashMap<String, DMLQueries>();
		criteriaBuilder = new PostgreCriteriaBuilder();
		
	}
	
	public void init()throws InitializationFailedException{
		
		for (Entry<String, SchemaMapping> schemaEntry : schemaMapping.entrySet()){
			
			SchemaMapping tableSchema = schemaEntry.getValue();
			DMLQueries dmlQueries = new DMLQueries();
			
			dmlQueries.setInsertQuery(generateSQLQueryForInsert(tableSchema));
			
			dmlQueries.setUpdateQuery(generateSQLQueryForUpdate(tableSchema));
			
			dmlQueries.setSessionCloseQuery(generateSQLQueryForSessionClose(tableSchema));
			
			dmlQueries.setDeleteQuery(generateSQLQueryForDelete(tableSchema));
			
			dmlQueryMap.put(tableSchema.getSchemaName(), dmlQueries);
		}
		
	}
	@Override
	public String getDeleteQuery(SessionData sessionData) {
		SchemaMapping schema = schemaMapping.get(sessionData.getSchemaName());
		FieldMapping fieldMapping = schema.getIdFieldMapping();
		return "delete from " + schema.getTableName() + " where " + fieldMapping.getColumnName() + "= ?";
	}
	
	@Override
	public String getDeleteQuery(Criteria criteria) {
		StringBuilder deleteQueryBuilder = new StringBuilder("DELETE ");
		if(criteria.getHint() != null){
			deleteQueryBuilder.append(criteria.getHint());
			deleteQueryBuilder.append(" ");
		}
		deleteQueryBuilder.append("FROM ");
		deleteQueryBuilder.append(schemaMapping.get(criteria.getSchemaName()).getTableName());
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		if(whereClause != null){
			deleteQueryBuilder.append(WHERE);
			deleteQueryBuilder.append(whereClause);
		}
		
		return deleteQueryBuilder.toString();
	}

	@Override
	public String getInsertQuery(SessionData sessionData) {
		return dmlQueryMap.get(sessionData.getSchemaName()).getInsertQuery();
	}

	
	@Override
	public String getSelectQuery(Criteria criteria) {
		
		SchemaMapping fetchQuerySchemaMapping = schemaMapping.get(criteria.getSchemaName());
		
		List<FieldMapping> fieldMappings  = fetchQuerySchemaMapping.getFieldMappings();
		
		StringBuilder selectQueryBuilder = new StringBuilder("SELECT ");
		if (criteria.getHint() != null) {
			selectQueryBuilder.append(criteria.getHint());
			selectQueryBuilder.append(" ");
		}
		
		selectQueryBuilder.append(fetchQuerySchemaMapping.getIdFieldMapping().getColumnName());
		selectQueryBuilder.append(",");
		
		int fieldMappingSize = fieldMappings.size();
		for (int index = 0; index < fieldMappingSize; index++) {
			selectQueryBuilder.append(fieldMappings.get(index).getColumnName());
			selectQueryBuilder.append(",");
		}
		
		selectQueryBuilder.append(fetchQuerySchemaMapping.getCreationTime().getColumnName());
		selectQueryBuilder.append(",");
		selectQueryBuilder.append(fetchQuerySchemaMapping.getLastUpdateTime().getColumnName());
		
		selectQueryBuilder.append(" FROM ");
		selectQueryBuilder.append(fetchQuerySchemaMapping.getTableName());
		
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		if (whereClause != null) {
			selectQueryBuilder.append(WHERE);
			selectQueryBuilder.append(whereClause);
		}
		
		return selectQueryBuilder.toString();
	}
	
	@Override
	public void setPreparedStatementForDelete(Criteria criteria,
			PreparedStatement preparedStatement) throws SessionException {
		criteriaBuilder.setParameters(preparedStatement, criteria);
	}
	
	@Override
	public void setPreparedStatementForUpdate(SessionData sessionData,Criteria criteria,
			PreparedStatement preparedStatement) throws SessionException {
		
		int columnIndex= 1;
		for(String key : sessionData.getKeySet()){
			try {
				preparedStatement.setString(columnIndex, sessionData.getValue(key));
				columnIndex++;
			} catch (SQLException e) {
				throw new SessionException(e);
			}			
		}
		criteriaBuilder.setParameters(preparedStatement, criteria,columnIndex);
	}
	
	@Override
	public void setPreparedStatementForSelect(Criteria criteria,
			PreparedStatement preparedStatement) throws SessionException {
		criteriaBuilder.setParameters(preparedStatement, criteria);
	}
	
	@Override
	public String getUpdateQuery(SessionData sessionData,Criteria criteria) {
		
		StringBuilder updateQueryBuilder = new StringBuilder("UPDATE ");
		if(criteria.getHint() != null){
			updateQueryBuilder.append(criteria.getHint());
			updateQueryBuilder.append(" ");
		}
		
		SchemaMapping schemaMapping = this.schemaMapping.get(criteria.getSchemaName());
		updateQueryBuilder.append(schemaMapping.getTableName());
		updateQueryBuilder.append(" SET ");
		boolean isFirstField = true;
		
		for (String key : sessionData.getKeySet()) {
			if (isFirstField) {
				updateQueryBuilder.append(schemaMapping.getFieldMapping(key).getColumnName());
				updateQueryBuilder.append("=?");
				isFirstField = false;
			} else {
				updateQueryBuilder.append(",");
				updateQueryBuilder.append(schemaMapping.getFieldMapping(key).getColumnName());
				updateQueryBuilder.append("=?");
			}
		}
		
		updateQueryBuilder.append(", ");
		updateQueryBuilder.append(schemaMapping.getLastUpdateTime().getColumnName());
		updateQueryBuilder.append("=CURRENT_DATE");						
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		
		if (whereClause != null) {
			updateQueryBuilder.append(WHERE);
			updateQueryBuilder.append(whereClause);
		}
		
		return updateQueryBuilder.toString();
	}

	@Override
	public String getUpdateQuery(SessionData sessionData) {
		
		String updateQuery = dmlQueryMap.get(sessionData.getSchemaName()).getUpdateQuery();
		FieldMapping fieldMapping = schemaMapping.get(sessionData.getSchemaName()).getIdFieldMapping();
		String whereClause = fieldMapping.getColumnName() + "='" + sessionData.getSessionId() + "'";
		
		if (whereClause != null) {
			updateQuery += WHERE + whereClause;
		}
		
		return updateQuery;
	}
	
	@Override
	public String getTruncateQuery(String tableName) {
		if(tableName == null)
			return null;
		return "TRUNCATE TABLE " + tableName;
	}

	@Override
	public String getCountQuery(Criteria criteria) {
		
		String countQuery = "SELECT COUNT(1) FROM " + schemaMapping.get(criteria.getSchemaName()).getTableName();
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		
		if (whereClause != null) {
			countQuery += WHERE + whereClause;
		}
		
		return countQuery;
	}
	
	@Override
	public String getSessionCloseQuery(String tableName) {
		return dmlQueryMap.get(tableName).getSessionCloseQuery();
	}
	
	class DMLQueries{
		private String insertQuery;
		private String updateQuery;
		private String deleteQuery;
		private String sessionCloseQuery;
		
		public void setInsertQuery(String insertQuery) {
			this.insertQuery = insertQuery;
		}
		public String getInsertQuery() {
			return insertQuery;
		}
		public void setUpdateQuery(String updateQuery) {
			this.updateQuery = updateQuery;
		}
		public String getUpdateQuery() {
			return updateQuery;
		}
		public void setDeleteQuery(String deleteQuery) {
			this.deleteQuery = deleteQuery;
		}
		public String getDeleteQuery() {
			return deleteQuery;
		}
		
		public void setSessionCloseQuery(String sessionCloseQuery){
			this.sessionCloseQuery = sessionCloseQuery;
		}
		
		public String getSessionCloseQuery(){
			return sessionCloseQuery;
		}
	}

	
	
	@Override
	public void setPreparedStatementForInsert(SessionData sessionData,
			PreparedStatement preparedStatement) throws SessionException{
		
		SchemaMapping tableSchema = schemaMapping.get(sessionData.getSchemaName());
		int parameterIndex=1;

		try {
			if (tableSchema.getIdGenerator() == SchemaMapping.LOCAL_SEQUENCE_GENERATOR) {
				preparedStatement.setString(parameterIndex++, sessionData.getSessionId());
			} else if (tableSchema.getIdGenerator() == SchemaMapping.UUID_GENERATOR) {
				preparedStatement.setString(parameterIndex++, UUID.randomUUID().toString());
			}
		} catch (SQLException e) {
			throw new SessionException(PREPARED_STATEMENT_ERROR_MESSAGE + e.getMessage(), e);
		}

		for(FieldMapping fieldMapping:tableSchema.getFieldMappings()){
			try {
				if (fieldMapping.getType() == FieldMapping.STRING_TYPE) {
					
					if ("SESSION_TIMEOUT".equals(fieldMapping.getColumnName())) {
						
						String value = sessionData.getValue(fieldMapping.getPropertyName());
						
						if (Strings.isNullOrBlank(value)) {
							preparedStatement.setNull(parameterIndex, Types.NUMERIC);
						} else {
							preparedStatement.setInt(parameterIndex, Integer.parseInt(value));
						}
						
					} else {
						preparedStatement.setString(parameterIndex, sessionData.getValue(fieldMapping.getPropertyName()));
					}
					
				} else {
					preparedStatement.setTimestamp(parameterIndex,  new Timestamp(Long.parseLong(sessionData.getValue(fieldMapping.getPropertyName())) * 1000 ));
				}
				
				parameterIndex++;
				
			} catch (SQLException e) {
				throw new SessionException(PREPARED_STATEMENT_ERROR_MESSAGE + e.getMessage(),e);
			} catch(NumberFormatException nfe){
				throw new SessionException(PREPARED_STATEMENT_ERROR_MESSAGE + nfe.getMessage(),nfe);
			}
		}
	}
	
	@Override
	public void setPreparedStatementForUpdate(SessionData sessionData,
			PreparedStatement preparedStatement) throws SessionException {
		
		SchemaMapping tableSchema = schemaMapping.get(sessionData.getSchemaName());
		int parameterIndex=1;
		
		for(FieldMapping fieldMapping:tableSchema.getFieldMappings()){
			try {
				if(fieldMapping.getType() == FieldMapping.STRING_TYPE){
					preparedStatement.setString(parameterIndex, sessionData.getValue(fieldMapping.getPropertyName()));
				} else {
					preparedStatement.setTimestamp(parameterIndex,  new Timestamp(Long.parseLong(sessionData.getValue(fieldMapping.getPropertyName())) * 1000 ));
				}
				parameterIndex++;
			} catch (SQLException e) {
				throw new SessionException(PREPARED_STATEMENT_ERROR_MESSAGE + e.getMessage(),e);
			} catch(NumberFormatException nfe){
				throw new SessionException(PREPARED_STATEMENT_ERROR_MESSAGE + nfe.getMessage(),nfe);
			}
		}
	}

	@Override
	public void setPreparedStatementForDelete(SessionData sessionData,
			PreparedStatement preparedStatement) throws SessionException {
		try {
			preparedStatement.setInt(1, Integer.parseInt(sessionData.getSessionId()));
		} catch (SQLException e) {
			throw new SessionException(PREPARED_STATEMENT_ERROR_MESSAGE + e.getMessage(),e);
		}
	}

	@Override
	public void setPreparedStatementForCount(Criteria criteria,
			PreparedStatement preparedStatement) {
		criteriaBuilder.setParameters(preparedStatement, criteria);
	}
	
	private String getColumnName(String fieldName,List<FieldMapping> fieldMappings){
		
		String columnName=null;
		for(FieldMapping fieldMapping: fieldMappings){
			if(fieldName.equals(fieldMapping.getField())){
				columnName = fieldMapping.getColumnName();
				break;
			}
		}
		return columnName;
	}

	private String generateSQLQueryForInsert(SchemaMapping tableSchema) {
		StringBuilder insertQueryBuilder = new StringBuilder("INSERT INTO " + tableSchema.getTableName() + "(" + tableSchema.getIdFieldMapping().getColumnName());
		StringBuilder values = new StringBuilder();
		
		if (SchemaMapping.DB_SEQUENCE_GENERATOR == tableSchema.getIdGenerator()) {
			values.append("nextval('" + tableSchema.getSequenceName() + "')");
		} else {
			values.append("?");
		}
		
		insertQueryBuilder.append(", " + tableSchema.getCreationTime().getColumnName());
		values.append(",CURRENT_TIMESTAMP");
		
		insertQueryBuilder.append(", " + tableSchema.getLastUpdateTime().getColumnName());
		values.append(",CURRENT_TIMESTAMP");
		
		for (FieldMapping fieldMapping:tableSchema.getFieldMappings()) {
			insertQueryBuilder.append(", " + fieldMapping.getColumnName());
			values.append(",?");
		}
		
		return insertQueryBuilder.toString() + ") VALUES (" + values.toString() + ")";
	}
	
	private String generateSQLQueryForUpdate(SchemaMapping tableSchema) {
		
		StringBuilder updateQueryBuilder = new StringBuilder("UPDATE " + tableSchema.getTableName() + " SET ");
		boolean isFirstField = true;
		
		for(FieldMapping fieldMapping:tableSchema.getFieldMappings()){
			if(isFirstField){
				updateQueryBuilder.append(fieldMapping.getColumnName() + "=?");
				isFirstField = false;
			}else{
				updateQueryBuilder.append("," + fieldMapping.getColumnName() + "=?");
			}
		}
		
		updateQueryBuilder.append(", " + tableSchema.getLastUpdateTime().getColumnName() + "=CURRENT_TIMESTAMP");
		return updateQueryBuilder.toString();
	}
	
	private String generateSQLQueryForSessionClose(SchemaMapping tableSchema){
		
		String sessionTimeoutColumn = getColumnName(SchemaMappingImpl.SESSION_TIMEOUT,tableSchema.getFieldMappings());
		String lastUpdateTimeColumn =  tableSchema.getLastUpdateTime().getColumnName();
		return "select * from " + tableSchema.getTableName() +" where to_timestamp(extract(epoch from "+lastUpdateTimeColumn+") + coalesce("+sessionTimeoutColumn+","+tableSchema.getSessionIdleTime()+")/86400 ) < to_timestamp(extract(epoch from current_timestamp)) limit "+tableSchema.getBatchCount();
	}
	
	private String generateSQLQueryForDelete(SchemaMapping tableSchema){
		return "delete from " + tableSchema.getTableName() + " where " + tableSchema.getIdFieldMapping().getColumnName() + "=?";
	}
	
	class PostgreCriteriaBuilder extends CriteriaBuilder{

		@Override
		protected String getColumnName(String tableName, String propertyName) {
			SchemaMapping tableSchema = schemaMapping.get(tableName);
			FieldMapping fieldMapping = tableSchema.getFieldMapping(propertyName);
			if(fieldMapping != null){
				return fieldMapping.getColumnName();
			}else if(SchemaMapping.LAST_UPDATE_TIME.equals(propertyName)){
				return tableSchema.getLastUpdateTime().getColumnName();
			}else if(SchemaMapping.CREATION_TIME.equals(propertyName)){
				return tableSchema.getCreationTime().getColumnName();
			}
			return null;
		}
	}
	
}