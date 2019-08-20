
<% String basePath = request.getContextPath();%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>EliteAAA Dashboard - Home</title>

    <script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery-1.6.4.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/themeroller.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/dashboardui.css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" /> 
    <script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery.dashboard.min.js"></script>
    	
    
<style>
/* a:hover {background-color:transparent;background-color:#14406F;color: black;font-weight: bold;color: black;text-decoration:none;} */

</style>
   
  </head>

  <body style="background-color: #FAFAFA">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="4" class=" labeltext">
			<table  width="100%">
				<tr>
					<td>
						  <div id="dashboard" class="dashboard" >
						    <!-- this HTML covers all layouts. The 5 different layouts are handled by setting another layout classname -->
						    <div class="layout" >
						      <div class="column first column-first"></div>
						      <div class="column second column-second"></div>
						      <div class="column third column-third"></div>
						    </div>
						  </div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
 
  </body>
  
   <script type="text/javascript">
    // This is the code for definining the dashboard
   $(window).load(function() {
      // load the templates
      $('body').append('<div id="templates"></div>');
      $("#templates").hide();
      $("#templates").load("<%=basePath%>/jsp/dashboarddemo/demotemplates.html", initDashboard);
      

      // call for the themeswitcher
      $('#switcher').themeswitcher();

      function initDashboard() {

        // to make it possible to add widgets more than once, we create clientside unique id's
        // this is for demo purposes: normally this would be an id generated serverside
        var startId = 100;

        var dashboard = $('#dashboard').dashboard({
          // layout class is used to make it possible to switch layouts
          layoutClass:'layout',
          // feed for the widgets which are on the dashboard when opened
          json_data : {
            url: "<%=basePath%>/jsp/dashboardwidgets/dashboardjson/aaawidgets.json"
          },
          // json feed; the widgets whcih you can add to your dashboard
          addWidgetSettings: {
            widgetDirectoryUrl:"<%=basePath%>/jsp/dashboardwidgets/dashboardjson/widgetcategories.json"
          },

          // Definition of the layout
          // When using the layoutClass, it is possible to change layout using only another class. In this case
          // you don't need the html property in the layout

          layouts :
            [
              { title: "Layout1",
                id: "layout1",
                image: "<%=basePath%>/jsp/dashboarddemo/layouts/layout1.png",
                html: '<div class="layout layout-a"><div class="column first column-first"></div></div>',
                classname: 'layout-a'
              },
              { title: "Layout2",
                id: "layout2",
                image: "<%=basePath%>/jsp/dashboarddemo/layouts/layout2.png",
                html: '<div class="layout layout-aa"><div class="column first column-first"></div><div class="column second column-second"></div></div>',
                classname: 'layout-aa'
              }
            ]

        }); // end dashboard call

        // binding for a widgets is added to the dashboard
        dashboard.element.live('dashboardAddWidget',function(e, obj){
       	  var widget = obj.widget;
	      if(currentTabId == 'dscDashboard'){
	          dscddashboard.addWidget({
	              "id":startId++,
	              "title":widget.title,
	              "url":widget.url,
	              "metadata":widget.metadata
	              }, dscddashboard.element.find('.column:first'));
	      }else if(currentTabId == 'dashboard'){
	          dashboard.addWidget({
	            "id":startId++,
	            "title":widget.title,
	            "url":widget.url,
	            "metadata":widget.metadata
	            }, dashboard.element.find('.column:first'));
	      }else if(currentTabId == 'instanceDashboard'){
	    	   instanceDashboard.addWidget({
	    	     "id":startId++,
	    	     "title":widget.title,
	    	     "url":widget.url,
	    	     "metadata":widget.metadata
	    	   }, instanceDashboard.element.find('.column:first'));
	      }
        
          
   
        });

        // the init builds the dashboard. This makes it possible to first unbind events before the dashboars is built.
        dashboard.init();
      }
    });
    var frameHeader = top.frames["topFrame"].document.getElementById('frameHeader');
	$(frameHeader).hide();

	
    </script>
</html>
