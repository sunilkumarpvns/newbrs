package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.netvertex.rnc.ReportedUsageSummary;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static java.util.Objects.nonNull;

public class ChargingHeaderBuilder {

    private static final String CDRTIMESTAMP = "CDRTIMESTAMP";
    private CSVDriverConfiguration csvDriverConf;
    private String delimiter;
    private String csvHeaderLine;
    private List<BiFunction<ReportedUsageSummary, PCRFResponse, String>> coloumValueProviders;

    public ChargingHeaderBuilder(@Nonnull CSVDriverConfiguration csvDriverConf){
        this.csvDriverConf = csvDriverConf;
        delimiter = csvDriverConf.getDelimiter();
    }

    public String getCSVHeaderLine() {
        if (csvHeaderLine == null) {
            StringBuilder csvHeaderLine = new StringBuilder();
            coloumValueProviders = new ArrayList<>();

            csvHeaderLine.append(getHeader());

            if(Objects.nonNull(csvDriverConf.getCDRFieldMappings())){
                for (CSVFieldMapping mapping : csvDriverConf.getCDRFieldMappings()) {
                    csvHeaderLine.append(mapping.getHeaderField()).append(delimiter);
                    coloumValueProviders.add((reportedUsageSummary, pcrfResponse) -> pcrfResponse.getAttribute(mapping.getKey()));
                }
            }

            if (csvDriverConf.getCDRTimeStampFormat() != null) {
                csvHeaderLine.append(CDRTIMESTAMP);
            } else {
                csvHeaderLine.delete(csvHeaderLine.length() - csvDriverConf.getDelimiter().length(), csvHeaderLine.length());
            }
            this.csvHeaderLine = csvHeaderLine.toString();
        }
        return csvHeaderLine;
    }

    public List<BiFunction<ReportedUsageSummary, PCRFResponse, String>> getColoumValueProviders() {
        if(Objects.isNull(csvHeaderLine)) {
            getCSVHeaderLine();
        }
        return coloumValueProviders;
    }

    private String getHeader() {
        StringBuilder csvHeaderLine = new StringBuilder();

        if(nonNull(csvDriverConf.getCoreSessionIdHeader())){
            csvHeaderLine.append(csvDriverConf.getCoreSessionIdHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CORESESSION_ID.getValueProvider());

        }
        if(nonNull(csvDriverConf.getRatingGroupHeader())){
            csvHeaderLine.append(csvDriverConf.getRatingGroupHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.RATING_GROUP.getValueProvider());
        }
        if(nonNull(csvDriverConf.getServiceIdentifierHeader())){
            csvHeaderLine.append(csvDriverConf.getServiceIdentifierHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.SERVICE_IDENTIFIER.getValueProvider());
        }
        if(nonNull(csvDriverConf.getReportedVolumeHeader())){
            csvHeaderLine.append(csvDriverConf.getReportedVolumeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.REPORTED_VOLUME.getValueProvider());
        }
        if(nonNull(csvDriverConf.getReportedUsageTimeHeader())){
            csvHeaderLine.append(csvDriverConf.getReportedUsageTimeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.REPORTED_TIME.getValueProvider());
        }
        if(nonNull(csvDriverConf.getReportedEventHeader())){
            csvHeaderLine.append(csvDriverConf.getReportedEventHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.REPORTED_EVENT.getValueProvider());
        }
        if(nonNull(csvDriverConf.getVolumePulseHeader())){
            csvHeaderLine.append(csvDriverConf.getVolumePulseHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.VOLUME_PULSE.getValueProvider());
        }
        if(nonNull(csvDriverConf.getCalculatedVolumePulseHeader())){
            csvHeaderLine.append(csvDriverConf.getCalculatedVolumePulseHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CALCULATED_VOLUME_PULSE.getValueProvider());
        }
        if(nonNull(csvDriverConf.getTimePulseHeader())){
            csvHeaderLine.append(csvDriverConf.getTimePulseHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.TIME_PULSE.getValueProvider());
        }
        if(nonNull(csvDriverConf.getCalculatedTimePulseHeader())){
            csvHeaderLine.append(csvDriverConf.getCalculatedTimePulseHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CALCULATED_TIME_PULSE.getValueProvider());
        }
        if(nonNull(csvDriverConf.getUnAccountedVolumeHeader())){
            csvHeaderLine.append(csvDriverConf.getUnAccountedVolumeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.UNACCOUNTED_VOLUME.getValueProvider());
        }
        if(nonNull(csvDriverConf.getUnAccountedUsageTimeHeader())){
            csvHeaderLine.append(csvDriverConf.getUnAccountedUsageTimeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.UNACCOUNTED_TIME.getValueProvider());
        }

        if(nonNull(csvDriverConf.getPreviousUnAccountedVolumeHeader())) {
            csvHeaderLine.append(csvDriverConf.getPreviousUnAccountedVolumeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.PREVIOUS_UNACCOUNTED_VOUME.getValueProvider());
        }

        if(nonNull(csvDriverConf.getPreviousUnAccountedTimeHeader())) {
            csvHeaderLine.append(csvDriverConf.getPreviousUnAccountedTimeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.PREVIOUS_UNACCOUNTED_TIME.getValueProvider());
        }

        if(nonNull(csvDriverConf.getAccountedNonMonetaryVolumeHeader())){
            csvHeaderLine.append(csvDriverConf.getAccountedNonMonetaryVolumeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.ACCOUNTED_NON_MONETARY_VOLUME.getValueProvider());
        }
        if(nonNull(csvDriverConf.getAccountedNonMonetaryUsageTimeHeader())){
            csvHeaderLine.append(csvDriverConf.getAccountedNonMonetaryUsageTimeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.ACCOUNTED_NON_MONETARY_TIME.getValueProvider());
        }
        if(nonNull(csvDriverConf.getAccountedMonetaryHeader())){
            csvHeaderLine.append(csvDriverConf.getAccountedMonetaryHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.ACCOUNTED_MONETARY.getValueProvider());
        }
        if(nonNull(csvDriverConf.getRateHeader())){
            csvHeaderLine.append(csvDriverConf.getRateHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.RATE.getValueProvider());
        }
        if(nonNull(csvDriverConf.getDiscountHeader())){
            csvHeaderLine.append(csvDriverConf.getDiscountHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.DISCOUNT.getValueProvider());
        }
        if(nonNull(csvDriverConf.getDiscountAmountHeader())){
            csvHeaderLine.append(csvDriverConf.getDiscountAmountHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.DISCOUNT_AMOUNT.getValueProvider());
        }
        if(nonNull(csvDriverConf.getRateMinorUnitHeader())){
            csvHeaderLine.append(csvDriverConf.getRateMinorUnitHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.RATE_MINOR_UNIT.getValueProvider());
        }

        if(nonNull(csvDriverConf.getCurrencyHeader())){
            csvHeaderLine.append(csvDriverConf.getCurrencyHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CURRENCY.getValueProvider());
        }

        if(nonNull(csvDriverConf.getExponentHeader())){
            csvHeaderLine.append(csvDriverConf.getExponentHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.EXPONENT.getValueProvider());
        }

        if(nonNull(csvDriverConf.getPackageNameHeader())){
            csvHeaderLine.append(csvDriverConf.getPackageNameHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.PACKAGE_NAME.getValueProvider());
        }

        if(nonNull(csvDriverConf.getProductOfferNameHeader())){
            csvHeaderLine.append(csvDriverConf.getProductOfferNameHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.PRODUCT_OFFER_NAME.getValueProvider());
        }

        if(nonNull(csvDriverConf.getEventActionHeader())){
            csvHeaderLine.append(csvDriverConf.getEventActionHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.EVENT_ACTION.getValueProvider());
        }

        if(nonNull(csvDriverConf.getQuotaProfileNameHeader())){
            csvHeaderLine.append(csvDriverConf.getQuotaProfileNameHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.QUOTA_PROFILE.getValueProvider());
        }

        if(nonNull(csvDriverConf.getLevelHeader())){
            csvHeaderLine.append(csvDriverConf.getLevelHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.LEVEL.getValueProvider());
        }

        if(nonNull(csvDriverConf.getCallingPartyHeader())){
            csvHeaderLine.append(csvDriverConf.getCallingPartyHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CALLING_PARTY.getValueProvider());
        }

        if(nonNull(csvDriverConf.getCalledPartyHeader())){
            csvHeaderLine.append(csvDriverConf.getCalledPartyHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CALLED_PARTY.getValueProvider());
        }

        if(nonNull(csvDriverConf.getRateCardHeader())){
            csvHeaderLine.append(csvDriverConf.getRateCardHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.RATE_CARD.getValueProvider());
        }

        if(nonNull(csvDriverConf.getRateCardGroupHeader())){
            csvHeaderLine.append(csvDriverConf.getRateCardGroupHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.RATE_CARD_GROUP.getValueProvider());
        }

        if(nonNull(csvDriverConf.getChargingOperationHeader())){
            csvHeaderLine.append(csvDriverConf.getChargingOperationHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.OPERATION.getValueProvider());
        }

        if(nonNull(csvDriverConf.getServiceHeader())){
            csvHeaderLine.append(csvDriverConf.getServiceHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.SERVICE.getValueProvider());
        }

        if(nonNull(csvDriverConf.getCallTypeHeader())){
            csvHeaderLine.append(csvDriverConf.getCallTypeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CALL_TYPE.getValueProvider());
        }

        if(nonNull(csvDriverConf.getTariffTypeHeader())){
            csvHeaderLine.append(csvDriverConf.getTariffTypeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.TARIFF_TYPE.getValueProvider());
        }

        if(nonNull(csvDriverConf.getRevenueCodeHeader())) {
            csvHeaderLine.append(csvDriverConf.getRevenueCodeHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.REVENUE_CODE.getValueProvider());
        }

        if(nonNull(csvDriverConf.getCallStartHeader())){
            csvHeaderLine.append(csvDriverConf.getCallStartHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CALL_START.getValueProvider());
        }

        if(nonNull(csvDriverConf.getCallStopHeader())){
            csvHeaderLine.append(csvDriverConf.getCallStopHeader()).append(delimiter);
            coloumValueProviders.add(MsccCdrFields.CALL_END.getValueProvider());
        }

        return csvHeaderLine.toString();
    }

}
