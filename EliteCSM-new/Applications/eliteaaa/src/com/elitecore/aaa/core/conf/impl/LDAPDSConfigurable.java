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
import com.elitecore.aaa.core.config.LDAPDataSourceImpl;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
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
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;

@XmlType(propOrder = {})
@XmlRootElement(name = "ldap-datasources")
@ConfigurationProperties(moduleName ="LDAP_DS_CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "ldap-datasources", schemaDirectories = {"system","schema"}, configDirectories = {"conf","datasource"})
public class LDAPDSConfigurable extends Configurable {
	
	private static final String SELECT_QUERY_FOR_SEARCHBASEDN = "SELECT searchbasedn FROM tblmldapbasedndetail WHERE LDAPDSID=?";
	private static final String SELECT_QUERY = "select * from tblmldapds";
	private List<LDAPDataSource> ldapDataSourceList;
	private static final String MODULE = "LDAP_DS_CONFIGURABLE";

	
	public LDAPDSConfigurable(){
		ldapDataSourceList = new ArrayList<LDAPDataSource>();
	}
	
	@XmlElement(type = LDAPDataSourceImpl.class, name = "ldap-datasource")
	public List<LDAPDataSource> getLdapDataSourceList() {
		return ldapDataSourceList;
	}

	public void setLdapDataSourceList(List<LDAPDataSource> ldapDataSourceList) {
		this.ldapDataSourceList = ldapDataSourceList;
	}

	@DBRead
	public void readFromDB() throws SQLException{

		Connection connection = null;
		PreparedStatement stmntLdapDs = null;
		ResultSet rsLdapDS = null;
		try{
			
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			stmntLdapDs = connection.prepareStatement(getQueryForDs());		
			if(stmntLdapDs == null){
				throw new SQLException("Problem reading LDAP ds configurations, reason: prepared statement received is NULL");
			}
			rsLdapDS = stmntLdapDs.executeQuery();
			
			String ldapDsID = "";
			int version;
			int timeOut;
			int ldapSizeLimit;
			String userPrefix;
			String name ;
			int minPoolSize ;
			int maxPoolSize;
			List<LDAPDataSource> tempLdapDataSourceList = new ArrayList<LDAPDataSource>();
			
			while(rsLdapDS.next()){
				timeOut = 1000;
				ldapSizeLimit =1;
				userPrefix = "uid=";
				name ="";
				minPoolSize = 2;
				maxPoolSize = 5;
				version = 2;
				LDAPDataSource dataSource = null;

				ldapDsID = rsLdapDS.getString("LDAPDSID");
				
				ArrayList<String>strDnList = new ArrayList<String>();
				PreparedStatement stmntLdapDnDetails = null;
				ResultSet rsLdapDNDetails = null;
				try {
					stmntLdapDnDetails = connection.prepareStatement(getQueryDnList());
					
					if(stmntLdapDnDetails == null){
						throw new SQLException("Problem reading LDAP ds configurations, reason: configuration database connection not found");
					}
					
					stmntLdapDnDetails.setString(1,ldapDsID);
					rsLdapDNDetails = stmntLdapDnDetails.executeQuery();
					
					while(rsLdapDNDetails.next()){
						strDnList.add(rsLdapDNDetails.getString("searchbasedn"));
					}
				}finally {
					DBUtility.closeQuietly(stmntLdapDnDetails);
					DBUtility.closeQuietly(rsLdapDNDetails);
				}
				
				if(rsLdapDS.getString("NAME")!=null && rsLdapDS.getString("NAME").trim().length()>0){
					name = rsLdapDS.getString("NAME");
				}
				
				version = Numbers.parseInt(rsLdapDS.getString("LDAPVERSION"),version);
				
				String addressStr = rsLdapDS.getString("ADDRESS");
				
				String timeOutStr = rsLdapDS.getString("TIMEOUT");
				if(Strings.isNullOrBlank(timeOutStr) == false){
					timeOut = Numbers.parseInt(timeOutStr.trim(), timeOut);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Timeout parameter for Ldap Datasource configuration : "+name+" is not defined , using default value");
				}
				String ldapSizeLimitStr = rsLdapDS.getString("LDAPSIZELIMIT");
				if(Strings.isNullOrBlank(ldapSizeLimitStr) == false){
					ldapSizeLimit =  Numbers.parseInt(ldapSizeLimitStr.trim(),ldapSizeLimit);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "LDAP Size Limit parameter for Ldap Datasource configuration : "+name+" is not defined , using default value");
				}
				String userPrefixStr = rsLdapDS.getString("USERDNPREFIX");
				if(Strings.isNullOrBlank(userPrefixStr) == false){
					userPrefix =  userPrefixStr;
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "User DN Prefix parameter for Ldap Datasource configuration : "+name+" is not defined , using default value");
				}
				
				String minimumPoolSizeStr = rsLdapDS.getString("MINIMUMPOOL");
				if(Strings.isNullOrBlank(minimumPoolSizeStr) == false){
					minPoolSize = Numbers.parseInt(minimumPoolSizeStr.trim(), minPoolSize);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Minimum Pool Size parameter for Ldap Datasource configuration : "+name+" is not defined , using default value");
				}
				
				String maximumPoolSizeStr = rsLdapDS.getString("MAXIMUMPOOL");
				if(Strings.isNullOrBlank(maximumPoolSizeStr) == false){
					maxPoolSize = Numbers.parseInt(maximumPoolSizeStr.trim(), maxPoolSize);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Maximum Pool Size parameter for Ldap Datasource configuration : "+name+" is not defined , using default value");
				}

				dataSource = new LDAPDataSourceImpl(ldapDsID,name, version, addressStr,
						timeOut,ldapSizeLimit,rsLdapDS.getString("ADMINISTRATOR"), rsLdapDS.getString("PASSWORD"),
						userPrefix,maxPoolSize,minPoolSize,strDnList );
				tempLdapDataSourceList.add(dataSource);
			}
			this.ldapDataSourceList = tempLdapDataSourceList;
			LogManager.getLogger().trace(MODULE,"LDAP ds configuration count: " + ldapDataSourceList.size());
			LogManager.getLogger().trace(MODULE,"Read configuration operation completed for LDAP ds");
		}finally{
			DBUtility.closeQuietly(rsLdapDS);
			DBUtility.closeQuietly(stmntLdapDs);
			DBUtility.closeQuietly(connection);
		}
	
	}

	@PostRead
	public void postReadProcessing() {
		// Do nothing
		
	}
	private String getQueryForDs(){
		return SELECT_QUERY;
	}
	private String getQueryDnList(){
		return SELECT_QUERY_FOR_SEARCHBASEDN;
	}
	
	@PostWrite
	public void postWriteProcessing(){
		// Do nothing
	}

	@PostReload
	public void postReloadProcessing(){
		// Do nothing
	}
	@DBReload
	public void dbReload(){
		// Do nothing
	}
}
