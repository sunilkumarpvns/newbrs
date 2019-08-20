package com.elitecore.netvertexsm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
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

/**
 * Servlet implementation class PCCNameRetrivalServlet
 */
public class PCCNameRetrivalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="PCCNAMERETRIVALSERVLET";
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PCCNameRetrivalServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> pccRuleNameList = new ArrayList<String>();
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		try {
			connection = databaseDSBLManager.getConnection((long)1);
			String query = "SELECT NAME FROM  TBLMPCCRULE";
			Logger.logDebug(MODULE, "Retriving Name from table: TBLSDEVICETYPE");
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			while(resultSet.next()){
				pccRuleNameList.add(resultSet.getString("NAME"));
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
		out.println(pccRuleNameList);
	}

}
