package com.elitecore.netvertex.core.util;

import com.elitecore.core.commons.util.db.DBConnectionManager;

public class DefaultDbConnectionManagerRepository implements DbConnectionManagerRepository {

    @Override
    public DBConnectionManager getDbConnectionManager(String dataSourceKey) {
        return DBConnectionManager.getInstance(dataSourceKey);
    }
}
