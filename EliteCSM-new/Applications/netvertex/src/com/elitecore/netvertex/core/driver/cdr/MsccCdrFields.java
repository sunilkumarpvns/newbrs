package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.rnc.ReportedUsageSummary;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public enum MsccCdrFields {
    RATING_GROUP {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getRatingGroup());
        }
    }, CORESESSION_ID {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getCoreSessionId();
        }
    }, SERVICE_IDENTIFIER {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            List<Long> serviceIds = reportedUsageSummary.getServiceIds();
            if (Objects.isNull(serviceIds)) {
                return null;
            }

            StringBuilder serviceIdentifierStr = new StringBuilder(1000);
            serviceIdentifierStr.append(String.valueOf(serviceIds.get(0)));

            for (int index = 1; index < serviceIds.size(); index++) {
                serviceIdentifierStr.append(CommonConstants.COMMA).append(String.valueOf(serviceIds.get(index)));
            }
            return serviceIdentifierStr.toString();
        }
    }, REPORTED_VOLUME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getReportedVolume());
        }
    }, REPORTED_TIME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getReportedTime());
        }
    }, REPORTED_EVENT {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getReportedEvent());
        }
    }, VOLUME_PULSE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getVolumePulse());
        }
    }, TIME_PULSE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getTimePulse());
        }
    }, CALCULATED_VOLUME_PULSE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getCalculatedVolumePulse());
        }
    }, CALCULATED_TIME_PULSE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getCalculatedTimePulse());
        }
    }, UNACCOUNTED_TIME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getCurrentUnAccountedTime());
        }
    }, UNACCOUNTED_VOLUME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getCurrentUnAccountedVolume());
        }
    }, ACCOUNTED_NON_MONETARY_VOLUME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getDeductedVolumeBalance());
        }
    }, ACCOUNTED_NON_MONETARY_TIME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getDeductedTimeBalance());
        }
    }, ACCOUNTED_MONETARY {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getDeductedMonetaryBalance());
        }
    }, RATE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getRate());
        }
    }, DISCOUNT {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getDiscount());
        }
    }, DISCOUNT_AMOUNT {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getDiscountAmount());
        }
    } , RATE_MINOR_UNIT {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getRateMinorUnit());
        }
    }, CURRENCY {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getCurrency();
        }
    }, EXPONENT {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getExponent());
        }
    }, PREVIOUS_UNACCOUNTED_VOUME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getPreviousUnAccountedVolume());
        }
    }, PREVIOUS_UNACCOUNTED_TIME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getPreviousUnAccountedTime());
        }
    }, PACKAGE_NAME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getPackageName();
        }
    }, PRODUCT_OFFER_NAME {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getProductOfferName();
        }
    }, EVENT_ACTION {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getRequestedAction();
        }
    }, QUOTA_PROFILE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getQuotaProfileName();
        }
    }, LEVEL {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return String.valueOf(reportedUsageSummary.getLevel());
        }
    }, CALLING_PARTY {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return pcrfResponse.getAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val);
        }
    }, CALLED_PARTY {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return pcrfResponse.getAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.val);
        }
    }, CALL_START {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return Objects.nonNull(pcrfResponse.getSessionStartTime())?getFormattedDate(pcrfResponse.getSessionStartTime(), pcrfResponse.getChargingCDRDateFormat()):"";
        }
    }, CALL_END {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return Objects.nonNull(reportedUsageSummary.getSessionStopTime())?getFormattedDate(reportedUsageSummary.getSessionStopTime(), pcrfResponse.getChargingCDRDateFormat()):"";
        }
    }, RATE_CARD {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getRateCardName();
        }
    }, RATE_CARD_GROUP {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getRateCardGroupName();
        }
    }, OPERATION {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getReportOperation().name();
        }
    }, SERVICE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return pcrfResponse.getAttribute(PCRFKeyConstants.CS_SERVICE.val);
        }
    }, CALL_TYPE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return pcrfResponse.getAttribute(PCRFKeyConstants.CS_CALLTYPE.val);
        }
    }, TARIFF_TYPE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getTariffType();
        }
    }, REVENUE_CODE {
        @Override
        public String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse) {
            return reportedUsageSummary.getRevenueCode();
        }
    };


    public abstract String getValue(ReportedUsageSummary reportedUsageSummary, PCRFResponse pcrfResponse);

    public BiFunction<ReportedUsageSummary, PCRFResponse, String> getValueProvider() {
        return MsccCdrFields.this::getValue;
    }

    public String getHeader() {
        return name();
    }

    public String getFormattedDate(Date date, SimpleDateFormat dateFormat) {
        SimpleDateFormat dateFormatVar = dateFormat==null?
                new SimpleDateFormat(CommonConstants.DEFAULT_TIMESTAMP_FORMAT): dateFormat;
        String strDateFormat = dateFormatVar.format(new Timestamp(date.getTime()));
        if (strDateFormat.contains(",")) {
            strDateFormat = strDateFormat.replaceAll(String.valueOf(CommonConstants.COMMA), "\\" + CommonConstants.COMMA);
        }
        return strDateFormat;
    }
}
