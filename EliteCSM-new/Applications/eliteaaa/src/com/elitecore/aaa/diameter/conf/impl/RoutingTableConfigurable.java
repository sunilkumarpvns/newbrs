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

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.DiameterRoutingTableConfiguration;
import com.elitecore.aaa.util.LogicalNameParser;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.routerx.SubscriberBasedRoutingTableData;

public abstract class RoutingTableConfigurable extends Configurable {

	protected List<DiameterRoutingTableConfigurationImpl> routingTableList;
	private Map<String, DiameterRoutingTableConfiguration> routingTableMap;
	
	public RoutingTableConfigurable() {
		this.routingTableList = new ArrayList<DiameterRoutingTableConfigurationImpl>();
		this.routingTableMap = new HashMap<String, DiameterRoutingTableConfiguration>();
	}
	
	@DBReload
	@DBRead
	public void readFromDB() throws Exception {
		Connection connection = null;
		String query = "";

		PreparedStatement psForTableId = null;
		ResultSet rsForTableId =  null;
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		
		
		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			query = getQueryForRoutingTableIds();
			
			psForTableId = connection.prepareStatement(query);

			rsForTableId = psForTableId.executeQuery();
			
			List<DiameterRoutingTableConfigurationImpl> tempRoutingTableList = new ArrayList<DiameterRoutingTableConfigurationImpl>();
			DiameterRoutingTableConfigurationImpl diameterRoutingTableConfiguration;
			if(rsForTableId.next()) {
				diameterRoutingTableConfiguration = new DiameterRoutingTableConfigurationImpl();
				String tableName = rsForTableId.getString("ROUTINGTABLENAME");
				OverloadAction overloadAction = OverloadAction.fromVal(rsForTableId.getString("OVERLOADACTION"));
				int resultCode = rsForTableId.getInt("RESULTCODE");
				String scriptInstanceName = rsForTableId.getString("ROUTINGSCRIPT");
				
				diameterRoutingTableConfiguration.setTableName(tableName);
				diameterRoutingTableConfiguration.setOverloadAction(overloadAction);
				diameterRoutingTableConfiguration.setResultCode(resultCode);
				diameterRoutingTableConfiguration.setScriptName(scriptInstanceName);
				
				List<RoutingEntryDataImpl> tempRoutingEntryDatas = new ArrayList<RoutingEntryDataImpl>();
				preparedStatement = connection.prepareStatement(getQueryForRoutingDetails(tableName));

				resultSet = preparedStatement.executeQuery();			
				while(resultSet.next()){

					RoutingEntryDataImpl routingEntryData = new RoutingEntryDataImpl();

					String destRealm = "*";
					String transMapId;
					String copyPacketMapId;
					String applicationIdValue= "0";
					String advancedCondition = null;
					String routingName="";
					boolean statefulRouting = true;
					long transActionTimeOut = 3000;
					String originRealm = "*";

					statefulRouting = resultSet.getBoolean("STATEFULROUTING");

					if (resultSet.getString("NAME") != null && resultSet.getString("NAME").trim().length() > 0){
						routingName=resultSet.getString("NAME").trim();
						routingEntryData.setRoutingName(routingName);
					}

					if (resultSet.getString("RULESET") != null && resultSet.getString("RULESET").trim().length() > 0){
						advancedCondition = resultSet.getString("RULESET").trim();
					}

					if (resultSet.getString("APPIDS") != null && resultSet.getString("APPIDS").trim().length() > 0){
						applicationIdValue = resultSet.getString("APPIDS").trim();
					}

					if (resultSet.getString("DESTREALM") != null && resultSet.getString("DESTREALM").trim().length() > 0){
						destRealm = resultSet.getString("DESTREALM").trim();
					}

					if (resultSet.getString("ORIGINHOST") != null && resultSet.getString("ORIGINHOST").trim().length() > 0){
						routingEntryData.setOriginHostIp(resultSet.getString("ORIGINHOST") .trim());
					}

					if (resultSet.getString("ORIGINREALM") != null && resultSet.getString("ORIGINREALM").trim().length() > 0){
						originRealm = resultSet.getString("ORIGINREALM").trim();
					}
					
					transActionTimeOut = resultSet.getLong("TRANSACTIONTIMEOUT");
					routingEntryData.setTransActionTimeOut(transActionTimeOut);
					routingEntryData.setAttachedRedirection(ConfigurationUtil.stringToBoolean(resultSet.getString("ATTACHEDREDIRECTION"), false));
					if(destRealm != null && destRealm.trim().length() > 0){
						routingEntryData.setDestRealm(destRealm);

						if(applicationIdValue != null && applicationIdValue.length() > 0){
							LogicalNameParser parser = new LogicalNameParser();
							parser.parse(applicationIdValue);
							String appIDs = Strings.join(",", parser.getValueToLogicalName().keySet());
							routingEntryData.setApplicationIds(appIDs);
						}else{
							LogManager.getLogger().debug(getModule(), "Application ID is not Defined for Diameter Realm Configuration For Realm "+destRealm+",while fetching Diameter Realm Configuration Details.");
						}

						int intRoutingAction = resultSet.getInt("ROUTINGACTION");
						routingEntryData.setRoutingAction(intRoutingAction);

						String routeId = resultSet.getString("ROUTINGCONFIGID");
						if(advancedCondition != null  && advancedCondition.trim().length() > 0 ){
							routingEntryData.setAdvancedCondition(advancedCondition);
						}
						
						transMapId = resultSet.getString("TRANSMAPCONFID");
						copyPacketMapId = resultSet.getString("COPYPACKETMAPCONFID");
						
						readTranslationOrCopyPacketMappingName(connection, routingEntryData, transMapId, copyPacketMapId, routingName);
						
						readFailOverDetails(connection, routingEntryData, routeId);
												
						readPeerGroupDetails(connection, routingEntryData, routeId);

						routingEntryData.setStatefulRouting(statefulRouting);
						routingEntryData.setOriginRealm(originRealm);
						/*
						 * Providing Subscriber-Based Tables
						 */
						String mode = resultSet.getString("SUBSCRIBERMODE");
						
						if (CommonConstants.IMSI_MSISDN.equals(mode)) {
							String subscriberTableId = resultSet.getString("IMSIBASEDROUTINGTABLEID");
							if (Strings.isNullOrEmpty(subscriberTableId) == false) {
								readImsiBasedSubscriberRoutingTblName(connection, routingEntryData,subscriberTableId);
							}
							subscriberTableId = resultSet.getString("MSISDNBASEDROUTINGTABLEID");
							if (Strings.isNullOrEmpty(subscriberTableId) == false) {
								readMsisdnBasedSubcriberRoutingTblName(connection, routingEntryData, subscriberTableId);
							}
						} else if (CommonConstants.MSISDN_IMSI.equals(mode)) {
							String subscriberTableId = resultSet.getString("MSISDNBASEDROUTINGTABLEID");
							if (Strings.isNullOrEmpty(subscriberTableId) == false) {
								readMsisdnBasedSubcriberRoutingTblName(connection, routingEntryData, subscriberTableId);
							}
							subscriberTableId = resultSet.getString("IMSIBASEDROUTINGTABLEID");
							if (Strings.isNullOrEmpty(subscriberTableId) == false) {
								readImsiBasedSubscriberRoutingTblName(connection, routingEntryData,subscriberTableId);
							}
						}
						tempRoutingEntryDatas.add(routingEntryData);
					}
				}
				RealmEntries realmEntries = new RealmEntries();
				realmEntries.setRealmEntryList(tempRoutingEntryDatas);
				diameterRoutingTableConfiguration.setRealmEntries(realmEntries);

				tempRoutingTableList.add(diameterRoutingTableConfiguration);
			}
			this.routingTableList = tempRoutingTableList;
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForTableId);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForTableId);
			DBUtility.closeQuietly(connection);
		}
		
	}

	private void readTranslationOrCopyPacketMappingName(Connection connection, RoutingEntryDataImpl routingEntryData,
			String transMapId, String copyPacketMapId, String routingName) throws SQLException {
		PreparedStatement psForTransMapName = null;
		ResultSet transMapNameRs = null;
		String trasMapName = null;
		try {
			if(Strings.isNullOrEmpty(transMapId) == false) {
				psForTransMapName = connection.prepareStatement(getQueryForTransMapName());
				psForTransMapName.setString(1, transMapId);
				transMapNameRs = psForTransMapName.executeQuery();
				if (transMapNameRs.next()) {
					trasMapName = transMapNameRs.getString("NAME");
				}
			} else if(Strings.isNullOrEmpty(copyPacketMapId) == false) {
				psForTransMapName = connection.prepareStatement(getQueryForCopyPacketName());
				psForTransMapName.setString(1, copyPacketMapId);
				transMapNameRs = psForTransMapName.executeQuery();
				if (transMapNameRs.next()) {
					trasMapName = transMapNameRs.getString("NAME");
				}
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(getModule(), "No Translation Mapping configured for Routing Entry: " + routingName);
				}
			}
			routingEntryData.setTransMapName(trasMapName);
		} finally {
			DBUtility.closeQuietly(transMapNameRs);
			DBUtility.closeQuietly(psForTransMapName);
		}
	}

	private void readFailOverDetails(Connection connection, RoutingEntryDataImpl routingEntryData, String routeId)
			throws SQLException {
		
		PreparedStatement psForFailover = null;
		ResultSet failoverParamRs = null;
		
		try {
			psForFailover = connection.prepareStatement(getQueryForFailoverParams());
			psForFailover.setString(1,routeId);
			failoverParamRs = psForFailover.executeQuery();
			List<DiameterFailoverConfigurationImpl> diameterFailoverDataList = new ArrayList<DiameterFailoverConfigurationImpl>();
			while (failoverParamRs.next()) {
				DiameterFailoverConfigurationImpl diameterFailoverConfigurationImpl  = new DiameterFailoverConfigurationImpl();
				String errorCode =failoverParamRs.getString("ERRORCODES");
				diameterFailoverConfigurationImpl.setErrorCodes(errorCode);
				diameterFailoverConfigurationImpl.setAction(failoverParamRs.getInt("FAILUREACTION"));
				diameterFailoverConfigurationImpl.setFailoverArguments(failoverParamRs.getString("FAILUREARGUMENTS"));
				diameterFailoverDataList.add(diameterFailoverConfigurationImpl);

			}
			routingEntryData.setFailoverDataList(diameterFailoverDataList);

		} finally {
			DBUtility.closeQuietly(failoverParamRs);
			DBUtility.closeQuietly(psForFailover);
		}
	}

	private void readPeerGroupDetails(Connection connection, RoutingEntryDataImpl routingEntryData, String routeId)
			throws SQLException {
		
		PreparedStatement psForPeerGroup = null;
		ResultSet peerGroupRs = null;
		List<PeerGroupImpl> tempPeerGroupData = new ArrayList<PeerGroupImpl>();
		
		try {
			psForPeerGroup = connection.prepareStatement(getQueryForPeerGroupId());
			psForPeerGroup.setString(1, routeId);
			peerGroupRs = psForPeerGroup.executeQuery();
			
			while (peerGroupRs.next()) {
				PeerGroupImpl peerGroup = new PeerGroupImpl();
				List<PeerInfoImpl> peerInfoList = new ArrayList<PeerInfoImpl>();
				peerGroup.setAdvancedConditionStr(peerGroupRs.getString("RULESET"));
				String peerGroupId = peerGroupRs.getString("PEERGROUPID");
				
				PreparedStatement psForPeerInfo = null;
				ResultSet peerInfoRs = null;
				
				try {
					
					psForPeerInfo = connection.prepareStatement(getQueryForPeerIdentity());
					psForPeerInfo.setString(1, peerGroupId);
					peerInfoRs = psForPeerInfo.executeQuery();
					
					while (peerInfoRs.next()) {
						
						PeerInfoImpl peerInfoImpl = new PeerInfoImpl();
						String peerName = peerInfoRs.getString("PEERNAME");
						if (Strings.isNullOrBlank(peerName) == false) {
							peerInfoImpl.setPeerName(peerName);
							peerInfoImpl.setLoadFactor(peerInfoRs.getInt("LOADFACTOR"));
							peerInfoList.add(peerInfoImpl);
						}
					}
					peerGroup.setPeerInfoList(peerInfoList);
					tempPeerGroupData.add(peerGroup);
				} finally {
					DBUtility.closeQuietly(peerInfoRs);
					DBUtility.closeQuietly(psForPeerInfo);
				}
				
			}
			routingEntryData.setPeerGroupList(tempPeerGroupData);
		} finally {
			DBUtility.closeQuietly(peerGroupRs);
			DBUtility.closeQuietly(psForPeerGroup);
		}
	}

	private void readMsisdnBasedSubcriberRoutingTblName(Connection connection, RoutingEntryDataImpl routingEntryData,
			String subscriberTableId) throws SQLException {
		PreparedStatement psForSubscriberTable = null;
		ResultSet rsForSubscriberTable = null;
		
		try {
			psForSubscriberTable = connection.prepareStatement(getQueryForMsisdnBasedRoutingTable());
			psForSubscriberTable.setString(1, subscriberTableId);
			rsForSubscriberTable = psForSubscriberTable.executeQuery();
			rsForSubscriberTable.next();
			routingEntryData.addSubscriberRoutingTableName(rsForSubscriberTable.getString("NAME"));
		} finally {
			DBUtility.closeQuietly(rsForSubscriberTable);
			DBUtility.closeQuietly(psForSubscriberTable);
		}
	}

	private void readImsiBasedSubscriberRoutingTblName(Connection connection,RoutingEntryDataImpl routingEntryData, String subscriberTableId) throws SQLException {
		PreparedStatement psForSubscriberTable = null;
		ResultSet rsForSubscriberTable = null;

		try {
			psForSubscriberTable = connection.prepareStatement(getQueryForImsiBasedRoutingTable());
			psForSubscriberTable.setString(1, subscriberTableId);
			rsForSubscriberTable = psForSubscriberTable.executeQuery();
			rsForSubscriberTable.next();
			routingEntryData.addSubscriberRoutingTableName(rsForSubscriberTable.getString("NAME"));
		}finally {
			DBUtility.closeQuietly(rsForSubscriberTable);
			DBUtility.closeQuietly(psForSubscriberTable);
			
		}
	}

	
	private String getQueryForImsiBasedRoutingTable() {
		return "SELECT ROUTINGTABLENAME AS NAME from TBLMIMSIBASEDROUTINGTABLE where "  +
				" ROUTINGTABLEID = ?";
	}
	
	private String getQueryForMsisdnBasedRoutingTable() {
		return "SELECT ROUTINGTABLENAME AS NAME from TBLMMSISDNBASEDROUTINGTABLE where "  +
				" ROUTINGTABLEID = ?";
	}

	@PostReload
	@PostRead
	public void doProcessing() {
		if(this.routingTableList!=null){
			
			ImsiBasedRoutingTableConfigurable imsiBasedRoutingTableConfigurable = getConfigurationContext().get(ImsiBasedRoutingTableConfigurable.class);
			MsisdnBasedRoutingTableConfigurable msisdnBasedRoutingTableConfigurable = getConfigurationContext().get(MsisdnBasedRoutingTableConfigurable.class);
			
			for(DiameterRoutingTableConfiguration diameterRealmsConfiguration: this.routingTableList){
				this.routingTableMap.put(diameterRealmsConfiguration.getTableName(), diameterRealmsConfiguration);
				
				for (RoutingEntryDataImpl routingEntryData : diameterRealmsConfiguration.getRoutingEntryDataList()) {
					
					for(String subcriberRoutingName : routingEntryData.getSubscriberRoutingTableNames()) {
						
						SubscriberBasedRoutingTableData subscriberBasedRoutingTable = imsiBasedRoutingTableConfigurable.getImsiBasedRoutingTableDataByName(subcriberRoutingName);
						if (subscriberBasedRoutingTable == null) {
							subscriberBasedRoutingTable = msisdnBasedRoutingTableConfigurable.getMsisdnBasedRoutingTableDataByName(subcriberRoutingName);
							if (subscriberBasedRoutingTable == null) {
								LogManager.getLogger().warn(getModule(), "Subscriber-Based Routing not found with Name: " + subcriberRoutingName);
								continue;
							}
						}
						routingEntryData.addSubscriberBasedRoutingTableData(subscriberBasedRoutingTable);
					}
				}
				LogManager.getLogger().debug(getModule(), diameterRealmsConfiguration.toString());
			}
		}
	}
	

	@PostWrite
	public void postWriteProcessing(){
		
	}

	
	private String getQueryForRoutingDetails(String tableName){
		return "select * from TBLMROUTINGCONFIG where ROUTINGTABLEID IN(select routingtableid from tblmroutingtable where ROUTINGTABLENAME = '"+tableName+"') ORDER BY ordernumber";
	}	
	private String getQueryForTransMapName(){
		return "select A.name from tblmtranslationmappingconf A where A.transmapconfid = ?";
	}
	
	private String getQueryForCopyPacketName() {
		return "select A.name from TBLMCOPYPACKETTRANSMAPCONF A where A.COPYPACKETMAPCONFID = ?";
	}
	private String getQueryForPeerGroupId(){
		return "select * from TBLMPEERGROUP A where A.routingconfigid = ?";
	}	
	private String getQueryForPeerIdentity(){
		return "select A.* , B.PEERNAME from tblmpeergrouprel A, tblmpeer B  where A.peergroupid = ? AND a.peeruuid = b.peeruuid"; 
	}	
	private String getQueryForFailoverParams(){
		return "select * from TBLMROUTINGCONFFAILUREPARAMS A where A.routingconfigid = ? ";
	}

	public void setRoutingTableList(
			List<DiameterRoutingTableConfigurationImpl> routingTableList) {
		this.routingTableList = routingTableList;
	}
	
	@XmlElement(name="routing-table")
	public List<DiameterRoutingTableConfigurationImpl> getRoutingTableList() {
		return routingTableList;
	}
	
	public DiameterRoutingTableConfiguration getDiameterRoutingTableConfiguration(String tableName) {
		return routingTableMap.get(tableName);		
	}
	
	protected abstract String getModule();
	
	protected abstract String getQueryForRoutingTableIds();
}
