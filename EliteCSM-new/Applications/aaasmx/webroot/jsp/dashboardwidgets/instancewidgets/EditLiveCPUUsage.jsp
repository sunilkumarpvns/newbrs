<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >

<%String liveCpuWidgetId = request.getParameter("widgetId");
 List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getSession().getAttribute("serverAAAList");
%>

<script type="text/javascript">

	 $(document).ready(function(){ 
	 
	/*  Fetch widget Id */
	 var liveCpuWidgetId=<%=liveCpuWidgetId%>; 

	 /* Assign Configuration widget Id */
	 var confPrimaryJsp="jsp/dashboardwidgets/instancewidgets/LiveCPUUsage.jsp?widgetId="+<%=liveCpuWidgetId%>; 
	
	 /* Fetch Widget Configuration */
	 fetchWidgetConfiguration(liveCpuWidgetId);
	
	/*  Submit Widget Configuration */
	 $("#submitLiveMemoryWidget"+<%=liveCpuWidgetId%>).click(function () {
		 if( $("#"+ <%=liveCpuWidgetId%>+"ELITEAAAINSTANCES").val() == null ) {
			 alert("Please select at least one Server Instance");
			 return;
		 }
		 saveWidgetConfiguration(<%=liveCpuWidgetId%>,confPrimaryJsp);
	 }); 
			 
	 $('#cancelButton'+<%=liveCpuWidgetId%>).click(function (){
		 console.log($('#'+<%=liveCpuWidgetId%>).find(".widgetcontent"));
		 $('#'+<%=liveCpuWidgetId%>).find(".widgetcontent").load("jsp/dashboardwidgets/instancewidgets/LiveCPUUsage.jsp?widgetId="+<%=liveCpuWidgetId%>);
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
		<form id="frm<%=liveCpuWidgetId%>">
		<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=liveCpuWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="Live CPU and Memory Usage"/>
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
								<input type="text" id="<%=liveCpuWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
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
								<input type="text" id="<%=liveCpuWidgetId%>DAYSPREVIOUSLY" name="DAYSPREVIOUSLY" title="To date" class="dashboardTextClass" value="90"/>
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
							 <select class="dashboardTextClass" multiple="multiple" name="ELITEAAAINSTANCES" id="<%=liveCpuWidgetId%>ELITEAAAINSTANCES">
							 		<option value="0">--select--</option>
									<%if (netServerInstanceList != null && netServerInstanceList.size() > 0) {
										for(NetServerInstanceData netServerInstanceData:netServerInstanceList){%>
											<option value="<%=netServerInstanceData.getName()%>">
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
						<td>&nbsp;</td>
						<td align="left" style="padding-left: 10px;padding-top: 10px;">
					            <input class="light-btn" type="button" value="Save" id="submitLiveMemoryWidget<%=liveCpuWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=liveCpuWidgetId%>"/>
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