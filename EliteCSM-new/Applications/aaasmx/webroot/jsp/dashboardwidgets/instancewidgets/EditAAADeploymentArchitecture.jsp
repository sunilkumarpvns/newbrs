<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
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
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="editConfigPage" name="editConfigPage" value="editConfigPage" />
<tr>
	<td align="left">
		<!-- <form action="#" method="post" id="d"  > -->
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Days Previously :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90">
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
						<td align="right">
							<label class="dashboardLabel">TPS Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select id="tpsUsage" class="dashboardDDClass">
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
								<select id="memoryUsage" class="dashboardDDClass">
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
								<select id="cpuUsage" class="dashboardDDClass">
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
								<select id="threadUsage" class="dashboardDDClass">
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
						<td align="right">
							<label class="dashboardLabel">
								<label class="dashboardLabel">Refresh Interval :</label>
							</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">	
							<select class="dashboardDDClass">
								<option>Never</option>
								<option>Every 15 Minute</option>
								<option>Every 30 Minute</option>
								<option>Every 1 Hour</option>
								<option>Every 2 Hours</option>
							</select></td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">How often you would like this gadget to update</label>	
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