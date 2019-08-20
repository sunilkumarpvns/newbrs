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
<script type="text/javascript">
    function setRevalidationMode(data, type, thisBean){
        if(data == '<s:property value="@com.elitecore.corenetvertex.constants.RevalidationMode@ClientInitiated.name()"/>'){
            return '<s:property value="@com.elitecore.corenetvertex.constants.RevalidationMode@ClientInitiated.getValue()"/>';
        }else if(data == '<s:property value="@com.elitecore.corenetvertex.constants.RevalidationMode@ServerInitiated.name()"/>'){
            return '<s:property value="@com.elitecore.corenetvertex.constants.RevalidationMode@ServerInitiated.getValue()"/>';
        }
    }


</script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="gateway.profile.list" />
        </h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li class="active" id="tab1">
                        <a data-toggle="tab" href="#section1" onclick="diameterGatewayProfileShow()"  role="button"><s:text name="diameter.gateway.profile"/> </a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2" onclick="radiusGatewayProfileShow()"  role="button"><s:text name="radius.gateway.profile"/></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <!-- Diameter Gateway configuration tab -->
                    <div id="section1" class="tab-pane fade in">
                        <s:form id="diameterGatewayProfileDataSearch" method="get" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                   <span class="btn-group btn-group-sm">
                                      <a href="${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/new"
                                         class="btn btn-default" role="button" id="btnAddDiameterGateway">
                                          <span class="glyphicon glyphicon-plus" title="Add"></span>
                                      </a>
                                      <span class="btn-group btn-group-sm" id="btnRemoveDiameterGatewayProfile"
                                            data-toggle="confirmation-singleton"
                                            onmousedown="return removeRecords(this,'diameterGatewayProfileDataSearch');"
                                            data-href="javascript:removeData('diameterGatewayProfileDataSearch','${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/*/destroy');">

                                           <button id="btnDeleteDiameterGatewayProfile" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                      </span>
                                   </span>
                                </div>
                                <nv:dataTable
                                        id="diameterProfiles"
                                        list="${dataListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/$<id>"
                                                        sortable="true" style="min-width:40%; !important" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="Type" beanProperty="gatewayType"
                                                        sortable="true" style="min-width:30%" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="Revalidation Mode" beanProperty="revalidationMode" renderFunction="setRevalidationMode"
                                                        sortable="true" style="min-width:30%" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/$<id>?_method=DELETE"
                                                        style="width:20px;"/>
                                </nv:dataTable>

                            </div>
                        </s:form>
                    </div>
                    <!-- Radius Gateway configuration tab -->
                    <div id="section2" class="tab-pane fade in">
                        <s:form namespace="/sm/gatewayprofile" id="radiusGatewayProfileDataSearch" action="radius-gateway-profile" method="get" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                    <span class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/new" class="btn btn-default" role="button" id="btnAddRadiusGatewayProfile"><span class="glyphicon glyphicon-plus" title="Add"></span></a>
                                        <span class="btn-group btn-group-sm" id="btnRemoveRadiusGatewayProfile"
                                              data-toggle="confirmation-singleton"
                                              onmousedown="return removeRecords(this,'radiusGatewayProfileDataSearch');"
                                              data-href="javascript:removeData('radiusGatewayProfileDataSearch','${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/*/destroy');">
                                           <button id="btnDeleteRadiusGatewayProfile" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                      </span>
                                    </span>
                                </div>
                                <nv:dataTable
                                        id="radiusProfiles"
                                        list="${dataListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/$<id>"
                                                        sortable="true" style="min-width:40%; !important" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="Type"
                                                        beanProperty="gatewayType"  sortable="true" style="min-width:30%" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="Revalidation Mode" beanProperty="revalidationMode" renderFunction="setRevalidationMode"
                                                        sortable="true" style="min-width:30%" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/$<id>?_method=DELETE"
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

    function radiusGatewayProfileShow(){
        document.forms["radiusGatewayProfileDataSearch"].action = "${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile";
        document.forms["radiusGatewayProfileDataSearch"].submit();
    }
    function diameterGatewayProfileShow(){
        document.forms["diameterGatewayProfileDataSearch"].action = "${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile";
        document.forms["diameterGatewayProfileDataSearch"].submit();
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
