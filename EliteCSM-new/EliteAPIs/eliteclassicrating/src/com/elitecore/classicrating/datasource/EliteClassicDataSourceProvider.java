/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Raghu G
 *  Last Modified October 1, 2008
 */

/*
 * EliteClassicDataSourceProvider.java
 * This class provides implementation of DataSourceProvider, DataSource, Connection.
 * It contains declaration of inner classes that implements related interface with 
 * definition for the methods declared in the interface.
 * It used oracle data source to create a connection to oracle server.
 * 
 */
package com.elitecore.classicrating.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import oracle.jdbc.pool.OracleDataSource;

import com.elitecore.classicrating.base.IRatingConstants;
import com.elitecore.classicrating.commons.util.logger.Log4jLogger;

public class EliteClassicDataSourceProvider implements IDataSourceProvider {

    private Log4jLogger Logger;

    public EliteClassicDataSourceProvider() {
        Logger = Log4jLogger.getInstance();
    }
    IRatingDBConnection connection;

    public IRatingDataSource getRatingDataSource() throws SQLException {

        return (new EliteOracleDataSource());
    }

    public class EliteOracleDataSource implements IRatingDataSource {

        public EliteOracleDataSource() {
        }

        public void close(IRatingDBConnection connection) throws SQLException {
            if (connection != null) {
                connection.close();
            }
        }

        public IRatingDBConnection getConnection() throws SQLException {
            DatabaseConnectionPool connectionPool = new DatabaseConnectionPool();
            Connection connection = connectionPool.getConnection();
            return (new EliteClassicRatingDBConnection(connection));

        }
    }

    public class EliteClassicRatingDBConnection implements IRatingDBConnection {

        private Connection connection;

        public EliteClassicRatingDBConnection(Connection connection) throws SQLException {
            this.connection = connection;
        }

        public void close() throws SQLException {
            if (connection != null) {
                connection.close();
            }
        }

        public void closePreparedStatement(PreparedStatement stmt)
                throws SQLException {
            stmt.close();

        }

        public void closePreparedStatement(PreparedStatement stmt, String key)
                throws SQLException {
            stmt.close();

        }

        public void closeStatement(Statement stmt) throws SQLException {
            stmt.close();
        }

        public void commit() throws SQLException {
            connection.commit();
        }

        public Statement createStatement() throws SQLException {
            return (connection.createStatement());
        }

        public PreparedStatement prepareStatement(String query) throws SQLException {

            return (connection.prepareStatement(query));
        }

        public PreparedStatement prepareStatement(String query, String key)
                throws SQLException {
            return (connection.prepareStatement(query));
        }

        public void rollback() throws SQLException {
            connection.rollback();
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            connection.setAutoCommit(autoCommit);
        }

        public void setTransactionIsolation(int level) throws SQLException {
            connection.setTransactionIsolation(level);
        }
    }

    public class DatabaseConnectionPool {

        private static final String MODULE = "DB Connection Manager";
        private OracleDataSource dataSource;
        private String url;
        private String username;
        private String password;

        public DatabaseConnectionPool() throws SQLException {

            HashMap<String, String> parameterMap = new HashMap<String, String>();
            url = "jdbc:oracle:thin:@192.168.14.101:1521:eliteaaa";
            username = "crestelaaapoc501";
            password = "crestelaaapoc501";
            parameterMap.put(IRatingConstants.CONNECTION_URL, url);
            parameterMap.put(IRatingConstants.USER_NAME, username);
            parameterMap.put(IRatingConstants.PASSWORD, password);


            try {
                dataSource = new OracleDataSource();

                dataSource.setURL(parameterMap.get(IRatingConstants.CONNECTION_URL));
                dataSource.setUser(parameterMap.get(IRatingConstants.USER_NAME));
                dataSource.setPassword(parameterMap.get(IRatingConstants.PASSWORD));



            } catch (Exception e) {
                Logger.error(MODULE, "Error during database initialization process, " + e.getMessage());
                Logger.trace(MODULE, e);
            }

        }

        public Connection getConnection() throws SQLException {

            Connection connection = null;
            try {
                if (dataSource != null) {
                    connection = dataSource.getConnection();
                }
            } catch (SQLException e) {
                Logger.error(MODULE, "Could not get database connection, reason : " + e.getMessage());
                Logger.trace(MODULE, e);
            }
            return connection;
        }

        public void close() throws SQLException {
            if (dataSource != null) {
                dataSource.close();
            }

        }
    }
}
