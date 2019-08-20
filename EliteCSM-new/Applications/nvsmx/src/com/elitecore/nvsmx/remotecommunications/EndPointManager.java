package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import com.elitecore.nvsmx.remotecommunications.data.ServerInstanceInformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.util.commons.collection.Lists.copy;

/**
 * Created by aditya on 4/18/17.
 */
public class EndPointManager {
    private static final String MODULE = "END-POINT-MGR";
    private static final String SHUTDOWN = "SHUTDOWN";
    private static final String SHUTDOWN_IN_PROGRESS = "SHUTDOWN_IN_PROGRESS";
    private static final String INITIALIZING = "INITIALIZING";
    private static final String STARTED = "STARTED";
    private static final String STOP = "STOP";
    private String status = SHUTDOWN;

    private PDContextInformation localPdContextInformation;
    private EndPointStore endPointStore;



    private EndPointFactory endPointFactory;
    private NetvertexInstanceGroupConfiguration netvertexInstanceGroupConfiguration;
    private NvsmxInstanceConfiguration nvsmxInstanceConfiguration;
    private TaskScheduler taskScheduler;


    private static final EndPointManager END_POINT_MANAGER;

    public EndPointManager() {
        endPointStore = new EndPointStore();
        status = STOP;
    }

    static {
        END_POINT_MANAGER = new EndPointManager();
    }


    public static EndPointManager getInstance(){
        return END_POINT_MANAGER;
    }

    public synchronized void init(EndPointFactory endPointFactory,PDContextInformation localPdContextInformation,NetvertexInstanceGroupConfiguration netvertexInstanceGroupConfiguration, NvsmxInstanceConfiguration nvsmxInstanceConfiguration,TaskScheduler taskScheduler) {
        if(STARTED.equals(status)){
            return;
        }
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Initializing End Point manager");
        }
        this.endPointFactory = endPointFactory;
        this.localPdContextInformation = localPdContextInformation;
        this.netvertexInstanceGroupConfiguration = netvertexInstanceGroupConfiguration;
        this.nvsmxInstanceConfiguration = nvsmxInstanceConfiguration;
        this.taskScheduler = taskScheduler;
        status = INITIALIZING;
        readNetvertexEndPoint(endPointStore);
        readNvsmxEndPoint(endPointStore);
        status = STARTED;

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE,"End Point manager initialization completed");
        }
    }

    private void readNvsmxEndPoint(EndPointStore endPointStore) {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Initializing PD End Points");
        }

        try {

            nvsmxInstanceConfiguration.readConfiguration();
            List<PDContextInformation> pdContextInformationList = nvsmxInstanceConfiguration.getPdContextInformations();
            if(Collectionz.isNullOrEmpty(pdContextInformationList)){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "End Point for PD Contexts can't be created.Reason: No PD Context found");
                }
                return;
            }
            createAndStoreNVSMXEndPoints(pdContextInformationList, endPointStore);
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE,"Initializing PD End Points completed");
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing NVSMX End Point.Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    private void createAndStoreNVSMXEndPoints(List<PDContextInformation> pdContextInformationList, EndPointStore endPointStore) throws InitializationFailedException {
        for (PDContextInformation pdContext : pdContextInformationList) {

            if (localPdContextInformation.getId().equals(pdContext.getId())) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping PD Context with ID: " + pdContext.getId() + " to initialize as End Point. Reason: This id refers to the current PD context ");
                }
                continue;
            }

           if(contains(this.endPointStore.getNvsmxEndPoints(), pdContext.getId())){
                continue;
            }
            endPointStore.addNVSMXEndPointList(endPointFactory.createNVSMXEndPoint(pdContext,localPdContextInformation.getId()));
        }
    }


    private void readNetvertexEndPoint(EndPointStore endPointStore) {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Initializing NetVertex End Points");
        }
        try {

            netvertexInstanceGroupConfiguration.readConfiguration();

            List<ServerGroupData> serverGroups = netvertexInstanceGroupConfiguration.getServerInstanceGroupDatas();
            if (Collectionz.isNullOrEmpty(serverGroups)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "End Point for NetVertex Instance can't be created.Reason: No netvertex instance found.");
                }
                return;
            }

            createAndStoreNetVertexEndPoints(serverGroups, endPointStore);

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Initializing NetVertex End Points completed");
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while initializing NetVertex Instances.Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    public synchronized void shutdown() {
        this.status = SHUTDOWN_IN_PROGRESS;
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Shutting down End Point Manager");
        }
        for (EndPoint endPoint : endPointStore.getNetvertexEndPoints()) {
            endPoint.stop();
        }

        for (EndPoint endPoint : endPointStore.getNvsmxEndPoints()) {
            endPoint.stop();
        }

        this.status = SHUTDOWN;
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "End Point Manager has been shut down");
        }
    }





    public synchronized void reload() {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Reloading End Points");
        }
        /*Steps
        * 1. Read all the end points
        * 2. Compare with existing end points
        * 3. if existing end point have same id as newly created end point then skip that end point
        *    else
        *   add new end points to the list
        * 4. Modification should be on newly created list not on existing one
        **/


        //add existing information in newly created list


        //read End points
        try {

            EndPointStore reloadEndPointStore = new EndPointStore();
            readNetvertexEndPoint(reloadEndPointStore);
            readNvsmxEndPoint(reloadEndPointStore);
            List<PDContextInformation> pdContextInformations = nvsmxInstanceConfiguration.getPdContextInformations();


            List<ServerGroupData> serverInstanceGroupDatas = netvertexInstanceGroupConfiguration.getServerInstanceGroupDatas();

            // retained from unchanged endpoints
            Predicate<EndPoint> retainedNVSMXEndPointPredicate = createNVSMXEndPointPredicate(pdContextInformations);
            Predicate<EndPoint> retainNetVertexEndPointPredicate = createNetVertexEndPointPredicate(serverInstanceGroupDatas);

            List<NVSMXEndPoint> retainedNVSMXEndPointList = copy(this.endPointStore.getNvsmxEndPoints(), retainedNVSMXEndPointPredicate);
            List<EndPoint> retainedNetVertexEndPointList = copy(this.endPointStore.getNetvertexEndPoints(), retainNetVertexEndPointPredicate);


            List<EndPoint> deletedEndPointList = copy(this.endPointStore.getNetvertexEndPoints(), NotPredicate.of(retainNetVertexEndPointPredicate));
            deletedEndPointList.addAll(copy(this.endPointStore.getNvsmxEndPoints(),NotPredicate.of(retainedNVSMXEndPointPredicate)));


            logDeletedEndPoints(deletedEndPointList);


            reloadEndPointStore.addIfAbsentNvsxmEndPoint(retainedNVSMXEndPointList);
            reloadEndPointStore.addIfAbsentNetVertexEndPoint(retainedNetVertexEndPointList);
            this.endPointStore = reloadEndPointStore;

             //DELETED END POINT SHOULD BE SHUTDOWN
            if(Collectionz.isNullOrEmpty(deletedEndPointList) == false ){
                taskScheduler.scheduleSingleExecutionTask(new EndPointsShutDownTask(deletedEndPointList, new Timestamp(System.currentTimeMillis())));
            }


            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Reloading End Points completed");
            }
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while reloading End Points. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }


    }

    private void logDeletedEndPoints(List<EndPoint> deletedEndPointList) {
        if(Collectionz.isNullOrEmpty(deletedEndPointList) == false){
           if(getLogger().isWarnLogLevel()){
             for(EndPoint endPoint : deletedEndPointList){
                   getLogger().warn(MODULE,"Removing End Point "+endPoint.getName()+".Reason: end point not found while reloading");
             }
           }
        }
    }


    private Predicate<EndPoint> createNVSMXEndPointPredicate(final List<PDContextInformation> list) {
        return nvsmxEndPoint -> {
            for (PDContextInformation pdContextInformation : list) {
                if (nvsmxEndPoint.getInstanceData().getId().equals(pdContextInformation.getId())){
                    return true;
                }
            }

            return false;
        };
    }
    private Predicate<EndPoint> createNetVertexEndPointPredicate(final List<ServerGroupData> serverGroupDatas) {
        return netvertexEndPoint -> {
            for(ServerGroupData serverInstanceGroupData : serverGroupDatas ){
                for(ServerInstanceData serverInstanceData : serverInstanceGroupData.getServerInstances() ){
                    if(((ServerInstanceInformation)netvertexEndPoint.getInstanceData()).getRestApiUrl().equalsIgnoreCase(serverInstanceData.getRestApiUrl()) &&
                            serverInstanceData.getId().equals(netvertexEndPoint.getInstanceData().getId())){
                        return true;
                    }
                }
            }
            return false;
        };
    }


    private void createAndStoreNetVertexEndPoints(@Nonnull List<ServerGroupData> serverGroups, EndPointStore endPointStore) throws InitializationFailedException {

        for (ServerGroupData serverGroupData : serverGroups) {
            for (ServerInstanceData serverInstanceData : serverGroupData.getServerInstances()) {
                if(contains(this.endPointStore.getNetvertexEndPoints(), serverInstanceData)) {
                        continue;
                }
                endPointStore.addNetVertexEndPoint(endPointFactory.createEndPoint(serverGroupData, serverInstanceData));
            }
        }
    }

    private boolean contains(List<? extends EndPoint> endPoints, ServerInstanceData serverInstanceData) {

        for(EndPoint endPoint : endPoints) {
            if (endPoint.getInstanceData().getId().equals(serverInstanceData.getId())
                    &&  endPoint.getInstanceData().getName().equals(serverInstanceData.getName())
                    && endPoint.getGroupData().getName().equals(serverInstanceData.getServerGroupServerInstanceRelData().getServerGroupData().getName())
                    && ((ServerInstanceInformation)endPoint.getInstanceData()).getRestApiUrl().equalsIgnoreCase(serverInstanceData.getRestApiUrl())) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping creating EndPoint for instance with name: " + endPoint.getInstanceData().getName()
                            + ".Reason: end point already exist");
                }
                return true;
            }
        }
        return false;
    }

    private boolean contains(List<? extends EndPoint> endPoints, String id) {

        for(EndPoint endPoint : endPoints) {
            if (endPoint.getInstanceData().getId().equals(id)) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping creating EndPoint for instance with name: " + endPoint.getInstanceData().getName()
                            + ".Reason: end point already exist");
                }
                return true;
            }
        }
        return false;
    }


    public @Nullable NVSMXEndPoint getPDInstanceById(String id) {
        return endPointStore.getIdToPDEndPoint().get(id);
    }

    public @Nonnull List<EndPoint> getAllNetvertexEndPoint() {
        return endPointStore.getNetvertexEndPoints();
    }

    public @Nonnull List<NVSMXEndPoint> getALLNvsmxEndPoints() {
        return endPointStore.getNvsmxEndPoints();
    }

    public @Nullable EndPoint getByServerCode(String id) {
        return endPointStore.getNetEngineServerCodeToEndPoint().get(id);
    }

    private class EndPointsShutDownTask extends BaseSingleExecutionAsyncTask {

        @Nonnull private final List<EndPoint> endPoints;
        @Nonnull private final Timestamp reloadedTimestamp;

        public EndPointsShutDownTask(@Nonnull List<EndPoint> endPoints,@Nonnull Timestamp reloadedTimestamp) {
            this.endPoints = endPoints;
            this.reloadedTimestamp = reloadedTimestamp;
        }

        @Override
        public long getInitialDelay() {
            return 1;
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MINUTES;
        }

        @Override
        public void execute(AsyncTaskContext context) {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Shutting down deleted end points for reload operation on: " + reloadedTimestamp);
            }

            for (EndPoint endPoint : endPoints) {
                endPoint.stop();
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Shutting down end points completed");
            }
        }
    }

    public void markShutDown(@Nonnull String id){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "mark shutdown called for end point with id: " + id);
        }
        NVSMXEndPoint endPoint = getPDInstanceById(id);
        if (endPoint == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "No end point found with given id: " + id);
            }
            return;
        }
        if (EndPointStatus.SHUT_DOWN.getVal().equals(endPoint.getStatus())) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping marking end point as shut down. Reason: end point already is in shut down state");
            }
            return;
        }

        endPoint.markShutDown();
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "End point has been marked as shut down");
        }
    }

    public void markStarted(@Nonnull String id){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "mark started called for end point with id: " + id);
        }
        NVSMXEndPoint endPoint = getPDInstanceById(id);
        if (endPoint == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "No end point found with given id: " + id);
            }
            return;
        }
        if (EndPointStatus.STARTED.getVal().equals(endPoint.getStatus())) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Skipping marking end point as started. Reason: end point already started");
            }
            return;
        }
        endPoint.markStarted();
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "End point has been marked as started");
        }

    }

}