/** This method is used for initialized plugin based on json data */
function initializedUserStatPostAuthPlugins(userStatPostAuthJson) {
	if(userStatPostAuthJson!=null){
		$.each($.parseJSON(userStatPostAuthJson), function(key,value){
			if( key == 'attributeList' ){
				$.each(value, function(objectKey,objectValue){
					addDefaultUserStatPostAuthPlugin(objectValue);
				});
			}else{
				var element;
				if(key == 'tableName'  ||  key == 'dbQueryTimeoutInMs' || key == 'maxQueryTimeoutCount' || key == 'batchUpdateIntervalInMs'){

					if(key == 'tableName'){
						$('input[name=tblname]').val(value);
					}
					element = $("#plugin-mapping-table-first").find("input[name='"+key+"']");
				}else if(key == 'databaseId'){
					element = $("#plugin-mapping-table-first").find("select[name='databaseId']");
				}
				$(element).val(value);
			}
		});
	}
}
/** This function is mainly used for initializing default plugins */
function addDefaultUserStatPostAuthPlugin(objectValue){
	var tableRowStr = $("#plugin-mapping-table-template").find("tr");
	$("#plugin-mapping-table").find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );

	$.each(objectValue, function( key, value ){
		if ( key == 'attributeId' || key == 'dbField' || key == 'defaultValue' ){
			var element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("input[name='"+key+"']");
			$(element).val(value);
			if(key == 'dbField'){
				$(element).attr("old-value",value);
			}
		}else if(  key == 'packetType' || key == 'useDictionaryValue'  || key == 'dataType' ){
			var element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("select[name='"+key+"']");
			$(element).val(value);
		}
	});
}
/** This function is used to validate all the fields of User Stat Plugin management */
function validateUserStatisticPlugin(){
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