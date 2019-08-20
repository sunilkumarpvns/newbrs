<%--
  Created by IntelliJ IDEA.
  User: jaidiptrivedi
  Date: 9/10/17
  Time: 3:16 PM
--%>
<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>


<style type="text/css">
    .attribute-name {
        font-weight: bold;
        margin: auto;
        vertical-align: middle;
    }

    .form-group {
        margin-bottom: 0px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="systemparameter.update"/>
        </h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/sm/systemparameter" action="system-parameter" id="systemParameter" name="systemparameterForm"
                method="post" cssClass="form-vertical" validate="true" validator="validateForm();">

        <s:hidden name="_method" value="put"/>

        <div class="row">
            <div class="col-sm-12">
                <table class="table table-blue dataTable no-footer">
                    <col width="20%">
                    <col width="20%">
                    <col width="60%">
                    <thead>
                    <tr>
                        <th><s:text name="header.name"/></th>
                        <th><s:text name="header.value"/></th>
                        <th><s:text name="header.description"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <s:iterator var="systemParameter" value="systemParameters" status="stat">
                        <tr>
                            <s:hidden name="systemParameters[%{#stat.index}].alias"/>
                            <td class="attribute-name"><s:text name="%{#systemParameter.name}"/></td>
                            <td>
                                <s:if test="%{systemParameterValuePoolMap.containsKey(#systemParameter.alias)}">
                                    <s:select name="systemParameters[%{#stat.index}].value"
                                              list="systemParameterValuePoolMap.get(#systemParameter.alias)"
                                              cssClass="form-control" listKey="val" listValue="displayValue"/>
                                </s:if>
                                <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@CURRENCY.name() == #systemParameter.alias}">
                                    <s:select name="systemParameters[%{#stat.index}].value" id="currency"
                                              cssClass="form-control select2" list="@java.util.Currency@getAvailableCurrencies()"
                                              listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" headerKey="" headerValue="-select-"
                                    cssStyle="width:100%"/>
                                </s:elseif>
                                <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@COUNTRY.name() == #systemParameter.alias}">
                                    <s:select name="systemParameters[%{#stat.index}].value" id="countryNames" value="countryData.id"
                                              cssClass="form-control select2"
                                              list="countryList" listKey="id" listValue="name"  headerKey="" headerValue="-select-" cssStyle="width:100%"/>
                                </s:elseif>
                                <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@OPERATOR.name() == #systemParameter.alias}">
                                    <s:select name="systemParameters[%{#stat.index}].value" id="operatorNames" value="operatorData.id"
                                              cssClass="form-control select2" list="operatorList" listKey="id"
                                              listValue="name"  cssStyle="width:100%" headerKey="" headerValue="-select-"/>
                                </s:elseif>
                                <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@DEPLOYMENT_MODE.name() == #systemParameter.alias}">
                                    <s:select name="systemParameters[%{#stat.index}].value" id="deploymentMode"
                                              cssClass="form-control"
                                              list="@com.elitecore.corenetvertex.constants.DeploymentMode@values()" listValue="value"
                                              cssStyle="width:100%"/>

                                </s:elseif>
                                <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@SSO_AUTHENTICATION.name() == #systemParameter.alias}">
                                    <s:if test="%{@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isSSOEnable()}">
                                        <s:select name="systemParameters[%{#stat.index}].value" id="ssoAuthentication"
                                                  cssClass="form-control"
                                                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                                  listKey="isBooleanValue()" listValue="getStringName()"
                                                  cssStyle="width:100%" readonly="true" disabled="true"/>
                                        <s:hidden name="systemParameters[%{#stat.index}].value"/>
                                    </s:if>
                                    <s:else>
                                        <s:select name="systemParameters[%{#stat.index}].value" id="ssoAuthentication"
                                                  cssClass="form-control"
                                                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                                  listKey="isBooleanValue()" listValue="getStringName()"
                                                  cssStyle="width:100%"/>
                                    </s:else>
                                </s:elseif>
                                <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@MULTI_CURRENCY_SUPPORT.name() == #systemParameter.alias}">
                                    <s:if test="%{@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()}">
                                        <s:select name="systemParameters[%{#stat.index}].value" id="multiCurrencySupport"
                                                  cssClass="form-control"
                                                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                                  listKey="isBooleanValue()" listValue="getStringName()"
                                                  cssStyle="width:100%" readonly="true" disabled="true"/>
                                        <s:hidden name="systemParameters[%{#stat.index}].value"/>
                                    </s:if>
                                    <s:else>
                                        <s:select name="systemParameters[%{#stat.index}].value" id="multiCurrencySupport"
                                                  cssClass="form-control"
                                                  list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                                  listKey="isBooleanValue()" listValue="getStringName()"
                                                  cssStyle="width:100%"/>
                                    </s:else>
                                </s:elseif>
                                <s:else>
                                    <s:textfield id="%{#systemParameter.alias}"
                                                 name="systemParameters[%{#stat.index}].value" cssClass="form-control"/>
                                </s:else>
                            </td>
                            <td><span><s:text name="%{#systemParameter.description}"  /></span></td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="row">
            <div class="col-sm-12">
                <table class="table table-blue dataTable no-footer">
                    <caption><s:text name="packageParameter.title"></s:text></caption>
                    <col width="20%">
                    <col width="20%">
                    <col width="60%">
                    <thead>
                    <tr>
                        <th><s:text name="header.name"/></th>
                        <th><s:text name="header.value"/></th>
                        <th><s:text name="header.description"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <s:iterator var="packageParameter" value="packageParameters" status="stat">
                        <tr>
                            <s:hidden name="packageParameters[%{#stat.index}].alias"/>
                            <td class="attribute-name"><s:text name="%{#packageParameter.name}"/></td>
                            <td><s:textfield name="packageParameters[%{#stat.index}].value"
                                             cssClass="form-control"/></td>
                            <td><s:text name="%{#packageParameter.description}"/></td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="row">
            <div class="col-xs-12" align="center">
                <button class="btn btn-sm btn-primary"
                        type="submit"
                        role="button"
                        formaction="${pageContext.request.contextPath}/sm/systemparameter/system-parameter/update"
                        onclick="return validateForm()">
                    <span class="glyphicon glyphicon-floppy-disk"></span>
                    <s:text name="button.save"/>
                </button>

                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"
                        style="margin-right:10px;"
                        onclick="restrictToUpdatePage();">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text
                        name="systemparameter.view"/></button>
            </div>
        </div>

    </div>


    <div class="modal fade" id="warningCurrencyChange" tabindex="-1" role="dialog" aria-labelledby="warningCurrencyChangeLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" id="warningCurrencyChangeLabel" >Warning!!!</h4>
                </div>
                <div class="modal-body">
                    <div>
                        <s:text name="warning.message"/>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal" ><s:text name="button.no"></s:text> </button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="submitForm();" ><s:text name="button.yes"></s:text> </button>
                    <%--<button class="btn btn-primary" role="submit" value="Update" type="submit" formaction="update"><s:text name="button.update" /></button>--%>
                </div>
            </div>
        </div>
    </div>
<%@include file="deploymentWarning.jsp"%>
<%@include file="ssoWarningModalForEnable.jsp"%>
<%@include file="ssoWarningModalForDisable.jsp"%>
</s:form>

</div>

<%@include file="SystemParameterValidation.jsp" %>
<script type="text/javascript">
    $(function () {
        $(".select2").select2();
    });
    function submitForm(){
        document.forms["systemparameterForm"].action = "${pageContext.request.contextPath}/sm/systemparameter/system-parameter/update";
        document.forms["systemparameterForm"].submit();

    }

    function restrictToUpdatePage(){
        var isValidConfiguration= true;
        var systemCountry =  '<s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCountry()"/>';;

        if( isNullOrEmpty(systemCountry)) {
            setError('countryNames','<s:text name="county.error.message"/>');
            isValidConfiguration = false;
        }

        var systemOperator = '<s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getOperator()"/>';;

        if(isNullOrEmpty(systemOperator)) {
            setError('operatorNames','<s:text name="operator.error.message"/>');
            isValidConfiguration = false;
        }

        var systemCurrency = '<s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>';;

        if(isNullOrEmpty(systemCurrency)) {
            setError('currency','<s:text name="currency.error.message"/>');
            isValidConfiguration = false;
        }

        if(isValidConfiguration){
            document.location.href='${pageContext.request.contextPath}/sm/systemparameter/system-parameter/show';
        }else{
            addDanger(".popup","Please save country,operator & currency information!!!");
            return false;
        }
    }

    $("#deploymentMode").change(function(){
        var deploymentMode = '<s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getDeploymentMode()"/>';
        if(deploymentMode != $("#deploymentMode").val()){
            $("#warningDeploymentModeChange").modal('show');
            return false;
        }
        return true;
    });
    $("#ssoAuthentication").change(function(){
        var ssoAuthentication = '<s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isSSOEnable()"/>';
        var configuredSSOValue = $("#ssoAuthentication").val();
        if(ssoAuthentication != configuredSSOValue){
            if(configuredSSOValue == 'true'){
                $("#warningSSOAuthenticationChangeForTrue").modal('show');
            }else if (configuredSSOValue == 'false'){
                $("#warningSSOAuthenticationChange").modal('show');
            }
            return false;
        }
        return true;
    });
</script>