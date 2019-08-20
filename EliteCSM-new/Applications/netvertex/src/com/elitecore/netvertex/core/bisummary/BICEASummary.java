package com.elitecore.netvertex.core.bisummary;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;

public class BICEASummary implements Cacheable{

	private static final String MODULE = "BI-SMR";
	private static final String DISPLAY_NAME = "BICEA-SUMMARY";
	
	private static BICEASummary summary;
	
	static{
		summary = new BICEASummary();
	}
	
	private ServerContext serverContext;
	
	private Map<String, String> biSummaryMap;
	
	private BICEASummary() {
		biSummaryMap = new HashMap<String, String>();
	}
	
	public void init(ServerContext serverContext) throws InitializationFailedException {
		this.serverContext = serverContext;
		readSummary();
	}

	public static BICEASummary getInstance() {
		return summary;
	}
	
	private void readSummary() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Reading BI/CEA Summary");
		Connection connection = null;
		Statement statement = null; 
		ResultSet resultSet = null;
		Map<String, String> tempBiSummaryMap = new HashMap<String, String>();
		try {
			connection = NetVertexDBConnectionManager.getInstance().getConnection();
			String query = "SELECT k.KEY, SK.SUBKEY, SK.VALUE FROM TBLMBICEATEMPLATE K, TBLMBICEASUBKEY SK WHERE SK.BICEAID = K.BICEAID";
			statement = connection.createStatement();
			statement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_SEC);
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				String key = resultSet.getString("KEY");
				String subKey = resultSet.getString("SUBKEY");
				String value = resultSet.getString("VALUE");
				String strTemp = key + "." + subKey;
				tempBiSummaryMap.put(strTemp, value);
			}
			biSummaryMap = tempBiSummaryMap;
			LogManager.getLogger().debug(MODULE, "BI/CEA Summary reading completed");
		}catch (DataSourceException e) {
			throw new InitializationFailedException("No DB connection available while reading BI/CEA Summary, Reason: " + e.getMessage(), e);
		} catch (SQLException sqlEx) {
			throw new InitializationFailedException("Error while reading NetVertex BI/CEA Summary. reason : " + sqlEx.getMessage(), sqlEx);
		} catch (Exception e) {
			throw new InitializationFailedException("Error while reading NetVertex BI/CEA Summary. reason : " + e.getMessage(), e);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	public String getValue(String key) {
		return biSummaryMap.get(key);
	}
	
	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(DISPLAY_NAME);
		cacheDetail.setSource("--");
		try{
			readSummary();
			cacheDetail.setResultCode(CacheConstants.SUCCESS);
			LogManager.getLogger().info(MODULE, "Reloading cache successful for BI/CEA Summary");
		} catch(InitializationFailedException e){
			LogManager.getLogger().error(MODULE, "Failed while Reloading cache for BI/CEA Summary. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Fail Reason: " + e.getMessage());
		}
		return cacheDetail;
	}

	@Override
	public String getName() {
		return MODULE;
	}
}