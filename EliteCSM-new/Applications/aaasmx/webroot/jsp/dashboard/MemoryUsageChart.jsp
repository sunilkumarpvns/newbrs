<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Memory Usage Chart</title>
<!-- js file for building class using jquery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.class.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/chart-generic-class.js"></script>
<script>
var interval = <%=request.getParameter("interval") != null ? request.getParameter("interval") : 5%>;
<%--
    Description :  Chart Data wrapper classes for Memory Usage Summary
--%>

var HMemoryUsageCategories=HCategoriesData.extend({
	init : function (){
		this._super();
	},
	setCategories : function(categories){
		this._super(categories);
	},
	getCategories : function(){
		return this._super();
	}
});

var HMemoryUsagePointData=HPointData.extend({
	init : function(){
		this._super();
	},
	setName : function(name){
		this._super(name);
	},
	setColor : function(color){
		this._super(color);
	},
	setYPoints : function(y){
		this._super(y);
	},
	setXPoint : function(x){
		this._super(x);
	},
	getName : function(){
		return this._super();
	},
	getColor : function(){
		return this._super();
	},
	getYPoints : function(){
		return this._super(); 
	},
	getXPoint : function(){
		return this._super();
	},
	buildPointData : function(){
		var pointData={};
		
		// TODO EC : NEED TO OVERRIDE IT 
	  return pointData;	
	}
});

var HMemoryUsageSeriesData=HSeriesData.extend({
	init : function(){
		this._super();
	},
	addPoint : function(pointData){
		this._super(pointData);
		//override behavior of super
	},
	setPointList : function(pointList){
		this._super(pointList);
	},
	setPointInterval : function(pointInterval){
		this.pointInterval=Number(pointInterval);
	},
	getPointInterval : function(pointInterval){
		return this.pointInterval;
	},
	buildSeriesData : function(){
		var seriesData={
             "name" : this.name,
             "pointInterval" : this.pointInterval,
             "data" : this.pointList,
             "tooltip" : {
                 "valueDecimals" : 4
             }
        };
		console.log("seriesData --> "+seriesData);
	   return seriesData;	
	},
	addPointsToSeries : function(memoryUsageData){
		var chart1 = $('#aaaPrimaryServerMemoryUsage').highcharts();
		if(typeof chart1 != "undefined")
		{
			var series0 = chart1.series[0];
			var x=memoryUsageData.epochTime;
			var y=memoryUsageData.memoryUsage;
			if(x != null && y != null)
			 series0.addPoint([x,y],true,true);
		}
	}
});


var HMemoryUsageSeries=HSeries.extend({
	init : function(){
		this._super();
	},
	setSeries : function(series){
		this._super(series);
	},
	addSeries : function(series){
			this._super(series);	
	},
	getSeries : function(){
		return this._super();
	}
	
	
});

<%--
    Description :  Memory Usage Chart  Chart Data Configuration helper class
--%>

var MemoryUsageChartConfigHelper=$.Class.extend({
	init :function (){
		
	},
	renderMemoryUsageSeries :function (memoryUsageDataList){
		 if(typeof memoryUsageDataList != "undefined"){
			 var memorySeries=new HMemoryUsageSeries();
				
				var memoryUsageSeriesData=new HMemoryUsageSeriesData();
					memoryUsageSeriesData.setPointList(memoryUsageDataList);
					memoryUsageSeriesData.setName("Memory Usage");
					memoryUsageSeriesData.setPointInterval(100000);
					
				var memSeriesData = memoryUsageSeriesData.buildSeriesData();
				memorySeries.addSeries(memSeriesData);
				return memorySeries.getSeries();
		 }
		 return null;
		 
	},
	addMemoryUsagePoint : function(memoryUsageData){
		var memoryUsageSeriesData=new HMemoryUsageSeriesData();
		    memoryUsageSeriesData.addPointsToSeries(memoryUsageData);
	}
});

<%-- 
   Description :  Data classes that is use to convert json data received from websocket  into Memory Usage Chart Data  object
--%>

var MemUsageChartData = $.Class.extend({
	  init: function(){
	  },
	  setEpochTime:function(epochTime){
		  this.epochTime=Number(epochTime);
	  },
	  setMemoryUsage:function(memoryUsage){
		  this.memoryUsage=Number(memoryUsage);
	  }
	});

<%--
    Description : Highchart option configuration for Memory Usage Chart
--%>
var options={
		chart: {                			
        	zoomType: 'xy',
        	events : {
				load : function() {

				}
			}
	    },
        rangeSelector : {
                selected : 1
        },
        title : {
                text : 'Live Memory Usage',
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
        series : []
};

<%--
    Description : Instantiate object of MemoryUsageChartConfigHelper and MemUsageChartData class 
--%>
var memoryUsageChartConfigHelper =new MemoryUsageChartConfigHelper();
var memUsageChartData=new MemUsageChartData(); 

<%--
    Description :  This method is called to process and render data to chart received by websocket for Memory Usage Chart 
--%>   

function renderMemoryUsageChartData(data){
	
	var seriesData=memoryUsageChartConfigHelper.renderMemoryUsageSeries(data);
	if(seriesData != null)
	  options.series=seriesData;
	 var chart1=$('#aaaPrimaryServerMemoryUsage').highcharts('StockChart',options);
}

<%--
   Description :  This method is called to process and update data to chart received by websocket for Memory Usage Chart 
--%>
 
function updateMemoryUsageChartData(data){
	
	for(var i=0;i<data.length;i++){
		if(data[i] != null){
			var memoryUsageData=new MemUsageChartData();
			memoryUsageData.setEpochTime(data[i].epochTime);
			memoryUsageData.setMemoryUsage(data[i].memoryUsage);
			memoryUsageChartConfigHelper.addMemoryUsagePoint(memoryUsageData);	
		}else{
			console.log("Memory Usage Chart:Null data received at index "+i);
		}
			
	}
}




$(function() {
	var data = {
			header : {
				id : "MEMUSAGE",
				type : "MEMUSAGE"
			},
			body : {
				interval : interval
			}
	};
	var intervalCounter = 0;
	var widgetHandler  = {
			renderData : function(data) {
				
				renderMemoryUsageChartData(data);
			},
			updateData : function(data) {
				updateMemoryUsageChartData(data);
			}
	};
	dashBoardWidgets.register(new Widget("MEMUSAGE", widgetHandler));
	dashBoardWidgets.sendRequest(data);
        
});




<%-- END : Primary Memory Usage Info --%>
</script>

</head>
<body>
   <div id="aaaPrimaryServerMemoryUsage" style="min-height: 300px; min-width: 250px"></div>
</body>
</html>

