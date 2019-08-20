<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.web.history.DatabaseHistoryData"%>
<%@page import="com.elitecore.elitesm.web.history.HistoryData" %>
<html>
<head>
<%
	int counter=0; 
	String name=request.getParameter("name");
	String strSystemAuditId=request.getParameter("systemAuditId");
	if(strSystemAuditId != null){
		strSystemAuditId=strSystemAuditId.trim();
	}
	
	String historyDatas = (String) request.getAttribute("historyDataString");
	
	List<DatabaseHistoryData> lstDatabaseDSHistoryDatas = (List<DatabaseHistoryData>)request.getAttribute("lstDatabaseDSHistoryDatas");
%>
<meta charset="utf-8" />
<script src="<%=request.getContextPath()%>/js/history/jquery-2.0.3.min.js"></script>
<link rel="stylesheet" 	href="<%=request.getContextPath()%>/css/history/jquery-ui.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" />
<script src="<%=request.getContextPath()%>/js/history/jquery-ui.js"></script>
<script src="<%=request.getContextPath()%>/js/history/treeTable.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/filesaver/FileSaver.min.js"></script>
<style type="text/css">
.new {
	background-color: red;
}

.all-label-text {
	padding-left: 20px;
}
</style>
<script type="text/javascript">

$(document).ready(function(){
	<%
	if( lstDatabaseDSHistoryDatas != null && lstDatabaseDSHistoryDatas.size() > 0 ){
		for(DatabaseHistoryData databaseHistoryData : lstDatabaseDSHistoryDatas ){
			String ipAddress = databaseHistoryData.getIpAddress();
			String userName = databaseHistoryData.getUserName();
			String lastUpdatedTime = databaseHistoryData.getLastupdatetime();
			String historyData = "";
			List<HistoryData> historyDataList = databaseHistoryData.getHistoryData();
			
			for( HistoryData historyDataObj : historyDataList ){
				historyData = historyDataObj.getHistory();
			}%>
			var historyData = JSON.stringify(<%=historyData.toString()%>);	
			
			addHistoryData('<%=ipAddress%>','<%=userName%>','<%=lastUpdatedTime%>',historyData);
		<%}
	}
%>

});

function addHistoryData(ipAddress,userName,lastUpdatedTime,historyData){
	
	if(historyData.length > 2){
		var tableRowStr = $("#mapping-template-table").find("tr");
		$('.main-mapping-table').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
		
		var username = $('.main-mapping-table').find(' > tbody > tr:last-child').find("span[class='usename']");
		$(username).text(userName);
		
		var ipAddressObj = $('.main-mapping-table').find(' > tbody > tr:last-child').find("span[class='ipAddress']");
		$(ipAddressObj).text(ipAddress);
		
		var lastupdatetime = $('.main-mapping-table').find(' > tbody > tr:last-child').find("span[class='lastupdatetime']");
		$(lastupdatetime).text(lastUpdatedTime);
		
		var innerTable = $('.main-mapping-table').find(' > tbody > tr:last-child').find(".inner-mapping-table");
		
		var index=0;
		$.each($.parseJSON(historyData), function(key,value) {
			
			if(value.values){
				
				$.each(value.values , function(key1,value1) {
					
					var grooyFileName = value1.Field;
					
					if(value1.values){
						
						$.each(value1.values, function(keys,values){
							
							var oldValue =  values.OldValue;
							var newValue = values.NewValue;
							
							if(oldValue != "-"){
								
								var elemm = document.createElement('span');
								elemm.className ="downloadElement";
								
								elemm.addEventListener("click", function(e) {
									downloadGroovyFile(this, oldValue,grooyFileName);
								}, false);
								
								
								var elementName = "oldvalue"+ index++ ;
								$(innerTable).append( "<tr><td class='labeltext'>"+grooyFileName+"</td><td class='"+elementName+" all-label-text'>" + $(elemm) +"</td><td class='labeltext all-label-text'>-</td></tr>" ); 
								$(innerTable).find('.'+elementName).html(elemm);
							}
							
							if(newValue != "-"){
								
								var elemm = document.createElement('span');
								elemm.className ="downloadElement";
								
								elemm.addEventListener("click", function(e) {
									downloadGroovyFile(this, newValue,grooyFileName);
								}, false);
								
								var elementName = "newvalue"+ index++ ;
								
								$(innerTable).append( "<tr><td class='labeltext'>"+grooyFileName+"</td><td class='labeltext all-label-text'>-</td><td class='"+elementName+" all-label-text'>" + $(elemm) +"</td></tr>" ); 
								$(innerTable).find('.'+elementName).html(elemm);
							}
						});
					}else{
						var oldValue =  value1.OldValue;
						var newValue = value1.NewValue;
						
						if( oldValue != "-" && newValue != "-"){
							
							var elemm = document.createElement('span');
							elemm.className ="downloadElement";
							
							elemm.addEventListener("click", function(e) {
								downloadGroovyFile(this, oldValue,grooyFileName);
							}, false);
							
							var elementName = "oldvalue"+ index++ ;
							
							var newElems = document.createElement('span');
							newElems.className="downloadElement";
							
							newElems.addEventListener("click", function(e) {
								downloadGroovyFile(this, newValue,grooyFileName);
							}, false);
							
							var newElementName = "newvalue"+ index++ ;
							
							$(innerTable).append( "<tr><td class='labeltext'>"+grooyFileName+"</td><td class='"+elementName+" all-label-text'>" + $(elemm) +"</td><td class='"+newElementName + " all-label-text'>" + $(newElems) +"</td></tr>" ); 
							$(innerTable).find('.'+elementName).html(elemm);
							$(innerTable).find('.'+newElementName).html(newElems);
						} else if( oldValue != "-" && newValue == "-"){
							var elemm = document.createElement('span');
							elemm.className ="downloadElement";
							
							elemm.addEventListener("click", function(e) {
								downloadGroovyFile(this, oldValue,grooyFileName);
							}, false);
							
							
							var elementName = "oldvalue"+ index++ ;
							$(innerTable).append( "<tr><td class='labeltext'>"+grooyFileName+"</td><td class='"+elementName+" all-label-text'>" + $(elemm) +"</td><td class='labeltext all-label-text'>-</td></tr>" ); 
							$(innerTable).find('.'+elementName).html(elemm);
						} else if( oldValue == "-" && newValue != "-"){
							var elemm = document.createElement('span');
							elemm.className ="downloadElement";
							
							elemm.addEventListener("click", function(e) {
								downloadGroovyFile(this, newValue,grooyFileName);
							}, false);
							
							var elementName = "newvalue"+ index++ ;
							
							$(innerTable).append( "<tr><td class='labeltext'>"+grooyFileName+"</td><td class='labeltext all-label-text'>-</td><td class='"+elementName+" all-label-text'>" + $(elemm) +"</td></tr>" ); 
							$(innerTable).find('.'+elementName).html(elemm);
						}
					}
				});
			}else{
				var oldValue =  value.OldValue;
				var newValue = value.NewValue;
				$(innerTable).append( "<tr><td class='labeltext'>"+value.Field+"</td><td class='labeltext'>" + oldValue +"</td><td class='labeltext'>" +newValue+"</td></tr>" ); 
			}
		});
	}else{
		var tableRowStr = $("#mapping--empty-template-table").find("tr");
		$('.main-mapping-table').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
		
		var username = $('.main-mapping-table').find(' > tbody > tr:last-child').find("span[class='usename']");
		$(username).text(userName);
		
		var ipAddressObj = $('.main-mapping-table').find(' > tbody > tr:last-child').find("span[class='ipAddress']");
		$(ipAddressObj).text(ipAddress);
		
		var lastupdatetime = $('.main-mapping-table').find(' > tbody > tr:last-child').find("span[class='lastupdatetime']");
		$(lastupdatetime).text(lastUpdatedTime);
		
	}
}

function downloadGroovyFile(obj,fileContent,fileName){
	 var blob = new Blob([fileContent], {type: "text/groovy"});
	 saveAs(blob, fileName);
}
</script>

<style type="text/css">
.downloadElement {
	position: relative;
	cursor: pointer;
}

.downloadElement:before {
	content: "\f019";
	font-family: FontAwesome;
	font-style: normal;
	font-weight: normal;
	text-decoration: inherit;
	color: #015198;
	font-size: 16px;
	cursor: pointer;
}

.main-mapping-table tr:hover {
    background-color: white;
}

.main-mapping-table tr:hover td {
    background-color: #f2f2f2;
}
</style>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/history/simple.css">
</head>
<body>
<table width="100%" cellspacing="0" cellpadding="0" border="0">
		<tr>
			<td class="tblheader-bold" colspan="3">
	  			  View History
			</td>
		</tr>
		<%int index=0; %>
		<logic:notEmpty name="lstDatabaseDSHistoryDatas">
		<tr>
			<td class="td-padding">
				<div class="">
				<table width="100%" cellspacing="0" cellpadding="0" border="0" class="main-mapping-table">
						
				</table>
				</div>
			</td>
		</tr>
		</logic:notEmpty>
		<logic:empty name="lstDatabaseDSHistoryDatas">
		<tr>
			<td colspan="2" align="center" class="labeltext no-history-details no-history-container" >No History Found</td>
		</tr>
		</logic:empty>
</table>

<table class="mapping-template-table" id="mapping-template-table"  style="display: none;" width="100%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td>
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td class="labeltext" style="vertical-align: top; padding-top: 10px;">
						&nbsp;<img alt="user" src="<%=basePath%>/images/useravatar.png"
						height="16" width="18" />&nbsp; <font class="userclass"
						style="text-transform: capitalize; vertical-align: top;"> 
						<span class="usename"></span>
					</font> <font style="vertical-align: top;">made changes from </font> <font
						class="userclass"
						style="text-transform: capitalize; vertical-align: top;"> 
						<span class="ipAddress"></span>
					</font> <font style="vertical-align: top;"> Address -
						<span class="lastupdatetime"></span>
					</font>
					</td>
				</tr>
				<tr>
					<td class="labeltext border-class" width="100%">
						<table width="100%" cellspacing="0" cellpadding="0" border="0" class="inner-mapping-table">
							<tr>
								<td class="labeltext" width="33.33%" style="font-weight: bold;">Field</td>
								<td class="labeltext" width="33.33%" style="font-weight: bold;">Old Value</td>
								<td class="labeltext" width="33.33%" style="font-weight: bold;">New
									Value</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table class="mapping-empty-template-table" id="mapping--empty-template-table"  style="display: none;" width="100%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td style="border-bottom: 1px solid #DDD;">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td class="labeltext" style="vertical-align: top; padding-top: 10px;">
						&nbsp;<img alt="user" src="<%=basePath%>/images/useravatar.png"
						height="16" width="18" />&nbsp; <font class="userclass"
						style="text-transform: capitalize; vertical-align: top;"> 
						<span class="usename"></span>
					</font> <font style="vertical-align: top;">made changes from </font> <font
						class="userclass"
						style="text-transform: capitalize; vertical-align: top;"> 
						<span class="ipAddress"></span>
					</font> <font style="vertical-align: top;"> Address -
						<span class="lastupdatetime"></span>
					</font>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</body>
</html>

