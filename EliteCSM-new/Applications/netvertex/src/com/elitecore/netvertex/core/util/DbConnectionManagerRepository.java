package com.elitecore.netvertex.core.util;

import com.elitecore.core.commons.util.db.DBConnectionManager;

public interface DbConnectionManagerRepository {


    public DBConnectionManager getDbConnectionManager(String dataSourceKey);


}
