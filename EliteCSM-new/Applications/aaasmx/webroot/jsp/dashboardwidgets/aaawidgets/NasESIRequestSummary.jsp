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
	<%String nasESIRequestSummaryId = request.getParameter("widgetId");%>

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

var jTimerValue_<%=nasESIRequestSummaryId%>=300000;
var unFreezeTimer_<%=nasESIRequestSummaryId%>;

<%-- Timer code --%>

var eliteNasESITimer = new (function() {

    var $nasCountDown1;
    var incrementNasTime = 70;
    var currentNasTime = jTimerValue_<%=nasESIRequestSummaryId%>; // 5 minutes (in milliseconds)

    function updateNasTimer() {

        /*  Output timer position */
        var timeNasString = formatNasTime(currentNasTime);
        
        /* $countdown.html(timeNasString);	 */        
        $nasCountDown1.attr('value', timeNasString);

        /*  If timer is complete, trigger alert */
        if (currentNasTime == 0) {
        	eliteNasESITimer.Timer.stop();
            /* reset counter division */
            /* $nasCountDown1.attr('value', timeNasString); */
            console.log("updateNasTimer --->methodcall ::");
            <%-- $('#freezebtn'+<%=nasESIRequestSummaryId%>).attr('value', 'Freeze'); --%>
            eliteNasESITimer.resetAcctCountdown();
            return;
        }

        // Increment timer position
        currentNasTime -= incrementNasTime;
        if (currentNasTime < 0) currentNasTime = 0;

    }

    this.resetAcctCountdown = function() {

    /*     Get time from form
        var newTime = parseInt($form.find('input[type=text]').val()) * 1000;
        if (newTime > 0) {currentNasTime = newTime;} */

       /*  Stop and reset timer */
        currentNasTime = jTimerValue_<%=nasESIRequestSummaryId%>;
        eliteNasESITimer.Timer.stop().once();

    };
    
    this.init = function() {
	    $nasCountDown1 = $('#freezebtn'+<%=nasESIRequestSummaryId%>);
	  /*   $countdown = $('#countdown'); */
        eliteNasESITimer.Timer = $.timer(updateNasTimer, incrementNasTime, false);
        currentNasTime = jTimerValue_<%=nasESIRequestSummaryId%>;
   };
});
	eliteNasESITimer.init();

	function padNas(number, length){
	    var str = '' + number;
	    while (str.length < length) {str = '0' + str;}
	    return str;
	}
	
	function formatNasTime(time) {
		time = time / 10;
	    var min = parseInt(time / 6000),
	        sec = parseInt(time / 100) - (min * 60),
	        hundredths = padNas(time - (sec * 100) - (min * 6000), 2);
	    return (min > 0 ? padNas(min, 2) : "00") + "m " + padNas(sec, 2) + "s "; 	}


	<%-- End Timer code --%>
	
	/* todo ec need to generalize it  */
	var colors = Highcharts.getOptions().colors;
	<%--
	var AC_color_code='#4572A7';
	var AR_color_code='#062173';
	var AA_color_code='#89A54E';
	var RD_color_code='#AA4643';
	--%>
	var AC_color_code_<%=nasESIRequestSummaryId%>=colors[0];
	var AR_color_code_<%=nasESIRequestSummaryId%>=colors[5];
	var AA_color_code_<%=nasESIRequestSummaryId%>=colors[2];
	var RD_color_code_<%=nasESIRequestSummaryId%>=colors[3];
	var CR_color_code_<%=nasESIRequestSummaryId%>=colors[0];
	var CA_color_code_<%=nasESIRequestSummaryId%>=colors[5];
	var CN_color_code_<%=nasESIRequestSummaryId%>=colors[2];
	var CT_color_code_<%=nasESIRequestSummaryId%>=colors[3];
	
	
	var esiName_<%=nasESIRequestSummaryId%>='';
	var daysPreviously_<%=nasESIRequestSummaryId%>=0;

	/* Range selector array define millinsecond of each rangeselector i.e.  [30min,1Month,1Week,1Month,All] which is used to calculate endtime  */
	var rangeSelectorArray_<%=nasESIRequestSummaryId%>=[1800000,3600000,43200000,86400000,604800000,111600000,-1];

	<%-- Description :  Chart Data wrapper classes for Total Request Statistics summary--%>
	
	
	var HNasESIStatisticsCategories=HCategoriesData.extend({
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
	
	var HNasESIStatisticsPointData=HPointData.extend({
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
	
	var HNasESIStatisticsSeriesData=HSeriesData.extend({
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
		setStackData : function(stack){
			this._super(stack);
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
		getStackData : function(){
			return this._super(this.stack);
		},
		buildSeriesData : function(){
			var seriesData={
		    		"name"  : this.name,
				    "color" : this.color,
				    "data"  : this.pointList,
				    "stack" : this.stack
				    };
		   return seriesData;	
		}
	});
	
	
	var HNasESIStatisticsSeries=HSeries.extend({
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
	var HNasESICategories=HCategoriesData.extend({
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
	var HNasESIPointData=HPointData.extend({
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
			pointData=[this.xPoint,this.yPoints[0],this.yPoints[1],this.yPoints[2],this.yPoints[3],this.yPoints[4],this.yPoints[5],this.yPoints[6],this.yPoints[7]];
	 	    return pointData;	
		}
	});

	var HNasESISeriesData=HSeriesData.extend({
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


	var HNasESISeries=HSeries.extend({
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
var TotalNasReqStatChartDataConfigHelper=$.Class.extend({
	 init: function(){
		 
	 },
	 configNasESIStatisticsSeries : function(totalNasESIReqChartData){
		 var esiStatSeries =new HNasESIStatisticsSeries();
		 
		 var acSeries=new HNasESIStatisticsSeriesData();
		 acSeries.setPointList(totalNasESIReqChartData.disconnectReq);
		 acSeries.setName("Disconnect-Request");
		 acSeries.setColor(AC_color_code_<%=nasESIRequestSummaryId%>);
		 acSeries.setStackData('dis');
		 var acSeriesData=acSeries.buildSeriesData();
		 esiStatSeries.addSeries(acSeriesData);
		 
		 var arSeries=new HNasESIStatisticsSeriesData();
		 arSeries.setPointList(totalNasESIReqChartData.disconnectNack);
		 arSeries.setName("Disconnect-Nack");
		 arSeries.setColor(AR_color_code_<%=nasESIRequestSummaryId%>);
		 arSeries.setStackData('dis');
		 var arSeriesData=arSeries.buildSeriesData();
		 esiStatSeries.addSeries(arSeriesData);
		 
		 var aaSeries=new HNasESIStatisticsSeriesData();
		 aaSeries.setPointList(totalNasESIReqChartData.disconnectAck);
		 aaSeries.setName("Disconnect-Ack");
		 aaSeries.setStackData('dis');
		 aaSeries.setColor(AA_color_code_<%=nasESIRequestSummaryId%>);
		 var aaSeriesData=aaSeries.buildSeriesData();
		 esiStatSeries.addSeries(aaSeriesData);
		 
		 var rrSeries=new HNasESIStatisticsSeriesData();
		 rrSeries.setPointList(totalNasESIReqChartData.disconnectTimeout);
		 rrSeries.setName("Disconnect-Timeout");
		 rrSeries.setColor(RD_color_code_<%=nasESIRequestSummaryId%>);
		 rrSeries.setStackData('dis');
		 var rrSeriesData=rrSeries.buildSeriesData();
		 esiStatSeries.addSeries(rrSeriesData);
		 
		 var crSeries=new HNasESIStatisticsSeriesData();
		 crSeries.setPointList(totalNasESIReqChartData.coaReq);
		 crSeries.setName("COA-Request");
		 crSeries.setColor(CR_color_code_<%=nasESIRequestSummaryId%>);
		 crSeries.setStackData('coa');
		 var crSeriesData=crSeries.buildSeriesData();
		 esiStatSeries.addSeries(crSeriesData);
		 
		 var cnSeries=new HNasESIStatisticsSeriesData();
		 cnSeries.setPointList(totalNasESIReqChartData.coaNack);
		 cnSeries.setName("COA-Nack");
		 cnSeries.setColor(CN_color_code_<%=nasESIRequestSummaryId%>);
		 cnSeries.setStackData('coa');
		 var cnSeriesData=cnSeries.buildSeriesData();
		 esiStatSeries.addSeries(cnSeriesData);
		 
		 var caSeries=new HNasESIStatisticsSeriesData();
		 caSeries.setPointList(totalNasESIReqChartData.coaAck);
		 caSeries.setName("COA-Ack");
		 caSeries.setColor(CA_color_code_<%=nasESIRequestSummaryId%>);
		 caSeries.setStackData('coa');
		 var caSeriesData=caSeries.buildSeriesData();
		 esiStatSeries.addSeries(caSeriesData);
		 
		 var ctSeries=new HNasESIStatisticsSeriesData();
		 ctSeries.setPointList(totalNasESIReqChartData.coaTimeout);
		 ctSeries.setName("COA-Timeout");
		 ctSeries.setStackData('coa');
		 ctSeries.setColor(CT_color_code_<%=nasESIRequestSummaryId%>);
		 var ctSeriesData=ctSeries.buildSeriesData();
		 esiStatSeries.addSeries(ctSeriesData);
		 
		 return esiStatSeries.getSeries();
		 
	 },
	 configNasESIStatisticsCategories : function(totalNasESIReqChartData1){
		    var esiStatCategories=new HNasESIStatisticsCategories();
		    esiStatCategories.setCategories(totalNasESIReqChartData1.esi);
			return esiStatCategories.getCategories();
	 },
	 configESISeries : function(esiDataArray){
		 /* make esiseries object */
		 var esiSeries=new HNasESISeries();
		 /* make for esi series data object */
		 
		 /*
		   name: 'Accept-Challenge',
	       color: '#4572A7',
	       type: 'column',
	       yAxis: 1,
	       data: jNumACDetailedDataArray
		 */
		 var acSeriesData=new HNasESISeriesData();
		 acSeriesData.setName("Disconnect-Request");
		 acSeriesData.setColor(AC_color_code_<%=nasESIRequestSummaryId%>);
		 acSeriesData.setType("spline");
		 acSeriesData.setYAxis(0);
		 
		 /*
	              name: 'Accept-Reject',
	              type: 'spline',
	              color: '#AA4643',
	              yAxis: 2,
	              data: jNumARDetailedDataArray
	              
		 */
		 
		 
		 var arSeriesData=new HNasESISeriesData();
         arSeriesData.setName("Disconnect-Nack");
         arSeriesData.setColor(AR_color_code_<%=nasESIRequestSummaryId%>);
         arSeriesData.setType("spline");
         arSeriesData.setYAxis(1);
		 
		 /*
		   name: 'Access-Accept',
	       color: '#89A54E',
	       yAxis: 3,
	       type: 'spline',
	       data: jNumAADetailedDataArray
		 */
		 var aaSeriesData=new HNasESISeriesData();
		 aaSeriesData.setName("Disconnect-Ack");
		 aaSeriesData.setColor(AA_color_code_<%=nasESIRequestSummaryId%>);
		 aaSeriesData.setType("spline");
		 aaSeriesData.setYAxis(2);
		 
		
		 
		 /*
		          name: 'Request-Drops',
	              color: '#4572A7',
	              type: 'spline',
	              yAxis: 1,
	              data: jNumRDDetailedDataArray
		 
		 */
		 var rdSeriesData=new HNasESISeriesData();
		 rdSeriesData.setName("Disconnect-Timeout");
		 rdSeriesData.setColor(RD_color_code_<%=nasESIRequestSummaryId%>);
		 rdSeriesData.setType("spline");
		 rdSeriesData.setYAxis(3);
		 
		 /*
         name: 'COA-Request',
         color: '#5572A7',
         type: 'spline',
         yAxis: 1,
         data: jNumRDDetailedDataArray

		*/
		var crSeriesData=new HNasESISeriesData();
		crSeriesData.setName("COA-Request");
		crSeriesData.setColor(CR_color_code_<%=nasESIRequestSummaryId%>);
		crSeriesData.setType("spline");
		crSeriesData.setYAxis(4);
		
		 /*
        name: 'COA-Nack',
        color: '#1572A7',
        type: 'spline',
        yAxis: 1,
        data: jNumRDDetailedDataArray

		*/
		var cnSeriesData=new HNasESISeriesData();
		cnSeriesData.setName("COA-Nack");
		cnSeriesData.setColor(CN_color_code_<%=nasESIRequestSummaryId%>);
		cnSeriesData.setType("spline");
		cnSeriesData.setYAxis(5);
		
		 /*
        name: 'COA-Nack',
        color: '#1572A7',
        type: 'spline',
        yAxis: 1,
        data: jNumRDDetailedDataArray

		*/
		var caSeriesData=new HNasESISeriesData();
		caSeriesData.setName("COA-Ack");
		caSeriesData.setColor(CA_color_code_<%=nasESIRequestSummaryId%>);
		caSeriesData.setType("spline");
		caSeriesData.setYAxis(6);
		
		 /*
        name: 'COA-Nack',
        color: '#1572A7',
        type: 'spline',
        yAxis: 1,
        data: jNumRDDetailedDataArray

		*/
		var ctSeriesData=new HNasESISeriesData();
		ctSeriesData.setName("COA-Timeout");
		ctSeriesData.setColor(CT_color_code_<%=nasESIRequestSummaryId%>);
		ctSeriesData.setType("spline");
		ctSeriesData.setYAxis(7);
		 
		 
		 
		 // build all four series
		 var ephochTimeDataList=esiDataArray.epochTime;
		 var disconnectReqList=esiDataArray.disconnectReq;
		 var disconnectNackList=esiDataArray.disconnectNack;
		 var disconnectAckList=esiDataArray.disconnectAck;
		 var disconnectTimeoutList=esiDataArray.disconnectTimeout;
		 var coaReqList=esiDataArray.coaReq;
		 var coaNackList=esiDataArray.coaNack;
		 var coaAckList=esiDataArray.coaAck;
		 var coaTimeoutList=esiDataArray.coaTimeout;
		 
         $.each(ephochTimeDataList,function(i,epochTime){
        	  
        	  var acPointData=new HNasESIPointData();
        	  acPointData.setXPoint(epochTime);
        	  acPointData.setYPoints(disconnectReqList[i]);
        	  var acPointJSONData=acPointData.buildPointData();
        	  acSeriesData.addPoint(acPointJSONData);
        	  
        	  var arPointData=new HNasESIPointData();
        	  arPointData.setXPoint(epochTime);
        	  arPointData.setYPoints(disconnectNackList[i]);
        	  var arPointJSONData=arPointData.buildPointData();
        	  arSeriesData.addPoint(arPointJSONData);
        	  
        	  var aaPointData=new HNasESIPointData();
        	  aaPointData.setXPoint(epochTime);
        	  aaPointData.setYPoints(disconnectAckList[i]);
        	  var aaPointJSONData=aaPointData.buildPointData();
        	  aaSeriesData.addPoint(aaPointJSONData);
        	  
        	  var rdPointData=new HNasESIPointData();
        	  rdPointData.setXPoint(epochTime);
        	  rdPointData.setYPoints(disconnectTimeoutList[i]);
        	  var rdPointJSONData=rdPointData.buildPointData();
        	  rdSeriesData.addPoint(rdPointJSONData);
        	  
        	  var coaPointData=new HNasESIPointData();
        	  coaPointData.setXPoint(epochTime);
        	  coaPointData.setYPoints(coaReqList[i]);
        	  var coaPointJSONData=coaPointData.buildPointData();
        	  crSeriesData.addPoint(coaPointJSONData);
        	  
        	  var cnPointData=new HNasESIPointData();
        	  cnPointData.setXPoint(epochTime);
        	  cnPointData.setYPoints(coaNackList[i]);
        	  var cnPointJSONData=cnPointData.buildPointData();
        	  cnSeriesData.addPoint(cnPointJSONData);
        	  
        	  var caPointData=new HNasESIPointData();
        	  caPointData.setXPoint(epochTime);
        	  caPointData.setYPoints(coaAckList[i]);
        	  var caPointJSONData=caPointData.buildPointData();
        	  caSeriesData.addPoint(caPointJSONData);
        	  
        	  var ctPointData=new HNasESIPointData();
        	  ctPointData.setXPoint(epochTime);
        	  ctPointData.setYPoints(coaTimeoutList[i]);
        	  var ctPointJSONData=ctPointData.buildPointData();
        	  ctSeriesData.addPoint(ctPointJSONData);
         });	 
		
         
         
         esiSeries.addSeries(acSeriesData.buildSeriesData());
         esiSeries.addSeries(arSeriesData.buildSeriesData());
         esiSeries.addSeries(aaSeriesData.buildSeriesData());
         esiSeries.addSeries(rdSeriesData.buildSeriesData());
         
         esiSeries.addSeries(crSeriesData.buildSeriesData());
         esiSeries.addSeries(cnSeriesData.buildSeriesData());
         esiSeries.addSeries(caSeriesData.buildSeriesData());
         esiSeries.addSeries(ctSeriesData.buildSeriesData());
		 
		 return esiSeries.getSeries();
	 },
	 configAllNasESISeries : function(esiObjId){
		    
 		    var responseData=sendNasESIDataFetchRequest("","",false);
 		   
			if(responseData != ""){
				
				var data=totalNasReqStatChartDataConfigHelper.getAllNasESISeriesData(responseData);
				var esiNavigatorData=data.pointList;
				jNasESIoptions.navigator.series.data=esiNavigatorData;
				jNasESIoptions.series=totalNasReqStatChartDataConfigHelper.configESISeries(responseData);
				jNasESIoptions.chart.renderTo='nasESIReqStatSummaryId_'+esiObjId+'_';
				var chart = $('#nasESIReqStatSummaryId_'+esiObjId+'_').highcharts('StockChart',jNasESIoptions);// need to check may break functionality
				chart.showLoading('Loading data from server...');
       		    chart.hideLoading();
			}
		 
	 },
	 getAllNasESISeriesData : function(esiDataArray){
		
		 var ephochTimeDataList=esiDataArray.epochTime;
		 var disconnectReqList=esiDataArray.disconnectReq;
		 var disconnectNackList=esiDataArray.disconnectNack;
		 var disconnectAckList=esiDataArray.disconnectAck;
		 var disconnectTimeoutList=esiDataArray.disconnectTimeout;
		 var coaReqList=esiDataArray.coaReq;
		 var coaNackList=esiDataArray.coaNack;
		 var coaAckList=esiDataArray.coaAck;
		 var coaTimeoutList=esiDataArray.coaTimeout;
		 
		 
		 var acSeriesData=new HNasESISeriesData();
		 
         $.each(ephochTimeDataList,function(i,epochTime){
        	  
        	  var acPointData=new HNasESIPointData();
        	  acPointData.setXPoint(epochTime);
        	  acPointData.setYPoints(disconnectReqList[i]);
        	  acPointData.setYPoints(disconnectNackList[i]);
        	  acPointData.setYPoints(disconnectAckList[i]);
        	  acPointData.setYPoints(disconnectTimeoutList[i]);
        	  
        	  acPointData.setYPoints(coaReqList[i]);
        	  acPointData.setYPoints(coaNackList[i]);
        	  acPointData.setYPoints(coaAckList[i]);
        	  acPointData.setYPoints(coaTimeoutList[i]);
        	  
        	  var acPointJSONData=acPointData.buildAllPointData();
        	  acSeriesData.addPoint(acPointJSONData);
        	 
         });
         
     
         return acSeriesData;
	 },
	 getNasESISeriesData : function(xData,yData){
		 
		 var xDataList=xData;
		 var yDataList=yData;
		 
		 var seriesData=new HNasESISeriesData();
         $.each(xDataList,function(i,xData){
        	  
        	  var pointData=new HNasESIPointData();
        	  pointData.setXPoint(xData);
        	  pointData.setYPoints(yDataList[i]);
        	  var pointJSONData=pointData.buildPointData();
        	  seriesData.addPoint(pointJSONData);
         });
         return seriesData;
	 }
});

<%-- 
Description :  Data class that is use to convert json data received from websocket  into totalNasESIReqChartData  object
--%>


var TotalNasESIReqChartData = $.Class.extend({
	  init: function(){},
	  setEpochTime:function(epochTime){
		  this.epochTime=epochTime;//number
	  },
	  setESI:function(jESIData){
		  this.esi=jESIData;
	  },
	  setDisconnectReq : function(disconnectReq){
		  this.disconnectReq=disconnectReq;
	  },
	  setDisconnectNack : function(disconnectNack){
 		this.disconnectNack=disconnectNack;  
	  },
	  setDisconnectAck : function(disconnectAck){
		  this.disconnectAck=disconnectAck;
	  },
	  setDisconnectTimeout : function(disconnectTimeout){
		  this.disconnectTimeout=disconnectTimeout;
	  },
	  setCoaReq : function(coaReq){
		  this.coaReq=coaReq;
	  },
	  setCoaNack : function(coaNack){
		  this.coaNack=coaNack;
	  },
	  setCoaAck : function(coaAck){
		  this.coaAck=coaAck;
	  },
	  setCoaTimeout : function(coaTimeout){
		  this.coaTimeout=coaTimeout;
	  },
	  setNasReqDataArray : function(nasReqDataArray){
		  this.nasReqDataArray=nasReqDataArray;
	  }
});
	
	

<%-- Description :  Instantiate  TotalNasESIReqChartData and TotalReqStatChartDataConfigHelper class object --%>
var totalNasESIReqChartData=new TotalNasESIReqChartData();
var totalNasReqStatChartDataConfigHelper=new TotalNasReqStatChartDataConfigHelper();
/* var categories=[];
var data; */

<%--Description : Highchart option configuration for ESI Chart --%>
var jNasESIoptions=
	{
		chart: {
            zoomType: 'xy',
            renderTo: 'nasESIReqStatSummaryId_'+<%=nasESIRequestSummaryId%>+'_',
     		events : {
                load: function(event) {
             		
                }
     		},
     		animation : false
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
				color : AC_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		title : {
			text : 'Disconnect-Request',
			style : {
				color : AC_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : true

	}, { 
		gridLineWidth : 0,
		title : {
			text : 'Disconnect-Nack',
			style : {
				color : AR_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : AR_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : true
	}, { 
		gridLineWidth : 0,
		title : {
			text : 'Disconnect-Ack',
			style : {
				color : AA_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : AA_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : false
	}, { 
		title : {
			text : 'Disconnect-Timeout',
			style : {
				color : RD_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : RD_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : true
	}, { 
		title : {
			text : 'COA-Request',
			style : {
				color : CR_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : CR_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : true
	},{ 
		title : {
			text : 'COA-Nack',
			style : {
				color : CN_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : CN_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : true
	}, { 
		title : {
			text : 'COA-Ack',
			style : {
				color : CA_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : CA_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : true
	},{ 
		title : {
			text : 'COA-Timeout',
			style : {
				color : CT_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		labels : {
			formatter : function() {
				return this.value;
			},
			style : {
				color : CT_color_code_<%=nasESIRequestSummaryId%>
			}
		},
		opposite : true
	}],
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
		},
		 column: {
             stacking: 'normal'
         }
	},
	legend : {
		layout : 'vertical',
		align : 'left',
		x : 120,
		verticalAlign : 'top',
		y : 80,
		floating : true,
		backgroundColor : '#FFFFFF'
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
var jNasStackedColumnoptions=
      {
	       	chart: {
			 		type: 'column',
		 		marginBottom : 80,
        		zoomType: 'xy',
        		renderTo: 'nasESIRequestSummaryId_'+'<%=nasESIRequestSummaryId%>'+'_',
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
			        align: 'left',
			        x : -12,
			        y : 15,
			        style: {
			        	 color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray',                               
			            fontWeight : 'bold',
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
	                cursor: 'pointer'
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
			},legend:
			 {
				  verticalAlign: 'top'
			}
			
};
<%-- Description :  This method is called to process and render data to chart received by websocket for Total Request Statistics Summary Chart --%> 
function updateNasTotalReqStatSummaryData(data,esiId){
    if(data != null){
    	
    	var configNASObj = getWidgetConfiguration(<%=nasESIRequestSummaryId%>);  <%-- Use ConfObj to retrive configuration --%>
    	var nasEsiServerIdsList = "";
    	    	
    	if(typeof configNASObj != 'undefined'){
    	 		nasEsiServerIdsList = configNASObj.get("ELITEAAAINSTANCES");
    	}
    	    	
    	if(nasEsiServerIdsList){
    		var acctEsiServerArrays = nasEsiServerIdsList.split(',');
    		var foundIndex = 0;
    	  	var nasESIServerIndexArray = [];
            var dataDisconnectReq = [];
    		var dataDisconnectNack = [];
    	    var dataDisconnectAck = [];
    	 	var dataESI = [];
    	    var dataDisconnectTimeout = [];
    	    var dataCoaReq = [];
    	    var dataCoaNack =[];
    	    var dataCoaAck = [];
    	    var dataCoaTimeout = [];
    	    var dataEpochTime = [];    	
    	    
    	    if(acctEsiServerArrays){
    	        		
    	    $.each(acctEsiServerArrays, function( filterKey, filterValue ) {
    	    	if(filterValue){
    	        	var foundIndex = 0,isIndexFound = false;
    	         	$.each( data.esi, function( key, value ) {
    	          		if(filterValue == value){
    	            	    var foundIndex = key;
    	            	 	nasESIServerIndexArray.push(foundIndex);
    	            	 	dataDisconnectReq.push(data.disconnectReq[foundIndex]);
    	            	 	dataDisconnectNack.push(data.disconnectNack[foundIndex]);
    	            	 	dataDisconnectAck.push(data.disconnectAck[foundIndex]);
    	            	 	dataDisconnectTimeout.push(data.disconnectTimeout[foundIndex]);
    	            	 	dataESI.push(data.esi[foundIndex]);
    	            	 	dataCoaReq.push(data.coaReq[foundIndex]);
    	            	 	dataCoaNack.push(data.coaNack[foundIndex]);
    	            	 	dataCoaAck.push(data.coaAck[foundIndex]);
    	            	 	dataCoaTimeout.push(data.coaTimeout[foundIndex]);
    	            	 	dataEpochTime.push(data.epochTime[foundIndex]);
    	            		isIndexFound = true;
    	             	}
    	        	});
    	      	}
    	   });
    	            	
    	  	data.esi = dataESI;
    	  	data.disconnectReq=dataDisconnectReq;
    	  	data.disconnectNack =dataDisconnectNack;
    	  	data.disconnectAck=dataDisconnectAck;
    	  	data.disconnectTimeout=dataDisconnectTimeout;
    	  	data.coaReq=dataCoaReq;
    	  	data.coaNack=dataCoaNack;
    	  	data.coaAck=dataCoaAck;
    	  	data.coaTimeout=dataCoaTimeout;
    	  	data.epochTime=dataEpochTime;
    	}
      }
    	
    	console.log("isInitialize value :  " +isInitializeWidget_<%=nasESIRequestSummaryId%>);
    	if(isInitializeWidget_<%=nasESIRequestSummaryId%>){
    		/* //totalNasESIReqChartData=new TotalNasESIReqChartData(); */
    	    totalNasESIReqChartData.setNasReqDataArray(data.nasReqDataArray);
    	    totalNasESIReqChartData.setESI(data.esi);
    		totalNasESIReqChartData.setDisconnectReq(data.disconnectReq);
    		totalNasESIReqChartData.setDisconnectNack(data.disconnectNack);
    		totalNasESIReqChartData.setDisconnectAck(data.disconnectAck);
    		totalNasESIReqChartData.setDisconnectTimeout(data.disconnectTimeout);
    		totalNasESIReqChartData.setCoaReq(data.coaReq);
    		totalNasESIReqChartData.setCoaNack(data.coaNack);
    		totalNasESIReqChartData.setCoaAck(data.coaAck);
    		totalNasESIReqChartData.setCoaTimeout(data.coaTimeout);
    		totalNasESIReqChartData.setEpochTime(data.epochTime);
    		 
    		/* 1. build configNasESIStatisticsSeries */
            jNasStackedColumnoptions.series=totalNasReqStatChartDataConfigHelper.configNasESIStatisticsSeries(totalNasESIReqChartData);
            jNasStackedColumnoptions.chart.renderTo='nasESIRequestSummaryId_'+esiId+'_';
            
            var chart = new Highcharts.Chart(jNasStackedColumnoptions);
            
            
            $('#progressbarDIV'+esiId).hide();
            
            /* 2. build configNasESIStatisticsCategories */
            var esiStatCategories=totalNasReqStatChartDataConfigHelper.configNasESIStatisticsCategories(totalNasESIReqChartData);
            
            chart.xAxis[0].setCategories(esiStatCategories);
            
            isInitializeWidget_<%=nasESIRequestSummaryId%> = false;
    	}else{
    		 var esiNASChart = $('#nasESIRequestSummaryId_'+esiId+'_').highcharts();
             
    		totalNasESIReqChartData.setNasReqDataArray(data.nasReqDataArray);
     	    totalNasESIReqChartData.setESI(data.esi);
     		totalNasESIReqChartData.setDisconnectReq(data.disconnectReq);
     		totalNasESIReqChartData.setDisconnectNack(data.disconnectNack);
     		totalNasESIReqChartData.setDisconnectAck(data.disconnectAck);
     		totalNasESIReqChartData.setDisconnectTimeout(data.disconnectTimeout);
     		totalNasESIReqChartData.setCoaReq(data.coaReq);
     		totalNasESIReqChartData.setCoaNack(data.coaNack);
     		totalNasESIReqChartData.setCoaAck(data.coaAck);
     		totalNasESIReqChartData.setCoaTimeout(data.coaTimeout);
     		totalNasESIReqChartData.setEpochTime(data.epochTime);
    		 
     		if ($('#nasESIRequestSummaryId_'+esiId+'_').find('#errorClassDiv').length ) {
     			$('#nasESIRequestSummaryId_'+esiId+'_').find('#errorClassDiv').hide(); 
     			$('#'+$('#currentTabId').val()).find($('div#'+esiId)).find(".widgetcontent").load("jsp/dashboardwidgets/aaawidgets/NasESIRequestSummary.jsp?widgetId="+esiId);
     		}
     		
    		//update chart data..
     		 var chartNasArray = totalNasReqStatChartDataConfigHelper.configNasESIStatisticsSeries(totalNasESIReqChartData);
    		
    		 var yIndex=0,dataIndex = 0;
			 var disconnectReqIndex = 0,disconnectNackIndex = 1,disconnectAck = 2,disconnectTimeout =3;
			 var coaReqIndex = 4,coaNackIndex = 5,coaAck = 6,coaTimeout =7;
			 
    		 $.each( chartNasArray, function( key, value ) {
    			 if(value.name == 'Disconnect-Request'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Disconnect-Request : "+ data);
    					 esiNASChart.series[disconnectReqIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Disconnect-Nack'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Disconnect-Nack : "+ data);
    					 esiNASChart.series[disconnectNackIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Disconnect-Ack'){
    				 var data=value.data;
    				 
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Disconnect-Ack  : "+ data);
    					 esiNASChart.series[disconnectAck].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    				 
    			 }else if(value.name == 'Disconnect-Timeout'){
    				 var data=value.data;
    				
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("Disconnect-Timeout : "+ data);
    					 esiNASChart.series[disconnectTimeout].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    			 }else if(value.name == 'COA-Request'){
    				 var data=value.data;
    				
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("COA-Request : "+ data);
    					 esiNASChart.series[coaReqIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    			 }else if(value.name == 'COA-Nack'){
    				 var data=value.data;
    				
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("COA-Nack : "+ data);
    					 esiNASChart.series[coaNackIndex].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    			 }else if(value.name == 'COA-Ack'){
    				 var data=value.data;
    				
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("COA-Ack : "+ data);
    					 esiNASChart.series[coaAck].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    			 }else if(value.name == 'COA-Timeout'){
    				 var data=value.data;
    				
    				 var cIndex = 0;
    				 $.each(data, function(dataKey,dataValue){
    					 console.log("COA-Timeout : "+ data);
    					 esiNASChart.series[coaTimeout].data[cIndex].update(data[dataKey]);
    					 cIndex++;
    				 });
    			 }
    		  });
    		 
    		 $('#progressbarDIV'+esiId).hide();
             
    		 //set inner chart data
             var responseData=sendNasESIDataFetchRequest("","",false);
   		   
 		     if(responseData != ""){
 				
 				var data=totalNasReqStatChartDataConfigHelper.getAllNasESISeriesData(responseData);
 				var esiNavigatorData=data.pointList;
 				jNasESIoptions.navigator.series.data=esiNavigatorData;
 				jNasESIoptions.series=totalNasReqStatChartDataConfigHelper.configESISeries(responseData);
 				jNasESIoptions.chart.renderTo='nasESIReqStatSummaryId_<%=nasESIRequestSummaryId%>_';
 				var chart = $('#nasESIReqStatSummaryId_<%=nasESIRequestSummaryId%>_').highcharts('StockChart',jNasESIoptions);// need to check may break functionality
 		
 			}
    	}
    }  
}

function flushDiv(){
	$('#nasESIRequestSummaryId_'+<%=nasESIRequestSummaryId%>+'_').empty();
}
<%--Use Below method to Stop Progress bar--%>
function hideProgressBar(){
	$('#progressbarDIV'+<%=nasESIRequestSummaryId%>).hide();
	$('#exportingdiv'+<%=nasESIRequestSummaryId%>).show();
}

function hideNasESIChart(esiObjId){
	$('#nasESIReqStatSummaryId_'+esiObjId+'_').hide();
	$('#nasESIRequestSummaryId_'+esiObjId+'_').show();		
}

function showNasESIChart(esiObjId){
	$('#nasESIReqStatSummaryId_'+esiObjId+'_').show();
	$('#nasESIRequestSummaryId_'+esiObjId+'_').hide();
}

function getNASESIData(startTimeValue,endTimeValue){
	
	var responseData=sendNasESIDataFetchRequest(startTimeValue,endTimeValue,false);
	var series="";
	if(responseData != ""){
		
		var series0=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.disconnectReq);
		var series1=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.disconnectNack);
		var series2=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.disconnectAck);
		var series3=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.disconnectTimeout);
		var series4=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.coaReq);
		var series5=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.coaNack);
		var series6=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.coaAck);
		var series7=totalNasReqStatChartDataConfigHelper.getNasESISeriesData(responseData.epochTime,responseData.coaTimeout);
		
   	    var chart1 = $('#nasESIReqStatSummaryId_'+<%=nasESIRequestSummaryId%>+'_').highcharts();
   	    chart1.series[0].setData(series0.pointList);
   	    chart1.series[1].setData(series1.pointList);
   	    chart1.series[2].setData(series2.pointList);
	    chart1.series[3].setData(series3.pointList);
	    chart1.series[4].setData(series4.pointList);
	    chart1.series[5].setData(series5.pointList);
	    chart1.series[6].setData(series6.pointList);
	    chart1.series[7].setData(series7.pointList);
	    
		chart1.hideLoading();
	
	}
}

function sendNasESIDataFetchRequest(startTimeValue,endTimeValue,isAsync){
    var jResponseData="";
	$.ajax({
		url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getNASESIData',
	    type:'GET',
	    async:isAsync,
	    data:{esiName:esiName_<%=nasESIRequestSummaryId%>,startTimeValue:startTimeValue,endTimeValue:endTimeValue},
	    success: function(esiDataList){
	    	if(esiDataList != null)
	    	 jResponseData=esiDataList;  	
	    }
	}); 
	
	return jResponseData;
}

<%--DO NOT REMOVE : Get widget Configuration here --%>
var nasESIRequestSummaryId=<%=nasESIRequestSummaryId%>;

var confObj = getWidgetConfiguration(nasESIRequestSummaryId);  <%-- Use ConfObj to retrive configuration --%>
var internval=1;
var nasESIIds =""; 
var nasWidgetName = "";
if(typeof confObj != 'undefined'){
	interval=confObj.get("REFRESHINTERVAL");
	daysPreviously=confObj.get("DAYSPREVIOUSLY");
	nasESIIds = confObj.get("ELITEAAAINSTANCES");
	nasWidgetName=confObj.get("NAME");
}
console.log("NAS ESI Stat Summary : interval value is "+interval);

var id='nasESIRequestSummaryId_'+nasESIRequestSummaryId+'_';
	
<%-- chart data fetch logic--%>

var data = {
		header : {
			id : 'nasESIRequestSummaryId_'+<%=nasESIRequestSummaryId%>+'_',
			type : "NAS_ESI_STATISTICS"
		},
		body : {
			interval : interval,
			aaaServerIds:nasESIIds
		}
};
var intervalCounter = 0;
var isInitializeWidget_<%=nasESIRequestSummaryId%>= true; 
var widgetNasHandler  = {
		renderData : function(data) {	
			updateNasTotalReqStatSummaryData(data,'<%=nasESIRequestSummaryId%>');
		},
		updateData : function(data) {
			updateNasTotalReqStatSummaryData(data,'<%=nasESIRequestSummaryId%>');
		}
};
getDashBoardSocket().register(new Widget(id, widgetNasHandler));
getDashBoardSocket().sendRequest(data);

function freeze(id)
{
	console.log("freeze call start");
	resetNasTimer();
	/* //do clear interval */
	
		var data = {
				header : {
					id : 'nasESIRequestSummaryId_'+<%=nasESIRequestSummaryId%>+'_',
					type : "NAS_ESI_STATISTICS"
				},
				body : {
					freeze : true
				}
		};
	
	getDashBoardSocket().sendRequest(data);
	eliteNasESITimer.resetAcctCountdown();
	eliteNasESITimer.Timer.play();
	unFreezeTimer_<%=nasESIRequestSummaryId%>=setTimeout(unFreezeNas,jTimerValue_<%=nasESIRequestSummaryId%>);
	
}

function unFreezeNas(id)
{
	console.log("unFreezeNas call start");
	
	var data = {
			header : {
				id : 'nasESIRequestSummaryId_'+<%=nasESIRequestSummaryId%>+'_',
				type : "NAS_ESI_STATISTICS"
			},
			body : {
				freeze : false
			}
	};
	
	getDashBoardSocket().sendRequest(data);
	resetNasTimer();
}
function resetNasTimer(){
	
    console.log("resetNasTimer call start");
	if(typeof unFreezeTimer_<%=nasESIRequestSummaryId%> != 'undefined'){
		clearInterval(unFreezeTimer_<%=nasESIRequestSummaryId%>);
	}
	
	eliteNasESITimer.Timer.pause();
	$('#freezebtn'+<%=nasESIRequestSummaryId%>).attr('value', 'Freeze');
}
	
$('.freezebtn').click(function(){
	var objId=$(this).attr("id");
	var esiObjId=objId.match(/\d+/);
	freeze(esiObjId);
});

$('.unfreezebtn').click(function(){
	var objId=$(this).attr("id");
	var esiObjId=objId.match(/\d+/);
	unFreezeNas(esiObjId);
	$('#freezebtn'+esiObjId).attr('value', 'Freeze');
});
 
$('.backbtn').click(function(){
	isInitializeWidget_<%=nasESIRequestSummaryId%> = true;
	
	var objId=$(this).attr("id");
	
	var esiObjId=objId.match(/\d+/);
	
	$('#backbtn'+esiObjId).hide();
	hideNasESIChart(esiObjId);
});

var EXPORT_WIDTH = 700;
var canvas;
var svg;
function save_nas_chart(chart) {
	
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
    image1.src =  $('#elitecore_Image').attr('src');
    canvas1.height=77;
    canvas1.width=213;
    
    // Create an image and draw the SVG onto the canvas
    var image = new Image;
    image.onload = function() {
        canvas.getContext('2d').drawImage(this, -5, 0, render_width+35, render_height+10);
        canvas.getContext("2d").fillStyle='white'; 
        
		var chartName= nasWidgetName;
		
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
	    doc.rect(15, 130, pageWidth-30,10, 'F');
	    
	    
	    doc.setFontSize(6.5);
	    doc.setTextColor(0); 
	    doc.setFontType("bold");
	    doc.text(20, 135, "ESI Name ");
	    
	    doc.text(45, 135, "IP Address ");
	    
	    doc.text(85, 135, "Disconnect-Message");
	    
	    doc.text(160, 135, "COA ");
	    
	    doc.setDrawColor(192,192,192); 
	    
	    //seperate line between IpAddress and Name
	    doc.line(37, 130, 37, 140); 
	    
	    //Disconnect Msg
	    doc.line(66, 130, 66, 140); 
	    doc.line(82, 135.5, 82, 140); 
	    doc.line(98, 135.5, 98, 140);
	    doc.line(114, 135.5, 114, 140); 
		  
	    //COA Line
	    doc.line(130, 130, 130, 140); 
	    doc.line(148, 135.5, 148, 140);
	    doc.line(162, 135.5, 162, 140);
	    doc.line(178, 135.5, 178, 140);
	    
	    //main line
    	doc.line(66, 135.5, pageWidth-15, 135.5);
	    
    	doc.setFontSize(6);
 	    doc.setTextColor(0); 
 	    doc.setFontType("bold");
 	    doc.text(70, 138.5, "Request");
 	    doc.text(88, 138.5, "Nack");
 	    doc.text(105, 138.5, "Ack");
 	    doc.text(118, 138.5, "Timeout");
 	    
 	    doc.text(134, 138.5, "Request");
	    doc.text(152, 138.5, "Nack");
	    doc.text(168, 138.5, "Ack");
	    doc.text(182, 138.5, "Timeout");
	    
	    var nasESIArray = getNasESIDataDetails();
	  
	    doc.setTextColor(0);  
	    doc.setFontType("normal");
	    var flag=1;
	    
	    var yIndex=146.5;
	    
	    $.each(nasESIArray.esi,function(key,value){
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
	    	doc.text(20, yIndex-2, value);
	    	doc.text(45, yIndex-2, nasESIArray.serverAddress[key].toString() + ':'+nasESIArray.serverPort[key].toString());
	    	
	    	//DM Request
	    	var dmRequestLength = nasESIArray.disconnectReq[key].toString().length;
	    	var charWidth = 1.25;
	    	var str = reverse(nasESIArray.disconnectReq[key].toString());
	    	for(var i= dmRequestLength - 1;i>=0; i--){
	    		doc.text(77 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	//DM Nack
	    	var dmNackLength = nasESIArray.disconnectNack[key].toString().length;
	    	str = reverse(nasESIArray.disconnectNack[key].toString());
	    	for(var i= dmNackLength - 1;i>=0; i--){
	    		doc.text(92 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	//DM Ack
	    	var dmAckLength = nasESIArray.disconnectAck[key].toString().length;
	    	str = reverse(nasESIArray.disconnectAck[key].toString());
	    	for(var i= dmAckLength - 1;i>=0; i--){
	    		doc.text(108 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	//DM Timeout
	    	var dmTimeoutLength = nasESIArray.disconnectTimeout[key].toString().length;
	    	str = reverse(nasESIArray.disconnectTimeout[key].toString());
	    	for(var i= dmTimeoutLength - 1;i>=0; i--){
	    		doc.text(125 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	//COA Request
	    	var dmCoaReqLength = nasESIArray.coaReq[key].toString().length;
	    	str = reverse(nasESIArray.coaReq[key].toString());
	    	for(var i= dmCoaReqLength - 1;i>=0; i--){
	    		doc.text(141.5 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	//COA Nack
	    	var dmCoaNackLength = nasESIArray.coaNack[key].toString().length;
	    	str = reverse(nasESIArray.coaNack[key].toString());
	    	for(var i= dmCoaNackLength - 1;i>=0; i--){
	    		doc.text(156 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	//COA Ack
	    	var dmCoaAckLength = nasESIArray.coaAck[key].toString().length;
	    	str = reverse(nasESIArray.coaAck[key].toString());
	    	for(var i= dmCoaAckLength - 1;i>=0; i--){
	    		doc.text(171 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	
	    	//COA Timeout
	    	var dmCoaTimeoutLength = nasESIArray.coaTimeout[key].toString().length;
	    	str = reverse(nasESIArray.coaTimeout[key].toString());
	    	for(var i= dmCoaTimeoutLength - 1;i>=0; i--){
	    		doc.text(189 - charWidth * i, yIndex-2, str[i]);
	    	}
	    	
	    	doc.setDrawColor(192,192,192); 
	    	doc.line(15, yIndex, pageWidth-15, yIndex);
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

	/** Export Widget Functionality */
	var pageIndex =1;
	$('#exportbtn<%=nasESIRequestSummaryId%>').click(function(){
		 save_nas_chart($('#nasESIRequestSummaryId_<%=nasESIRequestSummaryId%>_').highcharts()); 
	});

	$('#exportbtn_'+<%=nasESIRequestSummaryId%>).click(function(){
		 save_nas_chart($('#nasESIRequestSummaryId_<%=nasESIRequestSummaryId%>_').highcharts()); 
	});

	function reverse(s){
		    return s.split("").reverse().join("");
	}

	function getNasESIDataDetails(){
		    var jResponseData="";
			$.ajax({
				url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getNasESIData',
			    type:'GET',
			    async:false,
			    data:{serverIds:nasESIIds},
			    success: function(esiDataList){
			    	if(esiDataList != null)
			    	 jResponseData=esiDataList;  	
			    }
			}); 
			console.log(jResponseData);
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
<body >
<div id="parent_div" style="overflow: auto;border-color: rgb(192, 192, 192);border-style: solid;border-width: 1px;">
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditNasESIRequestSummary.jsp?widgetId=<%=nasESIRequestSummaryId%>" id="editJsp">

 <%--ProgressBar Div : Start  --%> 
 <div id="progressbarDIV<%=nasESIRequestSummaryId%>" class="progressbar" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>
 <div id="nasESIRequestSummaryId_<%=nasESIRequestSummaryId%>_"  class="widget-class" style="height: 200px;width: 99%;display: table;background-position: center;vertical-align: middle;text-align:center;" align="center">
 </div>
 <div id="nasESIReqStatSummaryId_<%=nasESIRequestSummaryId%>_" style="height: 200px;width: 99%;display: table;background-position: center;vertical-align: middle;text-align:center;" align="center">
 </div>
 <span id="exportbtn_<%=nasESIRequestSummaryId%>"></span>
 
 <%--ProgressBar Div : End  --%>
 </div>
</html>