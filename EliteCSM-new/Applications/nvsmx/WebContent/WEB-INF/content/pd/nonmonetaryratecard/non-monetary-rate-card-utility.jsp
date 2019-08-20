<script type="text/javascript">
    function validatePulseAndTime(){
        var timeVal = $("#nonmonetaryratecardTime").val();
        var pulseVal = $("#nonmonetaryratecardPulse").val();
        if(isNullOrEmpty(timeVal)==false){
            if(timeVal <=0 || timeVal >999999999999999999) {
                setErrorOnElement($("#nonmonetaryratecardTime"), "<s:text name="nonmonetary.ratecard.invalid.time.max"/>");
                return false;
            }
        }

        if(isNullOrEmpty(pulseVal)==false){
            if(pulseVal <=0 || pulseVal >999999999999999999) {
                setErrorOnElement($("#nonmonetaryratecardPulse"), "<s:text name="nonmonetary.ratecard.invalid.time.max"/>");
                return false;
            }
        } else {
            setErrorOnElement($("#nonmonetaryratecardPulse"), "<s:text name="error.valueRequired"/>");
            return false;
        }


        return true;
    }

    function validateEvent(){
        var isValidEvent = true;
        var eventElement = $("#nonmonetaryratecardEvent");
        if(isNullOrEmpty(eventElement.val())==false){
            if(isNaturalNumber(eventElement.val()) == false){
                setErrorOnElement(eventElement,"Value must be numeric");
                isValidEvent = false;
            }else if(eventElement.val() <= 0 || eventElement.val() >999999999999999999) {
                setErrorOnElement($("#nonmonetaryratecardEvent"), "<s:text name="nonmonetary.ratecard.invalid.event.max"/>");
                isValidEvent = false;
            }
        } else {
            setErrorOnElement(eventElement,"<s:text name="nonmonetary.ratecard.invalid.event"/>");
            isValidEvent = false;
        }

        return isValidEvent;
    }

    function validatePulseUnit() {
        var pulseUnitValue = '<s:property value="@com.elitecore.corenetvertex.constants.Uom@SECOND.name()"/>';
        $("#nonmonetaryratecardtimeUom").find('option').remove();
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/getRateUnitsUoms",
            async: true,
            dataType: "json",
            data: {
                "pulseUnit": pulseUnitValue,
            }, success: function (data) {
                var optionsAsString = "";
                for (var i in data) {

                    if(data[i] == '<s:property value="@com.elitecore.corenetvertex.constants.Uom@PERPULSE.name()"/>'
                    || data[i] == '<s:property value="@com.elitecore.corenetvertex.constants.Uom@PERPULSE.getValue()"/>'
                        || data[i] == '<s:property value="@com.elitecore.corenetvertex.constants.Uom@EVENT.name()"/>'){
                        continue;
                    }

                    if (data[i] == '${nonMonetaryRateCardData.timeUom}') {
                        //will be used in case of update
                        optionsAsString += "<option value='" + data[i] + "' selected='selected'>" + data[i] + "</option>";
                    } else {
                        optionsAsString += "<option value='" + data[i] + "'>" + data[i] + "</option>";
                    }

                }
                $("#nonmonetaryratecardtimeUom").append(optionsAsString);
            }, error: function (data) {
                console.log(data);
            }
        });
    }

    function checkForQuotaType(){
        var pulseUom = $("#nonmonetaryratecardPulseUom").val();

        if (pulseUom === '<s:property value="@com.elitecore.corenetvertex.constants.NonMonetaryRateCardType@TIME.name()"/>') {
            $("#nonmonetaryratecardtimeUom").removeAttr('disabled');
            $("#nonmonetaryratecardTime").removeAttr('disabled');
            $("#nonmonetaryratecardPulse").removeAttr('disabled');
            $("#nonmonetaryratecardEvent").attr('disabled', 'true');
        } else if (pulseUom === '<s:property value="@com.elitecore.corenetvertex.constants.NonMonetaryRateCardType@EVENT.name()"/>') {
            $("#nonmonetaryratecardtimeUom").attr('disabled', 'true');
            $("#nonmonetaryratecardTime").attr('disabled', 'true');
            $("#nonmonetaryratecardPulse").attr('disabled', 'true');
            $("#nonmonetaryratecardEvent").removeAttr('disabled');
        }
    }

    function validateUnits(){
        // checkForQuotaType();
        validatePulseUnit();
    }

    function checkForTillBillDate() {
        var renewalIntervalUnit = $("#renewalIntervalUnit").val();
        if (renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@TILL_BILL_DATE.name()"/>') {
            $("#renewalInterval").attr('disabled', 'true');
        } else {
            $("#renewalInterval").removeAttr('disabled');
        }
        checkForProration();
    }

    function checkForProration(){
        var renewalIntervalUnit = $("#renewalIntervalUnit").val();
        if (renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@TILL_BILL_DATE.name()"/>' ||
            renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@MONTH.name()"/>' ||
            renewalIntervalUnit === '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@MONTH_END.name()"/>') {
            $("#proration").removeAttr('disabled');
        }else {
            $("#proration").attr('disabled', 'true');
            $("#proration").val('<s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()}" />');

        }
    }
    function validateNonNegativeValue(){
        var renewalInterval = $("#renewalInterval").val();
        var renewalIntervalUnit = $("#renewalIntervalUnit").val();
        if(isNullOrEmpty(renewalInterval) == false && isNaturalNumber(renewalInterval) == false && renewalIntervalUnit != '<s:property value="@com.elitecore.corenetvertex.constants.RenewalIntervalUnit@TILL_BILL_DATE.name()"/>'){
            setErrorOnElement($("#renewalInterval"),'<s:text name="nonmonetary.ratecard.renewal.interval.positive" />');
            return false;
        }
        return true;
    }

</script>
