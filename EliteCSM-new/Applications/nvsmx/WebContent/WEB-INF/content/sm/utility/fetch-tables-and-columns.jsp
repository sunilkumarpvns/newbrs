<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 27/10/17
  Time: 6:43 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">

    function getSelectedTableColumnNames(obj) {
        var databaseId = $("#databaseId").val();
        var tableName = $("#tableName").val();
        if(isNullOrEmpty(tableName)) {
            return;
        }
        $.ajax({
            type: "POST",
            url:"${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/fetchColumnNamesByTableName",
            async:true,
            dataType : "json",
            data : {
                "databaseId" : databaseId,
                "tableName" : tableName,
            }, success: function(data){
                $(obj).autocomplete();
                var id= $(obj).attr('id');
                commonAutoComplete(id,data);
            }, error: function(data){
                console.log(data);
            }
        });

    }

    function getSelectedDatabaseTableNames (obj) {
        var databaseId = $("#databaseId").val();

        $.ajax({
            type: "POST",
            url:"${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/fetchTableNamesByDatabaseId",
            async:true,
            dataType : "json",
            data : {
                "databaseId" : databaseId,
            },
            success: function(data){
                $(obj).autocomplete();
                var id= $(obj).attr('id');
                commonAutoComplete(id,data);
            }, error: function(data){
                console.log("Error : "+data);
            }
        });
    }
</script>