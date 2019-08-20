<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="serverinstance.view" />
        </h3>
        <div class="nv-btn-group" align="right">

            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Reload Configuration"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}/reloadConfiguration'">
                    <span class="glyphicon glyphicon-refresh" ></span>
                </button>
			</span>

            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableId=${auditableId}&auditableResourceName=${name}&refererUrl=/sm/serverinstance/server-instance/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}/edit?serverGroupId=${serverGroupId}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>

    <div class="panel-body">

            <div class="row">
                <div align="right">
                    <span class="btn-group btn-group-xs">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px;margin-right: 10px" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverinstancelivedetail/server-instance-live-detail/${id}'">
                            <s:text name="serverinstance.live.detail"/>
                        </button>
                    </span>
                </div>
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverinstance.basicdetails"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:label    value="%{name}"
                                        key="serverinstance.name"
                                        cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label    value="%{description}"
                                        key="serverinstance.description"
                                        cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label    value="%{javaHome}"
                                        key="serverinstance.javaHome"
                                        cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label    value="%{serverHome}"
                                        key="serverinstance.serverHome"
                                        cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{restApiUrl}"
                                     key="serverinstance.rest.api.listener"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8" />

                            <s:label    value="%{snmpUrl}" key="serverinstance.snmp.address"
                                        cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </fieldset>
            </div>

			<s:if test="#session.serverGroupType == @com.elitecore.corenetvertex.constants.ServerGroups@PCC.getValue()">
                <div class="row">
                    <fieldset class="fieldSet-line">
                        <legend><s:text name="serverinstance.notification.services"/></legend>
                        <div class="row" >
                            <div class="col-xs-12 col-sm-6 col-lg-6">

                                <s:hrefLabel value="%{emailAgentData.name}" key="serverinstance.emailagent"
                                         url="/sm/notificationagents/email-agent/%{emailAgentData.id}"
                                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </div>


                            <div class="col-xs-12 col-sm-6 col-lg-6">

                                <s:hrefLabel value="%{smsAgentData.name}" key="serverinstance.smsgateway"
                                         url="/sm/notificationagents/sms-agent/%{smsAgentData.id}"
                                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            </div>

                        </div>
                    </fieldset>
                </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverinstance.diameter.stack"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:if test="%{diameterEnabled==1}">
                            <s:label value="Enabled"
                                      key="serverinstance.diameter.listener.enable"
                                      cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:if>
                            <s:else>
                                <s:label value="Disabled"
                                         key="serverinstance.diameter.listener.enable"
                                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:else>

                            <s:label value="%{diameterOriginHost}" key="serverinstance.diameter.origin.host"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>


                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:label value="%{diameterUrl}" key="serverinstance.diameter.url"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{diameterOriginRealm}" key="serverinstance.diameter.origin.realm"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>

                    </div>
                </fieldset>
            </div>

            <%--Radius Listener--%>
             <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>  <s:text name="serverinstance.listener"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:if test="%{radiusEnabled==1}">
                                <s:label value="Enabled"
                                         key="serverinstance.radius.listener.enable"
                                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:if>
                            <s:else>
                                <s:label value="Disabled"
                                      key="serverinstance.radius.listener.enable"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:else>
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:label value="%{radiusUrl}" key="serverinstance.radius.url"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>
                    </div>
                </fieldset>
             </div>
                <div class="row">
                    <!-- Groovy Script Configuration -->
                    <fieldset class="fieldSet-line">
                        <legend><s:text name="serverinstance.groovy.script"/></legend>
                        <s:set var="groovyScriptDataList" value="groovyScriptDatas"/>
                        <s:if test="%{#groovyScriptDataList.size() <= 1}">
                            <button class="btn btn-primary btn-xs" disabled
                                    style="float:right;padding-top: 3px; padding-bottom: 3px">
                                <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                                <s:text name="manage.order"/>
                            </button>
                        </s:if>
                        <s:else>
                            <button class="btn btn-primary btn-xs"
                                    style="float:right;padding-top: 3px; padding-bottom: 3px"
                                    onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}/initManageOrder'">
                                <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                                <s:text name="manage.order"/>
                            </button>
                        </s:else>
                        <div class="row">
                            <div class="col-sm-12">
                                <nv:dataTable
                                        id="groovyscripts"
                                        list="${groovyScripts}"
                                        width="100%"
                                        showPagination="false"
                                        showInfo="false"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn title="Order No" beanProperty="orderNumber" tdCssClass="text-left text-middle" />
                                    <nv:dataTableColumn title="ScriptName" beanProperty="scriptName" tdCssClass="text-left text-middle" />
                                    <nv:dataTableColumn title="Argument" beanProperty="argument" tdCssClass="text-left text-middle" />
                                </nv:dataTable>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </s:if>
            
             <!-- Offline RnC  -->
			<s:if test="#session.serverGroupType == @com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()">
				<div class="row">
				<fieldset class="fieldSet-line">
	                   <legend><s:text name="serverinstance.offlinernc"/></legend>
					 <div class="row">
						<div class="col-xs-12 col-sm-6 col-lg-6">
                         
                          <s:if test="%{offlineRncService==1}">
                                <s:label value="Enabled"
                                         key="serverinstance.offlinernc.service"
                                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:if>
                            <s:else>
                                <s:label value="Disabled"
                                      key="serverinstance.offlinernc.service"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:else>
						</div>
						<div class="col-xs-12 col-sm-6 col-lg-6">
					       <s:label value="%{fileLocationListJson}"
                                     key="serverinstance.filelocation"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
					   </div>
					 </div>
	               </fieldset>
				</div>
			</s:if>
            
            <div align="center">
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/servergroup/server-group'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="serverinstance.servergroups"/> </button>
            </div>
    </div>
</div>