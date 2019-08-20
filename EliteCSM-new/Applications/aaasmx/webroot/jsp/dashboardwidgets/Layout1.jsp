<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<% String basePath = request.getContextPath();%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" rel="stylesheet">
 
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/jquery.nanoscroller.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/main.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/overthrow.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/enscroll-0.5.1.min.js"></script>



<link href="<%=basePath%>/jquery/nanoscroll/main.css" rel="stylesheet"> 
<link href="<%=basePath%>/jquery/nanoscroll/nanoscroller.css" rel="stylesheet"> 
<link href="<%=basePath%>/jquery/nanoscroll/style.css" rel="stylesheet"> 
<style type="text/css">

	#mainContainer
{
	width: 100%;
}
	.scroll-pane-home
			{
				width: 100%;
				height: auto;
				overflow: auto;
			}

#scrollbox2 {
    overflow: auto;
    width: 400px;
    height: 360px;
    padding: 0 5px;
    border: 1px solid #b7b7b7;
}

#scrollbox2 p {
    width: 600px;
}

.vertical-track2 {
    width: 17px;
    background: #e4e6e3;
    border: 1px solid #dfdfdd;
}

.vertical-handle2 {
    width: 17px;
}

.vertical-handle2 .top {
    width: 17px;
    height: 17px;
    background: url(http://enscrollplugin.com/images/green-vert-scrollbar.png) no-repeat;
}

.vertical-handle2 .bottom {
    width: 17px;
    height: 100%;
    position: absolute;
    bottom: 0;
    clip: rect(17px 17px 9999px 0);
    background: url(http://enscrollplugin.com/images/green-vert-scrollbar.png) no-repeat 0 bottom;
}

.horizontal-track2 {
    width: 100%;
    height: 17px;
    background: #e4e6e3;
    border: 1px solid #dfdfdd;
}

.horizontal-handle2 {
    height: 17px;
}

.horizontal-handle2 .left {
    width: 17px;
    height: 17px;
    background: url(http://enscrollplugin.com/images/green-horiz-scrollbar.png) no-repeat;
}

.horizontal-handle2 .right {
    width: 100%;
    height: 17px;
    position: absolute;
    right: 0;
    top: 0;
    clip: rect(0 9999px 17px 17px);
    background: url(http://enscrollplugin.com/images/green-horiz-scrollbar.png) no-repeat right 0;
}

.corner2 {
    width: 17px;
    height: 17px;
    background: #e4e6e3;
    border: 1px solid #dfdfdd; 
}



</style>

<script type="text/javascript">
$(document).ready(function(){
$('#slider1').bxSlider({
	  slideMargin: 30,
	  auto: true,
	  autoControls: true,
	  pause: 5000,
	  captions: true,
	  autoControlsCombine:true,
	  adaptiveHeight:false,
	  onSlideAfter: function(){
		  $('.scrollDiv').each(function(i) {
			  $('.scrollDiv').animate({ scrollTop: "100%"}, 5000);
		  });		  
	  }
});

$('#scrollbox2').enscroll({
    horizontalScrolling: true,
    verticalTrackClass: 'vertical-track2',
    verticalHandleClass: 'vertical-handle2',
    horizontalTrackClass: 'horizontal-track2',
    horizontalHandleClass: 'horizontal-handle2',
    cornerClass: 'corner2'
});
$("#loading-overlay").css('display','none'); 
$("#loading-message").css('display','none');
});

</script>
<style type="text/css">
#loading-overlay { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background-color: #FAFAFA; }
#loading-message { position: absolute; width: 400px; height: 100px; line-height: 100px;text-align: center; font-size: 1.2em; left: 35%; top: 40%; margin-left: -200px; margin-top: -20px; }
</style>
</head>
<body style="background-color: #FAFAFA">
<div id="container">
<table cellpadding="0" cellspacing="0" border="0" align="center" width="100%">
	<tr>
		<td width="10%">&nbsp;</td>
		<td width="90%">
			<div class="slider" style="width: 90%;">
				<ul id="slider1" style="vertical-align: middle;">
				  <li title="EliteAAA">
				  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					AAA-Banglore-Primary Server
				  				</td>
				  			</tr>
				  			<tr>
								<td class="totalrequest">
									<div id="main">
		 							   <div class="nano">
			     							 <div class="overthrow content description scrollDiv">
				  								  <jsp:include page="/jsp/dashboardwidgets/aaadashboard/PrimaryServerWidget.jsp"></jsp:include>
				  							</div>
				  						</div>
				  					</div>
				  				</td>
				  			</tr>
				  		</table>
				  	
				  </li>
				  <li title="EliteAAA">
				  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					Reply Message Statistics
				  				</td>
				  			</tr>
				  			<tr>
								<td>
									<div id="main">
		 							   <div class="nano">
			     							 <div class="overthrow content description scrollDiv">
					  							<jsp:include page="/jsp/dashboardwidgets/aaadashboard/ReplyMsgStatsWidget.jsp"></jsp:include>
					  						</div>
				  						</div>
				  					</div>
				  				</td>
				  			</tr>
				  		</table>
				  </li>
				  <li title="EliteAAA">
				 		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					Total Request Statistics Summary
				  				</td>
				  			</tr>
				  			<tr>
								<td class="totalrequest">
									<div id="main">
		 							   <div class="nano">
		     							 <div class="overthrow content description scrollDiv">
						  						<jsp:include page="/jsp/dashboardwidgets/aaadashboard/TotalRequestStatsSummary.jsp"></jsp:include>
						  				</div>
						  			</div>
						  		</div>
				  				</td>
				  			</tr>
				  		</table>
				 </li>
				  <li title="EliteAAA">
				  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					MMS-Gateway ESI Summary
				  				</td>
				  			</tr>
				  			<tr>
								<td>
									<div id="main">
		 							   <div class="nano">
			     							 <div class="overthrow content description scrollDiv">
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/MMSGatewayStatsSummary.jsp"></jsp:include>
				  							</div>
				  						</div>
				  					</div>
				  				</td>
				  			</tr>
				  		</table>
				  </li>
				  <li title="EliteAAA">
				  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					Driver Dead Alive Statistics
				  				</td>
				  			</tr>
				  			<tr>
								<td>
									<div id="main">
		 							   <div class="nano">
			     							 <div class="overthrow content description scrollDiv">
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/DriverDeadAliveStats.jsp"></jsp:include>
				  							</div>
				  						</div>
				  					</div>
				  				</td>
				  			</tr>
				  		</table>
				  </li>
				  <li title="EliteAAA">
				  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					Auth Service Statistics
				  				</td>
				  			</tr>
				  			<tr>
								<td>
									<div id="main">
		 							   <div class="nano">
			     							 <div class="overthrow content description scrollDiv">
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/AuthServiceStatTable.jsp"></jsp:include>
				  							</div>
				  						</div>
				  					</div>
				  				</td>
				  			</tr>
				  		</table>
				  	</li>
				  <li title="EliteAAA">
				  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					Alert Statistics
				  				</td>
				  			</tr>
				  			<tr>
								<td>
									<div id="main">
		 							   <div class="nano">
			     							 <div class="overthrow content description scrollDiv">
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/AlertStatisticsTable.jsp"></jsp:include>
				  							</div>
				  						</div>
				  					</div>
				  				</td>
				  			</tr>
				  		</table>
				  </li>
				</ul>
			</div>
		</td>
		<td width="5%">&nbsp;</td>
	</tr>
</table>
</div>
<div id="loading-overlay"></div>
<div id="loading-message"><img alt="Loading, please wait" src="<%=basePath%>/images/loading14.gif" /></div>
</body>
</html>