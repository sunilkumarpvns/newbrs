package com.elitecore.aaa.radius.conf.impl;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.data.Vendor;
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
import com.elitecore.commons.base.DBUtility;

@XmlType(propOrder = {})
@XmlRootElement(name = "vendors")
@ConfigurationProperties(moduleName ="VENDOR-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, reloadWith = DBReader.class, synchronizeKey ="")
@XMLProperties(name = "vendors", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db"})
public class VendorConfigurable extends Configurable{

	private static final String MODULE = "VENDOR-CONFIGURABLE";
	
	/*
	 *	Data structures for storing the vendors 
	 */
	private List<Vendor> vendorList;
	
	private Map<Long,Vendor> vendorsMap;
	/*
	 * 	End of data structures for storing the vendors
	 */
	
	
	public VendorConfigurable(){
		vendorList = new ArrayList<Vendor>();
		vendorsMap = new LinkedHashMap<Long,Vendor>();
	}
	
	@XmlElement(name = "vendor")
	public List<Vendor> getVendorList() {
		return vendorList;
	}

	public void setVendorList(List<Vendor> vendorList) {
		this.vendorList = vendorList;
	}

	@DBRead
	public void readVendorConfiguration() throws Exception {

		Connection  connection = null;		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{			
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getQueryForVendors());
			if(preparedStatement == null){
				LogManager.getLogger().debug(MODULE, "PreparedStatement is null, while fetching vendor details.");			
				throw new SQLException("Problem reading Vendor configurations, reason: prepared statement received is NULL");
			}
			setQueryTimeout(preparedStatement,RadClientConfigurationImpl.DEFAULT_QUERY_TIMEOUT);			
			resultSet = preparedStatement.executeQuery();
			List<Vendor> tempVendors = new ArrayList<Vendor>();
			while(resultSet.next()){
				Vendor vendor = new Vendor();
				vendor.setVendorID(resultSet.getLong("VENDORID"));
				vendor.setVendorName(resultSet.getString("VENDORNAME"));
				tempVendors.add(vendor);
			}	
			vendorList = tempVendors;
		}finally{
			DBUtility.closeQuietly(connection);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}		
	}

	@PostRead
	public void postReadProcessing() {
		for(Vendor vendor : vendorList){
			storeInDataStructures(vendor);
		}
	}
	
	private void storeInDataStructures(Vendor vendor) {
		vendorsMap.put(vendor.getVendorID(), vendor);
	}

	@PostReload
	public void postReloadProcessing() {

	}
	
	private String getQueryForVendors(){
		return "SELECT * FROM tblmvendor";
	}
	
	private void setQueryTimeout(PreparedStatement preparedStatement,int timeout)throws SQLException {
		if(timeout < RadClientConfigurationImpl.DEFAULT_QUERY_TIMEOUT)
			timeout = RadClientConfigurationImpl.DEFAULT_QUERY_TIMEOUT;			
		preparedStatement.setQueryTimeout(timeout);
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@DBReload
	public void reloadVendorConfiguration(){
		
	}
	
	public Vendor getVendorForID(Long vendorID){
		return vendorsMap.get(vendorID);
	}
}
