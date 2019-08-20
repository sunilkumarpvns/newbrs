var handlerDetails= {"accountingTbl":{
							"ProfileLookupDriver":0,
							"CDRGeneration":0,
							"ProxyCommunication":0,
							"BroadcastingCommunication":0,
							"Plugin":0,
							"coaDMGen":0,
    						"StatefulProxyCommunication":0,
    						"StatefulProxyBroadcastCommunication":0},
					 "additionalTbl":{
						    "ProfileLookupDriver":0,
							"CDRGeneration":0,
							"ProxyCommunication":0,
							"BroadcastingCommunication":0,
							"Plugin":0,
							"coaDMGen":0,
                         	"StatefulProxyCommunication":0,
                         	"StatefulProxyBroadcastCommunication":0
					  }
					};

var handlerPagePath = {
						"ProfileLookupDriver":"ViewProfileLookupDriver.jsp",
						"CDRGeneration":"ViewCDRGeneration.jsp",
						"ProxyCommunication":"ViewAcctProxyCommunication.jsp",
						"BroadcastingCommunication":"ViewAcctBroadcastCommunication.jsp",
						"Plugin":"ViewPlugin.jsp",
						"coaDMGen":"ViewCOADMGeneration.jsp",
    					"StatefulProxyCommunication":"ViewStatefulProxySequentialHandler.jsp",
    					"StatefulProxyBroadcastCommunication":"ViewStatefulProxyBroadcastHandler.jsp"};

var acctdefaultHandler = [];
var acctAdditional = [];

function changeColorOfHandler(handlerName,handlerTable){
	var newRowId=$("#"+handlerTable+" > tbody > tr").length;
	$("#"+handlerTable).find("tr#"+newRowId).find('.tbl-header-bold').css("background-color","#007CC3");
	$("#"+handlerTable).find("tr#"+newRowId).find('.tbl-header-bold').css("border-color","#007CC3");
	$("#"+handlerTable).find("tr#"+newRowId).find('.tbl-header-bold').css("color","white");
	
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
	
	
	var copyPath =$("#"+handlerTable).find("tr#"+newRowId).find('.copy_class');
	
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
	
	$('html, body').animate({
        scrollTop: $("#"+handlerTable).find("tr#"+newRowId).offset().top
    }, 500);
	
}

function copyHandler(handlerId,handlerName){
	alert('jjj');
	var handlerTable = $('#'+handlerId).find('#handlerTable').val();
	var htmlcontent = '';
	htmlcontent=getNewHandlerData(handlerName,handlerTable);
	var newRowId=$("#"+handlerTable+" > tbody > tr").length;
	newRowId++;
	$('#'+handlerTable).append('<tr id='+newRowId+'><td class="handler-css"> '+htmlcontent+'</td></tr>');
	
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
	for(var i=0;i<acctdefaultHandler.length;i++){
		addNewHandler(acctdefaultHandler[i],'accountingTbl',false,true);
	}
	for(var i=0;i<acctAdditional.length;i++){
		addNewHandler(acctAdditional[i],'additionalTbl',false,true);
	}	
}

var profileLookUpDriverDetails = [];
var pluginHandlerDetails = [];
var proxyCommunicationHandler = [];
var broadcastCommunicationHandler = [];
var cdrGenerationHandler = [];

function setProfileLookupDriver(profileLookUpDriverDetails){
	this.profileLookUpDriverDetails = profileLookUpDriverDetails;
	
	$.each(profileLookUpDriverDetails, function(key,value) {
	  addDefaultHandler('ProfileLookupDriver', 'accountingTbl',value);
	});
}

function setPluginHandler(pluginHandler){
	this.pluginHandlerDetails=pluginHandler;
	$.each(pluginHandler, function(key,value) {
		
		var jsonData=JSON.stringify(value);
		var tableName = 'accountingTbl';
		$.each($.parseJSON(jsonData), function(key,value){
			if(key == 'isAdditional'){
				if( value == 'true'){
					tableName = 'additionalTbl';
				}
			}
	});
		  addDefaultHandler('Plugin', tableName ,value);
	});
}
function setProxyCommunicationHandler(proxyCommunicationHandler){
	this.proxyCommunicationHandler = proxyCommunicationHandler;
	$.each(proxyCommunicationHandler, function(key,value) {
		  addDefaultHandler('ProxyCommunication', 'accountingTbl',value);
	});
}
function setBroadcastCommunicationHandler(broadcastCommunicationHandler){
	this.broadcastCommunicationHandler = broadcastCommunicationHandler;
	$.each(broadcastCommunicationHandler, function(key,value) {
		  addDefaultHandler('BroadcastingCommunication', 'accountingTbl',value);
	});
}
function setCDRGenerationHandler(cdrGenerationHandler){
	this.cdrGenerationHandler= cdrGenerationHandler;
	$.each(cdrGenerationHandler, function(key,value) {
		
		var jsonData=JSON.stringify(value);
		var tableName = 'accountingTbl';
		$.each($.parseJSON(jsonData), function(key,value){
			if(key == 'isAdditional'){
				if( value == 'true'){
					tableName = 'additionalTbl';
				}
			}
		});	
		
		  addDefaultHandler('CDRGeneration', tableName ,value);
	});
}

function setCoaDMGenerationHandler(coaDMGEnerationHandler){
	$.each(coaDMGEnerationHandler, function(key,value) {
		
		var jsonData=JSON.stringify(value);
		var tableName = 'accountingTbl';
		$.each($.parseJSON(jsonData), function(key,value){
			if(key == 'isAdditional'){
				if( value == 'true'){
					tableName = 'additionalTbl';
				}
			}
		});
		
		addDefaultHandler('coaDMGen',tableName,value);
	});
}

function setStatefulProxySequentialHandler(statefulProxySequentialHandler){
    $.each(statefulProxySequentialHandler, function (key,value) {
        addDefaultHandler('StatefulProxyCommunication','accountingTbl',value)
    });
}

function setStatefulProxyBroadcastHandler(statefulProxyBroadcastHandler){
    $.each(statefulProxyBroadcastHandler, function (key,value) {
        addDefaultHandler('StatefulProxyBroadcastCommunication','accountingTbl',value)
    });
}

function addDefaultHandler(handlerName,handlerTable,jsonData){
	var htmlcontent = '';
	htmlcontent=getHandlerDataDetails(handlerName,handlerTable,jsonData);
	
	var orderNumber = '';
	
	jsonData=JSON.stringify(jsonData);
	$.each($.parseJSON(jsonData), function(key,value){
		if(key == 'orderNumber'){
			orderNumber = value;
		}
	});
	
	var newRowId=$("#"+handlerTable+" > tbody > tr").length;
	newRowId++;
	
	$('#'+handlerTable).append('<tr id='+orderNumber+'><td class="handler-css"> '+htmlcontent+'</td></tr>');
	var trIds = $('#'+handlerTable +" #"+orderNumber).find('input#orderNumber');
	$(trIds).val(orderNumber);
	
	
	$("#"+handlerTable+" > tbody > tr").sortElements(function(a, b){
	    return $(a).attr('id') > $(b).attr('id') ? 1 : -1;
	});
}

jQuery.fn.sortElements = (function() {
    var sort = [].sort;
    return function(comparator, getSortable) {
        getSortable = getSortable || function() {
            return this;
        };
        var placements = this.map(function() {
            var sortElement = getSortable.call(this),
                parentNode = sortElement.parentNode,
                nextSibling = parentNode.insertBefore(document.createTextNode(''), sortElement.nextSibling);
            return function() {
                if (parentNode === this) {
                    throw new Error("You can't sort elements if any one is a descendant of another.");
                }
                parentNode.insertBefore(this, nextSibling);
                parentNode.removeChild(nextSibling);
            };
        });
        return sort.call(this, comparator).each(function(i) {
            placements[i].call(getSortable.call(this));
        });
    };
})();

function getHandlerDataDetails(handlerName,handlerTable,jsonData){
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
	
	jsonData=JSON.stringify(jsonData);
	jsonData = jsonData.replace(/&quot;/ig, '\\"');
	jsonData = escape(jsonData);
    $.ajax({url:widgetPath,
          type:'POST',
          cache:false,
          data:'widgetId='+widgetNextId+'&isAdditional='+isAdditional+'&orderNumber='+orderNumber+'&jsonData='+jsonData+'&handlerTable='+handlerTable,
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

var filterRuleset = (function() {
	  var element = document.createElement('div');

	  function decodeHTMLEntities (str) {
	    if(str && typeof str === 'string') {
	      str = str.replace(/<script[^>]*>([\S\s]*?)<\/script>/gmi, '');
	      str = str.replace(/<\/?\w(?:[^"'>]|"[^"]*"|'[^']*')*>/gmi, '');
	      element.innerHTML = str;
	      str = element.textContent;
	      element.textContent = '';
	    }

	    return str;
	  }

	  return decodeHTMLEntities;
})();


/************************Serialize form methods***********************************/



$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            var element = $("[name = '"+this.name+"']");
           
			if($(element).length > 0 && $(element).is("select") && $(element).attr("multiple")) {
                 o[this.name] = [this.value || ''];
            } else {
                 o[this.name] = this.value || '';
            }
        } 
    });
    return o;
};


$.fn.serializeCDRGenObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            var element = $("[name = '"+this.name+"']");
           
			if($(element).length > 0 && $(element).is("select") && $(element).attr("multiple")) {
                 o[this.name] = [this.value || ''];
			}else if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "ruleset") {
				 o[this.name] = [this.value || ''];
			}else if($(element).length > 0 && $(element).is("select") && $(element).attr("name") == "primaryDriverId") {
				 o[this.name] = [this.value || ''];
			}else if($(element).length > 0 && $(element).is("select") && $(element).attr("name") == "secondaryDriverId") {
				 o[this.name] = [this.value || ''];
			}else if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "script") {
				 o[this.name] = [this.value || ''];
            }else if($(element).length > 0 && $(element).is("input:checkbox") && $(element).attr("name") == "waitForCDRDump") {
				 o[this.name] = [this.value || ''];
			} else {
                 o[this.name] = this.value || '';
            }
        } 
    });
    return o;
};

$.fn.serializePluginObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            var element = $("[name = '"+this.name+"']");
            
			 if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "ruleset") {
				 o[this.name] = [this.value || ''];
			}else if($(element).length > 0 && $(element).is("select") && $(element).attr("name") == "requestType") {
				 o[this.name] = [this.value || ''];
			}else if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "plugin") {
				 o[this.name] = [this.value || ''];
			} else {
                 o[this.name] = this.value || '';
            }
        } 
    });
    return o;
};

$.fn.serializeCoaDMGenerationObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            var element = $("[name = '"+this.name+"']");
            
			 if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "attributeIds") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "dbFields") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "defaultValue") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("select") && $(element).attr("name") == "mandatory") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "ruleset") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("select") && $(element).attr("name") == "server") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("select") && $(element).attr("name") == "translationMapping") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("input") && $(element).attr("name") == "script") {
				 o[this.name] = [this.value || ''];
			 }else if($(element).length > 0 && $(element).is("input:checkbox") && $(element).attr("name") == "acceptOnTimeout") {
				 o[this.name] = [this.value || ''];
			 } else {
                 o[this.name] = this.value || '';
             }
        } 
    });
    return o;
};

function setPluginDatas(pluginDataList, mappingTable, templateTable){
	
	var tableRowStr = $("." + templateTable).find("tr");
	
	$.each(pluginDataList, function(key,value) {
		console.log('setData  : ' +value);
		$("." + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
		  
		  var pluginName = $("." + mappingTable).find(' > tbody > tr ').last().find("input[name='pluginName']");
		  $(pluginName).val(value.pluginName);

		  var pluginArgument = $("." + mappingTable).find(' > tbody > tr ').last().find("textarea[name='pluginArgument']");
		  $(pluginArgument).val(value.pluginArgument);
		  
	});
}
