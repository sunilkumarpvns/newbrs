<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<script>
    function validityPeriod(data, type, thisBean) {
        if(isNullOrEmpty(thisBean.validity) == false) {
            return thisBean.validity + " " + thisBean.validityPeriodUnit;
        }
        return "0 DAY";
    }
    function setAmount(data, type, thisBean) {
        if(isNullOrEmpty(thisBean.amount)) {
            return "0";
        }
        return thisBean.amount;
    }
</script>
<s:form  id="monetaryRechargePlan" method="post"  enctype="multipart/form-data" cssClass="form-vertical">
    <s:set var="priceTag">
        <s:property value="getText('plan.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
    </s:set>
    <s:set var="amountTag">
        <s:property value="getText('plan.amount')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
    </s:set>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="plan.detail"/> </h3>
    </div>

    <div class="panel-body">
        <div class="dataTable-button-groups">
            <span class="btn-group btn-group-sm">
                <a href="${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/new" class="btn btn-default" role="button" >
                    <span class="glyphicon glyphicon-plus" title="Add"></span>
                </a>
            <span class="btn-group btn-group-sm" id="btnRemoveCity"
              data-toggle="confirmation-singleton" onmousedown="return removeRecords(this,'monetaryRechargePlan');"
                  data-href="javascript:removeData('monetaryRechargePlan','${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/*/destroy');">
                <button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
                        <span class="glyphicon glyphicon-trash" title="delete"></span>
                    </button>
            </span>
            </span>
        </div>

        <nv:dataTable
                id="ServiceData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                cssClass="table table-blue" showFilter="true">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="Name" 		beanProperty="name" hrefurl="${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/$<id>" sortable="true"  />
            <nv:dataTableColumn title="${priceTag}" 		beanProperty="price" />
            <nv:dataTableColumn title="${amountTag}" 		beanProperty="amount" renderFunction="setAmount"/>
            <nv:dataTableColumn title="Validity" 		beanProperty="validity" renderFunction="validityPeriod"/>
            <nv:dataTableColumn title="Mode" beanProperty="Mode" sortable="true"/>
            <nv:dataTableColumn title="Status" 		beanProperty="Status" sortable="true"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/$<id>/edit" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" disableWhen=" Mode==LIVE || Mode==LIVE2 " hrefurl="delete:${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/$<id>?_method=DELETE" 	 style="width:20px;"  />
        </nv:dataTable>
    </div>
</div>
</s:form>
<script type="text/javascript">


    function removeRecords(obj) {
        var selectVar = getSelectedValuesForDelete();
        if(selectVar == true){
            var flag = false;
            flag = deleteConfirmMsg(obj,"Delete selected packages ?");
            if(flag==true) {
                removeData();
            }
            return flag;
        }
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
            return addWarning(".popup","At least select one country for delete");
        }
        return selectedData;
    }

    function removeData(){
        $("#countryList").DataTable().rows('.selected').remove().draw(false);
    }
</script>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>