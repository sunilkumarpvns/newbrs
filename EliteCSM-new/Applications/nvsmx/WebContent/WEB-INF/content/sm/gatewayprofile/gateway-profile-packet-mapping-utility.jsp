<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<style type="text/css">
    .min-width{
        min-width: 50%;
    }
</style>
<script type="text/javascript">

    var NAME = "name"
    var i = $("#gatewayToPCCMappingTable tbody tr").length;
    var j = $("#pccToGatewayMappingTable tbody tr").length;

    function addPCCToGatewayMapping(){
        j = parseInt(j);
        $("#pccToGatewayMappingTable tbody").append("<tr>" + $("#pccToGatewayMappingModalTable").find("tr").html() + "</tr>");
        var NAME = "name";
        $("#pccToGatewayMappingTable").find("tr:last td:nth-child(1)").find("select").attr(NAME, 'pccToGWPacketMappings[' + j + '].packetMappingId');
        $("#pccToGatewayMappingTable").find("tr:last td:nth-child(2)").find("input").attr(NAME, 'pccToGWPacketMappings[' + j + '].condition').attr('id','pccToGateway'+j);
        j++;
        setConditionalSuggestions('pccToGateway',${advanceConditions});
    }
    function addGatewayToPCCMapping(){
        i = parseInt(i);
        $("#gatewayToPCCMappingTable tbody").append("<tr>" + $("#gatewayToPCCMappingModalTable").find("tr").html() + "</tr>");
        var NAME = "name";
        $("#gatewayToPCCMappingTable").find("tr:last td:nth-child(1)").find("select").attr(NAME, 'gwToPCCPacketMappings[' + i + '].packetMappingId');
        $("#gatewayToPCCMappingTable").find("tr:last td:nth-child(2)").find("input").attr(NAME, 'gwToPCCPacketMappings[' + i + '].condition').attr('id','gatewayToPCC'+i);
        i++;
        setConditionalSuggestions('gatewayToPCC',${suggestionsGatewayToPCC});
   }

    function setConditionalSuggestions(className,suggestions) {
        $("."+className).focus(function () {
            $(this).autocomplete();
            var id = $(this).attr('id');
            var conditionsAutoCompleter = createModel(suggestions);
            expresssionAutoComplete(id, conditionsAutoCompleter);
        });
    }


   function validateMapping(){
       var gatewayToPCCMappingTableBodyLength = $("#gatewayToPCCMappingTable tbody tr").length;
       var isValidateNullEntry = true;
       if (gatewayToPCCMappingTableBodyLength >= 1) {
           //This will check that entry should not be null or empty
           $("#gatewayToPCCMappingTable tbody tr").each(function () {
               var selectElement = $(this).children().first().find('select');
               if (isNullOrEmpty(selectElement.val())) {
                   setErrorOnElement(selectElement, "Packet Mapping can not be empty");
                   isValidateNullEntry = false;
               }
           });
       }
       var pccToGatewayMappingTableBodyLength = $("#pccToGatewayMappingTable tbody tr").length;
       if (pccToGatewayMappingTableBodyLength >= 1) {
           //This will check that entry should not be null or empty
           $("#pccToGatewayMappingTable tbody tr").each(function () {
               var selectElement = $(this).children().first().find('select');
               if (isNullOrEmpty(selectElement.val())) {
                   setErrorOnElement(selectElement, "Packet Mapping can not be empty");
                   isValidateNullEntry = false;
               }
           });
       }
       return isValidateNullEntry;
    }


</script>


<table id="gatewayToPCCMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="gatewayToPCCPacketMappingDataList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>

<table id="pccToGatewayMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="pccToGatewayPacketMappingDataList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>
