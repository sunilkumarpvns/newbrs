package com.elitecore.aaa.core.conf.impl;

import java.io.ByteArrayInputStream;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
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

/**
 * This is a configurable class for IMDG using Hazelcast 
 * 
 * @author malav
 *
 */
@XmlType(propOrder = {})
@XmlRootElement(name = "imdgs")
@ConfigurationProperties(moduleName = "IMDG-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "imdg", schemaDirectories = {"system", "schema"}, configDirectories = {"conf", "db"})
public class IMDGConfigurable extends Configurable {
	
	private static final String QUERY = "select * from TBLMINMEMORYDATAGRID";
	
	private ImdgConfigData imdgConfigData;
	
	public IMDGConfigurable() {
		imdgConfigData = new ImdgConfigData();
	}
	
	@DBRead
	@DBReload
	public void readConfiguration() throws JAXBException, SQLException {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		
		JAXBContext context = JAXBContext.newInstance(ImdgConfigData.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			prepareStatement = connection.prepareStatement(QUERY);
			resultSet = prepareStatement.executeQuery();
			if(resultSet.next()) {
				byte[] xmlbytes = resultSet.getBytes("IMDGXML");
				ByteArrayInputStream xmlDataAsAStream = new ByteArrayInputStream(xmlbytes);
				this.imdgConfigData = (ImdgConfigData) unmarshaller.unmarshal(xmlDataAsAStream);
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	@PostRead
	@PostReload
	public void postRead() throws UnknownHostException {
		imdgConfigData.postRead();
	}
	
	@PostWrite
	public void postWrite() {
		//comment is for sonar
	}

	@XmlElement(name="imdg", type=ImdgConfigData.class)
	public ImdgConfigData getImdgConfigData() {
		return imdgConfigData;
	}
	
	public void setImdgConfigData(ImdgConfigData imdgConfiguration) {
		this.imdgConfigData = imdgConfiguration;
	}
	
}