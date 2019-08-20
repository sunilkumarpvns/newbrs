package com.elitecore.netvertex.gateway.diameter.af;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.google.gson.JsonObject;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.*;

/**
 * Created by chetan on 30/5/17.
 */
@RunWith(HierarchicalContextRunner.class)
public class AFSessionRuleTest {

    public static final String PCC_1 = "pcc1";
    public static final String MEDIA_TYPE_VALUE = "1";
    public static final String MEDIA_COMP_NUMBER = "1";
    public static final String FLOW_NUMBER = "1";
    public static final String DOWNFLOW = "downflow";
    public static final String UPLINKFLOW = "uplinkflow";
    public static final String AFSESSIONID = "afsessionid";
    public static final String SCHEMA_NAME = "schemaName";
    public static final String SR_ID = "srId";

    @Before
    public void setUp() throws Exception {
    }


    @Test(expected = NullPointerException.class)
    public void test_create_should_throw_NPE_when_null_sessionData_passed() {
        SessionData sessionData = null;
        AFSessionRule.create(sessionData);
    }

    public class Test_CreateAFSessionRule_when_NoValueFoundForRespectiveKeysFromSessionData {

        private AFSessionRule actualAfSessionRule;

        @Before
        public void createAFSessionRule() {
            SessionData sessionData = new SessionDataImpl("schema");
            actualAfSessionRule = AFSessionRule.create(sessionData);
        }

        @Test
        public void flowStatus_is_Null() {
            assertNull(actualAfSessionRule.getFlowStatus());
        }

        @Test
        public void srId_is_Null() {
            assertNull(actualAfSessionRule.getSrId());
        }

        @Test
        public void qci_is_Null() {
            assertNull(actualAfSessionRule.getQci());
        }

        @Test
        public void downlinkFlow_is_Null() {
            assertNull(actualAfSessionRule.getDownlinkFlow());
        }

        @Test
        public void uplinkFlow_is_Null() {
            assertNull(actualAfSessionRule.getUplinkFlow());
        }

        @Test
        public void afSessionId_is_Null() {
            assertNull(actualAfSessionRule.getAfSessionId());
        }

        @Test
        public void pccRule_is_Null() {
            assertNull(actualAfSessionRule.getPccRule());
        }

        @Test
        public void mbrdl_is_zero() {
            assertEquals(0, actualAfSessionRule.getMbrdl());
        }

        @Test
        public void flowNumber_is_zero() {
            assertEquals(0, actualAfSessionRule.getFlowNumber());
        }

        @Test
        public void mediaComponentNumber_is_zero() {
            assertEquals(0, actualAfSessionRule.getMediaComponentNumber());
        }

        @Test
        public void mediaType_is_zero() {
            assertEquals(0, actualAfSessionRule.getMediaType());
        }

        @Test
        public void mbrul_is_zero() {
            assertEquals(0, actualAfSessionRule.getMbrul());
        }

        @Test
        public void gbrdl_is_zero() {
            assertEquals(0, actualAfSessionRule.getGbrdl());
        }

        @Test
        public void gbrul_is_zero() {
            assertEquals(0, actualAfSessionRule.getGbrul());
        }
    }

    public class Test_CreateAFSessionRule_when_EmptyPCCJsonFound {

        private AFSessionRule actualAfSessionRule;

        @Before
        public void createAFSessionRule() {
            SessionData sessionData = new SessionDataImpl("schema");
            sessionData.addValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.val, "{}");
            actualAfSessionRule = AFSessionRule.create(sessionData);
        }

        @Test
        public void flowStatus_is_Null() {
            assertNull(actualAfSessionRule.getFlowStatus());
        }

        @Test
        public void qci_is_Null() {
            assertNull(actualAfSessionRule.getQci());
        }

        @Test
        public void mbrdl_is_zero() {
            assertEquals(0, actualAfSessionRule.getMbrdl());
        }

        @Test
        public void mbrul_is_zero() {
            assertEquals(0, actualAfSessionRule.getMbrul());
        }

        @Test
        public void gbrdl_is_zero() {
            assertEquals(0, actualAfSessionRule.getGbrdl());
        }

        @Test
        public void gbrul_is_zero() {
            assertEquals(0, actualAfSessionRule.getGbrul());
        }
    }

    @Test
    public void test_create_AfFSessionRule_with_valid_pcc_attributes_when_PCCAdditionalAttributeFoundFromSessionData() {

        SessionData sessionData = new SessionDataImpl("schema");

        String pccJsonString = "{'"+PCRFKeyConstants.PCC_RULE_MBRDL.val+"':'20'," +
                "'"+PCRFKeyConstants.PCC_RULE_MBRUL.val+"':'20'," +
                "'"+PCRFKeyConstants.PCC_RULE_GBRDL.val+"':'30'," +
                "'"+PCRFKeyConstants.PCC_RULE_GBRUL.val+"':'40'," +
                "'"+PCRFKeyConstants.PCC_RULE_FLOW_STATUS.val+"':'1'," +
                "'"+PCRFKeyConstants.PCC_RULE_QCI.val+"':'1'}";

        sessionData.addValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.val, pccJsonString);

        AFSessionRule actualAfSessionRule = AFSessionRule.create(sessionData);

        AFSessionRule expectedSessionRule = new AFSessionRule(sessionData.getSessionId(),
                0,
                0,
                null,
                null,
                null,
                0,
                null,
                20, 20, 30, 40, QCI.QCI_GBR_1, FlowStatus.ENABLED_DOWNLINK);


        assertLenientEquals(expectedSessionRule, actualAfSessionRule);
    }

    @Test
    public void test_create_AfSessionRule_with_provided_SessionData() {

        SessionData sessionData = new SessionDataImpl("schemaName", SR_ID, null, null);

        sessionData.addValue(PCRFKeyConstants.PCC_RULE_LIST.val, PCC_1);
        sessionData.addValue(PCRFKeyConstants.CS_MEDIA_TYPE.val, MEDIA_TYPE_VALUE);
        sessionData.addValue(PCRFKeyConstants.CS_MEDIA_COMPONENT_NUMBER.val, MEDIA_COMP_NUMBER);
        sessionData.addValue(PCRFKeyConstants.CS_FLOW_NUMBSER.val, FLOW_NUMBER);
        sessionData.addValue(PCRFKeyConstants.CS_DOWNLINK_FLOW.val, DOWNFLOW);
        sessionData.addValue(PCRFKeyConstants.CS_UPLINK_FLOW.val, UPLINKFLOW);
        sessionData.addValue(PCRFKeyConstants.CS_AF_SESSION_ID.val, AFSESSIONID);

        AFSessionRule expectedSessionRule = new AFSessionRule(SR_ID,
                Long.parseLong(FLOW_NUMBER)
                , Long.parseLong(MEDIA_TYPE_VALUE)
                , PCC_1
                , UPLINKFLOW
                , DOWNFLOW
                , Long.parseLong(MEDIA_COMP_NUMBER)
                , AFSESSIONID
                ,0
                ,0
                ,0
                ,0
                , null
                , null);

        AFSessionRule actualAfSessionRule = AFSessionRule.create(sessionData);

        assertLenientEquals(expectedSessionRule, actualAfSessionRule);
    }

    @Test
    public void test_toSessionData_adds_respectiveKesy_of_AFSessionRule() {
        AFSessionRule afSessionRule = new AFSessionRule(SR_ID, 1, 2, PCC_1, UPLINKFLOW, DOWNFLOW, 2, AFSESSIONID, 10,20,30,40, QCI.QCI_GBR_1, FlowStatus.ENABLED_DOWNLINK);

        Date creationDate = Calendar.getInstance().getTime();
        Date updateDate = Calendar.getInstance().getTime();

        SessionData actualSessionData = new SessionDataImpl(SCHEMA_NAME, creationDate, updateDate);
        afSessionRule.toSessionData(actualSessionData);
        SessionData expectedSessionData = createExpectedSessionData(afSessionRule, creationDate, updateDate);
        assertReflectionEquals(expectedSessionData, actualSessionData);

    }

    public class Test_JSONKeyShouldNotBeCreated_When_RespectiveValueIsNotAssigned {
        private JsonObject jsonObject;

        @Before
        public void createAFSessionRule() {

            SessionData sessionData = new SessionDataImpl("schema");
            AFSessionRule afSessionRule = new AFSessionRule(SR_ID,
                    1,
                    2,
                    PCC_1,
                    UPLINKFLOW,
                    DOWNFLOW,
                    2,
                    AFSESSIONID,
                    0,
                    0,
                    0,
                    0,
                    null,
                    null);

            afSessionRule.toSessionData(sessionData);
            String additinalParametersJson = sessionData.getValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.val);
            jsonObject = GsonFactory.defaultInstance().fromJson(additinalParametersJson, JsonObject.class);
        }

        @Test
        public void flowStatus_key_not_exist() {
            assertNull(jsonObject.get(PCRFKeyConstants.PCC_RULE_FLOW_STATUS.val));
        }

        @Test
        public void qci_key_not_exist() {
            assertNull(jsonObject.get(PCRFKeyConstants.PCC_RULE_QCI.val));
        }

        @Test
        public void mbrdl_key_not_exist() {
           assertNull(jsonObject.get(PCRFKeyConstants.PCC_RULE_MBRDL.val));
        }

        @Test
        public void mbrul_key_not_exist() {
            assertNull(jsonObject.get(PCRFKeyConstants.PCC_RULE_MBRUL.val));
        }

        @Test
        public void gbrdl_key_not_exist() {
            assertNull(jsonObject.get(PCRFKeyConstants.PCC_RULE_GBRDL.val));
        }

        @Test
        public void gbrul_key_not_exist() {
            assertNull(jsonObject.get(PCRFKeyConstants.PCC_RULE_GBRUL.val));
        }
    }

    private SessionData createExpectedSessionData(AFSessionRule afSessionRule, Date creationDate, Date updateDate) {

        SessionData expectedSessionData = new SessionDataImpl(SCHEMA_NAME, creationDate, updateDate);
        expectedSessionData.addValue(PCRFKeyConstants.CS_MEDIA_COMPONENT_NUMBER.val, String.valueOf(afSessionRule.getMediaComponentNumber()));
        expectedSessionData.addValue(PCRFKeyConstants.CS_MEDIA_TYPE.val, String.valueOf(afSessionRule.getMediaType()));
        expectedSessionData.addValue(PCRFKeyConstants.CS_FLOW_NUMBSER.val, String.valueOf(afSessionRule.getFlowNumber()));
        expectedSessionData.addValue(PCRFKeyConstants.PCC_RULE_LIST.val, PCC_1);
        expectedSessionData.addValue(PCRFKeyConstants.CS_AF_SESSION_ID.val, afSessionRule.getAfSessionId());
        expectedSessionData.addValue(PCRFKeyConstants.CS_UPLINK_FLOW.val, afSessionRule.getUplinkFlow());
        expectedSessionData.addValue(PCRFKeyConstants.CS_DOWNLINK_FLOW.val, afSessionRule.getDownlinkFlow());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_MBRDL.val, afSessionRule.getMbrdl());
        jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_MBRUL.val, afSessionRule.getMbrul());
        jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_GBRDL.val, afSessionRule.getGbrdl());
        jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_GBRUL.val, afSessionRule.getGbrul());

        if (afSessionRule.getQci() != null) {
            jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_QCI.val, String.valueOf(afSessionRule.getQci().val));
        }

        if (afSessionRule.getFlowStatus() != null ) {
            jsonObject.addProperty(PCRFKeyConstants.PCC_RULE_FLOW_STATUS.val, String.valueOf(afSessionRule.getFlowStatus().val));
        }

        expectedSessionData.addValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.val, jsonObject.toString());

        return expectedSessionData;
    }
}