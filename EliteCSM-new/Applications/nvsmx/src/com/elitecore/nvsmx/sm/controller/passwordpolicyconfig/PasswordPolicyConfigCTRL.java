package com.elitecore.nvsmx.sm.controller.passwordpolicyconfig;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.policy.PasswordPolicyDAO;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author ashishkothari
 */
@ParentPackage(value = "sm")
@Namespace("/sm/passwordpolicyconfig")
@Results({
        @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {"actionName", "password-policy-config"}),
})
public class PasswordPolicyConfigCTRL extends CreateNotSupportedCTRL<PasswordPolicyConfigData> {

    private boolean validPasswordPolicy;

    @Override
    public HttpHeaders show() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called show()");

        }
        try {
            setModel(CRUDOperationUtil.get(PasswordPolicyConfigData.class, NVSMXCommonConstants.DEFAULT_PASSWORD_POLICY_ID));
            validPasswordPolicy = PasswordPolicyDAO.isValidPasswordPolicy((PasswordPolicyConfigData) getModel());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        } catch (Exception e) {
            addActionError("Error while viewing " + getModule().getDisplayLabel());
            getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " data. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }


        addActionError(getModule().getDisplayLabel() + " Not Found");
        return new DefaultHttpHeaders("error").withStatus(400);

    }

    @Override
    public String edit() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called edit()");
        }
        try {
            setModel(CRUDOperationUtil.get(PasswordPolicyConfigData.class, NVSMXCommonConstants.DEFAULT_PASSWORD_POLICY_ID));
            if (isAdminUser(getStaffData()) == false) {
                addActionError(getText("superadmin.allowed.access"));
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return NVSMXCommonConstants.REDIRECT_URL;
            }
            setActionChainUrl(getRedirectURL(METHOD_EDIT));
            return NVSMXCommonConstants.REDIRECT_URL;
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing Update Operation");
            return ERROR;
        }
    }

    @Override
    public HttpHeaders update() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called update()");
        }

        if (isAdminUser(getStaffData()) == false) {
            addActionError(getText("superadmin.allowed.access"));
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        }
        return super.update();
    }

    @Override
    public void validate() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called validate()");
        }
        validatePasswordPolicy((PasswordPolicyConfigData) getModel());
    }

    private void validatePasswordPolicy(PasswordPolicyConfigData passwordPolicyConfigData) {

        if(checkPasswordRange(passwordPolicyConfigData) == false) {
            return;
        }

        if (passwordPolicyConfigData.getTotalHistoricalPasswords() == null || passwordPolicyConfigData.getTotalHistoricalPasswords() > 10 || passwordPolicyConfigData.getTotalHistoricalPasswords() < 1) {
            getLogger().error(getLogModule(), getText("historicalPass.range.error"));
            addFieldError("totalHistoricalPasswords", getText("historicalPass.range.error"));
        }

        if (passwordPolicyConfigData.isAlphabetCheckReq() || passwordPolicyConfigData.isDigitCheckReq()|| passwordPolicyConfigData.isSpecialCharCheckReq()) {

            checkRequiredCharsLength(passwordPolicyConfigData);

            if (passwordPolicyConfigData.getProhibitedChars() != null && passwordPolicyConfigData.getProhibitedChars().length() > 50) {
                getLogger().error(getLogModule(), getText("prohibitedChar.error"));
                addFieldError("prohibitedChars", getText("prohibitedChar.error"));
            }

            if (passwordPolicyConfigData.getPasswordValidity() != null &&
                    (passwordPolicyConfigData.getPasswordValidity() < 0 || passwordPolicyConfigData.getPasswordValidity() > 999999999)) {
                getLogger().error(getLogModule(), getText("passValidity.invalid.error"));
                addFieldError("passwordValidity", getText("passValidity.invalid.error"));
            }
        }
    }

    private void checkRequiredCharsLength(PasswordPolicyConfigData passwordPolicyConfigData) {

        int totalAlphabets = 0;
        if (passwordPolicyConfigData.getAlphabetRange() != null) {
            totalAlphabets = passwordPolicyConfigData.getAlphabetRange();
            if (passwordPolicyConfigData.getAlphabetRange() < 0) {
                getLogger().error(getLogModule(), getText("passRange.int.error"));
                addFieldError("alphabetRange", getText("passRange.int.error"));
            }
            if (passwordPolicyConfigData.getAlphabetRange() > 50) {
                String message = getText("charrange.max.password.error",new String[]{getText("passwordpolicyconfig.alphabetRange"),"50"});
                getLogger().error(getLogModule(), message);
                addFieldError("alphabetRange", message);
            }
            if(passwordPolicyConfigData.getAlphabetRange() > passwordPolicyConfigData.getMaxPasswordLength()){
                String message = getText("alphaRange.minLen.error");
                getLogger().error(getLogModule(), message);
                addFieldError("alphabetRange", message);
            }
        }

        int totalDigits = 0;
        if (passwordPolicyConfigData.getDigitsRange() != null) {
            totalDigits = passwordPolicyConfigData.getDigitsRange();
            if (passwordPolicyConfigData.getDigitsRange() < 0 ){
                getLogger().error(getLogModule(), getText("digitRange.int.error"));
                addFieldError("digitsRange", getText("digitRange.int.error"));
            }
            if( passwordPolicyConfigData.getDigitsRange() > 50) {
                String message = getText("charrange.max.password.error",new String[]{getText("passwordpolicyconfig.digitsRange"),"50"});
                getLogger().error(getLogModule(), message);
                addFieldError("digitsRange", message);
            }
            if(passwordPolicyConfigData.getDigitsRange() > passwordPolicyConfigData.getMaxPasswordLength()){
                String message = getText("digitRange.minLen.error");
                getLogger().error(getLogModule(), message);
                addFieldError("digitsRange", message);
            }
        }

        int totalSpecialChars = 0;
        if (passwordPolicyConfigData.getSpecialCharRange() != null) {
            totalSpecialChars = passwordPolicyConfigData.getSpecialCharRange();
            if (passwordPolicyConfigData.getSpecialCharRange() < 0){
                getLogger().error(getLogModule(), getText("specialCharRange.invalid.error"));
                addFieldError("specialCharRange", getText("specialCharRange.int.error"));
            }
            if (passwordPolicyConfigData.getSpecialCharRange() > 50) {
                String message = getText("charrange.max.password.error",new String[]{getText("passwordpolicyconfig.specialCharRange"),"50"});
                getLogger().error(getLogModule(), getText("specialCharRange.invalid.error"));
                addFieldError("specialCharRange", message);
            }
            if(passwordPolicyConfigData.getSpecialCharRange() > passwordPolicyConfigData.getMaxPasswordLength()){
                String message = getText("specialCharRange.minLen.error");
                getLogger().error(getLogModule(), message);
                addFieldError("specialCharRange", message);
            }
        }

        int totalAll = totalAlphabets + totalDigits + totalSpecialChars;
        if (totalAll > passwordPolicyConfigData.getMinPasswordLength()) {
            getLogger().error(getLogModule(), getText("totalLength.error"));
            addFieldError("passwordRange", getText("totalLength.error"));
        }
    }

    private boolean checkPasswordRange(PasswordPolicyConfigData passwordPolicyConfigData) {
        boolean checkValid = true;
        if (passwordPolicyConfigData.isLengthCheckReq() == false) {
            getLogger().error(getLogModule(), getText("passRange.int.error"));
            addFieldError("passwordpolicyconfig.passwordRange", getText("passRange.int.error"));
            return false;
        }

        if (passwordPolicyConfigData.getPasswordRange() != null) {
            if (passwordPolicyConfigData.getMinPasswordLength() < 5) {
                getLogger().error(getLogModule(), getText("passRange.len.error"));
                addFieldError("passwordpolicyconfig.passwordRange", getText("passRange.len.error"));
                checkValid = false;
            }

            if (passwordPolicyConfigData.getMaxPasswordLength() > 50) {
                getLogger().error(getLogModule(), getText("passRange.minmax.error"));
                addFieldError("passwordpolicyconfig.passwordRange", getText("passRange.minmax.error"));
                checkValid = false;
            }

            if (passwordPolicyConfigData.getMinPasswordLength() > passwordPolicyConfigData.getMaxPasswordLength()) {
                getLogger().error(getLogModule(), getText("passRange.minmax.error"));
                addFieldError("passwordpolicyconfig.passwordRange", getText("passRange.minmax.error"));
                checkValid = false;
            }
        }
        return checkValid;
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.PASSWORDPOLICYCONFIG;
    }

    @Override
    public PasswordPolicyConfigData createModel() {
        return new PasswordPolicyConfigData();
    }

    public boolean isValidPasswordPolicy() {
        return validPasswordPolicy;
    }
}
