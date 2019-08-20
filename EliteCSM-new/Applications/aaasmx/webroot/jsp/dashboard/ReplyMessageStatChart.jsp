<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ReplyMessage Statistic chart</title>

<!-- js file for building class using jquery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.class.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/chart-generic-class.js"></script>
  
<script>
var interval = <%=request.getParameter("interval") != null ? request.getParameter("interval") : 5%>;
//options for charts 
var chart;
var colors = Highcharts.getOptions().colors;
// for testing make specific chart type class
<%--
   Description :  Chart Data wrapper classes for Reply Message Statistics summary
--%>
var HReplyMessageCategories=HCategoriesData.extend({
	init : function(){
		this._super();
	},
	setCategories : function(categories){
		this._super(categories);
	},
	getCategories : function(){
		return this._super();
	}
});
var HReplyMessagePointData=HPointData.extend({
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
	  var pointData={
				"y":this.yPoints[0],
				"color":this.color
		};
	  return pointData;
	}
});

var HReplyMessageSeriesData=HSeriesData.extend({
	init : function(){
		this._super();
	},
	setPointList : function(pointList){
		this._super(pointList);
	},
	setColor : function(color){
		this._super(color);
	},
	setName : function(name){
		this._super(name);
	},
	getPointList : function(){
		return this._super(); 
	},
	getColor : function(){
		return this._super();
	},
	getName : function(){
		return this._super();
	},
	addPoint : function(pointData){
		this._super(pointData);
		//override behavior of super
		
	},
	buildSeriesData : function(){
		var seriesData="";
		if(this.color != null && this.pointList.length>0){
			seriesData={
					"data":this.pointList,
					"color":this.color 
			};
		}
	   return seriesData;	
	}
});


var HReplyMessageSeries=HSeries.extend({
	init : function(){
		this._super();
	},
	setSeries : function(series){
		this.series=series;
	},
	addSeries : function(series){
			this.series.push(series);	
	},
	getSeries : function(){
		return this.series;
	}
	
});

<%--
    Description : Chart Data wrapper classes for Server Instance summary
--%>

var HSIDCategories=HCategoriesData.extend({
	init : function(){
		this._super();
	},
	setCategories : function(sidDetails){
		var jCategories=[];
		if(typeof sidDetails != "undefined"){
			$.each(sidDetails, function(i, sidData){
				jCategories.push(sidData.sidName);
			 });
		 }
		this._super(jCategories);
	},
	getCategories : function(){
		return this._super();
	}
});
var HSIDPointData=HPointData.extend({
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
		pointData={
				"y":this.yPoints[0]
		};
		//alert("buildPointData calls ");
	  return pointData;	
	}
});

var HSIDSeriesData=HSeriesData.extend({
	init : function(){
		this._super();
	},
	addPoint : function(pointData){
		this._super(pointData);
		//override behavior of super
	},
	buildSeriesData : function(){
		var seriesData={
            "name": this.name,
            "data": this.pointList
        };
	   return seriesData;	
	}
});


var HSIDSeries=HSeries.extend({
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
   Description :  Data classes that is use to convert json data received from websocket  into Reply Message Chart Data  object
--%>
var ReplyMessageData=$.Class.extend({
	init : function(){
       		
	},
	setReplyMessageName : function(replyMessageName){
		this.replyMessageName=replyMessageName;
	},
	setReplyMessageCount : function(replyMessageCount){
		this.replyMessageCount=replyMessageCount;
	},
	setSIDDetails : function(sidDetails){
		this.sidDetails=sidDetails;
	}
	
});

var ReplyMessages=$.Class.extend({
	init : function(){
	},
	setReplyMessages : function(replyMessages){
		this.replyMessages=replyMessages;//contains all reply messages
	},
	setReplyMessageDataList : function(replyMessageDataList){
		this.replyMessageDataList=replyMessageDataList;
	}
});

var SIDDetailData=$.Class.extend({
	init : function(){},
	setSIDName : function(sidName){
		this.sidName=sidName;
	},
	setSIDCount: function(sidCount){
		this.sidCount=sidCount;
	}
});

<%--
    Description :  Reply Message  Chart Data Configuration helper class
--%>
var ReplyMsgChartDataConfigHelper=$.Class.extend({
	init : function(){
	},
	configReplyMesssageCategories : function(replyMessageCategories){
		var rmCategoryData=new HReplyMessageCategories();
		rmCategoryData.setCategories(replyMessageCategories);
		return rmCategoryData.getCategories();
	},
	configSIDCategories : function (sidDetails){
		var sidCategoryData=new HSIDCategories();
		sidCategoryData.setCategories(sidDetails);
		return sidCategoryData.getCategories();
	},
	configReplyMesssageSeries : function(replyMessageDataList){
		 if(typeof replyMessageDataList != "undefined"){
			 name='Reply-Message';
			 var replyMessageSeries=new HReplyMessageSeries(); 
			 var replyMessageSeriesData=new HReplyMessageSeriesData();
			 replyMessageSeriesData.setName(name);
			 replyMessageSeriesData.setColor('white');
			 $.each(replyMessageDataList, function(i,replyMessages){
				 var replyMessagePointData=new HReplyMessagePointData();
				 replyMessagePointData.setYPoints(replyMessages.replyMessageCount);
				 replyMessagePointData.setColor(colors[0]);
				 var pointData=replyMessagePointData.buildPointData();
				 replyMessageSeriesData.addPoint(pointData);
			 });
			 
			 var seriesData=replyMessageSeriesData.buildSeriesData();
			 replyMessageSeries.addSeries(seriesData);
			 return replyMessageSeries.getSeries();
		 }
		 return null;
	},
	configSIDSeries :function (replyMessageData){
		
		 if(typeof replyMessageData != "undefined"){
			 //make sid series object
			 //make sid series data object
			 var series=new HSIDSeries();
			 var seriesData=new HSIDSeriesData();
			 var name=replyMessageData.replyMessageName;
			 $.each(replyMessageData.sidDetails, function(i, sidData){
				 var sidPointData=new HSIDPointData();
				 sidPointData.setYPoints(sidData.sidCount);
				 var pointData=sidPointData.buildPointData();
				 seriesData.addPoint(pointData);
			 });
			 seriesData.setName(name);
			 var seriesData=seriesData.buildSeriesData();
			 series.addSeries(seriesData);
			 return series.getSeries();
		 }
	}
	
});

<%--
    Description :  Instantiate  ReplyMessages and ReplyMsgChartDataConfigHelper class object
--%>
var replyMessageDataList=new ReplyMessages();
var replyMsgChartDataConfigHelper=new ReplyMsgChartDataConfigHelper();

<%--
   Description : Highchart option configuration for sidDetail Chart
--%>
var sidDetailOptions={
        chart: {
            type: 'column',
            renderTo:'replyMessageId'
        },
        title: {
            text: 'Server Instance Detail'
        },
        subtitle: {
            text: null
        },
        xAxis: {
            categories: [],//dynamic
            title: {
                text: null
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Reply Message Count',
                align: 'middle'
            },
            labels: {
                overflow: 'justify'
            }
        },
        tooltip: {
            valueSuffix: null
        },
        plotOptions: {
            bar: {
                dataLabels: {
                    enabled: true
                }
            }
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -40,
            y: 100,
            floating: true,
            borderWidth: 1,
            backgroundColor: '#FFFFFF',
            shadow: true
        },
        credits: {
            enabled: false
        },
        series: [],
        exporting: {
            enabled: true,
            buttons: {
	           back: {
	                text: 'Back',
	                onclick: function () {
	                	var replymessageCategories=replyMsgChartDataConfigHelper.configReplyMesssageCategories(replyMessageDataList.replyMessages);
	                	chart = new Highcharts.Chart(replyMessageOptions);
	                	chart.xAxis[0].setCategories(replymessageCategories);
	                }
	            }
            }
        }
    };
    
<%--
    Description : Highchart option configuration for ReplyMessage Chart
--%>

var replyMessageOptions={
        chart: {
        	 type: 'bar',
             marginLeft: 275, 
             marginRight:50,
             zoomType: 'xy',
             renderTo:'replyMessageId'
        },
        title: {
            text: null
        },
        subtitle: {
            text: 'Click the bar to view Reply Message per Instance.'
        },
        xAxis: {
            categories: []
        },
        yAxis: {
        	title: {
                text: '# Reply Message Sent',
                align: 'high'                    
            }
        },
        tooltip: {
        	followPointer : true,                
            formatter: function() {
                var point = this.point,
                    s = (this.x+200) +':<b>'+ (this.y-20) +' Reply-Message sent</b><br/>';
                if (point.drilldown) {
                    s += 'Click to view '+ point.category +' Details per Instance';
                } else {
                    s += 'Click to return to Reply Message Statistics';
                }
                return s;
            }
        },
        plotOptions: {
        	bar: {
                cursor: 'pointer',
                point: {
                    events: {
                        click: function() {
                            //custom code to display sid detail for specific reply message
                        	var replyMessageData=replyMessageDataList.replyMessageDataList[this.x];
                        	sidDetailOptions.series=replyMsgChartDataConfigHelper.configSIDSeries(replyMessageData);
                        	var sidCategories=replyMsgChartDataConfigHelper.configSIDCategories(replyMessageData.sidDetails);
                        	var sidChart = new Highcharts.Chart(sidDetailOptions);
                        	sidChart.xAxis[0].setCategories(sidCategories);
                        	
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
        series: [],
        exporting: {
            enabled: false
        }
    };
    
<%--
   Description :  This method is called to process and render data to chart received by websocket for Reply Message Statistics Chart 
--%>     
    
function updateReplyMessageStatChartData(jsonData){
	
	replyMessageDataList.setReplyMessages(jsonData.replyMessages);
	//alert(jsonData.toSource());
	var jReplyMessageDataArray=jsonData.replyMessageDataList;
	var jreplyMessageList=[];
	for(var i=0;i<jReplyMessageDataArray.length;i++){
		
		var replyMessageData=new ReplyMessageData();
		replyMessageData.setReplyMessageName(jReplyMessageDataArray[i].replyMessageName);
		replyMessageData.setReplyMessageCount(jReplyMessageDataArray[i].replyMessageCount);
		var jSIDDetails=jReplyMessageDataArray[i].sidDetail;
		var sidDetailList=[];
		for(var j=0;j<jSIDDetails.length;j++){
			var jSIDDetailData=jSIDDetails[j];
			var sidDetailData=new SIDDetailData();
			sidDetailData.setSIDName(jSIDDetailData.sidName);
			sidDetailData.setSIDCount(jSIDDetailData.sidCount);
			sidDetailList.push(sidDetailData);
		}
		replyMessageData.setSIDDetails(sidDetailList);
		jreplyMessageList.push(replyMessageData);
	}
	replyMessageDataList.setReplyMessageDataList(jreplyMessageList);
	
	
	var replymessageSeriesData=replyMsgChartDataConfigHelper.configReplyMesssageSeries(replyMessageDataList.replyMessageDataList);
	var replymessageCategories=replyMsgChartDataConfigHelper.configReplyMesssageCategories(replyMessageDataList.replyMessages);
	
	replyMessageOptions.series=replymessageSeriesData;
	chart = new Highcharts.Chart(replyMessageOptions);
	chart.xAxis[0].setCategories(replymessageCategories);
}    

$(document).ready(function(){ 
	var data = {
			header : {
				id : "REPLYMESSAGESTATSUMMARY",
				type : "REPLYMESSAGESTATSUMMARY"
			},
			body : {
				interval : interval
			}
	};
	var intervalCounter = 0;
	var widgetHandler  = {
			renderData : function(data) {
				updateReplyMessageStatChartData(data);
			},
			updateData : function(data) {
				updateReplyMessageStatChartData(data);
			}
	};
	dashBoardWidgets.register(new Widget("REPLYMESSAGESTATSUMMARY", widgetHandler));
	dashBoardWidgets.sendRequest(data);
	});


</script>

</head>
<body>
   <div id="replyMessageId" style="min-height: 300px; min-width: 250px"></div>
</body>
</html>
