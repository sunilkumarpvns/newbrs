package com.elitecore.netvertex.ws;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.CriteriaImpl;
import com.elitecore.corenetvertex.constants.JMXConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.EliteNetVertexServer;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.session.NetvertexSessionManager;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayController;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayController;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.elitecore.netvertex.ws.ReAuthorizationControllerTestUtil.PCRF_KEY_TO_VALIDATE;
import static com.elitecore.netvertex.ws.ReAuthorizationControllerTestUtil.createSessionDataWithIdentityValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ReAuthorizationByCoreSessionId {

    private final PCRFKeyConstants pcrfKey = PCRFKeyConstants.CS_CORESESSION_ID;
    private final String csid = UUID.randomUUID().toString();
    private ReAuthorizationController reAuthorizationController;
    private NetvertexSessionManager sessionManager;
    private EliteNetVertexServer.NetvertexGatewayController gatewayController;
    private DummyNetvertexServerContextImpl serverContext;
    private DummyNetvertexServerConfiguration netvertexServerConfiguration;
    private SessionLocator sessionLocator;
    private Criteria criteria = null;

    @Before
    public void setUp() throws Exception {
        prepareSetup();
        prepareCriteria();
    }



    @Test
    public void returnNotFoundIfNoSessionFoundFromCacheAndDB() throws SessionException {
        when(sessionLocator.getCoreSessionByCoreSessionIDFromCache(csid)).thenReturn(null);
        when(sessionLocator.getCoreSessionList(any())).thenReturn(null);
        Assert.assertTrue(JMXConstant.SESSION_NOT_FOUND.equalsIgnoreCase(reAuthorizationController.reAuthByCoreSessionId(csid)));
    }

    @Test
    public void reAuthorizeSessionFromCacheWhenSessionFromDBNotFound() throws SessionException {

        when(sessionLocator.getCoreSessionList(criteria)).thenReturn(null);

        SessionData sessionFromCache = createSessionDataWithIdentityValue(csid, pcrfKey, new Date(System.currentTimeMillis()), SessionTypeConstant.GX.val);

        when(sessionLocator.getCoreSessionByCoreSessionIDFromCache(csid)).thenReturn(sessionFromCache);

        verifyReAuthorizedSession();
    }

    @Test
    public void reAuthorizeSessionFromDBWhenSessionFromCacheNotFound() throws SessionException {

        when(sessionLocator.getCoreSessionByCoreSessionIDFromCache(csid)).thenReturn(null);

        SessionData sessionFromDB = createSessionDataWithIdentityValue(csid, pcrfKey, new Date(System.currentTimeMillis()), SessionTypeConstant.GX.val);

        List<SessionData> sessionsFromDB = Arrays.asList(sessionFromDB);
        when(sessionLocator.getCoreSessionList(criteria)).thenReturn(sessionsFromDB);

        verifyReAuthorizedSession();
    }

    @Test
    public void reAuthLatestSessionWhenSessionFoundFromBothCacheAndDB() throws SessionException {

        Calendar calendar = Calendar.getInstance();
        Date timeForDBSessionCreation = calendar.getTime();
        calendar.add(Calendar.SECOND, RandomUtils.nextInt(1000));
        Date timeForCacheSessionCreation = calendar.getTime();

        SessionData sessionFromDB = createSessionDataWithIdentityValue(csid, pcrfKey, timeForDBSessionCreation, SessionTypeConstant.GX.val);
        List<SessionData> sessionsFromDB = new ArrayList<>();
        sessionsFromDB.add(sessionFromDB);

        SessionData sessionFromCache = createSessionDataWithIdentityValue(csid, pcrfKey, timeForCacheSessionCreation, SessionTypeConstant.GX.val);

        when(sessionLocator.getCoreSessionByCoreSessionIDFromCache(csid)).thenReturn(sessionFromCache);
        when(sessionLocator.getCoreSessionList(criteria)).thenReturn(sessionsFromDB);

        ArgumentCaptor<PCRFRequest> requestArgumentCaptor = verifyReAuthorizedSession();
        assertEquals(timeForCacheSessionCreation.toString(), requestArgumentCaptor.getValue().getAttribute(PCRF_KEY_TO_VALIDATE));

    }

    @Test
    public void returnOperationNotSupportedWhenSessionIsOfTypeRO() throws SessionException{

        when(sessionLocator.getCoreSessionList(criteria)).thenReturn(null);

        SessionData sessionFromCache = createSessionDataWithIdentityValue(csid, pcrfKey, new Date(System.currentTimeMillis()), SessionTypeConstant.RO.val);
        when(sessionLocator.getCoreSessionByCoreSessionIDFromCache(csid)).thenReturn(sessionFromCache);
        reAuthorizationController.reAuthByCoreSessionId(csid);
        Assert.assertEquals(JMXConstant.OPERATION_NOT_SUPPORTED,reAuthorizationController.reAuthByCoreSessionId(csid));

    }
    private ArgumentCaptor<PCRFRequest> verifyReAuthorizedSession() {
        DiameterGatewayController diameterGatewayController = gatewayController.getDiameterGatewayController();
        Assert.assertEquals(JMXConstant.SUCCESS,reAuthorizationController.reAuthByCoreSessionId(csid));
        ArgumentCaptor<PCRFRequest> requestArgumentCaptor = ArgumentCaptor.forClass(PCRFRequest.class);
        verify(diameterGatewayController).handleSessionReAuthorization(requestArgumentCaptor.capture());
        assertEquals(csid,requestArgumentCaptor.getValue().getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
        return requestArgumentCaptor;
    }


    private void prepareSetup() {
        this.gatewayController = mock(EliteNetVertexServer.NetvertexGatewayController.class);
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

    private void prepareCriteria() throws SessionException {
        criteria = new CriteriaImpl("101");
        criteria.add(Restrictions.eq(pcrfKey.val, csid));
        when(sessionLocator.getCoreSessionCriteria()).thenReturn(criteria);
    }

}
