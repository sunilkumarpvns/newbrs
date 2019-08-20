
/** This method is used for initialized plugin based on json data */
function initializedPlugins(universalPluginJson) {
	
	$.each($.parseJSON(universalPluginJson), function(key,value){
		
		if (key === 'prePolicyLists') {
			var prePluginLists = value;
			initializedPrePluginList(prePluginLists);
		} else if (key === 'postPolicyLists') {
			var postPluginLists = value;
			initializedPostPluginList(postPluginLists);
		}
	});
}

/** Initialized Pre Plugin List */
function initializedPrePluginList(prePluginLists){
	
	$.each(prePluginLists, function(handlerKey,handlerData){
		addPreInitializedUniversalPlugin('plugin-mapping-table', 'plugin-pre-mapping-table-template', handlerData, 'pre-param-list-tbl-mapping','pre-param-list-tbl-template');
	});
}

/** Initialized Post Plugin List */
function initializedPostPluginList(postPluginLists){
	
	$.each(postPluginLists, function(handlerKey,handlerData){
		addPreInitializedUniversalPlugin('plugin-post-mapping-table', 'plugin-post-mapping-table-template', handlerData, 'post-param-list-tbl-mapping','post-param-list-tbl-template');
	});

}

/** This function is used for pre Initialized JSON String and convert into relevant html format */
function addPreInitializedUniversalPlugin(mappingTable, templateTable, handlerData, preParamMappingName, preParamTemplateName) {
	
	var tableRowStr = $("#" + templateTable).find("tr");

	$("#" + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
	
	$.each(handlerData, function( key, value ){
		
		if(key === 'parameterDetailsForPlugin'){
			
			var pluginList = value;
			$.each(pluginList, function(pluginKey, pluginValue) {
				addPreParameterList(preParamMappingName, preParamTemplateName, pluginValue, mappingTable);
			});
			
		}else{
			
			if( key == 'name'){
				var element = $("#" + mappingTable +" > tbody > tr:last-child").find("span[class='name']");
				$(element).text(value);
			}else if( key == 'action'){
				var element = $("#" + mappingTable +" > tbody > tr:last-child").find("span[class='action']");
				value = convertAction(value);
				$(element).text(value);
			}else if( key == 'rejectOnCheckedItemNotFound'){
				var element = $("#" + mappingTable +" > tbody > tr:last-child").find("span[class='rejectOnCheckedItemNotFound']");
				$(element).text(value);
			}else if( key == 'enabled'){
				var element = $("#" + mappingTable +" > tbody > tr:last-child").find("img[class='policy-status']");
				
				// Retriving path of image tag
				var path = $(element).attr('src');
				
				if( value == false ){
					if(typeof path != undefined){
						if (path.indexOf("active") > -1) {
							path=path.replace("active","deactive");
						}
						$(element).attr('src',path);
						$(element).attr('title',"Inactive");
						
					}
				}
			}
		}
	});
}

/** This function is used for adding nested parameters into nested mapping table */
function addPreParameterList (mappingTable, templateTable, mappingValue, parentMappingTable ){

	var tableRowStr = $("#" + templateTable).find("tr");

	var tableObj = $('#' + parentMappingTable + ' > tbody > tr').last().find('.' + mappingTable);
	$(tableObj).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );

	$.each(mappingValue, function( key, value ){

		if( key == 'active'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("span[class='active']");
			
			if( value ){
				value=value.toLowerCase();
			}
			
			$(element).text(value);
		}else if( key == 'packet_type'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("span[class='packet_type']");
			value = convertPacketType(value);
			$(element).text(value);
		}else if( key == 'attr_id'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("span[class='attr_id']");
			$(element).text(value);
		}else if( key == 'attribute_value'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("span[class='attribute_value']");
			$(element).text(value);
		}else if( key == 'parameter_usage'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("span[class='parameter_usage']");
			value = convertParameterUsage(value);
			$(element).text(value);
		}
	});
}

/** This function is used for convert packet type digit to character string */
function convertPacketType(packetTypeDigit) {
	var packetType = 'Default';
	if (packetTypeDigit == 4) {
		return 'Accounting Request';
	} else if (packetTypeDigit == 0) {
		return 'Default';
	} else if (packetTypeDigit == 5) {
		return 'Accounting Response';
	}
	return packetType;
}


/** This function is used for convert parameter usage character string */
function convertParameterUsage(parameterUsageChar) {
	var parameterUsage = 'Check Item';
	if (parameterUsageChar == 'C') {
		return 'Check Item';
	} else if (parameterUsageChar == 'A') {
		return 'Dynamical Assign Item';
	} else if (parameterUsageChar == 'F') {
		return 'Filter Item';
	} else if (parameterUsageChar == 'J') {
		return 'Reject Item';
	} else if (parameterUsageChar == 'R') {
		return 'Reply Item';
	} else if (parameterUsageChar == 'U') {
		return 'Update Item';
	} else if (parameterUsageChar == 'V') {
		return 'Value Replace Item';
	}
	return parameterUsage;
}

/** This function is used for convert action digit to relevant String */
function convertAction(actionParam){
	var action = 'none';
	if(actionParam == '1'){
		return 'Accept';
	}else if( actionParam == '3' ){
		return 'Drop';
	}else if( actionParam == '4' ){
		return 'none';
	}else if( actionParam == '5' ){
		return 'Stop';
	}
	return action;
}
