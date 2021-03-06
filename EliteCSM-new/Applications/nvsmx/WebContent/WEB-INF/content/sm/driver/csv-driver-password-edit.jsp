<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 28/10/17
  Time: 11:18 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="change.password.label"/> </h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/driver" action="csv-driver-password" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()" id="csvdriverchangepasswordform">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield type="password" id="oldPassword" name="oldPassword" key="Old Password" cssClass="form-control focusElement" />
                    <s:textfield type="password" id="newPassword" name="newPassword" key="New Password" cssClass="form-control" />
                    <s:textfield type="password" id="confirmPassword" name="confirmPassword" key="Confirm Password" cssClass="form-control"/>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/driver/csv-driver-password/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.cancel"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">

    $(function () {
        addRequired(document.getElementById("confirmPassword"));
    });

    function validateForm() {
        clearErrorMessages();
        var newPassword = $("#newPassword").val();
        var confirmPassword = $("#confirmPassword").val();

        if(isNullOrEmpty(confirmPassword)) {
            setError('confirmPassword', "Confirm Password is required");
            return false;
        }

       if (isNullOrEmpty(confirmPassword)) {
           setError('confirmPassword', "Confirm Password is required");
           return false;
       }else if(confirmPassword != newPassword) {
           setError('confirmPassword', '<s:text name="new.confirm.password.same.note" />');
           return false;
       }
        return true;
    }

</script>