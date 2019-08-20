<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="database.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/database" action="database" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="database.name" id="databaseName" cssClass="form-control focusElement" onblur="verifyUniqueness('databaseName','create','','com.elitecore.corenetvertex.database.DatabaseData','','');" maxlength="100" />
                    <s:textfield name="connectionUrl" key="database.connectionurl" cssClass="form-control" id="connectionUrl" maxlength="2000"/>
                    <s:textfield name="userName" key="database.username" cssClass="form-control" id="userName" maxlength="200" />
                    <s:textfield name="password" type="password" key="database.password" cssClass="form-control"  id="password" maxlength="150" />
                    <s:textfield name="passwordData.confirmPassword" type="password" key="database.confirm.password" cssClass="form-control"  id="confirmPassword" maxlength="150" />
                    <s:textfield name="statusCheckDuration" type="number" key="database.statuscheckduration" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="statusCheckDuration" min="0" />
                    <s:textfield name="minimumPool" type="number" key="database.minimumpool" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="minimumPool" min="1"/>
                    <s:textfield name="maximumPool" type="number" key="database.maximumpool" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="maximumPool" min="1"/>
                    <s:textfield name="queryTimeout" type="number" key="database.querytimeout" cssClass="form-control" onkeypress="return isNaturalInteger(event);" id="queryTimeout" min="0" />
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="5"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/database/database'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                        <button type="button" class="btn btn-primary btn-sm" onclick="checkDatabaseConnection('true')"><s:text name="database.connection.test" /></button>&nbsp;<strong id="testConnection"></strong>
                    </div>
                </div>
        </div>
        </s:form>
    </div>
</div>
<%@include file="database-utility.jsp"%>
<script type="text/javascript">

    $(function () {
        addRequired(document.getElementById("password"));
        addRequired(document.getElementById("confirmPassword"));
    });

    function validateForm(){
        clearErrorMessages();
        var minimumPool = $("#minimumPool").val();
        var maximumPool = $("#maximumPool").val();
        var password = $("#password").val();
        var confirmPassword = $("#confirmPassword").val();

        var isValidName = verifyUniquenessOnSubmit('databaseName','create','','com.elitecore.corenetvertex.database.DatabaseData','','');
        if(isValidName == false) {
            return false;
        } else if(isNullOrEmpty(password)) {
            setError('password', "Password is required");
            return false;
        } else if(isNullOrEmpty(confirmPassword)) {
            setError('confirmPassword', "Confirm Password is required");
            return false;
        } else if(password != confirmPassword) {
            setError('confirmPassword', "Password and Confirm Password should be same");
            return false;
        } else if (parseInt(minimumPool) > parseInt(maximumPool)) {
            setError('minimumPool', "MinimumPool value must be less than MaximumPool value");
            return false;
        }
    }

</script>

