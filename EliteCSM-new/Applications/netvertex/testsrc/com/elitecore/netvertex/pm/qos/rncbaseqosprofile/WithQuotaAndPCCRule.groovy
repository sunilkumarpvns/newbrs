package com.elitecore.netvertex.pm.qos.rncbaseqosprofile

import com.elitecore.commons.base.FixedTimeSource
import com.elitecore.commons.io.IndentingPrintWriter
import com.elitecore.corenetvertex.constants.*
import com.elitecore.corenetvertex.pkg.PkgType
import com.elitecore.corenetvertex.pm.constants.SelectionResult
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp
import com.elitecore.corenetvertex.spr.MonetaryBalance
import com.elitecore.corenetvertex.spr.MonetaryBalanceBuilder
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl
import com.elitecore.corenetvertex.spr.data.Subscription
import com.elitecore.corenetvertex.spr.data.SubscriptionType
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException
import com.elitecore.netvertex.pm.*
import com.elitecore.netvertex.pm.qos.rnc.RnCBaseQoSProfileDetail
import com.elitecore.netvertex.service.pcrf.PCRFResponse
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl
import org.unitils.reflectionassert.ReflectionAssert
import spock.lang.Specification
import spock.lang.Stepwise

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

import static com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilder.balanceIs
import static com.elitecore.netvertex.pm.ServiceInformation.service

@Stepwise
class WithQuotaAndPCCRule extends Specification {

    public static final String OPERATION__FAILED = "OPERATION_FAILED"
    private QoSInformation qoSInformation;
    private PolicyContext policyContext;
    private DummyPolicyRepository dummyPolicyRepository;

    private RnCQoSProfileDetailFactory.RnCQoSProfileDetialBuilder builder;
    private RnCQoSProfileDetailFactory.RnCQoSProfileDetialBuilder builderWithRejectAction;
    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;
    private SubscriberMonetaryBalance subscriberMonitoryBalance;
    private String packageId;
    private PCRFResponse pcrfResponse;

    private List<Subscription> preTopUpsubscriptions;
    private List<Subscription> spareTopUpsubscriptions;

    private SPRInfoImpl sprInfo;
    private Calendar calendar;
    private List<ChargingRuleBaseName> chargingRuleBaseNameList;
    def setup() {
        packageId = UUID.randomUUID().toString()
        qoSInformation = Spy(new QoSInformation())

        dummyPolicyRepository = Spy(new DummyPolicyRepository())
        pcrfResponse = new PCRFResponseImpl()

        policyContext = Mock(PolicyContext)
        builderWithRejectAction = RnCQoSProfileDetailFactory.createQoSProfile().rejectAction("REJECTED").hasRnCQuota()
        builder = RnCQoSProfileDetailFactory.createQoSProfile().hasRnCQuota()
        sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity("123")
        policyContext.getSPInfo() >> sprInfo
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());

        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Collections.emptyList());
        subscriberMonitoryBalance = Spy(SubscriberMonetaryBalance.class, constructorArgs:[FixedTimeSource.systemTimeSource()]);
        policyContext.getCurrentBalance() >> subscriberNonMonitoryBalance;


        calendar = Calendar.getInstance();
        policyContext.getCurrentTime() >> calendar;
        policyContext.getPCRFResponse() >> pcrfResponse;

        BasePackage basePackage = Mock(BasePackage);
        basePackage.getPackageType() >> PkgType.BASE
        basePackage.getId() >> packageId
        qoSInformation.startPackageQoSSelection(basePackage)

        preTopUpsubscriptions =  [];
        spareTopUpsubscriptions = [];

        policyContext.getPreTopUpSubscriptions() >> preTopUpsubscriptions
        policyContext.getSpareTopUpSubscriptions() >> spareTopUpsubscriptions
        policyContext.getPolicyRepository() >> dummyPolicyRepository
    }




    def "Not Applied when all-session quota exceeded"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        and:
        ServiceInformation http = service("test","http", packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }


        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        and:

        subscriberNonMonitoryBalance << http.serviceBalance << allService.serviceBalance


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == null
        result == SelectionResult.NOT_APPLIED

    }

    def "Not Applied when all-service quota not defined and pcc rule service quota exceeded"() {

        given:


        ServiceInformation http = service("test","http", packageId, 0d) {packageId ->
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
            pcc()
        }

        and:
        RnCBaseQoSProfileDetail detail = builder.quotaProfileDetail(http.rncQuota).pccRules([http.pccRule]).build();


        and:
        subscriberNonMonitoryBalance << http.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()


        then:
        result == SelectionResult.NOT_APPLIED
        qoSInformation.qoSProfileDetail == null

    }




    def "Fully selected when all-service and pcc rule service quota is not exceeded"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        and:
        ServiceInformation http = service("test","http", packageId, 0d) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        and:
        subscriberNonMonitoryBalance << http.serviceBalance << allService.serviceBalance;


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        data.qosProfileDetail == detail
        data.pccRules != null
        data.pccRules.values()[0] == http.pccRule
        result == SelectionResult.FULLY_APPLIED
    }

    /**
     * ALL Service rate kept zero, to check service specific rating scenario
     *
     * @return
     */
    def "Fully Applied when all-service and pcc rule service quota is not exceeded, rate is defined and monetary balance is exist"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(10)
                .build()
        subscriberMonitoryBalance.addMonitoryBalances(monetaryBalance)


        and:
        ServiceInformation http = service("test","http", packageId, 10) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build()

        and:
        policyContext.getCurrentMonetaryBalance() >> subscriberMonitoryBalance
        subscriberNonMonitoryBalance << http.serviceBalance << allService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        0 * monetaryBalance.isDataBalanceExist() // Because rate is not configured for all service.
        result == SelectionResult.FULLY_APPLIED
    }

    def "Topup not applied when error fetching topup balance"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now

                info.subscriptionId = subscriptionId;
            })
            pcc()
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        def policyContext = Mock(PolicyContext)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        policyContext.getSPInfo() >> sprInfo
        policyContext.getPCRFResponse() >> pcrfResponse;
        policyContext.getCurrentTime() >> calendar;
        policyContext.getPreTopUpSubscriptions() >> preTopUpsubscriptions;
        policyContext.getPolicyRepository() >> dummyPolicyRepository
        policyContext.getCurrentBalance() >>> subscriberNonMonitoryBalance >> {
            throw new OperationFailedException(OPERATION__FAILED)
        }
        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota]).pccRules([basePackageAllService.pccRule])
                .build()


        and:
        MockQuotaTopUp quotaTopUp = createTopUp(topUpAllService)
        setUpPreTopUpSubscription(quotaTopUp, subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), basePackageAllService.getTotalBalance(policyContext))
    }

    def "Selection Result fully applied when no pcc rule applied"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now

                info.subscriptionId = subscriptionId;
            })
        }

        def policyContext = Mock(PolicyContext)
        def subscriberNonMonetaryBalance = Mock(SubscriberNonMonitoryBalance)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        policyContext.getSPInfo() >> sprInfo
        policyContext.getPCRFResponse() >> pcrfResponse;
        policyContext.getCurrentTime() >> calendar;
        policyContext.getPreTopUpSubscriptions() >> preTopUpsubscriptions;
        policyContext.getPolicyRepository() >> dummyPolicyRepository
        policyContext.getCurrentBalance() >> subscriberNonMonetaryBalance
        subscriberNonMonetaryBalance.getPackageBalance(packageId) >> null

        and:
        subscriberNonMonitoryBalance << basePackageAllService.serviceBalance
        qoSInformation.setUsageException(new OperationFailedException("test"))

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota])
                .build()

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        result == SelectionResult.FULLY_APPLIED
    }

    def "crbn configured and previous qos selection was not found then set crbn"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now

                info.subscriptionId = subscriptionId;
            })
            pcc()
        }

        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)

        def policyContext = Mock(PolicyContext)
        def subscriberNonMonetaryBalance = Mock(SubscriberNonMonitoryBalance)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        policyContext.getSPInfo() >> sprInfo
        policyContext.getPCRFResponse() >> pcrfResponse;
        policyContext.getCurrentTime() >> calendar;
        policyContext.getPreTopUpSubscriptions() >> preTopUpsubscriptions;
        policyContext.getPolicyRepository() >> dummyPolicyRepository
        policyContext.getCurrentBalance() >> subscriberNonMonetaryBalance
        subscriberNonMonetaryBalance.getPackageBalance(packageId) >> null

        and:
        subscriberNonMonitoryBalance << basePackageAllService.serviceBalance
        qoSInformation.setUsageException(new OperationFailedException("test"))

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota]).withCRBN([chargingRuleBaseName]).pccRules([basePackageAllService.pccRule])
                .build()

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        result == SelectionResult.FULLY_APPLIED
        ReflectionAssert.assertLenientEquals(qoSInformation.getChargingRuleBaseNames(), [chargingRuleBaseName])
    }

    def "topup with applicable pcc profile is different then base package then topup won't be eligible"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now

                info.subscriptionId = subscriptionId;
            })
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota]).pccRules([basePackageAllService.pccRule])
                .build()


        and:
        MockQuotaTopUp quotaTopUp = createTopUpWithApplicablePCCProfiles(topUpAllService)
        setUpPreTopUpSubscription(quotaTopUp, subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), basePackageAllService.getTotalBalance(policyContext))
    }

    def "Selection Result fully applied when qos profile has action as reject"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now

                info.subscriptionId = subscriptionId;
            })
            pcc()
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        def policyContext = Mock(PolicyContext)
        def subscriberNonMonetaryBalance = Mock(SubscriberNonMonitoryBalance)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        policyContext.getSPInfo() >> sprInfo
        policyContext.getPCRFResponse() >> pcrfResponse;
        policyContext.getCurrentTime() >> calendar;
        policyContext.getPreTopUpSubscriptions() >> preTopUpsubscriptions;
        policyContext.getPolicyRepository() >> dummyPolicyRepository
        policyContext.getCurrentBalance() >> subscriberNonMonetaryBalance
        subscriberNonMonetaryBalance.getPackageBalance(packageId) >> null

        and:
        RnCBaseQoSProfileDetail detail = builderWithRejectAction
                .quotaProfileDetail([basePackageAllService.rncQuota]).pccRules([basePackageAllService.pccRule])
                .build()


        and:
        MockQuotaTopUp quotaTopUp = createTopUp(topUpAllService)
        setUpPreTopUpSubscription(quotaTopUp, subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance
        qoSInformation.setUsageException(new OperationFailedException("test"))

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), basePackageAllService.getTotalBalance(policyContext))
    }

    def "Topup not applied when topup balance is null"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now

                info.subscriptionId = subscriptionId;
            })
            pcc()
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        def subscriberNonMonetaryBalanceForTopUp = Mock(SubscriberNonMonitoryBalance)
        def policyContext = Mock(PolicyContext)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        policyContext.getSPInfo() >> sprInfo
        policyContext.getPCRFResponse() >> pcrfResponse;
        policyContext.getCurrentTime() >> calendar;
        policyContext.getPreTopUpSubscriptions() >> preTopUpsubscriptions;
        policyContext.getPolicyRepository() >> dummyPolicyRepository
        policyContext.getCurrentBalance() >>> subscriberNonMonitoryBalance >> subscriberNonMonetaryBalanceForTopUp
        subscriberNonMonetaryBalanceForTopUp.getPackageBalance(packageId) >> null
        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota]).pccRules([basePackageAllService.pccRule])
                .build()


        and:
        MockQuotaTopUp quotaTopUp = createTopUp(topUpAllService)
        setUpPreTopUpSubscription(quotaTopUp, subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        result == SelectionResult.FULLY_APPLIED
    }

    def "Not Applied when error fetching subscriber non monetary balance"() {
        given:

        ServiceInformation allService = service("test", CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs {
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }
        def policyContext = Mock(PolicyContext)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        policyContext.getSPInfo() >> sprInfo
        policyContext.getCurrentBalance() >> { throw new OperationFailedException(OPERATION__FAILED) }

        and:
        ServiceInformation http = service("test","http", packageId, 10) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:
        RnCBaseQoSProfileDetail detail = builderWithRejectAction
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        then:
        qoSInformation.getUsageException().getMessage() == OPERATION__FAILED;
        result == SelectionResult.NOT_APPLIED
    }

    def "Selection Result Not Applied when error fetching subscriber monetary balance"() {
        given:

        ServiceInformation allService = service("test", CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs {
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }
        def policyContext = Mock(PolicyContext)
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());
        policyContext.getSPInfo() >> sprInfo
        policyContext.getCurrentBalance() >> subscriberNonMonitoryBalance;
        policyContext.getCurrentMonetaryBalance() >> { throw new OperationFailedException(OPERATION__FAILED) }

        and:
        ServiceInformation http = service("test","http", packageId, 10) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:
        RnCBaseQoSProfileDetail detail = builderWithRejectAction
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        then:
        result == SelectionResult.NOT_APPLIED
    }

    def "Selection result should return NOT APLIED result when current non monetary balance is not found, rate defined and monetary balance exist and exception thrown for qos Information" () {
        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(0)
                .build();

        subscriberMonitoryBalance.addMonitoryBalances(monetaryBalance);


        and:
        ServiceInformation http = service("test","http", packageId, 10) { // THIS SERVICE RATE IS DEFINED

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        and:
        policyContext.getCurrentMonetaryBalance() >> subscriberMonitoryBalance
        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        result == SelectionResult.NOT_APPLIED
    }

    def "QoS Profile should return NOT APPLIED result when current non monetary balance is not found, rate defined and monetary balance not exist" () {
        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(0)
                .build();

        subscriberMonitoryBalance.addMonitoryBalances(monetaryBalance);


        and:
        ServiceInformation http = service("test","http", packageId, 10) { // THIS SERVICE RATE IS DEFINED

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        and:
        policyContext.getCurrentMonetaryBalance() >> subscriberMonitoryBalance
        qoSInformation.setUsageException(new OperationFailedException("test"))
        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        1 * subscriberMonitoryBalance.isDataBalanceExist() // ALL SERVICE isExist will not call, because rate is zero.
        result == SelectionResult.NOT_APPLIED
    }

    def "QoS Profile should not check for monetary balance when rate is not defined" () {
        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(0)
                .build();

        subscriberMonitoryBalance.addMonitoryBalances(monetaryBalance);


        and:
        ServiceInformation http = service("test","http", packageId, 0) { // THIS SERVICE RATE IS NOT DEFINED

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        and:
        subscriberNonMonitoryBalance << http.serviceBalance << allService.serviceBalance
        policyContext.getCurrentMonetaryBalance() >> subscriberMonitoryBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        0 * subscriberMonitoryBalance.isDataBalanceExist() // ALL SERVICE isExist will not call, because rate is zero.
        result == SelectionResult.FULLY_APPLIED
    }

    def "QoS Profile should not check for monetary balance when quota profile is not defined" () {
        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(10)
                .build();

        subscriberMonitoryBalance.addMonitoryBalances(monetaryBalance);


        and:
        ServiceInformation http = service("test","http", packageId, 10) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .pccRules([http.pccRule]).build();

        and:
        subscriberNonMonitoryBalance << http.serviceBalance << allService.serviceBalance
        policyContext.getCurrentMonetaryBalance() >> subscriberMonitoryBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        0 * subscriberMonitoryBalance.isDataBalanceExist() // ALL SERVICE isExist will not call, because rate is zero.
        result == SelectionResult.FULLY_APPLIED
    }

    /**
     * ALL Service rate kept zero, to check service specific rating scenario
     *
     * @return
     */
    def "Not Applied when all-service and pcc rule service quota is not exceeded, rate is defined and monetary balance is not exist"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(0)
                .build();

        subscriberMonitoryBalance.addMonitoryBalances(monetaryBalance);


        and:
        ServiceInformation http = service("test","http", packageId, 10) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        and:
        subscriberNonMonitoryBalance << http.serviceBalance << allService.serviceBalance
        policyContext.getCurrentMonetaryBalance() >> subscriberMonitoryBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        1 * subscriberMonitoryBalance.isDataBalanceExist() // ALL SERVICE isExist will not call, because rate is zero.
        result == SelectionResult.NOT_APPLIED
    }

    def "Partially-Selected when all-service not defined and at-least one pcc rule service quota is not exceeded"() {
        given:

        ServiceInformation http = service("test","http", packageId, 0d) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }



        and:
        ServiceInformation p2p = service("test","p2p", packageId, 0d) {

            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
            pcc()
        }


        and:

        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)

        and:

        RnCBaseQoSProfileDetail detail = builder.
                quotaProfileDetail([http.rncQuota, p2p.rncQuota]).
                withCRBN([chargingRuleBaseName]).
                pccRules([http.pccRule, p2p.pccRule]).build()

        and:

        subscriberNonMonitoryBalance << http.serviceBalance << p2p.serviceBalance


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        SelectionResult.PARTIALLY_APPLIED == result
        data.qosProfileDetail == detail
        data.pccRules != null
        data.pccRules.size() == 1
        data.pccRules.values()[0] == http.pccRule
    }

    def "Partially-Selected when all-service quota not exceeded and CRBN is configured and pcc rule not selected and "() {
        given:

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        and:
        ServiceInformation http = service("test","http", packageId, 0d) {

            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
            pcc()
        }

        and:

        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .withCRBN([chargingRuleBaseName])
                .pccRules([http.pccRule]).build();

        and:
        subscriberNonMonitoryBalance << http.serviceBalance << allService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        SelectionResult.PARTIALLY_APPLIED == result
        data.pccRules == null
        data.qosProfileDetail == detail
        data.chargingRuleBaseNames.size() == 1
        data.chargingRuleBaseNames[0] == chargingRuleBaseName
    }


    def "Skip all PCC Rules whose quota not configured"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })

            pcc()
        }

        and:


        ServiceInformation http = service("test","http", packageId, 0d) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota, http.rncQuota])
                .pccRules([allService.pccRule, http.pccRule]).build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance

        when:
        detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()
        def selectedPCCRules = data.pccRules;

        then:
        selectedPCCRules != null;
        selectedPCCRules.size() == 1
        selectedPCCRules.values().getAt(0) == allService.pccRule

    }

    def "Skip all ChargingRuleBaseName when all-service quota not configured"() {

        given:

        ServiceInformation http = service("test","http", packageId, 0d) {

            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }


        and:

        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([http.rncQuota])
                .withCRBN([chargingRuleBaseName])
                .pccRules([http.pccRule]).build();

        and:
        subscriberNonMonitoryBalance << http.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        SelectionResult.PARTIALLY_APPLIED == result
        data.chargingRuleBaseNames == null
    }

    def "Select all ChargingRuleBaseName when all-service quota not exceeded"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })

        }

        and:
        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .withCRBN([chargingRuleBaseName])
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:


        SelectionResult.FULLY_APPLIED == result
        data.chargingRuleBaseNames.size() == 1
        data.chargingRuleBaseNames[0] == chargingRuleBaseName
    }

    def "Not add ChargingRuleBaseName when any qos profile detail is selected from package" () {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })

        }
        and:
        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .withCRBN([chargingRuleBaseName])
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance
        qoSInformation.setQoSProfileDetail(detail);

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        SelectionResult.FULLY_APPLIED == result
        data.chargingRuleBaseNames == null
    }

    private MockQuotaTopUp createTopUpWithApplicablePCCProfiles(topUpAllService) {
        MockQuotaTopUp topUpPackage = MockQuotaTopUp.create(topUpAllService.pkgId, "name:" + UUID.randomUUID().toString(), Arrays.asList({"RANDOM"})).quotaProfileTypeIsRnC()

        Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail = [:]
        serviceToQuotaProfileDetail.put(topUpAllService.rncQuota.getServiceId(), topUpAllService.rncQuota)

        QuotaProfile quotaProfile =  new QuotaProfile(UUID.randomUUID().toString(),
                topUpAllService.pkgId,
                topUpAllService.quotaProfileId,
                BalanceLevel.HSQ,2, RenewalIntervalUnit.DAY, QuotaProfileType.RnC_BASED, Arrays.asList(serviceToQuotaProfileDetail), false, false);
        topUpPackage.setQuotaProfiles(Arrays.asList(quotaProfile))

        return topUpPackage
    }

    private MockQuotaTopUp createTopUp(topUpAllService){
        MockQuotaTopUp topUpPackage = MockQuotaTopUp.create(topUpAllService.pkgId, "name:" + UUID.randomUUID().toString()).quotaProfileTypeIsRnC()

        Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail = [:]
        serviceToQuotaProfileDetail.put(topUpAllService.rncQuota.getServiceId(), topUpAllService.rncQuota)

        QuotaProfile quotaProfile =  new QuotaProfile(UUID.randomUUID().toString(),
                topUpAllService.pkgId,
                topUpAllService.quotaProfileId,
                BalanceLevel.HSQ,2, RenewalIntervalUnit.DAY, QuotaProfileType.RnC_BASED, Arrays.asList(serviceToQuotaProfileDetail), false, false);
        topUpPackage.setQuotaProfiles(Arrays.asList(quotaProfile))

        return topUpPackage
    }


    private Subscription setUpSpareTopUpSubscription(MockQuotaTopUp topUpPackage, String subcriptionId){
        Subscription subscription = new Subscription(subcriptionId,
                UUID.randomUUID().toString(),
                topUpPackage.getId(),
                null,
                new Timestamp(policyContext.getCurrentTime().timeInMillis),
                new Timestamp(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)),
                SubscriptionState.fromValue(2),
                CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY,
                SubscriptionType.TOP_UP,
                null, null)
        spareTopUpsubscriptions.add(subscription);

        dummyPolicyRepository.getQuotaTopUpById(topUpPackage.getId()) >> topUpPackage

        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscription.getSubscriberIdentity())

        return subscription;
    }

    private Subscription setUpPreTopUpSubscriptionWithFutureDate(MockQuotaTopUp topUpPackage, String subcriptionId, Timestamp startTime) {
        Subscription subscription = new Subscription.SubscriptionBuilder().withStartTime(startTime).withId(subcriptionId)
                .withSubscriberIdentity(UUID.randomUUID().toString())
                .withPackageId(topUpPackage.getId())
                .withEndTime(new Timestamp(policyContext.getCurrentTime().timeInMillis + 24 * 2 * 60 * 60))
                .withStatus(SubscriptionState.fromValue(2))
                .withPriority(CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY)
                .withType(SubscriptionType.TOP_UP).build();
        return setPreTopUp(subscription, topUpPackage);
    }

    private Subscription setUpPreTopUpSubscription(MockQuotaTopUp topUpPackage, String subcriptionId){
        Subscription subscription = new Subscription(subcriptionId,
                UUID.randomUUID().toString(),
                topUpPackage.getId(),
                null,
                new Timestamp(policyContext.getCurrentTime().timeInMillis),
                new Timestamp(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)),
                SubscriptionState.fromValue(2),
                CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY,
                SubscriptionType.TOP_UP,
                null, null);
        return setPreTopUp(subscription, topUpPackage)
    }

    private Subscription setPreTopUp(Subscription subscription, MockQuotaTopUp topUpPackage) {
        preTopUpsubscriptions.add(subscription);

        dummyPolicyRepository.getQuotaTopUpById(topUpPackage.getId()) >> topUpPackage

        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscription.getSubscriberIdentity())

        return subscription;
    }

    def "Select pre topup quota when topup exists"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now

                info.subscriptionId = subscriptionId;
            })
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota])
                .build()


        and:
        MockQuotaTopUp quotaTopUp = createTopUp(topUpAllService)
        setUpSpareTopUpSubscription(quotaTopUp, subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        data.qosProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED
    }

    def "Select nearest expiry topup when multiple topup is defined"() {

        given:
        String subscriptionId = UUID.randomUUID().toString()
        String anotherSubscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId;
            })
        }

        ServiceInformation anotherTopUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = anotherSubscriptionId;
            })
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota])
                .build()

        and:
        setUpPreTopUpSubscription(createTopUp(topUpAllService), subscriptionId)

        setUpPreTopUpSubscription(createTopUp(anotherTopUpAllService), anotherSubscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << anotherTopUpAllService.serviceBalance << basePackageAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), topUpAllService.getTotalBalance(policyContext))
        result == SelectionResult.FULLY_APPLIED
    }

    def "Sets re-validation time of topup when subscription has future start date"() {

        given:
        String subscriptionId = UUID.randomUUID().toString()
        String anotherSubscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId;
            })
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota])
                .build()

        and:
        setUpPreTopUpSubscriptionWithFutureDate(createTopUp(topUpAllService), subscriptionId, new Timestamp(policyContext.getCurrentTime().timeInMillis + 24 * 60 * 60))

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        1 * policyContext.setRevalidationTime(new Timestamp(policyContext.getCurrentTime().timeInMillis + 24 * 60 * 60))
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), basePackageAllService.getTotalBalance(policyContext))
        result == SelectionResult.FULLY_APPLIED
    }


    def "Select spare topup when base quota exceeded"() {

        given:
        ServiceInformation baseAllService = service("test",CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([baseAllService.rncQuota])
                .build()


        and:
        setUpSpareTopUpSubscription(createTopUp(topUpAllService).topUpIsSpare(), subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << baseAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.FULLY_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), topUpAllService.getTotalBalance(policyContext))
        data.qosProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED

    }

    def "Select base package when topup quota exceeded"() {

        given:
        ServiceInformation baseAllService = service("test",CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
                info.subscriptionId = subscriptionId
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([baseAllService.rncQuota])
                .build()

        and:
        setUpSpareTopUpSubscription(createTopUp(topUpAllService), subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << baseAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.FULLY_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), baseAllService.getTotalBalance(policyContext))
        data.qosProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED

    }

    def "Select base package if future topup subscription is taken by the subscriber"() {

        given:
        ServiceInformation baseAllService = service("test",CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
                info.subscriptionId = subscriptionId
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([baseAllService.rncQuota])
                .build()

        and:
        Subscription subscription = setUpSpareTopUpSubscription(createTopUp(topUpAllService), subscriptionId)
        subscription.startTime = new Timestamp(System.currentTimeMillis())

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << baseAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.FULLY_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), baseAllService.getTotalBalance(policyContext))
        data.qosProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED

    }

    def "Select pre topup quota and base package qos"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId
            })
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        ServiceInformation http = service("test","http", packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
            pcc()
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota, http.rncQuota])
                .pccRules([http.pccRule]).build();

        and:
        setUpPreTopUpSubscription(createTopUp(topUpAllService), subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance << http.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), topUpAllService.getTotalBalance(policyContext))
        ReflectionAssert.assertLenientEquals(qoSInformation.getQoSProfileDetail().getSessionQoS(), detail.getSessionQoS())
        ReflectionAssert.assertLenientEquals(qoSInformation.getQoSProfileDetail().getPCCRules(), detail.getPCCRules())
        data.qosProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED
    }

    def "Select pre topup quota and base package session level qos"() {
        given:
        String subscriptionId = UUID.randomUUID().toString()
        ServiceInformation topUpAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, UUID.randomUUID().toString(), 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId
            })
        }

        ServiceInformation basePackageAllService = service(UUID.randomUUID().toString(),CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([basePackageAllService.rncQuota])
                .build();

        and:
        setUpPreTopUpSubscription(createTopUp(topUpAllService), subscriptionId)

        and:
        subscriberNonMonitoryBalance << topUpAllService.serviceBalance << basePackageAllService.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        ReflectionAssert.assertLenientEquals(qoSInformation.getQosBalance(), topUpAllService.getTotalBalance(policyContext))
        ReflectionAssert.assertLenientEquals(qoSInformation.getQoSProfileDetail().getSessionQoS(), detail.getSessionQoS())
        data.qosProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED
    }
}