<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>
<script src="${pageContext.request.contextPath}/js/ImportExportUtil.js"></script>

<%
  JsonArray importImsPkgs = (JsonArray) request.getAttribute("importedImsPkgs");
%>

<s:form  id="imsPkgImportTest" method="post" cssClass="form-vertical">
  <s:set var="entityName" value= "%{@com.elitecore.corenetvertex.constants.Discriminators@IMS_PACKAGE}"/>
  <div class="panel panel-primary">
    <div class="panel-heading">
      <h3 class="panel-title">
        <s:text name="ims.pkg.import" />
      </h3>
    </div>


    <div class="panel-body">
      <button type="button" class="btn btn-sm btn-primary" role="button" onclick="importPackage('<s:text name="entityName"/>');">Import</button>
      <div class="text-danger"><s:property value="#request.invalidEntityMessage" /></div>

      <nv:dataTable
              id="importImsPackageData"
              list="<%=importImsPkgs.toString()%>"
              width="100%"
              showPagination="false"
              showInfo="false"
              cssClass="table table-blue">
        <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
        <nv:dataTableColumn title="Name" 		 beanProperty="Name"  	 sortable="true" />
        <nv:dataTableColumn title="Status" 	 	 beanProperty="Status" 			sortable="true" />
        <nv:dataTableColumn title="Mode" 	 	 beanProperty="Mode" 	sortable="true" />
      </nv:dataTable>
      <s:hidden name="userAction" id="userAction"></s:hidden>
      <s:hidden name="selectedIndexes" id="selectedIndexes"></s:hidden>
    </div>
  </div>
  </div>
  <%@include file="/view/policydesigner/ImportActionDialog.jsp"%>
</s:form>
