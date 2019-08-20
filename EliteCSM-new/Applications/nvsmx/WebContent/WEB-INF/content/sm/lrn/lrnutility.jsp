<script type="text/javascript">
    var operatorNetworkRelationString = '<s:property value="%{operatorNetworkRelations}" escapeHtml="false" />';
    var operatorNetworkRelationJson = JSON.parse(operatorNetworkRelationString);
    function getNetwork(){
        var operatorId= $('#operatorNames').val();
        $('#networkNames').empty();
        var networkList = operatorNetworkRelationJson[operatorId];
        for (var i = 0; i < networkList.length; i++) {
            var network = networkList[i];
            $("#networkNames").append(new Option(network.name , network.id));
        }
        $("#networkNames").select2();
    }
</script>