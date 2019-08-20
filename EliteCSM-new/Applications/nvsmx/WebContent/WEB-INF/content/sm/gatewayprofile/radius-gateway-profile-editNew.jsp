<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="/view/commons/general/AutoCompleteIncludes.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="radius.profile.create" />
        </h3>
    </div>

    <div class="panel-body">

        <s:form namespace="/sm/gatewayprofile" id="radiusGatewayProfileCreate" action="radius-gateway-profile"
                method="post" cssClass="form-horizontal" labelCssClass="col-xs-12 col-sm-4 col-lg-3"
                elementCssClass="col-xs-12 col-sm-8 col-lg-9" validate="true" validator="validateForm()">
            <s:token/>


            <div class="row">
                <div class="col-xs-12 col-sm-6 col-lg-6">
                    <s:textfield name="name"
                                 key="radius.profile.name" id="name"
                                 cssClass="form-control focusElement"
                                 maxlength="255"
                                 tabindex="1"/>

                    <s:textarea name="description"
                                key="radius.profile.description"
                                cssClass="form-control"
                                cssStyle="height:60px;"
                                tabindex="2" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"/>

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
                            <s:textfield name="timeout" value="1000"  key="radius.profile.timeout"   onkeypress="return isNaturalInteger(event);"      cssClass="form-control" maxlength="5" tabindex="10"/>
                            <s:textfield name="maxRequestTimeout" value="100" key="radius.profile.maxrequesttimeout"  onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="5" tabindex="11" />
                            <s:textfield name="retryCount" value="10" key="radius.profile.retrycount" cssClass="form-control" onkeypress="return isNaturalInteger(event);" maxlength="8" tabindex="12" />
                            <s:textfield name="statusCheckDuration" value="120" key="radius.profile.statuscheckduration"  onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="8" tabindex="13" />

                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:textfield name="interimInterval" value="60" key="radius.profile.interim"  onkeypress="return isNaturalInteger(event);" cssClass="form-control" maxlength="4" tabindex="14" />
                            <s:select 	id="icmpPingEnable"	name="icmpPingEnabled" key="radius.profile.icmppingenable" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
                                         value="@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()" tabindex="16" />
                            <s:select 	id="sendAccountingResponse"	name="sendAccountingResponse" key="radius.profile.sendaccountingresponse" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
                                         value="@com.elitecore.corenetvertex.constants.CommonStatusValues@ENABLE.isBooleanValue()" tabindex="16" />
                            <s:select multiple="true" list="supportedVendorDataList" name="supportedVendorList"
                                      key="radius.profile.supportedvendorlist"
                                      cssClass="form-control select2" cssStyle="width:100%"
                                      tabindex="16"/>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="radius.profile.packet.mapping"/></legend>
                    <table id='gatewayToPCCMappingTable'  class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="radius.profile.gateway.pcrf.packet.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addGatewayToPCCMapping()"  id="addGatewayToPCRFGatewayRow"> <span
                                        class="glyphicon glyphicon-plus"> </span></span>
                            </div>
                        </caption>
                                <thead>
                                <th class="min-width"><s:text name="radius.profile.packet.mapping.name"/></th>
                                <th class="min-width"><s:text name="radius.profile.packet.mapping.condition"/></th>
                                <th style="width:35px;">&nbsp;</th>
                                </thead>
                                <tbody>
                                </tbody>
                    </table>


                    <table id='pccToGatewayMappingTable'  class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="radius.profile.pcrf.gateway.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCToGatewayMapping()"  id="addPCRFToGatewayRow"> <span
                                        class="glyphicon glyphicon-plus"> </span></span>
                            </div>
                        </caption>
                        <thead>
                        <th class="min-width"><s:text name="radius.profile.packet.mapping.name"/></th>
                        <th class="min-width"><s:text name="radius.profile.packet.mapping.condition"/></th>
                        <th style="width:35px;">&nbsp;</th>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </fieldset>
            </div>

            <!-- PCC Rule Mapping -->
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="radius.profile.pcc.rule.mapping"/></legend>
                    <table id="pccRuleMappingTable" class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="radius.profile.pcc.rule.mapping"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addPCCRuleMapping('radiusGwProfilePCCRuleMappings');" id="addPCCMappingRow"> <span
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
                        </tbody>
                    </table>
                </fieldset>
            </div>

            <!-- Service Guiding -->
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend><s:text name="radius.profile.service.guiding"/></legend>
                    <table id="serviceGuidingTable" class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="radius.profile.service.guiding"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn"
                                      onclick="addServiceGuiding();" id="addServiceGuidingRow"> <span
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
                        </tbody>
                    </table>
                </fieldset>
            </div>

                <!-- groovy script configuration -->
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
                            </tbody>
                        </table>
                    </fieldset>
                </div>
            <div class="col-xs-12" id="generalError"></div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="17" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="18"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>

        </s:form>

    </div>
</div>
<script type="text/javascript">

    $(function(){
        $( ".select2" ).select2();

    });

    function validateForm(){
        var isValidName = verifyUniquenessOnSubmit('name','create','','com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData','','');
        return isValidName  && validateMapping() && validatePccMappings() && validateGroovyScriptDatas() && validateServiceGuidingDatas();
    }

</script>

<%@include file="gateway-profile-packet-mapping-utility.jsp"%>
<%@include file="gateway-profile-pcc-mapping-utility.jsp"%>
<%@include file="gateway-profile-groovy-script-utility.jsp"%>
<%@include file="gateway-profile-service-guiding-utility.jsp"%>

