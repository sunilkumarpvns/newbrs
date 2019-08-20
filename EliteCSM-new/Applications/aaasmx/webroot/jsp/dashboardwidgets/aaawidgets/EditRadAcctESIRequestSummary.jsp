<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData"%>

<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<%-- REQUIRED : This Js is required for to get widget Configuration--%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dashboard/widget-configuration.js"/>

<%-- REQUIRED : Code for get Widget Id--%>
<%String acctESIRequestSummaryId=request.getParameter("widgetId");%>
<%  List<ESITypeAndInstanceData> radiusAcctESIPageList = (List<ESITypeAndInstanceData>)request.getSession().getAttribute("radiusAcctESIPageList");%>
 
<script type="text/javascript">
$(document).ready(function(){

	 <%-- Fetch widget Id --%>
	 var acctESIRequestSummaryId=<%=acctESIRequestSummaryId%>; 
	 
	 <%-- Assigh Configuration widget Id --%>
	 var confJsp="jsp/dashboardwidgets/aaawidgets/RadAcctESIRequestSummary.jsp?widgetId="+<%=acctESIRequestSummaryId%>; 
	
	 var isWidgetConfigSaved_<%=acctESIRequestSummaryId%> = checkWidgetConfigurationSaved(acctESIRequestSummaryId);
		
	 if(isWidgetConfigSaved_<%=acctESIRequestSummaryId%> == 'false'){
		 $('#cancelButton'+<%=acctESIRequestSummaryId%>).hide();
	 }
	 
	 <%-- Fetch Widget Configuration --%>
	 fetchWidgetConfiguration(acctESIRequestSummaryId);
	
	 <%-- Submit Widget Configuration --%>
	 $("#submitTotalReqStats"+<%=acctESIRequestSummaryId%>).click(function () {
		 <%if(radiusAcctESIPageList.size() > 0){ %>
			 saveWidgetConfiguration(acctESIRequestSummaryId,confJsp);
		 <%}else{%>
			alert('Please create atleast one Rad Acct ESI');
			return false;
		<%}%>
	 }); 
	 
	 $('#cancelButton'+<%=acctESIRequestSummaryId%>).click(function (){
		 $('#'+$('#currentTabId').val()).find($('div#'+<%=acctESIRequestSummaryId%>)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/RadAcctESIRequestSummary.jsp?widgetId="+<%=acctESIRequestSummaryId%>);
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
		<form id="frm<%=acctESIRequestSummaryId%>">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<!-- input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="90" placeholder="Enter Widget Name"> -->
								<input type="text" id="<%=acctESIRequestSummaryId%>NAME" name="NAME" title="To date" class="dashboardTextClass" value="Rad Acct ESI Request Summary" placeholder="Enter Widget Name">
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
							<input type="text" id="<%=acctESIRequestSummaryId%>REFRESHINTERVAL" name="REFRESHINTERVAL" title="To date" class="dashboardTextClass" value="90" placeholder="Enter Refresh Interval">
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Days Previously :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
							<input type="text" id="<%=acctESIRequestSummaryId%>DAYSPREVIOUSLY" name="DAYSPREVIOUSLY" title="To date" class="dashboardTextClass" value="90" placeholder="Enter Days Previously">
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
							<label class="dashboardLabel">Radius Acct ESI :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								 <!-- select class="dashboardTextClass" multiple="multiple"> -->
								 <!-- need to ask id name  -->
								 <select class="dashboardTextClass" name="ELITEAAAINSTANCES" id="<%=acctESIRequestSummaryId%>ELITEAAAINSTANCES" multiple="multiple">
									<%if (radiusAcctESIPageList != null && radiusAcctESIPageList.size() > 0) {
										for(ESITypeAndInstanceData esiData:radiusAcctESIPageList){%>
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
					            <input class="light-btn" type="button" value="Save" id="submitTotalReqStats<%=acctESIRequestSummaryId%>">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton<%=acctESIRequestSummaryId%>"/>
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