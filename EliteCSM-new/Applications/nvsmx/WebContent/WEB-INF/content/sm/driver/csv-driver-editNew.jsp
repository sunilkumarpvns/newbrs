<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 10/10/17
  Time: 12:34 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="driver.csv.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/driver" action="csv-driver" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()" id="csvdriverform">
            <s:token/>
            <div class="row">
                <div class="col-xs-12 col-sm-6">
                    <s:textfield name="name" key="csv.name" id="driverName" cssClass="form-control focusElement" onblur="verifyUniqueness('driverName','create','','com.elitecore.corenetvertex.sm.driver.DriverData','','');" />
                    <s:textarea name="description" key="csv.description" cssClass="form-control" rows="2" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"  />
                    <s:select list="@com.elitecore.corenetvertex.sm.driver.constants.ReportingType@values()" listKey="name()" listValue="getDisplayValue()" name="csvDriverData.reportingType" key="csv.reporting.type" cssClass="form-control" headerValue="Select" headerKey=""></s:select>
                </div>
                <div class="col-xs-12 col-sm-6">
                    <s:select value="false" id="headerField" name="csvDriverData.header" key="csv.header" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listValue="getStringNameBoolean()" listKey="getStringNameBoolean()" />
                    <s:textfield name="csvDriverData.delimiter" key="csv.delimiter" cssClass="form-control" value="," maxlength="1" />
                    <s:textfield name="csvDriverData.timeStampFormat" key="csv.cdr.timestamp" cssClass="form-control" value="EEE dd MMM,yyyy,hh:mm:ss aaa" maxlength="128" />
                </div>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.file.parameters"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.fileName" key="csv.file.name" cssClass="form-control" value="CDRs.csv" maxlength="64" />
                            <s:textfield name="csvDriverData.fileLocation" key="csv.location" cssClass="form-control" value="data/csvfiles" maxlength="512" />
                            <s:textfield name="csvDriverData.prefixFileName" key="csv.prefix.file.name" cssClass="form-control" maxlength="64" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.defaultFolderName" key="csv.default.folder.name" cssClass="form-control" maxlength="64" />
                            <s:textfield name="csvDriverData.folderName" key="csv.folder.name" cssClass="form-control" maxlength="64" />
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.file.rolling.parameters"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:select list="@com.elitecore.corenetvertex.sm.driver.constants.TimeUnits@values()" name="csvDriverData.timeBoundary" key="csv.time.boundary" cssClass="form-control" listValue="getVal()" listKey="getKey()" headerKey="" headerValue="Select"  ></s:select>
                            <s:textfield id="timeBasedRollingUnit" name="csvDriverData.timeBasedRollingUnit" key="csv.time.based.rolling.unit" cssClass="form-control" onkeypress="return isNaturalInteger(event);" min="0" maxLength="12" />
                            <s:textfield id="sizeBasedRollingUnit" name="csvDriverData.sizeBasedRollingUnit" key="csv.size.based.rolling.unit" cssClass="form-control" onkeypress="return isNaturalInteger(event);" min="0" maxLength="12" />
                            <s:textfield id="recordBasedRollingUnit" name="csvDriverData.recordBasedRollingUnit" key="csv.record.based.rolling.unit" cssClass="form-control" onkeypress="return isNaturalInteger(event);" min="0" maxLength="12" />
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.sequenceRange" key="csv.sequence.range" cssClass="form-control" maxlength="40" id="sequenceRange"/>
                            <s:select list="@com.elitecore.corenetvertex.sm.driver.constants.Position@values()" name="csvDriverData.sequencePosition" key="csv.sequence.position" cssClass="form-control" listValue="getDisplayValue()" ></s:select>
                            <s:select list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" name="csvDriverData.sequenceGlobalization" key="csv.sequence.globalization" cssClass="form-control" listValue="getStringNameBoolean()" listKey="getStringNameBoolean()" value="false" ></s:select>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="csv.file.transfer.parameters"/> </legend>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6">
                            <s:select list="@com.elitecore.corenetvertex.sm.driver.constants.FileTransferProtocol@values()" name="csvDriverData.allocatingProtocol" key="csv.allocating.protocol" cssClass="form-control" id="allocatingProtocol" onchange="disablePasswordWhenAllocatingProtocolIsDisabled()" ></s:select>
                            <s:textfield name="csvDriverData.address" key="csv.address" cssClass="form-control" value="127.0.0.1:22" maxlength="255" />
                            <s:textfield name="csvDriverData.remoteFileLocation" key="csv.remote.location" cssClass="form-control" maxlength="512" />
                            <s:textfield id="failOverTime" name="csvDriverData.failOverTime" key="csv.fail.over.time" cssClass="form-control" onkeypress="return isNaturalInteger(event);" value="3" min="0" maxLength="10"/>
                        </div>
                        <div class="col-xs-12 col-sm-6">
                            <s:textfield name="csvDriverData.userName" key="csv.user.name" cssClass="form-control" maxlength="64" />
                            <s:textfield name="csvDriverData.password" key="csv.password" cssClass="form-control" type="password" id="password" maxlength="64" />
                            <s:textfield name="passwordData.confirmPassword" type="password" key="csv.confirm.password" cssClass="form-control"  id="confirmPassword" maxlength="64" />
                            <s:textfield name="csvDriverData.archiveLocation" key="csv.archive.location" cssClass="form-control" value="data/csvfiles/archive" maxlength="255" />
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
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<table id="tempStripMappingTable" style="display: none">
    <tr name="StripMappingRow">
        <td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" maxlength="255"/></td>
        <td><s:select list="@com.elitecore.corenetvertex.sm.driver.constants.Position@values()" listValue="getDisplayValue()" cssClass="form-control" elementCssClass="col-xs-12" ></s:select></td>
        <td><s:textfield cssClass="form-control"  elementCssClass="col-xs-12" maxlength="1"/></td>
        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
    </tr>
</table>

<script type="text/javascript">

    $(function(){
        $( ".select2" ).select2();
        setPcrfKeySuggestions();
        disablePasswordWhenAllocatingProtocolIsDisabled();
    });

    var i=0;
    function addFieldMapping(){
        var tableRow= "<tr name='FieldMappingRow'>"+
            "<td><input maxlength='255' class='form-control' name='csvDriverData.csvDriverFieldMappingDataList["+i+"].headerField' id='headerField-"+i+"' type='text'/></td>"+
            "<td><input maxlength='255' class='form-control pcrf-key-suggestions' name='csvDriverData.csvDriverFieldMappingDataList["+i+"].pcrfKey' id='FieldMappingPcrfKey-"+i+"'  type='text'/></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
        $(tableRow).appendTo('#CsvFieldMappingTable');
        setPcrfKeySuggestions();
        i++;
    }

    var j=0;

    function addStripMapping() {

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

    function disablePasswordWhenAllocatingProtocolIsDisabled() {
        var allocatingProtocol = $("#allocatingProtocol").val();

        if('<s:property value="@com.elitecore.corenetvertex.sm.driver.constants.FileTransferProtocol@LOCAL.name()"/>' == allocatingProtocol ) {
            $("#password").attr("disabled","disabled");
            $("#confirmPassword").attr("disabled","disabled");
        } else {
            $("#password").removeAttr("disabled");
            $("#confirmPassword").removeAttr("disabled");
        }

    }


    function validateForm() {

        var isValidName = verifyUniquenessOnSubmit('driverName','create','','com.elitecore.corenetvertex.sm.driver.DriverData','','');

        var password = $("#password").val();
        var confirmPassword = $("#confirmPassword").val();
        var allocatingProtocol = $("#allocatingProtocol").val();

        if(isValidName == false) {
            return false;
        } else if('<s:property value="@com.elitecore.corenetvertex.sm.driver.constants.FileTransferProtocol@LOCAL.name()"/>' != allocatingProtocol && isNullOrEmpty(password) == false) {
            if(isNullOrEmpty(confirmPassword)) {
                setError('confirmPassword', "Confirm Password is required");
                return false;
            } else if(password != confirmPassword) {
                setError('confirmPassword', "Password and Confirm Password should be same");
                return false;
            }
        }


        return validateNumericFields() && validateMapping();
    }

</script>

<%@include file="CsvDriverUtility.jsp"%>
