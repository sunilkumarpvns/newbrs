/**
 * This Js file use for Add Broadcast Server Mapping in Service Policy
 */
var broadcastServerOptions = [];

function setBroadcastServerOptions(broadcastServerOptions){
	this.broadcastServerOptions = broadcastServerOptions;
}

function addProxyPolicyRow(templateId,tableId,obj){
	var mainTable = $('#'+tableId).closest('.tblmProxyCommunication');
	$(mainTable).css({'border':'none'});
	
	var tableRowStr = $("#"+templateId).find("tr");
	$("#"+tableId).append("<tr>"+$(tableRowStr).html()+"</tr>");
	var addMoreServerObj = $('#'+tableId+" tr:last").parent().find('.add-more-esi-button');
	addProxyDropDown(addMoreServerObj);
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
}

function addDropDown(){
	var broadcastValueArray = getSelectedBroadCastArray();
	var selectObj = $(".proxyCommunicationTbl tr:last").find("select:first");
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
	$(".proxyCommunicationTbl select[name='esiInstanceId']").each(function(){
		if($(this).val() != "0"){
			broadcastValueArray[arrIndex++] = $(this).val();
		}				
	});
	return broadcastValueArray;
}

function setDropDown(){
	var broadcastValueArray = getSelectedBroadCastArray();
	$(".proxyCommunicationTbl select[name='esiInstanceId']").each(function(){
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
	$(".proxyCommunicationTbl select[name='esiInstanceId']").each(function(){
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
	$(".proxyCommunicationTbl input[name='"+name+"']").each(function(index, item){
		$(this).append("<input type='hidden' name='"+hiddenName+"' value='"+item.checked+"'>");
	});
}

function addBroadCastingRow(templateId,tableId,obj){
	var mainTable = $('#'+tableId).closest('.tblmBroadCastCommunication');
	$(mainTable).css({'border':'none'});
	var tableRowStr = $("#"+templateId).find("tr");
	$("#"+tableId).append("<tr>"+$(tableRowStr).html()+"</tr>");
	var addMoreServerObj = $('#'+tableId+" tr:last").parent().find('.add-more-esi-button');
	addProxyDropDown(addMoreServerObj);
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
}

// Stateful Proxy Sequential Add Handler
function addStatefulProxyPolicyRow(templateId,tableId,obj){
    var mainTable = $('#'+tableId).closest('.statefulProxyCommunicationTbl');
    $(mainTable).css({'border':'none'});

    var tableRowStr = $("#"+templateId).find("tr");
    $("#"+tableId).append("<tr>"+$(tableRowStr).html()+"</tr>");
}

// Stateful Proxy Broadcast Add Handler
function addStatefulBroadcastProxyPolicyRow(templateId,tableId,obj){
    var mainTable = $('#'+tableId).closest('.statefulProxyBroadcastCommunicationTbl');
    $(mainTable).css({'border':'none'});

    var tableRowStr = $("#"+templateId).find("tr");
    $("#"+tableId).append("<tr>"+$(tableRowStr).html()+"</tr>");
}