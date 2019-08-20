<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<%
	String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>
<script type="text/javascript">
var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}
</script>

<nv:dataTable id="subscriberActiveSession"
	actionUrl="/genericSearch/policydesigner/session/Session/searchData"
	beanType="com.elitecore.nvsmx.policydesigner.model.session.SessionData"
	dataListName="dataList" 
	width="100%" 
	cssClass="table table-blue"
	showInfo="false"
	showPagination="false"
	subTableUrl="/policydesigner/session/Session/viewDetail">
	<nv:dataTableColumn title="Session Id"  		beanProperty="sessionInfo[CS.SessionID]" type="map" tdCssClass="word-break" />
	<nv:dataTableColumn title="Subscriber Identity" beanProperty="sessionInfo[Sub.SubscriberIdentity]" type="map" tdCssClass="word-break" />
	<nv:dataTableColumn title="Server Instance"  	beanProperty="instanceData.name"  tdCssClass="word-break" />
	<nv:dataTableColumn title="Creation Time" beanProperty="creationTime" tdCssClass="word-break" />
	<nv:dataTableColumn title="Last Update Time" beanProperty="lastUpdateTime" tdCssClass="word-break" />
	<nv:dataTableColumn title="Session IPv4" beanProperty="sessionInfo[CS.SessionIPv4]" type="map" tdCssClass="word-break" />
	<nv:dataTableColumn title="Session Type" beanProperty="sessionInfo[CS.SessionType]" type="map" tdCssClass="word-break" />
	<nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-log-out' data-toggle='tooltip' data-placement='bottom' title='Disconnect' ></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/session/Session/disconnect?coreSessionID=sessionInfo[CS.CoreSessionID]&insatanceIDCode=instanceData.id&fromViewPage=true" tdStyle="width:5px"  />
	<nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/session/Session/delete?coreSessionID=sessionInfo[CS.CoreSessionID]&insatanceIDCode=instanceData.id&fromViewPage=true" tdStyle="width:5px" />
</nv:dataTable>