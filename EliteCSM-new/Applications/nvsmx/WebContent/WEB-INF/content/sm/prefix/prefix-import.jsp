<%--
  Created by IntelliJ IDEA.
  User: Khush
  Date: 23-01-2019
  Time: 02:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page import="com.google.gson.JsonArray"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>
<script src="${pageContext.request.contextPath}/js/ImportExportUtil.js"></script>
<script>

</script>

<%
    JsonArray importDataPrefix = (JsonArray) request.getAttribute("importedDataPrefix");
%>

<s:form  id="prefixImportTest" method="post" cssClass="form-vertical">i
    <s:set var="entityName" value= "%{@com.elitecore.corenetvertex.constants.Discriminators@PREFIX}"/>
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="prefix.import" />
            </h3>
        </div>

        <div class="panel-body">
            <button type="button" class="btn btn-sm btn-primary" role="button" onclick="importPackage('<s:text name="entityName"/>');">  <s:text name="prefix.import"/></button>
            <div class="text-danger"><s:property value="#request.invalidEntityMessage" /></div>
            <nv:dataTable
                    id="importPrefixData"
                    list="<%= importDataPrefix.toString() %>"
                    beanType="com.elitecore.corenetvertex.pd.prefix.PrefixData"
                    width="100%"
                    showPagination="false"
                    showInfo="false"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
                <nv:dataTableColumn title="Name" 		 beanProperty="prefix"  	 sortable="true" />
                <nv:dataTableColumn title="Prefix" 		 beanProperty="prefix"  	 sortable="true"  />
                <nv:dataTableColumn title="Country" 		 beanProperty="countryData.name"  	 sortable="true" />
                <nv:dataTableColumn title="Operator" 		 beanProperty="operatorData.name"  	 sortable="true" />
                <nv:dataTableColumn title="Network" 		 beanProperty="networkData.name"  	 sortable="true" />
            </nv:dataTable>
            <s:hidden name="userAction" id="userAction"></s:hidden>
            <s:hidden name="selectedIndexes" id="selectedIndexes"></s:hidden>
        </div>
    </div>
    </div>
    <script>
        $('table tr').find('td:eq(1),th:eq(1)').hide();
    </script>
    <%@include file="/view/policydesigner/ImportActionDialog.jsp"%>
</s:form>