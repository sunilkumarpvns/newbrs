<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<style>
    .removePadding{
        display: inline;
    }
    .wrap-word{
        word-break: break-all;
        width: 20%;
    }
    .inActiveService {
        color:red;
    }

</style>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="diameter.profile.update" />
        </h3>
    </div>

    <div class="panel-body">

        <s:form namespace="/sm/gatewayprofile" id="diameterGatewayProfileUpdate" action="diameter-gateway-profile"
                method="post" cssClass="form-horizontal" labelCssClass="col-xs-12 col-sm-4 col-lg-3"
                elementCssClass="col-xs-12 col-sm-8 col-lg-9" validate="true" validator="validateForm()">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <div class="row">
                <div class="col-xs-12 col-sm-6 col-lg-6">
                    <s:textfield name="name"
                                 key="diameter.profile.name" id="name"
                                 cssClass="form-control focusElement"
                                 maxlength="255"
                                 tabindex="1"/>

                    <s:textarea name="description"
                                key="diameter.profile.description"
                                cssClass="form-control"
                                cssStyle="height:60px;"
                                tabindex="2"/>
                    <s:select name="status" key="diameter.profile.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="resourceStatus"	 tabindex="4" />
                    <s:select name="gatewayType" key="diameter.profile.gatewaytype" listKey="name()" listValue="getValue()" cssClass="form-control"	list="@com.elitecore.corenetvertex.constants.GatewayComponent@values()" id="gatewayType" tabindex="5" onchange="enableSessionLookUpKey()"/>
                </div>

                <div class="col-xs-12 col-sm-6 col-lg-6">
                    <s:select name="vendorId" key="diameter.profile.vendor"
                              cssClass="form-control"
                              list="%{vendorDataList}"
                              listKey="id" listValue="name" tabindex="6"/>

                    <s:textfield name="firmware"
                                 id="firmware"
                                 key="diameter.profile.firmware"
                                 cssClass="form-control"
                                 tabindex="7" maxlength="60" />

                    <s:select name="usageReportingType"
                              key="diameter.profile.usagereportingtype"
                              cssClass="form-control"
                              listKey="name()" listValue="getValue()" list="@com.elitecore.corenetvertex.constants.UsageReportingType@values()"
                              tabindex="8" />
                    <s:select name="revalidationMode"
                              key="diameter.profile.revalidationmode"
                              cssClass="form-control"
                              listKey="name()" listValue="getValue()" list="@com.elitecore.corenetvertex.constants.RevalidationMode@values()"
                              tabindex="9" />
                </div>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="diameter.profile.connection.parameter"/> </legend>
                    <div class="row">

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield name="timeout"  key="diameter.profile.timeout"   type="number"         cssClass="form-control" maxlength="10" tabindex="10"/>
                            <div class="form-group">
                                <label  class="col-xs-12 col-sm-4 col-lg-3 control-label">Session Cleanup On </label>
                                <div class="col-xs-12 col-sm-8 col-lg-9 controls">
                                    <s:checkbox name="sessionCleanUpCER" id="sessionCleanupOnCER" theme="simple"  tabindex="11" /><s:text name="diameter.profile.sessioncleanupcer" />
                                    <s:checkbox name="sessionCleanUpDPR" id="sessionCleanupOnDPR" theme="simple"  tabindex="11" /><s:text name="diameter.profile.sessioncleanupdpr" />
                                </div>
                            </div>

                            <s:textarea name="cerAvps"
                                        key="diameter.profile.ceravps"
                                        cssClass="form-control"
                                        cssStyle="height:60px;"
                                        tabindex="12"/>
                            <s:textarea name="dprAvps"
                                        key="diameter.profile.dpravps"
                                        cssClass="form-control"
                                        cssStyle="height:60px;"
                                        tabindex="13"/>
                            <s:textarea name="dwrAvps"
                                        key="diameter.profile.dwravps"
                                        cssClass="form-control"
                                        cssStyle="height:60px;"
                                        tabindex="14"/>

                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:select 	id="sendDPRCloseEvent"	name="sendDPRCloseEvent" key="diameter.profile.senddprcloseevent" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
                                         tabindex="15" />
                            <s:textfield name="socketReceiveBufferSize"  key="diameter.profile.socketreceivebuffersize" type="number" cssClass="form-control" maxlength="10" tabindex="16" />
                            <s:textfield name="socketSendBufferSize"  key="diameter.profile.socketsendbuffersize" type="number" cssClass="form-control" maxlength="10" tabindex="17" />
                            <s:select 	id="tcpNagleAlgorithm"	name="tcpNagleAlgorithm" key="diameter.profile.tcpnaglealgorithm" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
                                         tabindex="18" />
                            <s:textfield name="dwInterval"  key="diameter.profile.dwrduration" type="number" cssClass="form-control" maxlength="10" tabindex="19" />
                            <s:textfield name="initConnectionDuration" key="diameter.profile.initconnectionduration" type="number" cssClass="form-control" maxlength="10" tabindex="20" />
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="diameter.profile.application.parameter"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield name="gxApplicationId"  key="diameter.profile.customgxapplicationid"  cssClass="form-control"  tabindex="21" maxlength="60" />
                            <s:textfield name="gyApplicationId"  key="diameter.profile.customgyapplicationid"  cssClass="form-control" tabindex="22" maxlength="60" />
                            <s:textfield name="rxApplicationId"  key="diameter.profile.customrxapplicationid"  cssClass="form-control"  tabindex="23" maxlength="60" />
                            <s:textfield name="s9ApplicationId"  key="diameter.profile.customs9applicationid"  cssClass="form-control" tabindex="24" maxlength="60" />
                            <s:textfield name="syApplicationId"  key="diameter.profile.customsyapplicationid"  cssClass="form-control"  tabindex="25" maxlength="60" />
                        </div>
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:select name="chargingRuleInstallMode" key="diameter.profile.chargingruleinstallmode" cssClass="form-control"	list="@com.elitecore.corenetvertex.constants.ChargingRuleInstallMode@values()" listKey="name()" listValue="val" id="chargingRuleInstallMode" tabindex="26"/>
                            <s:select name="supportedStandard" key="diameter.profile.supportedstandard" cssClass="form-control"	list="@com.elitecore.corenetvertex.constants.SupportedStandard@values()" id="supportedStandard" listKey="name()" listValue="getName()" tabindex="27"/>
                            <s:select name="umStandard" key="diameter.profile.umstandard" cssClass="form-control"	list="@com.elitecore.corenetvertex.constants.UMStandard@values()" listKey="name()" listValue="displayValue" id="umStandard" tabindex="28"/>
                            <s:select multiple="true" value="supportedStandardValuesForUpdate" list="supportedVendorDataList" name="supportedVendorList"
                                      key="diameter.profile.supportedvendorlist"
                                      cssClass="form-control select2" cssStyle="width:100%"
                                      tabindex="29"/>
                            <div id="displaySessionLookUpKey" style="visibility:hidden;">
                            <s:select multiple="true" value="sessionKeysForUpdate" list="sessionKeyList" name="sessionLookUpKey"
                                      key="diameter.profile.sessionlookupkey" id="sessionLookUpKeyId"
                                      cssClass="form-control select2" cssStyle="width:100%;"
                                      tabindex="30"/>
                            </div>
                            </div>
                        </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="diamter.profile.gx.packet.mapping"/></legend>
                    <table id="gatewayToPccGxMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.gateway.pcrf.packet.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGatewayToPCCGatewayMapping('gatewayToPccGxMappingTable','GX');" id="addGatewayToPCRFGatewayRowGx"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="gwToPccGxPacketMappings" status="i" var="gatewayToPccGxMapping">
                            <tr>
                                <td style="min-width: 50%;">

                                    <s:select list="gatewayToPCCPacketMappingGxList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#gatewayToPccGxMapping.packetMappingId}"
                                              name="gwToPccGxPacketMappings[%{#i.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>

                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="gwToPccGxPacketMappings[%{#i.count - 1}].condition" value="%{#gatewayToPccGxMapping.condition}" id="gatewayToPCCGX%{#i.count - 1}" />

                                </td>
                                <td style="display:none;">
                                    <s:hidden name="gwToPccGxPacketMappings[%{#i.count - 1}].applicationType" value="%{#gatewayToPccGxMapping.applicationType}" />
                                </td>
                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                    <table id="pccToGatewayGxMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.pcrf.gateway.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCToGatewayMapping('pccToGatewayGxMappingTable','GX');" id="addPCCToGatewayRowGx"><span
                                        class="glyphicon glyphicon-plus"> </span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="pccToGWGxPacketMappings" status="j" var="pccToGatewayGxMapping">
                            <tr>
                                <td style="min-width: 50%;"><s:select list="pccToGatewayPacketMappingGxList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#pccToGatewayGxMapping.packetMappingId}"
                                              name="pccToGWGxPacketMappings[%{#j.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>
                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"
                                               name="pccToGWGxPacketMappings[%{#j.count - 1}].condition" value="%{#pccToGatewayGxMapping.condition}" id="pccToGatewayGX%{#j.count - 1}" />
                                </td>
                                <td style="display:none;">
                                    <s:hidden name="pccToGWGxPacketMappings[%{#j.count - 1}].applicationType" value="%{#pccToGatewayGxMapping.applicationType}" />
                                </td>
                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                </fieldset>

                <!-- Gy Packet Mappings -->
                <fieldset class="fieldSet-line">
                    <legend><s:text name="diamter.profile.gy.packet.mapping"/></legend>
                    <table id="gatewayToPccGyMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.gateway.pcrf.packet.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGatewayToPCCGatewayMapping('gatewayToPccGyMappingTable','GY');" id="addGatewayToPCRFGatewayRowGy"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="gwToPccGyPacketMappings" status="i" var="gatewayToPccGyMapping">
                            <tr>
                                <td style="min-width: 50%;">
                                    <s:select list="gatewayToPCCPacketMappingGyList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#gatewayToPccGyMapping.packetMappingId}"
                                              name="gwToPccGyPacketMappings[%{#i.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>

                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="gwToPccGyPacketMappings[%{#i.count - 1}].condition" value="%{#gatewayToPccGyMapping.condition}" id="gatewayToPCCGY%{#i.count - 1}" />

                                </td>
                                <td style="display:none;">
                                    <s:hidden name="gwToPccGyPacketMappings[%{#i.count - 1}].applicationType" value="%{#gatewayToPccGyMapping.applicationType}" />
                                </td>
                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                    <table id="pccToGatewayGyMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.pcrf.gateway.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCToGatewayMapping('pccToGatewayGyMappingTable','GY');" id="addPCCToGatewayRowGy"><span
                                        class="glyphicon glyphicon-plus"> </span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="pccToGWGyPacketMappings" status="j" var="pccToGatewayGyMapping">
                            <tr>
                                <td style="min-width: 50%;"><s:select list="pccToGatewayPacketMappingGxList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#pccToGatewayGyMapping.packetMappingId}"
                                              name="pccToGWGyPacketMappings[%{#j.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>
                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="pccToGWGyPacketMappings[%{#j.count - 1}].condition" value="%{#pccToGatewayGyMapping.condition}" id="pccToGatewayGY%{#j.count - 1}" />
                                </td>
                                <td style="display:none;">
                                    <s:hidden name="pccToGWGyPacketMappings[%{#j.count - 1}].applicationType" value="%{#pccToGatewayGyMapping.applicationType}" />
                                </td>
                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                </fieldset>



                <!-- Rx Packet Mappings -->
                <fieldset class="fieldSet-line">
                    <legend><s:text name="diamter.profile.rx.packet.mapping"/></legend>
                    <table id="gatewayToPccRxMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.gateway.pcrf.packet.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGatewayToPCCGatewayMapping('gatewayToPccRxMappingTable','RX');" id="addGatewayToPCRFGatewayRowRx"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="gwToPccRxPacketMappings" status="i" var="gatewayToPccRxMapping">
                            <tr>
                                <td style="min-width: 50%;">

                                    <s:select list="gatewayToPCCPacketMappingRxList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#gatewayToPccRxMapping.packetMappingId}"
                                              name="gwToPccRxPacketMappings[%{#i.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>

                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="gwToPccRxPacketMappings[%{#i.count - 1}].condition" value="%{#gatewayToPccRxMapping.condition}" id="gatewayToPCCRX%{#i.count - 1}" />

                                </td>
                                <td style="display:none;">
                                    <s:hidden name="gwToPccRxPacketMappings[%{#i.count - 1}].applicationType" value="%{#gatewayToPccRxMapping.applicationType}" />
                                </td>
                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                    <table id="pccToGatewayRxMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.pcrf.gateway.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCToGatewayMapping('pccToGatewayRxMappingTable','RX');" id="addPCCToGatewayRowRx"><span
                                        class="glyphicon glyphicon-plus"> </span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="pccToGWRxPacketMappings" status="j" var="pccToGatewayRxMapping">
                            <tr>
                                <td style="min-width: 50%;"><s:select list="pccToGatewayPacketMappingRxList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#pccToGatewayRxMapping.packetMappingId}"
                                              name="pccToGWRxPacketMappings[%{#j.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>
                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="pccToGWRxPacketMappings[%{#j.count - 1}].condition" value="%{#pccToGatewayRxMapping.condition}" id="pccToGatewayRX%{#j.count - 1}" />
                                </td>
                                <td style="display:none;">
                                    <s:hidden name="pccToGWRxPacketMappings[%{#j.count - 1}].applicationType" value="%{#pccToGatewayRxMapping.applicationType}" />
                                </td>

                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                </fieldset>
            </div>
            <!-- pcc rule mapping configuration -->
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="diameter.profile.pcc.rule.mapping"/></legend>
                    <table id="pccRuleMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.pcc.rule.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCRuleMapping('diameterGwProfilePCCRuleMappings');" id="addPCCRuleMapping"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.pccrule.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.pccrule.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="diameterGwProfilePCCRuleMappings" status="i" var="pccRuleMapping">
                            <tr>
                                <td style="min-width: 50%;">

                                    <s:select list="pccRuleMappings" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#pccRuleMapping.pccRuleMappingId}"
                                              name="diameterGwProfilePCCRuleMappings[%{#i.count - 1}].pccRuleMappingId"
                                              elementCssClass="col-xs-12"/>

                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control condition" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="diameterGwProfilePCCRuleMappings[%{#i.count - 1}].condition" value="%{#pccRuleMapping.condition}" id="condition%{#i.count - 1}" />

                                </td>
                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                </fieldset>
            </div>


            <!-- Service guiding configuration -->
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="diameter.profile.service.guiding"/></legend>
                    <table id="serviceGuidingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="diameter.profile.service.guiding"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addServiceGuiding();" id="addServiceGuiding"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.service.guiding.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.service.guiding.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="serviceGuidingDatas" status="i" var="serviceGuiding">
                            <s:if test="%{#serviceGuiding.serviceData.status != 'INACTIVE'}">
                                <tr>
                                    <td style="min-width: 50%;">

                                        <s:select list="serviceDatas" listKey="id" listValue="name"
                                                  cssClass="form-control" value="%{#serviceGuiding.serviceId}"
                                                  name="serviceGuidingDatas[%{#i.count - 1}].serviceId"
                                                  elementCssClass="col-xs-12"/>

                                    </td>
                                    <td style="min-width: 50%;">
                                        <s:textfield cssClass="form-control condition" elementCssClass="col-xs-12"
                                                     maxLength="4000"
                                                     name="serviceGuidingDatas[%{#i.count - 1}].condition"
                                                     value="%{#serviceGuiding.condition}"
                                                     id="condition%{#i.count - 1}"/>

                                    </td>
                                    <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                    </td>
                                </tr>
                            </s:if>
                            <s:else>
                                <tr>
                                    <td style="min-width: 50%;">

                                        <s:select list="serviceDatas" listKey="id" listValue="name"
                                                  cssClass="form-control inActiveService" value="%{#serviceGuiding.serviceId}"
                                                  name="serviceGuidingDatas[%{#i.count - 1}].serviceId"
                                                  elementCssClass="col-xs-12" onchange="removeCss(this);"/>

                                    </td>
                                    <td style="min-width: 50%;">
                                        <s:textfield cssClass="form-control condition inActiveService" elementCssClass="col-xs-12"
                                                     maxLength="4000"
                                                     name="serviceGuidingDatas[%{#i.count - 1}].condition"
                                                     value="%{#serviceGuiding.condition}"
                                                     id="condition%{#i.count - 1}"/>

                                    </td>
                                    <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                    </td>
                                </tr>
                            </s:else>
                        </s:iterator>
                        </tbody>
                    </table>
                    <div id="message">
                        <strong>Note :- </strong> Records mark as red indicates InActive Services
                    </div>
                </fieldset>
            </div>

            <!-- groovy Script configuration -->
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="diameter.profile.groovy.script.mapping"/></legend>
                    <table id="groovyScriptTable" class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="diameter.profile.groovy.script.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGroovyScript();" id="addGroovyRow"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="diameter.profile.groovy.script.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="diameter.profile.groovy.script.argument"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="groovyScriptDatas" status="i" var="groovyScript">
                            <tr>
                                <td style="min-width: 50%;">

                                    <s:textfield  cssClass="form-control" elementCssClass="col-xs-12" maxLength="2048"
                                                  name="groovyScriptDatas[%{#i.count - 1}].scriptName" value="%{#groovyScript.scriptName}" id="scriptName%{#i.count - 1}" />

                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control condition" elementCssClass="col-xs-12" maxLength="2048"
                                                  name="groovyScriptDatas[%{#i.count - 1}].argument" value="%{#groovyScript.argument}" id="argument%{#i.count - 1}" />

                                </td>
                                <td style='width:35px;'>
                                    <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                                        <a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a>
                                    </span>
                                </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                </fieldset>
            </div>
            <div class="col-xs-12" id="generalError"></div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button"
                            formaction="${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}" tabindex="31"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="32"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/>
                    </button>
                </div>
            </div>

        </s:form>

    </div>
</div>
<script type="text/javascript">
    function validateForm(){
        var isValidName = verifyUniquenessOnSubmit('name', 'update', '<s:text name="id"/>', 'com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData', '', '');

        var gatewayType = $("#gatewayType").val();
        var isValidSessionLookUpKey = true;
        if((gatewayType == "<s:property value="@com.elitecore.corenetvertex.constants.GatewayComponent@APPLICATION_FUNCTION.name()" />" || gatewayType == "<s:property value="@com.elitecore.corenetvertex.constants.GatewayComponent@DRA.name()" />") && isNullOrEmpty($("#sessionLookUpKeyId").val())){
            setError('sessionLookUpKeyId', '<s:text name="error.required.sessionlookupkey"/>');
            isValidSessionLookUpKey = false;
        }
        return isValidName && isValidSessionLookUpKey && validateMapping() && validatePccMappings() && validateGroovyScriptDatas() && validateServiceGuidingDatas();
    }
    $(function(){
        $( ".select2" ).select2({
                tags:"true"
            }
        );
        enableSessionLookUpKey();
        setConditionalSuggestions('pccToGateway',${advanceConditions});
        setConditionalSuggestions('gatewayToPCC',${suggestionsGatewayToPCC});
        setConditionSuggestionsForPCCRule();
    });
    function enableSessionLookUpKey(){
        var gatewayType = $("#gatewayType").val();
        if(gatewayType == "<s:property value="@com.elitecore.corenetvertex.constants.GatewayComponent@APPLICATION_FUNCTION.name()" />" || gatewayType == "<s:property value="@com.elitecore.corenetvertex.constants.GatewayComponent@DRA.name()" />" ){
            $("#displaySessionLookUpKey").attr("style","visibility:visible");
        }else{
            $("#displaySessionLookUpKey").attr("style","visibility:hidden");
        }
    }



</script>

<%@include file="diameter-gateway-profile-packet-mapping-utility.jsp"%>
<%@include file="gateway-profile-pcc-mapping-utility.jsp"%>
<%@include file="gateway-profile-groovy-script-utility.jsp"%>
<%@include file="gateway-profile-service-guiding-utility.jsp"%>