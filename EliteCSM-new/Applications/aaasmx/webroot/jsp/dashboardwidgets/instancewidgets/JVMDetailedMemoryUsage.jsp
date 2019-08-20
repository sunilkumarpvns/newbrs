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
	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<%-- REQUIRED : This Js is required for to get widget Configuration--%>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.class.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.timer.js"></script>
	
	
	<%
		String pageId = request.getParameter("pageId");
		String detailJVMMemoryWidgetId = request.getParameter("widgetId");
	%>
	
	<script type="text/javascript">

	var widgetTimer_<%=detailJVMMemoryWidgetId%>=new CommonTimer();
	widgetTimer_<%=detailJVMMemoryWidgetId%>.init('freezebtn'+<%=detailJVMMemoryWidgetId%>,<%=detailJVMMemoryWidgetId%>,'JVM_DETAIL_MEMUSAGE');


		$('#freezebtn'+<%=detailJVMMemoryWidgetId%>).click(function(){
			 freeze(widgetTimer_<%=detailJVMMemoryWidgetId%>);
		});

		$('#unfreezebtn'+<%=detailJVMMemoryWidgetId%>).click(function(){
			unfreeze(widgetTimer_<%=detailJVMMemoryWidgetId%>);
		});	


	
    	var colors = Highcharts.getOptions().colors;
		var PS_Eden_Space_color_code = colors[0];
		var PS_Survivor_Space_color_code = colors[5];
		var PS_PermGen_Space_color_code = colors[2];
		var PS_OldGen_Space_color_code = colors[3];

		var seriesName = {
			"psEdenSeries" : {
				"name" : "PS Eden Space",
				"id" : "psEdenSpace",
				"color" : colors[0]
			},
			"psSurvivorSeries" : {
				"name" : "PS Survivor Space",
				"id" : "psSurvivorSpace",
				"color" : colors[5]
			},
			"psOldGenSeries" : {
				"name" : "PS Old Gen Space",
				"id" : "psOldGenSpace",
				"color" : colors[3]
			},
			"psPermGenSeries" : {
				"name" : "PS Perm Gen space",
				"id" : "psPermGenSpace",
				"color" : colors[2]
			}
		};

		Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});
		var DetailMemoryUsageSeries = HSeries.extend({
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
		var DetailMemoryUsageSeriesPointData = HPointData.extend({
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

		var DetailMemoryUsageSeriesData = HSeriesData.extend({
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

		//Helper class to convert JSON Data from server to chart data
		var DetailMemoryUsageWidgetConfigHelper = $.Class.extend({
					init : function() {

					},

					// First create The series
					configMemoryUsageSeries : function(totalMemoryUsage) {

						var memoryUsageSeries = new DetailMemoryUsageSeries();

						var psEdenSpaceSeries = new DetailMemoryUsageSeriesData();
						psEdenSpaceSeries.setName(seriesName.psEdenSeries.name);
						psEdenSpaceSeries.setId(seriesName.psEdenSeries.id);
						psEdenSpaceSeries.setColor(seriesName.psEdenSeries.color);

						var psSurvivorSpaceSeries = new DetailMemoryUsageSeriesData();
						psSurvivorSpaceSeries.setName(seriesName.psSurvivorSeries.name);
						psSurvivorSpaceSeries.setId(seriesName.psSurvivorSeries.id);
						psSurvivorSpaceSeries.setColor(seriesName.psSurvivorSeries.color);

						var psPermGenSpaceSeries = new DetailMemoryUsageSeriesData();
						psPermGenSpaceSeries.setName(seriesName.psPermGenSeries.name);
						psPermGenSpaceSeries.setId(seriesName.psPermGenSeries.id);
						psPermGenSpaceSeries.setColor(seriesName.psPermGenSeries.color);

						var psOldGenSpaceSeries = new DetailMemoryUsageSeriesData();
						psOldGenSpaceSeries.setName(seriesName.psOldGenSeries.name);
						psOldGenSpaceSeries.setId(seriesName.psOldGenSeries.id);
						psOldGenSpaceSeries.setColor(seriesName.psOldGenSeries.color);

						// forming point data

						$.each(totalMemoryUsage,function(i, memoryUsage) {

											//adding pint for eden space 
											var edenSpacePoint = new DetailMemoryUsageSeriesPointData();
											edenSpacePoint.setXPoint((memoryUsage.time));
											edenSpacePoint.setYPoints(memoryUsage.psEdenSpace);
											var edenSpacePointData = edenSpacePoint.buildPointData();
											psEdenSpaceSeries.addPoint(edenSpacePointData);

											// adding point for survivor space
											var survivorSpacePoint = new DetailMemoryUsageSeriesPointData();
											survivorSpacePoint.setXPoint(memoryUsage.time);
											survivorSpacePoint.setYPoints(memoryUsage.psSurvivorSpace);
											var survivorSpacePointData = survivorSpacePoint.buildPointData();
											psSurvivorSpaceSeries.addPoint(survivorSpacePointData);

											//adding point for perm gen space
											var permGenSpacePoint = new DetailMemoryUsageSeriesPointData();
											permGenSpacePoint.setXPoint(memoryUsage.time);
											permGenSpacePoint.setYPoints(memoryUsage.psPermGenSpace);
											var permGenSpacePointData = permGenSpacePoint.buildPointData();
											psPermGenSpaceSeries.addPoint(permGenSpacePointData);

											// adding point for oldgen space
											var oldGenSpacePoint = new DetailMemoryUsageSeriesPointData();
											oldGenSpacePoint.setXPoint(memoryUsage.time);
											oldGenSpacePoint.setYPoints(memoryUsage.psOldGenSpace);
											var oldGenSpacePointData = oldGenSpacePoint.buildPointData();
											psOldGenSpaceSeries.addPoint(oldGenSpacePointData);

										});
						// adding Series Data into Chart series
						memoryUsageSeries.addSeries(psEdenSpaceSeries.buildSeriesData());
						memoryUsageSeries.addSeries(psSurvivorSpaceSeries.buildSeriesData());
						memoryUsageSeries.addSeries(psOldGenSpaceSeries.buildSeriesData());
						memoryUsageSeries.addSeries(psPermGenSpaceSeries.buildSeriesData());
						return memoryUsageSeries.getSeries();
					}
				});
		var detailMemoryUsageWidgetConfigHelper = new DetailMemoryUsageWidgetConfigHelper();
		var chart;

		function updateJVMDetailMemoryUsageData(data) {
			if (data != null) {
				chartLiveMemoryDetailUsageConfig.series = detailMemoryUsageWidgetConfigHelper.configMemoryUsageSeries(data);
				chart = new Highcharts.StockChart(chartLiveMemoryDetailUsageConfig);
			}
		}

		function updateJVMMemoryUsageDetailLiveData(data) {
            console.log("calling updateJVMMemoryUsageDetailLiveData");
			if (data != null ) {
				var psedenseries = chart.get(seriesName.psEdenSeries.id);
				var pssurvivorseries = chart.get(seriesName.psSurvivorSeries.id);
				var psoldgenseries = chart.get(seriesName.psOldGenSeries.id);
				var pspermgenseries = chart.get(seriesName.psPermGenSeries.id);

				$.each(data, function(i, memoryUsage) {

					//adding pint for eden space 
					var edenSpacePoint = new DetailMemoryUsageSeriesPointData();
					edenSpacePoint.setXPoint(memoryUsage.time);
					edenSpacePoint.setYPoints(memoryUsage.psEdenSpace);
					var edenSpacePointData = edenSpacePoint.buildPointData();
					psedenseries.addPoint(edenSpacePointData);

					// adding point for survivor space
					var survivorSpacePoint = new DetailMemoryUsageSeriesPointData();
					survivorSpacePoint.setXPoint(memoryUsage.time);
					survivorSpacePoint.setYPoints(memoryUsage.psSurvivorSpace);
					var survivorSpacePointData = survivorSpacePoint.buildPointData();
					pssurvivorseries.addPoint(survivorSpacePointData);

					//adding point for perm gen space
					var permGenSpacePoint = new DetailMemoryUsageSeriesPointData();
					permGenSpacePoint.setXPoint(memoryUsage.time);
					permGenSpacePoint.setYPoints(memoryUsage.psPermGenSpace);
					var permGenSpacePointData = permGenSpacePoint.buildPointData();
					pspermgenseries.addPoint(permGenSpacePointData);

					// adding point for oldgen space
					var oldGenSpacePoint = new DetailMemoryUsageSeriesPointData();
					oldGenSpacePoint.setXPoint(memoryUsage.time);
					oldGenSpacePoint.setYPoints(memoryUsage.psOldGenSpace);
					var oldGenSpacePointData = oldGenSpacePoint.buildPointData();
					psoldgenseries.addPoint(oldGenSpacePointData);

				});
				chart.redraw();
			}
		}

		//define chart configuration 
		chartLiveMemoryDetailUsageConfig = {
			chart : {
				renderTo : 'detailJVMMemoryWidgetId_'+ '<%=detailJVMMemoryWidgetId%>'+'_',
			        events: {
			            load: function (event) {
			            	hideProgressBarDiv('progressbarDIV'+<%=detailJVMMemoryWidgetId%>);
			            }
			        },
			        type: 'areaspline',
			        zoomType: 'xy'
			    },title: {
		            text: 'JVM Detail Memory Usage',
		            style: {
		                fontSize: '14px'
		            }
		        },
			    scrollbar: {
			        liveRedraw: false,
			        enabled: true
			    },
			    rangeSelector : {
					selected :1,
					inputEnabled : false,
					buttonSpacing : 6,
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
			       legend:{
				       enabled:true
				       },
			        turboThreshold: 200,
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
			                text: 'Detail Memory Usage'
			            }
			        },
			        series: []
			    };


		<%--DO NOT REMOVE : Get widget Configuration here --%>
		
		var detailJVMMemoryWidgetId=<%=detailJVMMemoryWidgetId%>;
		var detailConfObject = getWidgetConfiguration(detailJVMMemoryWidgetId);  <%-- Use detailConfObject to retrive configuration --%>
		var liveMemoryDetailChartName = "";
		var liveDetailServerList =""; 
		
		var internval=1;//default interval value
		if(typeof detailConfObject != 'undefined'){
			interval=detailConfObject.get("REFRESHINTERVAL");
			daysPreviously=detailConfObject.get("DAYSPREVIOUSLY");
			liveDetailServerList = detailConfObject.get("ELITEAAAINSTANCES");
			liveMemoryDetailChartName=detailConfObject.get("NAME");
		}
		
		var id='detailJVMMemoryWidgetId_'+<%=detailJVMMemoryWidgetId%>+'_';

		// chart data fetch logic
			var data = {
					header : {
						id : 'detailJVMMemoryWidgetId_'+<%=detailJVMMemoryWidgetId%>+'_',
						type : "JVM_DETAIL_MEMUSAGE"
					},
					body : {
						interval : interval
					}
			};
			var intervalCounter = 0;
			var widgetHandler  = {
					renderData : function(data) {
						updateJVMDetailMemoryUsageData(data);
					},
					updateData : function(data) {
						updateJVMMemoryUsageDetailLiveData(data);
					}
			};
			getDashBoardSocket().register(new Widget(id, widgetHandler));
			getDashBoardSocket().sendRequest(data);
			
			  /** Export As PDF Function**/
		    var EXPORT_WIDTH = 700;
			var canvas;
			var svg;
			function save_chart_jvm_details(detailChart) {
			
			    var render_width = EXPORT_WIDTH;
			    var render_height = render_width * detailChart.chartHeight / detailChart.chartWidth
			
			    // Get the cart's SVG code
			    svg = detailChart.getSVG({
			        exporting: {
			            sourceWidth: detailChart.chartWidth,
			            sourceHeight: detailChart.chartHeight
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
		        
					var jvmDetailChartName= liveMemoryDetailChartName;
				
					canvas.style.backgroundColor='white';
					var doc = new jsPDF();
				
					doc.setProperties({
			      	  title: jvmDetailChartName,
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
				    doc.rect(3, 25, pageWidth-10, 5, 'F');
				    
				    doc.setDrawColor(0);
				    doc.setFillColor(1,81,152);
				    doc.rect(5, 122, pageWidth-10, 5, 'F'); 
				    
				    doc.setFontSize(7.5);
				    doc.setTextColor(255);  
				    
				    doc.text(82, 28.5, jvmDetailChartName + " Report");
				    doc.text(79, 125.5, jvmDetailChartName + " Details");
				    
				    //ESI Name - Rectangle
				    doc.setDrawColor(0);
				    doc.setFillColor(217,230,246);
				    doc.rect(5, 130, pageWidth-10, 8, 'F');
			    
				    doc.setFontSize(6.5);
				    doc.setTextColor(0); 
				    doc.setFontType("bold");
				    
				    doc.text(6, 133, "Server Name");
				    
				    doc.text(25, 133, " Create Time ");
				    
				    doc.text(55, 133, "PS Eden Space ");
				    
				    doc.text(95, 133, "PS Survivor Space");
				    
				    doc.text(138,133,"PS Old Gen Space"); //Replace Tenured Gen with PS Old Gen Space
				    
				    doc.text(175, 133, "PS Perm Gen Space");
				   
				    
				    //set font style for sub header
				    doc.setFontSize(6);
				    doc.setTextColor(0); 
				    doc.setFontType("bold");
				    
				    //Code Cache
				    doc.text(50, 137, "Min");
				    doc.text(61, 137, "Max");
				    doc.text(72, 137, "Avg");
				    
				    //Survivor Space
				    doc.text(90, 137, "Min");
				    doc.text(101, 137, "Max");
				    doc.text(113, 137, "Avg");
				    
				    //Perm Gen [shared-rw]
				    doc.text(132, 137, "Min");
				    doc.text(143, 137, "Max");
				    doc.text(155, 137, "Avg");
				    //Perm Gen [shared-ro]
					
				    //Tenured Gen
				    doc.text(172, 137, "Min");
				    doc.text(184, 137, "Max");
				    doc.text(194, 137, "Avg");
					
				    var detailMemoryUsageArray = getLiveDetailMemoryDetails(); 
				    
				    doc.setTextColor(0);  
				    doc.setFontType("normal");
				    var isInit=false;
				    
				    var yIndex=144;
			        var trackServerName ='';
			        var createTime='';
			        var preservYIndex = 0;
			        var isPageChanged=false;
			        $.each(detailMemoryUsageArray,function(key,value){
			        	if (yIndex >= pageHeight-10)
				    	{
				    		isPageChanged=true;
				    		doc.addPage('a4','p');
					    	yIndex = 10; // Restart height position
				    		pageWidth= doc.internal.pageSize.width;
				    		pageHeight= doc.internal.pageSize.height;
				    	    pageIndex++;
				    	    doc.text(pageWidth-20,pageHeight - 5, "Page : "+pageIndex );
				            doc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);
				            doc.setDrawColor(192,192,192); 
				    	}    	
				    	doc.setFontSize(6);
				    	doc.setFontType("normal");
				    	
				    	if(trackServerName.length == 0){
				    		trackServerName=value.serverName;
				    		var splitTitle = doc.splitTextToSize(value.serverName, 6);
					    	doc.text(6, yIndex-2, splitTitle);
				    	}
				    	
				    	if(trackServerName != value.serverName){
				    		trackServerName = value.serverName;
				    		var splitTitle = doc.splitTextToSize(value.serverName, 6);
					    	doc.text(6, yIndex-2, splitTitle);
				    		doc.setDrawColor(192,192,192); 
				    	}
				    	
				    	//Create Date
				    	doc.text(21, yIndex-2, value.createTime); 
			    		doc.setDrawColor(192,192,192); 
			    	
			    		//Eden Space Min Value
 						if(value.edenSpaceMin){
 			    			var minValueLength = value.edenSpaceMin.toString().length;
 					    	var charWidth = 1.25;
 							var str = reverse(value.edenSpaceMin.toString());
 							for(var i = minValueLength-1 ; i >= 0 ; i--){			  
 								doc.text(53- charWidth * i,yIndex-2, str[i]);
 							} 
 						}else{
 							doc.text(53,yIndex-2, "0");
 						}
			    		
		    			if(value.edenSpaceMax){
		    				//Eden Space Max Value
			    			var maxValueLength = value.edenSpaceMax.toString().length;
					    	charWidth = 1.25;
							str = reverse(value.edenSpaceMax.toString());
							for(var i = maxValueLength-1 ; i >= 0 ; i--){			  
								doc.text(64- charWidth * i,yIndex-2, str[i]);
							} 
		    			}else{
		    				doc.text(64,yIndex-2, "0");
		    			}
		    			
						if(value.edenSpanceAvg){
							//Eden Space Avg Value
			    			var avgValueLength = value.edenSpanceAvg.toString().length;
					    	charWidth = 1.25;
							str = reverse(value.edenSpanceAvg.toString());
							for(var i = avgValueLength-1 ; i >= 0 ; i--){			  
								doc.text(75- charWidth * i,yIndex-2, str[i]);
							} 
						}else{
							doc.text(75-1.8,yIndex-2, "0.0");
						}
						
						if(value.survivorSpaceMin){
							//Survivor Space Min Value
			    			var minValueLength = value.survivorSpaceMin.toString().length;
					    	var charWidth = 1.25;
							var str = reverse(value.survivorSpaceMin.toString());
							for(var i = minValueLength-1 ; i >= 0 ; i--){			  
								doc.text(93- charWidth * i,yIndex-2, str[i]);
							} 
						}else{
							doc.text(93,yIndex-2, "0");
						}
						
						if(value.survivorSpaceMax){
							//Survivor Space Max Value
			    			var maxValueLength = value.survivorSpaceMax.toString().length;
					    	charWidth = 1.25;
							str = reverse(value.survivorSpaceMax.toString());
							for(var i = maxValueLength-1 ; i >= 0 ; i--){			  
								doc.text(104- charWidth * i,yIndex-2, str[i]);
							} 
						}else{
							doc.text(104,yIndex-2, "0");
						}
		    			
						if(value.survivorSpaceAvg){
							//Survivor Space Avg Value
			    			var avgValueLength = value.survivorSpaceAvg.toString().length;
					    	charWidth = 1.25;
							str = reverse(value.survivorSpaceAvg.toString());
							for(var i = avgValueLength-1 ; i >= 0 ; i--){			  
								doc.text(116- charWidth * i,yIndex-2, str[i]);
							} 
						}else{
							doc.text(116-1.8,yIndex-2,"0.0");
						}
						
						if(value.oldGenSpaceMin){
							var minValueLength = value.oldGenSpaceMin.toString().length;
					    	var charWidth = 1.25;
							var str = reverse(value.oldGenSpaceMin.toString());
							for(var i = minValueLength-1 ; i >= 0 ; i--){			  
								doc.text(135- charWidth * i,yIndex-2, str[i]);
							} 
						}else{

							doc.text(135,yIndex-2, "0");
						}
						
						if(value.oldGenSpaceMax){
							//Tenured Gen Max Value
			    			var maxValueLength = value.oldGenSpaceMax.toString().length;
					    	charWidth = 1.25;
							str = reverse(value.oldGenSpaceMax.toString());
							for(var i = maxValueLength-1 ; i >= 0 ; i--){			  
								doc.text(146- charWidth * i,yIndex-2, str[i]);
							} 
						}else{
							doc.text(146,yIndex-2, "0");
						}
		    			
						
						if(value.oldGenSpaceAvg){
							//Tenured Gen Avg Value
			    			var avgValueLength = value.oldGenSpaceAvg.toString().length;
					    	charWidth = 1.25;
							str = reverse(value.oldGenSpaceAvg.toString());
							for(var i = avgValueLength-1 ; i >= 0 ; i--){			  
								doc.text(158- charWidth * i,yIndex-2, str[i]);
							} 
						}else{
							doc.text(158-1.8,yIndex-2,"0.0");
						}
						
						
						if(value.permGenSpaceMin){
							//Tenured Gen Min Value
			    			var minValueLength = value.permGenSpaceMin.toString().length;
					    	var charWidth = 1.25;
							var str = reverse(value.permGenSpaceMin.toString());
							for(var i = minValueLength-1 ; i >= 0 ; i--){			  
								doc.text(175- charWidth * i,yIndex-2, str[i]);
							} 
						}else{
							doc.text(175,yIndex-2,"0");
						}
						
		    			if(value.permGenSpaceMax){
		    				//Tenured Gen Max Value
			    			var maxValueLength = value.permGenSpaceMax.toString().length;
					    	charWidth = 1.25;
							str = reverse(value.permGenSpaceMax.toString());
							for(var i = maxValueLength-1 ; i >= 0 ; i--){			  
								doc.text(187- charWidth * i,yIndex-2, str[i]);
							} 
		    			}else{
		    				doc.text(187,yIndex-2,"0");
		    			}
						
		    			if(value.permGenSpaceAvg){
							//Tenured Gen Avg Value
			    			var avgValueLength = value.permGenSpaceAvg.toString().length;
					    	charWidth = 1.25;
					    	console.log(value.permGenSpaceAvg.toString());
							str = reverse(value.permGenSpaceAvg.toString());
							for(var i = avgValueLength-1 ; i >= 0 ; i--){			  
								doc.text(197- charWidth * i,yIndex-2, str[i]);
							} 
		    			}else{
		    				doc.text(197-1.8,yIndex-2,"0.0");
		    			}
						
				    	doc.setDrawColor(192,192,192); 
				    	doc.line(5, yIndex-6, 5, yIndex); 
				    	doc.line(20, yIndex-6, 20, yIndex);
				    	doc.line(42, yIndex-6, 42, yIndex);
				    	doc.line(82, yIndex-6, 82, yIndex);
				    	doc.line(122, yIndex-6, 122, yIndex);
				    	doc.line(162, yIndex-6, 162, yIndex);
				    	doc.line(pageWidth-5, yIndex-6, pageWidth-5, yIndex);
				    
				    	doc.line(20, yIndex-6, pageWidth-5, yIndex-6); 
				    	
				    	
				    	yIndex=yIndex+5;
			        });
				    
				    doc.line(5, yIndex-5, pageWidth-5, yIndex-5); 
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
				    saveAs(blob, jvmDetailChartName+'.pdf');
				    console.log('aftre save');
			        
			    };
			    image.src = 'data:image/svg+xml;base64,' + window.btoa(svg);
			}
		    
		    var pageIndex =1;
		    $('#exportbtn<%=detailJVMMemoryWidgetId%>').click(function(){
		   		 save_chart_jvm_details($('#detailJVMMemoryWidgetId_<%=detailJVMMemoryWidgetId%>_').highcharts()); 
		    });
		    
		    $('#exportbtn_'+<%=detailJVMMemoryWidgetId%>).click(function(){
		   	 	save_chart_jvm_details($('#detailJVMMemoryWidgetId_<%=detailJVMMemoryWidgetId%>_').highcharts()); 
		    });
		    
		    function reverse(s){
			    return s.split("").reverse().join("");
			}
		    
		    function getLiveDetailMemoryDetails(){
			    var jResponseData="";
				$.ajax({
					url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getLiveDetailMemoryDetails',
				    type:'GET',
				    async:false,
				    data:{serverIds:liveDetailServerList},
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
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/instancewidgets/EditJVMDetailedMemoryUsage.jsp?widgetId=<%=detailJVMMemoryWidgetId%>" id="editJsp">
<%--ProgressBar Div : Start  --%>

  <div id="exportingdiv<%=detailJVMMemoryWidgetId%>" style="width: 100%;display: table;background-position: center;vertical-align: middle;text-align:right;padding-top:10px;" align="center">
  	<input type="button"  id="freezebtn<%=detailJVMMemoryWidgetId%>" name="button" value="Freeze" class="restart-btn" title="Freeze" align="right"/> 
 	<input type="button"  id="unfreezebtn<%=detailJVMMemoryWidgetId%>" name="button" value="Unfreeze" class="restart-btn" title="Unfreeze" align="right"/>
 	<input type="button"  id="exportbtn<%=detailJVMMemoryWidgetId%>" name="button" value="Export.." class="restart-btn" title="Unfreeze" align="right" style="margin-right: 20px;'"/>
 </div> 
 
 <div id="progressbarDIV<%=detailJVMMemoryWidgetId%>" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>
 
 <%--Trigger Export As PDF menu --%> 
<span id="exportbtn_<%=detailJVMMemoryWidgetId%>"></span>

 <%--ProgressBar Div : End  --%>
 <div id="detailJVMMemoryWidgetId_<%=detailJVMMemoryWidgetId%>_" class="detailJVMMemory<%=pageId%> widget-class" style="height: 300px; padding-bottom: 3px;overflow: auto;width:inherit;"></div>
</body>
</html>