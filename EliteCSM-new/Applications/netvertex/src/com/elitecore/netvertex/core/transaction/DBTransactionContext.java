package com.elitecore.netvertex.core.transaction;

import java.sql.PreparedStatement;

import com.elitecore.core.commons.utilx.db.TransactionException;

public interface DBTransactionContext {
	PreparedStatement getPreparedStatement(String strQuery) throws TransactionException;
	PreparedStatement getPrepareStatement(String strQuery,String[] generatedKeys) throws TransactionException;
	void setRollback();
}
