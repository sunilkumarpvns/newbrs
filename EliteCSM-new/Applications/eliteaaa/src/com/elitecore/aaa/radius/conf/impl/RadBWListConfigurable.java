package com.elitecore.aaa.radius.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.conf.RadBWListConfiguration;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
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

@XmlType(propOrder = {})
@XmlRootElement(name = "rad-bw-confs")
@ConfigurationProperties(moduleName ="RAD_BW_CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rad-bw-conf", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth"})
public class RadBWListConfigurable extends Configurable implements RadBWListConfiguration {

	private static final String MODULE = "RAD-BW-LIST-CONFIGURABLE";
	public static final String BLACK = "B";
	public static final String WHITE = "W";
	
	private boolean isInitialized = false;	
	private List<AttributeDetails> blackList;
	
	public RadBWListConfigurable(){
		this.blackList = new ArrayList<AttributeDetails>();
	}
	
	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		ResultSet bwListRS = null;
		PreparedStatement tblBWListStmt=null;

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String queryForBW = getQueryForBWList();
			tblBWListStmt = connection.prepareStatement(queryForBW);
			bwListRS = tblBWListStmt.executeQuery();
			String attrId = "";
			
			List<AttributeDetails> tempBlackOrWhiteList = new ArrayList<AttributeDetails>();
			
			while(bwListRS.next()){
				AttributeDetails attribute = new AttributeDetails();
				attrId = bwListRS.getString("ATTRID");
				attribute.setId(attrId);
				attribute.setValue(bwListRS.getString("ATTRVALUE"));
				attribute.setValidity(bwListRS.getTimestamp("VALIDITY"));
				attribute.setType(bwListRS.getString("TYPENAME"));
				tempBlackOrWhiteList.add(attribute);
			}
			this.blackList = tempBlackOrWhiteList;
		}finally{
			DBUtility.closeQuietly(bwListRS);
			DBUtility.closeQuietly(tblBWListStmt);
			DBUtility.closeQuietly(connection);
		}
		isInitialized = true;
	}

	@DBReload
	public void reloadBWConfiguration(){
		// Do nothing
	}
	
	private String getQueryForBWList() {
		return "select * from TBLBWLIST where COMMONSTATUSID = '"+CommonConstants.DATABASE_POLICY_STATUS_ACTIVE+"'"+" ORDER BY ORDERNUMBER";
	}
	
	@PostRead
	public void postReadProcessing() {
		isInitialized = true;
		if(blackList!=null){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			Date dateAfterOneDay = calendar.getTime();
			for(AttributeDetails attributeDetails:blackList){
				if(attributeDetails.getValidity()==null){
					attributeDetails.setValidity(dateAfterOneDay);
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Setting validity as date after one day. Reason: validity is not configured properly for attribute: " + attributeDetails.getId());
					}
				}
			}
		}
	}
	
	@PostReload
	public void postReloadProcessing() {
		// Do nothing
	}
	
	@PostWrite
	public void postWriteProcessing(){
		// Do nothing
	}

	@Override
	@XmlTransient
	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	@XmlElementWrapper(name="bw-list")
	@XmlElement(name="attribute")
	public List<AttributeDetails> getAttributeDetailList() {
		return blackList;
	}
	
	public void setAttributeDetailList(List<AttributeDetails> attributeDetailList) {
		this.blackList = attributeDetailList;
	}

}

