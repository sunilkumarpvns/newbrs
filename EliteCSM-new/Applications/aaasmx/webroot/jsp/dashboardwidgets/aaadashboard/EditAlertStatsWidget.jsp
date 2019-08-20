<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<script type="text/javascript">
 $('#testButton').click(function () {
	/*  $.ajax({
	        type: "POST",
	        url: "jsp/dashboardwidgets/aaadashboard/PrimaryServerWidget.jsp",
	        success: function(responseText) {
	        	$('#widget1').find(".widgetcontent").load(responseText); 
	        }
	    }); */
	 $('#widget1').find(".widgetcontent").load("jsp/dashboardwidgets/aaadashboard/PrimaryServerWidget.jsp");
}); 
 $('#cancelButton').click(function (){
	 $('#widget1').find(".widgetcontent").load("jsp/dashboardwidgets/aaadashboard/PrimaryServerWidget.jsp");
 });
 </script>
</head>
 <%  List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getSession().getAttribute("allServerList");%>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="editConfigPage" name="editConfigPage" value="editConfigPage" />
<tr>
	<td align="left">
		<!-- <form action="#" method="post" id="d"  > -->
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90" placeholder="Enter Widget Name">
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
							<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90" placeholder="Refresh Interval in Minute">
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
							<label class="dashboardLabel">Days Previously :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
							<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90" placeholder="Refresh Interval in Minute">
						</td>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Days (including Today) to show in Table</label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Severity :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
							<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90" placeholder="Refresh Interval in Minute">
						</td>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Select Severity Level to display</label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Server Instances :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
							<select class="dashboardTextClass">
									<%if (netServerInstanceList != null && netServerInstanceList.size() > 0) {
										for(NetServerInstanceData netServerInstanceData:netServerInstanceList){%>
											<option value="<%=netServerInstanceData.getNetServerId()%>">
												<%=netServerInstanceData.getName() %>
											</option>
										<%}
									} %>
								</select> 
						</td>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Selects the Server Instance for which alerts to display</label>	
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
					            <input class="light-btn" type="submit" value="Save" id="testButton">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton"/>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
				
				
		<!-- </form> -->
	</td>
</tr>
</table>
</body>
</html>