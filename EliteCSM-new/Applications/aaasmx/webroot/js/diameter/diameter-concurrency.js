/**
 * This validation js file contains the validation script for diameter concurrency
 * Author       : Nayana Rathod
 * 
 */

function customValidate(){
	
	if(isNull(document.forms[0].name.value)){
		alert('Diameter Concurrency Name must be specified');
		document.forms[0].name.focus();
		return false;
	}

	if(!isValidName) {
		alert('Enter Valid Diameter Concurrency Name');
		document.forms[0].name.focus();
		return false;
	}
	
	if($('#databaseDsId').val() == '0'){
		document.forms[0].databaseDsId.focus();
		alert('Please Select Datasource');
	}else if( isNull($('#tableName').val()) ){
		$('#tableName').focus();
		alert('Table Name must be specified');
	}else if( isNull($('#startTimeField').val()) ){
		$('#startTimeField').focus();
		alert('Start time field must be specified');
	}else if( isNull($('#lastUpdateTimeField').val()) ){
		$('#lastUpdateTimeField').focus();
		alert('Last update time field must be specified');
	}else if( isNull( $('#concurrencyIdentityField').val() ) ){
		$('#concurrencyIdentityField').focus();
		alert('Concurrency identity field must be specified');
	}else if( !validateMandatoryMappingsDBFieldName()){
		return;
	}else if( !validateMandatoryMappingsReferringAttribute()){
		return;
	}else if( !validateAdditionalMappingsforDBFieldName()){
		return;
	}else if( !validateAdditionalMappingsReferringAttribute()){
		return;
	}else if( !checkForDuplicateMappings()){
		return;
	}else if( !checkForSessionOverrideFieldsWithMappings()){
		return;
	}else if( isNull($('#sessionOverrideFields').val()) && $('#sessionOverrideAction').val() == 'Generate ASR' ){
		alert('Session Override Field must be specified');
		return;
	}else{
		retriveDbFieldMapping();
		document.forms[0].submit();
	}
}

function addAdditionalMappingRow(mappingTable,templateTable){
	var tableRowStr = $("#"+templateTable).find("tr");
	$("#"+mappingTable +" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+mappingTable +" tr:last").find(".dbFieldName").focus();
	setColumnsOnTextFields();
}

function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseDsId").value;
	retriveTableFieldsForSessionManager(dbId);
	retriveDiameterDictionaryAttributesForSessionManager();
}

function retriveTableFieldsForSessionManager(dbId) {
	var dbFieldStr;
	var dbFieldArray = new Array();
	var tableName = document.getElementById("tableName").value;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		data=data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		dbFieldArray = dbFieldStr.split(", ");
		setDBFields("lastUpdateTimeField",dbFieldArray);
		setDBFields("concurrencyIdentityField", dbFieldArray);
		setDBFields("startTimeField",dbFieldArray);
		setDBFields("dbFieldName",dbFieldArray);
		setSuggestionForSessionOverrideFields("sessionOverrideFields",dbFieldArray);
		
	});	
	return dbFieldArray;
}

function setSuggestionForSessionOverrideFields(txtField,myArray) {
	$( "#"+ txtField).autocomplete({	
			source:function( request, response ) {
				response( $.ui.autocomplete.filter(
						myArray, extractLast( request.term ) ) );
			},
				
			focus: function( event, ui ) {
				return false;
			},
			select: function( event, ui ) {
				var val = this.value;
				var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
				var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
				if(commaIndex == semiColonIndex) {
					val = "";
				}else if(commaIndex > semiColonIndex) {
					val = val.substring(0,commaIndex+1);
				} else {
					val = val.substring(0,semiColonIndex+1);
				}
				this.value = val + ui.item.value ;
				return false;
			}
		});		
}

function setDBFields(field,columnArray) {
	$('.'+ field).autocomplete({
		source: columnArray
	});
}

function retriveDiameterDictionaryAttributesForSessionManager() {
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	var searchNameOrAttributeId="";
	$.post("SearchDiameterAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
		data=data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#,");
		var value;
		var label;
		var desc;
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0].trim();
			label = tmpArray[1];
			var item = new ListItem(value,label); 
			myArray.push(item);
		}	
		
		setDiameterDictionaryReferringAttrib("referringAttribute",myArray);
		return dbFieldArray;
	});
}

function splitReferringAttribute( val ) {
	return val.split( /[,;(]\s*/ );
}
function extractLastReferringAttribute( term ) {
	return splitReferringAttribute( term ).pop();
} 
function setDiameterDictionaryReferringAttrib(txtField,myArray) {
	$('.referringAttribute').autocomplete({	
			source:function( request, response ) {
				response( $.ui.autocomplete.filter(
						myArray, extractLastReferringAttribute( request.term ) ) );
			},
				
			focus: function( event, ui ) {
				return false;
			},
			select: function( event, ui ) {
				var val = this.value;
				var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
				var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
				var bracketIndex=val.lastIndexOf("(") == -1 ? 0 :val.lastIndexOf("(");
				 if(commaIndex == semiColonIndex && bracketIndex == commaIndex ) {
						val = "";
				}else if(commaIndex > semiColonIndex) {
					if(commaIndex < bracketIndex){
						val = val.substring(0,bracketIndex+1);
					}else{
						val = val.substring(0,commaIndex+1); 
					}
				}else if(bracketIndex>commaIndex){
					val = val.substring(0,bracketIndex+1);
				} else {
					val = val.substring(0,semiColonIndex+1);
				}
				this.value = val + ui.item.value ;
				return false;
			}
		});		
}
function retriveDbFieldMapping(){
	var dbFieldMappingsListJson = [];
	
	$('.madatoryMappingsTable').find('tr').each(function(){
		var field='',dbFieldName='',referringAttr='',dataType='',defaultValue='',includeInASR='';
		
		 if(typeof $(this).find("input:hidden[name='field']").val() !== 'undefined'){
			 field =  $(this).find("input:hidden[name='field']").val();
		 }
		 
		 if(typeof $(this).find("input[name='dbFieldName']").val() !== 'undefined'){
			 dbFieldName =  $(this).find("input[name='dbFieldName']").val();
		 }
		 
		 if(typeof $(this).find("input[name='referringAttribute']").val() !== 'undefined'){
			 referringAttr =  $(this).find("input[name='referringAttribute']").val();
		 }
		 
		 if(typeof $(this).find("input:hidden[name='dataType']").val() !== 'undefined'){
			 dataType =  $(this).find("input:hidden[name='dataType']").val();
		 }
		 
	     if(typeof $(this).find("input[name='defaultValue']").val() !== 'undefined'){
	    	defaultValue =  $(this).find("input[name='defaultValue']").val();
		 }
		 
	     if(typeof $(this).find("input:checkbox[name='includeInASR']").val() !== 'undefined'){
	    	 includeInASR =  $(this).find("input:checkbox[name='includeInASR']").val();
		 }

	     if(!isEmpty(field) || !isEmpty(dbFieldName) || !isEmpty(referringAttr) || !isEmpty(dataType) || !isEmpty(defaultValue) || !isEmpty(includeInASR)){
	    	 dbFieldMappingsListJson.push({'field':field,'dbFieldName':dbFieldName,'referringAttribute':referringAttr,'dataType':dataType,'defaultValue':defaultValue,'includeInASR':includeInASR});
		 }
	});
	
	$('.additionalMappingTable').find('tr').each(function(){
		var field='',dbFieldName='',referringAttr='',dataType='',defaultValue='',includeInASR='';
		 
		 if(typeof $(this).find("input[name='dbFieldName']").val() !== 'undefined'){
			 dbFieldName =  $(this).find("input[name='dbFieldName']").val();
		 }
		 
		 if(typeof $(this).find("input[name='referringAttribute']").val() !== 'undefined'){
			 referringAttr =  $(this).find("input[name='referringAttribute']").val();
		 }
		 
		 if(typeof $(this).find("input:hidden[name='dataType']").val() !== 'undefined'){
			 dataType =  $(this).find("input:hidden[name='dataType']").val();
		 }
		 
	     if(typeof $(this).find("input[name='defaultValue']").val() !== 'undefined'){
	    	defaultValue =  $(this).find("input[name='defaultValue']").val();
		 }
		 
	     if(typeof $(this).find("input:checkbox[name='includeInASR']").val() !== 'undefined'){
	    	 includeInASR =  $(this).find("input:checkbox[name='includeInASR']").val();
		 }

	     if( !isEmpty(dbFieldName) || !isEmpty(referringAttr) || !isEmpty(dataType) || !isEmpty(defaultValue) || !isEmpty(includeInASR)){
	    	 dbFieldMappingsListJson.push({'dbFieldName':dbFieldName,'referringAttribute':referringAttr,'dataType':dataType,'defaultValue':defaultValue,'includeInASR':includeInASR});
		 }
	});
	
	$('#lstFieldMapping').val(JSON.stringify(dbFieldMappingsListJson));
}

function changeIncludeInASRValue(checkbox){
	if($(checkbox).attr('checked')){
		$(checkbox).val('true');
	}else{
		$(checkbox).val('false');
	}
}

function validateMandatoryMappingsDBFieldName(){
	var isValid = true;
	$('.madatoryMappingsTable').find('tr').each(function(){
		if(typeof $(this).find("input[name='dbFieldName']").val() !== 'undefined'){
			var dbFieldName =  $(this).find("input[name='dbFieldName']").val();
			if(isNull(dbFieldName)){
				alert('Db Field Name must be specified');
				$(this).find("input[name='dbFieldName']").focus();
				isValid = false;
			}
		}
	});
	return isValid;
}

function validateMandatoryMappingsReferringAttribute(){
	var isValid = true;
	$('.madatoryMappingsTable').find('tr').each(function(){
		
		if(typeof $(this).find("input[name='referringAttribute']").val() !== 'undefined'){
			var referringAttribute =  $(this).find("input[name='referringAttribute']").val();
			if(isNull(referringAttribute)){
				alert('Referring Attribute must be specified');
				$(this).find("input[name='referringAttribute']").focus();
				isValid = false;
			}
		}
	});
	return isValid;
}

function validateAdditionalMappingsforDBFieldName(){
	var isValid = true;
	$('.additionalMappingTable').find('tr').each(function(){
		if(typeof $(this).find("input[name='dbFieldName']").val() !== 'undefined'){
			var dbFieldName =  $(this).find("input[name='dbFieldName']").val();
			if(isNull(dbFieldName)){
				alert('Db Field Name must be specified');
				$(this).find("input[name='dbFieldName']").focus();
				isValid = false;
			}
		}
		
	});
	return isValid;
}

function validateAdditionalMappingsReferringAttribute(){
	var isValid = true;
	$('.additionalMappingTable').find('tr').each(function(){
		
		if(typeof $(this).find("input[name='referringAttribute']").val() !== 'undefined'){
			var referringAttribute =  $(this).find("input[name='referringAttribute']").val();
			if(isNull(referringAttribute)){
				alert('Referring Attribute must be specified');
				$(this).find("input[name='referringAttribute']").focus();
				isValid = false;
			}
		}
	});
	return isValid;
}

function checkForDuplicateMappings(){
	var isValid = true;
	var dbFieldNameArray = new Array();;
	var referringAttributeArray = new Array();
	var arrIndex = 0;
	$('.additionalMappingTable').find('tr').each(function(){
		var referringAttributeVal='',dbFieldNameVal='';
		if(typeof $(this).find("input[name='referringAttribute']").val() !== 'undefined'){
			var referringAttributeVal =  $(this).find("input[name='referringAttribute']").val();
		}
		if(typeof $(this).find("input[name='dbFieldName']").val() !== 'undefined'){
			var dbFieldNameVal =  $(this).find("input[name='dbFieldName']").val();
		}
		
		if(!isNull(referringAttributeVal) && !isNull(dbFieldNameVal)){
			if(!isNewDuplicateMapping(dbFieldNameArray, referringAttributeArray, dbFieldNameVal, referringAttributeVal)){
				dbFieldNameArray[arrIndex] =  dbFieldNameVal;
				referringAttributeArray[arrIndex++] = referringAttributeVal;
			}else{
				alert("Mapping with DB Field Name = "+dbFieldNameVal+" and Referring Attribute = "+referringAttributeVal+" exists multiple times");
				 $(this).find("input[name='dbFieldName']").focus();
				isValid = false;
				return false;
			}
		}
	});
	
	return isValid;
}

function isNewDuplicateMapping(dbFieldNameArray, referringAttributeArray, dbFieldNameVal, referringAttributeVal){
	var isDuplicate = false;
	$(dbFieldNameArray).each(function (index, element){
		if(element == dbFieldNameVal && referringAttributeArray[index] == referringAttributeVal){
			isDuplicate = true;
			return false;
		}			
	});
	return isDuplicate;
}

function checkForSessionOverrideFieldsWithMappings(){
	var isValid = true;
	var overrideValue = $("#sessionOverrideFields").val();
	
	if(!isNull($("#sessionOverrideFields").val())){
		var overrideColumnArray = overrideValue.split(/[,; ]/);
		
		$(overrideColumnArray).each(function(index, element){
			var isInMandatoryMappings = checkForManatoryMappings(element);
			var isInAdditionalMappings = checkForAdditionalMappings(element);
			
			if( !isInMandatoryMappings && !isInAdditionalMappings){
				alert('Configured Field ['+element+'] must be mapped with either Mandatory Field Mappings or DB Field Mappings');
				isValid= false;
				return false;
			}
		});
		
	}
	return isValid;
}

function checkForManatoryMappings(element){
	var isElementInMandatoryMapping = false;
	$('.madatoryMappingsTable').find('tr').each(function(){
		if(typeof $(this).find("input[name='dbFieldName']").val() !== 'undefined'){
			var dbFieldName =  $(this).find("input[name='dbFieldName']").val();
			if(element.trim() == dbFieldName.trim()){
				isElementInMandatoryMapping = true;
			}
		}
	});
	return isElementInMandatoryMapping;
}

function checkForAdditionalMappings(element){
	var isElementInAdditionalMapping = false;
	$('.additionalMappingTable').find('tr').each(function(){
		if(typeof $(this).find("input[name='dbFieldName']").val() !== 'undefined'){
			var dbFieldName =  $(this).find("input[name='dbFieldName']").val();
			if(element.trim() == dbFieldName.trim()){
				isElementInAdditionalMapping = true;
			}
		}
	});
	return isElementInAdditionalMapping;
}