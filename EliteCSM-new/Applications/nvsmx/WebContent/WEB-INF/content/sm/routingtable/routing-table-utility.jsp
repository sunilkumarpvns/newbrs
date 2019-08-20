<%--
  Created by IntelliJ IDEA.
  User: jaidiptrivedi
  Date: 16/11/17
  Time: 12:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<script type="text/javascript">

    $(function () {
        setType();
        setAction();
    });

    function addGateway() {
        var i = $('#gatewayTable tbody tr').length;
        $("#gatewayTable tbody").append("<tr>" + $("#tempGatewayTable").find("tr").html() + "</tr>");
        var NAME = "name";
        $("#gatewayTable").find("tr:last td:nth-child(1)").find("select").attr(NAME, 'routingTableGatewayRelDataList[' + i + '].diameterGatewayData.id');
        $("#gatewayTable").find("tr:last td:nth-child(2)").find("input").attr(NAME, 'routingTableGatewayRelDataList[' + i + '].weightage');
    }

    function validateRoutingTableForm(mode, id) {
        var isValidName = verifyUniquenessOnSubmit('name', mode, id, 'com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableData', '', '');
        return isValidName && isValidMccMncGroup() &&  isValidGateways();
    }


    function validateGateway(currentElement) {
        clearAllErrors();
        if (isUniqueGateway($(currentElement).val()) == false) {
            setErrorOnElement($(currentElement), '<s:text name="routingtable.gateway.validation"/>');
        }
    }

    function clearAllErrors() {
        $("#generalError").text("");
        $("#generalError").removeAttr("class");
        clearErrorMessages();
    }

    function isValidGateways() {

        if ($("#action").val() == '<s:property value="@com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingAction@PROXY.displayValue"/>') {
            if ($('#gatewayTable tbody tr').length < 1) {
                $("#generalError").addClass("bg-danger");
                $("#generalError").text('<s:text name="routingtable.gateway.required"/>');
                return false;
            }

            var duplicateFlag = false;
            var invalidWeightage = false;
            var gatewayRequiredFlag = false;

            $("#gatewayTable tbody tr").each(function () {
                var getewayValueName = $(this).children().eq(0).find("select").val();
                console.log(getewayValueName);
                var gatewayValue = $("select");
                if(isNullOrEmpty(gatewayValue.val())){
                    $("#generalError").addClass("bg-danger");
                    $("#generalError").text('<s:text name="routingtable.gateway.configuration.required"/>');
                    gatewayRequiredFlag = true;
                    return false;

                }
                if (isUniqueGateway(gatewayValue.val()) == false) {
                    $("#generalError").addClass("bg-danger");
                    $("#generalError").text('<s:text name="routingtable.gateway.duplicate"/>');
                    duplicateFlag = true;
                    return false;
                }
                var weightage = $(this).children().eq(1).find("input");
                if (isNullOrEmpty(weightage.val())) {
                    setErrorOnElement(weightage, '<s:text name="routingtable.weightage.invalid"/>');
                    invalidWeightage = true;
                    return false;
                }
            });
        }
        if (duplicateFlag || invalidWeightage || gatewayRequiredFlag) {
            return false;
        }
        return true;
    }

    function isValidMccMncGroup() {
        var type = $("#type").val();
        if (type == '<s:property value="@com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType@MCC_MNC_BASED.value"/>') {
            if (isNullOrEmpty($("#mccmncgroup").val())) {
                setErrorOnElement($("#mccmncgroup"), "MCC-MNC Group must be configured");
                return false;
            }
            return true;
        }
    }


    function isUniqueGateway(value) {
        var count = 0;
        $("#gatewayTable tbody select").each(function () {
            if ($(this).val() == value) {
                count++;
            }
        });
        if (count > 1) {
            return false;
        }
        return true;
    }

    function setType() {
        var type = $("#type").val();
        if (type == '<s:property value="@com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType@MCC_MNC_BASED.value"/>') {
            $("#realmCondition").attr("disabled", "true");
            $("#mccmncgroup").removeAttr("disabled");
        } else {
            $("#mccmncgroup").attr("disabled", "true");
            $("#realmCondition").removeAttr("disabled");
        }
    }

    function setAction() {
        var action = $("#action").val();
        if (action == '<s:property value="@com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingAction@LOCAL.displayValue"/>') {
            $("#addGateway").attr("disabled", "true");
        } else {
            $("#addGateway").removeAttr("disabled");
        }
    }


</script>


<table id="tempGatewayTable" style="display:none">
    <tr>
        <td><s:select list="diameterGatewayList" listValue="name" listKey="id" cssClass="form-control"
                      elementCssClass="col-xs-12" onblur="validateGateway(this)"></s:select></td>
        <td><s:textfield cssClass="form-control" type="number" elementCssClass="col-xs-12 weightage" min="0"
                         max="10"/></td>

        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span
                class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>
