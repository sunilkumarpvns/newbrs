<LINK REL ="stylesheet" TYPE="text/css" HREF="${pageContext.request.contextPath}/css/dashboard/widjettable.css?1=2"/>

<%String acctServiceWidgetId = request.getParameter("widgetId");%>

<div id="radAcctService" style="overflow: auto;" class="widget-class">
	<div id="progressDivId<%=acctServiceWidgetId%>" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=request.getContextPath()%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>
</div>

<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditAcctServiceTableWidget.jsp?widgetId=<%=acctServiceWidgetId%>" id="editJsp">
<span id="exportbtn_<%=acctServiceWidgetId%>"></span>
<script>
var acctServiceWidgetId=<%=acctServiceWidgetId%>;
var confObj = getWidgetConfiguration(acctServiceWidgetId);  <%-- Use ConfObj to retrive configuration --%>
var interval = 1;
var acctAAAServerIds =""; 
var acctServerName = "";
if(confObj != null) {
	interval =  confObj.get("REFRESHINTERVAL");
	acctAAAServerIds = confObj.get("ELITEAAAINSTANCES");
	acctServerName=confObj.get("NAME");
}
var parentID = "radAcctService_" + acctServiceWidgetId + "_" + new Date().getTime(); //  $('#radAcctService').parent().parent().attr("id");
$('#radAcctService').attr("id", parentID);
	
var data = {
		header : {
			id : parentID,
			type : "RAD_ACCT_SERV"
		},
		body : {
			interval : interval,
			aaaServerIds:acctAAAServerIds
		}
};
	
getDashBoardSocket().register($("#"+parentID).createDefaultTableWidget(interval));
getDashBoardSocket().sendRequest(data);

var pageIndex =1;
$('#exportbtn_'+<%=acctServiceWidgetId%>).click(function(){
	var acctServerArray = getAccountingServiceDataDetails();
	
	var acctServerDoc = new jsPDF('landscape');
	
	acctServerDoc.setProperties({
        title: acctServerName,
        subject: 'Widget Type Name : Generated on '+getDateTime(),           
        author: 'AutoGenerated',
        keywords: 'generated, javascript, web 2.0, ajax',
        creator: 'EliteCSM SM'
    });
	
	//create new canvas
    var elitecore_Image = new Image();
    var canvas_obj = document.createElement("canvas");
    var context_obj = canvas_obj.getContext('2d');
    elitecore_Image.src = $('#elitecore_Image').attr('src');
    canvas_obj.height=77;
    canvas_obj.width=213;
    
    context_obj.drawImage(elitecore_Image,0,0,213,77);
    var nextcontent = canvas_obj.toDataURL('image/jpeg');
	
	acctServerDoc.addImage(nextcontent, 'JPEG',5 ,1,30.43,11);
	
	acctServerDoc.setFontSize(8);
	acctServerDoc.setTextColor(0); 
	    
	var pageWidth= acctServerDoc.internal.pageSize.width;
	var pageHeight= acctServerDoc.internal.pageSize.height;
	    
	 acctServerDoc.text(pageWidth-50,10, "Generated on : " + getDateTime());
	    
	 acctServerDoc.setDrawColor(192,192,192); 
	 acctServerDoc.line(5,12 , pageWidth-5,12);
	     
	 acctServerDoc.text(pageWidth-20,pageHeight - 5, "Page : "+pageIndex );
	 acctServerDoc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);

	 acctServerDoc.setDrawColor(0);
	 acctServerDoc.setFillColor(1,81,152);
	 acctServerDoc.rect(5, 25, pageWidth-10, 5, 'F');
	    
	 acctServerDoc.setFontSize(8);
	 acctServerDoc.setTextColor(255);  
	    
	 acctServerDoc.text((pageWidth/2)-20, 28.5, acctServerName + " Details");
	 
	 //ESI Name - Rectangle
	 acctServerDoc.setDrawColor(217,230,246);
	 acctServerDoc.setFillColor(217,230,246);
	 console.log("pagewidth : " +pageWidth);
	 acctServerDoc.rect(5, 35, pageWidth-10, 10, 'FD');
	 
	 acctServerDoc.setFontSize(7);
	
	 acctServerDoc.setFontType("bold");
	 acctServerDoc.setTextColor(0); 
	 var splitTitle = acctServerDoc.splitTextToSize("Name", 20);
	 acctServerDoc.text(11, 39, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Duplicate Request", 20);
	 acctServerDoc.text(38, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Server Response", 20);
	 acctServerDoc.text(66, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Server Request", 15);
	 acctServerDoc.text(96, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "No Records", 20);
	 acctServerDoc.text(120, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Malformed Access Requests", 25);
	 acctServerDoc.text(148, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Bad Authenticators", 25);
	 acctServerDoc.text(186, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Packets Dropped", 20);
	 acctServerDoc.text(222, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Unknown Types", 20);
	 acctServerDoc.text(243, 38.5, splitTitle);
	 
	 splitTitle = acctServerDoc.splitTextToSize( "Invalid Request", 20);
	 acctServerDoc.text(270.5, 38.5, splitTitle);
	  	 
	 acctServerDoc.setTextColor(0);  
	 acctServerDoc.setFontType("normal");
	 var flag=1;
	    
	 var yIndex=52;
	 
	 //generate Table
	  $.each(acctServerArray,function(key,value){
		  if (yIndex >= pageHeight-10)
	    	{
			    acctServerDoc.addPage('a4','l');
		    	yIndex = 10; // Restart height position
	    		pageWidth= acctServerDoc.internal.pageSize.width;
	    		pageHeight= acctServerDoc.internal.pageSize.height;
	    	    pageIndex++;
	    	    acctServerDoc.text(pageWidth-20,pageHeight - 5, "Page : "+pageIndex );
	    	    acctServerDoc.line(5,pageHeight - 8 , pageWidth-5,pageHeight - 8);
	    	 
	    	}  
		  
		  acctServerDoc.text(11, yIndex-2, value.radiusAccServIdent);
		  
		  var totalDupRequestLength = value.radiusAccServTotalDupRequests.toString().length;
		  var charWidth = 1.70;
		  var str = reverse(value.radiusAccServTotalDupRequests.toString());
		  for(var i = totalDupRequestLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(47.5- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  var totalResLength = value.radiusAccServTotalResponses.toString().length;
		  str = reverse(value.radiusAccServTotalResponses.toString());
		  for(var i = totalResLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(76- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  var acctServerTotalRequest = value.radiusAccServTotalRequests.toString().length;
		  str = reverse(value.radiusAccServTotalRequests.toString());
		  for(var i = acctServerTotalRequest-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(105- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  var noRecFoundLength = value.radiusAccServTotalNoRecords.toString().length;
		  str = reverse(value.radiusAccServTotalNoRecords.toString());
		  for(var i = noRecFoundLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(133- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  var malformedRequestLength = value.totalMalformedRequests.toString().length;
		  str = reverse(value.totalMalformedRequests.toString());
		  for(var i = malformedRequestLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(169- charWidth * i, yIndex-2, str[i]);
		  }

		  var badAuthenticatorLength = value.totalBadAuthenticators.toString().length;
		  str = reverse(value.totalBadAuthenticators.toString());
		  for(var i = badAuthenticatorLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(208- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  var packetDroppedLength = value.totalPacketsDropped.toString().length;
		  str = reverse(value.totalPacketsDropped.toString());
		  for(var i = packetDroppedLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(231- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  var totalUnknownTypeLength = value.radiusAccServTotalUnknownTypes.toString().length;		  
		  str = reverse(value.radiusAccServTotalUnknownTypes.toString());
		  for(var i = totalUnknownTypeLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(261- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  var invalidReqLength = value.totalInvalidRequests.toString().length;
		  
		  str = reverse(value.totalInvalidRequests.toString());
		  for(var i = invalidReqLength-1 ; i >= 0 ; i--){			  
			  acctServerDoc.text(287- charWidth * i, yIndex-2, str[i]);
		  }
		  
		  acctServerDoc.setDrawColor(192,192,192); 
		  acctServerDoc.line(5, yIndex, pageWidth-5, yIndex);
		  yIndex=yIndex+7;
		  
	  });
	 
	 var data = acctServerDoc.output();
	 var buffer = new ArrayBuffer(data.length);
	 var array = new Uint8Array(buffer);

	 for (var i = 0; i < data.length; i++) {
	      array[i] = data.charCodeAt(i);
	 }

	 var blob = new Blob(
	       [array],
	       {type: 'application/pdf', encoding: 'raw'}
	 );
	
	 saveAs(blob, acctServerName+'.pdf');
});

function reverse(s){
    return s.split("").reverse().join("");
}

function getAccountingServiceDataDetails(){
    var jResponseData="";
	$.ajax({
		url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getAccountingServiceData',
	    type:'GET',
	    async:false,
	    data:{serverIds:acctAAAServerIds},
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