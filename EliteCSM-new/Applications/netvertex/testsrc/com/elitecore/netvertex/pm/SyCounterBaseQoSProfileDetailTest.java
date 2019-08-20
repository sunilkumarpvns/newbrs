package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.factory.PCCRuleFactory;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.netvertex.core.util.Maps;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

@RunWith(HierarchicalContextRunner.class)
public class SyCounterBaseQoSProfileDetailTest {

    private QoSInformation qoSInformation = new QoSInformation();
    private SyCounterBaseQoSProfileDetailFactory qoSProfileDetailFactory;
    @Mock
    private QoSProfileDetail previousQoSProfileDetail;

    @Before
    public void setUp() {
        previousQoSProfileDetail = mock(QoSProfileDetail.class);
        BasePackage basePackage = Mockito.mock(BasePackage.class);
        when(basePackage.getId()).thenReturn(UUID.randomUUID().toString());
        when(basePackage.getPackageType()).thenReturn(PkgType.BASE);
        qoSInformation.startPackageQoSSelection(basePackage);
        qoSProfileDetailFactory = SyCounterBaseQoSProfileDetailFactory.create()
                .hasMandatoryCounterOf(CommonConstants.ALL_SERVICE_ID)
                .withRandomQoS();
    }

    @After
    public void printTrace() {
        getLogger().info("SyCounterBaseQoSProfileDetailTest", getPolicyContext(null).getTrace());
    }

    public class QoSSelectionWithoutCounter {
        private SyCounterBaseQoSProfileDetail qoSProfileDetail;

        private ChargingRuleBaseName crbn;

        @Before
        public void setUp() {

            DataServiceType dataServiceType = new DataServiceType(CommonConstants.ALL_SERVICE_ID, CommonConstants.ALL_SERVICE_NAME, 1, null, null);
            crbn = new ChargingRuleBaseName(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    Maps.newLinkedHashMap(Maps.Entry.newEntry("mk1", dataServiceType)),
                    0,
                    new HashMap<String, SliceInformation>());

            qoSProfileDetailFactory = SyCounterBaseQoSProfileDetailFactory.create().withRandomQoS().hasMandatoryCounterOf("HTTP");
            qoSProfileDetailFactory.withCRBN(crbn);
        }

        @Test
        public void selectionResultFULLY_APPLIEDWhenQoSProfileActionIsReject() {
            PCRFResponse pcrfResponse = new PCRFResponseBuilder().build();
            qoSProfileDetailFactory.withRejectAction("REJECTED");
            qoSProfileDetail = qoSProfileDetailFactory.build();

            PolicyContext policyContext = getPolicyContext(pcrfResponse);
            SelectionResult selectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            Assert.assertEquals(SelectionResult.FULLY_APPLIED, selectionResult);
        }

        @Test
        public void pccRuleSetsAndFullyAppliedWhenAvailable() {
            PCRFResponse pcrfResponse = new PCRFResponseBuilder().build();
            PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

            qoSProfileDetail = qoSProfileDetailFactory.addPCCRules(Arrays.asList(pccRule)).build();
            PolicyContext policyContext = getPolicyContext(pcrfResponse);
            SelectionResult selectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            Assert.assertEquals(qoSInformation.getPCCRules().entrySet().stream().findFirst().get().getValue(), pccRule);
            Assert.assertEquals(SelectionResult.FULLY_APPLIED, selectionResult);
        }

        @Test
        public void crbnSetsAndFullyAppliedWhenPreviousQoSSelectionNotFound() {
            PCRFResponse pcrfResponse = new PCRFResponseBuilder().build();

            qoSProfileDetail = qoSProfileDetailFactory.build();
            PolicyContext policyContext = getPolicyContext(pcrfResponse);
            SelectionResult selectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            Assert.assertEquals(qoSInformation.getChargingRuleBaseNames().get(0), crbn);
            Assert.assertEquals(SelectionResult.FULLY_APPLIED, selectionResult);
        }
    }

    //WITH PCC, WITH CRBN
    public class PCCRuleConfiguredAndCRBNConfigured {

        private SyCounterBaseQoSProfileDetail qoSProfileDetail;

        private ChargingRuleBaseName crbn;

        @Before
        public void setUp() {

            DataServiceType dataServiceType = new DataServiceType(CommonConstants.ALL_SERVICE_ID, CommonConstants.ALL_SERVICE_NAME, 1, null, null);
            crbn = new ChargingRuleBaseName(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    Maps.newLinkedHashMap(Maps.Entry.newEntry("mk1", dataServiceType)),
                    0,
                    new HashMap<String, SliceInformation>());

            qoSProfileDetailFactory.withCRBN(crbn);
            qoSProfileDetail = qoSProfileDetailFactory.build();
        }

        public class NotAppliedWhen {

            @Test
            public void AllServiceCounterNotSatisfiedAndPCCRuleNotSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").withServiceName("http").build();
                qoSProfileDetailFactory.hasMandatoryCounterOf("http").pccRules(Arrays.asList(pccRule));
                qoSProfileDetail = qoSProfileDetailFactory.build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterNotMatchWith(allServiceQuotaProfileDetail)
                        .addCounterNotMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);
                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.NOT_APPLIED, qoSSelectionResult);
                assertNull(qoSInformation.getQoSProfileDetail());
            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileSelectedAndPCCRuleNotSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();
                qoSProfileDetailFactory.hasMandatoryCounterOf("http").pccRules(Arrays.asList(pccRule));

                qoSProfileDetail = qoSProfileDetailFactory.build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterNotMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(previousQoSProfileDetail);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.NOT_APPLIED, qoSSelectionResult);
                assertEquals(previousQoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }
        }

        public class AppliedWhen {
            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileSelectedAndPCCRuleSatisfied() {
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").withServiceName("http").build();
                qoSProfileDetailFactory.hasMandatoryCounterOf("http").pccRules(Arrays.asList(pccRule));

                qoSProfileDetail = qoSProfileDetailFactory.build();
                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(previousQoSProfileDetail);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertNull(qoSInformation.getChargingRuleBaseNames());
            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileNotSelectedAndPCCRuleSatisfied() {
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").withServiceName("http").build();
                qoSProfileDetailFactory.hasMandatoryCounterOf("http").pccRules(Arrays.asList(pccRule));

                qoSProfileDetail = qoSProfileDetailFactory.build();
                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertSame(crbn, qoSInformation.getChargingRuleBaseNames().get(0));
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }

            @Test
            public void AllServiceCounterSatisfiedAndActionIsReject() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                qoSProfileDetail = qoSProfileDetailFactory.withRejectAction("test").build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);
                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }
        }

        public class PartiallyAppliedWhen {

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileNotSelectedAndPCCRuleNotSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").withServiceName("http").build();
                qoSProfileDetailFactory.hasMandatoryCounterOf("http").pccRules(Arrays.asList(pccRule));

                qoSProfileDetail = qoSProfileDetailFactory.build();
                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterNotMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileSelectedAndAtleastOnePCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail = SyCounterQuotaProfileDetailFactory.create("http").optionalCounter("http", "http").build();
                qoSProfileDetail = qoSProfileDetailFactory.addQuotaProfileDetail(syCounterBaseQuotaProfileDetail).addPCCRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .addCounterNotMatchWith(syCounterBaseQuotaProfileDetail)
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(previousQoSProfileDetail);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);

                Collectionz.filter(qoSProfileDetail.getPCCRules(), new Predicate<PCCRule>() {
                    @Override
                    public boolean apply(PCCRule input) {
                        return input.getServiceTypeId().equals("http") == false;
                    }
                });
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());

            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileNotSelectedAndAtleastOnePCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail = SyCounterQuotaProfileDetailFactory.create("http").optionalCounter("http", "http").build();
                qoSProfileDetail = qoSProfileDetailFactory.addQuotaProfileDetail(syCounterBaseQuotaProfileDetail).addPCCRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .addCounterNotMatchWith(syCounterBaseQuotaProfileDetail)
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);

                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());

                Collectionz.filter(qoSProfileDetail.getPCCRules(), new Predicate<PCCRule>() {
                    @Override
                    public boolean apply(PCCRule input) {
                        return input.getServiceTypeId().equals("http") == false;
                    }
                });
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());

            }

            @Test
            public void AllServiceCounterNotSatisfiedAndAtleastOnePCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail = SyCounterQuotaProfileDetailFactory.create("http").optionalCounter("http", "http").build();
                qoSProfileDetail = qoSProfileDetailFactory.addQuotaProfileDetail(syCounterBaseQuotaProfileDetail).addPCCRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterNotMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .addCounterNotMatchWith(syCounterBaseQuotaProfileDetail)
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());

                Collectionz.filter(qoSProfileDetail.getPCCRules(), new Predicate<PCCRule>() {
                    @Override
                    public boolean apply(PCCRule input) {
                        return input.getServiceTypeId().equals("http") == false && input.getServiceTypeId().equals(CommonConstants.ALL_SERVICE_ID) == false;
                    }
                });
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());
            }

            @Test
            public void AllServiceCounterNotSatisfiedAndPCCRuleSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").withServiceName("http").build();
                qoSProfileDetailFactory.hasMandatoryCounterOf("http").pccRules(Arrays.asList(pccRule));

                qoSProfileDetail = qoSProfileDetailFactory.build();
                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterNotMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }


        }
    }

    //WITH PCC, NO CRBN
    public class PCCRuleConfiguredAndCRBNNotConfigured {

        private SyCounterBaseQoSProfileDetail qoSProfileDetail;

        public class AppliedWhen {

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileNotSelectedAndPCCRuleSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                qoSProfileDetail = qoSProfileDetailFactory.hasOptionalCounterOf("http").pccRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());
            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileSelectedAndPCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                qoSProfileDetail = qoSProfileDetailFactory.build();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(qoSProfileDetail.getAllServiceQuotaProfileDetail())
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(previousQoSProfileDetail);
                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
            }

            @Test
            public void AllServiceCounterSatisfiedAndActionIsReject() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                qoSProfileDetail = qoSProfileDetailFactory.withRejectAction("test").build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);
                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }

        }

        public class NotAppliedWhen {

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileSelectedAndNoPCCRuleSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                qoSProfileDetail = qoSProfileDetailFactory.hasOptionalCounterOf("http").pccRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterNotMatchWith(allServiceQuotaProfileDetail)
                        .addCounterNotMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);
                qoSInformation.setQoSProfileDetail(previousQoSProfileDetail);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
                assertEquals(SelectionResult.NOT_APPLIED, qoSSelectionResult);
                assertEquals(previousQoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileNotSelectedAndNoPCCRuleSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                qoSProfileDetail = qoSProfileDetailFactory.hasOptionalCounterOf("http").pccRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterNotMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.NOT_APPLIED, qoSSelectionResult);
                assertNull(qoSInformation.getQoSProfileDetail());
                assertNull(qoSInformation.getPCCRules());
            }

            @Test
            public void AllServiceCounterNotSatisfiedAndNoPCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                qoSProfileDetail = qoSProfileDetailFactory.build();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterNotMatchWith(qoSProfileDetail.getAllServiceQuotaProfileDetail())
                        .addCounterNotMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);
                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.NOT_APPLIED, qoSSelectionResult);
                assertNull(qoSInformation.getQoSProfileDetail());
                assertNull(qoSInformation.getPCCRules());

            }

        }

        public class PartiallyAppliedWhen {
            @Test
            public void AllServiceCounterNotSatisfiedAndPCCRuleSatisfied() {

                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail = SyCounterQuotaProfileDetailFactory.create("http").optionalCounter("http", "http").build();
                qoSProfileDetail = qoSProfileDetailFactory.quotaProfileDetail(syCounterBaseQuotaProfileDetail).pccRules(Arrays.asList(pccRule)).build();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());

            }


            @Test
            public void AllServiceCounterNotSatisfiedAndAtleastPCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail = SyCounterQuotaProfileDetailFactory.create("http").optionalCounter("http", "http").build();
                qoSProfileDetail = qoSProfileDetailFactory.addQuotaProfileDetail(syCounterBaseQuotaProfileDetail).addPCCRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterNotMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .addCounterNotMatchWith(syCounterBaseQuotaProfileDetail)
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());

                Collectionz.filter(qoSProfileDetail.getPCCRules(), new Predicate<PCCRule>() {
                    @Override
                    public boolean apply(PCCRule input) {
                        return input.getServiceTypeId().equals("http") == false && input.getServiceTypeId().equals(CommonConstants.ALL_SERVICE_ID) == false;
                    }
                });
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());
            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileSelectedAndAtleastOnePCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail = SyCounterQuotaProfileDetailFactory.create("http").optionalCounter("http", "http").build();
                qoSProfileDetail = qoSProfileDetailFactory.addQuotaProfileDetail(syCounterBaseQuotaProfileDetail).addPCCRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .addCounterNotMatchWith(syCounterBaseQuotaProfileDetail)
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(previousQoSProfileDetail);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);

                Collectionz.filter(qoSProfileDetail.getPCCRules(), new Predicate<PCCRule>() {
                    @Override
                    public boolean apply(PCCRule input) {
                        return input.getServiceTypeId().equals("http") == false;
                    }
                });
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());

            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileNotSelectedAndAtleastOnePCCRuleSatisfied() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

                SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail = SyCounterQuotaProfileDetailFactory.create("http").optionalCounter("http", "http").build();
                qoSProfileDetail = qoSProfileDetailFactory.addQuotaProfileDetail(syCounterBaseQuotaProfileDetail).addPCCRules(Arrays.asList(pccRule)).build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .addCounterNotMatchWith(syCounterBaseQuotaProfileDetail)
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);

                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());

                Collectionz.filter(qoSProfileDetail.getPCCRules(), new Predicate<PCCRule>() {
                    @Override
                    public boolean apply(PCCRule input) {
                        return input.getServiceTypeId().equals("http") == false;
                    }
                });
                assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());

            }
        }
    }

    private PolicyContext getPolicyContext(PCRFResponse pcrfResponse) {
        return new PCRFPolicyContextImpl(new PCRFRequestImpl(),
                pcrfResponse,
                null,
                null,
        null, null);
    }

    //NO PCC, WITH CRBN
    public class PCCRuleNotConfiguredAndCRBNConfigured {

        private SyCounterBaseQoSProfileDetail qoSProfileDetail;
        private ChargingRuleBaseName crbn;

        @Before
        public void setUp() {
            DataServiceType dataServiceType = new DataServiceType(CommonConstants.ALL_SERVICE_ID, CommonConstants.ALL_SERVICE_NAME, 1, null, null);
            crbn = new ChargingRuleBaseName(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    Maps.newLinkedHashMap(Maps.Entry.newEntry("mk1", dataServiceType)),
                    0,
                    null);

            qoSProfileDetailFactory.withCRBN(crbn);
            qoSProfileDetail = qoSProfileDetailFactory.build();
        }

        public class AppliedWhen {
            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileSelected() {

                PCRFResponse pcrfResponse = new PCRFResponseBuilder().
                        addCounterMatchWith(qoSProfileDetail.getAllServiceQuotaProfileDetail()).build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(previousQoSProfileDetail);
                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);
                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
            }

            @Test
            public void AllServiceCounterSatisfiedAndPreviousQoSProfileNotSelected() {

                PCRFResponse pcrfResponse = new PCRFResponseBuilder().
                        addCounterMatchWith(qoSProfileDetail.getAllServiceQuotaProfileDetail()).build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertNotNull(qoSInformation.getChargingRuleBaseNames());
                assertSame(crbn, qoSInformation.getChargingRuleBaseNames().get(0));
            }

            @Test
            public void AllServiceCounterSatisfiedAndActionIsReject() {

                qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
                qoSProfileDetail = qoSProfileDetailFactory.withRejectAction("test").build();

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                        .addCounterMatchWith(allServiceQuotaProfileDetail)
                        .addCounterMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                        .build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);
                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }
        }

        public class NotAppliedWhen {
            @Test
            public void AllServiceCounterNotSatisfied() {

                PCRFResponse pcrfResponse = new PCRFResponseBuilder().
                        addCounterNotMatchWith(qoSProfileDetail.getAllServiceQuotaProfileDetail()).build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.NOT_APPLIED, qoSSelectionResult);
                assertNull(qoSInformation.getQoSProfileDetail());
            }
        }
    }

    //NO PCC, NO CRBN
    public class PCCRuleNotConfiguredAndCRBNNotConfigured {

        private SyCounterBaseQoSProfileDetail qoSProfileDetail;

        @Before
        public void setUp() {
            qoSProfileDetail = qoSProfileDetailFactory.build();
        }

        public class AppliedWhen {
            @Test
            public void AllServiceCounterSatisfied() {

                PCRFResponse pcrfResponse = new PCRFResponseBuilder().
                        addCounterMatchWith(qoSProfileDetail.getAllServiceQuotaProfileDetail()).build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }

            @Test
            public void AllServiceCounterSatisfiedAndActionIsReject() {

                qoSProfileDetail = qoSProfileDetailFactory.withRejectAction("test").build();

                PCRFResponse pcrfResponse = new PCRFResponseBuilder().
                        addCounterMatchWith(qoSProfileDetail.getAllServiceQuotaProfileDetail()).build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
                assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
            }
        }

        public class NotAppliedWhen {
            @Test
            public void AllServiceCounterNotSatisfied() {

                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();
                PCRFResponse pcrfResponse = new PCRFResponseBuilder().
                        addCounterNotMatchWith(allServiceQuotaProfileDetail).build();

                PolicyContext policyContext = getPolicyContext(pcrfResponse);

                qoSInformation.setQoSProfileDetail(null);

                SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

                assertEquals(SelectionResult.NOT_APPLIED, qoSSelectionResult);
                assertNull(qoSInformation.getQoSProfileDetail());
            }
        }
    }

    public class Global {

        private SyCounterBaseQoSProfileDetail qoSProfileDetail;

        @Test
        public void AllServiceCounterIsOptionalAndCounterNotReceived() {

            qoSProfileDetail = SyCounterBaseQoSProfileDetailFactory.create()
                    .hasOptionalCounterOf(CommonConstants.ALL_SERVICE_ID)
                    .withRandomQoS()
                    .build();

            PCRFResponse pcrfResponse = new PCRFResponseBuilder().build();


            PolicyContext policyContext = new PCRFPolicyContextImpl(new PCRFRequestImpl(),
                    pcrfResponse,
                    null,
                    null,
                    null, null);
            SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            Assert.assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
            Assert.assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
        }


        @Test
        public void AppliedWhenAllServiceCounterIsOptionalAndCounterNotReceived() {

            qoSProfileDetail = SyCounterBaseQoSProfileDetailFactory.create()
                    .hasOptionalCounterOf(CommonConstants.ALL_SERVICE_ID)
                    .withRandomQoS()
                    .build();

            PCRFResponse pcrfResponse = new PCRFResponseBuilder().build();

            PolicyContext policyContext = getPolicyContext(pcrfResponse);
            SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertEquals(SelectionResult.FULLY_APPLIED, qoSSelectionResult);
            assertEquals(qoSProfileDetail, qoSInformation.getQoSProfileDetail());
        }

        @Test
        public void UserAllServiceCounterToEvaluatePCCIfCounterNotConfiguredForThatPCCService() {

            qoSProfileDetailFactory.hasMandatoryCounterOf("P2P").forEachServicesHasPCCRule();
            PCCRule pccRule = PCCRuleFactory.createPCCRuleWithRandomQoS().withServiceTypeId("http").build();

            qoSProfileDetail = qoSProfileDetailFactory.addPCCRules(Arrays.asList(pccRule)).build();

            SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail = qoSProfileDetail.getAllServiceQuotaProfileDetail();

            PCRFResponse pcrfResponse = new PCRFResponseBuilder()
                    .addCounterMatchWith(allServiceQuotaProfileDetail)
                    .addCounterNotMatchWith(qoSProfileDetail.getServiceToQuotaProfileDetail())
                    .build();

            PolicyContext policyContext = getPolicyContext(pcrfResponse);
            SelectionResult qoSSelectionResult = qoSProfileDetail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED);

            assertEquals(SelectionResult.PARTIALLY_APPLIED, qoSSelectionResult);
            assertEquals(qoSInformation.getQoSProfileDetail(), qoSProfileDetail);

            Collectionz.filter(qoSProfileDetail.getPCCRules(), new Predicate<PCCRule>() {
                @Override
                public boolean apply(PCCRule input) {
                    return input.getServiceTypeId().equals(CommonConstants.ALL_SERVICE_ID) || input.getServiceTypeId().equals("http");
                }
            });
            assertLenientEquals(qoSProfileDetail.getPCCRules(), qoSInformation.getPCCRules().values());

        }

    }
}
