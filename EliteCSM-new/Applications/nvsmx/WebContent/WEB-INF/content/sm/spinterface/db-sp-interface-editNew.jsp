<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 8/9/17
  Time: 1:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.elitecore.corenetvertex.constants.SpInterfaceType" %>
<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<style>
    #FieldMappingRow > td {
        padding-bottom: 1px;
    }

</style>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="sp.interface.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/spinterface" action="db-sp-interface" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()" id="dbSpInterface">
            <s:token/>
            <div class="row">
                <div class="col-sm-6">
                    <s:textfield name="name" key="sp.interface.name" id="spInterfaceName" cssClass="form-control focusElement" />
                    <s:textarea name="description" key="sp.interface.description" cssClass="form-control" rows="2" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}" />
                    <s:select name="dbSpInterfaceData.databaseId" key="sp.interface.db.database" cssClass="form-control" list="databaseDataList"  listValue="getName()" listKey="getId()" id="databaseId" />
                </div>
                <div class="col-sm-6">
                    <s:textfield name="dbSpInterfaceData.tableName" key="sp.interface.db.tablename" cssClass="form-control" onfocus="getSelectedDatabaseTableNames(this);" id="tableName" />
                    <s:textfield name="dbSpInterfaceData.identityField" key="sp.interface.db.identityfield" cssClass="form-control" onfocus="getSelectedTableColumnNames(this)" id="identityField" />
                    <s:textfield name="dbSpInterfaceData.maxQueryTimeoutCount"  key="sp.interface.db.maxquerytimeoutcount" cssClass="form-control" onkeypress="return isNaturalInteger(event);" maxlength="5"  />
                </div>
            </div>
            <div id="fieldMappingDiv">
                <div class="col-xs-12 col-sm-12">
                    <table id='fieldMappingTable'  class="table table-blue table-bordered">
                        <caption class="caption-header"><s:text name="sp.interface.field.mapping" />
                            <div align="right" class="display-btn">
                                <span class="btn btn-group btn-group-xs defaultBtn" onclick="$('#spInterfaceFieldMappingDialogId').modal('show');" id="addRow"> <span class="glyphicon glyphicon-plus"> </span></span>
                            </div>
                        </caption>
                        <thead>
                            <th><s:text name="sp.interface.field.mapping.logicalname" /></th>
                            <th><s:text name="sp.interface.field.mapping.fieldname" /></th>
                            <th style="width:35px;">&nbsp;</th>
                        </thead>
                        <tbody>
                            <s:iterator value="predefinedAttributes" status="i" var="predefinedAttribute">
                                <tr name='FieldMappingRow'>
                                    <td><s:property value="%{#predefinedAttribute.displayName}" /><s:hidden name="dbSpInterfaceData.spInterfaceFieldMappingDatas[%{#i.count - 1}].logicalName" value="%{#predefinedAttribute.name()}" /></td>
                                    <td><s:property value="%{#predefinedAttribute.columnName}" /><s:hidden name="dbSpInterfaceData.spInterfaceFieldMappingDatas[%{#i.count - 1}].fieldName" value="%{#predefinedAttribute.columnName}"/></td>
                                    <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                </tr>
                            </s:iterator>
                        </tbody>
                    </table>
                    <div class="col-xs-12" id="generalError"></div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/spinterface/db-sp-interface'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script>

    var spInterfaceType = '<%=SpInterfaceType.DB_SP_INTERFACE.name()%>';
    function validateForm() {
        clearErrorMessages();
        var isValidName = verifyUniquenessOnSubmit('spInterfaceName', 'create', '', 'com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData', '', '');
        if(isValidName == false) {
            return isValidName;
        }
        return validateFieldMappings() && isSubscriberIdentityMapped() && isProductOfferMapped();
    }
    $(function(){
        $( ".select2" ).select2();
        $("#tempFieldNameId").focus(function () {
            getSelectedTableColumnNames(this);
        });
    });



</script>

<%@include file="SpInterfaceFieldMappingDialog.jsp"%>
<%@include file="../utility/fetch-tables-and-columns.jsp"%>
<%@include file="sp-interface-utlity.jsp"%>