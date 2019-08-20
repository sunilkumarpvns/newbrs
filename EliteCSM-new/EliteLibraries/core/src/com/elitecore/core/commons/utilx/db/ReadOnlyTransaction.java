package com.elitecore.core.commons.utilx.db;

import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.utilx.DBTransactionSupport;
import com.elitecore.core.commons.utilx.db.DBTransaction;
import com.elitecore.core.commons.utilx.db.TransactionErrorCode;
import com.elitecore.core.commons.utilx.db.TransactionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ReadOnlyTransaction extends DBTransactionSupport implements DBTransaction {
	protected Connection connection;
	private boolean isBeginCalled = false;


	@Override
	public void begin() throws TransactionException{
		if(isBeginCalled){
			return;
		}
		isBeginCalled = true;
		try {
			connection = getConnection();

			if(connection != null){
				connection.setReadOnly(true);
				connection.setAutoCommit(false);
			}else{
				throw new TransactionException("Error in getting connection" , TransactionErrorCode.CONNECTION_NOT_FOUND);
			}
		} catch (SQLException e) {
			handleSQLException(e, "Error in begin transaction. Reason: ");
		}
	}

	@Override
	public void end() throws TransactionException{
		if(!isBeginCalled){
			throw new TransactionException("Transaction end called before begin");
		}

		if(connection != null) {
			try {
				connection.setReadOnly(false);
			} catch (SQLException e) {
				handleSQLException(e, "Error in ending transaction. Reason: " );
			}
		}

		endConnection(connection);
	}



	@Override
	public Statement statement() throws TransactionException {
		return statement(connection);
	}
	
	@Override
	public PreparedStatement prepareStatement(String query) throws TransactionException {
		return prepareStatement(connection, query);
	}
	
	@Override
	public PreparedStatement prepareStatement(String query,String[] generatedKeys) throws TransactionException {
		return prepareStatement(connection, query, generatedKeys);
	}
	
	@Override
	public boolean isBegin() {
		return isBeginCalled;
	}

 	@Override
	public void setTimeout() {
	}

	protected abstract Connection getConnection() throws DataSourceException;


}
