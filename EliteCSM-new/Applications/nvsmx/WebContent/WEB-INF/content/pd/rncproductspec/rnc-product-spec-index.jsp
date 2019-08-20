<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();

%>



<s:form  id="productSpecSearchForm" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="product.spec.search" />
            </h3>
        </div>

        <div class="panel-body">
            <div class="row">
                <div class="col-xs-5">
					<span class="btn-group btn-group-sm" >
						<a href="${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/new" class="btn btn-default" role="button" id="btnAddGroup">
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
					</span>
                </div>
            </div>
                <%-- id is mandatory --%>
                <%-- beanType is mandatory --%>
                <%-- rows must be greater than zero --%>
            <nv:dataTable
                    id="productSpecData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
                <nv:dataTableColumn title="Name" 		 beanProperty="Name"  hrefurl="${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/$<id>" sortable="true" />
                <nv:dataTableColumn title="Status" 	 	 beanProperty="Status" 			sortable="true" />
                <nv:dataTableColumn title="Mode" 	 	 beanProperty="Mode" 	sortable="true" />
                <nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-pencil'></span>"	hrefurl="edit:${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/$<id>/edit" style="width:20px;border-right:0px;"  />
                <nv:dataTableColumn title="" 	disableWhen="packageMode==LIVE2"		 icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/$<id>?_method=DELETE" 	 style="width:20px;"  />
            </nv:dataTable>

        </div>
    </div>
</s:form>