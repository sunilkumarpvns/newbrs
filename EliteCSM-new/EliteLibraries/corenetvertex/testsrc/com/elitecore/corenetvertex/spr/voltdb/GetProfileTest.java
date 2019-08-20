package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.tests.PrintMethodRule;
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
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.voltdb.InProcessVoltDBServer;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;

import java.io.IOException;

import static com.elitecore.corenetvertex.spr.voltdb.VoltDBTestUTIL.generateResponseOtherThanSuccess;
import static com.elitecore.corenetvertex.spr.voltdb.VoltDBTestUTIL.generateResponseOtherThanSuccessForGetProfile;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

@Ignore
@RunWith(HierarchicalContextRunner.class)
public class GetProfileTest {

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
        voltServer.runDDLFromString(profileData.insertQuery());
        voltDBSPInterface = new VoltDBSPInterface(null, new DummyVoltDBClient(voltServer.getClient()), new FixedTimeSource());
    }

    private SubscriberProfileData createProfile() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("Chetan")
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withPhone("123456")
                .build();
    }


    public class GenerateAlertWhen {

        @Test
        public void queryRespnseTimeisHigh() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            TimeSourceChain timeSource = new TimeSourceChain(2, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);

            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, new DummyVoltDBClient(voltServer.getClient()), timeSource);
            try {
                voltDBSPInterface.getProfile("101");
                verify(alertListener, only())
                        .generateSystemAlert(anyString(), eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
            } catch (Exception e) {
                throw e;
            }
        }


       /* @Test
        public void clientResponseStatusISConnectionLost() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_LOST);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(DBDownException.class);
            voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), anyString(), anyString());
        }

        @Test
        public void clientResponseStatusISCONNECTION_TIMEOUT() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_TIMEOUT);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(DBDownException.class);
            voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.QUERY_TIME_OUT), anyString(), anyString());
        }*/

    }

    public class GenerateDBDownExceptionWhen {

        /*@Test
        public void clientResponseStatusISSERVER_UNAVAILABLE() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.SERVER_UNAVAILABLE);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(DBDownException.class);
            voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
        }*/

        /*@Test
        public void VoltDBClientGenerateNoConnetionException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), Mockito.anyString())).thenThrow(NoConnectionsException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(DBDownException.class);
            voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
        }*/
    }

    public class GenerateOperationFailedExceptionWhen {
        @Test
        public void VoltDBClientGenerateIOException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), Mockito.anyString())).thenThrow(IOException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
        }

        @Test
        public void VoltDBClientGenerateProcCallException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), Mockito.anyString())).thenThrow(ProcCallException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
        }


        /*@Test
        public void clientResponseStatusISNotSuccessAndNotInCONNECTION_LOSTAndCONNECTION_TIMEOUTAndSERVER_UNAVAILABLE() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(generateResponseOtherThanSuccessForGetProfile());
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), anyString(), anyString());
        }*/


    }


    @Test
    public void returnProfileIfSubscriberExistInRepository() throws OperationFailedException, DBDownException {
        SPRInfo actualProfile = voltDBSPInterface.getProfile(profileData.getSubscriberIdentity());
        SPRInfo expectedProfile = profileData.getSPRInfo();
        expectedProfile.setSprLoadTime(actualProfile.getSprLoadTime());
        assertLenientEquals(expectedProfile, actualProfile);
    }


    @Test
    public void returnNullIfSubscriberNotExistInRepository() throws OperationFailedException, DBDownException {

        SPRInfo actualProfile = voltDBSPInterface.getProfile(UNKNOWN_SUBSCRIBER);
        assertTrue(actualProfile == null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void throughUnsupportedOperationExceptionWhenCreateTransactionCalled() throws OperationFailedException {
         voltDBSPInterface.createTransaction();
    }


    @After
    public void tearDown() {
        voltServer.runDDLFromString("TRUNCATE TABLE TBLM_SUBSCRIBER");
    }

    @AfterClass
    public static void afterClass() {
        voltServer.shutdown();
    }
}
