<style type="text/css">
.cellGreen 
{
background-color: Lime;
}

.cellWhite
{
background-color: white;
}
</style>
<body>
<div id="target">
</div>
</body>
<script>

var interval = <%=request.getParameter("interval") != null ? request.getParameter("interval") : 5%>;


$(function(){
	var data = {
			header : {
				id : "ESI",
				type : "ESI"
			},
			body : {
				interval : interval
			}
	};
	var intervalCounter = 0;
	var widgetHandler  = {
			renderData : function(data) {
				$("#target").renderTable(data);
			},
			updateData : function(data) {
				if(intervalCounter++ == interval) {
					$("#target").find(".cellGreen").each(function(){
						$(this).removeClass("cellGreen");
					});
					intervalCounter = 0;
				}
				setIds(data);
			}
	};
	dashBoardWidgets.register(new Widget("ESI", widgetHandler));
	dashBoardWidgets.sendRequest(data);
});
function setIds(messageData) {
	for(var i=0; i<messageData.length; i++ ) {
	    var tdId = replaceAllSpecialChar(messageData[i].id); 
		$("#"+tdId).text(messageData[i].value);
		if(messageData.length < 10) {
			$("#"+tdId).addClass("cellGreen");
		}
	}

}

function replaceAllSpecialChar(val){
	var replaceStr =  val.replace(/[^\w\s]/gi, "");
	return replaceStr.split(' ').join('');
}
/* var ws = new WebSocket("ws://10.106.1.188:8080/Dashboard/dashboardSocket");
ws.onopen = function(){
	alert("estalbish Connection");
	postToServer();
};

function replaceAllSpecialChar(val){
	var replaceStr =  val.replace(/[^\w\s]/gi, "");
	return replaceStr.split(' ').join('');
}

ws.onmessage = function(message){
	$(".cellGreen ").each(function(){
		$(this).removeClass("cellGreen");
	});
	console.log("Time : " + getCurTime() + "Data :" +message.data);
	var messageData = $.parseJSON(message.data);
	for(var i=0; i<messageData.length; i++ ) {
	    var header = messageData[i].header;
		if(header.type == "tableschema") {
			$("#target").renderTable($.parseJSON(messageData[i].body));
		} else if (header.type == "tabledata") {
			var idData = $.parseJSON(messageData[i].body);
			setIds(idData);
		}
	}
	
};

function postToServer(){
    ws.send(JSON.stringify(data));
}
function closeConnect(){
    ws.close();
}

function getCurTime() {
	var currentdate = new Date(); 
	return "Last Sync: " + currentdate.getDate() + "/"
	                + (currentdate.getMonth()+1)  + "/" 
	                + currentdate.getFullYear() + " @ "  
	                + currentdate.getHours() + ":"  
	                + currentdate.getMinutes() + ":" 
	                + currentdate.getSeconds();
} */


</script>
</html>