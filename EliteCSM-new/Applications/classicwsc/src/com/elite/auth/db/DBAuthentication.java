package com.elite.auth.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Map;

import javax.sql.DataSource;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleSQLException;
import sun.text.normalizer.ICUBinary.Authenticate;

import com.elite.auth.DefaultAuthenticationInterface;
import com.elite.config.ORACLEConfig;
import com.elite.context.Context_Manager;
import com.elite.context.WSCContext;
import com.elite.exception.CommunicationException;
import com.elite.user.UserOtherDetail;
import com.elite.user.Userbean;
import com.evermind.security.User;
import com.opensymphony.xwork2.ActionContext;


public class DBAuthentication extends DefaultAuthenticationInterface
{

	public boolean authenticate(String strUserName, String password) throws CommunicationException 
	{
		Connection oracleConnection = null;
		boolean isAuthenticated = false;
		
		try
		{ 
			String strDataSourceName = ((ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora")).getDatasourcename();
			oracleConnection = DBConnectionManager.getInstance(strDataSourceName).getConnection();
			Statement stat = oracleConnection.createStatement();
			String sql = "select * from customeraccount where customeridentifier = '"+ strUserName+ "' and password = '"+ password+ "'";
			ResultSet rs =  stat.executeQuery(sql);
			if(rs.next())
			{
				isAuthenticated = true;
			}
		}
		catch (Exception e) {
			throw new CommunicationException(e.getMessage()+ e);
		}
		return isAuthenticated;
	}
	
	
	
	public boolean changePassword(String user, String oldPassword, String newPassword) throws CommunicationException {
		Connection oracleConnection = null;
		boolean ismodified = false;
		try
		{ 
			String strDataSourceName = ((ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora")).getDatasourcename();
			oracleConnection = DBConnectionManager.getInstance(strDataSourceName).getConnection();
			Statement stat = oracleConnection.createStatement();
			String sql = "update customeraccount set password = '"+ newPassword + "' where customeridentifier = '"+ user + "' and password = '"+ oldPassword + "'";
			System.out.println(sql);
			int count =  stat.executeUpdate(sql);
			if(count == 1)
			{
				ismodified = true;
			}
		}
		catch (Exception e) {
			throw new CommunicationException(e.getMessage()+ e);
		}
		return ismodified;
	}
	
	public static void main(String[] args)throws Exception
	{
//		Context_Manager.init(null);
//		ORACLEConfig ora = new ORACLEConfig();
//		//ActionContext context_manager = ActionContext.getContext();
//        //WSCContext wsc_context = context_manager.getWsc_context();
//        wsc_context.setAttribute("ora", ora);
//        DBAuthentication db = new DBAuthentication();
//		System.out.println(db.authenticate("admin", "admin"));
	}

}
