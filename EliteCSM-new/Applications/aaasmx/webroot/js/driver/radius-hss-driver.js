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
