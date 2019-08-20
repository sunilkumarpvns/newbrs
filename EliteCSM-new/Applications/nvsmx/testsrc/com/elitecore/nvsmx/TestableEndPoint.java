package com.elitecore.nvsmx;

import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.nvsmx.remotecommunications.EndPoint;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import org.junit.Assert;

import javax.annotation.Nullable;
import java.util.concurrent.Future;

/**
 * Created by aditya on 4/14/17.
 */
public class TestableEndPoint extends TestableESCommunicator implements EndPoint {

    private RemoteMethod calledMethod;
    private ServerInformation groupData;
    private ServerInformation instanceData;

    public TestableEndPoint(ServerInformation groupData,ServerInformation instanceData) {
        super(new TaskScheduler() {
            @Nullable
            @Override
            public Future<?> scheduleSingleExecutionTask(@Nullable SingleExecutionAsyncTask task) {
                return null;
            }

            @Nullable
            @Override
            public Future<?> scheduleIntervalBasedTask(@Nullable IntervalBasedTask task) {
                return null;
            }

            @Override
            public void execute(Runnable command) {

            }
        });

        this.groupData = groupData;
        this.instanceData = instanceData;
    }

    public TestableEndPoint() {
        super(new TaskScheduler() {
            @Nullable
            @Override
            public Future<?> scheduleSingleExecutionTask(@Nullable SingleExecutionAsyncTask task) {
                return null;
            }

            @Nullable
            @Override
            public Future<?> scheduleIntervalBasedTask(@Nullable IntervalBasedTask task) {
                return null;
            }

            @Override
            public void execute(Runnable command) {

            }
        });

    }

    @Override
    public <V> Future<RMIResponse<V>> submit(RemoteMethod method)  {
         calledMethod = method;
        return  null;
    }

    @Override
    public ServerInformation getGroupData() {
        return groupData;
    }

    @Override
    public ServerInformation getInstanceData() {
        return instanceData;
    }

    public void submittedWith(RemoteMethod expectedMethod) {
        Assert.assertSame("End point should call with " + expectedMethod, expectedMethod, calledMethod);
    }
}
