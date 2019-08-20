<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
		<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/exporting.js"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%
		String pageId = request.getParameter("driverDeadId");
	%>
	<script type="text/javascript">
	$(function () {
		var $driverDeadContent = $('.driverChartDemo'+'<%=pageId%>');
		chartdriverDeadConfig = {
        chart: {
            type: 'spline',
            renderTo:null,
            zoomType: 'xy'
        },
        title: {
            text: null
        },
        xAxis: {
            type: 'datetime',                
            title: {
                text: null
            }
        },
        yAxis: {
            title: {
                text: 'Number of Dead Count'
            },
            labels: {
                formatter: function() {
                    return this.value 
                }
            }
        },
        tooltip: {                
            shared: true
        },
        plotOptions: {
            spline: {
                marker: {
                    radius: 2,
                    lineColor: '#666666',
                    lineWidth: 1
                }
            },
            series: {
                pointStart: Date.UTC(2013, 10, 11),
                pointInterval: 1000 // one day
            }
        },
        series: [{
            name: 'DB-Auth-Driver',                                
            marker: {
                symbol: 'circle'
            },
            data: [0, 0, 0, 0, 0, 0, 0, {
                y: 1,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            }, 1, 1, 1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]

        }, {
            name: 'DB-Acct-Driver',
            marker: {
                symbol: 'circle'
            },
            data: [{
                y: 1,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            }, 1, 1 ,1, 1, 1, 1, 1, 1, 1, 1, 1,{
                y: 2,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            },2,2,2,2,2,2,2,2,{
                y: 3,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            },3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3]
        }, {
            name: 'MAP-GW-Auth-Driver',
            marker: {
                symbol: 'circle'
            },
            data: [ 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0,{
                
                y: 1,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            }, 1,1,1,1,1,{
                y: 2,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            },2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2]
        }, {
            name: 'LDAP-Auth-Driver',
            marker: {
                symbol: 'circle'
            },
            data: [ 0, 0, 0, 0, 0, 0, 0, 0,{
                
                y: 1,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            }, 1,1, 1, 1, 1, 1, 1, 1, 1,{
                
                y: 2,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            }, 2,2, 2, 2,{
                
                y: 3,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            }, 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,{
                
                y: 4,
                marker: {
                    symbol: 'url(http://icons.iconarchive.com/icons/gakuseisean/ivista/24/Delete-Database-icon.png)'
                }
            },4,4,4,4,4,4,4,4,4,4]
        }]
    };
		
		 $driverDeadContent.each(function(i, e){
			 chartdriverDeadConfig.chart.renderTo = e;
			    new Highcharts.Chart(chartdriverDeadConfig);
		});
});
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaadashboard/EditDriverDeadAliveStatsWidget.jsp" id="editJsp">
	<div class="driverChartDemo<%=pageId%>" style="height: 310px;padding-right: 3px;padding-bottom: 1px;min-width: 100%;overflow: auto;"></div>
</body>
</html>