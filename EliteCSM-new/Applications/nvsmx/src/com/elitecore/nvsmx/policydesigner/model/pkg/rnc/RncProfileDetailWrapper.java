package com.elitecore.nvsmx.policydesigner.model.pkg.rnc;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;

import java.io.Serializable;
import java.util.Objects;

/**
 * Use of this class is to wrap the information of RnC Detail information into respective headers in to jsp file
 * Created by dhyani on 28/7/17.
 */
public class RncProfileDetailWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String fupLevel;
    private String quota;
    private String pulse;
    private String rate;
    private String dailyUsageLimit;
    private String weeklyUsageLimit;
    private String carryForwardLimit;
    private String serviceTypeName;
    private String serviceTypeId;
    private String ratingGroupName;
    private String ratingGroupId;

    private String revenueDetail;

    public RncProfileDetailWrapper(String id) {
        this.id = id;
    }

    public static class RncProfileDetailWrapperBuilder {
        private RncProfileDetailWrapper quotaProfileDetailWrapper;

        public RncProfileDetailWrapperBuilder(String id) {
            quotaProfileDetailWrapper = new RncProfileDetailWrapper(id);

        }

        public RncProfileDetailWrapper build() {
            return quotaProfileDetailWrapper;
        }

        public RncProfileDetailWrapperBuilder withQuotaProfileDetails(RncProfileDetailData rncProfileDetailData) {

            quotaProfileDetailWrapper.fupLevel = String.valueOf(rncProfileDetailData.getFupLevel());
            quotaProfileDetailWrapper.quota = getQuota(rncProfileDetailData);
            quotaProfileDetailWrapper.dailyUsageLimit = getDailyUsageLimit(rncProfileDetailData);
            quotaProfileDetailWrapper.weeklyUsageLimit = getWeeklyUsageLimit(rncProfileDetailData);
            quotaProfileDetailWrapper.pulse = getPulse(rncProfileDetailData);
            quotaProfileDetailWrapper.rate = getRate(rncProfileDetailData);
            quotaProfileDetailWrapper.serviceTypeName = rncProfileDetailData.getDataServiceTypeData().getName();
            quotaProfileDetailWrapper.ratingGroupName = rncProfileDetailData.getRatingGroupData().getName();
            quotaProfileDetailWrapper.ratingGroupId=rncProfileDetailData.getRatingGroupData().getId();
            quotaProfileDetailWrapper.serviceTypeId=rncProfileDetailData.getDataServiceTypeData().getId();
            quotaProfileDetailWrapper.carryForwardLimit = getCarryForwardLimit(rncProfileDetailData);
            quotaProfileDetailWrapper.revenueDetail = Objects.nonNull(rncProfileDetailData.getRevenueDetail())? rncProfileDetailData.getRevenueDetail().getName():"";
            return this;
        }

        private String getQuota(RncProfileDetailData rncProfileDetail) {

            String total = rncProfileDetail.getBalance() == null ?"" : String.valueOf(rncProfileDetail.getBalance()) + " " + rncProfileDetail.getBalanceUnit();
            String time = rncProfileDetail.getTimeBalance() == null ? "": String.valueOf(rncProfileDetail.getTimeBalance()) + " "+ rncProfileDetail.getTimeBalanceUnit();
            return getValue(total,time,rncProfileDetail.getRncProfileData());
        }

        private String getDailyUsageLimit( RncProfileDetailData rncProfileDetail ) {

            String total = rncProfileDetail.getDailyUsageLimit() == null ?"" : String.valueOf(rncProfileDetail.getDailyUsageLimit()) + " " + rncProfileDetail.getUsageLimitUnit();
            String time = rncProfileDetail.getDailyTimeLimit() == null ? "": String.valueOf(rncProfileDetail.getDailyTimeLimit()) + " "+ rncProfileDetail.getTimeLimitUnit();
            return getValue(total,time,rncProfileDetail.getRncProfileData());
        }

        private String getWeeklyUsageLimit( RncProfileDetailData rncProfileDetail ) {

            String total = rncProfileDetail.getWeeklyUsageLimit() == null ?"" : String.valueOf(rncProfileDetail.getWeeklyUsageLimit()) + " " + rncProfileDetail.getUsageLimitUnit();
            String time = rncProfileDetail.getWeeklyTimeLimit() == null ? "": String.valueOf(rncProfileDetail.getWeeklyTimeLimit()) + " "+ rncProfileDetail.getTimeLimitUnit();
            return getValue(total,time,rncProfileDetail.getRncProfileData());
        }

        private String getCarryForwardLimit( RncProfileDetailData rncProfileDetail ) {

            if(true == rncProfileDetail.getRncProfileData().getCarryForward().booleanValue()){
                String total = rncProfileDetail.getVolumeCarryForwardLimit() == null ?"" : String.valueOf(rncProfileDetail.getVolumeCarryForwardLimit()) + " " + DataUnit.MB.name();
                String time = rncProfileDetail.getTimeCarryForwardLimit() == null ? "": String.valueOf(rncProfileDetail.getTimeCarryForwardLimit()) + " "+ TimeUnit.MINUTE.name();
                return getValue(total,time,rncProfileDetail.getRncProfileData());
            }else{
                return "";
            }
        }

        private String getPulse( RncProfileDetailData rncProfileDetail ) {

            StringBuilder stringBuilder = new StringBuilder();

            String volume = rncProfileDetail.getPulseVolume() == null ?"" : String.valueOf(rncProfileDetail.getPulseVolume()) + " " + rncProfileDetail.getPulseVolumeUnit()+ "<br/>";
            String time = rncProfileDetail.getPulseTime() == null ? "": String.valueOf(rncProfileDetail.getPulseTime()) + " "+ rncProfileDetail.getPulseTimeUnit() + "<br/>";
            if(QuotaUsageType.TIME.name().equals(rncProfileDetail.getRncProfileData().getQuotaType()) == true){
                stringBuilder.append(time);
            } else if (QuotaUsageType.VOLUME.name().equals(rncProfileDetail.getRncProfileData().getQuotaType()) == true) {
                stringBuilder.append(volume);
            } else {
                stringBuilder.append(volume);
                stringBuilder.append(time);
            }
            return stringBuilder.toString();
        }

        private String getRate( RncProfileDetailData rncProfileDetail) {

            StringBuilder stringBuilder = new StringBuilder();

            String volume = rncProfileDetail.getRate() == null ?"" : String.format("%.6f", rncProfileDetail.getRate()) + " " + rncProfileDetail.getRateUnit()+ "<br/>";
            stringBuilder.append(volume);
            return stringBuilder.toString();
        }

        private String getValue(String total, String time, RncProfileData rncProfileData) {

            final String TOTAL = "<span style=\\\"width:10px;padding-right:10px\\\"></span><br/>";

            StringBuilder stringBuilder = new StringBuilder();
            String unitType = rncProfileData.getUnitType();
            String quotaType = rncProfileData.getQuotaType();

            if (QuotaUsageType.TIME.name().equals(quotaType)) {
                if(Strings.isNullOrBlank(time)) {
                    time = NVSMXCommonConstants.UNLIMITED_QUOTA_STRING;
                }
                stringBuilder.append(time).append(TOTAL);

            } else if(QuotaUsageType.VOLUME.name().equals(quotaType)) {
                if(Strings.isNullOrBlank(total)) {
                    total = NVSMXCommonConstants.UNLIMITED_QUOTA_STRING;
                }
                stringBuilder.append(total).append(fetchUnitTypeArrow(unitType));

            } else if(QuotaUsageType.HYBRID.name().equals(quotaType)) {
                if(Strings.isNullOrBlank(total)) {
                    total = NVSMXCommonConstants.UNLIMITED_QUOTA_STRING;
                }
                stringBuilder.append(total).append(fetchUnitTypeArrow(unitType));
                if(Strings.isNullOrBlank(time)) {
                    time = NVSMXCommonConstants.UNLIMITED_QUOTA_STRING;
                }
                stringBuilder.append(time).append(TOTAL);
            }

            return stringBuilder.toString();
        }

        private String fetchUnitTypeArrow(String unitType) {
            final String TOTAL = "<span style=\\\"width:10px;padding-right:10px\\\"></span><br/>";
            final String UPLOAD_STRING = "<span class=\\\"glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow\\\" style=\\\"float:right\\\"></span><br/>";
            final String DOWNLOAD_STRING = "<span class=\\\"glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow\\\" style=\\\"float:right\\\"></span><br/>";
            String unitTypeArrow = "";
            if (Strings.isNullOrBlank(unitType) == false) {

                if(VolumeUnitType.UPLOAD.name().equals(unitType)) {
                    unitTypeArrow = UPLOAD_STRING;
                } else if (VolumeUnitType.DOWNLOAD.name().equals(unitType)) {
                    unitTypeArrow = DOWNLOAD_STRING;
                } else {
                    unitTypeArrow = TOTAL;
                }
            }
            return unitTypeArrow;
        }
    }

    public String getId() {
        return id;
    }

    public String getFupLevel() {
        return fupLevel;
    }

    public String getQuota() {
        return quota;
    }

    public String getPulse() {
        return pulse;
    }

    public String getRate() {
        return rate;
    }

    public String getDailyUsageLimit() {
        return dailyUsageLimit;
    }

    public String getWeeklyUsageLimit() {
        return weeklyUsageLimit;
    }

    public String getCarryForwardLimit() {
        return carryForwardLimit;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public String getRatingGroupName() {
        return ratingGroupName;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public String getRatingGroupId() {
        return ratingGroupId;
    }

    public String getRevenueDetail() {
        return revenueDetail;
    }
}
