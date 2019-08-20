package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.nvsmx.commons.model.sessionmanager.NetServerInstanceData;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import com.elitecore.nvsmx.remotecommunications.data.ServerInstanceGroupData;

import java.util.concurrent.Future;

/**
 * Created by aditya on 9/13/16.
 */
public interface EndPoint extends ESCommunicator{

    public <V> Future<RMIResponse<V>> submit(RemoteMethod method) ;

    public ServerInformation getGroupData();
    public ServerInformation getInstanceData();

}
