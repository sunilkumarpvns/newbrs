package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.core.conf.LDAPDSConfiguration;
import com.elitecore.aaa.core.config.LDAPDataSourceImpl;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

@ReadOrder(order = { "ldapConfigurable" })
public class LDAPDSConfigurationImpl extends CompositeConfigurable implements LDAPDSConfiguration{

	@Configuration private LDAPDSConfigurable ldapConfigurable;
	private Map<String, LDAPDataSource>datasourceMap;
	private Map<String , LDAPDataSource>datasourceNameMap;
	
	public LDAPDSConfigurationImpl(ServerContext serverContext) {
		ldapConfigurable = new LDAPDSConfigurable();
		datasourceMap = new HashMap<String, LDAPDataSource>();
		datasourceNameMap = new HashMap<String, LDAPDataSource>();
	}
	
	public LDAPDSConfigurationImpl() {		
		datasourceMap = new HashMap<String, LDAPDataSource>();
		datasourceNameMap = new HashMap<String, LDAPDataSource>();
	}

		
	public LDAPDataSource getDatasurce(String dsID) {
		return this.datasourceMap.get(dsID);
	}
	
	public LDAPDataSource getDatasourceByName(String dsName){
		return this.datasourceNameMap.get(dsName);
	}

	public Map<String, LDAPDataSource> getDatasourceMap() {
		return this.datasourceMap;
	}
	
	public Map<String, LDAPDataSource> getDatasourceNameMap(){
		return this.datasourceNameMap;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println("-- LDAP Datasource Configuration - Start --");
		
		if (datasourceMap != null && !datasourceMap.isEmpty()) {
			for(Entry<String,LDAPDataSource> entry:datasourceMap.entrySet()){
				out.println(entry.getValue());
			}
			//out.println("    ------------------");
			//out.println("    TOTAL COUNT: " + datasourceMap.size());
		} else {
			out.println("      No configuration found");
		}
		out.println("-- LDAP Datasource Configuration [" +datasourceMap.size()+"]  - End --");
		out.close();
		return stringBuffer.toString();
	}

	

	
	@PostRead
	public void postReadProcessing(){
		List<LDAPDataSource> ldapDataSourceList = ldapConfigurable.getLdapDataSourceList();
		LDAPDataSourceImpl ldapDataSource;
		
		if(ldapDataSourceList!=null){
			int size = ldapDataSourceList.size();
			for(int i=0;i<size;i++){
				ldapDataSource = (LDAPDataSourceImpl) ldapDataSourceList.get(i);
				String addressStr = ldapDataSource.getIpAddress(); 
				URLData urlData = null;
				try{
					urlData = URLParser.parse(addressStr);
				}catch(InvalidURLException ex){
					continue;
				}
				
				String ipAddress = urlData.getHost();
				ldapDataSource.setStrIpAddress(ipAddress);
				int port = urlData.getPort();
				ldapDataSource.setPort(port);
				
				this.datasourceMap.put(ldapDataSource.getDataSourceId(), ldapDataSource);
				this.datasourceNameMap.put(ldapDataSource.getDataSourceName(), ldapDataSource);
			}
		}		
	}

	@DBReload
	public void reloadConfiguration() throws LoadConfigurationException {
		// TODO Auto-generated method stub
		
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}

}
