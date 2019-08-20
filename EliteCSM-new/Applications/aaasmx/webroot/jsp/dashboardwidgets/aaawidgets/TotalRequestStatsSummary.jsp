<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%--Widget data specific js files --%>
	
	<!--Note : DO NOT EXPORT exporting.js file -->
    <!-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/exporting.js"></script> -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/chart-generic-class.js"></script>
	
	<%-- REQUIRED : This Js is required for to get widget Configuration--%>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.class.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.timer.js"></script>
	
	<%--Code for get Widget Id--%>
	<%String totalReqStatSummaryId = request.getParameter("widgetId");%>

<style type="text/css">
.restart-btn{
	margin: 2px 2px;
	border: 1px solid rgba(0, 0, 0, 0.1);
	cursor: default !important;
	display: inline-block;
	min-width: 54px;
	text-align: center;
	text-decoration: none !important;
	border-radius: 2px;
	-moz-user-select: none;
	white-space: nowrap;
	color:rgb(34, 34, 34) ;	
	-webkit-touch-callout: none;
	-webkit-user-select: none;
	-khtml-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	
}
.restart-btn:hover {
    border-color: #C6C6C6;
    color: rgb(34, 34, 34) !important;
    box-shadow: 0px 1px 0px rgba(0, 0, 0, 0.15);
    background: -moz-linear-gradient(center top , #F8F8F8, #F1F1F1) repeat scroll 0% 0% transparent;
}

</style>
<script type="text/javascript">

var jTimerValue_<%=totalReqStatSummaryId%>=300000;
var unFreezeTimer_<%=totalReqStatSummaryId%>;

<%-- Timer code --%>

var eliteTimer = new (function() {

    var $countdown1;
    var incrementTime = 70;
    var currentTime = jTimerValue_<%=totalReqStatSummaryId%>; // 5 minutes (in milliseconds)

    function updateTimer() {

        /*  Output timer position */
        var timeString = formatTime(currentTime);
        
        /* $countdown.html(timeString);	 */        
        $countdown1.attr('value', timeString);

        /*  If timer is complete, trigger alert */
        if (currentTime == 0) {
        	eliteTimer.Timer.stop();
            /* reset counter division */
            /* $countdown1.attr('value', timeString); */
            console.log("updateTimer --->methodcall ::");
            <%-- $('#freezeBtnAuth'+<%=totalReqStatSummaryId%>).attr('value', 'Freeze'); --%>
            eliteTimer.resetCountdown();
            return;
        }

        // Increment timer position
        currentTime -= incrementTime;
        if (currentTime < 0) currentTime = 0;

    }

    this.resetCountdown = function() {

    /*     Get time from form
        var newTime = parseInt($form.find('input[type=text]').val()) * 1000;
        if (newTime > 0) {currentTime = newTime;} */

       /*  Stop and reset timer */
        currentTime = jTimerValue_<%=totalReqStatSummaryId%>;
        eliteTimer.Timer.stop().once();

    };
    
    this.init = function() {
	    $countdown1 = $('#freezeBtnAuth'+<%=totalReqStatSummaryId%>);
	  /*   $countdown = $('#countdown'); */
        eliteTimer.Timer = $.timer(updateTimer, incrementTime, false);
        currentTime = jTimerValue_<%=totalReqStatSummaryId%>;
   };
});
	eliteTimer.init();

	function pad(number, length){
	    var str = '' + number;
	    while (str.length < length) {str = '0' + str;}
	    return str;
	}
	
	function formatTime(time) {
		time = time / 10;
	    var min = parseInt(time / 6000),
	        sec = parseInt(time / 100) - (min * 60),
	        hundredths = pad(time - (sec * 100) - (min * 6000), 2);
	    return (min > 0 ? pad(min, 2) : "00") + "m " + pad(sec, 2) + "s "; 	}


	<%-- End Timer code --%>
	
	/* todo ec need to generalize it  */
	var colors = Highcharts.getOptions().colors;
	<%--
	var AC_color_code='#4572A7';
	var AR_color_code='#062173';
	var AA_color_code='#89A54E';
	var RD_color_code='#AA4643';
	--%>
	var AC_color_code_<%=totalReqStatSummaryId%>=colors[0];
	var AR_color_code_<%=totalReqStatSummaryId%>=colors[5];
	var AA_color_code_<%=totalReqStatSummaryId%>=colors[2];
	var RD_color_code_<%=totalReqStatSummaryId%>=colors[3];
	
	var esiName_<%=totalReqStatSummaryId%>='';
	var daysPreviously_<%=totalReqStatSummaryId%>=0;

	/* Range selector array define millinsecond of each rangeselector i.e.  [30min,1Month,1Week,1Month,All] which is used to calculate endtime  */
	var rangeSelectorArray_<%=totalReqStatSummaryId%>=[1800000,3600000,43200000,86400000,604800000,111600000,-1];

	<%-- Description :  Chart Data wrapper classes for Total Request Statistics summary--%>
	
	
	var HESIStatisticsCategories=HCategoriesData.extend({
		init : function(){
			this._super();
		},
		setCategories : function(esiStatCategories){
			this._super(esiStatCategories);
		},
		getCategories : function(){
			return this._super();
		}
	});
	
	var HESIStatisticsPointData=HPointData.extend({
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
		  return pointData;	
		}
	});
	
	var HESIStatisticsSeriesData=HSeriesData.extend({
		init : function(){
			this._super();
		},
		addPoint : function(pointData){
			this._super(pointData);
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
			return this._super(this.pointList); 
		},
		getColor : function(){
			return this._super(this.color);
		},
		getName : function(){
			return this._super(this.name);
		},
		buildSeriesData : function(){
			var seriesData={
		    		"name"  : this.name,
				    "color" : this.color,
				    "data"  : this.pointList 
				    };
		   return seriesData;	
		}
	});
	
	
	var HESIStatisticsSeries=HSeries.extend({
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
	/* detailed esi statistics data */
	var HESICategories=HCategoriesData.extend({
		init : function(){
			this._super();
		},
		setCategories : function(sidDetails){
			this._super(jCategories);
		},
		getCategories : function(){
			return this._super();
		}
	});
	var HESIPointData=HPointData.extend({
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
			pointData=[this.xPoint,this.yPoints[0]];
	 	    return pointData;	
		},
		buildAllPointData : function(){
			var pointData={};
			pointData=[this.xPoint,this.yPoints[0],this.yPoints[1],this.yPoints[2],this.yPoints[3]];
	 	    return pointData;	
		}
	});

	var HESISeriesData=HSeriesData.extend({
		init : function(){
			this._super();
		},
		addPoint : function(pointData){
			this._super(pointData);
			//override behavior of super
		},
		setYAxis : function(yAxis){
			this._super(yAxis);
		},
		setType: function(type){
			this._super(type);
		},
		getYAxis : function(){
			return this._super(); 
		},
		getType : function(){
			return this._super();
		},
		buildSeriesData : function(){
			var seriesData={
					   "name": this.name,
				       "color": this.color,
				       "yAxis": this.yAxis,
				       "type": this.type,
				       "data": this.pointList
	        };
		   return seriesData;	
		}
	});


	var HESISeries=HSeries.extend({
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
	

	<%-- Description : Chart Data wrapper classes for ESI summary--%>
	
	<%-- Description :  Total Request Statistics  Chart Data Configuration helper class--%>
var TotalReqStatChartDataConfigHelper=$.Class.extend({
	 init: function(){
		 
	 },
	 configESIStatisticsSeries : function(totalESIReqChartData){
		 var esiStatSeries =new HESIStatisticsSeries();
		 
		 var acSeries=new HESIStatisticsSeriesData();
		 acSeries.setPointList(totalESIReqChartData.accessChallenge);
		 acSeries.setName("Access-Challenge");
		 acSeries.setColor(AC_color_code_<%=totalReqStatSummaryId%>);
		 var acSeriesData=acSeries.buildSeriesData();
		 esiStatSeries.addSeries(acSeriesData);
		 
		 var arSeries=new HESIStatisticsSeriesData();
		 arSeries.setPointList(totalESIReqChartData.accessReject);
		 arSeries.setName("Access-Reject");
		 arSeries.setColor(AR_color_code_<%=totalReqStatSummaryId%>);
		 var arSeriesData=arSeries.buildSeriesData();
		 esiStatSeries.addSeries(arSeriesData);
		 
		 var aaSeries=new HESIStatisticsSeriesData();
		 aaSeries.setPointList(totalESIReqChartData.accessAccept);
		 aaSeries.setName("Access-Accept");
		 aaSeries.setColor(AA_color_code_<%=totalReqStatSummaryId%>);
		 var aaSeriesData=aaSeries.buildSeriesData();
		 esiStatSeries.addSeries(aaSeriesData);
		 
		 var rrSeries=new HESIStatisticsSeriesData();
		 rrSeries.setPointList(totalESIReqChartData.requestDrop);
		 rrSeries.setName("Request-Drops");
		 rrSeries.setColor(RD_color_code_<%=totalReqStatSummaryId%>);
		 var rrSeriesData=rrSeries.buildSeriesData();
		 esiStatSeries.addSeries(rrSeriesData);
		 return esiStatSeries.getSeries();
		 
	 },
	 configESIStatisticsCategories : function(totalESIReqChartData1){
		    var esiStatCategories=new HESIStatisticsCategories();
		    esiStatCategories.setCategories(totalESIReqChartData1.esi);
			return esiStatCategories.getCategories();
	 },
	 configESISeries : function(esiDataArray){
		 /* make esiseries object */
		 var esiSeries=new HESISeries();
		 /* make for esi series data object */
		 
		 /*
		   name: 'Accept-Challenge',
	       color: '#4572A7',
	       type: 'column',
	       yAxis: 1,
	       data: jNumACDetailedDataArray
		 */
		 var acSeriesData=new HESISeriesData();
		 acSeriesData.setName("Access-Challenge");
		 acSeriesData.setColor(AC_color_code_<%=totalReqStatSummaryId%>);
		 acSeriesData.setType("spline");
		 acSeriesData.setYAxis(0);
		 
		 /*
	              name: 'Accept-Reject',
	              type: 'spline',
	              color: '#AA4643',
	              yAxis: 2,
	              data: jNumARDetailedDataArray
	              
		 */
		 
		 
		 var arSeriesData=new HESISeriesData();
         arSeriesData.setName("Access-Reject");
         arSeriesData.setColor(AR_color_code_<%=totalReqStatSummaryId%>);
         arSeriesData.setType("spline");
         arSeriesData.setYAxis(1);
		 
		 /*
		   name: 'Access-Accept',
	       color: '#89A54E',
	       yAxis: 3,
	       type: 'spline',
	       data: jNumAADetailedDataArray
		 */
		 var aaSeriesData=new HESISeriesData();
		 aaSeriesData.setName("Access-Accept");
		 aaSeriesData.setColor(AA_color_code_<%=totalReqStatSummaryId%>);
		 aaSeriesData.setType("spline");
		 aaSeriesData.setYAxis(2);
		 
		
		 
		 /*
		          name: 'Request-Drops',
	              color: '#4572A7',
	              type: 'spline',
	              yAxis: 1,
	              data: jNumRDDetailedDataArray
		 
		 */
		 var rdSeriesData=new HESISeriesData();
		 rdSeriesData.setName("Request-Drops");
		 rdSeriesData.setColor(RD_color_code_<%=totalReqStatSummaryId%>);
		 rdSeriesData.setType("spline");
		 rdSeriesData.setYAxis(3);
		 
		 
		 
		 // build all four series
		 var ephochTimeDataList=esiDataArray.epochTime;
		 var accessChallengeDataList=esiDataArray.accessChallenge;
		 var accessRejectDataList=esiDataArray.accessReject;
		 var accessAcceptDataList=esiDataArray.accessAccept;
		 var requestDropDataList=esiDataArray.requestDrop;
		 
         $.each(ephochTimeDataList,function(i,epochTime){
        	  
        	  var acPointData=new HESIPointData();
        	  acPointData.setXPoint(epochTime);
        	  acPointData.setYPoints(accessChallengeDataList[i]);
        	  var acPointJSONData=acPointData.buildPointData();
        	  acSeriesData.addPoint(acPointJSONData);
        	  
        	  var arPointData=new HESIPointData();
        	  arPointData.setXPoint(epochTime);
        	  arPointData.setYPoints(accessRejectDataList[i]);
        	  var arPointJSONData=arPointData.buildPointData();
        	  arSeriesData.addPoint(arPointJSONData);
        	  
        	  var aaPointData=new HESIPointData();
        	  aaPointData.setXPoint(epochTime);
        	  aaPointData.setYPoints(accessAcceptDataList[i]);
        	  var aaPointJSONData=aaPointData.buildPointData();
        	  aaSeriesData.addPoint(aaPointJSONData);
        	  
        	  var rdPointData=new HESIPointData();
        	  rdPointData.setXPoint(epochTime);
        	  rdPointData.setYPoints(requestDropDataList[i]);
        	  var rdPointJSONData=rdPointData.buildPointData();
        	  rdSeriesData.addPoint(rdPointJSONData);
         });	 
		
         
         
         esiSeries.addSeries(acSeriesData.buildSeriesData());
         esiSeries.addSeries(arSeriesData.buildSeriesData());
         esiSeries.addSeries(aaSeriesData.buildSeriesData());
         esiSeries.addSeries(rdSeriesData.buildSeriesData());
         
		 return esiSeries.getSeries();
	 },
	 configAllESISeries : function(esiObjId){
		    
 		    var responseData=sendESIDataFetchRequest("","",false);
 		   
			if(responseData != ""){
				
				var data=totalReqStatChartDataConfigHelper.getAllESISeriesData(responseData);
				var esiNavigatorData=data.pointList;
				jAuthESIoptions.navigator.series.data=esiNavigatorData;
				jAuthESIoptions.series=totalReqStatChartDataConfigHelper.configESISeries(responseData);
				jAuthESIoptions.chart.renderTo='ESIReqStatSummaryId_'+esiObjId+'_';
				var chartESIObj = $('#ESIReqStatSummaryId_'+esiObjId+'_').highcharts('StockChart',jAuthESIoptions);// need to check may break functionality
				//chartESIObj.showLoading('Loading data from server...');
				//chartESIObj.hideLoading();
			}
		 
	 },
	 getAllESISeriesData : function(esiDataArray){
		 
		 var ephochTimeDataList=esiDataArray.epochTime;
		 var accessChallengeDataList=esiDataArray.accessChallenge;
		 var accessRejectDataList=esiDataArray.accessReject;
		 var accessAcceptDataList=esiDataArray.accessAccept;
		 var requestDropDataList=esiDataArray.requestDrop;
		 
		 var acSeriesData=new HESISeriesData();
		 
         $.each(ephochTimeDataList,function(i,epochTime){
        	  
        	  var acPointData=new HESIPointData();
        	  acPointData.setXPoint(epochTime);
        	  acPointData.setYPoints(accessChallengeDataList[i]);
        	  acPointData.setYPoints(accessRejectDataList[i]);
        	  acPointData.setYPoints(accessAcceptDataList[i]);
        	  acPointData.setYPoints(requestDropDataList[i]);
        	  var acPointJSONData=acPointData.buildAllPointData();
        	  acSeriesData.addPoint(acPointJSONData);
        	 
         });
         
     
         return acSeriesData;
	 },
	 getESISeriesData : function(xData,yData){
		 
		 var xDataList=xData;
		 var yDataList=yData;
		 
		 var seriesData=new HESISeriesData();
         $.each(xDataList,function(i,xData){
        	  
        	  var pointData=new HESIPointData();
        	  pointData.setXPoint(xData);
        	  pointData.setYPoints(yDataList[i]);
        	  var pointJSONData=pointData.buildPointData();
        	  seriesData.addPoint(pointJSONData);
         });
         return seriesData;
	 }
});

<%-- 
Description :  Data class that is use to convert json data received from websocket  into TotalESIReqChartData  object
--%>


var TotalESIReqChartData = $.Class.extend({
	  init: function(){},
	  setEpochTime:function(epochTime){
		  this.epochTime=epochTime;//number
	  },
	  setESI:function(jESIData){
		  this.esi=jESIData;
	  },
	  setAccessChallenge : function(accessChallenge){
       this.accessChallenge=accessChallenge;		  
	  },
   setAccessAccept : function(accessAccept){
 	this.accessAccept=accessAccept;  
	  },
	  setAccessReject : function(accessReject){
		  this.accessReject=accessReject;
	  },
   setRequestDrop : function(requestDrop){
		  this.requestDrop=requestDrop;
	  },
   setTotalReqDataArray : function(totalReqDataArray){
		  this.totalReqDataArray=totalReqDataArray;
	  }
	});
	
	

<%-- Description :  Instantiate  TotalESIReqChartData and TotalReqStatChartDataConfigHelper class object --%>
var totalESIReqChartData=new TotalESIReqChartData();
var totalReqStatChartDataConfigHelper=new TotalReqStatChartDataConfigHelper();
/* var categories=[];
var data; */

<%--Description : Highchart option configuration for ESI Chart --%>
var jAuthESIoptions=
	{
		chart: {
            zoomType: 'xy',
            renderTo: 'ESIReqStatSummaryId_'+<%=totalReqStatSummaryId%>+'_',
     		events : {
                load: function(event) {
             		
                }
     		},
     		animation : false
        },
        legend: {
            enabled: true,
            layout: 'horizontal',
            verticalAlign: 'bottom',
            shadow: true
        },
       navigator : {			
			enabled : true,
			adaptToUpdatedData : false,
			series :{
				dataGrouping:{
					smoothed : true
				},
				marker :{
					enabled : false
				},
				shadow : false
			}
		},
	scrollbar : {
		liveRedraw: false,
		enabled :true
	},
	rangeSelector : {
		selected :1,
		inputEnabled : false,
		buttonSpacing : 5,
		buttons : [ {
			type : 'minute',
			count : 30,
			text : '30m'
		}, {
			type : 'minute',
			count : 60,
			text : '1H'
		}, {
			type : 'minute',
			count : 720,
			text : '12H'
		}, {
			type : 'day',
			count : 1,
			text : '1D'
		}, {
			type : 'week',
			count : 1,
			text : '1W'
		}, {
			type : 'month',
			count : 1,
			text : '1M'
		}, {
			type : 'all',
			text : 'All'
		} ]
	},
	title : {
		text : null
	},
	xAxis : {
		type : 'datetime',
		dateTimeLabelFormats : {
			second : '%Y-%m-%d<br/>%H:%M:%S',
			minute : '%Y-%m-%d<br/>%H:%M',
			hour : '%Y-%m-%d<br/>%H:%M',
			day : '%Y<br/>%m-%d',
			week : '%Y<br/>%m-%d',
			month : '%Y-%m',
			year : '%Y'
		}
	},
	yAxis : [ { 
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : AC_color_code_<%=totalReqStatSummaryId%>
			}
		},
		title : {
			text : 'Accept-Challenge',
			style : {
				color : AC_color_code_<%=totalReqStatSummaryId%>
			}
		},
		opposite : true

	}, { 
		gridLineWidth : 0,
		title : {
			text : 'Accept-Reject',
			style : {
				color : AR_color_code_<%=totalReqStatSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : AR_color_code_<%=totalReqStatSummaryId%>
			}
		},
		opposite : true
	}, { 
		gridLineWidth : 0,
		title : {
			text : 'Access-Accept',
			style : {
				color : AA_color_code_<%=totalReqStatSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : AA_color_code_<%=totalReqStatSummaryId%>
			}
		},
		opposite : false
	}, { 
		title : {
			text : 'Request-Drops',
			style : {
				color : RD_color_code_<%=totalReqStatSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : RD_color_code_<%=totalReqStatSummaryId%>
			}
		},
		opposite : true
	} ],
	plotOptions : {
		dataLabels : {
			enabled : true,
			color : colors[0],
			style : {
				fontWeight : 'bold'
			},
			formatter : function() {
				return this.y + '%';
			}
		}
	},
	tooltip : {
		shared : true
	},
	series : [],
	exporting : {
		    enabled: true
		}    
};
	
<%--Description : Highchart option configuration for Total Request Statistic summary Chart --%>	
var jAuthStackedColumnoptions=
      {
	       	chart: {
			 		type: 'column',
		 		marginBottom : 80,
        		zoomType: 'xy',
        		renderTo: 'totalReqStatSummaryId_'+'<%=totalReqStatSummaryId%>'+'_',
        		events : {
                   load: function(event) {
                   	hideProgressBar();
                   }
        		}
			},
			title : {
			    text : null
			},
			xAxis: {
			    categories:[],//set esi names
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
			    shared: true
			},
			plotOptions: {
				series: {
	                cursor: 'pointer',
	                point: {
	                    events: {
	                        click: function(event) {
	                        	/* // esi-click  */
	                        	e = event || window.event;
	                        	var target = e.target || e.srcElement;
	                        	var container=target.series.chart.container;
	                        	var esiDivId=$(container).parent().attr('id');
	                      	 
	                        	var esiObjId=esiDivId.match(/\d+/);
	                        	
	                        	showESIChart(esiObjId);
	                        	esiName_<%=totalReqStatSummaryId%>=totalESIReqChartData.esi[this.x];
	                        	
	                        	jAuthESIoptions.chart.renderTo='ESIReqStatSummaryId_'+esiObjId+'_';
	                        	
	                        	var chart = new Highcharts.StockChart(jAuthESIoptions);
	                        	chart.showLoading('Loading data from server...');
	                        	$('#backbtnAuth'+esiObjId).show();
	                        	totalReqStatChartDataConfigHelper.configAllESISeries(esiObjId);/* // method call to set data to esi navigator */
	                        	$('#ESIReqStatSummaryId_'+esiObjId+'_').show();
	                        	$('#totalReqStatSummaryId_'+esiObjId+'_').hide();
	                        	
	                        }
	                    }
	                }
	        	},
			    column: {
			    	stacking: 'normal',
	                cursor: 'pointer',
	                point: {
	                    events: {
	                        click: function() {
	                        }
	                    }
	                },
	                dataLabels: {
	                	 enabled: false,
	                     color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
	                }
	            }
			    
			},
			series: [],
			exporting : {
				enabled : true
			}
			
};
<%-- Description :  This method is called to process and render data to chart received by websocket for Total Request Statistics Summary Chart --%> 
function updateTotalReqStatSummaryData(data,esiId){
    if(data != null){
    	
    	
    	var configObj_<%=totalReqStatSummaryId%> = getWidgetConfiguration(<%=totalReqStatSummaryId%>);  <%-- Use ConfObj to retrive configuration --%>
    	var esiServerIdsList_<%=totalReqStatSummaryId%> = "";
    	
    	if(typeof configObj_<%=totalReqStatSummaryId%> != 'undefined'){
    		esiServerIdsList_<%=totalReqStatSummaryId%> = configObj_<%=totalReqStatSummaryId%>.get("ELITEAAAINSTANCES");
    	}
    	
    	if(esiServerIdsList_<%=totalReqStatSummaryId%>){
    		var esiServerArrays_<%=totalReqStatSummaryId%> = esiServerIdsList_<%=totalReqStatSummaryId%>.split(',');
        	var filterData = data ;
        	var foundIndex = 0;
        	var esiServerIndexArray = [];
        	var dataAccessAccept = [];
        	var dataAccessReject = [];
        	var dataEpochTime = [];
        	var dataESI = [];
        	var dataRequestDrop = [];
        	var dataAccessChallenge = [];
        	
        	if(esiServerArrays_<%=totalReqStatSummaryId%>){
        		
        		$.each(esiServerArrays_<%=totalReqStatSummaryId%>, function( filterKey, filterValue ) {
            		if(filterValue){
            			var foundIndex = 0,isIndexFound = false;
            			$.each( data.esi, function( key, value ) {
            	    		if(filterValue == value){
            	    			var foundIndex = key;
            	    			esiServerIndexArray.push(foundIndex);
            	    			dataAccessAccept.push(data.accessAccept[foundIndex]);
            	    			dataAccessChallenge.push(data.accessChallenge[foundIndex]);
            	    			dataAccessReject.push(data.accessReject[foundIndex]);
            	    			dataEpochTime.push(data.epochTime[foundIndex]);
            	    			dataESI.push(data.esi[foundIndex]);
            	    			dataRequestDrop.push(data.requestDrop[foundIndex]);
            	    			
            	    			isIndexFound = true;
            	    		}
            	    	});
            		}
            	});
            	
        		data.esi = dataESI;
            	data.accessAccept=dataAccessAccept;
            	data.accessChallenge =dataAccessChallenge;
            	data.accessReject=dataAccessReject;
            	data.epochTime=dataEpochTime;
            	data.requestDrop=dataRequestDrop;
        	}
    	}
    	
    	console.log("isInitialize value :  " +isInitializeWidget_<%=totalReqStatSummaryId%>);
    	if(isInitializeWidget_<%=totalReqStatSummaryId%>){
    		/* //totalESIReqChartData=new TotalESIReqChartData(); */
    	    totalESIReqChartData.setTotalReqDataArray(data.totalReqDataArray);
    	    totalESIReqChartData.setESI(data.esi);
    		totalESIReqChartData.setAccessChallenge(data.accessChallenge);
    		totalESIReqChartData.setAccessReject(data.accessReject);
    		totalESIReqChartData.setAccessAccept(data.accessAccept);
    		totalESIReqChartData.setRequestDrop(data.requestDrop);
    		totalESIReqChartData.setEpochTime(data.epochTime);
    		 
    		/* 1. build configESIStatisticsSeries */
            jAuthStackedColumnoptions.series=totalReqStatChartDataConfigHelper.configESIStatisticsSeries(totalESIReqChartData);
            jAuthStackedColumnoptions.chart.renderTo='totalReqStatSummaryId_'+esiId+'_';
            
            var stackChart = new Highcharts.Chart(jAuthStackedColumnoptions);
            
            
            $('#progressbarDIV'+esiId).hide();
            
            /* 2. build configESIStatisticsCategories */
            var esiStatCategories=totalReqStatChartDataConfigHelper.configESIStatisticsCategories(totalESIReqChartData);
            
            stackChart.xAxis[0].setCategories(esiStatCategories);
            
            isInitializeWidget_<%=totalReqStatSummaryId%> = false;
    	}else{
    		 var esiChart = $('#totalReqStatSummaryId_'+esiId+'_').highcharts();
             
    		 totalESIReqChartData.setTotalReqDataArray(data.totalReqDataArray);
     	     totalESIReqChartData.setESI(data.esi);
     		 totalESIReqChartData.setAccessChallenge(data.accessChallenge);
     		 totalESIReqChartData.setAccessReject(data.accessReject);
     		 totalESIReqChartData.setAccessAccept(data.accessAccept);
     		 totalESIReqChartData.setRequestDrop(data.requestDrop);
     		 totalESIReqChartData.setEpochTime(data.epochTime);
    		 
     		if ($('#totalReqStatSummaryId_'+esiId+'_').find('#errorClassDiv').length ) {
     			$('#totalReqStatSummaryId_'+esiId+'_').find('#errorClassDiv').hide(); 
     			$('#'+$('#currentTabId').val()).find($('div#'+esiId)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/TotalRequestStatsSummary.jsp?widgetId="+esiId);
     		}
     		 
    		//update chart data..
     		 var chartArray = totalReqStatChartDataConfigHelper.configESIStatisticsSeries(totalESIReqChartData);
    		
    		 var yIndex=0,dataIndex = 0;
			 var challengeIndex = 0,rejectIndex = 1,accessAcceptIndex = 2,dropIndex =3;    	
    		 var retriveIndex=0;
			 $.each( chartArray, function( key, value ) {
    			 if(value.name == 'Accept-Challenge'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Accept-Challenge : "+ data);
    					 esiChart.series[challengeIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Accept-Reject'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Accept-Reject : "+ data);
    					 esiChart.series[rejectIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Access-Accept'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Access-Accept : "+ data);
    					 esiChart.series[accessAcceptIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				
    			 }else if(value.name == 'Request-Drops'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Request-Drops : "+ data);
    					 esiChart.series[dropIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }
    		  });
    		 
    		 $('#progressbarDIV'+esiId).hide();
             
    		 //set inner chart data
             var responseData=sendESIDataFetchRequest("","",false);
   		   
 		     if(responseData != ""){
 				
 				var data=totalReqStatChartDataConfigHelper.getAllESISeriesData(responseData);
  				var esiNavigatorData=data.pointList;
  				jAuthESIoptions.navigator.series.data=esiNavigatorData;
  				jAuthESIoptions.series=totalReqStatChartDataConfigHelper.configESISeries(responseData);
  				var configSeriesData = totalReqStatChartDataConfigHelper.configESISeries(responseData);
  				 				
				var esiSubChart = $('#ESIReqStatSummaryId_'+esiId+'_').highcharts();
  				
				var subChallengeIndex = 0,subRejectIndex = 1,subAccessAcceptIndex = 2,subDropIndex =3;    	
				 $.each( configSeriesData, function( key, value ) {
					 if(value.name == 'Accept-Challenge'){
						 var data=value.data;
						 
						 var cIndex = 0;
						 $.each(data, function(dataKey,dataValue){
							 console.log("SubChart : Accept-Challenge : "+ data);
							 esiSubChart.series[subChallengeIndex].data[cIndex].update(data[dataKey]);
							 cIndex++;
						 });
						 
					 }else if(value.name == 'Accept-Reject'){
						 var data=value.data;
						 
						 var cIndex = 0;
						 $.each(data, function(dataKey,dataValue){
							 console.log("SubChart : Accept-Reject : "+ data);
							 esiSubChart.series[subRejectIndex].data[cIndex].update(data[dataKey]);
							 cIndex++;
						 });
						 
					 }else if(value.name == 'Access-Accept'){
						 var data=value.data;
						 
						 var cIndex = 0;
						 $.each(data, function(dataKey,dataValue){
							 console.log("SubChart : Access-Accept : "+ data);
							 esiSubChart.series[subAccessAcceptIndex].data[cIndex].update(data[dataKey]);
							 cIndex++;
						 });
						
					 }else if(value.name == 'Request-Drops'){
						 var data=value.data;
						 
						 var cIndex = 0;
						 $.each(data, function(dataKey,dataValue){
							 console.log("SubChart : Request-Drops : "+ data);
							 esiSubChart.series[subDropIndex].data[cIndex].update(data[dataKey]);
							 cIndex++;
						 });
						 
					 }
				  });
  			
 			}
    	}
    }  
}

function flushDiv(){
	$('#totalReqStatSummaryId_'+<%=totalReqStatSummaryId%>+'_').empty();
}
<%--Use Below method to Stop Progress bar--%>
function hideProgressBar(){
	$('#progressbarDIV'+<%=totalReqStatSummaryId%>).hide();
	$('#exportingdiv'+<%=totalReqStatSummaryId%>).show();
}

function hideESIChart(esiObjId){
	$('#ESIReqStatSummaryId_'+esiObjId+'_').hide();
	$('#totalReqStatSummaryId_'+esiObjId+'_').show();		
}

function showESIChart(esiObjId){
	$('#ESIReqStatSummaryId_'+esiObjId+'_').show();
	$('#totalReqStatSummaryId_'+esiObjId+'_').hide();
}

function getESIData(startTimeValue,endTimeValue){
	
	var responseData=sendESIDataFetchRequest(startTimeValue,endTimeValue,false);
	var series="";
	if(responseData != ""){
		
		var series0=totalReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.accessChallenge);
		var series1=totalReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.accessReject);
		var series2=totalReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.accessAccept);
		var series3=totalReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.requestDrop);
   	    var chart1 = $('#ESIReqStatSummaryId_'+<%=totalReqStatSummaryId%>+'_').highcharts();
   	    chart1.series[0].setData(series0.pointList);
   	    chart1.series[1].setData(series1.pointList);
   	    chart1.series[2].setData(series2.pointList);
	    chart1.series[3].setData(series3.pointList);
		chart1.hideLoading();
	
	}
}

function sendESIDataFetchRequest(startTimeValue,endTimeValue,isAsync){
    var jResponseData="";
	$.ajax({
		url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getESIData',
	    type:'GET',
	    async:isAsync,
	    data:{esiName:esiName_<%=totalReqStatSummaryId%>,startTimeValue:startTimeValue,endTimeValue:endTimeValue},
	    success: function(esiDataList){
	    	if(esiDataList != null)
	    	 jResponseData=esiDataList;  	
	    }
	}); 
	
	return jResponseData;
}

<%--DO NOT REMOVE : Get widget Configuration here --%>
var totalReqStatSummaryId=<%=totalReqStatSummaryId%>;

var confObj = getWidgetConfiguration(totalReqStatSummaryId);  <%-- Use ConfObj to retrive configuration --%>
var internval=1;
var esiServerIds =""; 
var radAuthESIName = "";
if(typeof confObj != 'undefined'){
	interval=confObj.get("REFRESHINTERVAL");
	daysPreviously=confObj.get("DAYSPREVIOUSLY");
	esiServerIds = confObj.get("ELITEAAAINSTANCES");
	radAuthESIName= confObj.get("NAME");
}
console.log("Total Request Stat Summary : interval value is "+interval);
console.log('AAA Ids : ' +esiServerIds);
var authId='totalReqStatSummaryId_'+totalReqStatSummaryId+'_';
	
<%-- chart data fetch logic--%>

var dataAuth = {
		header : {
			id : 'totalReqStatSummaryId_'+<%=totalReqStatSummaryId%>+'_',
			type : "RAD_AUTH_ESI_STATISTICS"
		},
		body : {
			interval : interval,
			aaaServerIds:esiServerIds
		}
};
var intervalCounter = 0;
var isInitializeWidget_<%=totalReqStatSummaryId%>= true; 
var widgetHandler  = {
		renderData : function(data) {	
			updateTotalReqStatSummaryData(data,'<%=totalReqStatSummaryId%>');
		},
		updateData : function(data) {
			updateTotalReqStatSummaryData(data,'<%=totalReqStatSummaryId%>');
		}
};
getDashBoardSocket().register(new Widget(authId, widgetHandler));
getDashBoardSocket().sendRequest(dataAuth);

function freezeAuthWidget(id)
{
	console.log("freeze call start");
	resetTimer();
	/* //do clear interval */
	
		var data = {
				header : {
					id : 'totalReqStatSummaryId_'+<%=totalReqStatSummaryId%>+'_',
					type : "RAD_AUTH_ESI_STATISTICS"
				},
				body : {
					freeze : true
				}
		};
	
	getDashBoardSocket().sendRequest(data);
	eliteTimer.resetCountdown();
	eliteTimer.Timer.play();
	unFreezeTimer_<%=totalReqStatSummaryId%>=setTimeout(unFreeze,jTimerValue_<%=totalReqStatSummaryId%>);
	
}

function unfreezeAuthWidget(id)
{
	console.log("unfreeze call start");
	
	var data = {
			header : {
				id : 'totalReqStatSummaryId_'+<%=totalReqStatSummaryId%>+'_',
				type : "RAD_AUTH_ESI_STATISTICS"
			},
			body : {
				freeze : false
			}
	};
	
	getDashBoardSocket().sendRequest(data);
	resetTimer();
}
function resetTimer(){
	
    console.log("resetTimer call start");
	if(typeof unFreezeTimer_<%=totalReqStatSummaryId%> != 'undefined'){
		clearInterval(unFreezeTimer_<%=totalReqStatSummaryId%>);
	}
	
	eliteTimer.Timer.pause();
	$('#freezeBtnAuth'+<%=totalReqStatSummaryId%>).attr('value', 'Freeze');
}
	
$('.freezeBtnAuth').click(function(){
	var objId=$(this).attr("id");
	var esiObjId=objId.match(/\d+/);
	freezeAuthWidget(esiObjId);
});

$('.unfreezeBtnAuth').click(function(){
	var objId=$(this).attr("id");
	var esiObjId=objId.match(/\d+/);
	unfreezeAuthWidget(esiObjId);
	$('#freezeBtnAuth'+esiObjId).attr('value', 'Freeze');
});
 
$('.backbtnAuth').click(function(){
	isInitializeWidget_<%=totalReqStatSummaryId%> = true;
	
	var objId=$(this).attr("id");
	
	var esiObjId=objId.match(/\d+/);
	
	$('#backbtnAuth'+esiObjId).hide();
	hideESIChart(esiObjId);
	
	$('#totalReqStatSummaryId_<%=totalReqStatSummaryId%>_').css('width','100%');
	$('#totalReqStatSummaryId_<%=totalReqStatSummaryId%>_').css('margin-right','20px');
		
	var parentObj = $('#totalReqStatSummaryId_<%=totalReqStatSummaryId%>_').parent();
	var parentWidth = $(parentObj).width();
		
	$('#totalReqStatSummaryId_<%=totalReqStatSummaryId%>_').width(parentWidth + 'px');
	$('#totalReqStatSummaryId_<%=totalReqStatSummaryId%>_').css('border','none');
		
		
	$('#ESIReqStatSummaryId_<%=totalReqStatSummaryId%>_').css('width','100%');
	$('#ESIReqStatSummaryId_<%=totalReqStatSummaryId%>_').css('margin-right','20px');
		
	var parentESIObj = $('#ESIReqStatSummaryId_<%=totalReqStatSummaryId%>_').parent();
	var parentESIWidth = $(parentObj).width();
		
	$('#ESIReqStatSummaryId_<%=totalReqStatSummaryId%>_').width(parentESIWidth + 'px');
	$('#ESIReqStatSummaryId_<%=totalReqStatSummaryId%>_').css('border','none');
	$('#ESIReqStatSummaryId_'+esiObjId+'_').hide();
});
 
var EXPORT_WIDTH = 700;
var canvas;
var svg;
function save_chart(chart) {
	
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
        
		var chartName= radAuthESIName;
		
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
	    doc.rect(35, 25, 140, 5, 'F');
	    
	    doc.setDrawColor(0);
	    doc.setFillColor(1,81,152);
	    doc.rect(35, 122, 140, 5, 'F'); 
	    
	    doc.setFontSize(7.5);
	    doc.setTextColor(255);  
	    
	    doc.text(82, 28.5, chartName + " Report");
	    doc.text(79, 125.5, chartName + " Details");
	    
	    //ESI Name - Rectangle
	    doc.setDrawColor(0);
	    doc.setFillColor(217,230,246);
	    doc.rect(40, 130, 130, 4.2, 'F');
	    
	    
	    doc.setFontSize(6.5);
	    doc.setTextColor(0); 
	    doc.setFontType("bold");
	    doc.text(46, 133, "ESI Name ");
	    
	    doc.text(66, 133, "IP Address ");
	    
	    doc.text(86, 133, "Access-Challenge");
	    
	    doc.text(110, 133, "Access-Reject ");
	    
	    doc.text(130, 133, "Access-Accept");
	    
	    doc.text(150, 133, "Request-Drops");
	    
	    
	    var radESIArray = getRadESISummaryDataDetails();
	  
	    doc.setTextColor(0);  
	    doc.setFontType("normal");
	    var flag=1;
	    
	    var yIndex=140;
	    
	    $.each(radESIArray.esi,function(key,value){
	    	if (yIndex >= pageHeight-10)
	    	{
	    		doc.addPage('a4','p');
		    	yIndex = 10; // Restart height position
	    		pageWidth= doc.internal.pageSize.width;
	    		pageHeight= doc.internal.pageSize.height;
	    	    pageIndex++;
	    	    doc.text(pageWidth-20,pageHeight - 5, "Page : "+pageIndex );
	            doc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);
	    	 
	    	}    	
	    	doc.text(46, yIndex-2, value);
	    	doc.text(66, yIndex-2, radESIArray.serverAddress[key].toString() + ':'+radESIArray.serverPort[key].toString());
	    	
	    	var challengeLength = radESIArray.accessChallenge[key].toString().length;
	    	
	    	var charWidth = 1.25;
			var str = reverse(radESIArray.accessChallenge[key].toString());
			for(var i = challengeLength-1 ; i >= 0 ; i--){			  
				doc.text(104.5- charWidth * i, yIndex-2, str[i]);
			}
	    	
	    	var accessRejectLength = radESIArray.accessReject[key].toString().length;
	    	str = reverse(radESIArray.accessReject[key].toString());
			for(var i = accessRejectLength-1 ; i >= 0 ; i--){			  
				doc.text(124.5- charWidth * i, yIndex-2, str[i]);
			}
	    	
	    	var accessAcceptLength = radESIArray.accessAccept[key].toString().length;
	    	str = reverse(radESIArray.accessAccept[key].toString());
			for(var i = accessAcceptLength-1 ; i >= 0 ; i--){			  
				doc.text(145.25- charWidth * i, yIndex-2, str[i]);
			}
	    	
	    	var requestDropLength = radESIArray.requestDrop[key].toString().length;
	    	str = reverse(radESIArray.requestDrop[key].toString());
			for(var i = requestDropLength-1 ; i >= 0 ; i--){			  
				doc.text(165- charWidth * i, yIndex-2, str[i]);
			}

	    	doc.setDrawColor(192,192,192); 
	    	doc.line(40, yIndex, 170, yIndex);
	    	if(flag == 1){
	    		flag = 0;
	    		doc.setFillColor(1,81,152);
	    	}else{
	    		flag = 1;
	    		doc.setFillColor(255,255,172);
	    	}
	    	yIndex=yIndex+5;
	   }); 
	    
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
 $('#exportbtn<%=totalReqStatSummaryId%>').click(function(){
	 save_chart($('#totalReqStatSummaryId_<%=totalReqStatSummaryId%>_').highcharts()); 
 });
 
 $('#exportbtn_'+<%=totalReqStatSummaryId%>).click(function(){
	 save_chart($('#totalReqStatSummaryId_<%=totalReqStatSummaryId%>_').highcharts()); 
 });
 
 function reverse(s){
	    return s.split("").reverse().join("");
 }
 
 function getRadESISummaryDataDetails(){
	    var jResponseData="";
		$.ajax({
			url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getRadAuthESIData',
		    type:'GET',
		    async:false,
		    data:{serverIds:esiServerIds},
		    success: function(esiDataList){
		    	if(esiDataList != null)
		    	 jResponseData=esiDataList;  	
		    }
		}); 
		return jResponseData;
	}

	function getBase64Image(imgUrl) {
	    var image1 = new Image();
	    var canvas1 = document.createElement("canvas");
	    var context1 = canvas1.getContext('2d');
	    image1.src = imgUrl;
	    canvas1.height=77;
	    canvas1.width=213;
	    context1.drawImage(image1,0,0,213,77);
	    return canvas1.toDataURL('image/jpeg');
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
<body >
<div id="parent_div" class="parent_div_auth" style="border-color: rgb(192, 192, 192);border-style: solid;border-width: 1px;">
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditTotalRequestStatsWidget.jsp?widgetId=<%=totalReqStatSummaryId%>" id="editJsp">
 
  <div id="exportingdiv<%=totalReqStatSummaryId%>" style="width: 100%;display: table;background-position: center;vertical-align: middle;text-align:right;padding-top:10px;" align="center">
    <input type="button"  id="backbtnAuth<%=totalReqStatSummaryId%>"  name="button" value="Back" class="restart-btn backbtnAuth" title="Back" align="right" style="display: none;"/>
 	<input type="button"  id="freezeBtnAuth<%=totalReqStatSummaryId%>" name="button" value="Freeze" class="restart-btn freezeBtnAuth" title="Freeze" align="right"/> 
 	<input type="button"  id="unfreezeBtnAuth<%=totalReqStatSummaryId%>" name="button" value="Unfreeze" class="restart-btn unfreezeBtnAuth" title="Unfreeze" align="right"/>
 	<input type="button"  id="exportbtn<%=totalReqStatSummaryId%>" name="button" value="Export.." class="restart-btn exportbtn" title="Unfreeze" align="right" style="margin-right: 20px;'"/>
 </div> 
 
 <%--ProgressBar Div : Start  --%> 
 <div id="progressbarDIV<%=totalReqStatSummaryId%>" class="progressbar" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>
<div id="totalReqStatSummaryId_<%=totalReqStatSummaryId%>_"  class="widget-class auth-esi-request-chart" style="height: inherit;width: inherit;display: block;background-position: center;vertical-align: middle;text-align:center;" align="center">
 </div>
 <div id="ESIReqStatSummaryId_<%=totalReqStatSummaryId%>_" class="widget-class auth-esi-chart"  style="height: inherit;width: inherit;display: block;background-position: center;vertical-align: middle;text-align:center;margin-right:10px;" align="center">
 </div>
 <span id="exportbtn_<%=totalReqStatSummaryId%>"></span>
 <%--
	 <div id="countdown" style="height: 20px;width: 100%;display: block;background-position: right;vertical-align: bottom;text-align:right;" align="right">
	 </div>
  --%>
 <%--ProgressBar Div : End  --%>
 </div>
</html>