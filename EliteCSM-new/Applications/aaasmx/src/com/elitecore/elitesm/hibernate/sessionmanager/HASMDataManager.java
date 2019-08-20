/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HASMDataManager.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.sessionmanager;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.ASMDataManager;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerDBConfiguration;
import com.elitecore.elitesm.datamanager.sessionmanager.exception.DataNotInSyncException;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;



public class HASMDataManager extends HBaseDataManager implements ASMDataManager{

	private static String MODULE = "ASM DATA MANAGER";

	Connection con = null;
	Connection ipPoolConnection = null;
	
	private final String strReleaseIpAddressQuery = "UPDATE TBLMIPPOOLDETAIL set ASSIGNED='N', LAST_UPDATED_TIME=current_timestamp where IPADDRESS = ?";


	public HASMDataManager(){
/*
		Logger.logInfo(MODULE, "Trying to get a ASM(Active Session Management) database connection.");*/
		if(con==null){
			Logger.logInfo(MODULE, "Trying to get a Server Manager database connection.");
			try{
				con = getSMConnection();
			}catch(Exception e){
				Logger.logError(MODULE, "Failed to get Server Manager database connection :"+e.getMessage());
			}
		}

	}

	public Connection getSMConnection() throws SQLException, ClassNotFoundException, IOException, NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException{
		Connection connection;

		
		FileInputStream fileInputStream = null;
				
        Properties properties = new Properties();
        File dbPropsFile = new File(EliteUtility.getSMHome()+File.separator+ConfigConstant.DATABASE_CONFIG_FILE_LOCATION);
        fileInputStream = new FileInputStream(dbPropsFile);
        properties.load(fileInputStream);
        
        Set bundleKeys = properties.keySet();
		Iterator<String> iterator = bundleKeys.iterator();
		String driverClass="";
		String dbUrl="";
		String dbUser="";
		String dbPassword="";

		while (iterator.hasNext()) {
			String key = iterator.next();
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
				dbPassword = PasswordEncryption.getInstance().decrypt(value, PasswordEncryption.ELITE_PASSWORD_CRYPT);
			}
		}
		Logger.logInfo(MODULE, "Database Url      :"+dbUrl);
		Logger.logInfo(MODULE, "Database Class    :"+driverClass);
		Logger.logInfo(MODULE, "Database Username :"+dbUser);

		//TODO NAYANA: Whether exception is thrown or not FIS must be closed before
		//				leaving this method
		fileInputStream.close();
		Class.forName(driverClass);
		connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
		Logger.logInfo(MODULE, "connection successfull");
		return connection;

	}

	public Connection getIPPoolConnection() throws SQLException, ClassNotFoundException, IOException, NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException{
		if(ipPoolConnection==null)
			ipPoolConnection = getSMConnection();
		return ipPoolConnection;
	} 	

	
	private Connection getConnection(SessionManagerDBConfiguration dbConfiguration) throws SQLException, ClassNotFoundException,IllegalAccessException, InstantiationException{
		 Connection connection;
		 String dbUrl = dbConfiguration.getConnectionUrl();
		 String dbUser = dbConfiguration.getUserName();
		 String dbPassword = dbConfiguration.getPassword();
		 String driverClass = null;
		if(dbUrl.toLowerCase().contains("oracle"))
			driverClass = "oracle.jdbc.driver.OracleDriver";
		else if(dbUrl.toLowerCase().contains("mysql"))
			driverClass = "com.mysql.jdbc.Driver";
		else if(dbUrl.toLowerCase().contains("postgresql"))
			driverClass = "org.postgresql.Driver";
		 Logger.logDebug(MODULE, "db url  :"+dbUrl);
		 Logger.logDebug(MODULE, "db user :"+dbUser);
		 
		 Class.forName(driverClass).newInstance();
		 connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword);

		 Logger.logInfo(MODULE, "DB Connection successfull");
		 return connection;
	}


	public PageList search(IASMData asmData, int pageNo,int pageSize,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		PageList pageList = null;
		Connection connection = null;
		try{
			connection = getConnection(dbConfiguration);
			Statement stmt = connection.createStatement();
			String username = asmData.getUserName();
			String nasip = asmData.getNasIpAddress();
			String framedIp = asmData.getFramedIpAddress();
			String groupName = asmData.getGroupName();
			String idleTime = asmData.getIdleTime();

			ResultSet rs1 = null;
			ResultSet rs2 = null;
			Calendar calender = Calendar.getInstance();
			Timestamp systemDate;
			if(!(idleTime.equalsIgnoreCase(""))){

				Timestamp timestamp = getCurrentTimeStamp(connection);
				systemDate = timestamp;

				calender.setTime(timestamp);
				calender.add(Calendar.MINUTE, -(Integer.parseInt(idleTime)));

			}

			if(dbConfiguration.getFieldMappingList() != null && dbConfiguration.getFieldMappingList().size() > 0){
				List<SMDBFieldMapData> smdbFieldMapData=new ArrayList<SMDBFieldMapData>();
				smdbFieldMapData=dbConfiguration.getFieldMappingList();
				
				for(SMDBFieldMapData fieldMapData:smdbFieldMapData ){
					if(fieldMapData.getField()!=null && fieldMapData.getField().equals("User Identity")){
						dbConfiguration.setIdentityField(fieldMapData.getDbFieldName());
					}
					if(fieldMapData.getField()!=null &&fieldMapData.getField().equals("Session ID")){
						dbConfiguration.setSessionIdField(fieldMapData.getDbFieldName());
					}
					if(fieldMapData.getField()!=null &&fieldMapData.getField().equals("PDP Type")){
						dbConfiguration.setNasPortTypeField(fieldMapData.getDbFieldName());
					}
				}
			}
			
			ResultSet rs = null; 
			String queryString = "select * from "+dbConfiguration.getTableName()+" where  1 = 1";
			String totalQueryString = "SELECT count(*)as numRows FROM "+dbConfiguration.getTableName()+" WHERE 1 = 1";
			int count=1;

			Map hashMap = new HashMap(); 

			if(!"".equals(username)){
				queryString += ""+" AND LOWER("+dbConfiguration.getUserNameField()+") LIKE ?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getUserNameField()+") LIKE ?";
				hashMap.put(count+"", "%"+username.toLowerCase()+"%");
				count = count+1;
			}

			if(!"".equals(nasip)){
				queryString += ""+" AND LOWER("+dbConfiguration.getNasIpAddressField()+")=?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getNasIpAddressField()+")=?";
				hashMap.put(count+"", nasip.toLowerCase());
				count = count+1;        	
			}
			
			if(!"".equals(framedIp)){

				queryString += ""+" AND LOWER("+dbConfiguration.getFramedIpAddressField()+")=?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getFramedIpAddressField()+")=?";
				hashMap.put(count+"", framedIp.toLowerCase());
				count = count+1;        	
			}

			if(!"".equals(groupName)){

				queryString += ""+" AND LOWER("+dbConfiguration.getGroupNameField()+") LIKE ?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getGroupNameField()+") LIKE ?";
				hashMap.put(count+"", "%"+groupName.toLowerCase()+"%");
				count = count+1;        	
			}

			if(!"".equals(idleTime)){
				queryString += ""+" AND "+dbConfiguration.getLastUpdateTimeField()+" <= ?";
				totalQueryString +=  ""+" AND "+dbConfiguration.getLastUpdateTimeField()+" <= ?";
				hashMap.put(count+"", new Timestamp(calender.getTime().getTime()));
				count = count+1;        	
			}

			queryString += ""+" AND "+dbConfiguration.getSessionCloseRequestField()+"='N' order by "+dbConfiguration.getIdentityField()+" desc";
			
			String connectionUrl = connection.getMetaData().getURL();
			String paginationQry = "";
				
			if(connectionUrl.contains(ConfigConstant.ORACLE)){
				paginationQry = " select * from ( select row_.*, rownum rownum_ from (" +queryString+ " ) row_ where rownum <= "+ (pageNo*pageSize) + " ) where rownum_ >=" + ((pageNo*pageSize) - (pageSize)+ 1) ;
			}else if(connectionUrl.contains(ConfigConstant.POSTGRESQL)){
				paginationQry = " select row_.* from (" +queryString+ " ) row_ limit "+ (pageSize) +" offset " + ((pageNo-1)*pageSize) ;
			}
			
			totalQueryString += ""+" AND "+dbConfiguration.getSessionCloseRequestField()+"='N'";
			
			Logger.logDebug(MODULE, "SQL QUERY:"+paginationQry);  
			Logger.logDebug(MODULE, "TOTAL QUERYSTRING:"+totalQueryString);
			PreparedStatement preparedStatement = connection.prepareStatement(paginationQry);
			PreparedStatement preparedStatementtotal = connection.prepareStatement(totalQueryString);


			Iterator itr = hashMap.keySet().iterator();

			while(itr.hasNext()){

				String key = (String)itr.next();
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
			rs2 = preparedStatementtotal.executeQuery();
			int totalItems=0;
			while(rs2.next()){

				totalItems=rs2.getInt("NUMROWS");
			}

			List list = new ArrayList();
			Calendar calender1 = Calendar.getInstance();
			Calendar calender2 = Calendar.getInstance();

			int rowcount = 0;
			
			if(dbConfiguration.getGroupNameField() == null){
				dbConfiguration.setGroupNameField("GROUPNAME");
			}
		
			//&& rowcount < pageSize
			
			while(rs.next())
			{                       
				asmData.setConcUserId(rs.getLong(dbConfiguration.getConcurrentUserId()));
				asmData.setUserName(rs.getString(dbConfiguration.getUserNameField()));
				asmData.setNasIpAddress(rs.getString(dbConfiguration.getNasIpAddressField()));
				asmData.setFramedIpAddress(rs.getString(dbConfiguration.getFramedIpAddressField()));
				asmData.setAcctSessionId(rs.getString(dbConfiguration.getSessionIdField()));
				asmData.setNasPortType(rs.getString(dbConfiguration.getNasPortTypeField()));
				asmData.setGroupName(rs.getString(dbConfiguration.getGroupNameField()));
				asmData.setUserIdentity(rs.getString(dbConfiguration.getUserIdentityField()));			
				asmData.setLastUpdatedTime(rs.getTimestamp(dbConfiguration.getLastUpdateTimeField()));
				asmData.setProtocolType(rs.getString(dbConfiguration.getProtocolType()));
				List<SMDBFieldMapData> mappingList = dbConfiguration.getFieldMappingList();
				Map<String,Object> mappingMap = new LinkedHashMap<String, Object>();
				if(mappingList != null){
					for(int i=0;i<mappingList.size();i++){						
						SMDBFieldMapData data = mappingList.get(i);
						Object value = rs.getObject(data.getDbFieldName());						
						mappingMap.put(data.getDbFieldName(),value);
					}
					
				}
				asmData.setMappingMap(mappingMap);

				Timestamp timestamp = getCurrentTimeStamp(connection);
				if(timestamp != null)
				calender2.setTime(timestamp);


				Timestamp timestamp1 = rs.getTimestamp(dbConfiguration.getStartTimeField());
				if(timestamp1 != null){
					calender1.setTime(timestamp1);
				}
				
				Date d1 = calender2.getTime();
				Date d2 = calender1.getTime();

				long dateDifference = d1.getTime() - d2.getTime(); 

				long seconds = dateDifference/1000;
				long minutes = seconds/60;
				long hours = minutes/60; 

				long minutes1 = minutes%60;

				String Duration = hours+""+" hrs"+":"+minutes1+""+" mins";

				asmData.setStartTime(Duration);

				list.add(asmData);
				asmData = new ASMData();
				rowcount++;
			}

			rs.close();
			preparedStatement.close();

			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(list, pageNo, totalPages,totalItems);
		}
		catch(Exception e){
			throw new DataManagerException("Error While Searching Active Sessions",e);
		}finally{
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
		}

		return pageList;
	}

	public PageList searchGroupByCriteria(IASMData asmData, int pageNo, int pageSize,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		PageList listgroupby = null;
		Connection connection = null;
		try{
			connection = getConnection(dbConfiguration);
			Statement stmt = connection.createStatement();
			String username = asmData.getUserName();
			String nasip = asmData.getNasIpAddress();
			String framedIp = asmData.getFramedIpAddress();
			String groupName = asmData.getGroupName();
			String groupbyCriteria = asmData.getGroupbyCriteria();
			String idleTime = asmData.getIdleTime();

			ResultSet rs1 = null;
			ResultSet rs2 = null;
			Calendar calender = Calendar.getInstance();
			Timestamp systemDate;
			if(!(idleTime.equalsIgnoreCase(""))){

				Timestamp timestamp = getCurrentTimeStamp(connection);
				systemDate = timestamp;

				calender.setTime(timestamp);
				calender.add(Calendar.MINUTE, -(Integer.parseInt(idleTime)));

			}


			ResultSet rs = null; 
			int count=1;
			Map hashMap = new HashMap(); 
			/*
			 * SELECT *
              FROM (SELECT
			  ROW_NUMBER() OVER (ORDER BY "+groupbyCriteria+" DESC) rownumber, u.*,count(*) AS nameCount
			  FROM "+dbConfiguration.getTableName()+" u
	          ) b WHERE 1=1 rownumber between 11 and 20;
			 */
			String queryString = "SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY "+groupbyCriteria+"  DESC) rownumber, "+groupbyCriteria+" , count(*) as NAMECOUNT FROM "+dbConfiguration.getTableName()+" u WHERE 1=1";

			String totalQueryString = "SELECT count(*) as numRows FROM(SELECT * FROM ( select a.*, ROW_NUMBER() over(order by "+groupbyCriteria+" DESC ) rnum from (SELECT "+groupbyCriteria+",count(*) AS nameCount FROM "+dbConfiguration.getTableName()+" a WHERE 1 = 1";

			if(!(username.equalsIgnoreCase(""))){
				queryString += ""+" AND LOWER("+dbConfiguration.getUserNameField()+") LIKE ?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getUserNameField()+") LIKE ?";
				hashMap.put(count+"", "%"+username.toLowerCase()+"%");
				count = count+1;
			}

			if(!(nasip.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getNasIpAddressField()+")=?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getNasIpAddressField()+")=?";
				hashMap.put(count+"", nasip.toLowerCase());
				count = count+1;        	
			}
			
			if(!(framedIp.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getFramedIpAddressField()+")=?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getFramedIpAddressField()+")=?";
				hashMap.put(count+"", framedIp.toLowerCase());
				count = count+1;        	
			}

			if(!(groupName.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getGroupNameField()+") LIKE ?";
				totalQueryString += ""+" AND LOWER("+dbConfiguration.getGroupNameField()+") LIKE ?";
				hashMap.put(count+"", "%"+groupName.toLowerCase()+"%");
				count = count+1;        	
			}

			if(!(idleTime.equalsIgnoreCase(""))){

				queryString += ""+" AND "+dbConfiguration.getLastUpdateTimeField()+" <= ?";
				totalQueryString +=  ""+" AND "+dbConfiguration.getLastUpdateTimeField()+" <= ?";
				hashMap.put(count+"", new Timestamp(calender.getTime().getTime()));
				count = count+1;        	
			}
			queryString += ""+" AND "+dbConfiguration.getSessionCloseRequestField()+"='N' GROUP BY "+groupbyCriteria+") b where rownumber between "+((pageNo*pageSize)-(pageSize)+1)+" and "+(pageNo*pageSize);
			totalQueryString += ""+" AND "+dbConfiguration.getSessionCloseRequestField()+"='N' GROUP BY "+groupbyCriteria+") a where 1=1) b) c";


			if(username.equalsIgnoreCase("") && nasip.equalsIgnoreCase("") && framedIp.equalsIgnoreCase("") && groupName.equalsIgnoreCase("") && idleTime.equalsIgnoreCase("")){

				totalQueryString = "SELECT count(*) as numRows FROM(SELECT * FROM ( select a.*, ROW_NUMBER() over(order by "+groupbyCriteria+" DESC ) rnum from (SELECT "+groupbyCriteria+",count(*) AS NAMECOUNT FROM "+dbConfiguration.getTableName()+" WHERE "+dbConfiguration.getSessionCloseRequestField()+"='N' GROUP BY "+groupbyCriteria+") a where 1=1) b) c";
				queryString = "SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY "+groupbyCriteria+"  DESC) rownumber, "+groupbyCriteria+" , count(*) as NAMECOUNT FROM "+dbConfiguration.getTableName()+" u where "+dbConfiguration.getSessionCloseRequestField()+"='N' group by "+groupbyCriteria+" ) b "+ 
				"where rownumber between "+((pageNo*pageSize)-(pageSize)+1)+" and "+(pageNo*pageSize);
			}



			PreparedStatement preparedStatement = connection.prepareStatement(queryString);
			PreparedStatement preparedStatementtotal = connection.prepareStatement(totalQueryString);

			Iterator itr = hashMap.keySet().iterator();

			while(itr.hasNext()){

				String key = (String)itr.next();
				Object value = hashMap.get(key);
				if(value instanceof java.sql.Timestamp) {
					preparedStatement.setTimestamp(Integer.parseInt(key), (java.sql.Timestamp)value);
					preparedStatementtotal.setTimestamp(Integer.parseInt(key), (java.sql.Timestamp)value);
				}else{
					preparedStatement.setString(Integer.parseInt(key), (String)value);
					preparedStatementtotal.setString(Integer.parseInt(key), (String)value);
				}

			}

			Logger.logDebug(MODULE,"queryString     : " +queryString);
			Logger.logDebug(MODULE,"totalQueryString: " +totalQueryString);

			rs = preparedStatement.executeQuery();
			rs2 = preparedStatementtotal.executeQuery();
			int totalItems=0;
			while(rs2.next()){

				totalItems=rs2.getInt("NUMROWS");
			}


			List list = new ArrayList();

			if(dbConfiguration.getGroupNameField() == null){
				dbConfiguration.setGroupNameField("GROUPNAME");
			}
			
			while(rs.next())
			{                       

				if(groupbyCriteria.equalsIgnoreCase("USER_NAME")){
					asmData.setUserName(rs.getString(dbConfiguration.getUserNameField()));
					asmData.setNameCount(rs.getString("NAMECOUNT"));
				}
				else if(groupbyCriteria.equalsIgnoreCase("NAS_IP_ADDRESS")){
					asmData.setNasIpAddress(rs.getString(dbConfiguration.getNasIpAddressField()));	
					asmData.setNameCount(rs.getString("NAMECOUNT"));
				}
				else{
					asmData.setGroupName(rs.getString(dbConfiguration.getGroupNameField()));	
					asmData.setNameCount(rs.getString("NAMECOUNT"));
				}

				list.add(asmData);
				asmData = new ASMData();
			}

			rs.close();


			preparedStatement.close();

			//int totalItems=list.size();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;
			listgroupby = new PageList(list, pageNo, totalPages,totalItems);
		}
		catch(Exception e){
			throw new DataManagerException("Error While Searching Active Sessions By Group",e);
		}finally{
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
		}
		return listgroupby;
	}


	public PageList search(IASMData asmData,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		PageList pageList = null;
		Connection connection = null;
		try{
			connection = getConnection(dbConfiguration);
			Statement stmt = connection.createStatement();
			String username = asmData.getUserName();
			String nasip = asmData.getNasIpAddress();
			String framedIp = asmData.getFramedIpAddress();
			String groupName = asmData.getGroupName();
			String idleTime = asmData.getIdleTime();

			ResultSet rs1 = null;
			Calendar calender = Calendar.getInstance();
			Timestamp systemDate;

			if(!(idleTime.equalsIgnoreCase(""))){

				Timestamp timestamp = getCurrentTimeStamp(connection);
				systemDate = timestamp;

				calender.setTime(timestamp);
				calender.add(Calendar.MINUTE, -(Integer.parseInt(idleTime)));

				Logger.logDebug(MODULE,"Ending date is "+calender.getTime());

			}

			ResultSet rs = null; 

			String queryString = "SELECT * FROM "+dbConfiguration.getTableName()+" WHERE 1 = 1 ";
			int count=1;
			Map hashMap = new HashMap();

			if(!(username.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getUserNameField()+") LIKE ?";
				hashMap.put(count+"", "%"+username.toLowerCase()+"%");
				count = count+1;
			}

			if(!(nasip.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getNasIpAddressField()+")=?";
				hashMap.put(count+"", nasip.toLowerCase());
				count = count+1; 
			}
			
			if(!(framedIp.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getFramedIpAddressField()+")=?";
				hashMap.put(count+"", framedIp.toLowerCase());
				count = count+1; 
			}

			if(!(groupName.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getGroupNameField()+") LIKE ?";
				hashMap.put(count+"", "%"+groupName.toLowerCase()+"%");
				count = count+1;
			}

			if(!(idleTime.equalsIgnoreCase(""))){

				queryString += ""+" AND "+dbConfiguration.getLastUpdateTimeField()+" <= ?";
				hashMap.put(count+"", new Timestamp(calender.getTime().getTime()));
				count = count+1; 

			}

			queryString += ""+" AND "+dbConfiguration.getSessionCloseRequestField()+"='N'";


			if(username.equalsIgnoreCase("") && nasip.equalsIgnoreCase("") && framedIp.equalsIgnoreCase("") && groupName.equalsIgnoreCase("") && idleTime.equalsIgnoreCase("")){

				queryString = "SELECT * FROM "+dbConfiguration.getTableName()+" WHERE "+dbConfiguration.getSessionCloseRequestField()+"='N'";

			}

			PreparedStatement preparedStatement = connection.prepareStatement(queryString);

			Iterator itr = hashMap.keySet().iterator();

			while(itr.hasNext()){

				String key = (String)itr.next();
				Object value = hashMap.get(key);
				if(value instanceof java.sql.Timestamp) {
					preparedStatement.setTimestamp(Integer.parseInt(key), (java.sql.Timestamp)value);
				}else{
					preparedStatement.setString(Integer.parseInt(key), (String)value);
				}

			}

			rs = preparedStatement.executeQuery();


			List list = new ArrayList();
			Calendar calender1 = Calendar.getInstance();
			Calendar calender2 = Calendar.getInstance();

			if(dbConfiguration.getFieldMappingList() != null && dbConfiguration.getFieldMappingList().size() > 0){
				List<SMDBFieldMapData> smdbFieldMapData=new ArrayList<SMDBFieldMapData>();
				smdbFieldMapData=dbConfiguration.getFieldMappingList();
				
				for(SMDBFieldMapData fieldMapData:smdbFieldMapData ){
					if(fieldMapData.getField()!=null && fieldMapData.getField().equals("User Identity")){
						dbConfiguration.setIdentityField(fieldMapData.getDbFieldName());
					}
					if(fieldMapData.getField()!=null &&fieldMapData.getField().equals("Session ID")){
						dbConfiguration.setSessionIdField(fieldMapData.getDbFieldName());
					}
					if(fieldMapData.getField()!=null &&fieldMapData.getField().equals("PDP Type")){
						dbConfiguration.setNasPortTypeField(fieldMapData.getDbFieldName());
					}
				}
			}
			
			if(dbConfiguration.getGroupNameField() == null){
				dbConfiguration.setGroupNameField("GROUPNAME");
			}

			while(rs.next())
			{                       
				asmData.setConcUserId(rs.getLong(dbConfiguration.getConcurrentUserId()));
				asmData.setUserName(rs.getString(dbConfiguration.getUserNameField()));
				asmData.setNasIpAddress(rs.getString(dbConfiguration.getNasIpAddressField()));
				asmData.setFramedIpAddress(rs.getString(dbConfiguration.getFramedIpAddressField()));
				asmData.setAcctSessionId(rs.getString(dbConfiguration.getSessionIdField()));
				asmData.setNasPortType(rs.getString(dbConfiguration.getNasPortTypeField()));
				asmData.setGroupName(rs.getString(dbConfiguration.getGroupNameField()));
				asmData.setUserIdentity(rs.getString(dbConfiguration.getUserIdentityField()));
				asmData.setLastUpdatedTime(rs.getTimestamp(dbConfiguration.getLastUpdateTimeField()));
				asmData.setProtocolType(rs.getString(dbConfiguration.getProtocolType()));
				
				Timestamp timestamp = getCurrentTimeStamp(connection);

				calender2.setTime(timestamp);


				Timestamp timestamp1 = rs.getTimestamp(dbConfiguration.getStartTimeField());

				calender1.setTime(timestamp1);


				System.out.println("calendar2 " + calender2.getTime());

				System.out.println("calendar1 " + calender1.getTime());

				Date d1 = calender2.getTime();
				Date d2 = calender1.getTime();

				long dateDifference = d1.getTime() - d2.getTime(); 

				long seconds = dateDifference/1000;
				long minutes = seconds/60;
				long hours = minutes/60; 

				long minutes1 = minutes%60;

				String Duration = hours+"hrs"+"/"+minutes1+"mins";

				asmData.setStartTime(Duration);

				list.add(asmData);
				asmData = new ASMData();
			}
			rs.close();
			preparedStatement.close();

			int pageNo=0;
			int totalItems=list.size();
			long totalPages = (long) Math.ceil(totalItems / 10);
			pageList = new PageList(list, pageNo, totalPages,totalItems);
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
		}

		return pageList;
	}
	private void rollback(Connection connection){
		try{
			if(connection!=null && !connection.isClosed()){
				connection.rollback();
			}
		}catch(Exception ex){

		}
	}
	public void closeSession(Long userId,SessionManagerDBConfiguration dbConfiguration) throws DataNotInSyncException,DataManagerException{
		Connection connection = null;
		try{
			connection = getConnection(dbConfiguration);
			Statement stmt = connection.createStatement();

			String queryString = "UPDATE "+dbConfiguration.getTableName()+" SET "+dbConfiguration.getSessionCloseRequestField()+"='Y' WHERE "+dbConfiguration.getConcurrentUserId()+"='"+userId+"'";

			stmt.executeUpdate(queryString);
		}
		catch(SQLException sqlexp){
			throw new DataManagerException("Error while closing session: "+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			throw new DataManagerException("Close Session Operation Failed.Reason"+e.getMessage(),e);

		}finally{
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
		}
	}

	public void closeSession(String[] userIds,SessionManagerDBConfiguration dbConfiguration) throws DataNotInSyncException,DataManagerException{
		Connection connection = null;
		PreparedStatement preparedUpdate = null;
		try{
			connection = getConnection(dbConfiguration);
			connection.setAutoCommit(false);

			preparedUpdate = connection.prepareStatement("UPDATE "+dbConfiguration.getTableName()+" SET "+dbConfiguration.getSessionCloseRequestField()+"='Y' WHERE "+dbConfiguration.getConcurrentUserId()+"=?");
			for (int i = 0; i < userIds.length; i++) {

				preparedUpdate.setLong(1, Long.parseLong(userIds[i]));
				preparedUpdate.executeUpdate();
				preparedUpdate.clearParameters();
			}

			preparedUpdate.close();
			connection.commit();

		}
		catch(SQLException sqlexp){

			rollback(connection);
			throw new DataManagerException("Error while closing session: "+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			rollback(connection);
			throw new DataManagerException("Close Session Operation Failed.Reason"+e.getMessage(),e);

		}finally{
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
		}
	}
	public void closeAllSession(IASMData asmData,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{

		Connection connection = null;
		try{
			connection = getConnection(dbConfiguration);
			connection.setAutoCommit(false);
			String username = asmData.getUserName();
			String nasip = asmData.getNasIpAddress();
			String framedIp = asmData.getFramedIpAddress();
			String groupName = asmData.getGroupName();

			String idleTime = asmData.getIdleTime();

			ResultSet rs1 = null;
			Calendar calender = Calendar.getInstance();
			if(!(idleTime.equalsIgnoreCase(""))){

				Timestamp timestamp =getCurrentTimeStamp(connection);

				calender.setTime(timestamp);
				calender.add(Calendar.MINUTE, -(Integer.parseInt(idleTime)));

				Logger.logDebug(MODULE,"Ending date is "+calender.getTime());

			}

			ResultSet rs = null; 
			int count=1;
			Map hashMap = new HashMap(); 

			String queryString = "UPDATE "+dbConfiguration.getTableName()+" SET "+dbConfiguration.getSessionCloseRequestField()+"='Y' WHERE 1=1";

			if(!(username.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getUserNameField()+") LIKE ?";
				hashMap.put(count+"", "%"+username.toLowerCase()+"%");
				count = count+1;
			}



			if(!(nasip.equalsIgnoreCase(""))){

				queryString += ""+" AND "+dbConfiguration.getNasIpAddressField()+"=?";
				hashMap.put(count+"", nasip);
				count = count+1;   
			}
			
			if(!(framedIp.equalsIgnoreCase(""))){

				queryString += ""+" AND "+dbConfiguration.getFramedIpAddressField()+"=?";
				hashMap.put(count+"", framedIp);
				count = count+1;   
			}

			if(!(groupName.equalsIgnoreCase(""))){

				queryString += ""+" AND LOWER("+dbConfiguration.getGroupNameField()+") LIKE ?";
				hashMap.put(count+"","%"+groupName.toLowerCase()+"%");
				count = count+1;  
			}

			if(!(idleTime.equalsIgnoreCase(""))){

				queryString += ""+" AND "+dbConfiguration.getLastUpdateTimeField()+" <= ?";
				hashMap.put(count+"", new Timestamp(calender.getTime().getTime()));
				count = count+1;        	
			}

			if(username.equalsIgnoreCase("") && nasip.equalsIgnoreCase("") && framedIp.equalsIgnoreCase("") && groupName.equalsIgnoreCase("") && idleTime.equalsIgnoreCase("")){

				queryString = "UPDATE "+dbConfiguration.getTableName()+" SET "+dbConfiguration.getSessionCloseRequestField()+"='Y'";

			}

			PreparedStatement preparedStatement = connection.prepareStatement(queryString);


			Iterator itr = hashMap.keySet().iterator();

			while(itr.hasNext()){

				String key = (String)itr.next();
				Object value = hashMap.get(key);
				if(value instanceof java.sql.Timestamp) {
					preparedStatement.setTimestamp(Integer.parseInt(key), (java.sql.Timestamp)value);
				}else{
					preparedStatement.setString(Integer.parseInt(key), (String)value);
				}

			}

			preparedStatement.executeUpdate();

			preparedStatement.close();

			connection.commit();
		}
		catch(SQLException sqlexp){
			rollback(connection);
			throw new DataManagerException("Error while purging all sessions: "+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			rollback(connection);
			throw new DataManagerException("Close All Session Operation Failed.Reason"+e.getMessage(),e);
		}finally{
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
		}
	}

	public List<Map<String,Object>> purgeClosedSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		List<Map<String,Object>> purgedSessionsList = new ArrayList<Map<String,Object>>();
		Connection connection = null;
		Connection  ipPoolConn = null;
		try{
			connection = getConnection(dbConfiguration);
			ipPoolConn = getIPPoolConnection();
			connection.setAutoCommit(false);
			ipPoolConn.setAutoCommit(false);
			
			Statement stmt = connection.createStatement();

			String strSelectClosedSession = "SELECT * FROM "+dbConfiguration.getTableName()+" WHERE "+dbConfiguration.getSessionCloseRequestField()+"='Y'";
			PreparedStatement psIpPool = ipPoolConn.prepareStatement(strReleaseIpAddressQuery);
			ResultSet rs = stmt.executeQuery(strSelectClosedSession);
			ResultSetMetaData  rsMetadata = rs.getMetaData();

			while(rs.next()){
				Map<String,Object> columnValueMap = new HashMap<String,Object>();
				int columnCount = rsMetadata.getColumnCount();

				for(int i=1;i<=columnCount ;i++){
					Object object  = rs.getObject(i);
					String columnName = rsMetadata.getColumnName(i);
					columnValueMap.put(columnName.toLowerCase(),object);
				}

				purgedSessionsList.add(columnValueMap);
				Object policyKeyObject =columnValueMap.get("uid_policy_key");
				Object ipAddressObject =columnValueMap.get(dbConfiguration.getFramedIpAddressField().toLowerCase());
				String policyKey = null;
				if(policyKeyObject!=null){
					policyKey = policyKeyObject.toString();
				}
				String ipAddress =null;
				if(ipAddressObject!=null){
					ipAddress = ipAddressObject.toString();
				}


				if(ipAddress!=null && ipAddress.trim().length()>0){
					psIpPool.setString(1, ipAddress);
					psIpPool.executeUpdate();
					psIpPool.clearParameters();
				}
			}	
			psIpPool.close();

			if(dbConfiguration.getFieldMappingList() != null && dbConfiguration.getFieldMappingList().size() > 0){
				List<SMDBFieldMapData> smdbFieldMapData=new ArrayList<SMDBFieldMapData>();
				smdbFieldMapData=dbConfiguration.getFieldMappingList();
				
				for(SMDBFieldMapData fieldMapData:smdbFieldMapData ){
					if(fieldMapData.getField()!=null && fieldMapData.getField().equals("User Identity")){
						dbConfiguration.setIdentityField(fieldMapData.getDbFieldName());
					}
				}
			}
			
			String queryString = "DELETE  FROM "+dbConfiguration.getTableName()+" WHERE "+dbConfiguration.getIdentityField()+" IN (SELECT "+dbConfiguration.getIdentityField()+" FROM (SELECT a.*,ROW_NUMBER() OVER (ORDER BY "+dbConfiguration.getIdentityField()+" ) rownumber from "+dbConfiguration.getTableName()+" a ) b WHERE b."+dbConfiguration.getSessionCloseRequestField()+"='Y' AND b.rownumber <= 5000) AND "+dbConfiguration.getSessionCloseRequestField()+"='Y'"; 
			stmt.executeUpdate(queryString);
			stmt.close();
			connection.commit();
			ipPoolConn.commit();
			return purgedSessionsList;
		}
		catch(SQLException sqlexp){
			rollback(connection);
			rollback(ipPoolConn);
			throw new DataManagerException("Error while purging closed session: "+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			rollback(connection);
			rollback(ipPoolConn);
			throw new DataManagerException("Purge Operation Failed.Reason"+e.getMessage(),e);

		}finally{
			
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
			setAutoCommit(ipPoolConn,true);
		}

	}
	
	public void setAutoCommit(Connection con, boolean autoCommit){
		if(con!=null){
			try{
				con.setAutoCommit(autoCommit);
			}catch(Exception e){
				Logger.logError(MODULE, "Error in setting auto commit parameter :"+e.getMessage());
			}
		}
	}

	public void purgeAllSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		Connection connection = null;
		Connection  ipPoolConn = null;
		try{
			ipPoolConn = getIPPoolConnection();
			connection = getConnection(dbConfiguration);

			Statement stmt = connection.createStatement();


			String queryString = "DELETE from "+dbConfiguration.getTableName();

			String releaseAllIPQuery = "UPDATE TBLMIPPOOLDETAIL set ASSIGNED='N' , LAST_UPDATED_TIME=CURRENT_TIMESTAMP where ASSIGNED='Y'";
			stmt.executeUpdate(queryString);
			stmt.close();

			//Releasing all IP Addresses.
			Statement stmtIpPool = ipPoolConn.createStatement();
			stmtIpPool.executeUpdate(releaseAllIPQuery);
			stmtIpPool.close();

		}
		catch(SQLException sqlexp){
			throw new DataManagerException("Purge All Operation Failed.Reason :"+sqlexp.getMessage(),sqlexp);
		}
		catch(Exception e){
			throw new DataManagerException("Purge All Operation Failed.Reason"+e.getMessage(),e);

		}finally{
			try{
				if(connection!=null){
					connection.close();
				}
			}catch(Exception e){}
		}
	}
}
