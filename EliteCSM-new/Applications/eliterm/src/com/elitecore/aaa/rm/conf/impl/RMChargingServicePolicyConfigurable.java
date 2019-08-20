package com.elitecore.aaa.rm.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.rm.policies.conf.impl.RMChargingPolicyConfigurationImpl;
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
@XmlRootElement(name = "charging-service-policies")
@ConfigurationProperties(moduleName ="RM_CHARGING_POLICY_CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "charging-service-policies", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","charging","servicepolicy"})
public class RMChargingServicePolicyConfigurable extends Configurable{
	
	private List<RMChargingPolicyConfigurationImpl> chargingPolicyList;
	private Map<String, RMChargingPolicyConfigurationImpl> chargingPolicyMap;
	
	public RMChargingServicePolicyConfigurable() {
		this.chargingPolicyList = new ArrayList<RMChargingPolicyConfigurationImpl>();
		this.chargingPolicyMap = new LinkedHashMap<String, RMChargingPolicyConfigurationImpl>();
	}
	
	@DBRead
	public void readFromDB() throws Exception {

		Connection conn = null;
		PreparedStatement psForPolicyId =null;
		ResultSet rsForPolicyId = null;
		
		try {
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			String query = getQueryForServicePolicyConfig();
			psForPolicyId = conn.prepareStatement(query);
			if(psForPolicyId == null){
				throw new SQLException("Prepared statement is null");

			}
			psForPolicyId.setString(1,CommonConstants.DATABASE_POLICY_STATUS_ACTIVE );			
			rsForPolicyId=psForPolicyId.executeQuery();
			
			RMChargingPolicyConfigurationImpl rmChargingPolicyConfImpl;
			
			List<RMChargingPolicyConfigurationImpl> tempCGPolicyConfList = new ArrayList<RMChargingPolicyConfigurationImpl>();
						
			while(rsForPolicyId.next()){
				rmChargingPolicyConfImpl = new RMChargingPolicyConfigurationImpl();
				
				String policyId = rsForPolicyId.getString("policyid");
				
				rmChargingPolicyConfImpl.setPolicyId(policyId);
				rmChargingPolicyConfImpl.setPolicyName(rsForPolicyId.getString("name"));
				rmChargingPolicyConfImpl.setRuleSet(rsForPolicyId.getString("ruleset"));
				rmChargingPolicyConfImpl.setDriverScript(rsForPolicyId.getString("driverscript"));
				
				readDrivers(conn, rmChargingPolicyConfImpl);
				
				tempCGPolicyConfList.add(rmChargingPolicyConfImpl);
			}
			this.chargingPolicyList = tempCGPolicyConfList;
		} finally {
			DBUtility.closeQuietly(rsForPolicyId);
			DBUtility.closeQuietly(psForPolicyId);
			DBUtility.closeQuietly(conn);
		}		

	}

	private void readDrivers(Connection conn, RMChargingPolicyConfigurationImpl rmChargingPolicyConfImpl)
			throws SQLException {
		
		String query= "SELECT DRIVERINSTANCEID ,WEIGHTAGE FROM tblmcgpolicydriverrel WHERE policyid=?";
		
		PreparedStatement psDriverDetails = null;
		ResultSet rsDriverDetails = null;
		
		try {
			
			psDriverDetails = conn.prepareStatement(query);
			psDriverDetails.setString(1, rmChargingPolicyConfImpl.getPolicyId());
			rsDriverDetails = psDriverDetails.executeQuery();
			List<PrimaryDriverDetail> driverList = new ArrayList<PrimaryDriverDetail>();
			while (rsDriverDetails.next()) {		
				PrimaryDriverDetail driverDetail = new PrimaryDriverDetail();
				
				driverDetail.setDriverInstanceId(rsDriverDetails.getString("driverinstanceid")) ;
				
				driverDetail.setWeightage(rsDriverDetails.getInt("weightage")) ;
				
				driverList.add(driverDetail);
				
				rmChargingPolicyConfImpl.setDriverList(driverList);
			}
		} finally {
			DBUtility.closeQuietly(psDriverDetails);
			DBUtility.closeQuietly(rsDriverDetails);
		}
	}
	
	@DBReload
	public void reloadRMChargingServicePolicyConfiguration() throws Exception{

		int size = this.chargingPolicyList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select * from tblmcgpolicy where policyid IN (");

		for(int i = 0; i < size-1; i++){
			RMChargingPolicyConfigurationImpl rmChargingPolicyConfigurationImpl = chargingPolicyList.get(i);
			queryBuilder.append("'" + rmChargingPolicyConfigurationImpl.getPolicyId() + "',");
		}
		queryBuilder.append("'" + chargingPolicyList.get(size - 1).getPolicyId() + "')");
		String queryForReload = queryBuilder.toString();

		Connection conn = null;
		PreparedStatement psForPolicyId =null;
		ResultSet rsForPolicyId = null;
		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			psForPolicyId = conn.prepareStatement(queryForReload);
			rsForPolicyId = psForPolicyId.executeQuery();

			while(rsForPolicyId.next()){
				RMChargingPolicyConfigurationImpl rmChargingPolicyConfImpl = this.chargingPolicyMap.get(rsForPolicyId.getString("policyid"));

				rmChargingPolicyConfImpl.setRuleSet(rsForPolicyId.getString("ruleset"));
			}
		} finally{
			DBUtility.closeQuietly(rsForPolicyId);
			DBUtility.closeQuietly(psForPolicyId);
			DBUtility.closeQuietly(conn);
		}
	}

	@XmlElement(name="charging-service-policy")
	public List<RMChargingPolicyConfigurationImpl> getChargingPolicyList() {
		return chargingPolicyList;
	}

	public void setChargingPolicyList(
			List<RMChargingPolicyConfigurationImpl> chargingPolicyList) {
		this.chargingPolicyList = chargingPolicyList;
	}


	@PostRead
	public void postReadProcessing() {
		if(this.chargingPolicyList!=null){
			RMChargingPolicyConfigurationImpl policyConf;
			int numOfPolicy = this.chargingPolicyList.size();
			Map<String, RMChargingPolicyConfigurationImpl> chargingPolicies = new LinkedHashMap<String, RMChargingPolicyConfigurationImpl>();
			
			for(int i=0;i<numOfPolicy;i++){

				policyConf = chargingPolicyList.get(i);
				chargingPolicies.put(policyConf.getPolicyId(), policyConf);
				
				Map<String, Integer> driverMap = new HashMap<String, Integer>();
				List<PrimaryDriverDetail> driverList = policyConf.getDriverList();
				if(driverList!=null){
					int numOfDriver  = driverList.size();
					
					for(int j=0;j<numOfDriver;j++){
						PrimaryDriverDetail primaryDriverDetail = driverList.get(j);
						driverMap.put(primaryDriverDetail.getDriverInstanceId(), primaryDriverDetail.getWeightage());
					}
				}
				policyConf.setDriverInstanceIdsMap(driverMap);
			}
			this.chargingPolicyMap = chargingPolicies;
		}
					
	}
	@PostWrite
	public void postWriteProcessing(){
		// Do nothing
	}
	
	@PostReload
	public void postReloadProcessing(){
		// Do nothing
	}
	private String getQueryForServicePolicyConfig() {
		RMChargingServiceConfigurable chargingServiceConfigurable = getConfigurationContext().get(RMChargingServiceConfigurable.class);
		List<String> servicePolicies = chargingServiceConfigurable.getServicePolicies();
		if(servicePolicies==null || servicePolicies.isEmpty() || servicePolicies.contains(AAAServerConstants.ALL))
			return "select * from tblmcgpolicy where STATUS=? ORDER BY ordernumber";
		else{
			StringBuilder queryBldr =  new StringBuilder("select * from tblmcgpolicy where STATUS=? AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ORDER BY ( CASE ");
			
			int numOfPolicy = servicePolicies.size();
			for(int i=0;i<numOfPolicy;i++){
				queryBldr.append(" WHEN NAME = '"+servicePolicies.get(i)+"' THEN "+i);
			}
			queryBldr.append(" END )");
			
			return queryBldr.toString();
	}
	}
	
	@XmlTransient
	public Map<String, RMChargingPolicyConfigurationImpl> getPolicyConfigrationMap() {
		return this.chargingPolicyMap;		
	}

	public RMChargingPolicyConfigurationImpl getPolicyConfigration(String policyId) {
		return chargingPolicyMap.get(policyId);		
	}
}