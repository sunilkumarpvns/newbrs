<% String basePath = request.getContextPath();%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>EliteDSC Dashboard- Home</title>
  </head>
<body style="background-color: #FAFAFA">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="4" class=" labeltext">
			<table  width="100%">
				<tr>
					<td>
						  <div id="instanceDashboard" class="dashboard" >
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
    $(document).ready(function() { 

      // load the templates
      $('body').append('<div id="instanceTemplates"></div>');
      $("#instanceTemplates").hide();
      $("#instanceTemplates").load("<%=basePath%>/jsp/dashboarddemo/templates.html", initInstanceDashboard);
      

      // call for the themeswitcher
      $('#switcher2').themeswitcher();

      function initInstanceDashboard() {

        // to make it possible to add widgets more than once, we create clientside unique id's
        // this is for demo purposes: normally this would be an id generated serverside
        var startId = 300;

        instanceDashboard = $('#instanceDashboard').dashboard({
          // layout class is used to make it possible to switch layouts
          layoutClass:'layout',
          // feed for the widgets which are on the dashboard when opened
          json_data : {
            url: "<%=basePath%>/jsp/dashboardwidgets/dashboardjson/instancewidgets.json"
          },
          // json feed; the widgets whcih you can add to your dashboard
          addWidgetSettings: {
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
          instanceDashboard.element.live('dashboardAddWidget',function(e, obj){
          var widgetInstance = obj.widget;
          instanceDashboard.addWidget({
            "id":startId++,
            "title":widget.title,
            "url":widgetInstance.url,
            "metadata":widgetInstance.metadata
            }, instanceDashboard.element.find('.column:first'));
        }); 

        // the init builds the dashboard. This makes it possible to first unbind events before the dashboars is built.
        instanceDashboard.init();
      }
   }); 

    </script>
  
</html>
