package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.data.ProfileFieldMapping;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
/**
 * Used for external profile lookup
 * 
 * @author chetan.sankhala
 */
public class ExternalDBSPInterface extends NVDBSPInterface {

	private final DBSPInterfaceConfiguration dbspInterfaceConfiguration;
	private String profileQuery;

	public ExternalDBSPInterface(DBSPInterfaceConfiguration dbSPInterfaceConfiguration, AlertListener alertListener, TransactionFactory transactionFactory) {
		super(alertListener, transactionFactory);
		this.dbspInterfaceConfiguration = dbSPInterfaceConfiguration;
	}

	public void init() throws InitializationFailedException {

		if (Strings.isNullOrBlank(getIdentityField())) {
			throw new InitializationFailedException("Missing Identity Field");
		}

		List<String> attributeList = dbspInterfaceConfiguration.getProfileFieldMapping().getAttributeList();
		if (attributeList.isEmpty()) {
			throw new InitializationFailedException("Missing DB Field Mapping");
		}
		
		if (attributeList.contains(getIdentityField()) == false) {
			throw new InitializationFailedException("Missing DB field mapping for identity field: " + getIdentityField());
		}
		
		profileQuery = buildQuery(attributeList, getIdentityField());
	}

	private String buildQuery(List<String> attributeList, String dbFieldForIdentity) {
		StringBuilder driverQuery = new StringBuilder("SELECT ");
		for (String dbField : attributeList) {
			driverQuery.append(dbField).append(", ");
		}

		driverQuery.deleteCharAt(driverQuery.lastIndexOf(","));
		driverQuery.append(" FROM ").append(getTablename()).append(" WHERE ").append(dbFieldForIdentity).append(" = ? ");

		return driverQuery.toString();
	}

	@Override
	protected SPRInfo createProfileData(ResultSet rsForProfile) throws SQLException, OperationFailedException {
		ProfileFieldMapping accountDataFieldMapping = dbspInterfaceConfiguration.getProfileFieldMapping();

		SPRInfoImpl sprInfo = new SPRInfoImpl();
		String stringValue;
		long numericValue;
		Timestamp timestampValue;
		boolean validate = false;
		
		for (SPRFields sprField : SPRFields.values()) {
			String field = accountDataFieldMapping.getFieldMappingForKey(sprField);

			if (field == null) {
				continue;
			}
			if (sprField.type == Types.NUMERIC) {
				numericValue = rsForProfile.getLong(field);
				sprField.setNumericValue(sprInfo, numericValue, validate);
				
			} else if (sprField.type == Types.VARCHAR) {
				 stringValue = rsForProfile.getString(field);
				if (stringValue != null) {
					sprField.setStringValue(sprInfo, stringValue, validate);
				}
			} else if (sprField.type == Types.TIMESTAMP) {
				timestampValue = rsForProfile.getTimestamp(field);
				if (timestampValue != null) {
					sprField.setTimestampValue(sprInfo, timestampValue, validate);
				}
			}
		}

		return sprInfo;
	}

	@Override
	protected String getSelectQuery() {
		return profileQuery;
	}

	@Override
	protected String getTablename() {
		return dbspInterfaceConfiguration.getTableName();
	}

	@Override
	protected int getDbQueryTimeout() {
		return dbspInterfaceConfiguration.getDbQueryTimeout();
	}

	@Override
	protected long getMaxQueryTimeoutCount() {
		return dbspInterfaceConfiguration.getMaxQueryTimeoutCount();
	}

	@Override
	protected String getIdentityField() {
		return dbspInterfaceConfiguration.getIdentityField();
	}
    
    @Override
    public void addProfile(SPRInfo info) throws OperationFailedException {
    	throw new OperationFailedException("Add profile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public void addProfile(SPRInfo info, Transaction transaction) throws OperationFailedException, TransactionException {
    	throw new OperationFailedException("Add profile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public int updateProfile(String subscriberIdentity, EnumMap<SPRFields, String> updatedProfile) throws OperationFailedException {
    	throw new OperationFailedException("Update profile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public int purgeProfile(String subscriberIdentity) throws OperationFailedException {
    	throw new OperationFailedException("purge profile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public int purgeProfile(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {
    	throw new OperationFailedException("purge profile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {
    	throw new OperationFailedException("getDeleteMarkedProfiles operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public int markForDeleteProfile(String subscriberIdentity) throws OperationFailedException {
    	throw new OperationFailedException("markForDeleteProfile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public Map<String, Integer> restoreProfile(List<String> subscriberIdentities) throws OperationFailedException {
    	throw new OperationFailedException("restoreProfile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }
    
    @Override
    public int restoreProfile(String subscriberIdentity) throws OperationFailedException {
    	throw new OperationFailedException("restoreProfile operation is not supported in external SP interface", ResultCode.OPERATION_NOT_SUPPORTED);
    }   
}
