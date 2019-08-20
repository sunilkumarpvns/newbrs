<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />

<!-- Code for get Widget Id -->
<%String primaryWidgetId = request.getParameter("widgetId");%>

<script type="text/javascript">

	 $(document).ready(function(){ 
	 
	/*  Fetch widget Id */
	 var primaryWidgetId=<%=primaryWidgetId%>; 
	 
	 /* Assigh Configuration widget Id */
	 var confPrimaryJsp="jsp/dashboardwidgets/aaawidgets/PrimaryServerWidget.jsp?widgetId="+<%=primaryWidgetId%>; 
	
	 /* Fetch Widget Configuration */
	 fetchWidgetConfiguration(primaryWidgetId);
	
	/*  Submit Widget Configuration */
	 $("#submitPrimaryServer"+<%=primaryWidgetId%>).click(function () {
		 saveWidgetConfiguration(<%=primaryWidgetId%>,confPrimaryJsp);
	 }); 
			 
	 $('#cancelButton'+<%=primaryWidgetId%>).click(function (){
		 $('#'+<%=primaryWidgetId%>).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/PrimaryServerWidget.jsp?widgetId="+<%=primaryWidgetId%>);
	 });
	 
 });

 </script>
 <%  List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getSession().getAttribute("serverAAAList");%>
  
</head>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="editConfigPage" name="editConfigPage" value="editConfigPage" />
<tr>
	<td align="left">
		 	<form id="frm<%=primaryWidgetId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=primaryWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="Primary Server"/>
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
						<td align="right" width="30%">
							<label class="dashboardLabel">Refresh Interval :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=primaryWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">How often you would like this widget to update</label>	
						</td>
					</tr>
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Days Previously :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=primaryWidgetId%>DAYSPREVIOUSLY" name="DAYSPREVIOUSLY" title="To date" class="dashboardTextClass" value="90"/>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Days(including Today) to show in graph</label>	
						</td>
					</tr>
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Server Instances :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
							 <select class="dashboardTextClass" name="ELITEAAAINSTANCES" id="<%=primaryWidgetId%>ELITEAAAINSTANCES">
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
							<label class="dashboardHelpTag">Selects the Server Instance for which statistics to display</label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">TPS Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select class="dashboardDDClass" name="TPSUSAGE" id="<%=primaryWidgetId%>TPSUSAGE">
									<option>Yes</option>
									<option>No</option>
								</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show TPS Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Memory Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select class="dashboardDDClass" name="MEMORYUSAGE" id="<%=primaryWidgetId%>MEMORYUSAGE">
									<option>Yes</option>
									<option>No</option>
								</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show Memory Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">CPU Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select  class="dashboardDDClass" name="CPUUSAGE" id="<%=primaryWidgetId%>CPUUSAGE">
									<option>Yes</option>
									<option>No</option>
								</select>
						</td>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show CPU Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Thread Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select class="dashboardDDClass"  name="THREADUSAGE" id="<%=primaryWidgetId%>THREADUSAGE">
									<option>Yes</option>
									<option>No</option>
								</select>
						</td>
					</tr>
						<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show Thread Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2" style="padding-left: 10px;padding-right: 10px;">
							<div style="min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px; padding-top:5px;margin-top:5px;margin-bottom:5px;"></div>
						</td>
					</tr> 
					<tr>
						<td>&nbsp;</td>
						<td align="left" style="padding-left: 10px;padding-top: 10px;">
					            <input class="light-btn" type="button" value="Save" id="submitPrimaryServer<%=primaryWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=primaryWidgetId%>"/>
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
</body>
</html>