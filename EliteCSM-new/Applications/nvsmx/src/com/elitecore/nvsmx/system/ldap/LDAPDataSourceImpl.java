package com.elitecore.nvsmx.system.ldap;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.ldap.LdapBaseDn;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.nvsmx.system.util.PasswordUtility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class LDAPDataSourceImpl implements LDAPDataSource, com.elitecore.corenetvertex.core.ldap.LDAPDataSource{
	private static final String MODULE = "LDAP-DATA-SOURCE-IMPL";
	private static final String DEFAULT_USERNAME_PASSWORD = "admin";

	private String dsId;                                                                                                                                                                                    
	private String dsName;                                                                                                                                                                                 
	private String ipAddress;                                                                                                                                                                                 
	private int port;
	private String strIpAdderess;
	private int timeout = 1000;
	private int version = 2;
	private long ldapSizeLimit = 1;
	private String administrator = DEFAULT_USERNAME_PASSWORD;                                                                                                                                                                                 
	private String password = DEFAULT_USERNAME_PASSWORD;                                                                                                                                                                                 
	private String userPrefix = "uid=";                                                                                                                                                                                 
	private int maxPoolSize = 5;                                                                                                                                                                                    
	private int minPoolSize = 2;                                                                                                                                                                                    
	private ArrayList<String> searchBaseDnList = new ArrayList<String>();


	public LDAPDataSourceImpl(){
		//required by Jaxb.
	}

	public LDAPDataSourceImpl(String dsId, String dsName, int version, String ipAddress, int timeout, long ldapSizeLimit, String administrator, String password, String userPrefix, int maxPoolSize, int minPoolSize,ArrayList<String> searchBaseDnList, int port, String strIpAdderess ) {
		this.dsId = dsId;
		this.dsName = dsName;
		this.version = version;
		this.ipAddress = ipAddress;
		this.timeout = timeout;
		this.ldapSizeLimit = ldapSizeLimit;
		this.administrator = administrator;
		this.password = password;
		this.userPrefix = userPrefix;
		this.maxPoolSize = maxPoolSize;
		this.minPoolSize = minPoolSize;
		this.searchBaseDnList = searchBaseDnList;
		this.port = port;
		this.strIpAdderess = strIpAdderess;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setStrIpAddress(String strIpAdderess){
		this.strIpAdderess =strIpAdderess;
	}

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
		this.password = password;
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

	public String getAdministrator() {
		return administrator;
	}

	public int getVersion() {
		return version;
	}

	public String getDataSourceId() {
		return dsId;
	}

	public String getDataSourceName() {
		return dsName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public long getLdapSizeLimit() {
		return ldapSizeLimit;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getPlainTextPassword() {

		try {
			return PasswordUtility.getDecryptedPassword(password);
		} catch (Exception e) { //NOSONAR
			//this should not occur in normal scenarios, so returning th
			return password;
		}
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}

	public String getUserPrefix() {
		return userPrefix;
	}

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
			out.println("          " + iterator.next());
		}

		out.close();
		return stringBuffer.toString();
	}

	public static LDAPDataSourceImpl create(LdapData ldapData, FailReason failReason) {

		List<LdapBaseDn> ldapBaseDnDetailDatas = ldapData.getLdapBaseDns();
		if(Collectionz.isNullOrEmpty(ldapBaseDnDetailDatas)) {
			failReason.add("LDAP search base dn not configured in data source:" + ldapData.getName());
		}

		Integer  maximumPool = ldapData.getMaximumPool();
		if(maximumPool != null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Taking 10 as maximum pool size. Reason: Maximum pool size not configured in data source:" + ldapData.getName());
			}
			maximumPool = 10;
		}

		Integer minimumPool = ldapData.getMinimumPool();
		if(minimumPool== null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Taking 1 as manimum pool size. Reason: Minimum pool size not configured in data source:" + ldapData.getName());
			}
			minimumPool = 1;
		}

		Long sizeLimit = ldapData.getSizeLimit();
		if(sizeLimit== null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Taking 1 as size limit. Reason: Size limit not configured in data source:" + ldapData.getName());
			}
			sizeLimit = 1l;
		}

		Integer timeout = ldapData.getQueryTimeout();
		if(timeout == null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Taking 10 as timeout. Reason: Timeout not configured in data source:" + ldapData.getName());
			}
			timeout = 3;
		}

		Integer ldapVersion = ldapData.getVersion();
		if(ldapVersion == null) {
			if(getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Taking 2 as version value. Reason: Version not configured in data source:" + ldapData.getName());
			}
			ldapVersion = 2;
		}

		String strIpAddress = null;
		int port = CommonConstants.DEFAULT_LDAP_PORT;

		try {

			URLData urlData = URLParser.parse(ldapData.getAddress());
			strIpAddress = urlData.getHost();
			port = urlData.getPort();

			if(port == 0) {
				if(getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Taking "+ CommonConstants.DEFAULT_LDAP_PORT +" as port value. Reason: Port not configured in data source:" + ldapData.getName());
				}
				port = CommonConstants.DEFAULT_LDAP_PORT;
			}
		} catch (InvalidURLException e) {
			getLogger().error(MODULE,"Error while parsing LDAP address. Reason: "+e.getMessage());
			getLogger().trace(MODULE,e);
		}


		ArrayList<String> baseDnDetails = ldapBaseDnDetailDatas.
				stream().
				map(LdapBaseDn::getSearchBaseDn).
				collect(Collectors.toCollection(ArrayList::new));

		return new LDAPDataSourceImpl(
				String.valueOf(ldapData.getId()),
				ldapData.getName(),
				ldapVersion,
				ldapData.getAddress(),
				timeout.intValue(),
				sizeLimit,
				ldapData.getAdministrator(),
				ldapData.getPassword(),
				ldapData.getUserDnPrefix(),
				maximumPool,
				minimumPool,
				baseDnDetails,
				port,
				strIpAddress);
	}
}
