<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 10/10/17
  Time: 11:41 AM
  To change this template use File | Settings | File Templates.
--%>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="driver.search"/> </h3>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">

                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li class="active" id="tab1">
                        <a data-toggle="tab" href="#section1"  onclick="csvDriverShow()" role="button"><s:text name="csv.driver" /></a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2"  onclick="dbCDRDriverShow()" role="button"><s:text name="db.cdr.driver" /></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <div id="section1" class="tab-pane fade in">
                    <s:form namespace="/sm/driver" id="csvDriverSearch" method="get" action="csv-driver" enctype="multipart/form-data"
                       cssClass="form-vertical">
                        <div class="panel-body">
                            <div class="dataTable-button-groups">
                                <span class="btn-group btn-group-sm">
                                    <a href="${pageContext.request.contextPath}/sm/driver/csv-driver/new"
                                       class="btn btn-default" id="btnAddCsvDriver">
                                        <span class="glyphicon glyphicon-plus" title="Add"></span>
                                    </a>
                                        <span class="btn-group btn-group-sm" id="btnRemoveCSVDriver"
                                              data-toggle="confirmation-singleton"
                                              onmousedown="return removeRecords(this,'csvDriverSearch');"
                                              data-href="javascript:removeData('csvDriverSearch','${pageContext.request.contextPath}/sm/driver/csv-driver/*/destroy');">
                                           <button id="btnDeleteCSVDrivers" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                          </span>
                                </span>
                            </div>
                            <nv:dataTable
                                    id="CsvDriverDatas"
                                    list="${csvDriverDataAsJson}"
                                    rows="<%=rows%>"
                                    width="100%"
                                    showPagination="true"
                                    showFilter="true"
                                    cssClass="table table-blue">
                                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />" beanProperty="id" style="width:5px !important;"/>
                                <nv:dataTableColumn title="Name" beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/driver/csv-driver/$<id>" sortable="true" style="min-width:100% !important" tdCssClass="word-break"/>
                                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/driver/csv-driver/$<id>/edit" style="width:20px;border-right:0px;"/>
                                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/driver/csv-driver/$<id>?_method=DELETE" style="width:20px;"/>
                            </nv:dataTable>
                        </div>
                    </s:form>
                    </div>
                    <div id="section2" class="tab-pane fade">
                    <s:form id="dbCDRDriverSearch"  enctype="multipart/form-data" namespace="/sm/driver" method="get" action="db-cdr-driver"
                     cssClass="form-vertical">
                        <div class="panel-body">
                            <div class="dataTable-button-groups">
                                <span class="btn-group btn-group-sm">
                                    <a href="${pageContext.request.contextPath}/sm/driver/db-cdr-driver/new" class="btn btn-default" role="button" id="btnAddDbCdr">
                                        <span class="glyphicon glyphicon-plus" title="Add"></span>
                                    </a>
                                    <span class="btn-group btn-group-sm" id="btnRemoveDBCDRDriver"
                                          data-toggle="confirmation-singleton"
                                          onmousedown="return removeRecords(this,'dbCDRDriverSearch');"
                                          data-href="javascript:removeData('dbCDRDriverSearch','${pageContext.request.contextPath}/sm/driver/db-cdr-driver/*/destroy');">
                                           <button id="btnDeleteDBCDRDrivers" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                          </span>
                                </span>
                            </div>
                            <nv:dataTable
                                    id="dbCdrDriverDatas"
                                    list="${dbCdrDriverDataAsJson}"
                                    rows="<%=rows%>"
                                    width="100%"
                                    showPagination="true"
                                    showFilter="true"
                                    cssClass="table table-blue">
                                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />" beanProperty="id" style="width:5px !important;"/>
                                <nv:dataTableColumn title="Name" beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/driver/db-cdr-driver/$<id>" sortable="true" style="min-width:100% !important" tdCssClass="word-break" />
                                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/driver/db-cdr-driver/$<id>/edit" style="width:20px;border-right:0px;"/>
                                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/driver/db-cdr-driver/$<id>?_method=DELETE" style="width:20px;"/>
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

    function dbCDRDriverShow(){
        document.forms["dbCDRDriverSearch"].action = "${pageContext.request.contextPath}/sm/driver/db-cdr-driver";
        document.forms["dbCDRDriverSearch"].submit();
    }
    function csvDriverShow(){
        document.forms["csvDriverSearch"].action = "${pageContext.request.contextPath}/sm/driver/csv-driver";
        document.forms["csvDriverSearch"].submit();
    }
    $(document).ready(function() {
        var type = '<s:property value="%{#request.type}"/>';
        if(type == '<s:property value="@com.elitecore.corenetvertex.constants.DriverType@CSV_DRIVER.name()"/>'){
            $("#tab1").attr("class","active");
            $("#tab2").removeAttr("class");
            $("#section1").attr("class","tab-pane fade in active");
        }else if(type == '<s:property value="@com.elitecore.corenetvertex.constants.DriverType@DB_CDR_DRIVER.name()"/>'){
            $("#tab2").attr("class","active");
            $("#tab1").removeAttr("class");
            $("#section2").attr("class","tab-pane fade in active");
        }
    });


</script>
<%@include file="../utility/indexPageUtility.jsp"%>