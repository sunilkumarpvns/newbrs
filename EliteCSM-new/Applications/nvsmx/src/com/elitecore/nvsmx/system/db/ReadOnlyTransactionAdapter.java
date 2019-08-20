package com.elitecore.nvsmx.system.db;

import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.utilx.db.ReadOnlyTransaction;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ReadOnlyTransactionAdapter extends TransactionAdapterSupport implements DBTransaction {
	private com.elitecore.core.commons.utilx.db.DBTransaction transaction;
	private String dataSourceName;


	public ReadOnlyTransactionAdapter(String dataSourceName) {
		this.dataSourceName = dataSourceName;
		transaction = new ReadOnlyTransaction() {
			
			@Override
			public void markDead() {
				ReadOnlyTransactionAdapter.this.markDead();
			}
			
			@Override
			public boolean isDBDownSQLException(SQLException ex) {
				return ReadOnlyTransactionAdapter.this.isDBDownSQLException(ex);
			}
			
			@Override
			protected Connection getConnection() throws DataSourceException {
				DBConnectionManager manager = DBConnectionManager.getInstance(ReadOnlyTransactionAdapter.this.dataSourceName);
				try {
					return manager.getConnection();
				} catch (SQLException e) {
					throw DataSourceException.newException(e,manager.getVendor());
				}
			}
		};
		
	}

	public ReadOnlyTransactionAdapter(com.elitecore.core.commons.utilx.db.DBTransaction transaction, String dataSourceName) {
		this.dataSourceName = dataSourceName;
		this.transaction = transaction;

	}

	public void begin() throws TransactionException {
		begin(transaction);
	}

	public void end() throws TransactionException {
		super.end(transaction);
	}

	public PreparedStatement prepareStatement(String query)	throws TransactionException {
		return  super.prepareStatement(transaction, query);
	}

	public PreparedStatement prepareStatement(String query, String[] generatedKeys) throws TransactionException {
		return super.prepareStatement(transaction, query, generatedKeys);
	}

	public Statement statement() throws TransactionException {
		return super.statement(transaction);
	}

	public boolean isBegin() {
		return transaction.isBegin();
	}

	public void setTimeout() {
		transaction.setTimeout();
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
