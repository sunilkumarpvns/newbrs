package com.elitecore.aaa.radius.sessionx.conf.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.sessionx.ConcurrentPolicyConstants;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;

public class ConcurrentLoginPolicyContainer implements Cacheable, ConcurrentLoginPolicyConfiguration {

	private static final String MODULE = "CONC-LGN-PLCY-CONTAINER";
	
	private ConcurrentLoginPolicyConfigurable policyConfigurable;
	
	private static final String SYSTEM_FOLDER = "system";
	private static final String CONCURRENT_POLICY_FILENAME = "_sys.concurrentloginpolicy";

	private ServerContext serverContext;
	
	public ConcurrentLoginPolicyContainer(ServerContext serverContext) {
		this.serverContext = serverContext;
		this.policyConfigurable = new ConcurrentLoginPolicyConfigurable();
	}
	
	public void readConfiguration() {
		
		try {
			List<ConcurrentLoginPolicyData> policyList = readFromDB();
			policyConfigurable.getConcurrentLoginPolicies().addAll(policyList);
			policyConfigurable.postRead();
		} catch (Exception e) {
			readFromBackup();
		}
		createBackupFile();
		serverContext.registerCacheable(this);
	}

	private void readFromBackup() {
		try {
			policyConfigurable = ConfigUtil.deserialize(new File(backupFileLocation()), ConcurrentLoginPolicyConfigurable.class);
			policyConfigurable.postRead();
		} catch (FileNotFoundException ex) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Failed to initialize concurrent login policy. Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
		} catch (JAXBException ex) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Failed to initialize concurrent login policy. Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
		}
	}
	
	private void createBackupFile() {
		
		try {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Serialize configuration of concurrent login policy.");
			}
			ConfigUtil.serialize(new File(backupFileLocation()), ConcurrentLoginPolicyConfigurable.class, policyConfigurable);
		} catch (JAXBException e) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Failed to serialize configuration of concurrent login policy. Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(e);
		} catch (IOException e) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Failed to serialize configuration of concurrent login policy. Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(e);
		}
	}

	private String backupFileLocation() {
		return serverContext.getServerHome() + File.separator + SYSTEM_FOLDER + File.separator + CONCURRENT_POLICY_FILENAME;
	}
	
	private List<ConcurrentLoginPolicyData> readFromDB() throws Exception {

		Connection connection = null;

		PreparedStatement preparedStatement = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		ResultSet rs = null;

		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getQueryForconcurrentLoginPolicyData());
			ps = connection.prepareStatement(getQueryForConcurrentLoginPolicyDetils());
			if(preparedStatement == null){
				throw new SQLException("Cannot establish connection to database during reading Concurrent policy Configuration , Reason : Prepared Statement is null");
			}
			resultSet = preparedStatement.executeQuery();
			List<ConcurrentLoginPolicyData> policyLists = new ArrayList<ConcurrentLoginPolicyData>();
			while(resultSet.next()){
				ConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
				
				concurrentLoginPolicyData.setConcurrentLoginId(resultSet.getString("CONCURRENTLOGINID"));
				concurrentLoginPolicyData.setName(resultSet.getString("NAME"));
				concurrentLoginPolicyData.setDescription(resultSet.getString("DESCRIPTION"));
				concurrentLoginPolicyData.setPolicyType(resultSet.getString("CONCURRENTLOGINPOLICYTYPEID"));
				concurrentLoginPolicyData.setPolicyMode(resultSet.getString("CONCURRENTLOGINPOLICYMODEID"));
				concurrentLoginPolicyData.setServiceType(resultSet.getString("ATTRIBUTE"));
				concurrentLoginPolicyData.setMaxLogins(resultSet.getLong("MAXIMUMLOGIN"));
				
				if(ConcurrentPolicyConstants.SERVICE_WISE_POLICY.equals(concurrentLoginPolicyData.getPolicyMode())){
				
					ps.setString(1, concurrentLoginPolicyData.getConcurrentLoginId());
					rs= ps.executeQuery();										
					while(rs.next()){
						ServceWiseLogin serviceWiseLogin = new ServceWiseLogin();
						serviceWiseLogin.setConcurrentLoginPolicyId(concurrentLoginPolicyData.getConcurrentLoginId());
						serviceWiseLogin.setMaxServiceWiseSessions(rs.getLong("MAXIMUMLOGIN"));
						serviceWiseLogin.setServiceTypeValue(rs.getString("ATTRIBUTEVALUE"));
						concurrentLoginPolicyData.getServiceWiseLoginList().add(serviceWiseLogin);
					}
				}
				
				policyLists.add(concurrentLoginPolicyData);
			}
			return policyLists;
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(connection);
		}	
	}
	
	private String getQueryForConcurrentLoginPolicyDetils() {
		return "select * from TBLTCONCURRENTLOGINPOLICYDET where CONCURRENTLOGINID = ?";
	}

	private String getQueryForconcurrentLoginPolicyData() {
		return "SELECT * FROM TBLMCONCURRENTLOGIN where COMMONSTATUSID = '"+CommonConstants.DATABASE_POLICY_STATUS_ACTIVE+"'";
	}
	
	public ConcurrentLoginPolicyData getConcurrentLoginPolicyData(String policyName) { 
		return policyConfigurable.getConcurrentPolicyData(policyName);
	}

	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider data = new CacheDetailProvider();
		data.setName(getName());
		data.setSource("AAA_Server_DS");
		try {
			ConcurrentLoginPolicyConfigurable tmpPolicyConfigurable = new ConcurrentLoginPolicyConfigurable();
			List<ConcurrentLoginPolicyData> policyList = readFromDB();
			tmpPolicyConfigurable.getConcurrentLoginPolicies().addAll(policyList);
			tmpPolicyConfigurable.postRead();
			this.policyConfigurable = tmpPolicyConfigurable;
			createBackupFile();
			
			data.setResultCode(CacheConstants.SUCCESS);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Reloaded Concurrent-Login-Policies.\n " + this.policyConfigurable.getConcurrentLoginPolicies());
			}
		} catch (Exception e) {
			
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Reloading Concurrent-Login-Policies from backup, Reason: " + e.getMessage());
			}
			
			try {
				data.setSource(backupFileLocation());
				ConcurrentLoginPolicyConfigurable tmpPolicyConfiguration = new ConcurrentLoginPolicyConfigurable();
				tmpPolicyConfiguration = ConfigUtil.deserialize(new File(backupFileLocation()), ConcurrentLoginPolicyConfigurable.class);
				tmpPolicyConfiguration.postRead();
				this.policyConfigurable = tmpPolicyConfiguration;

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Reloaded Concurrent-Login-Policies from backup.\n" + this.policyConfigurable.getConcurrentLoginPolicies()); 
				}
				
				data.setResultCode(CacheConstants.SUCCESS);
			} catch (FileNotFoundException ex) {
				if (LogManager.getLogger().isErrorLogLevel()) {
					LogManager.getLogger().error(MODULE, "Failed to reload Concurrent-Login-Policies from backup, Reason: " + ex.getMessage());
				}
				data.setResultCode(CacheConstants.FAIL);
			} catch (JAXBException ex) {
				if (LogManager.getLogger().isErrorLogLevel()) {
					LogManager.getLogger().error(MODULE, "Failed to reload Concurrent-Login-Policies from backup, Reason: " + ex.getCause().getMessage());
				}
				data.setResultCode(CacheConstants.FAIL);
			}
		}
		return data;
	}

	@Override
	public String getName() {
		return "CONCURRENT-LOGIN-POLICY";
	}

	@Override
	public @Nullable ConcurrentLoginPolicyData getConcurrentLoginPolicy(String policyName) {
		return policyConfigurable.getConcurrentPolicyData(policyName);
	}
}