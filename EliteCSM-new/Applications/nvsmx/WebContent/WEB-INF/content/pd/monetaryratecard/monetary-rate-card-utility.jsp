<div style="display: none">
    <s:select name="revenueDetails" elementCssClass="col-xs-12" id="revenueDetails" cssClass="form-control"
              list="revenueDetails" listKey="id" listValue="name" headerKey="" headerValue="SELECT" />
</div>

<script type="text/javascript">
    var i = document.getElementsByName("monetaryRateCardDetailTableRow").length;
    if(isNullOrEmpty(i)) {
        i = 0;
    }

    function validateRateUnit() {
        var pulseUnitValue = $("#pulseUom").val();
        var rateUnitValue = $("#rateUom").val();
        if(isNullOrEmpty(rateUnitValue)){
            rateUnitValue = '${monetaryRateCardData.rateUnit}';
        }
        $("#rateUom").find('option').remove();
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/getRateUnitsUoms",
            async: true,
            dataType: "json",
            data: {
                "pulseUnit": pulseUnitValue,
            }, success: function (data) {
                setRateOption(data,rateUnitValue);
            }, error: function (data) {
                console.log(data);
            }
        });


    }

    function setRateOption(data,rateUnit){
        for (var i in data) {
            if (data[i] == rateUnit) {
                $("#rateUom").append(new Option(data[i], data[i], false, true));
            } else {
                $("#rateUom").append(new Option(data[i], data[i], false, false));
            }
        }

    }
    function updateLabelsOnBlur(labelKey, rateCardLabel){
        $("#monetaryRateCardDetail").find(labelKey).text($(rateCardLabel).val());
    }

    function setPcrfKeySuggestions() {
        commonAutoCompleteUsingCssClass('.pcrf-key-suggestions',<s:property value="@com.elitecore.nvsmx.system.util.NVSMXUtil@getPcrfKeySuggestions('request','')" escapeHtml="false" />);
    }

    function addRateCardVersionDetail(){
        var orderNumber = i+1;
        var revenueDetailId = "revenueDetail-"+i;
        var tableRow= "<tr name='monetaryRateCardDetailTableRow'>"+
            "<td>" +
            "<input type='hidden' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail["+i+"].orderNumber' value='"+orderNumber+"'/><input maxlength='100' class='form-control' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail["+i+"].label1'  type='text' id='defaultLabelKey1-'"+i+">" +
            "</td>"+
            "<td><input maxlength='100' class='form-control' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail["+i+"].label2'  type='text' id='defaultLabelKey1-'+i></td>";

            if (chargingType.isEventBased()) {
                tableRow += "<td><input maxlength='18' class='form-control' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[" + i + "].pulse1'  type='text' onkeypress='return isNaturalInteger(event);' id='defaultPulse-'" + i + " value='1' readonly='true'></td>" ;
            } else {
                tableRow += "<td><input maxlength='18' class='form-control' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[" + i + "].pulse1'  type='text' onkeypress='return isNaturalInteger(event);' id='defaultPulse-'" + i + "></td>" ;
            }

            tableRow += "<td><input maxlength='21' class='form-control' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail["+i+"].rate1'  type='text' id='defaultRate-'"+i+"></td>"+
            "<td><input maxlength='3' class='form-control col-xs-1' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail["+i+"].discount' type='number' min='0' max='100' id='defaultDiscount-"+i+"'/></td>"+
            "<td><select class='form-control col-xs-1 select2' name='monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail["+i+"].revenueDetail.id' type='number' min='0' max='100' id='"+revenueDetailId+"'/></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
        $(tableRow).appendTo('#monetaryRateCardDetailTable');
        $("#"+revenueDetailId).html($('#revenueDetails').html());
        setPcrfKeySuggestions();
        i++;
        enableSelect2();

    }

    function filterEmptyVersions() {
        var maxValue = '<s:property value="@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@LONG_MAX_VALUE" />';
        var versionTableBodyLength = $("#monetaryRateCardDetailTable tbody tr").length;
        var regExp = new RegExp("^((\\d{0,9}(\\.\\d{0,6})?))$");

        var isValidVersions = true;
        if(versionTableBodyLength < 1) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text('<s:text name="ratecard.version.error" />');
            isValidVersions = false;
        } else if(versionTableBodyLength >= 1) {
            $("#monetaryRateCardDetailTable tbody tr").each(function () {
                var pulse1Element = $(this).find("td:eq(2)").find('input');
                var rate1Element = $(this).find("td:eq(3)").find('input');
                var pulse1ElementValue = pulse1Element.val();
                var rate1ElementValue = rate1Element.val();

                if(chargingType.isSessionBased()) {
                    if (isNullOrEmpty(pulse1ElementValue)) {
                        setErrorOnElement(pulse1Element, "Pulse value can not be empty");
                        isValidVersions = false;
                    } else if (pulse1ElementValue <= 0 || pulse1ElementValue > maxValue) {
                        setErrorOnElement(pulse1Element, "Value must be between 1 to " + maxValue);
                        isValidVersions = false;
                    }
                }

                if (isNullOrEmpty(rate1ElementValue)) {
                    setErrorOnElement(rate1Element, "Rate value can not be empty");
                    isValidVersions = false;
                } else if(rate1ElementValue <0 ){
                    setErrorOnElement(rate1Element,"<s:text name="rate.card.varsion.should.zero.or.more"/>");
                    isValidVersions = false;
                } else if(regExp.test(rate1ElementValue) == false){
                    setErrorOnElement(rate1Element,"<s:text name="ratecard.version.double.length"/>");
                    isValidVersions = false;
                }

            });
        }
        if(isValidVersions == false) {
            return false;
        }else {
            return true
        }
    }

    var rateCardChargingType = function(isEventBasedRateCard) {
        var isEventBasedPackage = isEventBasedRateCard;
        this.setValue = function(isEventBasedRateCard) {
            isEventBasedPackage = isEventBasedRateCard;
        }
        this.isEventBased = function() {
            return isEventBasedPackage;
        }
        this.isSessionBased = function() {
            return isEventBasedPackage == false;
        }
    };

    var chargingType;

</script>
