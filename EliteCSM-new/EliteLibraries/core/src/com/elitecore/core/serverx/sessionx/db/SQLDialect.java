package com.elitecore.core.serverx.sessionx.db;

import java.sql.PreparedStatement;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;

public interface SQLDialect {
	void init()throws InitializationFailedException;
	String getInsertQuery(SessionData sessionData);
	String getUpdateQuery(SessionData sessionData);
	String getUpdateQuery(SessionData sessionData,Criteria criteria);
	String getDeleteQuery(SessionData sessionData);
	String getDeleteQuery(Criteria criteria);
	String getSelectQuery(Criteria criteria);
	String getSessionCloseQuery(String schemaName);
	String getTruncateQuery(String tableName);
	String getCountQuery(Criteria criteria);
	
	void setPreparedStatementForSelect(Criteria criteria,PreparedStatement preparedStatement)
	throws SessionException;
	void setPreparedStatementForDelete(Criteria criteria,PreparedStatement preparedStatement)
	throws SessionException;
	void setPreparedStatementForInsert(SessionData sessionData,PreparedStatement preparedStatement)
		throws SessionException;
	void setPreparedStatementForUpdate(SessionData sessionData,PreparedStatement preparedStatement)
	throws SessionException;
	void setPreparedStatementForUpdate(SessionData sessionData,Criteria criteria,PreparedStatement preparedStatement)
	throws SessionException;	
	void setPreparedStatementForDelete(SessionData sessionData,PreparedStatement preparedStatement)
	throws SessionException;
	void setPreparedStatementForCount(Criteria criteria, PreparedStatement preparedStatement);
}
