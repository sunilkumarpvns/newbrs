package com.elitecore.core.commons.utilx;

import com.elitecore.core.commons.utilx.db.DBTransaction;
import com.elitecore.core.commons.utilx.db.TransactionException;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DBTransactionSupport implements DBTransaction {

    protected Statement statement(@Nullable Connection connection) throws TransactionException {
        if (connection == null) {
            throw new TransactionException("Error in creating statement. Reason: Connection is null");
        }
        try {
            return connection.createStatement();
        } catch (SQLException sqlException) {
            handleSQLException(sqlException, "Error in creating Statement. Reason: ");
        }
        return null;
    }

    protected PreparedStatement prepareStatement(@Nullable Connection connection, String query) throws TransactionException {
        if (connection == null) {
            throw new TransactionException("Error in creating prepared statement. Reason: Connection is null");
        }
        try {
            return connection.prepareStatement(query);
        } catch (SQLException sqlException) {
            handleSQLException(sqlException, "Error in creating prepared statement. Reason: ");
        }
        return null;
    }

    protected PreparedStatement prepareStatement(Connection connection, String query, String[] generatedKeys) throws TransactionException {
        if (connection == null) {
            throw new TransactionException("Error in creating prepared statement. Reason: Connection is null");
        }
        try {
            return connection.prepareStatement(query, generatedKeys);
        } catch (SQLException sqlException) {
            handleSQLException(sqlException, "Error in creating prepared statement. Reason: ");
        }
        return null;
    }

    public void endConnection(@Nullable Connection connection) throws TransactionException {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException se) {
            if (isDBDownSQLException(se)) {
                markDead();
            }
            handleSQLException(se, "Error in closing connection. Reason:");
        }
    }

    protected void handleSQLException(SQLException sqlException, String exceptionMessage) throws TransactionException {
        if (isDBDownSQLException(sqlException)) {
            markDead();
        }
        throw new TransactionException(exceptionMessage + sqlException.getMessage(), sqlException);
    }
}
