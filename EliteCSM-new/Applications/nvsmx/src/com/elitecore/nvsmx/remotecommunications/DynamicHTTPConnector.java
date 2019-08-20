package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import com.elitecore.nvsmx.remotecommunications.exception.CommunicationException;
import com.elitecore.nvsmx.remotecommunications.ws.WebServiceConnector;
import com.elitecore.nvsmx.remotecommunications.ws.WebServiceMethods;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class DynamicHTTPConnector extends ESCommunicatorImpl implements NVSMXEndPoint{

    private static final int DEFAULT_STATUS_CHECK_DURATION = 10;
    private static final String STATUS_CHECK_DURATION_PROPERTY = "rmi.statuscheckduration";
    private final List<String> ipAddresses;
    private final String baseURI;
    private final String MODULE; //NOSONAR
    @Nullable private WebServiceConnector connector;
    private ExecutorService executorService;
    private String port ;
    private final String contextPath;
    private int statusCheckDuration = -1;
    private ServerInformation serverInstanceData;
    private ServerInformation instanceGroupData;
    private String endPointStatus;
    private final String localPdContextId;


    public DynamicHTTPConnector(PDContextInformation pdContextInformation, String baseURI, ServerInformation netServerInstanceData, ServerInformation serverInstanceGroupData, TaskScheduler taskScheduler, ExecutorService executorService, String localPdContextId) {
        super(taskScheduler);
        this.ipAddresses = CommonConstants.COMMA_SPLITTER.split(pdContextInformation.getIpAddresses());
        this.port = pdContextInformation.getPort();
        this.contextPath = pdContextInformation.getContextPath();
        this.baseURI = baseURI;
        this.serverInstanceData = netServerInstanceData;
        this.instanceGroupData = serverInstanceGroupData;
        this.executorService = executorService;
        this.endPointStatus = pdContextInformation.getStatus();
        this.localPdContextId = localPdContextId;
        this.MODULE = "DYNAMIC-HTTP-CONNECTOR-" + netServerInstanceData.getName();
    }


    @Override
    public void init() throws InitializationFailedException {

        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Initializing instance for context path: " +contextPath +": at port: "+port);

        readStatusCheckDuration();

        super.init();

        /**
        *if end point is in shut down state no need to mark it as Dead as we want to skip it from scanning
        */
        if(isShutDown()){
            if(getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Instance for context path: " +contextPath +": at port: "+port +" can't be initialized.Reason : Instance is in shut down state");
            }
              return ;
        }
          resolveConnectorIP();

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Instance for context path: " +contextPath +": at port: "+port +" initialized successfully");
        }
    }

    private void readStatusCheckDuration() {
        String env = System.getenv(STATUS_CHECK_DURATION_PROPERTY);
        if(Strings.isNullOrBlank(env) == false) {
            try {
                statusCheckDuration = Integer.parseInt(env);
            }catch (Exception e) {
                getLogger().error(MODULE, "Invalid value("+ env +") configured for "+ STATUS_CHECK_DURATION_PROPERTY + " in environment variable");
                getLogger().trace(MODULE, e);
            }
        }

        if(statusCheckDuration == -1) {
            String property = System.getProperty(STATUS_CHECK_DURATION_PROPERTY);
            if(Strings.isNullOrBlank(property) == false) {
                try {
                    statusCheckDuration = Integer.parseInt(property);
                }catch (Exception e) {
                    getLogger().error(MODULE, "Invalid value("+ property +") configured for "+ STATUS_CHECK_DURATION_PROPERTY + " in system property");
                    getLogger().trace(MODULE, e);
                }
            }
        }

        if(statusCheckDuration == -1) {
            statusCheckDuration = DEFAULT_STATUS_CHECK_DURATION;
        }

        if(getLogger().isInfoLogLevel()){
            getLogger().info(MODULE, "Status check duration is "+ statusCheckDuration +" seconds");
        }
    }

    private void resolveConnectorIP() {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Calling resolveConnectorIP");
        }
        for (String ipAddress : ipAddresses) {
            String restAddress = ipAddress + ":" + port + contextPath;
            WebServiceConnector webServiceConnector = new WebServiceConnector(serverInstanceData.getName(), restAddress, executorService);
            webServiceConnector.init();
            checkStatus(webServiceConnector);
            if (isAlive()) {
                return;
            }
        }
    }

    private void checkStatus(WebServiceConnector webServiceConnector) {
        boolean isLiveServer = false;
        try {
            Future<String> status = webServiceConnector.call(new RemoteMethod(baseURI, WebServiceMethods.SERVER_STATUS.name(), localPdContextId, HTTPMethodType.GET));
            String string = status.get(3, TimeUnit.SECONDS);
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, string + " status received from server");
            if (LifeCycleState.RUNNING.message.equalsIgnoreCase(string) || LifeCycleState.RUNNING_WITH_LAST_CONF.message.equalsIgnoreCase(string)) {
                markAlive();
                connector = webServiceConnector;
                isLiveServer =  true;
            }
        } catch (InterruptedException e) { //NOSONAR
            getLogger().error(MODULE, "Interruption occurred while checking for server aliveness");
        } catch (ExecutionException e) {
            getLogger().error(MODULE, "Error while checking for aliveness. Reason: " + e.getCause().getMessage());
            getLogger().trace(e);
        } catch (TimeoutException e) {
            getLogger().error(MODULE, "Timeout occurred while checking for server aliveness");
            getLogger().trace(e);
        } catch (CommunicationException e) {
            getLogger().error(MODULE, "Unable to check status for server aliveness. Reason: " + e.getMessage());
            getLogger().trace(e);
        }
        if(isLiveServer == false){
            markDead();
        }
    }

    @Override
    public void scan() {

        if(getLogger().isDebugLogLevel())
            getLogger().debug(MODULE, "Checking for aliveness");

        if(isShutDown()){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "End point is in shut down state scan not required");
            }
           return ;
        }
        if(connector == null ){
            resolveConnectorIP();
        } else {
            checkStatus(connector);
        }
    }



    @Override
    public String getName() {
        return MODULE;
    }

    @Override
    public String getTypeName() {
        return "REST";
    }

    @Override
    protected int getStatusCheckDuration() {
        return statusCheckDuration;
    }


    @Override
    public <V> Future<RMIResponse<V>> submit(RemoteMethod method)  {

        try {
            if(isShutDown()){
                SettableFuture<RMIResponse<V>> errorFuture = SettableFuture.create();
                errorFuture.set(new ErrorRMIResponse<V>(new CommunicationException("ServerInstance: "+serverInstanceData.getName()+" is in shut down state"), instanceGroupData , serverInstanceData));
                return errorFuture;
            }

            if(isAlive() == false ){
                SettableFuture<RMIResponse<V>> errorFuture = SettableFuture.create();
                errorFuture.set(new ErrorRMIResponse<V>(new CommunicationException("ServerInstance: "+serverInstanceData.getName()+" is not alive"), instanceGroupData , serverInstanceData));
                return errorFuture;
            }

            final Future<V> future = connector.call(method);

            return new ProxyFuture<V>(future);
        } catch (CommunicationException communicationException) {
            markDead();
            SettableFuture<RMIResponse<V>> errorFuture = SettableFuture.create();
            errorFuture.set(new ErrorRMIResponse<V>(communicationException, instanceGroupData, serverInstanceData));
            return errorFuture;
        }
    }

    @Override
    public ServerInformation getGroupData() {
        return instanceGroupData;
    }

    @Override
    public ServerInformation getInstanceData() {
        return serverInstanceData;
    }

    private class ProxyFuture<T> implements Future<RMIResponse<T>> {

        private Future<T> future;

        public ProxyFuture(Future<T> reAuthorizeBySubscriberId) {
            future = reAuthorizeBySubscriberId;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return future.isDone();
        }

        @Override
        public RMIResponse<T> get() throws InterruptedException{
            try {
                T response = future.get();
                return new SuccessRMIResponse<T>(response, instanceGroupData, serverInstanceData);
            } catch(ExecutionException executionException) {
                markDead();
                return new ErrorRMIResponse<T>(executionException, instanceGroupData, serverInstanceData);
            }
        }

        @Override
        public RMIResponse<T> get(long timeout, TimeUnit unit) throws InterruptedException,TimeoutException {
            try {
                T response = future.get(timeout,unit);
                return new SuccessRMIResponse<T>(response, instanceGroupData, serverInstanceData);
            } catch(ExecutionException executionException) {
                markDead();
                return new ErrorRMIResponse<T>(executionException, instanceGroupData, serverInstanceData);
            }
        }
    }

    @Override
    public void stop() {

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Shutting down Dynamic HTTP Connector for group: "+ instanceGroupData.getName());
        }

        super.stop();
        if (connector != null) {
            connector.shutDown();
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Dynamic HTTP Connector has shut down");
        }
    }

    @Override
    public void markShutDown(){
        this.endPointStatus = EndPointStatus.SHUT_DOWN.getVal();
    }

    @Override
    public void markStarted() {

        this.endPointStatus = EndPointStatus.STARTED.getVal();

        //TO check whether web service connector is null i.e connection is made to end point ip
        if(connector == null ){
            resolveConnectorIP();
        }else{
            checkStatus(connector);
        }
    }

    @Override
    public String getStatus(){
        return this.endPointStatus;
    }

    private boolean isShutDown(){
        return EndPointStatus.SHUT_DOWN.getVal().equalsIgnoreCase(endPointStatus);
    }
}