<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%
		String pageId = request.getParameter("pageId");
	%>
	<script type="text/javascript">
	
	$(document).ready(function(){
		
	});
	
	function hideProgressBar(){
		$('#responseTimeProgressDivId').hide();
	}
	<%--START : Reply Msg Statt=estics Info  --%>
	<%-- $(function () {
	    var $highResponseContent = $('.highResponseTimeChart'+'<%=pageId%>');
		chartHighResponseConfig = {
	        chart: {
	            zoomType: 'xy'
	        },

	        title: {
	            text: null
	        }, 
	        xAxis: {
	            type: 'datetime',
	            minRange: 1000
	        },
	        
	        
	        tooltip: {
	            formatter: function() {
	                return (new Date(this.x)).toLocaleString() + "<br/>Avg Response Time :<b>"  +(this.y + 3000) + "</b>";
	            }
	        },
	        
	        
	        yAxis : {
	            title: {
	                text: 'Response Time'
	            },            
	            labels: {
	                formatter: function() {
	                    return this.value + 3000;
	                }
	            }            
	        },
	        
	        plotOptions: {
	            spline : {
	                marker: {
	                    symbol : 'circle',
	                    enabled: false,
	                    states: {
	                        hover: {
	                            enabled: true
	                        }
	                    }
	                }
	            },
	        },
	        
	        series: [{
	            name: 'Avg Response Time',
	            type: 'spline',
	            data: [-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4,-6, -5, -3, 1, 2, 5, 8, 8, 5, 1, -1, -4],
	            pointStart: Date.UTC(2010, 0),
	            pointInterval: 100000,
	            color: '#FF0000',
	            negativeColor: '#0088FF'
	        }]
	    };
	    
	    $highResponseContent.each(function(i, e){
	    	chartHighResponseConfig.chart.renderTo = e;
			    new Highcharts.Chart(chartHighResponseConfig);
		});
	});
 --%>
	<%-- END : Reply Msg Statt=estics Info --%>

	
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditHighResponseTimeWidget.jsp" id="editJsp">
<%--ProgressBar Div : Start  --%>
 
 <div id="responseTimeProgressDivId" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>
<%-- <div class="highResponseTimeChart<%=pageId%>" style="height: 310px;padding-right: 3px;min-width: 100%;overflow: auto;" ></div> --%>
</body>
</html>