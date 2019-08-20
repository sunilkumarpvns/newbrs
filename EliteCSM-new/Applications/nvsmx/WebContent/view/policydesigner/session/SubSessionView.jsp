<%@page import="com.elitecore.commons.base.Strings"%>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
	String sessionId = request.getParameter("sessionId");
	if(Strings.isNullOrBlank(sessionId)) {
		sessionId = "RxSessionInformation";
	}
%>
<nv:dataTable
		id="<%=sessionId%>"
		actionUrl="/genericSearch/policydesigner/session/Session/searchData"
		beanType="com.elitecore.nvsmx.policydesigner.model.session.SessionData"
		dataListName="dataList"
		rows="<%=rows%>"
		pages="5"
		showPagination="false"
		showInfo="false"
		width="100%"
		cssClass="table table-blue"
		subTableUrl="/subTable/policydesigner/session/Session/viewDetail" >
	<nv:dataTableColumn title="Session Id"  		beanProperty="sessionInfo[CS.SessionID]" type="map" tdCssClass="word-break" />
	<nv:dataTableColumn title="Subscriber Identity" beanProperty="sessionInfo[Sub.SubscriberIdentity]"  type="map" tdCssClass="word-break"/>
	<nv:dataTableColumn title="Server Instance"  	beanProperty="instanceData.name"  tdCssClass="word-break"/>
	<nv:dataTableColumn title="Creation Time"  		beanProperty="creationTime" tdCssClass="word-break" />
	<nv:dataTableColumn title="Last Update Time"  	beanProperty="lastUpdateTime"  tdCssClass="word-break"/>
	<nv:dataTableColumn title="Session IPv4"  		beanProperty="sessionInfo[CS.SessionIPv4]"  		type="map" tdCssClass="word-break"/>
	<nv:dataTableColumn title="Session Type"  		beanProperty="sessionInfo[CS.SessionType]"  		type="map" tdCssClass="word-break"/>
	<nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-log-out' data-toggle='tooltip' data-placement='bottom' title='Disconnect' style='right : -5px;'></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/session/Session/disconnect?coreSessionID=sessionInfo[CS.CoreSessionID]&insatanceIDCode=instanceData.id" style="width:20px;"   />
	<nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/session/Session/delete?coreSessionID=sessionInfo[CS.CoreSessionID]&insatanceIDCode=instanceData.id" 	 style="width:20px;"  />
</nv:dataTable>

