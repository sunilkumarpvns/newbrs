<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="email.agent.update"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/notificationagents" name="emailAgent" id="emailAgent" action="email-agent" method="post" cssClass="form-horizontal"
                validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8"
                validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="email.agent.name" id="emailAgentName" tabindex="1"
                                 cssClass="form-control focusElement" maxlength="100"/>
                    <s:textfield name="emailHost" key="email.agent.hostaddress" cssClass="form-control" tabindex="2"
                                 id="connectionUrl" maxlength="255"/>
                    <s:textfield name="userName" key="email.agent.username" cssClass="form-control" id="userName" tabindex="3"
                                 maxlength="20"/>
                    <s:textfield name="fromAddress" key="email.agent.fromaddress" cssClass="form-control" tabindex="5" maxLength="255"
                                 id="fromAddress"/>
                </div>
                 <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm" tabindex="6"
                                formaction="${pageContext.request.contextPath}/sm/notificationagents/email-agent/${id}">
                            <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" tabindex="7"
                                onclick="javascript:location.href='${pageContext.request.contextPath}/sm/notificationagents/email-agent/${id}'">
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
        if(verifyUniquenessOnSubmit("emailAgentName",'update','${id}','com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData','','') == false){
            return false;
        }
   }
</script>