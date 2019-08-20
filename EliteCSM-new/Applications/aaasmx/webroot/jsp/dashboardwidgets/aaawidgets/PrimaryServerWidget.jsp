<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<%String primaryServerWidgetId = request.getParameter("widgetId");%>
	
	<script type="text/javascript">
	
	$(document).ready(function(){
		
		<%--DO NOT REMOVE : Get widget Configuration here --%>
		var primaryServerWidgetId=<%=primaryServerWidgetId%>;
		var confObj = getWidgetConfiguration(primaryServerWidgetId);  <%-- Use ConfObj to retrive configuration --%>
		
	});
	
	<%--Use Below method to Stop Progress bar--%>
	function hideProgressBar(){
		$('#progressDivId'+<%=primaryServerWidgetId%>).hide();
	}
	
	<%-- END : Primary Server TPS Info. --%>
	
	</script>
</head>
<body>
 <input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditPrimaryServerWidget.jsp?widgetId=<%=primaryServerWidgetId%>" id="editJsp"/>
 
 <%--ProgressBar Div : Start  --%>
 
 <div id="progressDivId<%=primaryServerWidgetId%>" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>

 <%--ProgressBar Div : End  --%>

</body>
</html>