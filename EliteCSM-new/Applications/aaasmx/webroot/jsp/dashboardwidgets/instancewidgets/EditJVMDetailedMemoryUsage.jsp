<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >

<%String detailJVMMemoryWidgetId = request.getParameter("widgetId");
 List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getSession().getAttribute("serverAAAList");
%>

<script type="text/javascript">

	 $(document).ready(function(){ 
	 
		 /*  Fetch widget Id */
		 var jvmDetailMemoryWidgetID=<%=detailJVMMemoryWidgetId%>; 

		 /* Assign Configuration widget Id */
		 var confPrimaryJsp="jsp/dashboardwidgets/instancewidgets/JVMDetailedMemoryUsage.jsp?widgetId="+<%=detailJVMMemoryWidgetId%>; 

		 /* Fetch Widget Configuration */
		 fetchWidgetConfiguration(<%=detailJVMMemoryWidgetId%>);
		
		 var isWidgetConfigSaved_<%=detailJVMMemoryWidgetId%> = checkWidgetConfigurationSaved(jvmDetailMemoryWidgetID);
		 
		 if(isWidgetConfigSaved_<%=detailJVMMemoryWidgetId%> == 'false'){
			 $('#cancelButton'+<%=detailJVMMemoryWidgetId%>).hide();
		 }
		 
		/*  Submit Widget Configuration */
		 $("#submitLiveMemoryWidget"+<%=detailJVMMemoryWidgetId%>).click(function () {
			 if( $("#"+<%=detailJVMMemoryWidgetId%>+"ELITEAAAINSTANCES").val() == "0" ) {
				 alert("Please select at least one Server Instance");
				 return;
			 }
			 saveWidgetConfiguration(<%=detailJVMMemoryWidgetId%>,confPrimaryJsp);
		 }); 
				 
		 $('#cancelButton'+<%=detailJVMMemoryWidgetId%>).click(function (){
			 $('#'+$('#currentTabId').val()).find($('div#'+<%=detailJVMMemoryWidgetId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/instancewidgets/JVMDetailedMemoryUsage.jsp?widgetId="+<%=detailJVMMemoryWidgetId%>);
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
		<form id="frm<%=detailJVMMemoryWidgetId%>">
		<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=detailJVMMemoryWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="JVM Detail Memory"/>
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
								<input type="text" id="<%=detailJVMMemoryWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
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
								<input type="text" id="<%=detailJVMMemoryWidgetId%>DAYSPREVIOUSLY" name="DAYSPREVIOUSLY" title="To date" class="dashboardTextClass" value="90"/>
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
							 <select class="dashboardTextClass" name="ELITEAAAINSTANCES" id="<%=detailJVMMemoryWidgetId%>ELITEAAAINSTANCES">
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
					            <input class="light-btn" type="button" value="Save" id="submitLiveMemoryWidget<%=detailJVMMemoryWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=detailJVMMemoryWidgetId%>"/>
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