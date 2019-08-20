package com.elitecore.aaa.radius.sessionx.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.sessionx.FieldMapping;

public interface LocalSessionManagerConfig extends ISessionManagercConfig {

	public enum BehaviourType{
		/* This means that the session will be created while the Accounting request */
		ACCOUNTING(1,"ACCOUNTING"),
		/* This means that the session will be created while the Authentication request */
		AUTHENTICATION(2,"AUTHENTICAION");
		
		public final int type;
		public final String name;
		private static final BehaviourType[] VALUES = values();
		private static final Map<Integer,BehaviourType> map;
		
		private BehaviourType(int type, String name) {
			this.type = type;
			this.name = name;
		}
		
		static{
			map =  new HashMap<Integer,BehaviourType>();
			for(BehaviourType val:VALUES){
				map.put(val.type, val);
			}
		}
		
		public int getValue(){
			return this.type;
		}
		public static boolean isValid(int value){
			return map.containsKey(value);	
		}
		
		public static BehaviourType get(int key){
			return map.get(key);
		}
		public static String getName(int value){
			return map.get(value)!=null?map.get(value).name:"Unknown Type";
		}
	}
	
	public enum SessionClosureAndOverrideActions{
		DM_ACTION(1),
		ACCT_STOP_ACTION (2),
		NONE(3),
		DM_AND_ACCT_STOP_ACTION(4);
		
		public final int type;
		private static final SessionClosureAndOverrideActions[] VALUES = values();
		private static final Map<Integer,SessionClosureAndOverrideActions> map;
		
		private SessionClosureAndOverrideActions(int type) {
			this.type = type;
		}
		
		static{
			map =  new HashMap<Integer,SessionClosureAndOverrideActions>();
			for(SessionClosureAndOverrideActions val:VALUES){
				map.put(val.type, val);
			}
		}
		
		public int getValue(){
			return this.type;
		}
		public static boolean isValid(int value){
			return map.containsKey(value);	
		}
		
		public static SessionClosureAndOverrideActions get(int key){
			return map.get(key);
		}
	}
	
	public void readConfiguration() throws LoadConfigurationException;
	
	public Long getSmInstanceId();
	
	public String getSmType() ;
	
	public String getSmInstName() ;
	
	public String getSmInstDesc() ;
	
	public long getDatabaseDsId() ;
	
	public String getCounterEnable() ;
	
	public String getTableName() ;
	
	public String getAutoSessionCloser() ;
	
	public long getSessionTimeout() ;

	public long getCloseBatchCount() ;

	public long getSessionThreadSleepTime() ;

	public long getStatusDuration() ;

	public long getExpiryReqLimitCount() ;
	
	public long getRemoteSMConfigId() ;

	public int getSessionCloseAction() ;
	
	public String getIdentityFeild() ;
	
	public String getIdSequenceName() ;

	public String getStartTimeFeild() ;
	
	public String getLastUpdateTimeFeild() ;
	
	public String getSessionIdFeild() ;

	public String getSessionIdReferenceEntity() ;

	public Map<Long, List<FieldMapping>> getFieldMappingMap() ;
	
	public String getGroupNameField() ;

	public String getServiceTypeField();
	
	public String getSearchCols() ;		
	
	public Map<Long, List<String>> getSmConfigEsiRelation() ;
	
	public Map<Long, List<String>> getSmConfigNAKEsiMap() ;
	
	public String getConfiguredUserIdentity() ;
	
	public String isBatchUpdateEnable();
	
	public int getBatchSize();
	
	public int getBatchUpdateInterval();
	
	public int getBatchUpdateDBQueryTimeout(); 
	
	public int getSessionManagerBehaviourType();

	/**
	 * This method returns the field mapping list based on the Session manager instance Id
	 * @param smInstanceId
	 * @return Returns the list of field mappings for the session manager if configuration read properly
	 */
	public List<FieldMapping> getFieldMappings(Long smInstanceId);
	
	public int getSessionOverrideAction();
	
	public String getSessionOverrideColumns();
}
