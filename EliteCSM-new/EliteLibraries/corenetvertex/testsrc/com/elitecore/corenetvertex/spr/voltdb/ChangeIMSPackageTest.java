package com.elitecore.corenetvertex.spr.voltdb;


import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.TimeSourceChain;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.voltdb.InProcessVoltDBServer;
import org.voltdb.client.ProcCallException;

import java.io.IOException;

import static com.elitecore.corenetvertex.spr.voltdb.SPRTestUtil.createProfile;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@Ignore
@RunWith(HierarchicalContextRunner.class)
public class ChangeIMSPackageTest {

    private static InProcessVoltDBServer voltServer;
    private VoltDBSPInterface voltDBSPInterface;
    private SubscriberProfileData profileData;
    private static final String UNKNOWN_SUBSCRIBER = "UNKNOWNSUBSCRIBER";
    private static final String UPDATED_IMS_PACKAGE = "IMS_PACKAGE_UPDATE";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();



    @BeforeClass
    public static void beforeClass() {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");
    }

    @Before
    public void setUp() throws Exception {
        profileData = createProfile();
        voltDBSPInterface = new VoltDBSPInterface(null, new DummyVoltDBClient(voltServer.getClient()), new FixedTimeSource());
    }


    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    @AfterClass
    public static void afterClass() {
        voltServer.shutdown();
    }


    public class GenerateAlertWhen {

        @Test
        public void queryRespnseTimeisHigh() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            TimeSourceChain timeSource = new TimeSourceChain(2, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);

            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, new DummyVoltDBClient(voltServer.getClient()), timeSource);
            try {
                voltDBSPInterface.changeIMSpackage(profileData.getSubscriberIdentity(),UPDATED_IMS_PACKAGE);
                verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
            } catch (Exception e) {
                throw e;
            }
        }

        /*@Test
        public void clientResponseStatusISConnectionLost() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_LOST);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString(),anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.changeIMSpackage(profileData.getSubscriberIdentity(),UPDATED_IMS_PACKAGE);
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), anyString(), anyString());
        }


        @Test
        public void clientResponseStatusISCONNECTION_TIMEOUT() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_TIMEOUT);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString(),anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.changeIMSpackage(profileData.getSubscriberIdentity(),UPDATED_IMS_PACKAGE);
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.QUERY_TIME_OUT), anyString(), anyString());
        }*/


    }


    public class GenerateOperationFailedExceptionWhen {
        /*@Test
        public void clientResponseStatusISNotSuccess() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(mockedClientResponse.getStatus()).thenReturn(generateResponseOtherThanSuccess());
            when(spiedVoltDBClient.callProcedure(anyString(),anyString(),anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.changeIMSpackage(profileData.getSubscriberIdentity(),UPDATED_IMS_PACKAGE);
        }*/


        /*@Test
        public void voltDBClientGenerateNoConnetionException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString(),anyString())).thenThrow(NoConnectionsException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.changeIMSpackage(profileData.getSubscriberIdentity(),UPDATED_IMS_PACKAGE);
        }*/


        @Test
        public void voltDBClientGenerateIOException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString(),anyString())).thenThrow(IOException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.changeIMSpackage(profileData.getSubscriberIdentity(),UPDATED_IMS_PACKAGE);
        }

        @Test
        public void voltDBClientGenerateProcCallException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString(),anyString())).thenThrow(ProcCallException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.changeIMSpackage(profileData.getSubscriberIdentity(),UPDATED_IMS_PACKAGE);
        }
    }


    @Test
    public void returnZeroIfSubscriberDoesNotExistInRepository() throws OperationFailedException {
        SPRInfo existProfile = createProfile("aditya").getSPRInfo();
        voltDBSPInterface.addProfile(existProfile);
        Assert.assertEquals(0,voltDBSPInterface.changeIMSpackage(UNKNOWN_SUBSCRIBER,UPDATED_IMS_PACKAGE));
    }

    @Test
    public void canChangeIMSPackageWhenSubscriberExist() throws OperationFailedException, DBDownException {
        SPRInfo existProfile = createProfile("aditya").getSPRInfo();

        existProfile.setImsPackage("IMS_PACKAGE");
        voltDBSPInterface.addProfile(existProfile);
        Assert.assertEquals(1,voltDBSPInterface.changeIMSpackage(existProfile.getSubscriberIdentity(),UPDATED_IMS_PACKAGE));
        Assert.assertTrue(UPDATED_IMS_PACKAGE.equalsIgnoreCase(voltDBSPInterface.getProfile(existProfile.getSubscriberIdentity()).getImsPackage()));
    }

}
