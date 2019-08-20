<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<%
		String pageId = request.getParameter("pageId");
	%>
	<script type="text/javascript">
	<%-- START :Memory Usage Info --%>
	<%--START : Total Request Statistics Summary--%>
	$(function () {
		
		var $realmContent = $('.realmWiseStatatics'+'<%=pageId%>');
		 chartRealmConfig = {
            chart: {
            	renderTo:null,
                type: 'column',
                zoomType: 'xy'
            },
    
            title: {
                text: null,
                style: {
        	        fontSize: '14px'
          		}
            },
        
            xAxis: {
                categories: ['realm1.aaa.com', 'realm2.aaa.com', 'realm3.aaa.com', 'realm4.aaa.com', 'realm5.aaa.com']
            },
    
            yAxis: {
                allowDecimals: false,
                min: 0,
                title: {
                    text: 'Number of Result-Code Received'
                }
            },
    
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.x +'</b><br/>'+
                        this.series.name +': '+ this.y +'<br/>'+
                        'Total: '+ this.point.stackTotal;
                }
            },
    
            plotOptions: {
                column: {
                    stacking: 'normal'
                }
            },
    
            series: [{
                name: '1XXX',
                data: [50000,50000, 50000, 50000, 50000],
                stack: '1XXX'
            }, {
                name: '2XXX',
                data: [232389, 232389, 232389, 232389, 232389],
                stack: '2XXX',
                color : '#8bbc21'
            }, {
                name: '3XXX',
                data: [100000, 100000, 100000, 100000, 100000],
                stack: '3XXX',
                color : '#BF0B23'
            },{
                name: '4XXX',
                data: [50000,50000, 50000, 50000, 50000],
                stack: '4XXX',
                color:'#1aadce'
            },{
                name: '5XXX',
                data: [25000,25000, 25000, 25000, 25000],
                stack: '5XXX',
                color : '#0d233a'
            }]
        };
		 
		 $realmContent.each(function(i, e){
			 chartRealmConfig.chart.renderTo = e;
			    new Highcharts.Chart(chartRealmConfig);
		});
    });

	<%--END : --%>

	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/dscdashboard/EditRealmWiseResultCodeStats.jsp" id="editJsp">
 <div class="realmWiseStatatics<%=pageId%>" style="height: 300px;min-width: 100%;overflow: auto;"></div>
</body>
</html>