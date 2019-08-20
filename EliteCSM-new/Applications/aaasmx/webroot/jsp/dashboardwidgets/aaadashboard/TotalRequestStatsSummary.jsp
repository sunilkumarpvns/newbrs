<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/exporting.js"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%
		String pageId = request.getParameter("totalPageId");
	%>
	<script type="text/javascript">
		<%--START : Total Request Statistics Summary--%>
		$(function () {
            	var $statasticSummayContent = $('.statasticSummay'+'<%=pageId%>');
        		chartstatasticSummayConfig = {
                chart: {
                	renderTo:null,
                    type: 'column',
                    marginBottom : 80,
                    zoomType: 'xy'
                },
                title : {
                    text : null
                },
                xAxis: {
                    categories: ['MMS-Gateway1', 'MMS-Gateway2', 'MMS-Gateway3', 'WAP-Gateway1','WAP-Gateway2', 'WAP-Gateway3','OCS-1', 'OCS-2', 'OCS-3', 'Cisco-HHS1','Cisco-HHS2', 'Cisco-HHS3'],
                    labels: {
                        rotation: -90,
                        align: 'left',
                        x : 4,
                        y : 5,
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.textColor) ||  'white',                                   
                            fontWeight : '600',
                            fontSize: '12px'
                        }
                    }                        
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Total Requests'
                    },
                    stackLabels: {
                        enabled: false,
                        style: {
                            fontWeight: 'bold',
                            color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                            
                        }
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
                        stacking: 'normal',
                        dataLabels: {
                            enabled: false,
                            color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                        }
                    }
                },
                series: [ {
                    name: 'Accept-Challenge',
                    color : Highcharts.getOptions().colors[0],
                    data: [20000,20000,20000,20000,20000,20000,20000,20000,20000,20000,20000,20000]
                },{
                    name: 'Accept-Reject',
                    color : Highcharts.getOptions().colors[5],
                    data: [3454,3574,3654,3754,3854,3954,4454,4154,4454,4554,4654,4754]
                },{
                    name: 'Access-Accept',
                    color : Highcharts.getOptions().colors[2],
                    data: [23454,23574,23654,23754,23854,23954,24454,24154,24454,24554,24654,24754]
                },{
                    name: 'Request-Drops',
                    color : Highcharts.getOptions().colors[3],
                    data: [100,200,300,400,500,600,700,800,900,1000,1100,1200]
                }]
            };
        		
        		 $statasticSummayContent.each(function(i, e){
        			 chartstatasticSummayConfig.chart.renderTo = e;
        			    new Highcharts.Chart(chartstatasticSummayConfig);
        		});
        });

	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaadashboard/EditTotalRequestStatsWidget.jsp" id="editJsp">
<div class="statasticSummay<%=pageId%>" style="padding-right: 4px;height: 410px;min-width: 100%;overflow: auto;"></div>
</body>
</html>