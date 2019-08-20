/** Global Parameters*/


/** This function is used for initialized Sortable for Quota Mgt Plugin 
 *  and manage remove mappings from the parameter list */
function initializeQuotaMgtPlugin(){
	
	/*  Universal Pre Plugin Handlers  */
	$("#plugin-mapping-table tbody.parent").sortable({
		placeholder: 'ui-state-highlight',
		helper: 'clone',
		start: function(e, ui){
			var height = ui.item.height();
			ui.placeholder.height(ui.item.height());
			ui.placeholder.css({'padding':'10px'});
			ui.placeholder.css({'background-color':'white'});
			ui.placeholder.html("<div style='margin:10px;font-family: serif;border:1px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;color:gray;'>Drag & Drop Quota Manager Plugin</div>");
		}
	});
}


/** This function is used for adding new plugin in corresponding mapping table */
function addQuotaMgtPlugin(mappingTable, templateTable) {

	var tableRowStr = $("#" + templateTable).find("tr");
	$("#" + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
	
	/* Handler Enabled/Disabled Code */
	var inputElement = $("#" + mappingTable).find(' > tbody > tr ').last().find('.is-handler-enabled');
	var newIds = getId();
	$(inputElement).attr("id",newIds);
	$(inputElement).parent().find('.handlerLabel').attr('for',newIds);
	
	/* Set Focus to newly added handler */
	$('html, body').animate({
        scrollTop: $(inputElement).offset().top - 10
    }, 500);
	
	$(inputElement).closest('div.plugin-policy-background').css({'background-color':'#007CC3'});
	var tableObject = $(inputElement).closest('table');
	
	$(tableObject).find('td').each(function(){
		$(this).css({'background-color':'#007CC3','border-color':'#007CC3','color':'white'});
	});

	//for delete image
	var path = $("#" + mappingTable).find(' > tbody > tr ').last().find('.delele_proxy').attr('src');
	var imgObj = $("#" + mappingTable).find(' > tbody > tr ').last().find('.delele_proxy');
	 
	if(typeof path != undefined){
		
		if (path.indexOf("delete_proxy") > -1) {
			 path=path.replace("delete_proxy","delete-proxy_white");
		}
	 		
	  $(imgObj).attr('src',path);
	}
	
	//for Expand image
	var expandPath = $("#" + mappingTable).find(' > tbody > tr ').last().find('.expand_class').attr('src');
	var expandObj = $("#" + mappingTable).find(' > tbody > tr ').last().find('.expand_class');
	 
	if(typeof expandPath != undefined){
		
		if (expandPath.indexOf("bottom") > -1) {
			expandPath=expandPath.replace("bottom","bottom_white");
		}
		
		$(expandObj).attr('src',expandPath);
	}
	
}

/** This function is mainly used for initializing default plugins */
function addDefaultQuotaMgtPlugin(mappingTable, templateTable) {
	
	var tableRowStr = $("#" + templateTable).find("tr");
	$("#" + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );

}

/** This function is used validating all the required parameters */
function validatePlugin(){
	if(validateQuotaManagement()){
		fetchQuotaMgtPluginData();
		document.forms['quotaManagementPlugin'].submit();
	}
}

/** This function is used for fetching all the plugin configuration */
function fetchQuotaMgtPluginData(){
	
	var quotaMgtPluginLists = [];
	$('#plugin-mapping-table').find('.quota_mgt_plugin_table_div').each(function(){
		
		var name 			    = 	$(this).find('input[name=name]').val();
		var ruleset 			= 	$(this).find('input[name=ruleset]').val();
		var prepaidQuotaType	=	$(this).find('select[name=prepaidQuotaType]').val();
		var furtherProcessing 	= 	$(this).find("select[name='furtherProcessing']").val();
		var action 				= 	$(this).find("select[name='action']").val();
		var packetType 			= 	$(this).find("select[name='packetType']").val();
		var keyForVolume 		= 	$(this).find("input[name='keyForVolume']").val();
		var keyForTime 			= 	$(this).find("input[name='keyForTime']").val();
		var strAttributes 		= 	$(this).find("input[name='strAttributes']").val();
		var enabled				= 	$(this).find("input[name='isHandlerEnabled']").val();
		
		ruleset = encodeHtmlEntity(ruleset);
		quotaMgtPluginLists.push({
			'name'					:	name,
			'ruleset'				: 	ruleset,
			'prepaidQuotaType' 		: 	prepaidQuotaType,
			'furtherProcessing' 	: 	furtherProcessing,
			'action'				: 	action,
			'packetType'     		: 	packetType,
			'keyForVolume'			:	keyForVolume,
			'keyForTime'			:	keyForTime,
			'strAttributes'			:	strAttributes,
			'enabled'				:	enabled
	    });
	});
	
	var policyName = $('#pluginName').val();
	
	var description = $('#description').val();
	
	var status = $('#status').val();
	
	var quotaMgtPluginPolicyJson = [];
	
	/* Create Universal Plugin Policy JSON Object*/
	quotaMgtPluginPolicyJson.push({
		'name' 				: policyName,
		'description' 		: description,
		'status' 			: status,
		'pluginsData' 		: quotaMgtPluginLists
	});
	
	
	$('#quotaMgtJson').val(JSON.stringify(quotaMgtPluginPolicyJson));
}


function deleteHandler(handlerId){
	var deleteResult = confirm('Are you sure you want to delete this policy ?');
	if(deleteResult){
		var $row = $(handlerId).closest('table[class^="quota-mgt-plugin-table"]');
		$($row).parent().parent().remove();
	}
}

function expandCollapse(tdObj){
	var $img=$(tdObj).find('img');
	var tableObj=$(tdObj).closest('table[class^="quota-mgt-plugin-table"]');
	var divObj=$(tableObj).find('.toggleDivs');
	
	$(divObj).css({'border-bottom':'none'});
	$(divObj).slideToggle();
	 
	var path=$($img).attr('src');

	if (path.indexOf("bottom") > -1) {
		 path=path.replace("bottom","up");
	}else{
		 path=path.replace("up","bottom");
	}
	
	$($img).attr('src',path);
}

function deleteMe(spanObject){
	$(spanObject).parent().parent().remove();
}

function changeValueOfFlow(obj){
	if( $(obj).val() == 'true'){
		$(obj).val('false');
		var handlerObject=$(obj).closest('table[class^="quota-mgt-plugin-table"]');
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-toggle-class');
		});
	}else{
		$(obj).val('true');
		var handlerObject=$(obj).closest('table[class^="quota-mgt-plugin-table"]');
		$(handlerObject).find('tr').each(function(){
			$(this).removeClass('disable-toggle-class');
		});
	}
	$(obj).parent().parent().css({'background-color':'none'});
	
}

var incrementValue = 0;
function getId(){
	return "id_"+ incrementValue++;
}

/**
 * This function validates the name of the Quota Manager Policy 
 * @returns true if values are correct else false
 */
function validateQuotaManagement(){
	var isNameValid=true;
	$('#plugin-mapping-table').find('.quota_mgt_plugin_table_div').each(function(){
		var nameObj = $(this).find('input[name=name]');
		var name = nameObj.val();
		if(isEmpty(name)){
			nameObj.focus();
			alert("Quota Manager Policy name cannot be empty");
			isNameValid=false;
			return false;
		}
	});
	return isNameValid;
}