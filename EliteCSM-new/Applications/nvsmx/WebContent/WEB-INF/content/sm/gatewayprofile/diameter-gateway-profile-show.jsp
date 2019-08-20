<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/gatewayprofile/diameter-gateway-profile/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}?_method=DELETE">
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
                            <s:label key="diameter.profile.description" value="%{description}" cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.groups" value="%{groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.status" value="%{status}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.gatewaytype" value="%{@com.elitecore.corenetvertex.constants.GatewayComponent@valueOf(gatewayType).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.vendor" value="%{vendorData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.firmware" value="%{firmware}" cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.usagereportingtype" value="%{@com.elitecore.corenetvertex.constants.UsageReportingType@valueOf(usageReportingType).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.revalidationmode" value="%{@com.elitecore.corenetvertex.constants.RevalidationMode@valueOf(revalidationMode).getValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
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
                <legend align="top"><s:text name="diameter.profile.connection.parameter" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="diameter.profile.timeout" value="%{timeout}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.sessioncleanuponcer" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(sessionCleanUpCER).getStringNameBoolean()}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.sessioncleanupondpr" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(sessionCleanUpDPR).getStringNameBoolean()}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.ceravps" value="%{cerAvps}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break" />
                            <s:label key="diameter.profile.dpravps" value="%{dprAvps}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break"/>
                            <s:label key="diameter.profile.dwravps" value="%{dwrAvps}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break"/>
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="diameter.profile.senddprcloseevent" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(sendDPRCloseEvent).getStringNameBoolean()}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.socketreceivebuffersize" value="%{socketReceiveBufferSize}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.socketsendbuffersize" value="%{socketSendBufferSize}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.tcpnaglealgorithm" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(tcpNagleAlgorithm).getStringNameBoolean()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.dwrduration" value="%{dwInterval}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.initconnectionduration" value="%{initConnectionDuration}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                        </div>
                    </div>
                </div>
                </legend>

            </fieldset>

            <!-- application parameters -->
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="diameter.profile.application.parameter" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="diameter.profile.customgxapplicationid" value="%{gxApplicationId}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break"/>
                            <s:label key="diameter.profile.customgyapplicationid" value="%{gyApplicationId}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break"/>
                            <s:label key="diameter.profile.customrxapplicationid" value="%{rxApplicationId}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break" />
                            <s:label key="diameter.profile.customs9applicationid" value="%{s9ApplicationId}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break"/>
                            <s:label key="diameter.profile.customsyapplicationid" value="%{syApplicationId}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6 word-break"/>
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="diameter.profile.chargingruleinstallmode" value="%{@com.elitecore.corenetvertex.constants.ChargingRuleInstallMode@valueOf(chargingRuleInstallMode).val}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.supportedstandard" value="%{@com.elitecore.corenetvertex.constants.SupportedStandard@valueOf(supportedStandard).name}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:label key="diameter.profile.umstandard" value="%{@com.elitecore.corenetvertex.constants.UMStandard@valueOf(umStandard).displayValue}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="diameter.profile.supportedvendorlist" value="%{supportedVendorList}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            <s:if test="%{gatewayType == @com.elitecore.corenetvertex.constants.GatewayComponent@APPLICATION_FUNCTION.name() || gatewayType == @com.elitecore.corenetvertex.constants.GatewayComponent@DRA.name()}">
                                <s:label key="diameter.profile.sessionlookupkey" value="%{sessionLookUpKey}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"/>
                            </s:if>
                        </div>
                    </div>
                </div>
                </legend>

            </fieldset>
            <fieldset class="fieldSet-line">
                <legend><s:text name="diamter.profile.gx.packet.mapping"/></legend>
                <s:set var="gwToPccGxPacketMappingList" value="gwToPccGxPacketMappings"  />
                <s:if test="%{#gwToPccGxPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                 <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrder?type=gxGatewayToPCC'">
                    <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                    <s:text name="manage.order"/>
                </button>
                </s:else>
                <table id="gatewayToPccGxMappingTable" class="table table-blue table-bordered" style="width: 100%">
                    <caption class="caption-header"><s:text name="diameter.profile.gateway.pcrf.packet.mapping" />
                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="diameter.profile.packetmapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                     </tr>
                    </thead>
                    <tbody>
                    <s:iterator value="gwToPccGxPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                               <s:property value="%{condition}"></s:property>
                           </td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>


                <s:set var="pccToGwGxPacketMappingList" value="pccToGWGxPacketMappings"/>
                <s:if test="%{#pccToGwGxPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrder?type=gxPccToGw'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="pccToGWGxMappingTable" class="table table-blue table-bordered" style="width: 100%">
                    <caption class="caption-header"><s:text name="diameter.profile.pcrf.gateway.mapping" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="diameter.profile.packetmapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <s:iterator value="pccToGwGxPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>
            </fieldset>

            <!-- Gy Packet Mapping -->
            <fieldset class="fieldSet-line">
                <legend><s:text name="diamter.profile.gy.packet.mapping"/></legend>
                <s:set var="gwToPccGyPacketMappingList" value="gwToPccGyPacketMappings"/>
                <s:if test="%{#gwToPccGyPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrder?type=gyGatewayToPCC'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="gatewayToPccGyMappingTable" class="table table-blue table-bordered" width="100%">
                    <caption class="caption-header"><s:text name="diameter.profile.gateway.pcrf.packet.mapping" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="diameter.profile.packetmapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="gwToPccGyPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
                <s:set var="pccToGWGyPacketMappingList" value="pccToGWGyPacketMappings"/>
                <s:if test="%{#pccToGWGyPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrder?type=gyPccToGw'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="pccToGWGyMappingTable" class="table table-blue table-bordered" width="100%">
                    <caption class="caption-header"><s:text name="diameter.profile.pcrf.gateway.mapping" />
                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="diameter.profile.packetmapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="pccToGWGyPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td>
                                <s:property value="%{condition}"></s:property>
                            </td class="min-width">
                        </tr>
                    </s:iterator>
                </table>
            </fieldset>
            <!--Rx Packet Mappings -->
            <fieldset class="fieldSet-line">
                <legend><s:text name="diamter.profile.rx.packet.mapping"/></legend>
                <s:set var="gwToPccRxPacketMappingList" value="gwToPccRxPacketMappings"/>
                <s:if test="%{#gwToPccRxPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrder?type=rxGatewayToPCC'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="gatewayToPccRxMappingTable" class="table table-blue table-bordered" width="100%">
                    <caption class="caption-header"><s:text name="diameter.profile.gateway.pcrf.packet.mapping" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="diameter.profile.packetmapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="gwToPccRxPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
                <s:set var="pccToGWRxPacketMappingList" value="pccToGWRxPacketMappings"/>
                <s:if test="%{#pccToGWRxPacketMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrder?type=rxPccToGw'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="pccToGWRxMappingTable" class="table table-blue table-bordered" width="100%">
                    <caption class="caption-header"><s:text name="diameter.profile.pcrf.gateway.mapping" />
                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="diameter.profile.packetmapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="pccToGWRxPacketMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/${packetMappingData.id}"><s:property value="%{packetMappingData.name}"></s:property></a>
                            </td>
                            <td class="min-width">
                                <s:property value="%{condition}"></s:property>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
            </fieldset>
            <!-- PCC Rule Mapping configuration -->
            <fieldset class="fieldSet-line">
                <legend><s:text name="diameter.profile.pcc.rule.mapping"/></legend>
                <s:set var="diameterGwProfilePCCRuleMappingList" value="diameterGwProfilePCCRuleMappings"/>
                <s:if test="%{#diameterGwProfilePCCRuleMappingList.size() <= 1}">
                    <button class="btn btn-primary btn-xs" disabled
                            style="float:right;padding-top: 3px; padding-bottom: 3px">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrderPccMapping'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="pccRuleMappingTable" class="table table-blue table-bordered" width="100%">
                    <caption class="caption-header"><s:text name="diameter.profile.pcc.rule.mapping" />

                    </caption>
                    <thead>
                    <tr>
                        <th>
                            <s:text name="diameter.profile.packetmapping.ordernumber"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.pccrule.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.pccrule.mapping.condition"/>
                        </th>
                    </tr>
                    </thead>

                    <s:iterator value="diameterGwProfilePCCRuleMappingList">
                        <tr>
                            <td>
                                <s:property value="%{orderNumber}"></s:property>
                            </td>
                            <td class="min-width">
                                <a href="${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/${pccRuleMappingData.id}"><s:property value="%{pccRuleMappingData.name}"></s:property></a>
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
                <legend><s:text name="diameter.profile.service.guiding"/></legend>
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
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrderForServiceGuiding'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
                <table id="groovyScriptTable" class="table table-blue table-bordered" width="100%">
                   <caption class="caption-header"><s:text name="diameter.profile.service.guiding" />

                   </caption>
                   <thead>
                   <tr>
                       <th>
                           <s:text name="diameter.profile.packetmapping.ordernumber"/>
                       </th>
                       <th class="min-width">
                           <s:text name="diameter.profile.service.guiding.name"/>
                       </th>
                       <th class="min-width">
                           <s:text name="diameter.profile.service.guiding.condition"/>
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
                <legend><s:text name="diameter.profile.groovy.script.mapping"/></legend>
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
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/initManageOrderForGroovy'">
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
                            <nv:dataTableColumn title="Order No" beanProperty="orderNumber"/>
                            <nv:dataTableColumn title="ScriptName" beanProperty="scriptName"
                                                tdCssClass="text-left text-middle min-width"/>
                            <nv:dataTableColumn title="Argument" beanProperty="argument"
                                                tdCssClass="text-left text-middle min-width"/>
                        </nv:dataTable>
                    </div>

                </div>

            </fieldset>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text
                            name="button.list"/>
                    </button>
                </div>
            </div>
        </div>

    </div>
    </div>
</div>
