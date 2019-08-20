<div id="peertarget"/>
<script>

var interval = <%=request.getParameter("interval") != null ? request.getParameter("interval") : 5%>;


$(function(){
	var data = {
			header : {
				id : "PEER",
				type : "PEER"
			},
			body : {
				interval : interval
			}
	};
	var intervalCounter = 0;
	var widgetHandler  = {
			renderData : function(data) {
				$("#peertarget").renderTable(data);
			},
			updateData : function(data) {
				if(intervalCounter++ == interval) {
					$("#peertarget").find(".cellGreen").each(function(){
						$(this).removeClass("cellGreen");
					});
					intervalCounter = 0;
				}
				setIds(data);
			}
	};
	dashBoardWidgets.register(new Widget("PEER", widgetHandler));
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
</script>