<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">
    var NAME = "name"
    var serviceGuidingIndex = $("#serviceGuidingTable tbody tr").length;
    function addServiceGuiding() {
        var serviceGuiding = "serviceGuidingDatas["+serviceGuidingIndex+"]";
        var id = "condition" + serviceGuidingIndex;
        $("#serviceGuidingTable tbody").append("<tr>" + $("#serviceGuidingConfigTable").find("tr").html() + "</tr>");
        $("#serviceGuidingTable").find("tr:last td:nth-child(1)").find("select").attr(NAME, serviceGuiding + '.serviceId');
        $("#serviceGuidingTable").find("tr:last td:nth-child(2)").find("input").attr(NAME, serviceGuiding + '.condition').attr("id",id);
        setConditionSuggestionsForService();
        serviceGuidingIndex++;
    }

    function validateServiceGuidingDatas(){
        var serviceGuidings = $("#serviceGuidingTable tbody tr").length;
        var isValidateNullEntry = true;
        var serviceArray = new Array();
        if (serviceGuidings >= 1) {
            //This will check that entry should not be null or empty
            $("#serviceGuidingTable tbody tr").each(function () {
                var selectElement = $(this).children().first().find('select');
                if (isNullOrEmpty(selectElement.val())) {
                    setErrorOnElement(selectElement, "Service can not be empty");
                    isValidateNullEntry = false;
                }
                var serviceList = JSON.parse('${serviceDataJson}');
                for(var i=0;i<serviceList.length;i++){
                    var serviceStatus = serviceList[i]["Status"];
                    var serviceId = serviceList[i]["id"];
                    if(serviceStatus == '<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@STATUS_INACTIVE" />' && serviceId == selectElement.val() && selectElement.hasClass('inActiveService') == false){
                        setErrorOnElement(selectElement, "InActive Service can not be configured");
                        isValidateNullEntry = false;
                    }
                }

                if (serviceArray.indexOf(selectElement.val()) == -1) {
                    serviceArray.push(selectElement.val());
                } else {
                    setErrorOnElement(selectElement, "Service already exist");
                    isValidateNullEntry = false;
                }

            });
        }
        if(serviceGuidings > 10){
            $("#generalError").addClass("bg-danger");
            $("#generalError").text("<s:text name="profile.service.guiding.max.limit.reach" />");
            isValidateNullEntry = false;
        }
        return isValidateNullEntry;
    }



    function setConditionSuggestionsForService() {
        $(".condition").focus(function () {
            $(this).autocomplete();
            var id = $(this).attr('id');
            var conditionsAutoCompleter = createModel(${advanceConditions});
            expresssionAutoComplete(id, conditionsAutoCompleter);
        });
    }
    function removeCss(obj){
        $(obj).removeClass('inActiveService');
    }
</script>

<table id="serviceGuidingConfigTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="serviceDatas" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control condition" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>
