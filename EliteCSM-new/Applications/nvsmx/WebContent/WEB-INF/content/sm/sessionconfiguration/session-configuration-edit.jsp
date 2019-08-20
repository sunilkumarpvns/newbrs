<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 31/8/17
  Time: 4:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.elitecore.corenetvertex.constants.BatchUpdateMode" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
           <s:text name="session.conf.update" />
        </h3>
    </div>
    <div class="panel-body" >
        <s:form namespace="/sm/sessionconfiguration" action="session-configuration" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" id="sessionconf" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-10">
                    <s:select name="databaseId" key="session.conf.database" cssClass="form-control focusElement" list="databaseDataList" listKey="id" listValue="name" />
                </div>
                <fieldset class="fieldSet-line">
                    <legend align="top"><s:text name="session.conf.batch.operation"/></legend>
                    <div class="col-sm-10">
                        <s:select name="batchMode" value="batchMode" key="session.conf.batchmode" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.BatchUpdateMode@values()" listKey="getValue()" listValue="getDisplayValue()" id="batchMode" />
                        <s:textfield name="batchSize" key="session.conf.batchsize" cssClass="form-control" maxlength="4" id="batchSize" onkeypress="return isNaturalInteger(event);" />
                        <s:textfield name="queryTimeout" key="session.conf.querytimeout" cssClass="form-control" maxlength="2" id="queryTimeout" onkeypress="return isNaturalInteger(event);" />
                    </div>
                </fieldset>
                <fieldset class="fieldSet-line">
                    <legend align="top"><s:text name="session.conf.field.mapping"/></legend>
                    <div id="sessionConfFieldMappingDiv">
                        <div class="col-xs-12 col-sm-12">
                            <table id='sessionConfFieldMappingTable'  class="table table-blue table-bordered">
                                <caption class="caption-header"><s:text name="session.conf.field.mapping.session" />
                                    <div align="right" class="display-btn">
                                        <span class="btn btn-group btn-group-xs defaultBtn" onclick="addFieldMapping();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                                    </div>
                                </caption>
                                <thead>
                                    <th><s:text name="session.conf.fieldmapping.fieldname"/></th>
                                    <th><s:text name="session.conf.fieldmapping.refferingattr"/></th>
                                    <th><s:text name="session.conf.fieldmapping.datatype"/></th>
                                    <th style="width:35px;">&nbsp;</th>
                                </thead>
                                <tbody>
                                <s:iterator value="sessionConfigurationFieldMappingDatas" status="i" var="sessionConfigurationFieldMappingData">
                                    <tr name='FieldMappingRow'>
                                        <td><s:textfield value="%{#sessionConfigurationFieldMappingData.fieldName}"	name="sessionConfigurationFieldMappingDatas[%{#i.count - 1}].fieldName" cssClass="form-control fieldMapping"  elementCssClass="col-xs-12" id="fieldMapping-%{#i.count - 1}" /></td>
                                        <td><s:textfield value="%{#sessionConfigurationFieldMappingData.referringAttribute}"	name="sessionConfigurationFieldMappingDatas[%{#i.count - 1}].referringAttribute" cssClass="form-control referAttributeSuggestions"  elementCssClass="col-xs-12" id="referAttribute-%{#i.count - 1}"></s:textfield></td>
                                        <td><s:select list="@com.elitecore.corenetvertex.constants.FieldMappingDataType@values()" listValue="getDisplayValue()" listKey="getValue()" value="%{#sessionConfigurationFieldMappingData.dataType}"	name="sessionConfigurationFieldMappingDatas[%{#i.count - 1}].dataType" cssClass="form-control"  elementCssClass="col-xs-12" ></s:select></td>
                                        <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                                    </tr>
                                </s:iterator>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </fieldset>

                <div class="row">
                    <div class="col-xs-12" align="center">

                        <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/sessionconfiguration/session-configuration/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/sessionconfiguration/session-configuration/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>
<table id="tempFieldMappingTable" style="display: none;">
    <tr>
        <td><s:textfield cssClass="form-control" elementCssClass="col-xs-12"/></td>
        <td><s:textfield cssClass="form-control"  elementCssClass="col-xs-12" /></td>
        <td><s:select list="@com.elitecore.corenetvertex.constants.FieldMappingDataType@values()" listValue="getDisplayValue()" listKey="getValue()"  cssClass="form-control"  elementCssClass="col-xs-12" ></s:select></td>
        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
    </tr>
</table>
<script type="text/javascript">

    $(function () {
       setReferAttributes();
       verifyBatchMode();

    });

    $("#batchMode").change(function () {
        verifyBatchMode();
    });
    function setReferAttributes() {
        commonAutoCompleteUsingCssClass('.referAttributeSuggestions',<s:property value="@com.elitecore.nvsmx.system.util.NVSMXUtil@getPcrfKeySuggestions('request','response')" escapeHtml="false" />);
    }
    var i = document.getElementsByName("FieldMappingRow").length;
    if(isNullOrEmpty(i)) {
        i = 0;
    }
    function addFieldMapping() {
        $("#sessionConfFieldMappingTable tbody").append("<tr>" + $("#tempFieldMappingTable").find("tr").html() + "</tr>");

        $("#sessionConfFieldMappingTable").find("tr:last td:nth-child(1)").find("input").focus();
        var NAME = "name";
        $("#sessionConfFieldMappingTable").find("tr:last td:nth-child(1)").find("input").attr(NAME,'sessionConfigurationFieldMappingDatas['+i+'].fieldName').attr('id','fieldMapping-'+i).attr('class','form-control fieldMapping');
        $("#sessionConfFieldMappingTable").find("tr:last td:nth-child(2)").find("input").attr(NAME,'sessionConfigurationFieldMappingDatas['+i+'].referringAttribute').attr('class','form-control referAttributeSuggestions');
        $("#sessionConfFieldMappingTable").find("tr:last td:nth-child(2)").find("input").attr('id','referAttribute-'+i);
        $("#sessionConfFieldMappingTable").find("tr:last td:nth-child(3)").find("select").attr(NAME,'sessionConfigurationFieldMappingDatas['+i+'].dataType');
        setReferAttributes();
        i++;
    }
    
    function verifyBatchMode() {
       var batchMode = $("#batchMode").val();
       if(isNullOrEmpty(batchMode)  || batchMode == <%=BatchUpdateMode.FALSE.getValue()%>) {
           $("#batchSize").attr("readOnly","readOnly");
           $("#batchUpdateInterval").attr("readOnly","readOnly");
           $("#queryTimeout").attr("readOnly","readOnly");
       } else {
           $("#batchSize").removeAttr("readOnly");
           $("#batchUpdateInterval").removeAttr("readOnly");
           $("#queryTimeout").removeAttr("readOnly");
       }
    }
    function validateForm(){
        return validateBatchParameters() && validateFieldMappingAndReferringAttributes();

    }
    function validateFieldMappingAndReferringAttributes(){
        var isValidMapping = true;
        $( ".fieldMapping" ).each(function( index ) {
            if (isNullOrEmpty($( this ).val())) {
                setError($( this ).attr('id'), '<s:text name="session.conf.fieldmapping.required" />');
                isValidMapping = false;
            }

        });

        $( ".referAttributeSuggestions" ).each(function( index ) {
                if (isNullOrEmpty($( this ).val())) {
                    setError($( this ).attr('id'), '<s:text name="session.conf.referringattr.required" />');
                    isValidMapping = false;
                }
        });
        return isValidMapping;

    }

    function validateBatchParameters(){
        var batchMode = $("#batchMode").val();
        if(batchMode != <%=BatchUpdateMode.FALSE.getValue()%>){
            var batchSize = $("#batchSize").val();
            var queryTimeout = $("#queryTimeout").val();
            if(isNumberGreaterThanZero(batchSize) == false){
                setError("batchSize","<s:text name="session.conf.invalid.batchsize" />");
                return false;
            }else if(batchSize < <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@BATCH_SIZE_MIN" /> || batchSize > <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_BATCH_SIZE" />){
                setError("batchSize","<s:text name="session.conf.invalid.batchsize.range" />");
                return false;
            }
            if(isNumberGreaterThanZero(queryTimeout) == false){
                setError("queryTimeout","<s:text name="session.conf.invalid.querytimeout" />");
                return false;
            } else if(queryTimeout < <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MIN_QUERY_TIMEOUT_IN_SEC" /> || queryTimeout > <s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@MAX_QUERY_TIMEOUT_IN_SEC" />){
                setError("queryTimeout","<s:text name="invalid.query.timeout"/>");
                return false;
            }

        }
        return true;

    }

</script>
