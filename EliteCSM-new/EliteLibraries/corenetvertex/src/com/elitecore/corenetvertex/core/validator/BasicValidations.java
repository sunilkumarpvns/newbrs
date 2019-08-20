package com.elitecore.corenetvertex.core.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;

/**
 * Created by Ishani on 8/7/16.
 */
public class BasicValidations {

    private static Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+.*[a-zA-Z0-9)]+$");

    public static void validateName(String name,String field, List<String> subReasons){
        if(Strings.isNullOrBlank(name)){
            subReasons.add(field + " Name must be provided");
        }else{
            Matcher matcher = pattern.matcher(name);
            if(name.length() < 2 || name.length() > 100){
                subReasons.add(field + " Name must be between (2-100): " + name);
            }else if(matcher.matches() == false){
                subReasons.add("Invalid Name (Name Should start and End with Alpha-numeric character): " + name + " configured with " + field);
            }
        }


    }


    public static void validateSliceInformation(boolean usageMonitoring, Long sliceTotal, String sliceTotalUnit, Long sliceDownload, String sliceDownloadUnit, Long sliceUpload, String sliceUploadUnit, Long sliceTime, String sliceTmeUnit, List<String> subReasons, String entityName) {
        if (sliceTotal== null && sliceDownload == null && sliceUpload == null && sliceTime == null &&  usageMonitoring== true) {
            subReasons.add("At least one slice should be configured for: " + entityName);
        }

        if (sliceTotal != null && isPositiveNonZeroNumber(sliceTotal) == false) {
            subReasons.add(FieldValueConstants.SLICE_TOTAL + " must be positive numeric for: " + entityName);
        }
        checkForValidQuota(FieldValueConstants.SLICE_TOTAL, sliceTotal, sliceTotalUnit,subReasons, entityName);
        if (sliceDownload != null
                && isPositiveNonZeroNumber(sliceDownload) == false) {
            subReasons.add(FieldValueConstants.SLICE_DOWNLOAD + " must be positive numeric for: " + entityName);
        }
        checkForValidQuota(FieldValueConstants.SLICE_DOWNLOAD, sliceDownload, sliceDownloadUnit,subReasons, entityName);


        if (sliceUpload != null
                && isPositiveNonZeroNumber(sliceUpload) == false) {
            subReasons.add(FieldValueConstants.SLICE_UPLOAD + " must be positive numeric for: " + entityName);
        }
        checkForValidQuota(FieldValueConstants.SLICE_UPLOAD, sliceUpload, sliceUploadUnit,subReasons , entityName);

        if (sliceTime != null
                && isPositiveNonZeroNumber(sliceTime) == false) {
            subReasons.add(FieldValueConstants.SLICE_TIME + " must be positive numeric for: " + entityName);
        }
        checkForValidSliceTime(FieldValueConstants.SLICE_TIME, sliceTime, sliceTmeUnit, subReasons, entityName);


    }

    private static void checkForValidSliceTime(String field, Long value, String unit, List<String> subReasons, String entityName) {
        if (value == null) {
            if(Strings.isNullOrBlank(unit)) {
                return;
            }else{
                TimeUnit timeUnit = TimeUnit.fromVal(unit);
                if(timeUnit == null){
                    subReasons.add("Invalid Unit: " + unit + " is configured with " + field + " associated with entity: " + entityName);
                    return;
                }

            }
        }

        boolean isValidSliceTime = true;
        Long maxSliceTimeValueInSecond = 86400L;
        Long maxSliceTimeValueInMinute = 1440L;
        Long maxSliceTimeValueInHour = 24L;
        if(value != null) {
            if (TimeUnit.HOUR.name().equals(unit)) {
                isValidSliceTime = (value > maxSliceTimeValueInHour) ? false : true;
                if (isValidSliceTime == false) {
                    subReasons.add("Max allowed value in HOUR is " + maxSliceTimeValueInHour + " for " + field + " associated with entity: " + entityName);
                }
            } else if (TimeUnit.MINUTE.name().equals(unit)) {
                isValidSliceTime = (value > maxSliceTimeValueInMinute) ? false : true;
                if (isValidSliceTime == false) {
                    subReasons.add("Max allowed value in MINUTE is " + maxSliceTimeValueInMinute + " for " + field + " associated with entity: " + entityName);
                }
            } else if (TimeUnit.SECOND.name().equals(unit)) {
                isValidSliceTime = (value > maxSliceTimeValueInSecond) ? false : true;
                if (isValidSliceTime == false) {
                    subReasons.add("Max allowed value in SECOND is " + maxSliceTimeValueInSecond + " for " + field + " associated with entity: " + entityName);
                }
            } else {
                subReasons.add("Invalid Time unit: " + unit + " is configured for " + field + " associated with entity: " + entityName);
            }
        }
    }


    private static void checkForValidQuota(String field, Long value, String unit, List<String> subReasons, String entityName) {
        if (value == null && Strings.isNullOrBlank(unit)) {
            return;
        }
        DataUnit dataUnit = DataUnit.fromName(unit);
        if (dataUnit == null) {
            subReasons.add("Invalid Unit: " + unit +"  is configured for " + field + " associated with entity: " + entityName);
        } else if (value != null && dataUnit.isInRange(value) == false) {
            subReasons.add("Max Value in " + dataUnit.name() + " is " + dataUnit.maxValueForSlice + " for " + field + " associated with entity: " + entityName);
        }

    }

    public static void checkForValidQos(String field,Long value, String unit,List<String> subReasons,String entityName) {
        if (value == null) {
            return;
        }
        QoSUnit qosUnit  = QoSUnit.fromVal(unit);
        if(qosUnit == null){
            subReasons.add("Invalid value " + unit + " configured in Qos unit parameter for " + field + " associated with entity: " + entityName);
        }else if(qosUnit.isInRange(value) == false){
            subReasons.add("Max Value in "+ qosUnit.name() +" is " + qosUnit.maxValueForQoS + " for " + field + " associated with entity: " + entityName);
        }

    }


    public static boolean isPositiveNumber(Long val) {
        if (val < 0) {
            return false;
        }
        return true;
    }

    public static boolean isPositiveNonZeroNumber(Long val) {
        if (val <= 0) {
            return false;
        }
        return true;
    }

    public static String printIdAndName(String id, String name){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(CommonConstants.OPENING_PARENTHESES);
        if(Strings.isNullOrBlank(id) == false){
            strBuilder.append(id);
        }
        if(Strings.isNullOrBlank(name) == false){
            if(Strings.isNullOrBlank(id) == false) {
                strBuilder.append(CommonConstants.COMMA);
            }
            strBuilder.append(name);
        }
        strBuilder.append(CommonConstants.CLOSING_PARENTHESES);
        return strBuilder.toString();
    }



}
