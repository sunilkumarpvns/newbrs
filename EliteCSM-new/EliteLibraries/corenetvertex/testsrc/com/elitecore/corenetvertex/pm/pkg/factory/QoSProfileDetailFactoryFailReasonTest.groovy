package com.elitecore.corenetvertex.pm.pkg.factory

import com.elitecore.corenetvertex.constants.CounterPresence
import com.elitecore.corenetvertex.constants.QoSProfileAction
import com.elitecore.corenetvertex.constants.QoSUnit
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData
import com.elitecore.corenetvertex.pm.pkg.PackageFactory
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail
import spock.lang.Specification

import static com.elitecore.corenetvertex.pm.PkgDataBuilder.createPCCRule
import static com.elitecore.corenetvertex.pm.PkgDataBuilder.createQosProfileDetailData

public class QoSProfileDetailFactoryFailReasonTest extends Specification{

    private QoSProfileDetailFactory qoSProfileDetailFactory;
    private PCCRuleFactory pccRuleFactory;
    private ChargingRuleBaseNameFactory chargingRuleBaseNameFactory;


    def setup() {
        pccRuleFactory = Mock(PCCRuleFactory.class)
        chargingRuleBaseNameFactory = Mock(ChargingRuleBaseNameFactory.class)

        qoSProfileDetailFactory = new QoSProfileDetailFactory(pccRuleFactory, chargingRuleBaseNameFactory, Mock(PackageFactory.class));
    }

	def "Detail contains more than one PCCRule with same service"() {
        given:

        DataServiceTypeData ftpServiceTypeData = new DataServiceTypeData();
        ftpServiceTypeData.setId("1");

        PCCRuleData ftpPCCRuleData = createPCCRule("ftpPCC", "1");
        ftpPCCRuleData.setDataServiceTypeData(ftpServiceTypeData);

        PCCRuleData duplicatePCCRuleData = createPCCRule("duplicateFTP", "2");
        duplicatePCCRuleData.setDataServiceTypeData(ftpServiceTypeData);

        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");

        qosProfileDetailData.setPccRules([ftpPCCRuleData, duplicatePCCRuleData]);

        List<String> failReasons = [];

        when:
            def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", null, null, 1, failReasons, CounterPresence.MANDATORY, null);

		then:
            qoSProfileDetail == null
            failReasons.size() == 1
            "One level of qos profile cannot have more than one pcc rule with same service(1)" == failReasons[0]
    }

    def "Invalid priority level value:"() {
        given:

        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");

        qosProfileDetailData.setPriorityLevel((byte)0);

        List<String> failReasons = [];

        when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", null, null, 1, failReasons, CounterPresence.MANDATORY, null);

        then:
        qoSProfileDetail == null
        failReasons.size() == 1
        "Invalid priority level value:0" == failReasons[0]


    }
	
    def "Invalid QCI value:"() {
        given:
        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");

        qosProfileDetailData.setQci(10)


        List<String> failReasons = []

        when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", null, null, 1, failReasons, CounterPresence.MANDATORY, null);

        then:
        qoSProfileDetail == null
        failReasons.size() == 1
        "Invalid QCI value:10" == failReasons[0]

    }


    def "AAMBUL,AAMBRDL,MBRUL,MBRDL not configured"() {
        given:
        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");
        List<String> failReasons = []

        qosProfileDetailData.setAambrdl(Long.valueOf(0))
        qosProfileDetailData.setAambrul(Long.valueOf(0))
        qosProfileDetailData.setMbrdl(Long.valueOf(0))
        qosProfileDetailData.setMbrul(Long.valueOf(0))

        qosProfileDetailData.setAambrdlUnit(QoSUnit.Mbps.name())
        qosProfileDetailData.setAambrulUnit(QoSUnit.Mbps.name())
        qosProfileDetailData.setMbrdlUnit(QoSUnit.Mbps.name())
        qosProfileDetailData.setMbrulUnit(QoSUnit.Mbps.name())


        when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData,
                "test",
                null,
                null,
                1,
                failReasons,
                CounterPresence.MANDATORY,
                null);

        then:
        qoSProfileDetail == null
        failReasons.size() == 1
        "AAMBUL,AAMBRDL,MBRUL,MBRDL not configured" == failReasons[0]
    }


   def "Service Id not found quota profile detail"() {
        given:
        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");
        List<String> failReasons = []


        def allServiceQuotaProfileDetail = Mock(QuotaProfileDetail.class)
        allServiceQuotaProfileDetail.getServiceId() >> "DATA_SERVICE_TYPE_1"

        def quotaProfile = Mock(QuotaProfile.class)

        quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails() >> ["DATA_SERVICE_TYPE_1" : allServiceQuotaProfileDetail, "service1": Mock(QuotaProfileDetail.class)]

       when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", quotaProfile, null, 1, failReasons, CounterPresence.MANDATORY, null);

        then:
        qoSProfileDetail == null
        failReasons.size() == 1
        "Service Id not found quota profile detail" == failReasons[0]
    }

    def "ALL service quota profile detail not defined"() {
        given:
        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");
        List<String> failReasons = []

        def quotaProfileDetail = Mock(QuotaProfileDetail.class)
        quotaProfileDetail.getServiceId() >> "SERVICE_2"

        def quotaProfile = Mock(QuotaProfile.class)

        quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails() >> ["SERVICE_2" : quotaProfileDetail]

        when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", quotaProfile, null, 1, failReasons, CounterPresence.MANDATORY, null);

        then:
        qoSProfileDetail == null
        failReasons.size() == 1
        "ALL service quota profile detail not defined" == failReasons[0]

    }

    def "No reason defined for reject action"() {
        given:
        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");
        qosProfileDetailData.setAction(QoSProfileAction.REJECT.id)
        qosProfileDetailData.setRejectCause(null as String)
        List<String> failReasons = []

        when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", null, null, 1, failReasons, CounterPresence.MANDATORY, null);

        then:
        qoSProfileDetail == null
        failReasons.size() == 1
        "No reason defined for reject action" == failReasons[0]
    }

    def "PCCRule creation fail"() {
        given:
        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");
        List<String> failReasons = []

        1* pccRuleFactory.createPCCRule(*_) >> {arguments ->
            List<String> pccFailReason = arguments[2];
            pccFailReason.add("Manually fail");
            return null;
        }

        when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", null, null, 1, failReasons, CounterPresence.MANDATORY, null);

        then:

        qoSProfileDetail == null
        failReasons.size() == 1
        "PCC rule(PCCTest) parsing fail. Cause by:Manually fail" == failReasons[0]
    }

    def "ChargingRuleBaseName creation fail"() {
        given:
        QosProfileDetailData qosProfileDetailData = createQosProfileDetailData("1", "test");
        List<String> failReasons = []

        1 * chargingRuleBaseNameFactory.createChargingRuleBaseName(*_) >> {arguments ->
            List<String> chargingRuleBaseNameFailReasons = arguments[2];
            chargingRuleBaseNameFailReasons.add("Manually fail");
            return null;
        }

        when:
        def qoSProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, "test", null, null, 1, failReasons, CounterPresence.MANDATORY, null);

        then:

        qoSProfileDetail == null
        failReasons.size() == 1
        "Charging Rule Base Name (CRBNTest) parsing fail. Cause by:Manually fail" == failReasons[0]
    }
	
}
