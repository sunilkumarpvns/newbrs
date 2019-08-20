<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 10/1/18
  Time: 6:40 PM
  To change this template use File | Settings | File Templates.
--%>

<style>
    .checkbox{
        margin-top: 5px;
        margin-bottom: -10px;
    }
</style>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<div class="row">
    <div class="col-xs-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><strong><s:text name="database.set.up" /></strong></h3>
            </div>

            <div class="panel-body">
                <s:actionerror escape="false" cssStyle=" margin-top: 0px;" />
                <s:form cssClass="form-vertical" validate="true" id="configureDatabaseForm" action="commons/databasesetup/DatabaseSetUp/configureDatabaseSetUp" validator="validateForm()">
                    <s:token />
                    <div class="row">
                        <div class="col-xs-12" >
                            <s:textfield  id="connectionUrl" key="database.set.up.connection.url" name="url" placeholder="jdbc:oracle:thin:@127.0.0.1:1521/orcl" cssClass="form-control"  />
                        </div>
                        <div class="col-xs-6">
                            <s:textfield id="userName" key="database.set.up.user.name" name="userName" placeholder="User Name" cssClass="form-control" />
                        </div>
                        <div class="col-xs-6">
                            <s:password id="password" key="database.set.up.password" name="password" placeholder="Password" cssClass="form-control" autocomplete="off" />
                        </div>
                    </div>
                    <table id="dataServiceType" class="table table-blue table-condensed table-bordered">
                        <caption class="caption-header">
                            <s:checkbox  key="database.set.up.create.schema" name="createDatabaseSchema" id="createDatabaseSchema" onchange="enableSchemaCreationFields()" />
                        </caption>
                        <tbody>
                            <tr>
                                <td>
                                    <div class="col-xs-6">
                                        <s:textfield id="dbAdmin" key="database.set.up.db.admin" name="dbAdmin" placeholder="DB Admin" cssClass="form-control" />
                                    </div>
                                    <div class="col-xs-6">
                                        <s:password id="dbPassword" key="database.set.up.db.password" name="dbPassword" placeholder="DB Password" cssClass="form-control" autocomplete="off" />
                                    </div>
                                    <div class="col-xs-12">
                                        <s:textfield id="dbfFile" key="database.set.up.dbf.file" name="dbfFile" placeholder="DBF File" cssClass="form-control" />
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="col-xs-12" align="center">
                        <s:submit cssClass="btn btn-primary btn-sm" formIds="databaseSetUpId" value="Configure"/>
                    </div>
                </s:form>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="pleaseWaitDialog" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" align="center">
    <div class="modal-dialog" style="margin:200px;">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title set-title"><s:text name="database.set.up.progress.bar.label"/></h4>
            </div>
            <div class="modal-body">
                <div class="progress">
                    <div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">

    $(function () {
        enableSchemaCreationFields();
    });

    function enableSchemaCreationFields() {
        var isCreateDatabaseSchema = $("#createDatabaseSchema").is(':checked');
        if(isCreateDatabaseSchema) {
            $("#dbAdmin").removeAttr("disabled");
            addRequiredCustomized($("#dbAdmin"));
            $("#dbPassword").removeAttr("disabled");
            addRequiredCustomized($("#dbPassword"));
            $("#dbfFile").removeAttr("disabled");
            addRequiredCustomized($("#dbfFile"));
        } else {
            $("#dbAdmin").attr("disabled","disabled");
            removeRequiredCustomized($("#dbAdmin"));
            $("#dbPassword").attr("disabled","disabled");
            removeRequiredCustomized($("#dbPassword"));
            $("#dbfFile").attr("disabled","disabled");
            removeRequiredCustomized($("#dbfFile"));
        }
    }

    function addRequiredCustomized(element) {
        var id = $(element).attr('id');
        var curElement = $("#lbl_".concat(id));
        if($("#lbl_".concat(id).length)){
            curElement.html(curElement.html().concat(" <span class=\"required\">*</span>"));
        }
    }
    function removeRequiredCustomized(element) {
        var id = $(element).attr('id');
        var curElement = $("#lbl_".concat(id));
        $(curElement).find(".required").remove();
    }

    function validateForm() {

        var isCreateDatabaseSchema = $("#createDatabaseSchema").is(':checked');
        var dbAdmin = $("#dbAdmin").val();
        var dbPassword = $("#dbPassword").val();
        var dbfFile = $("#dbfFile").val();

        if(isCreateDatabaseSchema && isNullOrEmpty(dbAdmin)) {
            setError("dbAdmin","<s:text name='error.valueRequired' />");
            return false;
        } else if (isCreateDatabaseSchema && isNullOrEmpty(dbPassword)) {
            setError("dbPassword","<s:text name='error.valueRequired' />");
            return false;
        } else if (isCreateDatabaseSchema && isNullOrEmpty(dbfFile)) {
            setError("dbfFile","<s:text name='error.valueRequired' />");
            return false;
        }
        showProgressBar();
    }

    var timer;
    function showProgressBar(){
        console.log("show Progress Bar called");
        var pleaseWait = $('#pleaseWaitDialog');
        $(pleaseWait).modal({backdrop: 'static', keyboard: false});
        showPleaseWait = function() {
            console.log("showPleaseWait called");
            pleaseWait.modal('show');
            timer = setInterval(isExportCompleted,5000);

        };

        hidePleaseWait = function () {
            pleaseWait.modal('hide');
        };
        showPleaseWait();
    }
</script>
