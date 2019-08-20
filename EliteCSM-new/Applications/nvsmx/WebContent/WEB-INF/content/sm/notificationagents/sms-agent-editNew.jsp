<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="sms.agent.create"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/notificationagents" name="smsAgent" id="smsAgent" action="sms-agent" method="post" cssClass="form-horizontal"
                validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8"
                validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="sms.agent.name" id="smsAgentName" tabindex="1"
                                 cssClass="form-control focusElement" maxlength="100"/>
                    <s:textfield name="serviceURL" key="sms.agent.serviceurl" cssClass="form-control" tabindex="2"
                                 id="connectionUrl" maxlength="124"/>
                    <s:textfield name="password" type="password" key="sms.agent.password" cssClass="form-control" tabindex="3"
                                 id="password" maxlength="20"/>
                    <s:textfield  type="password" key="sms.agent.confirm.password" cssClass="form-control" tabindex="4"
                                  id="confirmPassword" maxlength="20"/>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="5"><span
                                class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" tabindex="6"
                                onclick="javascript:location.href='${pageContext.request.contextPath}/sm/notificationagents/sms-agent'">
                            <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                                name="button.list"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>
<script>

    $(function () {
        addRequired(document.getElementById("password"));
        addRequired(document.getElementById("confirmPassword"));
    });
    function validateForm(){
        if(verifyUniquenessOnSubmit("smsAgentName",'create','','com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData','','') == false){
            setError('smsAgentName', "Name is Required");
            return false;
        }
        return validatePassword();
    }
</script>
<%@include file="notificationValidationUtility.jsp"%>