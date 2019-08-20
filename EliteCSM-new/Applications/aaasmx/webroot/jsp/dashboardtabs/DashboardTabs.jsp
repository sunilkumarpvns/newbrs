<LINK REL ="stylesheet" TYPE="text/css" HREF="${pageContext.request.contextPath}/css/dashboard/widjettable.css"/>
<%  String basePath = request.getContextPath();
	String dashboardId=request.getParameter("dashboardId");
	String dashboardJsonFileName=request.getParameter("dashboardJsonFileName");
%>
<div  align="left" id="dashboard<%=dashboardId%>" class="dashboard" style="width: 100%" >
	 <div class="column first column-first" id="first" style="padding-bottom: 200px;"></div>
     <div class="column second column-second" id="second" style="padding-bottom: 200px;"></div>
</div>
 <script>
$(document).ready(function() {

	 //This code check that json file is exist or not
	 function fileExists(url)
	 {
	     var http = new XMLHttpRequest();
	     http.open('HEAD', url, false);
	     http.send();
	     return http.status!=404;
	 }
	 
	 var json_path = "<%=basePath%>/jsp/dashboardwidgets/jsonfiles/<%=dashboardJsonFileName%>.json";
	 var jsonfile_url="<%=basePath%>/jsp/dashboardwidgets/jsonfiles/<%=dashboardJsonFileName%>.json";
	 
	 if(!fileExists(json_path)){
		 jsonfile_url="<%=basePath%>/jsp/dashboardwidgets/jsonfiles/template.json";
	 }
	var dashboardObj='#dashboard'+<%=dashboardId%>;
		dashboardObj = $('#dashboard'+<%=dashboardId%>).dashboard({
   	 	layoutClass:'layout',
	    json_data : {
	      url: jsonfile_url
	    },
	    addWidgetSettings: {
	      widgetDirectoryUrl:"<%=basePath%>/jsp/dashboardwidgets/categoriesfiles/widgetcategories.json"
	    },
	    layouts :
	      [
	        { title: "Layout1",
	          id: "layout1",
	          image: "layouts/layout1.png",
	          classname: 'layout-a'
	        },
	        { title: "Layout2",
	          id: "layout2",
	          image: "layouts/layout2.png",
	          classname: 'layout-aa'
	        },
	        { title: "Layout3",
	          id: "layout3",
	          image: "layouts/layout3.png",
	          classname: 'layout-ba'
	        },
	        { title: "Layout4",
	          id: "layout4",
	          image: "layouts/layout4.png",
	          classname: 'layout-ab'
	        },
	        { title: "Layout5",
	          id: "layout5",
	          image: "layouts/layout5.png",
	          classname: 'layout-aaa'
	        }
	      ]
	
	  }); // end dashboard call

  var startId = 100;

  // binding for a widgets is added to the dashboard
  dashboardObj.element.live('dashboardAddWidget',function(e, obj){
    var widget = obj.widget;
    if(dashboardObj.element.attr('id') == $('#currentTabId').val()){
    	 var templateId=widget.id;
    	 var dashboardid=$('#dashboardIntId').val();
    	 var widgetOrderid=""; 
    	 $.ajax({url:'<%=request.getContextPath()%>/GetWidgetOrder',
    	        type:'GET',
    	        data:{templateId:templateId,dashboardid:dashboardid},
    	        async:false,
    	        success: function(widgetOrderId){
    	        	widgetOrderid=widgetOrderId;
    	      }
    	 });
    	  $.ajax({url:'<%=request.getContextPath()%>/AddNewWidget',
 	        type:'GET',
 	        data:{templateId:templateId,dashboardid:dashboardid,widgetOrderid:widgetOrderid},
 	        async:false,
 	        success: function(widgetId){
 	        	startId=widgetId;
 	      }
 	    });
    	 var widgetEditUrl=widget.editurl+"?widgetId="+startId;
    	 dashboardObj.addWidget({
    	      "id":startId,
    	      "title":widget.title,
    	      "url":widgetEditUrl,
    	      "editurl":widgetEditUrl,
    	      "metadata":widget.metadata
    	 }, dashboardObj.element.find('.column:first'));
    	 $('html, body').animate({
    	        scrollTop: $("div#"+startId).offset().top
    	  }, 500);
    }
  });

 
  dashboardObj.init();
  dashboardManager.addDashboard(dashboardObj);

});
</script>


