<%--
  Created by IntelliJ IDEA.
  User: kirti.sachapara
  Date: 4/6/18
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>

<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<script>
    function networkRender(data, type , thisBean){
        return "<a href='${pageContext.request.contextPath}/sm/network/network/"+thisBean.networkData.id+"'>"+data+"</a>"
    }
</script>
<s:form id="lrnDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title"><s:text name="lrn.search"/> </h3>
        </div>
    <div class="panel-body">
    <div class="dataTable-button-groups">
				<span class="btn-group btn-group-sm" >
                     <a href="${pageContext.request.contextPath}/sm/lrn/lrn/new" class="btn btn-default" role="button" id="btnAddLrn">
                       <span class="glyphicon glyphicon-plus" title="Add"></span>
                     </a>
                    <span class="btn-group btn-group-sm" id="btnRemoveLrn"
                          data-toggle="confirmation-singleton"
                          onmousedown="return removeRecords(this,'lrnDataSearch');"
                          data-href="javascript:removeData('lrnDataSearch','${pageContext.request.contextPath}/sm/lrn/lrn/*/destroy');">
                                <button id="btnDeleteLrn" type="button" class="btn btn-default"
                                        data-toggle="tooltip" data-placement="right" title="delete"
                                        role="button">
                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                </button>
                          </span>
				</span>
            </div>
            <nv:dataTable
                    id="lrnData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    showFilter="true"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll' id='selectAll'/>" beanProperty="id"  style="width:5px !important;"/>
                <nv:dataTableColumn title="LRN" 		    beanProperty="lrn" hrefurl="${pageContext.request.contextPath}/sm/lrn/lrn/$<id>" sortable="true" tdCssClass="word-break" />
                <nv:dataTableColumn title="Operator Name" 		beanProperty="operatorData.name" sortable="true" tdCssClass="word-break"/>
                <nv:dataTableColumn title="Network Name" 		beanProperty="networkData.name" sortable="true" tdCssClass="word-break" renderFunction="networkRender"/>
                <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/lrn/lrn/$<id>/edit" style="width:20px;border-right:0px;"/>
                <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/lrn/lrn/$<id>?_method=DELETE" style="width:20px;"  />
            </nv:dataTable>
        </div>
    </div>
</s:form>

<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>