<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData"%>

<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<%-- REQUIRED : This Js is required for to get widget Configuration--%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dashboard/widget-configuration.js"/>

<%-- REQUIRED : Code for get Widget Id--%>
<%String totalReqStatsSummaryId=request.getParameter("widgetId");%>
<%  List<ESITypeAndInstanceData> esiList = (List<ESITypeAndInstanceData>)request.getSession().getAttribute("radESIList");
   System.out.println("Esi List Size :  " +esiList.size());
%>

<script type="text/javascript">
$(document).ready(function(){

	 <%-- Fetch widget Id --%>
	 var totalReqStatsSummaryId=<%=totalReqStatsSummaryId%>; 
	 
	 <%-- Assigh Configuration widget Id --%>
	 var confJsp="jsp/dashboardwidgets/aaawidgets/TotalRequestStatsSummary.jsp?widgetId="+<%=totalReqStatsSummaryId%>; 
	
	 var isWidgetConfigSaved_<%=totalReqStatsSummaryId%> = checkWidgetConfigurationSaved(totalReqStatsSummaryId);
		
	 if(isWidgetConfigSaved_<%=totalReqStatsSummaryId%> == 'false'){
		 $('#cancelButton'+<%=totalReqStatsSummaryId%>).hide();
	 }
	 
	 <%-- Fetch Widget Configuration --%>
	 fetchWidgetConfiguration(totalReqStatsSummaryId);
	
	 <%-- Submit Widget Configuration --%>
	 $("#submitTotalReqStats"+<%=totalReqStatsSummaryId%>).click(function () {
		 <%if(esiList.size() > 0){ %>
			 saveWidgetConfiguration(totalReqStatsSummaryId,confJsp);
		<%}else{%>
			alert('Please create atleast one Rad Auth ESI');
			return false;
		<%}%>
	 }); 
	 
	 $('#cancelButton'+<%=totalReqStatsSummaryId%>).click(function (){
		 $('#'+$('#currentTabId').val()).find($('div#'+<%=totalReqStatsSummaryId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/TotalRequestStatsSummary.jsp?widgetId="+<%=totalReqStatsSummaryId%>);
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
		<form id="frm<%=totalReqStatsSummaryId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<!-- input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90" placeholder="Enter Widget Name"> -->
								<input type="text" id="<%=totalReqStatsSummaryId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="Rad Auth ESI Statistics Summary" placeholder="Enter Widget Name">
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
							<!-- input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90" placeholder="Refresh Interval in Minute"> -->
							<input type="text" id="<%=totalReqStatsSummaryId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90" placeholder="Enter Refresh Interval">
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Days Previously :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
							<input type="text" id="<%=totalReqStatsSummaryId%>DAYSPREVIOUSLY" name="DAYSPREVIOUSLY" title="To date" class="dashboardTextClass" value="90" placeholder="Enter Days Previously">
						</td>
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
							<label class="dashboardLabel">Radius Auth ESI :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								 <!-- select class="dashboardTextClass" multiple="multiple"> -->
								 <!-- need to ask id name  -->
								 <select class="dashboardTextClass" name="ELITEAAAINSTANCES" id="<%=totalReqStatsSummaryId%>ELITEAAAINSTANCES" multiple="multiple">
									<%if (esiList != null && esiList.size() > 0) {
										for(ESITypeAndInstanceData esiData:esiList){%>
											<option value="<%=esiData.getName() %>">
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
							<label class="dashboardHelpTag">Selects the Radius ESI for which ESI Request Summary to display</label>	
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
					            <input class="light-btn" type="button" value="Save" id="submitTotalReqStats<%=totalReqStatsSummaryId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=totalReqStatsSummaryId%>"/>
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