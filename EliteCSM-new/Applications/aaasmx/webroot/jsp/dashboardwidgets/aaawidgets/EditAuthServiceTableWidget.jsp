<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
 <%String authServiceWidgetId = request.getParameter("widgetId");%>
 
<script type="text/javascript">
 $(document).ready(function(){ 

	 <%-- Fetch widget Id --%>
	 var authServiceWidgetId=<%=authServiceWidgetId%>; 
	 
	 <%-- Assigh Configuration widget Id --%>
	 var confAuthJsp="jsp/dashboardwidgets/aaawidgets/AuthServiceStatTable.jsp?widgetId="+<%=authServiceWidgetId%>; 
	
	 <%-- Fetch Widget Configuration --%>
	 fetchWidgetConfiguration(authServiceWidgetId);
	
	 var isWidgetConfigSaved_<%=authServiceWidgetId%> = checkWidgetConfigurationSaved(authServiceWidgetId);
	 
	 if(isWidgetConfigSaved_<%=authServiceWidgetId%> == 'false'){
		 $('#cancelButton'+<%=authServiceWidgetId%>).hide();
	 }
	 
	 <%-- Submit Widget Configuration --%>
	 $("#submitAuthService"+<%=authServiceWidgetId%>).click(function () {
		 if( $("#"+ <%=authServiceWidgetId%> +"ELITEAAAINSTANCES").val() == "0" ) {
			 alert("Please select at least one Server Instances ");
			 return;
		 }
		 saveWidgetConfiguration(<%=authServiceWidgetId%>,confAuthJsp);
	 }); 
	 
	 $('#cancelButton'+<%=authServiceWidgetId%>).click(function (){
		 $('#'+$('#currentTabId').val()).find($('div#'+<%=authServiceWidgetId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/AuthServiceStatTable.jsp?widgetId="+<%=authServiceWidgetId%>);
	 });
 });
</script>

<%  List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getSession().getAttribute("serverAAAList");%>
 
</head>
<body>
<div style="min-width: 200px;overflow: auto;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="editConfigPage" name="editConfigPage" value="editConfigPage" />
<tr>
	<td align="left">
		<form id="frm<%=authServiceWidgetId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=authServiceWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="Authentication Service" placeholder="Enter Widget Name">
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">The name of Widget on Dashboard</label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Refresh Interval :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
							<input type="text" id="<%=authServiceWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">
								How often you would like this widget to update
							 </label>	
						</td>
					</tr>
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Server Instances :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
							 <select class="dashboardTextClass" name="ELITEAAAINSTANCES" multiple="multiple" id="<%=authServiceWidgetId%>ELITEAAAINSTANCES">
							 		<option value="0">--select--</option>
									<%if (netServerInstanceList != null && netServerInstanceList.size() > 0) {
										for(NetServerInstanceData netServerInstanceData:netServerInstanceList){%>
											<option value="<%=netServerInstanceData.getNetServerId()%>">
												<%=netServerInstanceData.getName() %>
											</option>
										<%}
									} %>
							</select> 
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Selects the EliteAAA Instance for which Authentication statistics to display
							</label>	
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2" style="padding-left: 10px;padding-right: 10px;">
							<div style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px; padding-top:5px;margin-top:5px;margin-bottom:5px;"></div>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td align="left" style="padding-left: 10px;padding-top: 10px;">
					            <input class="light-btn" type="button" value="Save" id="submitAuthService<%=authServiceWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=authServiceWidgetId%>"/>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
				
				
		</form>
	</td>
</tr>
</table>
</div>
</body>
</html>