/**
 *  This Js file provide Websocket utility for dashboard 
 *   
 * @author punit.j.patel
 */

var commanFunction = {
		isLogEnabled : true,
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


var widgetAlerts = {
	module : "WIDGET-ALERTs", 
	typeName  : "WidgetAlerts",
	warnAlert : "WARN",
	errorAlert  : "ERROR",
	
	handleAlerts : function (widget, widgetJSONResponseData ) {
		commanFunction.log(this.module, "Handle Alerts for Widget ID : " + widget.id);
		
		var progressbarObject = $('#'+widget.id.toString()).parent().find('.progressbar');
		$(progressbarObject).hide();
		
		
		var id = widget.id.toString();
		var widgetDivId=id;
		if(id.indexOf("_") > 0 ) {
			var start =  widget.id.indexOf("_") + 1;
			var end = widget.id.lastIndexOf("_");
			id = id.substring(start,end);
		}
		if(widgetJSONResponseData.header.type == this.typeName ) {
			var alertType =  widgetJSONResponseData.body;
			commanFunction.log(this.module, "Alert Type : " + alertType);
			if( alertType ==  this.warnAlert) {
				$('#warnWidget'+id).show();
				$('#errorWidget'+id).hide();
			} else {
				var traceLog = 	 widgetJSONResponseData.exceptionDetails;
				$('#errorWidget'+id).show();
				$('#warnWidget'+id).hide();
				$('#progressbarDIV'+id).hide();
				$('#exportingdiv'+id).hide();
				
				var exportdivObject = $('#'+widget.id.toString()).parent().find('.exportdiv');
				if(exportdivObject){
					$(exportdivObject).hide();
				}
				
				var errorMessage=alertType;
				
				$.ajax({
                    url: "jsp/dashboardwidgets/WidgetsErrorHandler.jsp",
                    data:{errorMessgae:errorMessage,errorPriority:"MAJOR",traceLog:traceLog,widgetIds:id,widgetDivId:widgetDivId},
                    success: function (result) {
                    	$('#'+widgetDivId).html(result);
                    	$('#'+widgetDivId).css({"border-color": "#953B39", 
                            "border-width":"1px", 
                            "border-style":"solid","box-shadow": "0px 0px 6px #D59392"});
                    }
				});
			}
		} else {
			$('#warnWidget'+id).hide();
			$('#errorWidget'+id).hide();
			$('#exportingdiv'+id).show();
			$('.sub-esi-chart').hide();
			$('#'+widgetDivId).css({"border-color": "#C0C0C0", 
                "border-width":"0px", 
                "border-style":"solid","box-shadow": "0px 0px 1px #C0C0C0"});
		}
		
	}	
};

var dashBoardSocket = {
		module : "DASHBOARDSOCKET",
		websocket : null,
		dashboardWidgetArray : [],
		isConnect : false,
		cachedWidget : [],
		init : function (url) {
			  if ("WebSocket" in window)
			  {
				this.websocket = new WebSocket(url);
				this.initWebSocket();
			  }else{
				 alert("WebSocket NOT supported by your Browser!");
			  }
		},
		initWebSocket : function () {
			this.websocket.onopen = function(){
				commanFunction.log(dashBoardSocket.module, "Establish Connection");
				dashBoardSocket.isConnect = true;
				for ( var i = 0; i < dashBoardSocket.cachedWidget.length; i++) {
					dashBoardSocket.sendRequest(dashBoardSocket.cachedWidget[i]);
				}
			};
			this.websocket.onmessage = function(message){
				commanFunction.log(dashBoardSocket.module, "Receive Message Data : " + message.data);
				dashBoardSocket.handleResponse(message.data);
			};
			this.websocket.onclose = function(){ 
				commanFunction.log(dashBoardSocket.module, "Connection Closed");
				dashBoardSocket.dashboardWidgetArray = [];
		    };
		    this.websocket.onerror = function(message){
				alert('Socket Closed');
			};
		    
		},
		register : function(widget) {
			if(widget instanceof Widget) {
				this.dashboardWidgetArray[this.dashboardWidgetArray.length] = widget; 
			}
		},deRegister : function(widgetId){
			commanFunction.log(dashBoardSocket.module, "De - Register widget Id : "+widgetId);
			
			for ( var index = 0; index < this.dashboardWidgetArray.length; index++) {
				if (this.dashboardWidgetArray[index].id == widgetId) {
					removeByAttr(this.dashboardWidgetArray, 'id', widgetId);
				}
			}
			commanFunction.log(dashBoardSocket.module, "Successfully de - Register Widget, Widget Id : "+widgetId);
		},
		sendRequest : function (data) {
			if(!this.isConnect) {
				commanFunction.log(dashBoardSocket.module, "Connection not open Cache Message Data : " + JSON.stringify(data));
				this.cachedWidget[this.cachedWidget.length] = data;
				return;
			}
			
			commanFunction.log(dashBoardSocket.module, "Send Message Data : " + JSON.stringify(data));
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
						if (messageData[i].header.type == "tableschema") {
							widget.widgetHandler.renderData($.parseJSON(messageData[i].body));
						} else if (messageData[i].header.type == "tabledata") {
							widget.widgetHandler.updateData($.parseJSON(messageData[i].body));
						}
						widgetAlerts.handleAlerts(widget, messageData[i]);
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
		this.data="";
	}
	
	function getDashBoardSocket() {
		return dashBoardSocket;
	}
	
	var removeByAttr = function(arr, attr, value){
	    var i = arr.length;
	    while(i--){
	       if(arr[i] && arr[i].hasOwnProperty(attr) && (arguments.length > 2 && arr[i][attr] === value )){
	           arr.splice(i,1);
	       }
	    }
	    return arr;
	};

