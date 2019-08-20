package com.elitecore.corenetvertex.core.transaction;

import com.elitecore.corenetvertex.spr.DummyDBDataSource;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.mockito.Mockito.spy;


public class DummyTransactionFactory implements TransactionFactory {

    private DummyDBDataSource dbDataSource;
    private Connection connection;

    public DummyTransactionFactory(DummyDBDataSource dbDataSource) {
	this.dbDataSource = dbDataSource;
    }

    public static DummyTransactionFactory createTransactionFactory(String ssid) {
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", "Test-DS", "jdbc:h2:mem:" + ssid, "", "", 1, 5000, 3000);
        DummyTransactionFactory transactionFactory = spy(new DummyTransactionFactory(dbDataSource));
        transactionFactory.createTransaction();
        return transactionFactory;
    }

    @Override
    public Transaction createTransaction() {
	try {
	    connection = DriverManager.getConnection(dbDataSource.getConnectionURL(), dbDataSource.getUsername(), dbDataSource.getPassword());
	} catch (SQLException e) {	}
	
	if (connection == null) {
	    return null;
	}
	return new DummyTransaction(connection, dbDataSource.getDataSourceName());
    }

    @Nullable
    @Override
    public DBTransaction createReadOnlyTransaction() {
        try {
            connection = DriverManager.getConnection(dbDataSource.getConnectionURL(), dbDataSource.getUsername(), dbDataSource.getPassword());
        } catch (SQLException e) {	}

        if (connection == null) {
            return null;
        }
        return new DummyTransaction(connection, dbDataSource.getDataSourceName());
    }

    @Override
    public boolean isAlive() {
	return connection == null ? false : true;
    }
    
    public Connection getConnection() {
	return connection;
    }
}
