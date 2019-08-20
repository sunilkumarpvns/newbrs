package com.elitecore.aaa.diameter.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;

@ConfigurationProperties(moduleName = "PEER-GRP-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey = "")
@XMLProperties(name = "peer-groups", configDirectories = {"conf", "db", "diameter"}, schemaDirectories = {"system", "schema", "diameter"})
@XmlRootElement(name = "peer-groups")
public class DiameterPeerGroupConfigurable extends Configurable {

	private static final String MODULE = "PEER-GRP-CONFIGURABLE";
	
	private List<PeerGroupData> peerGroups = new ArrayList<PeerGroupData>();
	private Map<String, PeerGroupData> groupIdToGroup = new HashMap<String, PeerGroupData>();
	private Map<String, PeerGroupData> groupNameToGroup = new HashMap<String, PeerGroupData>();


	@XmlElement(name = "peer-group")
	public List<PeerGroupData> getPeerGroups() {
		return peerGroups;
	}
	
	
	@DBRead
	public void readFromDB() throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();

			String query = getQuery();
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				PeerGroupData peerGroupData = new PeerGroupData();
				String peerGroupId = resultSet.getString("PEERGROUPID");
				peerGroupData.setId(peerGroupId);
				peerGroupData.setName(resultSet.getString("PEERGROUPNAME"));
				peerGroupData.setStateFull(Boolean.parseBoolean(resultSet.getString("STATEFUL")));
				peerGroupData.setTransactionTimeoutInMs(resultSet.getLong("TRANSACTIONTIMEOUT"));
				peerGroupData.setGeoRedunduntGroupId(resultSet.getString("GRGROUPID"));
				readPeers(connection, peerGroupData);
				LogManager.getLogger().info(MODULE, "Configured Peer Groups:\n" +peerGroupData.toString());
				peerGroups.add(peerGroupData);
			}

			
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}


	private void readPeers(Connection connection, PeerGroupData peerGroupData) throws SQLException {
		
		PreparedStatement peersPreparedStmt = null;
		ResultSet peersResultSet = null;
		String peerSelectQuery = "select PEERNAME, WEIGHTAGE from TBLMDIAPEERPEERGROUPREL where PEERGROUPID = ?";
		
		try {
			
			peersPreparedStmt = connection.prepareStatement(peerSelectQuery);
			peersPreparedStmt.setString(1, peerGroupData.getId());
			peersResultSet = peersPreparedStmt.executeQuery();
			
			while (peersResultSet.next()) {
				PeerInfoImpl peerInfo = new PeerInfoImpl();
				peerInfo.setPeerName(peersResultSet.getString("PEERNAME"));
				peerInfo.setLoadFactor(peersResultSet.getInt("WEIGHTAGE"));
				peerGroupData.getPeers().add(peerInfo);
			}
		} finally {
			DBUtility.closeQuietly(peersResultSet);
			DBUtility.closeQuietly(peersPreparedStmt);
		}
	}
	
	private String getQuery() {
		return "SELECT * FROM TBLMDIAMETERPEERGROUP";
	}


	@PostRead
	public void postRead() {
		storeInDatastructures();
	}

	private void storeInDatastructures() {
		for (PeerGroupData peerGroupData : peerGroups) {
			groupIdToGroup.put(peerGroupData.getId(), peerGroupData);
			groupNameToGroup.put(peerGroupData.getName(), peerGroupData);
		}
	}
	
	@PostWrite
	public void postWrite() {
		// Do nothing
	}
	
	@PostReload
	public void postReload() {
		// Do nothing
	}

	public @Nullable PeerGroupData getPeerGroup(String peerGroupId) {
		return groupIdToGroup.get(peerGroupId);
	}
	
	public @Nullable PeerGroupData getPeerGroupByName(String peerGroupName) {
		return groupNameToGroup.get(peerGroupName);
	}
	
	public Map<String, PeerGroupData> getGroupNameToGroup() {
		return Collections.unmodifiableMap(this.groupNameToGroup);
	}
	
	public Map<String,PeerGroupData> getGroupIdMap(){
		return Collections.unmodifiableMap(this.groupIdToGroup);
	}
	
}
