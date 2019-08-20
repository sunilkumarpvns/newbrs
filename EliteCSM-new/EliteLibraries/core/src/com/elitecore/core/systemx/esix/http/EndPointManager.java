package com.elitecore.core.systemx.esix.http;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.configuration.EndPointConfiguration;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.client.HttpClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class EndPointManager {
    private Map<String, HTTPConnector> endPoints;
    private static EndPointManager endPointManager;
    private static final String MODULE = "EndPoint-Manager";
    public static final int TASK_CHECK_DURATION = 10;
    private static HTTPScanner httpScanner;

    private EndPointManager(){
        endPoints = new HashedMap();
    }

    static {
        endPointManager = new EndPointManager();
        httpScanner = new HTTPScanner();
    }

    public static EndPointManager getInstance(){
        return endPointManager;
    }

    public void addEndPoint(EndPointConfiguration endPointConfiguration, HttpClient httpClient, TaskScheduler taskScheduler) {
        addEndPoint(endPointConfiguration,httpClient,taskScheduler,httpScanner);
    }

    public void addEndPoint(EndPointConfiguration endPointConfiguration, HttpClient httpClient, TaskScheduler taskScheduler,
                            Scanner httpScanner) {
        if(endPoints.get(endPointConfiguration.getId())==null){
            HTTPConnector endPoint = new HTTPConnector(endPointConfiguration.getName(),
                    Integer.parseInt(endPointConfiguration.getPort()),
                    endPointConfiguration.getContextPath(),
                    TASK_CHECK_DURATION ,
                    httpClient,
                    taskScheduler,
                    httpScanner,
                    getAddressFromString(endPointConfiguration.getIpAddresses()));

            endPoint.addEndPointDetail("id",endPointConfiguration.getId());
            endPoint.withBasicAuthentication(endPointConfiguration.getUsername(),endPointConfiguration.getPassword());
            endPoints.put(endPointConfiguration.getId(),endPoint);

            initializeEndPoint(endPoint);

        } else {
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE, "Not adding an HTTPConnector with name "+endPointConfiguration.getName()
                        + "and id "+endPointConfiguration.getId()+" in EndPointManager as there is already one with the " +
                        "same Id");
            }
        }
    }

    private void initializeEndPoint(HTTPConnector endPoint){
        try {
            endPoint.init();
        } catch (InitializationFailedException e){
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE, "InitializationFailedException wheile initializing HTTPConnector" +
                        "(Connector is still added in EndPointManager) with name: " +endPoint.getName()+
                        " ip: "+ Arrays.toString(endPoint.getAddresses())+
                        " context path: "+endPoint.getContextPath()+
                        " port: "+endPoint.getPort()+" Reason: "+e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
    }

    private InetAddress[] getAddressFromString(String addresses){
        List<String> addressList =  Strings.splitter(',').trimTokens().split((addresses==null)?"":addresses);

        List<InetAddress> ipList = new ArrayList();
        for (int i=0;i<addressList.size(); i++){
            try {
                ipList.add(InetAddress.getByName(addressList.get(i)));
            }catch (UnknownHostException e){
                getLogger().error(MODULE, "Not adding "+addressList.get(i)+" into the IP list Reason: "+e.getMessage());
                getLogger().trace(e);
            }
        }

        return ipList.stream().toArray(InetAddress[]::new);
    }

    public HTTPConnector getEndPoint(String id) {
        return endPoints.get(id);
    }

    public void removeEndPoint(String id){
        endPoints.remove(id);
    }

    public void stop() {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Stopping End Point Manager");
        }

        endPoints.values().forEach(httpConnector -> {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Stoping connector: " + httpConnector.getName());
            }
            httpConnector.stop();
        });


        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "End Point Manager stopped successfully");
        }
    }
}
