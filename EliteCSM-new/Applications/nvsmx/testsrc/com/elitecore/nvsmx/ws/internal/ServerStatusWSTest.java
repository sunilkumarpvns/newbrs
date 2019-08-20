package com.elitecore.nvsmx.ws.internal;

import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.EndPointManagerTestSupport;
import com.elitecore.nvsmx.remotecommunications.NVSMXEndPoint;
import com.elitecore.nvsmx.remotecommunications.TestableNVSMXEndPoint;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.elitecore.corenetvertex.constants.CommonConstants.EMPTY_STRING;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aditya on 6/26/17.
 */


@RunWith(JUnitParamsRunner.class)
public class ServerStatusWSTest {

    private static final String END_POINT_ID = "123456";
    private ServerStatusWS serverStatusWS;
    private EndPointManagerTestSupport endPointManager;


    @Before
    public void setup() {
        endPointManager = EndPointManagerTestSupport.spy();
        serverStatusWS = new ServerStatusWS(endPointManager);
    }

    public Object[][] dataFor_testRemoteShutDownAndRemoteWakeUp() {
        return new Object[][]{
                {null, ResultCode.INPUT_PARAMETER_MISSING.name(),0},
                {EMPTY_STRING, ResultCode.INPUT_PARAMETER_MISSING.name(),0},
                {" ", ResultCode.INPUT_PARAMETER_MISSING.name(),0},
                {END_POINT_ID, ResultCode.SUCCESS.name(),1}
        };
    }


    @Test
    public void test_Calling_GetStatus_Should_Return_Running_Status() throws Exception {
        assertEquals(LifeCycleState.RUNNING.name(), serverStatusWS.getStatus("anyValue"));
    }

    @Test
    @Parameters(method = "dataFor_testRemoteShutDownAndRemoteWakeUp")
    public void test_RemoteShutDown(String inputString, String expectedString, Integer count) throws Exception {
        String resultString = serverStatusWS.remoteShutDown(inputString);
        verify(endPointManager, times(count)).markShutDown(inputString);
        assertEquals(expectedString, resultString);
    }



    @Test
    @Parameters(method = "dataFor_testRemoteShutDownAndRemoteWakeUp")
    public void test_RemoteWakeUp(String inputString, String expectedString, Integer count) throws Exception {
        String resultString = serverStatusWS.remoteWakeUp(inputString);
        verify(endPointManager, times(count)).markStarted(inputString);
        assertEquals(expectedString, resultString);
    }


    @Test
    public void test_EndPointManager_Mark_Started_should_Change_End_Point_Status_Started() throws Exception {
        EndPointManager endPointManager = spy(EndPointManager.getInstance());
        ServerStatusWS serverStatusWS = new ServerStatusWS(endPointManager);
        NVSMXEndPoint nvsmxEndPoint = TestableNVSMXEndPoint.spy();
        when(endPointManager.getPDInstanceById(END_POINT_ID)).thenReturn(nvsmxEndPoint);
        serverStatusWS.remoteWakeUp(END_POINT_ID);
        assertEquals(nvsmxEndPoint.getStatus(), EndPointStatus.STARTED.getVal());
    }

    @Test
    public void test_EndPointManager_ChangeEndPointStatusToShutdown_OnMarkShutDown() throws Exception {
        EndPointManager endPointManager = spy(EndPointManager.getInstance());
        ServerStatusWS serverStatusWS = new ServerStatusWS(endPointManager);
        NVSMXEndPoint nvsmxEndPoint = TestableNVSMXEndPoint.spy();
        when(endPointManager.getPDInstanceById(END_POINT_ID)).thenReturn(nvsmxEndPoint);
        serverStatusWS.remoteShutDown(END_POINT_ID);
        assertEquals(nvsmxEndPoint.getStatus(), EndPointStatus.SHUT_DOWN.getVal());
    }

}