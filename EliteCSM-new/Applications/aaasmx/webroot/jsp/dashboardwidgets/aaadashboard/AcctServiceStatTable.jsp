<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
	<style type="text/css">

	</style>
</head>
<body style="min-width: 100%;">
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaadashboard/EditAcctServiceTableWidget.jsp" id="editJsp">
						<table  border="0" cellspacing="0" cellpadding="0" width="100%">
		 								<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;background-color: #E6E6E6;font-weight: bold;">
		 									<td rowspan="2" class="dashboardTableHeader" width="25%">Counters</td>
		 									<td colspan="4" align="center" style="padding: 4px;">EliteAAA Instances</td>
		 								</tr>
		 								<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;background-color: #E6E6E6;font-weight: bold; color: #015198" >
		 									<td class="dashboardTableHeader" width="15%">AAA-Banglore</td>
		 									<td class="dashboardTableHeader" width="15%">AAA-Delhi</td>
		 									<td class="dashboardTableHeader" width="15%">AAA-Chennai</td>
		 									<td class="dashboardTableHeader" width="15%">AAA-Hyderabad</td>
		 								</tr>
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader">Accounting Request</td>
		 									<td align="right" class="dashboardTableData">233568</td>
		 									<td align="right" class="dashboardTableData">256568</td>
		 									<td align="right" class="dashboardTableData">245668</td>
		 									<td align="right" class="dashboardTableData">234568</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader">Accounting Response</td>
		 									<td align="right" class="dashboardTableData">233568</td>
		 									<td align="right" class="dashboardTableData">256568</td>
		 									<td align="right" class="dashboardTableData">245668</td>
		 									<td align="right" class="dashboardTableData">234568</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader">Request Drop</td>
		 									<td align="right" class="dashboardTableData">568</td>
		 									<td align="right" class="dashboardTableData">568</td>
		 									<td align="right" class="dashboardTableData">668</td>
		 									<td align="right" class="dashboardTableData">568</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader">Duplicate Request</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">568</td>
		 									<td align="right" class="dashboardTableData">5668</td>
		 									<td align="right" class="dashboardTableData">4568</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader">Malformed Packet</td>
		 									<td align="right" class="dashboardTableData">68</td>
		 									<td align="right" class="dashboardTableData">68</td>
		 									<td align="right" class="dashboardTableData">68</td>
		 									<td align="right" class="dashboardTableData">68</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader">Invalid Packet</td>
		 									<td align="right" class="dashboardTableData">23</td>
		 									<td align="right" class="dashboardTableData">25</td>
		 									<td align="right" class="dashboardTableData">24</td>
		 									<td align="right" class="dashboardTableData">23</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader">Unknown Types</td>
		 									<td align="right" class="dashboardTableData">233</td>
		 									<td align="right" class="dashboardTableData">256</td>
		 									<td align="right" class="dashboardTableData">245</td>
		 									<td align="right" class="dashboardTableData">234</td>
		 								</tr> 							
		 					</table>
 					</body>
</html>