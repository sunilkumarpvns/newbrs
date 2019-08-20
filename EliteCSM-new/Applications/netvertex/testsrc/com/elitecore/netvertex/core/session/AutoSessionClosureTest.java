package com.elitecore.netvertex.core.session;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import junitparams.JUnitParamsRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class AutoSessionClosureTest {

    private static final String MODULE = "AUTO_SESSION-CLOSURE-TEST";
    private static final long DAY_IN_MILLIS = 24*60*60*1000l;
    private NetvertexSessionManager netvertexSessionManager;
    private AutoSessionClosure autoSessionClosureCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        netvertexSessionManager = createNetvertexSessionManager();
        autoSessionClosureCache = new AutoSessionClosure(netvertexSessionManager, DAY_IN_MILLIS);
    }

    private NetvertexSessionManager createNetvertexSessionManager()
            throws InitializationFailedException, SessionException {
        NetvertexSessionManager netvertexSessionManager = Mockito.mock(NetvertexSessionManager.class);
        SessionLocator sessionLocator = Mockito.mock(SessionLocator.class);
        doReturn(sessionLocator).when(netvertexSessionManager).getSessionLookup();
        doReturn(Mockito.mock(Criteria.class)).when(sessionLocator).getCoreSessionCriteria();
        return netvertexSessionManager;
    }

    @Test
    public void test_evict_will_store_all_expired_session() throws Exception{

        GyServiceUnits gyServiceUnits = new GyServiceUnits();
        gyServiceUnits.setVolume(1);
        gyServiceUnits.setPackageId("test");
        gyServiceUnits.setQuotaProfileIdOrRateCardId("test");
        gyServiceUnits.setSubscriptionId("test");
        gyServiceUnits.setReservationRequired(true);

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(0l);
        mscc.setServiceIdentifiers(Arrays.asList(0l));
        mscc.setGrantedServiceUnits(gyServiceUnits);

        QuotaReservation quotaReservation = new QuotaReservation();
        quotaReservation.put(mscc);

        String quotaReservationString = quotaReservation.getAsJson();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);

        SessionData sessionData1 = new SessionDataImpl("test1", "session1", calendar.getTime(), calendar.getTime());
        sessionData1.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"Gy");
        sessionData1.addValue(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, quotaReservationString);

        SessionData sessionData2 = new SessionDataImpl("test2", "session2", calendar.getTime(), calendar.getTime());
        sessionData2.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"RADIUS");
        sessionData2.addValue(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, quotaReservationString);

        SessionData sessionData3 = new SessionDataImpl("test3", "session3", calendar.getTime(), calendar.getTime());
        sessionData3.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"Ro");
        sessionData3.addValue(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, quotaReservationString);

        List<SessionData> sessionDataList = Arrays.asList(sessionData3, sessionData2, sessionData1);

        when((netvertexSessionManager.getSessionLookup()).getCoreSessionList(Mockito.any(Criteria.class))).thenReturn(sessionDataList);

        Map<String,SessionData> collect = sessionDataList.
                stream().collect(Collectors.toMap(SessionData::getSessionId, Function.identity()));
        autoSessionClosureCache.evict(collect.entrySet());

        Assert.assertEquals(sessionDataList, autoSessionClosureCache.getExpiredSession());
    }

    @Test
    public void test_evict_will_throw_session_exception() throws Exception{

        SessionData sessionData1 = new SessionDataImpl("test1", "session1", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
        sessionData1.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"Gy");
        List<SessionData> sessionDataList = Arrays.asList(sessionData1);
        Map<String,SessionData> collect = sessionDataList.
                stream().collect(Collectors.toMap(SessionData::getSessionId, Function.identity()));
        try {
            when(netvertexSessionManager.getSessionLookup().getCoreSessionCriteria()).thenThrow(new SessionException("Session Exception"));
            autoSessionClosureCache.evict(collect.entrySet());
        }catch(SessionException e){
            Assert.assertEquals("Session Exception", e.getMessage());
        }
    }

    @Test
    public void test_evict_will_return_for_session_type_except_gy_and_radius() throws Exception{

        SessionData sessionData1 = new SessionDataImpl("test1", "session1", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
        sessionData1.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"Gx");
        List<SessionData> sessionDataList = Arrays.asList(sessionData1);
        Map<String,SessionData> collect = sessionDataList.
                stream().collect(Collectors.toMap(SessionData::getSessionId, Function.identity()));
        autoSessionClosureCache.evict(collect.entrySet());

        Assert.assertNull(autoSessionClosureCache.getExpiredSession());
    }

    @Test
    public void test_evict_will_return_for_null_quota_reservation_string() throws Exception{

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);

        SessionData sessionData1 = new SessionDataImpl("test1", "session1", calendar.getTime(), calendar.getTime());
        sessionData1.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"Gy");
        sessionData1.addValue(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, null);

        List<SessionData> sessionDataList = Arrays.asList(sessionData1);
        Map<String,SessionData> collect = sessionDataList.
                stream().collect(Collectors.toMap(SessionData::getSessionId, Function.identity()));
        autoSessionClosureCache.evict(collect.entrySet());

        Assert.assertNull(autoSessionClosureCache.getExpiredSession());
    }

    @Test
    public void test_evict_will_return_null_if_reservation_not_exist() throws Exception{

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);

        SessionData sessionData1 = new SessionDataImpl("test1", "session1", calendar.getTime(), calendar.getTime());
        sessionData1.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"Gy");

        List<SessionData> sessionDataList = Arrays.asList(sessionData1);

        when((netvertexSessionManager.getSessionLookup()).getCoreSessionList(Mockito.any(Criteria.class))).thenReturn(sessionDataList);

        Map<String,SessionData> collect = sessionDataList.
                stream().collect(Collectors.toMap(SessionData::getSessionId, Function.identity()));
        autoSessionClosureCache.evict(collect.entrySet());

        Assert.assertNull(autoSessionClosureCache.getExpiredSession());
    }

    @Test
    public void test_evict_will_return_null_if_auto_session_closure_not_eligible() throws Exception{

        SessionData sessionData1 = new SessionDataImpl("test1", "session1", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
        sessionData1.addValue(PCRFKeyConstants.CS_SESSION_TYPE.val,"Gy");
        sessionData1.addValue(PCRFKeyConstants.CS_QUOTA_RESERVATION.val,"test1");

        List<SessionData> sessionDataList = Arrays.asList(sessionData1);
        Map<String,SessionData> collect = sessionDataList.
                stream().collect(Collectors.toMap(SessionData::getSessionId, Function.identity()));
        autoSessionClosureCache.evict(collect.entrySet());

        Assert.assertNull(autoSessionClosureCache.getExpiredSession());
    }
}
