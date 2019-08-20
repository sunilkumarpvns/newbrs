package com.elitecore.core.commons.util.db;

import com.elitecore.core.commons.util.db.DBConnectionManager;

/**
 * Class only to be used in test cases, that allows breaking singleton-ness of DBConnectionManager.
 *  
 * @author narendra.pathai
 *
 */
public class DBConnectionManagerFactory {

	public DBConnectionManager newInstance(String datasourceName) {
		return new DBConnectionManager(datasourceName);
	}
}
