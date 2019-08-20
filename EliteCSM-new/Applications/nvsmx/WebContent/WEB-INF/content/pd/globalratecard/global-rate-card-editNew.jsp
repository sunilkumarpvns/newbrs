<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="ratecard.create"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/globalratecard" action="global-rate-card" id="globalRateCard" method="post" validate="true"
                cssClass="form-horizontal" labelCssClass="col-xs-12 col-sm-4"
                elementCssClass="col-xs-12 col-sm-8" validator="validate()">
            <s:token/>
            <s:hidden name="scope" value="GLOBAL"/>
            <s:hidden name="type" value="MONETARY"/>
            <s:hidden name="currency" id="currency" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>
            <div class="row">
                <div class="col-sm-6">
                    <s:textfield name="name" key="ratecard.name" id="rateCardName" cssClass="form-control focusElement"
                                 maxlength="100" tabindex="1"/>
                    <s:textarea name="description" key="ratecard.description" id="ratecardDescription"
                                cssClass="form-control" rows="2"
                                value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"
                                maxlength="2000" tabindex="2"/>
                    <s:select name="groups" id="groupNames" key="ratecard.groups" cssClass="form-control select2" list="#session.staffBelongingGroups" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3"/>
                    <s:set value="%{chargingType == @com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}" var="isEventBasedPackage"/>
                    <s:select name="chargingType" key="ratecard.chargingtype" list="@com.elitecore.corenetvertex.pkg.ChargingType@values()" id="chargingType" cssClass="form-control" tabindex="4"/>
                    <s:select name="currencyList" key="ratecard.currency" list="@java.util.Currency@getAvailableCurrencies()" listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" id="currencyList" cssClass="form-control" tabindex="5" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()" disabled="!@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()" onchange="updateCurrencyLabel()" />
                </div>
                <div class="col-xs-6">
                    <div id = "ratePulseUnit">
                        <s:select name="monetaryRateCardData.pulseUnit" key="ratecard.pulseUnit" id="pulseUom" cssClass="form-control"
                                  list="pulseUnits" tabindex="6"/>
                        <s:select name="monetaryRateCardData.rateUnit" key="ratecard.rateUnit"  list="{}" id="rateUom"  cssClass="form-control"
                                  tabindex="7"/>
                    </div>
                    <s:textfield name="monetaryRateCardData.labelKey1" key="ratecard.labelOne" id="labelKey1" cssClass="form-control pcrf-key-suggestions"
                                 maxlength="100" tabindex="8" onblur="updateLabelsOnBlur('#versionDetailLabelKey1','#labelKey1');" />
                    <s:textfield name="monetaryRateCardData.labelKey2" key="ratecard.labelTwo" id="labelKey2" cssClass="form-control pcrf-key-suggestions"
                                 maxlength="100" tabindex="9" onblur="updateLabelsOnBlur('#versionDetailLabelKey2','#labelKey2');" />
                </div>
            </div>
            <div id="monetaryRateCardDetail">
                <div class="col-xs-12">
                    <table id='monetaryRateCardDetailTable' class="table table-blue table-bordered">
                        <caption class="caption-header">
                            <s:text name="ratecard.versions.default.version"/>
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn" onclick="addRateCardVersionDetail();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                            </div>
                        </caption>
                        <thead>
                        <tr>
                            <th><span id="versionDetailLabelKey1" name="ratecard.version.label1"><s:property value="monetaryRateCardData.labelKey1"/></span></th>
                            <th><span id="versionDetailLabelKey2" name="ratecard.version.label2"><s:property value="monetaryRateCardData.labelKey2"/></span></th>
                            <th><s:text name="ratecard.version.pulse"/></th>
                            <th id="priceTag" name="priceTag">

                            </th>
                            <th><s:text name="ratecard.version.discount"/></th>
                            <th><s:text name="ratecard.version.revenuedetail"/></th>
                            <th style="width:35px;">&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody>

                        <tr name="monetaryRateCardDetailTableRow">
                            <td>
                                <s:hidden name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].orderNumber" value="1"/>
                                <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].label1"
                                             cssClass="form-control fieldMapping"  elementCssClass="col-xs-12" id="defaultLabelKey1-0" maxLength="100" tabindex="10"/></td>
                            <td><s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].label2"
                                             cssClass="form-control "  elementCssClass="col-xs-12" id="defaultLabelKey2-0" maxLength="100" tabindex="11"/></td>
                            <td id = "rateCardVersionDetail">
                                <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].pulse1"
                                             cssClass="form-control" elementCssClass="col-xs-12" id="defaultPulse-0" onkeypress='return isNaturalInteger(event);'
                                             maxlength="18" tabindex="12"/>
                            </td>
                            <td><s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].rate1"
                                             cssClass="form-control" elementCssClass="col-xs-12" id="defaultRate-0" maxlength="16" tabindex="13"/></td>
                            <td><s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].discount" type="number" min="0" max="100"
                                             cssClass="form-control" elementCssClass="col-xs-12" id="defaultDiscount-0" maxlength="3" tabindex="14"/></td>
                            <td><s:select name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].revenueDetail.id"
                                          elementCssClass="col-xs-12" id="revenueDetail-0" cssClass="form-control select2"
                                          list="revenueDetails" listKey="id" listValue="name" cssStyle="width:100%" tabindex="14" headerKey="" headerValue="SELECT" />
                            </td>

                            <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="col-xs-12" id="generalError"></div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" id="btnSubmit" type="button" role="button" tabindex="15"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/${rncPkgId}'"
                            tabindex="16"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                            name="button.cancel"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<div style="display: none" id = "tmpRatePulseUnitForSession">
    <s:select name="monetaryRateCardData.pulseUnit" key="ratecard.pulseUnit" id="pulseUom" cssClass="form-control"
              list="pulseUnits" tabindex="6"/>
    <s:select name="monetaryRateCardData.rateUnit" key="ratecard.rateUnit"  list="{}" id="rateUom"  cssClass="form-control"
              tabindex="7"/>
</div>
<div style="display: none" id = "tmpRatePulseUnitForEvent">
    <s:textfield name="monetaryRateCardData.pulseUnit" key="ratecard.pulseUnit" id="pulseUom" cssClass="form-control"
                 value="%{@com.elitecore.corenetvertex.constants.Uom@EVENT.name()}" tabindex="6" readonly="true"/>
    <s:textfield name="monetaryRateCardData.rateUnit" key="ratecard.rateUnit" id="rateUom" cssClass="form-control"
                 value="%{@com.elitecore.corenetvertex.constants.Uom@EVENT.name()}" tabindex="7" readonly="true"/>
</div>
<div style="display: none" id = "tmpRateCardVersionDetailForEvent">
    <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].pulse1"
                 cssClass="form-control" elementCssClass="col-xs-12" id="defaultPulse-0" maxlength="18" tabindex="12" readonly="true" value="1"/>
</div>
<div style="display: none" id = "tmpRateCardVersionDetailForSession">
    <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].pulse1"
                 cssClass="form-control" elementCssClass="col-xs-12" id="defaultPulse-0" onkeypress='return isNaturalInteger(event);'
                 maxlength="18" tabindex="12"/>
</div>
<%@include file="../monetaryratecard/monetary-rate-card-utility.jsp"%>
<script type="text/javascript">
    var sessionChargingType = '<s:text name="@com.elitecore.corenetvertex.pkg.ChargingType@SESSION.name()" />';
    $(function(){
        enableSelect2();
        updateCurrencyLabel();
    });

    // Initialized variable in utility file to verify event based package
    chargingType = new rateCardChargingType(false);

    $('#chargingType').on('change',function () {
        var configuredChargingType = $("#chargingType").val();

        if (configuredChargingType == sessionChargingType) {
            setSessionBaseRateAndPulseUnit();
            //for new line created
            chargingType.setValue(false);

            //for existing lines change data
            $('input[id^="defaultPulse-"]').attr("readonly",false);
            $('input[id^="defaultPulse-"]').val('');
        } else {
            setEventBaseRateAndPulseUnit();
            chargingType.setValue(true);

            $('input[id^="defaultPulse-"]').attr("readonly",true);
            $('input[id^="defaultPulse-"]').val(1);
        }
    });

    function validate() {
        var isValidName = verifyUniquenessOnSubmit('rateCardName', 'create', '', 'com.elitecore.corenetvertex.pd.ratecard.RateCardData', '', '');
        if(isValidName == false) {
            return false;
        }
        return filterEmptyVersions();
    }

    function setEventBaseRateAndPulseUnit() {
        var ratePulseUnitForEvent = $("#tmpRatePulseUnitForEvent").html();
        var rateCardVersionForEvent = $("#tmpRateCardVersionDetailForEvent").html();
        $("#ratePulseUnit").html(ratePulseUnitForEvent);
        $('#rateCardVersionDetail').html(rateCardVersionForEvent);
    }

    function setSessionBaseRateAndPulseUnit() {
        var ratePulseUnitForSession = $("#tmpRatePulseUnitForSession").html();
        var rateCardVersionForSession = $("#tmpRateCardVersionDetailForSession").html();
        $("#ratePulseUnit").html(ratePulseUnitForSession);
        $('#rateCardVersionDetail').html(rateCardVersionForSession);
        validateRateUnit();

    }

    function updateCurrencyLabel(){
        var currencyValue = $("#currencyList").val();
        $("#currency").val(currencyValue);
        var labelValue = '<s:property value="getText('ratecard.version.rate')"/>'+ ' '+ '<s:property value="getText('opening.braces')"/>' + currencyValue + '<s:property value="getText('closing.braces')"/>';
        $("#priceTag").html(labelValue);
    }


    $(function(){
        setPcrfKeySuggestions();
        var chargingTypeVal = $('#chargingType').val();

        if(isNullOrEmpty(chargingTypeVal) || chargingTypeVal == sessionChargingType ) {
            setSessionBaseRateAndPulseUnit();
        } else {
            setEventBaseRateAndPulseUnit();
        }
    });
</script>