/**
 * 
 */
package com.elitecore.classicrating.api.ejb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.elitecore.classicrating.datasource.IDataSourceProvider;
import com.elitecore.classicrating.datasource.IRatingDBConnection;
import com.elitecore.classicrating.datasource.IRatingDataSource;


/**
 * @author sheetalsoni
 *
 */
public class EliteClassicRatingBase {

	public class DataSourceProvider implements IDataSourceProvider{
		
		private String dataSourceName;
		
		public DataSourceProvider(){
			this.dataSourceName = getDataSourceName();
		}
		public IRatingDataSource getRatingDataSource() throws SQLException {
			// TODO Auto-generated method stub
			return new RatingDataSource(dataSourceName);
		}
	}
	
	public class RatingDataSource implements IRatingDataSource{
		private String dataSourceName;
		
		public RatingDataSource(String dataSourceName){
			this.dataSourceName = dataSourceName;
		}

		public void close(IRatingDBConnection connection) throws SQLException {
			// TODO Auto-generated method stub
			connection.close();
		}

		public IRatingDBConnection getConnection() throws SQLException {
			// TODO Auto-generated method stub
			DataSource dataSource = null;
			if(dataSource == null){
				try{
					InitialContext cntx = new javax.naming.InitialContext();
					dataSource = (DataSource) cntx.lookup(getDataSourceName());
					//Logger.logInfo("CRESTELRATINGBASE", "got data Source :)");
				}catch(NamingException e){
					e.printStackTrace();
					//Logger.logTrace("CRESTELRATINGBASE", "Exception while getting ds :(" + e);
				}
			}
			Connection connection = dataSource.getConnection();
			EliteRatingDBConnection ratingDBConnection = new EliteRatingDBConnection(connection);
			
			return ratingDBConnection;
		}

	}
	
	public class EliteRatingDBConnection extends EliteOracleConnection {
		public EliteRatingDBConnection (Connection connection){
			super(connection);
		}
	}
	
	public class EliteOracleConnection implements IRatingDBConnection{
		
		private Connection connection;
		
		public EliteOracleConnection (Connection connection){
			this.connection = connection;
		}

		public void close(IRatingDBConnection connection) throws SQLException {
			// TODO Auto-generated method stub
			connection.close();
			
		}

		public void closePreparedStatement(PreparedStatement pStmt) throws SQLException {
			// TODO Auto-generated method stub
			if(pStmt != null) {
				pStmt.close();
			}
		}

		public void closePreparedStatement(PreparedStatement pStmt, String key) throws SQLException {
			// TODO Auto-generated method stub
			if(pStmt != null) {
				try {
					pStmt.clearParameters();
				}catch (Exception e) {
					e.printStackTrace();
				}

				pStmt.close();
			}
		}

		public void closeStatement(Statement stmt) throws SQLException {
			// TODO Auto-generated method stub
			if(stmt != null)
				stmt.close();
	
		}

		public void commit() throws SQLException {
			// TODO Auto-generated method stub
			if(connection != null)
				connection.commit();
		}

		public Statement createStatement() throws SQLException {
			// TODO Auto-generated method stub
			Statement statement = null;
			if(connection != null) {
				statement = connection.createStatement();
			}
			return statement;
		}

		public PreparedStatement prepareStatement(String query) throws SQLException {
			PreparedStatement prepareStatement = null;
			try{
			if(connection != null){
				prepareStatement = connection.prepareStatement(query);
			}
			// TODO Auto-generated method stub
			
		}catch(Exception e){
			throw new SQLException("Error Occured while creating Prepared Statement " + e.getMessage());
		}
		return prepareStatement;
		}

		public PreparedStatement prepareStatement(String query, String key) throws SQLException {
			PreparedStatement statement = null;
			try{
				statement = connection.prepareStatement(query);
			}catch(Exception e){
				throw new SQLException("Error Occured while Creating Prepared Statement : " + e.getMessage());
			}
			// TODO Auto-generated method stub
			return statement;
		}

		public void rollback() throws SQLException {
			// TODO Auto-generated method stub
			if(connection != null){
				connection.rollback();
			}
		}

		public void setAutoCommit(boolean autoCommit) throws SQLException {
			// TODO Auto-generated method stub
			if(connection != null){
				connection.setAutoCommit(autoCommit);
			}
		}

		public void setTransactionIsolation(int level) throws SQLException {
			// TODO Auto-generated method stub
			if(connection != null){
				connection.setTransactionIsolation(level);
			}
		}

		public void close() throws SQLException {
			// TODO Auto-generated method stub
			if(connection != null){
				connection.close();
				System.out.println("Connection closed Successfully :)");
			}
		}
		
	}

	protected String getDataSourceName(){
		return "java:/jdbc/Jisp4xDS";
	}
}
