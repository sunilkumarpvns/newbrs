/**
 * This js file contains the functions for function used for only update tgpp service policy
 * Created Date : 22 Jan 2016 
 * Author       : Nayana Rathod
 * 
 */
/* Update Function */

function addCommandCodeFlow(display_label,commandCode,interfaceId,isView){
	
	var id = $(".nav-tabs").children().length;
	
	var new_display_label = display_label.replace(/,/g, '_');
	new_display_label = new_display_label.replace(/\./g, '_');
	new_display_label = new_display_label.replace(/-/g, '_');
	
	var uuId = createUUID();
	var uniqueId = new_display_label + "_" + uuId;
	
	if(isView == "false"){
		$('.nav-tabs li:last-child').before(' <li><a href="#'+new_display_label+'" role="tab" data-toggle="tab"><label id="tab_'+ uniqueId +'">' + display_label + '</label><input id="txt_'+ uniqueId +'" onkeyup="expand(this);" class="ccf-tab" onfocus="this.value = this.value;" />&nbsp; &nbsp; &nbsp; &nbsp;</a> <div id='+uniqueId+' > <i class="fa fa-pencil"></i> </div> &nbsp; &nbsp; &nbsp; <span> <i class="fa fa-trash-o"></i> </span> &nbsp;</li>');
	}else{
	    $('.nav-tabs li:last-child').before(' <li><a href="#'+new_display_label+'" role="tab" data-toggle="tab">&nbsp; &nbsp;<label id="tab_'+ uniqueId +'" style="margin-right: 0px;" >' + display_label + '</label><input id="txt_'+ uniqueId +'" onkeyup="expand(this);" class="ccf-tab" onfocus="this.value = this.value;" /> &nbsp; &nbsp;</a> <div id='+uniqueId+' ></div> &nbsp; &nbsp; &nbsp; <span></span></li>');
	}
	
	$('.tab-content').append('<div class="tab-pane fade" id="'+new_display_label+'"><table cellspacing="0" cellpadding="0" border="0" width="100%" class="service_handler_table" id="servicehandlertable_'+ uniqueId +'">'+ $('#service_handler_table').html() +'</table></div>');
	
	$('#txt_'+uniqueId).val(display_label);
	$('#txt_'+uniqueId).hide();
	$('#txt_'+uniqueId).attr("size",display_label.length);
	
	$('#servicehandlertable_'+uniqueId).find('.uniqueUUID').val(uuId);
	$('#servicehandlertable_'+uniqueId).find('.commandCode').attr('id','commandCode_'+uuId);
	$('#servicehandlertable_'+uniqueId).find('.interface').attr('id','interface_'+uuId);
	$('#servicehandlertable_'+uniqueId).find('.displayName').attr('id','displayName_'+uuId);
	
	$('#servicehandlertable_'+uniqueId).find('.commandcode-flow-table').attr('id','commandcode-flow-table_'+uuId);
	
	$('#servicehandlertable_'+uniqueId).find('.commandCode').val(commandCode);
	$('#servicehandlertable_'+uniqueId).find('.interface').val(interfaceId);
	$('#servicehandlertable_'+uniqueId).find('.displayName').val(display_label);
	
	return 'servicehandlertable_'+uniqueId;
}

function addAuthenticationHandler(handlerTable,handlerValue){
	var htmlcontent = '';
	htmlcontent=getNewHandlerData("AuthenticationHandler",handlerTable);
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	$('#'+handlerTable).find('.commandcode-flow-table').append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');
	if(handlerValue.enabled === 'true'){
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	
	}else if(handlerValue.enabled === 'false'){
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
 		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}
	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	
	$('#'+uniqueId).find("select[name=eapConfigId]").val(handlerValue.eapConfigId);
	
	var authMethodArray = handlerValue.supportedAuthenticationMethods;
	var supportedAuthenticationMethods = $('#'+uniqueId).find('input[name="supportedAuthenticationMethods"]');
	$.each(authMethodArray, function(key,value){
		$.each(supportedAuthenticationMethods,function(){
			if($(this).val() == value){
				$(this).attr('checked', true);
			}
		});
	});
}

function addAuthorizationHandler(handlerTable,handlerValue){
	var htmlcontent = '';
	htmlcontent=getNewHandlerData("AuthorizationHandler",handlerTable);
	
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	
	$('#'+handlerTable).find('.commandcode-flow-table').append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');

	if(handlerValue.enabled === 'true'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	
	}else if(handlerValue.enabled === 'false'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
 		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}
	
	if( handlerValue.wimaxEnabled == true ){
		$('#'+uniqueId).find("select[name=wimaxEnabled]").val("true");
	}else {
		$('#'+uniqueId).find("select[name=wimaxEnabled]").val("false");
	}
	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	
	$('#'+uniqueId).find("input[name=defaultSessionTimeoutInSeconds]").val(handlerValue.defaultSessionTimeoutInSeconds);
	
	if(handlerValue.rejectOnCheckItemNotFound == true){
		$('#'+uniqueId).find("input:checkbox[name=rejectOnCheckItemNotFound]").attr('checked', true);
		$('#'+uniqueId).find("input:checkbox[name=rejectOnCheckItemNotFound]").val(handlerValue.rejectOnCheckItemNotFound);
	}else if(handlerValue.rejectOnCheckItemNotFound == false){
		$('#'+uniqueId).find("input:checkbox[name=rejectOnCheckItemNotFound]").attr('checked', false);
		$('#'+uniqueId).find("input:checkbox[name=rejectOnCheckItemNotFound]").val(handlerValue.rejectOnCheckItemNotFound);
	}
	
	if(handlerValue.acceptOnPolicyOnFound == true){
		$('#'+uniqueId).find("input:checkbox[name=acceptOnPolicyOnFound]").attr('checked', true);
		$('#'+uniqueId).find("input:checkbox[name=acceptOnPolicyOnFound]").val(handlerValue.acceptOnPolicyOnFound);
	}else if(handlerValue.acceptOnPolicyOnFound == false){
		$('#'+uniqueId).find("input:checkbox[name=acceptOnPolicyOnFound]").attr('checked', false);
		$('#'+uniqueId).find("input:checkbox[name=acceptOnPolicyOnFound]").val(handlerValue.acceptOnPolicyOnFound);
	}
	
	if(handlerValue.rejectOnRejectItemNotFound == true){
		$('#'+uniqueId).find("input:checkbox[name=rejectOnRejectItemNotFound]").attr('checked', true);
		$('#'+uniqueId).find("input:checkbox[name=rejectOnRejectItemNotFound]").val(handlerValue.rejectOnRejectItemNotFound);
	}else if(handlerValue.rejectOnRejectItemNotFound == false){
		$('#'+uniqueId).find("input:checkbox[name=rejectOnRejectItemNotFound]").attr('checked', false);
		$('#'+uniqueId).find("input:checkbox[name=rejectOnRejectItemNotFound]").val(handlerValue.rejectOnRejectItemNotFound);
	}
	
	$('#'+uniqueId).find("select[name=gracePolicy]").val(handlerValue.gracePolicy);
}

function addProfileLookupHandler(handlerTable,handlerValue){
	var htmlcontent = '';
	htmlcontent=getNewHandlerData("ProfileLookupDriver",handlerTable);
	
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	
	$('#'+handlerTable).find('.commandcode-flow-table').append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');

	if(handlerValue.enabled === 'true'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	
	}else if(handlerValue.enabled == 'false'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
 		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}
	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	
	$('#'+uniqueId).find("select[name=stripIdentity]").val(handlerValue.stripIdentity);
	$('#'+uniqueId).find("input[name=separator]").val(handlerValue.separator);
	$('#'+uniqueId).find("select[name=iCase]").val(handlerValue.iCase);
	
	if( handlerValue.trimIdentity == true ){
		$('#'+uniqueId).find("input:checkbox[name=trimUserIdentity]").attr('checked', true);
		$('#'+uniqueId).find("input:checkbox[name=trimUserIdentity]").val(handlerValue.trimIdentity);
	}else if(handlerValue.trimIdentity == false){
		$('#'+uniqueId).find("input:checkbox[name=trimUserIdentity]").attr('checked', false);
		$('#'+uniqueId).find("input:checkbox[name=trimUserIdentity]").val(handlerValue.trimIdentity);
	}
	
	if( handlerValue.trimPassword == true ){
		$('#'+uniqueId).find("input:checkbox[name=trimPassword]").attr('checked', true);
		$('#'+uniqueId).find("input:checkbox[name=trimPassword]").val(handlerValue.trimPassword);
	}else if(handlerValue.trimPassword == false){
		$('#'+uniqueId).find("input:checkbox[name=trimPassword]").attr('checked', false);
		$('#'+uniqueId).find("input:checkbox[name=trimPassword]").val(handlerValue.trimPassword);
	}
	
	var widgetId = $('#'+uniqueId).find("input:hidden[name=widgetId]").val();
	
	if (typeof handlerValue.primaryDriverGroupList != 'undefined'){
		
		var selecteddriverIds = $('#'+uniqueId).find("select[name=selecteddriverIds]");
		
		$.each(handlerValue.primaryDriverGroupList, function(key1,value1){
			var driverName = '';
			
			$.each(driverListArray, function(driverKey,driverValue){
				if(driverValue.driverInstanceId == value1.driverInstanceId){
					driverName=driverValue.driverName;
				}
			});
			
			var optionvalue = value1.driverInstanceId +"("+value1.weightage+")";
			var optionDisplayValue = driverName +"-W-"+value1.weightage;
			var optionTag = "<option id='"+value1.driverInstanceId+"' value='"+optionvalue+"'>"+optionDisplayValue+"</option>";
			$(selecteddriverIds).append(optionTag);
			
		  	$("#addDriver"+widgetId+" "+"#rowdriverDataCheckBoxId"+widgetId+ value1.driverInstanceId).hide();
		 	$("#addCacheDriver"+widgetId+" #cacherowdriverDataRadioId"+widgetId+ value1.driverInstanceId).hide();		
        	$("#additionalDriver"+widgetId+" #additionalrowadditionalDriverCheckBoxId"+widgetId+ value1.driverInstanceId).hide();
			
		});
		
	}
	
	if (typeof handlerValue.secondaryDriverGroupList != 'undefined'){
		var selectedCacheDriverIds =  $('#'+uniqueId).find("select[name=selectedCacheDriverIds]");
		$.each(handlerValue.secondaryDriverGroupList, function(key1,value1){
			var driverName = '';
			$.each(driverListArray, function(driverKey,driverValue){
				if(driverValue.driverInstanceId == value1.secondaryDriverId){
					driverName=driverValue.driverName;
				}
			});
			
			var optionvalue = value1.secondaryDriverId;
			var optionDisplayValue = driverName;
			var optionTag = "<option id='"+value1.secondaryDriverId+"' value='"+optionvalue+"'>"+optionDisplayValue+"</option>";
			$(selectedCacheDriverIds).append(optionTag);
			
			$("#addDriver"+widgetId+" "+"#rowdriverDataCheckBoxId"+widgetId+ value1.secondaryDriverId).hide();
		 	$("#addCacheDriver"+widgetId+" #cacherowdriverDataRadioId"+widgetId+ value1.secondaryDriverId).hide();		
        	$("#additionalDriver"+widgetId+" #additionalrowadditionalDriverCheckBoxId"+widgetId+ value1.secondaryDriverId).hide();
		});
	}
	
	if (typeof handlerValue.additionalDriverList != 'undefined'){

		var selectedAdditionalDriverIds = $('#'+uniqueId).find("select[name=selectedAdditionalDriverIds]");
		$.each(handlerValue.additionalDriverList, function(key1,value1){
			var driverName = '';
			$.each(driverListArray, function(driverKey,driverValue){
				if(driverValue.driverInstanceId == value1.driverId){
					driverName=driverValue.driverName;
				}
			});
			
			var optionvalue = value1.driverId;
			var optionDisplayValue = driverName;
			var optionTag = "<option id='"+optionvalue+"' value='"+optionvalue+"'>"+optionDisplayValue+"</option>";
			$(selectedAdditionalDriverIds).append(optionTag);
			
		  	$("#addDriver"+widgetId+" "+"#rowdriverDataCheckBoxId"+widgetId+ value1.driverId).hide();
		 	$("#addCacheDriver"+widgetId+" #cacherowdriverDataRadioId"+widgetId+ value1.driverId).hide();		
        	$("#additionalDriver"+widgetId+" #additionalrowadditionalDriverCheckBoxId"+widgetId+ value1.driverId).hide();
		});
	}
	
	$('#'+uniqueId).find("input[name=anonymousProfileIdentity]").val(handlerValue.anonymousProfileIdentity);
	
	console.log("Driver Script : " +handlerValue.driverScript);
	$('#'+uniqueId).find("input[name=driverScript]").val(handlerValue.driverScript);
	
}

function addPluginHandler(handlerTable,handlerValue, className){
	var htmlcontent = '';
	htmlcontent=getNewHandlerData("Plugin",handlerTable);
	
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	
	$('#'+handlerTable).find('.'+className).append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');

	if(handlerValue.enabled === 'true'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	
	}else if(handlerValue.enabled === 'false'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
 		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}
	
	var widgetId = $('#'+uniqueId).find("input:hidden[name=widgetId]").val();
	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	
	if (typeof handlerValue.pluginEntries != 'undefined'){
		$.each(handlerValue.pluginEntries, function(key1,value1){
			addNewPluginRow("pluginTemplate_"+widgetId,"mappingtblplugin_"+widgetId);
			
			//set Ruleset Data
			var rulesetrow = $("#mappingtblplugin_"+widgetId+" tr:last").find("input[name='ruleset']");
			$(rulesetrow).val(value1.ruleset);
			
			//set PluginData
			var pluginrow = $("#mappingtblplugin_"+widgetId+" tr:last").find("input[name='pluginName']");
			$(pluginrow).val(value1.pluginName);
			
			//set Plugin Argument
			var pluginArgumentrow = $("#mappingtblplugin_"+widgetId+" tr:last").find("input[name='pluginArgument']");
			$(pluginArgumentrow).val(value1.pluginArgument);
			
			//set PluginData
			var isResponseTyperow = $("#mappingtblplugin_"+widgetId+" tr:last").find("select[name='requestType']");
			
			if( value1.onResponse == true){
				$(isResponseTyperow).val("true");
			}else if(value1.onResponse == false){
				$(isResponseTyperow).val("false");
			}
		});
	}
}

function addProxyCommunicationHandler(handlerTable,handlerValue){
	
	var htmlcontent = '';

	if( handlerValue.protocol == 'DIAMETER'){
		htmlcontent=getNewHandlerData("DiameterProxyCommunication",handlerTable);
	}else if( handlerValue.protocol == 'RADIUS'){
		htmlcontent=getNewHandlerData("RadiusProxyCommunication",handlerTable);
	}
	
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	
	$('#'+handlerTable).find('.commandcode-flow-table').append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');

	if(handlerValue.enabled === 'true'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	
	}else if(handlerValue.enabled === 'false'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
 		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}
	
	if( handlerValue.protocol == 'DIAMETER'){
		$('#'+uniqueId).find("select[name=proxyMode]").val(handlerValue.proxyMode);
	}
	
	var widgetId = $('#'+uniqueId).find("input:hidden[name=widgetId]").val();
	
	if (typeof handlerValue.priorityResultCodes != 'undefined'){
		$('#'+uniqueId).find("input[name=priorityResultCodes]").val(handlerValue.priorityResultCodes);
	}
	
	if (typeof handlerValue.resultCodeWhenNoEntrySelected != 'undefined'){
		$('#'+uniqueId).find("input[name=resultCodeWhenNoEntrySelected]").val(handlerValue.resultCodeWhenNoEntrySelected);
	}
	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	
	if (typeof handlerValue.entries != 'undefined'){
		
		$.each(handlerValue.entries, function(key1,value1){
		
			addNewPluginRow("radiusProxyCommunicationTemplate_"+widgetId,"radiusProxyCommunicationTbl_"+widgetId);
			
			//set Ruleset Data
			var rulesetrow = $("#radiusProxyCommunicationTbl_"+widgetId+" tr:last").find("input[name='ruleset']");
			$(rulesetrow).val(value1.ruleset);
			
			//set Translation Mapping
			var translationMappingrow = $("#radiusProxyCommunicationTbl_"+widgetId+" tr:last").find("select[name='translationMappingName']");
			$(translationMappingrow).val(value1.translationMapping);
			
			//set Peer Group Id
			var peerGrouprow = $("#radiusProxyCommunicationTbl_"+widgetId+" tr:last").find("select[name='peerGroupId']");
			$(peerGrouprow).val(value1.peerGroupId);
			
			//set Script data
			var script = $("#radiusProxyCommunicationTbl_"+widgetId+" tr:last").find("input[name='script']");
			$(script).val(value1.script);
			
			//Set Acceptable Result Code
			var acceptedResultCode = $("#radiusProxyCommunicationTbl_"+widgetId+" tr:last").find("input[name='acceptedResultCode']");
			$(acceptedResultCode).val(value1.acceptableResultCodes);
			
		});
	}
}

function addBroadcastCommunicationHandler(handlerTable,handlerValue){
	var htmlcontent = '';
	
	if( handlerValue.protocol == 'DIAMETER'){
		htmlcontent=getNewHandlerData("DiameterBroadcastingCommunication",handlerTable);
	}else if( handlerValue.protocol == 'RADIUS'){
		htmlcontent=getNewHandlerData("RadiusBroadcastingCommunication",handlerTable);
	}
	
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	
	$('#'+handlerTable).find('.commandcode-flow-table').append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');

	if(handlerValue.enabled === 'true'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	
	}else if(handlerValue.enabled === 'false'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
 		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}
	
	var widgetId = $('#'+uniqueId).find("input:hidden[name=widgetId]").val();
	
	if (typeof handlerValue.priorityResultCodes != 'undefined'){
		$('#'+uniqueId).find("input[name=priorityResultCodes]").val(handlerValue.priorityResultCodes);
	}
	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	
	if (typeof handlerValue.entries != 'undefined'){
		
		$.each(handlerValue.entries, function(key1,value1){
		
			addNewPluginRow("radiusBroadcastCommunicationTemplate_"+widgetId,"broadcastCommunicationTbl_"+widgetId);
			
			//set Ruleset Data
			var rulesetrow = $("#broadcastCommunicationTbl_"+widgetId+" tr:last").find("input[name='ruleset']");
			$(rulesetrow).val(value1.ruleset);
			
			//set Translation Mapping
			var translationMappingrow = $("#broadcastCommunicationTbl_"+widgetId+" tr:last").find("select[name='translationMappingName']");
			$(translationMappingrow).val(value1.translationMapping);
			
			//set Peer Group Id
			var peerGrouprow = $("#broadcastCommunicationTbl_"+widgetId+" tr:last").find("select[name='peerGroupId']");
			$(peerGrouprow).val(value1.peerGroupId);
			
			//set Script data
			var script = $("#broadcastCommunicationTbl_"+widgetId+" tr:last").find("input[name='script']");
			$(script).val(value1.script);
			
			//Set Acceptable Result Code
			var acceptedResultCode = $("#broadcastCommunicationTbl_"+widgetId+" tr:last").find("input[name='acceptedResultCode']");
			$(acceptedResultCode).val(value1.acceptableResultCodes);
			
			//Set Wait for Response
			var waitForResponse = $("#broadcastCommunicationTbl_"+widgetId+" tr:last").find("input:checkbox[name='waitForResponse']");
			
			if(value1.wait == true){
				$(waitForResponse).val(value1.wait);
				$(waitForResponse).attr('checked', true);
			}else{
				$(waitForResponse).val(value1.wait);
				$(waitForResponse).attr('checked', false);
			}
		});
	}
	
}
/**
 * This function used to add concurrency handler 
 * @param handlertable contains handler table
 * @param handlerValue contains handler value
 * @param className help to find elemenet
 */
function addConcurrencyHandler(handlerTable,handlerValue, className){

	var htmlcontent = '';
	
	htmlcontent=getNewHandlerData("ConcurrencyHandler",handlerTable);
	
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	
	$('#'+handlerTable).find('.'+className).append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');
	var widgetId = $('#'+uniqueId).find("input:hidden[name=widgetId]").val();
	
	if(handlerValue.enabled === 'true'){
		
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	}else if(handlerValue.enabled === 'false'){
		
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		$(handlerObject).find('tr').each(function(){
			
			$(this).addClass('disable-class');
		});
	}	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	if (typeof handlerValue.ruleset != 'undefined'){
		var rulesetrow = $("#concurrency_"+widgetId+"").find("input[name='ruleset']");
		$(rulesetrow).val(handlerValue.ruleset);	
	}

	if (typeof handlerValue.diaConConfigId != 'undefined'){
		var diaConConfigId=$("#concurrency_"+widgetId+"").find("select[name=diaConConfigId]");
		$(diaConConfigId).val(handlerValue.diaConConfigId);
	}
	
}
/* This function is used for adding CDR Gen Handler data */
function addCDRGenHandler(handlerTable,handlerValue,className){
	var htmlcontent=getNewHandlerData("CDRHandler",handlerTable);
	
	var uuId = createUUID();
	var uniqueId = "Row" + "_" + uuId;
	
	$('#'+handlerTable).find('.'+className).append('<tr id='+uniqueId+'><td class="handler-css">'+htmlcontent+'</td></tr>');

	if(handlerValue.enabled === 'true'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', true);
		$('#'+uniqueId).find('.is-handler-enabled').val('true');
	
	}else if(handlerValue.enabled === 'false'){
	
		$('#'+uniqueId).find('.is-handler-enabled').attr('checked', false);
		$('#'+uniqueId).find('.is-handler-enabled').val('false');
 		
		var handlerObject=$('#'+uniqueId).find('.is-handler-enabled').closest('table[class^="handler-class"]');
		
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-class');
		});
	}
	
	var widgetId = $('#'+uniqueId).find("input:hidden[name=widgetId]").val();
	
	if(typeof handlerValue.handlerName != 'undefined' && handlerValue.handlerName.length > 0 ){
		setHandlerProp(uniqueId,handlerValue.handlerName);
	}
	
	if (typeof handlerValue.entries != 'undefined'){
		
		$.each(handlerValue.entries, function(key1,value1){
		
			addNewCDRRow("cdrTemplate_"+widgetId,"mappingtblcdr_"+widgetId,true);
			
			//set Ruleset Data
			var rulesetrow = $("#mappingtblcdr_"+widgetId+" tr:last").find("input[name='ruleset']");
			$(rulesetrow).val(value1.ruleset);
			
			//set Primary Driver
			var primaryDriverId = $("#mappingtblcdr_"+widgetId+" tr:last").find("select[name='primaryDriverId']");
			$(primaryDriverId).append(generateOptionFromJSON(String(value1.secondaryDriverId)));
			$(primaryDriverId).val(value1.primaryDriverId);
			
			//set Secondary Id
			var secondaryDriverId = $("#mappingtblcdr_"+widgetId+" tr:last").find("select[name='secondaryDriverId']");
			$(secondaryDriverId).append(generateOptionFromJSON(String(value1.primaryDriverId)));
			$(secondaryDriverId).val(value1.secondaryDriverId);
			
			//set Driver Script data
			var script = $("#mappingtblcdr_"+widgetId+" tr:last").find("input[name='driverScript']");
			$(script).val(value1.driverScript);
			
			//Set Wait for Response
			var waitForResponse = $("#mappingtblcdr_"+widgetId+" tr:last").find("input:checkbox[name='wait']");
			
			if(value1.wait == true){
				$(waitForResponse).val(value1.wait);
				$(waitForResponse).attr('checked', true);
			}else{
				$(waitForResponse).val(value1.wait);
				$(waitForResponse).attr('checked', false);
			}
		});
	}
}

/* This function fetches JSON data from input hidden object and render TGPP Handler details on ready of Update jsp.*/
function fetchHandlers(){
	  var jsonDataObj=JSON.parse($('#tgppCommandCodeJSON').val());
		
	  $.each(jsonDataObj, function(commandCodeFlowKey,commandCodeFlowValue){
			
		  if(commandCodeFlowKey == 'commandCodeList'){
				
				$.each(commandCodeFlowValue, function( index, value ){
					   var serviceHandlerTableId = addCommandCodeFlow(value.name,value.commandCode,value.interfaceId,isView);
					   /* Command Code Flow */
					   var handlerList = value.handlerList;
					   jQuery.each(value.handlerList, function(handlerIndex, handlerValue) {
						   getHandlerInfo(serviceHandlerTableId,handlerValue);
					   });
					   /* Post Response Handler Command Code Flow */
					   jQuery.each(value.postResponseHandlerList, function(handlerIndex, handlerValue) {
						   getPostHandlerInfo(serviceHandlerTableId,handlerValue);
					   });
				});
			}
	  }); 
	  
	  /* Script Autocomplete */
	  setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	  setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
}