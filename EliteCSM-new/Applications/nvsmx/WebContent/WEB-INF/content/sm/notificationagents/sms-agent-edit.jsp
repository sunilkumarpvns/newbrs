<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="sms.agent.update"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/notificationagents" name="smsAgent" id="smsAgent" action="sms-agent" method="post" cssClass="form-horizontal"
                validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8"
                validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="sms.agent.name" id="smsAgentName" tabindex="1"
                                 cssClass="form-control focusElement" maxlength="100"/>
                    <s:textfield name="serviceURL" key="sms.agent.serviceurl" cssClass="form-control" tabindex="2"
                                 id="connectionUrl" maxlength="1024"/>
                </div>
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm" tabindex="3"
                                formaction="${pageContext.request.contextPath}/sm/notificationagents/sms-agent/${id}">
                            <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" tabindex="4"
                                onclick="javascript:location.href='${pageContext.request.contextPath}/sm/notificationagents/sms-agent/${id}'">
                            <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                                name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>
<script>
    function validateForm(){
        if(verifyUniquenessOnSubmit("smsAgentName",'update','${id}','com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData','','') == false){
            setError('smsAgentName', '<s:text name="error.required.field"/>');
            return false;
        }
    }
</script>