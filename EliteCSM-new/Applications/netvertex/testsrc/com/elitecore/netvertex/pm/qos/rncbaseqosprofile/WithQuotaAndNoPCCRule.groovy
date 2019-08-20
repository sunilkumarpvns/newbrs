package com.elitecore.netvertex.pm.qos.rncbaseqosprofile

import com.elitecore.commons.base.FixedTimeSource
import com.elitecore.commons.base.TimeSource
import com.elitecore.commons.io.IndentingPrintWriter
import com.elitecore.corenetvertex.constants.CommonConstants
import com.elitecore.corenetvertex.pkg.PkgType
import com.elitecore.corenetvertex.pm.constants.SelectionResult
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName
import com.elitecore.corenetvertex.spr.MonetaryBalance
import com.elitecore.corenetvertex.spr.MonetaryBalanceBuilder
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl
import com.elitecore.netvertex.pm.*
import com.elitecore.netvertex.pm.qos.rnc.RnCBaseQoSProfileDetail
import com.elitecore.netvertex.service.pcrf.PCRFResponse
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl
import org.mockito.Mockito
import spock.lang.Specification
import spock.lang.Unroll

import static com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilder.balanceIs
import static com.elitecore.netvertex.pm.ServiceInformation.service

class WithQuotaAndNoPCCRule extends Specification {

    private QoSInformation qoSInformation;
    private PolicyContext policyContext;

    private RnCQoSProfileDetailFactory.RnCQoSProfileDetialBuilder builder;
    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private String packageId;
    private PCRFResponse pcrfResponse;


    def setup() {
        packageId = "test"
        qoSInformation = new QoSInformation();

        policyContext = Mock(PolicyContext)
        builder = RnCQoSProfileDetailFactory.createQoSProfile().hasRnCQuota()
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity("123")
        policyContext.getSPInfo() >> sprInfo
        policyContext.getTraceWriter() >> new IndentingPrintWriter(new StringWriter());

        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Collections.emptyList());

        subscriberMonetaryBalance = Spy(SubscriberMonetaryBalance.class, constructorArgs:[FixedTimeSource.systemTimeSource()]);
        policyContext.getCurrentBalance() >> subscriberNonMonitoryBalance;
        policyContext.getCurrentMonetaryBalance() >> subscriberMonetaryBalance;


        Calendar calendar = Calendar.getInstance();
        policyContext.getCurrentTime() >> calendar;

        BasePackage basePackage = Mock(BasePackage);
        basePackage.getPackageType() >> PkgType.BASE
        basePackage.getId() >> packageId
        qoSInformation.startPackageQoSSelection(basePackage)

        pcrfResponse = new PCRFResponseImpl()
        policyContext.getPCRFResponse() >> pcrfResponse
    }


    def "Not Applied when all-service balance exceeded"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        and:

        subscriberNonMonitoryBalance << allService.serviceBalance



        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == null
        result == SelectionResult.NOT_APPLIED

    }

    def "Not Applied when all-service balance is not configured"() {

        given:


        ServiceInformation http = service("test","http", packageId, 0d) {packageId ->
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        and:
        RnCBaseQoSProfileDetail detail = builder.quotaProfileDetail(http.rncQuota).build();


        and:
        subscriberNonMonitoryBalance << http.serviceBalance

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == null
        result == SelectionResult.NOT_APPLIED

    }


    def "QoS profile will be fully selected when subscriber has all-service balance"() {

        given:

        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED



    }

    def "QoS Profile will be return not applied result when rate defined and monetary balance not exist" () {

        given:
        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 1d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }


        and:

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(0)
                .build();

        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        1 * subscriberMonetaryBalance.isDataBalanceExist()
        result == SelectionResult.NOT_APPLIED

    }

    def "QoS Profile will be return fully applied result when rate defined and monetary balance exist" () {

        given:
        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 1d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }


        and:

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(100)
                .build();

        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == detail
        1 * subscriberMonetaryBalance.isDataBalanceExist();
        result == SelectionResult.FULLY_APPLIED
    }

    def "QoS Profile should not check for monetary balance when rate is not defined" () {

        given:
        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }


        and:

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(100)
                .build();

        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        0 * subscriberMonetaryBalance.isDataBalanceExist();
        result == SelectionResult.FULLY_APPLIED
    }

    def "QoS Profile should not check for monetary balance when quota profile is not defined" () {

        given:
        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 1d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }


        and:

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(100)
                .build();

        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);

        and:

        RnCBaseQoSProfileDetail detail = builder
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        0 * subscriberMonetaryBalance.isDataBalanceExist()
        result == SelectionResult.FULLY_APPLIED
    }

    def "QoS Profile should return NOT APPLIED result when current non monetary balance is not found, rate defined and monetary balance not exist" () {

        given:
        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 1d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }


        and:

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(0)
                .build();

        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        1 * subscriberMonetaryBalance.isDataBalanceExist();
        result == SelectionResult.NOT_APPLIED
    }

    def "Select all ChargingRuleBaseName when all-service balance exist"() {

        given:
        ServiceInformation allService = service("test",CommonConstants.ALL_SERVICE_ID, packageId, 1d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }


        and:

        MonetaryBalance monetaryBalance = new MonetaryBalanceBuilder("id", "subscriberId")
                .withServiceId(CommonConstants.MONEY_DATA_SERVICE)
                .withAvailableBalance(10)
                .build();

        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);

        and:

        ChargingRuleBaseName chargingRuleBaseName = new ChargingRuleBaseName("test", "test", [:], 0, null)

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .withCRBN(Arrays.asList(chargingRuleBaseName))
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance;

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:

        SelectionResult.FULLY_APPLIED == result
        data.chargingRuleBaseNames.size() == 1
        data.chargingRuleBaseNames[0] == chargingRuleBaseName
    }

    @Unroll
    def "Not select ChargingRuleBaseName when any previous qos profile detail has selected from package" () {

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
                .withCRBN(Arrays.asList(chargingRuleBaseName))
                .build();

        and:
        subscriberNonMonitoryBalance << allService.serviceBalance;
        qoSInformation.setQoSProfileDetail(detail);

        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)

        FinalQoSSelectionData data = qoSInformation.stopSelectionProcess()

        then:
        SelectionResult.FULLY_APPLIED == result
        data.chargingRuleBaseNames == null
    }
}