package com.elitecore.netvertex.core.transaction;

import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;

import java.sql.PreparedStatement;
import java.sql.Statement;

public abstract class TransactionAdapterSupport implements DBTransaction {

    public void begin(com.elitecore.core.commons.utilx.db.DBTransaction transaction) throws TransactionException {
        try {
            transaction.begin();
        } catch (com.elitecore.core.commons.utilx.db.TransactionException e) {
            throw new TransactionException(e);
        }
    }

    public void end(com.elitecore.core.commons.utilx.db.DBTransaction transaction) throws TransactionException {
        try {
            transaction.end();
        } catch (com.elitecore.core.commons.utilx.db.TransactionException e) {
            throw new TransactionException(e);
        }
    }

    public PreparedStatement prepareStatement(com.elitecore.core.commons.utilx.db.DBTransaction transaction, String query)	throws TransactionException {
        try {
            return transaction.prepareStatement(query);
        } catch (com.elitecore.core.commons.utilx.db.TransactionException e) {
            throw new TransactionException(e);
        }
    }

    public PreparedStatement prepareStatement(com.elitecore.core.commons.utilx.db.DBTransaction transaction, String query, String[] generatedKeys) throws TransactionException {
        try {
            return transaction.prepareStatement(query,generatedKeys);
        } catch (com.elitecore.core.commons.utilx.db.TransactionException e) {
            throw new TransactionException(e);
        }
    }

    public Statement statement(com.elitecore.core.commons.utilx.db.DBTransaction transaction) throws TransactionException {
        try {
            return transaction.statement();
        } catch (com.elitecore.core.commons.utilx.db.TransactionException e) {
            throw new TransactionException(e);
        }
    }

}
