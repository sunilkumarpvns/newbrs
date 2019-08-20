var stompClient = null;
var progress = 0;
var count = 0;

function setConnected(connected) {
    enableButtons();
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
	$(".progress").css("visibility", "initial")
	postUpdate("Connecting...");
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        postUpdate("Connected");
        stompClient.subscribe('/topic/greetings', function (greeting) {
        	var message = JSON.parse(greeting.body);
        	var content = message.content;
        	if (content === 'Done') {
        		enableButtons();
        		$(".spinner").css("visibility", "hidden")
        		postUpdate("");
        	} else if (content === 'Count') {
        		disableButtons();
        		count = message.count;
        		console.log('Count event: ' + message.count);
        	} else if (content === 'Error') { 
        		$(".progress-bar").css({'background': '#d21c1c'});
        	} else {
        		postUpdate(content);
        		updateProgress();
        	}
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function create() {
    stompClient.send("/app/create", {}, JSON.stringify({'name': 'test'}));
    resetProgress();
}

function drop() {
    stompClient.send("/app/drop", {}, JSON.stringify({'name': 'test'}));
    resetProgress();
}

function resetProgress() {
	progress = 0;
	$(".progress-bar").css({'width': '0%'})
}

function updateProgress() {
	$(".progress-bar").css({'width': (++progress / (count)) * 100 + '%'})
}

function postUpdate(update) {
	$(".update-details").prepend("<div class='update-detail'>" + $(".update-value").html() + "</div>");
	$(".update-value").html("> " + update);
}

$(function () {
	$(".progress").css("visibility", "hidden")
	disableButtons();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
	connect();
    $("#connect" ).click(function() { create();});
    $("#drop" ).click(function() { drop(); });
});

function enableButtons() {
	$("#connect").removeProp("disabled");
	$("#drop").removeProp("disabled");
}

function disableButtons() {
	$("#connect" ).prop("disabled", true);
	$("#drop" ).prop("disabled", true);
}