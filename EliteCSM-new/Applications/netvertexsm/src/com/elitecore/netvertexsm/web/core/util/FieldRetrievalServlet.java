package com.elitecore.netvertexsm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.ws.db.DBConnectionManager;

/**
 * Servlet implementation class FieldRetrievalServlet
 */
public class FieldRetrievalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="FieldRetrievalServlet";
	private ResultSetMetaData rsMetaData = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FieldRetrievalServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		List<String> dataList = new ArrayList<String>();
		String databaseId = request.getParameter("databaseId");
		String tblName = request.getParameter("tblName");
		String countryID = request.getParameter("countryID");
		String fetchSubscribers=request.getParameter("fetchSubscribers");
		String subscriber=request.getParameter("subscribersLike");
		
		/*if(fetchSubscribers!=null && fetchSubscribers.equals("true")){
			
			dataList=getSubscriberIdentity(subscriber);
			
			
		}else*/ if(countryID!=null && countryID.trim().length()>0){
			
			dataList = getMCC(countryID);
		}else{			
			if(tblName.equalsIgnoreCase("TABS")){
				dataList = getTableNames(databaseId, tblName);
			}else{
				dataList = getColumnNames(databaseId,tblName);
			}			
		}
			
		out.println(dataList);
		out.close();
	}
	
	/*private List<String> getSubscriberIdentity(String subscriber) {

		Logger.logInfo(MODULE, "Retrieving Subscribers Ids");
		
		List<String> subscriberIdList = new ArrayList<String>();			
		String 	query 	= "SELECT "+ SubscriberProfileWSBLManager.USERIDFIELD +" FROM " + SubscriberProfileWSBLManager.DBTABLE + 
				" WHERE " + SubscriberProfileWSBLManager.USERIDFIELD + " LIKE '%'||?||'%' AND ROWNUM<=20";
		Connection connection = null;  
		PreparedStatement preparedStatement = null;    
		ResultSet resultSet = null;
		try {
			connection 	= DBConnectionManager.getInstance().getSubscriberProfileDBConection();						
			preparedStatement 	= connection.prepareStatement(query);
			preparedStatement.setString(1,subscriber);
			resultSet 	= preparedStatement.executeQuery();			
			while(resultSet.next()){
				String subscriberId = resultSet.getString(SubscriberProfileWSBLManager.USERIDFIELD);				
				subscriberIdList.add(subscriberId);
			}		
		}catch(SQLException e) {
			Logger.logError(MODULE, "SQL Exception  while retrieving subscriber Ids, Reason : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retrieving subscriber Ids, Reason : "+e.getMessage());
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
			}
		return subscriberIdList;
	}*/

	private String getQueryForDBField(String tblName) {
		return "SELECT * FROM " + tblName;
	}
	
	public List<String> getTableNames(String databaseId,String tblName){
		Logger.logInfo(MODULE, "Retrieving Table Names");
		List<String> dbTableNamesList = new ArrayList<String>();
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		Connection connection = null;  
		Statement statement = null;    
		ResultSet resultSet = null;    
		
		try {
			connection = databaseDSBLManager.getDBConnection(Long.parseLong(databaseId));
			String query = getQueryForDBField(tblName);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);			
			
			while(resultSet.next()){
				dbTableNamesList.add(resultSet.getString(1));
			}
			
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error1 while retriving fields : "+e.getMessage());
		}catch(SQLException e) {
			Logger.logError(MODULE, "Error2 while retriving fields : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error3 while retriving fields : "+e.getMessage());
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
			}
		return dbTableNamesList;
	}
	
	public List<String> getColumnNames(String databaseId,String tblName){
		Logger.logInfo(MODULE, "Retrieving Column Names");
		List<String> dbFieldList = new ArrayList<String>();
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		Connection connection = null;  
		Statement statement = null;    
		ResultSet resultSet = null;    
		try {
			connection = databaseDSBLManager.getDBConnection(Long.parseLong(databaseId));
			String query = getQueryForDBField(tblName);
			Logger.logDebug(MODULE, "Retriving columns from table: "+tblName);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			rsMetaData = resultSet.getMetaData();
			
			for(int i=1;i<=rsMetaData.getColumnCount();i++) {
				dbFieldList.add(rsMetaData.getColumnName(i));
			}
			
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(SQLException e) {
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
			}
		return dbFieldList;
	}
	
	public List<String> getMCC(String countryID){
		Logger.logInfo(MODULE, "Retrieving MCC for CountryID : "+countryID);
		List<String> mccList = new ArrayList<String>();			
		String 	query 	= "Select * from TBLSCOUNTRYMCCREL where COUNTRYID = "+countryID;
		Connection connection = null;  
		Statement statement = null;    
		ResultSet resultSet = null;    
		
		try {
			connection 	= DBConnectionManager.getInstance().getSMDatabaseConection();
			if(connection == null){
				return mccList;
			}
			statement 	= connection.createStatement();
			resultSet 	= statement.executeQuery(query);			
			
			while(resultSet.next()){
				String mccCode = resultSet.getString("MCC");				
				mccList.add(mccCode);
			}		
		}catch(SQLException e) {
			Logger.logError(MODULE, "Error while MCC for countryId : "+countryID+", Error : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while MCC for countryId : "+countryID+", Error : "+e.getMessage());
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
			}
		return mccList;
	}
	
	}
