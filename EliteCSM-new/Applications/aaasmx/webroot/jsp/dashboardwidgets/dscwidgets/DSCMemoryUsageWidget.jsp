<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<%
		String pageId = request.getParameter("pageId");
	%>
	<script type="text/javascript">
	<%-- START :Memory Usage Info --%>
	<%-- $(function() {
	
		$.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=usdeur.json&callback=?', function(data) {
			var $memoryUsageContent = $('.dscPrimaryServerMemoryUsage'+'<%=pageId%>');
			chartMemoryusageConfig = {
			         chart: {
			            renderTo: null
			         },
				rangeSelector : {
					selected : 1
				},

				title : {
					text : null,
					style: {
              	        fontSize: '14px'
                 	}
				},

				yAxis : {
					title : {
						text : 'Memory Usage'
					},
					plotLines : [{
						value : 0.6846,
						color : 'green',
						dashStyle : 'shortdash',
						width : 2,
						label : {
							text : 'Last quarter minimum'
						}
					}, {
						value :  0.8346,
						color : 'red',
						dashStyle : 'shortdash',
						width : 2,
						label : {
							text : 'Last quarter maximum'
						}
					}]
				},

				series : [{
					name : 'Heap Memory',
					data : data,
					tooltip : {
						valueDecimals : 4
					}
				}]
			};
			$memoryUsageContent.each(function(i, e){
				  chartMemoryusageConfig.chart.renderTo = e;
					    new Highcharts.StockChart(chartMemoryusageConfig);
				});
		});
		  
	}); --%>
	<%-- END : Primary Memory Usage Info --%>
$(document).ready(function(){
		
	});
	
	function hideProgressBar(){
		$('#totalReqProgressDivId').hide();
	}
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/dscwidgets/EditDSCMemoryUsageWidget.jsp" id="editJsp">
<%--ProgressBar Div : Start  --%>
 
 <div id="totalReqProgressDivId" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>
<%--  <div class="dscPrimaryServerMemoryUsage<%=pageId%>"  style="height: 300px; min-width: 100%;padding-bottom: 3px;overflow: auto;"></div> --%>
</body>
</html>