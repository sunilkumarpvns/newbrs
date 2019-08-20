<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes" %>
<%@page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants" %>
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
           <s:text name="pccrule.mapping.list"/>
        </h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li id="tab1">
                        <a data-toggle="tab" href="#section1" onclick="return diameterPCCRuleMappingShow()"  role="button"><s:text name="diameter.pccrule.mapping"/></a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2" onclick="return radiusPCCRuleMappingShow()"  role="button"><s:text name="radius.pccrule.mapping"/></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <!-- Diameter PCCRule Mapping configuration tab -->
                    <div id="section1" class="tab-pane fade in" >
                        <s:form id="diameterPCCRuleMappingSearch" method="get" enctype="multipart/form-data"
                                cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                   <span class="btn-group btn-group-sm">
                                      <a href="${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/new"
                                         class="btn btn-default" role="button" id="btnAddDiameterPCCRuleMapping">
                                          <span class="glyphicon glyphicon-plus" title="Add"></span>
                                      </a>
                                        <span class="btn-group btn-group-sm" id="btnRemoveDiameterPCCRule"
                                              data-toggle="confirmation-singleton"
                                              onmousedown="return removeRecords(this,'diameterPCCRuleMappingSearch');"
                                              data-href="javascript:removeData('diameterPCCRuleMappingSearch','${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/*/destroy');">

                                           <button id="btnDeleteDiameterPCCRuleMapping" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                      </span>
                                   </span>
                                </div>
                                <nv:dataTable
                                        id="diameterPCCRuleMappingData"
                                        list="${diameterPccRuleMappingListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/$<id>"
                                                        sortable="true" style="min-width:50%; !important" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="Status" beanProperty="Status"
                                                        sortable="true" tdCssClass="word-break" style="min-width:50%"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping/$<id>?_method=DELETE"
                                                        style="width:20px;"/>
                                </nv:dataTable>

                            </div>
                        </s:form>
                    </div>
                    <!-- Radius PCCRule Mapping configuration tab -->
                    <div id="section2" class="tab-pane fade in">
                        <s:form namespace="/sm/pcc-rule-mapping" id="radiusPCCRuleMappingSearch" method="get" enctype="multipart/form-data"
                                cssClass="form-vertical" action="radius-pcc-rule-mapping">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                        <span class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/new"
                                               class="btn btn-default" role="button" id="btnAddRadiusPCCRuleMapping">
                                             <span class="glyphicon glyphicon-plus" title="Add"></span>
                                             </a>
                                             <span class="btn-group btn-group-sm" id="btnRemoveRadiusPCCRule"
                                                   data-toggle="confirmation-singleton"
                                                   onmousedown="return removeRecords(this,'radiusPCCRuleMappingSearch');"
                                                   data-href="javascript:removeData('radiusPCCRuleMappingSearch','${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/*/destroy');">
                                           <button id="btnDeleteRadiusPCcRuleMapping" type="button" class="btn btn-default"
                                                   data-toggle="tooltip" data-placement="right" title="delete"
                                                   role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                      </span>
                                        </span>
                                </div>
                                <nv:dataTable
                                        id="radiusPCCRuleMappingData"
                                        list="${radiusPccRuleMappingListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn
                                            title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"
                                            beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/$<id>"
                                                        sortable="true" style="min-width:50%; !important" tdCssClass="word-break"  />
                                  <nv:dataTableColumn title="Status" beanProperty="Status"
                                                        sortable="true" tdCssClass="word-break" style="min-width:50%"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/$<id>?_method=DELETE"
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
    function radiusPCCRuleMappingShow(){
        document.forms["radiusPCCRuleMappingSearch"].action = "${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping";
        document.forms["radiusPCCRuleMappingSearch"].submit();
    }
    function diameterPCCRuleMappingShow(){
        document.forms["diameterPCCRuleMappingSearch"].action = "${pageContext.request.contextPath}/sm/pccrulemapping/diameter-pcc-rule-mapping";
        document.forms["diameterPCCRuleMappingSearch"].submit();
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
