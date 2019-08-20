<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertFileListenerData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTrapListenerData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.BaseAlertListener"%>

<%
    String basePath = request.getContextPath();
    AlertListenerData alertListenerData =(AlertListenerData)request.getAttribute("alertListenerData");
  
    AlertListenerTypeData  alertListenertypeData = alertListenerData.getAlertListenerTypeData();
    request.setAttribute("alertListenertypeData",alertListenertypeData);
 %>
 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
 
<script>
setTitle('<bean:message bundle="servermgrResources" key="servermgr.alert.alertlistener"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>"
	border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box" colspan="2">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td valign="top" align="right" width="*">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td valign="top">
										<%if("ALT0001".equalsIgnoreCase(alertListenerData.getTypeId())){
    		    	BaseAlertListener alertFileListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertFileListenerData",alertFileListenerData);
    		    	%>
												<%@ include file="ViewFileAlertListener.jsp"%>
										<%}else if("ALT0002".equalsIgnoreCase(alertListenerData.getTypeId())){
					BaseAlertListener alertTrapListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertTrapListenerData",alertTrapListenerData);
						%>
												<%@ include file="ViewTrapAlertListener.jsp"%>
										<%}else if("ALT0003".equalsIgnoreCase(alertListenerData.getTypeId())){
					BaseAlertListener alertSysListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertSysListenerData",alertSysListenerData);
				%>
												<%@ include file="ViewSysAlertListener.jsp"%>
										<%}%>
												<%@ include file="ViewAlertTree.jsp"%>
											</td>
										</tr>
										<tr>
											<td>
												<%@ include file="/jsp/core/system/history/ViewHistoryJSON.jsp"%>
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top"><%@  include
										file="AlertListenerNavigation.jsp"%></td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>

