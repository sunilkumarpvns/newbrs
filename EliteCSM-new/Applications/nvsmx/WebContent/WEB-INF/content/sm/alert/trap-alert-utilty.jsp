<%--
  Created by IntelliJ IDEA.
  User: aditya
  Date: 11/9/17
  Time: 12:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<script type="text/javascript">
    //trap listener related function
    function disableSnmpRequestType(){
        var trapVersion = $("#trapVersion").val();

        if (trapVersion == '<s:property value="@com.elitecore.core.serverx.alert.TrapVersion@V1.name()"/>') {
            $("#snmpRequestType").attr("disabled", "true");
            hideTimeOutFields();
        } else {
            $("#snmpRequestType").removeAttr("disabled");
            setTimeOutAndRetryCountValue();
        }

    }

    function setTimeOutAndRetryCountValue(){
        var snmpRequestType = $("#snmpRequestType").val();
        if(snmpRequestType == '<s:property value="@com.elitecore.core.serverx.alert.SnmpRequestType@TRAP.name()" />'){
            hideTimeOutFields();
        }else{
            showTimeOutFields();
        }
    }

    function hideTimeOutFields(){
        //$("#informSnmpRequestTypeAttribute").hide();
        $("#timeOut").attr("disabled","true");
        $("#retryCount").attr("disabled","true");
    }

    function showTimeOutFields(){
        //$("#informSnmpRequestTypeAttribute").hide();
        $("#timeOut").removeAttr("disabled");
        $("#retryCount").removeAttr("disabled");
    }

    function validateTrapListenerForm(mode, id){
        removeGeneralErrors();
        var isValidName = verifyUniquenessOnSubmit('name', mode, id, 'com.elitecore.corenetvertex.sm.alerts.AlertListenerData', '', '');;
        return isValidName && validateAlert() && validateTimeOut() && validateRetryCount();
    }

    function validateAlert() {
        var alertNotConfigured = true;
        $("input[name$=type]").each(function(){
            if(this.checked){
                alertNotConfigured = false;
                return false;
            }
        });
        if(alertNotConfigured){
            addGeneralError('<s:text name="no.alert.configured"/>');
            return false;
        }
        return true;
    }

    function validateTimeOut(){
        if ($("#trapVersion").val() == 'V2c' && $("#snmpRequestType").val() == 'INFORM') {
            var timeOut = $('#timeOut').val();
            if (isNullOrEmpty(timeOut) == false && (timeOut < 0 || timeOut > 10)) {
                setError('timeOut',  '<s:text name="trap.alert.invalid.timeout"/>');
                return false;
            }
        }
        return true;
    }

    function validateRetryCount(){
        if ($("#trapVersion").val() == 'V2c' && $("#snmpRequestType").val() == 'INFORM') {
            var retryCount = $('#retryCount').val();
            if (isNullOrEmpty(retryCount)) {
                setError('retryCount', '<s:text name="error.valueRequired"/>');
                return false;
            }else if(retryCount < 0 || retryCount > 10){
                setError('retryCount', '<s:text name="trap.alert.invalid.retrycount"/>');
                return false;
            }
        }
        return true;
    }

</script>