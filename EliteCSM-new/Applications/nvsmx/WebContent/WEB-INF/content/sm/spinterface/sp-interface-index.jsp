<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 8/9/17
  Time: 1:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="sp.interface.search"/> </h3>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li class="active" id="tab1">
                        <a data-toggle="tab" href="#section1"  onclick="dbSpInterfaceShow()" role="button"><s:text name="sp.interface.db" /></a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2" onclick="ldapSpInterfaceShow()"  role="button"><s:text name="sp.interface.ldap" /></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <div id="section1" class="tab-pane fade in">
                    <s:form id="dbSpInterfaceForm" method="get" enctype="multipart/form-data"
                    cssClass="form-vertical">
                        <div class="panel-body">
                            <div class="dataTable-button-groups">
                                <span class="btn-group btn-group-sm">
                                    <a href="${pageContext.request.contextPath}/sm/spinterface/db-sp-interface/new" class="btn btn-default" role="button" id="btnAddDbSpInterface">
                                        <span class="glyphicon glyphicon-plus" title="Add"></span>
                                    </a>
                                    <span class="btn-group btn-group-sm" id="btnRemoveDBSPInterface"
                                          data-toggle="confirmation-singleton"
                                          onmousedown="return removeRecords(this,'dbSpInterfaceForm');"
                                          data-href="javascript:removeData('dbSpInterfaceForm','${pageContext.request.contextPath}/sm/spinterface/db-sp-interface/*/destroy');">
                                           <button id="btnDeleteDBSPInterface" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                     </span>
                                </span>
                            </div>
                            <nv:dataTable
                                    id="dbSpInterfaceDatas"
                                    list="${dataListAsJson}"
                                    rows="<%=rows%>"
                                    width="100%"
                                    showPagination="true"
                                    showFilter="true"
                                    cssClass="table table-blue">
                                <nv:dataTableColumn
                                        title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll'/>"
                                        beanProperty="id" style="width:5px !important;"/>
                                <nv:dataTableColumn title="Name" beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/spinterface/db-sp-interface/$<id>" sortable="true" style="min-width:100%; !important" tdCssClass="word-break"/>
                                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/spinterface/db-sp-interface/$<id>/edit" style="width:20px;border-right:0px;"/>
                                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/spinterface/db-sp-interface/$<id>?_method=DELETE" style="width:20px;"/>
                            </nv:dataTable>
                        </div>
                    </s:form>
                    </div>
                    <div id="section2" class="tab-pane fade in">
                        <s:form id="ldapSpInterfaceForm" method="get" namespace="/sm/spinterface" action="ldap-sp-interface" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                    <span class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface/new" class="btn btn-default" role="button" id="btnAddLdapSpInterface">
                                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                                        </a>
                                    <span class="btn-group btn-group-sm" id="btnRemoveLdapSPInterface"
                                          data-toggle="confirmation-singleton"
                                          onmousedown="return removeRecords(this,'ldapSpInterfaceForm');"
                                          data-href="javascript:removeData('ldapSpInterfaceForm','${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface/*/destroy');">
                                           <button id="btnDeleteLdapSPInterface" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                     </span>
                                    </span>
                                </div>
                                <nv:dataTable
                                        id="ldapSpInterfaceAsJson"
                                        list="${dataListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />" beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface/$<id>" sortable="true" style="min-width:100%; !important" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface/$<id>/edit" style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface/$<id>?_method=DELETE" style="width:20px;"/>
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

    function ldapSpInterfaceShow(){
        document.forms["ldapSpInterfaceForm"].action = "${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface";
        document.forms["ldapSpInterfaceForm"].submit();
    }
    function dbSpInterfaceShow(){
        document.forms["dbSpInterfaceForm"].action = "${pageContext.request.contextPath}/sm/spinterface/db-sp-interface";
        document.forms["dbSpInterfaceForm"].submit();
    }
    $(document).ready(function() {

        var type = '<s:property value="%{#request.type}"/>';
        if(type == '<s:property value="@com.elitecore.corenetvertex.constants.SpInterfaceType@DB_SP_INTERFACE.name()"/>'){
            $("#tab1").attr("class","active");
            $("#tab2").removeAttr("class");
            $("#section1").attr("class","tab-pane fade in active");
        }else if(type == '<s:property value="@com.elitecore.corenetvertex.constants.SpInterfaceType@LDAP_SP_INTERFACE.name()"/>'){
            $("#tab2").attr("class","active");
            $("#tab1").removeAttr("class");
            $("#section2").attr("class","tab-pane fade in active");
        }
    });

</script>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>