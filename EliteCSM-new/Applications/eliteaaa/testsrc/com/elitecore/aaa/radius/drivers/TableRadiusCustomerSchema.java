package com.elitecore.aaa.radius.drivers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;

/**
 * @author narendra.pathai
 */
public class TableRadiusCustomerSchema {

    public static final String SCHEMA_QUERY = "CREATE TABLE TBLRADIUSCUSTOMER (" +
            "                            ID                     NUMERIC," +
            "                            USERNAME               VARCHAR(255)," +
            "                            PASSWORD               VARCHAR(255)," +
            "                            ENCRYPTIONTYPE         NUMERIC(3)," +
            "                            CUSTOMERSTATUS         VARCHAR(10)," +
            "                            EXPIRYDATE            TIMESTAMP," +
            "                            CUSTOMERREJECTITEM     VARCHAR(4000)," +
            "                            CUSTOMERCHECKITEM      VARCHAR(4000)," +
            "                            CUSTOMERREPLYITEM      VARCHAR(4000)," +
            "                            CREDITLIMIT            NUMERIC," +
            "                            CONCURRENTLOGINPOLICY  VARCHAR(50)," +
            "                            ACCESSPOLICY           VARCHAR(50)," +
            "                            RADIUSPOLICY VARCHAR(1000)," +
            "                            DIAMETERPOLICY VARCHAR(1000)," +
            "                            ADDITIONALPOLICY    VARCHAR(1000)," +
            "                            IPPOOLNAME             VARCHAR(50)," +
            "                            PARAM1                 VARCHAR(255)," +
            "                            PARAM2                 VARCHAR(255)," +
            "                            PARAM3                 VARCHAR(255)," +
            "                            PARAM4                 VARCHAR(255)," +
            "                            PARAM5                 VARCHAR(255)," +
            "                            GROUPNAME              VARCHAR(60)," +
            "                            CUSTOMERALTEMAILID     VARCHAR(50)," +
            "                            CUSTOMERSERVICES       VARCHAR(1000)," +
            "                            CUSTOMERTYPE           VARCHAR(50)," +
            "                            PASSWORDCHECK          VARCHAR(50)," +
            "                            CALLEDSTATIONID        VARCHAR(100)," +
            "                            CALLINGSTATIONID       VARCHAR(100)," +
            "                            USER_IDENTITY          VARCHAR(255) NOT NULL," +
            "                            CUI         VARCHAR(50)," +
            "                            HOTLINEPOLICY          VARCHAR(1000)," +
            "                            GRACEPERIOD            VARCHAR(1000)," +
            "                            CALLBACKID             VARCHAR(100)," +
            "                            BANDWIDTH              NUMERIC(20)," +
            "                            DYNAMICCHECKITEM       VARCHAR(4000)," +
            "                            MACVALIDATION VARCHAR(50)," +
            "                            IMSI VARCHAR(100)," +
            "                            MEID  VARCHAR(100)," +
            "                            MSISDN  VARCHAR(100)," +
            "                            MDN  VARCHAR(100)," +
            "                            IMEI VARCHAR(100)," +
            "                            DEVICEVENDOR          VARCHAR(255)," +
            "                            DEVICENAME            VARCHAR(255)," +
            "                            DEVICEPORT            VARCHAR(255)," +
            "                            GEOLOCATION           VARCHAR(255)," +
            "                            DEVICEVLAN            VARCHAR(255)," +
            "                            FRAMEDIPV4ADDRESS VARCHAR(255)," +
            "                            FRAMEDIPV6PREFIX      VARCHAR(255)," +
            "                            FRAMEDPOOL            VARCHAR(255)," +
            "                            RADIUSPOLICYGROUP     VARCHAR(255)," +
            "                            DIAMETERPOLICYGROUP   VARCHAR(255)," +
            "                            CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "                            LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    public void create(Connection connection) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SCHEMA_QUERY);
            statement.execute();
            connection.commit();
        } finally {
            DBUtility.closeQuietly(statement);
        }
    }

    public void insert(Connection connection, RadiusCustomerInsertQueryBuilder builder) throws SQLException {
    	executeStatement(connection, builder.build());
    }

    public static RadiusCustomerInsertQueryBuilder radiusCustomer() {
        return new RadiusCustomerInsertQueryBuilder();
    }
    public static class RadiusCustomerInsertQueryBuilder {
        private Map<String, Object> columnNameToValue = new HashMap<String, Object>();

        public RadiusCustomerInsertQueryBuilder withUserName(String userName) {
            columnNameToValue.put("USER_IDENTITY", userName);
            columnNameToValue.put("USERNAME", userName);
            return this;
        }

        public String build() {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("INSERT INTO TBLRADIUSCUSTOMER ")
                    .append("(")
                        .append(Strings.join(",", columnNameToValue.keySet()))
                    .append(")")
                    .append(" VALUES")
                    .append("(")
                        .append(Strings.join(",", columnNameToValue.values(), Strings.WITHIN_SINGLE_QUOTES))
                    .append(")");
            return queryBuilder.toString();
        }
    }
    
	public void truncate(Connection connection) throws SQLException {
		executeStatement(connection, "TRUNCATE TABLE TBLRADIUSCUSTOMER");
	}

	public void drop(Connection connection) throws SQLException {
		executeStatement(connection, "DROP TABLE TBLRADIUSCUSTOMER");
	}
	
	public void executeStatement(Connection connection, String statement) throws SQLException {
		PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.execute();
            connection.commit();
        } finally {
            DBUtility.closeQuietly(preparedStatement);
        }
	}
}
