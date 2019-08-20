package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.nvsmx.remotecommunications.exception.CommunicationException;

import javax.annotation.Nonnull;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by aditya on 4/18/17.
 */
public class SingleInstanceRMIGroup implements RMIGroup  {
    private final String MODULE;
    @Nonnull private String groupId;
    @Nonnull private EndPoint primaryRMIInstance;

    public SingleInstanceRMIGroup(@Nonnull String groupId, @Nonnull EndPoint primaryRMIInstance){
        this.MODULE="SINGLE-RMI-GRP-" + groupId;
        this.groupId = groupId;
        this.primaryRMIInstance = primaryRMIInstance;
    }



    @Override
    public <V> Future<RMIResponse<V>> call(RemoteMethod method) {
        if(primaryRMIInstance.isAlive() == false) {
            return createErrorRMIResponseForPrimary();
        }
        return primaryRMIInstance.submit(method);
    }

    @Override
    public <V> RMIResponse<V> call(RemoteMethod method, long time, TimeUnit timeUnit) {
        try {
            final Future<RMIResponse<V>> call = call(method);
            return  call.get(time, timeUnit);
        } catch (Exception e) {
            return new ErrorRMIResponse<V>(e, primaryRMIInstance.getGroupData(), primaryRMIInstance.getInstanceData());
        }
    }


    @Override
    public <V> RMIResponse<V> call(RemoteMethod method, long time, TimeUnit timeUnit, String serverInstanceId) {
        try {
            final Future<RMIResponse<V>> call = call(method);
            return  call.get(time, timeUnit);
        } catch (Exception e) {
            return new ErrorRMIResponse<V>(e, primaryRMIInstance.getGroupData(), primaryRMIInstance.getInstanceData());
        }

    }

    public String getName() {
        return  "RMI-"+ groupId ;
    }

    @Override
    public String id() {
        return groupId;
    }

    private <V> SettableFuture<RMIResponse<V>> createErrorRMIResponseForPrimary() {
        SettableFuture<RMIResponse<V>> errorFuturePrimary = SettableFuture.create();
        errorFuturePrimary.set(new ErrorRMIResponse<V>(new CommunicationException(primaryRMIInstance.getName() + " not live from group for id: " + groupId), primaryRMIInstance.getGroupData(), primaryRMIInstance.getInstanceData()));
        return errorFuturePrimary;
    }

}
