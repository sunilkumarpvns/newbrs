<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
    .label-bold{
        font-weight: bold;
        margin-bottom: 0;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" id="auditButton" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"

                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${auditableId}&auditPageHeadingName=${name}&refererUrl=/pd/nonmonetaryratecard/non-monetary-rate-card/${id}'">

                  <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <s:if test="%{rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                <span class="btn-group btn-group-xs">
                    <button type="button" id="editButton" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/${id}/edit?rncPackageId=${rncPackageData.id}'">
                        <span class="glyphicon glyphicon-pencil"></span>
                    </button>
                </span>
                <span class="btn-group btn-group-xs" id="deleteButton" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/${id}?_method=DELETE&rncPackageId=${rncPackageData.id}">
                    <button  type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                        <span class="glyphicon glyphicon-trash"></span>
                    </button>
                </span>
            </s:if>
            <s:else>
                <span class="btn-group btn-group-xs">
                    <button disabled="disabled" type="button" class="btn btn-default header-btn" data-toggle="tooltip"
                            data-placement="bottom" title="edit">
                        <span class="glyphicon glyphicon-pencil"></span>
                    </button>
                </span>
                <span disabled="disabled" class="btn-group btn-group-xs" id="deleteButton"
                      data-toggle="confirmation-singleton">
                    <button disabled="disabled" type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                            title="delete">
                        <span class="glyphicon glyphicon-trash"></span>
                    </button>
			    </span>
            </s:else>
        </div>
    </div>
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="nonmonetary.ratecard.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="nonmonetary.ratecard.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="nonmonetary.ratecard.renewal.interval" value="%{nonMonetaryRateCardData.renewalInterval} %{nonMonetaryRateCardData.renewalIntervalUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="nonmonetary.ratecard.pulse" value="%{nonMonetaryRateCardData.pulse} %{nonMonetaryRateCardData.pulseUom}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:if test="%{chargingType == @com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}">
                                <s:label key="nonmonetary.ratecard.freeunits" value="%{nonMonetaryRateCardData.event}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            </s:if>
                            <s:else>
                                <s:if test="%{nonMonetaryRateCardData.time != null}">
                                    <s:label key="nonmonetary.ratecard.freeunits" value="%{nonMonetaryRateCardData.time} %{nonMonetaryRateCardData.timeUom}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                                </s:if>
                                <s:else>
                                    <s:label key="nonmonetary.ratecard.freeunits" value="UNLIMITED %{nonMonetaryRateCardData.timeUom}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                                </s:else>
                            </s:else>
                            <s:label key="nonmonetary.ratecard.proration" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(nonMonetaryRateCardData.proration).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>

                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>
            <div class="row" >
                <div class="col-xs-12 back-to-list" align="center">
                    <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${rncPackageData.id}'" tabindex="6"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.rncPackage"/></button>
                </div>
            </div>
        </div>
    </div>
</div>


