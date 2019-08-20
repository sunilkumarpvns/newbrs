<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	  <link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/custom_scrollbar/jquery.custom-scrollbar.css" />
    <script type="text/javascript" src="<%=basePath%>/jquery/custom_scrollbar/jquery.custom-scrollbar.js"></script>
	 
	<script type="text/javascript">
 		 $('#aaaclientDemo').customScrollbar();
 	</script>
 	<style type="text/css">
 	#container
{
	width: 100%;
	padding-left:80px;
	background: #eeeef4;
	
}
	.scroll-pane
			{
				width: 100%;
				height: 100%;
				overflow: auto;
			}
			.horizontal-only
			{
				height: 100%;
				max-height: 100%;
			}
			
		#aaaclientDemo {
            width: auto;
            height: 400px;
        }

        #aaaclientDemo .overview {
            width: auto;
        }
			
 	</style>
</head>
<body >
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaadashboard/EditAAAClientDetailsWidget.jsp" id="editJsp">
<div id="aaaclientDemo" class="default-skin demo" style="min-width: 98%;">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
		 				<tr>
		 					<td valign="top">
									<div id="clientSummay"  style="border-right-style: solid;border-right-width: 1px;border-right-color: #C0C0C0;">
									<table style="border-left-style: none;"  border="0" cellspacing="0" cellpadding="0" >
		 								<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;font-weight: bold;background-color: #E6E6E6;">
		 									<td rowspan="2" class="dashboardTableHeader"  style="border-left-style: none;" >Counters</td>
		 									<td colspan="10" valign="bottom" align="center" style="padding: 4px;" >EliteAAA Clients</td>
		 								</tr>
		 								<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;background-color: #E6E6E6; color: #015198;border-left-style: none;" >
		 									<td class="dashboardTableHeader">10.106.1.11</td>
		 									<td class="dashboardTableHeader">10.106.1.12</td>
		 									<td class="dashboardTableHeader">10.106.1.13</td>
		 									<td class="dashboardTableHeader">10.106.1.14</td>
		 									<td class="dashboardTableHeader">10.106.1.15</td>
		 									<td class="dashboardTableHeader">10.106.1.16</td>
		 									<td class="dashboardTableHeader">10.106.1.17</td>
		 									<td class="dashboardTableHeader">10.106.1.18</td>
		 									<td class="dashboardTableHeader">10.106.1.19</td>
		 									<td class="dashboardTableHeader">10.106.1.20</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;" >Access Request</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">233568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">234568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">233568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">256568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">245668</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">234568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">233568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">256568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">245668</td>
		 									<td align="right" class="dashboardTableData" style="background-color: white;">234568</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;">Access Accept</td>
		 									<td align="right" class="dashboardTableData">233568</td>
		 									<td align="right" class="dashboardTableData">234568</td>
		 									<td align="right" class="dashboardTableData">233568</td>
		 									<td align="right" class="dashboardTableData">256568</td>
		 									<td align="right" class="dashboardTableData">245668</td>
		 									<td align="right" class="dashboardTableData">234568</td>
		 									<td align="right" class="dashboardTableData">233568</td>
		 									<td align="right" class="dashboardTableData">256568</td>
		 									<td align="right" class="dashboardTableData">245668</td>
		 									<td align="right" class="dashboardTableData">234568</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;" >Access Reject</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;" >Access Challenge</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 									<td align="right" class="dashboardTableData">22323</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;" >Request Drop</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData" style="background-color: yellow;">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 									<td align="right" class="dashboardTableData">1000</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;">Duplicate Request</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData" style="background-color: red;">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 									<td align="right" class="dashboardTableData">3568</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;">Malformed Packet</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 									<td align="right" class="dashboardTableData" style="background-color: yellow;"> 565</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 									<td align="right" class="dashboardTableData" >565</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 									<td align="right" class="dashboardTableData">565</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;">Invalid Packet</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 									<td align="right" class="dashboardTableData" > 34</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 									<td align="right" class="dashboardTableData" >34</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 									<td align="right" class="dashboardTableData">34</td>
		 								</tr>
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;">Unknown Types</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 									<td align="right" class="dashboardTableData" > 232</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 									<td align="right" class="dashboardTableData" >232</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 									<td align="right" class="dashboardTableData">232</td>
		 								</tr> 	
		 								<tr>
		 									<td class="dashboardTableDataHeader" style="border-left-style: none;">Bad Authenticator</td>
		 									<td align="right" class="dashboardTableData">23232</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 								</tr> 
		 														
		 							</table>
		 						</div>
		 					</td>
		 				</tr>
 					</table>
 		</div>
 	</body>
</html>