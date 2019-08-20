/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HASMDataManager.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.reports.userstat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.reports.userstat.UserStatisticsDataManager;
import com.elitecore.elitesm.datamanager.reports.userstat.data.IUserStatisticsData;
import com.elitecore.elitesm.datamanager.reports.userstat.data.UserStatisticsData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;



public class HUserStatisticsDataManager extends HBaseDataManager implements UserStatisticsDataManager{

	private static String MODULE = "USER STATISTICS DATA MANAGER";
	private static String dbPropsFileLocation = EliteUtility.getSMHome() + File.separator +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;
	Connection con = null;

	public HUserStatisticsDataManager(){

		Logger.logInfo(MODULE, "Trying to get a User Statistics Database connection.");
		try{
			con = getUserStatDBConnection();
		}catch(Exception e){
			Logger.logError(MODULE, "Failed to get User Statistics database connection");
			Logger.logTrace(MODULE, e);
		}

		if(con==null){
			Logger.logInfo(MODULE, "Trying to get a Server Manager database connection.");
			try{
				con = getSMConnection();
			}catch(Exception e){
				Logger.logError(MODULE, "Failed to get Server Manager database connection");
				Logger.logTrace(MODULE, e);
			}
		}

	}

	public Connection getUserStatDBConnection() throws SQLException, ClassNotFoundException, IOException{
		Connection connection;
	
        Properties properties = new Properties();
        Logger.logInfo(MODULE, "Loading database properties from "+dbPropsFileLocation);
        File dbPropsFile = new File(dbPropsFileLocation);
        FileInputStream fileInputStream = new FileInputStream(dbPropsFile);
        properties.load(fileInputStream);
        
	
		String driverClass="";
		String dbUrl="";
		String dbUser="";
		String dbPassword="";

		Enumeration<Object> enumeration = properties.keys();
		while (enumeration.hasMoreElements()) {
			String key = (String)enumeration.nextElement();
			String value = properties.getProperty(key);
			if(key.equalsIgnoreCase("userstatistics.database.driver_class")){
				driverClass=value;
			}
			if(key.equalsIgnoreCase("userstatistics.database.url")){
				dbUrl=value;
			}
			if(key.equalsIgnoreCase("userstatistics.database.username")){
				dbUser=value;
			}
			if(key.equalsIgnoreCase("userstatistics.database.password")){
				dbPassword=value;
			}
		}
		Logger.logInfo(MODULE, "Database Url :"+dbUrl);
		Logger.logInfo(MODULE, "Database Class :"+driverClass);
		Logger.logInfo(MODULE, "Database Username :"+dbUser);
		Logger.logInfo(MODULE, "Database Password :"+dbPassword);

		//TODO NAYANA: Whether exception is thrown or not FIS must be closed before
		//				leaving this method
		fileInputStream.close();
		Class.forName(driverClass);
		connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
		Logger.logInfo(MODULE, "connection successfull");
		
		return connection;

	}

	public Connection getSMConnection() throws SQLException, ClassNotFoundException,IOException{
		Connection connection;
		Locale currentLocale = Locale.getDefault();


		
		
		 Properties properties = new Properties();
	        Logger.logInfo(MODULE, "Loading database properties from "+dbPropsFileLocation);
	        File dbPropsFile = new File(dbPropsFileLocation);
	        FileInputStream fileInputStream = new FileInputStream(dbPropsFile);
	        properties.load(fileInputStream);
	        
		
			String driverClass="";
			String dbUrl="";
			String dbUser="";
			String dbPassword="";

			Enumeration<Object> enumeration = properties.keys();
			while (enumeration.hasMoreElements()) {
				String key = (String)enumeration.nextElement();
				String value = properties.getProperty(key);
				if(key.equalsIgnoreCase("hibernate.connection.driver_class")){
					driverClass=value;
				}
				if(key.equalsIgnoreCase("hibernate.connection.url")){
					dbUrl=value;
				}
				if(key.equalsIgnoreCase("hibernate.connection.username")){
					dbUser=value;
				}
				if(key.equalsIgnoreCase("hibernate.connection.password")){
					dbPassword=value;
				}
			}
			Logger.logInfo(MODULE, "Database Url :"+dbUrl);
			Logger.logInfo(MODULE, "Database Class :"+driverClass);
			Logger.logInfo(MODULE, "Database Username :"+dbUser);
			Logger.logInfo(MODULE, "Database Password :"+dbPassword);

			//TODO NAYANA: Whether exception is thrown or not FIS must be closed before
			//				leaving this method
			fileInputStream.close();
			Class.forName(driverClass);
			connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			Logger.logInfo(MODULE, "connection successfull");
		return connection;
	}

	public Connection getConnection() throws SQLException
	{
		if(con == null)
			throw new SQLException();
		return con;
	}

	public void finalize()
	{
		try{
			con.close();                    
		}catch(Exception exp){}

	}

	public PageList search(IUserStatisticsData userStatisticsData, int pageNo,int pageSize) throws DataManagerException{
		PageList pageList = null;
		Connection connection=null;
		try{
			connection = getConnection();

			String userIdentity = userStatisticsData.getUserIdentity();

			ResultSet rsTotalRecs = null;
			ResultSet rs = null; 
			
			String queryString = "select * from ( select a.*, ROW_NUMBER() OVER (ORDER BY userstatisticsid DESC) rownumber from tbluserstatistics a where 1=1";
			String totalQueryString = "SELECT count(1) as numRows FROM TBLUSERSTATISTICS WHERE 1 = 1 ";
			int count=1;

			Map<String,String> hashMap = new HashMap<String,String>(); 

			String searchQueryPart  = "";
			
			if(userIdentity!=null && !(userIdentity.trim().equals(""))){
				queryString += " AND USER_IDENTITY=?";  
				totalQueryString += " AND USER_IDENTITY=?";
				hashMap.put(count+"", userIdentity.toLowerCase());
				count = count+1;        	
			}
			queryString+= ") b where rownumber between " +((pageNo*pageSize)-(pageSize)+1)+ " and " +(pageNo*pageSize);
             
			if(userIdentity==null || userIdentity.trim().equals("") ){
				/*select * from tbluserstatistics where userstatisticsid in 
				(select desc_rec_10.userstatisticsid from
				 (select  desc_rec.*,ROW_NUMBER() over (order by userstatisticsid desc ) rnum
				        from ( select userstatisticsid,ROW_NUMBER() over (order by userstatisticsid desc ) inum from tbluserstatistics order by userstatisticsid desc) desc_rec
				       where inum <= 10) desc_rec_10
				where rnum  >=1)*/
				queryString= "select * from (select a.*, ROW_NUMBER() OVER (ORDER BY userstatisticsid DESC) rownumber from tbluserstatistics a ) b where rownumber between " +((pageNo*pageSize)-(pageSize)+1)+ " and " +(pageNo*pageSize);
				//original//queryString      = "select * from tbluserstatistics where userstatisticsid in (select desc_rec_10.userstatisticsid from (select  desc_rec.*,ROW_NUMBER() over (order by userstatisticsid desc ) rnum from ( select userstatisticsid,ROW_NUMBER() over (order by userstatisticsid desc ) inum from tbluserstatistics order by userstatisticsid desc) desc_rec where inum <= "+(pageNo*pageSize)+")  desc_rec_10 where rnum  >= "+((pageNo*pageSize)-(pageSize)+1)+") ";
				totalQueryString = "SELECT count(1) as numRows FROM TBLUSERSTATISTICS WHERE 1 = 1 ";
			}

			Logger.logInfo(MODULE, "Query for searching             : "+ queryString);
			Logger.logInfo(MODULE, "Query for finding total records : "+ totalQueryString);
			
			PreparedStatement preparedStatement = connection.prepareStatement(queryString);
			PreparedStatement preparedStatementtotal = connection.prepareStatement(totalQueryString);


			Iterator<String> itr = hashMap.keySet().iterator();

			while(itr.hasNext()){

				String key = itr.next();
				Object value = hashMap.get(key);
				if(value instanceof java.sql.Timestamp) {
					preparedStatement.setTimestamp(Integer.parseInt(key), (java.sql.Timestamp)value);
					preparedStatementtotal.setTimestamp(Integer.parseInt(key), (java.sql.Timestamp)value);
				}else{
					preparedStatement.setString(Integer.parseInt(key), (String)value);
					preparedStatementtotal.setString(Integer.parseInt(key), (String)value);
				}

			}       

			rs = preparedStatement.executeQuery(); 
			rsTotalRecs = preparedStatementtotal.executeQuery();
			int totalItems=0;
			while(rsTotalRecs.next()){

				totalItems=rsTotalRecs.getInt("NUMROWS");
			}
			List list = new ArrayList();


			int rowcount = 0;

			//&& rowcount < pageSize
			while(rs.next())
			{                       
				userStatisticsData.setUserStatisticsId(rs.getLong("USERSTATISTICSID"));
				userStatisticsData.setUserName(rs.getString("USER_NAME"));
				userStatisticsData.setNasIpAddress(rs.getString("NAS_IP_ADDRESS"));
				userStatisticsData.setFramedIpAddress(rs.getString("FRAMED_IP_ADDRESS"));
				userStatisticsData.setCallingStationId(rs.getString("CALLING_STATION_ID"));
				userStatisticsData.setReplyMessage(rs.getString("REPLY_MESSAGE"));
				userStatisticsData.setGroupName(rs.getString("GROUPNAME"));
				userStatisticsData.setUserIdentity(rs.getString("USER_IDENTITY"));	
				userStatisticsData.setParamStr0(rs.getString("PARAM_STR0"));
				userStatisticsData.setParamStr1(rs.getString("PARAM_STR1"));
				userStatisticsData.setParamStr2(rs.getString("PARAM_STR2"));
				
				Timestamp eventTimestamp = rs.getTimestamp("CREATE_DATE");
				Date dateEventTimestamp = null;
				if(eventTimestamp!=null){
					dateEventTimestamp = new Date(eventTimestamp.getTime());
				}

				userStatisticsData.setCreateDate(dateEventTimestamp);

				list.add(userStatisticsData);
				userStatisticsData = new UserStatisticsData();
				rowcount++;
			}

			rs.close();
			preparedStatement.close();
			preparedStatementtotal.close();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems%pageSize == 0)
		    totalPages-=1;
			pageList = new PageList(list, pageNo, totalPages,totalItems);
		}
		catch(Exception e){
			e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					Logger.logDebug(MODULE,"Error while closing Connection object. Reason: "+ e.getMessage());
				}
			}
		}

		return pageList;
	}


}
