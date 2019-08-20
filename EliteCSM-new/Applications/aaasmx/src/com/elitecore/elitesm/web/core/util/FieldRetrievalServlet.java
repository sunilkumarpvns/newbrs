package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class FieldRetrievalServlet
 */
public class FieldRetrievalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="FieldRetrievalServlet";
	private Connection connection = null;
	private PreparedStatement prepareStatement = null;
	private ResultSet resultSet = null;
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
		List<String> dbFieldList = new ArrayList<String>();
		String databaseId = request.getParameter("databaseId");
		String tblName = request.getParameter("tblName");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		try {
			connection = databaseDSBLManager.getConnection(databaseId);
			String query = getQueryForDBField(tblName);
			Logger.logDebug(MODULE, "Retriving columns from table: "+tblName);
			prepareStatement = connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();
			rsMetaData = resultSet.getMetaData();
			
			for(int i=1;i<=rsMetaData.getColumnCount();i++) {
				dbFieldList.add(rsMetaData.getColumnName(i).toUpperCase());
			}
			
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(SQLException e) {
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}finally{
			if(resultSet != null){
				try{resultSet.close();}catch(SQLException e){}
			}
			if(prepareStatement != null){
				try{prepareStatement.close();}catch(SQLException e){}
			}
			if(connection != null){
				try{connection.close();}catch(SQLException e){}
			}
		}
		out.println(dbFieldList);
	}
	
	private String getQueryForDBField(String tblName) {
		return new StringBuilder(26+tblName.length()).append("SELECT * FROM ")
						   .append(tblName)
						   .append(" where 1=0").toString();
	}
	
}
