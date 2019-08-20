/** This method is used for initialized plugin based on json data */
function initializedUserStatPostAuthPlugins(userStatPostAuthJson) {
	if(userStatPostAuthJson!=null){
		$.each($.parseJSON(userStatPostAuthJson), function(key,value){
			if( key == 'attributeList' ){
				$.each(value, function(objectKey,objectValue){
					addDefaultUserStatPostAuthPlugin(objectValue);
				});
			}else{
				var spanObj = $("#plugin-mapping-table-first > tbody > tr").find("span[class='"+key+"']");
				$(spanObj).text(value);
			}
		});
	}
}
/** This function is used to fetch json string and add handler with its configuration to the mapping table */
function addDefaultUserStatPostAuthPlugin(objectValue){
	var tableRowSec = $("#plugin-mapping-table-template").find("tr");
	$("#plugin-mapping-table").find(' > tbody:last-child').append( "<tr>" + $(tableRowSec).html() +"</tr>" );

	$.each(objectValue, function( key, value ){
		var spanObj = $("#plugin-mapping-table > tbody > tr:last-child").find("span[class='"+key+"']");
		if(key=="packetType"){
			if(value=="0"){
				$(spanObj).text("Request Packet");
			}else{
				$(spanObj).text("Response Packet");
			}	
		}else{
			$(spanObj).text(value);
		}
	});
}
/** This function is used to validate all the fields of User Statistic Plugin */
function validateUserStatPostAuthPlugin(){
	if($('#pluginName').val() == ""){
		alert('Name must be specified');
		$('#pluginName').focus();
	}else if(!isValidName) {
		alert('Enter Valid Plugin Name');
		$('#pluginName').focus();
		return;
	}else{
		fetchUserStatPostAuthPluginData();
		document.forms['userStatisticPostAuthPlugin'].submit();
	}
}