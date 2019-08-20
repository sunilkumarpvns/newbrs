<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="email.agent.create"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/notificationagents" name="emailAgent" id="emailAgent" action="email-agent" method="post" cssClass="form-horizontal"
                validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8"
                validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="email.agent.name" id="emailAgentName" tabindex="1"
                                 cssClass="form-control focusElement" maxlength="100"/>
                    <s:textfield name="emailHost" key="email.agent.hostaddress" cssClass="form-control" tabindex="2"
                                 id="connectionUrl" maxlength="255"/>
                    <s:textfield name="userName" key="email.agent.username" cssClass="form-control" id="userName" tabindex="3"
                                 maxlength="20"/>
                    <s:textfield name="password" type="password" key="email.agent.password" cssClass="form-control" tabindex="4"
                                 id="password" maxlength="20"/>
                    <s:textfield  type="password" key="email.agent.confirm.password" cssClass="form-control" tabindex="5"
                                 id="confirmPassword" maxlength="20"/>
                    <s:textfield name="fromAddress" key="email.agent.fromaddress" cssClass="form-control" maxLength="255" tabindex="6"
                                 id="fromAddress"/>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="7"><span
                                class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" tabindex="8"
                                onclick="javascript:location.href='${pageContext.request.contextPath}/sm/notificationagents/email-agent'">
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
        if (verifyUniquenessOnSubmit("emailAgentName", 'create', '', 'com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData', '', '') == false) {
            return false;
        }
        return validatePassword();
    }
</script>
<%@include file="notificationValidationUtility.jsp"%>