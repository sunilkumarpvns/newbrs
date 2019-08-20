package com.elitecore.nvsmx.ws.util;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class ConvertStringToDigit {

    public static Long convertStringToLong(String strValue) throws OperationFailedException {
        if (Strings.isNullOrBlank(strValue)) {
            return null;
        }
        strValue = StringUtils.trim(strValue);
        if (NumberUtils.isDigits(strValue) == false) {
            throw new OperationFailedException("Invalid credit limit (" + strValue + ") received, only numeric values are allowed ", ResultCode.INVALID_INPUT_PARAMETER);
        }
        try {
            return Long.parseLong(strValue);
        } catch (NumberFormatException e) {
            throw new OperationFailedException("Invalid credit limit (" + strValue + ") received, only numeric values are allowed ", ResultCode.INVALID_INPUT_PARAMETER);
        }
    }

    public static Integer convertStringToInt(String strValue) throws OperationFailedException {
        if (Strings.isNullOrBlank(strValue)) {
            return null;
        }
        strValue = StringUtils.trim(strValue);
        if (NumberUtils.isDigits(strValue) == false) {
            throw new OperationFailedException("Invalid applicable billing cycle (" + strValue + ") received, only numeric values are allowed ", ResultCode.INVALID_INPUT_PARAMETER);
        }
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            throw new OperationFailedException("Invalid applicable billing cycle (" + strValue + ") received, only numeric values are allowed ", ResultCode.INVALID_INPUT_PARAMETER);
        }

    }
}
