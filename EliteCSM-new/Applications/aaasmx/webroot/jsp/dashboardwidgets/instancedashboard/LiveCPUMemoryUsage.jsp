<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highcharts-more.js"></script>
	<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/exporting.js"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
	<%
		String pageId = request.getParameter("pageId");
	%>
	<script type="text/javascript">
	<%--  $(function () {
		
	    $('.cpuUsageMeter1'+'<%=pageId%>').highcharts({
		
		    chart: {
		        type: 'gauge',
		        plotBackgroundColor: null,
		        plotBackgroundImage: null,
		        plotBorderWidth: 0,
		        plotShadow: false,
		        zoomType: 'xy'
		    },
		    
		    title: {
		        text: 'AAA-Banglore-Primary',
		        style: {
		        	fontSize: '12px'
		        }
		    },
		    
		    pane: {
		        startAngle: -150,
		        endAngle: 150,
		        background: [{
		            backgroundColor: {
		                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
		                stops: [
		                    [0, '#FFF'],
		                    [1, '#333']
		                ]
		            },
		            borderWidth: 0,
		            outerRadius: '109%'
		        }, {
		            backgroundColor: {
		                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
		                stops: [
		                    [0, '#333'],
		                    [1, '#FFF']
		                ]
		            },
		            borderWidth: 1,
		            outerRadius: '107%'
		        }, {
		            // default background
		        }, {
		            backgroundColor: '#DDD',
		            borderWidth: 0,
		            outerRadius: '105%',
		            innerRadius: '103%'
		        }]
		    },
		       
		    // the value axis
		    yAxis: {
		        min: 0,
		        max: 100,
		        
		        minorTickInterval: 'auto',
		        minorTickWidth: 1,
		        minorTickLength: 5,
		        minorTickPosition: 'inside',
		        minorTickColor: '#666',
		
		        tickPixelInterval: 30,
		        tickWidth: 2,
		        tickPosition: 'inside',
		        tickLength: 4,
		        tickColor: '#666',
		        labels: {
		            step: 3,
		            rotation: 'auto'
		        },
		        title: {
		        	style: {
				        	fontSize: '8px'
				     },
		            text: 'km/h',
	                x:4,
	                y:14
		        },
		        plotBands: [{
		            from: 0,
		            to: 60,
		            color: '#55BF3B' // green
		        }, {
		            from: 60,
		            to: 80,
		            color: '#DDDF0D' // yellow
		        }, {
		            from: 80,
		            to: 100,
		            color: '#DF5353' // red
		        }]        
		    },
		
		    series: [{
		        name: 'Speed',
		        data: [80],
		        tooltip: {
		            valueSuffix: ' km/h'
		        }
		    }]
		
		}, 
		// Add some life
		function (chart) {
			if (!chart.renderer.forExport) {
			    setInterval(function () {
			        var point = chart.series[0].points[0],
			            newVal,
			            inc = Math.round((Math.random() - 0.5) * 20);
			        
			        newVal = point.y + inc;
			        if (newVal < 0 || newVal > 200) {
			            newVal = point.y - inc;
			        }
			        
			        point.update(newVal);
			        
			    }, 3000);
			}
		});
	}); --%>
	 <%--
END : CPU Usage

<%-- STRAT : CPU Usage

$(function () {
	
    $('.cpuUsageMeter2'+'<%=pageId%>').highcharts({
	
	    chart: {
	        type: 'gauge',
	        plotBackgroundColor: null,
	        plotBackgroundImage: null,
	        plotBorderWidth: 0,
	        plotShadow: false,
	        zoomType: 'xy'
	    },
	    
	    title: {
	        text: 'AAA-Chennai-Primary',
	        style: {
		        fontSize: '12px'
		    }
	    },
	    
	    pane: {
	        startAngle: -150,
	        endAngle: 150,
	        background: [{
	            backgroundColor: {
	                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
	                stops: [
	                    [0, '#FFF'],
	                    [1, '#333']
	                ]
	            },
	            borderWidth: 0,
	            outerRadius: '109%'
	        }, {
	            backgroundColor: {
	                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
	                stops: [
	                    [0, '#333'],
	                    [1, '#FFF']
	                ]
	            },
	            borderWidth: 1,
	            outerRadius: '107%'
	        }, {
	            // default background
	        }, {
	            backgroundColor: '#DDD',
	            borderWidth: 0,
	            outerRadius: '105%',
	            innerRadius: '103%'
	        }]
	    },
	       
	    // the value axis
	    yAxis: {
	        min: 0,
	        max: 100,
	        
	        minorTickInterval: 'auto',
	        minorTickWidth: 1,
	        minorTickLength: 5,
	        minorTickPosition: 'inside',
	        minorTickColor: '#666',
	
	        tickPixelInterval: 30,
	        tickWidth: 2,
	        tickPosition: 'inside',
	        tickLength: 4,
	        tickColor: '#666',
	        labels: {
	            step: 3,
	            rotation: 'auto'
	        },
	        title: {
	        	style: {
		        	fontSize: '8px'
		     	},
	            text: 'km/h',
                x:4,
                y:14
	        },
	        plotBands: [{
	            from: 0,
	            to: 60,
	            color: '#55BF3B' // green
	        }, {
	            from: 60,
	            to: 80,
	            color: '#DDDF0D' // yellow
	        }, {
	            from: 80,
	            to: 100,
	            color: '#DF5353' // red
	        }]        
	    },
	
	    series: [{
	        name: 'Speed',
	        data: [80],
	        tooltip: {
	            valueSuffix: ' km/h'
	        }
	    }]
	
	}, 
	// Add some life
	function (chart) {
		if (!chart.renderer.forExport) {
		    setInterval(function () {
		        var point = chart.series[0].points[0],
		            newVal,
		            inc = Math.round((Math.random() - 0.5) * 20);
		        
		        newVal = point.y + inc;
		        if (newVal < 0 || newVal > 200) {
		            newVal = point.y - inc;
		        }
		        
		        point.update(newVal);
		        
		    }, 3000);
		}
	});
});


$(function () {

$('.cpuUsageMeter3'+'<%=pageId%>').highcharts({

    chart: {
        type: 'gauge',
        plotBackgroundColor: null,
        plotBackgroundImage: null,
        plotBorderWidth: 0,
        plotShadow: false,
        zoomType: 'xy'
    },
    
    title: {
        text: 'AAA-Delhi-Primary',
        style: {
	        fontSize: '12px'
	    }
    },
    
    pane: {
        startAngle: -150,
        endAngle: 150,
        background: [{
            backgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                stops: [
                    [0, '#FFF'],
                    [1, '#333']
                ]
            },
            borderWidth: 0,
            outerRadius: '109%'
        }, {
            backgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                stops: [
                    [0, '#333'],
                    [1, '#FFF']
                ]
            },
            borderWidth: 1,
            outerRadius: '107%'
        }, {
            // default background
        }, {
            backgroundColor: '#DDD',
            borderWidth: 0,
            outerRadius: '105%',
            innerRadius: '103%'
        }]
    },
       
    // the value axis
    yAxis: {
        min: 0,
        max: 100,
        
        minorTickInterval: 'auto',
        minorTickWidth: 1,
        minorTickLength: 5,
        minorTickPosition: 'inside',
        minorTickColor: '#666',

        tickPixelInterval: 30,
        tickWidth: 2,
        tickPosition: 'inside',
        tickLength: 4,
        tickColor: '#666',
        labels: {
            step: 3,
            rotation: 'auto'
        },
        title: {
        	style: {
	        	fontSize: '8px'
	     	},
            text: 'km/h',
            x:4,
            y:14
        },
        plotBands: [{
            from: 0,
            to: 60,
            color: '#55BF3B' // green
        }, {
            from: 60,
            to: 80,
            color: '#DDDF0D' // yellow
        }, {
            from: 80,
            to: 100,
            color: '#DF5353' // red
        }]        
    },

    series: [{
        name: 'Speed',
        data: [80],
        tooltip: {
            valueSuffix: ' km/h'
        }
    }]

}, 
// Add some life
function (chart) {
	if (!chart.renderer.forExport) {
	    setInterval(function () {
	        var point = chart.series[0].points[0],
	            newVal,
	            inc = Math.round((Math.random() - 0.5) * 20);
	        
	        newVal = point.y + inc;
	        if (newVal < 0 || newVal > 200) {
	            newVal = point.y - inc;
	        }
	        
	        point.update(newVal);
	        
	    }, 3000);
	}
});
});


$(function () {

$('.cpuUsageMeter4'+'<%=pageId%>').highcharts({

chart: {
    type: 'gauge',
    plotBackgroundColor: null,
    plotBackgroundImage: null,
    plotBorderWidth: 0,
    plotShadow: false,
    zoomType: 'xy'
},

title: {
    text: 'AAA-Hyd-Primary',
    style: {
        fontSize: '12px'
    }
},

pane: {
    startAngle: -150,
    endAngle: 150,
    background: [{
        backgroundColor: {
            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
            stops: [
                [0, '#FFF'],
                [1, '#333']
            ]
        },
        borderWidth: 0,
        outerRadius: '109%'
    }, {
        backgroundColor: {
            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
            stops: [
                [0, '#333'],
                [1, '#FFF']
            ]
        },
        borderWidth: 1,
        outerRadius: '107%'
    }, {
        // default background
    }, {
        backgroundColor: '#DDD',
        borderWidth: 0,
        outerRadius: '105%',
        innerRadius: '103%'
    }]
},
   
// the value axis
yAxis: {
    min: 0,
    max: 100,
    
    minorTickInterval: 'auto',
    minorTickWidth: 1,
    minorTickLength: 5,
    minorTickPosition: 'inside',
    minorTickColor: '#666',

    tickPixelInterval: 30,
    tickWidth: 2,
    tickPosition: 'inside',
    tickLength: 4,
    tickColor: '#666',
    labels: {
        step: 3,
        rotation: 'auto'
    },
    title: {
    	style: {
        	fontSize: '8px'
     	},
        text: 'km/h',
        x:4,
        y:14
    },
    plotBands: [{
        from: 0,
        to: 60,
        color: '#55BF3B' // green
    }, {
        from: 60,
        to: 80,
        color: '#DDDF0D' // yellow
    }, {
        from: 80,
        to: 100,
        color: '#DF5353' // red
    }]        
},

series: [{
    name: 'Speed',
    data: [80],
    tooltip: {
        valueSuffix: ' km/h'
    }
}]

}, 
//Add some life
function (chart) {
if (!chart.renderer.forExport) {
    setInterval(function () {
        var point = chart.series[0].points[0],
            newVal,
            inc = Math.round((Math.random() - 0.5) * 20);
        
        newVal = point.y + inc;
        if (newVal < 0 || newVal > 200) {
            newVal = point.y - inc;
        }
        
        point.update(newVal);
        
    }, 3000);
}
});
});
 --%>


$(function() {

    

    Highcharts.setOptions({

                    global : {

                                    useUTC : false

                    }

    });

    

    // Create the chart
		var $liveCPUMemoryUsageContent = $('.memoryUsage'+'<%=pageId%>');
				chartLiveCpuMemoryusageConfig = {
                    chart : {
                    			renderTo: null,
                                    events : {

                                                    load : function() {



                                                                    // set up the updating of the chart each second

                                                                    var series = this.series[0];

                                                                    setInterval(function() {

                                                                                    var x = (new Date()).getTime(), // current time

                                                                                    y = Math.round(Math.random() * 100);

                                                                                    series.addPoint([x, y], true, true);

                                                                    }, 3000);

        

        var series1 = this.series[1];

                                                                    setInterval(function() {

                                                                                    var x = (new Date()).getTime(), // current time

                                                                                    y = Math.round(Math.random() * 100);

                                                                                    series1.addPoint([x, y], true, true);

                                                                    }, 3000);

        

        var series2 = this.series[2];

                                                                    setInterval(function() {

                                                                                    var x = (new Date()).getTime(), // current time

                                                                                    y = Math.round(Math.random() * 100);

                                                                                    series2.addPoint([x, y], true, true);

                                                                    }, 3000);

        

        var series3 = this.series[3];

                                                                    setInterval(function() {

                                                                                    var x = (new Date()).getTime(), // current time

                                                                                    y = Math.round(Math.random() * 100);

                                                                                    series3.addPoint([x, y], true, true);

                                                                    }, 3000);

                                                    }

    

                                    }

                    },

                    

                    rangeSelector: {

                                    buttons: [{

                                                    count: 1,

                                                    type: 'minute',

                                                    text: '1M'

                                    }, {

                                                    count: 5,

                                                    type: 'minute',

                                                    text: '5M'

                                    }, {

                                                    type: 'all',

                                                    text: 'All'

                                    }],

                                    inputEnabled: false,

                                    selected: 0

                    },

                    

                    title : {

                                    text : null,
                                    style: {
                                	        fontSize: '14px'
                                   }

                    },

                    

legend : {

layout : 'vertical',

verticalAlign : "top",

align : 'right',

x : 0,

y : 20,

enabled : true,
floating:true

},



                    exporting: {

                                    enabled: false

                    },

                    

                    series : [{

                                    name : 'AAA-Banglore-Primary',

type : 'areaspline',

marker : {symbol : 'circle'},

color: Highcharts.getOptions().colors[1],

                                    data : (function() {

                                                    // generate an array of random data

                                                    var data = [], time = (new Date()).getTime(), i;



                                                    for( i = -999; i <= 0; i++) {

                                                                    data.push([

                                                                                    time + i * 1000,

                                                                                    Math.round(Math.random() * 100)

                                                                    ]);

                                                    }

                                                    return data;

                                    })()

                    },{

                                    name : 'AAA-Chennai-Primary',

type : 'areaspline',

marker : {symbol : 'circle'},

color: Highcharts.getOptions().colors[0],

                                    data : (function() {

                                                    // generate an array of random data

                                                    var data = [], time = (new Date()).getTime(), i;



                                                    for( i = -999; i <= 0; i++) {

                                                                    data.push([

                                                                                    time + i * 1000,

                                                                                    Math.round(Math.random() * 100)

                                                                    ]);

                                                    }

                                                    return data;

                                    })()

                    },{

                                    name : 'AAA-Delhi-Primary',

color: Highcharts.getOptions().colors[2],

type : 'areaspline',

marker : {symbol : 'circle'},

                                    data : (function() {

                                                    // generate an array of random data

                                                    var data = [], time = (new Date()).getTime(), i;



                                                    for( i = -999; i <= 0; i++) {

                                                                    data.push([

                                                                                    time + i * 1000,

                                                                                    Math.round(Math.random() * 100)

                                                                    ]);

                                                    }

                                                    return data;

                                    })()

                    },{

                                    name : 'AAA-Hydrabad-Primary',

color: Highcharts.getOptions().colors[4],

type : 'areaspline',

marker : {symbol : 'circle'},

                                    data : (function() {

                                                    // generate an array of random data

                                                    var data = [], time = (new Date()).getTime(), i;



                                                    for( i = -999; i <= 0; i++) {

                                                                    data.push([

                                                                                    time + i * 1000,

                                                                                    Math.round(Math.random() * 100)

                                                                    ]);

                                                    }

                                                    return data;

                                    })()

                    }]

    };
				$liveCPUMemoryUsageContent.each(function(i, e){
					chartLiveCpuMemoryusageConfig.chart.renderTo = e;
						    new Highcharts.StockChart(chartLiveCpuMemoryusageConfig);
				});


});
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/instancedashboard/EditLiveCPUMemoryUsage.jsp" id="editJsp">
 	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%-- <tr>
 			<td width="12%" valign="top"><div class="cpuUsageMeter1<%=pageId%>" style="height: 200px; min-width: 120px;overflow:auto;"></div></td>
 			<td width="12%" valign="top"><div class="cpuUsageMeter2<%=pageId%>" style="height: 200px; min-width: 120px;overflow:auto;"></div></td>
 			<td width="12%" valign="top"><div class="cpuUsageMeter3<%=pageId%>" style="height: 200px; min-width: 120px;overflow:auto;"></div></td>
 			<td width="12%" valign="top"><div class="cpuUsageMeter4<%=pageId%>" style="height: 200px; min-width: 120px;overflow:auto;"></div></td>
 		</tr> --%>
 		<tr>
 			<td colspan="4">
 				<div class="memoryUsage<%=pageId%>" style="height: 300px; min-width: 545px;overflow:auto;"></div>
 			</td>
 		</tr>
	</table>
</body>
</html>