package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class RnCProfileValidator implements Validator<RncProfileData,PkgData,PkgData> {
    private static final String MODULE = "RNC-PROFILE-VALIDATOR";
    public static final Long LONG_MAX_VALUE = 999999999999999L;
    private static final Pattern RATE_REGEX = Pattern.compile("^((\\d{0,9}(\\.\\d{0,6})?))$");

    @Override
    public List<String> validate(RncProfileData rncProfileImported, final PkgData pkgData, PkgData superObject, String action, SessionProvider session) {

        List<String> subReasons = new ArrayList<>();
        if(QuotaProfileType.RnC_BASED == superObject.getQuotaProfileType()) {
            String rncProfileName = rncProfileImported.getName();
            try {

                List<RncProfileDetailData> rncProfileDetails = rncProfileImported.getRncProfileDetailDatas();
                if (Collectionz.isNullOrEmpty(rncProfileDetails) == false) {
                    for (RncProfileDetailData rncProfileDetailData : rncProfileDetails) {
                        Long pulseVolume = rncProfileDetailData.getPulseVolume();
                        Long pulseTime = rncProfileDetailData.getPulseTime();
                        validatePulse(subReasons, pulseVolume, "volume");
                        validatePulse(subReasons, pulseTime, "time");
                        validateRate(subReasons, rncProfileDetailData);
                        validateRevenueCode(subReasons, rncProfileDetailData, session);
                    }
                }

                if (Objects.equals(RenewalIntervalUnit.TILL_BILL_DATE.name(), rncProfileImported.getRenewalIntervalUnit())) {
                    rncProfileImported.setRenewalInterval(null);
                } else {
                    if (Objects.nonNull(rncProfileImported.getRenewalInterval())) {
                        if (rncProfileImported.getRenewalInterval() <= 0 || rncProfileImported.getRenewalInterval() > 999) {
                            subReasons.add("Renewal Interval must be between 1 to 999");
                        }
                    }
                }
                if(isValidRenewalInterval(rncProfileImported)){
                    subReasons.add("Invalid renewal interval unit. Valid values are (TILL_BILL_DATE, MONTH_END or MONTH)");
                }
                //validate proration field
                if(rncProfileImported.getProration() == null || CommonStatusValues.fromBooleanValue(rncProfileImported.getProration()) == null){
                    if(LogManager.getLogger().isDebugLogLevel()){
                        LogManager.getLogger().debug(MODULE, "Invalid Proration Field: " + rncProfileImported.getProration() + " configured for Qos Profile detail" + BasicValidations.printIdAndName(rncProfileImported.getId(), rncProfileName) + ". So taking default proration as DISABLE(0)");
                    }
                    rncProfileImported.setProration(CommonStatusValues.DISABLE.isBooleanValue());
                }

                //validate carry-forward field
                if(rncProfileImported.getCarryForward() == null || CommonStatusValues.fromBooleanValue(rncProfileImported.getCarryForward()) == null){
                    if(LogManager.getLogger().isDebugLogLevel()){
                        LogManager.getLogger().debug(MODULE, "Invalid carry Forward Field: " + rncProfileImported.getCarryForward() + " configured for Qos Profile detail" + BasicValidations.printIdAndName(rncProfileImported.getId(), rncProfileName) + ". So taking default proration as DISABLE(0)");
                    }
                    rncProfileImported.setCarryForward(CommonStatusValues.DISABLE.isBooleanValue());
                }

                if(isValidRenewalInterval(rncProfileImported)){
                    rncProfileImported.setProration(CommonStatusValues.DISABLE.isBooleanValue());
                }

            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Failed to validate rnc profile " + BasicValidations.printIdAndName(rncProfileImported.getId(), rncProfileName) + " associated with package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
                LogManager.getLogger().trace(MODULE, e);
                subReasons.add("Failed to validate rnc profile" + BasicValidations.printIdAndName(rncProfileImported.getId(), rncProfileName) + " associated with Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) + ". Kindly refer logs for further details");
            }
        }
        return subReasons;
    }

    private void validateRevenueCode(List<String> subReasons, RncProfileDetailData rncProfileDetailData, SessionProvider session) throws Exception {
        if (Objects.nonNull(rncProfileDetailData.getRevenueDetail()) && Strings.isNullOrEmpty(rncProfileDetailData.getRevenueDetail().getName()) == false) {
            if (CollectionUtils.isEmpty(ImportExportCRUDOperationUtil.getAll(RevenueDetailData.class, "name", rncProfileDetailData.getRevenueDetail().getName(), session))) {
                subReasons.add("No such revenue code found : " + rncProfileDetailData.getRevenueDetail() + " for data service type : " + rncProfileDetailData.getDataServiceTypeData().getName());
            }
        }else{
            rncProfileDetailData.setRevenueDetail(null);
        }
    }

    private boolean isValidRenewalInterval(RncProfileData rncProfileImported) {
        return RenewalIntervalUnit.TILL_BILL_DATE.name().equals(rncProfileImported.getRenewalIntervalUnit()) == false &&
                RenewalIntervalUnit.MONTH.name().equals(rncProfileImported.getRenewalIntervalUnit())  == false &&
                RenewalIntervalUnit.MONTH_END.name().equals(rncProfileImported.getRenewalIntervalUnit()) == false;
    }

    protected void validateRate(List<String> subReasons, RncProfileDetailData rncProfileDetailData) {
        Double rate = rncProfileDetailData.getRate();
        if (rate == null) {
            return;
        }
        String rateStr = BigDecimal.valueOf(rate).toPlainString();
        if (rate != null && rate < 0) {
            subReasons.add("Rate must be greater than equal to zero(0)");
        } else if (rate != null && rate > CommonConstants.MONETARY_VALUE_LIMIT) {
            subReasons.add("Rate allows maximum 9 numbers and up to 6 decimal");
        } else if (rate != null && RATE_REGEX.matcher(rateStr).matches() == false) {
            subReasons.add("Rate allows maximum 9 numbers and up to 6 decimal");
        }
    }

    private void validatePulse(List<String> subReasons, Long pulse, String message) {
        if(pulse != null && (pulse <= 0 || pulse > LONG_MAX_VALUE)){
            subReasons.add("Pulse "+ message +" value must be between 1 to " + LONG_MAX_VALUE);
        }
    }
}
