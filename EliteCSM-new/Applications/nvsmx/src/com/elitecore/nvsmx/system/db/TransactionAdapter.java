package com.elitecore.nvsmx.system.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.utilx.db.UpdateOperationImpl;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;

public class TransactionAdapter extends TransactionAdapterSupport implements Transaction {

	private com.elitecore.core.commons.utilx.db.Transaction transaction;
	private String dataSourceName;
	
	
	public TransactionAdapter(String dataSourceName) {
		this.dataSourceName = dataSourceName;
		this.transaction = new UpdateOperationImpl() {
			
			@Override
			public void markDead() {
				TransactionAdapter.this.markDead();
			}
			
			@Override
			public boolean isDBDownSQLException(SQLException ex) {
				return TransactionAdapter.this.isDBDownSQLException(ex);
			}
			
			@Override
			protected Connection getConnection() throws DataSourceException {
				DBConnectionManager manager = DBConnectionManager.getInstance(TransactionAdapter.this.dataSourceName);
				try {
					return manager.getConnection();
				} catch (SQLException e) {
					throw DataSourceException.newException(e,manager.getVendor());
				}
			}
		};
		
	}

	public TransactionAdapter(com.elitecore.core.commons.utilx.db.Transaction transaction, String dataSourceName) {
		this.transaction = transaction;
		this.dataSourceName = dataSourceName;
	}

	@Override
	public void begin() throws TransactionException {
		begin(transaction);
	}

	@Override
	public void end() throws TransactionException {
		end(transaction);
	}

	@Override
	public void commit() throws TransactionException {
		try {
			transaction.commit();
		} catch (com.elitecore.core.commons.utilx.db.TransactionException e) {
			throw new TransactionException(e);
		}
	}

	@Override
	public PreparedStatement prepareStatement(String query)	throws TransactionException {
		return prepareStatement(transaction, query);
	}

	@Override
	public PreparedStatement prepareStatement(String query, String[] generatedKeys) throws TransactionException {
		return prepareStatement(transaction, query, generatedKeys);
	}

	@Override
	public Statement statement() throws TransactionException {
		return statement(transaction);
	}

	@Override
	public boolean isBegin() {
		return transaction.isBegin();
	}

	@Override
	public void rollback() {
		transaction.rollback();
	}

	@Override
	public void setTimeout() {
		transaction.setTimeout();
	}
	
	
	
	@Override
	public void setNoWait(){
		transaction.setNoWait();
 	}
	
	@Override
	public void setBatch(){
		transaction.setBatch();
	}

	@Override
	public boolean wasCommitted() {
		return transaction.wasCommitted();
	}

	@Override
	public boolean wasRolledBack() {
		return transaction.wasRolledBack();
	}

	@Override
	public void markDead() {
		transaction.markDead();
	}

	@Override
	public boolean isDBDownSQLException(SQLException ex) {
		return transaction.isDBDownSQLException(ex);
	}

	@Override
	public String getDataSourceName() {
		return dataSourceName;
	}


}
