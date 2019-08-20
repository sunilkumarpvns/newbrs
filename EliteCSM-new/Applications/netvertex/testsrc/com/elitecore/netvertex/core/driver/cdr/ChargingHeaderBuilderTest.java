package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.sm.driver.constants.ReportingType;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.CSVDriverConfigurationImpl;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChargingHeaderBuilderTest {

    @Test
    public void headerWillContainHeaderFieldRealtedToCharingCDR() {

        CSVDriverConfiguration csvDriverConfiguration = new CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .withReportingType(ReportingType.CHARGING_CDR.getDisplayValue()).build();

        ChargingHeaderBuilder chargingHeaderBuilder = new ChargingHeaderBuilder(csvDriverConfiguration);

        StringBuilder expectedString = createFixHeader();

        System.out.println(expectedString);

        Assert.assertEquals(expectedString.toString(), chargingHeaderBuilder.getCSVHeaderLine());
    }

    private StringBuilder createFixHeader() {
        return new StringBuilder("SESSION_ID").append(CommonConstants.COMMA)
                .append("RATING_GROUP").append(CommonConstants.COMMA)
                .append("SERVICES").append(CommonConstants.COMMA)
                .append("REPORTED_VOLUME").append(CommonConstants.COMMA)
                .append("REPORTED_TIME").append(CommonConstants.COMMA)
                .append("REPORTED_EVENT").append(CommonConstants.COMMA)
                .append("VOLUME_PULSE").append(CommonConstants.COMMA)
                .append("CALCULATED_VOLUME_PULSE").append(CommonConstants.COMMA)
                .append("TIME_PULSE").append(CommonConstants.COMMA)
                .append("CALCULATED_TIME_PULSE").append(CommonConstants.COMMA)
                .append("UNACCOUNTED_VOLUME").append(CommonConstants.COMMA)
                .append("UNACCOUNTED_TIME").append(CommonConstants.COMMA)
                .append("PREVIOUS_UNACCOUNTED_VOUME").append(CommonConstants.COMMA)
                .append("PREVIOUS_UNACCOUNTED_TIME").append(CommonConstants.COMMA)
                .append("ACCOUNTED_NON_MONETARY_VOLUME").append(CommonConstants.COMMA)
                .append("ACCOUNTED_NON_MONETARY_TIME").append(CommonConstants.COMMA)
                .append("ACCOUNTED_MONETARY").append(CommonConstants.COMMA)
                .append("RATE").append(CommonConstants.COMMA)
                .append("DISCOUNT(%)").append(CommonConstants.COMMA)
                .append("DISCOUNTED_AMOUNT").append(CommonConstants.COMMA)
                .append("RATE_MINOR_UNIT").append(CommonConstants.COMMA)
                .append("CURRENCY").append(CommonConstants.COMMA)
                .append("EXPONENT").append(CommonConstants.COMMA)
                .append("PACKAGE_NAME").append(CommonConstants.COMMA)
                .append("PRODUCT_OFFER_NAME").append(CommonConstants.COMMA)
                .append("EVENT_ACTION").append(CommonConstants.COMMA)
                .append("QUOTA_PROFILE").append(CommonConstants.COMMA)
                .append("LEVEL").append(CommonConstants.COMMA)
                .append("CALLING_PARTY").append(CommonConstants.COMMA)
                .append("CALLED_PARTY").append(CommonConstants.COMMA)
                .append("RATECARD").append(CommonConstants.COMMA)
                .append("RATECARD_GROUP").append(CommonConstants.COMMA)
                .append("OPERATION").append(CommonConstants.COMMA)
                .append("SERVICE").append(CommonConstants.COMMA)
                .append("CALL_TYPE").append(CommonConstants.COMMA)
                .append("TARIFF_TYPE").append(CommonConstants.COMMA)
                .append("REVENUE_CODE").append(CommonConstants.COMMA)
                .append("CALL_START").append(CommonConstants.COMMA)
                .append("CALL_STOP");
    }

    @Test
    public void headerWillContainsTimestampHeaderWhenCDRTimestampFormatConfigured() {

        CSVDriverConfiguration csvDriverConfiguration = new CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .withReportingType(ReportingType.CHARGING_CDR.getDisplayValue())
                .withCDRTimeStampFormat(new SimpleDateFormat("dd/MM/yyyy hh:mm a")).build();
        ChargingHeaderBuilder chargingHeaderBuilder = new ChargingHeaderBuilder(csvDriverConfiguration);

        StringBuilder expectedString = createFixHeader().append(CommonConstants.COMMA).append("CDRTIMESTAMP");

        Assert.assertEquals(expectedString.toString(), chargingHeaderBuilder.getCSVHeaderLine());
    }

    @Test
    public void headerWillContainsTimestampHeaderWhenCDRTimestampFormatNotConfigured() {


        CSVDriverConfiguration csvDriverConfiguration = new CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .withReportingType(ReportingType.CHARGING_CDR.getDisplayValue())
                .build();
        ChargingHeaderBuilder chargingHeaderBuilder = new ChargingHeaderBuilder(csvDriverConfiguration);

        StringBuilder expectedString = createFixHeader();

        Assert.assertEquals(expectedString.toString(), chargingHeaderBuilder.getCSVHeaderLine());
    }

    @Test
    public void appendAdditionFieldMappingWithHeader() {

        List<CSVFieldMapping> fieldMappings = new ArrayList<>();
        fieldMappings.add(new CSVFieldMapping("IMSI", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val));
        fieldMappings.add(new CSVFieldMapping("MSISDN", PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val));

        CSVDriverConfiguration csvDriverConfiguration = new CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .withReportingType(ReportingType.CHARGING_CDR.getDisplayValue())
                .withFieldMappings(fieldMappings)
                .withCDRTimeStampFormat(new SimpleDateFormat("dd/MM/yyyy hh:mm a")).build();
        ChargingHeaderBuilder chargingHeaderBuilder = new ChargingHeaderBuilder(csvDriverConfiguration);

        StringBuilder expectedString = createFixHeader().append(CommonConstants.COMMA)
                .append("IMSI").append(CommonConstants.COMMA)
                .append("MSISDN").append(CommonConstants.COMMA)
                .append("CDRTIMESTAMP");

        Assert.assertEquals(expectedString.toString(), chargingHeaderBuilder.getCSVHeaderLine());
    }
}
