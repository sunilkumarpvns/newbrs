package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.AfterClass;
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
import java.util.EnumMap;

import static com.elitecore.corenetvertex.spr.voltdb.VoltDBTestUTIL.generateResponseOtherThanSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@Ignore
@RunWith(HierarchicalContextRunner.class)
public class UpdateProfileTest {


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

    private SubscriberProfileData createProfile() {
        return createProfile("Chetan");
    }


    private SubscriberProfileData createProfile(String subscriberIdentity) {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity(subscriberIdentity)
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withPhone("123456")
                .build();
    }






    public class GenerateAlertWhen {


        @Test
        public void queryResponseTimeIsHigh() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            TimeSource timeSource = spy(TimeSource.class);
            when(timeSource.currentTimeInMillis()).thenReturn(100l,201l,300l);
            VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, new DummyVoltDBClient(voltServer.getClient()), timeSource));

            try {
                SPRInfoImpl profile = SPRTestUtil.createSubscriberProfileWithAllField();
                doReturn(profile).when(voltDBSPInterface).getProfile(anyString());
                voltDBSPInterface.updateProfile(profile.getSubscriberIdentity(),new EnumMap<SPRFields, String>(SPRFields.class));
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
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any(),any())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource()));
            doReturn(profileData.getSPRInfo()).when(voltDBSPInterface).getProfile(anyString());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.updateProfile(profileData.getSPRInfo().getSubscriberIdentity(), new EnumMap<SPRFields, String>(SPRFields.class));
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), anyString(), anyString());
        }


        @Test
        public void clientResponseStatusISCONNECTION_TIMEOUT() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_TIMEOUT);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any(),any())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource()));
            doReturn(profileData.getSPRInfo()).when(voltDBSPInterface).getProfile(anyString());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.updateProfile(profileData.getSPRInfo().getSubscriberIdentity(), new EnumMap<SPRFields, String>(SPRFields.class));
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), anyString(), anyString());
        }*/


    }


    public class GenerateOperationFailedExceptionWhen {

        /*@Test
        public void voltDBClientGenerateNoConnetionException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any(),any())).thenThrow(NoConnectionsException.class);
            VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource()));
            doReturn(profileData.getSPRInfo()).when(voltDBSPInterface).getProfile(anyString());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.updateProfile(profileData.getSPRInfo().getSubscriberIdentity(), new EnumMap<SPRFields, String>(SPRFields.class));
        }*/


        @Test
        public void voltDBClientGenerateIOException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any(),any())).thenThrow(IOException.class);
            VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource()));
            doReturn(profileData.getSPRInfo()).when(voltDBSPInterface).getProfile(anyString());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.updateProfile(profileData.getSPRInfo().getSubscriberIdentity(), new EnumMap<SPRFields, String>(SPRFields.class));
        }

        @Test
        public void voltDBClientGenerateProcCallException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any(),any())).thenThrow(ProcCallException.class);
            VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource()));
            doReturn(profileData.getSPRInfo()).when(voltDBSPInterface).getProfile(anyString());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.updateProfile(profileData.getSPRInfo().getSubscriberIdentity(), new EnumMap<SPRFields, String>(SPRFields.class));
        }

        /*@Test
        public void clientResponseStatusISNotSuccess() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(generateResponseOtherThanSuccess());
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any(),any())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource()));
            doReturn(profileData.getSPRInfo()).when(voltDBSPInterface).getProfile(anyString());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.updateProfile(profileData.getSPRInfo().getSubscriberIdentity(), new EnumMap<SPRFields, String>(SPRFields.class));
        }*/

    }

    @Test
    public void returnZeroIfSubscriberIdentityDoesnotExistInRepository() throws Exception {
        AlertListener alertListener = mock(AlertListener.class);
        VoltDBSPInterface voltDBSPInterface = spy(new VoltDBSPInterface(alertListener, new DummyVoltDBClient(voltServer.getClient()), TimeSource.systemTimeSource()));
        SPRInfoImpl existProfile = SPRTestUtil.createSubscriberProfileWithAllField();
        doReturn(existProfile).when(voltDBSPInterface).getProfile(anyString());
        assertEquals(0,voltDBSPInterface.updateProfile(UNKNOWN_SUBSCRIBER,new EnumMap<SPRFields, String>(SPRFields.class)));
    }

    @Test
    public void subscriberWillBeUpdatedIfExistInSubscriberRepository() throws OperationFailedException, DBDownException {
        SPRInfoImpl existProfile = SPRTestUtil.createSubscriberProfileWithAllField();
        voltDBSPInterface.addProfile(existProfile);
        existProfile.setUserName(existProfile.getUserName()+"_updated");
        existProfile.setMsisdn("1111111111");
        voltDBSPInterface.updateProfile(existProfile.getSubscriberIdentity(),SPRTestUtil.createSPRFieldMap(existProfile));
        SPRInfo updatedProfile = voltDBSPInterface.getProfile(existProfile.getSubscriberIdentity());
        assertTrue(existProfile.getUserName().equals(updatedProfile.getUserName()));
        assertTrue(existProfile.getMsisdn().equals(updatedProfile.getMsisdn()));
    }

   @Test(expected = OperationFailedException.class)
   public void throwOperationFailedExceptionWhenUpdateProfileWithTransactionCalled() throws OperationFailedException, TransactionException {
       voltDBSPInterface.updateProfile(profileData.getSubscriberIdentity(),new EnumMap<SPRFields, String>(SPRFields.class),mock(Transaction.class));


   }


    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    @AfterClass
    public static void afterClass() {
        voltServer.shutdown();
    }
}
