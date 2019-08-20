<%--
  Created by IntelliJ IDEA.
  User: jaidiptrivedi
  Date: 5/10/17
  Time: 5:11 PM
--%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .attribute-name{
        font-weight:bold;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="systemparameter.view"/>
        </h3>
        <div class="nv-btn-group" align="right">
            <span class="btn-group btn-group-xs">
                <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="Update"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/systemparameter/system-parameter/*/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
            </span>
        </div>
    </div>

    <div class="panel-body">
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
                            <td class="attribute-name"><s:text name="%{#systemParameter.name}"/></td>
                            <s:if test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@CURRENCY.name() == #systemParameter.alias}">
                                <td>
                                    <s:if test="%{#systemParameter.value == null || #systemParameter.value == ''}">
                                        &nbsp;
                                    </s:if>
                                    <s:else>
                                        <s:text name="%{#systemParameter.value} (%{@java.util.Currency@getInstance(#systemParameter.value).getDisplayName()})"/>
                                    </s:else>
                                </td>
                            </s:if>
                            <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@COUNTRY.name() == #systemParameter.alias}">
                                <td><s:text name="%{countryData.name}"/></td>
                            </s:elseif>
                            <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@OPERATOR.name() == #systemParameter.alias}">
                                <td><s:text name="%{operatorData.name}"/></td>
                            </s:elseif>
                            <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@DEPLOYMENT_MODE.name() == #systemParameter.alias}">
                                <td><s:text name="%{@com.elitecore.corenetvertex.constants.DeploymentMode@fromName(#systemParameter.value).value}"/></td>
                            </s:elseif>
                            <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@SSO_AUTHENTICATION.name() == #systemParameter.alias}">
                                <td>
                                    <s:text name="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(#systemParameter.value).getStringName()}"/>
                                </td>
                            </s:elseif>
                            <s:elseif test="%{@com.elitecore.corenetvertex.sm.systemparameter.SystemParameter@MULTI_CURRENCY_SUPPORT.name() == #systemParameter.alias}">
                                <td>
                                    <s:text name="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(#systemParameter.value).getStringName()}"/>
                                </td>
                            </s:elseif>
                            <s:else>
                                <td><s:property value="%{#systemParameter.value}"/></td>
                            </s:else>
                            <td><s:text name="%{#systemParameter.description}"/></td>
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
                            <td class="attribute-name"><s:text name="%{#packageParameter.name}"/></td>
                            <td><s:text name="%{#packageParameter.value}"/></td>
                            <td><s:text name="%{#packageParameter.description}"/></td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
