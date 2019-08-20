/**
 * This method is used to add "auto-complete" on Criteria when a mapping is selected. If the mapping is 
 * selected in any of the previous scenario then it will fetch the auto complete suggestion array values from
 * autoCompleteArrayJson else it will make an ajax call to get values of that mapping.
 * @param obj It is the select object from which the mapping has to be selected
 */
function setAutoCompleteForSelectedMapping(obj){
	var criteriaObj = $(obj).parent().parent().find('.scenarioCriteria');
	var mappingValue = $(obj).val();
	var mapId = $(obj).find('option[value='+mappingValue+']').attr('id-value');
	
	if(typeof autoCompleteArrayJson[mappingValue] == 'undefined'){
		retriveDBFieldsForDiameterSessionManagerMap(criteriaObj,mapId,mappingValue)
	} else {
		setAutoCompleteData(criteriaObj,autoCompleteArrayJson[mappingValue]);
	}
}

/**
 * This method makes an ajax call based on <strong>mapId</strong> to get DBField values. Those values are
 * added as auto-complete suggestions in the Criteria. The DBField values is added to autoCompleteArrayJson, 
 * so that when  again the same mapping is selected it does not have to make an ajax call again.
 * @param object The input object for criteria
 * @param mapId Id of the mapping whose DBFeild values you want to fetch
 * @param mappingValue name of the mapping, it is used to store DBFeild values array in autoCompleteArrayJson, 
 * so that it can be used again
 */
function retriveDBFieldsForDiameterSessionManagerMap(object,mapId,mappingValue) {
	var dbFieldStr;
	var dbFieldArray = new Array();
	$.post("FetchMapDBField", {databaseId:dbId, mapId:mapId}, function(data){
		data = data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		dbFieldArray = dbFieldStr.split(", ");
		autoCompleteArrayJson[mappingValue] = dbFieldArray.slice();
		setAutoCompleteData(object,dbFieldArray);
	});	
}

/**
 * This function is used by auto-complete for splitting the values
 */
function splitDbFields( val ) {
	return val.split( /[,;]/ );
}

/**
 * This function is used by auto-complete for splitting the values
 */
function extractLastDbFields( term ) {
	return splitDbFields( term ).pop();
}

/**
 * This function sets auto-complete on the html input object.
 * @param object The object on which you want to set auto-complete
 * @param dbFieldArray List of suggestion values for auto-complete
 */
function setAutoCompleteData(object,dbFieldArray){
	 $(object).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
	 }).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			response( $.ui.autocomplete.filter(
				dbFieldArray, extractLastDbFields( request.term ) ) );
		},
		focus: function() {
			return false;
		},
		select: function( event, ui ) {
			var val = this.value;
			var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
			var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
			 if(commaIndex == semiColonIndex) {
					val = "";
			}  else if(commaIndex > semiColonIndex) {
					val = val.substring(0,commaIndex+1); 
			} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
				val = val.substring(0,semiColonIndex+1); 
			}	 
			this.value = val + ui.item.value ;
			return false;
		}
	});
}

/**
 * This function validates that the value of Criteria is from the mapping. The Criteria value should be from the
 * DBField values from that selected mapping.
 * @param criteriaObj input object that contains criteria
 * @returns true if the value if from DBFeild values else false
 */
function isCriteriaFromMap(criteriaObj){
	var valueToReturn = true;

	var mappingObj = $(criteriaObj).parent().parent().find(".scenarioMapping");
	var mappingValue = $(mappingObj).val();
	
	if(mappingValue != '0'){
		var dbFieldArray = autoCompleteArrayJson[mappingValue];

		var criteriaVal = $(criteriaObj).val();
		var criteriaArray = criteriaVal.split(/,|;/);
		var criteriaArraySize = criteriaArray.length;
		
		for(var i=0; i<criteriaArraySize; i++){
			if(dbFieldArray.indexOf(criteriaArray[i].trim()) == -1){
				alert(criteriaArray[i] + " does not exist in " + mappingValue);
				$(criteriaObj).focus();
				valueToReturn = false;
			}
		}
	}
	return valueToReturn;
}

/**
 * 
 */
function validateScenarioMappingNameIsDuplicateOrNot(){

	var isValidFieldMapping = true;
	var nameArray = new Array();
	var results = [];

	$('.dbScenarioTable').find('.scenarioName').each(function(){
		var nameValue = $.trim($(this).val());
		nameArray.push(nameValue);
		
		var sorted_arr = nameArray.slice().sort();
		for (var i = 0; i < nameArray.length - 1; i++) {
			if (sorted_arr[i + 1] == sorted_arr[i]) {
				results.push(sorted_arr[i]);
			}
			if(results.length > 0){
				alert("Duplicate Scenario Mapping Name " + results + " found");
				isValidFieldMapping = false;
				$(this).focus();
				return false;
			}
		}
	});
	
	return isValidFieldMapping;
}