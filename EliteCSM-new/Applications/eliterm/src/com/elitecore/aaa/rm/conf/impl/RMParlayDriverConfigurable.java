package com.elitecore.aaa.rm.conf.impl;

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
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.rm.drivers.conf.impl.RMParlayDriverConfigurationImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.commons.base.DBUtility;
@XmlType(propOrder = {})
@XmlRootElement(name = "rm-parlay-drivers")
@ConfigurationProperties(moduleName ="RM-PARLAY-DRV-CNFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rm-parlay-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","charging","driver"})
public class RMParlayDriverConfigurable extends Configurable {
	
	private final String MODULE = "RM-PARLAY-DRV-CNFIGURABLE";

	
	private List<RMParlayDriverConfigurationImpl> rmParlayDriverConfList;

	public RMParlayDriverConfigurable(){
		rmParlayDriverConfList = new ArrayList<RMParlayDriverConfigurationImpl>();
	}
	
	@DBRead
	public void readFromDB() throws Exception {
		Connection connection = null;
		String query = "";

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;
		
		PreparedStatement pstmtRMParlayConf=null;
		ResultSet rsRMParlayConf =null;
		
		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			query = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+DriverTypes.RM_PARLAY_DRIVER.value+"'";

			List<RMParlayDriverConfigurationImpl> tempRMParlayDriverConfList = new ArrayList<RMParlayDriverConfigurationImpl>();
			psForDriverInstanceId = connection.prepareStatement(query);

			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				
				pstmtRMParlayConf = connection.prepareStatement(getParlayConfQuery());
				pstmtRMParlayConf.setString(1, driverInstanceId);
				rsRMParlayConf = pstmtRMParlayConf.executeQuery();
				RMParlayDriverConfigurationImpl rmParlayDriverConfigurationImpl;
				if(rsRMParlayConf.next()){
					rmParlayDriverConfigurationImpl = new RMParlayDriverConfigurationImpl();
					rmParlayDriverConfigurationImpl.setDriverInstanceId(driverInstanceId);
					rmParlayDriverConfigurationImpl.setUserName(rsRMParlayConf.getString("username"));
					rmParlayDriverConfigurationImpl.setSessionManagerServiceName(rsRMParlayConf.getString("smservicename"));
					rmParlayDriverConfigurationImpl.setWebServiceAddress(rsRMParlayConf.getString("wsaddress"));
					rmParlayDriverConfigurationImpl.setParlayServiceName(rsRMParlayConf.getString("parleyservicename"));
					rmParlayDriverConfigurationImpl.setPassword(rsRMParlayConf.getString("password"));
					rmParlayDriverConfigurationImpl.setTranslationMappingName(rsRMParlayConf.getString("TRANSMAPCONFNAME"));
					tempRMParlayDriverConfList.add(rmParlayDriverConfigurationImpl);
				}
			}
			this.rmParlayDriverConfList = tempRMParlayDriverConfList;
		} finally{
			DBUtility.closeQuietly(rsRMParlayConf);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(pstmtRMParlayConf);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(connection);
		}

		
		
	}

	@PostRead
	public void postReadProcessing() {
		// TODO Auto-generated method stub
		
	}

	@XmlElement(name ="rm-parlay-driver")
	public List<RMParlayDriverConfigurationImpl> getDriverList() {
		return this.rmParlayDriverConfList;
	}
	
	public void setDriverList(List<RMParlayDriverConfigurationImpl> rmParlayDriverConfList) {
		this.rmParlayDriverConfList = rmParlayDriverConfList;
	}
	
	private String getParlayConfQuery(){
		return "select A.*,B.NAME TRANSMAPCONFNAME from tblmparleychargingdriver A, tblmtranslationmappingconf B where a.driverinstanceid = ? AND b.transmapconfid = a.transmapconfid"; 
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	@DBReload
	public void reloadFromDB() throws SQLException {
		
	}

	private String getReadQueryForChargingServicePolicyConfiguration() {
		
		RMChargingServiceConfigurable rmChargingServiceConfigurable = getConfigurationContext().get(RMChargingServiceConfigurable.class);
		if(rmChargingServiceConfigurable == null) {
			return "''";
}
		List<String> servicePolicies = rmChargingServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select policyid from tblmcgpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select policyid from tblmcgpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+ConfigurationUtil.getStrFromStringArrayList(servicePolicies)+")";

	}

}
