<%--
  Created by IntelliJ IDEA.
  User: dhyani
  Date: 26/9/17
  Time: 3:43 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">

    function checkDatabaseConnection(isPlainTextPassword) {

        var connectionUrl = $("#connectionUrl").val();
        var userName = $("#userName").val();
        var password = $("#password").val();

        $.ajax({
            type: "POST",
            url:"${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/checkDatabaseConnection",
            dataType : "text",
            async : true,
            data : {
                "connectionUrl" : connectionUrl,
                "userName" : userName,
                "password" : password,
                "isPlainTextPassword" : isPlainTextPassword, //for create it will be true , for update it will be false
            },
            success: function(data){
                var jsonData = JSON.parse(data);
                if(jsonData.sqlstate == '200') {
                    return addSuccess(".popup",jsonData.message);
                } else {
                    return addDanger(".popup",jsonData.message);
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                return addDanger(".popup","Failed");
            },

        });

    }
</script>
