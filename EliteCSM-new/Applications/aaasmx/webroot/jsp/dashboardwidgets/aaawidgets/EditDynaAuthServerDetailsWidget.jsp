<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData"%>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
 <%String dynaAuthServerWidgetId = request.getParameter("widgetId");%>
 
<script type="text/javascript">
 $(document).ready(function(){ 

	 <%-- Fetch widget Id --%>
	 var dynaAuthServerWidgetId=<%=dynaAuthServerWidgetId%>; 
	 
	 <%-- Assigh Configuration widget Id --%>
	 var confAuthJsp="jsp/dashboardwidgets/aaawidgets/DynaAuthServerDetailsTable.jsp?widgetId="+<%=dynaAuthServerWidgetId%>; 
	
	 <%-- Fetch Widget Configuration --%>
	 fetchWidgetConfiguration(dynaAuthServerWidgetId);
	
	 var isWidgetConfigSaved_<%=dynaAuthServerWidgetId%> = checkWidgetConfigurationSaved(dynaAuthServerWidgetId);
	 
	 if(isWidgetConfigSaved_<%=dynaAuthServerWidgetId%> == 'false'){
		 $('#cancelButton'+<%=dynaAuthServerWidgetId%>).hide();
	 }
	 
	 <%-- Submit Widget Configuration --%>
	 $("#submitDynaAuthServer"+<%=dynaAuthServerWidgetId%>).click(function () {
		 saveWidgetConfiguration(<%=dynaAuthServerWidgetId%>,confAuthJsp);
	 }); 
	 
	 $('#cancelButton'+<%=dynaAuthServerWidgetId%>).click(function (){
		 $('#'+$('#currentTabId').val()).find($('div#'+<%=dynaAuthServerWidgetId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/DynaAuthServerDetailsTable.jsp?widgetId="+<%=dynaAuthServerWidgetId%>);
	 });
 });
</script>
<%  List<ESITypeAndInstanceData> radiusAcctESIPageList = (List<ESITypeAndInstanceData>)request.getSession().getAttribute("radiusNASESIPageList");%>
  

 
</head>
<body>
<div style="min-width: 200px;overflow: auto;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="editConfigPage" name="editConfigPage" value="editConfigPage" />
<tr>
	<td align="left">
		<form id="frm<%=dynaAuthServerWidgetId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="<%=dynaAuthServerWidgetId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="DynaAuth Server Details" placeholder="Enter Widget Name">
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
							<input type="text" id="<%=dynaAuthServerWidgetId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90"/>
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
							<label class="dashboardLabel">NAS Server :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<%-- <textarea rows="3" cols="40" id="<%=dynaAuthServerWidgetId%>CLIENTS" name="CLIENTS"></textarea> --%>
								 <select class="dashboardTextClass" name="ELITEAAAINSTANCES" id="<%=dynaAuthServerWidgetId%>ELITEAAAINSTANCES" multiple="multiple">
									<%if (radiusAcctESIPageList != null && radiusAcctESIPageList.size() > 0) {
										for(ESITypeAndInstanceData esiData:radiusAcctESIPageList){%>
											<option value="<%=esiData.getEsiTypeId()%>">
												<%=esiData.getName() %>
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
							<label class="dashboardHelpTag">Select NAS Server to display server details.</label>	
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
					            <input class="light-btn" type="button" value="Save" id="submitDynaAuthServer<%=dynaAuthServerWidgetId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=dynaAuthServerWidgetId%>"/>
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