package com.elitecore.aaa.radius.conf.impl;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
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
@XmlRootElement(name = "grace-policies")
@ConfigurationProperties(moduleName ="GRACE-POLICY-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "grace-policy", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth"})
public class GracePolicyConfigurable extends Configurable{

	private List<GracePolicyDetail> gracePolicyList;
	private Map<String, int[]> gracePolicyConfigurationMap;
	
	public GracePolicyConfigurable(){
		
		gracePolicyList = new ArrayList<GracePolicyDetail>();
		gracePolicyConfigurationMap = new HashMap<String, int[]>();
	}
	@XmlTransient
	public Map<String, int[]> getGracePolicyConfigurationMap() {
		return gracePolicyConfigurationMap;
	}

	public void setGracePolicyConfigurationMap(
			Map<String, int[]> gracePolicyConfigurationMap) {
		this.gracePolicyConfigurationMap = gracePolicyConfigurationMap;
	}

	@XmlElement(name = "grace-policy")
	public List<GracePolicyDetail> getGracePolicyList() {
		return gracePolicyList;
	}
	
	public void setGracePolicyList(List<GracePolicyDetail> gracePolicy) {
		this.gracePolicyList = gracePolicy;
	}


	@DBRead
	public void readFromDB() throws Exception {

		Connection conn =null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			String query = "select NAME,VALUE from TBLMGRACEPOLICY";
			preparedStatement = conn.prepareStatement(query);
			if(preparedStatement == null){
				throw new SQLException("Cannot establish connection to database during reading Grace Policy Configuration , Reason : Prepared Statement is null");
			}
			resultSet=preparedStatement.executeQuery();
			List<GracePolicyDetail> gracePolicyList = new ArrayList<GracePolicyDetail>();
			while(resultSet.next()){
				GracePolicyDetail gracePolicy = new GracePolicyDetail();

				String name = resultSet.getString("NAME");
				String value = resultSet.getString("VALUE");
				gracePolicy.setName(name);
				gracePolicy.setValue(value);
				gracePolicyList.add(gracePolicy);
			}	

			this.gracePolicyList = gracePolicyList;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(conn);
		}			
	}


	@PostRead
	public void postReadProcessing() {
		this.gracePolicyConfigurationMap = new HashMap<String, int[]>();
		int size = this.gracePolicyList.size();
		GracePolicyDetail gracePolicy;
		String name;
		String value;
		for(int i=0;i<size;i++){
			gracePolicy = this.gracePolicyList.get(i);
			name = gracePolicy.getName();
			value = gracePolicy.getValue();
			
			int[] gracePeriod;
			if(name!=null && name.length()>0 &&value != null && value.length()>0){
				String strValueArray[] = value.split(",");
				gracePeriod = new int[strValueArray.length];
				for(int j=0;j<strValueArray.length;j++){
					try{
						gracePeriod[j]= Integer.parseInt(strValueArray[j]);
					}catch (NumberFormatException e) {
					}	
				}
				gracePolicyConfigurationMap.put(name, gracePeriod);	
			}
		}
		
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	
	@DBReload
	public void dbReload(){
		
	}
}

@XmlType(propOrder = {})
class GracePolicyDetail{
	
	private String name ;
	private String value;

	public GracePolicyDetail(){
		//required by Jaxb.
	}
	@XmlElement(name = "name",type = String.class)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name = "value",type= String.class)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}