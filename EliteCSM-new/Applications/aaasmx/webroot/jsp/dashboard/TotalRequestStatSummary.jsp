<html>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Total Request Status Summary</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.class.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/chart-generic-class.js"></script>
<script>
var interval = <%=request.getParameter("interval") != null ? request.getParameter("interval") : 5%>;
//todo ec need to generalize it 
var colors = Highcharts.getOptions().colors;


<%--
   Description :  Chart Data wrapper classes for Total Request Statistics summary
--%>

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


<%--
     Description : Chart Data wrapper classes for ESI summary
--%>

//detailed esi statistics data
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

<%--
    Description :  Total Request Statistics  Chart Data Configuration helper class
--%>
var TotalReqStatChartDataConfigHelper=$.Class.extend({
	 init: function(){
	 },
	 configESIStatisticsSeries : function(totalESIReqChartData){
		 var esiStatSeries =new HESIStatisticsSeries();
		 
		 var acSeries=new HESIStatisticsSeriesData();
		 acSeries.setPointList(totalESIReqChartData.accessChallenge);
		 acSeries.setName("Accept-Challenge");
		 acSeries.setColor(colors[0]);
		 var acSeriesData=acSeries.buildSeriesData();
		 esiStatSeries.addSeries(acSeriesData);
		 
		 var arSeries=new HESIStatisticsSeriesData();
		 arSeries.setPointList(totalESIReqChartData.accessReject);
		 arSeries.setName("Accept-Reject");
		 arSeries.setColor(colors[5]);
		 var arSeriesData=arSeries.buildSeriesData();
		 esiStatSeries.addSeries(arSeriesData);
		 
		 var aaSeries=new HESIStatisticsSeriesData();
		 aaSeries.setPointList(totalESIReqChartData.accessAccept);
		 aaSeries.setName("Access-Accept");
		 aaSeries.setColor(colors[2]);
		 var aaSeriesData=aaSeries.buildSeriesData();
		 esiStatSeries.addSeries(aaSeriesData);
		 
		 var rrSeries=new HESIStatisticsSeriesData();
		 rrSeries.setPointList(totalESIReqChartData.requestDrop);
		 rrSeries.setName("Request-Drops");
		 rrSeries.setColor(colors[3]);
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
		 // make esiseries object
		 var esiSeries=new HESISeries();
		 //make for esi series data object
		 
		 /*
		   name: 'Accept-Challenge',
	       color: '#4572A7',
	       type: 'column',
	       yAxis: 1,
	       data: jNumACDetailedDataArray
		 */
		 var acSeriesData=new HESISeriesData();
		 acSeriesData.setName("Accept-Challenge");
		 acSeriesData.setColor("#4572A7");
		 acSeriesData.setType("column");
		 acSeriesData.setYAxis(1);
		 
		 /*
	              name: 'Accept-Reject',
	              type: 'spline',
	              color: '#AA4643',
	              yAxis: 2,
	              data: jNumARDetailedDataArray
	              
		 */
		 
		 
		 var arSeriesData=new HESISeriesData();
         arSeriesData.setName("Accept-Reject");
         arSeriesData.setColor("#AA4643");
         arSeriesData.setType("spline");
         arSeriesData.setYAxis(2);
		 
		 /*
		   name: 'Access-Accept',
	       color: '#89A54E',
	       yAxis: 3,
	       type: 'spline',
	       data: jNumAADetailedDataArray
		 */
		 var aaSeriesData=new HESISeriesData();
		 aaSeriesData.setName("Access-Accept");
		 aaSeriesData.setColor("#89A54E");
		 aaSeriesData.setType("spline");
		 aaSeriesData.setYAxis(3);
		 
		
		 
		 /*
		          name: 'Request-Drops',
	              color: '#4572A7',
	              type: 'spline',
	              yAxis: 1,
	              data: jNumRDDetailedDataArray
		 
		 */
		 var rdSeriesData=new HESISeriesData();
		 rdSeriesData.setName("Request-Drops");
		 rdSeriesData.setColor("#4572A7");
		 rdSeriesData.setType("spline");
		 rdSeriesData.setYAxis(1);
		 
		 
		 
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
	 }	 
});


<%-- 
   Description :  Data class that is use to convert json data received from websocket  into TotalESIReqChartData  object
--%>


var TotalESIReqChartData = $.Class.extend({
	  init: function(){
	  },
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


<%--
    Description :  Instantiate  TotalESIReqChartData and TotalReqStatChartDataConfigHelper class object
 --%>
var totalESIReqChartData=new TotalESIReqChartData();
var totalReqStatChartDataConfigHelper=new TotalReqStatChartDataConfigHelper();
//var categories=[];
//var data;

<%--
   Description : Highchart option configuration for ESI Chart
--%>
var jESIoptions=
	{
		chart: {
            zoomType: 'xy'
        },
        title : {
		    text : null
		},
        xAxis: {
        	type: 'datetime',
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
        yAxis: [{ // Primary yAxis
            labels: {
                formatter: function() {
                    return this.value ;
                },
                style: {
                    color: '#89A54E'
                }
            },
            title: {
                text: 'Accept-Challenge',
                style: {
                    color: '#89A54E'
                }
            },
            opposite: true

        }, { // Secondary yAxis
            gridLineWidth: 0,
            title: {
                text: 'Accept-Reject',
                style: {
                    color: '#4572A7'
                }
            },
            labels: {
                formatter: function() {
                    return this.value;
                },
                style: {
                    color: '#4572A7'
                }
            }

        }, { // Tertiary yAxis
            gridLineWidth: 0,
            title: {
                text: 'Access-Accept',
                style: {
                    color: '#AA4643'
                }
            },
            labels: {
                formatter: function() {
                    return this.value;
                },
                style: {
                    color: '#AA4643'
                }
            },
            opposite: true
        },{ // Tertiary yAxis
            //gridLineWidth: 0,
            title: {
                text: 'Request-Drops',
                style: {
                    color: '#4572A7'
                }
            },
            labels: {
                formatter: function() {
                    return this.value;
                },
                style: {
                    color: '#AA4643'
                }
            },
            opposite: true
        }
        ],
        plotOptions: {
        	dataLabels: {
                enabled: true,
                color: colors[0],
                style: {
                    fontWeight: 'bold'
                },
                formatter: function() {
                    return this.y +'%';
                }
            }
        },
        legend: {
            layout: 'vertical',
            align: 'left',
            x: 120,
            verticalAlign: 'top',
            y: 80,
            floating: true,
            backgroundColor: '#FFFFFF'
        },
        tooltip: {
            shared:true
        },
        series: [],
        exporting: {
            enabled: true,
            buttons: {
	            refresh: {
	                text: 'Refresh',
	                onclick: function () {
	                    //Get current data of esi and render it on graph
	                    
	                }
	            },
	            back: {
	                text: 'Back',
	                onclick: function () {
	                	var chart = new Highcharts.Chart(jStackedColumnoptions);
	                	//4. todo ec make configESIStatisticsCategories
	                	var esiStatCategories=totalReqStatChartDataConfigHelper.configESIStatisticsCategories(totalESIReqChartData);
	                	chart.xAxis[0].setCategories(esiStatCategories);
	                }
	            }
            }
        }
		
	};
	
<%--
     Description : Highchart option configuration for Total Request Statistic summary Chart 
--%>	
var jStackedColumnoptions=
           {
		       	chart: {
  			 		type: 'column',
    		 		marginBottom : 80,
             		zoomType: 'xy',
             		renderTo: 'statasticSummay'
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
		                        	//3. make configESISeries
		                        	var jESIDataArray=totalESIReqChartData.totalReqDataArray[this.x];
		                        	jESIoptions.series=totalReqStatChartDataConfigHelper.configESISeries(jESIDataArray);
		                        	$('#statasticSummay').highcharts(jESIoptions);
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
				series: []
};


<%--
    Description :  This method is called to process and render data to chart received by websocket for Total Request Statistics Summary Chart 
--%> 
function updateTotalReqStatSummaryData(data){
	    if(data != null){
	    	
	    	totalESIReqChartData=new TotalESIReqChartData();
		    totalESIReqChartData.setTotalReqDataArray(data.totalReqDataArray);
		    totalESIReqChartData.setESI(data.esi);
			totalESIReqChartData.setAccessChallenge(data.accessChallenge);
			totalESIReqChartData.setAccessReject(data.accessReject);
			totalESIReqChartData.setAccessAccept(data.accessAccept);
			totalESIReqChartData.setRequestDrop(data.requestDrop);
			totalESIReqChartData.setEpochTime(data.epochTime);
			
			// 1. build configESIStatisticsSeries
	        jStackedColumnoptions.series=totalReqStatChartDataConfigHelper.configESIStatisticsSeries(totalESIReqChartData);
	        var chart = new Highcharts.Chart(jStackedColumnoptions);
	        // 2. build configESIStatisticsCategories
	        var esiStatCategories=totalReqStatChartDataConfigHelper.configESIStatisticsCategories(totalESIReqChartData);
	        chart.xAxis[0].setCategories(esiStatCategories);
	    }  
 }

$(document).ready(function(){ 
	var data = {
			header : {
				id : "TOTALREQSTATSUMMARY",
				type : "TOTALREQSTATSUMMARY"
			},
			body : {
				interval : interval
			}
	};
	var intervalCounter = 0;
	var widgetHandler  = {
			renderData : function(data) {
				 updateTotalReqStatSummaryData(data);
			},
			updateData : function(data) {
				updateTotalReqStatSummaryData(data);
			}
	};
	dashBoardWidgets.register(new Widget("TOTALREQSTATSUMMARY", widgetHandler));
	dashBoardWidgets.sendRequest(data);
	});
</script>

<body>
<div id="statasticSummay" style="height: 290px; min-width: 250px"></div>
</body>
</html>
