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
	<%String acctESIRequestSummaryId = request.getParameter("widgetId");%>

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

var jTimerValue_<%=acctESIRequestSummaryId%>=300000;
var unFreezeTimer_<%=acctESIRequestSummaryId%>;

<%-- Timer code --%>

var eliteAcctESITimer = new (function() {

    var $acctCountDown1;
    var incrementAcctTime = 70;
    var currentAcctTime = jTimerValue_<%=acctESIRequestSummaryId%>; // 5 minutes (in milliseconds)

    function updateAcctTimer() {

        /*  Output timer position */
        var timeAcctString = formatAcctTime(currentAcctTime);
        
        /* $countdown.html(timeAcctString);	 */        
        $acctCountDown1.attr('value', timeAcctString);

        /*  If timer is complete, trigger alert */
        if (currentAcctTime == 0) {
        	eliteAcctESITimer.Timer.stop();
            /* reset counter division */
            /* $acctCountDown1.attr('value', timeAcctString); */
            console.log("updateAcctTimer --->methodcall ::");
            <%-- $('#freezeAcctBtn'+<%=acctESIRequestSummaryId%>).attr('value', 'Freeze'); --%>
            eliteAcctESITimer.resetAcctCountdown();
            return;
        }

        // Increment timer position
        currentAcctTime -= incrementAcctTime;
        if (currentAcctTime < 0) currentAcctTime = 0;

    }

    this.resetAcctCountdown = function() {

    /*     Get time from form
        var newTime = parseInt($form.find('input[type=text]').val()) * 1000;
        if (newTime > 0) {currentAcctTime = newTime;} */

       /*  Stop and reset timer */
        currentAcctTime = jTimerValue_<%=acctESIRequestSummaryId%>;
        eliteAcctESITimer.Timer.stop().once();

    };
    
    this.init = function() {
	    $acctCountDown1 = $('#freezeAcctBtn'+<%=acctESIRequestSummaryId%>);
	  /*   $countdown = $('#countdown'); */
        eliteAcctESITimer.Timer = $.timer(updateAcctTimer, incrementAcctTime, false);
        currentAcctTime = jTimerValue_<%=acctESIRequestSummaryId%>;
   };
});
	eliteAcctESITimer.init();

	function padAcct(number, length){
	    var str = '' + number;
	    while (str.length < length) {str = '0' + str;}
	    return str;
	}
	
	function formatAcctTime(time) {
		time = time / 10;
	    var min = parseInt(time / 6000),
	        sec = parseInt(time / 100) - (min * 60),
	        hundredths = padAcct(time - (sec * 100) - (min * 6000), 2);
	    return (min > 0 ? padAcct(min, 2) : "00") + "m " + padAcct(sec, 2) + "s "; 	}


	<%-- End Timer code --%>
	
	/* todo ec need to generalize it  */
	var colors = Highcharts.getOptions().colors;
	<%--
	var AC_color_code='#4572A7';
	var AR_color_code='#062173';
	var AA_color_code='#89A54E';
	var RD_color_code='#AA4643';
	--%>
	var AC_color_code_<%=acctESIRequestSummaryId%>=colors[0];
	var AR_color_code_<%=acctESIRequestSummaryId%>=colors[5];
	var AA_color_code_<%=acctESIRequestSummaryId%>=colors[2];
	var RD_color_code_<%=acctESIRequestSummaryId%>=colors[3];
	
	var esiName_<%=acctESIRequestSummaryId%>='';
	var daysPreviously_<%=acctESIRequestSummaryId%>=0;

	/* Range selector array define millinsecond of each rangeselector i.e.  [30min,1Month,1Week,1Month,All] which is used to calculate endtime  */
	var rangeSelectorArray_<%=acctESIRequestSummaryId%>=[1800000,3600000,43200000,86400000,604800000,111600000,-1];

	<%-- Description :  Chart Data wrapper classes for Total Request Statistics summary--%>
	
	
	var HAcctESIStatisticsCategories=HCategoriesData.extend({
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
	
	var HAcctESIStatisticsPointData=HPointData.extend({
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
	
	var HAcctESIStatisticsSeriesData=HSeriesData.extend({
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
	
	
	var HAcctESIStatisticsSeries=HSeries.extend({
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
	var HAcctESICategories=HCategoriesData.extend({
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
	var HAcctESIPointData=HPointData.extend({
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

	var HAcctESISeriesData=HSeriesData.extend({
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


	var HAcctESISeries=HSeries.extend({
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
var TotalAcctReqStatChartDataConfigHelper=$.Class.extend({
	 init: function(){
		 
	 },
	 configAcctESIStatisticsSeries : function(totalAcctESIReqChartData){
		 var esiStatSeries =new HAcctESIStatisticsSeries();
		 
		 var acSeries=new HAcctESIStatisticsSeriesData();
		 acSeries.setPointList(totalAcctESIReqChartData.accountingReq);
		 acSeries.setName("Server-Request");
		 acSeries.setColor(AC_color_code_<%=acctESIRequestSummaryId%>);
		 var acSeriesData=acSeries.buildSeriesData();
		 esiStatSeries.addSeries(acSeriesData);
		 
		 var arSeries=new HAcctESIStatisticsSeriesData();
		 arSeries.setPointList(totalAcctESIReqChartData.retransmission);
		 arSeries.setName("Retransmission");
		 arSeries.setColor(AR_color_code_<%=acctESIRequestSummaryId%>);
		 var arSeriesData=arSeries.buildSeriesData();
		 esiStatSeries.addSeries(arSeriesData);
		 
		 var aaSeries=new HAcctESIStatisticsSeriesData();
		 aaSeries.setPointList(totalAcctESIReqChartData.accountingRes);
		 aaSeries.setName("Server-Response");
		 aaSeries.setColor(AA_color_code_<%=acctESIRequestSummaryId%>);
		 var aaSeriesData=aaSeries.buildSeriesData();
		 esiStatSeries.addSeries(aaSeriesData);
		 
		 var rrSeries=new HAcctESIStatisticsSeriesData();
		 rrSeries.setPointList(totalAcctESIReqChartData.requestDrop);
		 rrSeries.setName("Request-Drops");
		 rrSeries.setColor(RD_color_code_<%=acctESIRequestSummaryId%>);
		 var rrSeriesData=rrSeries.buildSeriesData();
		 esiStatSeries.addSeries(rrSeriesData);
		 return esiStatSeries.getSeries();
		 
	 },
	 configAcctESIStatisticsCategories : function(totalAcctESIReqChartData1){
		    var esiStatCategories=new HAcctESIStatisticsCategories();
		    esiStatCategories.setCategories(totalAcctESIReqChartData1.esi);
			return esiStatCategories.getCategories();
	 },
	 configESISeries : function(esiDataArray){
		 /* make esiseries object */
		 var esiSeries=new HAcctESISeries();
		 /* make for esi series data object */
		 
		 /*
		   name: 'Accept-Challenge',
	       color: '#4572A7',
	       type: 'column',
	       yAxis: 1,
	       data: jNumACDetailedDataArray
		 */
		 var acSeriesData=new HAcctESISeriesData();
		 acSeriesData.setName("Server-Request");
		 acSeriesData.setColor(AC_color_code_<%=acctESIRequestSummaryId%>);
		 acSeriesData.setType("spline");
		 acSeriesData.setYAxis(0);
		 
		 /*
	              name: 'Accept-Reject',
	              type: 'spline',
	              color: '#AA4643',
	              yAxis: 2,
	              data: jNumARDetailedDataArray
	              
		 */
		 
		 
		 var arSeriesData=new HAcctESISeriesData();
         arSeriesData.setName("Retransmission");
         arSeriesData.setColor(AR_color_code_<%=acctESIRequestSummaryId%>);
         arSeriesData.setType("spline");
         arSeriesData.setYAxis(1);
		 
		 /*
		   name: 'Access-Accept',
	       color: '#89A54E',
	       yAxis: 3,
	       type: 'spline',
	       data: jNumAADetailedDataArray
		 */
		 var aaSeriesData=new HAcctESISeriesData();
		 aaSeriesData.setName("Server-Response");
		 aaSeriesData.setColor(AA_color_code_<%=acctESIRequestSummaryId%>);
		 aaSeriesData.setType("spline");
		 aaSeriesData.setYAxis(2);
		 
		
		 
		 /*
		          name: 'Request-Drops',
	              color: '#4572A7',
	              type: 'spline',
	              yAxis: 1,
	              data: jNumRDDetailedDataArray
		 
		 */
		 var rdSeriesData=new HAcctESISeriesData();
		 rdSeriesData.setName("Request-Drops");
		 rdSeriesData.setColor(RD_color_code_<%=acctESIRequestSummaryId%>);
		 rdSeriesData.setType("spline");
		 rdSeriesData.setYAxis(3);
		 
		 
		 
		 // build all four series
		 var ephochTimeDataList=esiDataArray.epochTime;
		 var accountingReqDataList=esiDataArray.accountingReq;
		 var retransmissionDataList=esiDataArray.retransmission;
		 var accountingResDataList=esiDataArray.accountingRes;
		 var requestDropDataList=esiDataArray.requestDrop;
		 
         $.each(ephochTimeDataList,function(i,epochTime){
        	  
        	  var acPointData=new HAcctESIPointData();
        	  acPointData.setXPoint(epochTime);
        	  acPointData.setYPoints(accountingReqDataList[i]);
        	  var acPointJSONData=acPointData.buildPointData();
        	  acSeriesData.addPoint(acPointJSONData);
        	  
        	  var arPointData=new HAcctESIPointData();
        	  arPointData.setXPoint(epochTime);
        	  arPointData.setYPoints(retransmissionDataList[i]);
        	  var arPointJSONData=arPointData.buildPointData();
        	  arSeriesData.addPoint(arPointJSONData);
        	  
        	  var aaPointData=new HAcctESIPointData();
        	  aaPointData.setXPoint(epochTime);
        	  aaPointData.setYPoints(accountingResDataList[i]);
        	  var aaPointJSONData=aaPointData.buildPointData();
        	  aaSeriesData.addPoint(aaPointJSONData);
        	  
        	  var rdPointData=new HAcctESIPointData();
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
		    
 		    var responseData=sendAcctESIDataFetchRequest("","",false);
 		   
			if(responseData != ""){
				
				var data=totalAcctReqStatChartDataConfigHelper.getAllESISeriesData(responseData);
				var esiNavigatorData=data.pointList;
				jESIoptions.navigator.series.data=esiNavigatorData;
				jESIoptions.series=totalAcctReqStatChartDataConfigHelper.configESISeries(responseData);
				jESIoptions.chart.renderTo='acctESIReqStatSummaryId_'+esiObjId+'_';
				var chart = $('#acctESIReqStatSummaryId_'+esiObjId+'_').highcharts('StockChart',jESIoptions);// need to check may break functionality
				/* chart.showLoading('Loading data from server...');
       		    chart.hideLoading(); */
			}
		 
	 },
	 getAllESISeriesData : function(esiDataArray){
		 
		 var ephochTimeDataList=esiDataArray.epochTime;
		 var accountingReqDataList=esiDataArray.accountingReq;
		 var retransmissionDataList=esiDataArray.retransmission;
		 var accountingResDataList=esiDataArray.accountingRes;
		 var requestDropDataList=esiDataArray.requestDrop;
		 
		 
		 var acSeriesData=new HAcctESISeriesData();
		 
         $.each(ephochTimeDataList,function(i,epochTime){
        	  
        	  var acPointData=new HAcctESIPointData();
        	  acPointData.setXPoint(epochTime);
        	  acPointData.setYPoints(accountingReqDataList[i]);
        	  acPointData.setYPoints(retransmissionDataList[i]);
        	  acPointData.setYPoints(accountingResDataList[i]);
        	  acPointData.setYPoints(requestDropDataList[i]);
        	  var acPointJSONData=acPointData.buildAllPointData();
        	  acSeriesData.addPoint(acPointJSONData);
        	 
         });
         
     
         return acSeriesData;
	 },
	 getESISeriesData : function(xData,yData){
		 
		 var xDataList=xData;
		 var yDataList=yData;
		 
		 var seriesData=new HAcctESISeriesData();
         $.each(xDataList,function(i,xData){
        	  
        	  var pointData=new HAcctESIPointData();
        	  pointData.setXPoint(xData);
        	  pointData.setYPoints(yDataList[i]);
        	  var pointJSONData=pointData.buildPointData();
        	  seriesData.addPoint(pointJSONData);
         });
         return seriesData;
	 }
});

<%-- 
Description :  Data class that is use to convert json data received from websocket  into totalAcctESIReqChartData  object
--%>


var TotalAcctESIReqChartData = $.Class.extend({
	  init: function(){},
	  setEpochTime:function(epochTime){
		  this.epochTime=epochTime;//number
	  },
	  setESI:function(jESIData){
		  this.esi=jESIData;
	  },
	  setAccountingReq : function(accountingReq){
     	  this.accountingReq=accountingReq;		  
	  },
	  setAccountingRes : function(accountingRes){
 		this.accountingRes=accountingRes;  
	  },
	  setRetransmission : function(retransmission){
		  this.retransmission=retransmission;
	  },
	  setRequestDrop : function(requestDrop){
		  this.requestDrop=requestDrop;
	  },
      setTotalReqDataArray : function(totalReqDataArray){
		  this.totalReqDataArray=totalReqDataArray;
	  }
	});
	
	

<%-- Description :  Instantiate  TotalAcctESIReqChartData and TotalReqStatChartDataConfigHelper class object --%>
var totalAcctESIReqChartData=new TotalAcctESIReqChartData();
var totalAcctReqStatChartDataConfigHelper=new TotalAcctReqStatChartDataConfigHelper();
/* var categories=[];
var data; */

<%--Description : Highchart option configuration for ESI Chart --%>
var jESIoptions=
	{
		chart: {
            zoomType: 'xy',
            renderTo: 'acctESIReqStatSummaryId_'+<%=acctESIRequestSummaryId%>+'_',
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
				color : AC_color_code_<%=acctESIRequestSummaryId%>
			}
		},
		title : {
			text : 'Server-Request',
			style : {
				color : AC_color_code_<%=acctESIRequestSummaryId%>
			}
		},
		opposite : true

	}, { 
		gridLineWidth : 0,
		title : {
			text : 'Retransmission',
			style : {
				color : AR_color_code_<%=acctESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : AR_color_code_<%=acctESIRequestSummaryId%>
			}
		},
		opposite : true
	}, { 
		gridLineWidth : 0,
		title : {
			text : 'Server-Response',
			style : {
				color : AA_color_code_<%=acctESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : AA_color_code_<%=acctESIRequestSummaryId%>
			}
		},
		opposite : false
	}, { 
		title : {
			text : 'Request-Drops',
			style : {
				color : RD_color_code_<%=acctESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : RD_color_code_<%=acctESIRequestSummaryId%>
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
var jStackedColumnoptions=
      {
	       	chart: {
			 		type: 'column',
		 		marginBottom : 80,
        		zoomType: 'xy',
        		renderTo: 'acctESIRequestSummaryId_'+'<%=acctESIRequestSummaryId%>'+'_',
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
	                        	esiName_<%=acctESIRequestSummaryId%>=totalAcctESIReqChartData.esi[this.x];
	                        
	                        	jESIoptions.chart.renderTo='acctESIReqStatSummaryId_'+esiObjId+'_';
	                        	
	                        	var chart = new Highcharts.StockChart(jESIoptions);
	                        	chart.showLoading('Loading data from server...');
	                        	$('#backbtn'+esiObjId).show();
	                        	totalAcctReqStatChartDataConfigHelper.configAllESISeries(esiObjId);/* // method call to set data to esi navigator */
	                        	
	                        	$('#acctESIReqStatSummaryId_'+esiObjId+'_').show();
	                        	$('#acctESIRequestSummaryId_'+esiObjId+'_').hide();
	                        	
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
function updateAcctTotalReqStatSummaryData(data,esiId){
    if(data != null){
    	
    	var configAcctObj_<%=acctESIRequestSummaryId%> = getWidgetConfiguration(<%=acctESIRequestSummaryId%>);  <%-- Use ConfObj to retrive configuration --%>
    	var acctEsiServerIdsList_<%=acctESIRequestSummaryId%> = "";
    	    	
    	if(typeof configAcctObj_<%=acctESIRequestSummaryId%> != 'undefined'){
    	 		acctEsiServerIdsList_<%=acctESIRequestSummaryId%> = configAcctObj_<%=acctESIRequestSummaryId%>.get("ELITEAAAINSTANCES");
    	}
    	    	
    	if(acctEsiServerIdsList_<%=acctESIRequestSummaryId%>){
    		var acctEsiServerArrays_<%=acctESIRequestSummaryId%> = acctEsiServerIdsList_<%=acctESIRequestSummaryId%>.split(',');
    		var foundIndex = 0;
    	  	var esiServerIndexArray = [];
            var dataAccountingReq = [];
    		var dataAccountingRes = [];
    	    var dataRetransmission = [];
    	 	var dataESI = [];
    	    var dataAcctRequestDrop = [];
    	    var dataEpochTime = [];    	
    	    
    	    if(acctEsiServerArrays_<%=acctESIRequestSummaryId%>){
    	        		
    	    $.each(acctEsiServerArrays_<%=acctESIRequestSummaryId%>, function( filterKey, filterValue ) {
    	    	if(filterValue){
    	        	var foundIndex = 0,isIndexFound = false;
    	         	$.each( data.esi, function( key, value ) {
    	          		if(filterValue == value){
    	            	    var foundIndex = key;
    	            	 	esiServerIndexArray.push(foundIndex);
    	            	 	dataAccountingReq.push(data.accountingReq[foundIndex]);
    	            	 	dataAccountingRes.push(data.accountingRes[foundIndex]);
    	            	 	dataRetransmission.push(data.retransmission[foundIndex]);
    	                    dataEpochTime.push(data.epochTime[foundIndex]);
    	           	     	dataESI.push(data.esi[foundIndex]);
    	           	        dataAcctRequestDrop.push(data.requestDrop[foundIndex]);
    	            	    			
    	            		isIndexFound = true;
    	             	}
    	        	});
    	      	}
    	   });
    	            	
    	  	data.esi = dataESI;
    	  	data.accountingReq=dataAccountingReq;
    	    data.accountingRes =dataAccountingRes;
    	    data.retransmission=dataRetransmission;
    	    data.epochTime=dataEpochTime;
    	    data.requestDrop=dataAcctRequestDrop;
    	}
      }
    	
    	console.log("isInitialize value :  " +isInitializeWidget_<%=acctESIRequestSummaryId%>);
    	if(isInitializeWidget_<%=acctESIRequestSummaryId%>){
    		/* //totalAcctESIReqChartData=new TotalAcctESIReqChartData(); */
    	    totalAcctESIReqChartData.setTotalReqDataArray(data.totalReqDataArray);
    	    totalAcctESIReqChartData.setESI(data.esi);
    		totalAcctESIReqChartData.setAccountingReq(data.accountingReq);
    		totalAcctESIReqChartData.setAccountingRes(data.accountingRes);
    		totalAcctESIReqChartData.setRetransmission(data.retransmission);
    		totalAcctESIReqChartData.setRequestDrop(data.requestDrop);
    		totalAcctESIReqChartData.setEpochTime(data.epochTime);
    		 
    		/* 1. build configAcctESIStatisticsSeries */
            jStackedColumnoptions.series=totalAcctReqStatChartDataConfigHelper.configAcctESIStatisticsSeries(totalAcctESIReqChartData);
            jStackedColumnoptions.chart.renderTo='acctESIRequestSummaryId_'+esiId+'_';
            
            var chart = new Highcharts.Chart(jStackedColumnoptions);
            
            
            $('#progressbarDIV'+esiId).hide();
            
            /* 2. build configAcctESIStatisticsCategories */
            var esiStatCategories=totalAcctReqStatChartDataConfigHelper.configAcctESIStatisticsCategories(totalAcctESIReqChartData);
            
            chart.xAxis[0].setCategories(esiStatCategories);
            
            isInitializeWidget_<%=acctESIRequestSummaryId%> = false;
    	}else{
    		 var esiAcctChart = $('#acctESIRequestSummaryId_'+esiId+'_').highcharts();
             
    		 totalAcctESIReqChartData.setTotalReqDataArray(data.totalReqDataArray);
     	     totalAcctESIReqChartData.setESI(data.esi);
     		 totalAcctESIReqChartData.setAccountingReq(data.accountingReq);
     		 totalAcctESIReqChartData.setAccountingRes(data.accountingRes);
     		 totalAcctESIReqChartData.setRetransmission(data.retransmission);
     		 totalAcctESIReqChartData.setRequestDrop(data.requestDrop);
     		 totalAcctESIReqChartData.setEpochTime(data.epochTime);
    		 
     		if ($('#acctESIRequestSummaryId_'+esiId+'_').find('#errorClassDiv').length ) {
     			$('#acctESIRequestSummaryId_'+esiId+'_').find('#errorClassDiv').hide(); 
     			$('#'+$('#currentTabId').val()).find($('div#'+esiId)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/RadAcctESIRequestSummary.jsp?widgetId="+esiId);
     		}
     		 
    		//update chart data..
     		 var chartAcctArray = totalAcctReqStatChartDataConfigHelper.configAcctESIStatisticsSeries(totalAcctESIReqChartData);
    		
    		 var yIndex=0,dataIndex = 0;
			 var challengeIndex = 0,rejectIndex = 1,accessAcceptIndex = 2,dropAcctIndex =3;    	
    		 $.each( chartAcctArray, function( key, value ) {
    			 if(value.name == 'Server-Request'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Server-Request : "+ data);
    					 esiAcctChart.series[challengeIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Retransmission'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Retransmission : "+ data);
    					 esiAcctChart.series[rejectIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Server-Response'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Server-Response : "+ data);
    					 esiAcctChart.series[accessAcceptIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Request-Drops'){
    				 var data=value.data;
    				
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Request-Drops : "+ data);
    					 esiAcctChart.series[dropAcctIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    			 }
    		  });
    		 
    		 $('#progressbarDIV'+esiId).hide();
             
    		 //set inner chart data
             var responseData=sendAcctESIDataFetchRequest("","",false);
   		   
 		     if(responseData != ""){
 				
 				var data=totalAcctReqStatChartDataConfigHelper.getAllESISeriesData(responseData);
 				var esiNavigatorData=data.pointList;
 				jESIoptions.navigator.series.data=esiNavigatorData;
 				jESIoptions.series=totalAcctReqStatChartDataConfigHelper.configESISeries(responseData);
 				var configAcctSeriesData = totalReqStatChartDataConfigHelper.configESISeries(responseData);
 				
 				var esiAcctSubChart = $('#acctESIReqStatSummaryId_'+esiId+'_').highcharts();
 		
 				 var challengeAcctIndex = 0,rejectAcctIndex = 1,accessAcctAcceptIndex = 2,dropIndex =3;    	
 	    		 $.each( configAcctSeriesData, function( key, value ) {
 	    			 if(value.name == 'Server-Request'){
 	    				 var data=value.data;
 	    				 
 	    				 var cIndex = 0;
 	    				 $.each(data, function(dataKey,dataValue){
 	    					 console.log("Server-Request : "+ data);
 	    					 esiAcctSubChart.series[challengeAcctIndex].data[cIndex].update(data[dataKey]);
 	    					 cIndex++;
 	    				 });
 	    				 
 	    			 }else if(value.name == 'Retransmission'){
 	    				 var data=value.data;
 	    				 
 	    				 var cIndex = 0;
 	    				 $.each(data, function(dataKey,dataValue){
 	    					 console.log("Retransmission : "+ data);
 	    					 esiAcctSubChart.series[rejectAcctIndex].data[cIndex].update(data[dataKey]);
 	    					 cIndex++;
 	    				 });
 	    				 
 	    			 }else if(value.name == 'Server-Response'){
 	    				 var data=value.data;
 	    				 
 	    				 var cIndex = 0;
 	    				 $.each(data, function(dataKey,dataValue){
 	    					 console.log("Server-Response : "+ data);
 	    					 esiAcctSubChart.series[accessAcctAcceptIndex].data[cIndex].update(data[dataKey]);
 	    					 cIndex++;
 	    				 });
 	    				 
 	    			 }else if(value.name == 'Request-Drops'){
 	    				 var data=value.data;
 	    				
 	    				 var cIndex = 0;
 	    				 $.each(data, function(dataKey,dataValue){
 	    					 console.log("Request-Drops : "+ data);
 	    					 esiAcctSubChart.series[dropIndex].data[cIndex].update(data[dataKey]);
 	    					 cIndex++;
 	    				 });
 	    			 }
 	    		  });
 	    		 
 			}
    	}
    }  
}

function flushDiv(){
	$('#acctESIRequestSummaryId_'+<%=acctESIRequestSummaryId%>+'_').empty();
}
<%--Use Below method to Stop Progress bar--%>
function hideProgressBar(){
	$('#progressbarDIV'+<%=acctESIRequestSummaryId%>).hide();
	$('#exportingdiv'+<%=acctESIRequestSummaryId%>).show();
}

function hideESIChart(esiObjId){
	$('#acctESIReqStatSummaryId_'+esiObjId+'_').hide();
	$('#acctESIRequestSummaryId_'+esiObjId+'_').show();		
}

function showESIChart(esiObjId){
	$('#acctESIReqStatSummaryId_'+esiObjId+'_').show();
	$('#acctESIRequestSummaryId_'+esiObjId+'_').hide();
}

function getAcctESIData(startTimeValue,endTimeValue){
	
	var responseData=sendAcctESIDataFetchRequest(startTimeValue,endTimeValue,false);
	var series="";
	if(responseData != ""){
		
		var series0=totalAcctReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.accessChallenge);
		var series1=totalAcctReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.accessReject);
		var series2=totalAcctReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.accessAccept);
		var series3=totalAcctReqStatChartDataConfigHelper.getESISeriesData(responseData.epochTime,responseData.requestDrop);
   	    var chart1 = $('#acctESIReqStatSummaryId_'+<%=acctESIRequestSummaryId%>+'_').highcharts();
   	    chart1.series[0].setData(series0.pointList);
   	    chart1.series[1].setData(series1.pointList);
   	    chart1.series[2].setData(series2.pointList);
	    chart1.series[3].setData(series3.pointList);
		chart1.hideLoading();
	
	}
}

function sendAcctESIDataFetchRequest(startTimeValue,endTimeValue,isAsync){
    var jResponseData="";
	$.ajax({
		url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getAcctESIData',
	    type:'GET',
	    async:isAsync,
	    data:{esiName:esiName_<%=acctESIRequestSummaryId%>,startTimeValue:startTimeValue,endTimeValue:endTimeValue},
	    success: function(esiDataList){
	    	if(esiDataList != null)
	    	 jResponseData=esiDataList;  	
	    }
	}); 
	
	return jResponseData;
}

<%--DO NOT REMOVE : Get widget Configuration here --%>
var acctESIRequestSummaryId=<%=acctESIRequestSummaryId%>;

var confObj = getWidgetConfiguration('<%=acctESIRequestSummaryId%>');  <%-- Use ConfObj to retrive configuration --%>
var internval=1;
var acctServerIds =""; 
var acctServerName = "";
if(typeof confObj != 'undefined'){
	interval=confObj.get("REFRESHINTERVAL");
	daysPreviously=confObj.get("DAYSPREVIOUSLY");
	acctServerIds = confObj.get("ELITEAAAINSTANCES");
	acctServerName=confObj.get("NAME");
}

console.log("Srevere Ids :  " +acctServerIds);
console.log("Total Request Stat Summary : interval value is "+interval);

var id='acctESIRequestSummaryId_'+acctESIRequestSummaryId+'_';
	
<%-- chart data fetch logic--%>

var data = {
		header : {
			id : 'acctESIRequestSummaryId_'+<%=acctESIRequestSummaryId%>+'_',
			type : "RAD_ACCT_ESI_STATISTICS"
		},
		body : {
			interval : interval,
			aaaServerIds:acctServerIds
		}
};
var intervalCounter = 0;
var isInitializeWidget_<%=acctESIRequestSummaryId%>= true; 
var widgetHandler  = {
		renderData : function(data) {	
			updateAcctTotalReqStatSummaryData(data,'<%=acctESIRequestSummaryId%>');
		},
		updateData : function(data) {
			updateAcctTotalReqStatSummaryData(data,'<%=acctESIRequestSummaryId%>');
		}
};
getDashBoardSocket().register(new Widget(id, widgetHandler));
getDashBoardSocket().sendRequest(data);

function freezeTimer(id)
{
	console.log("freeze call start");
	resetAcctTimer();
	/* //do clear interval */
	
		var data = {
				header : {
					id : 'acctESIRequestSummaryId_'+<%=acctESIRequestSummaryId%>+'_',
					type : "RAD_ACCT_ESI_STATISTICS"
				},
				body : {
					freeze : true
				}
		};
	
	getDashBoardSocket().sendRequest(data);
	eliteAcctESITimer.resetAcctCountdown();
	eliteAcctESITimer.Timer.play();
	unFreezeTimer_<%=acctESIRequestSummaryId%>=setTimeout(unFreeze,jTimerValue_<%=acctESIRequestSummaryId%>);
	
}

function unfreezeTimer(id)
{
	console.log("unfreeze call start");
	
	var dataAcct = {
			header : {
				id : 'acctESIRequestSummaryId_'+<%=acctESIRequestSummaryId%>+'_',
				type : "RAD_ACCT_ESI_STATISTICS"
			},
			body : {
				freeze : false
			}
	};
	
	getDashBoardSocket().sendRequest(dataAcct);
	resetAcctTimer();
}
function resetAcctTimer(){
	
    console.log("resetAcctTimer call start");
	if(typeof unFreezeTimer_<%=acctESIRequestSummaryId%> != 'undefined'){
		clearInterval(unFreezeTimer_<%=acctESIRequestSummaryId%>);
	}
	
	eliteAcctESITimer.Timer.pause();
	$('#freezeAcctBtn'+<%=acctESIRequestSummaryId%>).attr('value', 'Freeze');
}
	
$('.freezeAcctBtn').click(function(){
	var objId=$(this).attr("id");
	var esiObjId=objId.match(/\d+/);
	freezeTimer(esiObjId);
});

$('.unfreezeAcctBtn').click(function(){
	var objId=$(this).attr("id");
	var esiObjId=objId.match(/\d+/);
	unfreezeTimer(esiObjId);
	$('#freezeAcctBtn'+esiObjId).attr('value', 'Freeze');
});
 
$('.backbtn').click(function(){
	isInitializeWidget_<%=acctESIRequestSummaryId%> = true;
	
	var objId=$(this).attr("id");
	
	var esiObjId=objId.match(/\d+/);
	
	$('#backbtn'+esiObjId).hide();
	hideESIChart(esiObjId);
	
	$('#acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_').css('width','100%');
	$('#acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_').css('margin-right','20px');
	
	var parentObj = $('#acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_').parent();
	var parentWidth = $(parentObj).width();
		
	$('#acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_').width(parentWidth + 'px');
	$('#acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_').css('border','none');
		
		
	$('#acctESIReqStatSummaryId_<%=acctESIRequestSummaryId%>_').css('width','100%');
	$('#acctESIReqStatSummaryId_<%=acctESIRequestSummaryId%>_').css('margin-right','20px');
	
	var parentESIObj = $('#acctESIReqStatSummaryId_<%=acctESIRequestSummaryId%>_').parent();
	var parentESIWidth = $(parentObj).width();
		
	$('#acctESIReqStatSummaryId_<%=acctESIRequestSummaryId%>_').width(parentESIWidth + 'px');
	$('#acctESIReqStatSummaryId_<%=acctESIRequestSummaryId%>_').css('border','none');
	
	$('#acctESIReqStatSummaryId_'+esiObjId+'_').hide();
	$('#acctESIRequestSummaryId_'+esiObjId+'_').show();
}); 

var EXPORT_WIDTH = 700;
var canvas;
var svg;
function save_Acct_ESI_Chart(chart) {
	
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
        
		canvas.style.backgroundColor='white';
		var doc = new jsPDF();
		
		doc.setProperties({
	        title: acctServerName,
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
	     
    	doc.text(pageWidth-20,pageHeight - 5, "Page : "+pageAcctIndex );
    	doc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);

	    doc.setDrawColor(0);
	    doc.setFillColor(1,81,152);
	    doc.rect(35, 25, 140, 5, 'F');
	    
	    doc.setDrawColor(0);
	    doc.setFillColor(1,81,152);
	    doc.rect(35, 122, 140, 5, 'F'); 
	    
	    doc.setFontSize(7.5);
	    doc.setTextColor(255);  
	    
	    doc.text(82, 28.5, acctServerName + " Report");
	    doc.text(79, 125.5, acctServerName + " Details");
	    
	    //ESI Name - Rectangle
	    doc.setDrawColor(0);
	    doc.setFillColor(217,230,246);
	    doc.rect(40, 130, 130, 4.2, 'F');
	    
	    
	    doc.setFontSize(6.5);
	    doc.setTextColor(0); 
	    doc.setFontType("bold");
	    doc.text(46, 133, "ESI Name ");
	    
	    doc.text(66, 133, "IP Address ");
	    
	    doc.text(86, 133, "Server-Request");
	    
	    doc.text(110, 133, "Retransmission ");
	    
	    doc.text(130, 133, "Server-Response");
	    
	    doc.text(150, 133, "Request-Drops");
	    
	    
	    var radESIArray = getRadAcctESISummaryDataDetails();
	  
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
	    	    pageAcctIndex++;
	    	    doc.text(pageWidth-20,pageHeight - 5, "Page : "+pageAcctIndex );
	            doc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);
	    	 
	    	}    	
	    	doc.text(46, yIndex-2, value);
	    	doc.text(66, yIndex-2, radESIArray.serverAddress[key].toString() + ':'+radESIArray.serverPort[key].toString());
	    	
	    	var serverReqLength = radESIArray.accountingReq[key].toString().length;
	    	
	    	var charWidth = 1.25;
			var str = reverse(radESIArray.accountingReq[key].toString());
			for(var i = serverReqLength-1 ; i >= 0 ; i--){			  
				doc.text(104.5- charWidth * i, yIndex-2, str[i]);
			}
	    	
	    	var retransmissionLength = radESIArray.retransmission[key].toString().length;
	    	str = reverse(radESIArray.retransmission[key].toString());
			for(var i = retransmissionLength-1 ; i >= 0 ; i--){			  
				doc.text(124.5- charWidth * i, yIndex-2, str[i]);
			}
	    	
	    	var serverResLength = radESIArray.accountingRes[key].toString().length;
	    	str = reverse(radESIArray.accountingRes[key].toString());
			for(var i = serverResLength-1 ; i >= 0 ; i--){			  
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
	    saveAs(blob, acctServerName+'.pdf');
	    console.log('aftre save');
        
    };
    image.src = 'data:image/svg+xml;base64,' + window.btoa(svg);
   
}


var pageAcctIndex =1;
$('#exportbtn<%=acctESIRequestSummaryId%>').click(function(){
	 save_Acct_ESI_Chart($('#acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_').highcharts()); 
});

$('#exportbtn_'+<%=acctESIRequestSummaryId%>).click(function(){
	 save_Acct_ESI_Chart($('#acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_').highcharts()); 
});

function reverse(s){
	    return s.split("").reverse().join("");
}

function getRadAcctESISummaryDataDetails(){
	    var jResponseData="";
		$.ajax({
			url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getRadAcctESIData',
		    type:'GET',
		    async:false,
		    data:{serverIds:acctServerIds},
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
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditRadAcctESIRequestSummary.jsp?widgetId=<%=acctESIRequestSummaryId%>" id="editJsp">
 
  <div id="exportingdiv<%=acctESIRequestSummaryId%>" style="width: 100%;display: table;background-position: center;vertical-align: middle;text-align:right;padding-top:10px;" align="center">
    <input type="button"  id="backbtn<%=acctESIRequestSummaryId%>"  name="button" value="Back" class="restart-btn backbtn" title="Back" align="right" style="display: none;"/>
 	<input type="button"  id="freezeAcctBtn<%=acctESIRequestSummaryId%>" name="button" value="Freeze" class="restart-btn freezeAcctBtn" title="Freeze" align="right"/> 
 	<input type="button"  id="unfreezeAcctBtn<%=acctESIRequestSummaryId%>" name="button" value="Unfreeze" class="restart-btn unfreezeAcctBtn" title="Unfreeze" align="right"/>
 	<input type="button"  id="exportbtn<%=acctESIRequestSummaryId%>" name="button" value="Export.." class="restart-btn exportbtn" title="Unfreeze" align="right" style="margin-right: 20px;'"/>
 </div> 
 
 <%--ProgressBar Div : Start  --%> 
 <div id="progressbarDIV<%=acctESIRequestSummaryId%>" class="progressbar" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>
 <div id="acctESIRequestSummaryId_<%=acctESIRequestSummaryId%>_"  class="widget-class auth-esi-request-chart" style="height: inherit;width: 100%;display: block;background-position: center;vertical-align: middle;text-align:center;" align="center">
 </div>
 <div id="acctESIReqStatSummaryId_<%=acctESIRequestSummaryId%>_" class="widget-class auth-esi-chart"  style="height: inherit;width: 100%;display: block;background-position: center;vertical-align: middle;text-align:center;" align="center">
 </div>
 <span id="exportbtn_<%=acctESIRequestSummaryId%>"></span>
 <%--
	 <div id="countdown" style="height: 20px;width: 100%;display: block;background-position: right;vertical-align: bottom;text-align:right;" align="right">
	 </div>
  --%>
 <%--ProgressBar Div : End  --%>
 </div>
</html>