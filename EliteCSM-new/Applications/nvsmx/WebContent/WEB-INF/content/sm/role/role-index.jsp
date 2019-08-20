<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
  int rows = ConfigurationProvider.getInstance().getPageRowSize();

%>
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

  .collapsing {
    -webkit-transition: none;
    transition: none;
  }
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/import.css"/>
<%
  String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>
<s:form  id="RoleDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
  <div class="panel panel-primary">
    <div class="panel-heading">
      <h3 class="panel-title"><s:text name="role.search"/></h3>
    </div>

    <div class="panel-body">
      <div class="dataTable-button-groups">
					<span class="btn-group btn-group-sm" >
						<a href="${pageContext.request.contextPath}/sm/role/role/new" class="btn btn-default" role="button" id="btnAddGroup">
                          <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
						<%--<span class="btn-group btn-group-sm"  id="btnRemove" data-toggle="confirmation-singleton" onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
							<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                            </button>
						</span>
						<span class="btn-group btn-group-sm"  id="btnImportGroup">
							<button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse" data-target="#import" aria-expanded="false" aria-controls="import" data-placement="right" title="import" role="button">
                                <span class="glyphicon glyphicon-import" title="Import"></span>
                            </button>
						</span>
						<span class="btn-group btn-group-sm"  id="btnExportPkg" data-toggle="confirmation-singleton" onclick="return exportPackage(this);" onmousedown="return exportPackage(this);" data-href="javascript:exportData();">
							<button id="btnExport" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="export" role="button">
                                <span class="glyphicon glyphicon-export" title="export"></span>
                            </button>
						</span>
						
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" data-target="#exportAll" aria-expanded="false">
                            <span class="caret"></span>
                            <span class="sr-only">Toggle Dropdown</span>
                        </button>
		  				<span id="exportAll">
		 					<ul class="dropdown-menu" >
                                <li><a href="javascript:exportAll();">Export All</a></li>
                            </ul>
						</span>
						
						--%>
					</span>
      </div>

      <nv:dataTable
              id="roleData"
              list="${dataListAsJson}"
              rows="<%=rows%>"
              width="100%"
              showPagination="true"
              showFilter="true"
              cssClass="table table-blue">
        <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
        <nv:dataTableColumn title="Name" 		 beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/role/role/$<id>" sortable="true" style="width:200px;" />
        <nv:dataTableColumn title="Description"  beanProperty="description"  sortable="true"	/>
        <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/role/role/$<id>/edit" style="width:20px;border-right:0px;"/>
        <nv:dataTableColumn title="" 	 icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/role/role/$<id>?_method=DELETE" 	 style="width:20px;"  />
      </nv:dataTable>
    </div>
  </div>
</s:form>
