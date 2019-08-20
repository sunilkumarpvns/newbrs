package com.elitecore.netvertex.pm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl.PCCRuleBuilder;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.util.Maps;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(HierarchicalContextRunner.class)
public class RateCardBasedQoSProfileDetailTest {
    public static final String INR = "INR";
    private final String name = "rateCardName";
    private final String id = "id";
    private DataRateCard dataRateCard;
    private static final String keyOne = "k1";
    private static final String keyTwo = "k2";
    private PolicyContext policyContext;
    private QoSInformation qoSInformation;
    private RateCardBasedQoSProfileDetail detail;
    private Uom pulseUom = Uom.SECOND;
    private Uom rateUom = Uom.SECOND;
    private List<PCCRule> pccRules;
    private List<ChargingRuleBaseName> chargingRuleBaseNames;

    @Before
    public void setUp() {
        dataRateCard = spy(new DataRateCard(id, name, keyOne, keyTwo, Collections.emptyList(), pulseUom, rateUom));
        qoSInformation = new QoSInformation();
        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        policyContext = spy(new PCRFPolicyContextImpl(pcrfRequest, pcrfResponse,
                new MockBasePackage(),
                new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR),
                new PCRFQoSProcessor(qoSInformation), new DummyPolicyRepository()));

        PCCRuleImpl pccRule1 = new PCCRuleBuilder("pcc1", "name1").withServiceIdentifier(1).withQci(QCI.QCI_GBR_1).build();
        PCCRuleImpl pccRule2 = new PCCRuleBuilder("pcc2", "name2").withServiceIdentifier(2).withQci(QCI.QCI_GBR_1).build();
        pccRules = new ArrayList<>();
        pccRules.add(pccRule1);
        pccRules.add(pccRule2);
        chargingRuleBaseNames = new ArrayList<>();
        chargingRuleBaseNames.add(createCRBN());
        chargingRuleBaseNames.add(createCRBN());

        detail = new RateCardBasedQoSProfileDetailBuilder().withDataRateCard(dataRateCard).withPCCRules(pccRules).withChargingRuleBaseNames(chargingRuleBaseNames).build();
        qoSInformation.startPackageQoSSelection(new MockBasePackage());
    }

    public ChargingRuleBaseName createCRBN() {
        DataServiceType dataServiceType = new DataServiceType(CommonConstants.ALL_SERVICE_ID, CommonConstants.ALL_SERVICE_NAME, 1, null, null);
        return new ChargingRuleBaseName(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Maps.newLinkedHashMap(Maps.Entry.newEntry("mk1", dataServiceType)),
                0,
                new HashMap<String, SliceInformation>());
    }

    public class applyReturnsFullyAppliedWhen {
        @Test
        public void rateCardIsApplicable() {
            doReturn(true).when(dataRateCard).isApplicable(policyContext, qoSInformation);

            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
            qoSInformation.endQoSSelectionProcess();

            assertSame(SelectionResult.FULLY_APPLIED, selectionResult);
            assertSame(detail, qoSInformation.getQoSProfileDetail());
            assertReflectionEquals(pccRules, qoSInformation.getPCCRules().values());
            assertReflectionEquals(chargingRuleBaseNames, qoSInformation.getChargingRuleBaseNames());
        }

        @Test
        public void qosProfileActionIsReject() {
            doReturn(true).when(dataRateCard).isApplicable(policyContext, qoSInformation);

            detail = new RateCardBasedQoSProfileDetailBuilder().withDataRateCard(dataRateCard).withAction(QoSProfileAction.REJECT).buildWithAction();
            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
            qoSInformation.endQoSSelectionProcess();

            assertEquals(SelectionResult.FULLY_APPLIED, selectionResult);
        }
    }

    public class applyReturnsNotAppliedWhen {

        @Test
        public void rateCardIsNotApplicable() {
            doReturn(false).when(dataRateCard).isApplicable(policyContext, qoSInformation);
            SelectionResult selectionResult = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertSame(SelectionResult.NOT_APPLIED, selectionResult);
            qoSInformation.endProcess();

            assertNull(qoSInformation.getQoSProfileDetail());
            assertNull(qoSInformation.getPCCRules());
            assertNull(qoSInformation.getChargingRuleBaseNames());
        }
    }
}