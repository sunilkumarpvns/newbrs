<%@ page import="com.google.gson.JsonArray" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style>
    .subtable td:nth-child(odd) {
        text-align:left;
        font-weight:bold;
        width: 110px;
    }

    .subtable td:nth-child(even) {
        text-align:left;
        width: 110px;
    }

</style>

<script>
    /*
    * This function is used to provided custom rendering of name column of search page based
    * on Emergency or Data packages
    * * */
    function statusRenderingFunction(data, type, thisBean){
        if (data == '<s:property value="@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@ENTITY_IMPORT_FAIL" />') {
            return "<span style='color:red'>"+data+"</span>"
        } else {
            return "<span style='color:green'>"+data+"</span>"
        }
    }

</script>

<s:form id="deviceUploadStatus" method="post" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="import.status.report"/>
            </h3>
        </div>


        <div class="panel-body">
            <nv:dataTable
                    id="uploadStatusDataTable"
                    list="${uploadStatus}"
                    beanType="com.elitecore.corenetvertex.core.validator.Reason"
                    width="100%"
                    showPagination="false"
                    showInfo="false"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="TAC" 		 beanProperty="name"  	 sortable="true" />
                <nv:dataTableColumn title="Status" 	 	 beanProperty="messages" sortable="true"  renderFunction="statusRenderingFunction"/>
                <nv:dataTableColumn title="Remarks" 	 	 beanProperty="remarks"/>
            </nv:dataTable>
        </div>
    </div>
</s:form>