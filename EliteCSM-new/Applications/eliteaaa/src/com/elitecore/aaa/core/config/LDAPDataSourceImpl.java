package com.elitecore.aaa.core.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;

public class LDAPDataSourceImpl implements LDAPDataSource{

	private static final String DEFAULT_USERNAME_PASSWORD = "admin"; //NOSONAR - Reason: Credentials should not be hard-coded
	
	private String dsId;                                                                                                                                                                                    
	private String dsName;                                                                                                                                                                                 
	private String ipAddress;                                                                                                                                                                                 
	private int port;
	private String strIpAdderess;
	private int timeout = 1000;
	private int version = 2;
	private long ldapSizeLimit = 1;
	private String administrator = DEFAULT_USERNAME_PASSWORD;                                                                                                                                                                                 
	private Password password;                                                                                                                                                                                 
	private String userPrefix = "uid=";                                                                                                                                                                                 
	private int maxPoolSize = 5;                                                                                                                                                                                    
	private int minPoolSize = 2;                                                                                                                                                                                    
	private ArrayList<String> searchBaseDnList = new ArrayList<String>();


	public LDAPDataSourceImpl(){
		//required by Jaxb.
		this.password = new Password(DEFAULT_USERNAME_PASSWORD);
	}
	
	public LDAPDataSourceImpl(String dsId, String dsName, int version, String ipAddress, int timeout, long ldapSizeLimit, String administrator, String password, String userPrefix, int maxPoolSize, int minPoolSize,ArrayList<String> searchBaseDnList) {		
		this.dsId = dsId;
		this.dsName = dsName;
		this.version = version;
		this.ipAddress = ipAddress;
		this.timeout = timeout;
		this.ldapSizeLimit = ldapSizeLimit;
		this.administrator = administrator;
		this.password = new Password(password);
		this.userPrefix = userPrefix;
		this.maxPoolSize = maxPoolSize;
		this.minPoolSize = minPoolSize;
		this.searchBaseDnList = searchBaseDnList;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public void setStrIpAddress(String strIpAdderess){
		this.strIpAdderess =strIpAdderess;
	}
	@XmlTransient
	public String getStrIpAddress(){
		return strIpAdderess;
	}
	public void setDataSourceId(String dsId) {
		this.dsId = dsId;
	}
	public void setDataSourceName(String dsName) {
		this.dsName = dsName;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public void setLdapSizeLimit(long ldapSizeLimit) {
		this.ldapSizeLimit = ldapSizeLimit;
	}
	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}
	public void setPassword(String password) {
		this.password.setPassword(password);
	}
	public void setUserPrefix(String userPrefix) {
		this.userPrefix = userPrefix;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public void setSearchBaseDnList(ArrayList<String> searchBaseDnList) {
		this.searchBaseDnList = searchBaseDnList;
	}

	@XmlElement(name = "administrator",type = String.class,defaultValue = DEFAULT_USERNAME_PASSWORD)
	public String getAdministrator() {
		return administrator;
	}

	@XmlElement(name = "version",type = int.class, defaultValue = "2")
	public int getVersion() {
		return version;
	}

	@XmlElement(name = "id",type = String.class)
	public String getDataSourceId() {
		return dsId;
	}

	@XmlElement(name = "datasource",type = String.class)
	public String getDataSourceName() {
		return dsName;
	}

	@XmlElement(name = "ip-address",type = String.class)
	public String getIpAddress() {
		return ipAddress;
	}

	@XmlElement(name = "ldap-size",type = long.class,defaultValue ="1")
	public long getLdapSizeLimit() {
		return ldapSizeLimit;
	}

	@XmlElement(name = "maximum-pool-size",type = int.class,defaultValue ="5")
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	@XmlElement(name = "minimum-pool-size",type = int.class,defaultValue = "2")
	public int getMinPoolSize() {
		return minPoolSize;
	}
	
	@XmlElement(name = "password",type = String.class,defaultValue = DEFAULT_USERNAME_PASSWORD)
	public String getPassword() {
		return password.getPassword();
	}
	
	@Override
	public String getPlainTextPassword(){
		return password.getPlainTextPassword();
	}
	
	@XmlTransient
	public int getPort() {
		return port;
	}
	@XmlElement(name = "timeout",type = int.class,defaultValue ="1000")
	public int getTimeout() {
		return timeout;
	}
	@XmlElement(name = "user-prefix",type = String.class,defaultValue ="uid=")
	public String getUserPrefix() {
		return userPrefix;
	}
	@XmlElementWrapper(name = "ldap-base-details")
	@XmlElement(name = "ldap-base-name")
	public ArrayList<String> getSearchBaseDnList(){
		return this.searchBaseDnList;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    Datasource Name = " + dsName);
		out.println("    Version = " + version);
		out.println("    IP Address = " + this.ipAddress);
		out.println("    Port = " + this.port);
		out.println("    Password = *****");
		out.println("    User Prefix = " + userPrefix );
		out.println("    Minimum Pool Size = " + minPoolSize);
		out.println("    Maximum Pool Size = " + maxPoolSize);
		out.println("    Timeout = " + timeout );
		out.println("    LDAP Size Limit " + ldapSizeLimit);
		out.println("    Administrator = " + administrator);
		out.println("    Search BaseDN List: ");
		Iterator<String> iterator =  searchBaseDnList.iterator();
		while(iterator.hasNext()){
			out.println("          " + iterator.next().toString());			
		}

		out.close();
		return stringBuffer.toString();
	}
}
