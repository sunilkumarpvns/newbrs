package com.elitecore.aaa.radius.conf.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
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
/**
 *  <p> Configuration of this group is used for proxy communication
 *   in RADIUS. It supports N - M or Active-Passive redundancy mode 
 *   and communication can be stateful or stateless as per configuration. 
 *  <p>  
 * @author soniya.patel
 *
 */
@ConfigurationProperties(moduleName = "RADIUS-ESI-GRP-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey = "")
@XMLProperties(name = "radius-esi-groups", configDirectories = {"conf", "db", "radius"}, schemaDirectories = {"system", "schema", "radius"})
@XmlRootElement(name = "radius-esi-groups")
public class RadiusESIGroupConfigurable extends Configurable {

	private static final String MODULE = "RADIUS-ESI-GRP-CONFIGURABLE";

	private List<RadiusEsiGroupData> radiusEsiGroups = new ArrayList<>();
	private Map<String, RadiusEsiGroupData> groupNameToGroup = new HashMap<>();

	@XmlElement(name = "radius-esi-group")
	public List<RadiusEsiGroupData> getEsiGroups() {
		return radiusEsiGroups;
	}

	public @Nullable RadiusEsiGroupData getESIGroupByName(String esiGroupId) {
		return groupNameToGroup.get(esiGroupId);
	}

	@DBRead
	public void readFromDB() throws SQLException, JAXBException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		JAXBContext context = JAXBContext.newInstance(RadiusEsiGroupData.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement("SELECT * FROM TBLMRADIUSESIPROXYGRP");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				byte[] radiusEsiGroupXml = resultSet.getBytes("GRPXML");
				InputStream inputStream = new ByteArrayInputStream(radiusEsiGroupXml);
				RadiusEsiGroupData groupData = (RadiusEsiGroupData) unMarshaller.unmarshal(inputStream);
				groupData.setId(resultSet.getString("ID"));
				groupData.setName(resultSet.getString("NAME"));
				groupData.setDescription(resultSet.getString("DESCRIPTION"));
				
				radiusEsiGroups.add(groupData);
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	@PostRead
	public void postRead() {
		for (RadiusEsiGroupData esiGroupData : radiusEsiGroups) {
			groupNameToGroup.put(esiGroupData.getName(),esiGroupData);
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

}
