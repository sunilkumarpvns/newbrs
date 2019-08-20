<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<style>
    .inActiveService {
        color:red;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="radius.profile.update" />
        </h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/sm/gatewayprofile" id="radiusGatewayProfileUpdate" action="radius-gateway-profile" method="post" cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                validator="validateForm()">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="radius.profile.name" id="name"
                                 cssClass="form-control focusElement"
                                 tabindex="1"/>
                    <s:textarea name="description"
                                key="radius.profile.description"
                                cssClass="form-control"
                                cssStyle="height:60px;"
                                tabindex="2"/>
                    <s:select name="status" key="radius.profile.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="resourceStatus"	 tabindex="4" />
                    <s:select name="gatewayType" key="radius.profile.gatewaytype" listKey="name()" listValue="getValue()" cssClass="form-control"	list="@com.elitecore.corenetvertex.constants.GatewayComponent@values()" id="gatewayType" tabindex="5"/>
                </div>
                <div class="col-xs-12 col-sm-6 col-lg-6">
                    <s:select name="vendorId" key="radius.profile.vendor"
                              cssClass="form-control"
                              list="%{vendorDataList}"
                              listKey="id" listValue="name" tabindex="6"/>
                    <s:textfield name="firmware"
                                 id="firmware"
                                 key="radius.profile.firmware"
                                 cssClass="form-control"
                                 tabindex="7" maxlength="60" />

                    <s:select name="usageReportingType"
                              key="radius.profile.usagereportingtype"
                              cssClass="form-control"
                              listKey="name()" listValue="getValue()" list="@com.elitecore.corenetvertex.constants.UsageReportingType@values()"
                              tabindex="8" />
                    <s:select name="revalidationMode"
                              key="radius.profile.revalidationmode"
                              cssClass="form-control"
                              listKey="name()" listValue="getValue()" list="@com.elitecore.corenetvertex.constants.RevalidationMode@values()"
                              tabindex="9" />

                </div>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="radius.profile.connection.parameter"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield name="timeout"  key="radius.profile.timeout"    onkeypress="return isNaturalInteger(event);"      cssClass="form-control" maxlength="5" tabindex="10"/>
                            <s:textfield name="maxRequestTimeout" key="radius.profile.maxrequesttimeout" onkeypress="return isNaturalInteger(event);"  cssClass="form-control" maxlength="5" tabindex="11" />
                            <s:textfield name="retryCount"  key="radius.profile.retrycount"  onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="8" tabindex="12" />
                            <s:textfield name="statusCheckDuration"  key="radius.profile.statuscheckduration" onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="8" tabindex="13" />

                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield name="interimInterval"  key="radius.profile.interim"  onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="4" tabindex="14" />
                            <s:select 	id="icmpPingEnable"	name="icmpPingEnabled" key="radius.profile.icmppingenable" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
                                         tabindex="15" />
                            <s:select 	id="sendAccountingResponse"	name="sendAccountingResponse" key="radius.profile.sendaccountingresponse" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
                                          tabindex="16" />
                            <s:select multiple="true" value="supportedStandardValuesForUpdate" list="supportedVendorDataList" name="supportedVendorList"
                                      key="radius.profile.supportedvendorlist"
                                      cssClass="form-control select2" cssStyle="width:100%"
                                      tabindex="17"/>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="radius.profile.packet.mapping"/></legend>
                    <table id="gatewayToPCCMappingTable" class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="radius.profile.gateway.pcrf.packet.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGatewayToPCCMapping();" id="addGatewayToPCRFGatewayRow"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="gwToPCCPacketMappings" status="i" var="gatewayToPCCMapping">
                            <tr>
                                <td>

                                    <s:select list="gatewayToPCCPacketMappingDataList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#gatewayToPCCMapping.packetMappingId}"
                                              name="gwToPCCPacketMappings[%{#i.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>

                                </td>
                                <td>
                                    <s:textfield  cssClass="form-control gatewayToPCC" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="gwToPCCPacketMappings[%{#i.count - 1}].condition" value="%{#gatewayToPCCMapping.condition}" id="gatewayToPCC%{#i.count -1}" />

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
                    <table id="pccToGatewayMappingTable" class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="radius.profile.pcrf.gateway.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCToGatewayMapping();" id="addPCRFToGatewayRow"><span
                                        class="glyphicon glyphicon-plus"> </span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.packet.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="pccToGWPacketMappings" status="j" var="pccToGatewayMapping">
                            <tr>
                                <td><s:select list="pccToGatewayPacketMappingDataList" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#pccToGatewayMapping.packetMappingId}"
                                              name="pccToGWPacketMappings[%{#j.count - 1}].packetMappingId"
                                              elementCssClass="col-xs-12"/>
                                </td>
                                <td>
                                    <s:textfield  cssClass="form-control pccToGateway" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="pccToGWPacketMappings[%{#j.count - 1}].condition" value="%{#pccToGatewayMapping.condition}" id="gatewayToPCC%{#i.count -1}" />
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
                    <legend><s:text name="radius.profile.pcc.rule.mapping"/></legend>
                    <table id="pccRuleMappingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="radius.profile.pcc.rule.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCRuleMapping('radiusGwProfilePCCRuleMappings');" id="addPCCRuleMapping"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="radius.profile.pccrule.mapping.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.pccrule.mapping.condition"/>
                        </th>
                        <th style='width:35px;'></th>
                        </thead>
                        <tbody>
                        <s:iterator value="radiusGwProfilePCCRuleMappings" status="i" var="pccRuleMapping">
                            <tr>
                                <td style="min-width: 50%;">

                                    <s:select list="pccRuleMappings" listKey="id" listValue="name"
                                              cssClass="form-control" value="%{#pccRuleMapping.pccRuleMappingId}"
                                              name="radiusGwProfilePCCRuleMappings[%{#i.count - 1}].pccRuleMappingId"
                                              elementCssClass="col-xs-12"/>

                                </td>
                                <td style="min-width: 50%;">
                                    <s:textfield  cssClass="form-control condition" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="radiusGwProfilePCCRuleMappings[%{#i.count - 1}].condition" value="%{#pccRuleMapping.condition}" id="condition%{#i.count - 1}"  />

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

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="radius.profile.service.guiding"/></legend>
                    <table id="serviceGuidingTable" class="table table-blue table-bordered" width="100%">
                        <caption class="caption-header"><s:text name="radius.profile.service.guiding"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addServiceGuiding();" id="addServiceGuiding"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="radius.profile.service.guiding.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.service.guiding.condition"/>
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
                                    <s:textfield  cssClass="form-control condition" elementCssClass="col-xs-12" maxLength="4000"
                                                  name="serviceGuidingDatas[%{#i.count - 1}].condition" value="%{#serviceGuiding.condition}" id="condition%{#i.count - 1}" />

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

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="radius.profile.groovy.script.mapping"/></legend>
                    <table id="groovyScriptTable" class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="radius.profile.groovy.script.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGroovyScript();" id="addGroovyRow"> <span
                                        class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width">
                            <s:text name="radius.profile.groovy.script.name"/>
                        </th>
                        <th class="min-width">
                            <s:text name="radius.profile.groovy.script.argument"/>
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
                            formaction="${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}" tabindex="17"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="18"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/${id}'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/>
                    </button>
                </div>
            </div>

        </s:form>

    </div>
</div>
<script type="text/javascript">
    function validateForm() {
        var isValidName = verifyUniquenessOnSubmit('name', 'update', '<s:text name="id"/>', 'com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData', '', '');

        return isValidName && validateMapping() && validatePccMappings() && validateGroovyScriptDatas() && validateServiceGuidingDatas();
    }
    $( ".select2" ).select2({
            tags:"true"
        }
    );
    $(function(){
        setConditionalSuggestions('pccToGateway',${advanceConditions});
        setConditionalSuggestions('gatewayToPCC',${suggestionsGatewayToPCC});
        setConditionSuggestionsForPCCRule();
    });
</script>

<%@include file="gateway-profile-packet-mapping-utility.jsp"%>
<%@include file="gateway-profile-pcc-mapping-utility.jsp"%>
<%@include file="gateway-profile-groovy-script-utility.jsp"%>
<%@include file="gateway-profile-service-guiding-utility.jsp"%>