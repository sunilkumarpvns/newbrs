package com.elitecore.core.serverx.sessionx.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.CriteriaBuilder;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;

public class MySQLDialectImpl implements SQLDialect {
	Map<String,SchemaMapping> schemaMapping;
	Map<String,DMLQueries> dmlQueryMap;
	OracleCriteriaBuilder criteriaBuilder;
	
	public MySQLDialectImpl(List<SchemaMapping> schemaMappingList) {
		schemaMapping = new HashMap<String, SchemaMapping>();
		for(SchemaMapping tableSchema:schemaMappingList){
			schemaMapping.put(tableSchema.getTableName(),tableSchema);
		}
		dmlQueryMap = new HashMap<String, DMLQueries>();
		criteriaBuilder = new OracleCriteriaBuilder();
		
	}
	public void init()throws InitializationFailedException{
		for(Map.Entry<String, SchemaMapping> schemaEntry:schemaMapping.entrySet()){
			SchemaMapping tableSchema = schemaEntry.getValue();
			DMLQueries dmlQueries = new DMLQueries();
			
			//Generate SQL for Insert
			String insertSQL = "INSERT INTO " + tableSchema.getTableName() + "(";
			String values = "";
			if(tableSchema.getIdGenerator() != SchemaMapping.DB_SEQUENCE_GENERATOR){
				insertSQL += tableSchema.getIdFieldMapping().getColumnName()+",";
				values += "?,";
			}
				
			
			insertSQL +=tableSchema.getCreationTime().getColumnName();
			values += "NOW()";
			
			insertSQL += ","+tableSchema.getLastUpdateTime().getColumnName();
			values += ",NOW()";
			
			for(FieldMapping fieldMapping:tableSchema.getFieldMappings()){
				insertSQL += ", " + fieldMapping.getColumnName();
				values += ",?";
			}
			dmlQueries.setInsertQuery(insertSQL + ") VALUES (" + values + ")");
			
			//Generate SQL for Update
			String updateSQL = "UPDATE " + tableSchema.getTableName() + " SET ";
			boolean isFirstField = true;
			for(FieldMapping fieldMapping:tableSchema.getFieldMappings()){
				if(isFirstField){
					updateSQL += fieldMapping.getColumnName() + "=?";
					isFirstField = false;
				}else{
					updateSQL += "," + fieldMapping.getColumnName() + "=?";
				}
			}
			updateSQL += ", " + tableSchema.getLastUpdateTime().getColumnName() + "=NOW()";
			dmlQueries.setUpdateQuery(updateSQL);
			
			String sessionTimeoutColumn = getColumnName(SchemaMappingImpl.SESSION_TIMEOUT,tableSchema.getFieldMappings());
			
			
			//Generate SQL for Session Close
			String lastUpdateTimeColumn =  tableSchema.getLastUpdateTime().getColumnName();
			
			String sessionCloseSQL ="select * from " + tableSchema.getTableName()+" where " + lastUpdateTimeColumn + " + nvl("+sessionTimeoutColumn+"," + tableSchema.getSessionIdleTime() + ")/86400 < systimestamp AND rownum <= "+tableSchema.getBatchCount();
			
			dmlQueries.setSessionCloseQuery(sessionCloseSQL);
			//Generate SQL for Delete
			dmlQueries.setDeleteQuery("delete from " + tableSchema.getTableName() + " where " + tableSchema.getIdFieldMapping().getColumnName() + "=?");
			dmlQueryMap.put(tableSchema.getTableName(), dmlQueries);
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
		StringBuilder stringBuilder = new StringBuilder("DELETE ");
		if(criteria.getHint() != null){
			stringBuilder.append(criteria.getHint());
			stringBuilder.append(" ");
		}
		stringBuilder.append("FROM ");
		stringBuilder.append(schemaMapping.get(criteria.getSchemaName()));
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		if(whereClause != null){
			stringBuilder.append(" WHERE ");
			stringBuilder.append(whereClause);
		}
			
		return stringBuilder.toString();
	}

	@Override
	public String getInsertQuery(SessionData sessionData) {
		return dmlQueryMap.get(sessionData.getSchemaName()).getInsertQuery();
	}

	
	@Override
	public String getSelectQuery(Criteria criteria) {
		
		SchemaMapping tempSchemaMapping = schemaMapping.get(criteria.getSchemaName());
		
		List<FieldMapping> fieldMappings  = tempSchemaMapping.getFieldMappings();
		
		StringBuilder stringBuilder = new StringBuilder("SELECT ");
		if(criteria.getHint() != null){
			stringBuilder.append(criteria.getHint());
			stringBuilder.append(" ");
		}
		
		
		stringBuilder.append(tempSchemaMapping.getIdFieldMapping().getColumnName());
		stringBuilder.append(",");
		
		
		
		for(int index = 0; index < fieldMappings.size(); index++){
			stringBuilder.append(fieldMappings.get(index).getColumnName());
			stringBuilder.append(",");
		}
		
		
		stringBuilder.append(tempSchemaMapping.getCreationTime().getColumnName());
		stringBuilder.append(",");
		stringBuilder.append(tempSchemaMapping.getLastUpdateTime().getColumnName());
		
		
		
		stringBuilder.append(" FROM ");
		stringBuilder.append(tempSchemaMapping.getTableName());
		
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		if(whereClause != null){
			stringBuilder.append(" WHERE ");
			stringBuilder.append(whereClause);
		}
		
		return stringBuilder.toString();
	}
	@Override
	public void setPreparedStatementForDelete(Criteria criteria,
			PreparedStatement preparedStatement) throws SessionException {
		criteriaBuilder.setParameters(preparedStatement, criteria);
	}
	@Override
	public void setPreparedStatementForUpdate(SessionData sessionData,Criteria criteria,
			PreparedStatement preparedStatement) throws SessionException {
		int index= 1;
		for(String key : sessionData.getKeySet()){
			try {
				preparedStatement.setString(index, sessionData.getValue(key));
				index++;
			} catch (SQLException e) {
				throw new SessionException(e);
			}			
		}
		criteriaBuilder.setParameters(preparedStatement, criteria,index);
	}
	@Override
	public void setPreparedStatementForSelect(Criteria criteria,
			PreparedStatement preparedStatement) throws SessionException {
		criteriaBuilder.setParameters(preparedStatement, criteria);
	}
	@Override
	public String getUpdateQuery(SessionData sessionData,Criteria criteria) {
		StringBuilder stringBuilder = new StringBuilder("UPDATE ");
		if(criteria.getHint() != null){
			stringBuilder.append(criteria.getHint());
			stringBuilder.append(" ");
		}
		
		SchemaMapping schemaMapping = this.schemaMapping.get(criteria.getSchemaName());
		stringBuilder.append(schemaMapping.getTableName());
		stringBuilder.append(" SET ");
		boolean isFirstField = true;
		for(String key : sessionData.getKeySet()){
			if(isFirstField){
				stringBuilder.append(schemaMapping.getFieldMapping(key).getColumnName());
				stringBuilder.append("=?");
				isFirstField = false;
			}else{
				stringBuilder.append(",");
				stringBuilder.append(schemaMapping.getFieldMapping(key).getColumnName());
				stringBuilder.append("=?");
			}
		}
		stringBuilder.append(", ");
		stringBuilder.append(schemaMapping.getLastUpdateTime().getColumnName());
		stringBuilder.append("=NOW()");						
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		if(whereClause != null){
			stringBuilder.append(" WHERE ");
			stringBuilder.append(whereClause);
		}
		return stringBuilder.toString();
	}

	@Override
	public String getUpdateQuery(SessionData sessionData) {
		String updateQuery = dmlQueryMap.get(sessionData.getSchemaName()).getUpdateQuery();
		FieldMapping fieldMapping = schemaMapping.get(sessionData.getSchemaName()).getIdFieldMapping();
		String whereClause = fieldMapping.getColumnName() + "='" + sessionData.getSessionId() + "'"; 
		if(whereClause != null)
			updateQuery += " WHERE " + whereClause;
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
		String countQuery = "SELECT COUNT(1) FROM " + schemaMapping.get(criteria.getSchemaName());
		String whereClause = criteriaBuilder.buildCriteria(criteria);
		if(whereClause != null)
			countQuery += " WHERE " + whereClause;
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
			throw new SessionException("Error while setting values to Prepared Statement, Reason: " + e.getMessage(), e);
		}

		for(FieldMapping fieldMapping:tableSchema.getFieldMappings()){
			try {
				if(fieldMapping.getType() == FieldMapping.STRING_TYPE)
					preparedStatement.setString(parameterIndex, sessionData.getValue(fieldMapping.getPropertyName()));
				else
					preparedStatement.setTimestamp(parameterIndex,  new Timestamp(Long.parseLong(sessionData.getValue(fieldMapping.getPropertyName())) * 1000 ));
				parameterIndex++;
			} catch (SQLException e) {
				throw new SessionException("Error while setting values to Prepared Statement, Reason: " + e.getMessage(),e);
			} catch(NumberFormatException nfe){
				throw new SessionException("Error while setting values to Prepared Statement, Reason: " + nfe.getMessage(),nfe);
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
				if(fieldMapping.getType() == FieldMapping.STRING_TYPE)
					preparedStatement.setString(parameterIndex, sessionData.getValue(fieldMapping.getPropertyName()));
				else
					preparedStatement.setTimestamp(parameterIndex,  new Timestamp(Long.parseLong(sessionData.getValue(fieldMapping.getPropertyName())) * 1000 ));
				parameterIndex++;
			} catch (SQLException e) {
				throw new SessionException("Error while setting values to Prepared Statement, Reason: " + e.getMessage(),e);
			} catch(NumberFormatException nfe){
				throw new SessionException("Error while setting values to Prepared Statement, Reason: " + nfe.getMessage(),nfe);
			}
		}
	}

	@Override
	public void setPreparedStatementForDelete(SessionData sessionData,
			PreparedStatement preparedStatement) throws SessionException {
		try {
			preparedStatement.setString(1, sessionData.getSessionId());
		} catch (SQLException e) {
			throw new SessionException("Error while setting values to Prepared Statement, Reason: " + e.getMessage(),e);
		}
	}

	class OracleCriteriaBuilder extends CriteriaBuilder {

		@Override
		protected String getColumnName(String tableName,String propertyName) {
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

}
