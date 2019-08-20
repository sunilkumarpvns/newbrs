package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import com.elitecore.nvsmx.remotecommunications.data.ServerInstanceGroupData;

import javax.annotation.Nullable;

/**
 * Created by harsh on 7/20/16.
 */
public abstract class RMIResponse<T> {


    public abstract @Nullable T getResponse();
    public abstract boolean isSuccess();
    public abstract boolean isErrorOccurred();
    public abstract @Nullable Exception getError();

    public ServerGroupData getInstanceGroupData() {
        return ServerInstanceGroupData.from(instanceGroupData);
    }

    public ServerInstanceData getInstanceData() {
        ServerInstanceData netServerInstanceData = new ServerInstanceData();
        netServerInstanceData.setName(instanceData.getName());
        netServerInstanceData.setId(instanceData.getId());
        return netServerInstanceData;
    }

    private ServerInformation instanceGroupData;
    private ServerInformation instanceData;



    public RMIResponse(ServerInformation instanceGroupData, ServerInformation instanceData) {
        this.instanceGroupData = instanceGroupData;
        this.instanceData = instanceData;
    }

}
