/**
 * This Js file use for Add Broadcast Server Mapping in Service Policy
 */
var broadcastServerOptions = [];

function setBroadcastServerOptions(broadcastServerOptions){
	this.broadcastServerOptions = broadcastServerOptions;
}

function addBroadCastRow(){
	var tableRowStr = $("#broadcastTemplate").find("tr");
	$("#mappingtbl1 tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	addDropDown();
	$("#mappingtbl1 tr:last").find("select:first").focus();
}

function addDropDown(){
	var broadcastValueArray = getSelectedBroadCastArray();
	var selectObj = $("#mappingtbl1 tr:last").find("select:first");
	$(selectObj).append("<option value='0'>  --Select--  </option>");
	if(broadcastServerOptions!=undefined){
		$.each(broadcastServerOptions, function(index, item) {
			if($.inArray(item.value,broadcastValueArray) < 0 ){
				$(selectObj).append("<option value='" + item.value + "'>" + item.name + "</option>");
			}
		});
	}
}

function getSelectedBroadCastArray(){
	var broadcastValueArray = new Array();
	var arrIndex = 0; 
	$("#mappingtbl1 select[name='esiInstanceId']").each(function(){
		if($(this).val() != "0"){
			broadcastValueArray[arrIndex++] = $(this).val();
		}				
	});
	return broadcastValueArray;
}

function setDropDown(){
	var broadcastValueArray = getSelectedBroadCastArray();
	$("#mappingtbl1 select[name='esiInstanceId']").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value='0'>  --Select--  </option>");
		if(broadcastServerOptions!=undefined){
			$.each(broadcastServerOptions, function(index, item) {
				if($.inArray(item.value,broadcastValueArray) < 0 || item.value == currentVal){
					$(selectObj).append("<option value='" + item.value + "'>" + item.name + "</option>");
				}
			});
		}
		$(selectObj).val(currentVal);
	});
}

function validate(){
	var isValid = true;
	$("#mappingtbl1 select[name='esiInstanceId']").each(function(){
		if($(this).val() == "0"){
			alert("BroadCasting Server must specified");
			isValid = false;
			$(this).focus();
			return false;
		}
	});
	if(isValid){
		setCheckBoxData("isResponseMandatoryChkBox","isResponseMandatory");
		setCheckBoxData("trueOnAttrNotFoundChkBox","trueOnAttrNotFound");
		document.forms[0].submit();
	}
	
}
function setCheckBoxData(name,hiddenName){
	$("#mappingtbl1 input[name='"+name+"']").each(function(index, item){
		$(this).append("<input type='hidden' name='"+hiddenName+"' value='"+item.checked+"'>");
	});
}