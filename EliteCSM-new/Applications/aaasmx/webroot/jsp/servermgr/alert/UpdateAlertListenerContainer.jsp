<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.BaseAlertListener"%>
<%
    String basePath = request.getContextPath();
    AlertListenerData alertListenerData =(AlertListenerData)request.getAttribute("alertListenerData");
    AlertListenerTypeData  alertListenertypeData = alertListenerData.getAlertListenerTypeData();
    request.setAttribute("alertListenertypeData",alertListenertypeData);
 	
%>

 <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<%! 
public void drawTree(JspWriter out,AlertTypeData alertData,String[] selectedAlertsType) throws Exception {
	boolean flag = false;
	if(selectedAlertsType != null && selectedAlertsType.length > 0) {
		for(int i=0;i<selectedAlertsType.length;i++) {
			String alertId = selectedAlertsType[i];
			if((alertData.getAlertTypeId()).equalsIgnoreCase(alertId))
				flag = true;
		}
	}
	if(alertData.getType().equalsIgnoreCase("L")) {
		if(flag)
			out.write("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=alerts value=" + '"' + alertData.getAlertTypeId() + '"' + "checked" + '/' + '>' + alertData.getName() +"</li>");
		else
			out.write("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=alerts value=" + '"' + alertData.getAlertTypeId() + '"' + '/' + '>' + alertData.getName() +"</li>");			
	}else{
		Set<AlertTypeData> childList = alertData.getNestedChildDetailList();
		Iterator<AlertTypeData> childListIterator = childList.iterator();
		while(childListIterator.hasNext()) {
			AlertTypeData childAlertData = (AlertTypeData)childListIterator.next();
			if(childAlertData.getType().equalsIgnoreCase("P")){
				out.write("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=" + '"' + childAlertData.getName() + "value=" + '"' + childAlertData.getAlertTypeId() + '"' + '/' + '>' + "<b>" + childAlertData.getName() + "</b>" + "<ul>");
			}
			drawTree(out,childAlertData,selectedAlertsType);
			if(childAlertData.getType().equalsIgnoreCase("P")){
			out.write("</li></ul>");
			}
		}
	}
}
%>

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
										<%if("ALT0001".equalsIgnoreCase(alertListenerData.getTypeId())){
    		    	
    		    	BaseAlertListener alertFileListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertFileListenerData",alertFileListenerData);
    		    	
    		    	%>
										<tr>
											<td valign="top"><%@ include
													file="ViewFileAlertListener.jsp"%>
											</td>
										</tr>

										<tr>
											<td valign="top"><%@ include
													file="UpdateFileAlertListener.jsp"%>
											</td>
										</tr>
										<%}else if("ALT0002".equalsIgnoreCase(alertListenerData.getTypeId())){
				  
					BaseAlertListener alertTrapListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertTrapListenerData",alertTrapListenerData);
    		    	
				
				%>
										<tr>
											<td valign="top"><%@ include
													file="ViewTrapAlertListener.jsp"%>
											</td>
										</tr>
										<tr>
											<td valign="top"><%@ include
													file="UpdateTrapAlertListener.jsp"%>
											</td>
										</tr>
										<%} else if("ALT0003".equalsIgnoreCase(alertListenerData.getTypeId())){
				  
					BaseAlertListener alertSysListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertSysListenerData",alertSysListenerData);
				%>
										<tr>
											<td valign="top"><%@ include
													file="ViewSysAlertListener.jsp"%></td>
										</tr>
										<tr>
											<td valign="top"><%@ include
													file="UpdateSysAlertListener.jsp"%>
											</td>
										</tr>
										<%}%>
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