<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

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
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="gateway.list"/>
        </h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li class="active" id="tab1">
                        <a data-toggle="tab" href="#section1"  onclick="diameterGatewayShow()" role="button"><s:text name="diameter.gateway"/></a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2" onclick="radiusGatewayShow()" role="button"><s:text name="radius.gateway"/></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <!-- Diameter Gateway configuration tab -->
                    <div id="section1" class="tab-pane fade in">
                        <s:form id="diameterGatewayDataSearch" method="get" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                   <span class="btn-group btn-group-sm">
                                      <a href="${pageContext.request.contextPath}/sm/gateway/diameter-gateway/new"
                                         class="btn btn-default" role="button" id="btnAddDiameterGateway">
                                          <span class="glyphicon glyphicon-plus" title="Add"></span>
                                      </a>
                                       <span class="btn-group btn-group-sm" id="btnRemoveDiameterGateway"
                                             data-toggle="confirmation-singleton"
                                             onmousedown="return removeRecords(this,'diameterGatewayDataSearch');"
                                             data-href="javascript:removeData('diameterGatewayDataSearch','${pageContext.request.contextPath}/sm/gateway/diameter-gateway/*/destroy');">
                                        <button id="btnDeleteDiameterGateway" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                        </button>
                                        </span>
                                   </span>
                                </div>
                                <nv:dataTable
                                        id="diameterGatewayData"
                                        list="${dataListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="Name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/gateway/diameter-gateway/$<id>"
                                                        sortable="true"  style="min-width:40%; !important" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="Policy Enforcement Method"
                                                        beanProperty="Policy Enforcement Method" sortable="true" tdCssClass="word-break" style="min-width:20%"/>
                                    <nv:dataTableColumn title="Host Identity" beanProperty="Host Identity"
                                                        sortable="true" tdCssClass="word-break" style="min-width:20%"/>
                                    <nv:dataTableColumn title="Connection URL" beanProperty="Connection URL"
                                                        sortable="true" tdCssClass="word-break" style="min-width:20%"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/gateway/diameter-gateway/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/gateway/diameter-gateway/$<id>?_method=DELETE"
                                                        style="width:20px;"/>
                                </nv:dataTable>

                            </div>
                        </s:form>
                    </div>
                    <!-- Radius Gateway configuration tab -->
                    <div id="section2" class="tab-pane fade in">
                        <s:form namespace="/sm/gateway" id="radiusGatewayDataSearch" method="get" action="radius-gateway" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                    <span class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/sm/gateway/radius-gateway/new" class="btn btn-default" role="button" id="btnAddRadiusGateway"><span class="glyphicon glyphicon-plus" title="Add"></span></a>
                                        <span class="btn-group btn-group-sm" id="btnRemoveRadiusGateway"
                                              data-toggle="confirmation-singleton"
                                              onmousedown="return removeRecords(this,'radiusGatewayDataSearch');"
                                              data-href="javascript:removeData('radiusGatewayDataSearch','${pageContext.request.contextPath}/sm/gateway/radius-gateway/*/destroy');">
                                        <button id="btnDeleteRadiusGateway" type="button" class="btn btn-default"
                                                data-toggle="tooltip" data-placement="right" title="delete"
                                                role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                        </button>
                                        </span>
                                    </span>
                                </div>
                                <nv:dataTable
                                        id="radiusGatewayData"
                                        list="${dataListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="Name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/gateway/radius-gateway/$<id>"
                                                        sortable="true" style="min-width:40%; !important" tdCssClass="word-break"  />
                                    <nv:dataTableColumn title="Policy Enforcement Method"
                                                        beanProperty="Policy Enforcement Method" sortable="true" tdCssClass="word-break" style="min-width:20%"/>
                                    <nv:dataTableColumn title="Connection URL" beanProperty="Connection URL"
                                                        sortable="true" tdCssClass="word-break" style="min-width:20%" />
                                    <nv:dataTableColumn title="Shared Secret" beanProperty="Shared Secret"
                                                        sortable="true" tdCssClass="word-break" style="min-width:20%" />
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/gateway/radius-gateway/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/gateway/radius-gateway/$<id>?_method=DELETE"
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
</div>
</div>
<script type="text/javascript">

    function radiusGatewayShow(){
        document.forms["radiusGatewayDataSearch"].action = "${pageContext.request.contextPath}/sm/gateway/radius-gateway";
        document.forms["radiusGatewayDataSearch"].submit();
    }
    function diameterGatewayShow(){
        document.forms["diameterGatewayDataSearch"].action = "${pageContext.request.contextPath}/sm/gateway/diameter-gateway";
        document.forms["diameterGatewayDataSearch"].submit();
    }
    $(document).ready(function() {
        var type = '<s:property value="%{#request.type}"/>';
        if(type == '<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@DIAMETER.name()"/>'){
            $("#tab1").attr("class","active");
            $("#tab2").removeAttr("class");
            $("#section1").attr("class","tab-pane fade in active");
        }else if(type == '<s:property value="@com.elitecore.corenetvertex.constants.CommunicationProtocol@RADIUS.name()"/>'){
            $("#tab2").attr("class","active");
            $("#tab1").removeAttr("class");
            $("#section2").attr("class","tab-pane fade in active");
        }
    });


</script>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>
