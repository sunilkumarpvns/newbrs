<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%
		String pageId = request.getParameter("pageId");
	%>
	<script type="text/javascript">
	var dscWidgetId;
	
	//used for all configurations
	var dscServerConfigParamList = [];
	
	//get Widget Id
	dscWidgetId=$("#dscPrimaryServerDivId").parent().parent().find('.widgetcontent').parent().attr("id");
	var isElementFound=false;
	//get widget configuration details
	$.ajax({url:'<%=request.getContextPath()%>/FetchWidgetConfiguration',
	        type:'GET',
	        data:{widgetId:dscWidgetId},
	        async:false,
	        success: function(transport){
		       	 $.each(transport, function(key, widgetConfigData) {
		       		 if(widgetConfigData != null){
		       			dscServerConfigParamList.push({'parameterId': widgetConfigData.parameterId,
		            		   'parameterKey' : widgetConfigData.parameterKey,
		            		   'parameterValue':widgetConfigData.parameterValue,
		            		   'widgetId':widgetConfigData.widgetId});
		       				isElementFound=true;
		       		 }else{
		       			$("#"+dscWidgetId).find(".widgetcontent").load("jsp/dashboardwidgets/dscwidgets/EditDSCPrimaryServerWidget.jsp");
		       		 }
		       		
		       	 });
		       	 if(isElementFound == false){
		    			$("#"+dscWidgetId).find(".widgetcontent").load("jsp/dashboardwidgets/dscwidgets/EditDSCPrimaryServerWidget.jsp");
		       	 }
	      }
	});
	
	function hideDscServerProgressBar(){
		$('#dscPrimaryServerDivId').hide();
	}
	
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/dscwidgets/EditDSCPrimaryServerWidget.jsp" id="editJsp">
<%--ProgressBar Div : Start  --%>
 
 <div id="dscPrimaryServerDivId" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>
</body>
</html>