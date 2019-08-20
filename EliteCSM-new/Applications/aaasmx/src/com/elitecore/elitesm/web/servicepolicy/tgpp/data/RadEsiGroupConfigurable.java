package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

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

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;

@ConfigurationProperties(moduleName = "RAD-ESI-GRP-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey = "")
@XMLProperties(name = "rad-esi-groups", configDirectories = {"conf", "db", "diameter"}, schemaDirectories = {"system", "schema", "diameter"})
@XmlRootElement (name = "rad-esi-groups")
public class RadEsiGroupConfigurable extends Configurable {

	private List<RadEsiGroupData> radEsiGroups = new ArrayList<RadEsiGroupData>();
	private Map<Integer, RadEsiGroupData> groupIdToGroup = new HashMap<Integer, RadEsiGroupData>();
	
	@XmlElement (name = "rad-esi-group")
	public List<RadEsiGroupData> getRadEsiGroups() {
		return radEsiGroups;
	}
	
	@DBRead
	public void readFromDB() throws SQLException {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		
			try {
				connection = EliteAAADBConnectionManager.getInstance().getConnection();
				String query = getQuery();
				preparedStatement = connection.prepareStatement(query);
				resultSet = preparedStatement.executeQuery();
				
				while(resultSet.next()) {
					RadEsiGroupData radEsiGroupData = new RadEsiGroupData();
					radEsiGroupData.setId(resultSet.getInt("ESIGROUPID"));
					radEsiGroupData.setName(resultSet.getString("ESIGROUPNAME"));
					
					PeerInfoImpl primaryEsi = new PeerInfoImpl();
					primaryEsi.setPeerName(resultSet.getString("PRIMARYESINAME"));
					primaryEsi.setLoadFactor(1);
				    radEsiGroupData.getEsis().add(primaryEsi);
				    
				    if (DBUtility.isValueAvailable(resultSet, "SECONDARYESINAME")) {
				    	PeerInfoImpl secondaryEsi = new PeerInfoImpl();
				    	secondaryEsi.setPeerName(resultSet.getString("SECONDARYESINAME"));
				    	secondaryEsi.setLoadFactor(0);
				    	radEsiGroupData.getEsis().add(secondaryEsi);
				    }
				    
				    radEsiGroups.add(radEsiGroupData);
				}
			} finally {
				DBUtility.closeQuietly(connection);
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(resultSet);
			}
		 
	}

	
	@PostRead
	public void postRead() {
		storeInDataStructure();
	}
	
	@PostWrite
	public void postWrite() {
		
	}
	
	public  RadEsiGroupData getRadEsiGroup(String groupId) {
		return groupIdToGroup.get(groupId);
	}
	
	private String getQuery() {
		return "SELECT * FROM TBLMRADIUSESIGROUP";
	}
	
	
	public void storeInDataStructure() {
		for(RadEsiGroupData radEsiGroupData : radEsiGroups){
			groupIdToGroup.put(radEsiGroupData.getId(), radEsiGroupData);
		}
	}
}
