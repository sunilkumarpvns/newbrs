<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 23/10/17
  Time: 2:54 PM
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
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/driver/db-cdr-driver/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/db-cdr-driver/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/driver/db-cdr-driver/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-8">
                        <div class="row">
                            <s:label key="db.cdr.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:hrefLabel key="db.cdr.datasource" value="%{dbCdrDriverData.databaseData.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"
                                     url="/sm/database/database/%{dbCdrDriverData.databaseData.id}" />
                            <s:label key="db.cdr.max.query.timeout.count" value="%{dbCdrDriverData.maxQueryTimeoutCount}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
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
                <legend> <s:text name="db.cdr.details"/> </legend>
                <div class="row">
                    <div class="col-xs-12 col-sm-6">
                        <div class="row">
                            <s:label key="db.cdr.table.name" value="%{dbCdrDriverData.tableName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.identity.field" value="%{dbCdrDriverData.identityField}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="db.cdr.sequence.name" value="%{dbCdrDriverData.sequenceName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.store.all.cdr" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromStringBooleanValue(dbCdrDriverData.storeAllCdr).displayBooleanValue}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>
            </fieldset>


            <fieldset class="fieldSet-line">
                <legend> <s:text name="db.cdr.batch.update.header"/> </legend>
                <div class="row">
                    <div class="col-xs-12 col-sm-6">
                        <div class="row">
                            <s:label key="db.cdr.batch.update" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromStringBooleanValue(dbCdrDriverData.batchUpdate).displayBooleanValue}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.batch.size" value="%{dbCdrDriverData.batchSize}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="db.cdr.batch.update.query.timeout" value="%{dbCdrDriverData.batchUpdateQueryTimeout}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="db.cdr.mandatory.field.details"/></legend>
                <div class="row">
                    <div class="col-xs-12 col-sm-6 ">
                        <div class="row">
                            <s:label key="db.cdr.session.id.field.name" value="%{dbCdrDriverData.sessionIdFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.create.date.field.name" value="%{dbCdrDriverData.createDateFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="db.cdr.last.modified.date.field.name" value="%{dbCdrDriverData.lastModifiedDateFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.timestamp.field.name" value="%{dbCdrDriverData.timeStampFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>
            </fieldset>


            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="db.cdr.usage.fields" /></legend>
                <div class="row">
                    <div class="col-xs-12 col-sm-6 ">
                        <div class="row">
                            <s:label key="db.cdr.reporting.type" value="%{@com.elitecore.corenetvertex.sm.driver.constants.ReportingType@fromValue(dbCdrDriverData.reportingType).displayValue}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.usage.key.field.name" value="%{dbCdrDriverData.usageKeyFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.usage.time.field.name" value="%{dbCdrDriverData.usageTimeFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="db.cdr.input.octets.field.name" value="%{dbCdrDriverData.inputOctetsFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.output.octets.field.name" value="%{dbCdrDriverData.outputOctetsFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="db.cdr.total.octets.field.name" value="%{dbCdrDriverData.totalOctetsFieldName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="db.cdr.field.mapping" /></legend>
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="dbCdrFieldMapping"
                                list="${dbCdrFieldMappingAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="PCRF Key" beanProperty="pcrfKey" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="DB Field" beanProperty="dbField" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="Data Type" beanProperty="dataType" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="Defaut Value" beanProperty="defaultValue" tdCssClass="text-left text-middle" />
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="button" class="btn btn-primary btn-sm"  id="btnList" value="List" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/db-cdr-driver'"><span class="glyphicon glyphicon-backward" title="List"></span>&nbsp;<s:text name="button.list"/></button>
                </div>
            </div>

        </div>
    </div>
</div>
