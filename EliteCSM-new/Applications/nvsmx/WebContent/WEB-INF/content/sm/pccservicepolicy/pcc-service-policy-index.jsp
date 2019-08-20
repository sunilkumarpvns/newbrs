<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>

<%--
  Created by IntelliJ IDEA.
  User: kirpalsinh
  Date: 6/12/17
  Time: 3:44 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/datatables/js/commons.dataTables.js"></script>
<script src="${pageContext.request.contextPath}/datatables/js/jquery.dataTables.js"></script>

<%  int rows = ConfigurationProvider.getInstance().getPageRowSize(); %>
<s:form  id="pccServicePolicySearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="pccServicePolicy.search" />
        </h3>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-3 col-xs-5">
                <span class="btn-group btn-group-sm" >
                    <a href="${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/new"
                       class="btn btn-default" role="button" id="btnAdd">
                        <span class="glyphicon glyphicon-plus" title="Add"></span>
                    </a>
                      <span class="btn-group btn-group-sm" id="btnRemovePCCServicePolicy"
                            data-toggle="confirmation-singleton"
                            onmousedown="return removeRecords(this,'pccServicePolicySearch');"
                            data-href="javascript:removeData('pccServicePolicySearch','${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/*/destroy');">
                           <button id="btnDeletePCCServicePolicy" type="button" class="btn btn-default"
                                   data-toggle="tooltip" data-placement="right" title="delete"
                                   role="button">
                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                            </button>
                      </span>
                </span>
            </div>
            <div class="col-sm-7 col-xs-7 collapse" id="import" style="display:block;">
            </div>
            <div class="row col-sm-2 col-xs-12">
                <s:if test="%{getList().isEmpty() || getList().size()==1}">
                    <button class="btn btn-primary btn-xs" disabled style="float:right;padding-top: 3px; padding-bottom: 3px" onclick="">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" style="float:right;padding-top: 3px; padding-bottom: 3px" type="button"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/*/initManageOrder'">
                        <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                        <s:text name="manage.order"/>
                    </button>
                </s:else>
            </div>
        </div>

        <nv:dataTable
           id="pccServicePolicies"
           list="${dataListAsJson}"
           rows="<%=rows%>"
           width="100%"
           showPagination="true"
           cssClass="table table-blue">
           <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll'/>"  beanProperty="id" sortable="false"  style="width:5px !important;" />
           <nv:dataTableColumn title="Name" 		 beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/$<id>" sortable="true" style="width:200px;" />
           <nv:dataTableColumn title="Description"  beanProperty="description" />
           <nv:dataTableColumn title="Order Number"  beanProperty="orderNumber" />
           <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/$<id>/edit" style="width:20px;border-right:0px;"/>
           <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/$<id>?_method=DELETE" 	 style="width:20px;"  />
        </nv:dataTable>
    </div>
</div>
</s:form>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>
