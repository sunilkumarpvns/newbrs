<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="trap.alert.update"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/alert"  id="trapAlertListenerForm" action="trap-alert" method="post" cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                validator="validateTrapListenerForm('update','%{id}')">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <div class="row">
                <div class="col-xs-12 col-sm-6">
                    <s:textfield name="name" key="trap.alert.name" id="name"
                                 cssClass="form-control focusElement" tabindex="1" maxlength="100"/>
                    <s:hidden name="type" value="%{@com.elitecore.corenetvertex.sm.alerts.AlertTypes@TRAP.name()}"/>
                    <s:textfield name="trapServer" key="trap.alert.server" id="trapServer" cssClass="form-control"
                                 maxlength="18" tabindex="2" maxLength="100" />

                    <s:select name="trapVersion" key="trap.alert.version" cssClass="form-control" id="trapVersion"
                              list="@com.elitecore.core.serverx.alert.TrapVersion@values()" tabindex="3"
                              onchange="disableSnmpRequestType()"/>

                    <s:select name="snmpRequestType" key="trap.alert.snmp.request.type" cssClass="form-control"
                              id="snmpRequestType"
                              list="@com.elitecore.core.serverx.alert.SnmpRequestType@values()" tabindex="4"
                              onchange="setTimeOutAndRetryCountValue()" disabled="true "/>
                </div>
                <div class="col-xs-12 col-sm-6">
                    <s:textfield name="timeOut" key="trap.alert.timeout" id="timeOut" cssClass="form-control"
                                 maxlength="2" tabindex="5" disabled="true" onkeypress="return isNaturalInteger(event);"/>
                    <s:textfield name="retryCount" key="trap.alert.retryCount" id="retryCount" cssClass="form-control"
                                 maxlength="2"  tabindex="6" disabled="true" onkeypress="return isNaturalInteger(event);"/>

                    <s:textfield name="community" key="trap.alert.community" id="community" cssClass="form-control"
                                 maxlength="50" tabindex="7" maxLength="100" />
                    <s:select name="advanceTrap" key="trap.alert.advance.trap" id="advanceTrap" listValue="stringName"
                              listKey="booleanValue"
                              list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" tabindex="6"/>
                </div>
            </div>

            <%--alert mapping--%>
            <%@include file="alert-utility.jsp"%>

            <%--general error div--%>
            <%@include file="/view/commons/general/GeneralErrorDiv.jsp"%>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button"
                            formaction="${pageContext.request.contextPath}/sm/alert/trap-alert/${id}" tabindex="14"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="15"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/alert/trap-alert/${id}'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>
<%@ include file="trap-alert-utilty.jsp"%>
<script>
    $(function(){
        disableSnmpRequestType();
    });

</script>

