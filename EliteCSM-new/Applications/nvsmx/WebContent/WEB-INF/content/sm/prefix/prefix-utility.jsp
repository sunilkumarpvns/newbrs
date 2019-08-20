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

    var networkRelationString = '<s:property value="%{networkRelations}" escapeHtml="false" />';
    var networkRelationJson = JSON.parse(networkRelationString);

    function getNetwork(){

        countryId = $( "#countryNames option:selected" ).val();
        operatorId = $( "#operatorNames option:selected" ).val();
        $('#networkNames').empty();
        $("#networkNames").append(new Option("-select-", ""));
        var operatorList = networkRelationJson[countryId];
        if(operatorList != undefined) {
            for (var i = 0; i < operatorList.length; i++) {

                var networkList = operatorList[i][operatorId];
                if(networkList != undefined) {
                    for (var j = 0; j < networkList.length; j++) {
                        var network = networkList[j];
                        $("#networkNames").append(new Option(network.name, network.id));
                    }
                }

            }
        }
        $('#networkNames').select2();

    }


</script>