
/** This method is used for initialized plugin based on json data */
function initializedQuotaMgtPlugins(quotaMgtPluginJson) {
	
	$.each($.parseJSON(quotaMgtPluginJson), function(key,value){
		
		if( key == 'pluginsData' ){
			$.each(value, function(objectKey,objectValue){
				addDefaultQuotaPlugin(objectValue);
			});
		}
	});
}

/** This function is used to fetch json string and add handler with its configuration to the mapping table */
function addDefaultQuotaPlugin(objectValue){
	
	var tableRowStr = $("#plugin-mapping-table-template").find("tr");
	$("#plugin-mapping-table").find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
	
	/* Handler Enabled/Disabled Code */
	var inputElement = $("#plugin-mapping-table").find(' > tbody > tr ').last().find('.is-handler-enabled');
	var newIds = getId();
	$(inputElement).attr("id",newIds);
	$(inputElement).parent().find('.handlerLabel').attr('for',newIds);
	
	$.each(objectValue, function( key, value ){
		if ( key == 'prepaidQuotaType' || key == 'furtherProcessing' || key == 'action' ||  key == 'packetType'  ){
			var element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("select[name='"+key+"']");
			$(element).val(value);
		}else if( key == 'keyForVolume' || key == 'keyForTime' || key == 'strAttributes' ||  key == 'name'){
			var element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("input[name='"+key+"']");
			$(element).val(value);
		}else if(key == 'ruleset'){
			var element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("input[name='"+key+"']");
			$(element).val(decodeHtmlEntity(value));
		}else if(key == 'enabled'){
			var element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("input[name='isHandlerEnabled']"); 
			value = value.toString();
			$(element).val(value);
			
			if( value == "true" ){
        		$(element).attr('checked', true);
        		$(element).val('true');
        	}else{
        		$(element).attr('checked', false);
        		$(element).val('false');
        		var handlerObject=$(element).closest('table[class^="quota-mgt-plugin-table"]');
        		$(handlerObject).find('tr').each(function(){
        			$(this).addClass('disable-toggle-class');
        		});
        		
        		$(element).parent().parent().css({'background-color':'#D9E6F6'});
        	}
			
			/* Handler Enabled/Disabled Code */
    		var newIds = getId();
    		$(element).attr("id",newIds);
    		$(element).parent().find('.handlerLabel').attr('for',newIds);
			
		}
	});
}

/** This function is used to validate all the fields of quota management */
function validateQuotaMgtPlugin(){
	if($('#pluginName').val() == ""){
		alert('Name must be specified');
		$('#pluginName').focus();
	}else if(!isValidName) {
		alert('Enter Valid Plugin Name');
		$('#pluginName').focus();
		return;
	}else{
		validatePlugin();
	}
}