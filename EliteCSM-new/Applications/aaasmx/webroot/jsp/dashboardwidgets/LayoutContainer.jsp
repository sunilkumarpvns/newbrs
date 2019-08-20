<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<% String basePath = request.getContextPath();%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

   <script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery-1.6.4.min.js"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/slideshow/jquery.bxslider.css" />
<link REL="SHORTCUT ICON" HREF="<%=basePath%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
<script type="text/javascript" src="<%=basePath%>/jquery/slideshow/jquery.bxslider.js"></script> 
<%-- <script type="text/javascript" src="<%=basePath%>/js/highstock.js"></script> --%>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/dashboardui.css" /> 
   <link type="text/css" href="<%=basePath%>/jquery/development/redmond/jquery-ui.css" rel="stylesheet"/>
   
<script type="text/javascript">
$(document).ready(function(){
	
	 $('ul.custom-menu li').click(function(e) 
	{ 
			     var layoutId=$(this).attr('id');
			     if(layoutId=="layout1"){
			    	 $('.layoutContainerDiv').load("<%=basePath%>/jsp/dashboardwidgets/Layout1.jsp");
			     }else if(layoutId=="layout2"){
			    	  $('.layoutContainerDiv').load("<%=basePath%>/jsp/dashboardwidgets/Layout2.jsp");
			     }else{
			    	  $('.layoutContainerDiv').load("<%=basePath%>/jsp/dashboardwidgets/Layout3.jsp");
			     }
	});
$("#imgLayout").click(function(){
	 $("#layoutHidden").css("display", "block");
});

<%-- $("#layout2").click(function(){
	alert('layout2');
	  $('.layoutContainerDiv').load("<%=basePath%>/jsp/dashboardwidgets/Layout2.jsp");
});

$("#layout3").click(function(){
	alert('layout3');
	  $('.layoutContainerDiv').load("<%=basePath%>/jsp/dashboardwidgets/Layout3.jsp");
});
$("#layout1").click(function(){
	alert('layout1');
	  $('.layoutContainerDiv').load("<%=basePath%>/jsp/dashboardwidgets/Layout1.jsp");
}); --%>
$("#headerTd").mouseleave(function() {
	$("#layoutHidden").css("display", "none");
}); 


$("#exit").click(function(){
    window.close();
});
});

//Trigger action when the contexmenu is about to be shown
$(document).bind("contextmenu", function (event) {
    // Avoid the real one
    event.preventDefault();
    // Show contextmenu
    $(".custom-menu").toggle(100).
    // In the right position (the mouse)
    css({
        top: event.pageY + "px",
        left: event.pageX + "px"
    });
});

// If the document is clicked somewhere
$(document).bind("mousedown", function () {
    $(".custom-menu").hide(100);
});

function exitFs(){
	if(document.cancelFullScreen) {
	    document.cancelFullScreen();
	  } else if(document.mozCancelFullScreen) {
	    document.mozCancelFullScreen();
	  } else if(document.webkitCancelFullScreen) {
	    document.webkitCancelFullScreen();
	  }
}
</script>
<style type="text/css">
.custom-menu {
    display: none;
    width:150px;
    z-index:1000;
    overflow: hidden;
     border: 1px solid #DDD;
  background: white;
  background-color: white;
    
  -webkit-box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
  -moz-box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
  -ms-box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
  -o-box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
    
  font-family: Verdana, Arial, Helvetica, sans-serif;
  font-size: 11px;
  border: 1px solid #DDD;
  margin:-6px;
  margin-right:2px;
  padding:0;
  right:0;
  position: absolute;
}

.custom-menu li {
}

.custom-menu li:hover {
    background-color: #015198;
    cursor: pointer;
}
</style>
<title>SlideShow</title>
</head>
<body style="background-color: #FAFAFA">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<%-- <tr>
		<td  align="right" style="cursor: pointer;padding-right: 10px;" id="headerTd">
			<img class="menutrigger" src="<%=basePath%>/images/layoutTools.png" id="imgLayout" style="padding-top: 5px;">
			 <span class="hiddenmenu" style="text-align: right;">
					<!-- <ul style="z-index: 1;position: absolute;display: none;width: 120px;" class="tools-controls" id="layoutHidden">
								<li class="widgetClose" id="layout1">
									<span  style="color: black;">Layout 1</span>
								</li>
								<li class="widgetClose" id="layout2" >
									<span style="color: black;">Layout 2</span>
								</li>
								<li class="widgetEdit" id="layout3">
									<span  style="color: black;">Layout 3</span>
								</li>
								<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
								<li class="widgetDelete" id="exit" onclick="exitFs();" style="padding-top: 5px;">
									<span style="color: black;">Exit</span>
								</li>
					</ul> -->
			</span>
		</td> 
	</tr> --%>
	<tr>
		<td width="100%" rowspan="2" align="center">
			<div id="layoutContainerDiv" class="layoutContainerDiv">
				<jsp:include page="/jsp/dashboardwidgets/Layout1.jsp"></jsp:include>
			</div>
		</td>
	</tr>
</table>
<ul class='custom-menu'>
  <li  id="layout1">Layout 1</li>
  <li  id="layout2">Layout 2</li>
  <li  id="layout3">Layout 3</li>
  <li style="min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
  <li  id="exit">Exit</li>
</ul>
</body>
</html>