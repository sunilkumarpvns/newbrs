package com.elitecore.corenetvertex.pm.pkg.factory

import com.elitecore.corenetvertex.constants.AggregationKey
import com.elitecore.corenetvertex.constants.CommonConstants
import com.elitecore.corenetvertex.constants.DataUnit
import com.elitecore.corenetvertex.constants.TimeUnit
import com.elitecore.corenetvertex.pkg.PkgData
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData
import com.elitecore.corenetvertex.pm.PkgDataBuilder
import com.elitecore.corenetvertex.pm.pkg.PackageFactory
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile
import spock.lang.Specification

import static com.elitecore.corenetvertex.pm.PkgDataBuilder.createUMBaseQuotaDetailData

class UMBasedQuotaProfileFactoryTest extends Specification{
    private static final long DEFAULT_FOR_USAGE = 0l
    private UMBasedQuotaProfileFactory umBasedQuotaProfileFactory
    private PackageFactory packageFactory
    private QuotaProfileData quotaProfileData
    private PkgData pkgData

    def setup() {
        packageFactory = new PackageFactory()
        quotaProfileData = new QuotaProfileData()
        quotaProfileData.setRenewalIntervalUnit("DAY")
        umBasedQuotaProfileFactory = new UMBasedQuotaProfileFactory(packageFactory)
        pkgData = Mock(PkgData.class)
    }

    def "No quota profile when quota profile details are not configured"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        quotaProfileData.setQuotaProfileDetailDatas([])

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile == null
        quotaProfileFailReasons.size() == 1
        "No quota profile details configured for quota profile: null" == quotaProfileFailReasons[0]
    }

    def "skip to create quota profile detail when failReason is not empty"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setAggregationKey("CENTURY")

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData])


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfileFailReasons.isEmpty() == false
        quotaProfile == null
    }

    def "skip to create quota profile detail when invalid renewal interval is given"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setAggregationKey("WEEKLY")
        quotaProfileData.setName("Test")

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData])
        quotaProfileData.setRenewalIntervalUnit("hello")


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfileFailReasons.isEmpty() == false
        "Invalid Renewal Interval Value set for Quota profile: Test" == quotaProfileFailReasons[0]
        quotaProfile == null
    }

    def "Aggregation key not provided"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setAggregationKey(null as String)

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData])


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile == null
        quotaProfileFailReasons.size() == 1
        "Quota Profile Detail(1 parsing fail. Cause by:No aggregation key provided for quota profile detail: 1" == quotaProfileFailReasons[0]
    }

    def "Invalid aggregation key provided"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setAggregationKey("YEARLY")

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData])

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile == null
        quotaProfileFailReasons.size() == 1
        "Quota Profile Detail(1 parsing fail. Cause by:Invalid aggregation key(null) provided for quota profile detail: 1" == quotaProfileFailReasons[0]
    }

    def "service type not provided"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setDataServiceTypeData(null)

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData])


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile == null
        quotaProfileFailReasons.isEmpty() == false
        "Quota Profile Detail(1 parsing fail. Cause by:No data service type configured for quota profile detail: 1" == quotaProfileFailReasons[0]
    }

    def "UNLIMITED USAGE for when Total usage not configured"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setTotal(null)

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true
        quotaProfileDetailData.getTotal() == CommonConstants.QUOTA_UNLIMITED
    }

    def "UNLIMITED USAGE for when upload usage not configured"() {
        given:

        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setUpload(null)

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true
        quotaProfileDetailData.getUpload() == CommonConstants.QUOTA_UNLIMITED
    }

    def "UNLIMITED USAGE for when download usage not configured"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setDownload(null)

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true
        quotaProfileDetailData.getDownload() == CommonConstants.QUOTA_UNLIMITED
    }

    def "UNLIMITED USAGE for when time usage not configured"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setTime(null)

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true
        quotaProfileDetailData.getTime() == CommonConstants.QUOTA_UNLIMITED
    }

    def "Different Service with same aggregation key found on same fup level"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData11 = createUMBaseQuotaDetailData("1", "1")
        quotaProfileDetailData11.setFupLevel(0)
        QuotaProfileDetailData quotaProfileDetailData12 = createUMBaseQuotaDetailData("2", "2")
        quotaProfileDetailData12.setFupLevel(0)

        and:
        QuotaProfileDetailData quotaProfileDetailData21 = createUMBaseQuotaDetailData("3", "1")
        quotaProfileDetailData21.setFupLevel(1)
        QuotaProfileDetailData quotaProfileDetailData22 = createUMBaseQuotaDetailData("4", "2")
        quotaProfileDetailData22.setFupLevel(1)

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData11, quotaProfileDetailData12, quotaProfileDetailData21, quotaProfileDetailData22])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty()
    }

    def "Zero renewal interval when Null set in database"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []
        quotaProfileData.setRenewalInterval(null)

        and:
        QuotaProfileDetailData quotaProfileDetailData11 = createUMBaseQuotaDetailData("1", "1")
        quotaProfileDetailData11.setFupLevel(0)
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData11])
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty()
        quotaProfile.getRenewalInterval()==0
    }

    def "same service and aggregation key found on same fup level"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")


        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile == null
        quotaProfileFailReasons.isEmpty() == false
        quotaProfileFailReasons.size() == 2
        ["Same service(ServiceTypeTest) and aggregation-key(BILLING_CYCLE) found on level:hsq in 2","Same service(ServiceTypeTest) and aggregation-key(BILLING_CYCLE) found on level:hsq in 3"] == quotaProfileFailReasons
    }

    def "fail reasons are added when different Quota profile detail data fails"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setAggregationKey(null as String)

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)
        quotaProfileDetailData2.setDataServiceTypeData(null)

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)
        quotaProfileDetailData3.setAggregationKey("Monthly")

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile == null
        quotaProfileFailReasons.isEmpty() == false
        ["Quota Profile Detail(1 parsing fail. Cause by:No aggregation key provided for quota profile detail: 1", "Quota Profile Detail(2 parsing fail. Cause by:No data service type configured for quota profile detail: 2", "Quota Profile Detail(3 parsing fail. Cause by:Invalid aggregation key(null) provided for quota profile detail: 3"] == quotaProfileFailReasons
    }


    def "cumulative quota value configuration is removed"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setTotal(1l)
        quotaProfileDetailData.setUpload(1l)
        quotaProfileDetailData.setDownload(1l)
        quotaProfileDetailData.setTime(1l)
        quotaProfileDetailData.setTotalUnit(DataUnit.GB.name())
        quotaProfileDetailData.setUploadUnit(DataUnit.GB.name())
        quotaProfileDetailData.setDownloadUnit(DataUnit.GB.name())
        quotaProfileDetailData.setTimeUnit(TimeUnit.HOUR.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)
        quotaProfileDetailData2.setTotal(1l)
        quotaProfileDetailData2.setUpload(1l)
        quotaProfileDetailData2.setDownload(1l)
        quotaProfileDetailData2.setTime(1l)
        quotaProfileDetailData2.setTotalUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setUploadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setDownloadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setTimeUnit(TimeUnit.MINUTE.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)
        quotaProfileDetailData3.setTotal(1l)
        quotaProfileDetailData3.setUpload(1l)
        quotaProfileDetailData3.setDownload(1l)
        quotaProfileDetailData3.setTime(1l)
        quotaProfileDetailData3.setTotalUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setUploadUnit(DataUnit.KB.name())
        quotaProfileDetailData3.setDownloadUnit(DataUnit.KB.name())
        quotaProfileDetailData3.setTimeUnit(TimeUnit.SECOND.name())

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true
        quotaProfileDetailData3.getTotal() == 2049
        quotaProfileDetailData3.getDownload() == 1049601
        quotaProfileDetailData3.getUpload() == 1049601
        quotaProfileDetailData3.getTime() == 3661

    }

    def "cumulative calculation should not be done when one level has UNLIMITED QUOTA configured"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        /// null represents unlimited quota
        quotaProfileDetailData.setTotal(1)
        quotaProfileDetailData.setUpload(null)
        quotaProfileDetailData.setDownload(null)
        quotaProfileDetailData.setTime(null)
        quotaProfileDetailData.setTotalUnit(DataUnit.GB.name())
        quotaProfileDetailData.setUploadUnit(DataUnit.GB.name())
        quotaProfileDetailData.setDownloadUnit(DataUnit.GB.name())
        quotaProfileDetailData.setTimeUnit(TimeUnit.HOUR.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)
        quotaProfileDetailData2.setTotal(1l)
        quotaProfileDetailData2.setUpload(1l)
        quotaProfileDetailData2.setDownload(1l)
        quotaProfileDetailData2.setTime(1l)
        quotaProfileDetailData2.setTotalUnit(DataUnit.GB.name())
        quotaProfileDetailData2.setUploadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setDownloadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setTimeUnit(TimeUnit.SECOND.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)
        quotaProfileDetailData3.setTotal(null)
        quotaProfileDetailData3.setUpload(null)
        quotaProfileDetailData3.setDownload(1)
        quotaProfileDetailData3.setTime(null)
        quotaProfileDetailData3.setTotalUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setUploadUnit(DataUnit.KB.name())
        quotaProfileDetailData3.setDownloadUnit(DataUnit.KB.name())
        quotaProfileDetailData3.setTimeUnit(TimeUnit.SECOND.name())

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true
        quotaProfileDetailData.getTotal() == 1
        quotaProfileDetailData.getDownload() == CommonConstants.QUOTA_UNLIMITED
        quotaProfileDetailData.getUpload() == CommonConstants.QUOTA_UNLIMITED
        quotaProfileDetailData.getTime() == CommonConstants.QUOTA_UNLIMITED
        quotaProfileDetailData2.getTotal() == 2
        quotaProfileDetailData2.getDownload() == 1
        quotaProfileDetailData2.getUpload() == 1
        quotaProfileDetailData2.getTime() == 1
        quotaProfileDetailData3.getTotal() == CommonConstants.QUOTA_UNLIMITED
        quotaProfileDetailData3.getDownload() == 1025
        quotaProfileDetailData3.getUpload() == CommonConstants.QUOTA_UNLIMITED
        quotaProfileDetailData3.getTime() == CommonConstants.QUOTA_UNLIMITED

    }

    def "Partial fail reason is added when service is not found on upper fup level"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData allServiceQuotaDetail = createUMBaseQuotaDetailData("1")
        allServiceQuotaDetail.setFupLevel(0)
        allServiceQuotaDetail.setAggregationKey(AggregationKey.BILLING_CYCLE.name())
        DataServiceTypeData allServiceTypeData = PkgDataBuilder.createServiceTypeWithRatingGroup()
        allServiceTypeData.setId(CommonConstants.ALL_SERVICE_ID)
        allServiceQuotaDetail.setDataServiceTypeData(allServiceTypeData)
        allServiceQuotaDetail.setServiceId(allServiceTypeData.getId())
        and:
        QuotaProfileDetailData telnetServiceQuotaDetail = createUMBaseQuotaDetailData("2")
        telnetServiceQuotaDetail.setFupLevel(0)
        telnetServiceQuotaDetail.setAggregationKey(AggregationKey.BILLING_CYCLE.name())
        DataServiceTypeData telnetServiceTypeData = PkgDataBuilder.createServiceTypeWithRatingGroup()
        telnetServiceTypeData.setId("telnet")
        telnetServiceQuotaDetail.setDataServiceTypeData(telnetServiceTypeData)
        telnetServiceQuotaDetail.setServiceId(telnetServiceTypeData.getId())

        and:
        QuotaProfileDetailData httpServiceQuotaDetail = createUMBaseQuotaDetailData("3")
        httpServiceQuotaDetail.setFupLevel(1)
        httpServiceQuotaDetail.setAggregationKey(AggregationKey.BILLING_CYCLE.name())
        DataServiceTypeData httpServiceTypeData = PkgDataBuilder.createServiceTypeWithRatingGroup()
        httpServiceTypeData.setId("http")
        httpServiceQuotaDetail.setDataServiceTypeData(httpServiceTypeData)
        httpServiceQuotaDetail.setServiceId(httpServiceTypeData.getId())

        and:
        quotaProfileData.setQuotaProfileDetailDatas([allServiceQuotaDetail, telnetServiceQuotaDetail, httpServiceQuotaDetail])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)


        then:
        quotaProfilePartialFailReasons.isEmpty() == false
        quotaProfile != null
        "Service(ServiceTypeTest) was not found on upper level:hsq in Quota Profile Detail: 3" == quotaProfilePartialFailReasons[0]
    }


    def "Unlimited quota is skipped in calculation of cumulative quota"() {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setTotal(999999999999999999l)
        quotaProfileDetailData.setUpload(999999999999999999l)
        quotaProfileDetailData.setDownload(999999999999999999l)
        quotaProfileDetailData.setTime(1l)
        quotaProfileDetailData.setTotalUnit(DataUnit.KB.name())
        quotaProfileDetailData.setUploadUnit(DataUnit.KB.name())
        quotaProfileDetailData.setDownloadUnit(DataUnit.KB.name())
        quotaProfileDetailData.setTimeUnit(TimeUnit.HOUR.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)
        quotaProfileDetailData2.setTotal(1l)
        quotaProfileDetailData2.setUpload(1l)
        quotaProfileDetailData2.setDownload(1l)
        quotaProfileDetailData2.setTime(1l)
        quotaProfileDetailData2.setTotalUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setUploadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setDownloadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setTimeUnit(TimeUnit.MINUTE.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)
        quotaProfileDetailData3.setTotal(1l)
        quotaProfileDetailData3.setUpload(1l)
        quotaProfileDetailData3.setDownload(1l)
        quotaProfileDetailData3.setTime(1l)
        quotaProfileDetailData3.setTotalUnit(DataUnit.KB.name())
        quotaProfileDetailData3.setUploadUnit(DataUnit.KB.name())
        quotaProfileDetailData3.setDownloadUnit(DataUnit.KB.name())
        quotaProfileDetailData3.setTimeUnit(TimeUnit.SECOND.name())

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true


        quotaProfileDetailData.getTotal() == 999999999999999999
        quotaProfileDetailData.getUpload() == 999999999999999999
        quotaProfileDetailData.getDownload() == 999999999999999999

        quotaProfileDetailData2.getTotal() == 1
        quotaProfileDetailData2.getUpload() == 1
        quotaProfileDetailData2.getDownload() == 1

        quotaProfileDetailData3.getTotal() == 1025
        quotaProfileDetailData3.getDownload() == 1025
        quotaProfileDetailData3.getUpload() == 1025
        quotaProfileDetailData3.getTime() == 3661

        quotaProfileDetailData.getTotalUnit() == DataUnit.KB.name()
        quotaProfileDetailData.getUploadUnit() == DataUnit.KB.name()
        quotaProfileDetailData.getDownloadUnit() == DataUnit.KB.name()

        quotaProfileDetailData2.getTotalUnit() == DataUnit.MB.name()
        quotaProfileDetailData2.getUploadUnit() == DataUnit.MB.name()
        quotaProfileDetailData2.getDownloadUnit() == DataUnit.MB.name()

        quotaProfileDetailData3.getTotalUnit() == DataUnit.KB.name()
        quotaProfileDetailData3.getUploadUnit() == DataUnit.KB.name()
        quotaProfileDetailData3.getDownloadUnit() == DataUnit.KB.name()

    }


    def "Total balance, upload balance and download  balance is calculated and their data unit is converted to the smallest data unit from all the Quota Profile Detail" () {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setTotal(1l)
        quotaProfileDetailData.setUpload(1l)
        quotaProfileDetailData.setDownload(1l)
        quotaProfileDetailData.setTime(1l)
        quotaProfileDetailData.setTotalUnit(DataUnit.KB.name())
        quotaProfileDetailData.setUploadUnit(DataUnit.KB.name())
        quotaProfileDetailData.setDownloadUnit(DataUnit.KB.name())
        quotaProfileDetailData.setTimeUnit(TimeUnit.HOUR.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)
        quotaProfileDetailData2.setTotal(1l)
        quotaProfileDetailData2.setUpload(1l)
        quotaProfileDetailData2.setDownload(1l)
        quotaProfileDetailData2.setTime(1l)
        quotaProfileDetailData2.setTotalUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setUploadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setDownloadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setTimeUnit(TimeUnit.MINUTE.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)
        quotaProfileDetailData3.setTotal(1l)
        quotaProfileDetailData3.setUpload(1l)
        quotaProfileDetailData3.setDownload(1l)
        quotaProfileDetailData3.setTime(1l)
        quotaProfileDetailData3.setTotalUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setUploadUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setDownloadUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setTimeUnit(TimeUnit.SECOND.name())

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true
        quotaProfileDetailData3.getTotal() == 1049601
        quotaProfileDetailData3.getDownload() == 1049601
        quotaProfileDetailData3.getUpload() == 1049601

        quotaProfileDetailData.getTime() == 1
        quotaProfileDetailData2.getTime() == 61
        quotaProfileDetailData3.getTime() == 3661

        quotaProfileDetailData.getTotalUnit() == DataUnit.KB.name()
        quotaProfileDetailData2.getTotalUnit() == DataUnit.KB.name()
        quotaProfileDetailData3.getTotalUnit() == DataUnit.KB.name()

        quotaProfileDetailData.getUploadUnit() == DataUnit.KB.name()
        quotaProfileDetailData2.getUploadUnit() == DataUnit.KB.name()
        quotaProfileDetailData3.getUploadUnit() == DataUnit.KB.name()

        quotaProfileDetailData.getDownloadUnit() == DataUnit.KB.name()
        quotaProfileDetailData2.getDownloadUnit() == DataUnit.KB.name()
        quotaProfileDetailData3.getDownloadUnit() == DataUnit.KB.name()

        quotaProfileDetailData.getTimeUnit() == TimeUnit.HOUR.name()
        quotaProfileDetailData2.getTimeUnit() == TimeUnit.MINUTE.name()
        quotaProfileDetailData3.getTimeUnit() == TimeUnit.SECOND.name()

    }

    def "Total time balance is calculated and its time unit is converted to the smallest time unit from all the Quota Profiles" () {
        given:
        List<String> quotaProfileFailReasons = []
        List<String> quotaProfilePartialFailReasons = []

        and:
        QuotaProfileDetailData quotaProfileDetailData = createUMBaseQuotaDetailData("1")
        quotaProfileDetailData.setTotal(1l)
        quotaProfileDetailData.setUpload(1l)
        quotaProfileDetailData.setDownload(1l)
        quotaProfileDetailData.setTime(1l)
        quotaProfileDetailData.setTotalUnit(DataUnit.KB.name())
        quotaProfileDetailData.setUploadUnit(DataUnit.KB.name())
        quotaProfileDetailData.setDownloadUnit(DataUnit.KB.name())
        quotaProfileDetailData.setTimeUnit(TimeUnit.SECOND.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData2 = createUMBaseQuotaDetailData("2")
        quotaProfileDetailData2.setFupLevel(1)
        quotaProfileDetailData2.setTotal(1l)
        quotaProfileDetailData2.setUpload(1l)
        quotaProfileDetailData2.setDownload(1l)
        quotaProfileDetailData2.setTime(1l)
        quotaProfileDetailData2.setTotalUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setUploadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setDownloadUnit(DataUnit.MB.name())
        quotaProfileDetailData2.setTimeUnit(TimeUnit.MINUTE.name())

        and:
        QuotaProfileDetailData quotaProfileDetailData3 = createUMBaseQuotaDetailData("3")
        quotaProfileDetailData3.setFupLevel(2)
        quotaProfileDetailData3.setTotal(1l)
        quotaProfileDetailData3.setUpload(1l)
        quotaProfileDetailData3.setDownload(1l)
        quotaProfileDetailData3.setTime(1l)
        quotaProfileDetailData3.setTotalUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setUploadUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setDownloadUnit(DataUnit.GB.name())
        quotaProfileDetailData3.setTimeUnit(TimeUnit.HOUR.name())

        and:
        quotaProfileData.setQuotaProfileDetailDatas([quotaProfileDetailData, quotaProfileDetailData2, quotaProfileDetailData3])
        quotaProfileData.setId("firstQuota")
        quotaProfileData.setName("firstQuotaProfile")
        quotaProfileData.setPkgData(pkgData)
        quotaProfileData.getPkgData().setName("pkgName")

        when:
        QuotaProfile quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(quotaProfileData, quotaProfileFailReasons, quotaProfilePartialFailReasons)

        then:
        quotaProfile != null
        quotaProfileFailReasons.isEmpty() == true

        quotaProfileDetailData.getTotal() == 1
        quotaProfileDetailData.getUpload() == 1
        quotaProfileDetailData.getDownload() == 1

        quotaProfileDetailData2.getTotal() == 1025
        quotaProfileDetailData2.getUpload() == 1025
        quotaProfileDetailData2.getDownload() == 1025

        quotaProfileDetailData3.getTotal() == 1049601
        quotaProfileDetailData3.getDownload() == 1049601
        quotaProfileDetailData3.getUpload() == 1049601

        quotaProfileDetailData.getTime() == 1
        quotaProfileDetailData2.getTime() == 61
        quotaProfileDetailData3.getTime() == 3661

        quotaProfileDetailData.getTimeUnit() == TimeUnit.SECOND.name()
        quotaProfileDetailData2.getTimeUnit() == TimeUnit.SECOND.name()
        quotaProfileDetailData3.getTimeUnit() == TimeUnit.SECOND.name()



    }
}