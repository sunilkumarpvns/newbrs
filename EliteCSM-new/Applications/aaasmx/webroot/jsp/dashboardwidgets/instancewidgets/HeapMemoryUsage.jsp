<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%
		String pageId = request.getParameter("pageId");
	%>
		<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<script type="text/javascript">
	<%--START : HEAP Memory Usage--%>
	
	
	<%-- $(function () {
		 var $instanceContainer = $('.heapMemoryUsage'+'<%=pageId%>');
		 chartConfig = {
             chart: {
            	renderTo: null,
            	type: 'areaspline',
                zoomType: 'xy'
            },
            title: {
                text: null
            },
            xAxis: {
                type: 'datetime',
                maxZoom: 1000, // fourteen days
                title: {
                    enabled: false
                }
            },
            yAxis: {
                title: {
                    text: 'Heap Memory'
                },
                labels: {
                    formatter: function() {
                        return this.value + " mb";
                    }
                }
            },
            tooltip: {
                shared: true,
                valueSuffix: ' mb'
            },
            plotOptions: {
                areaspline: {
                    stacking: 'normal',
                    lineColor: '#666666',
                    lineWidth: 1,
                    marker: {
                        symbol : 'circle',
                        lineWidth: 1,
                        lineColor: '#666666',
                        enabled: false,
                        radius : 1,
                        states: {
                            hover: {
                                enabled: true
                            }
                        }
                    }
                },
                series : {
                    pointInterval: 1000,
                    pointStart: Date.UTC(2013, 21, 11)
                }
            },
            series: [{
                name: 'PS Eden Space',
                color : Highcharts.getOptions().colors[0],
                data: [502, 635, 809, 947, 1402, 1300, 1200,502, 635, 809, 947, 1402, 1300, 1200,502, 635, 809, 947, 1402, 1300, 1200,502, 635, 809, 947, 1402, 1300, 1200,502, 635, 809, 947, 1402, 1300, 1200,502, 635, 809, 947, 1402, 1300, 1200]
            }, {
                name: 'PS Survivor Space',
                color : Highcharts.getOptions().colors[1],
                data: [106, 107, 111, 133, 221, 767,106, 107, 111, 133, 221, 767,106, 107, 111, 133, 221, 767,106, 107, 111, 133, 221, 767,106, 107, 111, 133, 221, 767,106, 107, 111, 133, 221, 767,106, 107, 111, 133, 221, 767 ]
            }, {
                name: 'PS Old Gen',
                color : Highcharts.getOptions().colors[2],
                data: [163, 203, 276, 408, 547, 729, 628,163, 203, 276, 408, 547, 729, 628,163, 203, 276, 408, 547, 729, 628,163, 203, 276, 408, 547, 729, 628,163, 203, 276, 408, 547, 729, 628,163, 203, 276, 408, 547, 729, 628]
            }, {
                name: 'PS Perm Gen',
                color : Highcharts.getOptions().colors[4],
                data: [18, 31, 54, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156, 156, 156,156]
            }]
        };
		 
		 $instanceContainer.each(function(i, e){
			    chartConfig.chart.renderTo = e;
			    new Highcharts.Chart(chartConfig);
		});
    });
    
 --%>
 $(document).ready(function(){
		
 });

 function hideProgressBar(){
 	$('#totalReqProgressDivId').hide();
 }
<%--END :HEAP memory Usage--%>
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/instancewidgets/EditHeapMemoryUsage.jsp" id="editJsp">
	<%--ProgressBar Div : Start  --%>
 
 <div id="totalReqProgressDivId" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>
 <%-- <table class="labeltext" cellpadding="0" cellspacing="0" border="0" width="100%">
			 							<tr> 
			 								<td><div class="heapMemoryUsage<%=pageId%>" style="height: 300px; min-width: 545px;overflow: auto;"></div></td>
			 							</tr>
			 							<tr>
			 								<td align="right">
			 									<table align="center" class="labeltext">
			 										<tr>
			 											<td style="color: #015198;font-weight: bold;">Time&nbsp;&nbsp;</td>
			 											<td style="color: #015198;font-weight: bold;">:</td>
			 											<td colspan="2">&nbsp;</td>
			 											<td style="color: #585858;">&nbsp;&nbsp;2013-11-22 12:16:54 </td>
			 										</tr>
			 										<tr>
			 											<td style="color: #015198;font-weight: bold;">Used&nbsp;&nbsp;</td>
			 											<td style="color: #015198;font-weight: bold;">:</td>
			 											<td colspan="2">&nbsp;</td>
			 											<td style="color: #585858;">&nbsp;&nbsp;2,751  mb </td>
			 										</tr>
			 										<tr>
			 											<td style="color: #015198;font-weight: bold;">Committed&nbsp;&nbsp;</td>
			 											<td style="color: #015198;font-weight: bold;">:</td>
			 											<td colspan="2">&nbsp;</td>
			 											<td style="color: #585858;">&nbsp;&nbsp;3,500  mb</td>
			 										</tr>
			 										<tr>
			 											<td style="color: #015198;font-weight: bold;">Max&nbsp;&nbsp;</td>
			 											<td style="color: #015198;font-weight: bold;">:</td>
			 											<td colspan="2">&nbsp;</td>
			 											<td style="color: #585858;">&nbsp;&nbsp;4,000  mb</td>
			 										</tr>
			 										<tr>
			 											<td style="color: #015198;font-weight: bold;back">GC time&nbsp;&nbsp;</td>
			 											<td style="color: #015198;font-weight: bold;">:</td>
			 											<td colspan="2">&nbsp;</td>
			 											<td style="color: #585858;">&nbsp;&nbsp;5.107 seconds on PS MarkSweep (1 collections) </td>
			 										</tr>
			 										<tr>
			 											<td style="color: #015198;font-weight: bold;"></td>
			 											<td style="color: #015198;font-weight: bold;"></td>
			 											<td colspan="2">&nbsp;</td>
			 											<td style="color: #585858;">&nbsp;&nbsp;0.674 seconds on PS Scavenge (18 collections) </td>
			 										</tr>
			 										
			 										
			 									</table>	
			 								</td>
			 		</tr>
		</table> --%>
</body>
</html>