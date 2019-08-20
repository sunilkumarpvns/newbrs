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
           <s:text name="packet.mapping.list"/>
        </h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li id="tab1">
                        <a data-toggle="tab" href="#section1" onclick="return diameterPacketMappingShow()"  role="button"><s:text name="diameter.packet.mapping"/></a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2" onclick="return radiusPacketMappingShow()"  role="button"><s:text name="radius.packet.mapping"/></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <!-- Diameter Packet Mapping configuration tab -->
                    <div id="section1" class="tab-pane fade in" >
                        <s:form id="diameterPacketMappingSearch" method="get" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                   <span class="btn-group btn-group-sm">
                                      <a href="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/new"
                                         class="btn btn-default" role="button" id="btnAddDiameterPacketMapping">
                                          <span class="glyphicon glyphicon-plus" title="Add"></span>
                                      </a>
                                        <span class="btn-group btn-group-sm" id="btnRemoveDiameterPacketMapping"
                                              data-toggle="confirmation-singleton"
                                              onmousedown="return removeRecords(this,'diameterPacketMappingSearch');"
                                              data-href="javascript:removeData('diameterPacketMappingSearch','${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/*/destroy');">
                                        <button id="btnDeleteDiameterPacketMapping" type="button" class="btn btn-default"
                                                data-toggle="tooltip" data-placement="right" title="delete"
                                                role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                        </button>
                                        </span>
                                     </span>
                                </div>
                                <nv:dataTable
                                        id="diameterPacketMappingData"
                                        list="${diameterPacketMappingListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll' id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/$<id>"
                                                        sortable="true" style="min-width:40%; !important" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="Packet Type" beanProperty="packetType"
                                                        sortable="true" tdCssClass="word-break" style="min-width:30%"/>
                                    <nv:dataTableColumn title="Type" beanProperty="type"
                                                        sortable="true" tdCssClass="word-break" style="min-width:30%"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping/$<id>?_method=DELETE"
                                                        style="width:20px;"/>
                                </nv:dataTable>

                            </div>
                        </s:form>
                    </div>
                    <!-- Radius Packet Mapping configuration tab -->
                    <div id="section2" class="tab-pane fade in">
                        <s:form namespace="/sm/packet-mapping" id="radiusPacketMappingSearch" method="get" enctype="multipart/form-data"
                                cssClass="form-vertical" action="radius-packet-mapping">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                        <span class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping/new"
                                               class="btn btn-default" role="button" id="btnAddRadiusPacketMapping">
                                             <span class="glyphicon glyphicon-plus" title="Add"></span>
                                             </a>
                                            <span class="btn-group btn-group-sm" id="btnRemoveRadiusPacketMapping"
                                                  data-toggle="confirmation-singleton"
                                                  onmousedown="return removeRecords(this,'radiusPacketMappingSearch');"
                                                  data-href="javascript:removeData('radiusPacketMappingSearch','${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping/*/destroy');">
                                                <button id="btnDeleteRadiusPacketMapping" type="button" class="btn btn-default"
                                                        data-toggle="tooltip" data-placement="right" title="delete"
                                                        role="button">
                                                        <span class="glyphicon glyphicon-trash" title="delete"></span>
                                                </button>
                                           </span>
                                        </span>
                                </div>
                                <nv:dataTable
                                        id="radiusPacketMappingData"
                                        list="${radiusPacketMappingListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping/$<id>"
                                                        sortable="true" style="min-width:40%; !important" tdCssClass="word-break"  />
                                    <nv:dataTableColumn title="Packet Type"
                                                        beanProperty="packetType" sortable="true" tdCssClass="word-break" style="min-width:30%"/>
                                    <nv:dataTableColumn title="Type" beanProperty="type"
                                                        sortable="true" tdCssClass="word-break" style="min-width:30%"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping/$<id>?_method=DELETE"
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

    function radiusPacketMappingShow(){
        document.forms["radiusPacketMappingSearch"].action = "${pageContext.request.contextPath}/sm/packetmapping/radius-packet-mapping";
        document.forms["radiusPacketMappingSearch"].submit();
    }
    function diameterPacketMappingShow(){
        document.forms["diameterPacketMappingSearch"].action = "${pageContext.request.contextPath}/sm/packetmapping/diameter-packet-mapping";
        document.forms["diameterPacketMappingSearch"].submit();
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