<%--
  Created by IntelliJ IDEA.
  User: dhyani
  Date: 3/10/17
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">
    var dbMap = new Map();
    dbMap = '<s:property value="dbNameDbUrlMap" escapeHtml="false" />';

    if(isNullOrEmpty(dbMap) == false){
        dbMap = JSON.parse(dbMap);
    }

    $('#databaseId').change(changeDS);

    function changeDS() {
        var element = $('#databaseId option:selected').text();
        $('#batchSize').prop('disabled',false);
        if(dbMap[element].indexOf("volt")!=-1)
        {
            $('#batchSize').prop('disabled',true);
        }
    }

    function getAlternateIdFieldSuggestions() {
        $("#alternateIdField").autocomplete();
        commonAutoComplete('alternateIdField',${alternateIdFieldSuggestions});
    }
    $(function(){
        $( ".select2" ).select2();
        changeDS();
    });
</script>
