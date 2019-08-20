<%--
  User: jaidiptrivedi
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<script type="text/javascript">

    $(function(){
        $(".select2").select2();
    });

    function addServiceDataFlow() {
        var i = $('#defaultServiceDataFlowTable tbody tr').length;
        $("#defaultServiceDataFlowTable tbody").append("<tr>" + $("#tempSDFTable").find("tr").html() + "</tr>");
        var NAME = "name";
        $("#defaultServiceDataFlowTable").find("tr:last td:nth-child(1)").find("select").attr(NAME, 'defaultServiceDataFlows[' + i + '].flowAccess');
        $("#defaultServiceDataFlowTable").find("tr:last td:nth-child(2)").find("select").attr(NAME, 'defaultServiceDataFlows[' + i + '].protocol');
        $("#defaultServiceDataFlowTable").find("tr:last td:nth-child(3)").find("input").attr(NAME, 'defaultServiceDataFlows[' + i + '].sourceIP');
        $("#defaultServiceDataFlowTable").find("tr:last td:nth-child(4)").find("input").attr(NAME, 'defaultServiceDataFlows[' + i + '].sourcePort');
        $("#defaultServiceDataFlowTable").find("tr:last td:nth-child(5)").find("input").attr(NAME, 'defaultServiceDataFlows[' + i + '].destinationIP');
        $("#defaultServiceDataFlowTable").find("tr:last td:nth-child(6)").find("input").attr(NAME, 'defaultServiceDataFlows[' + i + '].destinationPort');


    }

    function validateForm(mode, id) {
        return verifyUniquenessOnSubmit('serviceTypeName', mode, id, 'com.elitecore.corenetvertex.pd.dataservicetype.DataServiceTypeData', '', '') && verifyUniquenessOnSubmit('serviceIdentifier', mode, id, 'com.elitecore.corenetvertex.pd.dataservicetype.DataServiceTypeData', '', 'serviceIdentifier');
    }

    function clearAllErrors() {
        $("#generalError").text("");
        $("#generalError").removeAttr("class");
        clearErrorMessages();
    }

</script>

<table id="tempSDFTable" style="display:none">

    <tr>
        <td>
            <s:select name="status" cssClass="form-control" elementCssClass="col-xs-12"
                      list="@com.elitecore.corenetvertex.pd.dataservicetype.SDFFlowAccess@values()" id="flowAccess"
                      tabindex="6" listKey="val" listValue="displayValue"/>
        </td>
        <td>
            <s:select name="status" cssClass="form-control"
                      list="@com.elitecore.corenetvertex.pd.dataservicetype.SDFProtocols@values()"
                      id="flowAccess" elementCssClass="col-xs-12"
                      tabindex="7" listKey="val" listValue="displayValue"/>
        </td>
        <td><input tabindex="8" class="form-control" type="text"/></td>
        <td><input tabindex="9" class="form-control" type="text"/></td>
        <td><input tabindex="10" class="form-control" type="text"/></td>
        <td><input tabindex="11" class="form-control" type="text"/></td>
        <td><span tabindex="12" class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span
                class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
    </tr>

</table>
