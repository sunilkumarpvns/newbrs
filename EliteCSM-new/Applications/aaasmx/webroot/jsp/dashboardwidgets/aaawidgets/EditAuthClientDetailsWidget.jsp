<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
 <%String authClientWidgetId = request.getParameter("widgetId");%>
 
<script type="text/javascript">
 $(document).ready(function(){ 

	 <%-- Fetch widget Id --%>
	 var authClientWidgetId=<%=authClientWidgetId%>; 
	 
	 <%-- Assigh Configuration widget Id --%>
	 var confAuthJsp="jsp/dashboardwidgets/aaawidgets/AuthClientDetailsTable.jsp?widgetId="+<%=authClientWidgetId%>; 
	
	 <%-- Fetch Widget Configuration --%>
	 fetchWidgetConfiguration(authClientWidgetId);
	
	 var isWidgetConfigSaved_<%=authClientWidgetId%> = checkWidgetConfigurationSaved(authClientWidgetId);
		
	 if(isWidgetConfigSaved_<%=authClientWidgetId%> == 'false'){
		 $('#cancelButton'+<%=authClientWidgetId%>).hide();
	 }
	 
	 <%-- Submit Widget Configuration --%>
	 $("#submitauthClient"+<%=authClientWidgetId%>).click(function () {
		 if( $("#"+ <%=authClientWidgetId%> +"ELITEAAAINSTANCES").val() == "0" ) {
			 alert("Please select at least one Server Instances ");
			 return;
		 }
		 
		 saveWidgetConfiguration(<%=authClientWidgetId%>,confAuthJsp);
	 }); 
	 
	 $('#cancelButton'+<%=authClientWidgetId%>).click(function (){
		 $('#'+$('#currentTabId').val()).find($('div#'+<%=authClientWidgetId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/AuthClientDetailsTable.jsp?widgetId="+<%=authClientWidgetId%>);
	 });
 });
</script>

 
</head>
<body>
<div style="min-width: 200px;overflow: auto;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="editConfigPage" name="editConfigPage" value="editConfigPage" />
<tr>
	<td align="left">
		<form id="frm<%=authClientWidgetId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=authClientWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="Auth Client Details" placeholder="Enter Widget Name">
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
							<input type="text" id="<%=authClientWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
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
						<td align="right">
							<label class="dashboardLabel">Clients :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<textarea rows="3" cols="40" id="<%=authClientWidgetId%>CLIENTS" name="CLIENTS"></textarea>
						</td>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Specify comma separated client address or client ranges or network address or keep it blank for all clients</label>	
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
					            <input class="light-btn" type="button" value="Save" id="submitauthClient<%=authClientWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=authClientWidgetId%>"/>
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