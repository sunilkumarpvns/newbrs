package com.elitecore.corenetvertex.core.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DummyTransaction implements Transaction{

    private Connection connection;
    private String datasourceName;

    public DummyTransaction(Connection connection, String datasourceName) {
	this.connection = connection;
	this.datasourceName = datasourceName;
	
    }
    
    @Override
    public void begin() throws TransactionException {
	
    }

    @Override
    public void end() throws TransactionException {
	try {
	    connection.close();
	} catch (SQLException e) {
	   throw new TransactionException(e);
	}
    }

    @Override
    public void commit() throws TransactionException {
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

    @Override
    public PreparedStatement prepareStatement(String query) throws TransactionException {
	try {
	    return connection.prepareStatement(query);
	} catch (SQLException e) {
	    throw new TransactionException(e);
	}
	
    }

    @Override
    public PreparedStatement prepareStatement(String query, String[] generatedKeys) throws TransactionException {
	return null;
    }

    @Override
    public boolean isBegin() {
	return false;
    }

    @Override
    public void rollback() {
	
    }

    @Override
    public void setTimeout() {
	
    }

    @Override
    public void setNoWait() {
	
    }

    @Override
    public void setBatch() {
	
    }

    @Override
    public boolean wasCommitted() {
	return false;
    }

    @Override
    public boolean wasRolledBack() {
	return false;
    }

    @Override
    public void markDead() {
	
    }

    @Override
	public Statement statement() throws TransactionException {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

    @Override
    public boolean isDBDownSQLException(SQLException ex) {
	return false;
    }

    @Override
    public String getDataSourceName() {
	return datasourceName;
    }
}
