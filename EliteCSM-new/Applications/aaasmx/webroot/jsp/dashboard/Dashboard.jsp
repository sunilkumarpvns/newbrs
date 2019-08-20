<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<LINK REL ="stylesheet" TYPE="text/css" HREF="${pageContext.request.contextPath}/css/dashboard/widjettable.css"/>
<title>Dashboard</title>
</head>
<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard/table-schema.js"></script>

	
 <!-- js file for highcharts -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/highstock.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/exporting.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard/no-data-to-display.src.js"/>
<script>

//set high chart's global options 
Highcharts.setOptions({
    noData: {
            // Custom positioning/aligning options
            position: {	
                //align: 'right',
                //verticalAlign: 'bottom'
            },
            // Custom svg attributes
            attr: {
                //'stroke-width': 1,
                //stroke: '#cccccc'
            },
            // Custom css
            style: {                    
                //fontWeight: 'bold',     
                //fontSize: '15px',
                //color: '#202030'        
            }
        }
 });
</script>
<body>
    <%--@include file="ESIWidget.jsp" --%>
    <%--@include file="PeerWidget.jsp" --%>
	<%@include file="TotalRequestStatSummary.jsp"%>
	<%@include file="MemoryUsageChart.jsp"%>
	<%@include file="ReplyMessageStatChart.jsp"%>
</body>
<script>
var contextPath = window.location.host + "${pageContext.request.contextPath}";

var commanFunction = {
		isLogEnabled : false,
		setLogEnabled : function (flag) {
			this.isLogEnabled = flag ? true : false; 
		},
		log : function (module, message) {
			if(this.isLogEnabled) {
				console.log(this.getCurrentTime() + "[" + module +"] : " + message);
			}
		},
		logError : function (module, message) {
			if(this.isLogEnabled) {
				console.error(this.getCurrentTime() + "[" + module +"] : " + message);
			}
		}, 
		getCurrentTime : function () {
			var currentdate = new Date(); 
			return  currentdate.getDate() + "/"
			                + (currentdate.getMonth()+1)  + "/" 
			                + currentdate.getFullYear() + "     "  
			                + currentdate.getHours() + ":"  
			                + currentdate.getMinutes() + ":" 
			                + currentdate.getSeconds();
		}
		
};



var dashBoardWidgets = {
		module : "DASHBOARDWIDGET",
		websocket : null,
		dashboardWidgetArray : [],
		isConnect : false,
		cachedWidget : [],
		init : function (url) {
			this.websocket = new WebSocket(url);
			this.initWebSocket();
		},
		initWebSocket : function () {
			this.websocket.onopen = function(){
				commanFunction.log(dashBoardWidgets.module, "Establish Connection");
				dashBoardWidgets.isConnect = true;
				for ( var i = 0; i < dashBoardWidgets.cachedWidget.length; i++) {
					dashBoardWidgets.sendRequest(dashBoardWidgets.cachedWidget[i]);
				}
			};
			this.websocket.onmessage = function(message){
				commanFunction.log(dashBoardWidgets.module, "Receive Message Data : " + message.data);
				dashBoardWidgets.handleResponse(message.data);
			};
			this.websocket.onclose = function(){ 
				commanFunction.log(dashBoardWidgets.module, "Connection Closed");
				dashBoardWidgets.dashboardWidgetArray = [];
		    };
		    
		},
		register : function(widget) {
			if(widget instanceof Widget) {
				this.dashboardWidgetArray[this.dashboardWidgetArray.length] = widget; 
			}
		},
		sendRequest : function (data) {
			if(!this.isConnect) {
				commanFunction.log(dashBoardWidgets.module, "Connection not open Cache Message Data : " + JSON.stringify(data));
				this.cachedWidget[this.cachedWidget.length] = data;
				return;
			}
			commanFunction.log(dashBoardWidgets.module, "Send Message Data : " + JSON.stringify(data));
			if( this.isValidWidgetJSON(data) ) {
				data.body = JSON.stringify(data.body);
				this.websocket.send(JSON.stringify(data));
			} else {
				commanFunction.logError(this.module, "Send Request Failed, Reason : invalid JSON Data " + data.toString());
			}				
		},
		
		handleResponse : function(data) {
			var messageData = $.parseJSON(data);
			for ( var i = 0; i < messageData.length; i++) {
				if (this.isValidWidgetJSON(messageData[i])) {
					var widget = this.getWidget(messageData[i].header.id);
					if (widget != null && widget instanceof Widget) {
						var body = $.parseJSON(messageData[i].body);
						if (messageData[i].header.type == "tableschema") {
							widget.widgetHandler.renderData(body);
						} else if (messageData[i].header.type == "tabledata") {
							widget.widgetHandler.updateData(body);
						}
					} else {
						commanFunction.logError(this.module,"Can not Find Widget For ID  :" + messageData[i].header.id);
					}
				} else {
					commanFunction.logError(this.module,"Receive invalid JSON Data :" + data.toString());
				}
			}
		},
		getWidget : function(id) {
			for ( var index = 0; index < this.dashboardWidgetArray.length; index++) {
				if (this.dashboardWidgetArray[index].id == id) {
					return this.dashboardWidgetArray[index];
				}
			}
		},
		isValidWidgetJSON : function(json) {
			if (json.hasOwnProperty("header") && json.hasOwnProperty("body")) {
				var header = json.header;
				return header.hasOwnProperty("id")
						&& header.hasOwnProperty("type");
			}
			return false;
		}
	};

	function Widget(id, widgetHandler) {
		this.id = id;
		this.widgetHandler = widgetHandler;
	}
	
	var socketLocation="ws://"+contextPath+"/dashboardSocket";
	dashBoardWidgets.init(socketLocation);	
</script>
</html>