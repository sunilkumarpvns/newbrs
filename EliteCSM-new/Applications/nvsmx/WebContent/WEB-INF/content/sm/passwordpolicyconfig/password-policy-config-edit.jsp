<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>

<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="passwordpolicyconfig.update"/>
        </h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/sm/passwordpolicyconfig" action="password-policy-config" id="passwordPolicyConfig" name="passwordPolicyConfigForm"
                method="post" validate="true" validator="validateForm();"
                cssClass="form-horizontal form-group-sm "
                labelCssClass="col-xs-4 col-sm-5 text-right"
                elementCssClass="col-xs-8 col-sm-7"
                theme="bootstrap" >

        <s:hidden name="_method" value="put"/>

            <div class="row">
                <div class="col-sm-9">
                        <s:set var="passwordRangeDesc">
                            <s:text name="passwordpolicyconfig.passwordRangeDesc"/>
                        </s:set>
                        <s:textfield id="passwordRange"
                                     name="passwordRange"
                                     key="passwordpolicyconfig.passwordRange"
                                     cssClass="form-control focusElement"
                                     maxlength="6"
                                     title="%{passwordRangeDesc}"
                                     helpText="%{passwordRangeDesc}"/>

                        <s:set var="alphabetRangeDesc">
                            <s:text name="passwordpolicyconfig.alphabetRangeDesc"/>
                        </s:set>
                        <s:textfield name="alphabetRange"
                                     id="alphabetRange"
                                     key="passwordpolicyconfig.alphabetRange"
                                     type="number"
                                     cssClass="form-control"
                                     maxlength="2"
                                     max="50"
                                     step="1"
                                     title="%{alphabetRangeDesc}"
                                     helpText="%{alphabetRangeDesc}" />

                        <s:set var="digitsRangeDesc">
                            <s:text name="passwordpolicyconfig.digitsRangeDesc"/>
                        </s:set>
                        <s:textfield name="digitsRange"
                                     id="digitsRange"
                                     key="passwordpolicyconfig.digitsRange"
                                     type="number"
                                     cssClass="form-control"
                                     maxlength="2"
                                     max="50"
                                     step="1"
                                     title="%{digitsRangeDesc}"
                                     helpText="%{digitsRangeDesc}" />

                        <s:set var="specialCharRangeDesc">
                            <s:text name="passwordpolicyconfig.specialCharRangeDesc"/>
                        </s:set>
                        <s:textfield name="specialCharRange"
                                     id="specialCharRange"
                                     key="passwordpolicyconfig.specialCharRange"
                                     type="number"
                                     cssClass="form-control"
                                     maxlength="2"
                                     max="50"
                                     step="1"
                                     title="%{specialCharRangeDesc}"
                                     helpText="%{specialCharRangeDesc}" />

                        <s:set var="prohibitedCharsDesc">
                            <s:text name="passwordpolicyconfig.prohibitedCharsDesc"/>
                        </s:set>
                        <s:textfield name="prohibitedChars"
                                     id="prohibitedChars"
                                     key="passwordpolicyconfig.prohibitedChars"
                                     cssClass="form-control"
                                     maxlength="50"
                                     title="%{prohibitedCharsDesc}"
                                     helpText="%{prohibitedCharsDesc}" />

                        <s:set var="passwordValidityDesc">
                            <s:text name="passwordpolicyconfig.passwordValidityDesc"/>
                        </s:set>
                        <s:textfield name="passwordValidity"
                                     id="passwordValidity"
                                     key="passwordpolicyconfig.passwordValidity"
                                     type="number"
                                     cssClass="form-control"
                                     maxlength="9" min="0" max="999999999"
                                     title="%{passwordValidityDesc}"
                                     helpText="%{passwordValidityDesc}" />

                        <s:set var="changePwdOnFirstLoginDesc">
                            <s:text name="passwordpolicyconfig.changePwdOnFirstLoginDesc"/>
                        </s:set>
                        <s:select name="changePwdOnFirstLogin" cssClass="form-control"
                                  key="passwordpolicyconfig.changePwdOnFirstLogin"
                                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                  listKey="isBooleanValue()" listValue="getStringNameBoolean()"
                                  id="changePwdOnFirstLoginId"
                                  title="%{changePwdOnFirstLoginDesc}"
                                  helpText="%{changePwdOnFirstLoginDesc}" />

                        <s:set var="totalHistoricalPasswordsDesc">
                            <s:text name="passwordpolicyconfig.totalHistoricalPasswordsDesc"/>
                        </s:set>
                        <s:textfield name="totalHistoricalPasswords"
                                     id="totalHistoricalPasswords"
                                     key="passwordpolicyconfig.totalHistoricalPasswords"
                                     type="number"
                                     cssClass="form-control"
                                     maxlength="3"
                                     title="%{totalHistoricalPasswordsDesc}"
                                     helpText="%{totalHistoricalPasswordsDesc}" />
                </div>

        </div>


        <div class="row">
            <div class="col-xs-12" align="center">
                <button class="btn btn-sm btn-primary"
                        type="submit"
                        role="button"
                        formaction="${pageContext.request.contextPath}/sm/passwordpolicyconfig/password-policy-config/${id}"
                        onclick="return validateForm()">
                    <span class="glyphicon glyphicon-floppy-disk"></span>
                    <s:text name="button.save"/>
                </button>

                <button type="button" class="btn btn-sm btn-primary" role="button"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/passwordpolicyconfig/password-policy-config/*'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span>
                    <s:text name="button.back"/>
                </button>
            </div>
        </div>
    </div>
 </s:form>

</div>

<script type="text/javascript">
    function  validateForm() {
        clearErrorMessages();
        var checkPasswordRange = $('#passwordRange').val();
        var checkAlphaRange = $('#alphabetRange').val();
        var checkDigitRange = $('#digitsRange').val();
        var checkSpecialCharRange = $('#specialCharRange').val();
        var checkProhibitedChar = $('#prohibitedChars').val();
        var checkPasswordValidity = $('#passwordValidity').val();
        var checkTotalHistoricalPasswords = $('#totalHistoricalPasswords').val();

        if (isNullOrEmpty(checkPasswordRange)) {
            setError('passwordRange', '<s:text name="error.required.field"><s:param><s:text name="passwordpolicyconfig.passwordRange"/></s:param></s:text>');
            return false;
        }

        var totallength = 0, maxLength = 0;
        if (isNullOrEmpty(checkPasswordRange) && isNullOrEmpty(checkAlphaRange) && isNullOrEmpty(checkDigitRange) && isNullOrEmpty(checkSpecialCharRange) && isNullOrEmpty(checkProhibitedChar) && isNullOrEmpty(checkPasswordValidity)) {
            setError('passwordRange', '<s:text name="passPolicy.error"/>');
            setError('alphabetRange', '');
            setError('digitsRange', '');
            setError('specialCharRange', '');
            setError('prohibitedChars', '');
            setError('passwordValidity', '');
            return true;
        } else {

            if (checkPasswordRange != '') {
                if (!(checkPasswordRange.indexOf("-") != -1)) {
                    setError('passwordRange', '<s:text name="minmax.error"/>');
                    return false;
                } else {
                    if (checkPasswordRange.lastIndexOf('-') == checkPasswordRange.length - 1) {
                        setError('passwordRange', '<s:text name="minmax.error"/>');
                        return false;
                    }

                    if (checkPasswordRange.lastIndexOf('-') != checkPasswordRange.indexOf('-')) {
                        setError('passwordRange', '<s:text name="minmax.error"/>');
                        return false;
                    }

                    var pwdRangefields = checkPasswordRange.split(/-/);
                    var minPwdRange = pwdRangefields[0];
                    var maxPwdRange = pwdRangefields[1];

                    if(isNullOrEmpty(minPwdRange) || isNullOrEmpty(maxPwdRange)){
                        setError('passwordRange', '<s:text name="minmax.error"/>');
                        return false;
                    }

                    if (isValidMinMaxRange(minPwdRange, maxPwdRange) == false) {
                        return false;
                    }

                }
                maxLength = parseInt(minPwdRange);
            }

            if (isNullOrEmpty(checkAlphaRange) == false) {
                if (isValidAlphabetRange(checkAlphaRange, maxPwdRange) == false) {
                    return false;
                }
                totallength = parseInt(totallength) + parseInt(checkAlphaRange);
            }

            if (isNullOrEmpty(checkDigitRange) == false) {
                if (isValidDigitRange(checkDigitRange, maxPwdRange) == false) {
                    return false;
                }
                totallength = parseInt(totallength) + parseInt(checkDigitRange);
            }

            if (isNullOrEmpty(checkSpecialCharRange) == false) {
                if (isValidSpecialCharactersRange(checkSpecialCharRange, maxPwdRange) == false) {
                    return false;
                }
                totallength = parseInt(totallength) + parseInt(checkSpecialCharRange);
            }

            if (isNullOrEmpty(checkPasswordRange) == false) {
                if (totallength > maxLength) {
                    setError('alphabetRange', '');
                    setError('digitsRange', '');
                    setError('specialCharRange', '');
                    setError('passwordRange', '<s:text name="totalLength.error"/>');
                    return false;
                }
            }

            if (isValidProhibitedCharacters(checkProhibitedChar) == false) {
                return false;
            }

            if (isValidPasswordValidity(checkPasswordValidity) == false) {
                return false;
            }

            if (isValidTotalHistoricalPassword(checkTotalHistoricalPasswords) == false) {
                return false;
            }


            return true;
        }
    }

    function isValidMinMaxRange(minPwdRange, maxPwdRange) {
        if (isNaN(minPwdRange) || isNaN(maxPwdRange)) {
            setError('passwordRange', '<s:text name="passRange.int.error"/>');
            return false;
        }

        if (isNumberGreaterThanZero(minPwdRange) == false || isNumberGreaterThanZero(maxPwdRange) == false) {
            setError('passwordRange', '<s:text name="passRange.int.error"/>');
            return false;
        }
        minPwdRange = parseInt(minPwdRange);
        maxPwdRange = parseInt(maxPwdRange);
        if (minPwdRange < 5) {
            setError('passwordRange', '<s:text name="passRange.len.error"/>');
            return false;
        }
        if (minPwdRange > maxPwdRange) {
            setError('passwordRange', '<s:text name="passRange.minLen.error"/>');
            return false;
        }
        if (minPwdRange > 50 || maxPwdRange > 50) {
            setError('passwordRange', '<s:text name="passRange.minmax.error"/>');
            return false;
        }
        return true;
    }


    function isValidAlphabetRange(checkAlphaRange, maxPwdRange) {

        if(isNaN(checkAlphaRange)){
            setError('alphabetRange', '<s:text name="alphaRange.int.error"/>');
            return false;
        }
        checkAlphaRange=parseInt(checkAlphaRange);
        if (checkAlphaRange < 0) {
            setError('alphabetRange', '<s:text name="alphaRange.int.error"/>');
            return false;
        }
        if (checkAlphaRange > 50) {
            setError('alphabetRange', '<s:text name="alphaRange.maxLen.error"/>');
            return false;
        }
        if (checkAlphaRange > parseInt(maxPwdRange)) {
            setError('alphabetRange', '<s:text name="alphaRange.minLen.error"/>');
            return false;
        }
        return true;
    }

    function isValidDigitRange(checkDigitRange, maxPwdRange) {
        if (isNaN(checkDigitRange)) {
            setError('digitsRange', '<s:text name="digitRange.int.error"/>');
            return false;
        }
        checkDigitRange = parseInt(checkDigitRange);

        if (checkDigitRange < 0) {
            setError('digitsRange', '<s:text name="digitRange.int.error"/>');
            return false;
        }
        if (checkDigitRange > 50) {
            setError('digitsRange', '<s:text name="digitRange.maxLen.error"/>');
            return false;
        }
        if (checkDigitRange > parseInt(maxPwdRange)) {
            setError('digitsRange', '<s:text name="digitRange.minLen.error"/>');
            return false;
        }
        return true;
    }

    function isValidSpecialCharactersRange(checkSpecialCharRange, maxPwdRange) {
        if(isNaN(checkSpecialCharRange)){
            setError('specialCharRange', '<s:text name="specialCharRange.int.error"/>');
            return false;
        }
        checkSpecialCharRange = parseInt(checkSpecialCharRange);
        if (checkSpecialCharRange < 0) {
            setError('specialCharRange', '<s:text name="specialCharRange.int.error"/>');
            return false;
        }
        if (checkSpecialCharRange > 50) {
            setError('specialCharRange', '<s:text name="specialCharRange.maxLen.error"/>');
            return false;
        }
        if (checkSpecialCharRange > parseInt(maxPwdRange)) {
            setError('specialCharRange', '<s:text name="specialCharRange.minLen.error"/>');
            return false;
        }
        return true;
    }

    function isValidProhibitedCharacters(checkProhibitedChar) {
        if (checkProhibitedChar.length > 50) {
            setError('prohibitedChars', '<s:text name="prohibitedChar.error"/>');
            return false;
        }

        return true;
    }


    function isValidPasswordValidity(checkPasswordValidity) {
        if (isNullOrEmpty(checkPasswordValidity)){
            setError('passwordValidity', '<s:text name="error.required.field"><s:param><s:text name="passwordpolicyconfig.passwordValidity"/></s:param></s:text>');
            return false;
        }
        if(isNaN(checkPasswordValidity)){
            setError('passwordValidity', '<s:text name="passValidity.error"/>');
            return false;
        }

        if(checkPasswordValidity < 0) {
            setError('passwordValidity', '<s:text name="passValidity.error"/>');
            return false;
        }

        if (checkPasswordValidity < 0) {
            setError('passwordValidity', '<s:text name="passValidity.error"/>');
            return false;
        }

        if (checkPasswordValidity > 999999999) {
            setError('passwordValidity', '<s:text name="passValidity.max.error"/>');
            return false;
        }
        return true;
    }

    function isValidTotalHistoricalPassword(checkTotalHistoricalPasswords) {
        if (isNullOrEmpty(checkTotalHistoricalPasswords) || isNaN(checkTotalHistoricalPasswords)) {
            setError('totalHistoricalPasswords', '<s:text name="historicalPass.int.error"/>');
            return false;
        }

        if (checkTotalHistoricalPasswords < 1 || checkTotalHistoricalPasswords > 10) {
            setError('totalHistoricalPasswords', '<s:text name="historicalPass.range.error"/>');
            return false;
        }

        return true;
    }

</script>