<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<html>
<head>
  
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	<%
		String pageId = request.getParameter("pageId");
	%>
	<script type="text/javascript">
	<%--START : Primary Server TPS Info. --%>
	 
	$(function () {
		 
		var $dscContent = $('.dsctpsmemoryusage'+'<%=pageId%>');
		 chartPrimaryConfig = {
            chart: {
                renderTo: null,
            	zoomType: 'xy'
            },
            title: {
                text: null,
                style: {
        	        fontSize: '14px'
          		}
            },
            subtitle: {
                text: ''
            },
            xAxis: [{
                categories: ['00:00', '00:10', '00:20', '00:30', '00:40', '00:50',
                             '01:00','01:10','01:20','01:30']
            }],
            yAxis: [{ // Primary yAxis
                labels: {
                    format: '{value}',
                    style: {
                        color: '#89A54E'
                    }
                },
                title: {
                    text: 'TPS',
                    style: {
                        color: '#89A54E'
                    }
                }
            }, { // Secondary yAxis
                title: {
                    text: 'Memory Usage',
                    style: {
                        color: '#4572A7'
                    }
                },
                labels: {
                    format: '{value} mb',
                    style: {
                        color: '#4572A7'
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
                x: 70,
                verticalAlign: 'top',
                y: 25,
                floating: true,
                backgroundColor: '#FFFFFF'
            },
            series: [{
                name: 'Memory Usage',
                color: '#4572A7',
                type: 'column',
                yAxis: 1,
                data: [450, 527, 680, 800, 1049, 800, 600,450,350,300],
                tooltip: {
                    valueSuffix: ' mb'
                }
    
            }, {
                name: 'TPS',
                color: '#89A54E',
                type: 'spline',
                data: [120, 130, 140, 145, 150, 155, 135,134,133,132],
                tooltip: {
                    valueSuffix: ''
                }
            }]
        };
		 
		 $dscContent.each(function(i, e){
			 chartPrimaryConfig.chart.renderTo = e;
			    new Highcharts.Chart(chartPrimaryConfig);
		});
    });	
	
	<%-- END : Primary Server TPS Info. --%>

	
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/dscdashboard/EditDSCPrimaryServerWidget.jsp" id="editJsp">
 <div class="dsctpsmemoryusage<%=pageId%>"  style="height:200px;padding-right: 3px;padding-bottom: 3px;min-width: 100%;overflow: auto;"/> 
</body>
</html>