package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.nvsmx.TestableEndPoint;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import org.mockito.Mockito;

/**
 * Created by aditya on 6/26/17.
 */
public class TestableNVSMXEndPoint extends TestableEndPoint implements NVSMXEndPoint{

     private String status = "";//EndPointStatus.SHUT_DOWN.getStatus();

    public TestableNVSMXEndPoint(ServerInformation groupData, ServerInformation instanceData){
        super(groupData,instanceData);
    }

    public TestableNVSMXEndPoint() {
        super();
    }

    public static TestableNVSMXEndPoint spy() {
        return Mockito.spy(new TestableNVSMXEndPoint());
    }

    @Override
    public void markShutDown() {
         status = EndPointStatus.SHUT_DOWN.getVal();
    }

    @Override
    public void markStarted() {
       status = EndPointStatus.STARTED.getVal();
    }

    @Override
    public String getStatus() {
        return status;
    }
}
