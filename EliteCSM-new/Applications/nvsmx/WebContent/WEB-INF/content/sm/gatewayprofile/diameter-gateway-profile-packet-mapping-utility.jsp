<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<style type="text/css">
    .min-width{
        min-width: 50%;
    }
</style>
<script type="text/javascript">
    var NAME = "name"
    var gwToPCCGx = $("#gatewayToPccGxMappingTable tbody tr").length;
    var pccToGwGx = $("#pccToGatewayGxMappingTable tbody tr").length;

    var gwToPCCGy = $("#gatewayToPccGyMappingTable tbody tr").length;
    var pccToGwGy = $("#pccToGatewayGyMappingTable tbody tr").length;

    var gwToPCCRx = $("#gatewayToPccRxMappingTable tbody tr").length;
    var pccToGwRx = $("#pccToGatewayRxMappingTable tbody tr").length;
    function addPCCToGatewayMapping(tableId, applicationType){

        var referingTable = null;
        var mappingList = null;
        var id;
        if(applicationType == '<s:text name="@com.elitecore.corenetvertex.constants.PacketApplication@GX.name()"/>'){
            referingTable = "gxPCCToGatewayMappingModalTable";
            mappingList = "pccToGWGxPacketMappings[" + pccToGwGx + "]";
            id= "pccToGateway"+applicationType+pccToGwGx;
            pccToGwGx++;
        }else if(applicationType == '<s:text name="@com.elitecore.corenetvertex.constants.PacketApplication@GY.name()"/>'){
            referingTable = "gyPCCToGatewayMappingModalTable";
            mappingList = "pccToGWGyPacketMappings[" + pccToGwGy + "]";
            id= "pccToGateway"+applicationType+pccToGwGy;
            pccToGwGy++;
        }else if(applicationType == '<s:text name="@com.elitecore.corenetvertex.constants.PacketApplication@RX.name()"/>'){
            referingTable = "rxPCCToGatewayMappingModalTable";
            mappingList = "pccToGWRxPacketMappings[" + pccToGwRx + "]";
            id= "pccToGateway"+applicationType+pccToGwRx;
            pccToGwRx++;
        }
        $("#" +tableId+" tbody").append("<tr>" + $("#"+referingTable).find("tr").html() + "</tr>");
        var NAME = "name";
        $("#"+tableId).find("tr:last td:nth-child(1)").find("select").attr(NAME, mappingList+'.packetMappingId');
        $("#"+tableId).find("tr:last td:nth-child(2)").find("input").attr(NAME, mappingList+'.condition').attr('id',id);
        $("#"+tableId).find("tr:last td:nth-child(3)").find("input").attr(NAME, mappingList+'.applicationType').attr("value", applicationType);
        setConditionalSuggestions('pccToGateway',${advanceConditions});
    }
    function addGatewayToPCCGatewayMapping(tableId, applicationType){

        var referingTable = null;
        var mappingList = null;
        var id;
        if(applicationType == '<s:text name="@com.elitecore.corenetvertex.constants.PacketApplication@GX.name()"/>'){
            referingTable = "gxGatewayToPCCMappingModalTable";
            mappingList = "gwToPccGxPacketMappings[" + gwToPCCGx + "]";
            id= "gatewayToPCC"+applicationType+gwToPCCGx;
            gwToPCCGx++;
        }else if(applicationType == '<s:text name="@com.elitecore.corenetvertex.constants.PacketApplication@GY.name()"/>'){
            referingTable = "gyGatewayToPCCMappingModalTable";
            mappingList = "gwToPccGyPacketMappings[" + gwToPCCGy + "]";
            id= "gatewayToPCC"+applicationType+gwToPCCGy;
            gwToPCCGy++;
        }else if(applicationType == '<s:text name="@com.elitecore.corenetvertex.constants.PacketApplication@RX.name()"/>'){
            referingTable = "rxGatewayToPCCMappingModalTable";
            mappingList = "gwToPccRxPacketMappings[" + gwToPCCRx + "]";
            id= "gatewayToPCC"+applicationType+gwToPCCRx;
            gwToPCCRx++;
        }
        $("#" +tableId+" tbody").append("<tr>" + $("#"+referingTable).find("tr").html() + "</tr>");
        var NAME = "name";
        $("#"+tableId).find("tr:last td:nth-child(1)").find("select").attr(NAME, mappingList+'.packetMappingId');
        $("#"+tableId).find("tr:last td:nth-child(2)").find("input").attr(NAME, mappingList+'.condition').attr('id',id);
        $("#"+tableId).find("tr:last td:nth-child(3)").find("input").attr(NAME, mappingList+'.applicationType').attr("value", applicationType);
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
       var gatewayToPccGxMappings = $("#gatewayToPccGxMappingTable tbody tr").length;
       var isValidateNullEntry = true;
       if (gatewayToPccGxMappings >= 1) {
           //This will check that entry should not be null or empty
           $("#gatewayToPccGxMappingTable tbody tr").each(function () {
               var selectElement = $(this).children().first().find('select');
               if (isNullOrEmpty(selectElement.val())) {
                   setErrorOnElement(selectElement, "Packet Mapping can not be empty");
                   isValidateNullEntry = false;
               }
           });
       }
       var pccToGatewayGxMappings = $("#pccToGatewayGxMappingTable tbody tr").length;
       if (pccToGatewayGxMappings >= 1) {
           $("#pccToGatewayGxMappingTable tbody tr").each(function () {
               var selectElement = $(this).children().first().find('select');
               if (isNullOrEmpty(selectElement.val())) {
                   setErrorOnElement(selectElement, "Packet Mapping can not be empty");
                   isValidateNullEntry = false;
               }
           });
       }

       var gatewayToPccGyMappings = $("#gatewayToPccGyMappingTable tbody tr").length;
       if (gatewayToPccGyMappings >= 1) {
           //This will check that entry should not be null or empty
           $("#gatewayToPccGyMappingTable tbody tr").each(function () {
               var selectElement = $(this).children().first().find('select');
               if (isNullOrEmpty(selectElement.val())) {
                   setErrorOnElement(selectElement, "Packet Mapping can not be empty");
                   isValidateNullEntry = false;
               }
           });
       }
       var pccToGatewayGyMappings = $("#pccToGatewayGyMappingTable tbody tr").length;
       if (pccToGatewayGyMappings >= 1) {
           $("#pccToGatewayGyMappingTable tbody tr").each(function () {
               var selectElement = $(this).children().first().find('select');
               if (isNullOrEmpty(selectElement.val())) {
                   setErrorOnElement(selectElement, "Packet Mapping can not be empty");
                   isValidateNullEntry = false;
               }
           });
       }

       var gatewayToPccRxMappings = $("#gatewayToPccRxMappingTable tbody tr").length;
       if (gatewayToPccRxMappings >= 1) {
           //This will check that entry should not be null or empty
           $("#gatewayToPccRxMappingTable tbody tr").each(function () {
               var selectElement = $(this).children().first().find('select');
               if (isNullOrEmpty(selectElement.val())) {
                   setErrorOnElement(selectElement, "Packet Mapping can not be empty");
                   isValidateNullEntry = false;
               }
           });
       }
       var pccToGatewayRxMappings = $("#pccToGatewayRxMappingTable tbody tr").length;
       if (pccToGatewayRxMappings >= 1) {
           $("#pccToGatewayRxMappingTable tbody tr").each(function () {
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


<table id="gxGatewayToPCCMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="gatewayToPCCPacketMappingGxList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td  style="display:none;">
            <s:hidden id="hiddenVal" />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>

<table id="gxPCCToGatewayMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="pccToGatewayPacketMappingGxList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td style="display:none;">
            <s:hidden id="hiddenVal" />
        </td>

        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>

<!-- Gy Mapping Model -->

<table id="gyGatewayToPCCMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="gatewayToPCCPacketMappingGyList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td  style="display:none;">
            <s:hidden id="hiddenVal" />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>


<table id="gyPCCToGatewayMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="pccToGatewayPacketMappingGyList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td style="display:none;">
            <s:hidden id="hiddenVal" />
        </td>

        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>
<!-- Rx Mapping Model -->

<table id="rxGatewayToPCCMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="gatewayToPCCPacketMappingRxList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td  style="display:none;">
            <s:hidden id="hiddenVal" />
        </td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>


<table id="rxPCCToGatewayMappingModalTable" class="table table-blue table-bordered" style="display: none;">
    <tr>
        <td><s:select list="pccToGatewayPacketMappingRxList" listKey="id" listValue="name" cssClass="form-control"
                      elementCssClass="col-xs-12"/></td>
        <td>
            <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"  />
        </td>
        <td style="display:none;">
            <s:hidden id="hiddenVal" />
        </td>

        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
        </span>
        </td>
    </tr>
</table>
