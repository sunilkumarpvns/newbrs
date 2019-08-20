<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%
		String pageId = request.getParameter("mmsPageId");
	%>
	<script type="text/javascript">
	
$(document).ready(function(){
		
	});
	
	function hideProgressBar(){
		$('#totalReqProgressDivId').hide();
	}
		<%--START : MMS Gateway Summary--%>
		<%-- $(function () {
			var $mmsGWContent = $('.esiChart'+'<%=pageId%>');
    		chartMmsConfig = {
		        chart: {
		        	renderTo:null,
		            zoomType: 'xy'
		        },
		        title: {
		            text: null
		        },
		        xAxis: [{
		            categories: ['00:00', '00:10','00:20','00:30','00:40','00:50','01:00','01:10','01:20','01:30' ]
		        }],
		        yAxis: [{ // Primary yAxis
		            labels: {
		                format: '{value}',
		                style: {
		                    color: '#89A54E'
		                }
		            },
		            title: {
		                text: 'Total Timeout',
		                style: {
		                    color: '#89A54E'
		                }
		            }
		        }, { // Secondary yAxis
		            title: {
		                text: 'Total Response',
		                style: {
		                    color: '#4572A7'
		                }
		            },
		            labels: {
		                format: '{value}',
		                style: {
		                    color: '#4572A7'
		                }
		            },
		            opposite: true
		        },{ // Tertiary yAxis
		            gridLineWidth: 0,
		            title: {
		                text: 'Total Error',
		                style: {
		                    color: '#AA4643'
		                }
		            },
		            labels: {
		                formatter: function() {
		                    return this.value;
		                },
		                style: {
		                    color: '#AA4643'
		                }
		            },
		            opposite: true
		        }],
		        tooltip: {
		            shared: true
		        },
		        legend: {
		            layout: 'vertical',
		            align: 'left',
		            x: 120,
		            verticalAlign: 'top',
		            y: 100,
		            floating: true,
		            backgroundColor: '#FFFFFF'
		        },
		        series: [{
		            name: 'Total Response',
		            color: '#4572A7',
		            type: 'column',
		            yAxis: 1,
		            data: [40000, 41000, 42200, 43300, 44400, 45500, 46600, 47700, 48800, 49900]
		        }, {
		            name: 'Total Timeout',
		            color: '#89A54E',
		            type: 'spline',
		            data: [3000,3000,3200,3200,3400,3400,3600,3600,3800,3800]
		        }, {
		            name: 'Total Error',
		            type: 'spline',
		            color: '#AA4643',
		            yAxis: 2,
		            data: [100,110,120,200,320,340,450,460,570],
		            marker: {
		                enabled: false
		            },
		            dashStyle: 'shortdot',
		            tooltip: {
		                valueSuffix: ' mb'
		            }

		        }]
		    };
    		 $mmsGWContent.each(function(i, e){
    			 chartMmsConfig.chart.renderTo = e;
    			    new Highcharts.Chart(chartMmsConfig);
    		});
		}); --%>
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditMMSGatewayStatsWidget.jsp" id="editJsp">
<%--ProgressBar Div : Start  --%>
 
 <div id="totalReqProgressDivId" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>
<%-- <div class="esiChart<%=pageId%>" style="height: 310px;padding-right: 3px;min-width: 100%;overflow: auto;"></div> --%>
</body>
</html>