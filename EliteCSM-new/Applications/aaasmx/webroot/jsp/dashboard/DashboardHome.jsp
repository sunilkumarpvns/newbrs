<% String basePath = request.getContextPath();%>
						  <div  align="left" id="dashboard" class="dashboard" style="width: 100%" >
						      <div class="column first column-first"></div>
						      <div class="column second column-second"></div>
						      <div class="column third column-third"></div>
						    </div>
 
  
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

    </script>

