package com.elitecore.elitesm.web.driver;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
@XmlRootElement
public class CreateDriverConfig {
	
	private DBAuthDriverData dbAuthDriverData;	
	private DBAuthFieldMapData dbAuthFeildMapData;
	private DriverInstanceData driverInstanceData;
	private LDAPAuthDriverData ldapAuthDriverData;
	private LDAPAuthFieldMapData ldapFeildMapData;
	private UserFileAuthDriverData userFileAuthDriverData;
	private HttpAuthDriverData httpAuthDriverData;
	private HssAuthDriverData hssAuthDriverData;
	@XmlTransient
	public HttpAuthDriverData getHttpAuthDriverData() {
		return httpAuthDriverData;
	}
	public void setHttpAuthDriverData(HttpAuthDriverData httpAuthDriverData) {
		this.httpAuthDriverData = httpAuthDriverData;
	}
	@XmlTransient
	public DBAuthDriverData getDbAuthDriverData() {
		return dbAuthDriverData;
	}
	public void setDbAuthDriverData(DBAuthDriverData dbAuthDriverData) {
		this.dbAuthDriverData = dbAuthDriverData;
	}	
	@XmlTransient
	public DBAuthFieldMapData getDbAuthFeildMapData() {
		return dbAuthFeildMapData;
	}
	public void setDbAuthFeildMapData(DBAuthFieldMapData dbAuthFeildMapData) {
		this.dbAuthFeildMapData = dbAuthFeildMapData;
	}
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}
	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
	}
	@XmlTransient
	public LDAPAuthDriverData getLdapAuthDriverData() {
		return ldapAuthDriverData;
	}
	public void setLdapAuthDriverData(LDAPAuthDriverData ldapAuthDriverData) {
		this.ldapAuthDriverData = ldapAuthDriverData;
	}
	@XmlTransient
	public LDAPAuthFieldMapData getLdapFeildMapData() {
		return ldapFeildMapData;
	}
	public void setLdapFeildMapData(LDAPAuthFieldMapData ldapFeildMapData) {
		this.ldapFeildMapData = ldapFeildMapData;
	}
	@XmlElement(name = "user-file-driver-data")
	public UserFileAuthDriverData getUserFileAuthDriverData() {
		return userFileAuthDriverData;
	}
	public void setUserFileAuthDriverData(
			UserFileAuthDriverData userFileAuthDriverData) {
		this.userFileAuthDriverData = userFileAuthDriverData;
	}
	@XmlTransient
	public HssAuthDriverData getHssAuthDriverData() {
		return hssAuthDriverData;
	}
	public void setHssAuthDriverData(HssAuthDriverData hssAuthDriverData) {
		this.hssAuthDriverData = hssAuthDriverData;
	}
}
