<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 19/4/18
  Time: 4:38 PM
  To change this template use File | Settings | File Templates.
--%>
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
						onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${pkgData.id}&auditPageHeadingName=${name}&refererUrl=/pd/dataratecard/data-rate-card/${id}'">
                  <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>

            <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                <span class="btn-group btn-group-xs">
                    <button type="button" id="editButton" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/dataratecard/data-rate-card/${id}/edit?pkgId=${pkgData.id}'">
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

            <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/dataratecard/data-rate-card/${id}?_method=DELETE&pkgId=${pkgData.id}">
                    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                        <span class="glyphicon glyphicon-trash"></span>
                    </button>
                </span>
            </s:if>
            <s:else>
               <span disabled="disabled" class="btn-group btn-group-xs" data-toggle="confirmation-singleton">
                    <button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
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
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="rate.card.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="rate.card.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="rate.card.pulse.unit" value="%{pulseUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>

					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
							<s:label key="rate.card.rate.unit" value="%{rateUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
							<s:label key="rate.card.label.key1" value="%{labelKey1}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
							<s:label key="rate.card.label.key2" value="%{labelKey2}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
						</div>
					</div>
                   
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
				</div>
			</fieldset>
            <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                <div class="col-xs-12"
                     style="text-align: right; margin-bottom: 10px;">
                    <button type="button" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"
                            onclick="$('#dataRateCardVersionConfigurationBulkUpdate').modal('show');"><s:text name="rate.card.version.bulk.update"/>
                    </button>
                </div>
            </s:if>
            <s:else>
                <div class="col-xs-12"
                     style="text-align: right; margin-bottom: 10px;">
                    <button disabled="disabled" type="button" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"><s:text name="rate.card.version.bulk.update"/>
                    </button>
                </div>
            </s:else>
                <div class="col-xs-12">
                    <table id='versionDivTable' class="table table-blue table-bordered">
                        <caption class="caption-header">
                            <s:text name="rate.card.version"/> <s:property value="dataRateCardVersionRelationData[0].versionName" />
                        </caption>
                        <thead>
                            <tr>
                                <th><s:text name="%{labelKey1}"/></th>
                                <th><s:text name="%{labelKey2}"/></th>
                                <th><s:text name="Pulse"/></th>
                                <s:set var="priceTag">
                                    <s:property value="getText('rate.card.version.rate')"/> <s:property value="getText('opening.braces')"/><s:property value="pkgData.currency"/><s:property value="getText('closing.braces')"/>
                                </s:set>
                                <th><s:text name="priceTag"/></th>
                                <th><s:text name="Rate Type"/></th>
                                <th><s:text name="Revenue Detail"/></th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:if test="dataRateCardVersionRelationData[0].dataRateCardVersionDetailDataList != null">
                                <s:iterator value="dataRateCardVersionRelationData[0].dataRateCardVersionDetailDataList" var="data" status="i" begin="0" step="">
                                    <tr>
                                        <td><s:property value="%{#data.labelKey1}"  /></td>
                                        <td><s:property value="%{#data.labelKey2}" /></td>
                                        <td><s:property value="%{#data.pulse1}" /></td>
                                        <td><s:property value="%{#data.rate1}" /></td>
                                        <td><s:property value="%{#data.rateType}" /></td>
                                        <td><s:property value="%{#data.revenueDetail.name}" /></td>
                                    </tr>
                                </s:iterator>
                            </s:if>
                            <s:else>
                                <tr>
                                    <td style="text-align: center;" colspan="5"><s:text name="empty.table.message"/></td>
                                </tr>
                            </s:else>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="row" >
                <div class="col-xs-12 back-to-list" align="center">
                    <button id="btnBack" class="btn btn-primary btn-sm" type="button" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
                </div>
            </div>
        </div>

	</div>
</div>
<%@include file="data-rate-card-version-configuration-modal.jsp"%>
