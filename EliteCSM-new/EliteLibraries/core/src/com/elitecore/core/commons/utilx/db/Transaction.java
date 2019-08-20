package com.elitecore.core.commons.utilx.db;

public interface Transaction extends DBTransaction {
	void commit() throws TransactionException;
	void rollback();
	void setNoWait();
	void setBatch();
	boolean wasCommitted();
	boolean wasRolledBack();
}
