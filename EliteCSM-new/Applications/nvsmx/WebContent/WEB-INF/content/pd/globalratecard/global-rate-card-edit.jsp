<%--
  Created by IntelliJ IDEA.
  User: arpit
  Date: 5/10/18
  Time: 2:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="ratecard.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/globalratecard" action="global-rate-card" id="rateCardEdit" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validate('edit','%{id}')">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <s:hidden name="type" value="MONETARY"/>
            <s:hidden name="scope" value="GLOBAL"/>
            <s:hidden name="currency" id="currency" value="%{currency}"/>
            <div>
                <div class="col-sm-6">
                    <s:textfield name="name" key="ratecard.name" id="rateCardName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
                    <s:textarea name="description" key="ratecard.description" id="ratecardDescription" cssClass="form-control" rows="2" tabindex="2" maxlength="2000"/>
                    <s:select name="groups" id="planGroups" value="groupValuesForUpdate" key="plan.groups" cssClass="form-control select2" list="#session.staffBelongingGroups" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3"/>
                    <s:set value="%{chargingType == @com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}" var="isEventBasedPackage"/>
                    <s:textfield name="chargingType" key="ratecard.chargingtype" cssClass="form-control" tabindex="4" readonly="true"/>
                    <s:select name="currencyList" key="ratecard.currency" list="@java.util.Currency@getAvailableCurrencies()" listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" id="currencyList" cssClass="form-control" tabindex="5" value="%{currency}" onchange="updateCurrencyLabel()" />
                </div>

                <div class="col-xs-6">
                    <s:if test="%{isEventBasedPackage}" >
                        <s:textfield name="monetaryRateCardData.pulseUnit" key="ratecard.pulseUnit" id="pulseUom" cssClass="form-control"
                                     value="%{@com.elitecore.corenetvertex.constants.Uom@EVENT.name()}" tabindex="5" readonly="true"/>
                    </s:if>
                    <s:else>
                        <s:select name="monetaryRateCardData.pulseUnit" key="ratecard.pulseUnit" id="pulseUom" cssClass="form-control"
                                  list="pulseUnits" tabindex="5" onchange="validateRateUnit()"/>
                    </s:else>

                    <s:if test="%{isEventBasedPackage}" >
                        <s:textfield name="monetaryRateCardData.rateUnit" key="ratecard.rateUnit" id="rateUom" cssClass="form-control"
                                     value="%{@com.elitecore.corenetvertex.constants.Uom@EVENT.name()}" tabindex="6" readonly="true"/>
                    </s:if>
                    <s:else>
                        <s:select name="monetaryRateCardData.rateUnit" key="ratecard.rateUnit" id="rateUom" cssClass="form-control" list="{}" tabindex="6"  />
                    </s:else>

                    <s:textfield name="monetaryRateCardData.labelKey1" key="ratecard.labelOne" id="labelKey1" cssClass="form-control pcrf-key-suggestions" maxlength="100" tabindex="7" onblur="updateLabelsOnBlur('#versionDetailLabelKey1','#labelKey1');"  />
                    <s:textfield name="monetaryRateCardData.labelKey2" key="ratecard.labelTwo" id="labelKey2" cssClass="form-control pcrf-key-suggestions" maxlength="100" tabindex="8" onblur="updateLabelsOnBlur('#versionDetailLabelKey2','#labelKey2');" />

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
                                <th id="priceTag" name="priceTag"></th>
                                <th><s:text name="ratecard.version.discount"/></th>
                                <th><s:text name="ratecard.version.revenuedetail"/></th>
                                <th style="width:35px;">&nbsp;</th>
                            </tr>
                            </thead>
                            <tbody>
                            <s:hidden name="monetaryRateCardData.monetaryRateCardVersions[0].id"/>
                            <s:iterator value="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail" status="i" var="monetaryRateCardVersionDetailData">
                                <tr name='monetaryRateCardDetailTableRow'>
                                    <td>
                                        <s:hidden    name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].orderNumber"/>
                                        <s:hidden    name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].id"/>
                                        <s:textfield value="%{#monetaryRateCardVersionDetailData.label1}"	name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].label1" cssClass="form-control fieldMapping"  elementCssClass="col-xs-12" id="defaultLabelKey1-%{#i.count - 1}" maxLength="100"  tabindex="7" />
                                    </td>
                                    <td><s:textfield value="%{#monetaryRateCardVersionDetailData.label2}"	name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].label2" cssClass="form-control "  elementCssClass="col-xs-12" id="defaultLabelKey2-%{#i.count - 1}" maxLength="100" tabindex="8"/></td>
                                    <td>
                                        <s:if test="%{isEventBasedPackage}" >
                                            <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].pulse1"
                                                         cssClass="form-control" elementCssClass="col-xs-12" id="defaultPulse-0" maxlength="18" tabindex="9" readonly="true" value="1"/>
                                        </s:if>
                                        <s:else>
                                            <s:textfield name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].pulse1"
                                                         cssClass="form-control" elementCssClass="col-xs-12" id="defaultPulse-0" onkeypress='return isNaturalInteger(event);' maxlength="18" tabindex="9"/>
                                        </s:else>
                                    </td>
                                    <td><s:textfield value="%{#monetaryRateCardVersionDetailData.rate1}"	name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].rate1" cssClass="form-control "  elementCssClass="col-xs-12" id="defaultRate-%{#i.count - 1}" maxLength="16" tabindex="11"/></td>
                                    <td><s:textfield value="%{#monetaryRateCardVersionDetailData.discount}"	name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].discount" cssClass="form-control "  elementCssClass="col-xs-12" id="defaultDiscount-%{#i.count - 1}" tabindex="12" type='number' min='0' max='100' /></td>

                                    <td><s:select name="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail[%{#i.count - 1}].revenueDetail.id"
                                                  elementCssClass="col-xs-12" id="revenueDetail-0" cssClass="form-control select2"
                                                  list="revenueDetails" listKey="id" listValue="name" cssStyle="width:100%" tabindex="13" headerKey="" headerValue="SELECT" /></td>

                                    <td style="width:35px;"><span class="btn defaultBtn" tabindex="14" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                </tr>
                            </s:iterator>

                            </tbody>
                        </table>
                        <div class="col-xs-12" id="generalError"></div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-12" align="center" >
                        <button type="submit" class="btn btn-primary btn-sm" id="btnSubmit" role="submit" formaction="${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/${id}" tabindex="14" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/${id}'" tabindex="15" ><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">
    function validate(){
        var isValidName = verifyUniquenessOnSubmit('rateCardName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.ratecard.RateCardData','${rncPkgId}','');
        if(isValidName == false) {
            return false;
        }
        return filterEmptyVersions();
    }

    $(function () {
        var chargingTypes = '<s:property value="%{chargingType}"/>';
        var sessionChargingType = '<s:text name="@com.elitecore.corenetvertex.pkg.ChargingType@SESSION.name()" />';

        chargingType = new rateCardChargingType(chargingTypes == sessionChargingType ? false : true);

        validateRateUnit();
        setPcrfKeySuggestions();

        enableSelect2();
        updateCurrencyLabel();
    });

    function updateCurrencyLabel(){
        var currencyValue = $("#currencyList").val();
        $("#currency").val(currencyValue);
        var labelValue = '<s:property value="getText('ratecard.version.rate')"/>'+ ' '+ '<s:property value="getText('opening.braces')"/>' + currencyValue + '<s:property value="getText('closing.braces')"/>';
        $("#priceTag").html(labelValue);

        var systemParameterUpdated = <s:property value="getCurrencyUpdateAllowed()"/>;
        if(systemParameterUpdated == false){
            $("#currencyList").attr("disabled",true);
        }
    }
</script>
<%@include file="../monetaryratecard/monetary-rate-card-utility.jsp"%>
