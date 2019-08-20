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
				onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${auditableId}&auditPageHeadingName=${name}&refererUrl=/pd/monetaryratecard/monetary-rate-card/${id}'">
                              
                  <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <s:if test="%{scope == @com.elitecore.corenetvertex.pd.ratecard.RateCardScope@LOCAL.name()}">
            <s:if test="%{rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
			<span class="btn-group btn-group-xs">
				<button type="button" id="editButton" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/${id}/edit?rncPackageId=${rncPackageData.id}'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            </s:if>
            <s:else>
                <span class="btn-group btn-group-xs">
				<button disabled="disabled" type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            </s:else>

            <s:if test="%{rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
            <span class="btn-group btn-group-xs" id="deleteButton" data-toggle="confirmation-singleton"
                  onmousedown="deleteConfirm()"
                  data-href="${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/${id}?_method=DELETE&rncPackageId=${rncPackageData.id}">
                <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
            </span>
            </s:if>
            <s:else>
                 <span disabled="disabled" class="btn-group btn-group-xs" id="deleteButton"
                       data-toggle="confirmation-singleton">
                <button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
            </span>
            </s:else>
            </s:if>
        </div>
    </div>
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="ratecard.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.pulseUnit" value="%{monetaryRateCardData.pulseUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>

                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:label key="ratecard.rateUnit" value="%{monetaryRateCardData.rateUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.labelOne" value="%{monetaryRateCardData.labelKey1}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.labelTwo" value="%{monetaryRateCardData.labelKey2}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>

                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>

                  </fieldset>
            <div class="col-xs-12" style="text-align: right; margin-bottom: 10px;">
                <s:if test="%{rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && rncPackageData.mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                    <button type="button" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"
                            onclick="showBulkUpdateModal()">
                        <s:text name="ratecard.version.bulk.update"/>
                    </button>
                </s:if>
                <s:else>
                    <button type="button" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"
                            disabled="disabled">
                        <s:text name="ratecard.version.bulk.update"/>
                    </button>
                </s:else>
            </div>
            <div id="monetaryRateCardDetail">
                <div class="col-xs-12">
                    <table id='monetaryRateCardDetailTable' class="table table-blue table-bordered">
                        <caption class="caption-header">
                           <s:text name="monetaryRateCardData.monetaryRateCardVersions[0].name" />
                        </caption>
                        <thead>
                        <tr>
                            <th><s:text name="%{monetaryRateCardData.labelKey1}"/></th>
                            <th><s:text name="%{monetaryRateCardData.labelKey2}"/></th>
                            <th><s:text name="ratecard.version.pulse"/></th>
                            <s:set var="priceTag">
                                <s:property value="getText('ratecard.version.rate')"/> <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
                            </s:set>
                            <th><s:text name="priceTag"/></th>
                            <th><s:text name="ratecard.version.discount"/></th>
                            <th><s:text name="ratecard.version.revenuedetail"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <s:if test="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail != null">
                                <s:iterator value="monetaryRateCardData.monetaryRateCardVersions[0].monetaryRateCardVersionDetail" var="data" status="i" begin="0" step="">
                                    <tr>
                                        <td><s:property value="%{#data.label1}"  /></td>
                                        <td><s:property value="%{#data.label2}" /></td>
                                        <td><s:property value="%{#data.pulse1}" /></td>
                                        <td><s:property value="%{#data.rate1}" /></td>
                                        <td><s:property value="%{#data.discount}" /></td>
                                        <td><s:property value="%{#data.revenueDetail.name}" /></td>
                                    </tr>
                                </s:iterator>
                            </s:if>
                            <s:else>
                                <tr>
                                    <td style="text-align: center;" colspan="5"><s:text name="empty.table.message"/></td>
                                </tr>
                            </s:else>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
                <s:if test="%{scope == @com.elitecore.corenetvertex.pd.ratecard.RateCardScope@LOCAL.name()}">
                    <div class="row" >
                        <div class="col-xs-12 back-to-list" align="center">
                            <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${rncPackageData.id}'" tabindex="6"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.rncPackage"/></button>
                        </div>
                    </div>
                </s:if>
        </div>
    </div>
</div>
<%@include file="monetary-rate-card-version-configuration-modal.jsp"%>
<script>
    function showBulkUpdateModal(){
        $('#monetaryRateCardVersionConfigurationBulkUpdate').modal('show');
        $('#bulkUpdateRate').focus();
    }
</script>