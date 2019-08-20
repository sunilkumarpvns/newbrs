<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">
    var NAME = "name"
    var pccMappingIndex = $("#pccRuleMappingTable tbody tr").length;
    function addPCCRuleMapping(gwProfilePCCRuleMapping) {
        var mappingList = gwProfilePCCRuleMapping+"["+pccMappingIndex+"]";
        var id = "condition" + pccMappingIndex;
        $("#pccRuleMappingTable tbody").append("<tr>" + $("#pccRuleMappingModelTable").find("tr").html() + "</tr>");
        $("#pccRuleMappingTable").find("tr:last td:nth-child(1)").find("select").attr(NAME, mappingList + '.pccRuleMappingId');
        $("#pccRuleMappingTable").find("tr:last td:nth-child(2)").find("input").attr(NAME, mappingList + '.condition').attr("id",id);
        setConditionSuggestionsForPCCRule();
        pccMappingIndex++;
    }

    function validatePccMappings(){
        var pccMappings = $("#pccRuleMappingTable tbody tr").length;
        var isValidateNullEntry = true;
        if (pccMappings >= 1) {
            //This will check that entry should not be null or empty
            $("#pccRuleMappingTable tbody tr").each(function () {
                var selectElement = $(this).children().first().find('select');
                if (isNullOrEmpty(selectElement.val())) {
                    setErrorOnElement(selectElement, "PCCRule Mappings can not be empty");
                    isValidateNullEntry = false;
                }
            });
        }
        return isValidateNullEntry;
    }

    function setConditionSuggestionsForPCCRule() {
        $(".condition").focus(function () {
            $(this).autocomplete();
            var id = $(this).attr('id');
            var conditionsAutoCompleter = createModel(${advanceConditions});
            expresssionAutoComplete(id, conditionsAutoCompleter);
        });
    }
</script>

<table id="pccRuleMappingModelTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="pccRuleMappings" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <!--todo autosuggested values -->
            <s:textfield  cssClass="form-control condition" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>
