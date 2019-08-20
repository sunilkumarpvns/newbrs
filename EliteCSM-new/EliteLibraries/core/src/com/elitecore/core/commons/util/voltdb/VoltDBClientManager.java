package com.elitecore.core.commons.util.voltdb;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltDBClientManager {

    private static final String MODULE = "VOLTDB-CLIENT-MANAGER";
    private Map<String, VoltDBClient> voltDBClientsByName = new HashMap<>();

    /**
     *
     * @param dataSource the data source which contains parameters require to initialize the  database connection (url, username, password, maximum pool size, minimum pool size)
     * @param taskScheduler
     * @throws InitializationFailedException - if data source initialization failed due to any reason
     */
    public synchronized void createAndRegisterClient(DBDataSource dataSource,TaskScheduler taskScheduler)
            throws InitializationFailedException {

        VoltDBClient voltDBClient;
        if(dataSource.getStatusCheckDuration() > 0){
            voltDBClient = new VoltDBClient(dataSource, taskScheduler,dataSource.getStatusCheckDuration());
        } else{
            voltDBClient = new VoltDBClient(dataSource, taskScheduler, ESCommunicator.NO_SCANNER_THREAD);
        }

        voltDBClientsByName.put(dataSource.getDataSourceName(), voltDBClient);

        try {
            voltDBClient.init();
        } catch (InitializationFailedException e) {
            voltDBClient.markDead();
            getLogger().error(MODULE, "Error while creating VoltDB Client. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }

    }

    public VoltDBClient getOrCreateClient(DBDataSource dataSource, TaskScheduler taskScheduler) throws InitializationFailedException {

        if(voltDBClientsByName.get(dataSource.getDataSourceName()) == null){
            createAndRegisterClient(dataSource, taskScheduler);
        }

        return voltDBClientsByName.get(dataSource.getDataSourceName());
    }

    public VoltDBClient getVoltDBClient(String name){
        return voltDBClientsByName.get(name);
    }

    public List<String> getDataSources() {

        Iterator<String> iterator = voltDBClientsByName.keySet().iterator();
        List<String> datasources = new ArrayList<>();
        while(iterator.hasNext()){
            VoltDBClient voltdbClient = voltDBClientsByName.get(iterator.next());
            datasources.add(voltdbClient.getName());
        }
        return datasources;
    }

    /**
     * Checks dataSourceName is initialized in DBConnectionManager or not
     *
     * @param dataSourceName
     * @return
     */
    public boolean isExist(String dataSourceName) {
        return voltDBClientsByName.keySet().contains(dataSourceName);
    }


    public void stop() {
        voltDBClientsByName.values().forEach((VoltDBClient voltDBClient) -> voltDBClient.stop());
    }
}
