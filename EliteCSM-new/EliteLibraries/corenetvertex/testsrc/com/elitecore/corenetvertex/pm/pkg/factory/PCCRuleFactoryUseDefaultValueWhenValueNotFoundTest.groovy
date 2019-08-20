package com.elitecore.corenetvertex.pm.pkg.factory

import com.elitecore.corenetvertex.constants.PriorityLevel
import com.elitecore.corenetvertex.constants.QCI
import com.elitecore.corenetvertex.core.constant.ChargingModes
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData
import com.elitecore.corenetvertex.pm.PkgDataBuilder
import com.elitecore.corenetvertex.pm.constants.FlowStatus
import com.elitecore.corenetvertex.pm.pkg.PackageFactory
import spock.lang.Specification
import spock.lang.Unroll

class PCCRuleFactoryUseDefaultValueWhenValueNotFoundTest extends Specification {
    private PCCRuleFactory pccRuleFactory;
    private PCCRuleData pccRuleData;
    private List<RatingGroupData> ratingGroupDatas

    def setup() {
        PackageFactory packageFactory = new PackageFactory()
        RatingGroupFactory ratingGroupFactory = new RatingGroupFactory(packageFactory);
        DataServiceTypeFactory serviceTypeFactory = new DataServiceTypeFactory(ratingGroupFactory, packageFactory);
        pccRuleFactory = new PCCRuleFactory(serviceTypeFactory, packageFactory);

        pccRuleData = PkgDataBuilder.createPCCRule("1", "test");

        def ratingGroupData = PkgDataBuilder.createRatingGroupData("ratingGroupTest", "1")
        ratingGroupData.setId(pccRuleData.getChargingKey())
        ratingGroupDatas = [ratingGroupData];
    }

    def "QCI_9 for QCI"() {
        given:

        pccRuleData.setQci(null);
        List<String> failReasons = []


        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getQCI() == QCI.QCI_NON_GBR_9
    }

    @Unroll
    def 'STATIC type for PCC rule when type is #pccType'() {
        given:
        pccRuleData.setType(pccType)
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRuleData.getType() == pccRuleFactory.STATIC_PCC

        where:
        pccType      | _
        null         | _
        "otherType" || _

    }

    def "null for charging mode type"() {
        given:
        pccRuleData.setChargingMode(null)
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getChargingMode() == ChargingModes.NONE
    }

    def "OFFLINE for charging mode"() {
        given:
        pccRuleData.setChargingMode((byte) 100);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getChargingMode() == ChargingModes.NONE
    }

    def "ENABLED for flow status"() {
        given:
        pccRuleData.setFlowStatus((byte) 5);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getFlowStatus() == FlowStatus.ENABLED
    }

    def "ENABLED for no flow status"() {
        given:
        pccRuleData.setFlowStatus(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getFlowStatus() == FlowStatus.ENABLED;
    }

    def "null MBRDL"() {
        given:
        pccRuleData.setMbrdl(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getMBRDL() == 0l
    }

    def "null MBRUL"() {
        given:
        pccRuleData.setMbrul(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getMBRUL() == 0l
    }

    def "null GBRDL"() {
        given:
        pccRuleData.setGbrdl(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getGBRDL() == 0l
    }

    def "null GBRUL"() {
        given:
        pccRuleData.setGbrul(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getGBRUL() == 0l
    }

    def "null SliceTotal"() {
        given:
        pccRuleData.setSliceTotal(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRuleData.getSliceTotal() == 0l
    }

    def "null SliceUpload"() {
        given:
        pccRuleData.setSliceUpload(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRuleData.getSliceUpload() == 0l
    }

    def "null SliceDownload"() {
        given:
        pccRuleData.setSliceDownload(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRuleData.getSliceDownload() == 0l
    }

    def "null SliceTime"() {
        given:
        pccRuleData.setSliceTime(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRuleData.getSliceTime() == 0l
    }

    def "null ARP"() {
        given:
        pccRuleData.setArp(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getPriorityLevel() == PriorityLevel.PRIORITY_LEVEL_10
    }

    def "null PreCapability"() {
        given:
        pccRuleData.setPreCapability(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRule.getPeCapability() == false
    }

    def "null PreVulnerability"() {
        given:
        pccRuleData.setPreVulnerability(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRuleData.getPreVulnerability() == false
    }

    def "null usage monitoring"() {
        given:
        pccRuleData.setUsageMonitoring(null);
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        failReasons.isEmpty() == true
        pccRule != null
        pccRuleData.getUsageMonitoring() == false
    }
}