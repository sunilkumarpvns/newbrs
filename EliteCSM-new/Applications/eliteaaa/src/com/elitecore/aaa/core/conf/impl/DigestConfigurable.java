package com.elitecore.aaa.core.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

@XmlType(propOrder = {})
@XmlRootElement(name = "digest-confs")
@ConfigurationProperties(moduleName ="DIGEST_CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "digest-conf", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth"})
public class DigestConfigurable extends Configurable {

	private List<DigestConfigurationImpl> digestConfigList = new ArrayList<DigestConfigurationImpl>();
	
	@XmlElement(name = "digest-conf")
	public List<DigestConfigurationImpl> getDigestConfigList() {
		return digestConfigList;
	}
	public void setDigestConfigList(List<DigestConfigurationImpl> digestConfigList) {
		this.digestConfigList = digestConfigList;
	}
	@DBRead
	public void readFromDB() throws Exception {

		Connection conn = null;
		PreparedStatement psForDigestConf = null;
		ResultSet rsForDigestConf = null;
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = "SELECT DIGESTCONFID from TBLMDIGESTCONF WHERE DIGESTCONFID IN ( SELECT DISTINCT DIGESTCONFID FROM TBLMAUTHSERVICEPOLICY)";

			psForDigestConf = conn.prepareStatement(baseQuery);	
			if(psForDigestConf == null){
				throw new SQLException("Prepared Statement is null for reading DigestConfiguraion");
			}
			rsForDigestConf = psForDigestConf.executeQuery();

			List<DigestConfigurationImpl> tempList = new ArrayList<DigestConfigurationImpl>();
			while(rsForDigestConf.next()){

				String digestConfId = rsForDigestConf.getString("DIGESTCONFID");
				String query = "SELECT NAME, DESCRIPTION, REALM, DEFAULTQOP, DEFAULTALGO, OPAQUE, DEFAULTNONCE, DEFAULTNONCELENGTH, DRAFT_AAA_SIPENABLE FROM TBLMDIGESTCONF WHERE DIGESTCONFID =?";

				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1, digestConfId);
				resultSet=preparedStatement.executeQuery();

				DigestConfigurationImpl digestConfigurationImpl = new DigestConfigurationImpl();

				while(resultSet.next()){

					digestConfigurationImpl.setConfId(digestConfId);

					if(resultSet.getString("NAME")!=null && resultSet.getString("NAME").length()>0)
						digestConfigurationImpl.setName(resultSet.getString("NAME"));

					if(resultSet.getString("REALM")!=null && resultSet.getString("REALM").length()>0)
						digestConfigurationImpl.setRealm(resultSet.getString("REALM"));

					if(resultSet.getString("DEFAULTQOP")!=null && resultSet.getString("DEFAULTQOP").length()>0)
						digestConfigurationImpl.setDefaultQOP(resultSet.getString("DEFAULTQOP"));

					if(resultSet.getString("DEFAULTALGO")!=null && resultSet.getString("DEFAULTALGO").length()>0)
						digestConfigurationImpl.setDefaultAlgorithm(resultSet.getString("DEFAULTALGO"));

					if(resultSet.getString("OPAQUE")!=null && resultSet.getString("OPAQUE").length()>0)
						digestConfigurationImpl.setOpaque(resultSet.getString("OPAQUE"));

					if(resultSet.getString("DEFAULTNONCE") != null &&  resultSet.getString("DEFAULTNONCE").length()>0)
						digestConfigurationImpl.setDefaultNonce(resultSet.getString("DEFAULTNONCE"));

					if(resultSet.getString("DEFAULTNONCELENGTH")!=null)
						digestConfigurationImpl.setDefaultNonceLength(resultSet.getInt("DEFAULTNONCELENGTH"));

					if(resultSet.getString("DRAFT_AAA_SIPENABLE")!=null && resultSet.getString("DRAFT_AAA_SIPENABLE").length()>0)
						digestConfigurationImpl.setIsDraftAAASIPEnable(stringToBoolean(resultSet.getString("DRAFT_AAA_SIPENABLE"),digestConfigurationImpl.getIsDraftAAASIPEnable()));

				}		
				tempList.add(digestConfigurationImpl);
			}	
			this.digestConfigList = tempList;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDigestConf);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDigestConf);
			DBUtility.closeQuietly(conn);
		}
	}
	
	@DBReload
	public void reloadDigestConfiguration(){
		// Do nothing
	}
	
	public boolean stringToBoolean(String originalString,boolean defaultValue) {
		boolean resultValue = defaultValue;
		try{
			resultValue = Boolean.parseBoolean(originalString.trim());
		}catch (Exception e) {
		}
		return resultValue;
		
	}

	@PostRead
	public void postReadProcessing() {
		// Do thing
	}
	@PostWrite
	public void postWriteProcessing(){
		// Do thing		
	}

	@PostReload
	public void postReloadProcessing(){
		// Do thing
	}

}
