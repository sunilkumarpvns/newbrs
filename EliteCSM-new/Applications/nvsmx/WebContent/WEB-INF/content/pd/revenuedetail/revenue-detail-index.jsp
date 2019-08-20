<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<s:form  id="revenueDetail" method="post"  enctype="multipart/form-data" cssClass="form-vertical">
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="revenue.detail.search"/> </h3>
    </div>

    <div class="panel-body">
        <div class="dataTable-button-groups">
            <span class="btn-group btn-group-sm">
                <a href="${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/new"
                   class="btn btn-default" role="button" id="btnAddRevenueDetail">
                    <span class="glyphicon glyphicon-plus" title="Add"></span>
                </a>
                <span class="btn-group btn-group-sm" id="btnRemoveCity"
                      data-toggle="confirmation-singleton" onmousedown="return removeRecords(this);"
                      data-href="javascript:removeData();">
                <button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
                        <span class="glyphicon glyphicon-trash" title="delete"></span>
                    </button>
                </span>
            </span>
        </div>

        <nv:dataTable
                id="RevenueDetailData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />" beanProperty="id"
                                style="width:5px !important;"/>
            <nv:dataTableColumn title="Name" beanProperty="name"
                                hrefurl="${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/$<id>"
                                sortable="true"/>
            <nv:dataTableColumn title="Id" beanProperty="id" sortable="true"/>
            <nv:dataTableColumn title="Description" beanProperty="description"/>
            <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                hrefurl="edit:${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/$<id>/edit"
                                style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""
                                hrefurl="delete:${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/$<id>?_method=DELETE"
                                style="width:20px;"/>
        </nv:dataTable>
    </div>
</div>
    </s:form>
<script>
    function removeRecords(obj) {
        var selectVar = getSelectedValuesForDelete();
        if(selectVar == true){
            var flag = false;
            flag = deleteConfirmMsg(obj,"Delete selected Entity?");
            if(flag==true) {
                removeData();
            }
            return flag;
        }
    }

    function removeData(){
        document.forms["revenueDetail"].action = "${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/*/destroy";
        document.forms["revenueDetail"].submit();
    }

    function getSelectedValuesForDelete(){
        var selectedData = false;
        var selectedDatas = document.getElementsByName("ids");
        for (var index=0; index < selectedDatas.length; index++){
            if(selectedDatas[index].name == 'ids'){
                if(selectedDatas[index].checked == true){
                    selectedData = true;
                }
            }


        }
        if(selectedData == false){
            return addWarning(".popup","At least select one revenue detail for delete");
        }
        return selectedData;
    }
</script>