package com.elitecore.core.commons.util.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.core.commons.util.db.datasource.DbcpConnectionDataSource;
import com.elitecore.core.commons.util.db.datasource.IConnectionDataSource;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

/**
 * 
 * @author Malav Desai
 *
 */
public class DBConnectionFactory {
	
	public IConnectionDataSource createDataBaseConnection(String url,
			String username, String password, int minPoolSize, int maxPoolSize,
			Map<EliteDBConnectionProperty, String> properties) throws DatabaseTypeNotSupportedException {
		
		validateValues(url, username, password);
		DBVendors vendor = DBVendors.fromUrl(url);
		HashMap<String, String> additionalProperty = new HashMap<String, String>();
		for (Entry<EliteDBConnectionProperty, String> property : properties.entrySet()) {
			String key = vendor.getVendorConnectionProperty(property.getKey());
			if(key != null && key.trim().length() > 0){
				additionalProperty.put(key, property.getValue());
			}
			
		}
		return new DbcpConnectionDataSource(url, username, password, minPoolSize, maxPoolSize, 
				additionalProperty, vendor);
	}

	private void validateValues(String url, String username, String password) {
		checkNotNull(url, "url is null");
		checkNotNull(username, "username is null");
		checkNotNull(password, "password is null");
	}

}

