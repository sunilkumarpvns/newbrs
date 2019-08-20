package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;

/**
 * Created by aditya on 4/14/17.
 */
public class RMIGroupFactory {

    private EndPointManager endPointManager;

    public RMIGroupFactory(EndPointManager endPointManager) {
        this.endPointManager = endPointManager;
    }

    public RMIGroup create(PDContextInformation pdContext){
        return new SingleInstanceRMIGroup(pdContext.getId(), endPointManager.getPDInstanceById(pdContext.getId()));
    }

    public RMIGroup create(ServerGroupData serverInstanceGroupData, ServerInstanceData netServerInstanceData){
        return new SingleInstanceRMIGroup(serverInstanceGroupData.getId(), endPointManager.getByServerCode(netServerInstanceData.getId()));
    }

    public RMIGroup create(ServerInstanceData primaryServerInstanceData, ServerInstanceData secondaryServerInstanceData){

        EndPoint primaryRMIInstance = endPointManager.getByServerCode(primaryServerInstanceData.getId());
        EndPoint secondaryRMIInstance = endPointManager.getByServerCode(secondaryServerInstanceData.getId());

        FailOverRMIGroup failOverRMIGroup = new FailOverRMIGroup(primaryRMIInstance,secondaryRMIInstance);
        failOverRMIGroup.init();

        return failOverRMIGroup;
    }
}
