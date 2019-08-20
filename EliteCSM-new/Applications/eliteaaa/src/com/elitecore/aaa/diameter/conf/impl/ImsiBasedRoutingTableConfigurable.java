package com.elitecore.aaa.diameter.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.diameter.conf.ImsiBasedRoutingTableConfiguration;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
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
import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedRoutingTableData;

@XmlType(propOrder = {})
@XmlRootElement(name = "imsi-based-routing-tables")
@ConfigurationProperties(moduleName = "IMSI-BSD-ROUTING-TABLE-CONFIGURABLE", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey = "")
@XMLProperties(name = "imsi-based-routing-table", schemaDirectories = {"system","schema"}, configDirectories = {"conf","diameter"} )
public class ImsiBasedRoutingTableConfigurable extends Configurable 
	implements ImsiBasedRoutingTableConfiguration {

	private static final String MODULE = "IMSI-BSD-ROUTING-TABLE-CONFIGURABLE";
	/*
	 * TBLMIMSIFIELDMAP Column constants
	 */
	private static final String TBLM_IMSI_FIELDMAP = "TBLMIMSIFIELDMAP";
	private static final String IMSI_RANGE = "IMSIRANGE";
	public static final String PRIMARY_PEERNAME = "PRIMARYPEERNAME";
	public static final String SECONDARY_PEERNAME = "SECONDARYPEERNAME";
	public static final String TAG = "TAG";
	/*
	 * TBLMIMSIBASEDROUTINGTABLE Column Constants
	 */
	private static final String TBLM_IMSIBASED_ROUTINGTABLE = "TBLMIMSIBASEDROUTINGTABLE";
	private static final String ROUTING_TABLE_ID = "ROUTINGTABLEID";
	private static final String ROUTINGTABLE_NAME = "ROUTINGTABLENAME";
	private static final String IMSI_IDENTITY_ATTRIBUTES = "IMSIIDENTITYATTRIBUTES";
	
	private List<ImsiBasedRoutingTableDataImpl> imsiBasedRoutingTables;
	private Map<String, ImsiBasedRoutingTableDataImpl> imsiTableNameToImsiTable;

	public ImsiBasedRoutingTableConfigurable() {
		imsiBasedRoutingTables = new ArrayList<ImsiBasedRoutingTableDataImpl>();
		imsiTableNameToImsiTable = new HashMap<String, ImsiBasedRoutingTableDataImpl>();
	}
	
	@DBRead
	@DBReload
	public void read() throws Exception {
		Connection  connection = null;		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		

		try {						
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getImsiRoutingTableQuery());
			if (preparedStatement == null) {
				throw new SQLException("Unable to create prepared statement, while fetching Imsi Based Routing Details");
			}
			
			List<ImsiBasedRoutingTableDataImpl> tempImsiTables = new ArrayList<ImsiBasedRoutingTableDataImpl>();
			
			ImsiBasedRoutingTableDataImpl imsiBasedRoutingTableDataImpl;
			resultSet = preparedStatement.executeQuery();			
		
			while (resultSet.next()) {
				imsiBasedRoutingTableDataImpl = readTable(resultSet, connection);
				tempImsiTables.add(imsiBasedRoutingTableDataImpl);
			}
			this.imsiBasedRoutingTables = tempImsiTables;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	
	private String getImsiRoutingTableQuery() {
		return "SELECT * FROM " + TBLM_IMSIBASED_ROUTINGTABLE;
	}

	private ImsiBasedRoutingTableDataImpl readTable(ResultSet resultSet, 
			final Connection connection) throws SQLException {
		
		ImsiBasedRoutingTableDataImpl imsiTable = new ImsiBasedRoutingTableDataImpl();
		
		String imsiTableId = resultSet.getString(ROUTING_TABLE_ID);
		imsiTable.setName(resultSet.getString(ROUTINGTABLE_NAME));
		
		String imsiIdAttribute = resultSet.getString(IMSI_IDENTITY_ATTRIBUTES);
		if (Strings.isNullOrBlank(imsiIdAttribute)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Imsi-Identity Attribute not configured for Imsi-Table: " 
						+ imsiTable.getName() + ", Using Default Value: " + imsiTable.getImsiIdentityAttributeStr());
			}
		} else {
			imsiTable.setImsiIdentityAttributeStr(imsiIdAttribute.trim());
		}
		PreparedStatement preparedStatement = null;
		ResultSet resultSetForImsiEntries = null;		

		try {						
			preparedStatement = connection.prepareStatement(getImsiEntriesQueryFor());
			preparedStatement.setString(1, imsiTableId);
			
			if (preparedStatement == null) {
				throw new SQLException("Unable to create prepared statement, while fetching Imsi Based Routing Entries");
			}
			
			List<ImsiBasedRouteEntryDataImpl> imsiBasedRouteEntryDataList = new ArrayList<ImsiBasedRouteEntryDataImpl>();
			
			ImsiBasedRouteEntryDataImpl imsiBasedRouteEntryData;
			resultSetForImsiEntries = preparedStatement.executeQuery();			
		
			while (resultSetForImsiEntries.next()) {
				imsiBasedRouteEntryData = new ImsiBasedRouteEntryDataImpl();
				
				String imsiRange = resultSetForImsiEntries.getString(IMSI_RANGE);
				if (Strings.isNullOrBlank(imsiRange)) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().info(MODULE, "Skipping entry for Imsi-Table: " 
								+ imsiTable.getName() + ", Reason: Imsi-Range not configured.");
					}
					continue;
				} else {
					imsiBasedRouteEntryData.setImsiRange(imsiRange.trim());
				}
				String peer = resultSetForImsiEntries.getString(PRIMARY_PEERNAME);
				imsiBasedRouteEntryData.setPrimaryPeer(peer);
				imsiBasedRouteEntryData.setSecondaryPeer(resultSetForImsiEntries.getString(SECONDARY_PEERNAME));
				imsiBasedRouteEntryData.setTag(resultSetForImsiEntries.getString(TAG));
				
				imsiBasedRouteEntryDataList.add(imsiBasedRouteEntryData);
			}
			imsiTable.setEntries(imsiBasedRouteEntryDataList);
		} finally {
			DBUtility.closeQuietly(resultSetForImsiEntries);
			DBUtility.closeQuietly(preparedStatement);
		}
		return imsiTable;
	}
	

	private String getImsiEntriesQueryFor() {
		return "select " + IMSI_RANGE + ", " +
				PRIMARY_PEERNAME + ", " + SECONDARY_PEERNAME  +
				", " + TAG + 
				" from " + TBLM_IMSI_FIELDMAP + 
				" where " + ROUTING_TABLE_ID + " = ?";
	}


	@PostReload
	@PostRead
	public void postReadProcessing() {
		
		
		for (ImsiBasedRoutingTableDataImpl imsiTable : imsiBasedRoutingTables) {
			
			
			imsiTable.setImsiIdentityAttributes(Strings.splitter(',')
					.trimTokens()
					.split(imsiTable.getImsiIdentityAttributeStr()));
			
			imsiTableNameToImsiTable.put(imsiTable.getName(), imsiTable);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,  "\n" + imsiTable.toString());
			}
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		// Do nothing
	}
	
	@Override
	@XmlElement(name="imsi-based-routing-table")
	public List<ImsiBasedRoutingTableDataImpl> getImsiTables() {
		return imsiBasedRoutingTables;
	}


	@Override
	public ImsiBasedRoutingTableData getImsiBasedRoutingTableDataByName(String name) {
		return imsiTableNameToImsiTable.get(name);
	}
	
}
