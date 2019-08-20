package com.elitecore.aaa.radius.plugins.userstatistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.util.db.DBConnectionManager;

public class TableUserStatisticsSchema {

	public static final String SCHEMA_QUERY = "CREATE TABLE TBLUSERSTATISTICS("
			+ "USERSTATISTICSID          VARCHAR(36),"
			+ "CREATE_DATE               TIMESTAMP,"
			+ "USER_NAME                 VARCHAR(253),"
			+ "REPLY_MESSAGE             VARCHAR(253),"
			+ "NAS_IP_ADDRESS            VARCHAR(20),"
			+ "NAS_PORT                  NUMERIC(10),"
			+ "SERVICE_TYPE              VARCHAR(50),"
			+ "FRAMED_PROTOCOL           VARCHAR(50),"
			+ "FRAMED_IP_ADDRESS         VARCHAR(20),"
			+ "FRAMED_IP_NETMASK         VARCHAR(20),"
			+ "FRAMED_ROUTING            VARCHAR(50),"
			+ "FILTER_ID                 VARCHAR(50),"
			+ "FRAMED_MTU                NUMERIC(10),"
			+ "CALLBACK_NUMBER           VARCHAR(50),"
			+ "CALLBACK_ID               VARCHAR(50),"
			+ "FRAMED_ROUTE              VARCHAR(50),"
			+ "SESSION_TIMEOUT           NUMERIC(10),"
			+ "IDLE_TIMEOUT              NUMERIC(10),"
			+ "TERMINATION_ACTION        VARCHAR(50),"
			+ "CALLED_STATION_ID         VARCHAR(253),"
			+ "CALLING_STATION_ID        VARCHAR(253),"
			+ "NAS_IDENTIFIER            VARCHAR(50),"
			+ "PROXY_STATE               NUMERIC(10),"
			+ "NAS_PORT_TYPE             VARCHAR(50),"
			+ "PORT_LIMIT                NUMERIC(10),"
			+ "EVENT_TIMESTAMP           NUMERIC(14),"
			+ "NAS_PORT_ID               VARCHAR(50),"
			+ "CONNECT_INFO              VARCHAR(253),"
			+ "PARAM_STR0                VARCHAR(253),"
			+ "PARAM_STR1                VARCHAR(253),"
			+ "PARAM_STR2                VARCHAR(253),"
			+ "PARAM_STR3                VARCHAR(253),"
			+ "PARAM_STR4                VARCHAR(253),"
			+ "PARAM_STR5                VARCHAR(253),"
			+ "PARAM_STR6                VARCHAR(253),"
			+ "PARAM_STR7                VARCHAR(253),"
			+ "PARAM_STR8                VARCHAR(253),"
			+ "PARAM_STR9                VARCHAR(253),"
			+ "PARAM_INT0                NUMERIC,"
			+ "PARAM_INT1                NUMERIC,"
			+ "PARAM_INT2                NUMERIC,"
			+ "PARAM_INT3                NUMERIC,"
			+ "PARAM_INT4                NUMERIC,"
			+ "PARAM_DATE0              TIMESTAMP,"
			+ "PARAM_DATE1              TIMESTAMP,"
			+ "PARAM_DATE2              TIMESTAMP,"
			+ "GROUPNAME                 VARCHAR(60),"
			+ "USER_IDENTITY             VARCHAR(100),"
			+ "ESN_MEID 				  	VARCHAR(16),"
			+ "CUI 	 				  	VARCHAR(64),"
			+ "HA_IP					  	VARCHAR(64),"
			+ "BS_ID					  	VARCHAR(64),"
			+ "PCF_SGSN_AGW 				VARCHAR(64),"
			+ "NAP_OPERATOR_CARRIER 		VARCHAR(64),"
			+ "LOCATION			        VARCHAR(255))";


	public static final String USERSTATSTICS_SEQUENCE = "CREATE SEQUENCE SEQ_USERSTATISTICS "
			+ "INCREMENT BY 1 "
			+ "START WITH 50 ";


	private DBConnectionManager connectionManager;

	public TableUserStatisticsSchema(DBConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
	
	public void create() throws Exception {
		executeStatement(SCHEMA_QUERY);
	}

	public void truncate() throws SQLException {
		executeStatement("TRUNCATE TABLE TBLUSERSTATISTICS");
	}

	public void drop() throws SQLException {
		executeStatement("DROP TABLE TBLUSERSTATISTICS");
	}

	public int count() throws SQLException {
		Connection connection = connectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement("select COUNT(*) from TBLUSERSTATISTICS");
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			return resultSet.getInt(1);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	private void executeStatement(String statement) throws SQLException {
		Connection connection = connectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(statement);
			preparedStatement.execute();
			connection.commit();
		} finally {
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}

	}
}
