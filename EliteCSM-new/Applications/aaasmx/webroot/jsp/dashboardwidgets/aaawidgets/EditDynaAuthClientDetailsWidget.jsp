<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
 <%String radiusDynaAuthClientWidgetId = request.getParameter("widgetId");%>
 
<script type="text/javascript">
 $(document).ready(function(){ 

	 <%-- Fetch widget Id --%>
	 var radiusDynaAuthClientWidgetId=<%=radiusDynaAuthClientWidgetId%>; 
	 
	 <%-- Assigh Configuration widget Id --%>
	 var confAuthJsp="jsp/dashboardwidgets/aaawidgets/DynaAuthClientDetailsTable.jsp?widgetId="+<%=radiusDynaAuthClientWidgetId%>; 
	
	 <%-- Fetch Widget Configuration --%>
	 fetchWidgetConfiguration(radiusDynaAuthClientWidgetId);
	
 	 var isWidgetConfigSaved_<%=radiusDynaAuthClientWidgetId%> = checkWidgetConfigurationSaved(radiusDynaAuthClientWidgetId);
	 
	 if(isWidgetConfigSaved_<%=radiusDynaAuthClientWidgetId%> == 'false'){
		 $('#cancelButton'+<%=radiusDynaAuthClientWidgetId%>).hide();
	 }
	 
	 <%-- Submit Widget Configuration --%>
	 $("#submitdynaAuthClient"+<%=radiusDynaAuthClientWidgetId%>).click(function () {
		 saveWidgetConfiguration(<%=radiusDynaAuthClientWidgetId%>,confAuthJsp);
	 }); 
	 
	 $('#cancelButton'+<%=radiusDynaAuthClientWidgetId%>).click(function (){
		 $('#'+$('#currentTabId').val()).find($('div#'+<%=radiusDynaAuthClientWidgetId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/DynaAuthClientDetailsTable.jsp?widgetId="+<%=radiusDynaAuthClientWidgetId%>);
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
		<form id="frm<%=radiusDynaAuthClientWidgetId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=radiusDynaAuthClientWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="DynaAuth Client Details" placeholder="Enter Widget Name">
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
							<input type="text" id="<%=radiusDynaAuthClientWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
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
								<textarea rows="3" cols="40" id="<%=radiusDynaAuthClientWidgetId%>CLIENTS" name="CLIENTS"></textarea>
						</td>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Specify comma separated client address or client ranges or network address or keep it blank for getting all clients</label>	
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
					            <input class="light-btn" type="button" value="Save" id="submitdynaAuthClient<%=radiusDynaAuthClientWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=radiusDynaAuthClientWidgetId%>"/>
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