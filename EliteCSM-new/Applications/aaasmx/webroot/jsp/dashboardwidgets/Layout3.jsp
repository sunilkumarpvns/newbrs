<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<% String basePath = request.getContextPath();%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 <link href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" rel="stylesheet"> 
<link href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" rel="stylesheet"> 
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/ga.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/jquery.nanoscroller.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/main.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/nanoscroll/overthrow.min.js"></script>
<link href="<%=basePath%>/jquery/nanoscroll/main.css" rel="stylesheet"> 
<link href="<%=basePath%>/jquery/nanoscroll/nanoscroller.css" rel="stylesheet"> 
<link href="<%=basePath%>/jquery/nanoscroll/style.css" rel="stylesheet">  

<link type="text/css" href="<%=basePath%>/jquery/jscroll/jquery.jscrollpane.css" rel="stylesheet" media="all" />
<script type="text/javascript" src="<%=basePath%>/jquery/jscroll/jquery.mousewheel.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/jscroll/jquery.jscrollpane.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/jquery/jscroll/jquery.jscrollpane.min.js"></script>

<style type="text/css">

#loading-overlay { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background-color: #FAFAFA; }
#loading-message { position: absolute; width: 400px; height: 100px; line-height: 100px;text-align: center; font-size: 1.2em; left: 35%; top: 40%; margin-left: -200px; margin-top: -20px; }


#container
{
	width: 100%;
	padding-left:80px;
	background: #eeeef4;
	
}
.scroll-pane
			{
				width: 89%;
				height: 200px;
				overflow: auto;
			}
			.horizontal-only
			{
				height: 200px;
				max-height: 200px;
			}
</style>  
<script type="text/javascript" id="page-css">

$(document).ready(function(){
var slider1=$('#slider1').bxSlider({
	  slideMargin: 30,
	  auto: true,
	  autoControls: true,
	  autoControlsCombine:true,
	  pause: 5000,
	  captions:true,
	  adaptiveHeight:false,
	  onSliderLoad: function(){
	  },
	  onSlideAfter: function(){
		  $('.scrollDiv').each(function(i) {
			  $('.scrollDiv').animate({ scrollTop: "100%"}, 5000);
		  });		  
	  }
});
var slider2=$('#slider2').bxSlider({
	  slideMargin: 30,
	  auto: true,
	  autoControls: true,
	  autoControlsCombine:true,
	  pause: 5000,
	  captions:true,
	  adaptiveHeight:false,
	  onSliderLoad: function(){
	  },
	  onSlideAfter: function(){
		  $('.scrollDiv').each(function(i) {
			  $('.scrollDiv').animate({ scrollTop: "100%"}, 5000);
		  });		  
	  }
});
var slider3=$('#slider3').bxSlider({
	  slideMargin: 30,
	  auto: true,
	  autoControls: true,
	  autoControlsCombine:true,
	  pause: 5000,
	  captions:true,
	  adaptiveHeight:false,
	  onSliderLoad: function(){
	  },
	  onSlideAfter: function(){
		  $('.scrollDiv').each(function(i) {
			  $('.scrollDiv').animate({ scrollTop: "100%"}, 15000);
		  });		  
	  }
});
var slider4=$('#slider4').bxSlider({
	  slideMargin: 30,
	  auto: true,
	  autoControls: true,
	  autoControlsCombine:true,
	  pause: 5000,
	  captions:true,
	  adaptiveHeight:false,
	  onSliderLoad: function(){
	  },
	  onSlideAfter: function(){
		  $('.scrollDiv').each(function(i) {
			  $('.scrollDiv').animate({ scrollTop: "100%"}, 15000);
		  });		  
	  }
	 
});
doResize();

$("#loading-overlay").css('display','none'); 
$("#loading-message").css('display','none');
$("#pId").css({"width":$(window).width()});
$("#scroll-pane").css({"height":$(window).height()}); 
$('.scroll-pane').jScrollPane();
});
function doResize() {
   /*  $("#container").css({
        position:'relative',
        left: '3%',
        top: '1%',
        }); */
}
</script>
<style type="text/css">
</style>
</head>
<body id="container"  style="background-color: #FAFAFA;margin-right: 0;padding-right: 0;">
<div class="scroll-pane" id="scroll-pane">
<p  id="pId">
<table cellpadding="0" id="maintable" cellspacing="0" border="0">
	<tr>
		<td style="padding-left: 30px;">&nbsp;</td>
		<td width="50%" style="padding-left: 20px;">
			<div id="slider11" class="slider" style="width: 90%;height: 100%;">
				<ul id="slider1" style="vertical-align: middle;">
				  <li title="EliteAAA">
				  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
				  			<tr>
				  				<td class="ui-widget-header widgetheader">
				  					AAA-Banglore-Primary Server
				  				</td>
				  			</tr>
				  			<tr>
								<td class="totalrequest" align="center">
								
								<div id="main">
		 							 <div class="nano">
				     						 <div class="overthrow content description scrollDiv">
				  					 			 <jsp:include page="/jsp/dashboardwidgets/aaadashboard/PrimaryServerWidget.jsp"> 
				  					 			 	<jsp:param value="1" name="pageId"/> 
				  					 			 </jsp:include>
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
						  						<jsp:include page="/jsp/dashboardwidgets/aaadashboard/ReplyMsgStatsWidget.jsp">
						  							<jsp:param value="1" name="replyPageId"/>
						  						</jsp:include>
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
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/TotalRequestStatsSummary.jsp">
				  									<jsp:param value="1" name="totalPageId"/>
				  								</jsp:include>
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
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/MMSGatewayStatsSummary.jsp">
				  									<jsp:param value="1" name="mmsPageId"/>
				  								</jsp:include>
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
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/DriverDeadAliveStats.jsp">
				  									<jsp:param value="1" name="driverDeadId"/>
				  								</jsp:include>
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
		<td width="50%">
		<div class="slider" style="width: 90%;">
				<ul id="slider2" style="vertical-align: middle;">
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
				  					 				 <jsp:include page="/jsp/dashboardwidgets/aaadashboard/PrimaryServerWidget.jsp"> 
				  					 				 <jsp:param value="2" name="pageId"/> 
				  					 				</jsp:include>
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
						  						<jsp:include page="/jsp/dashboardwidgets/aaadashboard/ReplyMsgStatsWidget.jsp"><jsp:param value="2" name="replyPageId"/></jsp:include>
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
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/TotalRequestStatsSummary.jsp"><jsp:param value="2" name="totalPageId"/></jsp:include>
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
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/MMSGatewayStatsSummary.jsp"><jsp:param value="2" name="mmsPageId"/></jsp:include>
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
				  								<jsp:include page="/jsp/dashboardwidgets/aaadashboard/DriverDeadAliveStats.jsp"><jsp:param value="2" name="driverDeadId"/></jsp:include>
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
	</tr>
	<tr>
		<td style="padding-left: 30px;">&nbsp;</td>
		<td width="50%" style="padding-left: 20px;">
				<div class="slider" style="width: 90%;">
				<ul id="slider3" style="vertical-align: middle;">
				<li title="EliteDSC">
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
							  					  			<jsp:include page="/jsp/dashboardwidgets/dscdashboard/DSCPrimaryServerWidget.jsp">
							  					  			<jsp:param value="2" name="pageId"/> 
							  					  			</jsp:include>
							  							</div>
							  						</div>
							  				</div>
							  				</td>
							  			</tr>
							  		</table>
							  	
							  </li>
							  <li title="EliteDSC">
							  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					AAA-Banglore-Primary Memory Usage
							  				</td>
							  			</tr>
							  			<tr>
											<td>
												<div id="main">
		 							 			 	 <div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/DSCMemoryUsageWidget.jsp">
							  									<jsp:param value="2" name="pageId"/>
							  								</jsp:include>
							  							</div>
							  						</div>
							  					</div>
							  				</td>
							  			</tr>
							  		</table>
							  </li>
							  <li title="EliteDSC">
							 		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					Realm wise Result-Code Statistics
							  				</td>
							  			</tr>
							  			<tr>
											<td class="totalrequest">
												<div id="main">
		 							 			  	<div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/RealmWiseResultCodeStats.jsp">
							  										<jsp:param value="2" name="pageId"/>
							  								</jsp:include>
							  							</div>
							  						</div>
							  					</div>
							  				</td>
							  			</tr>
							  		</table>
							 </li>
							   <li title="EliteDSC">
							  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					Peer Statistics
							  				</td>
							  			</tr>
							  			<tr>
											<td>
												<div id="main">
		 							 			  	<div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/PeerStatsTable.jsp"></jsp:include>
							  							</div>
							  						</div>
							  					</div>
							  				</td>
							  			</tr>
							  		</table>
							  </li>
							  <li title="EliteDSC">
							  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					Realm Statistics
							  				</td>
							  			</tr>
							  			<tr>
											<td>
												<div id="main">
		 							 				  <div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/RealmStatistics.jsp"></jsp:include>
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
		<td width="50%">
			<div class="slider" style="width: 90%;">
				<ul id="slider4" style="vertical-align: middle;">
				 <li title="EliteDSC">
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
							  					  			<jsp:include page="/jsp/dashboardwidgets/dscdashboard/DSCPrimaryServerWidget.jsp">
							  					  			<jsp:param value="1" name="pageId"/> 
							  					  			</jsp:include>
							  							</div>
							  						</div>
							  				</div>
							  				</td>
							  			</tr>
							  		</table>
							  	
							  </li>
							  <li title="EliteDSC">
							  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					AAA-Banglore-Primary Memory Usage
							  				</td>
							  			</tr>
							  			<tr>
											<td>
												<div id="main">
		 							 			 	 <div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/DSCMemoryUsageWidget.jsp">
							  									<jsp:param value="1" name="pageId"/>
							  								</jsp:include>
							  							</div>
							  						</div>
							  					</div>
							  				</td>
							  			</tr>
							  		</table>
							  </li>
							  <li title="EliteDSC">
							 		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					Realm wise Result-Code Statistics
							  				</td>
							  			</tr>
							  			<tr>
											<td class="totalrequest">
												<div id="main">
		 							 			  	<div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/RealmWiseResultCodeStats.jsp">
							  										<jsp:param value="1" name="pageId"/>
							  								</jsp:include>
							  							</div>
							  						</div>
							  					</div>
							  				</td>
							  			</tr>
							  		</table>
							 </li>
							   <li title="EliteDSC">
							  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					Peer Statistics
							  				</td>
							  			</tr>
							  			<tr>
											<td>
												<div id="main">
		 							 			  	<div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/PeerStatsTable.jsp"></jsp:include>
							  							</div>
							  						</div>
							  					</div>
							  				</td>
							  			</tr>
							  		</table>
							  </li>
							  <li title="EliteDSC">
							  		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:-10px;">
							  			<tr>
							  				<td class="ui-widget-header widgetheader">
							  					Realm Statistics
							  				</td>
							  			</tr>
							  			<tr>
											<td>
												<div id="main">
		 							 				  <div class="nano">
				     									 <div class="overthrow content description scrollDiv">
							  								<jsp:include page="/jsp/dashboardwidgets/dscdashboard/RealmStatistics.jsp"></jsp:include>
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
	</tr>
</table>
</p>
<!-- </div> -->
</div>
<!--  <div id="scroller" class="scroll-pane horizontal-only"></div>
 --><div id="loading-overlay"></div>
<div id="loading-message"><img alt="Loading, please wait" src="<%=basePath%>/images/loading14.gif" /></div>
</body>
</html>