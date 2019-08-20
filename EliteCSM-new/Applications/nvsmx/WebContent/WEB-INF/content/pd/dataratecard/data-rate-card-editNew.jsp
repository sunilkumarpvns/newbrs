<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 19/4/18
  Time: 3:07 PM
  To change this template use File | Settings | File Templates.
--%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="rate.card.create" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/pd/dataratecard" action="data-rate-card" id="dataRateCardCreate" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-3" elementCssClass="col-xs-12 col-sm-9" validator="validateForm()">
			<s:token/>
			<s:hidden name="pkgData.id"  />
			<s:hidden name="pkgData.groups" />
			<s:hidden name="currency" id="currency" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>
			<div class="row">
				<div class="col-xs-6">
					<s:textfield name="name" key="rate.card.name" id="ratecardName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
					<s:textarea name="description" key="rate.card.description" id="rateCardDescription" cssClass="form-control" rows="2" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}" maxlength="2000" tabindex="2"/>
					<s:textfield name="labelKey1" key="rate.card.label.key1" id="rateCardLabelKey1" cssClass="form-control pcrf-key-suggestions" maxlength="100" tabindex="3" onblur="updateLabelsOnBlur('#versionDetailLabelKey1','#rateCardLabelKey1');" />
				</div>

				<div class="col-xs-6">
					<s:select name="pulseUnit" key="rate.card.pulse.unit" id="rateCardPulseUnit" cssClass="form-control" list="getUomListForPulse()" tabindex="4" listValue="getValue()" listKey="name()" onchange="validateRateUnit()" />
					<s:select name="rateUnit" key="rate.card.rate.unit" id="rateCardRateUnit" cssClass="form-control" list="{}" tabindex="5" />
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
									<th><span id="versionDetailLabelKey1"/></th>
									<th><span id="versionDetailLabelKey2"/></th>
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
								<tr name="dataRateCardDetailTableRow">
									<td><s:textfield name='dataRateCardVersionDetailDataList[0].labelKey1'
													 cssClass="form-control" elementCssClass="col-xs-12" id='versionLabelKey1-0' maxlength="100"/></td>
									<td><s:textfield name='dataRateCardVersionDetailDataList[0].labelKey2'
													 cssClass="form-control" elementCssClass="col-xs-12" id="versionLabelKey2-0" maxlength="100"/></td>
									<td><s:textfield name='dataRateCardVersionDetailDataList[0].pulse1' value="1"
													 cssClass="form-control" elementCssClass="col-xs-12" id='pulse1-0' onkeypress='return isNaturalInteger(event);' maxlength="18"/></td>
									<td><s:textfield name='dataRateCardVersionDetailDataList[0].rate1' value="0"
													 cssClass="form-control" elementCssClass="col-xs-12" id='rate1-0' maxlength="16"/></td>
									<td><s:textfield name='dataRateCardVersionDetailDataList[0].rateType'
													 cssClass="form-control" elementCssClass="col-xs-12" id='rateType-0'  value='%{@com.elitecore.corenetvertex.constants.TierRateType@FLAT.name()}' readonly="true"/></td>
									<td><s:select name="dataRateCardVersionDetailDataList[0].revenueDetail.id"
												  elementCssClass="col-xs-12" id="revenueDetail-0" cssClass="form-control select2"
												  list="revenueDetails" listKey="id" listValue="name" cssStyle="width:100%" tabindex="3" headerKey="" headerValue="SELECT" />
									</td>
									<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
								</tr>
							</tbody>
						</table>
						<div class="col-xs-12" id="generalError"></div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-xs-12" align="center" >
					<s:submit cssClass="btn  btn-sm btn-primary" id="btnSubmit"  type="button" role="button" tabindex="9"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
				</div>
			</div>
		</s:form>
	</div>
</div>
<script>
    function validateForm() {
        var isValidName = verifyUniquenessOnSubmit('ratecardName','create','','com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData','<s:text name="pkgData.id"/>','');
        if(isValidName == false) {
            return false;
		}
        return filterEmptyVersions();
	}

    enableSelect2();
</script>

<%@include file="data-rate-card-utility.jsp"%>