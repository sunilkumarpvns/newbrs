package com.elitecore.netvertex.pm.qos.rncbaseqosprofile.SpareTopUp

import com.elitecore.commons.base.FixedTimeSource
import com.elitecore.commons.io.IndentingPrintWriter
import com.elitecore.corenetvertex.constants.CommonConstants
import com.elitecore.corenetvertex.constants.TimeUnit
import com.elitecore.corenetvertex.pd.topup.TopUpType
import com.elitecore.corenetvertex.pm.constants.SelectionResult
import com.elitecore.corenetvertex.pm.util.MockQuotaProfile
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl
import com.elitecore.corenetvertex.spr.data.Subscription
import com.elitecore.netvertex.pm.*
import com.elitecore.netvertex.pm.qos.rnc.RnCBaseQoSProfileDetail
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail
import com.elitecore.netvertex.pm.util.MockBasePackage
import com.elitecore.netvertex.service.pcrf.PCRFResponse
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl
import org.unitils.reflectionassert.ReflectionAssert
import spock.lang.Specification

import java.sql.Timestamp

import static com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilder.balanceIs
import static com.elitecore.netvertex.pm.ServiceInformation.service

class SpareTopupWithBasePackageQuotaAndPCCRule extends Specification {

    private static final String PCC_PROFILE_NAME = "test";
    private static final String UNKNOWN = "unknown";

    private QoSInformation qoSInformation;
    private PolicyContext policyContext;

    private RnCQoSProfileDetailFactory.RnCQoSProfileDetialBuilder builder;
    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private String packageId;
    private MockQuotaTopUp quotaTopUpWithNoApplicablePccProfiles, quotaTopUpWithTestAsApplicablePCCProfile, quotaTopUpWithUnknownApplicablePccProfile;
    private MockQuotaProfile quotaProfile,quotaProfile2,quotaProfile3;
    private Subscription subscription,subscription2,subscription3;

    private PCRFResponse pcrfResponse;
    private DummyPolicyRepository dummyPolicyRepository = new DummyPolicyRepository();



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

        BasePackage basePackage = MockBasePackage.create(packageId,packageId+"-name");
        qoSInformation.startPackageQoSSelection(basePackage)

        createPackageAndSubscription();

        policyContext.getPolicyRepository() >> dummyPolicyRepository;

        pcrfResponse = new PCRFResponseImpl()
        policyContext.getPCRFResponse() >> pcrfResponse;

        Calendar calendar = Calendar.getInstance();
        policyContext.getCurrentTime() >> calendar;

    }


    def "Not Applied when top-up balance exceeded"() {

        given:
        policyContext.getPreTopUpSubscriptions() >> [subscription]
        String subscriptionId = subscription.getId();
        ServiceInformation allService = service("test", CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        and:
        ServiceInformation topAllService = service(quotaProfile.getName(),CommonConstants.ALL_SERVICE_ID, quotaTopUpWithNoApplicablePccProfiles.getId(),0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
                info.subscriptionId = subscriptionId;
            })
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        List<RnCQuotaProfileDetail> details = [topAllService.rncQuota]

        quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, details[0]);

        and:

        subscriberNonMonitoryBalance << allService.serviceBalance << topAllService.serviceBalance;


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == null
        result == SelectionResult.NOT_APPLIED

    }

    def "Fully Applied and spare Top Up balance will be considered when base balance exceeded"() {

        given:
        policyContext.getPreTopUpSubscriptions() >> [subscription]
        String subscriptionId = subscription.getId();
        ServiceInformation allService = service("test", CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        ServiceInformation topAllService = service(quotaProfile.getName(),CommonConstants.ALL_SERVICE_ID, quotaTopUpWithNoApplicablePccProfiles.getId(),0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId;
            })
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        List<RnCQuotaProfileDetail> details = [topAllService.rncQuota]

        quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, details[0]);

        and:

        subscriberNonMonitoryBalance << allService.serviceBalance << topAllService.serviceBalance;


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED
        ReflectionAssert.assertReflectionEquals(qoSInformation.getQosBalance(), topAllService.getTotalBalance(policyContext))

    }


    def "Fully applied And base balance considered even TopUp & Base both balance available"() {

        given:
        policyContext.getPreTopUpSubscriptions() >> [subscription]
        String subscriptionId = subscription.getId();
        ServiceInformation allService = service("test", CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
            })
        }

        ServiceInformation topAllService = service(quotaProfile.getName(),CommonConstants.ALL_SERVICE_ID, quotaTopUpWithNoApplicablePccProfiles.getId(),0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
                info.subscriptionId = subscriptionId;
            })
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        List<RnCQuotaProfileDetail> details = [topAllService.rncQuota]

        quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, details[0]);

        and:

        subscriberNonMonitoryBalance << allService.serviceBalance << topAllService.serviceBalance;


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED
        ReflectionAssert.assertReflectionEquals(qoSInformation.getQosBalance(), allService.getTotalBalance(policyContext))

    }


    def "For multiple subscription balance of TopUp with valid Applicable PCC Profile will be considered "() {

        given:
        policyContext.getPreTopUpSubscriptions() >> [subscription2,subscription3]

        String subscriptionId2 = subscription2.getId();
        String subscriptionId3 = subscription3.getId();


        ServiceInformation allService = service("test", CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        and:

        ServiceInformation validApplicablePccProfileTopUpAllServiceBalance = service(quotaProfile2.getName(),CommonConstants.ALL_SERVICE_ID, quotaTopUpWithTestAsApplicablePCCProfile.getId(),0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId2;
            })
        }

        ServiceInformation inValidApplicablePccProfileTopUpAllServiceBalance = service(quotaProfile3.getName(),CommonConstants.ALL_SERVICE_ID, quotaTopUpWithUnknownApplicablePccProfile.getId(),0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId3;
            })
        }

        and:

        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();



        quotaProfile2.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, validApplicablePccProfileTopUpAllServiceBalance.rncQuota);
        quotaProfile3.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID,inValidApplicablePccProfileTopUpAllServiceBalance.rncQuota);

        and:

        subscriberNonMonitoryBalance << allService.serviceBalance << validApplicablePccProfileTopUpAllServiceBalance.serviceBalance << inValidApplicablePccProfileTopUpAllServiceBalance.serviceBalance;


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED
        ReflectionAssert.assertReflectionEquals(qoSInformation.getQosBalance(), validApplicablePccProfileTopUpAllServiceBalance.getTotalBalance(policyContext))


    }


    def "For multiple subscriptions balance of TopUp with balance will be considered"() {

        given:
        policyContext.getPreTopUpSubscriptions() >> [subscription,subscription2]

        String subscriptionId = subscription.getId();
        String subscriptionId2 = subscription2.getId();
        ServiceInformation allService = service("test", CommonConstants.ALL_SERVICE_ID, packageId,0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
            })
        }

        and:

        ServiceInformation zeroAllServiceBalanceTopUp = service(quotaProfile.getName(),CommonConstants.ALL_SERVICE_ID, quotaTopUpWithNoApplicablePccProfiles.getId(),0d) {
            randomQuota().outOfThat(balanceIs{
                billingCycleUsage << [total:0, resetOn:1.hour.from.now]
                info.subscriptionId = subscriptionId;
            })
        }

        ServiceInformation topUpwithAllServiceBalance = service(quotaProfile2.getName(),CommonConstants.ALL_SERVICE_ID, quotaTopUpWithTestAsApplicablePCCProfile.getId(),0d) {
            randomQuota().outOfThat(balanceIs{
                allBalancesAreAvailable() and() resetOn 1.hour.from.now
                info.subscriptionId = subscriptionId2;
            })
        }


        and:
        RnCBaseQoSProfileDetail detail = builder
                .quotaProfileDetail([allService.rncQuota])
                .build();

        quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID,zeroAllServiceBalanceTopUp.rncQuota);
        quotaProfile2.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, topUpwithAllServiceBalance.rncQuota);

        and:

        subscriberNonMonitoryBalance << allService.serviceBalance << zeroAllServiceBalanceTopUp.serviceBalance << topUpwithAllServiceBalance.serviceBalance;


        when:
        def result = detail.apply(policyContext, qoSInformation, SelectionResult.NOT_APPLIED)
        qoSInformation.stopSelectionProcess()

        then:
        qoSInformation.qoSProfileDetail == detail
        result == SelectionResult.FULLY_APPLIED
        ReflectionAssert.assertReflectionEquals(qoSInformation.getQosBalance(), topUpwithAllServiceBalance.getTotalBalance(policyContext))


    }



    private Subscription createSubscription(String packageId, String subscriptionId) {
        return new Subscription.SubscriptionBuilder().withId(subscriptionId)
                .withPackageId(packageId)
                .withEndTime(new Timestamp(new Date().getTime() + TimeUnit.DAY.toSeconds(1) * 1000))
                .withStartTime(new Timestamp(new Date().getTime()))
                .build();
    }

    def createPackageAndSubscription(){

        quotaTopUpWithNoApplicablePccProfiles = dummyPolicyRepository.mockQuotaTopUp().withPackageType(TopUpType.SPARE_TOP_UP);
        quotaTopUpWithTestAsApplicablePCCProfile = dummyPolicyRepository.mockQuotaTopUp(Arrays.asList(PCC_PROFILE_NAME)).withPackageType(TopUpType.SPARE_TOP_UP);
        quotaTopUpWithUnknownApplicablePccProfile = dummyPolicyRepository.mockQuotaTopUp(Arrays.asList(PCC_PROFILE_NAME+UNKNOWN)).withPackageType(TopUpType.SPARE_TOP_UP);

        quotaProfile = MockQuotaProfile.create(quotaTopUpWithNoApplicablePccProfiles.getName() +"Id", quotaTopUpWithNoApplicablePccProfiles.getName(), Collections.emptyMap());
        quotaTopUpWithNoApplicablePccProfiles.setQuotaProfiles(quotaProfile);

        quotaProfile2 = MockQuotaProfile.create(quotaTopUpWithTestAsApplicablePCCProfile.getName() +"Id", quotaTopUpWithTestAsApplicablePCCProfile.getName(), Collections.emptyMap());
        quotaTopUpWithTestAsApplicablePCCProfile.setQuotaProfiles(quotaProfile2);

        quotaProfile3 = MockQuotaProfile.create(quotaTopUpWithUnknownApplicablePccProfile.getName() +"Id", quotaTopUpWithUnknownApplicablePccProfile.getName(), Collections.emptyMap());
        quotaTopUpWithUnknownApplicablePccProfile.setQuotaProfiles(quotaProfile3);


        subscription = createSubscription(quotaTopUpWithNoApplicablePccProfiles.getId(), quotaTopUpWithNoApplicablePccProfiles.getName() + "-subscription");
        subscription2 = createSubscription(quotaTopUpWithTestAsApplicablePCCProfile.getId(), quotaTopUpWithTestAsApplicablePCCProfile.getName() + "-subscription");
        subscription3 = createSubscription(quotaTopUpWithUnknownApplicablePccProfile.getId(), quotaTopUpWithUnknownApplicablePccProfile.getName() + "-subscription");

    }
}