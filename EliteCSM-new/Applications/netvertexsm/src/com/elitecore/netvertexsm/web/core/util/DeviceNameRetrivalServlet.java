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

import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.util.logger.Logger;

/**
 * Servlet implementation class DeviceNameRetrivalServlet
 */
public class DeviceNameRetrivalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="DEVICENAMERETRIVALSERVLET";
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeviceNameRetrivalServlet() {
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
		List<String> deviceNameList = new ArrayList<String>();
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		try {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			session.beginTransaction();
			String query = "SELECT NAME FROM  TBLSDEVICETYPE";
			Logger.logDebug(MODULE, "Retriving columns from table: TBLSDEVICETYPE");
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			while(resultSet.next()){
				deviceNameList.add(resultSet.getString("NAME"));
			}
			
		}catch(SQLException e) {
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}finally{
			if(resultSet != null){
				try{resultSet.close();}catch(SQLException e){}
			}
			if(statement != null){
				try{statement.close();}catch(SQLException e){}
			}
			if(connection != null){
				try{connection.close();}catch(SQLException e){}
			}
		}
		out.println(deviceNameList);
	}

}
