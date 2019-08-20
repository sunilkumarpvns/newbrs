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
	
	<%
		String pageId = request.getParameter("pageId");
		String liveMemoryWidgetId = request.getParameter("widgetId");
	%>
	
	<script type="text/javascript">

  var widgetTimer_<%=liveMemoryWidgetId%>=new CommonTimer();
  widgetTimer_<%=liveMemoryWidgetId%>.init('freezebtn'+<%=liveMemoryWidgetId%>,<%=liveMemoryWidgetId%>,'MEMUSAGE');


  	$('#freezebtn'+<%=liveMemoryWidgetId%>).click(function(){
		 freeze(widgetTimer_<%=liveMemoryWidgetId%>);
	});

	$('#unfreezebtn'+<%=liveMemoryWidgetId%>).click(function(){
		unfreeze(widgetTimer_<%=liveMemoryWidgetId%>);
	});	

	

 var colors = Highcharts.getOptions().colors;
	Highcharts.setOptions({
	    global: {
	        useUTC: false
	    }
	});
	var MemoryUsageSeries=HSeries.extend({
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
	var MemoryUsageSeriesPointData=HPointData.extend({
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

	var MemoryUsgaeSeriesData=HSeriesData.extend({
		init : function(){
			this._super();
			this.id='';
			
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
		setId:function(id){
             this.id=id;
			},
		getId:function(){
           return this.id;
		},
		buildSeriesData : function(){
			var seriesData={
					   "name": this.name,
				       "color": this.color,
				       "yAxis": this.yAxis,
				       "type": this.type,
				       "data": this.pointList,
				       "id":this.id
	        };
		   return seriesData;	
		}
	});
	
	var ServerWiseMemoryUsageData = $.Class.extend({
		  init: function(){
                  this.name='';
                  this.epochtime=[];
                  this.memoryusage=[];
		  },
		  setEpochTime:function(epochTime){
			  this.epochtime=epochTime;//number
		  },
		  setMemoryUsage:function(memoryusage){
			  this.memoryusage=memoryusage;
		  },
		  setName : function(name){
	          this.name=name;		  
		  },
	      getName:function(){
				return this.name;
		  },
		  getEpochTime:function(){
			  return this.epochtime; 
		  },
		  getMemoryUsage:function(){
             return this.memoryusage;
         }
	});

     var TotalMemoryUsageData=$.Class.extend({
		  init: function(){
              this.ServerWiseMemoryUsageData=[];
	  },
	  setServerWiseMemoryUsageData:function(serverwisememoryusagedata){
		  this.ServerWiseMemoryUsageData=serverwisememoryusagedata;//number
	  },
	  addServerWiseMemoryUsageData:function(serverMemoruData){
		  this.ServerWiseMemoryUsageData.push(serverMemoruData);
	  },
	  getServerWiseMemoryUsageData:function(){
         return this.ServerWiseMemoryUsageData;
     }
});
	<%--Helper class to convert JSON Data from server to chart data--%>
      var MemoryUsageWidgetConfigHelper=$.Class.extend({
		 init: function(){
			 
		 },
		 configMemoryUsageSeries : function(totalMemoryUsage){
		 	var memoryUsageSeries =new MemoryUsageSeries();
		    var i=0;
		 	$.each(totalMemoryUsage.getServerWiseMemoryUsageData(),function(i,memoryUsage){
             var usageSeries=new MemoryUsgaeSeriesData();
             usageSeries.setName(memoryUsage.getName());
             usageSeries.setId(memoryUsage.getName());
             usageSeries.setColor(colors[i]);
             usageSeries.setType("spline");
             //forming point data
             $.each(memoryUsage.getEpochTime(),function(i,tempMemoryUsage){
		             var pointData=new MemoryUsageSeriesPointData();
		             pointData.setXPoint(tempMemoryUsage);
		             pointData.setYPoints(memoryUsage.getMemoryUsage()[i]);
		             var jsonPointData=pointData.buildPointData();
		             usageSeries.addPoint(jsonPointData);
            });
             //pushing series data into series
             var usageSeriesData=usageSeries.buildSeriesData();
             memoryUsageSeries.addSeries(usageSeriesData);
             
			});
		 	return memoryUsageSeries.getSeries();	
		 }
	}); 
	var memoryUsageWidgetConfigHelper=new MemoryUsageWidgetConfigHelper();
    var chart; 
   
	function updateTotalMemoryUsageData(data){
		    if(data !== null){
				var totalMemoryUsage=new TotalMemoryUsageData();
			    $.each(data.memoryUsageData,function(i,memoryUsage){
		        	  totalMemoryUsage.addServerWiseMemoryUsageData(getServerData(memoryUsage));
		         });
			  
			  chartLiveMemoryusageConfig.series=memoryUsageWidgetConfigHelper.configMemoryUsageSeries(totalMemoryUsage);
		      chart = new Highcharts.StockChart(chartLiveMemoryusageConfig);
		    }  
	 }

	 function updateTotalMemoryUsageLiveData(data){
		 console.log(" calling updateTotalMemoryUsageLiveData");
		  if(data !== null){
				var totalMemoryUsageLiveData=new TotalMemoryUsageData();
			    $.each(data.memoryUsageData,function(i,memoryUsage){
			    	  totalMemoryUsageLiveData.addServerWiseMemoryUsageData(getServerData(memoryUsage));
		         });
		    }  
		 	    var i=0;
			 	$.each(totalMemoryUsageLiveData.getServerWiseMemoryUsageData(),function(i,memoryUsage){
					//getting series from chart object
			     var usageSeries=chart.get(memoryUsage.getName());
	             //forming point data
	             $.each(memoryUsage.getEpochTime(),function(i,tempMemoryUsage){
	                     var pointData=new MemoryUsageSeriesPointData();
			             pointData.setXPoint(tempMemoryUsage);
			             pointData.setYPoints(memoryUsage.getMemoryUsage()[i]);
			             var jsonPointData=pointData.buildPointData();
			             // adding point into series
			             usageSeries.addPoint(jsonPointData,false, true,true);
			       		 
	            });
	          }); 
	 			chart.redraw();
			}

	
		function getServerData(memoryUsage) {
			var serverWiseMemoryUsage1 = new ServerWiseMemoryUsageData();
			serverWiseMemoryUsage1.setName(memoryUsage.serverId);
			serverWiseMemoryUsage1.setEpochTime(memoryUsage.epochTime);
			serverWiseMemoryUsage1.setMemoryUsage(memoryUsage.memoryUsed);
			return serverWiseMemoryUsage1;
		}

		chartLiveMemoryusageConfig = {
			chart : {
				renderTo : 'liveMemoryUsageId_' + '<%=liveMemoryWidgetId%>'+'_',
		    	events : {
                    load: function(event) {
                    	hideProgressBarDiv('progressbarDIV'+<%=liveMemoryWidgetId%>);
                    }
         		},
         		zoomType: 'xy'
		    },
		    legend: {
	            enabled: true,
	            layout: 'horizontal',
	            verticalAlign: 'bottom',
	            shadow: true
	        },
		    scrollbar : {
				liveRedraw: false,
				enabled :true
			}  ,
			    tooltip: {
			    	formatter: function() {
			    		var s = '<b>'+ Highcharts.dateFormat('%a,%e %b %Y, %H:%M', this.x) +'</b>';	
						$.each(this.points, function(i, point) {
							s += '<br/>'+point.series.name+'='+ (point.y/1024).toFixed(2) +' Mb';
						});
		           	return s;
					}
			 } , 
			    rangeSelector : {
				selected :1,
				inputEnabled : false,
				buttonSpacing : 3,
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
			turboThreshold:200,
			title : {
				text : 'Live Memory Usage',
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
			series : []
		};

	
		<%--DO NOT REMOVE : Get widget Configuration here --%>
		
		var liveMemoryWidgetId=<%=liveMemoryWidgetId%>;
		var liveServerConfObj = getWidgetConfiguration(liveMemoryWidgetId);  <%-- Use liveServerConfObj to retrive configuration --%>
		
		var internval=1;//default interval value
		var liveMemoryChartName = "";
		var liveServerList =""; 
		
		if(typeof liveServerConfObj != 'undefined'){
			interval=liveServerConfObj.get("REFRESHINTERVAL");
			daysPreviously=liveServerConfObj.get("DAYSPREVIOUSLY");
			liveServerList = liveServerConfObj.get("ELITEAAAINSTANCES");
			liveMemoryChartName=liveServerConfObj.get("NAME");
		}
		
		var id='liveMemoryUsageId_'+<%=liveMemoryWidgetId%>+'_';

			<%-- chart data fetch logic--%>
			
			var data = {
					header : {
						id : 'liveMemoryUsageId_'+<%=liveMemoryWidgetId%>+'_',
						type : "MEMUSAGE"
					},
					body : {
						interval : interval,
						aaaServerIds:liveServerList
					}
			};
			var intervalCounter = 0;
			var widgetHandler  = {
					renderData : function(data) {					
						updateTotalMemoryUsageData(data);
					},
					updateData : function(data) {
						updateTotalMemoryUsageLiveData(data);
					}
			};
			getDashBoardSocket().register(new Widget(id, widgetHandler));
			getDashBoardSocket().sendRequest(data);

		    /** Export As PDF Function**/
		    var EXPORT_WIDTH = 700;
			var canvas;
			var svg;
			function save_chart_live_memory_widget(chart) {
			
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
		        
					var chartName= liveMemoryChartName;
				
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
				    doc.rect(15, 130, pageWidth-30, 8, 'F');
			    
				    doc.setFontSize(6.5);
				    doc.setTextColor(0); 
				    doc.setFontType("bold");
				    
				    doc.text(16, 133, "Server Name ");
				    
				    doc.text(36, 133, " Create Time ");
				    
				    doc.text(90, 133, "Heap Memory ");
				    
 					doc.text(70,137,"Min Value");
				    
				    doc.text(92,137,"Max Value");
				    
				    doc.text(116,137," Used ");
				    
				    doc.text(155, 133, "Non Heap Memory");
				    
					doc.text(135,137,"Min Value");
				    
				    doc.text(158,137,"Max Value");
				    
				    doc.text(182,137," Used ");
				    
				    var liveCPUArray = getLiveMemoryDetails(); 
				    
				    console.log("Live Memory Usage : "+liveCPUArray);
				  
				    doc.setTextColor(0);  
				    doc.setFontType("normal");
				    var flag=1;
				    
				    var yIndex=143.5;
			        var trackServerName ='';
			        var trackPoolType='';
			        var createTime = '';
			        var isPageChanged=false;
				    $.each(liveCPUArray.serverName,function(key,value){
				    	if (yIndex >= pageHeight-10)
				    	{
				    		isPageChanged=true;
				    		doc.line(15, yIndex-5, 60, yIndex-5); 
				    		doc.addPage('a4','p');
					    	yIndex = 10; // Restart height position
				    		pageWidth= doc.internal.pageSize.width;
				    		pageHeight= doc.internal.pageSize.height;
				    	    pageIndex++;
				    	    doc.text(pageWidth-20,pageHeight - 5, "Page : "+pageIndex );
				            doc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);
				            
				            doc.setDrawColor(192,192,192); 
				    		doc.line(15, yIndex-6, pageWidth-15, yIndex-6);
				    	}    	
				    	doc.setFontSize(6);
				    	doc.setFontType("normal");
				    	
				    	if(trackServerName.length == 0){
				    		trackServerName=value;
				    		doc.text(16, yIndex-2, value);
				    	}
				    	
				    	if(trackServerName != value){
				    		trackServerName=value;
				    		doc.text(16, yIndex-2, value);
				    		doc.setDrawColor(192,192,192); 
				    		doc.line(15, yIndex-5, 35, yIndex-5);
				    	}
				    	
				    	 if(createTime.length == 0){
				    		 createTime=liveCPUArray.strCreateTime[key].toString();
				    		 doc.text(37, yIndex-2, liveCPUArray.strCreateTime[key].toString());
				    	}
				    	
				    	if(createTime != liveCPUArray.strCreateTime[key].toString()){
				    		createTime = liveCPUArray.strCreateTime[key].toString();
				    		doc.text(37, yIndex-2, liveCPUArray.strCreateTime[key].toString());
				    		doc.setDrawColor(192,192,192); 
				    		if(isPageChanged == false){
				    			doc.line(35, yIndex-5, 60, yIndex-5);
				    		}else{
				    			isPageChanged = false;
				    		}
				    		
				    	}
				    	
				    	var minHeapUsedLength = liveCPUArray.heapUsedMinVal[key].toString().length;
				    	var charWidth = 1.25;
						var str = reverse(liveCPUArray.heapUsedMinVal[key].toString());
						for(var i = minHeapUsedLength-1 ; i >= 0 ; i--){			  
							doc.text(80- charWidth * i, yIndex-2, str[i]);
						}
				    	
						var maxHeapUsedValueLength = liveCPUArray.heapUsedMaxVal[key].toString().length;
				    	str = reverse(liveCPUArray.heapUsedMaxVal[key].toString());
						for(var i = maxHeapUsedValueLength-1 ; i >= 0 ; i--){			  
							doc.text(102 - charWidth * i, yIndex-2, str[i]);
						}
						
						var heapUsedValueLength = liveCPUArray.heapUsed[key].toString().length;
				    	str = reverse(liveCPUArray.heapUsed[key].toString());
						for(var i = heapUsedValueLength-1 ; i >= 0 ; i--){			  
							doc.text(121 - charWidth * i, yIndex-2, str[i]);
						}
						
						var minNonHeapUsedLength = liveCPUArray.nonHeapMinVal[key].toString().length;
				    	var charWidth = 1.25;
						var str = reverse(liveCPUArray.nonHeapMinVal[key].toString());
						for(var i = minNonHeapUsedLength-1 ; i >= 0 ; i--){			  
							doc.text(145- charWidth * i, yIndex-2, str[i]);
						}
				    	
						var maxNonHeapUsedValueLength = liveCPUArray.nonHeapMaxVal[key].toString().length;
				    	str = reverse(liveCPUArray.nonHeapMaxVal[key].toString());
						for(var i = maxNonHeapUsedValueLength-1 ; i >= 0 ; i--){			  
							doc.text(168 - charWidth * i, yIndex-2, str[i]);
						}
						
						var nonheapUsedValueLength = liveCPUArray.nonHeapUsed[key].toString().length;
				    	str = reverse(liveCPUArray.nonHeapUsed[key].toString());
						for(var i = nonheapUsedValueLength-1 ; i >= 0 ; i--){			  
							doc.text(187 - charWidth * i, yIndex-2, str[i]);
						}
				    	
				    	doc.setDrawColor(192,192,192); 
				    	
						doc.line(15, yIndex-5.5, 15, yIndex);
				    	doc.line(62, yIndex-5.5, 62, yIndex);
				    	doc.line(35, yIndex-5.5, 35, yIndex);
				    	doc.line(130,yIndex-5.5, 130, yIndex);
				    	doc.line( pageWidth-15,yIndex-5.5,  pageWidth-15, yIndex);
				    	
				    	doc.line(59, yIndex, pageWidth-15, yIndex);
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
		    		doc.line(15, yIndex-5, 60, yIndex-5); 
				    
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
		    $('#exportbtn<%=liveMemoryWidgetId%>').click(function(){
		   		 save_chart_live_memory_widget($('#liveMemoryUsageId_<%=liveMemoryWidgetId%>_').highcharts()); 
		    });
		    
		    $('#exportbtn_'+<%=liveMemoryWidgetId%>).click(function(){
		   	 	save_chart_live_memory_widget($('#liveMemoryUsageId_<%=liveMemoryWidgetId%>_').highcharts()); 
		    });
		    
		    function reverse(s){
			    return s.split("").reverse().join("");
			}
		    
		    function getLiveMemoryDetails(){
			    var jResponseData="";
				$.ajax({
					url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getLiveMemoryDetails',
				    type:'GET',
				    async:false,
				    data:{serverIds:liveServerList},
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
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/instancewidgets/EditLiveMemoryUsage.jsp?widgetId=<%=liveMemoryWidgetId%>" id="editJsp">

<!-- expoting div -->

 <div id="exportingdiv<%=liveMemoryWidgetId%>" style="width: 100%;display: table;background-position: center;vertical-align: middle;text-align:right;padding-top:10px;" align="center">
	<input type="button"  id="freezebtn<%=liveMemoryWidgetId%>" name="button" value="Freeze" class="restart-btn" title="Freeze" align="right"/> 
 	<input type="button"  id="unfreezebtn<%=liveMemoryWidgetId%>" name="button" value="Unfreeze" class="restart-btn" title="Unfreeze" align="right"/>
 	<input type="button"  id="exportbtn<%=liveMemoryWidgetId%>" name="button" value="Export.." class="restart-btn" title="Unfreeze" align="right" style="margin-right: 20px;'"/> 
 </div> 



<%--ProgressBar Div : Start  --%>
 <div id="progressbarDIV<%=liveMemoryWidgetId%>" class="progressbar" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

<%--Trigger Export As PDF menu --%> 
<span id="exportbtn_<%=liveMemoryWidgetId%>"></span>
 
<%--ProgressBar Div : End  --%>
<div id="liveMemoryUsageId_<%=liveMemoryWidgetId%>_" class="liveMemoryUsage<%=pageId%> widget-class" style="height: 300px; width:inherit !important;padding-bottom: 3px;overflow: auto;"></div> 
</body>
</html>
