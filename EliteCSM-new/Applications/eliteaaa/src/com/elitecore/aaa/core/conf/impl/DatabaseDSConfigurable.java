package com.elitecore.aaa.core.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.config.DBDataSourceImpl;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;

@XmlType(propOrder = {})
@XmlRootElement(name = "database-datasources")
@ConfigurationProperties(moduleName ="DB-DSCNF-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "database-datasources", schemaDirectories = {"system","schema"}, configDirectories = {"conf","datasource"})
public class DatabaseDSConfigurable extends Configurable{

	private static final String MODULE = "DB-DSCNF-CONFIGURABLE";
	private List<DBDataSource> dbDataSourceList;
	
	public DatabaseDSConfigurable(){
		dbDataSourceList = new ArrayList<DBDataSource>();
	}
	
	@XmlElement(type = DBDataSourceImpl.class, name = "database-datasource")
	public List<DBDataSource> getDbDataSourceList() {
		return dbDataSourceList;
	}

	public void setDbDataSourceList(List<DBDataSource> dbDataSourceList) {
		this.dbDataSourceList = dbDataSourceList;
	}

	@DBRead
	public void readFromDB() throws Exception {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		List<DBDataSource> dbDataSourceList = new ArrayList<DBDataSource>();
		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement("select * from tblmdatabaseds");
			if(preparedStatement == null){
				throw new SQLException("Problem reading database ds configurations, reason: prepared statement received is NULL");
			}
			rs = preparedStatement.executeQuery();

			String name;
			int minPoolSize ;
			int maxPoolSize;
			int statusCheckDuration = 120;
			int networkReadTimeoutInMs;
			
			while(rs.next()){

				name="";
				minPoolSize = 2;
				maxPoolSize = 5;
				DBDataSource dbDataSource = null;
				statusCheckDuration = 120;

				if(rs.getString("name")!=null && rs.getString("name").length()>0){
					name = rs.getString("name");
				}

				if(rs.getString("minimumpool")!=null && rs.getString("minimumpool").trim().length()>0)
					minPoolSize = Numbers.parseInt(rs.getString("minimumpool").trim(),minPoolSize);
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Minimum Pool Size parameter for Database Datasource configuration : "+name+" is not defined , using default value");
				}
				if(rs.getString("maximumpool")!=null && rs.getString("maximumpool").trim().length()>0)
					maxPoolSize = Numbers.parseInt(rs.getString("maximumpool").trim(),maxPoolSize);
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Minimum Pool Size parameter for Database Datasource configuration : "+name+" is not defined , using default value");
				}
				statusCheckDuration =  rs.getInt("STATUSCHECKDURATION");
				networkReadTimeoutInMs = rs.getInt("TIMEOUT");
				dbDataSource = new DBDataSourceImpl(rs.getString("databasedsid"),name,rs.getString("connectionurl"),rs.getString("username"),
						rs.getString("password"),minPoolSize,maxPoolSize, statusCheckDuration,networkReadTimeoutInMs);
				dbDataSourceList.add(dbDataSource);
			}		
			this.dbDataSourceList = dbDataSourceList;
			LogManager.getLogger().trace(MODULE,"Database ds configuration count: " + dbDataSourceList.size());
			LogManager.getLogger().trace(MODULE,"Read configuration operation completed for database ds");
		}finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	@PostRead
	public void postReadProcessing(){
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	
	@DBReload
	public void dbReload(){
		
	}
	
}
