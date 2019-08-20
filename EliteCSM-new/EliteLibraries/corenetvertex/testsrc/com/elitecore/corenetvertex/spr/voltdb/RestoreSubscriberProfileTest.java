package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
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
import org.mockito.Mockito;
import org.voltdb.InProcessVoltDBServer;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.elitecore.corenetvertex.spr.voltdb.SPRTestUtil.createProfile;
import static com.elitecore.corenetvertex.spr.voltdb.VoltDBTestUTIL.generateResponseOtherThanSuccess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@Ignore
@RunWith(HierarchicalContextRunner.class)
public class RestoreSubscriberProfileTest {


    private static InProcessVoltDBServer voltServer;
    private VoltDBSPInterface voltDBSPInterface;
    private SubscriberProfileData profileData;
    private static final String UNKNOWN_SUBSCRIBER = "UNKNOWNSUBSCRIBER";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();



    @BeforeClass
    public static void beforeClass() {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        // Add SQL File containing Create Table and Create Procedure query
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
                voltDBSPInterface.restoreProfile(profileData.getSubscriberIdentity());
                verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
            } catch (Exception e) {
                throw e;
            }
        }

        /*@Test
        public void clientResponseStatusISConnectionLost() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_LOST);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.restoreProfile(profileData.getSubscriberIdentity());
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), anyString(), anyString());
        }


        @Test
        public void clientResponseStatusISCONNECTION_TIMEOUT() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_TIMEOUT);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.restoreProfile(profileData.getSubscriberIdentity());
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.QUERY_TIME_OUT), anyString(), anyString());
        }*/


    }


    public class GenerateOperationFailedExceptionWhen {
        /*@Test
        public void clientResponseStatusISNotSuccess() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(generateResponseOtherThanSuccess());
            when(spiedVoltDBClient.callProcedure(anyString(),anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.restoreProfile(profileData.getSubscriberIdentity());
        }*/


        /*@Test
        public void voltDBClientGenerateNoConnetionException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString())).thenThrow(NoConnectionsException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.restoreProfile(profileData.getSubscriberIdentity());
        }*/


        @Test
        public void voltDBClientGenerateIOException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString())).thenThrow(IOException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.restoreProfile(profileData.getSubscriberIdentity());
        }

        @Test
        public void voltDBClientGenerateProcCallException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(anyString(),anyString())).thenThrow(ProcCallException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.restoreProfile(profileData.getSubscriberIdentity());
        }
    }

    @Test
    public void returnZeroIfSubscriberDoesntExistInRepository() throws OperationFailedException {
        SPRInfo existProfile = createProfile("aditya").getSPRInfo();
        voltDBSPInterface.addProfile(existProfile);
        SPRInfo unknownSubscriber = createProfile(UNKNOWN_SUBSCRIBER).getSPRInfo();
        Assert.assertEquals(0,voltDBSPInterface.restoreProfile(unknownSubscriber.getSubscriberIdentity()));
    }

    @Test
    public void canRestoreOnlyDeletedMarkProfile() throws OperationFailedException {
        SPRInfo existProfile = createProfile("aditya").getSPRInfo();
        voltDBSPInterface.addProfile(existProfile);
        Assert.assertEquals(0,voltDBSPInterface.restoreProfile(existProfile.getSubscriberIdentity()));
    }

    @Test
    public void restoreSubscriberToInActiveStateIfExistAndInDeletedState() throws OperationFailedException, DBDownException {
        SPRInfo existProfile = createProfile("aditya").getSPRInfo();
        voltDBSPInterface.addProfile(existProfile);
        voltDBSPInterface.markForDeleteProfile(existProfile.getSubscriberIdentity());
        voltDBSPInterface.restoreProfile(existProfile.getSubscriberIdentity());
        SPRInfo fetchedProfile = voltDBSPInterface.getProfile(existProfile.getSubscriberIdentity());
        assertTrue(SubscriberStatus.INACTIVE.name().equalsIgnoreCase(fetchedProfile.getStatus()));
    }

    public class RestoringMultipleSubscriberProfile{
        @Test
        public void returnsEmptyMapIfEmptyListOfSubscriberIsPassed() throws OperationFailedException {
            Assert.assertTrue(voltDBSPInterface.restoreProfile(new ArrayList<String>()).isEmpty());
        }

        @Test
        public void returnsMapWithSubscriberIdentityWithRestoredStatus() throws OperationFailedException{
             voltDBSPInterface.addProfile(createProfile("aditya").getSPRInfo());
             voltDBSPInterface.addProfile(createProfile("aditya_1").getSPRInfo());
             voltDBSPInterface.markForDeleteProfile("aditya");

            List<String> subscriberIdentities = Arrays.asList(new String[]{"aditya", "aditya_1",UNKNOWN_SUBSCRIBER});
            Map<String, Integer> stringIntegerMap = voltDBSPInterface.restoreProfile(subscriberIdentities);
            Assert.assertTrue(stringIntegerMap.get("aditya")==1);
            Assert.assertTrue(stringIntegerMap.get("aditya_1")==0);
            Assert.assertTrue(stringIntegerMap.get(UNKNOWN_SUBSCRIBER)==0);
        }

        @Test
        public void throwsOperationFailedExceptionIfExceptionOccurWhileRestoringAnySubscriber() throws OperationFailedException {
            VoltDBSPInterface spiedVoltDbInterface = spy(voltDBSPInterface);
            when(spiedVoltDbInterface.restoreProfile(Mockito.anyString())).thenThrow(new OperationFailedException(""));
            expectedException.expect(OperationFailedException.class);
            List<String> subscriberIdentities = Arrays.asList(new String[]{"aditya", "aditya_1"});
            spiedVoltDbInterface.restoreProfile(subscriberIdentities);
        }
    }

}
