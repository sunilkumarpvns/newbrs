package com.elitecore.netvertexsm.datamanager.servermgr.drivers.data;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;

public class DriverInstanceData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long driverInstanceId;	
	private String name;
	private String description;
	private String status;
	private Long createdByStaffId;
	private Long lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	private Long driverTypeId;
	
	private DriverTypeData driverTypeData;
	
	private ArrayList<DBFieldMapData> dbFieldMapList;
	private ArrayList<LDAPFieldMapData> ldapFieldMapList;
	
	
//  For the purpose of displaying name when needed.....
	private String driverTypeName;

	private LDAPSPInterfaceData ldapSPInterfaceDriverData;
	private DatabaseSPInterfaceData databaseSpInterfaceDriverData;
	private LDAPSPInterfaceData ldapDriverData;
	private DatabaseSPInterfaceData databaseDriverData;
	private DBFieldMapData fieldMapData;
	private LDAPFieldMapData ldapFieldMapData;
	
	
	
	private Set<DatabaseSPInterfaceData> databaseSPInterfaceDriverSet;
	private Set<LDAPSPInterfaceData> ldapspInterfaceDriverSet;
	
	private Set<CSVDriverData> csvDriverDataSet;
	private Set<DBCDRDriverData> dbcdrDriverDataSet;
	
	public Set<DBCDRDriverData> getDbcdrDriverDataSet() {
		return dbcdrDriverDataSet;
	}
	public void setDbcdrDriverDataSet(Set<DBCDRDriverData> dbcdrDriverDataSet) {
		this.dbcdrDriverDataSet = dbcdrDriverDataSet;
	}
	public Set<DatabaseSPInterfaceData> getDatabaseSPInterfaceDriverSet() {
		return databaseSPInterfaceDriverSet;
	}
	public void setDatabaseSPInterfaceDriverSet(
			Set<DatabaseSPInterfaceData> databaseSPRDriverSet) {
		this.databaseSPInterfaceDriverSet = databaseSPRDriverSet;
	}
	public Set<LDAPSPInterfaceData> getLdapspInterfaceDriverSet() {
		return ldapspInterfaceDriverSet;
	}
	public void setLdapspInterfaceDriverSet(Set<LDAPSPInterfaceData> ldapspInterfaceDriverSet) {
		this.ldapspInterfaceDriverSet = ldapspInterfaceDriverSet;
	}
	public ArrayList<LDAPFieldMapData> getLdapFieldMapList() {
		return ldapFieldMapList;
	}
	public void setLdapFieldMapList(ArrayList<LDAPFieldMapData> ldapFieldMapList) {
		this.ldapFieldMapList = ldapFieldMapList;
	}
	public ArrayList<DBFieldMapData> getDbFieldMapList() {
		return dbFieldMapList;
	}
	public void setDbFieldMapList(ArrayList<DBFieldMapData> dbFieldMapList) {
		this.dbFieldMapList = dbFieldMapList;
	}
	public LDAPSPInterfaceData getLdapSPInterfaceDriverData() {
		return ldapSPInterfaceDriverData;
	}
	public void setLdapSPInterfaceDriverData(LDAPSPInterfaceData ldapSPRDriverData) {
		this.ldapSPInterfaceDriverData = ldapSPRDriverData;
	}
	public LDAPSPInterfaceData getLdapDriverData() {
		return ldapDriverData;
	}
	public void setLdapDriverData(LDAPSPInterfaceData ldapDriverData) {
		this.ldapDriverData = ldapDriverData;
	}
	public DatabaseSPInterfaceData getDatabaseDriverData() {
		return databaseDriverData;
	}
	public void setDatabaseDriverData(DatabaseSPInterfaceData databaseDriverData) {
		this.databaseDriverData = databaseDriverData;
	}
	public DatabaseSPInterfaceData getDatabaseSpInterfaceDriverData() {
		return databaseSpInterfaceDriverData;
	}
	public void setDatabaseSpInterfaceDriverData(DatabaseSPInterfaceData databaseSpInterfaceDriverData) {
		this.databaseSpInterfaceDriverData = databaseSpInterfaceDriverData;
	}
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Long getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(Long lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}	
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public Long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(Long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
	public Timestamp getLastModifiedDate() {
		return this.lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getDriverTypeName() {
		return driverTypeName;
	}
	public void setDriverTypeName(String driverTypeName) {
		this.driverTypeName = driverTypeName;
	}
	public DriverTypeData getDriverTypeData() {
		return driverTypeData;
	}
	public void setDriverTypeData(DriverTypeData driverTypeData) {
		this.driverTypeData = driverTypeData;
	}
	public DBFieldMapData getFieldMapData() {
		return fieldMapData;
	}
	public void setFieldMapData(DBFieldMapData fieldMapData) {
		this.fieldMapData = fieldMapData;
	}
	public LDAPFieldMapData getLDAPFieldMapData() {
		return ldapFieldMapData;
	}
	public void setLDAPFieldMapData(LDAPFieldMapData ldapFieldMapData) {
		this.ldapFieldMapData = ldapFieldMapData;
	}
	public Set<CSVDriverData> getCsvDriverDataSet() {
		return csvDriverDataSet;
	}
	public void setCsvDriverDataSet(Set<CSVDriverData> csvDriverDataSet) {
		this.csvDriverDataSet = csvDriverDataSet;
	}
	
	public DatabaseSPInterfaceData getDBSpInterface(){
		if(Collectionz.isNullOrEmpty(databaseSPInterfaceDriverSet)==false){
			return databaseSPInterfaceDriverSet.iterator().next();
		}
		return null;
	}
	
	public LDAPSPInterfaceData getLDAPSPInterface(){
		if(Collectionz.isNullOrEmpty(ldapspInterfaceDriverSet)==false){
			return ldapspInterfaceDriverSet.iterator().next();
		}
		return null;
	}
}

