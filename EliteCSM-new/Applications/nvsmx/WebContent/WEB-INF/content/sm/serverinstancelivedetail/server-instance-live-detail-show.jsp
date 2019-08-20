<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<%  int rows = ConfigurationProvider.getInstance().getPageRowSize();%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
             ${name}&nbsp;<s:text name="serverinstance.live.detail" />
        </h3>
    </div>
    <div class="panel-body" >

        <div class="row">
            <fieldset class="fieldSet-line">
                <legend> <s:text name="serverinstance.server.info"/> </legend>
                <div class="row">
                    <div class="col-xs-12 col-sm-6 col-lg-6">
                        <s:label value="%{serverInfo.serverName}"       key="serverinstance.server.name" cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label value="%{serverInfo.version}"  key="serverinstance.server.version" cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                    </div>

                    <div class="col-xs-12 col-sm-6 col-lg-6">

                        <s:if test="%{serverInfo.serverStartUpTime != null}">
                            <s:set var="startedTime">
                                <s:date name="%{serverInfo.serverStartUpTime}"
                                        format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
                            </s:set>
                            <s:label value="%{startedTime}" key="serverinstance.server.start.time"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4"
                                     elementCssClass="col-sm-7 col-xs-8"/>
                        </s:if>
                        <s:if test="%{serverInfo.lastConfigurationReloadTime != null}">
                            <s:set var="lastConfReloadTime">
                                <s:date name="%{serverInfo.lastConfigurationReloadTime}"
                                        format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
                            </s:set>
                            <s:label value="%{lastConfReloadTime}" key="serverinstance.server.conf.reloadon" cssClass="control-label light-text word-break"
                                     labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        </s:if>

                    </div>
                </div>
            </fieldset>
        </div>

        <div class="row">
        <fieldset class="fieldSet-line">
            <legend> <s:text name="serverinstance.services"/> </legend>
            <div class="col-sm-12">
                    <nv:dataTable
                            id="servicesInfoTable"
                            list="${serviceInfoListAsJson}"
                            rows="<%=rows%>"
                            width="100%"
                            showPagination="true"
                            cssClass="table table-blue">
                        <nv:dataTableColumn title="Name" 		 beanProperty="name" style="width:200px;" />
                        <nv:dataTableColumn title="Status" 		 beanProperty="status" />
                        <nv:dataTableColumn title="Start Date" 	 beanProperty="startDate" />
                        <nv:dataTableColumn title="Remarks" 	     beanProperty="remarks" />
                    </nv:dataTable>
            </div>
        </fieldset>
        </div>

        <div class="row">
        <fieldset class="fieldSet-line">
            <legend> <s:text name="serverinstance.gateways"/> </legend>
            <div class="col-sm-12">
                    <nv:dataTable
                            id="gatewaysInfoTable"
                            list="${gatewayStatusInfoListAsJson}"
                            rows="<%=rows%>"
                            width="100%"
                            showPagination="true"
                            cssClass="table table-blue">
                        <nv:dataTableColumn title="Type" 		        beanProperty="communicationProtocol" style="width:200px;" />
                        <nv:dataTableColumn title="Gateway Identity" 	beanProperty="gatewayIdentity" style="width:200px;" />
                        <nv:dataTableColumn title="Status" 		        beanProperty="status" />
                    </nv:dataTable>
            </div>
        </fieldset>
        </div>

        <div class="row">
        <fieldset class="fieldSet-line">
            <legend> <s:text name="serverinstance.global.listeners"/> </legend>
            <div class="col-sm-12">
                    <nv:dataTable
                            id="globalListenersInfoTable"
                            list="${globalListenersInfoListAsJson}"
                            rows="<%=rows%>"
                            width="100%"
                            showPagination="true"
                            cssClass="table table-blue">
                        <nv:dataTableColumn title="Name" 		 beanProperty="listenerName" style="width:200px;" />
                        <nv:dataTableColumn title="Address" 		 beanProperty="listenerAddress" />
                        <nv:dataTableColumn title="Port" 		 beanProperty="listenerPort" />
                        <nv:dataTableColumn title="Status" 		 beanProperty="status" />
                        <nv:dataTableColumn title="Start Date" 		 beanProperty="startDate" />
                        <nv:dataTableColumn title="Remarks" 		 beanProperty="remarks" />
                    </nv:dataTable>
            </div>
        </fieldset>
        </div>

        <div class="row">
            <fieldset class="fieldSet-line">
                <legend> <s:text name="serverinstance.datasources"/> </legend>
                <div class="col-sm-12">
                    <nv:dataTable
                            id="dataSourceInfoTable"
                            list="${dataSourceInfoListAsJson}"
                            rows="<%=rows%>"
                            width="100%"
                            showPagination="true"
                            cssClass="table table-blue">
                        <nv:dataTableColumn title="DataSource Name" 		 beanProperty="dataSourceName" width="200px"/>
                        <nv:dataTableColumn title="Type" 		             beanProperty="dataSourceType" width="80px"/>
                        <nv:dataTableColumn title="Active Connections" 		 beanProperty="noOfActiveconnection" width="120px"/>
                        <nv:dataTableColumn title="Minimum Pool Size" 		 beanProperty="minimumPoolSize" width="120px"/>
                        <nv:dataTableColumn title="Maximum Pool Size" 		 beanProperty="maximumPoolSize" width="120px"/>
                        <nv:dataTableColumn title="Status" 		 beanProperty="status" width="150px"/>
                    </nv:dataTable>
                </div>
            </fieldset>
        </div>

        <div class="row">
            <fieldset class="fieldSet-line">
                <legend> <s:text name="serverinstance.policy.status"/> </legend>
                <div class="col-sm-12">
                    <nv:dataTable
                            id="policyDetailInfoTable"
                            list="${policyDetailListAsJson}"
                            rows="<%=rows%>"
                            width="100%"
                            showPagination="true"
                            cssClass="table table-blue">
                        <nv:dataTableColumn title="Name" 		 beanProperty="name" />
                        <nv:dataTableColumn title="Status" 		 beanProperty="status" />
                        <nv:dataTableColumn title="Remark" 		 beanProperty="remark" />
                    </nv:dataTable>
                </div>
            </fieldset>
        </div>

        <div class="row">
            <div class="col-xs-12" align="center">
                <button id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/> </button>
            </div>
        </div>

    </div>
</div>
