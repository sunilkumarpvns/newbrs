<%@page import="com.google.gson.JsonArray"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>
<script src="${pageContext.request.contextPath}/js/ImportExportUtil.js"></script>
<%
  JsonArray importedChargingRuleBaseNames = (JsonArray) request.getAttribute("importedChargingRuleBaseNames");

%>

<s:form  id="chargingRuleBaseNameImportForm" method="post" cssClass="form-vertical">
  <s:set var="entityName" value= "%{@com.elitecore.corenetvertex.constants.Discriminators@CHARGING_RULE_BASE_NAME}"/>
  <div class="panel panel-primary">
    <div class="panel-heading">
      <h3 class="panel-title"><s:text name="chargingrulebasename.import"/>
        </h3>
    </div>

    <div class="panel-body">
      <button type="button" class="btn btn-sm btn-primary" role="button" onclick="importPackage('<s:text name="entityName"/>');">Import</button>
      <div class="text-danger"><s:property value="#request.invalidEntityMessage" /></div>

      <nv:dataTable
              id="importChargingRuleBaseNames"
              list="<%=importedChargingRuleBaseNames.toString() %>"
              beanType="com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData"
              width="100%"
              showPagination="false"
              showInfo="false"
              cssClass="table table-blue">
        <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
        <nv:dataTableColumn title="Name" 		  beanProperty="name"  	      sortable="true" />
        <nv:dataTableColumn title="Description"   beanProperty="description"  sortable="true" />
        <nv:dataTableColumn title="Groups" 		  beanProperty="groupNames"  	  sortable="true" />
      </nv:dataTable>
      <s:hidden name="userAction" id="userAction"></s:hidden>
      <s:hidden name="selectedIndexes" id="selectedIndexes"></s:hidden>
    </div>
  </div>
  </div>
  <%@include file="/view/policydesigner/ImportActionDialog.jsp"%>
</s:form>