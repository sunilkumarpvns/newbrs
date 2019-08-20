<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.HeapMemoryUsageData" %>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.NonHeapMemoryUsage" %>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.GarbageCollectorData" %>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerGraphForm"%>

<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/dashboardmenu.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/managedashboard.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/dashboard/systemconfiguration.css" />
<script type="text/javascript" src="<%=basePath%>/js/datatable/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/datatable/jquery-ui.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/datatable/jquery.dataTables.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/datatable/jquery.dataTables.columnFilter.js"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/development/redmond/jquery-ui.css" />
<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/dashboard/system-pie-chart.js"></script> 
<script type="text/javascript" src="<%=basePath%>/js/dashboard/server-graph.js"></script> 
<script type="text/javascript" src="<%=basePath%>/js/exporting.js"></script> 

<%
	ViewNetServerGraphForm viewNetServerGraphForm = (ViewNetServerGraphForm)request.getAttribute("viewNetServerGraph");
	List<GarbageCollectorData> garbageCollectorDataList=viewNetServerGraphForm.getGarbageCollectorDataList();
	List<HeapMemoryUsageData> heapMemoryList=viewNetServerGraphForm.getHeapMemoryList();
	List<NonHeapMemoryUsage> nonHeapMemoryList=viewNetServerGraphForm.getNonHeapMemoryList();
	List<ThreadInformationData> threadInfoDataList=viewNetServerGraphForm.getThreadInfoDataList();
	int index=1;
%>

<script>
var pinImage=false;
var	memoryThreadChart;
var pieChart;
var tcount=0;

function arrowUp() {
    var activeTableRow = $('#threadDemo tbody tr.active').removeClass('active').first();
    if (activeTableRow.length) {
        activeTableRow.prev().addClass('active');
    } else {
        $('#threadDemo tbody').children().last().addClass('active');
    }
};

function arrowDown() {
    var activeTableRow = $('#threadDemo tbody tr.active').removeClass('active').first();
    if (activeTableRow.length) {
        activeTableRow.next().addClass('active');
    } else {
        $('#threadDemo tbody').children().first().addClass('active');
    }
};

$(window).keydown(function (key) {
    if (key.keyCode == 38) {
        arrowUp();
    }
    if (key.keyCode == 40) {
        arrowDown();
    }
});
function refreshJVMThread(){
	requestForThreadDetails();
}
function requestForThreadDetails(){
	tcount=0;
	$.ajax({
        url: '<%=basePath%>/initViewNetServerGraph.do?method=getThreadData',
        dataType: 'json',
        type: 'GET',
	    async: true,
        error: function (threadDetailsList) {
        },
        success: function (threadDetailsList) {
        	$("#tbodyId").empty();
        	$.each(threadDetailsList, function( key, threadInformationData ) {
        		if(threadInformationData.totalWaitedTime == ""){
        			threadInformationData.totalWaitedTime="-";
        		}
        		if(threadInformationData.totalBlockedTime == ""){
        			threadInformationData.totalBlockedTime="-";
        		}
        		
        		$('#threadDemo #tbodyId').append('<tr id="tr'+tcount+'" class="table-Tr-style threadTable"><td class="tabledata-text" width="8%" style="font-size:12px;border-bottom:1px solid #ccc"><table width="100%" cellpadding="0"	cellspacing="0" border="0" style="border-style:none; !important;" ><tr><td class="labeltext" style="border-style: none;" width="1%"><img src="<%=basePath%>/images/pin.gif" alt="pin" title="Pin/Unpin" height="20px" width="20px" style="display:none;cursor: pointer;" class="pinImage" id="pinImage" onclick="pinThread('+tcount+');" /></td><td>'+threadInformationData.threadName+'</td></tr></table></td><td class="tabledata-text" width="6%" align="left" style="font-size:12px;border-bottom:1px solid #ccc"><div style="display:none;padding:0;margin:0;text-align: left;float: left;" id="threadStackDetail">'+threadInformationData.statckTraceInfo+'</div>'+threadInformationData.threadState+'</td><td style="font-size:12px;border-bottom:1px solid #ccc" class="tabledata-text" width="6%" align="right">'+threadInformationData.blockedCount+'</td><td style="font-size:12px;border-bottom:1px solid #ccc" class="tabledata-text" width="6%" align="right">'+threadInformationData.totalBlockedTime+'</td><td style="font-size:12px;border-bottom:1px solid #ccc;" class="tabledata-text" width="6%" align="right">'+threadInformationData.waitedCount+'</td><td style="font-size:12px;border-bottom:1px solid #ccc" class="tabledata-text" width="6%" align="right"> '+threadInformationData.totalWaitedTime+'</td></tr>');
        		tcount++;
        	});
        	
       	
        	$('#printThreadStackTrace').css("display","block");
    		
    		$('#threadDemo #tbodyId').children().first().find('td #pinImage').css("display","block");
    		
    		$('#threadDemo #tbodyId tr').filter(':nth(0)').css('background-color', '#F2F2F2');
    		
        	
        	var stackData=$.trim($(this).find('td #threadStackDetail').html());
    		var stackArray = stackData.split('\n');
    		
	    	if(stackArray.length > 1){
	    		$('#printThreadStackTrace').append("<img src='<%=basePath%>/images/nestedthread.gif'/>");
	    	}
    			
    		$.each(stackArray,function(key,value){
    		 		if(key >= 1){
    		 			if(value.toLowerCase().indexOf("monitor info") >= 0){
    		 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
    		 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
    		 			}else if(value.toLowerCase().indexOf("lock info") >= 0){
    		 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
    		 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
    		 			}else{

    			 			$('#printThreadStackTrace').append("\n\t<img src='<%=basePath%>/images/threadimg.png'/>");
    			 			$('#printThreadStackTrace').append(value);
    		 			}
    		 		}else{
    		 			$('#printThreadStackTrace').append("<b>"+value+"</b>");
    		 		}
    		 	});
    		 	
    		 	
    		 	$(".threadTable").hover(
    		 			function() {
    		 				if(pinImage == false){
    		 					$('#threadDemo tbody').children().first().find('td #pinImage').css("display","none");
    		 					$('#threadDemo tbody tr').filter(':nth(0)').css('background-color', 'white'); 
    		 					$('#printThreadStackTrace').css("display","block");
    			 				$(this).find('td #pinImage').css("display","block");
    			 				
    							var data=$.trim($(this).find('td #threadStackDetail').html());
    			 				var finalData=[];
    			 				var arr = data.split('\n');
    			 				$('#printThreadStackTrace').empty();
    			 				$('#printThreadStackTrace').append("<img src='<%=basePath%>/images/nestedthread.gif'/>");
    			 			 	$.each(arr,function(key,value){
    			 			 		if(key >= 1){
    			 			 			if(value.toLowerCase().indexOf("monitor info") >= 0){
    			 			 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
    			 			 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
    			 			 			}else if(value.toLowerCase().indexOf("lock info") >= 0){
    			 			 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
    			 			 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
    			 			 			}else{
    				 			 			$('#printThreadStackTrace').append("\n\t<img src='<%=basePath%>/images/threadimg.png'/>");
    				 			 			$('#printThreadStackTrace').append(value);
    			 			 			}
    			 			 		}else{
    			 			 			$('#printThreadStackTrace').append("<b>"+value+"</b>");
    			 			 		}
    			 			 	});
    		 				}
    		 			}, function() {
    		 				if(pinImage == false){
    		 					$('#printThreadStackTrace').css("background-color","white");
    		 					$(this).find('td #pinImage').css("display","none");
    		 				}
    			});
    		 	
    		 
    			
    		 	
        },
        cache: false
    });
}

function pinThread(ids){
	if(pinImage == true){
		pinImage =false;
		$('#tr'+ids).css('background-color','white');
		$('.pinImage').css("display","none");
	}else{
		pinImage=true;
		$('#tr'+ids).css('background-color','#F2F2F2');
	}
}
function requestPieChartData(){
	
	 $.ajax({
	        url: '<%=basePath%>/initViewNetServerGraph.do?method=getPieChartData',
	        dataType: 'json',
	        type: 'GET',
		    async: true,
	        error: function (pieData) {
	            setTimeout(requestPieChartData, 3000);
	        },
	        success: function (pieData) {
	        	var pidata = [{
	        		name:'Free Memory',
	                y: (pieData.freeHeapMemory)+(pieData.freeNonHeapMemory),
	                color: "#2f7ed8",
	                drilldown: {
	                    name: 'Free Memory',
	                    categories: ['Heap', 'Non-Heap'],
	                    data: [(pieData.freeHeapMemory), (pieData.freeNonHeapMemory)],
	                    color: "#2f7ed8"
	                }
	            }, {
	            	name:'Used Memory',
	                y: (pieData.heapUsed)+(pieData.nonHeapused),
	                color: "#0d233a",
	                drilldown: {
	                    name: 'Used Memory',
	                    categories: ['Heap', 'Non-Heap'],
	                    data: [(pieData.heapUsed),(pieData.nonHeapused)],
	                    color: "#0d233a"
	                }
	            }];
	        	
	        	// Build the data arrays
	        	 var innerData = [];
	        	 var outerData = [];
	        	 for (var i = 0; i < pidata.length; i++) {

	        	     // add browser data
	        	     innerData.push({
	        	         name: pidata[i].name,
	        	         y: pidata[i].y,
	        	         color: pidata[i].color
	        	         
	        	     });

	        	     // add version data
	        	     for (var j = 0; j < pidata[i].drilldown.data.length; j++) {
	        	         var brightness = 0.2 - (j / pidata[i].drilldown.data.length) / 3 ;
	        	         outerData.push({
	        	             name: pidata[i].drilldown.categories[j],
	        	             y: pidata[i].drilldown.data[j],
	        	             color: Highcharts.Color(pidata[i].color).brighten(brightness).get()
	        	             
	        	         });
	        	     }
	        	 }
	        	 
	        	pieChart.series[0].setData(innerData);
	        	pieChart.series[1].setData(outerData);
	        	
	            setTimeout(requestPieChartData,3000);
	        },
	        cache: false
	    });
	
}
function requestData() {
	
    $.ajax({
        url: '<%=basePath%>/initViewNetServerGraph.do?method=getMemoryThreadInfo',
        dataType: 'json',
        type: 'GET',
	    async: true,
        error: function (memoryData) {
        	
            var series = memoryThreadChart.series[0],

            shift = series.data.length > 100;  // shift if the series is longer than 20

            memoryThreadChart.series[0].addPoint([memoryData.currentTime, memoryData.heapMemoryUsage], true, false);
            memoryThreadChart.series[1].addPoint([memoryData.currentTime, memoryData.heapMemoryPeakUsage], true, false);
            memoryThreadChart.series[2].addPoint([memoryData.currentTime, memoryData.totalThread], true, false);
            memoryThreadChart.series[3].addPoint([memoryData.currentTime, memoryData.peakThread], true, false);

            // call it again after defined seconds
            setTimeout(requestData, 1000);
        },
        success: function (memoryData) {
            var series = memoryThreadChart.series[0],
                shift = series.data.length > 100;  // shift if the series is longer than 20
            memoryThreadChart.series[0].addPoint([memoryData.currentTime, memoryData.heapMemoryUsage], true, false);
            memoryThreadChart.series[1].addPoint([memoryData.currentTime, memoryData.heapMemoryPeakUsage], true, false);
            memoryThreadChart.series[2].addPoint([memoryData.currentTime, memoryData.totalThread], true, false);
            memoryThreadChart.series[3].addPoint([memoryData.currentTime, memoryData.peakThread], true, false);
           
            updateGarbageCollectionData(memoryData.psMarkSweepCount,memoryData.psMarkSweepTime,memoryData.psScavengeCount,memoryData.psScavengeTime);
          	updateJVMMemoryDetails(memoryData.psEdenused,memoryData.psEdenmax,memoryData.psEdenusage,memoryData.psEdenpeakused,memoryData.psEdenpeakmax,memoryData.psSurvivorused,memoryData.psSurvivormax,memoryData.psSurvivorusage,memoryData.psSurvivorpeakused,memoryData.psSurvivorpeakmax,memoryData.oldgenused,memoryData.oldgenmax,memoryData.oldgenusage,memoryData.oldgenpeakused,memoryData.oldgenpeakmax);
            updateNonHeapDetails(memoryData.permgenused,memoryData.permgenmax,memoryData.permgenusage,memoryData.permgenpeakused,memoryData.permgenpeakmax,memoryData.codecacheused,memoryData.codecachemax,memoryData.codecacheusage,memoryData.codecachepeakused,memoryData.codecachepeakmax);

			$( ".progress_bar" ).progressbar({
				 value: parseInt(memoryData.psEdenusage)
			 });
			 
			 var progressvarVal=parseInt(memoryData.psEdenusage);
			 var selector = "#edenProgress > div";
			 
			 if(progressvarVal < 50){
				 $(selector).css({ 'background': 'green' });
			 }else if(progressvarVal > 50 && progressvarVal <75){
				 $(selector).css({ 'background': 'orange' });
			 }else{
				 $(selector).css({ 'background': 'red' });
			 }
			 
			
			 $( ".psSurvivor_Progress" ).progressbar({
				 value: parseInt(memoryData.psSurvivorusage)
			 });
			 
			 
			 progressvarVal=parseInt(memoryData.psSurvivorusage);
			 selector = "#psSurvivor_Progress > div";
			 
			 if(progressvarVal < 50){
				 $(selector).css({ 'background': 'green' });
			 }else if(progressvarVal > 50 && progressvarVal <75){
				 $(selector).css({ 'background': 'orange' });
			 }else{
				 $(selector).css({ 'background': 'red' });
			 }
			 
			 $( ".psOldGen_Progress" ).progressbar({
				 value: parseInt(memoryData.oldgenusage)
			 });
			 
			 progressvarVal=parseInt(memoryData.oldgenusage);
			 selector = "#psOldGen_Progress > div";
			 
			 if(progressvarVal < 50){
				 $(selector).css({ 'background': 'green' });
			 }else if(progressvarVal > 50 && progressvarVal <75){
				 $(selector).css({ 'background': 'orange' });
			 }else{
				 $(selector).css({ 'background': 'red' });
			 }
			 
			 
			 $( ".psPermGen_Progress" ).progressbar({
				 value: parseInt(memoryData.permgenusage)
			 });
			 
			 progressvarVal=parseInt(memoryData.permgenusage);
			 selector = "#psPermGen_Progress > div";
			 
			 if(progressvarVal < 50){
				 $(selector).css({ 'background': 'green' });
			 }else if(progressvarVal > 50 && progressvarVal <75){
				 $(selector).css({ 'background': 'orange' });
			 }else{
				 $(selector).css({ 'background': 'red' });
			 }
			 
			 $( ".codeCache_Progress" ).progressbar({
				 value: parseInt(memoryData.codecacheusage)
			 });
			 
			 progressvarVal=parseInt(memoryData.codecacheusage);
			 selector = "#codeCache_Progress > div";
			 
			 if(progressvarVal < 50){
				 $(selector).css({ 'background': 'green' });
			 }else if(progressvarVal > 50 && progressvarVal <75){
				 $(selector).css({ 'background': 'orange' });
			 }else{
				 $(selector).css({ 'background': 'red' });
			 }
           
          	setTimeout(requestData, 1000);
        },
        cache: false
    });
}


$(document).ready(function(){
	
	$('#threadDemo').dataTable( {
		"sScrollY": "200px",
		"bPaginate": false
	});
	
	$('.dataTables_scrollHeadInner').css("width", "100%");
	$('.dataTable').css("width", "100%");
	$("#threadDemo_filter").append('<input class="light-btn" type="button" id="exportBtn" value="  export  " style="float :right !important;margin-left:0.2em;" />');
	$("#threadDemo_filter").append('<input class="light-btn" type="button" id="refreshBtn" value=" Refresh" style="float :right !important;" onclick="refreshJVMThread();" />');

	$( ".threadTable").hover(
 			function() {
 				if(pinImage == false){
 					$('#threadDemo tbody').children().first().find('td #pinImage').css("display","none");
 					$('#threadDemo tbody tr').filter(':nth(0)').css('background-color', 'white'); 
 					$('#printThreadStackTrace').css("display","block");
	 				$(this).find('td #pinImage').css("display","block");
	 				
					var data=$.trim($(this).find('td #threadStackDetail').html());
	 				var finalData=[];
	 				var arr = data.split('\n');
	 				$('#printThreadStackTrace').empty();
	 				$('#printThreadStackTrace').append("<img src='<%=basePath%>/images/nestedthread.gif'/>");
	 			 	$.each(arr,function(key,value){
	 			 		if(key >= 1){
	 			 			if(value.toLowerCase().indexOf("monitor info") >= 0){
	 			 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
	 			 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
	 			 			}else if(value.toLowerCase().indexOf("lock info") >= 0){
	 			 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
	 			 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
	 			 			}else{
		 			 			$('#printThreadStackTrace').append("\n\t<img src='<%=basePath%>/images/threadimg.png'/>");
		 			 			$('#printThreadStackTrace').append(value);
	 			 			}
	 			 		}else{
	 			 			$('#printThreadStackTrace').append("<b>"+value+"</b>");
	 			 		}
	 			 	});
 				}
 			}, function() {
 				if(pinImage == false){
 					$('#printThreadStackTrace').css("background-color","white");
 					$(this).find('td #pinImage').css("display","none");
 				}
	});
	
	$('#printThreadStackTrace').css("display","block");
	
	$('#threadDemo tbody').children().first().find('td #pinImage').css("display","block");
	
	$('#threadDemo tbody tr').filter(':nth(0)').css('background-color', '#F2F2F2');
	
	$('#threadDemo tbody tr').filter(':nth(0)').hover(
 			function() {
 				$(this).css('background-color', '#F2F2F2');
 			},function(){
 				var pincat=$(this).find('.pinImage').css('display');
 				if(pincat == "block"){
 					$(this).css('background-color', '#F2F2F2');
 				}else{
 					$(this).css('background-color', 'white');
 				}
 	});
	
	var tracedata=$.trim($(this).find('td #threadStackDetail').html());
	var tracearr = tracedata.split('\n');
		$('#printThreadStackTrace').empty();
		$('#printThreadStackTrace').append("<img src='<%=basePath%>/images/nestedthread.gif'/>");
	 	$.each(tracearr,function(key,value){
	 		if(key >= 1){
	 			if(value.toLowerCase().indexOf("monitor info") >= 0){
	 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
	 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
	 			}else if(value.toLowerCase().indexOf("lock info") >= 0){
	 				$('#printThreadStackTrace').append("\n<img src='<%=basePath%>/images/nestedthread.gif'/>");
	 				$('#printThreadStackTrace').append("<b>"+value+"</b>");
	 			}else{

		 			$('#printThreadStackTrace').append("\n\t<img src='<%=basePath%>/images/threadimg.png'/>");
		 			$('#printThreadStackTrace').append(value);
	 			}
	 		}else{
	 			$('#printThreadStackTrace').append("<b>"+value+"</b>");
	 		}
	 	});
	
	//set UTC time to Highchart 
	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});
	
	//Memory Thread Chart
	memoryThreadChart = new Highcharts.StockChart({
				chart: {        	
                	 renderTo: 'memoryThreadChart',
                	 zoomType : 'xy',
                     type: 'spline',
                     plotBackgroundColor: 'rgba(255, 255, 255, .9)',
                     plotShadow: true,
                     plotBorderWidth: 1,
                     events: {
                         load: requestData
                     }                     
                 },
                 rangeSelector : {
                		inputEnabled : false,
         		},
         		 navigator: {
         	    	height: 15
         	    },
                 title: {
                     text: 'Memory and Thread Statistics',
                     style: {
                          color: '#000',
                          font: 'bold 16px "Trebuchet MS", Verdana, sans-serif'
                      }                                                              
                 },
                 xAxis: {
                     type: 'datetime',
                     dateTimeLabelFormats : {
         				second : '%Y-%m-%d<br/>%H:%M:%S',
         				minute : '%Y-%m-%d<br/>%H:%M',
         				hour : '%Y-%m-%d<br/>%H:%M',
         				day : '%Y<br/>%m-%d',
         				week : '%Y<br/>%m-%d',
         				month : '%Y-%m',
         				year : '%Y'
         			},
                     lineColor: '#000',
                     tickColor: '#000',
                     labels: {
                         style: {
                                  color: '#000',
                                  font: '11px Trebuchet MS, Verdana, sans-serif'
                                }
                     },
                     title: {
                         style: {
                                 color: '#333',
                                 fontWeight: 'bold',
                                 fontSize: '12px',
                                 fontFamily: 'Trebuchet MS, Verdana, sans-serif'
                                 }
                     }
                 },
                 yAxis: [{                                    
                     title: {
                         text: 'Memory',
                         floating: true,
                         x:30,
                         y:30
                     },
                     min: 0,
                     minorTickInterval: 'auto',
                     gridLineWidth: 0.10,
               		 lineColor: '#000',
            		 lineWidth: 1,
              		 tickWidth: 1,
               	     tickColor: '#000',
                  	 labels: {
        	             style: {
        	    			  color: '#000',
        	           		  font: '11px Trebuchet MS, Verdana, sans-serif'
        	             },
        	             formatter: function() {
                             return this.value +' mb';
                         }
                     },
               		 title: {
               			  style: {
        	            		    color: '#333',
        	                  	    fontWeight: 'bold',
        	           				fontSize: '12px',
        	              			fontFamily: 'Trebuchet MS, Verdana, sans-serif'
                  			      }
                          }
                 },{                                               
                     title: {
                         text: 'Thread'
                     },
                     min: 0,
                     minorTickInterval: 'auto',
                     lineColor: '#000',
              		 lineWidth: 1,
                  	 tickWidth: 1,
                     tickColor: '#000',
               		 labels: {
                            style: {
                                    color: '#000',
                           			font: '11px Trebuchet MS, Verdana, sans-serif'
                        	}
                     },
                     title: {
                        style: {
        	                       color: '#333',
        	                       fontWeight: 'bold',
        	                  	   fontSize: '12px',
        	                       fontFamily: 'Trebuchet MS, Verdana, sans-serif'
                     	       }
                     },                                                           
                     opposite : true
                 }],
                 tooltip: {
                     shared : true
                 },
                 legend: {
                	enabled : true,
                  	itemStyle: {
                    	font: '9pt Trebuchet MS, Verdana, sans-serif',
                       	color: 'black'
                    },
              	 itemHoverStyle: {
               		color: '#039'
               	 },
             	itemHiddenStyle: {
                     color: 'gray'
              	}
              },
                                                     
              plotOptions: {
                 series: {
                     marker: {
                         enabled: false
                     }
                 }
             },            
          	 labels: {
                style: {
                        color: '#99b'
            	}
             },

             navigation: {
          		buttonOptions: {
           		     theme: {
                            stroke: '#CCCCCC'
                    }
          		}
            },
                                     
            series: [{
                name: 'Heap Memory',
                color: Highcharts.getOptions().colors[2],
                type : 'line',
                data: [],
                tooltip: {
                    valueSuffix: ' mb'
                }
            }, {
                name: 'Heap Memory Max Usage',
                dashStyle: 'ShortDot',
                lineWidth: 2.8,
                color: Highcharts.getOptions().colors[2],
                data: []
            }, {
                name: 'Total Thread',
                color: Highcharts.getOptions().colors[0],
                type : 'line',
                yAxis: 1,
                data: []
            }, {
                name: 'Peak Thread',                
                color: Highcharts.getOptions().colors[0],
                dashStyle: 'ShortDot',
                lineWidth: 2.8,
                yAxis: 1,
                data: []
            }]
        });	
	
	searchTable("BLOCKED");
});

function searchTable(inputVal)
{
	var table = $('#threadDemo');
	table.find('tr').each(function(index, row)
	{
		var allCells = $(row).find('td');
		if(allCells.length > 0)
		{
			var found = false;
			allCells.each(function(index, td)
			{
				var regExp = new RegExp(inputVal, 'i');
				if(regExp.test($(td).text()))
				{
					found = true;
					return false;
				}
			});
			if(found == true){
					$(row).css("color","red");
			}
		}
	});
}
function popup(mylink, windowname)
{
	
	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
					href=mylink;
	else
					href=mylink.href;
					
	window.open(href, windowname, 'width=850,height=450,left=150,top=100,scrollbars=yes');
	
	return false;
} 

$(function () {
    
	$("#exportBtn").click(function(e) {
	    window.open('data:application/vnd.ms-excel,' + encodeURIComponent( $('div[id$=threaddemo]').html()));
	    e.preventDefault(); 
	});
	
	
	$( "#restart" ).click(function() {
		alert( "Handler for .click() called." );
	});
	
	 var colors = Highcharts.getOptions().colors,
     categories = ['Free Memory', 'Used Memory'],
     name = 'JVM Heap Memory',
     data = [{
             y: <%=viewNetServerGraphForm.getFreeHeapMemory()%>+<%=viewNetServerGraphForm.getFreeNonHeapMemory()%>,
             color: colors[0],
             drilldown: {
                 name: 'Free Memory',
                 categories: ['Heap', 'Non-Heap'],
                 data: [<%=viewNetServerGraphForm.getFreeHeapMemory()%>, <%=viewNetServerGraphForm.getFreeNonHeapMemory()%>],
                 color: colors[0]
             }
         }, {
             y: <%=viewNetServerGraphForm.getHeapMemoryUsed()%>+<%=viewNetServerGraphForm.getNonHeapMemoryUsed()%>,
             color: colors[1],
             drilldown: {
                 name: 'Used Memory',
                 categories: ['Heap', 'Non-Heap'],
                 data: [<%=viewNetServerGraphForm.getHeapMemoryUsed()%>,<%=viewNetServerGraphForm.getNonHeapMemoryUsed()%>],
                 color: colors[1]
             }
         }];


 // Build the data arrays
 var innerData = [];
 var outerData = [];
 for (var i = 0; i < data.length; i++) {

     // add browser data
     innerData.push({
         name: categories[i],
         y: data[i].y,
         color: data[i].color
         
     });

     // add version data
     for (var j = 0; j < data[i].drilldown.data.length; j++) {
         var brightness = 0.2 - (j / data[i].drilldown.data.length) / 3 ;
         outerData.push({
             name: data[i].drilldown.categories[j],
             y: data[i].drilldown.data[j],
             color: Highcharts.Color(data[i].color).brighten(brightness).get()
             
         });
     }
                                         
 }

 
pieChart= new Highcharts.Chart({
		chart: {        	
     		renderTo: 'jvmMemoryChart',
     		type: 'pie',
        	marginTop : 0,
        	spacing : [0, 0, 0, 0],
        	events: {
             	 load: requestPieChartData
         	}                     
      },
     title: {
         text: null
     },
     yAxis: {
         title: {
             text: 'Total JVM Memory Usage'
         }
     },
     
     legend: {            
         align: 'center',
         floating: true,
     },            
     plotOptions: {
         pie: {
             allowPointSelect : true,
             shadow: false,
             center: ['50%', '50%'],
             dataLabels: {
                 enabled: true,
                 inside : true,
                 crop : false,
                 overflow : 'none',
                 distance : 10
             },
             showInLegend: true                    
         }
     },
     series: [{
         name: 'JVM Memory',
         showInLegend: false,
         data: innerData,
         size: '50%',
         dataLabels: {
             formatter: function() {
                 return this.y > 5 ? this.point.name : null;
             },
             color: 'white',
             distance: -40
         }
     }, {
         name: 'Heap Memory',
         data: outerData,
         size: '80%',
         innerSize: '60%',
         dataLabels: {
             formatter: function() {
                 // display only if larger than 1
                 return this.y >= 1 ? '<b>'+ this.point.name +':</b> '+ this.y + 'MB' : null;
             }
         }
     }]
 });
 
 memoryThreadChart.yAxis[0].update({
     title:{
         text: "Memory"
     }
 });
  
 memoryThreadChart.yAxis[1].update({
     title:{
         text: "Thread"
     }
 });
 
});

</script>

<style>
	.active{background-color:#FAFAFA;}
</style>
	<table cellpadding="0" cellspacing="0" border="0" width="100%" >    
	<tr>
     <td valign="top" align="right">  
       <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
       	  <tr>
		  	<td>&nbsp;</td>
		  </tr>
		  <tr>
				<td colspan="3" style="padding-left: 10px;padding-right: 10px;">
					<table name="c_tblCrossProductList" width="100%" border="0" cellpadding="0" cellspacing="0" style="border: 1px solid #CCC;">
						<tr>
						<td class="tblheader-bold" colspan="3" >Online Graphs</td>
						</tr>
						<tr>
							<td width="2%" style="padding-left: 20px;padding-top: 10px;">	
								<img src="<%=request.getContextPath()%>/images/clock.png" alt="clock"/>	
							</td>
							<td class="serverUpTime" style="padding-top: 10px;">	
								<span style="color: rgb(1, 81, 152);">Server Up Time :</span>
								<span><%=(viewNetServerGraphForm.getServerUpTime() == null) ? "Connection Failure : Connection could not be established. " : viewNetServerGraphForm.getServerUpTime() %></span>
							</td>
						</tr>
						<tr>
							<td width="2%" colspan="2"  style="padding-left: 20px">		
								<input type="button"  name="button" value="Restart Server" class="restart-btn" id="restart"/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<div id="memoryThreadChart" style="min-width: 200px; height: 320px; margin: 25 auto;margin-top: 0;margin-bottom: 0;"></div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td colspan="3" style="padding: 10px;">
					<table name="c_tblCrossProductList" width="100%" border="0" cellpadding="0" cellspacing="0" style="border: 1px solid #CCC;">
						<tr>
							<td class="tblheader-bold" colspan="2">JVM Memory Usage</td>
						</tr>
							<tr>
								<td colspan="2" align="center" >
									<table width="100%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td width="30%">
												<table>
													<tr>	
														<td>
															<table width="100%">
																<tr>
																	<td>
																		<%if(viewNetServerGraphForm.getFreeHeapMemory() == null && viewNetServerGraphForm.getFreeNonHeapMemory() == null 
																		&& viewNetServerGraphForm.getHeapMemoryUsed() == null && viewNetServerGraphForm.getNonHeapMemoryUsed() == null){%>
																			<div align="center" style="width: 400px; height: 200px;vertical-align: middle;font-size: 12px;text-align: center;background-color: #DCDCDC;border-style: dashed;border-color: gray;border-width: 2px;">
																				<span style="padding-top:50px;vertical-align: middle;text-align: center;font-size: 14px;color: gray;">No chart found</span>
																			</div>
																		<%}else{ %>
																			<div id="jvmMemoryChart" style="width: 400px; height: 400px; margin: 0 auto;"></div>
																		<%}%>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
											<td style="vertical-align: top;padding-top: 10px;padding-right: 10px;" align="left"> 
												<table width="100%"  cellpadding="0" cellspacing="0" border="0">
													<tr>
														<td>
															<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
																<tr>
																	<td colspan="2" class="garbage-collection-header">		
																		<bean:message bundle="dashboardResources" key="system.garbagecollection" />  
																	</td>
																</tr>
																<tr>
																	<td colspan="2" align="center" >
																		<table width="100%"  cellpadding="0" cellspacing="0" border="0" id="garbageCollectionTable">
																		 	<tr class="header-color">
																				<th width="20%"  class="system-table-header" align="left">
																					<bean:message bundle="dashboardResources" key="system.garbagecollection.name" />  
																				</th>																			 
																		 		<th  class="system-table-header" align="right">
																		 			<bean:message bundle="dashboardResources" key="system.garbagecollection.totalcollectioncount" />  
																		 		</th>
																		 		<th  class="system-table-header" align="right">
																		 			<bean:message bundle="dashboardResources" key="system.garbagecollection.totalcollectiontime" />  
																		 		</th>
																		 	</tr>
																		 	<%if(garbageCollectorDataList !=null && garbageCollectorDataList.size() > 0){ %>
																				 <logic:iterate id="garbageCollectionData" name="viewNetServerGraphForm" property="garbageCollectorDataList" type="com.elitecore.elitesm.web.dashboard.memoryusage.GarbageCollectorData">
																			 		<tr class="garbage-table-Tr-style garbageTr" id="garbageTr">
																						<td class="first-left-border tabledata-text">
																					 			<bean:write name="garbageCollectionData" property="name" />
																						</td>
																						<td class="tabledata-text" align="right"><bean:write name="garbageCollectionData" property="collectionCount" /></td>
																						<td class="tabledata-text" align="right"><bean:write name="garbageCollectionData" property="collectionTime" /></td>
																					</tr>
																				</logic:iterate>
																			<%}else{ %>
																				<tr>
																						<td colspan="3" class="labeltext" align="center">
																							No data available in table
																						</td>
																					</tr>
																			<%} %> 
																		</table>
																	 </td>
																	</tr>
																</table>
														   </td>
														  </tr>
														  <tr>
																<td>&nbsp;</td>
										   				  </tr>
										   				  <tr>
														 	 <td>
														 		<table width="100%"  cellpadding="0" cellspacing="0" border="0" id="JVMMemory">
																 	<tr>
																		<td colspan="7" class="memory-usage-title">		
																				<bean:message bundle="dashboardResources" key="system.systemconfiguration.memoryusage" />  
																		</td>
																	</tr>
																 	<tr class="header-color" style="width: 100%;vertical-align: bottom;" >
																	 		<th width="13%" class="system-table-header memory-category" align="left">Memory Category</th>
																			<th width="20%" class="system-table-header" align="left">
																				<bean:message bundle="dashboardResources" key="system.memoryusage.name" />  
																			</th>																			 
																	 		<th width="10%" class="system-table-header" align="right">
																	 			<bean:message bundle="dashboardResources" key="system.memoryusage.used" /> 
																	 		</th>
														 			 		<th width="7%" class="system-table-header" align="right">
														 			 			<bean:message bundle="dashboardResources" key="system.memoryusage.maxused" /> 
														 			 		</th>
																	 		<th width="25%" class="system-table-header" align="right">
																	 			<bean:message bundle="dashboardResources" key="system.memoryusage.usage" /> 
																	 		</th>
																	 		<th width="12%" class="system-table-header" align="right">
																	 			<bean:message bundle="dashboardResources" key="system.memoryusage.peakused" /> 
																	 		</th>
																	 		<th width="7%" class="system-table-header" align="right" style="border-right-style: solid;border-right-color: #CCCCCC;border-right-width: 1px;">
																	 			<bean:message bundle="dashboardResources" key="system.memoryusage.peakmax" /> 
																	 		</th>
																	 </tr>
																	 <%	boolean checkHeapMemory=false;
																		if(heapMemoryList !=null && heapMemoryList.size() > 0){ %>
																		 <logic:iterate id="heapMemoryData" name="viewNetServerGraphForm" property="heapMemoryList" type="com.elitecore.elitesm.web.dashboard.memoryusage.HeapMemoryUsageData">
																			<tr class="table-Tr-style" style="vertical-align: top;">
																		 		<% if(checkHeapMemory == false){%>
																			 		<td width="13%" class="first-left-border tabledata-text noHover" rowspan="<%=heapMemoryList.size()%>" style="border-right-color: #CCCCCC;border-right-style: solid;border-right-width: 1px;">Heap Memory</td>
																				<%}checkHeapMemory=true; %>
																					<td width="20%" class="tabledata-text"><bean:write name="heapMemoryData" property="name" /></td>
																					<td width="10%" class="tabledata-text" align="right" id="heapUsed"><bean:write name="heapMemoryData" property="used" />MB</td>
																					<td width="7%" class="tabledata-text" align="right" id="heapMax"><bean:write name="heapMemoryData" property="max" />MB</td>
																					<td width="25%" class="tabledata-text" style="vertical-align: top;" align="left" id="heapUsage">
																						<table id="innerheapMemory" style="border: none;">
																							<tr>
																								<td><div class="progress_bar" value ="${heapMemoryData.usage}" id="progressId" style="height: 15px;width:150px;"></div>
																								</td>
																								<td class="tabledata-text"> 
																									<bean:write name="heapMemoryData" property="usage" />%
																								</td>
																							</tr>
																						</table>
																					</td>
																					<td width="12%" class="tabledata-text" align="right" id="heapPeakUsed"><bean:write name="heapMemoryData" property="peakused" />MB</td>
																					<td width="7%" class="tabledata-text" align="right" style="border-right-style: solid;border-right-color: #CCCCCC;border-right-width: 1px;" id="heapPeakMax"><bean:write name="heapMemoryData" property="peakmax" />MB</td>
																			</tr>
																			</logic:iterate>
																		<%}else{ %>
																				<tr>
																					<td colspan="7" class="labeltext" align="center" style="border-right-style: solid;border-right-color: #CCCCCC;border-right-width: 1px;border-left-style: solid;border-left-color: #CCCCCC;border-left-width: 1px;">
																						No data available in table
																					</td>
																				</tr>
																		<%} %> 
																		<%	boolean checkNonHeapMemory=false;
																		if(nonHeapMemoryList !=null && nonHeapMemoryList.size() > 0){ %>
																			 <logic:iterate id="nonHeapMemoryData" name="viewNetServerGraphForm" property="nonHeapMemoryList" type="com.elitecore.elitesm.web.dashboard.memoryusage.NonHeapMemoryUsage">
																				<tr class="table-Tr-style" style="vertical-align: top;">
																		 			<% if(checkNonHeapMemory == false){%>
																			 			<td width="13%" class="first-left-border tabledata-text noHover" rowspan="<%=nonHeapMemoryList.size()%>" style="border-right-color: #CCCCCC;border-right-style: solid;border-right-width: 1px;">Non Heap Memory</td>
																					<%}checkNonHeapMemory=true; %>
																			 			<td width="20%" class="tabledata-text"><bean:write name="nonHeapMemoryData" property="name" /></td>
																			 			<td width="10%" class="tabledata-text" align="right"><bean:write name="nonHeapMemoryData" property="used" />MB</td>
																						<td width="7%" class="tabledata-text" align="right"><bean:write name="nonHeapMemoryData" property="max" />MB</td>
																			 			<td width="25%" class="tabledata-text" style="vertical-align: top;" align="left">
																							<table id="innernonheapMemory" style="border-bottom: none;">
																								<tr>
																									<td><div class="progress_bar" value ="${nonHeapMemoryData.usage}" style="height: 15px;width:150px;"></div>
																						 			</td>
																									<td class="tabledata-text">
																										<bean:write name="nonHeapMemoryData" property="usage" />%
																									</td>
																								</tr>
																							</table>
																						</td>
																						<td width="12%" class="tabledata-text" align="right"><bean:write name="nonHeapMemoryData" property="peakused" />MB</td>
																						<td width="7%" class="tabledata-text" align="right" style="border-right-style: solid;border-right-color: #CCCCCC;border-right-width: 1px;"><bean:write name="nonHeapMemoryData" property="peakmax" />MB</td>
																					</tr>
																				</logic:iterate>
																		<%} %>
																		</table>
																		</td>
																	</tr>
																	</table>
																	</td>
																</tr>
															</table>
															</td>
												</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="3" style="padding: 10px;">
					<table name="c_tblCrossProductList" width="100%" border="0" cellpadding="0" cellspacing="0" style="border: 1px solid #CCC;">
						<tr>
							<td class="tblheader-bold" colspan="2">Live Thread Details</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<div id="threaddemo" style="width: 100%;">
									<table width="100%" cellpadding="0"	cellspacing="0" border="0" id="threadDemo">
										<thead style="width: 100%;">
											<tr class="header-color" style="cursor: pointer;line-height: 20px;">
												<th width="8%" style="text-align: left;" class="labeltext"><bean:message bundle="dashboardResources" key="system.thread.name" /></th>
												<th width="6%" style="text-align: left;" class="labeltext" align="left"><bean:message bundle="dashboardResources" key="system.thread.state" /></th>
												<th width="6%" style="text-align: right; " class="labeltext" />
													<bean:message bundle="dashboardResources" key="system.thread.blockcount" />
												</th>
												<th width="6%" style="text-align: right;" class="labeltext" />
													<bean:message bundle="dashboardResources" key="system.thread.totalblockedtime" />
												</th>
												<th width="6%" style="text-align: right;" class="labeltext" />
													<bean:message bundle="dashboardResources" key="system.thread.waitedcount" />
												</th>
												<th width="6%" style="text-align: right; border-right-style: solid; border-right-color: #CCCCCC; border-right-width: 1px;" class="labeltext" />
													<bean:message bundle="dashboardResources" key="system.thread.totalwaitedtime" />
												</th>
											</tr>
										</thead>
										<tbody id="tbodyId">
											<%int counter=0;if (threadInfoDataList != null && threadInfoDataList.size() > 0) {%>
												<logic:iterate id="threadInfoData" name="viewNetServerGraphForm" property="threadInfoDataList" type="com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData">
												<tr id="tr<%=counter%>" class="table-Tr-style threadTable">
													<td class="tabledata-text" width="8%">
														<table width="100%" cellpadding="0"	cellspacing="0" border="0" style="border-style: none;">
															<tr>
																<td class="labeltext" style="border-style: none;" width="1%"><img alt="pin" title="Pin/Unpin" src="<%=basePath%>/images/pin.gif" height="20px" width="20px" style="display: none;cursor: pointer;" class="pinImage" id="pinImage" onclick="pinThread(<%=counter%>);"/></td>
																<td class="labeltext" style="border-style: none;">	<bean:write name="threadInfoData" property="threadName" /></td>
															</tr>
														</table>
													</td>
													<%counter++; %>
													<td class="tabledata-text" width="6%" align="left">
														&nbsp;&nbsp;&nbsp;
														<bean:write name="threadInfoData" property="threadState" />
														<div style="display:none;padding:0;margin:0;text-align: left;float: left;" id="threadStackDetail">
															<bean:write name="threadInfoData" property="statckTraceInfo" />
														</div>
													</td>
													<td class="tabledata-text" width="6%" align="right"><bean:write name="threadInfoData" property="blockedCount" /></td>
													<td class="tabledata-text" width="6%" align="right"><bean:write name="threadInfoData" property="totalBlockedTime" />-</td>
													<td class="tabledata-text" width="6%" align="right"><bean:write name="threadInfoData" property="waitedCount" /></td>
													<td class="tabledata-text" width="6%" align="right"><bean:write name="threadInfoData" property="totalWaitedTime" />-</td>
												</tr>
											</logic:iterate>
										<%}%>
									</tbody>
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="2"	style="padding-left: 10px; padding-right: 10px;text-align: left;" class="labeltext">
							<pre id="printThreadStackTrace" style="border: 1px solid #CCC;display: none;background-color: white;overflow: auto;width: 950px;height: 150px;margin: 0;padding: 0;display: block;padding: 10px;"></pre>
						</td>
					</tr>
				</table>
			</td>
		</tr>		
	</table>
</td>
</tr>
<tr> 
    <td>&nbsp;</td>
</tr>
</table>