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
import com.elitecore.aaa.rm.drivers.conf.impl.RMDiameterChargingDriverConfImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
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
@XmlRootElement(name = "rm-diameter-charging-drivers")
@ConfigurationProperties(moduleName ="RM-DC-CNFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rm-diameter-charging-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","charging","driver"})
public class RMDiameterChargingDriverConfigurable  extends Configurable {
	
	List<RMDiameterChargingDriverConfImpl> rmDCDriverList;
	
	public RMDiameterChargingDriverConfigurable() {
		this.rmDCDriverList = new ArrayList<RMDiameterChargingDriverConfImpl>();
	}

	@DBRead
	public void readFromDB() throws SQLException {
		Connection connection = null;
		String query = "";

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;
		
		PreparedStatement pstmtRMDiameterConf=null;
		ResultSet rsRMDiameterConf =null;
		
		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			query = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+DriverTypes.RM_DIAMETER_DRIVER.value+"'"; 

			List<RMDiameterChargingDriverConfImpl> tempRMDCDriverConfList = new ArrayList<RMDiameterChargingDriverConfImpl>();
			psForDriverInstanceId = connection.prepareStatement(query);

			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				
				pstmtRMDiameterConf = connection.prepareStatement(getDCConfQuery());
				pstmtRMDiameterConf.setString(1, driverInstanceId);
				pstmtRMDiameterConf.setString(2, driverInstanceId);
				rsRMDiameterConf = pstmtRMDiameterConf.executeQuery();
				RMDiameterChargingDriverConfImpl rmDiameterChargingDriverConfImpl;
				
				if(rsRMDiameterConf.next()){
					
					rmDiameterChargingDriverConfImpl = new RMDiameterChargingDriverConfImpl();
					rmDiameterChargingDriverConfImpl.setDriverInstanceId(driverInstanceId);
							
					rmDiameterChargingDriverConfImpl.setTranslationMappingName(rsRMDiameterConf.getString("TRANSMAPCONFNAME"));
					
					rmDiameterChargingDriverConfImpl.setDriverName(rsRMDiameterConf.getString("DRIVER_NAME"));
					rmDiameterChargingDriverConfImpl.setDisconnectUrl(rsRMDiameterConf.getString("DISCONNECTURL"));
					
					tempRMDCDriverConfList.add(rmDiameterChargingDriverConfImpl);

				}
			}
			this.rmDCDriverList = tempRMDCDriverConfList;
		} finally{
			DBUtility.closeQuietly(rsRMDiameterConf);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(pstmtRMDiameterConf);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(connection);
		}
		
	}

	private String getDCConfQuery() {
		return "select A.*,B.NAME AS TRANSMAPCONFNAME, C.NAME AS DRIVER_NAME from tblmdcdriver A, tblmtranslationmappingconf B, tblmdriverinstance C where a.driverinstanceid = ? AND b.transmapconfid = a.transmapconfid AND C.driverinstanceid=?";
	}

	@PostRead
	public void postReadProcessing() {
		
	}

	@XmlElement(name="rm-diameter-charging-driver")
	public List<RMDiameterChargingDriverConfImpl> getDriverList() {
		return rmDCDriverList;
	}
	
	public void setDriverList(List<RMDiameterChargingDriverConfImpl> rmDCDriverList) {
		this.rmDCDriverList = rmDCDriverList;
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
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+")";

	}

}
