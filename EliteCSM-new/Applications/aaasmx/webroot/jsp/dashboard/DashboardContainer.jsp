<%@page import="com.elitecore.elitesm.web.dashboard.form.DashboardForm"%>
<%@page import="com.elitecore.elitesm.datamanager.dashboard.data.DashboardData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.util.*"%>
<% String basePath = request.getContextPath();%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>Dashboard</title>
  	<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/dashboardui.css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom1.css" /> 
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/dashboardmenu.css" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard/mbcsmbmcp.css" type="text/css" />
	<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery-1.6.4.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery-ui-1.8.16.custom.min1.js"></script>
 
	<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/themeroller.js"></script>
    <script src="${pageContext.request.contextPath}/js/dashboard/table-schema.js?time"=<%=System.currentTimeMillis()%>></script>
    <script src="${pageContext.request.contextPath}/js/dashboard/table-widget.js?time="><%=System.currentTimeMillis()%></script>
    <script src="${pageContext.request.contextPath}/js/dashboard/dashboard-socket.js?time="><%=System.currentTimeMillis()%></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.class.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/widget-configuration.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/highstock.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/exporting.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.timer.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/commonTimer.js"></script>
	<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery.dashboard.min1.js"></script>
 
   <%--Generating PDF Js --%>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.addimage.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.cell.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.from_html.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.ie_below_9_shim.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.javascript.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.sillysvgrenderer.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.split_text_to_size.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.plugin.standard_fonts_metrics.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/jspdf.PLUGINTEMPLATE.js"></script>
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/Blob.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/canvas-toBlob.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/FileSaver.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/pdfgenerator/FileSaver.min.js"></script>
 
	 <%  DashboardForm dashboardForm = (DashboardForm) request.getAttribute("dashboardForm");
		 List<DashboardData> dashboardList = dashboardForm.getDashboardList();
		 DashboardData firstTabId=null;
	 %> 
	 
	<script>
	//	var currentTabId = 'dashboard';
	</script>
 
  <style type="text/css">
    .ui-sortable-placeholder { padding:20px;visibility: visible !important; height: 250px !important;background-color: #F5F5F5;font-size: 20px;}
	.ui-sortable-placeholder:AFTER{color:black;height: 400px;width: 400px;border-style: dashed;border: 2px;border-color: red;}
	.ui-sortable-placeholder * { visibility: hidden;}
	.ui-state-highlight {text-align:center;background: #F5F5F5 !important;border: 2px dashed black !important;border-color: #D8D8D8 !important;font-size: large !important;vertical-align: middle !important;width: 550px !important;height: 300px !important;color: black}
  	.ui-state-highlight:AFTER{color: gray;text-align: center !important;vertical-align: top;content: "Drag your Widget here ";}
	.activemenu{
		color:black;
	}
	.toolsMenu:hover{
		background: url("jquery-ui-images/ui-bg_inset-hard_100_f5f8f9_1x100.png") repeat-x scroll 50% 50% rgb(245, 248, 249);
	}
	.rightLabel:hover{
		color: blue;
	}
	
	.dashboardMenu1 ul li:hover{
		cursor:pointer;
		background-color: rgb(60, 120, 181);
	}
	
	.toolsMenu ul li:hover{
		cursor:pointer;
		background-color: rgb(60, 120, 181);
	}
	.cellGreen {
		/* color: green; */
	}
	.cellRed {
		background-color : red;
	}
	.cellYellow {
		background-color : yellow !important;
	}
	
  </style>
  <script type="text/javascript">
  var aaaFlag=0,dscFlag=0,instanceFlag=0;	
  var dscddashboard = null;
  var instanceDashboard = null;
  var dashboard = null;
  
  var frameHeader = top.frames["topFrame"].document.getElementById('frameHeader');
  $(frameHeader).hide();

 /*  var dashboardInitTime = top.frames["topFrame"].document.getElementById('dashboardInitTime');
  $(dashboardInitTime).show(); */
  
  Number.prototype.padLeft = function(base,chr){
   var  len = (String(base || 10).length - String(this).length)+1;
   return len > 0? new Array(len).join(chr || '0')+this : this;
  }
    
  $(function() {
	  
	  /* Socket Implementation Code */
	  var contextPath = window.location.host + "${pageContext.request.contextPath}";
	  var socketLocation="ws://"+contextPath+"/dashboardSocket";
	  getDashBoardSocket().init(socketLocation);	
	  
  });
  
  $(document).ready(function() { 
	  		$("#dashboardMenus").tabs({closable: true});
 		
		 	 $(".slideshow").click(function(){
		 		  var popup =  window.open("<%=basePath%>/jsp/dashboardwidgets/LayoutContainer.jsp"); 
		 	       popup.moveTo(0,0);
		 	 });
		 	
		 	$('.refreshDiv').click(function(){
		 		top.mainFrame.location.reload();
		 	});
		 	
		 	$('.closeDashboard').click(function(){
		 		var r = confirm("Are you sure you want to close this Dashboard ? ");
		 		if (r == true) {
		 			var closeId=$(this).attr('id');
		 			var dashboardId=$(this).attr('id');
			 		var start =  dashboardId.indexOf("_") + 1;
					var end = dashboardId.length;
					dashboardId = dashboardId.substring(start,end);
					var isActive="I";
					 $.ajax({url:'<%=request.getContextPath()%>/dashboard.do?method=closeDashboard',
				          type:'POST',
				          data:{isActive:isActive,dashboardId:dashboardId},
				          async:false,
				          success: function(transport){
				        	$('#'+closeId).attr("href", "<%=basePath%>/dashboard.do?method=getDashboardData");
				        }
				   });
		 		} 
		 	});
		 	
		 	$(".img_24 li").hover(
		 			function() {
		 				var imgObj=$(this).find('img');
		 				if($(imgObj).attr('class') == 'opennewwindow'){
		 					$(imgObj).attr('src','<%=basePath%>/images/open-in-new-window-White.png');
		 				}else if($(imgObj).attr('class') == 'refreshdashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/Refresh-White.png');
		 				}else if($(imgObj).attr('class') == 'slideshowdashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/slideshow -white.png');
		 				}else if($(imgObj).attr('class') == 'closedashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/close-white.png');
		 				}else if($(imgObj).attr('class') == 'editlayout'){
		 					$(imgObj).attr('src','<%=basePath%>/images/layout-white.png');
		 				}else if($(imgObj).attr('class') == 'addnewwidget'){
		 					$(imgObj).attr('src','<%=basePath%>/images/add-widget-white.png');
		 				}else if($(imgObj).attr('class') == 'createnewdashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/add-dashboard-white.png');
		 				}else if($(imgObj).attr('class') == 'managedashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/configuration-white.png');
		 				}
		 			}, function() {
		 				var imgObj=$(this).find('img');
		 				if($(imgObj).attr('class') == 'opennewwindow'){
		 					$(imgObj).attr('src','<%=basePath%>/images/open-in-new-window.png');
		 				}else if($(imgObj).attr('class') == 'refreshdashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/Refresh_Submenu.png');
		 				}else if($(imgObj).attr('class') == 'slideshowdashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/slideshow_SubMenu.png');
		 				}else if($(imgObj).attr('class') == 'closedashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/close.png');
		 				}else if($(imgObj).attr('class') == 'editlayout'){
		 					$(imgObj).attr('src','<%=basePath%>/images/layout.png');
		 				}else if($(imgObj).attr('class') == 'addnewwidget'){
		 					$(imgObj).attr('src','<%=basePath%>/images/add-widget.png');
		 				}else if($(imgObj).attr('class') == 'createnewdashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/add-dashboard.png');
		 				}else if($(imgObj).attr('class') == 'managedashboard'){
		 					$(imgObj).attr('src','<%=basePath%>/images/configuration.png');
		 				}
		 			}
		 	);
		 	
		 	  $( ".topitem" ).each(function( index ) {
              	var obj=$(this).hasClass( "ui-tabs-selected");
              	if(obj == false){
              		 $(this).find(".buttonbg div").removeClass('arrow1');
                  	 $(this).find(".buttonbg div").addClass('arrow');
              	}else{
              		 $(this).find(".buttonbg div").removeClass('arrow');
              		 $(this).find(".buttonbg div").addClass('arrow1');
              	}
              });
		 	  
		 	 var d = new Date,
		        dformat = [ d.getDate().padLeft(),
		                   (d.getMonth()+1).padLeft(),
		                    d.getFullYear()].join('/')+
		                   ' ' +
		                  [ d.getHours().padLeft(),
		                    d.getMinutes().padLeft(),
		                    d.getSeconds().padLeft()].join(':');

		
		 	  $('#dashboardInitTime').text("Dashboard Init Time : " +  dformat);
		 }); 
	  function getDashboardId(dashboard_id){
		
			$('#currentTabId').val("dashboard"+dashboard_id);
			$('#dashboardIntId').val(dashboard_id);
	}
 
  </script>
   <script type="text/javascript">
      // DashboardManager which contains the dashboards
      var dashboardManager = function() {
        var dashboards = new Array();

        function addDashboard(d) {
          dashboards.push(d);
        }

        function getDashboard(id) {
          var r;
          
          for(i=0;i<dashboards.length;i++) {
            if (dashboards[i].element.attr("id") == id) {
              r = dashboards[i];
            }
          }
          return r;
        }

        // Public methods and variables.
        return {
          addDashboard:addDashboard,
          getDashboard:getDashboard
        }

      }();


      $(function() {
        // load the templates
        $('body').append('<div id="templates"></div>');
        
        $("#templates").hide();
        $("#templates").load("<%=basePath%>/jsp/dashboarddemo/templates.html", initDashboard);

        $('#switcher').themeswitcher();

        function initDashboard() {
      //    $("#tabs").tabs({ cache: true });

          $('.dmopenaddwidgetdialog').click(function() {
            // open the lightbox for active tab
            var dashboard = dashboardManager.getDashboard(getCurrentTab().find(".dashboard").attr("id"));
            dashboard.element.trigger("dashboardOpenWidgetDialog");
			
            return false;
          });

          $('.dmeditLayout').click(function() {
            // open the lightbox for active tab
            var dashboard = dashboardManager.getDashboard(getCurrentTab().find(".dashboard").attr("id"));
            dashboard.element.trigger("dashboardOpenLayoutDialog");

            return false;
          });

          function getCurrentTab() {
            // return the tab which has not the class "ui-tabs-hide"
            return $('#dashboardMenus').find('.ui-tabs-panel').filter(function() {
              return !$(this).hasClass('ui-tabs-hide');
            });
          }
        }

      });
      
    </script>
    <style type="text/css">
    .ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {
      background-image: none;
      background-color:white;
      color:black !important;
    }
  </style>
  </head>
  <div id="dashboardInitTime" style="vertical-align:right;text-align: right;vertical-align: bottom;padding-right: 15px;margin-top: 20px;font-family: Lucida Grande,Lucida Sans Unicode,Verdana,Arial,Helvetica,sans-serif;font-size: 12px;color: #005197;"> 
  </div>
	<div id="dashboardMenus">
	<ul id="mbmcpebul_table" class="mbmcpebul_menulist css_menu" style="height: 25px;">
  		<%if(dashboardList != null && dashboardList.size() > 0){
         		   firstTabId=dashboardList.get(0);
         %>
        <input type="hidden" id="currentTabId" name="currentTabId" value="dashboard<%=firstTabId.getDashboardId()%>"/>
        <input type="hidden" id="dashboardIntId" name="dashboardIntId" value="<%=firstTabId.getDashboardId()%>"/>
	    <% for(DashboardData dashboardData:dashboardList){%>
  		<li class="topitem spaced_li first_button">
  			<div class="buttonbg gradient_button gradient30" style="position: relative;padding-left: 10px;" title="<%=dashboardData.getDashboardName()%>">
  				<div class="arrow" style="position: relative;" >
	  				<a href="#<%=dashboardData.getDashboardName()%>" onclick="getDashboardId('<%=dashboardData.getDashboardId()%>');">
	 				 	 <%=EliteUtility.formatDashboardName(dashboardData.getDashboardName()) %>
	 					 <img style="position: relative; top: 2px; left: 3px;" src="<%=request.getContextPath()%>/images/dots.gif" height="13" width="1"/>
	  				</a>
	  			</div>
	  		</div>
	 		 <ul class="img_24">
		 		 <li class="first_item">
				  	<a class="with_img_24 documents" title="" href="#">
				  		<img class="opennewwindow" src="<%=request.getContextPath()%>/images/open-in-new-window.png" alt="" height="16px" width="16px" />
				  		Open in New Window
				  	</a>
		 		 </li>
		  		<li>
				  	<a class="with_img_24 refresh refreshDiv" title="" href="#" id="refresh_<%=dashboardData.getDashboardId()%>">
				  		<img class="refreshdashboard" src="<%=request.getContextPath()%>/images/Refresh_Submenu.png" alt="" height="16px" width="16px" />
				  		Refresh
				  	</a>
		  		</li>
			    <li class="separator"><div></div></li>
			   <li>
				  <a class="with_img_24 slideshow" title="" href="#">
						<img class="slideshowdashboard" src="<%=request.getContextPath()%>/images/slideshow_SubMenu.png" alt="" height="16px" width="16px" />		  
					  	Slide Show
				  </a>
			   </li>
		  	   <li class="separator"><div></div></li>
		  	   <li class="last_item">
			  	   	<a class="with_img_24 close closeDashboard" title="" href="#" id='close_<%=dashboardData.getDashboardId()%>'>
				 		 <img class="closedashboard" src="<%=request.getContextPath()%>/images/close.png" alt="" height="16px" width="16px" />		  
				  		 Close
			  		</a>
		 	  </li>
	    </ul>
   </li>
   <% }} %>
   <li class="topitem spaced_li first_button" style="float: right;padding-right: 7px;">
	   	<div class="buttonbg gradient_button gradient30" style="width: 70px;">
	   		<div class="arrow">
	   			<a href="#"> Tools
	   				<img style="position: relative; top: 2px; left: 3px;" src="<%=request.getContextPath()%>/images/dots.gif" height="13" width="1"/>
	    		</a>
	    	</div>
	    </div>
	 	<ul class="img_24">
		<%--   <li class="first_item dmeditLayout headerlink">
			  	<a class="with_img_24 editlayout" title="" href="#">
			  		<img class="editlayout" src="<%=request.getContextPath()%>/images/layout.png" alt="" height="16px" width="16px" />		 
  					Edit Layout
			  	</a>
		  </li> --%>
		   <%if(dashboardForm.isAccessGroupId() == true){ %>
			  <li class="dmopenaddwidgetdialog">
			  	<a class="with_img_24 addWidget" >
			  		<img class="addnewwidget" src="<%=request.getContextPath()%>/images/add-widget.png" alt="" height="16px" width="16px" />	
	  				Add New Widget
			  	</a>
			  </li>
			  <li class="separator"><div></div></li>
			  <li>
			  	<a class="with_img_24" title=""  href="<%=basePath%>/dashboard.do?method=initCreate">
					<img class="createnewdashboard" src="<%=request.getContextPath()%>/images/add-dashboard.png" alt="" height="16px" width="16px" />	
	  				Create New Dashboard
			  	</a>
			  </li>
			 <%}else{ %>
			  <li class="invisible-menu">
			  	<a class="with_img_24" title="">
			  		<img class="addnewwidget" src="<%=request.getContextPath()%>/images/add-widget.png" alt="" height="16px" width="16px" />	
	  				Add New Widget
			  	</a>
			  </li>
			  <li class="separator invisible-menu"><div></div></li>
			  <li class="invisible-menu">
			  	<a class="with_img_24" title="">
					<img class="createnewdashboard" src="<%=request.getContextPath()%>/images/add-dashboard.png" alt="" height="16px" width="16px" />	
	  				Create New Dashboard
			  	</a>
			  </li>
			 <%} %>
			  <li class="last_item">
			  	<a class="with_img_24" title="" href="<%=basePath%>/dashboard.do?method=initManage">
			 	  <img class="managedashboard" src="<%=request.getContextPath()%>/images/configuration.png" alt="" height="16px" width="16px" />
	 				Manage Dashboard
			  	</a>
			  </li>
	  </ul>
  </li> 
</ul>
      <%if(dashboardList != null && dashboardList.size() > 0){
        	for(DashboardData dashboardData:dashboardList){
				String jspPath="/jsp/dashboardtabs/DashboardTabs.jsp";%>
					 <div id="<%=dashboardData.getDashboardName()%>">
					        <jsp:include page="<%=jspPath%>">
					        	 	<jsp:param value="<%=dashboardData.getDashboardId()%>" name="dashboardId"/>
					        	 	<jsp:param value="<%=dashboardData.getDashboardName()%>" name="dashboardJsonFileName"/>
					        </jsp:include> 
					 </div>
		<%}} %> 
    </div>
    <img src="<%=request.getContextPath()%>/images/elitecore_logo_new.png" id="elitecore_Image" style="display: none;"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dashboard/mbjsmbmcp.js"></script>
