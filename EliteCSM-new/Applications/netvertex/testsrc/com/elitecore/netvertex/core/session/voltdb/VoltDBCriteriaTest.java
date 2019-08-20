package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.CORE_SESS_TABLE_NAME;
import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.SESSION_RULE_TABLE_NAME;
import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.checkListsAreNotEmpty;
import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.checkListsAreNullOrEmpty;
import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.createORCriterian;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class VoltDBCriteriaTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public class CoreSessionCriteriaOperations{
        private VoltDBCoreSessionCriteria voltDBCoreSessionCriteria;
        private final List<String> allowedCoreSessionKeys = new ArrayList<>(Arrays.asList(
                PCRFKeyConstants.CS_CORESESSION_ID.val
                , PCRFKeyConstants.CS_SESSION_ID.val
                , PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val
                , PCRFKeyConstants.CS_SESSION_IPV4.val
                , PCRFKeyConstants.CS_SESSION_IPV6.val
                , PCRFKeyConstants.CS_SY_SESSION_ID.val
                , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val)
        );

        @Before
        public void setup(){
            voltDBCoreSessionCriteria = new VoltDBCoreSessionCriteria(CORE_SESS_TABLE_NAME);
        }

        @Test
        public void throwsExceptionWhenTryingToAddMultipleEntries(){
            voltDBCoreSessionCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx"));
            thrown.expect(UnsupportedOperationException.class);
            thrown.expectMessage("Only one criterion is supported in VoltDB");
            voltDBCoreSessionCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, "1234567890_gx"));
        }

        @Test
        public void throwsExceptionWhenTryingToAddNotAllowedEntry(){
            thrown.expect(UnsupportedOperationException.class);
            thrown.expectMessage("Only following keys are supported in Criterion for VoltDB: "+allowedCoreSessionKeys);
            voltDBCoreSessionCriteria.add(Restrictions.eq(PCRFKeyConstants.REQUEST_NUMBER.val, "1"));
        }

        @Test
        public void throwsExceptionWhenTryingToAddNotExpression(){
            Criterion criterion = Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx");
            thrown.expect(UnsupportedOperationException.class);
            thrown.expectMessage("NOT Expression is not supported in VoltDB");
            voltDBCoreSessionCriteria.add(Restrictions.not(criterion));
        }

        @Test
        public void throwsExceptionWhenOtherCriterionORedWithCoreSessionId(){
            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, "12345678790"));
            thrown.expect(UnsupportedOperationException.class);
            thrown.expectMessage("Other Criterion cannot be combined with CS_CORESESSION_ID.");
            voltDBCoreSessionCriteria.add(createORCriterian(criterions));
        }

        @Test
        public void allowToAddSingleCoreSessionId(){
            voltDBCoreSessionCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx"));
            assertEquals("1234567890_gx:Gx", voltDBCoreSessionCriteria.getCoreSessionIdValues().get(0));
            assertTrue(checkListsAreNullOrEmpty(
                    voltDBCoreSessionCriteria.getSessionIdValues()
                    , voltDBCoreSessionCriteria.getSubscriberIdValues()
                    , voltDBCoreSessionCriteria.getSessionIpV4Values()
                    , voltDBCoreSessionCriteria.getSessionIpV6Values()
                    , voltDBCoreSessionCriteria.getSySessionIdValues()
                    , voltDBCoreSessionCriteria.getGatewayAddressValues())
            );
        }

        @Test
        public void allowToAddMultipleORedSessionIds(){
            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, "1234567890_rx"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, "1234567891_rx"));
            voltDBCoreSessionCriteria.add(createORCriterian(criterions));
            assertEquals(2, voltDBCoreSessionCriteria.getSessionIdValues().size());
            assertTrue(checkListsAreNullOrEmpty(
                    voltDBCoreSessionCriteria.getCoreSessionIdValues()
                    , voltDBCoreSessionCriteria.getSubscriberIdValues()
                    , voltDBCoreSessionCriteria.getSessionIpV4Values()
                    , voltDBCoreSessionCriteria.getSessionIpV6Values()
                    , voltDBCoreSessionCriteria.getSySessionIdValues()
                    , voltDBCoreSessionCriteria.getGatewayAddressValues())
            );
        }

        @Test
        public void addAllowedKeysWithORedCriterionExceptCoreSessionId(){
            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, "1234567890_gx"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, "12345678790"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, "127.0.0.1"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, "2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SY_SESSION_ID.val, "12345678790_sy"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "af.elite.com"));
            voltDBCoreSessionCriteria.add(createORCriterian(criterions));
            assertTrue(checkListsAreNotEmpty(
                    voltDBCoreSessionCriteria.getSessionIdValues()
                    , voltDBCoreSessionCriteria.getSubscriberIdValues()
                    , voltDBCoreSessionCriteria.getSessionIpV4Values()
                    , voltDBCoreSessionCriteria.getSessionIpV6Values()
                    , voltDBCoreSessionCriteria.getSySessionIdValues()
                    , voltDBCoreSessionCriteria.getGatewayAddressValues())
            );
        }
    }


    public class SimpleCriteriaOperations{
        private VoltDBSimpleCriteria voltDBSimpleCriteria;

        @Before
        public void setup(){
            voltDBSimpleCriteria = new VoltDBSimpleCriteria(SESSION_RULE_TABLE_NAME);
        }

        @Test
        public void throwsExceptionWhenTryingToAddMultipleEntries(){
            voltDBSimpleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_rx:Rx"));
            thrown.expect(UnsupportedOperationException.class);
            thrown.expectMessage("Only one criterion is supported in VoltDB");
            voltDBSimpleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx"));
        }

        @Test
        public void throwsExceptionWhenOtherThanSimpleExpressionProvided(){
            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_rx:Rx"));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx"));
            thrown.expect(UnsupportedOperationException.class);
            thrown.expectMessage("Only simple expression is supported in VoltDB");
            voltDBSimpleCriteria.add(createORCriterian(criterions));
        }

        @Test
        public void allowToAddSingleSimpleExpression(){
            voltDBSimpleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx"));
            assertEquals(PCRFKeyConstants.CS_AF_SESSION_ID.val, voltDBSimpleCriteria.getSimpleKey());
            assertEquals("1234567890_rx", voltDBSimpleCriteria.getSimpleValue());
        }
    }

}
