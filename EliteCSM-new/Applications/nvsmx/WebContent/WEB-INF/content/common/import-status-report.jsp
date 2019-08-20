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
<%
    JsonArray reasons = (JsonArray) request.getAttribute("importStatus");
    String sender = (String) request.getAttribute("Sender");
    String SenderPackage = (String) request.getAttribute("SenderPackage");
%>
<script>
    function downloadCSV() {
        document.forms["pkgImport"].action = "${pageContext.request.contextPath}/<%=SenderPackage%>/<%=sender%>/<%=sender%>/*/mergeValuesAndStatusJSP";
        document.forms["pkgImport"].submit();
    }
</script>

<s:form id="pkgImport" method="post" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="import.status.report"/>
            </h3>
        </div>


        <div class="panel-body">
            <%
                if (!(sender == null || sender == "")) {
                    if (sender.equalsIgnoreCase("prefix")) {
            %>
            <button onclick="javascript:downloadCSV()" class="btn btn-sm btn-primary"><s:text
                    name="import.status.report.download"/></button>
            <%
                    }
                }
            %>
            <nv:dataTable
                    id="importPackagestatus"
                    list="<%= reasons.toString() %>"
                    beanType="com.elitecore.corenetvertex.core.validator.Reason"
                    width="100%"
                    showPagination="false"
                    showInfo="false"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="Name" 		 beanProperty="name"  	 sortable="true" />
                <nv:dataTableColumn title="Status" 	 	 beanProperty="messages" 			sortable="true" />
                <nv:dataTableColumn title="Remarks" 	 	 beanProperty="remarks" 	sortable="true" />
            </nv:dataTable>
        </div>
    </div>
</s:form>