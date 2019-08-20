package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.core.conf.DatabaseDSConfiguration;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;

@ReadOrder(order = { "configurableInstance" })
public class DatabaseDSConfigurationImpl extends CompositeConfigurable implements DatabaseDSConfiguration{
	private Map<String, DBDataSource> datasourceMap;
	private Map<String, DBDataSource> datasourceNameMap;
	
	@Configuration private DatabaseDSConfigurable configurableInstance;
	
	public DatabaseDSConfigurationImpl() {
		datasourceMap = new HashMap<String, DBDataSource>();
		datasourceNameMap = new HashMap<String, DBDataSource>();
	}
	
	@Override
	public DBDataSource getDataSource(String dsID) {
		return datasourceMap.get(dsID);
	}
	
	@Override
	public Map<String, DBDataSource> getDatasourceMap() {
		return datasourceMap;
	}
	
	@Override
	public Map<String, DBDataSource> getDatasourceNameMap() {
		return this.datasourceNameMap;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println(" -- Database DS Configuration - Start -- ");
		
		if (datasourceMap != null && !datasourceMap.isEmpty()) {
			for(Entry<String,DBDataSource> entry:datasourceMap.entrySet()){
				out.println(entry.getValue());
			}
		} else {
			out.println("      No configuration found");
		}

		out.println(" -- Database DS Configuration [" +datasourceMap.size()+"] - End -- ");
		out.close();
		return stringBuffer.toString();
	}
	

	@Override
	public DBDataSource getDataSourceByName(String dsName) {
		return datasourceNameMap.get(dsName);
	}

	
	@PostRead
	public void postReadProcessing(){
		List<DBDataSource> dataSourceList = configurableInstance.getDbDataSourceList();
		DBDataSource dbDataSource ;
		if(dataSourceList!=null){
			int size = dataSourceList.size();
			for(int i=0;i<size;i++){
				dbDataSource = dataSourceList.get(i);
				this.datasourceMap.put(dbDataSource.getDatasourceID(), dbDataSource);
				this.datasourceNameMap.put(dbDataSource.getDataSourceName(), dbDataSource);
			}
		}
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}




	@Override
	public void reloadConfiguration() throws LoadConfigurationException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	} 
}
