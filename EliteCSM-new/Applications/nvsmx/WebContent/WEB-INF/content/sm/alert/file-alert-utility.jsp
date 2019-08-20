<%--
  Created by IntelliJ IDEA.
  User: aditya
  Date: 11/9/17
  Time: 1:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags/ec" %>

<script type="text/javascript" >



    function setRollingUnit() {

        var rollingType = $("#rollingType").val();
        console.log("Setting rolling unit as per configured rolling type: "+rollingType);

        if (rollingType == '<s:property value="@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value"/>') {
            setTimeBasedRollingUnit();
        } else {
            setSizeBasedRollingUnit();
        }

    }

    function setTimeBasedRollingUnit() {

        var timeBasedSelectTag = $("#tempTimeBaseRollingDiv").html();
        $("#rollingUnitDiv").html(timeBasedSelectTag);

        //  $("#maxRollingUnit").attr('disabled', 'true');
        //    $("#maxRolledUnitDiv").hide();
    }

    function setSizeBasedRollingUnit() {
        var sizeBasedInputTag = $("#tempSizeBaseRollingUnitDiv").html();
        $("#rollingUnitDiv").html(sizeBasedInputTag);
        //  $("#maxRolledUnitDiv").show();
        //  $("#maxRollingUnit").removeAttr("disabled");

    }


    function validateFileAlertListenerForm(mode, id){
        removeGeneralErrors();
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
        return verifyUniquenessOnSubmit('name', mode, id, 'com.elitecore.corenetvertex.sm.alerts.AlertListenerData', '', '');
    }
</script>