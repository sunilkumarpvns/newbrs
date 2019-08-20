package com.elitecore.nvsmx.system.spr;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;

public class TransactionAdapter implements Transaction {

	private final com.elitecore.core.commons.utilx.db.Transaction transaction;
	private String dataSourceName;

	public TransactionAdapter(com.elitecore.core.commons.utilx.db.Transaction transaction, String dataSourceName) {
		this.transaction = transaction;
		this.dataSourceName = dataSourceName;
	}

	@Override
	public void begin() throws TransactionException {
		try {
			transaction.begin();
		} catch (final com.elitecore.core.commons.utilx.db.TransactionException e) {
			throw new TransactionException(e.getMessage(), e, TransactionErrorCode.fromVal(e.getErrorCode().errorCause));
		}
	}

	@Override
	public void end() throws TransactionException {
		try {
			transaction.end();
		} catch (final com.elitecore.core.commons.utilx.db.TransactionException e) {
			throw new TransactionException(e.getMessage(), e, TransactionErrorCode.fromVal(e.getErrorCode().errorCause));
		}

	}

	@Override
	public void commit() throws TransactionException {
		try {
			transaction.commit();
		} catch (final com.elitecore.core.commons.utilx.db.TransactionException e) {
			throw new TransactionException(e.getMessage(), e, TransactionErrorCode.fromVal(e.getErrorCode().errorCause));
		}
	}

	@Override
	public PreparedStatement prepareStatement(String query)
			throws TransactionException {
		try {
			return transaction.prepareStatement(query);
		} catch (final com.elitecore.core.commons.utilx.db.TransactionException e) {
			throw new TransactionException(e.getMessage(), e, TransactionErrorCode.fromVal(e.getErrorCode().errorCause));
		}

	}

	@Override
	public PreparedStatement prepareStatement(String query,
			String[] generatedKeys) throws TransactionException {
		try {
			return transaction.prepareStatement(query, generatedKeys);
		} catch (final com.elitecore.core.commons.utilx.db.TransactionException e) {
			throw new TransactionException(e.getMessage(), e, TransactionErrorCode.fromVal(e.getErrorCode().errorCause));
		}
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
	public void setNoWait() {
		transaction.setNoWait();
	}

	@Override
	public void setBatch() {
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
	public Statement statement() throws TransactionException {
		try {
			return transaction.statement();
		} catch (final com.elitecore.core.commons.utilx.db.TransactionException e) {
			throw new TransactionException(e.getMessage(), e, TransactionErrorCode.valueOf(e.getErrorCode().errorCause));
		}
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
