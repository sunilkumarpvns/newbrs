<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
	String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>
<script type="text/javascript">
var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}
</script>

<s:form id="SearchSessionForm" method="post" cssClass="form-vertical">
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="session.search"></s:text> </h3>		
	</div>
	<div class="panel-body"> 

		<nv:dataTable
			id="sessionImplDatas"
			actionUrl="/genericSearch/policydesigner/session/Session/searchData"
			beanType="com.elitecore.nvsmx.policydesigner.model.session.SessionData" 
			dataListName="dataList"
			rows="<%=rows%>"						
			width="100%"
			showPagination="true"
			cssClass="table table-blue" 
			subTableUrl="/policydesigner/session/Session/viewDetail" >
			<nv:dataTableColumn title="Session Id"  		beanProperty="sessionInfo[CS.SessionID]" type="map" tdCssClass="word-break" />
			<nv:dataTableColumn title="Subscriber Id" beanProperty="sessionInfo[Sub.SubscriberIdentity]"  type="map" tdCssClass="word-break"/>
			<nv:dataTableColumn title="Server Instance"  	beanProperty="instanceData.name"  tdCssClass="word-break"/>
			<nv:dataTableColumn title="Creation Time"  		beanProperty="creationTime" tdCssClass="word-break" />
			<nv:dataTableColumn title="Last Update Time"  	beanProperty="lastUpdateTime"  tdCssClass="word-break"/>
			<nv:dataTableColumn title="Session IPv4"  		beanProperty="sessionInfo[CS.SessionIPv4]"  		type="map" tdCssClass="word-break"/>
			<nv:dataTableColumn title="Session Type"  		beanProperty="sessionInfo[CS.SessionType]"  		type="map" tdCssClass="word-break"/>
			<nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-refresh' data-toggle='tooltip' data-placement='bottom' title='ReAuth' style='right : -5px;'></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/session/Session/reauth?coreSessionID=sessionInfo[CS.CoreSessionID]&insatanceIDCode=instanceData.id" style="width:20px;"   />
			<nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-log-out' data-toggle='tooltip' data-placement='bottom' title='Disconnect' style='right : -5px;'></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/session/Session/disconnect?coreSessionID=sessionInfo[CS.CoreSessionID]&insatanceIDCode=instanceData.id" style="width:20px;"   />
			<nv:dataTableColumn title="" 	icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/session/Session/delete?coreSessionID=sessionInfo[CS.CoreSessionID]&insatanceIDCode=instanceData.id" 	 style="width:20px;"  />
		</nv:dataTable>
	</div>
</div>
 </s:form>
