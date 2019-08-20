<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 23/10/17
  Time: 2:51 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="driver.db.cdr.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form id="dbcdrdriverform" name="dbcdrdriverform" namespace="/sm/driver" method="post" action="db-cdr-driver" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:hidden name="dbCdrDriverData.id"/>
            <s:token/>
            <div class="row">
                <div class="col-xs-12 col-sm-6">
                    <s:textfield name="name" key="db.cdr.name" id="driverName" cssClass="form-control focusElement" onblur="verifyUniqueness('driverName','update','%{id}','com.elitecore.corenetvertex.sm.driver.DriverData','','');" />
                    <s:textarea name="description" key="db.cdr.description" cssClass="form-control" rows="2" />
                </div>
                <div class="col-xs-12 col-sm-6">
                    <s:select name="dbCdrDriverData.databaseId" key="db.cdr.datasource" cssClass="form-control" list="databaseDataList"  listValue="getName()" listKey="getId()" id="databaseId" />
                    <s:textfield name="dbCdrDriverData.maxQueryTimeoutCount" key="db.cdr.max.query.timeout.count" cssClass="form-control" type="number" onkeypress="return isNaturalInteger(event);" min="1" />
                </div>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="db.cdr.details"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield id="tableName" name="dbCdrDriverData.tableName" key="db.cdr.table.name" cssClass="form-control" onfocus="getSelectedDatabaseTableNames(this);" maxlength="30" />
                            <s:textfield id="identityField" name="dbCdrDriverData.identityField" key="db.cdr.identity.field" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="dbCdrDriverData.sequenceName" key="db.cdr.sequence.name" cssClass="form-control" maxlength="30" />
                            <s:select list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" id="storeAllCdr" name="dbCdrDriverData.storeAllCdr" key="db.cdr.store.all.cdr" cssClass="form-control" listValue="getDisplayBooleanValue()" listKey="getStringNameBoolean()" onchange="disableBatchParameters(this)" ></s:select>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="db.cdr.batch.update.header"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:select id="batchUpdate" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" name="dbCdrDriverData.batchUpdate" key="db.cdr.batch.update" cssClass="form-control" listValue="getDisplayBooleanValue()" listKey="getStringNameBoolean()" onchange="disableBatchParametersFromBatchUpdate(this)"  ></s:select>
                            <s:textfield id="batchSize" name="dbCdrDriverData.batchSize" key="db.cdr.batch.size" cssClass="form-control" type="number" onkeypress="return isNaturalInteger(event);" min="1" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield id="batchUpdateQueryTimeout" name="dbCdrDriverData.batchUpdateQueryTimeout" key="db.cdr.batch.update.query.timeout" cssClass="form-control" onkeypress="return isNaturalInteger(event);" maxLength="2"/>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="db.cdr.mandatory.field.details"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield id="sessionIdFieldName" name="dbCdrDriverData.sessionIdFieldName" key="db.cdr.session.id.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30"  />
                            <s:textfield id="createDateFieldName" name="dbCdrDriverData.createDateFieldName" key="db.cdr.create.date.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield id="lastModifiedDateFieldName" name="dbCdrDriverData.lastModifiedDateFieldName" key="db.cdr.last.modified.date.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30"  />
                            <s:textfield id="timeStampFieldName" name="dbCdrDriverData.timeStampFieldName" key="db.cdr.timestamp.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="128"  />
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="db.cdr.usage.fields"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:select list="#{@com.elitecore.corenetvertex.sm.driver.constants.ReportingType@UM:@com.elitecore.corenetvertex.sm.driver.constants.ReportingType@UM.getDisplayValue()}" name="dbCdrDriverData.reportingType" key="db.cdr.reporting.type" cssClass="form-control" headerValue="SELECT" headerKey=""/>
                            <s:textfield id="usageKeyFieldName" name="dbCdrDriverData.usageKeyFieldName" key="db.cdr.usage.key.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30" />
                            <s:textfield id="usageTimeFieldName" name="dbCdrDriverData.usageTimeFieldName" key="db.cdr.usage.time.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30"  />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield id="inputOctetsFieldName" name="dbCdrDriverData.inputOctetsFieldName" key="db.cdr.input.octets.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30"  />
                            <s:textfield id="outputOctetsFieldName" name="dbCdrDriverData.outputOctetsFieldName" key="db.cdr.output.octets.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30"  />
                            <s:textfield id="totalOctetsFieldName" name="dbCdrDriverData.totalOctetsFieldName" key="db.cdr.total.octets.field.name" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" maxlength="30" />
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="db.cdr.field.mapping"/> </legend>
                    <div id="DbCdrFieldMapping">
                        <div class="col-xs-12 col-sm-12">
                            <table id="DbCdrFieldMappingTable"  class="table table-blue table-bordered">
                                <caption class="caption-header">
                                    <s:text name="db.cdr.field.mapping" />
                                    <div align="right" class="display-btn">
                                        <span class="btn btn-group btn-group-xs defaultBtn" onclick="addFieldMapping();" id="addFieldMappingRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                    </div>
                                </caption>
                                <thead>
                                    <th><s:text name="db.cdr.field.mapping.pcrf.key"/></th>
                                    <th><s:text name="db.cdr.field.mapping.db.field"/></th>
                                    <th><s:text name="db.cdr.field.mapping.datatype"/></th>
                                    <th><s:text name="db.cdr.field.mapping.default.value"/></th>
                                    <th style="width:35px;">&nbsp;</th>
                                </thead>
                                <tbody>
                                    <s:iterator value="dbCdrDriverData.dbCdrDriverFieldMappingDataList" status="i" var="dbCdrDriverFieldMappingData">
                                        <tr name="FieldMappingTableRow">
                                            <td>
                                                <s:hidden name="dbCdrDriverData.dbCdrDriverFieldMappingDataList[%{#i.count - 1}].id" value="%{#dbCdrDriverFieldMappingData.id}"/>
                                                <s:textfield id="FieldMappingPcrfKey-%{#i.count - 1}" value="%{#dbCdrDriverFieldMappingData.pcrfKey}"	name="dbCdrDriverData.dbCdrDriverFieldMappingDataList[%{#i.count - 1}].pcrfKey" cssClass="form-control pcrf-key-suggestions" elementCssClass="col-xs-12" maxlength="255"/>
                                            </td>
                                            <td><s:textfield id="FieldMappingDbField-%{#i.count - 1}" value="%{#dbCdrDriverFieldMappingData.dbField}" name="dbCdrDriverData.dbCdrDriverFieldMappingDataList[%{#i.count - 1}].dbField" cssClass="form-control"  elementCssClass="col-xs-12" maxlength="30" /></td>
                                            <td><s:select value="%{#dbCdrDriverFieldMappingData.dataType}" name="dbCdrDriverData.dbCdrDriverFieldMappingDataList[%{#i.count - 1}].dataType" list="@com.elitecore.corenetvertex.constants.FieldMappingDataType@values()" listValue="getDisplayValue()" listKey="getValue()"  cssClass="form-control"  elementCssClass="col-xs-12" ></s:select></td>
                                            <td><s:textfield value="%{#dbCdrDriverFieldMappingData.defaultValue}" name="dbCdrDriverData.dbCdrDriverFieldMappingDataList[%{#i.count - 1}].defaultValue" cssClass="form-control"  elementCssClass="col-xs-12" maxlength="256" /></td>
                                            <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
                                        </tr>
                                    </s:iterator>
                                </tbody>
                            </table>
                            <div class="col-xs-12" id="generalError"></div>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-primary btn-sm"  formaction="${pageContext.request.contextPath}/sm/driver/db-cdr-driver/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/db-cdr-driver/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<table id="tempFieldMappingTable" style="display: none">
    <tr>
        <td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" maxlength="255"/></td>
        <td><s:textfield cssClass="form-control"  elementCssClass="col-xs-12" maxlength="30" /></td>
        <td><s:select list="@com.elitecore.corenetvertex.constants.FieldMappingDataType@values()" listValue="getDisplayValue()" listKey="getValue()"  cssClass="form-control"  elementCssClass="col-xs-12" ></s:select></td>
        <td><s:textfield cssClass="form-control"  elementCssClass="col-xs-12" maxlength="256" /></td>
        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
    </tr>
</table>

<script>

    function validateForm(){

        var isValidName = verifyUniquenessOnSubmit('driverName','update','<s:property value="id"/>','com.elitecore.corenetvertex.sm.driver.DriverData','','');
        if(isValidName == false) {
            return isValidName;
        }
        return validateMapping() && validateBatchParameters();

    }

</script>
<%@include file="../utility/fetch-tables-and-columns.jsp"%>
<%@include file="DbCdrDriverUtility.jsp"%>