package com.elitecore.aaa.radius.sessionx.conf.impl;


import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

@ReadOrder(order = { "configurableInstance"})
public class LocalSessionManagerConfiguration extends CompositeConfigurable{

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

	@XmlEnum
	public enum DBFailureActions {
		IGNORE {
			
			@Override
			public void forAuthentication(RadServiceRequest request, RadServiceResponse response, String smInstanceName) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(ConcurrencySessionManager.MODULE, "Applying failure behavior: IGNORE for session manager : " + smInstanceName + ".");
				}
			}
			
			@Override
			public void forAccounting(RadServiceRequest request, RadServiceResponse response, String smInstanceName) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(ConcurrencySessionManager.MODULE, "Applying failure behavior: IGNORE for session manager : " + smInstanceName + ".");
				}
			}
			
		},
		REJECT {
			@Override
			public void forAuthentication(RadServiceRequest request, RadServiceResponse response, String smInstanceName) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(ConcurrencySessionManager.MODULE, "Applying failure behavior: REJECT for session manager : " + smInstanceName + ".");
				}
				
				response.setFurtherProcessingRequired(false);
				response.setResponseMessage(AuthReplyMessageConstant.CONCURRENCY_FAILED_DUE_TO_DBOPERATION_FAILURE);			
				response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				response.setProcessingCompleted(true);
			}

			@Override
			public void forAccounting(RadServiceRequest request, RadServiceResponse response, String smInstanceName) {
				DROP.forAccounting(request, response, smInstanceName);
			}
		},
		DROP {
			@Override
			public void forAuthentication(RadServiceRequest request, RadServiceResponse response, String smInstanceName) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(ConcurrencySessionManager.MODULE, "Applying failure behavior: DROP for session manager : " + smInstanceName + ".");
				}
				
				response.setFurtherProcessingRequired(false);
				response.markForDropRequest();
				response.setProcessingCompleted(true);
			}

			@Override
			public void forAccounting(RadServiceRequest request, RadServiceResponse response, String smInstanceName) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(ConcurrencySessionManager.MODULE, "Applying failure behavior: DROP for session manager : " + smInstanceName + ".");
				}
				response.setFurtherProcessingRequired(false);
				response.markForDropRequest();
				response.setProcessingCompleted(true);
			}
		};
		
		private static final Map<String, DBFailureActions> nameToDBFailureActionMap;
		
		static {
			nameToDBFailureActionMap = new HashMap<String, DBFailureActions>();
			for(DBFailureActions failureAction: values()){
				nameToDBFailureActionMap.put(failureAction.name(), failureAction);
			}
		}
		
		public static DBFailureActions fromActionName(String actionName, String smInstanceName){
			DBFailureActions dbFailureAction = nameToDBFailureActionMap.get(actionName);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(ConcurrencySessionManager.MODULE, "Db Failure Action: " + actionName + " is applied for session manager: " + smInstanceName);
			}
			if(dbFailureAction == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(ConcurrencySessionManager.MODULE, "Configured action: " + actionName + " for session manager: " + smInstanceName + " is not supported, so applying default IGNORE failure action.");
				}
				dbFailureAction = DBFailureActions.IGNORE;
			}
			return dbFailureAction;
		}
		
		public abstract void forAccounting(RadServiceRequest request, RadServiceResponse response, String smInstanceName);
		public abstract void forAuthentication(RadServiceRequest request, RadServiceResponse response, String smInstanceName);
		
		/** This adapter will make case-insensitive conversion from String --> DBFailureActions **/
		public static class DBFailureActionsAdapter extends CaseInsensitiveEnumAdapter<DBFailureActions> {

			public DBFailureActionsAdapter() {
				super(DBFailureActions.class, DBFailureActions.IGNORE);
			}
		}
	}
	
	@Configuration private LocalSessionManagerConfigurable configurableInstance;
	private Map<String, LocalSessionManagerData> nameMap;
	private Map<String, SessionManagerData> idMap;
	
	@Deprecated
	public LocalSessionManagerConfiguration(ServerContext servercontext) {
		nameMap = new HashMap<String, LocalSessionManagerData>();
		idMap = new HashMap<String, SessionManagerData>();
	}
	
	
	public LocalSessionManagerConfiguration() {
		nameMap = new HashMap<String, LocalSessionManagerData>();
		idMap = new HashMap<String, SessionManagerData>();
	}
	
	@PostRead
	public void doProcessing(){
		for(LocalSessionManagerData localSessionManager : configurableInstance.getLocalSessionMangaerList()){
			nameMap.put(localSessionManager.getInstanceName(), localSessionManager);
			idMap.put(localSessionManager.getInstanceId(), localSessionManager);
		}
	}
	
	@Override
	public String toString() {
		return "";
	}
	
	public Map<String,SessionManagerData> getSessionMapBaseOnIdMap(){
		return idMap;
	}

	@Override
	public boolean isEligible(
			Class<? extends com.elitecore.core.commons.config.core.Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}
	@PostReload
	public void postReloadProcessing() {
	
	}
	@PostWrite
	public void postWriteProcessing() {

	}
}
