var handlerDetails= {"authenticationTbl":{
							"ProfileLookupDriver":0,
							"AuthenticationHandler":0,
							"AuthorizationHandler":0,
							"CDRGeneration":0,
							"ConcurrencyHandler":0,
							"ProxyCommunication":0,
							"BroadcastingCommunication":0,
							"Plugin":0,
							"coaDMGen":0,
							"RadiusIMDGConcurrency":0,
							"StatefulProxyCommunication":0,
							"StatefulProxyBroadcastCommunication":0},
					 "additionalTbl":{
						 	"ProfileLookupDriver":0,
							"AuthenticationHandler":0,
							"AuthorizationHandler":0,
							"CDRGeneration":0,
							"ConcurrencyHandler":0,
							"ProxyCommunication":0,
							"BroadcastingCommunication":0,
							"Plugin":0,
							"coaDMGen":0,
                         	"RadiusIMDGConcurrency":0,
                         	"StatefulProxyCommunication":0,
						 	"StatefulProxyBroadcastCommunication":0
					  }
					};

var handlerPagePath = {"ProfileLookupDriver":"ProfileLookupDriver.jsp",
						"AuthenticationHandler":"AuthenticationHandler.jsp",
						"AuthorizationHandler":"AuthorizationHandler.jsp",
						"CDRGeneration":"CDRGeneration.jsp",
						"ConcurrencyHandler":"ConcurrencyHandler.jsp",
						"ProxyCommunication":"ProxyCommunication.jsp",
						"BroadcastingCommunication":"BroadcastCommunication.jsp",
						"Plugin":"Plugin.jsp",
						"coaDMGen":"COADMGeneration.jsp",
    					"RadiusIMDGConcurrency":"ConcurrencyIMDGHandler.jsp",
						"StatefulProxyCommunication":"StatefulProxySequentialHandler.jsp",
						"StatefulProxyBroadcastCommunication":"StatefulProxyBroadcastHandler.jsp"};

var authdefaultHandler = ['AuthenticationHandler','AuthorizationHandler','ProfileLookupDriver','ConcurrencyHandler','ProxyCommunication'];
var authAdditional = [];

var handlerArray = [];
var handlerType =['Authentication','AuthAdditional','Accounting','AcctAdditional'];

var handler = {
		handlerType : null,
		orderNumber : null,
		handlerId   : null,
		handlerData : function (){
			
		}
};

function Widget(id, handler) {
	this.id = id;
	this.handler = handler;
	this.data="";
}

function initializedHandler(){
	Widget();
}

function servicehandlerpopup(handlerTable,imdgEnable){
	var id="";
	var fixedHeight="";
	if(handlerTable == 'authenticationTbl'){
		id="addServiceHandlerPopup"
		fixedHeight=320;
	}else if(handlerTable == 'additionalTbl'){
		id="addPostServiceHandlerPopup"
		fixedHeight=165;
	}
	$('#'+id).dialog({
		modal: true,
		autoOpen: false,
		height:fixedHeight,
		width: 495,
		position: 'top',
		buttons:{
			        'Add': function() {
			            var selectedItems=$('#'+id).find("input[type='radio'][name='servicehandler']:checked");
			            var handlerName=$(selectedItems).val();

			           /* CASE 1 :AUTHENTICATION HANDLER */
			            var isAuthenticationTestPassed = validateAuthenticationHandler(handlerName,handlerTable);
			            var isImdgHandlerIsConfigured = validateConcurrencyImdgHanler(handlerName,imdgEnable);
			            if(isAuthenticationTestPassed && isImdgHandlerIsConfigured){
			            	 if(!(isEmpty(handlerName)) && handlerName != null && handlerName.length > 0){
						            addNewHandler(handlerName,handlerTable,true,false);
						            changeColorOfHandler(handlerName,handlerTable);
					         }
			            	 $(this).dialog('close');
			            }

					},
					Cancel: function() {
					$(this).dialog('close');
					}
				},
		open: function() {

		},
		close: function() {

		}
		});
		$('#'+id).dialog("open");
}

function validateAuthenticationHandler(handlerName,handlerTable){
	var isAuthenticationHandlerPassed = true;
	if(handlerName == 'AuthenticationHandler'){
		$("."+handlerTable).find('.form_auth').each(function (){
			if($(this).find("input[name='isHandlerEnabled']").val() == "true"){
				alert('You are not allowed to add enabled Authentication Handler twice');
				isAuthenticationHandlerPassed = false;
				return;
			}
		});
	}
	return isAuthenticationHandlerPassed;
}

function validateConcurrencyImdgHanler(handlerName,imdgEnable) {
	if(handlerName == "RadiusIMDGConcurrency" && imdgEnable == false){
		alert("You are not allowed to add Concurrency IMDG Handler as Imdg is Disabled or not Configured");
		return false;
	}else{
		return true;
	}
}

function changeColorOfHandler(handlerName,handlerTable){
	var newRowId=$("#"+handlerTable+" > tbody > tr").length;

	$("#"+handlerTable).find("tr#"+newRowId).find('.tbl-header-bold').css("background-color","#007CC3");
	$("#"+handlerTable).find("tr#"+newRowId).find('.tbl-header-bold').css("border-color","#007CC3");
	$("#"+handlerTable).find("tr#"+newRowId).find('.tbl-header-bold').css("color","white");
	$("#"+handlerTable).find("tr#"+newRowId).find('.editable_icon_bg').css("background-color","rgb(0,124,195)");

	var handlerNameText = $("#"+handlerTable).find("tr#"+newRowId).find('.tbl-header-bold').find('.handler-name-txt');
	$(handlerNameText).css("color","white");
	$(handlerNameText).addClass('new-handler');

	//for delete image
	var path = $("#"+handlerTable).find("tr#"+newRowId).find('.delele_proxy').attr('src');
	var imgObj = $("#"+handlerTable).find("tr#"+newRowId).find('.delele_proxy');

	if(typeof path != undefined){
		if (path.indexOf("delete_proxy") > -1) {
			 path=path.replace("delete_proxy","delete-proxy_white");
		}

	 $(imgObj).attr('src',path);
	}

	//for Expand image
	var expandPath = $("#"+handlerTable).find("tr#"+newRowId).find('.expand_class').attr('src');
	var expandObj = $("#"+handlerTable).find("tr#"+newRowId).find('.expand_class');

	if(typeof expandPath != undefined){
		if (expandPath.indexOf("bottom") > -1) {
			expandPath=expandPath.replace("bottom","bottom_white");
		}

		$(expandObj).attr('src',expandPath);
	}

	var copyPath = $("#"+handlerTable).find("tr#"+newRowId).find('.copy_class');

	if(copyPath.length >= 1){
		var copyPath = $("#"+handlerTable).find("tr#"+newRowId).find('.copy_class').attr('src');
		var copyObj = $("#"+handlerTable).find("tr#"+newRowId).find('.copy_class');

		if( copyPath != 'undefined'){
			if (copyPath.indexOf("copy_proxy") > -1) {
				copyPath=copyPath.replace("copy_proxy","copy_proxy_white");
			}

			$(copyObj).attr('src',copyPath);
		}
	}

	var editIcon = $("#"+handlerTable).find("tr#"+newRowId).find('.edit_handler_icon');
	$(editIcon).addClass('edit_handler_icon_white');

	var saveIcon = $("#"+handlerTable).find("tr#"+newRowId).find('.save_handler_icon');
	$(saveIcon).addClass('save_handler_icon_white');

	$('html, body').animate({
        scrollTop: $("#"+handlerTable).find("tr#"+newRowId).offset().top
    }, 500);

}

function retriveRadiusDictionaryAttributesForUsernameResponse() {
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	var searchNameOrAttributeId='';
	$.post("SearchRadiusAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#,");
		var value;
		var label;
		var desc;
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0].trim();
			label = tmpArray[1];
			var item = new ListItem(value,label);
			myArray.push(item);
		}
		setRadiusDictionaryFieldsForUserNameRes(myArray);
		return dbFieldArray;
	});
}

function setRadiusDictionaryFieldsForUserNameRes(myArray) {
	$( ".userNameResponseAttribs" ).autocomplete({
			source:function( request, response ) {
				response( $.ui.autocomplete.filter(
						myArray, extractLast( request.term ) ) );
			},

			focus: function( event, ui ) {
				return false;
			},
			select: function( event, ui ) {
				var terms = split( this.value );
				terms.pop();
				terms.push( ui.item.value );
				this.value = terms.join( "," );
				return false;
			}
		});
}

function copyHandler(handlerId,handlerName){

	var handlerTable = $('#'+handlerId).find('#handlerTable').val();
	var htmlcontent = '';
	htmlcontent=getNewHandlerData(handlerName,handlerTable);
	var newRowId=$("#"+handlerTable+" > tbody > tr").length;
	newRowId++;
	$('#'+handlerTable).append('<tr id='+newRowId+'><td class="handler-css"> '+htmlcontent+'</td></tr>');
	retriveRadiusDictionaryAttributesForUsernameResponse();

	reAssignValueOfHandler(handlerId,handlerName,newRowId,handlerTable);
	changeColorOfHandler(handlerName,handlerTable);
}

function reAssignValueOfHandler(oldHandlerId,handlerName,newHandlerId,handlerTable){
	if(handlerName == 'ProfileLookupDriver'){
		doAssignProfileHandlerData(oldHandlerId,newHandlerId,handlerTable);
	}
}

function doAssignProfileHandlerData(oldHandlerId,newHandlerId,handlerTable){

	//For Strip Identity
	var stripIdentityValue = $('#'+handlerTable).find('#'+oldHandlerId).find('.stripIdentity').val();
	$('#'+handlerTable).find('#'+newHandlerId).find('.stripIdentity').val(stripIdentityValue);

	//For Seperator
	var seperatorValue = $('#'+handlerTable).find('#'+oldHandlerId).find('.separator').val();
	$('#'+handlerTable).find('#'+newHandlerId).find('.separator').val(seperatorValue);

	//For Select Case
	var selectCaseValue = $('#'+handlerTable).find('#'+oldHandlerId).find('.selectCase').val();
	$('#'+handlerTable).find('#'+newHandlerId).find('.selectCase').val(selectCaseValue);

	//For Trim User Name
	var trimUserIdentityValue =$('#'+handlerTable).find('#'+oldHandlerId).find('.trimUserIdentity').val();

	if ($('#'+handlerTable).find('#'+oldHandlerId).find('.trimUserIdentity').is(':checked')) {
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimUserIdentity').attr("checked", true);
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimUserIdentity').val(trimUserIdentityValue);
	}else{
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimUserIdentity').attr("checked", false);
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimUserIdentity').val(trimUserIdentityValue);
	}

	//For Trim Password
	var trimPasswordValue = $('#'+handlerTable).find('#'+oldHandlerId).find('.trimPassword').val();

	if ($('#'+handlerTable).find('#'+oldHandlerId).find('.trimPassword').is(':checked')) {
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimPassword').attr("checked", true);
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimPassword').val(trimPasswordValue);
	}else{
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimPassword').attr("checked", false);
		$('#'+handlerTable).find('#'+newHandlerId).find('.trimPassword').val(trimPasswordValue);
	}

	//For Anonymous Profile Identity
	var anonymousProfileIdentityValue =$('#'+handlerTable).find('#'+oldHandlerId).find('.anonymousProfileIdentity').val();
	$('#'+handlerTable).find('#'+newHandlerId).find('.anonymousProfileIdentity').val(anonymousProfileIdentityValue);

	//For Primary Group Server
	$('#'+handlerTable).find('#'+oldHandlerId).find('.selecteddriverIds option').each(function(){
		$('#'+handlerTable).find('#'+newHandlerId).find('.selecteddriverIds').append($(this).clone());
	});

	//For Secondary Group Server
	$('#'+handlerTable).find('#'+oldHandlerId).find('.selectedCacheDriverIds option').each(function(){
		$('#'+handlerTable).find('#'+newHandlerId).find('.selectedCacheDriverIds').append($(this).clone());
	});

	//For Additional Group Server
	$('#'+handlerTable).find('#'+oldHandlerId).find('.selectedAdditionalDriverIds option').each(function(){
		$('#'+handlerTable).find('#'+newHandlerId).find('.selectedAdditionalDriverIds').append($(this).clone());
	});

	//For Driver Script
	var driverScriptValue = $('#'+handlerTable).find('#'+oldHandlerId).find('.driverScript').val();
	$('#'+handlerTable).find('#'+newHandlerId).find('.driverScript').val(driverScriptValue);
}

function deleteHandler(handlerId){
	var deleteResult = confirm('Are you sure you want to delete this item?');
	if(deleteResult){
		var $div = $(handlerId).closest('table[class^="handler-class"]');
		$($div).parent().parent().remove();
	}
}

function expandCollapse(tdObj){
	var $img=$(tdObj).find('img');
	var tableObj=$(tdObj).closest('table[class^="handler-class"]');


	var divObj=$(tableObj).find('div[class^="toggleDivs"]');

	$(divObj).slideToggle();

	 var path=$($img).attr('src');

	 if (path.indexOf("bottom") > -1) {
		 path=path.replace("bottom","up");
	 }else{
		 path=path.replace("up","bottom");
	 }

	$($img).attr('src',path);
}


var fixHelper = function(e, ui) {
	ui.children().each(function() {
		$(this).width($(this).width());
	});
	return ui;
};

function getContextPath() {
	   return window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}
function getNewHandlerData(handlerName,handlerTable){
	var widgetPath=getContextPath()+"/jsp/servicepolicy/radiusservicepolicy/"+handlerPagePath[handlerName];
	var isAdditional=checkIsAdditional(handlerTable);
	var currentObj=(handlerDetails[handlerTable]);
	var formNextId=parseFloat((currentObj[handlerName])) +1;

	$.each(handlerDetails[handlerTable], function(key, value) {
	    if(key === handlerName){
	    	delete handlerDetails[handlerTable[handlerName]];
	    	handlerDetails[handlerTable][handlerName] = formNextId ;
	    }
	});
	var widgetNextId=formNextId;
	var response='';
	var orderNumber=$('#'+handlerTable+'>tbody >tr').length;
	orderNumber = orderNumber +1;
    $.ajax({url:widgetPath,
          type:'GET',
          cache:false,
          data:'widgetId='+widgetNextId+'&isAdditional='+isAdditional+'&orderNumber='+orderNumber+'&handlerTable='+handlerTable,
          async:false,
          success: function(transport){
             response=transport;
         }
    });
    if(response!=null){
    	return response;
    }else{
    	return null;
    }
}
function checkIsAdditional(handlerTable){
	if(handlerTable == 'additionalTbl'){
		return true;
	}else{
		return false;
	}
}

function loadDefaultServiceHandler(){
	for(var i=0;i<authdefaultHandler.length;i++){
		if(authdefaultHandler[i] == 'ConcurrencyHandler'){
			 var sessionManagerIds = $('#sessionManagerId').val();
        	 if(sessionManagerIds){
        		 if(sessionManagerIds > 0){
            		 addNewHandler(authdefaultHandler[i],'authenticationTbl',true,false);
            	 }
        	 }
		}else{
			 addNewHandler(authdefaultHandler[i],'authenticationTbl',true,false);
		}
	}
	for(var i=0;i<authAdditional.length;i++){

		if(authAdditional[i] == 'ConcurrencyHandler'){
			 var sessionManagerIds = $('#sessionManagerId').val();
	       	 if(sessionManagerIds){
	       		 if(sessionManagerIds > 0){
	           		 addNewHandler(authAdditional[i],'additionalTbl',true,false);
	           	 }
	       	 }
		}else{
			 addNewHandler(authAdditional[i],'additionalTbl',true,false);
		}
	}
}


function deleteMe(currentObj){
	$(this).parent().parent().remove();
}

function addPluginMapping(mappingTable, templateTable ){
	var tableRowStr = $("." + templateTable).find("tr");
	$("." + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
}