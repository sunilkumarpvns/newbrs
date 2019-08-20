<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<script>
    function updateProductOfferNotification(id,name) {
        $("#productOfferNotificationId").val(id);
        $("#duplicateEntityName").val("CopyOf_" + name);
        $('#productOfferCloningForm').attr('action', "${pageContext.request.contextPath}/pd/productoffer/product-offer/"+id+"/copymodel");
        $("#productOfferCloningDialog").modal('show');
        $("#duplicateEntityName").focus();
        $("#method").val("post");
    }

    function updateNotificationInfo(data,type,thisBean){
        var notificationFunction = "javascript:updateProductOfferNotification('"+thisBean.id+"','"+thisBean.Name+"');"
        return "<a style='cursor:pointer' href="+notificationFunction+"><span class='glyphicon glyphicon-duplicate'></span></a>";
    }
</script>

<s:form  id="productSpecSearchForm" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="product.offer.search" />
            </h3>
        </div>

        <div class="panel-body">
            <div class="dataTable-button-groups">
				<span class="btn-group btn-group-sm">
						<a href="${pageContext.request.contextPath}/pd/productoffer/product-offer/new"
                           class="btn btn-default" role="button" id="btnAddGroup">
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
                      <span class="btn-group btn-group-sm" id="btnDeleteMultipleSpan"
                            data-toggle="confirmation-singleton"
                            onmousedown="return removeRecords(this);"
                            data-href="javascript:removeData();">
                           <button id="btnDeleteMultiple" type="button" class="btn btn-default"
                                   data-toggle="tooltip" data-placement="right" title="delete"
                                   role="button">
                            <span class="glyphicon glyphicon-trash" title="delete"/></button>
                    </span>
                </span>
            </div>
            <s:set var="priceTag">
                <s:if test="%{@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()}">
                    <s:property value="getText('product.offer.subscription.price')"/>
                </s:if>
                <s:else>
                    <s:property value="getText('product.offer.subscription.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                </s:else>
            </s:set>
            <nv:dataTable
                    id="productOfferData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    cssClass="table table-blue" showFilter="true">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
                <nv:dataTableColumn title="Name" 		 beanProperty="Name"  hrefurl="${pageContext.request.contextPath}/pd/productoffer/product-offer/$<id>" sortable="true" />
                <nv:dataTableColumn title="Type" beanProperty="Type"/>
                <nv:dataTableColumn title="Mode" 	 	 beanProperty="Mode" 			sortable="true" />
                <nv:dataTableColumn title="Status" 	 	 beanProperty="Status" 			sortable="true" />
                <nv:dataTableColumn title="${priceTag}"  beanProperty="Subscription Price" sortable="true" style="width:180px;" />
                <nv:dataTableColumn title="Currency"     beanProperty="Currency" sortable="true" style="width:80px;"/>
                <nv:dataTableColumn style="width:20px;" tdStyle="width:20px;"  renderFunction="updateNotificationInfo"/>
                <nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-pencil'></span>"	hrefurl="edit:${pageContext.request.contextPath}/pd/productoffer/product-offer/$<id>/edit" style="width:20px;border-right:0px;"  />
                <nv:dataTableColumn title=""   disableWhen="Mode==LIVE||Mode==LIVE2"		 icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/productoffer/product-offer/$<id>?_method=DELETE" 	 style="width:20px;"  />            </nv:dataTable>

        </div>
    </div>
</s:form>
<script>
    function getSelectedValuesForDelete(){
        var selectedData = false;
        var selectedDatas = document.getElementsByName("ids");
        //var selectedModes = document.getElementsByName("modes");
        for (var index=0; index < selectedDatas.length; index++){
            if(selectedDatas[index].name == 'ids'){
                if(selectedDatas[index].checked == true){
                    selectedData = true;
                    /*if(selectedModes[index].value == 'LIVE2'){
                        selectedData = false;
                        return addWarning(".popup","LIVE2 Packages can not be deleted");
                    }*/
                }
            }


        }
        if(selectedData == false){
            return addWarning(".popup","At least select one product offer for delete");
        }
        return selectedData;
    }

    function removeRecords(obj) {
        var selectVar = getSelectedValuesForDelete();
        if(selectVar == true){
            var flag = false;
            flag = deleteConfirmMsg(obj,"Delete selected Product Offers?");
            if(flag==true) {
                removeData();
            }
            return flag;
        }
    }

    function removeData(){
        document.forms["productSpecSearchForm"].action = "${pageContext.request.contextPath}/pd/productoffer/product-offer/*/destroy";
        document.forms["productSpecSearchForm"].submit();
    }

</script>
<%@include file="product-offer-duplicate-dialog.jsp"%>
