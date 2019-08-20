package com.elitecore.aaa.radius.sessionx.conf;

import java.util.List;

import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.DBFailureActions;
import com.elitecore.core.serverx.sessionx.impl.FieldMappingImpl;

public interface LocalSessionManagerData extends SessionManagerData {
	public static final String SESSION_STOP_ACTION_DELETE = "DELETE";
	public static final String SESSION_STOP_ACTION_UPDATE = "UPDATE";
	public static final String GROUPNAME_FIELD = "GROUPNAME";
	
	public String getInstanceId();
	
	public String getType() ;
	
	public String getInstanceName() ;
	
	public String getInstanceDesc() ;
	
	public String getDatabaseDsId() ;
	
	public boolean getCounterEnable() ;
	
	public String getTableName() ;
	
	public boolean getAutoSessionCloser() ;
	
	public long getSessionTimeout() ;

	public long getCloseBatchCount() ;

	public long getSessionThreadSleepTime() ;

	public long getStatusDuration() ;

	public long getExpiryReqLimitCount() ;

	public int getSessionCloseAction() ;
	
	public String getIdentityFeild() ;
	
	public String getIdSequenceName() ;

	public String getStartTimeFeild() ;
	
	public String getLastUpdateTimeFeild() ;
	
	public String getSearchCols() ;		
	
	public List<String> getEsiRelations() ;
	
	public List<String> getNakEsiList() ;
	
	public boolean getIsBatchUpdateEnable();
	
	public int getBatchSize();
	
	public int getBatchUpdateInterval();
	
	public int getQueryTimeoutInSecs(); 
	
	public int getBehaviourType();
	
	public List<FieldMappingImpl> getFieldMappings();
	
	public int getSessionOverrideAction();
	
	public String getSessionOverrideColumns();

	public DBFailureActions getDBFailureAction();

	public String getActionOnStop();

	public String getConcurrencyIdentityField();
}
