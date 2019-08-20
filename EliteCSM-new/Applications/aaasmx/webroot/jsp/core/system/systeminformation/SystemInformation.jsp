<%@page import="java.util.HashMap" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.Properties" %>
<%@page import="java.util.TreeSet" %>
<%@page import="java.util.List" %>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@page import="com.elitecore.elitesm.web.core.system.systeminformation.form.SystemInformationForm" %>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.HeapMemoryUsageData" %>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.NonHeapMemoryUsage" %>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.GarbageCollectorData" %>
<%@page import="com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<% String basePath = request.getContextPath();%>

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
<%
	SystemInformationForm systemInformationForm = (SystemInformationForm) request.getAttribute("systemInformationForm");
	List<HeapMemoryUsageData> heapMemoryList=systemInformationForm.getHeapMemoryList();
	List<NonHeapMemoryUsage> nonHeapMemoryList=systemInformationForm.getNonHeapMemoryList();
	List<GarbageCollectorData> garbageCollectorDataList=systemInformationForm.getGarbageCollectorDataList();
	List<ThreadInformationData> threadInfoDataList=systemInformationForm.getThreadInfoDataList();
	Map<String,String> systemPropertiesMap=systemInformationForm.getSystemPropertiesMap();
	int threadCount=0;
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript">
var pieChart;
var pinImage=false;
var tcount=0;
	$(document).ready(function(){
		$('#example').dataTable( {
			"sScrollY": "162px",
			"bPaginate": false
		});
		$('#threadDemo').dataTable( {
			"sScrollY": "200px",
			"bPaginate": false
		});
		
		$('.dataTables_scrollHeadInner').css("width", "100%");
		$('.dataTable').css("width", "100%");
		$("#threadDemo_filter").append('&nbsp;');
		$("#threadDemo_filter").append('<input class="light-btn" type="button" id="exportBtn" value="  Export  " style="float :right !important;margin-left:0.2em;" />');
		$("#threadDemo_filter").append('<input class="light-btn" type="button" id="refreshBtn" value=" Refresh" style="float :right !important;" onclick="refreshJVMThread();" />');
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
		

		
		requestGarbageCollectionData();
		requestJVMHeapMemoryData();
		requestJVMNonHeapMemoryData();
		 
	});
	function refreshJVMThread(){
		requestForThreadDetails();
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
	
	function requestForThreadDetails(){
		tcount=0;
		$.ajax({
	        url: '<%=basePath%>/systemInformation.do?method=getThreadDetails',
	        dataType: 'json',
	        type: 'GET',
		    async: true,
	        error: function (threadDetailsList) {
	          /*   setTimeout(requestForThreadDetails, 1000); */
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
	        	
	        /* 	$('#threadDemo').dataTable().fnDestroy();
    		 	
    		 	$('#threadDemo').dataTable({
    				"sScrollY": "200px",
    				"bPaginate": false
    			});
	        	
    			$('.dataTables_scrollHeadInner').css("width", "100%");
    			$('.dataTable').css("width", "100%");
    			$("#threadDemo_filter").append('&nbsp;');
    			$("#threadDemo_filter").append('<input class="light-btn" type="button" id="exportBtn" value="  Export  " style="float :right !important;margin-left:0.2em;" />');
    			$("#threadDemo_filter").append('<input class="light-btn" type="button" id="refreshBtn" value=" Refresh" style="float :right !important;" onclick="refreshJVMThread();" />');
    		
    		  */	
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
		
	function requestJVMNonHeapMemoryData(){
		$.ajax({
	        url: '<%=basePath%>/systemInformation.do?method=getJVMNonHeapMemoryData',
	        dataType: 'json',
	        type: 'GET',
		    async: true,
	        error: function (jvmNonHeapMemoryDataList) {
	            setTimeout(requestJVMNonHeapMemoryData, 1000);
	        },
	        success: function (jvmNonHeapMemoryDataList) {
	        	
	        	$.each(jvmNonHeapMemoryDataList, function( key, nonHeapMemoryData ) {
	        		$('#nonHeapname'+key).html(nonHeapMemoryData.name);
	        		$('#nonHeapUsed'+key).html(nonHeapMemoryData.used+"MB");
	        		$('#nonHeapMax'+key).html(nonHeapMemoryData.max+"MB");
	        		
	        		$('#nonHeapUsage'+key).html("<table id='innerheapMemory' style='border-bottom: none;text-align:right'><tr><td><div class='progress_bar' id='nonHeapProgress"+key+"' value ='50' style='height: 15px;width:150px;'></div></td><td class='tabledata-text'>"+nonHeapMemoryData.usage+"%</td></tr></table>");
	        			
	        		$( "#nonHeapProgress"+key).progressbar({
	   				 value: parseInt(nonHeapMemoryData.usage)
	   				});
	   			 
		   			 var progressvarVal=parseInt(nonHeapMemoryData.usage);
		   			 var selector = "#nonHeapProgress"+key+" > div";
		   			 
		   			 if(progressvarVal < 50){
		   				 $(selector).css({ 'background': 'green' });
		   			 }else if(progressvarVal > 50 && progressvarVal <75){
		   				 $(selector).css({ 'background': 'orange' });
		   			 }else{
		   				 $(selector).css({ 'background': 'red' });
		   			 }
	        		
	        		$('#nonHeapPeakUsed'+key).html(nonHeapMemoryData.peakused+"MB");
	        		$('#nonHeapPeakMax'+key).html(nonHeapMemoryData.peakmax+"MB");
	        		
	        		$('#nonHeapname'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#nonHeapUsed'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#nonHeapMax'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#nonHeapUsage'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#nonHeapPeakUsed'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#nonHeapPeakMax'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		
	        	});
	        	$('#nonHeapMemoryTd').css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        	setTimeout(requestJVMHeapMemoryData, 1000);
	        },
	        cache: false
	    });
	}
	function requestJVMHeapMemoryData(){
		$.ajax({
	        url: '<%=basePath%>/systemInformation.do?method=getJVMHeapMemoryData',
	        dataType: 'json',
	        type: 'GET',
		    async: true,
	        error: function (jvmHeapMemoryDataList) {
	            setTimeout(requestJVMHeapMemoryData, 1000);
	        },
	        success: function (jvmHeapMemoryDataList) {
	        	
	        	$.each(jvmHeapMemoryDataList, function( key, heapMemoryData ) {
	        		$('#name'+key).html(heapMemoryData.name);
	        		$('#heapUsed'+key).html(heapMemoryData.used+"MB");
	        		$('#heapMax'+key).html(heapMemoryData.max+"MB");
	        		
	        		$('#heapUsage'+key).html("<table id='innerheapMemory' style='border-bottom: none;'><tr><td><div class='progress_bar' id='edenProgress"+key+"' value ='50' style='height: 15px;width:150px;'></div></td><td class='tabledata-text'>"+heapMemoryData.usage+"%</td></tr></table>");
	        			
	        		$( "#edenProgress"+key).progressbar({
	   				 value: parseInt(heapMemoryData.usage)
	   				});
	   			 
		   			 var progressvarVal=parseInt(heapMemoryData.usage);
		   			 var selector = "#edenProgress"+key+" > div";
		   			 
		   			 if(progressvarVal < 50){
		   				 $(selector).css({ 'background': 'green' });
		   			 }else if(progressvarVal > 50 && progressvarVal <75){
		   				 $(selector).css({ 'background': 'orange' });
		   			 }else{
		   				 $(selector).css({ 'background': 'red' });
		   			 }
	        		
	        		$('#heapPeakUsed'+key).html(heapMemoryData.peakused+"MB");
	        		$('#heapPeakMax'+key).html(heapMemoryData.peakmax+"MB");
	        		
	        		
	        		$('#name'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#heapUsed'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#heapMax'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#heapUsage'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#heapPeakUsed'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#heapPeakMax'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		
	        	});
	        	$('#heapMemoryTd').css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        	setTimeout(requestJVMHeapMemoryData, 1000);
	        },
	        cache: false
	    });
	}
	function requestGarbageCollectionData(){
		$.ajax({
	        url: '<%=basePath%>/systemInformation.do?method=getGarbageInfo',
	        dataType: 'json',
	        type: 'GET',
		    async: true,
	        error: function (garbageData) {
	            setTimeout(requestGarbageCollectionData, 1000);
	        },
	        success: function (garbageDataList) {
	        	
	        	$.each( garbageDataList, function( key, value ) {
	        		$('#gcName'+key).html(value.name);
	        		$('#gcCollectionCount'+key).html(value.collectionCount);
	        		$('#gcCollectionTime'+key).html(value.collectionTime);
	        		
	        		$('#gcName'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#gcCollectionCount'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        		$('#gcCollectionTime'+key).css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	        	});
	        	
	        	setTimeout(requestGarbageCollectionData, 1000);
	        },
	        cache: false
	    });
	}
	function requestPieChartData(){
		
		 $.ajax({
		        url: '<%=basePath%>/systemInformation.do?method=getPieChartData',
		        dataType: 'json',
		        type: 'GET',
			    async: true,
		        error: function (pieData) {
		            setTimeout(requestPieChartData, 1000);
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
		        	
		            setTimeout(requestPieChartData,1000);
		        },
		        cache: false
		    });
		
	}

	$(function () {
	    
		$("#exportBtn").click(function(e) {
		    window.open('data:application/vnd.ms-excel,' + encodeURIComponent( $('div[id$=threaddemo]').html()));
		    e.preventDefault(); 
		});
		
		 var colors = Highcharts.getOptions().colors,
	     categories = ['Free Memory', 'Used Memory'],
	     name = 'JVM Heap Memory',
	     data = [{
	             y: <%=systemInformationForm.getFreeHeapMemory()%>+<%=systemInformationForm.getFreeNonHeapMemory()%>,
	             color: colors[0],
	             drilldown: {
	                 name: 'Free Memory',
	                 categories: ['Heap', 'Non-Heap'],
	                 data: [<%=systemInformationForm.getFreeHeapMemory()%>, <%=systemInformationForm.getFreeNonHeapMemory()%>],
	                 color: colors[0]
	             }
	         }, {
	             y: <%=systemInformationForm.getHeapMemoryUsed()%>+<%=systemInformationForm.getNonHeapMemoryUsed()%>,
	             color: colors[1],
	             drilldown: {
	                 name: 'Used Memory',
	                 categories: ['Heap', 'Non-Heap'],
	                 data: [<%=systemInformationForm.getHeapMemoryUsed()%>,<%=systemInformationForm.getNonHeapMemoryUsed()%>],
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
	});
	
	setTitle('<bean:message key="systemconfiguration.parameter"/>');
</script>

  <style>
  .searchTextBox{
	background-image:url('images/Search.png');
	background-repeat:no-repeat;
	padding-left:20px;
	background-position:5px center;
	border:1px solid #CCCCCC;
	border-radius:2px 2px 2px 2px;
	padding-bottom:2px}
</style>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
<tr>
	<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
	<td class="box">
		 <table cellpadding="0" cellspacing="0" border="0" width="100%">
		 		 <tr>
		    		<td align="right" >
					 	<table cellpadding="0" cellspacing="0" border="0" width="100%">
					 			<tr>
									<td valign="right" colspan="2" >
										<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<tr>
												<td colspan="3" class="system-main-table" align="right" >
												 	<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
														<tr>
															<td width="2%">	
																<img src="<%=request.getContextPath()%>/images/clock.png" alt="clock"/>	
															</td>
															<td class="serverUpTime">	
																<span style="color: rgb(1, 81, 152);">Server Up Time :</span>
																<span><%=systemInformationForm.getServerUpTime() %></span>
															</td>
														</tr>
														<tr>
															<td width="2%" colspan="2">	
																<input type="button"  name="button" value="Restart Server" class="restart-btn"/>
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
		    		<td>
					 	<table cellpadding="0" cellspacing="0" border="0" width="100%">
					 		<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<tr>
												<td colspan="3"  style="padding-left: 20px;padding-right: 20px;">
												 	<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
														<tr>
															<td class="memory-usage-style ui-widget-header widgetheader" style="font-weight: bold;" colspan="2">		
																<bean:message bundle="dashboardResources" key="system.systemconfiguration.memoryusage" />  
															</td>
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
																									<div id="jvmMemoryChart" style="width: 400px; height: 400px; margin: 0 auto;"></div>
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
																										<table width="100%"  cellpadding="0" cellspacing="0" border="0" id="garbageDataTable">
																												 	<tr class="header-color">
																														<th width="15%"  class="system-table-header" align="left">
																															<bean:message bundle="dashboardResources" key="system.garbagecollection.name" />  
																														</th>																			 
																												 		<th  class="system-table-header" align="right">
																												 			<bean:message bundle="dashboardResources" key="system.garbagecollection.totalcollectioncount" />  
																												 		</th>
																									 			 		<th  class="system-table-header" align="right">
																									 			 			<bean:message bundle="dashboardResources" key="system.garbagecollection.totalcollectiontime" />  
																									 			 		</th>
																												 	</tr>
																													 	<%  int gcCounter=0;
																													 		if(garbageCollectorDataList !=null && garbageCollectorDataList.size() > 0){ %>
																														 <logic:iterate id="garbageCollectionData" name="systemInformationForm" property="garbageCollectorDataList" type="com.elitecore.elitesm.web.dashboard.memoryusage.GarbageCollectorData">
																															<tr class="garbage-table-Tr-style">
																														 		<td class="first-left-border tabledata-text" id="gcName<%=gcCounter%>">
																														 			<bean:write name="garbageCollectionData" property="name" />
																														 		</td>
																														 		<td class="tabledata-text" align="right" id="gcCollectionCount<%=gcCounter%>"><bean:write name="garbageCollectionData" property="collectionCount" /></td>
																														 		<td class="tabledata-text" align="right" id="gcCollectionTime<%=gcCounter%>"><bean:write name="garbageCollectionData" property="collectionTime" /></td>
																														 	</tr>
																														 	<%gcCounter++;%>
																														</logic:iterate>
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
																					<table width="100%"  cellpadding="0" cellspacing="0" border="0" id="heapMemoryTable">
																					 	<tr>
																							<td colspan="7" class="memory-usage-title">		
																									<bean:message bundle="dashboardResources" key="system.systemconfiguration.memoryusage" />  
																								</td>
																						</tr>
																					 	<tr class="header-color">
																					 		<th width="10%" class="system-table-header memory-category" align="left">Memory Category</th>
																							<th width="15%" class="system-table-header" align="left">
																								<bean:message bundle="dashboardResources" key="system.memoryusage.name" />  
																							</th>																			 
																					 		<th width="10%" class="system-table-header" align="right">
																					 			<bean:message bundle="dashboardResources" key="system.memoryusage.used" /> 
																					 		</th>
																		 			 		<th width="15%" class="system-table-header" align="right">
																		 			 			<bean:message bundle="dashboardResources" key="system.memoryusage.maxused" /> 
																		 			 		</th>
																					 		<th width="22%" class="system-table-header" align="right">
																					 			<bean:message bundle="dashboardResources" key="system.memoryusage.usage" /> 
																					 		</th>
																					 		<th width="12%" class="system-table-header" align="right">
																					 			<bean:message bundle="dashboardResources" key="system.memoryusage.peakused" /> 
																					 		</th>
																					 		<th width="12%" class="system-table-header" align="right" style="border-right-style: solid;border-right-color: #CCCCCC;border-right-width: 1px;">
																					 			<bean:message bundle="dashboardResources" key="system.memoryusage.peakmax" /> 
																					 		</th>
																					 	</tr>
																					 	<%	boolean checkHeapMemory=false;
																					 		int heapMemoryCount=0;
																					 		if(heapMemoryList !=null && heapMemoryList.size() > 0){ %>
																							 <logic:iterate id="heapMemoryData" name="systemInformationForm" property="heapMemoryList" type="com.elitecore.elitesm.web.dashboard.memoryusage.HeapMemoryUsageData">
																								<tr class="table-Tr-style">
																							 		<% if(checkHeapMemory == false){%>
																							 		<td width="10%" class="first-left-border tabledata-text noHover" id="heapMemoryTd" rowspan="<%=heapMemoryList.size()%>" style="border-right-color: #CCCCCC;border-right-style: solid;border-right-width: 1px;">Heap Memory</td>
																							 		<%}checkHeapMemory=true; %>
																							 		<td width="15%" class="tabledata-text" id="name<%=heapMemoryCount%>"><bean:write name="heapMemoryData" property="name" /></td>
																							 		<td width="10%" class="tabledata-text" align="right" id="heapUsed<%=heapMemoryCount%>"><bean:write name="heapMemoryData" property="used" />MB</td>
																							 		<td width="15%" class="tabledata-text" align="right" id="heapMax<%=heapMemoryCount%>"><bean:write name="heapMemoryData" property="max" />MB</td>
																							 		<td width="22%" class="tabledata-text" style="vertical-align: top;" align="left" id="heapUsage<%=heapMemoryCount%>">
																							 			<table id="innerheapMemory">
																											<tr>
																												<td><div class="progress_bar" value ="${heapMemoryData.usage}" style="height: 15px;width:150px;"></div>
																									 			</td>
																												<td class="tabledata-text"> 
																													<bean:write name="heapMemoryData" property="usage" />%
																												</td>
																											</tr>
																										</table>
																							 		</td>
																							 		<td width="12%" class="tabledata-text" align="right" id="heapPeakUsed<%=heapMemoryCount%>"><bean:write name="heapMemoryData" property="peakused" />MB</td>
																							 		<td width="12%" class="tabledata-text" align="right" id="heapPeakMax<%=heapMemoryCount%>" style="border-right-style: solid;border-right-color: #CCCCCC;border-right-width: 1px;"><bean:write name="heapMemoryData" property="peakmax" />MB</td>
																							 	</tr>
																							 	<%heapMemoryCount++;%>
																							</logic:iterate>
																					 	<%} %>
																					 	<%	int nonHeapCounter=0;
																					 		boolean checkNonHeapMemory=false;
																					 		if(nonHeapMemoryList !=null && nonHeapMemoryList.size() > 0){ %>
																							 <logic:iterate id="nonHeapMemoryData" name="systemInformationForm" property="nonHeapMemoryList" type="com.elitecore.elitesm.web.dashboard.memoryusage.NonHeapMemoryUsage">
																									<tr class="table-Tr-style">
																							 			<% if(checkNonHeapMemory == false){%>
																							 			<td width="10%" class="first-left-border tabledata-text noHover" id="nonHeapMemoryTd" rowspan="<%=nonHeapMemoryList.size()%>" style="border-right-color: #CCCCCC;border-right-style: solid;border-right-width: 1px;">Non Heap Memory</td>
																							 			<%}checkNonHeapMemory=true; %>
																							 			<td width="15%" class="tabledata-text" id="nonHeapname<%=nonHeapCounter%>"><bean:write name="nonHeapMemoryData" property="name" /></td>
																							 			<td width="10%" class="tabledata-text" align="right" id="nonHeapUsed<%=nonHeapCounter%>"><bean:write name="nonHeapMemoryData" property="used" />MB</td>
																							 			<td width="15%" class="tabledata-text" align="right" id="nonHeapMax<%=nonHeapCounter%>"><bean:write name="nonHeapMemoryData" property="max" />MB</td>
																							 			<td width="22%" class="tabledata-text" id="nonHeapUsage<%=nonHeapCounter%>">
																							 			<table id="innernonheapMemory">
																											<tr>
																												<td><div class="progress_bar" value ="${nonHeapMemoryData.usage}" style="height: 15px;width:150px;"></div>
																									 			</td>
																												<td class="tabledata-text">
																													<bean:write name="nonHeapMemoryData" property="usage" />%
																												</td>
																											</tr>
																										</table>
																							 		</td>
																							 		<td width="12%" class="tabledata-text" align="right" id="nonHeapPeakUsed<%=nonHeapCounter%>"><bean:write name="nonHeapMemoryData" property="peakused" />MB</td>
																							 		<td width="12%" class="tabledata-text" align="right" id="nonHeapPeakMax<%=nonHeapCounter%>" style="border-right-style: solid;border-right-color: #CCCCCC;border-right-width: 1px;"><bean:write name="nonHeapMemoryData" property="peakmax" />MB</td>
																							 	</tr>
																							 	<%nonHeapCounter++; %>
																							</logic:iterate>
																					 	<%} %>
																					</table>
																				</td>
																	</tr>
																	</table>
																	</td>
																</tr>
																<tr>
																	<td colspan="2"	style="padding-left: 10px; padding-right: 10px;">
																		<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box">
																			<tr>
																				<td  colspan="2" style="font-size: 12px;color: white;background-color: #2565A5;font-weight: bold;text-align: left;padding-top: 2px;padding-bottom: 2px;">
																					<bean:message bundle="dashboardResources" key="system.livethreads" />
																				</td>
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
																								<% int counter=0;
																									if (threadInfoDataList != null && threadInfoDataList.size() > 0) {
																								%>
																								<logic:iterate id="threadInfoData" name="systemInformationForm" property="threadInfoDataList" type="com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData">
																									<tr id="tr<%=counter%>" class="table-Tr-style threadTable">
																										<td class="tabledata-text" width="8%">
																											<table width="100%" cellpadding="0"	cellspacing="0" border="0" style="border-style: none;">
																												<tr>
																													<td class="labeltext" style="border-style: none;" width="1%"><img alt="pin" title="Pin/Unpin" src="<%=basePath%>/images/pin.gif" height="20px" width="20px" style="display: none;cursor: pointer;" class="pinImage" id="pinImage" onclick="pinThread(<%=counter%>);"/></td>
																													<td class="labeltext" style="border-style: none;" id="threadName<%=counter%>">	<bean:write name="threadInfoData" property="threadName" /></td>
																												</tr>
																											</table>
																										</td>
																										<%counter++; %>
																										<td class="tabledata-text" width="6%" align="left">
																											&nbsp;&nbsp;&nbsp;<bean:write name="threadInfoData" property="threadState" />
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
																					<pre id="printThreadStackTrace" style="border: 1px solid #CCC;display: none;background-color: white;overflow: auto;width: 1055px;height: 150px;margin: 0;padding: 0;display: block;padding: 10px;"></pre>
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
												<td colspan="3"  style="padding-left: 20px;padding-top: 20px;padding-right: 20px;" >
												 	<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
														<tr>
															<td class="ui-widget-header widgetheader" colspan="2" style="font-size: 12px;color: white;background-color: rgb(217, 230, 246);font-weight: bold;">		
																<bean:message bundle="dashboardResources" key="system.properties" />  
															</td>
														</tr>
														<tr>
															<td colspan="2" align="center" >
															<div id="demo" style="width: 100%;">
																<table width="100%"  cellpadding="0" cellspacing="0" border="0" id="example" style="table-layout: fixed; width: 100%">
																 	<thead style="width: 100%;">
																		<tr style="cursor: pointer;background-color: #E0ECF8;line-height: 20px;">
																			<th style="text-align: left;" width="30%" class="labeltext">
																				<bean:message bundle="dashboardResources" key="system.key" />
																			</th>
																			<th width="70%" style="text-align: left;border-right-style: solid;border-right-color:#CCCCCC ;border-right-width: 1px;" class="labeltext"/>
																				<bean:message bundle="dashboardResources" key="system.value" />
																			</th>
																		</tr>	
																	</thead>
																	<tbody>
																	<%if(systemPropertiesMap != null && systemPropertiesMap.size() > 0){ 
																		for (Map.Entry<String, String> systemProp : systemPropertiesMap.entrySet()){%>
																	    <tr id="2" class="table-Tr-style">
																			<td class="labeltext table-Tr-style" width="30%"  style="vertical-align: top;word-wrap: break-word"><%=systemProp.getKey() %></td>
																			<td class="labeltext" style="vertical-align: top;word-wrap: break-word" width="70%">
																				<%if(systemProp.getValue().length() > 0 && systemProp.getValue() != null){ %>
																					<%=systemProp.getValue()%>
																				<%}else{ %>
																					-
																				<%} %>
																			</td>
																		</tr>
																	<%}}%>
																	</tbody>
																</table>
																</div>
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
				 	</td>
				 </tr>
				 
		 </table>
	</td>
</tr>
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>