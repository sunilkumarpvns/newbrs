<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
    .min-width{
        min-width: 50%;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
            <%--<span class="btn-group btn-group-xs">
                <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/export?ids=${ratingGroupData.id}&${ratingGroupData.id}=${ratingGroupData.groups}'">
                    <span class="glyphicon glyphicon-export" ></span>
                </button>
            </span>--%>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/gatewayprofile/radius-gateway-profile/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="radius.profile.description" value="%{description}" cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.profile.groups" value="%{groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.profile.status" value="%{status}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.profile.gatewaytype" value="%{@com.elitecore.corenetvertex.constants.GatewayComponent@valueOf(gatewayType).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.profile.vendor" value="%{vendorData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.profile.firmware" value="%{firmware}" cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.profile.usagereportingtype" value="%{@com.elitecore.corenetvertex.constants.UsageReportingType@valueOf(usageReportingType).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.profile.revalidationmode" value="%{@com.elitecore.corenetvertex.constants.RevalidationMode@valueOf(revalidationMode).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>

            <!-- connection parameters-->
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="radius.profile.connection.parameter" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="radius.profile.timeout" value="%{timeout}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="radius.profile.maxrequesttimeout" value="%{maxRequestTimeout}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="radius.profile.retrycount" value="%{retryCount}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="radius.profile.statuscheckduration" value="%{statusCheckDuration}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="radius.profile.interim" value="%{interimInterval}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="radius.profile.icmppingenable" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(icmpPingEnabled).getStringNameBoolean()}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="radius.profile.sendaccountingresponse" value="%{sendAccountingResponse}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="radius.profile.supportedvendorlist" value="%{supportedVendorList}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                        </div>
                    </div>
                </div>
                </legend>

            </fieldset>
            <fieldset class="fieldSet-line">
                <legend><s:text name="radius.profile.packet.mapping"/></legend>
                <s:set var="gwToPCCPacketMappingList" value="gwToPCCPacketMappings"  />
                <s:if test="%{#gwToPCCPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}/initManageOrder?type=gatewayToPCC'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="gatewayToPccMappingTable" class="table table-blue table-bordered">
                    <caption class="caption-header"><s:text name="radius.profile.gateway.pcrf.packet.mapping" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="radius.profile.packet.mapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="gwToPCCPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>



                </table>

                <s:set var="pccToGWPacketMappingList" value="pccToGWPacketMappings"  />
                <s:if test="%{#pccToGWPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}/initManageOrder?type=pccToGateway'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="pccToGWMappingTable" class="table table-blue table-bordered">
                    <caption class="caption-header"><s:text name="radius.profile.pcrf.gateway.mapping" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="radius.profile.packet.mapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="pccToGWPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend><s:text name="radius.profile.pcc.rule.mapping"/></legend>
                <s:set var="radiusGwProfilePCCRuleMappingList" value="radiusGwProfilePCCRuleMappings"/>
                <s:if test="%{#radiusGwProfilePCCRuleMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}/initManageOrderPccMapping'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="pccRuleMappingTable" class="table table-blue table-bordered" width="100%">
                    <caption class="caption-header"><s:text name="radius.profile.pcc.rule.mapping" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="radius.profile.pccrule.mapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.pccrule.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.pccrule.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="radiusGwProfilePCCRuleMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/${pccRuleMappingData.id}"><s:property value="%{pccRuleMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
            </fieldset>

            <!-- service guiding -->
            <fieldset class="fieldSet-line">
                <legend><s:text name="radius.profile.service.guiding"/></legend>
                <s:set var="serviceGuidingList" value="serviceGuidingDatas"/>
                <s:if test="%{#serviceGuidingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}/initManageOrderForServiceGuiding'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="groovyScriptTable" class="table table-blue table-bordered" width="100%">
                    <caption class="caption-header"><s:text name="radius.profile.service.guiding" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="radius.profile.service.guiding.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.service.guiding.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.service.guiding.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="serviceGuidingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/pd/service/service/${serviceData.id}"><s:property value="%{serviceData.name}"></s:property></a>
                            </td>
                            <td class="min-width word-break">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
            </fieldset>

            <!-- Groovy Script Configuration -->
            <fieldset class="fieldSet-line">
                <legend><s:text name="radius.profile.groovy.script.mapping"/></legend>
                <s:set var="groovyScriptDataList" value="groovyScriptDatas"/>
                <s:if test="%{#groovyScriptDataList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}/initManageOrderForGroovy'">
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
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>