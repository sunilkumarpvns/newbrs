package com.elitecore.corenetvertex.core.transaction;

public interface Transaction extends DBTransaction {
	void commit() throws TransactionException;
	void rollback();
	void setNoWait();
	void setBatch();
	boolean wasCommitted();
	boolean wasRolledBack();
}