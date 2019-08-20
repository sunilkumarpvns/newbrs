<div style="display: none">
    <s:select name="revenueDetails" elementCssClass="col-xs-12" id="revenueDetails" cssClass="form-control"
              list="revenueDetails" listKey="id" listValue="name" headerKey="" headerValue="SELECT" />
</div>

<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 19/4/18
  Time: 4:38 PM
  To change this template use File | Settings | File Templates.
--%>

<script>
    $(function () {
        validateRateUnit();
        setPcrfKeySuggestions();
    });
    var i = document.getElementsByName("dataRateCardDetailTableRow").length;
    if(isNullOrEmpty(i)) {
        i = 0;
    }
    function addRateCardVersionDetail(){
        var revenueDetailId = "revenueDetail-"+i;
        var tableRow= "<tr name='dataRateCardDetailTableRow'>"+
            "<td><input maxlength='100' class='form-control' name='dataRateCardVersionDetailDataList["+i+"].labelKey1'  type='text' id='versionLabelKey1-'"+i+"></td>"+
            "<td><input maxlength='100' class='form-control' name='dataRateCardVersionDetailDataList["+i+"].labelKey2'  type='text' id='versionLabelKey2-'+i></td>"+
            "<td><input maxlength='18' class='form-control' name='dataRateCardVersionDetailDataList["+i+"].pulse1'  type='text' onkeypress='return isNaturalInteger(event);' id='pulse1-'"+i+"></td>"+
            "<td><input maxlength='21' class='form-control' name='dataRateCardVersionDetailDataList["+i+"].rate1'  type='text' id='rate1-'"+i+"></td>"+
            "<td><input class='form-control' name='dataRateCardVersionDetailDataList["+i+"].rateType'  type='text' readonly='true' value='FLAT' id='rateType-'"+i+"></td>"+
            "<td><select class='form-control col-xs-1 select2' name='dataRateCardVersionDetailDataList["+i+"].revenueDetail.id' type='number' min='0' max='100' id='"+revenueDetailId+"'/></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
        $(tableRow).appendTo('#dataRateCardDetailTable');
        $("#"+revenueDetailId).html($('#revenueDetails').html());
        setPcrfKeySuggestions();
        i++;

        enableSelect2();
    }

    function updateLabelsOnBlur(labelKey, rateCardLabel){
        $("#dataRateCardDetailTable").find(labelKey).text($(rateCardLabel).val());
    }

    function filterEmptyVersions() {
        var maxValue = '<s:property value="@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@LONG_MAX_VALUE" />';
        var versionTableBodyLength = $("#dataRateCardDetailTable tbody tr").length;
        var regExp = new RegExp("^((\\d{0,9}(\\.\\d{0,6})?))$");

        var isValidVersions = true;
        if(versionTableBodyLength < 1) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text('<s:text name="rate.card.versions.required" />');
            isValidVersions = false;
        } else if(versionTableBodyLength >= 1) {
            $("#dataRateCardDetailTable tbody tr").each(function () {
                var pulse1Element = $(this).find("td:eq(2)").find('input');
                var rate1Element = $(this).find("td:eq(3)").find('input');
                var pulse1ElementValue = pulse1Element.val();
                var rate1ElementValue = rate1Element.val();


                if (isNullOrEmpty(pulse1ElementValue)) {
                    setErrorOnElement(pulse1Element, "Pulse value can not be empty");
                    isValidVersions = false;
                } else if(pulse1ElementValue <= 0 || pulse1ElementValue > maxValue) {
                    setErrorOnElement(pulse1Element,"Value must be between 1 to " + maxValue);
                    isValidVersions = false;
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

    function validateRateUnit() {
        var pulseUnitValue = $("#rateCardPulseUnit").val();
        var rateUnit = $('#rateCardRateUnit').val();
        if(isNullOrEmpty(rateUnit)){
            rateUnit = '${rateUnit}';
        }
        $("#rateCardRateUnit").find('option').remove();
            $.ajax({
                type: "POST",
                url:"${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/getRateUnitsUoms",
                async:true,
                dataType : "json",
                data : {
                    "pulseUnit" : pulseUnitValue,
                }, success: function(data){
                    setRateOption(data,rateUnit);
                }, error: function(data){
                    console.log(data);
                }
            });

    }

    function setRateOption(data,rateUnit){
        for (var i in data) {
            if (data[i] == rateUnit) {
                $("#rateCardRateUnit").append(new Option(data[i], data[i], false, true));
            } else {
                $("#rateCardRateUnit").append(new Option(data[i], data[i], false, false));
            }
        }

    }

    function setPcrfKeySuggestions() {
        commonAutoCompleteUsingCssClass('.pcrf-key-suggestions',<s:property value="@com.elitecore.nvsmx.system.util.NVSMXUtil@getPcrfKeySuggestions('request','')" escapeHtml="false" />);
    }

</script>



