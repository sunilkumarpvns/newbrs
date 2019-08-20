/**
 * This js file contains the functions for major client side validations and Common Functions
 * Created Date : 22 Jan 2016 
 * Author       : Nayana Rathod
 * 
 */

var tabId;
var proxyESIServerData;
var pluginList = [];
var applicationIdList = [];
var diaAVPAutoComplete = [];//Diameter AVP list for Command Code Wise Response Attribute

/*Stores reference of the object whom you want to focus after the command code flow div is triggered*/
var focusObject;
var handlerDetails = {"commandcode-flow-table":{
								"ProfileLookupDriver" : 0,
								"AuthenticationHandler" : 0,
								"AuthorizationHandler" : 0,
								"CDRHandler" : 0,
								"DiameterProxyCommunication" : 0,
								"RadiusProxyCommunication" : 0,
								"DiameterBroadcastingCommunication" : 0,
								"BroadcastingCommunication" : 0,
								"RadiusBroadcastingCommunication" : 0,
								"Plugin" : 0,
								"ConcurrencyHandler" : 0,
    							"RadiusIMDGConcurrency":0
						      }
	                      };                     
	
	var handlerPagePath = {"ProfileLookupDriver" : "ProfileLookupDriver.jsp",
							"AuthenticationHandler" : "AuthenticationHandler.jsp",
							"AuthorizationHandler" : "AuthorizationHandler.jsp",
							"CDRHandler" : "CDRHandler.jsp",
							"DiameterProxyCommunication" : "DiameterProxyCommunication.jsp",
							"RadiusProxyCommunication" : "RadiusProxyCommunication.jsp",
							"DiameterBroadcastingCommunication" : "DiameterBroadcastCommunication.jsp",
							"RadiusBroadcastingCommunication" : "RadiusBroadcastCommunication.jsp",
							"Plugin" : "PluginHandler.jsp",
							"ConcurrencyHandler" : "ConcurrencyHandler.jsp",
        					"RadiusIMDGConcurrency":"ConcurrencyIMDGHandler.jsp"
	                      };
	
	var authdefaultHandler = [ 'AuthenticationHandler', 'AuthorizationHandler','ProfileLookupDriver', 'ConcurrencyHandler', 'ProxyCommunication' ];
	var authAdditional = [];
	
	var handlerArray = [];
	var handlerType = [ 'Authentication', 'AuthAdditional', 'Accounting','AcctAdditional' ];
	
/**
 * Get diameter attributes avp values
 * @param attrName Attribute name
 */
function setDiameterDicAttr(attrName){
	var dicAttr = document.getElementById(attrName).value;
	retriveRadiusDictionaryAttributes(dicAttr,attrName);
}

/** This function deletes command code flow tab */
$(".nav-tabs").on("click", "span", function() {
	var result = confirm("Are you sure you want to remove ?");
	if (result == false) {
	    return;
	}
	
	var id = $(this).parent().children('a').attr('href');
	$(id).remove();
	$(this).parent().remove();
	$(".nav-tabs li").children('a').first().click();

});

/** This function edit command code flow tab */
$(".nav-tabs").on("click", "div", function() {
	var className = $(this).find("i").attr('class');
	var uniqueId = $(this).attr("id");
	if(className.indexOf("fa-pencil") > -1){
		$('#tab_'+uniqueId).hide();
		$('#txt_'+uniqueId).show();
		$('#txt_'+uniqueId).focus();
		var tmpStr = $('#txt_'+uniqueId).val();
		$('#txt_'+uniqueId).val('');
		$('#txt_'+uniqueId).val(tmpStr);
		
		var editObj = $(this).find('.fa-pencil');
		editObj.addClass("fa-save");
		editObj.removeClass("fa-pencil");
	}else{
		saveEditedTabValue(uniqueId,this);
	}
});

/** This function checks max command code limit */
function maxCommandCodeLimit() {
	if($('#totalCommandCodeLimit').val()!='null' && parseInt($(".nav-tabs").children().length) > 0){
		if((parseInt($(".nav-tabs").children().length))-1 >= parseInt($('#totalCommandCodeLimit').val())){
			alert('System will not allow to create more than '+$('#totalCommandCodeLimit').val()+' Command Code flow');
			return false;
		}
	}
	return true;
};

/** This function adds the tab */
$('#add-command-code').on("click", function() {
	var display_label = $('#displaylabel').val();
	if(!validateValues(display_label,'Label Name')){
		$('#displaylabel').focus();
		return;
	}
	
	var commandCode =  $('#command_code_txt').val();
	if(!validateValues(commandCode,'Command Code')){
		$('#command_code_txt').focus();
		return;
	}
	
	var interfaceId = $('#interface').val();
	if(!validateValues(interfaceId,'Interface')){
		$('#interface').focus();
		return;
	}
	$("#btns").show();
	var uniqueId;
	
	var isValidName = true;
	for (var i = 0, len = display_label.length; i < len; i++) {
		  var ascii = display_label[i].charCodeAt();

		  if(ascii == 44 || ascii == 45 || ascii == 46 || ascii == 95 || (ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122) || (ascii >= 48 && ascii <= 57)){
			  continue;
		  }else{
			  isValidName = false;
			  break;
		  }
	}
	
	if(!isValidName){
		alert('Not a valid Name. Valid Characters : A-Z, a-z, 0-9, ., -, _ ');
		$('#displaylabel').focus();
		return;
	}
	var new_display_label = display_label.replace(/,/g, '_');
	new_display_label = new_display_label.replace(/\./g, '_');
	new_display_label = new_display_label.replace(/-/g, '_');
	
	var id = $(".nav-tabs").children().length;
	
	var uuId = createUUID();
	uniqueId = new_display_label + "_" + uuId;
	
	$('.nav-tabs li:last-child').before(' <li><a href="#'+new_display_label+'" role="tab" data-toggle="tab"><label id="tab_'+ uniqueId +'">' + display_label + '</label><input id="txt_'+ uniqueId +'" onkeyup="expand(this);" class="ccf-tab" />&nbsp; &nbsp; &nbsp; &nbsp;</a> <div id='+uniqueId+' > <i class="fa fa-pencil"></i> </div> &nbsp; &nbsp; &nbsp; <span> <i class="fa fa-trash-o"></i> </span> &nbsp;</li>');
	$('.tab-content').append('<div class="tab-pane fade" id="'+new_display_label+'"><table cellspacing="0" cellpadding="0" border="0" width="100%" class="service_handler_table" id="servicehandlertable_'+ uniqueId +'">'+ $('#service_handler_table').html() +'</table></div>');

	$('#txt_'+uniqueId).val(display_label);
	$('#txt_'+uniqueId).hide();
	$('#txt_'+uniqueId).attr("size",display_label.length);
	
	$('#servicehandlertable_'+uniqueId).find('.uniqueUUID').val(uuId);
	$('#servicehandlertable_'+uniqueId).find('.commandCode').attr('id','commandCode_'+uuId);
	$('#servicehandlertable_'+uniqueId).find('.interface').attr('id','interface_'+uuId);
	$('#servicehandlertable_'+uniqueId).find('.displayName').attr('id','displayName_'+uuId);
	$('#servicehandlertable_'+uniqueId).find('.commandcode-flow-table').attr('id','commandcode-flow-table_'+uuId);
	
	$('#displaylabel').val('');
	$('#command_code_txt').val('');
	$('#interface').val('');
	
	$('#servicehandlertable_'+uniqueId).find('.commandCode').val(commandCode);
	$('#servicehandlertable_'+uniqueId).find('.interface').val(interfaceId);
	$('#servicehandlertable_'+uniqueId).find('.displayName').val(display_label);
	
	$('.nav-tabs li:nth-child(' + id + ') a').click(function(){
		setFocusAfterTrigger();
	});
	
	$('.nav-tabs li:nth-child(' + id + ') a').click();
});

/** This function will validate value that can not be empty */
function validateValues(value,fieldName) {
	if(value.trim() == ''){
		alert(fieldName + ' cannot be empty');
		return false;
	}
	return true;
}

/** This function is used for adding service handler into a specific flow */
function addServiceHandler(obj){
	var handlerTable = $(obj).closest('table.service_handler_table');
	var id="addServiceHandlerPopup";
	
	$('#' + id).dialog({
		modal : true,
		autoOpen : false,
		minHeight : 200,
		height : 300,
		width : 495,
		position: 'top',
		buttons : {
			'Add' : function() {
				
				var selectedItems = $('#'+id).find("input[type='radio'][name='servicehandler']:checked");
		        var handlerName=$(selectedItems).val();
		        
		        var isAuthenticationHandlerMultiple=validateMultipleAuthenticationHandler(handlerName,handlerTable);
		        if(handlerName == "ConcurrencyHandler" && $("#sessionManagement").val() == "false"){
		        	alert("Concurrency will never reach as session management is disable in 3GPP service policy.");
	        	  }
		        if(isAuthenticationHandlerMultiple){
		        	if(!(isEmpty(handlerName)) && handlerName != null && handlerName.length > 0){
		        		addNewHandlerToFlow(handlerName,handlerTable);
		        	}
		        	$(this).dialog('close');
		        }
		        
		        setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
				
			},
			Cancel : function() {
				$(this).dialog('close');
			}
		},
		open  : function() {},
		close : function() {}
	});
	
	$('#'+id).dialog("open");
}

/** This function is used for adding handler into a post response handler
 *  @param obj current object of service handler */
function addPostResponseServiceHandler(obj){
	var handlerTable = $(obj).closest('table.service_handler_table');
	
	var id="addPostServiceHandlerPopup";

	$('#' + id).dialog({
		modal : true,
		autoOpen : false,
		minHeight : 200,
		height : 150,
		width : 495,
		position: 'top',
		buttons : {
			'Add' : function() {
				
				var selectedItems = $('#'+id).find("input[type='radio'][name='servicehandler']:checked");
		        var handlerName=$(selectedItems).val();
		        
		        	if(!(isEmpty(handlerName)) && handlerName != null && handlerName.length > 0){
		        		addPostResponseNewHandlerToFlow(handlerName,handlerTable);
		        }
		        
				$(this).dialog('close');
			},
			Cancel : function() {
				$(this).dialog('close');
			}
		},
		open  : function() {},
		close : function() {}
	});
	
	$('#'+id).dialog("open");
}

/**
 * This function is used to provide validation for adding only one authentication handler in the service flow.
 * @param handlerName Handler Name
 * @param handlerTable Handler table
 * @returns false if more then one authentication handler found else true.
 */
function validateMultipleAuthenticationHandler(handlerName,handlerTable){
	var isAuthenticationHandlerMultiple = true;
	 if(handlerName == 'AuthenticationHandler'){
		 $(handlerTable).find('.form_auth').each(function (){
			 if($(this).find("input[name='isHandlerEnabled']").val() == "true"){
				 alert('You are not allowed to add enabled Authentication Handler twice');
				 isAuthenticationHandlerMultiple = false;
				 return false;
			 }
		 });
	}
	return isAuthenticationHandlerMultiple;
}

/** This function is used for adding new handler to flow 
 *  @param handlerName Handler Name
 *  @param handlerTable Handler table */
function addNewHandlerToFlow(handlerName,handlerTable){
	var htmlcontent = '';
	htmlcontent=getNewHandlerData(handlerName,handlerTable);
	
	var newRowId =$(handlerTable).find('.commandcode-flow-table > tbody > tr').length;
	newRowId++;
	$(handlerTable).find('.commandcode-flow-table').append('<tr id='+newRowId+'><td class="handler-css">'+htmlcontent+'</td></tr>');
	$(handlerTable).find("#"+newRowId).find('.tbl-header-bold').css({"background-color":"#007CC3","border-color":"#007CC3","color":"white"});
	var editHandlerElement = $(handlerTable).find("#"+newRowId).find('.edit_handler_icon');
	editHandlerElement.removeClass('edit_handler_icon');
	editHandlerElement.addClass('edit_handler_icon_white');

	var deleteButton = $(handlerTable).find("#"+newRowId).find(".delele_proxy"); 
	var src = $(deleteButton).attr("src");
	src = src.replace("_proxy","-proxy_white");
	$(deleteButton).attr("src",src);
	
	var expandButton = $(handlerTable).find("#"+newRowId).find(".expand_class");
	var src = $(expandButton).attr("src");
	src = src.replace("bottom","bottom_white");
	$(expandButton).attr("src",src);
	var textboxObj = $(handlerTable).find('.commandcode-flow-table > tbody > tr:last-child').find('.handler-name-txt');
	var textboxVal = $(textboxObj).val();
	
	var handlerCounter = 0;
	var handlerNumbers = [];
	var tableId = handlerTable.attr("id");
	$('#'+tableId + " input:hidden[name='handlerJsName']").each( function() {
		if($(this).val() == handlerName){
			var name = $(this).parent().find("input[name='hiddenHandlerName']").val();
			
			var startIndex = name.lastIndexOf("(");
			var endIndex = name.lastIndexOf(")");
			
			if(startIndex > -1 && endIndex > -1 && startIndex+1 != endIndex){
				var number = name.substring(startIndex+1,endIndex);

				if(isNaN(number) == false){
					handlerNumbers.push(parseInt(number));
				} else {
					handlerNumbers.push(1);
				}
			} else if(name == textboxVal){
				handlerNumbers.push(1);
			}
		}
	});
	
	if(handlerNumbers.length == 1){
		handlerCounter = 1;
	}else{
		var maxNumber = Math.max.apply(Math, handlerNumbers);
		handlerCounter = ++maxNumber;
	}
		
	var hiddenObj = $(handlerTable).find('.commandcode-flow-table > tbody > tr:last-child').find('.hidden-handler-name');
	
	var newValue = textboxVal;
	if( handlerCounter > 1 ){
		newValue += "("+handlerCounter+")";
	}
	
	$(textboxObj).empty();
	$(hiddenObj).empty();
	
	$(textboxObj).attr('size', newValue.length);
	$(handlerTable).find('.commandcode-flow-table > tbody > tr:last-child').find("div.handler-label").text(newValue);
	$(textboxObj).val(newValue);
	$(textboxObj).hide();
	$(hiddenObj).val(newValue);
	
	$('html, body').animate({
        scrollTop: $(".commandcode-flow-table").find("#"+newRowId).offset().top
    }, 500); 
    
	setAutocompleteOnCommandCode();
	setAutocompleteOnInterface();
}

/** This function is used for adding new handler to post response  
 *  @param handlerName Handler Name
 *  @param handlerTable Handler table */
function addPostResponseNewHandlerToFlow(handlerName,handlerTable){
	var htmlcontent = '';
	htmlcontent=getPostResponseNewHandlerData(handlerName,handlerTable);
	
	var newRowId =$(handlerTable).find('.post-response-commandcode-flow-table > tbody > tr').length;
	newRowId++;
	$(handlerTable).find('.post-response-commandcode-flow-table').append('<tr id='+newRowId+'><td class="handler-css">'+htmlcontent+'</td></tr>');

	var textboxObj = $(handlerTable).find('.post-response-commandcode-flow-table > tbody > tr:last-child').find('.handler-name-txt');
	var textboxVal = $(textboxObj).val();
	
	var handlerCounter = 0;
	var handlerNumbers = [];
	var tableId = handlerTable.attr("id");
	
	var postHandler = $("#post-response-commandcode-flow-table"); 
	$(postHandler).find("#"+newRowId).find('.tbl-header-bold').css({"background-color":"#007CC3","border-color":"#007CC3","color":"white"});
	var editHandlerElement = $(postHandler).find("#"+newRowId).find('.edit_handler_icon');
	console.log(editHandlerElement);
	editHandlerElement.removeClass('edit_handler_icon');
	editHandlerElement.addClass('edit_handler_icon_white');

	var deleteButton = $(postHandler).find("#"+newRowId).find(".delele_proxy"); 
	var src = $(deleteButton).attr("src");
	src = src.replace("_proxy","-proxy_white");
	$(deleteButton).attr("src",src);
	
	var expandButton = $(postHandler).find("#"+newRowId).find(".expand_class");
	var src = $(expandButton).attr("src");
	src = src.replace("bottom","bottom_white");
	$(expandButton).attr("src",src);
	
	$('#'+tableId + " input:hidden[name='handlerJsName']").each( function() {
		if($(this).val() == handlerName){
			var name = $(this).parent().find("input[name='hiddenHandlerName']").val();
			
			var startIndex = name.lastIndexOf("(");
			var endIndex = name.lastIndexOf(")");
			
			if(startIndex > -1 && endIndex > -1 && startIndex+1 != endIndex){
				var number = name.substring(startIndex+1,endIndex);

				if(isNaN(number) == false){
					handlerNumbers.push(parseInt(number));
				} else {
					handlerNumbers.push(1);
				}
			} else if(name == textboxVal){
				handlerNumbers.push(1);
			}
		}
	});
	
	if(handlerNumbers.length == 1){
		handlerCounter = 1;
	}else{
		var maxNumber = Math.max.apply(Math, handlerNumbers);
		handlerCounter = ++maxNumber;
	}
	
	var newValue = textboxVal;
	if( handlerCounter > 1 ){
		newValue += "("+handlerCounter+")";
	}
	
	var hiddenObj = $(handlerTable).find('.post-response-commandcode-flow-table > tbody > tr:last-child').find('.hidden-handler-name');
	
	$(textboxObj).empty();
	$(hiddenObj).empty();
	
	$(textboxObj).val(newValue);
	$(hiddenObj).val(newValue);
	$(handlerTable).find('.post-response-commandcode-flow-table > tbody > tr:last-child').find("div.handler-label").text(newValue);
	
	$(textboxObj).attr('size',newValue.length);
	
	$('html, body').animate({
        scrollTop: $(".post-response-commandcode-flow-table").find("#"+newRowId).offset().top
    }, 500); 
}

/** This function is used for retriving new handler HTML for command code flow
 *  @param handlerName Handler Name
 *  @param handlerTable Handler table */
function getNewHandlerData(handlerName,handlerTable){
	var widgetPath=getContextPath()+"/jsp/servicepolicy/diameter/tgppaaapolicy/"+handlerPagePath[handlerName];
	var formNextId=handlerName +"_"+createUUID();
	var isAdditional = false;
	var widgetNextId=formNextId;
	var response='';
	var orderNumber=$(handlerTable).find('.commandcode-flow-table').find('tbody').find('tr').length;
	orderNumber = orderNumber +1;
    $.ajax({url:widgetPath,
          type:'GET',
          cache:false,
          data:'widgetId='+widgetNextId+'&isAdditional='+isAdditional+'&orderNumber='+orderNumber+'&handlerTable='+handlerTable+'&isViewPage='+isViewPage,
          async:false,
          success: function(transport){
             response=transport;
             doSortable(true);
         }
    });
    if(response!=null){
    	return response;
    }else{
    	return null;
    }
}

/** This function is used for retriving new handler HTML for post response command code flow
 *  @param handlerName Handler Name
 *  @param handlerTable Handler table */
function getPostResponseNewHandlerData(handlerName,handlerTable){
	var widgetPath=getContextPath()+"/jsp/servicepolicy/diameter/tgppaaapolicy/"+handlerPagePath[handlerName];
	
	var formNextId=handlerName +"_"+createUUID();
	var isAdditional = false;
	var widgetNextId=formNextId;
	var response='';
	var orderNumber=$(handlerTable).find('.post-response-commandcode-flow-table').find('tbody').find('tr').length;
	orderNumber = orderNumber +1;
    $.ajax({url:widgetPath,
          type:'GET',
          cache:false,
          data:'widgetId='+widgetNextId+'&isAdditional='+isAdditional+'&orderNumber='+orderNumber+'&handlerTable='+handlerTable+'&isViewPage='+isViewPage,
          async:false,
          success: function(transport){
             response=transport;
             doSortable(true);
         }
    });
    if(response!=null){
    	return response;
    }else{
    	return null;
    }
}

/** This function is used for applying sortable in all table
 *  @param isNewHandler  */
function doSortable(isNewHandler){
	var obj = '.service_handler_table tbody.parent';
	
	if(isNewHandler){
		obj = '.active ' + obj;
	}

	$(obj).sortable({
		placeholder: 'ui-state-highlight',
		helper: fixHelper,
	 	update: function(event, ui) {
		},start: function(e, ui){
			var height = ui.item.height();
			ui.placeholder.height(ui.item.height());
			height =  height - 10;
		    ui.placeholder.html("<div style='border:1px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;margin-right:0px;color:gray;'>Drag & Drop Handler</div>");
	    }
	});
}

/**
 * This function triggers click event of anchor
 * @param id of div that contains the form
 */
function triggerTab(id) {
	id = '#'+id;
	$(".nav-tabs li").each(function() {
		  var href = $(this).children('a').attr('href');
		  if(href == id){
			  $(this).children('a').click();
		  }
	});
}

/**
 * Validates command code flow before submitting it
 * @returns {Boolean}
 */
function validateForm(){
	selectAll();
	
	validateNoOfHandler();
	
	if(validateAuthenticationHandler() == false){
		return false; 
	}else if(validateProfileLookupHandler() == false){
		return false;
	}else if(validateProxyCommunicationHandler() == false){
		return false;
	}else if(validateBroadCastHandler() == false){
		return false; 
	}else if(validatePluginHandler() == false){
		return false;
	}else if(validateHandlerName() == false){
		return false;
	}else if(validateCommandCodeAndInterface() == false ){
		return false;
	}else if(validateCDRHandler() == false ){
		return false;
	}else if (validateConcurrenyHandler() == false) {
		 return false;   
	}else{
		
		var isAllTabValSaved = true;
		$( ".nav-tabs > li" ).each(function() {
			var isEditable = $(this).find("input").css("display");
			if(isEditable == 'inline'){
				var obj = $(this).closest('li').find('div');
				var uniqueId = obj.attr('id');
				isAllTabValSaved = saveEditedTabValue(uniqueId,obj);
				if(!isAllTabValSaved){
					return false;
				}
			}
		});
		if(!isAllTabValSaved){
			return false;
		}
		
		var tgppPolicyCommandCodeList = [];
		$( ".tab-content > div" ).each(function() {
			
			if($(this).attr('id') !== 'add_command_code'){
				
				var handlerList = [];
				var serviceHandlerTableObj =  $(this).find('.service_handler_table');
				var commandCode = serviceHandlerTableObj.find('input[name=commandCode]').val();
				var interfaceId = serviceHandlerTableObj.find('input[name=interface]').val();
				var name = serviceHandlerTableObj.find('input:hidden[name=displayName]').val();
				
				handlerList =  fetchHandlerData(serviceHandlerTableObj);
				postResponseHandlerList = fetchPostResponseHandlerData(serviceHandlerTableObj);
				
				tgppPolicyCommandCodeList.push({
					'commandCode' : commandCode,
					'interfaceId' : interfaceId,
					'name' : name,
					'handlerList' : handlerList,
					'postResponseHandlerList':postResponseHandlerList
				});
				
			}
		});
		
		$('#tgppCommandCodeJSON').val(JSON.stringify(tgppPolicyCommandCodeList));
		document.forms['tgppForm'].submit();
	}
}
/**
 * This function use for validate concurrency Handler Data
 */
function validateConcurrenyHandler() {
	var valueToReturn = true;
	$('.form_concurrency').each(function() {
		var concurrency_handlerTable = $(this).find('.tblconcurrency_handler');
		var divId = $(this).closest('div').attr('id');
		var diaConcurrencyObj = $(concurrency_handlerTable).find("select[name='diaConConfigId']");

		if ($(diaConcurrencyObj).val() == "0") {
			alert('Please select Diameter Concurrency policy from Concurrency Handler');
			focusObject=$(diaConcurrencyObj)[0];
			$(diaConcurrencyObj).addClass("validateDiaconcurrency");
			triggerTab(divId);
			valueToReturn = false;
			return false;
		}
	});
	return valueToReturn;
}
/**
 * Validates authentication handler in command code flow
 * @returns {Boolean}
 */
function validateAuthenticationHandler(){
	var valueToReturn = true;
	$('.form_auth').each(function(){
		var divId = $(this).closest('div').attr('id');
		var isMethodSelected = false;
		var authHandlerTable = $(this).find('.authHandlerClass');
		$(authHandlerTable).find('input:checkbox[name="supportedAuthenticationMethods"]').each(function(){
			if($(this).is(':checked') == true){
				isMethodSelected = true;
				if($(this).val() == "3"){
					var eapConfObj = $(authHandlerTable).find("select[name='eapConfigId']");
					if($(eapConfObj).val() == "0"){
						alert('Please select EAP Configuration from Authentication Handler');
						focusObject = eapConfObj;
						triggerTab(divId);
						valueToReturn = false;
						return false;
					}
				}
			}
		});

		if( !isMethodSelected ){
			focusObject = $(authHandlerTable).find('input:checkbox[name^=supportedAuthenticationMethods]')[0];
			alert('At least one supported authentication method is mandatory');
			valueToReturn = false;
			triggerTab(divId);
			return false;
		}

		var userName = $(this).find('.advanced-username');
		var userNameExpression = $(this).find('.userNameExpression');

		if( $(userName).val() == 'Advanced' && isNull($(userNameExpression).val())){
			alert('Advanced Username Expression must be specified');
			$(userNameExpression).focus();
			valueToReturn = false;
			return false;
		} 

	});
	return valueToReturn;
}

/** This function is used for validating proxy communication handler */
function validateProxyCommunicationHandler(){
	return proxyAndBrocastCommValidation('form_proxycommunication',
			'RadiusProxyCommunication','proxyCommunicationTbl',
			'Atleast one Mapping is Required in Proxy Communication Handler','proxy-com-btn');
}

/** This function is used for validating broadcast communication handler */
function validateBroadCastHandler(){
	return proxyAndBrocastCommValidation('form_Broadcastcommunication',
			'RadiusBroadcastingCommunication','broadcastCommunicationTbl',
			'Atleast one Mapping is Required in Broadcasting(Parallel) Communication Handler','broadcast-com-btn');
}

/** This function is used for validating profile lookup communication handler */
function validateProfileLookupHandler(){
	var valueToReturn = true;
	$('.form_profilelookup').each(function(){
		var divId = $(this).closest('div').attr('id');
		var selectbox= $(this).find("select[name='selecteddriverIds']");
		var addButton = $(this).find('.driver-popup');
		
		if($(selectbox).val() == null){
			alert('Select at least one primary driver for policy');
			focusObject=addButton;
			valueToReturn = false;
			triggerTab(divId);
			return false;
		}
	});
	return valueToReturn;
}


/** This function is used for selecting all multiple element of select */
function selectAll(){
	
	$('.form_profilelookup').each(function(){
		 var selecteddriverIds = $("select[name = 'selecteddriverIds']");
		 $(selecteddriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
		 
		 var selectedCacheDriverIds = $("select[name = 'selectedCacheDriverIds']");
		 $(selectedCacheDriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
         
		 var selectedAdditionalDriverIds = $("select[name = 'selectedAdditionalDriverIds']");
		 $(selectedAdditionalDriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
	});
}

/** This function is used for validating plugin handler */
function validatePluginHandler(){
    var noValueConfigured = false;
    var isValueConfigured = false;
    var valueToReturn = true;
    var isErrorFound = false;
    $('.form_pluginHandler').each(function(){
        var handlerData = handlerTableInfoJson(this,'mappingtblplugin');
		var divId = handlerData['divId'];
		noValueConfigured= handlerData['noValueConfigured'];
		isValueConfigured = handlerData['isValueConfigured'];
		var pluginTableObj = handlerData['tableObj'];
		var pluginMaintableObj = $(this).find('.tblmPlugin');

        if(noValueConfigured){
        	$(this).children('table').css({'border':'1px solid red'});
        	valueToReturn = false;
        	$(pluginMaintableObj).focus();
        	return manageNoValConfigured('Atleast one mapping is required in Plugin Handler',this,'light-btn');
        }else if(isValueConfigured){
        	valueToReturn=validatePluginEntry(this,divId);
        }
    });
    if(isErrorFound){
    	return false;
    }
    return valueToReturn;
}

/** This function is used for validating plugin enrty */
function validatePluginEntry(obj,div_id){
		$(obj).find("input[name='pluginName']").each(function(){
	        	var pluginValue = $(this).val();
	        	if( pluginValue.length > 0 ){
	        		valueToReturn = true;
	        	}else{
	        		alert('Plugin Name must be specified');
	        		focusObject=this;
	        		triggerTab(div_id);
	        		valueToReturn=false;
	        		return false;
	        	}
		});
	return valueToReturn;
}

/** This function will fetches all handler data from DOM */
function fetchHandlerData( serviceHandlerTableObj ){
	var handlerList =[];
	var commandCodeTable = serviceHandlerTableObj.find('.commandcode-flow-table');
	var counter = 0;
	
	commandCodeTable.find('tr').each(function(){
	
		var handlerData = {};
		var handlerType = $(this).find('input:hidden[name=handlerType]').val();
		
		if( handlerType == 'DiameterSubscriberProfileRepositoryDetails'){
			
			selectAll();
			var formObject = $(this).find('.form_profilelookup');
			handlerData = JSON.stringify(fetchSubscriberProfileRepositoryDetails(formObject));
			handlerList.push({counter:handlerData});
			
		}else if(handlerType == 'DiameterAuthenticationHandlerData'){
			
			var formObject = $(this).find('.form_auth');
			handlerData = JSON.stringify(fetchAuthenticationHandlerData(formObject));
			handlerList.push({counter:handlerData});
			
		}else if( handlerType == 'DiameterAuthorizationHandlerData'){
			
			var formObject = $(this).find('.form_authorization');
			handlerData = JSON.stringify(fetchAuthorizationHandlerData(formObject));
			handlerList.push({counter:handlerData});
			
		}else if( handlerType == 'DiameterPluginHandlerData'){
			
			var formObject = $(this).find('.form_pluginHandler');
			handlerData = JSON.stringify(fetchPluginData(formObject));
			handlerList.push({counter:handlerData});
			
		}else if( handlerType == 'DiameterSynchronousCommunicationHandlerData'){
			
			var formObject = $(this).find('.form_proxycommunication');
			handlerData = JSON.stringify(fetchProxyCommunicationData(formObject));
			handlerList.push({counter:handlerData});
			
			
		}else if( handlerType == 'DiameterBroadcastCommunicationHandlerData'){
			
			var formObject = $(this).find('.form_broadcastcommunication');
			handlerData = JSON.stringify(fetchBroadcastCommunicationData(formObject));
			handlerList.push({counter:handlerData});
			
		}else if( handlerType == 'DiameterCDRGenerationHandlerData'){
			
			var formObject = $(this).find('.form_cdrHandler');
			handlerData = JSON.stringify(fetchCDRHandlerData(formObject));
			handlerList.push({counter:handlerData});
	
		}else if (handlerType == 'DiameterConcurrencyHandlerData') {
          	
  			var formObject = $(this).find('.form_concurrency');
  			handlerData = JSON.stringify(fetchConcurrencyHandlerData(formObject));
  			handlerList.push({counter : handlerData});
		}else{
			handlerData ={};
		}
		counter++;
	});
	
	return handlerList;
}  

/* This function will fetch post response handler data */
function fetchPostResponseHandlerData( serviceHandlerTableObj ){
	var handlerList =[];
	var commandCodeTable = serviceHandlerTableObj.find('.post-response-commandcode-flow-table');
	var counter = 0;
	
	commandCodeTable.find('tr').each(function(){
	
		var handlerData = {};
		var handlerType = $(this).find('input:hidden[name=handlerType]').val();
		
		if( handlerType == 'DiameterCDRGenerationHandlerData'){
			
			var formObject = $(this).find('.form_cdrHandler');
			handlerData = fetchCDRHandlerData(formObject);
			handlerList.push({counter:handlerData});
	
		} else if ( handlerType == 'DiameterPluginHandlerData'){
			var formObject = $(this).find('.form_pluginHandler');
			handlerData = fetchPluginData(formObject);
			handlerList.push({counter:handlerData});
			
		} else{
			handlerData ={};
		}
		counter++;
	});
	
	return handlerList;
}  
/**
 * This function use for fetching Concurrency handler Data
 * @param formObject is form object from where we can find element for concurrency handler
 */
function fetchConcurrencyHandlerData(formObject) {
	
	var concurrencyTable = formObject.find('.tblconcurrency_handler');
	var orderNumber = formObject.find('input[name=orderNumber]').val();
	var isHandlerEnabled = formObject.find('input:checkbox[name=isHandlerEnabled]').val();
	var handlerTypeVal = formObject.find('input:hidden[name=handlerType]').val();
	var handlerName = formObject.find('input[name=handlerName]').val();
	var ruleset, diaConConfigId;
	var concurrencyHandlerData={};
	
	concurrencyTable.find('tr').each(function() {
			if (typeof $(this).find("input[name='ruleset']").val() !== 'undefined') {
				ruleset = $(this).find("input[name='ruleset']").val();
			}
			if (typeof $(this).find("select[name=diaConConfigId]").val() !== 'undefined') {
				diaConConfigId = $(this).find("select[name=diaConConfigId]").val();
			}
	});
	
	concurrencyHandlerData["orderNumber"]=orderNumber;
	concurrencyHandlerData["handlerType"]=handlerTypeVal;
	concurrencyHandlerData["handlerName"]=handlerName;
	concurrencyHandlerData["isHandlerEnabled"]=isHandlerEnabled;
	concurrencyHandlerData["ruleset"]=ruleset;
	concurrencyHandlerData["diaConConfigId"]=diaConConfigId;
	
	return  concurrencyHandlerData;
	}

/* Fecth CDR Handler data */
function fetchCDRHandlerData(formObject){
	
	var cdrTableObj = formObject.find('.mappingtblcdr');
	var cdrHandlerData = [];
	var orderNumber = formObject.find('input[name=orderNumber]').val();
	var isHandlerEnabled=formObject.find('input:checkbox[name=isHandlerEnabled]').val();
	var handlerTypeVal = formObject.find('input:hidden[name=handlerType]').val();
	var handlerName = formObject.find('input[name=handlerName]').val();
	
	cdrTableObj.find('tr').each(function(){
		var ruleset,primaryDriverId,secondaryDriverId,driverScript,wait;
		
	   if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
		   ruleset=  $(this).find("input[name='ruleset']").val();
	   }
	   
	   if(typeof $(this).find("select[name='primaryDriverId']").val() !== 'undefined'){
		   primaryDriverId=  $(this).find("select[name='primaryDriverId']").val();
	   }
	   
	   if(typeof $(this).find("select[name='secondaryDriverId']").val() !== 'undefined'){
		   secondaryDriverId=  $(this).find("select[name='secondaryDriverId']").val();
	   }
	   
	   if(typeof $(this).find("input[name='driverScript']").val() !== 'undefined'){
		   driverScript=  $(this).find("input[name='driverScript']").val();
	   }
	   
	   if(typeof $(this).find("input:checkbox[name='wait']").val() !== 'undefined'){
		   wait = $(this).find("input:checkbox[name='wait']").val();
	   }
	   
	   if(!isEmpty(ruleset) || !isEmpty(primaryDriverId) || !isEmpty(secondaryDriverId) || !isEmpty(driverScript) || !isEmpty(wait)){
		   cdrHandlerData.push({'ruleset':ruleset,'primaryDriverId':primaryDriverId,'secondaryDriverId':secondaryDriverId,'driverScript':driverScript,'wait':wait});
	   }
	 
	}); 
	
	if(cdrHandlerData.length >= 1){
		return {'orderNumber':orderNumber,'handlerType':handlerTypeVal,'handlerName':handlerName,'isHandlerEnabled':isHandlerEnabled,'entries':cdrHandlerData};
	}else{
		return {'orderNumber':orderNumber,'handlerType':handlerTypeVal,'handlerName':handlerName,'isHandlerEnabled':isHandlerEnabled};
	}
}

function validateCommasNumber(e){
	
	if((((e.charCode < 48 || e.charCode > 57) && e.charCode!=0 )) && e.charCode != 44){
			e.preventDefault();
			return false;
    }
}

function validateOnlyNumbers(e){
	if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
	       e.preventDefault();
	       return false;
 		}
}

/* Handler Name Functions */

/** This function  is used for expand textbox while editing 
 * 	@param textbox denotes current textbox object 
 * */
function expand(textbox) {
    if (!textbox.startW) { textbox.startW = textbox.offsetWidth; }
   
    var style = textbox.style;
    style.width = 0;
    
    var desiredW = textbox.scrollWidth;
    desiredW += textbox.offsetHeight;
    
    style.width = Math.max(desiredW, textbox.startW) + 'px';
}

/** This function is used changing handler name
 *  @param obj  denotes current textbox object  */
function changeHandlerName(obj){
	var parentObj = $(obj).parent().parent();
	var handlerNameTextbox = parentObj.find(".handler-name-txt");
	handlerNameTextbox.attr( "disabled", false );
	$(handlerNameTextbox).css({'border':'1px solid #CCC','background-color':'white'});
	
	var isHandlerisNew = $( handlerNameTextbox ).hasClass( "new-handler" );
	if(isHandlerisNew){
		$(handlerNameTextbox).css({'color':'black'});
	}
	var saveHandler = $(obj).parent().parent().find(".save_handler_icon"); 
	if($(obj).hasClass("edit_handler_icon_white")){
		
		$(saveHandler).css({"display":"inline"});
		$(saveHandler).removeClass("save_handler_icon");
		$(saveHandler).addClass("save_handler_icon_white");
		$(obj).parent().parent().find(".save_handler_icon_white").css({"display":"inline"});
		$(obj).css({"display":"none"});

	}else{
		$(obj).parent().parent().find(".save_handler_icon").css({"display":"inline"});
		$(obj).css({"display":"none"});
		
	}
	
	parentObj.find(".handler-label").hide();
	parentObj.find("input[name=handlerName]").show();
	parentObj.find(".handler-type").hide();
}

/** This function is used for changing handler name
 *  @param obj  denotes current textbox object
 * */
function saveHandlerName(obj){
	var parentObj = $(obj).parent().parent();
	var handlerNameTextbox = parentObj.find(".handler-name-txt");
	if( handlerNameTextbox.val() <= 0 ){
		alert('Please Enter Handler Name');
		handlerNameTextbox.focus();
	}else{
		
		var currentVal = handlerNameTextbox.val();
		currentVal=currentVal.trim();
		var properFlowFound = true;
		var attribId = handlerNameTextbox.attr('id');
		
		var serviceHandlerObj = $(obj).closest('.service_handler_table');
		
		$(serviceHandlerObj).find('.handler-name-txt').each(function(){
			if($(this).attr('id') != attribId ){
				if($(this).val() == currentVal){
					alert('System will not allow duplicate Handler name not allowed. Kindly change it');
					handlerNameTextbox.focus();
					properFlowFound = false;
				return false;
				}
			}
		});
		
		if(properFlowFound){
			var oldValue = getOldValue(handlerNameTextbox);
			var newVal = $(handlerNameTextbox).val();
			if(oldValue.trim() != newVal.trim()){
				$("#confirmDialog").data('obj',handlerNameTextbox).dialog("open");
			}else{
				disableTextBox(handlerNameTextbox);
			}
			parentObj.find("input[name=handlerName]").hide();
			parentObj.find(".handler-type").show();
			parentObj.find(".handler-label").text(newVal);
			parentObj.find(".handler-label").show();
		}
	}
}

/** This function is used for on key pressing it will save data
 *  @param e denotes  javascript event
 *  @param obj  denotes current textbox object
 *  @return boolean for condition satisfied or not
 *  */
function keyPressedForHandler(e,obj){
	var parentObj = $(obj).parent().parent();
	if(e.keyCode === 27){
		setOldValue(obj);
		disableTextBox(obj);
		parentObj.find(".handler-type").show();
	}
	if(e.keyCode === 13){
		if($(obj).val().length <= 0){
			alert('Please Enter Handler Name');
			$(obj).focus();
			e.preventDefault();
			parentObj.find(".handler-type").show();
			return false;
		}else{
			
			var currentVal = $(obj).val();
			currentVal=currentVal.trim();
			var properFlowFound = true;
			var attribId = $(obj).attr('id');
			
			$('.handler-name-txt').each(function(){
				if($(this).attr('id') != attribId ){
					if($(this).val() == currentVal){
						alert('System will not allow duplicate Handler name not allowed. Kindly change it');
						$(obj).focus();
						properFlowFound = false;
						e.preventDefault();
						return false;  
					}
				}
			});
			
			if ( properFlowFound ){
				var oldValue = getOldValue(obj);
				var newVal = $(obj).val();
				if(oldValue.trim() != newVal.trim()){
					$(obj).focus();
					$("#confirmDialog").data('obj',obj).dialog("open");
				}else{
					disableTextBox(obj);
				}
				
				parentObj.find("input[name=handlerName]").hide();
				parentObj.find(".handler-type").show();
				parentObj.find(".handler-label").text(newVal);
				parentObj.find(".handler-label").show();
			}
			
			e.preventDefault();
			return false;
		}
    }
}

/** Checking duplicate from array
 *  @param A denotes array 
 *  @return Boolean return true if duplicate found else false */
function arrHasDupes( A ) {                         
	var i, j, n;
	n=A.length;
                                                     
	for (i=0; i<n; i++) {                      
		for (j=i+1; j<n; j++) {              
			if (A[i]==A[j]) return true;
	}	}
	return false;
}

/**
 * Set old value of the handler
 * @param obj of Text box
 */
function setOldValue(obj) {
	var oldVal = getOldValue(obj);
	$(obj).val(oldVal);
}

function getOldValue(obj) {
	return $(obj).parent().find('.hidden-handler-name').val();
}

/**
 * This function disable text box for further editing
 * @param obj Text box that you want to disable
 */
function disableTextBox(obj){
	if($(obj).parent().parent().find(".save_handler_icon_white").length != 0){
	$(obj).parent().parent().find(".edit_handler_icon_white").css({"display":"inline"});
	$(obj).parent().parent().find(".save_handler_icon_white").css({"display":"none"});
	$(obj).css({'border':'none','background-color':'transparent','font-weight':'bold'});
	
	}else{
		$(obj).parent().parent().find(".edit_handler_icon").css({"display":"inline"});
		$(obj).parent().parent().find(".save_handler_icon").css({"display":"none"});
		$(obj).css({'border':'none','background-color':'transparent','font-weight':'bold'});	
	}
	var isHandlerisNew = $( obj ).hasClass( "new-handler" );
	if(isHandlerisNew){
		$(obj).css({'color':'white'});
	}
	
	$(obj).attr( "disabled", true );
}

function loadConfirmDialog() {
	$("#confirmDialog").dialog({
		autoOpen: false,
		modal: true,
		position: 'top',
		open: function( event, ui ) {
			$('button').removeClass("ui-state-focus");
			var obj = $("#confirmDialog").data('obj');
			var oldValue = getOldValue(obj);
			var newVal = $(obj).val();
			$('#oldHandlerName').text(oldValue);
			$('#newHandlerName').text(newVal);
		},
		close:function(){
			var obj = $("#confirmDialog").data('obj');
			setOldValue(obj);
			$(obj).parent().find('.handler-label').text(getOldValue(obj));
			$(this).dialog("close");
			disableTextBox(obj);
		},
		buttons : {
			"Keep it" : function() {
				var obj = $("#confirmDialog").data('obj');
				var newVal = $(obj).val();
				$(obj).parent().find('.hidden-handler-name').val(newVal);
				$(this).dialog("close");
				disableTextBox(obj);
			},
			"Discard" : function() {
				var obj = $("#confirmDialog").data('obj');
				setOldValue(obj);
				$(obj).parent().find('.handler-label').text(getOldValue(obj));
				$(this).dialog("close");
				disableTextBox(obj);
			}
		}
	});
}

function validateCommandCodeAndInterface(){
	
	var valueToReturn = true;
	
	$('.service_handler_table').each(function(){
		var commandCodeValue = $(this).find('.commandCode').val();
		
		if( isEmpty(commandCodeValue) ){
			if($(this).find('.commandCode').attr("id") != 'command_code_txt' && $(this).find('.commandCode').attr("id") != 'commandCode_' ){
				alert('Please Enter Command Code Value');
				$(this).find('.commandCode').focus();
				valueToReturn = false;
				return false;
			}
		}else if( !isContainsAlpha(commandCodeValue) ){
			alert('Only Comma Seperated Digits allowed in Command Code');
			$(this).find('.commandCode').focus();
			valueToReturn = false;
			return false;
		}
		
		var interfaceValue = $(this).find('.applicationid').val();
		
		if( isEmpty(interfaceValue) ){
			if($(this).find('.applicationid').attr("id") != 'interface' && $(this).find('.applicationid').attr("id") != 'interface_'){
				alert('Please Enter valid Interface value');
				$(this).find('.applicationid').focus();
				valueToReturn = false;
				return false;
			}
		}else if( !isContainsAlpha(interfaceValue) ){
			alert('Only Comma Seperated Digits allowed in Interface');
			$(this).find('.applicationid').focus();
			valueToReturn = false;
			return false;
		}
	});
	
	return valueToReturn;
}


function isContainsAlpha(commandCodeValue){
	if( !isEmpty(commandCodeValue)) {
		if(matchExact(commandCodeValue) == false){
			return false;
		}
	}
	return true;
}

var r = /[0-9,]*/;
function matchExact(str) {
	   var match = str.match(r);
	   return match != null && str == match[0];
}


function validateHandlerName(){
	
	var valueToReturn = true;
	
	$('.service_handler_table').each(function(){
		var handlerNameArray = [];
		$(this).find('.handler-name-txt').each(function(){
			
			if($(this).val().length <= 0){
				alert('Please Enter Handler Name');
				$(this).focus();
				valueToReturn = false;
				return false;
		}else{
				handlerNameArray.push($(this).val());
			}
		});
		
		var handlerNameVal = $(this).find('input:hidden[name=displayName]').val();
		
		var isDuplicatehandlerName = arrHasDupes(handlerNameArray);
		if( isDuplicatehandlerName ){
			alert('Duplicate handler name found in '+ handlerNameVal +'. Kindly verify all Handlers name.');
			triggerTab(handlerNameVal);
			valueToReturn = false;
			return false;
		}
	});
	
	if(!valueToReturn){
		return valueToReturn;
	}
	
	var savedFlag = true;
	$('.handler-name-txt').each(function(){
		if($(this).attr("disabled") == false || typeof $(this).attr("disabled") == 'undefined'){
			var oldValue = getOldValue(this);
			var newVal = $(this).val();
			if(oldValue.trim() != newVal.trim()){
				$(this).focus();
				$("#confirmDialog").data('obj',this).dialog("open");
				savedFlag = false;
				return false;
			}else{
				disableTextBox(this);
			}
		}
	});
	
	return savedFlag;
}

/**
 * this functions show/hide button in command code flow
 */
function hideButtons(){
	$(".nav-tabs li a").on("click", function() {
		if($(this).attr("id") == 'editCCFlow'){
			var isAdded = maxCommandCodeLimit();
			
			if(!isAdded){
				return false;
			}
			$("#btns").hide();
		}else{
			$("#btns").show();
		}
	});
}

/* Common Functions List */

/**
 * This function is used for Retriving radius dictionary Attributes
 * */
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

/** This Function is used for setting Radius Dictionary Attributes to username response attribute 
 *  @param myArray array of dictionary AVPs
 * */
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
/** This function will delete handler
 *  @param handlerId provide Specific Handler Id
 * */
function deleteHandler(handlerId){
	var deleteResult = confirm('Are you sure you want to delete this item?');
	if(deleteResult){
		var $div = $(handlerId).closest('table[class^="handler-class"]');
		$($div).parent().parent().remove();
	}
}

/** This function is used for expand Collapse handler 
 *  @parame tdObj current Object */
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

/* This function will returns unique random number */
function createUUID() {
	
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "_";

    var uuid = s.join("");
    return uuid;
}

/* Required Functions */
function setproxyESIServerData(proxyESIServerData){
	this.proxyESIServerData = proxyESIServerData;
}

function splitDbFields( val ) {
	return val.split( /[,;]\s*/ );
}

function extractLastDbFields( term ) {
	return splitDbFields( term ).pop();
}

function  setAutoCompleteDataforPlugin(dbFieldObject){
	$( dbFieldObject ).bind( "keydown", function( event ) {
		if ( event.keyCode === $.ui.keyCode.TAB &&
			$( this ).autocomplete( "instance" ).menu.active ) {
			event.preventDefault();
		}
	 }).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			response( $.ui.autocomplete.filter(
				pluginList, extractLastDbFields( request.term ) ) );
		},
		focus: function() {
			return false;
		},
		select: function( event, ui ) {
			this.value = ui.item.label ;
			return false;
		}
	});
}

function addProxyDropDown(selectedObject){
	var proxyListIdNameArray = getSelectedESIArray(selectedObject);
	var tableObj = $(selectedObject).parent().parent().parent().parent();
	var selectBoxObj = $(tableObj).find( "tr:last").find("select:first");
	$(selectBoxObj).append("<option value='0'>--Select--</option>");
	
	if(proxyESIServerData!=undefined){
		$.each(proxyESIServerData, function(index, item) {
			if($.inArray(item.id,proxyListIdNameArray) < 0 ){
				$(selectBoxObj).append("<option value='" + item.id + "'>" + item.peerGroupName + "</option>");
			}
		});
	}
}

function getSelectedESIArray(selectedObject){
	var proxyListIdNameArray = new Array();
	var arrIndex = 0;
	var tableObj=$(selectedObject).parent().parent().parent().parent();
	$(tableObj).find(" select[name='esiId']").each(function(){
		if($(this).val() != "0"){
			proxyListIdNameArray[arrIndex++] = $(this).val();
		}				
	});
	return proxyListIdNameArray;
} 

function getAfterDeletedSelectedESIArray(tableObj){
	var proxyListIdNameArray = new Array();
	var arrIndex = 0;
	$(tableObj).find(" select[name='esiId']").each(function(){
		if($(this).val() != "0"){
			proxyListIdNameArray[arrIndex++] = $(this).val();
		}				
	});
	return proxyListIdNameArray;
} 

function setProxyHandlerESIDropDown(selectedObject){
	var proxyListIdNameArray = getSelectedESIArray(selectedObject);
	var tableObj = $(selectedObject).parent().parent().parent().parent();
	
	
	$(tableObj).find("select[name='esiId']").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value='0'>--Select--</option>");
		if(proxyESIServerData!=undefined){
			$.each(proxyESIServerData, function(index, item) {
				if( $.inArray(item.id,proxyListIdNameArray) < 0 ||  item.id == currentVal ){
					$(selectObj).append("<option value='" + item.id + "'>" + item.peerGroupName + "</option>");
				}
			});
		}
		$(selectObj).val(currentVal);
	}); 
	$(tableObj).css({'border':'1px solid #c0c0c0'});
} 	

function resetProxyHandlerESIDropDown(tableObj){
	var proxyListIdNameArray = getAfterDeletedSelectedESIArray(tableObj);
	
	$(tableObj).find("select[name='esiId']").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value='0'>--Select--</option>");
		if(proxyESIServerData!=undefined){
			$.each(proxyESIServerData, function(index, item) {
				if( $.inArray(item.id,proxyListIdNameArray) < 0 ||  item.id == currentVal ){
					$(selectObj).append("<option value='" + item.id + "'>" + item.name + "</option>");
				}
			});
		}
		$(selectObj).val(currentVal);
	}); 
	$(tableObj).css({'border':'1px solid #c0c0c0'});
} 

function addMoreServer(obj){
	var tableObj=$(obj).parent().parent().parent();
	var htmlSource="<tr>"+
	"<td align='left' class='top-border labeltext' valign='top' width='55%' id='tbl_attrid'>"+
	"<select name='esiId' class='noborder' style='width:100%;' onchange='setProxyHandlerESIDropDown(this);'>"+
	"</select></td>"+
	"<td align='center' class='top-border labeltext' valign='top' width='5%'><span class='delete remove-proxy-server' onclick='deleteMe(this);'/>&nbsp;</td></tr>";
	$(tableObj).append(htmlSource);
	addProxyDropDown(obj);
}

function selectAll(){
	
	$('.form_profilelookup').each(function(){
		
		 /* select all primary drivers */
		 var selecteddriverIds = $("select[name = 'selecteddriverIds']");
		 $(selecteddriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
		 
		 /* select all secondary drivers */
		 var selectedCacheDriverIds = $("select[name = 'selectedCacheDriverIds']");
		 $(selectedCacheDriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
        
		 /* select all Additional drivers */
		 var selectedAdditionalDriverIds = $("select[name = 'selectedAdditionalDriverIds']");
		 $(selectedAdditionalDriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
	});
}


$.fn.serializeAuthObject = function()
{
   var o = {};
   // Find disabled inputs, and remove the "disabled" attribute
   var disabled = this.find(':input:disabled').removeAttr('disabled');
   var a = this.serializeArray();
   $.each(a, function() {
       if (o[this.name] !== undefined) {
           if (!o[this.name].push) {
               o[this.name] = [o[this.name]];
           }
           o[this.name].push(this.value || '');
       } else {
           var element = $("[name = '"+this.name+"']");
          
			if($(element).length > 0 && $(element).is("input:checkbox[name='supportedAuthenticationMethods']")) {
                o[this.name] = [this.value || ''];
           } else {
                o[this.name] = this.value || '';
           }
       } 
   });
   disabled.attr('disabled','disabled');
   return o;
};


$.fn.serializeObject = function()
{
   var o = {};
   // Find disabled inputs, and remove the "disabled" attribute
   var disabled = this.find(':input:disabled').removeAttr('disabled');
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
   disabled.attr('disabled','disabled');
   return o;
};

function fetchSubscriberProfileRepositoryDetails(formObject){
	var profileLookUpData = $(formObject).serializeObject();
	return profileLookUpData;
}
 
function fetchAuthenticationHandlerData(formObject) {
	var authenticationHandlerData = $(formObject).serializeAuthObject();
	return authenticationHandlerData;
}

function fetchAuthorizationHandlerData(formObject){
	var authorizationHandlerData = $(formObject).serializeObject();
	return authorizationHandlerData;
}

function fetchPluginData(formObject){
	
	var plugintable = formObject.find('.mappingtblplugin');
	var pluginDetails = [];
	var orderNumber = formObject.find('input[name=orderNumber]').val();
	var isHandlerEnabled = formObject.find('input:checkbox[name=isHandlerEnabled]').val();
	var handlerTypeVal = formObject.find('input:hidden[name=handlerType]').val();
	var handlerName = formObject.find('input[name=handlerName]').val();
	
	plugintable.find('tr').each(function(){
		var ruleset,pluginName,isResponse,pluginArgument;	
	
		if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
			ruleset=  $(this).find("input[name='ruleset']").val();
		}
		
		if(typeof $(this).find("input[name='pluginName']").val() !== 'undefined'){
			pluginName=  $(this).find("input[name='pluginName']").val();
		}
			
		
		if(typeof $(this).find("input[name='pluginArgument']").val() !== 'undefined'){
			pluginArgument=  $(this).find("input[name='pluginArgument']").val();
		}
			
		if(typeof $(this).find("select[name='requestType']").val() !== 'undefined'){
			isResponse=  $(this).find("select[name='requestType']").val();
		}
		
		if(!isEmpty(ruleset) || !isEmpty(pluginName) || !isEmpty(isResponse)){
			pluginDetails.push({'ruleset':ruleset,'pluginName':pluginName,'pluginArgument':pluginArgument,'onResponse':isResponse});
		}
	});
		
	return {'orderNumber':orderNumber,'handlerType':handlerTypeVal,'handlerName':handlerName,'isHandlerEnabled':isHandlerEnabled,'pluginEntries':pluginDetails};
}

function fetchProxyCommunicationData(formObject){
	
	var proxyTableObj = formObject.find('.proxyCommunicationTbl');
	var proxyCommunicationData = [];
	var orderNumber = formObject.find('input[name=orderNumber]').val();
	var isHandlerEnabled=formObject.find('input:checkbox[name=isHandlerEnabled]').val();
	var protocol = formObject.find('input:hidden[name=protocol]').val();
	var priorityResultCode = formObject.find('input[name=priorityResultCodes]').val();
	var resultCodeWhenNoEntrySelected = formObject.find('input[name=resultCodeWhenNoEntrySelected]').val();
	var handlerTypeVal = formObject.find('input:hidden[name=handlerType]').val();
	var handlerName = formObject.find('input[name=handlerName]').val();
	var proxyMode =  formObject.find("select[name=proxyMode]").val();
	
	proxyTableObj.find('tr').each(function(){
		var ruleset,translationMapping,script,acceptedResultCode,peerGroupId;
		
	   if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
		   ruleset=  $(this).find("input[name='ruleset']").val();
	   }
	   
	   if(typeof $(this).find("select[name='translationMappingName']").val() !== 'undefined'){
		   translationMapping=  $(this).find("select[name='translationMappingName']").val();
	   }
	   
	   if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
		   script=  $(this).find("input[name='script']").val();
	   }
	   
	   if(typeof $(this).find("input[name='acceptedResultCode']").val() !== 'undefined'){
		   acceptedResultCode = $(this).find("input[name='acceptedResultCode']").val();
	   }
	   
	   if(typeof $(this).find("select[name='peerGroupId']").val() !== 'undefined'){
		   peerGroupId=  $(this).find("select[name='peerGroupId']").val();
	   }
	   
		if(!isEmpty(ruleset) || !isEmpty(translationMapping) || !isEmpty(acceptedResultCode) || !isEmpty(script) || !isEmpty(peerGroupId)){
		   proxyCommunicationData.push({'ruleset':ruleset,'translationMapping':translationMapping,'script':script,'acceptableResultCodes':acceptedResultCode,'peerGroupId':peerGroupId});
	   }
	 
	}); 
	
	if(proxyCommunicationData.length >= 1){
		return {'orderNumber':orderNumber,'handlerType':handlerTypeVal,'handlerName':handlerName,'isHandlerEnabled':isHandlerEnabled,'protocol':protocol,'priorityResultCodes':priorityResultCode,'resultCodeWhenNoEntrySelected':resultCodeWhenNoEntrySelected,'proxyMode':proxyMode,'entries':proxyCommunicationData};
	}else{
		return {'orderNumber':orderNumber,'handlerType':handlerTypeVal,'handlerName':handlerName,'isHandlerEnabled':isHandlerEnabled,'protocol':protocol,'priorityResultCodes':priorityResultCode,'resultCodeWhenNoEntrySelected':resultCodeWhenNoEntrySelected,'proxyMode':proxyMode};
	}
}

function fetchBroadcastCommunicationData(formObject){
	
	var proxyTableObj = formObject.find('.broadcastCommunicationTbl');
	var broadcastCommunicationData = [];
	var orderNumber = formObject.find('input[name=orderNumber]').val();
	var isHandlerEnabled=formObject.find('input:checkbox[name=isHandlerEnabled]').val();
	var protocol = formObject.find('input:hidden[name=protocol]').val();
	var priorityResultCode = formObject.find('input[name=priorityResultCodes]').val();
	var resultCodeWhenNoEntrySelected = formObject.find('input[name=resultCodeWhenNoEntrySelected]').val();
	var handlerTypeVal = formObject.find('input:hidden[name=handlerType]').val();
	var handlerName = formObject.find('input[name=handlerName]').val();
	
	proxyTableObj.find('tr').each(function(){
		var ruleset,translationMapping,script,acceptedResultCode,peerGroupId,waitForResponse;
		
	   if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
		   ruleset=  $(this).find("input[name='ruleset']").val();
	   }
	   
	   if(typeof $(this).find("select[name='translationMappingName']").val() !== 'undefined'){
		   translationMapping=  $(this).find("select[name='translationMappingName']").val();
	   }
	   
	   if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
		   script=  $(this).find("input[name='script']").val();
	   }
	   
	   if(typeof $(this).find("input[name='acceptedResultCode']").val() !== 'undefined'){
		   acceptedResultCode = $(this).find("input[name='acceptedResultCode']").val();
	   }
	   
	   if(typeof $(this).find("input:checkbox[name='waitForResponse']").val() !== 'undefined'){
		   waitForResponse = $(this).find("input:checkbox[name='waitForResponse']").val();
	   }
	   
	   if(typeof $(this).find("select[name='peerGroupId']").val() !== 'undefined'){
		   peerGroupId=  $(this).find("select[name='peerGroupId']").val();
	   }
	   
	   if(!isEmpty(ruleset) || !isEmpty(translationMapping) || !isEmpty(acceptedResultCode) || !isEmpty(script) || !isEmpty(peerGroupId)){
			broadcastCommunicationData.push({'ruleset':ruleset,'translationMapping':translationMapping,'script':script,'acceptableResultCodes':acceptedResultCode,'wait':waitForResponse,'peerGroupId':peerGroupId});
	   }
	 
	}); 
	
	if(broadcastCommunicationData.length >= 1){
		return {'orderNumber':orderNumber,'handlerType':handlerTypeVal,'handlerName':handlerName,'isHandlerEnabled':isHandlerEnabled,'protocol':protocol,'priorityResultCodes':priorityResultCode,'resultCodeWhenNoEntrySelected':resultCodeWhenNoEntrySelected,'entries':broadcastCommunicationData};
	}else{
		return {'orderNumber':orderNumber,'handlerType':handlerTypeVal,'handlerName':handlerName,'isHandlerEnabled':isHandlerEnabled,'protocol':protocol,'priorityResultCodes':priorityResultCode,'resultCodeWhenNoEntrySelected':resultCodeWhenNoEntrySelected};
	}
}

function changeValueOfFlow(obj){
	if( $(obj).val() == 'true'){
		$(obj).val('false');
		var handlerObject=$(obj).closest('table[class^="handler-class"]');
		var flag = false;
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}else{
			var handlerEnabledFlag = false;
			var formObj = $(obj).closest('form');
			if($(formObj).hasClass("form_auth")){
				$(obj).closest('table.service_handler_table').find('.form_auth').each(function (){
					if($(this).find("input[name='isHandlerEnabled']").val() == "true"){
						handlerEnabledFlag = true;
						return false;
					}
				});
			}
			if(handlerEnabledFlag){
				$(obj).attr('checked', false);
				$(obj).val('false');
				alert("You cannot have multiple enabled authentication handler");
				return false;
			}else{
				$(obj).attr('checked', true);
				$(obj).val('true');
				var handlerObject=$(obj).closest('table[class^="handler-class"]');
				$(handlerObject).find('tr').each(function(){
					$(this).removeClass('disable-class');
				});
			}
		}
}

/**
 * This method saves unsaved tab name
 * @param uniqueId that is generated when tab is added
 * @param obj Div object that contains edit symbol
 * @returns {Boolean}
 */
function saveEditedTabValue(uniqueId,obj) {
	var newVal = $('#txt_'+uniqueId).val();
	
	if(newVal.trim() == ''){
		alert("Command code flow name cannot be empty");
		$('#txt_'+uniqueId).focus();
		return false;
	}
	
	var isValidName = true;
	for (var i = 0, len = newVal.length; i < len; i++) {
		  var ascii = newVal[i].charCodeAt();

		  if(ascii == 44 || ascii == 45 || ascii == 46 || ascii == 95 || (ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122) || (ascii >= 48 && ascii <= 57)){
			  continue;
		  }else{
			  isValidName = false;
			  break;
		  }
	}
	
	if(!isValidName){
		alert('Not a valid Name. Valid Characters : A-Z, a-z, 0-9, ., -, _ ');
		$('#txt_'+uniqueId).focus();
		return false;
	}
	
	$('#tab_'+uniqueId).text(newVal);
	
	$('#tab_'+uniqueId).show();
	$('#txt_'+uniqueId).hide();
	
	$('#servicehandlertable_'+uniqueId).find('.displayName').val(newVal);
	
	var editObj = $(obj).find('.fa-save');
	editObj.addClass("fa-pencil");
	editObj.removeClass("fa-save");
	
	return true;
}

function setHandlerProp(uniqueId,handlerName){
	$('#'+uniqueId).find("input[name=handlerName]").attr('size', handlerName.length);
	$('#'+uniqueId).find("div.handler-label").text(handlerName);
	$('#'+uniqueId).find("input[name=handlerName]").val(handlerName);
	$('#'+uniqueId).find("input[name=handlerName]").hide();
	$('#'+uniqueId).find("input:hidden[name=hiddenHandlerName]").val(handlerName);
}

/** This function is used for validating CDR handler */
function validateCDRHandler(){
	var noValueConfigured = false;
	var isValueConfigured = false;
	var valueToReturn = true;
	$('.form_cdrHandler').each(function(){
		
		var handlerData = handlerTableInfoJson(this,'mappingtblcdr');
		noValueConfigured= handlerData['noValueConfigured'];
		isValueConfigured = handlerData['isValueConfigured'];
		var cdrTableObj = handlerData['tableObj'];
		var isErrorFound = false;
		
		if(noValueConfigured){
			$(this).children('table').css({'border':'1px solid red'});
			valueToReturn = false;
			return manageNoValConfigured('Atleast one mapping is required in CDR Handler',this,'cdr-btn');
		}else if(isValueConfigured){
			var skipFirstStep = false;
			cdrTableObj.find('tr').each(function(){
				 	if(!skipFirstStep){
			        	skipFirstStep=true;
			        }else{
			        		var innerTableObj = $(this).find('select[name="primaryDriverId"]');
			        		var throwErrorMsg = false;
				         
			        		var primaryDriverId = '';
			        		var innerTableObjVal = innerTableObj.val();
			        		if(typeof innerTableObjVal !== 'undefined' && innerTableObjVal != 0){
			        			primaryDriverId = innerTableObjVal;
			        		}
						       
			        		if(isEmpty(primaryDriverId)){
					       		throwErrorMsg=true;
					       		valueToReturn=false;
			        		}
							 
			        		if(throwErrorMsg){
							 	$(innerTableObj).parent().css({'border':'1px solid #FF5555 '}).css({'box-shadow': '0 0 0 1px #FF5555 '});
					       		alert('Please Select at least one Primary Driver from drop down');
					       		isErrorFound=true;
					       		return false;
			        		}
			        }
			}); 
		}
		
		if(isErrorFound){
			return false;
		}
	});
	return valueToReturn; 
}

/**
 * Common method for validating proxy and broadcast handler of radius and diameter
 * @param formClass Form of handler
 * @param radiusHandlerJsName hidden field inside handler
 * @param tableClass table containing handler data
 * @param noMappingErrorMsg Error message when no entry is made by user
 * @param addBtnClass class of button used to add new row
 * @returns {Boolean}
 */
function proxyAndBrocastCommValidation(formClass,radiusHandlerJsName,tableClass,noMappingErrorMsg,addBtnClass){
	var noValueConfigured = false;
	var isValueConfigured = false;
	var valueToReturn = true;
	
	$('.'+formClass).each(function(){
		var isRadius = ($(this).find('#handlerJsName').val() == radiusHandlerJsName);
		
		if(isRadius == false && formClass == 'form_proxycommunication'){
			var resultCodeObj = $(this).find('input[name=resultCodeWhenNoEntrySelected]');
			var resultCodeWhenNoEntrySelected = resultCodeObj.val();
			if(isEmpty(resultCodeWhenNoEntrySelected)){
				resultCodeObj.focus();
				alert('Please specify the result code when no entry selected');
				valueToReturn=false;
				return false;
			}
		}
		
		if(isRadius == false && formClass == 'form_Broadcastcommunication'){
			var resultCodeObj = $(this).find('input[name=resultCodeWhenNoEntrySelected]');
			var resultCodeWhenNoEntrySelected = resultCodeObj.val();
			if(isEmpty(resultCodeWhenNoEntrySelected)){
				alert('Please specify the result code when no entry selected');
				resultCodeObj.focus();
				valueToReturn=false;
				return false;
			}
		}	
		var handlerData = handlerTableInfoJson(this,tableClass);
		noValueConfigured= handlerData['noValueConfigured'];
		isValueConfigured = handlerData['isValueConfigured'];
		var tableObj = handlerData['tableObj'];
		
		var isErrorFound = false;
		if(noValueConfigured){
			$(this).children('table').css({'border':'1px solid red'});
			valueToReturn=false;
			return manageNoValConfigured(noMappingErrorMsg,this,addBtnClass);
		}else if(isValueConfigured){
			var skipFirstStep = false;
			tableObj.find('tr').each(function(){
				 	if(!skipFirstStep){
			        	skipFirstStep=true;
			        }else{
			        		var innerTableObj = $(this).find('select[name="peerGroupId"]').parent();
			        		var throwErrorMsg = false;
				         
			        		var serverId = '';
			        		if(typeof $(this).find("select[name='peerGroupId']").val() !== 'undefined'){
					        	serverId = $(this).find("select[name='peerGroupId']").val();
			        		}
						       
			        		if(isEmpty(serverId)){
					       		throwErrorMsg=true;
					       		valueToReturn=false;
			        		}
							 
			        		if(throwErrorMsg){
							 	$(innerTableObj).css({'border':'1px solid #FF5555 '}).css({'box-shadow': '0 0 0 1px #FF5555 '});
							 	if(isRadius){
							 		alert('Please Select at least one ESI from drop down');
							 	}else{
							 		alert('Please Select at least one Peer Group from drop down');
							 	}
					       		isErrorFound=true;
					       		return false;
			        		}
			        }
			}); 
		}
		
		if(isErrorFound){
			return false;
		}
	
	});
	return valueToReturn; 
}

/**
 * This function is usedf to get basic details of handler for vaditaion
 * @param obj handler form object
 * @param tableClass class of handler table
 * @returns Json contain id of division Handler, value is there or not and table object
 */
function handlerTableInfoJson(obj,tableClass){
	var noValueConfigured;
	var isValueConfigured;
	var tableObj = $(obj).find('.'+tableClass);
	var idofTable = $(tableObj).attr('id');
	var rowCount = $('#'+idofTable+' tr').length;
	if( rowCount == 1){
		noValueConfigured= true;
	}else if(rowCount > 1){
		isValueConfigured = true;
	}
	
	return {noValueConfigured : noValueConfigured, isValueConfigured : isValueConfigured, tableObj : tableObj};
}

/**
 * This function gives you error message when no row is added to the handler
 * @param errorMsg Message when no row is added to the handler
 * @param obj form that contains handler
 * @param btnClass class of button that is used to add record/row
 * @returns {Boolean}
 */
function manageNoValConfigured(errorMsg,obj,btnClass){
	var divId = $(obj).closest('div').attr('id');
	alert(errorMsg);
	var addMappingButton = $(obj).find('.'+btnClass);
	focusObject=addMappingButton;
	valueToReturn=false;
	isErrorFound=true;
	triggerTab(divId);
	return false;
} 

/**
 * This function is use for remove validation class from concurrency policy drop down onchange event
 * @param object specify form element
 */
function removeValidation(object) {
	$(object).removeClass("validateDiaconcurrency");
}

/**
 * This method validates whether at least one handler is added to the flow or not, 
 * and if the handler is added then at least one handler should be enabled 
 * @returns {Boolean} Return true if invalid else false
 */
function validateNoOfHandler(){
	var commandCodeName;
	var cnt;
	var isHandlerEnabled;
	var tabId;
	var isCommandCodeFlow;
	
	$( ".nav-tabs > li" ).each(function() {
		var anchorTag = $(this).find("a");
		var commandCode = anchorTag.attr("href");
		tabId = commandCode.substring(1,commandCode.length);
		commandCodeName  = anchorTag.children("label").text();
		
		isCommandCodeFlow = typeof(commandCodeName) != 'undefined' && isEmpty(commandCodeName) == false;
		if(isCommandCodeFlow){
			var divObj = $(commandCode);
			var tableObj = $(divObj).find(".commandcode-flow-table");
			
			cnt = 0; 
			isHandlerEnabled = false;
			var tableTr = $(tableObj).children("tbody").children("tr");
			
			if(tableTr.length > 0){
				tableTr.each(function(){
					if(isEmpty($(this).text()) == false){
						cnt++;
					}
					if($(this).find('input[name="isHandlerEnabled"]').val() == 'true'){
						isHandlerEnabled = true;
						return false;
					}
				});
			} 
			
			if(isHandlerEnabled == false || cnt == 0){
				return false;
			}
		}
	});
	
	if(isCommandCodeFlow){
		if(cnt == 0 ){
			alert('Please Add atleast one handler in ' + commandCodeName);
			triggerTab(tabId);
		}else{
			if(isHandlerEnabled == false){
				alert('Please Enable atleast one handler in ' + commandCodeName);
				triggerTab(tabId);
			}
		}
	}
}

/**
 * This function add's a new row to Command Code Wise Response Attribute Table in TGPP basic detail 
 * @param tableId Id of the table in which you want to add a row
 * @param templateId Id of the table the template table
 */
function addResponseAttributesTable(tableId,templateId){
	var tableRowStr = $("#"+templateId).find("tr");
	$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	var tableLastTr = $("#"+tableId+" tr:last");
	$(tableLastTr).find("input:first").focus();
	setAutoComplete($(tableLastTr).find('.commandCode'),commandCodeList,false);
	setAutoComplete($(tableLastTr).find('.responseAttributes'),diaAVPAutoComplete,true);
}

/**
 * This function is used for sperating values in the autocomplete object/textbox and it is used
 * by setAutoComplete() function.
 */
function splitData(val,isColonSepetrated) {
	var data;
	if(isColonSepetrated){
		data = val.split( /[,;:]\s*/ )
	}else{
		data = val.split( /[,;]\s*/ )
	}
	return data;
}

/**
 * This function is used for sperating values in the autocomplete object/textbox and it is used
 * by setAutoComplete() function.
 */
function extractLastItems(term,isColonSepetrated) {
	return splitData(term,isColonSepetrated).pop();
}

/**
 * This function is used to add auto complete functionality
 * @param obj Html textbox object on which you want autocomplete feature 
 * @param suggestionArray The array for the suggestion values for autocomplete when you type in it
 */
function setAutoComplete(obj,suggestionArray,isColonSepetrated){
	$(obj).bind( "keydown", function( event ) {
		if ( event.keyCode === $.ui.keyCode.TAB && $( this ).autocomplete( "instance" ).menu.active ) {
			event.preventDefault();
		}
	}).autocomplete({
	minLength: 0,
	source: function( request, response ) {
		response( $.ui.autocomplete.filter(
				suggestionArray, extractLastItems(request.term,isColonSepetrated)));
		},
	focus: function() {
		return false;
	},
	select: function( event, ui ) {
		var val = this.value;
		var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
		var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
		var colonIndex=val.lastIndexOf(":") == -1 ? 0 :val.lastIndexOf(":");
		
		if(commaIndex == semiColonIndex && colonIndex == commaIndex ) {
				val = "";
		}else if(colonIndex > semiColonIndex && colonIndex > commaIndex) {
			var lastSepIndex;
			if(commaIndex > semiColonIndex){
				lastSepIndex = commaIndex; 
			}else if(commaIndex < semiColonIndex){
				lastSepIndex = semiColonIndex;
			}else{
				lastSepIndex = -1;
			}
			
			var colonVal = val.substring(lastSepIndex+1,colonIndex+1);
			if(colonVal.toLowerCase() === '$res:' || colonVal.toLowerCase() === '$req:'){
				val = val.substring(0,colonIndex+1);
			}else{
				val = val.substring(0,lastSepIndex+1);
			}
		}else if(commaIndex > semiColonIndex){
			val = val.substring(0,commaIndex+1);
		} else {
			val = val.substring(0,semiColonIndex+1);
		}
		this.value = val + ui.item.value ;
		return false;
	}
	});
}

/**
 * The function deletes the row from Command Code Wise Rsponse Attributes table in TGPP basic details
 * @param spanObject HTML Object that has the remove button
 */
function deleteMe(spanObject){
	$(spanObject).parent().parent().remove();
}

/**
 * This function fetch the date from Command Code Wise Rsponse Attributes table and store into the hidden field
 * with id(commandCodeWiseRespAttrib) in JSON string format at the time of submitting the form
 */
function fetchResponseAttrData(){
	var resAttrArray = [];
	var skipFirstTr = true;
	$('#responseAttributeTable > tbody > tr').each( function(){
		if(skipFirstTr){
			skipFirstTr = false;
			return;
		}
		
		var resAttrJson = {};
		var commandCodeVal = $(this).find('input[name="commandCode"]').val();
		var resAttrVal = $(this).find('textarea[name="responseAttributes"]').val();
		resAttrJson['commandCode'] = commandCodeVal;
		resAttrJson['responseAttr'] = resAttrVal;
		resAttrArray.push(resAttrJson);
	});
	
	$('#commandCodeWiseRespAttrib').val(JSON.stringify(resAttrArray));
}

/**
 * When you update Basic Details of TGPP service policy, this function would parse the JSON object
 * and add rows to Command Code Wise Rsponse Attributes table
 * @param templateId is the id of the table that contains template for Command Code Wise Rsponse Attributes table
 */
function addCommandCodeResponseAttrRows(templateId){
	var resAttrArray = JSON.parse($('#commandCodeWiseRespAttrib').val());
	var length = resAttrArray.length;
	
	var templateTr = $("#"+templateId).find("tr");
	var mainTable = $('#responseAttributeTable');
	for (var i = 0; i < length; i++) {
		var tr = templateTr.clone();
		$(tr).find('input[name="commandCode"]').val(resAttrArray[i].commandCode);
		$(tr).find('textarea[name="responseAttributes"]').val(resAttrArray[i].responseAttr);
		mainTable.append(tr);
	}
	
	$('.commandCode').each( function(){
		setAutoComplete(this,commandCodeList,false);
	});
	
	setAutoCompleteInRespAttr();
}

/**
 * This function makes a post request to the server for getting Diameter AVP, that is used to provide autocomplete
 * values for response attribute in "Command Code Wise Response Attr. After making the post request it will store
 * the values in a global array(diaAVPAutoComplete).
 */
function setAutoCompleteInRespAttr(){
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	$.post("SearchDiameterAttributesServlet",{searchNameOrAttributeId:''}, function(data){
		data = data.trim();
		dbFieldStr = data.substring(1,data.length-1);
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
		diaAVPAutoComplete = myArray;
		$(".responseAttributes").each( function(){
			setAutoComplete(this,diaAVPAutoComplete,true);
		});
	});
}

/**
 * This function is used to validate the command code value of "Command Code wise response attribute.
 * It command code value is not specified in each mapping then it will not allow you to submit the form.
 * @returns true if command code value is specified for each mapping else false.
 */
function isRespAttrValidMappings(){
	var isValidMapping = true;
	$('.responseAttributeTable').find('.commandCode').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == 0) {
			alert("Command Code must be Specified");
			isValidMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidMapping;
}

/**
 * This function focus on object after the Div of a particular command code flow is triggered.
 */
function setFocusAfterTrigger(){
	if(typeof(focusObject) != 'undefined'){
		setTimeout(function(){$(focusObject).focus();},200);
	}
}