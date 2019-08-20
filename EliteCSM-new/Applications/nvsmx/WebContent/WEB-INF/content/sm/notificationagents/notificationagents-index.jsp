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
            <s:text name="notificationagents.list" />
        </h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li class="active" id="tab1">
                        <a data-toggle="tab" href="#section1" onclick="emailAgentShow()"  role="button"><s:text name="email.agent.title"/> </a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2" onclick="smsAgentShow()"  role="button"><s:text name="sms.agent.title"/></a>
                    </li>
                </ul>

                <div class="tab-content">
                    <!-- Email Agent configuration tab -->
                    <div id="section1" class="tab-pane fade in">
                        <s:form id="emailAgentSearch" method="get" enctype="multipart/form-data" cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                   <span class="btn-group btn-group-sm">
                                      <a href="${pageContext.request.contextPath}/sm/notificationagents/email-agent/new" class="btn btn-default" role="button" id="btnAddEmailAgent">
                                          <span class="glyphicon glyphicon-plus" title="Add"></span>
                                      </a>
                                      <span class="btn-group btn-group-sm" id="btnRemoveEmailAgent" data-toggle="confirmation-singleton" onmousedown="return removeRecords(this,'emailAgentSearch');"
                                            data-href="javascript:removeData('emailAgentSearch','${pageContext.request.contextPath}/sm/notificationagents/email-agent/*/destroy');">
                                           <button id="btnDeleteEmailAgent" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                      </span>
                                   </span>
                                </div>
                                <nv:dataTable
                                        id="emailAgents"
                                        list="${dataListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />" beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="Name" hrefurl="${pageContext.request.contextPath}/sm/notificationagents/email-agent/$<id>"
                                                        sortable="true" style="min-width:50%; !important" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="Email Host" beanProperty="emailHost" style="min-width:50%" tdCssClass="word-break" />
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/notificationagents/email-agent/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/notificationagents/email-agent/$<id>?_method=DELETE"
                                                        style="width:20px;"/>
                                </nv:dataTable>
                            </div>
                        </s:form>
                    </div>
                    <!-- SMS Agent configuration tab -->
                    <div id="section2" class="tab-pane fade in">
                        <s:form namespace="/sm/notificationagents" id="smsAgentSearch" action="sms-agent" method="get" enctype="multipart/form-data" cssClass="form-vertical">
                            <div class="panel-body">
                                <div class="dataTable-button-groups">
                                    <span class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/sm/notificationagents/sms-agent/new" class="btn btn-default" role="button" id="btnAddSMSAgent"><span class="glyphicon glyphicon-plus" title="Add"></span></a>
                                        <span class="btn-group btn-group-sm" id="btnRemoveRadiusGatewayProfile" data-toggle="confirmation-singleton" onmousedown="return removeRecords(this,'smsAgentSearch');"
                                              data-href="javascript:removeData('smsAgentSearch','${pageContext.request.contextPath}/sm/notificationagents/sms-agent/*/destroy');">
                                           <button id="btnDeletesmsAgentSearch" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
                                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                            </button>
                                      </span>
                                    </span>
                                </div>
                                <nv:dataTable
                                        id="smsAgents"
                                        list="${dataListAsJson}"
                                        rows="<%=rows%>"
                                        width="100%"
                                        showPagination="true"
                                        showFilter="true"
                                        cssClass="table table-blue">
                                    <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />" beanProperty="id" style="width:5px !important;"/>
                                    <nv:dataTableColumn title="Name" beanProperty="Name" hrefurl="${pageContext.request.contextPath}/sm/notificationagents/sms-agent/$<id>"
                                                        sortable="true" style="min-width:100%; !important" tdCssClass="word-break"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                        hrefurl="edit:${pageContext.request.contextPath}/sm/notificationagents/sms-agent/$<id>/edit"
                                                        style="width:20px;border-right:0px;"/>
                                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                                        hrefurl="delete:${pageContext.request.contextPath}/sm/notificationagents/sms-agent/$<id>?_method=DELETE"
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

    function emailAgentShow(){
        document.forms["emailAgentSearch"].action = "${pageContext.request.contextPath}/sm/notificationagents/email-agent";
        document.forms["emailAgentSearch"].submit();
    }
    function smsAgentShow(){
        document.forms["smsAgentSearch"].action = "${pageContext.request.contextPath}/sm/notificationagents/sms-agent";
        document.forms["smsAgentSearch"].submit();
    }
    $(document).ready(function() {
        var type = '<s:property value="%{#request.type}"/>';
        if(type == '<s:property value="@com.elitecore.corenetvertex.constants.NotificationAgentType@EMAIL_AGENT.name()"/>'){
            $("#tab1").attr("class","active");
            $("#tab2").removeAttr("class");
            $("#section1").attr("class","tab-pane fade in active");
        }else if(type == '<s:property value="@com.elitecore.corenetvertex.constants.NotificationAgentType@SMS_AGENT.name()"/>'){
            $("#tab2").attr("class","active");
            $("#tab1").removeAttr("class");
            $("#section2").attr("class","tab-pane fade in active");
        }
    });
</script>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>
