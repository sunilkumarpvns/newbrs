package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.TimeSourceChain;
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
import org.voltdb.client.ProcCallException;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

@Ignore
@RunWith(HierarchicalContextRunner.class)
public class AddProfileTest {


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
        public void queryRespnseTimeisHigh() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            TimeSourceChain timeSource = new TimeSourceChain(2, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);

            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, new DummyVoltDBClient(voltServer.getClient()), timeSource);
            try {
                voltDBSPInterface.addProfile(profileData.getSPRInfo());
                verify(alertListener, only())
                        .generateSystemAlert(anyString(), eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
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
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.addProfile(createProfile("aditya").getSPRInfo());
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), anyString(), anyString());
        }


        @Test
        public void clientResponseStatusISCONNECTION_TIMEOUT() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(ClientResponse.CONNECTION_TIMEOUT);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.addProfile(profileData.getSPRInfo());
            verify(alertListener, only()).generateSystemAlert(anyString(), eq(Alerts.QUERY_TIME_OUT), anyString(), anyString());
        }*/


    }


    public class GenerateOperationFailedExceptionWhen {

        /*@Test
        public void voltDBClientGenerateNoConnetionException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any())).thenThrow(NoConnectionsException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.addProfile(profileData.getSPRInfo());
        }*/


        @Test
        public void voltDBClientGenerateIOException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any())).thenThrow(IOException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.addProfile(profileData.getSPRInfo());
        }

        @Test
        public void voltDBClientGenerateProcCallException() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            VoltDBClient spiedVoltDBClient = spy(VoltDBClient.class);
            when(spiedVoltDBClient.isAlive()).thenReturn(true);
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any())).thenThrow(ProcCallException.class);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.addProfile(profileData.getSPRInfo());
        }

        /*@Test
        public void clientResponseStatusISNotSuccess() throws Exception {
            AlertListener alertListener = mock(AlertListener.class);
            ClientResponse mockedClientResponse = mock(ClientResponse.class);
            DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
            when(mockedClientResponse.getStatus()).thenReturn(generateResponseOtherThanSuccess());
            when(spiedVoltDBClient.callProcedure(Mockito.anyString(), any(), any(), any(), any())).thenReturn(mockedClientResponse);
            VoltDBSPInterface voltDBSPInterface = new VoltDBSPInterface(alertListener, spiedVoltDBClient, TimeSource.systemTimeSource());
            expectedException.expect(OperationFailedException.class);
            voltDBSPInterface.addProfile(profileData.getSPRInfo());
        }*/

    }

      @Test
      public void throwOperationFailedExceptionIfSubscriberAlreadyExist() throws OperationFailedException  {
              voltDBSPInterface.addProfile(profileData.getSPRInfo());
              expectedException.expect(OperationFailedException.class);
              voltDBSPInterface.addProfile(profileData.getSPRInfo());
      }

      @Test
      public void fetchedSubscriberProfileShouldBeSameAsAdded() throws OperationFailedException, DBDownException {
          SPRInfo addedProfile = createProfile("aditya").getSPRInfo();
          voltDBSPInterface.addProfile(addedProfile);
          SPRInfo fetchedProfile = voltDBSPInterface.getProfile("aditya");
          ((SPRInfoImpl) addedProfile).setCreatedDate(fetchedProfile.getCreatedDate());
          ((SPRInfoImpl) addedProfile).setModifiedDate(fetchedProfile.getCreatedDate());
          fetchedProfile.setSprLoadTime(addedProfile.getSprLoadTime());
          assertLenientEquals(fetchedProfile, addedProfile);
      }

     @Test(expected = UnsupportedOperationException.class)
     public void addProfileWithCreateTransactionThrowUnSupportedOperationException() throws OperationFailedException, TransactionException {
        voltDBSPInterface.addProfile(createProfile().getSPRInfo(),Mockito.mock(Transaction.class));


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
