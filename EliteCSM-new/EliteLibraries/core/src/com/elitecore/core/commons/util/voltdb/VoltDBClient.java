package com.elitecore.core.commons.util.voltdb;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class VoltDBClient extends ESCommunicatorImpl {

    private static final String MODULENAME = "VOLTDB-CLIENT";
    private DBDataSource dataSource;
    private int statusCheckDuration;
    private TaskScheduler taskSchedular;
    private Client client;
    private boolean isInitCalled = false;

    private static final String IS_VOLT_DB_AVAILABLE = "IsVoltDBAvailable";

    public VoltDBClient(DBDataSource dataSource, TaskScheduler taskScheduler, int statusCheckDuration) {
        super(taskScheduler);
        this.dataSource = dataSource;
        this.taskSchedular = taskScheduler;
        this.statusCheckDuration = statusCheckDuration;
    }

    @Override
    protected int getStatusCheckDuration() {
        return statusCheckDuration;
    }

    @Override
    public void init() throws InitializationFailedException {

        if (isDatabaseAvailable() && isInitCalled) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULENAME, "Datasource: " + dataSource.getDataSourceName() + " is already initialized.");
            }
            return;
        }
        initializeClient();
    }

    private void initializeClient() throws InitializationFailedException {
        ClientConfig config = new ClientConfig(dataSource.getUsername(), dataSource.getPassword(),
                new VoltDBClientStatusListener(dataSource.getDataSourceName()));

        config.setProcedureCallTimeout(TimeUnit.SECONDS.toMillis(CommonConstants.QUERY_TIMEOUT_SEC));

        try {
            super.init();
            client = ClientFactory.createClient(config);
            addConnectionToClient(client, dataSource);
            isInitCalled = true;
            markAlive();

        } catch (IOException e) {
            getLogger().error(MODULENAME, "Error while initializing VoltDB Client. Reason: " + e.getMessage());
            getLogger().trace(MODULENAME, e);
        }
    }

    private void addConnectionToClient(Client client, DBDataSource dbDataSource) throws IOException {

        //FIXME SUPPORT FOR IPV6
        String commaSeperatedIpAddressList = dbDataSource.getConnectionURL().split("//")[1];

        List<String> ipAddressList = CommonConstants.COMMA_SPLITTER.split(commaSeperatedIpAddressList);

        for (String ipAddress : ipAddressList) {
            String[] ipPort = Strings.splitter(CommonConstants.COLON).trimTokens().splitToArray(ipAddress);
            client.createConnection(ipPort[0], Integer.parseInt(ipPort[1]));
        }
    }

    @Override
    public synchronized void scan() {

        if (isDatabaseAvailable()) {
            markAlive();
        } else {
            try {
                reInit();
            } catch (InitializationFailedException e) {
                getLogger().error(MODULENAME, "Error while re-initializing VoltDB Client. Reason: " + e.getMessage());
                getLogger().trace(MODULENAME, e);
            }
        }
    }

    private boolean isDatabaseAvailable() {

        try {
            ClientResponse clientResponse = client.callProcedure(IS_VOLT_DB_AVAILABLE, UUID.randomUUID().toString());
            return clientResponse.getStatus() == ClientResponse.SUCCESS;
        } catch (Exception e) {
            markDead();
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULENAME, "Error while checking for aliveness of datasource: " +
                        dataSource.getDataSourceName() + ", Reason: " + e.getMessage());
            }
            LogManager.getLogger().trace(e);

            return false;
        }
    }

    @Override
    public synchronized void reInit() throws InitializationFailedException {
        try {
            client.drain();
            client.close();
        } catch (InterruptedException | NoConnectionsException e) {
            markDead();
            if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
                LogManager.getLogger().warn(MODULENAME, "Error while checking for aliveness of datasource: " +
                        dataSource.getDataSourceName() + ", Reason: " + e.getMessage());
            }
            LogManager.getLogger().trace(MODULENAME, e);
            Thread.currentThread().interrupt();
        }

        initializeClient();
    }

    @Override
    public String getName() {
        return dataSource.getDataSourceName();
    }

    @Override
    public String getTypeName() {
        return MODULENAME;
    }

    @Override
    protected boolean checkForFallback() {
        taskSchedular.scheduleSingleExecutionTask(
            new SingleExecutionAsyncTask() {

                @Override
                public TimeUnit getTimeUnit() {
                    return TimeUnit.SECONDS;
                }

                @Override
                public long getInitialDelay() {
                    return 0;
                }

                @Override
                public void execute(AsyncTaskContext context) {
                    scan();
                }
            });

        return false;
    }

    public ClientResponse callProcedure(String procName, Object... parameters)
            throws IOException, NoConnectionsException, ProcCallException {
        try {
            return client.callProcedure(procName, parameters);
        }catch (NoConnectionsException e) {
            markDead();
            throw e;
        }  catch (IOException e) {
            throw e;
        } catch (ProcCallException e) {
            handleFailScenario(e.getClientResponse());
            throw e;
        }
    }

    public void handleFailScenario(ClientResponse clientResponse) {

        if(clientResponse.getStatus() == ClientResponse.CONNECTION_LOST || clientResponse.getStatus() == ClientResponse.SERVER_UNAVAILABLE){
            markDead();
        }
    }

    public boolean callProcedure(ProcedureCallback callback, String procName, Object... parameters)
            throws IOException, NoConnectionsException {
        try {
            return client.callProcedure(callback, procName, parameters);
        } catch (NoConnectionsException e) {
            markDead();
            throw e;
        }  catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void stop() {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULENAME, "Stopping VoltDB Client for datasource: " +
                    dataSource.getDataSourceName());
        }

        super.stop();

        try {
            client.drain();
            client.close();

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULENAME, "VoltDB Client for datasource: " +
                        dataSource.getDataSourceName() + " stopped successfully");
            }
        } catch (Exception e) {
            markDead();
            getLogger().warn(MODULENAME, "Error while stopping VoltDB Client for datasource: " +
                        dataSource.getDataSourceName() + ". Reason: " + e.getMessage());
            getLogger().trace(MODULENAME, e);
        }

    }
}
