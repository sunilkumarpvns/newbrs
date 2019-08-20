package com.elitecore.netvertex.ws;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.CriteriaImpl;
import com.elitecore.corenetvertex.constants.JMXConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.EliteNetVertexServer.NetvertexGatewayController;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.session.NetvertexSessionManager;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.util.Maps;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayController;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.elitecore.netvertex.ws.ReAuthorizationControllerTestUtil.PCRF_KEY_TO_VALIDATE;
import static com.elitecore.netvertex.ws.ReAuthorizationControllerTestUtil.contains;
import static com.elitecore.netvertex.ws.ReAuthorizationControllerTestUtil.createSessionData;
import static com.elitecore.netvertex.ws.ReAuthorizationControllerTestUtil.createSessionDataWithIdentityValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ReAuthorizationBySubscriberIdentity {

    private ReAuthorizationController reAuthorizationController;
    private NetvertexSessionManager sessionManager;
    private NetvertexGatewayController gatewayController;
    private DummyNetvertexServerContextImpl serverContext;
    private DummyNetvertexServerConfiguration netvertexServerConfiguration;
    private static final String SCHEMA_NAME = "SCHEMA_1";
    private SessionLocator sessionLocator;

    @Before
    public void setUp() throws Exception {
        prepareSetup();
    }



    @Test
    public void returnOperationNotSupportedWhenRARisDisabled(){
        MiscellaneousConfiguration miscellaneousConfiguration = netvertexServerConfiguration.spyMiscConf();
        when(miscellaneousConfiguration.getRAREnabled()).thenReturn(false);
        Assert.assertTrue(ResultCode.OPERATION_NOT_SUPPORTED.name().equalsIgnoreCase(reAuthorizationController.doReAuthorization(PCRFKeyConstants.fromKeyConstants(anyString()), anyString(), anyBoolean())));
    }

    
    public class ReturnInvalidInputParameterOnReauthWhen {

        @Test
        public void noCoreSessionIdProvided() {
            Assert.assertEquals(JMXConstant.INVALID_INPUT_PARAMETER, reAuthorizationController.reAuthByCoreSessionId(null));
        }

        @Test
        public void noSubscriberIdentityProvided() {
            Assert.assertEquals(JMXConstant.INVALID_INPUT_PARAMETER, reAuthorizationController.reAuthBySessionIPv4(null));
        }

        @Test
        public void noSessionIPProvided() {
            Assert.assertEquals(JMXConstant.INVALID_INPUT_PARAMETER, reAuthorizationController.reAuthBySessionIPv6(null));
            Assert.assertEquals(JMXConstant.INVALID_INPUT_PARAMETER, reAuthorizationController.reAuthBySessionIPv4(null));
        }

    }

    public class ReturnInternalErrorWhen{

        @Test
        public void sessionExceptionOccurInAnyReAuthorizationOperation() throws SessionException {
            doThrow(SessionException.class).when(sessionLocator).getCoreSessionCriteria();
            Assert.assertEquals(JMXConstant.INTERNAL_ERROR,reAuthorizationController.reAuthByCoreSessionId( UUID.randomUUID().toString()));
            Assert.assertEquals(JMXConstant.INTERNAL_ERROR,reAuthorizationController.reAuthBySessionIPv4(UUID.randomUUID().toString()));
            Assert.assertEquals(JMXConstant.INTERNAL_ERROR,reAuthorizationController.reAuthBySessionIPv6(UUID.randomUUID().toString()));
            Assert.assertEquals(JMXConstant.INTERNAL_ERROR,reAuthorizationController.reAuthBySubscriberIdentity(UUID.randomUUID().toString()));
        }
    }

    
    public class ReturnFailOnReauthWhen{

        @Before
        public void setup(){
            MiscellaneousConfiguration miscellaneousConfiguration = netvertexServerConfiguration.spyMiscConf();
            when(miscellaneousConfiguration.getRAREnabled()).thenReturn(true);
        }

        @Test
        public void gatewayControllerIsNull(){

            ReAuthorizationController reAuthorizationController = spy(new ReAuthorizationController(sessionManager, null, serverContext));
            Assert.assertTrue(JMXConstant.FAIL.equalsIgnoreCase(reAuthorizationController.doReAuthorization(PCRFKeyConstants.fromKeyConstants(anyString()), anyString(), anyBoolean())));

        }
        @Test
        public void diameterGatewayControllerIsNullAndRadiusGatewayControllerIsNull(){
            when(gatewayController.getDiameterGatewayController()).thenReturn(null);
            when(gatewayController.getRadiusGatewayController()).thenReturn(null);
            Assert.assertTrue(JMXConstant.FAIL.equalsIgnoreCase(reAuthorizationController.doReAuthorization(PCRFKeyConstants.fromKeyConstants(anyString()), anyString(), anyBoolean())));
        }

    }


    public class DoReAuthorization {

        private final PCRFKeyConstants pcrfKey = PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY;
        private final String subscriberIdentity = UUID.randomUUID().toString();
        private Criteria criteria = null;

        @Before
        public void setup() throws SessionException {

            criteria = new CriteriaImpl(SCHEMA_NAME);
            criteria.add(Restrictions.eq(pcrfKey.val, subscriberIdentity));
            when(sessionLocator.getCoreSessionCriteria()).thenReturn(criteria);
        }

        @Test
        public void returnNotFoundWhenSessionNotFromDBAndCache() {
            when(sessionLocator.getCoreSessionByUserIdentityFromCache(subscriberIdentity)).thenReturn(null);
            when(sessionLocator.getCoreSessionList(any())).thenReturn(null);
            Assert.assertTrue(JMXConstant.SESSION_NOT_FOUND.equalsIgnoreCase(reAuthorizationController.reAuthBySubscriberIdentity(subscriberIdentity)));
        }


        @Test
        public void reAuthorizeSessionFromCacheWhenSessionNotFoundFromDB() {
            List<SessionData> sessionsFromCache = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            Date firstSessionCreationTimeInCache = calendar.getTime();
            String firstSessionCSIDFromCache = UUID.randomUUID().toString();
            SessionData firstSessionFromCache = createSessionDataWithIdentityValue(firstSessionCSIDFromCache, PCRFKeyConstants.CS_CORESESSION_ID, firstSessionCreationTimeInCache,SessionTypeConstant.GX.val);
            firstSessionFromCache.addValue(pcrfKey.getVal(), subscriberIdentity);
            sessionsFromCache.add(firstSessionFromCache);

            calendar.add(Calendar.MINUTE, RandomUtils.nextInt(100));
            Date secondSessionCreationTimeInCache = calendar.getTime();
            String secondSessionCSIDFromCache = UUID.randomUUID().toString();
            SessionData secondSessionFromCache = createSessionDataWithIdentityValue(secondSessionCSIDFromCache, PCRFKeyConstants.CS_CORESESSION_ID, secondSessionCreationTimeInCache,SessionTypeConstant.GY.val);
            secondSessionFromCache.addValue(pcrfKey.getVal(), subscriberIdentity);
            sessionsFromCache.add(secondSessionFromCache);

            when(sessionLocator.getCoreSessionByUserIdentityFromCache(subscriberIdentity)).thenReturn(sessionsFromCache.iterator());
            when(sessionLocator.getCoreSessionList(criteria)).thenReturn(null);

            reAuthorizeSessionAssertion(firstSessionFromCache, secondSessionFromCache);
        }

        @Test
        public void reAuthorizeSessionsFromDBWhenSessionNotFoundFromCache() {
            List<SessionData> sessionsFromDB = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            Date firstSessionCreationTimeInDB = calendar.getTime();
            String firstSessionCSIDFromDB = UUID.randomUUID().toString();
            SessionData firstSessionFromDB = createSessionDataWithIdentityValue(firstSessionCSIDFromDB, PCRFKeyConstants.CS_CORESESSION_ID, firstSessionCreationTimeInDB,SessionTypeConstant.GX.val);
            firstSessionFromDB.addValue(pcrfKey.getVal(), subscriberIdentity);
            sessionsFromDB.add(firstSessionFromDB);

            calendar.add(Calendar.MINUTE, RandomUtils.nextInt(100));
            Date secondSessionCreationTimeInDB = calendar.getTime();
            String secondSessionCSIDFromDB = UUID.randomUUID().toString();
            SessionData secondSessionFromDB = createSessionDataWithIdentityValue(secondSessionCSIDFromDB, PCRFKeyConstants.CS_CORESESSION_ID, secondSessionCreationTimeInDB, SessionTypeConstant.GY.val);
            secondSessionFromDB.addValue(pcrfKey.getVal(), subscriberIdentity);

            sessionsFromDB.add(secondSessionFromDB);

            when(sessionLocator.getCoreSessionByUserIdentityFromCache(subscriberIdentity)).thenReturn(null);
            when(sessionLocator.getCoreSessionList(criteria)).thenReturn(sessionsFromDB);

            reAuthorizeSessionAssertion(firstSessionFromDB, secondSessionFromDB);
        }


        @Test
        public void reAuthorizeLatestSessionsFromDBWhenSessionFoundFromCacheAndDBBoth() {

            List<SessionData> sessionsFromDB = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            String firstSessionCSID = UUID.randomUUID().toString();
            String secondSessionCSID = UUID.randomUUID().toString();

            Date firstSessionCreationTimeInDB = calendar.getTime();
            SessionData firstSessionFromDB = createSessionData(firstSessionCreationTimeInDB, Arrays.asList(Maps.newLinkedHashMap(
                    Arrays.asList(
                            Maps.Entry.newEntry(pcrfKey,subscriberIdentity),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, firstSessionCSID),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_SESSION_TYPE, SessionTypeConstant.GX.val)

                    ))));
            sessionsFromDB.add(firstSessionFromDB);

            calendar.add(Calendar.MINUTE, 100);
            Date secondSessionCreationTimeInDB = calendar.getTime();

            SessionData secondSessionFromDB = createSessionData(secondSessionCreationTimeInDB, Arrays.asList(Maps.newLinkedHashMap(
                    Arrays.asList(
                            Maps.Entry.newEntry(pcrfKey,subscriberIdentity),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, secondSessionCSID),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_SESSION_TYPE, SessionTypeConstant.GY.val)
                    ))));
            sessionsFromDB.add(secondSessionFromDB);

            List<SessionData> sessionsFromCache = new ArrayList<>();
            calendar.add(Calendar.MINUTE, -1000);

            Date firstSessionCreationTimeInCache = calendar.getTime();
            SessionData firstSessionFromCache = createSessionData(firstSessionCreationTimeInCache, Arrays.asList(Maps.newLinkedHashMap(
                    Arrays.asList(
                            Maps.Entry.newEntry(pcrfKey,subscriberIdentity),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, firstSessionCSID),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_SESSION_TYPE, SessionTypeConstant.GX.val)
                    ))));
            sessionsFromCache.add(firstSessionFromCache);

            calendar.add(Calendar.MINUTE, 2000);
            Date secondSessionCreationTimeInCache = calendar.getTime();
            SessionData secondSessionFromCache = createSessionData(secondSessionCreationTimeInCache, Arrays.asList(Maps.newLinkedHashMap(
                    Arrays.asList(
                            Maps.Entry.newEntry(pcrfKey,subscriberIdentity),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, secondSessionCSID),
                            Maps.Entry.newEntry(PCRFKeyConstants.CS_SESSION_TYPE, SessionTypeConstant.GY.val)
                    ))));
            sessionsFromCache.add(secondSessionFromCache);

            when(sessionLocator.getCoreSessionByUserIdentityFromCache(subscriberIdentity)).thenReturn(sessionsFromCache.iterator());
            when(sessionLocator.getCoreSessionList(criteria)).thenReturn(sessionsFromDB);

            reAuthorizeSessionAssertion(firstSessionFromDB, secondSessionFromCache);
        }


        @Test
        public void returnsOperationNotSupportedWhenSessionsAreOfTypeRo(){
            List<SessionData> sessionsFromCache = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            Date firstSessionCreationTimeInCache = calendar.getTime();
            String firstSessionCSIDFromCache = UUID.randomUUID().toString();
            SessionData firstSessionFromCache = createSessionDataWithIdentityValue(firstSessionCSIDFromCache, PCRFKeyConstants.CS_CORESESSION_ID, firstSessionCreationTimeInCache,SessionTypeConstant.RO.val);
            firstSessionFromCache.addValue(pcrfKey.getVal(), subscriberIdentity);
            sessionsFromCache.add(firstSessionFromCache);

            calendar.add(Calendar.MINUTE, RandomUtils.nextInt(100));
            Date secondSessionCreationTimeInCache = calendar.getTime();
            String secondSessionCSIDFromCache = UUID.randomUUID().toString();
            SessionData secondSessionFromCache = createSessionDataWithIdentityValue(secondSessionCSIDFromCache, PCRFKeyConstants.CS_CORESESSION_ID, secondSessionCreationTimeInCache,SessionTypeConstant.RO.val);
            secondSessionFromCache.addValue(pcrfKey.getVal(), subscriberIdentity);
            sessionsFromCache.add(secondSessionFromCache);

            when(sessionLocator.getCoreSessionByUserIdentityFromCache(subscriberIdentity)).thenReturn(sessionsFromCache.iterator());
            when(sessionLocator.getCoreSessionList(criteria)).thenReturn(null);

            Assert.assertEquals(JMXConstant.OPERATION_NOT_SUPPORTED,reAuthorizationController.reAuthBySubscriberIdentity(subscriberIdentity));
        }

        @Test
        public void returnsPartialSuccessWhenSomeSessionsAreOfTypefRo(){
            List<SessionData> sessionsFromCache = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            Date firstSessionCreationTimeInCache = calendar.getTime();
            String firstSessionCSIDFromCache = UUID.randomUUID().toString();
            SessionData firstSessionFromCache = createSessionDataWithIdentityValue(firstSessionCSIDFromCache, PCRFKeyConstants.CS_CORESESSION_ID, firstSessionCreationTimeInCache,SessionTypeConstant.GX.val);
            firstSessionFromCache.addValue(pcrfKey.getVal(), subscriberIdentity);
            sessionsFromCache.add(firstSessionFromCache);

            calendar.add(Calendar.MINUTE, RandomUtils.nextInt(100));
            Date secondSessionCreationTimeInCache = calendar.getTime();
            String secondSessionCSIDFromCache = UUID.randomUUID().toString();
            SessionData secondSessionFromCache = createSessionDataWithIdentityValue(secondSessionCSIDFromCache, PCRFKeyConstants.CS_CORESESSION_ID, secondSessionCreationTimeInCache,SessionTypeConstant.RO.val);
            secondSessionFromCache.addValue(pcrfKey.getVal(), subscriberIdentity);
            sessionsFromCache.add(secondSessionFromCache);

            when(sessionLocator.getCoreSessionByUserIdentityFromCache(subscriberIdentity)).thenReturn(sessionsFromCache.iterator());
            when(sessionLocator.getCoreSessionList(criteria)).thenReturn(null);

            Assert.assertEquals(JMXConstant.PARTIAL_SUCCESS,reAuthorizationController.reAuthBySubscriberIdentity(subscriberIdentity));
        }

        private void reAuthorizeSessionAssertion(SessionData session1, SessionData session2) {
            DiameterGatewayController diameterGatewayController = gatewayController.getDiameterGatewayController();
            Assert.assertEquals(JMXConstant.SUCCESS,reAuthorizationController.reAuthBySubscriberIdentity(subscriberIdentity));
            ArgumentCaptor<PCRFRequest> requestArgumentCaptor = ArgumentCaptor.forClass(PCRFRequest.class);
            verify(diameterGatewayController, atMost(2)).handleSessionReAuthorization(requestArgumentCaptor.capture());
            List<PCRFRequest> reAuthRequests = requestArgumentCaptor.getAllValues();
            assertEquals(2, reAuthRequests.size());
            assertThat(reAuthRequests,contains(session1,PCRF_KEY_TO_VALIDATE));
            assertThat(reAuthRequests,contains(session2,PCRF_KEY_TO_VALIDATE));
        }
    }
    


    private void prepareSetup() {
        this.gatewayController = mock(NetvertexGatewayController.class);
        this.serverContext = DummyNetvertexServerContextImpl.spy();
        this.netvertexServerConfiguration = serverContext.getServerConfiguration();
        this.sessionLocator = mock(SessionLocator.class);
        this.sessionManager = mock(NetvertexSessionManager.class);
        when(sessionManager.getSessionLookup()).thenReturn(sessionLocator);
        this.reAuthorizationController = spy(new ReAuthorizationController(sessionManager, gatewayController, serverContext));
        MiscellaneousConfiguration miscellaneousConfiguration = netvertexServerConfiguration.spyMiscConf();
        when(miscellaneousConfiguration.getRAREnabled()).thenReturn(true);
        RadiusGatewayController radiusGatewayController = mock(RadiusGatewayController.class);
        DiameterGatewayController diameterGatewayController = mock(DiameterGatewayController.class);
        when(diameterGatewayController.isEnabled()).thenReturn(true);
        when(gatewayController.getRadiusGatewayController()).thenReturn(radiusGatewayController);
        when(gatewayController.getDiameterGatewayController()).thenReturn(diameterGatewayController);
    }
}