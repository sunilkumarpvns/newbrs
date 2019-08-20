<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="ratecard.create"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/monetaryratecard" action="monetary-rate-card" id="rateCardCreate" method="post"
                cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4"
                elementCssClass="col-xs-12 col-sm-8" validator="validate()">
        <s:token/>
        <s:hidden name="rncPkgId" value="%{rncPkgId}"/>
        <s:hidden name="currency" value="%{currency}"/>

        <s:hidden name="scope" value="LOCAL"/>
        <div class="row">
            <div class="col-sm-6">
                <s:textfield name="name" key="ratecard.name" id="rateCardName" cssClass="form-control focusElement"
                             maxlength="100" tabindex="1"/>
                <s:textarea name="description" key="ratecard.description" id="ratecardDescription"
                            cssClass="form-control" rows="2"
                            value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"
                            maxlength="2000" tabindex="2"/>
                <s:set value="%{chargingType == @com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}" var="isEventBasedPackage"/>
                <s:if test="%{isEventBasedPackage}" >
                    <s:textfield name="monetaryRateCardData.pulseUnit" key="ratecard.pulseUnit" id="pulseUom" cssClass="form-control"
                                 value="%{@com.elitecore.corenetvertex.constants.Uom@EVENT.name()}" tabindex="3" readonly="true"/>
                </s:if>
                <s:else>
                    <s:select name="monetaryRateCardData.pulseUnit" key="ratecard.pulseUnit" id="pulseUom" cssClass="form-control"
                              list="pulseUnits" tabindex="3" onchange="validateRateUnit()"/>
                </s:else>
            </div>
            <div class="col-xs-6">
                <s:if test="%{isEventBasedPackage}" >
                    <s:textfield name="monetaryRateCardData.rateUnit" key="ratecard.rateUnit" id="rateUom" cssClass="form-control"
                                 value="%{@com.elitecore.corenetvertex.constants.Uom@EVENT.name()}" tabindex="3" readonly="true"/>
                </s:if>
                <s:else>
                <s:select name="monetaryRateCardData.rateUnit" key="ratecard.rateUnit"  list="{}" id="rateUom" cssClass="form-control"
                           tabindex="4"/>
                </s:else>
                <s:textfield name="monetaryRateCardData.labelKey1" key="ratecard.labelOne" id="labelKey1" cssClass="form-control pcrf-key-suggestions"
                             maxlength="100" tabindex="5" onblur="updateLabelsOnBlur('#versionDetailLabelKey1','#labelKey1');" />
                <s:textfield name="monetaryRateCardData.labelKey2" key="ratecard.labelTwo" id="labelKey2" cssClass="form-control pcrf-key-suggestions"
                             maxlength="100" tabindex="6" onblur="updateLabelsOnBlur('#versionDetailLabelKey2','#labelKey2');" />
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
                        <s:set var="priceTag">
                            <s:property value="getText('ratecard.version.rate')"/> <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
                        </s:set>
                        <th><s:text name="priceTag"/></th>
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
                                         cssClass="form-control fieldMapping"  elementCssClass="col-xs-12" id="defaultLabelKey1-0" maxLength="100" tabindex="7"/></td>
                        <td><s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].label2"
                                         cssClass="form-control "  elementCssClass="col-xs-12" id="defaultLabelKey2-0" maxLength="100" tabindex="8"/></td>
                        <td>
                            <s:if test="%{isEventBasedPackage}" >
                                <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].pulse1"
                                             cssClass="form-control" elementCssClass="col-xs-12" id="defaultPulse-0" maxlength="18" tabindex="9" readonly="true" value="1"/>
                            </s:if>
                            <s:else>
                                <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].pulse1"
                                             cssClass="form-control" elementCssClass="col-xs-12" id="defaultPulse-0" onkeypress='return isNaturalInteger(event);'
                                             maxlength="18" tabindex="9"/>
                            </s:else>
                        </td>
                        <td><s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].rate1"
                                         cssClass="form-control" elementCssClass="col-xs-12" id="defaultRate-0" maxlength="16" tabindex="10"/></td>
                        <td><s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].discount" type="number" min="0" max="100"
                                         cssClass="form-control" elementCssClass="col-xs-12" id="defaultDiscount-0" maxlength="3" tabindex="11"/></td>
                        <td><s:select name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[0].revenueDetail.id" elementCssClass="col-xs-12" id="revenueDetail-0" cssClass="form-control select2"
                                      list="revenueDetails" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3" headerKey="" headerValue="SELECT" /></td>
                        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
                    </tr>
                    </tbody>
                </table>
                <div class="col-xs-12" id="generalError"></div>
            </div>
        </div>

    <div class="row">
        <div class="col-xs-12" align="center">
            <s:submit cssClass="btn  btn-sm btn-primary" id="btnSubmit" type="button" role="button" tabindex="12"><span
                    class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
            <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                    onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${rncPkgId}'"
                    tabindex="13"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                    name="button.cancel"/></button>
        </div>
    </div>
    </s:form>
    </div>
</div>

<%@include file="monetary-rate-card-utility.jsp"%>
<script type="text/javascript">
    // Initialized variable in utility file to verify event based package
    chargingType =new rateCardChargingType(<s:property value="%{isEventBasedPackage}"/>);

    function validate() {
        var isValidName = verifyUniquenessOnSubmit('rateCardName', 'create', '', 'com.elitecore.corenetvertex.pd.ratecard.RateCardData', '${rncPkgId}', '');
        if(isValidName == false) {
            return false;
        }
        return filterEmptyVersions();
    }
    $(function(){
        validateRateUnit();
        setPcrfKeySuggestions();
        enableSelect2();
    });
</script>
