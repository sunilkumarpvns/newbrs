<%--
  Created by IntelliJ IDEA.
  User: arpit
  Date: 21/9/18
  Time: 11:17 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<s:form  id="globalRateCard" method="post"  enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title"><s:text name="ratecard.detail"/> </h3>
        </div>

        <div class="panel-body">
            <div class="dataTable-button-groups">
				<span class="btn-group btn-group-sm">
						<a href="${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/new" class="btn btn-default" role="button" >
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
                      <span class="btn-group btn-group-sm" id="btnDeleteMultipleSpan"
                            data-toggle="confirmation-singleton"
                            onmousedown="return removeRecords(this);"
                            data-href="javascript:removeData('globalRateCard','${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/*/destroy');">
                           <button id="btnDeleteMultiple" type="button" class="btn btn-default"
                                   data-toggle="tooltip" data-placement="right" title="delete"
                                   role="button">
                            <span class="glyphicon glyphicon-trash" title="delete"/></button>
                    </span>
                </span>
            </div>

            <nv:dataTable id="monetaryRateCardData"
                          list="${monetaryRateCardListAsjson}"
                          width="100%"
                          showPagination="false" showFilter="true" showInfo="false"
                          cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
                <nv:dataTableColumn title="Name" beanProperty="name"
                                    hrefurl="${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/$<id>"
                                    sortable="true"/>
                <nv:dataTableColumn title="Pulse UOM"
                                    beanProperty="monetaryRateCardData.pulseUnit"/>
                <nv:dataTableColumn title="Rate UOM" beanProperty="monetaryRateCardData.rateUnit"/>
                <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/$<id>/edit" style="width:20px;border-right:0px;"/>
                <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/$<id>?_method=DELETE" 	 style="width:20px;"  />
            </nv:dataTable>
        </div>
    </div>
</s:form>

<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>