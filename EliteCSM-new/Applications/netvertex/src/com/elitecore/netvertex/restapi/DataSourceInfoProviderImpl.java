package com.elitecore.netvertex.restapi;

import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.corenetvertex.DataSourceInfo;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataSourceInfoProviderImpl implements DataSourceInfoProvider {

    public static final String DB_DATA_SOURCE_TYPE = "DB";
    public static final String LDAP_DATA_SOURCE_TYPE = "LDAP";
    private static final String DEAD = "DEAD";
    private static final String ALIVE = "ALIVE";
    private static final String NOT_INITIALIZED = "NOT_INITIALIZED";

    private NetVertexServerContext serverContext;

    public DataSourceInfoProviderImpl(NetVertexServerContext serverContext){
        this.serverContext = serverContext;
    }

    @Override
    public Map<String, DataSourceInfo> getDataSourceInfo() {
        return createDataSourceInfo(getAllDataSources(), serverContext.getServerConfiguration().getLDAPDSConfiguration()
                .getDatasourceNameMap().values().stream()
                .map(LDAPDataSource.class::cast)
                .collect(Collectors.toMap(LDAPDataSource::getDataSourceName, Function.identity())));

    }

    @Override
    public Map<String, List<DataSourceInfo>> getDataSourceInfoByType() {
        return createDataSourceInfoByType(getAllDataSources(), serverContext.getServerConfiguration().getLDAPDSConfiguration()
                .getDatasourceNameMap().values().stream()
                .map(LDAPDataSource.class::cast)
                .collect(Collectors.toMap(LDAPDataSource::getDataSourceName,Function.identity())));

    }

    private Map<String, DBDataSource> getAllDataSources() {

        Map<String, DBDataSource> dataSourceMap = new HashMap<>();
        DBDataSource defaultDataSource = NetVertexDBConnectionManager.getInstance().getDataSource();
        dataSourceMap.put(defaultDataSource.getDataSourceName(), defaultDataSource);

        Map<String, DBDataSourceImpl> tempDataSourceMap = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDatasourceNameMap();
        dataSourceMap.putAll(tempDataSourceMap);

        return dataSourceMap;

    }

    private Map<String, DataSourceInfo> createDataSourceInfo(Map<String, DBDataSource> dataSourceMap, Map<String, LDAPDataSource> ldapDataSourceMap){
        Map<String, DataSourceInfo> dataSourceInfoMap = new HashMap<>();

        for (DBDataSource dbDataSource: dataSourceMap.values()) {
            if (dbDataSource.getConnectionURL().contains(CommonConstants.VOLTDB) == false) {
                dataSourceInfoMap.put(dbDataSource.getDataSourceName(), new DataSourceInfo(DB_DATA_SOURCE_TYPE, dbDataSource.getDataSourceName(), DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).getNumberOfActiveConnections(), dbDataSource.getMinimumPoolSize(), dbDataSource.getMaximumPoolSize(), DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).checkDSStatus()));
            }else{
                dataSourceInfoMap.put(dbDataSource.getDataSourceName(), new DataSourceInfo(DB_DATA_SOURCE_TYPE, dbDataSource.getDataSourceName(), DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).getNumberOfActiveConnections(), dbDataSource.getMinimumPoolSize(), dbDataSource.getMaximumPoolSize(), checkVoltDbStatus(dbDataSource.getDataSourceName())));
            }
        }

        for (LDAPDataSource ldapDataSource: ldapDataSourceMap.values()){
            dataSourceInfoMap.put(ldapDataSource.getDataSourceName(), new DataSourceInfo(LDAP_DATA_SOURCE_TYPE, ldapDataSource.getDataSourceName(), 0, ldapDataSource.getMinPoolSize(), ldapDataSource.getMaxPoolSize(), null));
        }
        return dataSourceInfoMap;
    }

    private Map<String, List<DataSourceInfo>> createDataSourceInfoByType(Map<String, DBDataSource> dataSourceMap, Map<String, LDAPDataSource> ldapDataSourceMap){
        Map<String, List<DataSourceInfo>> dataSourceInfoMapByType = new HashMap<>();

        ArrayList<DataSourceInfo> dbDataSourceInfo = new ArrayList<>();
        for (DBDataSource dbDataSource: dataSourceMap.values()) {
            dbDataSourceInfo.add(new DataSourceInfo(DB_DATA_SOURCE_TYPE, dbDataSource.getDataSourceName(), DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).getNumberOfActiveConnections(), dbDataSource.getMinimumPoolSize(), dbDataSource.getMaximumPoolSize(), DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).checkDSStatus()));
            dataSourceInfoMapByType.put(DB_DATA_SOURCE_TYPE, dbDataSourceInfo);
        }

        ArrayList<DataSourceInfo> ldapDataSourceInfo = new ArrayList<>();
        for (LDAPDataSource ldapDataSource: ldapDataSourceMap.values()){
            ldapDataSourceInfo.add(new DataSourceInfo(LDAP_DATA_SOURCE_TYPE, ldapDataSource.getDataSourceName(), 0, ldapDataSource.getMinPoolSize(), ldapDataSource.getMaxPoolSize(), LDAPConnectionManager.getInstance(ldapDataSource.getDataSourceName()).getDSStatus()));
            dataSourceInfoMapByType.put(LDAP_DATA_SOURCE_TYPE, ldapDataSourceInfo);
        }
        return dataSourceInfoMapByType;
    }

    private String checkVoltDbStatus(String dataSourceName){
        if(Objects.isNull(serverContext.getVoltDBClientManager().getVoltDBClient(dataSourceName))){
            return NOT_INITIALIZED;
        }
        return serverContext.getVoltDBClientManager().getVoltDBClient(dataSourceName).isAlive()?ALIVE:DEAD;
    }
}