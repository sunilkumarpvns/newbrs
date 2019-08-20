package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 * Class <code>FetchMapDBField</code> is used to fetch DBField values from the respective mapping in
 * Diameter session manager.<p>
 * 
 * When the user selects a mapping in the scenario, based on the <strong>Id</strong> of that mapping this class 
 * fetches the DBField values that are specified in the mapping.
 * 
 * @author Animesh Christie
 */
public class FetchMapDBField extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final String MODULE="FetchMapDBField";
	private Connection connection = null;
	private PreparedStatement prepareStatement = null;
	private ResultSet resultSet = null;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<String> dbFieldList = new ArrayList<String>();
		String mapId = req.getParameter("mapId");
		String databaseId = req.getParameter("databaseId");
		
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
		
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		try {
			connection = databaseDSBLManager.getConnection(databaseId);
			String query = "SELECT DBFIELDNAME FROM TBLMDIASESSIONDBFIELDMAP WHERE MAPPINGID = ?";
			prepareStatement = connection.prepareStatement(query);
			prepareStatement.setString(1,mapId);
			resultSet = prepareStatement.executeQuery();
			
			while(resultSet.next()){
				dbFieldList.add(resultSet.getString("DBFIELDNAME"));
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
			out.println(dbFieldList);
		}
	}
}
