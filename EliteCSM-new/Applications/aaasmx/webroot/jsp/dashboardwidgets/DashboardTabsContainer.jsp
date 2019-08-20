<% String basePath = request.getContextPath();%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>Dashboard</title>
  	<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/dashboardui.css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" />
   
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/aui-all.css" /> 
   
	<script type="text/javascript" src="<%=basePath%>/js/auijs/aui-flatpack-docs.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/auijs/aui-all.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/auijs/aui-flatpack-extras.js"></script> 
	<script type="text/javascript" src="<%=basePath%>/js/auijs/highlight.pack.js"></script> 
	<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/exporting.js"></script> 
	<script type="text/javascript">
		var currentTabId = 'dashboard';
	</script>
 
  <style type="text/css">

.custom_scrollbar {
	height: 620px;
	width: auto;
	overflow: scroll;
}

.custom_scrollbar::-webkit-scrollbar {
	width: 6px;
	height: 6px;
}
.custom_scrollbar::-webkit-scrollbar-track {
	background-color: rgba(113,112,107,0.1);
	-webkit-border-radius: 5px;
}
.custom_scrollbar::-webkit-scrollbar-thumb:vertical {
	background-color: rgba(0,0,0,.2);
	-webkit-border-radius: 6px;
}
.custom_scrollbar::-webkit-scrollbar-thumb:vertical:hover,
.custom_scrollbar::-webkit-scrollbar-thumb:horizontal:hover {
	background: rgba(0,0,0,.5);
}
.custom_scrollbar::-webkit-scrollbar-thumb:horizontal {
	background-color: rgba(0,0,0,.2);
	-webkit-border-radius: 6px;
}
  	.innerHover:hover{background-color: #D8D8D8; }
	
	#outer {
            width: 100%;
       		 min-height: 600px; 
    }
    
    .ui-sortable-placeholder { padding:20px;visibility: visible !important; height: 250px !important;background-color: #F5F5F5;font-size: 20px;}
	.ui-sortable-placeholder:AFTER{color:black;height: 400px;width: 400px;border-style: dashed;border: 2px;border-color: red;}
	.ui-sortable-placeholder * { visibility: hidden; }
	.ui-state-highlight {text-align:center;background: #F5F5F5 !important;border: 2px dashed black !important;border-color: #D8D8D8 !important;font-size: large !important;vertical-align: middle !important;width: 550px !important;height: 300px !important;color: black}
  	.ui-state-highlight:AFTER{color: gray;text-align: center !important;vertical-align: top;content: "Drag your Widget here ";}
	li {display:inline;}
	
  </style>
  <script type="text/javascript">
  </script>
  </head>

 <body style="background-color: #FAFAFA;">
 <!--  <div class="custom_scrollbar"> -->
  <div >
	<div class="aui-tabs horizontal-tabs" id="tabs-example1">
        <ul class="tabs-menu sDashboardWidgetHeaderBarMain ui-widget-header" id="active" >
            <li class="menu-item active-tab" id="firstTab">
                <a href="#tabs-example-first">
                	<span style="padding-left: 5px;text-align:center;line-height: 1em;vertical-align: middle;">EliteAAA</span>&nbsp;
                	<span>
                		<img id="imgServerSeperator" class="selerator" src="images/dots.gif"   alt="menu"  style="cursor:pointer;text-align:bottom;vertical-align: middle;line-height: 1em;" align="bottom"/> 	
                	</span>
                	<span id="imgAAAMenu" style="text-align:top;vertical-align: top;padding-left: 4px;padding-right: 4px;padding-bottom: 1px;padding-top: -2px;" class="innerHover">
						<img  class="menuImage menutrigger" src="images/menuicon.png" alt="menu" style="cursor:pointer;text-align:top;vertical-align:top;padding-top:8px;"/> 				
					</span>
                </a>
                	<span class="hiddenmenu" style="text-align: left;">
							<ul style="z-index: 1;position: absolute;left:5px;margin-top: -2px;display: none;" class="child-controls" id="mainHidden">
								<li class="widgetOpen" id="openAAA">
									<span class="ui-icon ui-icon-extlink"></span>
									<a  class="minimization" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Open in New Window</a>
								</li>
								<li class="widgetRefresh" style="padding-bottom: 5px;">
									<span class="ui-icon ui-icon-arrowrefresh-1-w"></span>
									<a class="no_target" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Refresh</a>
								</li>
								<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
								<li class="widgetRefresh" id="slideshow">
									<span class="ui-icon ui-icon-tag"></span>
									<a class="no_target" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Slideshow</a>
								</li>
								<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
								<li class="widgetDelete" style="padding-top: 5px;">
									<span class="ui-icon ui-icon-close"></span>
									<a class="delete" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Close</a>
								</li>
							</ul>
						</span>
            </li>
            <li class="menu-item" id="secondTab">
                <a href="#tabs-example-second" >
              	 	<span style="padding-left: 5px;text-align:center;line-height: 1em;vertical-align: middle;">EliteDSC</span>&nbsp;
                	<span>
                		<img id="imgServer" class="seperator" src="images/dots.gif"   alt="menu"  style="cursor:pointer;text-align:bottom;vertical-align: middle;line-height: 1em;" align="bottom"/> 	
                	</span>
                	<span id="imgCSMMenu" style="text-align:top;vertical-align: top;padding-left: 4px;padding-right: 4px;padding-bottom: 1px;padding-top: -2px;" class="innerHover">
						<img  class="menuImage menutrigger" src="images/menu_indicator.png"   alt="menu"  style="cursor:pointer;text-align:top;vertical-align:top;padding-top:8px;"/> 				
					</span>
                </a>
                <span class="hiddenmenu" style="text-align: left;">
							<ul style="z-index: 1;position: absolute;left:5px;display: none;" class="child-controls" id="csmHidden">
								<li class="widgetClose">
									<span class="ui-icon ui-icon-extlink"></span>
									<a  class="minimization" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Open in New Window</a>
								</li>
								<li class="widgetRefresh" style="padding-bottom: 5px;">
									<span class="ui-icon ui-icon-arrowrefresh-1-w"></span>
									<a class="no_target" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Refresh</a>
								</li>
								<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
								<li class="widgetRefresh" >
									<span class="ui-icon ui-icon-tag"></span>
									<a class="no_target" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Slideshow</a>
								</li>
								<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
								<li class="widgetDelete" style="padding-top: 5px;">
									<span class="ui-icon ui-icon-close"></span>
									<a class="delete" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Close</a>
								</li>
							</ul>
						</span>
            </li>
            <li class="menu-item" id="thirdTab">
                <a href="#tabs-example-third">
                	<span style="padding-left: 5px;text-align:center;line-height: 1em;vertical-align: middle;">Instance</span>&nbsp;
                	<span>
                		<img id="imgServer" class="seperator" src="images/dots.gif"   alt="menu"  style="cursor:pointer;text-align:bottom;vertical-align: middle;line-height: 1em;" align="bottom"/> 	
                	</span>
                	<span id="imgInstanceMenu" style="text-align:top;vertical-align: top;padding-left: 4px;padding-right: 4px;padding-bottom: 1px;padding-top: -2px;" class="innerHover">
						<img  class="menuImage" src="images/menu_indicator.png"   alt="menu"  style="cursor:pointer;text-align:top;vertical-align:top;padding-top:8px;"/> 				
					</span>
                </a>
                 <span class="hiddenmenu" style="text-align: left;">
							<ul style="z-index: 1;position: absolute;left:5px;display: none;" class="child-controls" id="instanceHidden">
								<li class="widgetClose">
									<span class="ui-icon ui-icon-extlink"></span>
									<a  class="minimization" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Open in New Window</a>
								</li>
								<li class="widgetRefresh" style="padding-bottom: 5px;">
									<span class="ui-icon ui-icon-arrowrefresh-1-w"></span>
									<a class="no_target" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Refresh</a>
								</li>
								<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
								<li class="widgetRefresh" >
									<span class="ui-icon ui-icon-tag"></span>
									<a class="no_target" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Slideshow</a>
								</li>
								<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
								<li class="widgetDelete" style="padding-top: 5px;">
									<span class="ui-icon ui-icon-close"></span>
									<a class="delete" href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Close</a>
								</li>
							</ul>
						</span>
            </li>
			<div align="right"  style="padding-left:1000px;">
					<table cellpadding="0" cellspacing="0" style="cursor: pointer;padding-top: 2px;" height="100%" id="toolsConfig">
						<tr>
							<td style="padding-right: 5px;"></td>
							<td style="font-weight: bold;" id="toolsMenuLabel">Tools</td>
							<td style="padding-left: 5px;padding-bottom: 5px;"> 
								<img id="menuTools" class="toolsImage menutrigger" src="images/menu_indicator.png" alt="menu" style="cursor: pointer;" />&nbsp;&nbsp;
								<span class="hiddenmenu" style="text-align: left;">
								<ul style="z-index: 1; position: absolute; display: none; margin-top: 5px; margin-right: -12px; width: 150px;" class="child-controls" id="rightHidden">
									<li class="editlayout headerlink" style="padding:2px">
										<span class="ui-icon ui-icon-minus"></span> 
											<a  href="#">
   												 Edit layout
											</a>
									</li>
									<li class="openaddwidgetdialog">
									  <span class="ui-icon ui-icon-plus"></span>
									  <a href="#" style="background-color: transparent;text-decoration: none;border-bottom:none;font-weight: normal;">Add Widget</a>
									</li>
									<li style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px;"></li>
									<li class="widgetOpen" style="padding:2px"><span
										class="ui-icon ui-icon-plus"></span> <a href="<%=basePath%>/jsp/dashboardwidgets/CopyDashboard.jsp"
										class="maximization" href="#"
										style="background-color: transparent; text-decoration: none; border-bottom: none; font-weight: normal;">Create Dashboard</a>
									</li>
									<li style="padding:2px">
										<span class="ui-icon ui-icon-extlink"></span> 
										<a class="delete" href="<%=basePath%>/jsp/dashboardwidgets/ManageDashboard.jsp" style="background-color: transparent; text-decoration: none; border-bottom: none; font-weight: normal;">Manage Dashboard</a>
									</li> 
								</ul>
						</span>
							</td>
						</tr>
					</table>
				</div>
		</ul>
        <div class="tabs-pane active-pane" id="tabs-example-first">
        	<jsp:include page="/jsp/dashboardwidgets/AAADashboardHome.jsp"></jsp:include>
        </div>
        <div class="tabs-pane" id="tabs-example-second">
        </div>
        <div class="tabs-pane" id="tabs-example-third">
        </div>
    </div>
    </div>
  </body>
  
 
  <script type="text/javascript">
  
  var aaaFlag=0,dscFlag=0,instanceFlag=0;	
  var dscddashboard = null;
  var instanceDashboard = null;
   $(document).ready(function() { 
  		 $("#secondTab").click(function(){
  			currentTabId="dscDashboard";
  			 if(aaaFlag==0){
 					 $("#tabs-example-second").load("<%=basePath%>/jsp/dashboardwidgets/DSCDashboardHome.jsp");
 					 aaaFlag++;
 					 //$("#tabs-example-first").hide();
 				}
 	 	 }); 
  		 $("#thirdTab").click(function(){
			currentTabId="instanceDashboard";
  			if(instanceFlag==0){
 				 $("#tabs-example-third").load("<%=basePath%>/jsp/dashboardwidgets/InstanceDashboardHome.jsp");
 				 instanceFlag++;
 			}
  		 });
  		 
  		 $("#firstTab").click(function(){
   			currentTabId="dashboard";
   		 });
  			$('#mainFrame').attr('scrolling', 'no');
  		 	$("#mainHidden").css("display", "none");
  		 	$("#csmHidden").css("display","none");
  		 	$("#instanceHidden").css("display","none");
  			$("#sessionHidden").css("display", "none");
  			$("#mainRightCSS").css("color", "white");
  			$("#toolsMenuLabel").css("color", "white");
  			
 		     $("li a").click(function()
 		     {
 		    	 $(".menuImage").attr('src','images/menu_indicator.png');
 		    	 $(this).find(".menuImage").attr('src','images/menuicon.png');
 		     });
 		     
 		     $("#imgAAAMenu").click(function(){
 		    	 $("#mainHidden").css("display", "block");
 		    	 $("#csmHidden").css("display", "none");
 		     });
 		     
 		     $("#imgCSMMenu").click(function(){
 		    	 $("#csmHidden").css("display", "block");
 		     });
 		     
 		     $("#imgInstanceMenu").click(function(){
 		    	 $("#instanceHidden").css("display", "block");
 		     });
 		     
 		     $("#imgSessionMenu").click(function(){
 		    	 $("#sessionHidden").css("display", "block");
 		     });
 		     
 		     $("#imgToolsConfig").click(function(){
 		    	 $("#toolsHidden").css("display", "block");
 		    	 $(".toolsConf").css("color","red");
 		    	 $(".toolsConf").addClass(".aui-tabs.horizontal-tabs > .tabs-menu > .menu-item.active-tab a");
 		     });
 		     
 		     $("#imgToolsMenu").click(function(){
 		    	 $("#mainRightCSS").css("color", "black");
 		    	 $("#mainRightCSS").css("background-color","#FAFAFA");
 		    	 $("#rightHidden").css("display", "block");
 		    	 $("#mainRightCSS").css("border-bottom", "1px solid #FAFAFA");
 		    	 
 		     });
 		     
 		 	 $(".menu-item").hover(function(){
 		 		 $("#mainHidden").css("display", "none");
 		 		 $("#csmHidden").css("display", "none");
 		 		 $("#instanceHidden").css("display", "none");
 		 	   	 $("#sessionHidden").css("display", "none");
 		 	     $("#rightHidden").css("display", "none");
 			 });
 		 	 
 		 	 	 
 		 	$("#toolsConfig").mouseenter(function() {
 		 		 $("#toolsConfig").css("background-color","rgb(29, 98, 167)");
 			}).mouseleave(function() {
 				 $("#toolsConfig").css("background-color","transparent");
 				 $("#toolsMenuLabel").css("color", "white");
 				 $("#menuTools").attr('src','images/menu_indicator.png');
 				 $("#rightHidden").css("display", "none");
 			});
 		 	 
 		 	$("#toolsConfig").click(function(){
 		 		 $("#toolsConfig").css("background-color","#FAFAFA");
 		 		 $("#toolsConfig").css("border-radius","3px 0px 0px");
 		 		 $("#toolsMenuLabel").css("color", "black");
 		 		 $("#menuTools").attr('src','images/menuicon.png');
 		 		 $("#toolsConfig").css("border-radius","1px solid rgb(250, 250, 250)");
 		 		 $("#rightHidden").css("display", "block");
 		     }); 
 		 	
 		 	
 		 	 $('.rightMenu').mouseleave(function(){
 		 		$("#mainRightCSS").css("background","none");
 		 		$("#mainRightCSS").css("border-bottom", "none");
 		 		$("#mainRightCSS").css("color", "white");
 		 		$("#menuTools").attr('src','images/menuicon.png');
 		 		
 			 });
 		 	
 		 	$( ".toolsConf").hover(
 		 			function() {
 		 				$(this).css("background","rgb(29, 98, 167)");
 		 			}, function() {
 		 				$(this).css("background","transparent");
 		 			}
 		 	);
 		 	
 		 	 $("#openAAA").click(function(){
 		 		  var w = window.open();
 		 		  var html = $("#tabs-example-first").html();

 		 		  $(w.document.body).html(html);
 		 	 });
 		 	 
 		 	 $("#slideshow").click(function(){
 		 		  var popup =  window.open("<%=basePath%>/jsp/dashboardwidgets/LayoutContainer.jsp"); 
 		 	       popup.moveTo(0,0);
 		 	 });
 		 	$("#mainFrame").css('overflow','hidden');
 		 }); 
  
  function visibleHover(spanId){
		$('#csmMenu').css('display','block');
	}

  </script>
</html>
