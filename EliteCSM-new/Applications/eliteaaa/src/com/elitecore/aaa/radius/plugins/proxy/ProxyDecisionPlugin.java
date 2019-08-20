package com.elitecore.aaa.radius.plugins.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.plugins.core.BaseRadPlugin;
import com.elitecore.aaa.radius.plugins.proxy.conf.ProxyDecisionPluginConf;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class ProxyDecisionPlugin extends BaseRadPlugin<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "PROXY-DECISSION-PLUGIN";
	private ProxyDecisionPluginConf config;
	private DBDataSource datasource;
	private DBConnectionManager dbConnectionManager = null;
	public ProxyDecisionPlugin(PluginContext pluginContext,PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}

	@Override
	public void init() throws InitializationFailedException {
		config = (ProxyDecisionPluginConf)getPluginConfiguration();
		if(config == null){
			throw new InitializationFailedException("Proxy decission plugin configuration is null");
		}
		datasource = ((AAAServerContext)getPluginContext().getServerContext()).getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(config.getDataSourceName());
		if(datasource == null){
			LogManager.getLogger().debug(MODULE, "DataSource for Proxy Decission plugin is null");
			throw new InitializationFailedException("DataSource for Proxy Decission plugin is null");
		}
		dbConnectionManager	= DBConnectionManager.getInstance(datasource.getDataSourceName());
		try {
			dbConnectionManager.init(datasource, getPluginContext().getServerContext().getTaskScheduler());
		} catch (DatabaseInitializationException e) {
			LogManager.getLogger().trace(MODULE,e.getMessage());
		} catch (DatabaseTypeNotSupportedException e) {
			LogManager.getLogger().trace(MODULE,e.getMessage());
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Proxy Decission Plugin Initialized Successfully");
	}
	@Override
	public void reInit() throws InitializationFailedException {
		config = (ProxyDecisionPluginConf)getPluginConfiguration();
		if(config==null){
			throw new InitializationFailedException("Proxy decission plugin configuration is null");
		}
	}

	@Override
	public void handlePreRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		PreparedStatement preparedStatement = null;
		
		Connection connection = null;
		String userIdentity;
		String query = "SELECT user_identity FROM " + config.getTableName() + " WHERE user_identity=?";
		String userID;
		ResultSet rs = null;
		try{
			connection = dbConnectionManager.getConnection();
			userIdentity = config.getUserIdentity();
			if(userIdentity == null || userIdentity.trim().length() == 0){
				LogManager.getLogger().info(MODULE, "Configuration for user identity not found. Using 0:1 as default user identity");
				userIdentity = "0:1";
			}
			IRadiusAttribute userIdentityAttribute = ((RadServiceRequest)serviceRequest).getRadiusAttribute(userIdentity);
			if(userIdentityAttribute == null){
				LogManager.getLogger().warn(MODULE, "Configured attribute not found in the request. Returning from further processing . ");
				return;
			}
			userID = userIdentityAttribute.getStringValue();
			preparedStatement = connection.prepareStatement(query);
			if(preparedStatement == null){
				LogManager.getLogger().debug(MODULE, "Prepared statement is null. Returning fron further processing");
				return;
			}
			preparedStatement.setString(1, userID.trim());
			rs = preparedStatement.executeQuery();
			if (!rs.next()) {						
				IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_RESOURCE_MANAGER_AVPAIR);
				radiusAttribute.setStringValue("unknown_user");
				((RadServiceRequest)serviceRequest).addInfoAttribute(radiusAttribute);
				LogManager.getLogger().info(MODULE, "User Unknown. Attriute added to the Request: " + (RadServiceRequest)serviceRequest);
			}
		}catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"Data Source " + config.getDataSourceName() + " is unavailable, Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e.getMessage());
		} catch (SQLException e) {
			LogManager.getLogger().trace(MODULE,e.getMessage());
		} finally {
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}

	}

	@Override
	public void handlePostRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		
		
	}
	
	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(getPluginName());
		cacheDetail.setResultCode(CacheConstants.SUCCESS);
		cacheDetail.setSource("--");

		//FIXME : Implement reload.
		/*try {
			ProxyDecisionPluginConfImpl config = (ProxyDecisionPluginConfImpl)getPluginConfiguration();
			if(config == null){
				cacheDetail.setResultCode(CacheConstants.FAIL);
				cacheDetail.setDescription("Failed, reason: String Plugin configuration is null");
			}else{
				config.readConfiguration();			
				this.init();
			}

		} catch (LoadConfigurationException e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Failed, reason: " + e.getMessage());
		} catch (InitializationFailedException e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Failed, reason: " + e.getMessage());
		}				
		return cacheDetail;
	}*/
		return cacheDetail;
	}
}