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
import com.elitecore.aaa.diameter.conf.MsisdnBasedRoutingTableConfiguration;
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
import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedRoutingTableData;

@XmlType(propOrder = {})
@XmlRootElement(name = "msisdn-based-routing-tables")
@ConfigurationProperties(moduleName = "MSISDN-BSD-ROUTING-TABLE-CONFIGURABLE", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey = "")
@XMLProperties(name = "msisdn-based-routing-table", schemaDirectories = {"system","schema"}, configDirectories = {"conf","diameter"} )
public class MsisdnBasedRoutingTableConfigurable extends Configurable 
	implements MsisdnBasedRoutingTableConfiguration {

	private static final String MODULE = "MSISDN-BSD-ROUTING-TABLE-CONFIGURABLE";
	/*
	 * TBLMMSISDNFIELDMAP Column constants
	 */
	private static final String TBLM_MSISDN_FIELDMAP = "TBLMMSISDNFIELDMAP";
	private static final String MSISDN_RANGE = "MSISDNRANGE";
	public static final String PRIMARY_PEERNAME = "PRIMARYPEERNAME";
	public static final String SECONDARY_PEERNAME = "SECONDARYPEERNAME";
	public static final String TAG = "TAG";
	/*
	 * TBLMMSISDNBASEDROUTINGTABLE Column Constants
	 */
	private static final String TBLM_MSISDN_BASED_ROUTINGTABLE = "TBLMMSISDNBASEDROUTINGTABLE";
	private static final String ROUTING_TABLE_ID = "ROUTINGTABLEID";
	private static final String ROUTINGTABLE_NAME = "ROUTINGTABLENAME";
	private static final String MSISDN_IDENTITY_ATTRIBUTES = "MSISDNIDENTITYATTRIBUTES";
	private static final String MSISDN_LENGTH = "MSISDN_LENGTH";
	private static final String MCC = "MCC";
	
	private List<MsisdnBasedRoutingTableDataImpl> msisdnBasedRoutingTables;
	private Map<String, MsisdnBasedRoutingTableDataImpl> msisdnTableNameToMsisdnTable;

	public MsisdnBasedRoutingTableConfigurable() {
		msisdnBasedRoutingTables = new ArrayList<MsisdnBasedRoutingTableDataImpl>();
		msisdnTableNameToMsisdnTable = new HashMap<String, MsisdnBasedRoutingTableDataImpl>();
	}
	
	@DBRead
	@DBReload
	public void read() throws Exception {
		Connection  connection = null;		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		

		try {						
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getMsisdnRoutingTableQuery());
			if (preparedStatement == null) {
				throw new SQLException("Unable to create prepared statement, while fetching Msisdn Based Routing Details");
			}
			
			List<MsisdnBasedRoutingTableDataImpl> tempMsisdnBasedRoutingTables = new ArrayList<MsisdnBasedRoutingTableDataImpl>();
			
			MsisdnBasedRoutingTableDataImpl msisdnBasedRoutingTableDataImpl;
			resultSet = preparedStatement.executeQuery();			
		
			while (resultSet.next()) {
				msisdnBasedRoutingTableDataImpl = readTable(resultSet, connection);
				tempMsisdnBasedRoutingTables.add(msisdnBasedRoutingTableDataImpl);
			}
			this.msisdnBasedRoutingTables = tempMsisdnBasedRoutingTables;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	
	private String getMsisdnRoutingTableQuery() {
		return "SELECT * FROM " + TBLM_MSISDN_BASED_ROUTINGTABLE;
	}

	private MsisdnBasedRoutingTableDataImpl readTable(ResultSet resultSet, 
			final Connection connection) throws SQLException {
		
		MsisdnBasedRoutingTableDataImpl msisdnBasedRoutingTableData = new MsisdnBasedRoutingTableDataImpl();
		
		String msisdnTableId = resultSet.getString(ROUTING_TABLE_ID);
		msisdnBasedRoutingTableData.setName(resultSet.getString(ROUTINGTABLE_NAME));
		
		String msisdnIdAttribute = resultSet.getString(MSISDN_IDENTITY_ATTRIBUTES);
		if (Strings.isNullOrBlank(msisdnIdAttribute)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Msisdn-Identity Attribute not configured for Msisdn-Table: " 
						+ msisdnBasedRoutingTableData.getName() + ", Using Default Value: " + 
						msisdnBasedRoutingTableData.getMsisdnIdentityAttributeStr());
			}
		} else {
			msisdnBasedRoutingTableData.setMsisdnIdentityAttributeStr(msisdnIdAttribute.trim());
		}
		msisdnBasedRoutingTableData.setMcc(resultSet.getString(MCC));
		msisdnBasedRoutingTableData.setMsisdnLength(resultSet.getInt(MSISDN_LENGTH));
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSetForMsisdnEntries = null;		

		try {						
			preparedStatement = connection.prepareStatement(getMsisdnEntriesQueryFor());
			preparedStatement.setString(1, msisdnTableId);
			if (preparedStatement == null) {
				throw new SQLException("Unable to create prepared statement, while fetching Msisdn Based Routing Entries");
			}
			List<MsisdnBasedRouteEntryDataImpl> msisdnBasedRouteEntryDataList = new ArrayList<MsisdnBasedRouteEntryDataImpl>();
			
			MsisdnBasedRouteEntryDataImpl msisdnBasedRouteEntryData;
			resultSetForMsisdnEntries = preparedStatement.executeQuery();			
		
			while (resultSetForMsisdnEntries.next()) {
				msisdnBasedRouteEntryData = new MsisdnBasedRouteEntryDataImpl();
				
				String msisdnRange = resultSetForMsisdnEntries.getString(MSISDN_RANGE);
				if (Strings.isNullOrBlank(msisdnRange)) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().info(MODULE, "Skipping entry for Msisdn-Table: " 
								+ msisdnBasedRoutingTableData.getName() + ", Reason: Msisdn-Range not configured.");
					}
					continue;
				} else {
					msisdnBasedRouteEntryData.setMsisdnRange(msisdnRange.trim());
				}
				String peer = resultSetForMsisdnEntries.getString(PRIMARY_PEERNAME);
				if (Strings.isNullOrBlank(peer)) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().info(MODULE, "Skipping entry for Msisdn-Table: " 
								+ msisdnBasedRoutingTableData.getName() + ", Reason: Primary Peer not configured.");
					}
					continue;
				} else {
					msisdnBasedRouteEntryData.setPrimaryPeer(peer.trim());
				}
				msisdnBasedRouteEntryData.setSecondaryPeer(resultSetForMsisdnEntries.getString(SECONDARY_PEERNAME));
				msisdnBasedRouteEntryData.setTag(resultSetForMsisdnEntries.getString(TAG));
				
				msisdnBasedRouteEntryDataList.add(msisdnBasedRouteEntryData);
			}
			msisdnBasedRoutingTableData.setEntries(msisdnBasedRouteEntryDataList);
		} finally {
			DBUtility.closeQuietly(resultSetForMsisdnEntries);
			DBUtility.closeQuietly(preparedStatement);
		}
		return msisdnBasedRoutingTableData;
	}
	

	private String getMsisdnEntriesQueryFor() {
		return "select " + MSISDN_RANGE + ", " +
				PRIMARY_PEERNAME + ", " + SECONDARY_PEERNAME  +
				", " + TAG + 
				" from " + TBLM_MSISDN_FIELDMAP + 
				" where " + ROUTING_TABLE_ID + " = ?";
	}


	@PostReload
	@PostRead
	public void postReadProcessing() {
		
		
		for (MsisdnBasedRoutingTableDataImpl msisdnTable : msisdnBasedRoutingTables) {
			
			
			msisdnTable.setMsisdnIdentityAttributes(Strings.splitter(',')
					.trimTokens()
					.split(msisdnTable.getMsisdnIdentityAttributeStr()));
			
			msisdnTableNameToMsisdnTable.put(msisdnTable.getName(), msisdnTable);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "\n" + msisdnTable.toString());
			}
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		// Do nothing
	}
	
	@Override
	@XmlElement(name="msisdn-based-routing-table")
	public List<MsisdnBasedRoutingTableDataImpl> getMsisdnTables() {
		return msisdnBasedRoutingTables;
	}


	@Override
	public MsisdnBasedRoutingTableData getMsisdnBasedRoutingTableDataByName(String name) {
		return msisdnTableNameToMsisdnTable.get(name);
	}
	
}
