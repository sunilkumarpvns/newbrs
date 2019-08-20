package com.elitecore.nvsmx.remotecommunications;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.util.commons.collection.Maps.copy;

/**
 * Created by aditya on 4/14/17.
 */
public class RMIGroupManager {
    private static final String MODULE = "RMI-GRP-MGR";
    private static final String INITIALIZING = "INITIALIZING";
    private static final String STARTED = "STARTED";
    private static final String STOP = "STOP";

    private @Nonnull RMIGroupStore rmiGroupStore;
    private @Nonnull RMIGroupFactory rmiGroupFactory;
    private @Nonnull String status;
    private @Nonnull NetvertexInstanceGroupConfiguration netvertexInstanceGroupConfiguration;


    private static final RMIGroupManager RMI_GROUP_MANAGER;


    private RMIGroupManager() {
        rmiGroupStore = new RMIGroupStore();
        status = STOP;
    }

    static {
        RMI_GROUP_MANAGER = new RMIGroupManager();
    }

    public static RMIGroupManager getInstance() {
        return RMI_GROUP_MANAGER;
    }

    public synchronized void init(@Nonnull RMIGroupFactory rmiGroupFactory, @Nonnull NetvertexInstanceGroupConfiguration netvertexInstanceGroupConfiguration){

        if (STARTED.equals(status)) {
            return;
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Initializing RMI group manager");
        }


        this.rmiGroupFactory = rmiGroupFactory;
        this.netvertexInstanceGroupConfiguration = netvertexInstanceGroupConfiguration;

        status = INITIALIZING;
        readNetvertexInstanceGroup(rmiGroupStore);
        status = STARTED;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "RMI group manager initialization completed successfully");
        }

    }


    private void readNetvertexInstanceGroup(RMIGroupStore rmiGroupStore) {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Initializing NetVertex RMI Groups");
        }

        netvertexInstanceGroupConfiguration.readConfiguration();

        List<ServerGroupData> serverInstanceGroupDatas = netvertexInstanceGroupConfiguration.getServerInstanceGroupDatas();
        if (Collectionz.isNullOrEmpty(serverInstanceGroupDatas)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "RMI Group for NetVertex Server Group can't be created.Reason: No instance group found");
            }
            return;
        }
        for (ServerGroupData serverGroupData : serverInstanceGroupDatas) {
            createAndStoreNetVertexRMIGroup(serverGroupData, rmiGroupStore);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Initializing NetVertex RMI Groups completed");
        }
    }

    private void createAndStoreNetVertexRMIGroup(ServerGroupData serverInstanceGroupData, RMIGroupStore rmiGroupStore) {


        ServerInstanceData primaryServerInstanceData = serverInstanceGroupData.getServerInstances().get(0);
        ServerInstanceData secondaryServerInstanceData = null;
        RMIGroup rmiGroup;

        if (serverInstanceGroupData.getServerInstances().size() > 1) {
            secondaryServerInstanceData = serverInstanceGroupData.getServerInstances().get(1);
            rmiGroup = rmiGroupFactory.create(primaryServerInstanceData, secondaryServerInstanceData);
        } else {
            rmiGroup = rmiGroupFactory.create(serverInstanceGroupData, primaryServerInstanceData);
        }

        rmiGroupStore.add(rmiGroup, primaryServerInstanceData, secondaryServerInstanceData);
    }

    public @Nonnull List<RMIGroup> getNetvertexInstanceRMIGroups() {
        return rmiGroupStore.getNetvertexInstanceRMIGroups();
    }

    public @Nullable RMIGroup getRMIGroupFromServerCode(String netServerCode) {
        return rmiGroupStore.getNetEngineServerCodeToGroup().get(netServerCode);
    }

    public synchronized void reload(){

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Reloading rmi group manager");
        }
        if(netvertexInstanceGroupConfiguration==null){
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE,"RMI Interface is not Initialized. Therefore ignoring Reload operation.");
            }
            return;
        }

        RMIGroupStore rmiGroupStoreLocal = new RMIGroupStore();
        readNetvertexInstanceGroup(rmiGroupStoreLocal);

        List<ServerGroupData> serverInstanceGroupDatas = netvertexInstanceGroupConfiguration.getServerInstanceGroupDatas();
        Predicate<Map.Entry<String, RMIGroup>> retainedPredicate = createRetainedPredicate(serverInstanceGroupDatas);

        Map<String, RMIGroup> retainedRMIGroupMap = copy(this.rmiGroupStore.getNetEngineServerCodeToGroup(), retainedPredicate);
        rmiGroupStoreLocal.addIfAbsent(retainedRMIGroupMap);
        this.rmiGroupStore = rmiGroupStoreLocal;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading rmi group manager completed");
        }

    }

    private Predicate<Map.Entry<String, RMIGroup>> createRetainedPredicate(final List<ServerGroupData> serverInstanceGroupDataList){
            return entry -> {
                for(ServerGroupData serverInstanceGroupData : serverInstanceGroupDataList){
                    RMIGroup rmiGroup = entry.getValue();
                    if(rmiGroup.id().equals(serverInstanceGroupData.getId()) && rmiGroup.getName().equals(serverInstanceGroupData.getName())){
                        return true;
                    }
                }
                return false;
            };
    }

}
