<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	<%--Widget data specific js files --%>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/highstock.js"></script>
	<!--Note : DO NOT EXPORT exporting.js file -->
    <!-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/exporting.js"></script> -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/chart-generic-class.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.timer.js"></script>
	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<%-- REQUIRED : This Js is required for to get widget Configuration--%>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.class.js"></script>
	<%
		String pageId = request.getParameter("pageId");
		String liveCpuWidgetId= request.getParameter("widgetId");
	%>
<script type="text/javascript">
var widgetTimer_<%=liveCpuWidgetId%>=new CommonTimer();
widgetTimer_<%=liveCpuWidgetId%>.init('freezebtn'+<%=liveCpuWidgetId%>,<%=liveCpuWidgetId%>,'CPU_USAGE');


	$('#freezebtn'+<%=liveCpuWidgetId%>).click(function(){
		 freeze(widgetTimer_<%=liveCpuWidgetId%>);
	});

	$('#unfreezebtn'+<%=liveCpuWidgetId%>).click(function(){
		unfreeze(widgetTimer_<%=liveCpuWidgetId%>);
	});	



var colors = Highcharts.getOptions().colors;


Highcharts.setOptions({
	global : {
		useUTC : false
	}
});

var CpuUsageSeries = HSeries.extend({
	init : function() {
		this._super();
	},
	setSeries : function(series) {
		this._super(series);
	},
	addSeries : function(series) {
		this._super(series);
	},
	getSeries : function() {
		return this._super();
	}

});
var CpuUsageSeriesPointData = HPointData.extend({
	init : function() {
		this._super();
	},
	setName : function(name) {
		this._super(name);
	},
	setColor : function(color) {
		this._super(color);
	},
	setYPoints : function(y) {
		this._super(y);
	},
	setXPoint : function(x) {
		this._super(x);
	},
	getName : function() {
		return this._super();
	},
	getColor : function() {
		return this._super();
	},
	getYPoints : function() {
		return this._super();
	},
	getXPoint : function() {
		return this._super();
	},
	buildPointData : function() {
		var pointData = {};
		pointData = [ this.xPoint, this.yPoints[0] ];
		return pointData;
	}
});

var CpuUsgaeSeriesData = HSeriesData.extend({
	init : function() {
		this._super();
		this.id = '';

	},
	addPoint : function(pointData) {
		this._super(pointData);
		//override behavior of super
	},
	setYAxis : function(yAxis) {
		this._super(yAxis);
	},
	setType : function(type) {
		this._super(type);
	},
	getYAxis : function() {
		return this._super();
	},
	getType : function() {
		return this._super();
	},
	setId : function(id) {
		this.id = id;
	},
	getId : function() {
		return this.id;
	},
	buildSeriesData : function() {
		var seriesData = {
			"name" : this.name,
			"color" : this.color,
			"yAxis" : this.yAxis,
			"type" : this.type,
			"data" : this.pointList,
			"id" : this.id
		};
		return seriesData;
	}
});

//Helper class to convert JSON Data from server to chart data-
var CpuUsageWidgetConfigHelper = $.Class.extend({
    init: function () {

    },
    configureCpuUsageSeries: function (cpuUsageMap) {
        var cpuUsageSeries = new CpuUsageSeries();
        var i = 0;
        $.each(cpuUsageMap[0], function (serverInstance, cpuUsageList){
            var usageSeries = new CpuUsgaeSeriesData();
            usageSeries.setName(serverInstance);
            usageSeries.setId(serverInstance);
            usageSeries.setColor(colors[++i]);
            usageSeries.setType("areaspline");
            $.each(cpuUsageList, function (i, cpuUsageData) {
                var pointData = new CpuUsageSeriesPointData();
                pointData.setXPoint(cpuUsageData.createTime);
                pointData.setYPoints(cpuUsageData.cpuAverageUsage);
                var jsonPointData = pointData.buildPointData();
                usageSeries.addPoint(jsonPointData);
            });
            var usageSeriesData = usageSeries.buildSeriesData();
            cpuUsageSeries.addSeries(usageSeriesData);
        });
        return cpuUsageSeries.getSeries();
    },
    updateCpuUsageSeries: function (cpuUsageMap){
        console.log("updateCpuUsageSeries called");
        $.each(cpuUsageMap[0], function (serverInstance, cpuUsageList){
            var usageSeries = cpuUsageChart.get(serverInstance);
            $.each(cpuUsageList, function (i, cpuUsageData){
                var pointData = new CpuUsageSeriesPointData();
                pointData.setXPoint(cpuUsageData.createTime);
                pointData.setYPoints(cpuUsageData.cpuAverageUsage);
                var jsonPointData = pointData.buildPointData();
                usageSeries.addPoint(jsonPointData,false,false,false);
            });
        });
        cpuUsageChart.redraw();
   }
});

var cpuUsageWidgetConfigHelper = new CpuUsageWidgetConfigHelper();
var cpuUsageChart;

function updateCpuUsageInitialData(data) {
	console.log("updateCpuUsageInitialData called");
	if (data !== null) {
		chartCpuUsageConfig.series = cpuUsageWidgetConfigHelper.configureCpuUsageSeries(data);
		cpuUsageChart = new Highcharts.StockChart(chartCpuUsageConfig);
	}
}

function updateCpuUsageLiveData(data) {
    console.log("updateCpuUsageLiveData called");
	if (data !== null ) {
		cpuUsageWidgetConfigHelper.updateCpuUsageSeries(data);
	}
}


chartCpuUsageConfig = {
	    chart: {
	        renderTo: 'liveCpuWidgetId_' + '<%=liveCpuWidgetId%>'+'_',
	        events: {
	            load: function (event) {
	            	hideProgressBarDiv('progressbarDIV'+<%=liveCpuWidgetId%>);
	            }
	        },
	        zoomType: 'xy'
	    },
	    scrollbar: {
	        liveRedraw: false,
	        enabled: true
	    },
	    rangeSelector: {
	        selected: 1,
	        inputEnabled: false,
	        buttonSpacing: 3,
	        buttons: [{
	            type: 'minute',
	            count: 30,
	            text: '30m'
	        }, {
	            type: 'minute',
	            count: 60,
	            text: '1H'
	        }, {
	            type: 'minute',
	            count: 720,
	            text: '12H'
	        }, {
	            type: 'day',
	            count: 1,
	            text: '1D'
	        }, {
	            type: 'week',
	            count: 1,
	            text: '1W'
	        }, {
	            type: 'month',
	            count: 1,
	            text: '1M'
	        }, {
	            type: 'all',
	            text: 'All'
	        }]
	    },
	    plotOptions: {
	        areaspline: {
	            stacking: 'normal',
	            lineColor: '#666666',
	            lineWidth: 1,
	            marker: {
	                symbol: 'circle',
	                lineWidth: 1,
	                lineColor: '#666666',
	                enabled: false,
	                radius: 1,
	                states: {
	                    hover: {
	                        enabled: true
	                    }
	                }
	            }
	        }
	    },
	    legend: {
	        enabled: true
	    },
	    turboThreshold: 200,
	    title: {
	        text: 'Live Cpu Usage',
	        style: {
	            fontSize: '14px'
	        }
	    },
	    xAxis: {
	        type: 'datetime',
	        tickPixelInterval: 150,
	        maxZoom: 100000, // fourteen days
	        title: {
	            text: null
	        },
	        dateTimeLabelFormats: {
	            second: '%Y-%m-%d<br/>%H:%M:%S',
	            minute: '%Y-%m-%d<br/>%H:%M',
	            hour: '%Y-%m-%d<br/>%H:%M',
	            day: '%Y<br/>%m-%d',
	            week: '%Y<br/>%m-%d',
	            month: '%Y-%m',
	            year: '%Y'
	        }
	    },
	    yAxis: {
	        title: {
	            text: 'Cpu Usage'
	        }
	    },
	    series: []
	};


var liveCpuWidgetId=<%=liveCpuWidgetId%>;
var confObj = getWidgetConfiguration(liveCpuWidgetId);  <%-- Use ConfObj to retrive configuration --%>

var internval=1;//default interval value
var liveCPUChartName = "";
var serverIdList =""; 
if(typeof confObj != 'undefined'){
	interval=confObj.get("REFRESHINTERVAL");
	daysPreviously=confObj.get("DAYSPREVIOUSLY");
	serverIdList = confObj.get("ELITEAAAINSTANCES");
	liveCPUChartName=confObj.get("NAME");
}
	// chart data fetch logic
	
	var id='liveCpuWidgetId_'+<%=liveCpuWidgetId%>+'_';
	
	var data = {
			header : {
				id : 'liveCpuWidgetId_'+<%=liveCpuWidgetId%>+'_',
				type : "CPU_USAGE"
			},
			body : {
				interval : interval,
				aaaServerIds:serverIdList
			}
	};
	var intervalCounter = 0;
	var widgetHandler  = {
			renderData : function(data) {
				updateCpuUsageInitialData(data);
			},
			updateData : function(data) {
				updateCpuUsageLiveData(data);
			}
	};
	getDashBoardSocket().register(new Widget(id, widgetHandler));
    getDashBoardSocket().sendRequest(data);
    
    /** Export As PDF Function**/
    var EXPORT_WIDTH = 700;
	var canvas;
	var svg;
	function save_chart_livecpu(chart) {
	
	    var render_width = EXPORT_WIDTH;
	    var render_height = render_width * chart.chartHeight / chart.chartWidth
	
	    // Get the cart's SVG code
	    svg = chart.getSVG({
	        exporting: {
	            sourceWidth: chart.chartWidth,
	            sourceHeight: chart.chartHeight
	        }
	    });

	    // Create a canvas
	    canvas = document.createElement('canvas');
	    canvas.height = render_height;
	    canvas.width = render_width;
	    canvas.style.backgroundColor='white';
	 
	    //create new canvas
	    var image1 = new Image();
	    var canvas1 = document.createElement("canvas");
	    var context1 = canvas1.getContext('2d');
	    image1.src = $('#elitecore_Image').attr('src');
	    canvas1.height=77;
	    canvas1.width=213;
    
	    // Create an image and draw the SVG onto the canvas
	    var image = new Image;
	    image.onload = function() {
	        canvas.getContext('2d').drawImage(this, -5, 0, render_width+35, render_height+10);
      		canvas.getContext("2d").fillStyle='white'; 
        
			var chartName= liveCPUChartName;
		
			canvas.style.backgroundColor='white';
			var doc = new jsPDF();
		
			doc.setProperties({
	      	  title: chartName,
	       	  subject: 'Widget Type Name : Generated on '+getDateTime(),           
	       	  author: 'AutoGenerated',
	          keywords: 'generated, javascript, web 2.0, ajax',
	          creator: 'EliteCSM SM'
	   		});
		
			context1.drawImage(image1,0,0,213,77);
      		var nextcontent = canvas1.toDataURL('image/jpeg');
		
			doc.addImage(nextcontent, 'JPEG',5 ,1,30.43,11);
			 
			console.log('before URl');
		    var content = canvas.toDataURL('image/jpeg');
	   
		    doc.addImage(content, 'JPEG', 40, 35, 130, 85);
		    
		    doc.setFontSize(7.5);
		    doc.setTextColor(0); 
		    
		    var pageWidth= doc.internal.pageSize.width;
		    var pageHeight= doc.internal.pageSize.height;
	    
		    doc.text(pageWidth-50,10, "Generated on : " + getDateTime());
		    
		    doc.setDrawColor(192,192,192); 
	    	doc.line(5,12 , pageWidth-5,12);
		     
	    	doc.text(pageWidth-20,pageHeight - 5, "Page : "+pageIndex );
	    	doc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);
	
		    doc.setDrawColor(0);
		    doc.setFillColor(1,81,152);
		    doc.rect(10, 25, pageWidth-20, 5, 'F');
		    
		    doc.setDrawColor(0);
		    doc.setFillColor(1,81,152);
		    doc.rect(10, 122, pageWidth-20, 5, 'F'); 
		    
		    doc.setFontSize(7.5);
		    doc.setTextColor(255);  
		    
		    doc.text(82, 28.5, chartName + " Report");
		    doc.text(79, 125.5, chartName + " Details");
		    
		    //ESI Name - Rectangle
		    doc.setDrawColor(0);
		    doc.setFillColor(217,230,246);
		    doc.rect(10, 130, pageWidth-20, 8, 'F');
	    
		    doc.setFontSize(6.5);
		    doc.setTextColor(0); 
		    doc.setFontType("bold");
		    
		    doc.text(12, 137, "Server Name ");
		    
		    doc.text(38,137,"Create Date");
		    
		    doc.text(62, 137, " JVM Pool Type ");
		    
		    doc.text(92, 137, " JVM Memory Pool Name ");
		    
		    //JVM System Load 
		    doc.text(158, 133," JVM System Load ");
		    
		    doc.text(136, 137, " Min Value ");
		    
		    doc.text(162, 137,"  Max Value ");
		    
		    doc.text(184, 137, " Average ");
		    
		    var liveCPUArray = getLiveCPUDetails(); 
		  
		    doc.setTextColor(0);  
		    doc.setFontType("normal");
		    var flag=1;
		    
		    var yIndex=143.5;
	        var trackServerName ='';
	        var trackPoolType='';
	        var createTime = '';
		    $.each(liveCPUArray.serverName,function(key,value){
		    	if (yIndex >= pageHeight-10)
		    	{
		    		doc.addPage('a4','p');
			    	yIndex = 10; // Restart height position
		    		pageWidth= doc.internal.pageSize.width;
		    		pageHeight= doc.internal.pageSize.height;
		    	    pageIndex++;
		    	    doc.text(pageWidth-20,pageHeight - 5, "Page : "+pageIndex );
		            doc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);
		            doc.setDrawColor(192,192,192); 
		            doc.line(10, yIndex-6, pageWidth-10, yIndex-6);
		    	 
		    	}    	
		    	doc.setFontSize(6);
		    	
		    	if(createTime.length == 0){
		    		createTime=liveCPUArray.createTime[key].toString();
		    		doc.text(33, yIndex-2, liveCPUArray.createTime[key].toString());
		    	}
		    	
		    	if(createTime != liveCPUArray.createTime[key].toString()){
		    		createTime = liveCPUArray.createTime[key].toString();
		    		doc.text(33, yIndex-2, liveCPUArray.createTime[key].toString());
		    		doc.setDrawColor(192,192,192); 
		    		doc.line(30, yIndex-5, 155, yIndex-5); 
		    	}
		    	
		    	if(trackServerName.length == 0){
		    		trackServerName=value;
		    		doc.text(12, yIndex-2, value);
		    	}
		    	
		    	if(trackServerName != value){
		    		trackServerName =value;
		    		doc.text(12, yIndex-2, value);
		    		doc.setDrawColor(192,192,192); 
		    		doc.line(10, yIndex-5, 30, yIndex-5);
		    	}
		    	
		    	
		    	if(trackPoolType.length == 0){
		    		trackPoolType=liveCPUArray.jvmPoolType[key].toString();
		    		doc.text(62, yIndex-2, liveCPUArray.jvmPoolType[key].toString());
		    	}
		    	
		    	if(trackPoolType != liveCPUArray.jvmPoolType[key].toString()){
		    		trackPoolType = liveCPUArray.jvmPoolType[key].toString();
		    		doc.text(62, yIndex-2, liveCPUArray.jvmPoolType[key].toString());
		    		doc.setDrawColor(192,192,192); 
		    		doc.line(58, yIndex-5, 85, yIndex-5);
		    	}
		    	
		    	
		    	doc.text(92, yIndex-2, liveCPUArray.jvmPoolName[key].toString());
		    	
		    	var minValueLength = liveCPUArray.systemMinValue[key].toString().length;
		    	var charWidth = 1.25;
				var str = reverse(liveCPUArray.systemMinValue[key].toString());
				for(var i = minValueLength-1 ; i >= 0 ; i--){			  
					doc.text(147- charWidth * i, yIndex-2, str[i]);
				}
		    	
				var maxValueLength = liveCPUArray.systemMaxValue[key].toString().length;
		    	str = reverse(liveCPUArray.systemMaxValue[key].toString());
				for(var i = maxValueLength-1 ; i >= 0 ; i--){			  
					doc.text(174 - charWidth * i, yIndex-2, str[i]);
				}
		    	
		    	var systemAverageLength = liveCPUArray.systemAverage[key].toString().length;
		    	str = reverse(liveCPUArray.systemAverage[key].toString());
				for(var i = systemAverageLength-1 ; i >= 0 ; i--){			  
					doc.text(193 - charWidth * i, yIndex-2, str[i]);
				} 
		    	
		    	doc.setDrawColor(192,192,192); 
		    	
		   		
		    	doc.line(30, yIndex-5.5, 30, yIndex);
		    	doc.line(58, yIndex-5.5, 58, yIndex);
		    	doc.line(85, yIndex-5.5, 85, yIndex);
		    	doc.line(129,yIndex-5.5, 129, yIndex);
		    	doc.line(85, yIndex, pageWidth-10, yIndex); 
		    	
		    	//Min val- Max val Line
		    	
		    	doc.line(155, yIndex-5.5, 155, yIndex);
		    	doc.line(180,yIndex-5.5, 180, yIndex);
		    	doc.line(pageWidth-10,yIndex-5.5,pageWidth-10,yIndex);
		    	doc.line(10,yIndex-5.5,10,yIndex);
		    	
		    	if(flag == 1){
		    		flag = 0;
		    		doc.setFillColor(1,81,152);
		    	}else{
		    		flag = 1;
		    		doc.setFillColor(255,255,172);
		    	}
		    	yIndex=yIndex+5;
		   }); 
		    
		    doc.setDrawColor(192,192,192); 
    		doc.line(10, yIndex-5, 85, yIndex-5);
		    
		    var data = doc.output();
		    var buffer = new ArrayBuffer(data.length);
		    var array = new Uint8Array(buffer);
	
		    for (var i = 0; i < data.length; i++) {
		        array[i] = data.charCodeAt(i);
		    }
	
		    var blob = new Blob(
		        [array],
		        {type: 'application/pdf', encoding: 'raw'}
		    );
		    console.log('before save');
		    saveAs(blob, chartName+'.pdf');
		    console.log('aftre save');
	        
	    };
	    image.src = 'data:image/svg+xml;base64,' + window.btoa(svg);
	}
    
    var pageIndex =1;
    $('#exportbtn<%=liveCpuWidgetId%>').click(function(){
   		 save_chart_livecpu($('#liveCpuWidgetId_<%=liveCpuWidgetId%>_').highcharts()); 
    });
    
    $('#exportbtn_'+<%=liveCpuWidgetId%>).click(function(){
   	 	save_chart_livecpu($('#liveCpuWidgetId_<%=liveCpuWidgetId%>_').highcharts()); 
    });
    
    function reverse(s){
	    return s.split("").reverse().join("");
	}
    
    function getLiveCPUDetails(){
	    var jResponseData="";
		$.ajax({
			url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getLiveCpuUsageData',
		    type:'GET',
		    async:false,
		    data:{serverIds:serverIdList},
		    success: function(esiDataList){
		    	if(esiDataList != null)
		    	 jResponseData=esiDataList;  	
		    }
		}); 
		return jResponseData;
	}
    
    function getDateTime() {
	    var now     = new Date(); 
	    var year    = now.getFullYear();
	    var month   = now.getMonth()+1; 
	    var day     = now.getDate();
	    var hour    = now.getHours();
	    var minute  = now.getMinutes();
	    var second  = now.getSeconds(); 
	    if(month.toString().length == 1) {
	        var month = '0'+month;
	    }
	    if(day.toString().length == 1) {
	        var day = '0'+day;
	    }   
	    if(hour.toString().length == 1) {
	        var hour = '0'+hour;
	    }
	    if(minute.toString().length == 1) {
	        var minute = '0'+minute;
	    }
	    if(second.toString().length == 1) {
	        var second = '0'+second;
	    }   
	    var dateTime = day+'/'+month+'/'+year+' '+hour+':'+minute+':'+second;   
	     return dateTime;
	}
    
</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/instancewidgets/EditLiveCPUUsage.jsp?widgetId=<%=liveCpuWidgetId%>" id="editJsp">
<!-- expoting div -->

 <div id="exportingdiv<%=liveCpuWidgetId%>" style="width: 100%;display: table;background-position: center;vertical-align: middle;text-align:right;padding-top:10px;" align="center">
	<input type="button"  id="freezebtn<%=liveCpuWidgetId%>" name="button" value="Freeze" class="restart-btn" title="Freeze" align="right"/> 
 	<input type="button"  id="unfreezebtn<%=liveCpuWidgetId%>" name="button" value="Unfreeze" class="restart-btn" title="Unfreeze" align="right"/>
 	<input type="button"  id="exportbtn<%=liveCpuWidgetId%>" name="button" value="Export.." class="restart-btn" title="Unfreeze" align="right" style="margin-right: 20px;'"/> 
 </div> 



<%--ProgressBar Div : Start  --%>
 <div id="progressbarDIV<%=liveCpuWidgetId%>" class="progressbar" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>
<span id="exportbtn_<%=liveCpuWidgetId%>"></span>
 <%--ProgressBar Div : End  --%>
<div id="liveCpuWidgetId_<%=liveCpuWidgetId%>_" class="liveMemoryUsage<%=pageId%>" style="height: 300px; width:inherit !important;padding-bottom: 3px;overflow: auto;"></div> 
</body>
</html>