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
import com.elitecore.aaa.radius.data.RadiusClientProfile;
import com.elitecore.commons.base.Optional;
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
@XmlRootElement(name = "rad-client-profiles")
@ConfigurationProperties(moduleName ="RAD-CLIENT-PROFILE-CNFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, reloadWith = DBReader.class, synchronizeKey ="")
@XMLProperties(name = "rad-clients-profiles", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db"})
public class ClientProfileConfigurable extends Configurable{
	
	/* 
	 * Start of DB Field constants
	 */
	private static final String	PROFILEID = "PROFILEID";
	private static final String	PROFILENAME="PROFILENAME";
	private static final String	CLIENTPOLICY="CLIENTPOLICY";
	private static final String	DHCPADDRESS="DHCPADDRESS";
	private static final String	DNSLIST="DNSLIST";
	private static final String	HAADDRESS="HAADDRESS";
	private static final String	HOTLINEPOLICY="HOTLINEPOLICY";
	private static final String	FILTERUNSUPPORTEDVSA="FILTERUNSUPPORTEDVSA";
	private static final String	MULTIPLECLASSATTRIBUTE="MULTIPLECLASSATTRIBUTE";
	private static final String	PREPAIDSTANDARD="PREPAIDSTANDARD";
	private static final String	USERIDENTITIES="USERIDENTITIES";
	private static final String	VENDORNAME = "VENDORNAME";
	private static final String	TYPEID = "TYPEID";
	private static final String	VENDORID = "VENDORID";
	private static final String NO = "no";
	private static final String DYNAAUTHPORT = "DYNAAUTHPORT";
	private static final String COASUPPORTEDATTRIBUTES = "COASUPPORTEDATTRIBUTES";
	private static final String COAUNSUPPORTEDATTRIBUTES = "COAUNSUPPORTEDATTRIBUTES";
	private static final String DMSUPPORTEDATTRIBUTES = "DMSUPPORTEDATTRIBUTES";
	private static final String DMUNSUPPORTEDATTRIBUTES = "DMUNSUPPORTEDATTRIBUTES";
	
	/*
	 * End of DB Field constants
	 */

	private static final String MODULE = "RAD-CLIENT-PROFILE-CNFIGURABLE";

	/*
	 * Data structures for storing the client profiles
	 */
	private List<RadiusClientProfile> radiusClientProfileList;
	
	private Map<String,RadiusClientProfile> clientProfilesNameMap = null;
	
	private Map<String,RadiusClientProfile> clientProfilesIDMap = null;
	/*
	 * End of data structures
	 */
	
	
	public ClientProfileConfigurable(){
		radiusClientProfileList = new ArrayList<RadiusClientProfile>();
		clientProfilesNameMap = new LinkedHashMap<String,RadiusClientProfile>();
		clientProfilesIDMap = new LinkedHashMap<String, RadiusClientProfile>();
	}
	
	@XmlElement(name = "radius-client-profile")
	public List<RadiusClientProfile> getRadiusClientProfileList() {
		return radiusClientProfileList;
	}

	public void setRadiusClientProfileList(List<RadiusClientProfile> radiusClientProfileList) {
		this.radiusClientProfileList = radiusClientProfileList;
	}

	
	@DBRead
	@DBReload
	public void readClientProfileConfiguration() throws Exception {

		Connection  connection = null;		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		
		try{						
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getQueryForProfiles());
			if(preparedStatement == null){
				LogManager.getLogger().debug(MODULE, "PreparedStatement is null,while fetching ClientProfile details.");			
				throw new SQLException("Prepared Statement is null,while fetching ClientProfile details.");
			}		
			setQueryTimeout(preparedStatement,RadClientConfigurationImpl.DEFAULT_QUERY_TIMEOUT);			
			resultSet = preparedStatement.executeQuery();			
			RadiusClientProfile radiusClientProfile = null;
			List<RadiusClientProfile> tempRadiusClientProfile = new ArrayList<RadiusClientProfile>();
			
			while(resultSet.next()){
				radiusClientProfile = readClientProfile(connection, resultSet);
				tempRadiusClientProfile.add(radiusClientProfile);
			}
			radiusClientProfileList = tempRadiusClientProfile;
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}		
	}
	
	private RadiusClientProfile readClientProfile(Connection conn, ResultSet resultSet) throws SQLException{
		RadiusClientProfile radiusClientProfile = new RadiusClientProfile(resultSet.getString(PROFILEID));
		radiusClientProfile.setProfileName(resultSet.getString(PROFILENAME));
		radiusClientProfile.setClientPolicy(resultSet.getString(CLIENTPOLICY));
		radiusClientProfile.setDHCPAddress(resultSet.getString(DHCPADDRESS));
		radiusClientProfile.setDnsAddresses(resultSet.getString(DNSLIST));
		radiusClientProfile.setHAAddress(resultSet.getString(HAADDRESS));
		radiusClientProfile.setHotlinePolicy(resultSet.getString(HOTLINEPOLICY));
		radiusClientProfile.setIsFilterUnsupportedVSA(Boolean.parseBoolean(resultSet.getString(FILTERUNSUPPORTEDVSA)));
		String multipleClassAttrStr = resultSet.getString(MULTIPLECLASSATTRIBUTE);
		if(NO.equalsIgnoreCase(multipleClassAttrStr)){
						radiusClientProfile.setIsMultipleClassAttributeSupported(false);
		} else {
			radiusClientProfile.setIsMultipleClassAttributeSupported(true);
				}
		radiusClientProfile.setPrepaidStandard(resultSet.getString(PREPAIDSTANDARD));				
		radiusClientProfile.setUserIdentities(resultSet.getString(USERIDENTITIES));

		radiusClientProfile.setVendorName(resultSet.getString(VENDORNAME));
		radiusClientProfile.setVendorType(resultSet.getInt(TYPEID));  //This is VendorType
		radiusClientProfile.setVendorId(resultSet.getLong(VENDORID));
		
		int dynauthPort = resultSet.getInt(DYNAAUTHPORT);
		Optional<Integer> optionalDynauthPort = 
			dynauthPort == 0 ? Optional.<Integer>absent() : Optional.<Integer>of(dynauthPort);
			
		radiusClientProfile.setDynauthPort(optionalDynauthPort);
		
		radiusClientProfile.setSupportedAttributeStrCOA(resultSet.getString(COASUPPORTEDATTRIBUTES));
		radiusClientProfile.setUnsupportedAttributeStrCOA(resultSet.getString(COAUNSUPPORTEDATTRIBUTES));
		radiusClientProfile.setSupportedAttributeStrDM(resultSet.getString(DMSUPPORTEDATTRIBUTES));
		radiusClientProfile.setUnsupportedAttributeStrDM(resultSet.getString(DMUNSUPPORTEDATTRIBUTES));

		//Reading all the supported vendors list
		readSupportedVendorsList(radiusClientProfile, conn);

		return radiusClientProfile;
	}

	private void readSupportedVendorsList(RadiusClientProfile radiusClientProfile, Connection conn) throws SQLException {
		PreparedStatement psForVendorList = null;
		ResultSet rsForVendorList = null;

		try{
				// fetch all supported vendors list 
			psForVendorList = conn.prepareStatement(getQueryForVendorList());
			if(psForVendorList == null){
					LogManager.getLogger().debug(MODULE, "PreparedStatement is null.");			
					throw new SQLException("Prepared Statement is null");
				}
			psForVendorList.setString(1,radiusClientProfile.getProfileID());
				setQueryTimeout(psForVendorList,RadClientConfigurationImpl.DEFAULT_QUERY_TIMEOUT);			
				rsForVendorList = psForVendorList.executeQuery();
				List<Vendor> vendorList = new ArrayList<Vendor>();				
				while(rsForVendorList.next()){	
					Vendor vendor = new Vendor();
				vendor.setVendorID(rsForVendorList.getLong(VENDORID));
				vendor.setVendorName(rsForVendorList.getString(VENDORNAME));
					vendorList.add(vendor);
				}	
				radiusClientProfile.setSupportedVendorsList(vendorList);
		}finally{
			DBUtility.closeQuietly(rsForVendorList);
			DBUtility.closeQuietly(psForVendorList);
		}		
	}

	@PostRead
	@PostReload
	public void postReadProcessing() {
		for(RadiusClientProfile radiusClientProfile : radiusClientProfileList){
			storeInDataStructures(radiusClientProfile);
		}
	}
	
	private void storeInDataStructures(RadiusClientProfile radiusClientProfile) {
		clientProfilesNameMap.put(radiusClientProfile.getProfileName(), radiusClientProfile);
		clientProfilesIDMap.put(radiusClientProfile.getProfileID(), radiusClientProfile);
	}

	@PostWrite
	public void postWriteProcessing() {
		//Nothing to do here
	}
	

	public RadiusClientProfile getRadiusClientProfileForName(String profileName){
		return clientProfilesNameMap.get(profileName);
	}
	
	private String getQueryForProfiles(){
		return "SELECT * FROM tblmradiusclientprofile A, tblmvendor B where B.vendorinstanceid = A.vendorinstanceid";
	}
	
	private String getQueryForVendorList(){
		return "SELECT * FROM tblmvendor WHERE vendorinstanceid " +
		"IN( SELECT VENDORINSTANCEID FROM tblmprofilesuppvendorrel WHERE PROFILEID=? )";
	}
	
	private void setQueryTimeout(PreparedStatement preparedStatement,int timeout)throws SQLException {
		if(timeout < RadClientConfigurationImpl.DEFAULT_QUERY_TIMEOUT)
			timeout = RadClientConfigurationImpl.DEFAULT_QUERY_TIMEOUT;			
		preparedStatement.setQueryTimeout(timeout);
	}

	int countRadiusClientProfiles(){
		return clientProfilesIDMap.size();
	}
}
