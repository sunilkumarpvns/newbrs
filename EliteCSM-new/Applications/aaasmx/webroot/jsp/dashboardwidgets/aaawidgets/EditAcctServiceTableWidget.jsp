<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<%String acctServiceWidgetId = request.getParameter("widgetId");%>

<script type="text/javascript">
 $(document).ready(function(){ 

	 <%-- Fetch widget Id --%>
	 var acctServiceWidgetId=<%=acctServiceWidgetId%>; 
	 
	 <%-- Assigh Configuration widget Id --%>
	 var confAcctJsp="jsp/dashboardwidgets/aaawidgets/AcctServiceStatTable.jsp?widgetId="+<%=acctServiceWidgetId%>; 
	
	 <%-- Fetch Widget Configuration --%>
	 fetchWidgetConfiguration(acctServiceWidgetId);
	
	 var isWidgetConfigSaved_<%=acctServiceWidgetId%> = checkWidgetConfigurationSaved(acctServiceWidgetId);
		
	 if(isWidgetConfigSaved_<%=acctServiceWidgetId%> == 'false'){
		 $('#cancelButton'+<%=acctServiceWidgetId%>).hide();
	 }
	 
	 <%-- Submit Widget Configuration --%>
	 $("#submitAuthService"+<%=acctServiceWidgetId%>).click(function () {
		 if( $("#"+ <%=acctServiceWidgetId%> +"ELITEAAAINSTANCES").val() == "0" ) {
			 alert("Please select at least one Server Instances ");
			 return;
		 }
		 saveWidgetConfiguration(<%=acctServiceWidgetId%>,confAcctJsp);
	 }); 
	 
	 $('#cancelButton'+<%=acctServiceWidgetId%>).click(function (){
		 $('#'+$('#currentTabId').val()).find($('div#'+<%=acctServiceWidgetId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/AcctServiceStatTable.jsp?widgetId="+<%=acctServiceWidgetId%>);
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
		<form id="frm<%=acctServiceWidgetId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=acctServiceWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="Accounting Service" placeholder="Enter Widget Name">
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
							<input type="text" id="<%=acctServiceWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
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
							 <select class="dashboardTextClass" name="ELITEAAAINSTANCES"  multiple="multiple" id="<%=acctServiceWidgetId%>ELITEAAAINSTANCES">
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
							<label class="dashboardHelpTag">Selects the EliteAAA Instance for which Accounting statistics to display
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
					            <input class="light-btn" type="button" value="Save" id="submitAuthService<%=acctServiceWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=acctServiceWidgetId%>"/>
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
</div>
</html>