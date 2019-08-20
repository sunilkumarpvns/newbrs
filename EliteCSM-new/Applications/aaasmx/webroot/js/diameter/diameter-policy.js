/**
 * Add new row to relavent table
 * @param {Object} Mapping Table where you wants add new row
 * @param {Object} Template Table
 * @return none
 */
function addPluginMapping(mappingTable, templateTable) {
	var tableRowStr = $("." + templateTable).find("tr");
	$("." + mappingTable).find(' > tbody:last-child').append("<tr>" + $(tableRowStr).html() + "</tr>");
}

/**
 * Removing Existing Row
 * @param currentObj 
 * @return none
 */
function deleteMe(currentObj) {
	$(currentObj).parent().parent().remove();
}
	
function splitDbFields(val) {
		return val.split(/[,;]\s*/);
}

function extractLastDbFields(term) {
	return splitDbFields(term).pop();
}

/**
 * Fetch list from "pluginList" and set autocomplete to relavent textbox of plugin
 * @param {Object} dbFieldObject, Object where you wants to apply autocomplete
 * @return none
 */
function setAutocompletePluginData(dbFieldObject, list) {
	$(dbFieldObject).bind("keydown",function(event) {
					if (event.keyCode === $.ui.keyCode.TAB
							&& $(this).autocomplete("instance").menu.active) {
						event.preventDefault();
					}
				}).autocomplete({
						minLength : 0,
						source : function(request, response) {
							response($.ui.autocomplete.filter(list,extractLastDbFields(request.term)));
					},
					focus : function() {
						return false;
					},
					select : function(event, ui) {
						this.value = ui.item.label;
						return false;
					}
				});
}

/**
 * To get plugin data mapping json from the given tableId 
 * @param tableId
 * @param txtAreaName
 * @param inputName
 * @returns JSON
 */
function getPluginDataMapping(tableId,txtAreaName,inputName) {
	var pluginDataMapping = [];
	var returnVal = true;
	
	$('table#'+tableId+' > tbody > tr').each(function() {
		var prePluginNameTxt, prePluginArgumentTxt;

		if (typeof $(this).find("textarea[name='"+txtAreaName+"']").val() !== 'undefined') {
			prePluginArgumentTxt = $(this).find("textarea[name='"+txtAreaName+"']").val();
		}

		if (typeof $(this).find("input[name='"+inputName+"']").val() !== 'undefined') {
			prePluginNameTxt = $(this).find("input[name='"+inputName+"']").val();
		}

		if (!isEmpty(prePluginNameTxt) || !isEmpty(prePluginArgumentTxt)) {
			if (isEmpty(prePluginNameTxt)) {
				alert('Please enter Plugin Name');
				$(this).find("input[name='"+inputName+"']").focus();
				returnVal = false;
				return {'returnVal' : returnVal,'pluginDataMapping' : []};
			} else {
				pluginDataMapping.push({
					'pluginName' : prePluginNameTxt,
					'pluginArgument' : prePluginArgumentTxt
				});
			}
		}
	});
	return {'returnVal' : returnVal,'pluginDataMapping' : pluginDataMapping};
}

/**
 * Validate Plugins if Configured
 * @param preTableId
 * @param postTableId
 * @param preHiddenId
 * @param postHiddenId
 * @returns {Boolean}
 */
function validatePlugins(preTableId,postTableId,preHiddenId,postHiddenId) {
	/* Iterate over #pre-plugin-mapping-table if any plugin configured */
	var prePluginJson = getPluginDataMapping(preTableId,'prePluginArgument','prePluginName');
	if(!prePluginJson.returnVal){
		return false;
	}
	
	/* Iterate over #post-plugin-mapping-table if any plugin configured */
	var postPluginJson = getPluginDataMapping(postTableId,'postPluginArgument','postPluginName');
	if(!postPluginJson.returnVal){
		return false;
	}

	/* Assigning value to hidden fields */
	$('#'+preHiddenId).val(JSON.stringify(prePluginJson.pluginDataMapping));
	$('#'+postHiddenId).val(JSON.stringify(postPluginJson.pluginDataMapping));

	return true;
}