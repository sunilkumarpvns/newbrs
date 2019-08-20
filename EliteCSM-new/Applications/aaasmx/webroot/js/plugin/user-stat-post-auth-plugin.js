/** Global Parameters*/
/** This Array Variable is used for store DBField value retrived from Database using FieldRetrievalServlet call*/
var dbFieldMappingArray;
var newdbFieldMappingArray;
/**Retrive Radius Attributes  Array */
var myRadiusAttributeArray;

/**
 * This Function Is Used To Split Value.
 */
function split( val ) {
	return val.split( /,\s*|;/ );
}

/** Retrive Field For DBField in MApping*/
function setFieldSuggestion(){
	dbFieldMappingArray = new Array();
	newdbFieldMappingArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	var dbId =  document.getElementById("databaseId").value;
	var tableName = document.getElementById("tableName").value;
	var oldTableValue=$('input[name=tblname]').val();
	
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		if(data.length == 3 && dbId != '0' && tableName!=""){
			alert("Table does not exist");
			return false;
		}else{
			data=data.trim();
			dbFieldStr = data.substring(1,data.length-1);
			dbFieldArray = dbFieldStr.split(",");
			for(var i=0;i<dbFieldArray.length;i++) {
				var dbFieldVal = dbFieldArray[i].trim();
				var item = new ListItem(dbFieldVal,dbFieldVal);
				dbFieldMappingArray.push(item);
				newdbFieldMappingArray.push(dbFieldVal);
			}
			if (oldTableValue != tableName) {
				var obj= $("#plugin-mapping-table" +" > tbody > tr").find("input[name='dbField']");
				$(obj).each(function(){
					$(this).attr('old-value','');
				});
			}
			deleteFromArray();
			setDBFieldMappingData(dbFieldMappingArray,"dbField");
		}
	});
}

/**Autocomplete For DBField */
function setDBFieldMappingData(autoCompleteArray,mappingObj){
	if(mappingObj!=null){
		$("."+ mappingObj ).bind("keydown",function(event) {
			if (event.keyCode === $.ui.keyCode.TAB && $(this).autocomplete("instance").menu.active) {
				event.preventDefault();
			}
		}).autocomplete({
			minLength : 0,
			source:function( request, response ) {
				response( $.ui.autocomplete.filter(autoCompleteArray, extractLast( request.term ) ) );
			},
			select : function(event, ui) {
				var val = this.value;
				if(val.lastIndexOf(",")  != -1 || val.lastIndexOf(";") != -1)
					return false;
				var terms = split( this.value );
				terms.pop();
				terms.push( ui.item.value );
				this.value = terms.join(",");
				return false;
			}
		}).on("blur",function(event) {
			var getValue=$(this).val();
			if(getValue != "") {
				for(var i = 0; i < dbFieldMappingArray.length; i++) {
					if(getValue == dbFieldMappingArray[i].value) {
						dbFieldMappingArray.splice(i, 1);
						break;
					}
				}

				var oldVal = $(this).attr("old-value");
				if(oldVal != getValue){
					if(isValueInDBFieldArray(oldVal) == false){
						dbFieldMappingArray.push(new ListItem(oldVal,oldVal));
						if(newdbFieldMappingArray.indexOf(oldVal) != -1){
							$(this).attr("old-value",getValue);
						} else {
							$(this).attr("old-value","");
						}
					}
				}
			} else {
				var oldVal = $(this).attr("old-value");
				var status = isValueInDBFieldArray(oldVal);
				if(status == false){
					dbFieldMappingArray.push(new ListItem(oldVal,oldVal));
					$(this).attr("old-value","");
				}
			}
		}).on("focus",function(event) {
			var getValue=$(this).val();
			if(newdbFieldMappingArray.indexOf(getValue) != -1 && isValueInDBFieldArray(getValue) == false){
				dbFieldMappingArray.push(new ListItem(getValue,getValue));
			}
		});
	}
}

/**
 * This Function is check for is DB Field is in Array or not.
 * @param oldVal denotes DB Field Value.
 * @returns status true/false
 */
function  isValueInDBFieldArray(oldVal){
	var status=false;

	if(oldVal.trim() == ''){
		status = true;
	} else {
		var len = dbFieldMappingArray.length;
		for (i=0; i < len; i++){
			var element = dbFieldMappingArray[i].value;
			if(element == oldVal){
				status = true;
				break;
			}
		}
	}
	return status;
}
/**Autocomplete For AttribureId */
function setRadiusAttributeData(autoCompleteArray, mappingObj) {
	if (mappingObj != null) {
		$("." + mappingObj).bind("keydown",function(event) {
			if (event.keyCode === $.ui.keyCode.TAB && $(this).autocomplete("instance").menu.active) {
				event.preventDefault();
			}
		}).autocomplete({
			minLength : 0,
			source : function(request, response) {
				response($.ui.autocomplete.filter(autoCompleteArray,extractLast(request.term)));
			},
			focus : function() {
				return false;
			},
			select : function(event, ui) {
				var val = this.value;
				var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
				var semiColonIndex = val.lastIndexOf(";") == -1 ? 0: val.lastIndexOf(";");
				if (commaIndex == semiColonIndex) {
					val = "";
				} else if (commaIndex > semiColonIndex) {
					val = val.substring(0, commaIndex + 1);
				}
				this.value = val + ui.item.value;
				return false;
			}
		});
	}
}

/**Retrive Radius Attribute on Page Load*/
function retriveRadiusDictionaryAttributes() {
	myRadiusAttributeArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	$.post("SearchRadiusAttributesServlet", {searchNameOrAttributeId:""}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#,");
		var value;
		var label;
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0].trim();
			label = tmpArray[1];
			var item = new ListItem(value,label); 
			myRadiusAttributeArray.push(item);
		}
		return dbFieldArray;
	});
}

/**
 * This Function Is Used To Set Radius Attribute in Autocomplete.
 */
function setColumnsOnUserIdentity(){
	setRadiusAttributeData(myRadiusAttributeArray,"attributeid");
}

/** This function is used for adding new plugin in corresponding mapping table */
function addUserStatPostAuthPlugin(mappingTable, templateTable) {
	var tableRowStr = $("#" + templateTable).find("tr");
	$("#" + mappingTable).find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
	setDBFieldMappingData(dbFieldMappingArray,"dbField");
}

/** This function is mainly used for initializing default plugins */
function initializedUserStatPostAuthPlugins(userStatPostAuthJson) {
	$.each($.parseJSON(userStatPostAuthJson), function(key,value){
		if( key == 'attributeList' ){
			$.each(value, function(objectKey,objectValue){
				addDefaultUserStatPostAuthPlugin(objectValue);
			});
		}
	});
}
/**
 * This Function Is Used To Add Default Mapping On Create.
 */
function addDefaultUserStatPostAuthPlugin(objectValue){
	var tableRowStr = $("#plugin-mapping-table-template").find("tr");
	$("#plugin-mapping-table").find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );

	$.each(objectValue, function( key, value ){
		var element; 
		if ( key == 'attributeId' || key == 'dbField'){
			element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("input[name='"+key+"']");
		}else if(  key == 'packetType' || key == 'dataType' ){
			element = $("#plugin-mapping-table" +" > tbody > tr:last-child").find("select[name='"+key+"']");
		}
		$(element).val(value);
		$(element).attr("old-value",value);
	});
}
/** This function is used validating all the required parameters */
function validatePlugin(){
	if(validateUserStatisticPostAuth()){
		fetchUserStatPostAuthPluginData();
		document.forms['userStatisticPostAuthPlugin'].submit();
	}
}
/** This function is used for fetching all the plugin configuration */
function fetchUserStatPostAuthPluginData(){
	var databaseId 			    	= 	$('#databaseId').val();
	var tablename					=	$('#tableName').val();
	var dbquerytimeout 				= 	$('#dbQueryTimeoutInMs').val();
	var maxquerytimeoutcount 		= 	$('#maxQueryTimeoutCount').val();
	var batchupdateinterval 		= 	$('#batchUpdateIntervalInMs').val();
	
	var userStatPostAuthPluginLists = [];
	var flgCount = 1;
	
	$('#plugin-mapping-table tr').each(function(){
		if(flgCount !=1){
			var attributeid 			    = 	$(this).find('input[name=attributeId]').val();
			var packettype 					= 	$(this).find('select[name=packetType]').val();
			var dbfield						=	$(this).find('input[name=dbField]').val();
			var datatype                    =   $(this).find('select[name=dataType]').val(); 
			var defaultvalue 				= 	$(this).find("input[name='defaultValue']").val();
			var usedictionaryvalue 			= 	$(this).find("select[name='useDictionaryValue']").val();

			userStatPostAuthPluginLists.push({
				'attributeId'				:	attributeid,
				'packetType'				: 	packettype,
				'dbField' 					: 	dbfield,
				'dataType'					:   datatype,
				'defaultValue'				: 	defaultvalue,
				'useDictionaryValue'     	: 	usedictionaryvalue,
			});
		}
		flgCount = 2;
	});
	
	var policyName = $('#pluginName').val();
	var description = $('#description').val();
	var status = $('#status').val();
	var userStatPostAuthPluginPolicyJson = [];

	/** Create UserStatisticPostAuth Plugin Policy JSON Object*/
	userStatPostAuthPluginPolicyJson.push({
		'name'					: policyName,
		'description' 				: description,
		'status' 				: status,
		'databaseId' 				: databaseId,
		'tableName' 				: tablename,
		'dbQueryTimeoutInMs' 			: dbquerytimeout,
		'maxQueryTimeoutCount' 			: maxquerytimeoutcount,
		'batchUpdateIntervalInMs' 		: batchupdateinterval,
		'attributeList' 			: userStatPostAuthPluginLists
	});

	$('#userStatPostAuthJson').val(JSON.stringify(userStatPostAuthPluginPolicyJson));
}

/** This Function Is Used to Restrict Textbox to Text*/
function validateNumbers(object){
	$('#'+object.name).keypress(function (e) {
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
	});
}
/** This Function Is Used To Delete DB Field Mapping Entry */
function deleteMe(spanObject){
	var isoNumId = $(spanObject).parent().parent().find("input[name='dbField']");
	var oldValue=$(isoNumId).val();
	var temp = [] ;
	var selectedValueArray=[];
	var flag=false;

	$('#plugin-mapping-table tr').next('tr').each(function(){ 
		var dbField = $(this).find('input[name=dbField]');
		var dbValue = dbField.val();
		if(selectedValueArray.indexOf(dbValue) == -1){
			selectedValueArray.push(dbValue);
		} else {
			flag=true;
		}
	});

	if(temp.length == 0){
		for(var i = 0; i < newdbFieldMappingArray.length; i++){
			var str= newdbFieldMappingArray[i];
			temp[i]  = str.trim();
		}
	}
	var index = temp.indexOf(oldValue.trim());
	if(index >= 0){
		var status = isValueInDBFieldArray(oldValue);
		if(status == false && flag==false){
			dbFieldMappingArray.push(new ListItem(oldValue,oldValue));
			$(this).attr("old-value","");
		}
	}
	$(spanObject).parent().parent().remove();
}
/**
 * This function validates the name of the Uset Statistic Post Auth Plugin Field 
 * @returns true if values are correct else false
 */
function validateUserStatisticPostAuth(){
	var isValidAll =true;
	if(!validateDatasourceName()){
		return false;
	}else if(!validateTableName()){
		return false;
	}else if(!validateAttributesId()){
		return false;
	}else if(!validateDBField()){
		return false;
	}else if(!isMultipleDBFieldOrNot()){
		return false;
	}else if(!validateAtLeastOneDBFieldMapping()){
		return false;
	}else {
		return isValidAll;
	}
}
/** This Function Is Used to Delete Value From Array which Is Already Selected In DBField*/
function deleteFromArray()
{
	var obj= $("#plugin-mapping-table" +" > tbody > tr").find("input[name='dbField']");
	$(obj).each(function(){
		var storedValue=$(this).attr('old-value');
		for(var i = 0 ; i < $(dbFieldMappingArray).size(); i++){
			var str=dbFieldMappingArray[i].value;
			if(storedValue == str.trim()){
				dbFieldMappingArray.splice(i, 1);
				break;
			}
		}
	});
}
/**
 * This Function Is Used to Validate Datasource Name.
 * @returns true if values are correct else false
 */
function validateDatasourceName(){
	var isNameValid=true;
	var databaseId =document.getElementById("databaseId").value;
	if(databaseId==0){
		alert("Please select Datasource Name");
		document.getElementById("databaseId").focus();
		isNameValid=false;
		return false;
	}
	return isNameValid;
}

/**
 * This Function Is Used To Validate Table Name.
 * @returns true if values are correct else false
 */
function validateTableName(){
	var isNameValid=true;
	var tblName =document.getElementById("tableName").value;
	if(tblName==""){
		alert("Table name cannot be empty");
		document.getElementById("tableName").focus();
		isNameValid=false;
		return false;
	}
	return isNameValid;
}

/**
 * This Function Is Used To Validate Attribute Id.
 * @returns true if values are correct else false
 */
function validateAttributesId(){
	var isIdValid=true;
	$('#plugin-mapping-table tr').next('tr').each(function(){
		var attributeId = $(this).find('input[name=attributeId]');
		var attributeValue = attributeId.val();
		if(isEmpty(attributeValue)){
			attributeId.focus();
			alert("Attribute Id cannot be empty");
			isIdValid=false;
			return false;
		}
	});
	return isIdValid;
}

/**
 * This Function Is Used To Validate DB Field.
 * @returns true if values are correct else false
 */
function validateDBField(){
	var isFieldValid=true;
	var temp = [] ;
	$('#plugin-mapping-table tr').next('tr').each(function(){ 
		var dbField = $(this).find('input[name=dbField]');
		var dbValue = dbField.val();
		if(temp.length == 0){
			for(var i = 0; i < newdbFieldMappingArray.length; i++){
				var str= newdbFieldMappingArray[i];
				temp[i]  = str.trim();
			}
		}
		var index = temp.indexOf(dbValue.trim());
		if(index < 0){
			dbField.focus();
			if(dbValue== ""){
				alert("DB Field cannot be empty");
			}else{
				alert(dbValue+" is invalid DB Field");
			}
			isFieldValid=false;
			return false;
		}
	});
	return isFieldValid;
}

/**
 * This Function Is Used To Validate DB Field Is Multiple or Not.
 * @returns true if values are correct else false
 */
function isMultipleDBFieldOrNot(){
	
	var isFieldMultiple=true;
	var selectedValueArray = [];
	var mulitpleDuplicateValue=[];
	$('#plugin-mapping-table tr').next('tr').each(function(){ 
		var dbField = $(this).find('input[name=dbField]');
		var dbValue = dbField.val();
		if(selectedValueArray.indexOf(dbValue) == -1){
			selectedValueArray.push(dbValue);
		} else {
			dbField.focus();
			if(mulitpleDuplicateValue.indexOf(dbValue) < 0){
				mulitpleDuplicateValue.push(dbValue);
			}
			isFieldMultiple=false;
		}
	});
	if(mulitpleDuplicateValue.length > 0){
		alert("Duplicate DB Field mapping for " +mulitpleDuplicateValue.toString() + " is not allowed");
	}
	return isFieldMultiple;
}

/**
 * This Function Is Used To Validate DB Field Mapping. 
 * @returns true if values are correct else false
 */
function validateAtLeastOneDBFieldMapping(){
	var isMappingValid=true;
	var rowMappingLength = document.getElementById("plugin-mapping-table").rows.length;
	if(rowMappingLength==1){	
		alert("Atleast One DB Field mapping is required");
		isMappingValid=false;
		return false;
	}
	return isMappingValid;
}
