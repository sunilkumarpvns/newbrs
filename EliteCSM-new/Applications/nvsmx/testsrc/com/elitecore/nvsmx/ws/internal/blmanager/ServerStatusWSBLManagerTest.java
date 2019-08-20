 package com.elitecore.nvsmx.ws.internal.blmanager;

import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.NVSMXEndPoint;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.TestableNVSMXEndPoint;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aditya on 6/26/17.
 */
public class ServerStatusWSBLManagerTest {

    private EndPointManager endPointManager;
    private ServerStatusWSBLManager serverStatusWSBLManager;
    private String END_POINT_ID = "123456";



    @Before
    public void setup(){
           endPointManager = Mockito.mock(EndPointManager.class);
           serverStatusWSBLManager = new ServerStatusWSBLManager(endPointManager);
    }

    @Test
    public void testAnnouncingShutDown() throws Exception {
           NVSMXEndPoint endPoint = mock(NVSMXEndPoint.class);
           when(endPointManager.getALLNvsmxEndPoints()).thenReturn(Arrays.asList(endPoint));
           ArgumentCaptor<RemoteMethod> remoteCapture = ArgumentCaptor.forClass(RemoteMethod.class);
           serverStatusWSBLManager.announcingShutDown(END_POINT_ID);
           verify(endPoint).submit(remoteCapture.capture());
           RemoteMethod capture = remoteCapture.getValue();
           verify(endPoint,times(1)).submit(capture);
           Assert.assertEquals(capture.getName(),RemoteMethodConstant.NVSMX_REMOTE_SHUT_DOWN);
           Assert.assertEquals(capture.getArgument(),END_POINT_ID);
    }

    @Test
    public void testAnnouncingWakeUp() throws Exception {

    }
}