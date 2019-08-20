/** Global Parameters*/
var universalPluginPolicyJson = [];

/** This function is used for initialized Sortable for Universal Pre and post Policy 
 *  and manage remove mappings from the parameter list */
function initializeUniversalPlugin(){
	
	/*  Universal Pre Plugin Handlers  */
	$("#plugin-mapping-table tbody.parent").sortable({
		placeholder: 'ui-state-highlight',
		helper: 'clone',
		start: function(e, ui){
			var height = ui.item.height();
			ui.placeholder.height(ui.item.height());
			ui.placeholder.css({'padding':'10px'});
			ui.placeholder.css({'background-color':'white'});
			ui.placeholder.html("<div style='margin:10px;font-family: serif;border:1px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;color:gray;'>Drag & Drop Policy</div>");
		}
	});
	
	/*  Universal Post Plugin Handlers  */
	$("#plugin-post-mapping-table tbody.parent").sortable({
		placeholder: 'ui-state-highlight',
		helper: 'clone',
		start: function(e, ui){
			var height = ui.item.height();
			ui.placeholder.height(ui.item.height());
			ui.placeholder.css({'padding':'10px'});
			ui.placeholder.css({'background-color':'white'});
			ui.placeholder.html("<div style='margin:10px;font-family: serif;border:1px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;color:gray;'>Drag & Drop Policy</div>");
		}
	});
}


/** This function is used for adding new plugin in corresponding mapping table */
function addUniversalPlugin(mappingTable, templateTable, mappingChildTableClass, templateChildTableId) {
	
	var tableRowStr = $("#" + templateTable).find("tr");
	$("#" + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
	var nestedTableObj = $("#" + mappingTable).find(' > tbody > tr ').last().find('.'+mappingChildTableClass);
	var nestedTablRow = $('#'+templateChildTableId).find("tr");
	$(nestedTableObj).find(' > tbody:last-child').append( "<tr>" + $(nestedTablRow).html() +"</tr>" );

	/* Handler Enabled/Disabled Code */
	var inputElement = $("#" + mappingTable).find(' > tbody > tr ').last().find('.is-handler-enabled');
	var newIds = getId();
	$(inputElement).attr("id",newIds);
	$(inputElement).parent().find('.handlerLabel').attr('for',newIds);
	
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

/** This function is used for adding new plugin in corresponding mapping table */
function addDefaultUniversalPlugin(mappingTable, templateTable, mappingChildTableClass, templateChildTableId) {
	
	var tableRowStr = $("#" + templateTable).find("tr");
	$("#" + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
	var nestedTableObj = $("#" + mappingTable).find(' > tbody > tr ').last().find('.'+mappingChildTableClass);
	var nestedTablRow = $('#'+templateChildTableId).find("tr");
	$(nestedTableObj).find(' > tbody:last-child').append( "<tr>" + $(nestedTablRow).html() +"</tr>" );

	/* Handler Enabled/Disabled Code */
	var inputElement = $("#" + mappingTable).find(' > tbody > tr ').last().find('.is-handler-enabled');
	var newIds = getId();
	$(inputElement).attr("id",newIds);
	$(inputElement).parent().find('.handlerLabel').attr('for',newIds);
	
}

/** This function is used for adding new ParameterList in Universal Plugin Parameter Table */
function addParameterList(mappingTable, templateTable, addObj) {
	
	var tableRowStr = $("#" + templateTable).find("tr");
	var tableObj = $(addObj).closest('table').find('.'+mappingTable);
	$(tableObj).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );

}

/** This function is used for fetching all the plugin configuration */
function fetchPreAndPostPluginData(){
	
	var prePolicyLists = [];
	$('#plugin-mapping-table').find('.universal_pre_plugin_div').each(function(){
		
		var name = $(this).find('input[name=name]').val();
		var action = $(this).find("select[name='action']").val();
		var enabled = $(this).find("input[name='isHandlerEnabled']").val();
		var parameterTableObject = $(this).find('.pre-param-list-tbl-mapping');
		var parameterListData = [];
		
		parameterTableObject.find('tr').each(function(){
			
			var active 			= null,
				packet_type 	= null,
				attr_id			= null,
				attribute_value	= null,
				parameter_usage	= null;
			
			 if(typeof $(this).find("select[name='active']").val() !== 'undefined'){
				 active =  $(this).find("select[name='active']").val();
			 }
			
			 if(typeof $(this).find("select[name='packet_type']").val() !== 'undefined'){
				 packet_type =  $(this).find("select[name='packet_type']").val();
			 }
			 
			 if(typeof $(this).find("input[name='attr_id']").val() !== 'undefined'){
				 attr_id =  $(this).find("input[name='attr_id']").val();
			 }
			
			 if(typeof $(this).find("input[name='attribute_value']").val() !== 'undefined'){
				 attribute_value =  $(this).find("input[name='attribute_value']").val();
			 }
		
			 if(typeof $(this).find("select[name='parameter_usage']").val() !== 'undefined'){
				 parameter_usage =  $(this).find("select[name='parameter_usage']").val();
			 }
			 
			 if( !isEmpty(active) || !isEmpty(packet_type) || !isEmpty(attr_id) || !isEmpty(attribute_value) || !isEmpty(parameter_usage) ){
				 parameterListData.push({'active':active,'packet_type':packet_type,'attr_id':attr_id,'attribute_value':attribute_value,'parameter_usage':parameter_usage});
			 }
		});
		
		if(parameterListData.length < 1){
			parameterListData = [];
		}
		
		prePolicyLists.push({
				'name' 							: name,
				'action' 						: action,
				'parameterDetailsForPlugin'     : parameterListData,
				'enabled'						: enabled
		});
		
	});

	var postPolicyLists = [];
	$('#plugin-post-mapping-table').find('.universal_post_plugin_div').each(function(){
		
		var name = $(this).find('input[name=name]').val();
		var action = $(this).find("select[name='action']").val();
		var enabled = $(this).find("input[name='isHandlerEnabled']").val();
		var parameterTableObject = $(this).find('.post-param-list-tbl-mapping');
		var parameterListData = [];
		
		parameterTableObject.find('tr').each(function(){
			
			var active 			= null,
				packet_type 	= null,
				attr_id			= null,
				attribute_value	= null,
				parameter_usage	= null;
			
			 if(typeof $(this).find("select[name='active']").val() !== 'undefined'){
				 active =  $(this).find("select[name='active']").val();
			 }
			
			 if(typeof $(this).find("select[name='packet_type']").val() !== 'undefined'){
				 packet_type =  $(this).find("select[name='packet_type']").val();
			 }
			 
			 if(typeof $(this).find("input[name='attr_id']").val() !== 'undefined'){
				 attr_id =  $(this).find("input[name='attr_id']").val();
			 }
			
			 if(typeof $(this).find("input[name='attribute_value']").val() !== 'undefined'){
				 attribute_value =  $(this).find("input[name='attribute_value']").val();
			 }
		
			 if(typeof $(this).find("select[name='parameter_usage']").val() !== 'undefined'){
				 parameter_usage =  $(this).find("select[name='parameter_usage']").val();
			 }
			 
			 if( !isEmpty(active) || !isEmpty(packet_type) || !isEmpty(attr_id) || !isEmpty(attribute_value) || !isEmpty(parameter_usage) ){
				 parameterListData.push({'active':active,'packet_type':packet_type,'attr_id':attr_id,'attribute_value':attribute_value,'parameter_usage':parameter_usage});
			 }
		});
		
		if(parameterListData.length < 1){
			parameterListData = [];
		}
		
		postPolicyLists.push({
				'name' 							: name,
				'action' 						: action,
				'parameterDetailsForPlugin'     : parameterListData,
				'enabled'						: enabled
		});
	});
	
	var policyName = $('#pluginName').val();
	
	var description = $('#description').val();
	
	/* Create Universal Plugin Policy JSON Object*/
	universalPluginPolicyJson.push({
		'name' 			: policyName,
		'description' 	: description,
		'inPluginList' 	: prePolicyLists,
		'outPluginList' : postPolicyLists
	});
	
	$('#universalPluginPolicyJson').val(JSON.stringify(universalPluginPolicyJson));
	
}

/** This function is used validating all the required parameters */
function validatePlugin(){
	fetchPreAndPostPluginData();
	
	if( !validateUniversalInPluginData() ){
		return false;
	}else if( !validateUniversalOutPluginData() ){
		return false;
	}else if( !isUnique("policy-name") ){
		alert("Policy Name should be unique for each universal policy");
		return false;
	}else{
		document.forms['uniAuthPlugin'].submit();
	}
}

function validateUniversalInPluginData(){
var returnValue = true;
	
	$('#plugin-mapping-table').find('.universal_pre_plugin_div').each(function(){
		
		if(typeof $(this).find("input[name='name']").val() !== 'undefined' && $(this).find("input[name='name']").val().length <= 0 ){
			alert('Please Enter Policy Name');
			$(this).find("input[name='name']").focus();
			returnValue = false;
			return false;
		}
		
		var parameterTableObject = $(this).find('.pre-param-list-tbl-mapping');
		var totalRows = parameterTableObject.find('tr').length;
		var valueIsConfigured = false;
		
		if( totalRows == 1){
			valueIsConfigured= false;
		}else if(totalRows > 1){
			valueIsConfigured = true;
		}
		
		if(valueIsConfigured == false){
			alert('Atleast one mapping is required in Universal Policy');
			$(parameterTableObject).closest('.light-btn').focus();
			returnValue = false;
			return false;
		}
		
	});
	
	return returnValue;
}

function validateUniversalOutPluginData(){
var returnValue = true;
	
	$('#plugin-post-mapping-table').find('.universal_post_plugin_div').each(function(){
		
		if(typeof $(this).find("input[name='name']").val() !== 'undefined' && $(this).find("input[name='name']").val().length <= 0 ){
			alert('Please Enter Policy Name');
			$(this).find("input[name='name']").focus();
			returnValue = false;
			return false;
		}
		
		var parameterTableObject = $(this).find('.post-param-list-tbl-mapping');
		var totalRows = parameterTableObject.find('tr').length;
		var valueIsConfigured = false;
		
		if( totalRows == 1){
			valueIsConfigured= false;
		}else if(totalRows > 1){
			valueIsConfigured = true;
		}
		
		if(valueIsConfigured == false){
			alert('Atleast one mapping is required in Universal Policy');
			$(parameterTableObject).closest('table').find('.light-btn').focus();
			returnValue = false;
			return false;
		}
		
	});
		
	return returnValue;
}

function isUnique(className) {
	var arrayIndex = 0;
	var arrayData = [];
	$('#plugin-mapping-table').find('.universal_pre_plugin_div').each(function(){
		
		if(typeof $(this).find("input[name='name']").val() !== 'undefined' && $(this).find("input[name='name']").val().length > 0 ){
			var nameValue = $(this).find("input[name='name']").val();
			arrayData[arrayIndex++] = nameValue;
		}
	});
	
	$('#plugin-post-mapping-table').find('.universal_post_plugin_div').each(function(){
		if(typeof $(this).find("input[name='name']").val() !== 'undefined' && $(this).find("input[name='name']").val().length > 0 ){
			var nameValue = $(this).find("input[name='name']").val();
			arrayData[arrayIndex++] = nameValue;
		}
	});
	
	for(var i=0; i<arrayData.length; i++ ) {
		for(var j=i+1; j<arrayData.length; j++ ){
			if(arrayData[i] == arrayData[j]) {
				return false;
			}
		}
	}
	
	return true;
}


function deleteHandler(handlerId){
	var deleteResult = confirm('Are you sure you want to delete this Plugin?');
	if(deleteResult){
		var $row = $(handlerId).closest('table[class^="universal-plugin-table"]');
		$($row).parent().parent().remove();
	}
}

function expandCollapse(tdObj){
	var $img=$(tdObj).find('img');
	var tableObj=$(tdObj).closest('table[class^="universal-plugin-table"]');
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

/* This method is used for initialized plugin based on json data */
function initializedPlugins(universalPluginJson) {
	$.each($.parseJSON(universalPluginJson), function(key,value){
		
		if (key === 'inPluginList') {
			var prePluginLists = value;
			initializedPrePluginList(prePluginLists);
		} else if (key === 'outPluginList') {
			var postPluginLists = value;
			initializedPostPluginList(postPluginLists);
		}
	});
}

/* Initialized Pre Plugin List */
function initializedPrePluginList(prePluginLists){
	
	$.each(prePluginLists, function(handlerKey,handlerData){
		addPreInitializedUniversalPlugin('plugin-mapping-table', 'plugin-pre-mapping-table-template', handlerData, 'pre-param-list-tbl-mapping','pre-param-list-tbl-template');
	});
}

/* Initialized Post Plugin List */
function initializedPostPluginList(postPluginLists){
	
	$.each(postPluginLists, function(handlerKey,handlerData){
		addPreInitializedUniversalPlugin('plugin-post-mapping-table', 'plugin-post-mapping-table-template', handlerData, 'post-param-list-tbl-mapping','post-param-list-tbl-template');
	});

}

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
				var element = $("#" + mappingTable +" > tbody > tr:last-child").find("input[name='name']");
				$(element).val(value);
			}else if( key == 'action'){
				var element = $("#" + mappingTable +" > tbody > tr:last-child").find("select[name='action']");
				$(element).val(value);
			}else if(key == 'enabled'){
				var element = $("#" + mappingTable +" > tbody > tr:last-child").find("input[name='isHandlerEnabled']"); 
				value = value.toString();
				$(element).val(value);
				
				if( value == "true" ){
	        		$(element).attr('checked', true);
	        		$(element).val('true');
	        	}else{
	        		$(element).attr('checked', false);
	        		$(element).val('false');
	        		var handlerObject=$(element).closest('table[class^="universal-plugin-table"]');
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
		}
	});
}

function addPreParameterList (mappingTable, templateTable, mappingValue, parentMappingTable ){

	var tableRowStr = $("#" + templateTable).find("tr");

	var tableObj = $('#' + parentMappingTable + ' > tbody > tr').last().find('.' + mappingTable);
	$(tableObj).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );

	$.each(mappingValue, function( key, value ){

		if( key == 'active'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("select[name='active']");
			$(element).val(value);
		}else if( key == 'packet_type'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("select[name='packet_type']");
			$(element).val(value);
		}else if( key == 'attr_id'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("input[name='attr_id']");
			$(element).val(value);
		}else if( key == 'attribute_value'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("input[name='attribute_value']");
			$(element).val(value);
		}else if( key == 'parameter_usage'){
			var element = $(tableObj).find(' > tbody > tr:last-child').find("select[name='parameter_usage']");
			$(element).val(value);
		}
	});
}

function deleteMe(spanObject){
	$(spanObject).parent().parent().remove();
}

function changeValueOfFlow(obj){
	if( $(obj).val() == 'true'){
		$(obj).val('false');
		var handlerObject=$(obj).closest('table[class^="universal-plugin-table"]');
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-toggle-class');
		});
	}else{
		$(obj).val('true');
		var handlerObject=$(obj).closest('table[class^="universal-plugin-table"]');
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

function validatePluginForm(){
	fetchPreAndPostPluginData();

	if($('#pluginName').val() == ""){
		alert('Name must be specified');
		$('#pluginName').focus();
	}else if(!isValidName) {
		alert('Enter Valid Plugin Name');
		$('#pluginName').focus();
		return;
	}else if( !validateUniversalInPluginData() ){
		return false;
	}else if( !validateUniversalOutPluginData() ){
		return false;
	}else if( !isUnique("policy-name") ){
		alert("Policy Name should be unique for each universal policy");
		return false;
	}else{
		document.forms['uniDiameterPlugin'].submit();
	}
}