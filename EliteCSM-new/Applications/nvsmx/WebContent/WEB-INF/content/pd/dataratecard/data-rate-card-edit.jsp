<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 19/4/18
  Time: 5:09 PM
  To change this template use File | Settings | File Templates.
--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>


<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="rate.card.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/dataratecard" action="data-rate-card" id="dataRateCardUpdate" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <s:hidden name="_method" value="put" />
            <s:hidden name="pkgData.id"  />
            <div class="row">
                <div class="col-xs-6">
                    <s:textfield name="name" key="rate.card.name" id="ratecardName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
                    <s:textarea name="description" key="rate.card.description" id="rateCardDescription" cssClass="form-control" rows="2" maxlength="2000" tabindex="2"/>
                    <s:textfield name="labelKey1" key="rate.card.label.key1" id="rateCardLabelKey1" cssClass="form-control pcrf-key-suggestions" maxlength="100" tabindex="3" onblur="updateLabelsOnBlur('#versionDetailLabelKey1','#rateCardLabelKey1');"/>
                </div>

                <div class="col-xs-6">
                    <s:select name="pulseUnit" key="rate.card.pulse.unit" id="rateCardPulseUnit" cssClass="form-control" list="getUomListForPulse()" tabindex="4" listValue="getValue()" listKey="name()" onchange="validateRateUnit()" />
                    <s:select name="rateUnit" key="rate.card.rate.unit" id="rateCardRateUnit" cssClass="form-control" list="{}" tabindex="5" value="" />
                    <s:textfield name="labelKey2" key="rate.card.label.key2" id="rateCardLabelKey2" cssClass="form-control pcrf-key-suggestions" maxlength="100" tabindex="6" onblur="updateLabelsOnBlur('#versionDetailLabelKey2','#rateCardLabelKey2');" />
                </div>

                <div id="dataRateCardDetail">
                    <div class="col-xs-12">
                        <table id='dataRateCardDetailTable'  class="table table-blue table-bordered">
                            <caption class="caption-header">
                                <s:text name="rate.card.version.conf"  />
                                <div align="right" class="display-btn">
                                    <span class="btn btn-group btn-group-xs defaultBtn" onclick="addRateCardVersionDetail();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                </div>
                            </caption>
                            <thead>
                                <tr>
                                    <th><span id="versionDetailLabelKey1"><s:property value="labelKey1"/></span></th>
                                    <th><span id="versionDetailLabelKey2"><s:property value="labelKey2"/></span></th>
                                    <th><s:text name="rate.card.version.pulse"/></th>
                                    <s:set var="priceTag">
                                        <s:property value="getText('rate.card.version.rate')"/> <s:property value="getText('opening.braces')"/><s:property value="pkgData.currency" /><s:property value="getText('closing.braces')"/>
                                    </s:set>
                                    <th><s:text name="priceTag"/></th>
                                    <th><s:text name="rate.card.version.rate.type"/></th>
                                    <th><s:text name="rate.card.version.revenuedetail"/></th>
                                    <th style="width:35px;">&nbsp;</th>
                                </tr>
                            </thead>
                            <tbody>
                            <s:hidden name="dataRateCardVersionRelationData[0].id"/>
                            <s:hidden name="dataRateCardVersionRelationData[0].versionName"/>
                            <s:hidden name="dataRateCardVersionRelationData[0].effectiveFromDate"/>
                            <s:iterator value="dataRateCardVersionRelationData[0].dataRateCardVersionDetailDataList" status="i" var="dataRateCardVersionDetailData">
                                <tr name='dataRateCardDetailTableRow'>
                                    <td>
                                        <s:hidden  value="%{#dataRateCardVersionDetailData.id}"  name="dataRateCardVersionDetailDataList[%{#i.count - 1}].id"/>
                                        <s:textfield value="%{#dataRateCardVersionDetailData.labelKey1}"	name="dataRateCardVersionDetailDataList[%{#i.count - 1}].labelKey1" cssClass="form-control fieldMapping"  elementCssClass="col-xs-12" id="versionLabelKey1-%{#i.count - 1}" maxLength="100" />
                                    </td>
                                    <td><s:textfield value="%{#dataRateCardVersionDetailData.labelKey2}"	name="dataRateCardVersionDetailDataList[%{#i.count - 1}].labelKey2" cssClass="form-control "  elementCssClass="col-xs-12" id="versionLabelKey2-%{#i.count - 1}" maxLength="100"/></td>
                                    <td><s:textfield value="%{#dataRateCardVersionDetailData.pulse1}"	name="dataRateCardVersionDetailDataList[%{#i.count - 1}].pulse1" cssClass="form-control "  elementCssClass="col-xs-12" id="pulse1-%{#i.count - 1}" maxLength="18" onkeypress='return isNaturalInteger(event);'/></td>
                                    <td><s:textfield value="%{#dataRateCardVersionDetailData.rate1}"	name="dataRateCardVersionDetailDataList[%{#i.count - 1}].rate1" cssClass="form-control "  elementCssClass="col-xs-12" id="rate1-%{#i.count - 1}" maxLength="16"/></td>
                                    <td><s:textfield value="%{#dataRateCardVersionDetailData.rateType}"	name="dataRateCardVersionDetailDataList[%{#i.count - 1}].rateType" cssClass="form-control "  elementCssClass="col-xs-12" id="rateType-%{#i.count - 1}" readonly="true"/></td>
                                    <td><s:select name="dataRateCardVersionDetailDataList[%{#i.count - 1}].revenueDetail.id" value="%{#dataRateCardVersionDetailData.revenueDetail.id}"
                                                  elementCssClass="col-xs-12" id="revenueDetail-0" cssClass="form-control select2" list="revenueDetails"
                                                  listKey="id" listValue="name" cssStyle="width:100%" tabindex="3" headerKey="" headerValue="SELECT" /></td>
                                    <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                </tr>
                            </s:iterator>
                            </tbody>
                        </table>
                        <div class="col-xs-12" id="generalError"></div>
                    </div>
                </div>

            </div>

            <div class="row">
                <div class="col-xs-12" align="center" >
                    <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/pd/dataratecard/data-rate-card/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script>
    function validateForm() {
        var isValidName = verifyUniquenessOnSubmit('ratecardName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData','<s:text name="pkgData.id"/>','');
        if(isValidName == false) {
            return false;
        }
        return filterEmptyVersions();
    }

    enableSelect2();
</script>
<%@include file="data-rate-card-utility.jsp"%>