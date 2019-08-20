<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

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
<s:form  id="groupDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="group.search"/>
            </h3>
        </div>

        <div class="panel-body">
            <div class="dataTable-button-groups">
					<span class="btn-group btn-group-sm" >
						<a href="${pageContext.request.contextPath}/sm/group/group/new" class="btn btn-default" role="button" id="btnAddGroup">
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
					</span>
            </div>

            <nv:dataTable
                    id="groupData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    showFilter="true"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
                <nv:dataTableColumn title="Name" 		 beanProperty="Name" hrefurl="${pageContext.request.contextPath}/sm/group/group/$<id>" sortable="true" style="width:200px;" />
                <nv:dataTableColumn title="Description"  	     beanProperty="Description"  	/>
                <nv:dataTableColumn title="Status" 	 	 beanProperty="Status" 			sortable="true" />
                <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/group/group/$<id>/edit" style="width:20px;border-right:0px;"/>
             </nv:dataTable>

        </div>
    </div>
</s:form>
<script type="text/javascript">

    function createGroup(){
        document.forms["groupDataSearch"].action = "${pageContext.request.contextPath}/sm/group/group/new";
        document.forms["groupDataSearch"].submit();
    }
</script>
