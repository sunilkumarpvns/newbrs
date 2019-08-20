package com.elitecore.netvertex.core.session;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;
import com.elitecore.commons.base.Predicates;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import org.junit.Before;
import org.junit.Test;

import static com.elitecore.corenetvertex.constants.PCRFKeyConstants.CS_SESSION_TYPE;
import static org.junit.Assert.*;

public class SessionSortTest {
    private SessionData sessionData;
    private SessionData sessionDataLatest;

    @Before
    public void setUp() {
        Date date = new Date(System.currentTimeMillis());
        sessionData = new SessionDataImpl(null, date, date);
        sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, "test1:Gx");
        sessionData.addValue(CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
        sessionData.addValue(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val, "{}");
        sessionData.setSessionLoadTime(100);

        date = new Date(System.currentTimeMillis() + 1000);
        sessionDataLatest = new SessionDataImpl(null, date, date);
        sessionDataLatest.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, "test2:Radius");
        sessionDataLatest.addValue(CS_SESSION_TYPE.val, SessionTypeConstant.RADIUS.val);
        sessionDataLatest.addValue(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val, "{}");
        sessionDataLatest.setSessionLoadTime(100);
    }

    @Test
    public void testsGetListReturnsNullWhenSessionDataIteratorIsNull() {
        SessionSort sort = new SessionSort(data -> true, SessionSortOrder.DESCENDING);
        sort.accept(null);

        assertNull(sort.getList());
    }

    @Test
    public void testsIfConditionNotSatisfiedThenListReturnsNull() {
        SessionSort sort = new SessionSort(data -> false, SessionSortOrder.DESCENDING);
        sort.accept(Arrays.asList(sessionData, sessionDataLatest).iterator());

        assertNull(sort.getList());
    }

    @Test
    public void testsSortedSessionInAcendingOrderWhenAcedingOrderPassed() throws Exception {
        SessionSortOrder sessionSortOrderAscending = SessionSortOrder.ASCENDING;
        SessionSort sessionSortInAscendingOrder = new SessionSort(data -> true, sessionSortOrderAscending);

        sessionSortInAscendingOrder.accept(Arrays.asList(sessionData, sessionDataLatest).iterator());

        assertNotNull(sessionSortInAscendingOrder.getList());
        assertEquals(sessionSortInAscendingOrder.getList().size(),2);
        assertEquals(sessionSortInAscendingOrder.getList().get(0),sessionData);
    }

    @Test
    public void testsSortedSessionInDescendingOrderWhenDescendingOrderPassed() throws Exception {
        SessionSortOrder sessionSortOrderDescending = SessionSortOrder.DESCENDING;
        SessionSort sessionSortInDescendingOrder = new SessionSort(data -> true, sessionSortOrderDescending);

        sessionSortInDescendingOrder.accept(Arrays.asList(sessionData, sessionDataLatest).iterator());

        assertNotNull(sessionSortInDescendingOrder.getList());
        assertEquals(sessionSortInDescendingOrder.getList().size(),2);
        assertEquals(sessionSortInDescendingOrder.getList().get(0),sessionDataLatest);
    }
}