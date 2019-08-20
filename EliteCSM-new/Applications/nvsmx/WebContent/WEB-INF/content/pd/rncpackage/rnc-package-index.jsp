<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>

<script type="text/javascript">

    function updateRncNotification(id,name) {
        $("#rncNotificationId").val(id);
        $("#duplicateEntityName").val("CopyOf" + name);
        $('#rncPackageCloningForm').attr('action', "${pageContext.request.contextPath}/pd/rncpackage/rnc-package/"+id+"/copymodel");
        $("#rncPackageCloningDialog").modal('show');
        $("#duplicateEntityName").focus();
        $("#method").val("post");
    }

    function updateNotificationInfo(data,type,thisBean){
        var notificationFunction = "javascript:updateRncNotification('"+thisBean.id+"','"+thisBean.name+"');"
        return "<a style='cursor:pointer' href="+notificationFunction+"><span class='glyphicon glyphicon-duplicate'></span></a>";
    }

</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="rncpackage.search"/> </h3>
    </div>

    <div class="panel-body">
        <div class="dataTable-button-groups">
            <span class="btn-group btn-group-sm" >
                <a href="${pageContext.request.contextPath}/pd/rncpackage/rnc-package/new" class="btn btn-default" role="button" id="btnAddrncpackage">
                    <span class="glyphicon glyphicon-plus" title="Add"></span>
                </a>
            </span>
        </div>
       <nv:dataTable
                id="rncPackageData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue"
                width="100%">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="Name" beanProperty="name" hrefurl="${pageContext.request.contextPath}/pd/rncpackage/rnc-package/$<id>" sortable="true"  />
            <nv:dataTableColumn title="Type" beanProperty="type"/>
            <nv:dataTableColumn title="Mode" beanProperty="mode"/>
            <nv:dataTableColumn title="Charging Type" beanProperty="chargingType"/>
            <nv:dataTableColumn title="Status" beanProperty="Status"/>
            <nv:dataTableColumn style="width:20px;" tdStyle="width:20px;"  renderFunction="updateNotificationInfo"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/rncpackage/rnc-package/$<id>/edit" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" disableWhen="mode==LIVE2" hrefurl="delete:${pageContext.request.contextPath}/pd/rncpackage/rnc-package/$<id>?_method=DELETE" style="width:20px;" />
        </nv:dataTable>
    </div>
</div>

<%@include file="rnc-duplicate-dialog.jsp"%>