<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath();
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/custom_scrollbar/jquery.custom-scrollbar.css" />
    <script type="text/javascript" src="<%=basePath%>/jquery/custom_scrollbar/jquery.custom-scrollbar.js"></script>

 	<style type="text/css">
 	.vertical-horizontal-scrollbar-demo1 {
            width: 100%;
            height: 400px;
            overflow: auto;
        }

        .vertical-horizontal-scrollbar-demo1 .overview {
            width: auto;
        }
 	</style>
 	<script type="text/javascript">
 		 /*  $('.vertical-horizontal-scrollbar-demo1').customScrollbar(); */
 	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/dscdashboard/EditPeerStatsTable.jsp" id="editJsp">
<div class="default-skin demo vertical-horizontal-scrollbar-demo1">
<table  border="0" cellspacing="0" cellpadding="0" width="100%">
		 					<tr>
		 					<td valign="top">
		 					<div  id="containerPeer">
		 					<div id="statasticSummay2" style="border-right-style: solid;border-right-width: 1px;border-right-color: #C0C0C0;">
								<table cellpadding="0" cellspacing="0" border="0"  id="dashboardTableData" width="100%">
		 								<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;background-color: #E6E6E6;font-weight: bold;">
		 									<td rowspan="3" class="dashboardTableHeader" width="25%" valign="bottom" align="center">Peers</td>
		 									<td rowspan="3" class="dashboardTableHeader" width="5%" valign="bottom" align="center">CMD</td>
		 									<td colspan="13" align="center" style="padding: 4px;">Counter Details</td>
		 								</tr>
		 								<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;background-color: #E6E6E6;font-weight: bold; color: #015198" >
		 									<td class="dashboardTableHeader" width="">Request</td>
		 									<td class="dashboardTableHeader" width="">Answer</td>
		 									<td class="dashboardTableHeader" width="">Request</td>
		 									<td class="dashboardTableHeader" width="">Answer</td>
		 									<td class="dashboardTableHeader" width="">Request</td>
		 									<td class="dashboardTableHeader" width="">Request</td>
		 									<td class="dashboardTableHeader" width="">Request</td>
		 									<td class="dashboardTableHeader" width="">Answer</td>
		 									<td class="dashboardTableHeader" width="">Answer</td>
		 									<td class="dashboardTableHeader" width="">Request</td>
		 									<td class="dashboardTableHeader" width="">Answer</td>
		 									<td class="dashboardTableHeader" width="">Request</td>
		 									<td class="dashboardTableHeader" width="">Request</td>
		 								</tr>
		 								<tr style="border-style: solid;border-color: #C0C0C0;border-width: 1px;background-color: #E6E6E6;font-weight: bold; color: #015198" >
		 									<td class="dashboardTableHeader" align="center">Rx</td>
		 									<td class="dashboardTableHeader" align="center">Tx</td>
		 									<td class="dashboardTableHeader" align="center">Tx</td>
		 									<td class="dashboardTableHeader" align="center">Rx</td>
		 									<td class="dashboardTableHeader" align="center">Rt</td>
		 									<td class="dashboardTableHeader" align="center">To</td>
		 									<td class="dashboardTableHeader" align="center">Dr</td>
		 									<td class="dashboardTableHeader" align="center">Dr</td>
		 									<td class="dashboardTableHeader" align="center">Un</td>
		 									<td class="dashboardTableHeader" align="center">Du</td>
		 									<td class="dashboardTableHeader" align="center">Du</td>
		 									<td class="dashboardTableHeader" align="center">Mal-Msg</td>
		 									<td class="dashboardTableHeader" align="center">Pn</td>
		 								</tr>
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="2" valign="top">dsc1.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" id="dsc1" class="dashboardTableData dscData" title="dsc1.eliteaaa.com &#013; CE R-Rx : 100">100</td>
		 									<td align="right" class="dashboardTableData dscData" title="dsc1.eliteaaa.com &#013; CE R-Rx : 100">100</td>
		 									<td align="right" class="dashboardTableData dscData" title="dsc1.eliteaaa.com &#013; CE R-Rx : 0">0</td>
		 									<td align="right" class="dashboardTableData dscData" title="dsc1.eliteaaa.com &#013; CE R-Rx : 0">0</td>
		 									<td align="right" class="dashboardTableData dscData">0</td>
		 									<td align="right" class="dashboardTableData dscData">0</td>
		 									<td align="right" class="dashboardTableData dscData">0</td>
		 									<td align="right" class="dashboardTableData dscData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 								</tr>
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData" title="dsc1.eliteaaa.com &#013; CC R-Rx : 232389">232389</td>
		 									<td align="right" class="dashboardTableData" title="dsc1.eliteaaa.com &#013; CC R-Rx : 232389">232389</td>
		 									<td align="right" class="dashboardTableData" title="dsc1.eliteaaa.com &#013; CC R-Rx : 232389">232389</td>
		 									<td align="right" class="dashboardTableData" title="dsc1.eliteaaa.com &#013; CC R-Rx : 232389">232389</td>
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
		 									<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" valign="top">dsc2.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="3" valign="top">dsc3.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									
		 								</tr>		
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">DP</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 								
		 								
		 								<%--- --%>
		 								
		 								
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="2" valign="top">dsc4.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
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
		 									<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" valign="top" style="color: #015198">dsc5.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="3" valign="top">dsc6.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									
		 								</tr>		
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">DP</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 								
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="2" valign="top">dsc7.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
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
		 									<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" valign="top">dsc8.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="3" valign="top">dsc9.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									
		 								</tr>		
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">DP</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 									
		 									
		 								<%--- --%>
		 								
		 								
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="2" valign="top">dsc10.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
		 									<td align="right" class="dashboardTableData">232389</td>
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
		 									<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" valign="top">dsc11.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 								<tr class="dashboardTableData">
		 									<td class="dashboardTableDataHeader" rowspan="3" valign="top">dsc12.eliteaaa.com</td>
		 									<td align="center" class="dashboardTableData" style="color: #015198">CE</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">100</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 											
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">CC</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">232323</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									
		 								</tr>		
		 								<tr class="dashboardTableData">
		 									<td align="center" class="dashboardTableData" style="color: #015198">DP</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
		 									<td align="right" class="dashboardTableData">0</td>
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
		 							</div>
		 							</td>
		 							</tr>
		 							</table>
		 							</div>
</body>					
<style type="text/css">
 #containerPeer
{
	width: 100%;
	
}
.scroll-pane-peer
			{
				width: 80%;
				height: 100%;
				overflow: inherit;
			}
.horizontal-only-peer
			{
				height: inherit;
				max-height: inherit;
			}
 	</style>	
</html>