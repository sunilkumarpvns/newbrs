package com.elitecore.corenetvertex.pm.pkg.factory

import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData
import com.elitecore.corenetvertex.pm.PkgDataBuilder
import com.elitecore.corenetvertex.pm.pkg.PackageFactory
import spock.lang.Specification
import spock.lang.Unroll

class PCCRuleFactoryFailReasonTest extends Specification {
    private PCCRuleFactory pccRuleFactory;
    private PCCRuleData pccRuleData;
    private List<RatingGroupData> ratingGroupDatas
    private RatingGroupFactory ratingGroupFactory;
    private DataServiceTypeFactory serviceTypeFactory;
    private PackageFactory packageFactory;
    def setup() {
        packageFactory = Mock(PackageFactory.class)
        serviceTypeFactory = Mock(DataServiceTypeFactory.class);
        pccRuleFactory = new PCCRuleFactory(serviceTypeFactory, packageFactory);
        ratingGroupFactory = Mock(RatingGroupFactory.class);
        pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        def ratingGroupData = PkgDataBuilder.createRatingGroupData("ratingGroupTest", "1")
        ratingGroupData.setId(pccRuleData.getChargingKey())
        ratingGroupDatas = [ratingGroupData];
    }

    @Unroll
    def 'Rating group not found raring groupd data is #ratingGroupDatas'() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setChargingKey("Key1")
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "Rating group not found for charging id:Key1" == failReasons[0]

        where:
        ratingGroupDatas | _
        null| _
        []  | _

    }

    def "Monitoring key not configured"() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test2");
        pccRuleData.setMonitoringKey("");
        List<String> failReasons = []

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.isEmpty() != true
        "No monitoring key configured for pcc rule: 1" == failReasons[0]

    }


    def "negative MBRDL value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setMbrdl(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "MBRDL value is negative(-1)" == failReasons[0]

    }

    def "negative MBRUL value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setMbrul(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "MBRUL value is negative(-1)" == failReasons[0]

    }

    def "negative GBRDL value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setGbrdl(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "GBRDL value is negative(-1)" == failReasons[0]
    }

    def "negative GBRUL value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setGbrul(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "GBRUL value is negative(-1)" == failReasons[0]
    }

    def "negative slice total value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setUsageMonitoring(true);
        pccRuleData.setSliceTotal(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "Slice total value is negative(-1)" == failReasons[0]
    }

    def "negative slice upload value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setUsageMonitoring(true);
        pccRuleData.setSliceUpload(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "Slice upload value is negative(-1)" == failReasons[0]
    }

    def "negative slice download value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setUsageMonitoring(true);
        pccRuleData.setSliceDownload(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "Slice download value is negative(-1)" == failReasons[0]
    }

    def "negative slice time value "() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setUsageMonitoring(true);
        pccRuleData.setSliceTime(-1l);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "Slice time value is negative(-1)" == failReasons[0]
    }

    def "Slice infomation not provide or configured as 0 or negative"() {
        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setUsageMonitoring(true);
        pccRuleData.setSliceTotal(0);
        pccRuleData.setSliceUpload(0);
        pccRuleData.setSliceDownload(0);
        pccRuleData.setSliceTime(0);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "Slice infomation not provide or configured as 0 or negative" == failReasons[0]
    }


    def "service type not configured"() {

        given:
        PCCRuleData pccRuleData = PkgDataBuilder.createPCCRule("1", "test");
        pccRuleData.setDataServiceTypeData(null);
        List<String> failReasons = [];

        when:
        def pccRule = pccRuleFactory.createPCCRule(pccRuleData, 0, failReasons, ratingGroupDatas);

        then:
        pccRule == null
        failReasons.size() == 1
        "No data service type configured for pcc rule: 1" == failReasons[0]


    }
}