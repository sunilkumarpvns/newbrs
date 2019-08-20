<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<style>
    .subtable td:nth-child(odd) {
        text-align: left;
        font-weight: bold;
        width: 110px;
    }

    .subtable td:nth-child(even) {
        text-align: left;
        width: 110px;
    }

    .collapsing {
        -webkit-transition: none;
        transition: none;
    }
</style>
<script>

    function setRollingType(data, type, thisBean){

        if(data == '<s:property value="@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value"/>'){
           return '<s:property value="@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.label"/>';
        }else if(data == '<s:property value="@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.value"/>'){
            return '<s:property value="@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.label"/>';
        }
    }

    function setRollingUnit(data,type,thisBean){
        if (thisBean.rollingType != '<s:property value="@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value"/>') {
          return data;
        }
        if (data == '<s:property value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@MINUTE.value"/>') {
            return '<s:property value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@MINUTE.unit"/>'
        }
        if (data == '<s:property value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@HOUR.value"/>') {
            return '<s:property value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@HOUR.unit"/>'
        }
        if (data == '<s:property value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@DAILY.value"/>') {
            return '<s:property value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@DAILY.unit"/>'
        }
    }

    function setAdvancedTrap(data,type,thisBean){

        if (data == <s:property value="@com.elitecore.corenetvertex.constants.CommonStatusValues@ENABLE.isBooleanValue()"/>) {
            return '<s:property value="@com.elitecore.corenetvertex.constants.CommonStatusValues@ENABLE.getStringName()"/>'
        }
        if (data == <s:property value="@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()"/>) {
            return '<s:property value="@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.getStringName()"/>'
        }
    }



</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="alert.search"/>
        </h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li class="active" id="tab1">
                        <a data-toggle="tab" href="#section1"  onclick="fileAlertShow()" role="button"><s:text name="alert.file.type"/></a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2"  onclick="trapAlertShow()" role="button"><s:text name="alert.trap.type"/></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <!-- File Alert configuration tab -->
                    <div id="section1" class="tab-pane fade in">
                        <s:form  id="fileAlertForm" method="get" namespace="/sm/alert" action="file-alert" enctype="multipart/form-data"
                                  cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                        <span class="btn-group btn-group-sm">
                                          <a href="${pageContext.request.contextPath}/sm/alert/file-alert/new"
                                             class="btn btn-default" role="button" id="btnAddFileAlertListener">
                                              <span class="glyphicon glyphicon-plus" title="Add"/>
                                          </a>
                                          <span class="btn-group btn-group-sm" id="btnRemoveFileAlerts" data-toggle="confirmation-singleton"
                                                onmousedown="return removeRecords(this,'fileAlertForm');"
                                                data-href="javascript:removeData('fileAlertForm','${pageContext.request.contextPath}/sm/alert/file-alert/*/destroy');" >
                                           <button id="btnDeleteFileAlerts" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                          </span>
                                        </span>
                                </div>
                                <nv:dataTable
                                        id="fileAlertListeners"
                                        list="${fileAlertListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/alert/file-alert/$<id>"
                                                        sortable="true" style="min-width:40%; !important" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="File Name" beanProperty="fileName"  style="min-width:30%; " tdCssClass="word-break" />
                                    <nv:dataTableColumn title="Rolling Type" beanProperty="rollingType" renderFunction="setRollingType"  style="min-width:15%;" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="Rolling Unit" beanProperty="rollingUnit" renderFunction="setRollingUnit" style="min-width:15%;" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/alert/file-alert/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/alert/file-alert/$<id>?_method=DELETE"
                                                        style="width:20px;"/>
                                </nv:dataTable>

                            </div>
                        </s:form>
                    </div>
                    <!-- Trap Alert configuration tab -->
                    <div id="section2" class="tab-pane fade">
                        <s:form id="trapAlertForm" name="trapAlertForm" method="get" namespace="/sm/alert" action="trap-alert" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                        <span class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/sm/alert/trap-alert/new"
                                               class="btn btn-default" role="button" id="btnAddTrapAlert">
                                             <span class="glyphicon glyphicon-plus" title="Add"></span>
                                             </a>
                                            <span class="btn-group btn-group-sm" id="btnRemoveTrapAlerts" data-toggle="confirmation-singleton"
                                                  onmousedown="return removeRecords(this,'trapAlertForm');"
                                                  data-href="javascript:removeData('trapAlertForm','${pageContext.request.contextPath}/sm/alert/trap-alert/*/destroy');" >
                                           <button id="btnDeleteTrapAlerts" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                          </span>
                                        </span>
                                </div>
                                <nv:dataTable
                                        id="trapAlertListeners"
                                        list="${trapAlertListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll' id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/alert/trap-alert/$<id>"
                                                        sortable="true" style="min-width:40%; !important" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="Trap Version"
                                                        beanProperty="trapVersion" sortable="true" style="min-width:30%; " tdCssClass="word-break" />
                                    <nv:dataTableColumn title="Trap Server" beanProperty="trapServer"
                                                        sortable="true" style="min-width:15%; " tdCssClass="word-break" />
                                    <nv:dataTableColumn title="Advanced Trap" beanProperty="advanceTrap" renderFunction="setAdvancedTrap"
                                                        sortable="true" style="min-width:15%; " tdCssClass="word-break" />
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/alert/trap-alert/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/alert/trap-alert/$<id>?_method=DELETE"
                                                        style="width:20px;"/>
                                </nv:dataTable>
                            </div>
                        </s:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">

    function trapAlertShow(){
        document.forms["trapAlertForm"].action = "${pageContext.request.contextPath}/sm/alert/trap-alert";
        document.forms["trapAlertForm"].submit();
    }
    function fileAlertShow(){
        document.forms["fileAlertForm"].action = "${pageContext.request.contextPath}/sm/alert/file-alert";
        document.forms["fileAlertForm"].submit();
    }
    $(document).ready(function() {
        var type = '<s:property value="%{#request.type}"/>';
        if(type == '<s:property value="@com.elitecore.corenetvertex.sm.alerts.AlertTypes@FILE.name()"/>'){
            $("#tab1").attr("class","active");
            $("#tab2").removeAttr("class");
            $("#section1").attr("class","tab-pane fade in active");
        }else if(type == '<s:property value="@com.elitecore.corenetvertex.sm.alerts.AlertTypes@TRAP.name()"/>'){
            $("#tab2").attr("class","active");
            $("#tab1").removeAttr("class");
            $("#section2").attr("class","tab-pane fade in active");
        }
    });

</script>
<%@include file="../utility/indexPageUtility.jsp"%>
