<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/dashboard/exporting.js"></script>
    
	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%
		String pageId = request.getParameter("replyPageId");
	%>
	<script type="text/javascript">
	
	<%--START : Reply Msg Statt=estics Info  --%>
	$(function () {
	    
        var colors = Highcharts.getOptions().colors,
        categories= ['Authentication Success', 
                             'User not found', 
                             'Authentication failed due to Invalid password', 
                             'EAP-Failure',
                             'Authentication Failed',
                             'Max Login Limit Reached',
                             'Concurrency Failed',
                             'No Policy Satisfied',
                             'Packet validation failed',
                            'Account is blacklisted',
                            'Authorization Failed',
                            'Account is not active',
                            'Account Expired',
                            'Account Credit Limit Exceeded',
                            'MAC Validation Failed',
                             'Invalid Calling Station Id',
                             'Calling Station Id not found',
                             'Invalid Called Station Id',
                             'Called Station Id not found',
                            'Invalid NAS Port Type',
                            'NAS Port Type not found',
                            'Unsupported Authentication method'],
            name = 'Reply-Message',
            data = [{
                    y: 23234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [20000,3000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 43234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [40000,3000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 13234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [10000,3000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 28234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [20000,8000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 3234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [3000,200,30,4],
                        color: colors[0]
                    }
                }, {
                    y: 23234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [20000,3000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 23234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [20000,3000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 3234,
                    color: '#BF0B23',
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [3000,200,30,4],
                        color: '#BF0B23'
                    }
                }, {
                    y: 2234,
                    color: '#BF0B23',
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [2000,200,30,4],
                        color: '#BF0B23'
                    }
                }, {
                    y: 43234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [40000,3000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 3234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [3000,200,30,4],
                        color: colors[0]
                    }
                }, {
                    y: 8234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [8000,200,30,4],
                        color: colors[0]
                    }
                }, {
                    y: 33234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [30000,3000,200,34],
                        color: colors[0]
                    }
                }, {
                    y: 234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [200,30,4,0],
                        color: colors[0]
                    }
                }, {
                    y: 6234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [6000,200,30,4],
                        color: colors[0]
                    }
                }, {
                    y: 3234,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [3000,200,30,4],
                        color: colors[0]
                    }
                }, {
                    y: 0,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [0,0,0,0],
                        color: colors[0]
                    }
                }, {
                    y: 0,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [0,0,0,0],
                        color: colors[0]
                    }
                }, {
                    y: 0,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [0,0,0,0],
                        color: colors[0]
                    }
                }, {
                    y: 0,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [0,0,0,0],
                        color: colors[0]
                    }
                }, {
                    y: 0,
                    color: colors[0],
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [0,0,0,0],
                        color: colors[0]
                    }
                }, {
                    y: 3234,
                    color: '#BF0B23',
                    drilldown: {
                        name: 'AAA Instances',
                        categories: ['AAA-Banglore-Primary', 'AAA-Chennai-Primary', 'AAA-Hydrabad-Primary', 'AAA-Delhi-Primary'],
                        data: [3000,200,30,4],
                        color: '#BF0B23'
                    }
                }];
    
        function setChart(name, categories, data, color) {
                                                chart.xAxis[0].setCategories(categories, false);
                                                chart.series[0].remove(false);
                                                chart.addSeries({
                                                                name: name,
                                                                data: data,
                                                                color: color || 'white'
                                                }, false);
                                                chart.redraw();
        }
    
        var chart = $('.replyMsg'+<%=pageId%>).highcharts({
            chart: {
                type: 'bar',
                marginLeft: 275
            },
            title: {
                text: 'Reply Message Statistics'
            },
            subtitle: {
                text: 'Click the bar to view Reply Message per Instance.'
            },
            xAxis: {
                categories: categories
            },
            yAxis: {
                title: {
                    text: '# Reply Message Sent',
                    align: 'high'                    
                }
            },
            plotOptions: {
                bar: {
                    cursor: 'pointer',
                    point: {
                        events: {
                            click: function() {
                                var drilldown = this.drilldown;
                                if (drilldown) { // drill down
                                    setChart(drilldown.name, drilldown.categories, drilldown.data, drilldown.color);
                                } else { // restore
                                    setChart(name, categories, data);
                                }
                            }
                        }
                    },
                    dataLabels: {
                        enabled: true,
                        color: colors[0],
                        style: {
                            fontWeight: 'bold'
                        },
                        formatter: function() {
                            return this.y;
                        }
                    }
                }
            },
            tooltip: {
                followPointer : true,                
                formatter: function() {
                    var point = this.point,
                        s = this.x +':<b>'+ this.y +' Reply-Message sent</b><br/>';
                    if (point.drilldown) {
                        s += 'Click to view '+ point.category +' Details per Instance';
                    } else {
                        s += 'Click to return to Reply Message Statistics';
                    }
                    return s;
                }
            },
            series: [{
                name: name,
                data: data,
                color: 'white'
            }],
            exporting: {
                enabled: false
            }
        })
        .highcharts(); // return chart
    });
    


	
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaadashboard/EditReplyMsgWidget.jsp" id="editJsp">
<div class="replyMsg<%=pageId%>" style="height:410px;padding-right: 3px;padding-bottom: 2px;overflow: auto;min-width: 50%;"></div>
</body>
</html>