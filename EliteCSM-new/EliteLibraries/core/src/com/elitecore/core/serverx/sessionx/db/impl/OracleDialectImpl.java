package com.elitecore.core.serverx.sessionx.db.impl;

import java.util.List;

import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.criterion.CriteriaBuilder;

public class OracleDialectImpl extends BaseDialectImpl {
	
	OracleCriteriaBuilder criteriaBuilder;
	
	public OracleDialectImpl(List<SchemaMapping> schemaMappingList) {
		super(schemaMappingList);
		criteriaBuilder = new OracleCriteriaBuilder();
	}
	
	protected String getTimeStampCommand(){
		return "SYSDATE";
	}
	
	class OracleCriteriaBuilder extends CriteriaBuilder{

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
	protected CriteriaBuilder getCriteriaBuilder(){
		return criteriaBuilder;
	}

	@Override
	protected String getSequenceCommand(SchemaMapping tableSchema) {
		return tableSchema.getSequenceName() + ".nextval";
	}

}
