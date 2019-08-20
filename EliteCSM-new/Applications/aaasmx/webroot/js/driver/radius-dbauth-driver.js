/**
 * This validation js file contains the functions for major client side validations for Radius DB Auth Driver.
 * Created Date : 17th July 2012 
 * Author       : Punit Patel
 * 
 */

var action = "create";

function setAction(action){
	this.action = action; 
}

function validateForm(){
	if(this.action != "create" && !validateName()){
		return;
	}
	if(document.forms[0].databaseId.value == 0){
		alert('Select atleast one datasource value ');
		document.forms[0].databaseId.focus();
	}else if(isNull(document.forms[0].tableName.value)){
		alert('Table Name must be specified.');
		document.forms[0].tableName.focus();
	}else if(isNull(document.forms[0].querytimeout.value)){
		document.forms[0].querytimeout.focus();
		alert('DB Query Timeout must be specified.');
	}else if(!isPositiveNumber(document.forms[0].querytimeout.value)){
		document.forms[0].querytimeout.focus();
		alert('DB Query Timeout must be zero or positive number.');
	}else if(isNull(document.forms[0].timeoutcount.value)){
		document.forms[0].timeoutcount.focus();
		alert('Maximum Query Timeout Count must be specified.');
	}else if(!isPositiveNumber(document.forms[0].timeoutcount.value)){
		document.forms[0].timeoutcount.focus();
		alert('Maximum Query Timeout Count must be zero or positive number.');
	}else if(isNull(document.forms[0].profileLookupColumn.value)){
		alert('Profile Lookup Column must be specified.');
		document.forms[0].profileLookupColumn.focus();
	}else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
		alert('At least one mapping must be there.');
	}else{ 	 		
 		if(isValidLogicalNameMapping("mappingtbl", "logicalnmVal", "dbfieldVal")){
	 		document.forms[0].submit();
		}
 	}		
}
	
function setFieldSuggestion(){
	if(document.getElementById("databaseId").value != "0"){
		$(retriveTableFields(document.getElementById("databaseId").value,document.getElementById("tableName").value,"profileLookupColumn"));
	}	
}

function enableAll() {
	if(!document.getElementById("cacheable").checked) {
		document.getElementById("primaryKeyColumn").disabled = true;
		document.getElementById("sequenceName").disabled = true;
	}else{
		document.getElementById("primaryKeyColumn").disabled = false;
		document.getElementById("sequenceName").disabled = false;
	}
}

function setColumnsOnUserIdentity(){
	var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
	retriveRadiusDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
}

function  setAutoCompleteData(dbFieldObject){
	$(dbFieldObject).autocomplete({
		source : $( "#profileLookupColumn" ).autocomplete( "option", "source" ),
	});
}
