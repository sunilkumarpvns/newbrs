<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 18/10/17
  Time: 11:02 AM
  To change this template use File | Settings | File Templates.
--%>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/driver/csv-driver/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/driver/csv-driver/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body" >
        <s:if test="%{@com.elitecore.corenetvertex.sm.driver.constants.FileTransferProtocol@LOCAL.name() == csvDriverData.allocatingProtocol}">
            <button type="button" class="btn btn-primary btn-sm pull-right" disabled="disabled">
                <s:text name="change.password.label"/>
            </button>
        </s:if>
        <s:else>
            <button type="button" class="btn btn-primary btn-sm pull-right" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver-password/${id}/edit'">
                <s:text name="change.password.label"/>
            </button>
        </s:else>
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-8">
                        <div class="row">
                            <s:label key="csv.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.header" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromStringBooleanValue(csvDriverData.header).displayBooleanValue}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.delimiter" value="%{csvDriverData.delimiter}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.cdr.timestamp" value="%{csvDriverData.timeStampFormat}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.reporting.type" value="%{@com.elitecore.corenetvertex.sm.driver.constants.ReportingType@fromValue(csvDriverData.reportingType).displayValue}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="csv.file.parameters" /></legend>
                <div class="row">
                    <div class="col-sm-6 ">
                        <div class="row">
                            <s:label key="csv.file.name" value="%{csvDriverData.fileName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.location" value="%{csvDriverData.fileLocation}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.prefix.file.name" value="%{csvDriverData.prefixFileName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="csv.default.folder.name" value="%{csvDriverData.defaultFolderName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.folder.name" value="%{csvDriverData.folderName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="csv.file.rolling.parameters" /></legend>
                <div class="row">
                    <div class="col-sm-6 ">
                        <div class="row">
                            <s:label key="csv.time.boundary" value="%{csvDriverData.timeBoundary}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.time.based.rolling.unit" value="%{csvDriverData.timeBasedRollingUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.size.based.rolling.unit" value="%{csvDriverData.sizeBasedRollingUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.record.based.rolling.unit" value="%{csvDriverData.recordBasedRollingUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="csv.sequence.range" value="%{csvDriverData.sequenceRange}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.sequence.position" value="%{csvDriverData.sequencePosition}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.sequence.globalization" value="%{csvDriverData.sequenceGlobalization}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>

            </fieldset>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="csv.file.transfer.parameters" /></legend>
                <div class="row">
                    <div class="col-sm-6 ">
                        <div class="row">
                            <s:label key="csv.allocating.protocol" value="%{csvDriverData.allocatingProtocol}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.address" value="%{csvDriverData.address}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.remote.location" value="%{csvDriverData.remoteFileLocation}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.fail.over.time" value="%{csvDriverData.failOverTime}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="csv.user.name" value="%{csvDriverData.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.archive.location" value="%{csvDriverData.archiveLocation}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="csv.post.operation" value="%{csvDriverData.postOperation}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>

            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="csv.field.mapping" /></legend>
                    <s:if test="csvDriverData.csvDriverFieldMappingDataList == null || csvDriverData.csvDriverFieldMappingDataList.size() <= 1">
                        <button class="btn btn-primary btn-xs" disabled style="float:right;padding-top: 3px; padding-bottom: 3px">
                            <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                            <s:text name="manage.order"/>
                        </button>
                    </s:if>
                    <s:else>
                        <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver/${csvDriverData.id}/initManageOrder'">
                            <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                            <s:text name="manage.order"/>
                        </button>
                    </s:else>
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="csvFieldMapping"
                                list="${csvFieldMappingAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="Order No" beanProperty="orderNo" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="Header Field" beanProperty="headerField" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="PCRF Key" beanProperty="pcrfKey" tdCssClass="text-left text-middle" />
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="csv.strip.attribute" /></legend>
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="csvStripMapping"
                                list="${csvStripMappingAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="PCRF Key" beanProperty="pcrfKey" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="Pattern" beanProperty="pattern" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="Separator" beanProperty="separator" tdCssClass="text-left text-middle" />
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="button" class="btn btn-primary btn-sm"  id="btnList" value="List" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver'"><span class="glyphicon glyphicon-backward" title="List"></span>&nbsp;<s:text name="button.list"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
