<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 17/10/17
  Time: 3:19 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="driver.csv.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form id="csvdriverform" name="csvdriverform" namespace="/sm/driver" method="post" action="csv-driver" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-xs-12 col-sm-6">
                    <s:hidden name="id" value="%{id}" />
                    <s:hidden name="csvDriverData.id" />
                    <s:textfield name="name" key="csv.name" id="driverName" cssClass="form-control focusElement"  />
                    <s:textarea name="description" key="csv.description" cssClass="form-control" rows="2" />
                    <s:select list="@com.elitecore.corenetvertex.sm.driver.constants.ReportingType@values()" listKey="name()" listValue="getDisplayValue()" name="csvDriverData.reportingType" key="csv.reporting.type" cssClass="form-control" headerValue="Select" headerKey="" ></s:select>
                </div>
                <div class="col-xs-12 col-sm-6">
                    <s:select id="headerField" name="csvDriverData.header" key="csv.header" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listValue="getStringNameBoolean()" listKey="getStringNameBoolean()" />
                    <s:textfield name="csvDriverData.delimiter" key="csv.delimiter" cssClass="form-control" maxlength="1" />
                    <s:textfield name="csvDriverData.timeStampFormat" key="csv.cdr.timestamp" cssClass="form-control" maxlength="128" />
                </div>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.file.parameters"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.fileName" key="csv.file.name" cssClass="form-control" maxlength="64" />
                            <s:textfield name="csvDriverData.fileLocation" key="csv.location" cssClass="form-control" maxlength="512" />
                            <s:textfield name="csvDriverData.prefixFileName" key="csv.prefix.file.name" cssClass="form-control" maxlength="64" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.defaultFolderName" key="csv.default.folder.name" cssClass="form-control" maxlength="64"/>
                            <s:textfield name="csvDriverData.folderName" key="csv.folder.name" cssClass="form-control" maxlength="64"/>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.file.rolling.parameters"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:select list="@com.elitecore.corenetvertex.sm.driver.constants.TimeUnits@values()" name="csvDriverData.timeBoundary" key="csv.time.boundary" cssClass="form-control" listValue="getVal()" listKey="getKey()" headerKey="" headerValue="Select" ></s:select>
                            <s:textfield id="timeBasedRollingUnit" name="csvDriverData.timeBasedRollingUnit" key="csv.time.based.rolling.unit" cssClass="form-control" onkeypress="return isNaturalInteger(event);" min="0" maxLength="12" />
                            <s:textfield id="sizeBasedRollingUnit" name="csvDriverData.sizeBasedRollingUnit" key="csv.size.based.rolling.unit" cssClass="form-control" onkeypress="return isNaturalInteger(event);" maxLength="12" min="0"/>
                            <s:textfield id="recordBasedRollingUnit" name="csvDriverData.recordBasedRollingUnit" key="csv.record.based.rolling.unit" cssClass="form-control" onkeypress="return isNaturalInteger(event);" maxLength="12" min="0" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.sequenceRange" key="csv.sequence.range" cssClass="form-control" maxlength="40" id="sequenceRange"/>
                            <s:select list="@com.elitecore.corenetvertex.sm.driver.constants.Position@values()" name="csvDriverData.sequencePosition" key="csv.sequence.position" cssClass="form-control" listValue="getDisplayValue()" ></s:select>
                            <s:select list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" name="csvDriverData.sequenceGlobalization" key="csv.sequence.globalization" cssClass="form-control" listValue="getStringNameBoolean()" listKey="getStringNameBoolean()" ></s:select>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.file.transfer.parameters"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.allocatingProtocol" key="csv.allocating.protocol" cssClass="form-control" id="allocatingProtocol" readOnly="true"/>
                            <s:textfield name="csvDriverData.address" key="csv.address" cssClass="form-control" maxlength="255" />
                            <s:textfield name="csvDriverData.remoteFileLocation" key="csv.remote.location" cssClass="form-control" maxlength="512" />
                            <s:textfield id="failOverTime" name="csvDriverData.failOverTime" key="csv.fail.over.time" cssClass="form-control" onkeypress="return isNaturalInteger(event);" min="0" maxLength="10" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.userName" key="csv.user.name" cssClass="form-control" maxlength="64" />
                            <s:textfield name="csvDriverData.archiveLocation" key="csv.archive.location" cssClass="form-control" maxlength="255" />
                            <s:select list="#{'DELETE':'Delete','RENAME':'Rename','ARCHIVE':'Archive'}" name="csvDriverData.postOperation" key="csv.post.operation" cssClass="form-control" ></s:select>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.field.mapping"/> </legend>
                    <div id="CsvFieldMapping">
                        <div class="col-xs-12 col-sm-12">
                            <table id="CsvFieldMappingTable"  class="table table-blue table-bordered">
                                <caption class="caption-header">
                                    <s:text name="csv.field.mapping" />
                                    <div align="right" class="display-btn">
                                        <span class="btn btn-group btn-group-xs defaultBtn" onclick="addFieldMapping();" id="addFieldMappingRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                    </div>
                                </caption>
                                <thead>
                                    <th><s:text name="csv.field.mapping.header"/></th>
                                    <th><s:text name="csv.mapping.pcrf.key"/></th>
                                    <th style="width:35px;">&nbsp;</th>
                                </thead>
                                <tbody>
                                    <s:iterator value="csvDriverData.csvDriverFieldMappingDataList" status="i" var="csvDriverFieldMappingData">
                                        <tr name='FieldMappingRow'>
                                            <td>
                                                <s:hidden name="csvDriverData.csvDriverFieldMappingDataList[%{#i.count - 1}].id" value="%{#csvDriverFieldMappingData.id}"/>
                                                <s:textfield maxlength="255" value="%{#csvDriverFieldMappingData.headerField}"	name="csvDriverData.csvDriverFieldMappingDataList[%{#i.count - 1}].headerField" id="headerField-%{#i.count - 1}" cssClass="form-control"  elementCssClass="col-xs-12" />
                                            </td>
                                            <td><s:textfield maxlength="255" id="FieldMappingPcrfKey-%{#i.count - 1}" value="%{#csvDriverFieldMappingData.pcrfKey}"	name="csvDriverData.csvDriverFieldMappingDataList[%{#i.count - 1}].pcrfKey" cssClass="form-control pcrf-key-suggestions"  elementCssClass="col-xs-12" /></td>
                                            <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                        </tr>
                                    </s:iterator>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.strip.attribute"/> </legend>
                    <div id="CsvStripMapping">
                        <div class="col-xs-12 col-sm-12">
                            <table id="CsvStripMappingTable"  class="table table-blue table-bordered">
                                <caption class="caption-header">
                                    <s:text name="csv.field.mapping" />
                                    <div align="right" class="display-btn">
                                        <span class="btn btn-group btn-group-xs defaultBtn" onclick="addStripMapping();" id="addStripMappingRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                    </div>
                                </caption>
                                <thead>
                                    <th><s:text name="csv.mapping.pcrf.key"/></th>
                                    <th><s:text name="csv.strip.mapping.pattern"/></th>
                                    <th><s:text name="csv.strip.mapping.separator"/></th>
                                    <th style="width:35px;">&nbsp;</th>
                                </thead>
                                <tbody>
                                <s:iterator value="csvDriverData.csvDriverStripMappingDataList" status="i" var="csvDriverStripMappingData">
                                    <tr name='StripMappingRow'>
                                        <td>
                                            <s:hidden name="csvDriverData.csvDriverStripMappingDataList[%{#i.count - 1}].id" value="%{#csvDriverStripMappingData.id}"/>
                                            <s:textfield maxlength="255" id="StripMappingPcrfKey-%{#i.count - 1}" value="%{#csvDriverStripMappingData.pcrfKey}"	name="csvDriverData.csvDriverStripMappingDataList[%{#i.count - 1}].pcrfKey" cssClass="form-control pcrf-key-suggestions"  elementCssClass="col-xs-12" />
                                        </td>

                                        <td><s:select list="@com.elitecore.corenetvertex.sm.driver.constants.Position@values()" listValue="getDisplayValue()" value="%{#csvDriverStripMappingData.pattern}"	name="csvDriverData.csvDriverStripMappingDataList[%{#i.count - 1}].pattern" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
                                        <td><s:textfield maxlength="1"  value="%{#csvDriverStripMappingData.separator}"	name="csvDriverData.csvDriverStripMappingDataList[%{#i.count - 1}].separator" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
                                        <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                    </tr>
                                </s:iterator>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="submit" class="btn btn-primary btn-sm" formaction="${pageContext.request.contextPath}/sm/driver/csv-driver/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<table id="tempStripMappingTable" style="display: none">
    <tr name="StripMappingRow">
        <td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" maxlength="255"/></td>
        <td><s:select list="@com.elitecore.corenetvertex.sm.driver.constants.Position@values()" listValue="getDisplayValue()" cssClass="form-control" elementCssClass="col-xs-12" ></s:select></td>
        <td><s:textfield cssClass="form-control"  elementCssClass="col-xs-12" maxlength="1" /></td>
        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
    </tr>
</table>

<script type="text/javascript">
    $(function(){
        $( ".select2" ).select2();
        setPcrfKeySuggestions();
        var headerField = $("#headerField");
    });

    var i = '<s:property value="csvDriverData.csvDriverFieldMappingDataList.size"/>';
    if(isNullOrEmpty(i)) {
        i = 0;
    }
    function addFieldMapping(){
        var tableRow= "<tr name='FieldMappingRow'>"+
            "<td><input maxlength='255' class='form-control' name='csvDriverData.csvDriverFieldMappingDataList["+i+"].headerField' id='headerField-"+i+"' type='text'></td>"+
            "<td><input maxlength='255' class='form-control pcrf-key-suggestions' name='csvDriverData.csvDriverFieldMappingDataList["+i+"].pcrfKey' id='FieldMappingPcrfKey-"+i+"'  type='text'></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
        $(tableRow).appendTo('#CsvFieldMappingTable');
        setPcrfKeySuggestions();
        i++;
    }


    var j = '<s:property value="csvDriverData.csvDriverStripMappingDataList.size"/>';

    if(isNullOrEmpty(j)) {
        j = 0;
    }

    function addStripMapping(){

        $("#CsvStripMappingTable tbody").append("<tr name='StripMappingRow'>" + $("#tempStripMappingTable").find("tr").html() + "</tr>");

        $("#CsvStripMappingTable").find("tr:last td:nth-child(1)").find("input").focus();
        var NAME = "name";
        $("#CsvStripMappingTable").find("tr:last td:nth-child(1)").find("input").attr(NAME,'csvDriverData.csvDriverStripMappingDataList['+j+'].pcrfKey');
        $("#CsvStripMappingTable").find("tr:last td:nth-child(1)").find("input").addClass("pcrf-key-suggestions");
        $("#CsvStripMappingTable").find("tr:last td:nth-child(1)").find("input").attr('id','StripMappingPcrfKey-'+j);
        $("#CsvStripMappingTable").find("tr:last td:nth-child(2)").find("select").attr(NAME,'csvDriverData.csvDriverStripMappingDataList['+j+'].pattern');
        $("#CsvStripMappingTable").find("tr:last td:nth-child(3)").find("input").attr(NAME,'csvDriverData.csvDriverStripMappingDataList['+j+'].separator');
        setPcrfKeySuggestions();
        j++;
    }

    function validateForm() {

        var isValidForm = verifyUniquenessOnSubmit('driverName', 'update', '<s:property value="id"/>', 'com.elitecore.corenetvertex.sm.driver.DriverData', '', '');

        if (isValidForm == false) {
            return false;
        }

        return validateNumericFields() && validateMapping();
    }


</script>

<%@include file="CsvDriverUtility.jsp"%>