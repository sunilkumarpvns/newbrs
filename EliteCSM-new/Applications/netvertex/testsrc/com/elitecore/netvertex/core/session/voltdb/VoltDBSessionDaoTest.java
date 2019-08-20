package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SaveCoreSessionStoredProcedure;
import com.elitecore.corenetvertex.spr.voltdb.storedprocedure.SaveSubSessionStoredProcedure;
import com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UpdateCoreSessionStoredProcedure;
import com.elitecore.corenetvertex.spr.voltdb.storedprocedure.UpdateSubSessionStoredProcedure;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.session.SessionDao;
import com.elitecore.netvertex.core.session.SessionDaoTestUtil;
import com.elitecore.netvertex.core.session.conf.impl.SessionManagerConfigurationImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.CORE_SESS_TABLE_NAME;
import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.SESSION_RULE_TABLE_NAME;
import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.createORCriterian;
import static com.elitecore.netvertex.core.session.SessionDaoTestUtil.getDataArray;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SAVE_CORE_SESSION;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SAVE_SESSION_RULE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SELECT_CORE_SESSIONS_BY_CRITERIA;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SELECT_SESSION_RULES_BY_SINGLE_KEY_VALUE;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.UPDATE_CORE_SESSION;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.UPDATE_SESSION_RULE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class VoltDBSessionDaoTest {
    private SessionDao voltDBSessionDao;
    @Mock NetVertexServerContext serverContext;
    @Mock SessionConfigurationImpl sessionConfiguration;
    @Mock VoltDBClient voltDBClient;
    @Mock NetvertexServerConfiguration netVertexServerConfiguration;
    @Mock SessionManagerConfigurationImpl sessionManagerConfiguration;

    @Rule public ExpectedException expectedException = ExpectedException.none();

    private List<SchemaMapping> schemaMappings;
    private SchemaMapping coreSessionSchemaMapping;
    private SchemaMapping sessionRuleSchemaMapping;


    @Before
    public void setup() throws InitializationFailedException {
        MockitoAnnotations.initMocks(this);
        when(serverContext.getServerConfiguration()).thenReturn(netVertexServerConfiguration);
        when(netVertexServerConfiguration.getSessionManagerConfiguration()).thenReturn(sessionManagerConfiguration);

        coreSessionSchemaMapping = SessionDaoTestUtil.createCoreSessionSchemaMapping();
        sessionRuleSchemaMapping = SessionDaoTestUtil.createSessionRuleSchemaMapping();

        schemaMappings = new ArrayList<>();
        schemaMappings.add(coreSessionSchemaMapping);
        schemaMappings.add(sessionRuleSchemaMapping);

        when(sessionConfiguration.getSchemaList()).thenReturn(schemaMappings);

        voltDBSessionDao = new VoltDBSessionDao(serverContext, sessionConfiguration, voltDBClient);
    }


    public class CoreSessionOperations{
        @Test
        public void saveWithExactArguments() throws IOException, ProcCallException, ClassNotFoundException, IllegalAccessException, InstantiationException {
            SessionData sessionData = SessionDaoTestUtil.createDummySessionDataForCoreSession();

            int actualResult = voltDBSessionDao.saveCoreSession(sessionData);
            assertEquals(1, actualResult);
            String[] dataArray = getDataArray(sessionData, coreSessionSchemaMapping);
            verify(voltDBClient, times(1)).callProcedure(SAVE_CORE_SESSION
                    , sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
                    , dataArray);

            long queryParameterCount = new SaveCoreSessionStoredProcedure().insertCoreSessionQuery.getText().chars().filter(ch -> ch=='?').count();
            assertEquals(queryParameterCount, dataArray.length+3);
        }

        @Test
        public void updateWithExactArguments() throws IOException, ProcCallException {
            SessionData sessionData = SessionDaoTestUtil.createDummySessionDataForCoreSession();

            int actualResult = voltDBSessionDao.updateCoreSession(sessionData);
            assertEquals(1, actualResult);
            String[] dataArray = getDataArray(sessionData, coreSessionSchemaMapping);
            verify(voltDBClient, times(1)).callProcedure(UPDATE_CORE_SESSION
                    , sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
                    , dataArray
                    , sessionData.getCreationTime());
            long queryParameterCount = new UpdateCoreSessionStoredProcedure().updateCoreSessionQuery.getText().chars().filter(ch -> ch=='?').count();
            assertEquals(queryParameterCount, dataArray.length+3);
        }

        @Test
        public void deleteByCoreSessionIdWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx"));
            int actualResult = voltDBSessionDao.deleteCoreSession(criteria);
            assertEquals(1, actualResult);
            verify(voltDBClient, times(1)).callProcedure(DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx");
        }

        @Test
        public void deleteByGatewayAddressWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "gx.elite.com"));
            int actualResult = voltDBSessionDao.deleteCoreSession(criteria);
            assertEquals(1, actualResult);
            verify(voltDBClient, times(1)).callProcedure(DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "gx.elite.com");
        }

        @Test
        public void deleteThrowsUnsupportedOperationExceptionWhenNoCriteriaMatches() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SY_SESSION_ID.val, "1234567890_sy"));
            expectedException.expect(UnsupportedOperationException.class);
            expectedException.expectMessage("Complex Criteria is not supported.");
            voltDBSessionDao.deleteCoreSession(criteria);
        }

        @Test
        public void getByCoreSessionIdWithExactArgument() throws IOException, ProcCallException {
            voltDBSessionDao.getCoreSessionByCoreSessionID("1234567890_gx:Gx");
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    ,PCRFKeyConstants.CS_CORESESSION_ID.val , "1234567890_gx:Gx");
        }

        @Test
        public void getBySubscriberIdWithExactArgument() throws IOException, ProcCallException {
            voltDBSessionDao.getCoreSessionBySubscriberIdentity("1234567890");
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, "1234567890");
        }

        @Test
        public void getByIPv4WithExactArgument() throws IOException, ProcCallException {
            voltDBSessionDao.getCoreSessionBySessionIPv4("127.0.0.1");
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_SESSION_IPV4.val, "127.0.0.1");
        }

        @Test
        public void getByIPv6WithExactArgument() throws IOException, ProcCallException {
            voltDBSessionDao.getCoreSessionBySessionIPv6("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_SESSION_IPV6.val, "2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        }

        @Test
        public void getByGatewayAddressWithExactArgument() throws IOException, ProcCallException {
            voltDBSessionDao.getCoreSessionByGatewayAddress("gx.elite.com");
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "gx.elite.com");
        }

        @Test
        public void getByCoreSessionIdInCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx"));
            voltDBSessionDao.getCoreSessionList(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    ,PCRFKeyConstants.CS_CORESESSION_ID.val , "1234567890_gx:Gx");
        }

        @Test
        public void getBySessionIdInCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, "1234567890_gx"));
            voltDBSessionDao.getCoreSessionList(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    ,PCRFKeyConstants.CS_SESSION_ID.val , "1234567890_gx");
        }

        @Test
        public void getBySubscriberIdInCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, "1234567890"));
            voltDBSessionDao.getCoreSessionList(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, "1234567890");
        }

        @Test
        public void getByIPv4InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, "127.0.0.1"));
            voltDBSessionDao.getCoreSessionList(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_SESSION_IPV4.val, "127.0.0.1");
        }

        @Test
        public void getByIPv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, "2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
            voltDBSessionDao.getCoreSessionList(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_SESSION_IPV6.val, "2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        }

        @Test
        public void getBySySessionIdInCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SY_SESSION_ID.val, "1234567890_sy"));
            voltDBSessionDao.getCoreSessionList(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    ,PCRFKeyConstants.CS_SY_SESSION_ID.val , "1234567890_sy");
        }

        @Test
        public void getByGatewayAddressInCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "gx.elite.com"));
            voltDBSessionDao.getCoreSessionList(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "gx.elite.com");
        }

        @Test
        public void getByMultipleSessionIdsInCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            List<String> sessionIds = new ArrayList<>(Arrays.asList("1_rx", "2_rx", "3_rx"));
            List<Criterion> criterions = new ArrayList<>();
            for(String sessionId : sessionIds){
                criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            }

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , sessionIds.toArray(new String[sessionIds.size()]), null, null, null);
        }

        @Test
        public void getBySessionIdOrSubscriberIdOrIpv4OrIpv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String sessionId = "1234567890_gx";
            String subscriberId = "1234567890";
            String ipV4 = "127.0.0.1";
            String ipV6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, ipV4));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, ipV6));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , new String[] {sessionId}, new String[] {subscriberId}, new String[] {ipV4}, new String[] {ipV6});
        }

        @Test
        public void getBySessionIdOrSubscriberIdOrIpv4InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String sessionId = "1234567890_gx";
            String subscriberId = "1234567890";
            String ipV4 = "127.0.0.1";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, ipV4));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , new String[] {sessionId}, new String[] {subscriberId}, new String[] {ipV4}, null);
        }

        @Test
        public void getBySessionIdOrSubscriberIdOrIpv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String sessionId = "1234567890_gx";
            String subscriberId = "1234567890";
            String ipV6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, ipV6));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , new String[] {sessionId}, new String[] {subscriberId}, null, new String[] {ipV6});
        }

        @Test
        public void getBySessionIdOrIpv4OrIpv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String sessionId = "1234567890_gx";
            String ipV4 = "127.0.0.1";
            String ipV6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, ipV4));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, ipV6));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , new String[] {sessionId}, null, new String[] {ipV4}, new String[] {ipV6});
        }

        @Test
        public void getBySubscriberIdOrIpv4OrIpv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String subscriberId = "1234567890";
            String ipV4 = "127.0.0.1";
            String ipV6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, ipV4));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, ipV6));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , null, new String[] {subscriberId}, new String[] {ipV4}, new String[] {ipV6});
        }

        @Test
        public void getBySessionIdOrSubscriberIdInCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String sessionId = "1234567890_gx";
            String subscriberId = "1234567890";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , new String[] {sessionId}, new String[] {subscriberId}, null, null);
        }

        @Test
        public void getBySessionIdOrIpv4InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String sessionId = "1234567890_gx";
            String ipV4 = "127.0.0.1";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, ipV4));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , new String[] {sessionId}, null, new String[] {ipV4}, null);
        }

        @Test
        public void getBySessionIdOrIpv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String sessionId = "1234567890_gx";
            String ipV6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_ID.val, sessionId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, ipV6));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , new String[] {sessionId}, null, null, new String[] {ipV6});
        }

        @Test
        public void getBySubscriberIdOrIpv4InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String subscriberId = "1234567890";
            String ipV4 = "127.0.0.1";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, ipV4));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , null, new String[] {subscriberId}, new String[] {ipV4}, null);
        }

        @Test
        public void getBySubscriberIdOrIpv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String subscriberId = "1234567890";
            String ipV6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, ipV6));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , null, new String[] {subscriberId}, null, new String[] {ipV6});
        }

        @Test
        public void getByIpv4OrIpv6InCriteriaWithExactArgument() throws IOException, ProcCallException {
            Criteria coreSessionCriteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);

            String ipV4 = "127.0.0.1";
            String ipV6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

            List<Criterion> criterions = new ArrayList<>();
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.val, ipV4));
            criterions.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.val, ipV6));

            coreSessionCriteria.add(createORCriterian(criterions));

            voltDBSessionDao.getCoreSessionList(coreSessionCriteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_CORE_SESSIONS_BY_CRITERIA
                    , null, null, new String[] {ipV4}, new String[] {ipV6});
        }

        @Test
        public void getThrowsUnsupportedOperationExceptionWhenNoCriteriaMatches() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            expectedException.expect(UnsupportedOperationException.class);
            criteria.add(Restrictions.eq(PCRFKeyConstants.REQUEST_NUMBER.val, "1"));
            voltDBSessionDao.getCoreSessionList(criteria);
        }
    }


    public class SessionRuleOperations{
        @Test
        public void saveWithExactArguments() throws IOException, ProcCallException {
            SessionData sessionData = SessionDaoTestUtil.createDummySessionDataForSessionRule();

            int actualResult = voltDBSessionDao.saveSessionRule(sessionData);
            assertEquals(1, actualResult);
            String[] dataArray = getDataArray(sessionData, sessionRuleSchemaMapping);
            verify(voltDBClient, times(1)).callProcedure(SAVE_SESSION_RULE
                    , sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
                    , dataArray);
            long queryParameterCount = new SaveSubSessionStoredProcedure().insertSubSessionQuery.getText().chars().filter(ch -> ch=='?').count();
            assertEquals(queryParameterCount, dataArray.length+3);
        }

        @Test
        public void updateWithExactArguments() throws IOException, ProcCallException {
            SessionData sessionData = SessionDaoTestUtil.createDummySessionDataForSessionRule();

            int actualResult = voltDBSessionDao.updateSessionRule(sessionData);
            assertEquals(1, actualResult);
            String[] dataArray = getDataArray(sessionData, sessionRuleSchemaMapping);
            verify(voltDBClient, times(1)).callProcedure(UPDATE_SESSION_RULE
                    , sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
                    , dataArray
                    , sessionData.getCreationTime());
            long queryParameterCount = new UpdateSubSessionStoredProcedure().updateSubSessionQuery.getText().chars().filter(ch -> ch=='?').count();
            assertEquals(queryParameterCount, dataArray.length+3);
        }

        @Test
        public void deleteWithCriteriaAsCoreSessionIdWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx"));
            voltDBSessionDao.deleteSessionRule(criteria);
            verify(voltDBClient, times(1)).callProcedure(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx");
        }

        @Test
        public void deleteWithCriteriaAsGatewayAddressWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "af.elite.com"));
            voltDBSessionDao.deleteSessionRule(criteria);
            verify(voltDBClient, times(1)).callProcedure(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "af.elite.com");
        }

        @Test
        public void deleteWithCriteriaAsAfSessionIdWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx"));
            voltDBSessionDao.deleteSessionRule(criteria);
            verify(voltDBClient, times(1)).callProcedure(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx");
        }

        @Test
        public void deleteWithCriteriaThrowsUnsupportedOperationExceptionWhenNoCriteriaMatches() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.REQUEST_NUMBER.val, "1"));
            expectedException.expect(UnsupportedOperationException.class);
            voltDBSessionDao.deleteSessionRule(criteria);
        }

        @Test
        public void deleteWithSessionDataAsCoreSessionIdWithExactArgument() throws IOException, ProcCallException {
            SessionData sessionData = new SessionDataImpl(SESSION_RULE_TABLE_NAME);
            sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx");
            int actualResult = voltDBSessionDao.deleteSessionRule(sessionData);
            assertEquals(1, actualResult);
            verify(voltDBClient, times(1)).callProcedure(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx");
        }

        @Test
        public void deleteWithSessionDataAsAfSessionIdWithExactArgument() throws IOException, ProcCallException {
            SessionData sessionData = new SessionDataImpl(SESSION_RULE_TABLE_NAME);
            sessionData.addValue(PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx");
            int actualResult = voltDBSessionDao.deleteSessionRule(sessionData);
            assertEquals(1, actualResult);
            verify(voltDBClient, times(1)).callProcedure(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx");
        }

        @Test
        public void deleteWithSessionDataAsGatewayAddressWithExactArgument() throws IOException, ProcCallException {
            SessionData sessionData = new SessionDataImpl(SESSION_RULE_TABLE_NAME);
            sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "af.elite.com");
            int actualResult = voltDBSessionDao.deleteSessionRule(sessionData);
            assertEquals(1, actualResult);
            verify(voltDBClient, times(1)).callProcedure(DELETE_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "af.elite.com");
        }

        @Test
        public void deleteWithSessionDataThrowsUnsupportedOperationExceptionWhenNoMatches() throws IOException, ProcCallException {
            SessionData sessionData = new SessionDataImpl(SESSION_RULE_TABLE_NAME);
            sessionData.addValue(PCRFKeyConstants.REQUEST_NUMBER.val, "1");
            expectedException.expect(UnsupportedOperationException.class);
            voltDBSessionDao.deleteSessionRule(sessionData);
        }

        @Test
        public void getWithCriteriaAsCoreSessionIdWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx"));
            voltDBSessionDao.getSessionRules(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx");
        }

        @Test
        public void getWithCriteriaAsAfSessionIdWithExactArgument() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx"));
            voltDBSessionDao.getSessionRules(criteria);
            verify(voltDBClient, times(1)).callProcedure(SELECT_SESSION_RULES_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_AF_SESSION_ID.val, "1234567890_rx");
        }

        @Test
        public void getWithCriteriaThrowsUnsupportedOperationExceptionWhenNoCriteriaMatches() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.REQUEST_NUMBER.val, "1"));
            expectedException.expect(UnsupportedOperationException.class);
            voltDBSessionDao.getSessionRules(criteria);
        }
    }


    public class CommonOperations{
        @Test
        public void isAliveOfVoltDBClientCalled(){
            voltDBSessionDao.isAlive();
            verify(voltDBClient, times(1)).isAlive();
        }

        @Test
        public void setMaxBatchSizeOfSessionConfigurationCalledOnCallingReloadSessionManagerConfiguration(){
            voltDBSessionDao.reloadSessionManagerConfiguration();
            verify(sessionConfiguration, times(1)).setMaxBatchSize(anyLong());
        }

        @Test
        public void getCriteriaByTableNameWillReturnCoreSessionCriteriaForCoreSessionSchema(){
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            assertEquals(CORE_SESS_TABLE_NAME, criteria.getSchemaName());
            assertTrue(criteria instanceof VoltDBCoreSessionCriteria);
        }

        @Test
        public void getCriteriaByTableNameWillReturnSimpleCriteriaForOtherSchema(){
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
            assertEquals(SESSION_RULE_TABLE_NAME, criteria.getSchemaName());
            assertTrue(criteria instanceof VoltDBSimpleCriteria);
        }

        @Test
        public void returnsNegativeOneOnProcCallExceptionInSaveOperation() throws IOException, ProcCallException {
            SessionData sessionData = SessionDaoTestUtil.createDummySessionDataForCoreSession();
            String[] dataArray = getDataArray(sessionData, coreSessionSchemaMapping);
            doThrow(ProcCallException.class).when(voltDBClient).callProcedure(SAVE_CORE_SESSION
                    , sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
                    , dataArray);

            int actualResult = voltDBSessionDao.saveCoreSession(sessionData);
            assertEquals(-1, actualResult);
        }


        @Test
        public void returnsNegativeOneOnProcCallExceptionInUpdateOperation() throws IOException, ProcCallException {
            SessionData sessionData = SessionDaoTestUtil.createDummySessionDataForCoreSession();
            String[] dataArray = getDataArray(sessionData, coreSessionSchemaMapping);
            doThrow(ProcCallException.class).when(voltDBClient).callProcedure(UPDATE_CORE_SESSION
                    , sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
                    , dataArray
                    , sessionData.getCreationTime());

            int actualResult = voltDBSessionDao.updateCoreSession(sessionData);
            assertEquals(-1, actualResult);
        }

        @Test
        public void returnsNegativeOneOnProcCallExceptionInDeleteOperation() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx"));
            doThrow(ProcCallException.class).when(voltDBClient).callProcedure(DELETE_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx");
            int actualResult = voltDBSessionDao.deleteCoreSession(criteria);
            assertEquals(-1, actualResult);
        }

        @Test
        public void returnsNullOnProcCallExceptionInSelectOperation() throws IOException, ProcCallException {
            Criteria criteria = voltDBSessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx"));
            doThrow(ProcCallException.class).when(voltDBClient).callProcedure(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE
                    , PCRFKeyConstants.CS_CORESESSION_ID.val, "1234567890_gx:Gx");
            assertNull(voltDBSessionDao.getCoreSessionList(criteria));
        }
    }

}
