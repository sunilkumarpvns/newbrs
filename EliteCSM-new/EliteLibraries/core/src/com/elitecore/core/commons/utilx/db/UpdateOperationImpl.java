package com.elitecore.core.commons.utilx.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.utilx.DBTransactionSupport;

public abstract class UpdateOperationImpl extends DBTransactionSupport implements Transaction{
	protected Connection connection;
	private boolean isBeginCalled = false;
	private String commitCmd = null;
	private boolean rollbacked = false;
	private boolean commited = false;
	
	@Override
	public void begin() throws TransactionException{
		if(isBeginCalled){
			return;
		}
		isBeginCalled = true;
		try {
			connection = getConnection();
			
			if(connection != null){
				connection.setAutoCommit(false);
			}else{
				throw new TransactionException("Error in getting connection object" , TransactionErrorCode.CONNECTION_NOT_FOUND);
			}
		} catch (SQLException e) {
			if(isDBDownSQLException(e)){
				markDead();
			}
			throw new TransactionException("Error in begin transaction, Reason: " + e.getMessage(),e);
		}
	}

	@Override
	public void end() throws TransactionException{
		if(!isBeginCalled){
			throw new TransactionException("Transaction end called before begin");
		}
		if(connection == null){
			return;
		}
		PreparedStatement pstmtCommit = null;
		try {
			if (wasRolledBack()) {
				connection.rollback();
			} else {

				if (commitCmd != null) {
					pstmtCommit = connection.prepareStatement(commitCmd);
					pstmtCommit.executeUpdate();
				} else {
					connection.commit();
				}
				commited = true;
			}
		} catch (SQLException e) {
			if (isDBDownSQLException(e)) {
				markDead();
			}
			throw new TransactionException("Problem during ending transaction, Reason: " + e.getMessage(), e);
		} finally {
			DBUtility.closeQuietly(pstmtCommit);
			endConnection(connection);
		}
	}

	@Override
	public void commit() throws TransactionException {
		if(connection != null){
			PreparedStatement pstmtCommit = null;
			try{
				if(commitCmd != null){				
					pstmtCommit = connection.prepareStatement(commitCmd);
					pstmtCommit.executeUpdate();
				}else{					
					connection.commit();
				}
			}catch(SQLException e){
				if(isDBDownSQLException(e)){
					markDead();
				}
				throw new TransactionException("Problem during ending transaction, Reason: " + e.getMessage(),e);
			}finally{
				DBUtility.closeQuietly(pstmtCommit);
			}
		}else{ 
			throw new TransactionException("Connection is null");		
		}
	}
	
	
	@Override
	public Statement statement() throws TransactionException {
		return super.statement(connection);
	}
	
	@Override
	public PreparedStatement prepareStatement(String query) throws TransactionException {
		return super.prepareStatement(connection, query);
	}
	
	@Override
	public PreparedStatement prepareStatement(String query,String[] generatedKeys) throws TransactionException {
		return super.prepareStatement(connection, query, generatedKeys);
	}
	
	@Override
	public boolean isBegin() {
		return isBeginCalled;
	}

	@Override
	public void rollback() {
		rollbacked = true;
	}
	
	@Override
	public void setNoWait(){
		if(commitCmd == null){			
			commitCmd = "COMMIT WORK WRITE NOWAIT ";
		}else{
			commitCmd += " NOWAIT ";
		}
 	}
	
	@Override
	public void setBatch(){
		if(commitCmd == null){
			commitCmd = "COMMIT WORK WRITE BATCH ";
		}else{
			commitCmd += " BATCH ";	
		}		
	}
	
	@Override
	public void setTimeout() {
	}

	@Override
	public boolean wasCommitted() {
		return commited;
	}

	@Override
	public boolean wasRolledBack() {
		return rollbacked;
	}
	
	protected abstract Connection getConnection() throws DataSourceException;
}
