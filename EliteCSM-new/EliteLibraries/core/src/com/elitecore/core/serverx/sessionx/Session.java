package com.elitecore.core.serverx.sessionx;

import java.util.List;

import javax.annotation.Nullable;

public interface Session {
	
	/**
	 * Executes insert database operation on data provided by session data
	 * 
	 * @param sessionData
	 * @return number of rows updated or -1 in case of failure
	 */
	int save(SessionData sessionData);
	
	/**
	 * Executes update database operation on data provided by session data
	 * 
	 * @param sessionData
	 * @return number of rows updated or -1 in case of failure
	 */
	int update(SessionData sessionData);
	
	/**
	 * Executes update database operation with applied criteria on data provided by session data
	 * 
	 * @param sessionData
	 * @return number of rows updated or -1 in case of failure
	 */
	int update(SessionData sessionData,Criteria criteria);
	
	/**
	 * Executes delete database operation on data provided by session data
	 * 
	 * @param sessionData
	 * @return number of rows updated or -1 in case of failure
	 */
	int delete(SessionData sessionData);
	
	/**
	 * Executes delete database operation with applied criteria
	 * 
	 * @param sessionData
	 * @return number of rows updated or -1 in case of failure
	 */
	int delete(Criteria criteria);
	
	/**
	 * Executes truncate database operation on supplied table name
	 * 
	 * @param sessionData
	 * @return number of rows updated or -1 in case of failure
	 */
	int truncate(String tableName);
	
	/**
	 * Executes select operation with supplied criteria
	 * 
	 * @param criteria
	 * @return List of Session Data or null in case of DB Failuer  
	 */
	@Nullable List<SessionData> list(Criteria criteria);
	
	/**
	 * Executes select operation with supplied criteria and returns the numbers
	 * of rows received
	 * 
	 * @param sessionData
	 * @return number of rows updated or -1 in case of failure
	 */
	int count(Criteria criteria);
	Criteria createCriteria(String schemaName);

}
