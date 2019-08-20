<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%String basePath=request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/highcharts.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<%--Code for get Widget Id--%>
	<%String replyWidgetId = request.getParameter("widgetId");%>
	
	<script type="text/javascript">
	
	$(document).ready(function(){
		
		<%--DO NOT REMOVE : Get widget Configuration here --%>
		var replyMsgId=<%=replyWidgetId%>;
		var confObj = getWidgetConfiguration(replyMsgId);  <%-- Use ConfObj to retrive configuration --%>
	});
	
	
	<%--Use Below method to Stop Progress bar--%>
	function hideProgressBar(){
		$('#replyMsgDivId'+<%=replyWidgetId%>).hide();
	}
	</script>
</head>
<body>
<input type="hidden" name="editJsp" value="jsp/dashboardwidgets/aaawidgets/EditReplyMsgWidget.jsp?widgetId=<%=replyWidgetId%>" id="editJsp"/>
 
 <%--ProgressBar Div : Start  --%>
 <div id="replyMsgDivId<%=replyWidgetId%>" style="height: 200px;width: 100%;display: table;background-position: center;vertical-align: middle;text-align:center; " align="center">
 	<div style="display: table-cell;vertical-align: middle;">
 		<img src="<%=basePath%>/images/loading1.gif" align="center" style="vertical-align: middle;"/>
 	</div>
 </div>
 <%--ProgressBar Div : End  --%>
 
 </body>
</html>