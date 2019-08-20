
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
	
	$.each(objectValue, function( key, value ){
		
		if( key == 'enabled'){
			var element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("img[class='policy-status']");
			
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
		}else if(key == 'packetType'){
			var spanObj = $("#plugin-mapping-table > tbody > tr:last-child").find("span[class='"+key+"']");
			if( value == '43'){
				$(spanObj).text("CoA");
			}else if( value == '40'){
				$(spanObj).text("DM");
			}
		}else if(key == 'action'){
			var spanObj = $("#plugin-mapping-table > tbody > tr:last-child").find("span[class='"+key+"']");
			if( value == '1'){
				$(spanObj).text("ACCEPT");
			}else if( value == '3'){
				$(spanObj).text("DROP");
			}
		}else if(key == 'ruleset'){
			var spanObj = $("#plugin-mapping-table > tbody > tr:last-child").find("span[class='"+key+"']");
			$(spanObj).html(value);
		}else{
			var spanObj = $("#plugin-mapping-table > tbody > tr:last-child").find("span[class='"+key+"']");
			$(spanObj).text(value);
		}
	});
}

/** This function is used to validate all the fields of quota management */
function validateQuotaMgtPlugin(){
	
	fetchQuotaMgtPluginData();
	
	if($('#pluginName').val() == ""){
		alert('Name must be specified');
		$('#pluginName').focus();
	}else if(!isValidName) {
		alert('Enter Valid Plugin Name');
		$('#pluginName').focus();
		return;
	}else{
		document.forms['quotaManagementPlugin'].submit();
	}
}