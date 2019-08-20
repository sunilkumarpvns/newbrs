package com.elitecore.core.commons.util.db;

import java.sql.SQLException;

import oracle.jdbc.driver.OracleConnection;

import com.elitecore.core.commons.util.db.errorcodes.OracleErrorCodes;
import com.elitecore.core.commons.util.db.errorcodes.PostgreSQLErrorCodes;

/**
 * 
 * A class representing database vendors supported.
 * 
 * @author narendra.pathai
 *
 */
//TODO when added enum for this just add respective test case in DataSourceExceptionTest
public enum DBVendors {
	
	ORACLE(		"select 1 from dual",	"oracle.jdbc.driver.OracleDriver") {
		@Override
		public boolean isDBDownSQLException(SQLException e) {
			return OracleErrorCodes.isDBDownErrorCode(e.getErrorCode());
		}

		@Override
		public String getVendorErrorMessage(SQLException e) {
			return OracleErrorCodes.getVendorErrorMessageFromErrorCode(e.getErrorCode());
		}

		@Override
		public String getVendorConnectionProperty(EliteDBConnectionProperty property) {
			switch (property) {
			case NETWORK_READ_TIMEOUT:
				return OracleConnection.CONNECTION_PROPERTY_THIN_READ_TIMEOUT;
			}
			return property.name();
		}

		@Override
		public String getVendorSpecificSequenceSyntax(String sequenceName) {
			return sequenceName + ".NEXTVAL";
		}
	},
	
	MYSQL(		"select 1",				"com.mysql.jdbc.Driver") {
		@Override
		public boolean isDBDownSQLException(SQLException e) {
			return false;
		}

		@Override
		public String getVendorErrorMessage(SQLException e) {
			return null;
		}

		@Override
		public String getVendorConnectionProperty(EliteDBConnectionProperty property) {
			return property.name();
		}

		@Override
		public String getVendorSpecificSequenceSyntax(String sequenceName) {
			return "nextval('" + sequenceName + "')";
		}
	},
	
	POSTGRESQL(	"select 1",				"org.postgresql.Driver") {
		@Override
		public boolean isDBDownSQLException(SQLException e) {
			return PostgreSQLErrorCodes.isDBDownSQLState(e.getSQLState());
		}

		@Override
		public String getVendorErrorMessage(SQLException e) {
			return PostgreSQLErrorCodes.getVendorErrorMessageFromSQLState(e.getSQLState());
		}

		@Override
		public String getVendorConnectionProperty(EliteDBConnectionProperty property) {
			return property.name();
		}

		@Override
		public String getVendorSpecificSequenceSyntax(String sequenceName) {
			return "nextval('"+ sequenceName +"')";
		}
	},

	TIMESTEN(	"select 1 from dual",	"com.timesten.jdbc.TimesTenDriver") {
		@Override
		public boolean isDBDownSQLException(SQLException e) {
			return false;
		}

		@Override
		public String getVendorErrorMessage(SQLException e) {
			return null;
		}

		@Override
		public String getVendorConnectionProperty(EliteDBConnectionProperty property) {
			return property.name();
		}

		@Override
		public String getVendorSpecificSequenceSyntax(String sequenceName) {
			return ORACLE.getVendorSpecificSequenceSyntax(sequenceName);
		}
	}, 
	
	DERBY(		"values 1", 	"org.apache.derby.jdbc.EmbeddedDriver"){
		@Override
		public boolean isDBDownSQLException(SQLException ex) {
			return false;
		}

		@Override
		public String getVendorErrorMessage(SQLException ex) {
			return null;
		}

		@Override
		public String getVendorConnectionProperty(
				EliteDBConnectionProperty property) {
			return null;
		}

		@Override
		public String getVendorSpecificSequenceSyntax(String sequenceName) {
			return "'" 
						+ "next value for " + sequenceName
					+ "'";
		}
	};
	
	
	public final String validationQuery;
	public final String driverClassName;
	
	private DBVendors(String validationQuery, String driverClassName) {
		this.validationQuery = validationQuery;
		this.driverClassName = driverClassName;
	}
	
	public abstract boolean isDBDownSQLException(SQLException ex);
	public abstract String getVendorErrorMessage(SQLException ex);
	public abstract String getVendorConnectionProperty(EliteDBConnectionProperty property);
	
	/**
	 * Returns vendor specific syntax for sequence
	 * @param sequenceName sequence name which will be used for generating sequence syntax
	 * @return sequence syntax
	 */
	public abstract String getVendorSpecificSequenceSyntax(String sequenceName);
	
	/**
	 * @return DB vendor representing the url if known.
	 * @throws DatabaseTypeNotSupportedException if there is no vendor that supports this url.
	 */
	public static DBVendors fromUrl(String url) throws DatabaseTypeNotSupportedException {
		if (url.toLowerCase().contains(DBVendors.ORACLE.name().toLowerCase())) {
			return DBVendors.ORACLE;
		} else if(url.toLowerCase().contains(DBVendors.POSTGRESQL.name().toLowerCase())){
			return DBVendors.POSTGRESQL;
		} else if(url.toLowerCase().contains(DBVendors.MYSQL.name().toLowerCase())){
			return DBVendors.MYSQL;
		} else if(url.toLowerCase().contains(DBVendors.TIMESTEN.name().toLowerCase())){
			return DBVendors.TIMESTEN;
		} else if(url.toLowerCase().contains(DBVendors.DERBY.name().toLowerCase())){
			return DBVendors.DERBY;
		} else {
			throw new DatabaseTypeNotSupportedException("Requested Database URL " + url + " is not supported");
		}
	}
}
