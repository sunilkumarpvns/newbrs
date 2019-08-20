package com.elitecore.netvertex.core.driver.cdr

import com.elitecore.commons.base.FixedTimeSource
import com.elitecore.commons.base.TimeSource
import com.elitecore.core.driverx.cdr.data.CSVStripMapping
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport
import com.elitecore.corenetvertex.constants.CommonConstants
import com.elitecore.corenetvertex.constants.PCRFKeyConstants
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants
import com.elitecore.corenetvertex.spr.MonetaryBalance
import com.elitecore.corenetvertex.spr.NonMonetoryBalance
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance
import com.elitecore.netvertex.core.driver.cdr.conf.impl.CSVDriverConfigurationImpl
import com.elitecore.netvertex.gateway.diameter.gy.MSCC
import com.elitecore.netvertex.gateway.diameter.gy.RequestedAction
import com.elitecore.netvertex.rnc.ReportOperation
import com.elitecore.netvertex.rnc.ReportSummary
import com.elitecore.netvertex.rnc.ReportedUsageSummary
import com.elitecore.netvertex.service.pcrf.PCRFRequest
import com.elitecore.netvertex.service.pcrf.PCRFResponse
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl
import spock.lang.Specification

import java.sql.Timestamp
import java.text.SimpleDateFormat

import static org.apache.commons.lang3.RandomUtils.*

class MsccCsvLineBuilderTest extends Specification {

    private MsccCsvLineBuilder mscccsvLineBuilder
    private CSVDriverConfigurationImpl csvDriverConfiguration
    private TimeSource timeSource;
    private PCRFRequest pcrfRequest;
    private PCRFResponse pcrfResponse;
    private MSCC reportedMSCC

    private SubscriberNonMonitoryBalance subscriberNonMonetaryBalance
    private SubscriberMonetaryBalance subscriberMonetaryBalance

    def setup() {

        reportedMSCC = new MSCC()
        csvDriverConfiguration = new CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .withDelimiter(",")
                .withCDRTimeStampFormat(new SimpleDateFormat("EEE dd MMM,yyyy,hh:mm:ss aaa"))
                .withFieldMappings(new ArrayList<CSVFieldMapping>())
                .withStripMappings(new HashMap<String, CSVStripMapping>())
                .build()

        timeSource = new FixedTimeSource(1000)
        pcrfRequest = new PCRFRequestImpl(timeSource)
        mscccsvLineBuilder = new MsccCsvLineBuilder(csvDriverConfiguration, timeSource, new ChargingHeaderBuilder(csvDriverConfiguration).getColoumValueProviders())

        subscriberNonMonetaryBalance = new SubscriberNonMonitoryBalance([])
        subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()))

        pcrfResponse = new PCRFResponseImpl()
        ReportSummary reportSummary = new ReportSummary();
        ReportedUsageSummary summary = createReportedUsageSummary()
        reportSummary.add(summary);
        pcrfResponse.setReportSummary(reportSummary)
        pcrfResponse.setSessionStartTime(new Date(System.currentTimeMillis()));
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val,uuid());
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.val,uuid());
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SERVICE.val,PCRFKeyValueConstants.DATA_SERVICE_ID.val);
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CALLTYPE.val,PCRFKeyValueConstants.CALLTYPE_ONNET.val);
        pcrfResponse.setChargingCDRDateFormat(csvDriverConfiguration.getCDRTimeStampFormat())
    }

    def "Empty list when reporting summary not found"() {
        given:
        PCRFRequest pcrfRequest = new PCRFRequestImpl(timeSource)
        PCRFResponse pcrfResponse = new PCRFResponseImpl()

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        records == Collections.emptyList()
    }

    def "For each reported MSCC line builder creates a new line"() {
        given:

        ReportSummary summary = pcrfResponse.getReportSummary()
        summary.add(createReportedUsageSummary());
        summary.add(createReportedUsageSummary());
        summary.add(createReportedUsageSummary());


        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        println records
        then:
        List<String> expected = [];
        summary.reportedUsageSummaries.each { expected.add(buildHeader(it, csvDriverConfiguration.getDelimiter(), pcrfResponse)) }
        expected == records

    }

    def "skip to create line when reported operation is Close Reservation"() {
        given:

        ReportSummary summary = pcrfResponse.getReportSummary()
        List<String> expected = [];
        summary.getReportedUsageSummaries().each {
            expected.add(buildHeader(it, csvDriverConfiguration.getDelimiter(), pcrfResponse))
        }

        ReportedUsageSummary summaryWithCloseReservation = createReportedUsageSummary()
        summaryWithCloseReservation.setReportOperation(ReportOperation.CLOSE_RESERVATION);
        summary.add(summaryWithCloseReservation);



        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        println records
        then:
        expected == records

    }

    def 'Comma separated string will be generated when multiple service identifier found in MSCC'() {
        given:
        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0);

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records

    }

    def 'Append additionalMapping values in records'() {
        given:
        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0);

        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "987654321")
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "123456789")


        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "987654321")
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "123456789")


        List<CSVFieldMapping> fieldMappings = [ new CSVFieldMapping("subscriberIdentity", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val)
                                                ,new CSVFieldMapping("subscriberIdentity", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val)
        ]


        this.csvDriverConfiguration.getCDRFieldMappings().addAll(fieldMappings)

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;
    }

    def 'Append escape in value if value contains delimiters'() {
        given:
        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0);


        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "98765432,1")
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "12.345,6789")

        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "98765432,1")
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "12.345,6789")
        summary.setCoreSessionId("geteway,session")

        List<CSVFieldMapping> fieldMappings = [ new CSVFieldMapping("subscriberIdentity", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val)
                                                ,new CSVFieldMapping("subscriberIdentity", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val)
        ]
        this.csvDriverConfiguration.getCDRFieldMappings().addAll(fieldMappings)

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        summary.setCoreSessionId("geteway\\,session")
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;


    }

    def 'Apply stripping for value if value found from PCRFRequest'() {
        given:


        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "98765,4321@gmail.com@yahoo.com")
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "123456789@sterlite.com")

        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "98765,4321@gmail.com@yahoo.com")
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "123456789@sterlite.com")
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val,"Session123")

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0);


        List<CSVFieldMapping> fieldMappings = [ new CSVFieldMapping("subscriberIdentity", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val)
                                                ,new CSVFieldMapping("subscriberIdentity", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val)
        ]
        this.csvDriverConfiguration.getCDRFieldMappings().addAll(fieldMappings)
        this.csvDriverConfiguration.getStripMappings().put(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val,
                new CSVStripMapping(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val,
                        CSVLineBuilderSupport.PREFIX, "@"))
        this.csvDriverConfiguration.getStripMappings().put(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val,
                new CSVStripMapping(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val,
                        CSVLineBuilderSupport.SUFFIX, "@"))
        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;
    }

    def "Append blank field when granted service units is null"() {
        given:

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0);
        summary.serviceIds = null

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;


    }

    def "Append blank fields for session id when core session id in report summary is not present/null"() {
        given:

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0)
        summary.setCoreSessionId(null);

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        summary.setCoreSessionId("");
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;


    }

    def "Append blank fields for currency when currency in report summary is not present/null"() {
        given:

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0)
        summary.setCurrency(null);

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        summary.setCurrency("");
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;
    }

    def "Append blank fields for package name when package name in report summary is not present/null"() {
        given:

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0)
        summary.setPackageName(null);

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        summary.setPackageName("");
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;
    }

    def "Append blank fields for quota profile name when quota profile in report summary is not present/null"() {
        given:

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0)
        summary.setQuotaProfile(summary.getQuotaProfileId(),  null);

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        summary.setQuotaProfile(summary.getQuotaProfileId(), "");
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;
    }

    def "Append blank fields for rate card name when rate card in report summary is not present/null"() {
        given:

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0)
        summary.setRateCard(summary.getRateCardId(),  null);

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        summary.setRateCard(summary.getRateCardId(), "");
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;
    }

    def "Append blank fields for call type when call type is not present/null"() {
        given:

        ReportedUsageSummary summary = pcrfResponse.getReportSummary().getReportedUsageSummaries().get(0)
        PCRFResponse pcrfResponse = new PCRFResponseImpl()
        ReportSummary reportSummary = new ReportSummary();
        reportSummary.add(summary);
        pcrfResponse.setReportSummary(reportSummary)
        pcrfResponse.setSessionStartTime(new Date(System.currentTimeMillis()));
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val,uuid());
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.val,uuid());
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SERVICE.val,PCRFKeyValueConstants.DATA_SERVICE_ID.val);
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CALLTYPE.val,null);
        pcrfResponse.setChargingCDRDateFormat(csvDriverConfiguration.getCDRTimeStampFormat())

        when:
        List<String> records = mscccsvLineBuilder.getCSVRecords(new ValueProviderExtImpl(pcrfRequest,pcrfResponse))

        then:
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_CALLTYPE.val,"");
        List<String> expectedRecords = [buildHeader(summary, csvDriverConfiguration.getDelimiter(), pcrfResponse)]
        expectedRecords == records;
    }

    public String buildHeader(ReportedUsageSummary reportedUsageSummary, String delimiter, PCRFResponse pcrfResponse) {
        StringBuilder builder = new StringBuilder()
        builder << reportedUsageSummary.getCoreSessionId() << delimiter;
        builder << reportedUsageSummary.getRatingGroup() << delimiter;
        builder << getServiceIdString(reportedUsageSummary) << delimiter;
        builder << reportedUsageSummary.getReportedVolume() << delimiter;
        builder << reportedUsageSummary.getReportedTime() << delimiter;
        builder << reportedUsageSummary.getReportedEvent() << delimiter;
        builder << reportedUsageSummary.getVolumePulse() << delimiter;
        builder << reportedUsageSummary.getCalculatedVolumePulse() << delimiter;
        builder << reportedUsageSummary.getTimePulse() << delimiter;
        builder << reportedUsageSummary.getCalculatedTimePulse() << delimiter;
        builder << reportedUsageSummary.getCurrentUnAccountedVolume() << delimiter;
        builder << reportedUsageSummary.getCurrentUnAccountedTime() << delimiter;
        builder << reportedUsageSummary.getPreviousUnAccountedVolume() << delimiter;
        builder << reportedUsageSummary.getPreviousUnAccountedTime() << delimiter;
        builder << reportedUsageSummary.getDeductedVolumeBalance() << delimiter;
        builder << reportedUsageSummary.getDeductedTimeBalance() << delimiter;
        builder << reportedUsageSummary.getDeductedMonetaryBalance() << delimiter;
        builder << reportedUsageSummary.getRate() << delimiter;
        builder << reportedUsageSummary.getDiscount() << delimiter;
        builder << reportedUsageSummary.getDiscountAmount() << delimiter;
        builder << reportedUsageSummary.getRateMinorUnit() << delimiter;
        builder << reportedUsageSummary.getCurrency() << delimiter
        builder << reportedUsageSummary.getExponent() << delimiter
        builder << reportedUsageSummary.getPackageName() << delimiter
        builder << reportedUsageSummary.getProductOfferName() << delimiter
        builder << reportedUsageSummary.getRequestedAction() << delimiter
        builder << reportedUsageSummary.getQuotaProfileName() << delimiter;
        builder << reportedUsageSummary.getLevel() << delimiter;
        builder << pcrfResponse.getAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val) << delimiter;
        builder << pcrfResponse.getAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.val) << delimiter;
        builder << reportedUsageSummary.getRateCardName() << delimiter;
        builder << reportedUsageSummary.getRateCardGroupName() << delimiter;
        builder << reportedUsageSummary.getReportOperation().name() << delimiter;
        builder << pcrfResponse.getAttribute(PCRFKeyConstants.CS_SERVICE.val) << delimiter;
        builder << pcrfResponse.getAttribute(PCRFKeyConstants.CS_CALLTYPE.val) << delimiter;
        builder << reportedUsageSummary.getTariffType() << delimiter;
        builder << reportedUsageSummary.getRevenueCode() << delimiter;
        builder << getFormattedDate(pcrfResponse.getSessionStartTime()) << delimiter;
        builder << getFormattedDate(reportedUsageSummary.getSessionStopTime()) << delimiter;
        builder << getFormattedDate(new Timestamp(timeSource.currentTimeInMillis()))

        return builder;
    }

    private String getServiceIdString(ReportedUsageSummary reportedUsageSummary) {
        if(Objects.isNull(reportedUsageSummary.getServiceIds())) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        reportedUsageSummary.getServiceIds().each { builder.append(it) }

        return builder.toString()
    }

    public ReportedUsageSummary createReportedUsageSummary() {
        ReportedUsageSummary summary = new ReportedUsageSummary(nextLong(1, 10), Arrays.asList(nextInt(1, 10)))
        summary.setCoreSessionId(uuid());
        summary.setQuotaProfile(uuid(), uuid());
        summary.setRateCard(uuid(), uuid());
        summary.setLevel(nextInt(0,2));
        summary.setPackageId(uuid(), uuid());
        summary.setProductOfferName(uuid());
        summary.setRequestedAction(RequestedAction.DIRECT_DEBITING.toString())
        summary.setSubscriptionId(uuid());
        summary.setCurrency(uuid());
        summary.setExponent(nextInt(0, 10));
        summary.setReportedTime(nextLong(0, 10));
        summary.setReportedVolume(nextLong(0, 10));
        summary.setReportedEvent(nextLong(0, 10));
        summary.setCurrentUnAccountedVolume(nextLong(0, 10));
        summary.setCurrentUnAccountedTime(nextLong(0, 10));
        summary.setPreviousUnAccountedVolume(nextLong(0,10));
        summary.setPreviousUnAccountedTime(nextLong(0,10));
        summary.setDeductedMonetaryBalance(nextDouble(0,10));
        summary.setDeductedTimeBalance(nextLong(0,10));
        summary.setDeductedVolumeBalance(nextLong(0,10));
        summary.setCalculatedTimePulse(nextLong(0,10));
        summary.setCalculatedVolumePulse(nextLong(0,10));
        summary.setVolumePulse(nextLong(0,10));
        summary.setTimePulse(nextLong(0,10));
        summary.setReportOperation(ReportOperation.FINAL);
        summary.setRateCardName(uuid());
        summary.setDiscount(0);
        summary.setDiscountAmount(0);
        summary.setRateCardGroupName(uuid());
        summary.setRevenueCode(uuid());
        summary.setSessionStopTime(new Date(System.currentTimeMillis()));
        summary.setTariffType(PCRFKeyValueConstants.TARIFF_TYPE_NORMAL.val);
        summary.setRevenueCode("");
        return summary;
    }

    private String uuid() {
        UUID.randomUUID().toString()
    }

    private MonetaryBalance createMonetaryBalance(String monetaryBalanceId, double availableBalance) {
        new MonetaryBalance.MonetaryBalanceBuilder(monetaryBalanceId, "1", "1", UUID.randomUUID().toString(), nextLong())
                .withAvailableBalance(availableBalance).build()
    }

    private NonMonetoryBalance createNonMonetaryBalance(long ratingGroup, String nonMonetaryBalanceId) {
        new NonMonetoryBalance.NonMonetaryBalanceBuilder(nonMonetaryBalanceId, nextInt(0, Integer.MAX_VALUE), "1", ratingGroup,"1","1", 0, "1")
                .withBillingCycleVolumeBalance(500, 50)
                .withBillingCycleTimeBalance(500, 150).build()
    }

    public String getFormattedDate(Date date){
        SimpleDateFormat timestampFormat = csvDriverConfiguration.getCDRTimeStampFormat();
        String strDateFormat = timestampFormat.format(new Timestamp(date.getTime()));
        if(strDateFormat.contains(",")) {
            strDateFormat = strDateFormat.replaceAll(String.valueOf(CommonConstants.COMMA), "\\\\" + String.valueOf(CommonConstants.COMMA));
        }
        return strDateFormat;
    }

}