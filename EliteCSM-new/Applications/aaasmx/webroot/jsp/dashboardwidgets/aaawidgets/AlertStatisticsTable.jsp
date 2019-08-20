<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%-- <LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" /> --%>
<script type="text/javascript">
	$(document).ready(function(){
		
	});
	
	function hideProgressBar(){
		$('#alertProgressDivId').hide();
	}
</script>
</head>
<body style="min-width: 100%;">
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditAlertStatsWidget.jsp" id="editJsp">
<%--ProgressBar Div : Start  --%>
 
 <div id="alertProgressDivId" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>					
					
						<%-- 		<table  border="0" cellspacing="0" cellpadding="0" style="padding-bottom: 20px;" width="100%">
									<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;background-color: #E6E6E6;font-weight: bold; color: #015198">
											<td class="dashboardTableHeader" width="12%">Timestamp</td>
		 									<td class="dashboardTableHeader" width="4%">&nbsp;</td>
		 									<td class="dashboardTableHeader" width="24%">Server</td>
		 									<td class="dashboardTableHeader" width="20%">Alert</td>
		 									<td class="dashboardTableHeader" width="40%">Description</td>
									</tr>
									<tr>
											<td align="left" class="dashboardTableData" style="color: #015198;padding: 1px;" colspan="5" >18 Mar 2013</td>
									</tr>
									<tr>
		 									<td align="right" class="dashboardTableData" valign="top">10:58:53,961</td>
		 									<td align="center" class="dashboardTableData" valign="top"><img src="<%=basePath%>/images/icons/priority_minor.gif" /></td>
		 									<td align="left" class="dashboardTableData" valign="top">AAA-Banglore-Primary</td>
		 									<td align="left" class="dashboardTableData" valign="top">SERVER DOWN</td>
		 									<td align="left" class="dashboardTableData" valign="top">EliteAAA server shutdown successfully</td>
		 								</tr>
		 								<tr>
		 									<td align="right" class="dashboardTableData" valign="top">10:58:53,961</td>
		 									<td align="center" class="dashboardTableData" valign="top"><img src="<%=basePath%>/images/icons/priority_blocker.gif" /></td>
		 									<td align="left" class="dashboardTableData" valign="top">AAA-Banglore-Primary</td>
		 									<td align="left" class="dashboardTableData" valign="top">LICENSE</td>
		 									<td align="left" class="dashboardTableData" valign="top">Invalid License,number of available processor is not mathching with license number(licensed:4,available:2)</td>
		 								</tr>
		 								
		 								<tr>
		 									<td align="right" class="dashboardTableData" valign="top">10:58:55,614</td>
		 									<td align="center" class="dashboardTableData" valign="top"><img src="<%=basePath%>/images/icons/priority_minor.gif" /></td>
		 									<td align="left" class="dashboardTableData" valign="top">AAA-Banglore-Primary</td>
		 									<td align="left" class="dashboardTableData" valign="top">DIAMETER-STACK</td>
		 									<td align="left" class="dashboardTableData" valign="top">Diameter Stack UP</td>
		 								</tr>
		 								
		 								<tr class="dashboardTableData">
		 									<td align="left" class="dashboardTableData" style="color: #015198;padding: 1px;" colspan="5">19-Mar-13</td>
		 								</tr>
		 								<tr>
		 									<td align="right" class="dashboardTableData" valign="top">10:58:55,624</td>
		 									<td align="center" class="dashboardTableData" valign="top"><img src="<%=basePath%>/images/icons/priority_minor.gif" /></td>
		 									<td align="left" class="dashboardTableData" valign="top">AAA-Banglore-Primary</td>
		 									<td align="left" class="dashboardTableData" valign="top">SERVER UP</td>
		 									<td align="left" class="dashboardTableData" valign="top">EliteAAA server started successfully with services: RAD-AUTH,RAD-ACCT,WEB-SERVICE ,DIAMETER-SER</td>
		 								</tr>
		 								<tr>
		 									<td align="right" class="dashboardTableData" valign="top">11:12:25,381</td>
		 									<td align="center" class="dashboardTableData" valign="top"><img src="<%=basePath%>/images/icons/priority_major.gif" /></td>
		 									<td align="left" class="dashboardTableData" valign="top">AAA-Banglore-Primary</td>
		 									<td align="left" class="dashboardTableData" valign="top">DATABASE QUERY TIMEOUT</td>
		 									<td align="left" class="dashboardTableData" valign="top">OPEN-DB Query execution time getting high, - Last Query execution time = 61 milliseconds.</td>
		 								</tr>
		 								
		 								<tr>
		 									<td align="right" class="dashboardTableData" valign="top">11:12:25,390</td>
		 									<td align="center" class="dashboardTableData" valign="top"><img src="<%=basePath%>/images/icons/priority_major.gif" /></td>
		 									<td align="left" class="dashboardTableData" valign="top">AAA-Banglore-Primary</td>
		 									<td align="left" class="dashboardTableData" valign="top">HIGH AAA RESPONSE TIME</td>
		 									<td align="left" class="dashboardTableData" valign="top">High Response Time : 158.0ms.</td>
		 								</tr>
		 								
		 								<tr>
		 									<td align="right" class="dashboardTableData" valign="top">11:13:28,280</td>
		 									<td align="center" class="dashboardTableData" valign="top"><img src="<%=basePath%>/images/icons/priority_minor.gif" /></td>
		 									<td align="left" class="dashboardTableData" valign="top">AAA-Banglore-Primary</td>
		 									<td align="left" class="dashboardTableData" valign="top">CONTRAINTS</td>
		 									<td align="left" class="dashboardTableData" valign="top">Unique Constrain Violated in db.</td>
		 								</tr>
								</table> --%>
								</body>
		 			
</html>