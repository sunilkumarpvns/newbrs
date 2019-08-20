package com.elitecore.aaa.radius.esi.radius.conf.impl;

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
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;


@ConfigurationProperties(moduleName = "CORRELATED-RADIUS-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey = "")
@XMLProperties(name = "correlated-radius", configDirectories = {"conf", "db", "radius"}, schemaDirectories = {"system", "schema", "radius"})
@XmlRootElement(name = "correlated-radius")
public class CorrelatedRadiusConfigurable  extends Configurable {

	private List<CorrelatedRadiusData> correlatedRadiusEsiList = new ArrayList<>();
	private Map<String, CorrelatedRadiusData> nameToCorrelatedRadiusEsi = new HashMap<>();
	
	@XmlElement( name = "correlated-radius-data")
	public List<CorrelatedRadiusData> getCorrelatedRadiusEsiList() {
		return correlatedRadiusEsiList;
	}

	public CorrelatedRadiusData getCorrelatedRadiusUsingName(String correlatedRadiusName) {
		return nameToCorrelatedRadiusEsi.get(correlatedRadiusName);
	}

	@DBRead
	public void readFromDB() throws SQLException  {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement("select * from TBLMCORRELATEDRADIUSESI");
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
				CorrelatedRadiusData correlatedRadiusData = new CorrelatedRadiusData();
				correlatedRadiusData.setId(resultSet.getString("CORRELATEDID"));
				correlatedRadiusData.setName(resultSet.getString("NAME"));
				correlatedRadiusData.setDescription(resultSet.getString("DESCRIPTION"));
				correlatedRadiusData.setAuthEsiId(resultSet.getString("AUTHESIID"));
				correlatedRadiusData.setAuthEsiName(resultSet.getString("AUTHESINAME"));
				correlatedRadiusData.setAcctEsiId(resultSet.getString("ACCTESIID"));
				correlatedRadiusData.setAcctEsiName(resultSet.getString("ACCTESINAME"));
				correlatedRadiusEsiList.add(correlatedRadiusData);
			}
		} finally {
			DBUtility.closeQuietly(connection);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(resultSet);
		}
	}

	@PostRead
	public void postRead() {

		for (CorrelatedRadiusData data: correlatedRadiusEsiList) {
			nameToCorrelatedRadiusEsi.put(data.getName(), data);
		}
	}

	@PostWrite
	public void postWrite() {
		//no-op
	}
	
	@PostReload
	public void postReload() {
		// Do nothing
	}

}
