package com.elitecore.netvertex.gateway.diameter.utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.util.ConfigLogger;

public class CiscoPackageMapping implements Cacheable {
	
	private static final String MODULE = "PCKG-MAP";
	private static final String DISPLAY_NAME = "CISCO-PCKG-MAPPING";
	private static final String SOURCE_STRING = "--";
	private Map<String, String> packageMap;
	private NetVertexServerContext serverContext;
	private boolean isInitialized;

	private static CiscoPackageMapping packageMapping ;

	static{
		packageMapping = new CiscoPackageMapping();
	}

	public CiscoPackageMapping(){
		this.isInitialized = false;
		this.packageMap = new HashMap<String, String>();
	}

	public static CiscoPackageMapping getInstance(){
		return packageMapping;
	}

	
	public void init(NetVertexServerContext serverContext) throws InitializationFailedException{
		if(isInitialized){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Cisco package mapping is already initialized");
			return ;
		}
		this.serverContext = serverContext;
		if(getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info(MODULE, "Initializing Cisco package mapping");
		Connection connection = null;
		PreparedStatement psForServiceMapping = null;
		ResultSet rsForServiceMapping = null;
		try{
			connection = NetVertexDBConnectionManager.getInstance().getConnection();
			String queryForServiceMapping = "SELECT PACKAGEID, NAME FROM TBLMCISCOPACKAGEMAPPING";
			psForServiceMapping = connection.prepareStatement(queryForServiceMapping);
			if(psForServiceMapping == null){
				throw new InitializationFailedException("Problem initializing Cisco package mapping, reason: preparestatment not found.");
			}
			psForServiceMapping.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_SEC);
			rsForServiceMapping = psForServiceMapping.executeQuery();
			Map<String, String> tempPackageMap = new HashMap<String, String>();
			while(rsForServiceMapping.next()){
				String ruleName = rsForServiceMapping.getString("NAME");
				String packageId = rsForServiceMapping.getString("PACKAGEID");
				
				if(ruleName == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Rule Name is not provided"+ (packageId != null ? " for Package ID = " + packageId : "") +"so skipping further processing");
					continue;
				}
				
				if(packageId == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Package id is not provided"+ (ruleName != null ? " for Rule Name = " + ruleName : "") +"so skipping further processing");
					continue;
				}
				
				tempPackageMap.put(ruleName, packageId);
			}
			this.packageMap = tempPackageMap;
			isInitialized = true;
			ConfigLogger.getInstance().info(MODULE, this.toString());
			getLogger().info(MODULE, "Cisco package mapping initialization completed");
		}catch(DataSourceException e){
			throw new InitializationFailedException(e);
		}catch(SQLException sqlEx){
			throw new InitializationFailedException(sqlEx);
		}catch(Exception ex){
			if(getLogger().isLogLevel(LogLevel.ERROR)){
				getLogger().error(MODULE, "Error in initializing Cisco package mapping. Reasong : " + ex.getMessage());
				getLogger().trace(ex);
			}
		}finally{
			DBUtility.closeQuietly(rsForServiceMapping);
			DBUtility.closeQuietly(psForServiceMapping);
			DBUtility.closeQuietly(connection);
		}
	}
	
	public String getPackageID(String authRuleName){
		return packageMap.get(authRuleName);
	}
	
	@Override
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    [Name = Package ID]");
		for(Map.Entry<String, String> entry : packageMap.entrySet()){
			out.println("      " + entry.getKey() + " = " + entry.getValue());
		}
		out.println();
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public String getName() {
		return MODULE;
	}

	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(DISPLAY_NAME);
		cacheDetail.setSource(getSource());
		try{
			isInitialized = false;
			init(serverContext);
			cacheDetail.setResultCode(CacheConstants.SUCCESS);

			getLogger().info(MODULE, "Reloding cache successfully for Cisco package mapping");

		} catch(InitializationFailedException initEx){
			LogManager.getLogger().error(MODULE, "Faiil in Reloding cache for Policy Manager. Reason : " + initEx.getMessage());
			LogManager.getLogger().trace(MODULE, initEx);
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Fail Reason : " + initEx.getMessage());
		}
		return cacheDetail;
	}
	
	private ILogger getLogger() {
			return LogManager.getLogger();
	}

	private String getSource(){
		return SOURCE_STRING;
	}

}