/**
 * This validation js file contains the functions for major client side validations for driver Mapping.
 * Created Date : 27th April 2012 
 * Author       : Punit Patel
 * 
 */

var logicalNameData;
function setLogicalNameData(logicalNameData){
	this.logicalNameData = logicalNameData;
}

function addNewRow(templateTableId, tableId,logicalName,multipleAllowFunctionality){
		var tableRowStr = $("#"+templateTableId).find("tr");
  		$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
  		if($("#"+tableId+" select").size() > 0){
  			addDropDown(tableId,logicalName,multipleAllowFunctionality);
  	  		$("#"+tableId+" tr:last").find("select:first").focus();
  		}else{
  			$("#"+tableId+" tr:last").find("input:first").focus();
  		}
	}
		
var unique = function(origArray) {
	var newArray = [],
	origLen = origArray.length,found,x, y;
	 
	for ( x = 0; x < origLen; x++ ) {
		found = undefined;
		for ( y = 0; y < newArray.length; y++ ) {
			if ( origArray[x] === newArray[y] ) {
				found = true;
				break;
			}
		}
		if ( !found) newArray.push( origArray[x] );
	}
	return newArray;
};
function addDropDown(tableId,logicalName,multipleAllowFunctionality){
		var logicalNameValueArray = getSelectedLogicalNameArray(tableId,logicalName);;
		var selectObj = $("#"+tableId+" tr:last").find("select:first");
		var arrayItemName=new Array();
		var arrayItemValue=new Array();
		$(selectObj).append("<option value='0'>--Select--</option>");
		if(logicalNameData!=undefined){
			$.each(logicalNameData, function(index, item) {
				if($.inArray(item.value,logicalNameValueArray) < 0  ||  (multipleAllowFunctionality==true  && item.multipleAllow == "true")){
					if((multipleAllowFunctionality==true  && item.multipleAllow == "true")){
						arrayItemName.push(item.name);
						arrayItemValue.push(item.value);	
						for(var logicalIndex=0 in logicalNameValueArray){
							if(logicalNameValueArray[logicalIndex]==item.value ){
								return;
							}
						}
					}else{
						$(selectObj).append("<option value='" + item.value + "'>" + item.name + "</option>");
					}
				}
			});
		}
		if(multipleAllowFunctionality==true){
			$(selectObj).append("<optgroup label='Additional Mapping'></optgroup>");
			arrayItemName = unique(arrayItemName);
			for(var value in arrayItemName){
				$(selectObj).append("<option value='" + arrayItemValue[value] + "'>" + arrayItemName[value] + "</option>");
			}
		}
	}

function setLogicalnameDropDown(tableId,logicalName,multipleAllowFunctionality){
	var logicalNameValueArray = getSelectedLogicalNameArray(tableId,logicalName);
	var arrayItemName=new Array();
	var arrayItemValue=new Array();
	$("#"+tableId+" select[name='"+logicalName+"']").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value='0'>--Select--</option>");
		if(logicalNameData!=undefined){
			$.each(logicalNameData, function(index, item) {
				if($.inArray(item.value,logicalNameValueArray) < 0 ||  item.value == currentVal || (multipleAllowFunctionality==true  && item.multipleAllow == "true") ){
					if((multipleAllowFunctionality==true  && item.multipleAllow == "true")){
						arrayItemName.push(item.name);
						arrayItemValue.push(item.value);
					
						for(var logicalIndex=0 in logicalNameValueArray){
							if(logicalNameValueArray[logicalIndex]==item.value ){
								return;
							}
						}
					}else{
						$(selectObj).append("<option value='" + item.value + "'>" + item.name + "</option>");
					}
				}
			});
		}
		
		if(multipleAllowFunctionality==true){
			$(selectObj).append("<optgroup label='Additional Mapping'></optgroup>");
			arrayItemName = unique(arrayItemName);
			for(var value in arrayItemName){
				$(selectObj).append("<option value='" + arrayItemValue[value] + "'>" + arrayItemName[value] + "</option>");
			}
		}
		$(selectObj).val(currentVal);
	}); 
	
} 	

function getSelectedLogicalNameArray(tableId,logicalName){
	var logicalNameValueArray = new Array();
	var arrIndex = 0; 
	$("#"+tableId+" select[name='"+logicalName+"']").each(function(){
		if($(this).val() != "0"){
			logicalNameValueArray[arrIndex++] = $(this).val();
		}				
	});
	return logicalNameValueArray;
}

function isValidLogicalNameMapping(tableId, logicalName, dbFieldName){
	var isValid = true;
	var logicalNameArray = new Array();
	var dbFieldArray = new Array();
	var arrIndex = 0;
	var firstColumnName = "";
	var secondColumnName = "";
		$("#"+tableId+" tr").each(function(){
		var logicalNameObj = $(this).find("select[name='"+logicalName+"']");
		var dbFieldObj = $(this).find("input[name='"+dbFieldName+"']");
		if($(logicalNameObj).size() == 1 && $(dbFieldObj).size() == 1){
			var logicalNameVal =  $.trim($(logicalNameObj).val());
			var dbFieldVal = $.trim($(dbFieldObj).val());
			/* check Logical Name Value */
			if(logicalNameVal == "0"){
				if(!(firstColumnName.indexOf('?') === -1)){
					firstColumnName = firstColumnName.replace('?','').trim();
				}
				
				alert(firstColumnName+" must be specified");
				$(logicalNameObj).focus();
				isValid = false;
				return false;
			}
			/* check DbField  Value */
			if(isNull(dbFieldVal)){
				if(!(secondColumnName.indexOf('?') === -1)){
					secondColumnName = secondColumnName.replace('?','').trim();
				}
				
				alert(secondColumnName+" must be specified");
				$(dbFieldObj).focus();
				isValid = false;
				return false;
			}
			/* check Duplicate Value */
			if(!isNewDuplicateMapping(logicalNameArray, dbFieldArray, logicalNameVal, dbFieldVal)){
				logicalNameArray[arrIndex] =  logicalNameVal;
				dbFieldArray[arrIndex++] = dbFieldVal;
			}else{
				alert("Mapping with "+firstColumnName+" "+logicalNameVal+" and "+secondColumnName+" "+dbFieldVal+" exists multiple times");
				$(dbFieldObj).focus();
				isValid = false;
				return false;
			}
		}else{
			firstColumnName = $.trim($(this).find("td").eq(0).text());
			secondColumnName = $.trim($(this).find("td").eq(1).text());
		}
	});
	return isValid;
}
function isValidJNDIPropertyMapping(tableId, jndiProperty, jndiPropertyValue){
	var isValid = true;
	var jndiPropertyArray = new Array();;
	var jndiPropertyValueArray = new Array();
	var arrIndex = 0;
	var firstColumnName = "";
	var secondColumnName = "";
	$("#"+tableId+" tr").each(function(){
		var jndiPropertyObj = $(this).find("input[name='"+jndiProperty+"']");
		var jndiPropertyValueObj = $(this).find("input[name='"+jndiPropertyValue+"']");
		if($(jndiPropertyObj).size() == 1 && $(jndiPropertyValueObj).size() == 1){
			var jndiPropertyVal =  $.trim($(jndiPropertyObj).val());
			var jndiPropertyValueVal = $.trim($(jndiPropertyValueObj).val());
			
			/* check Property*/
			if(isNull(jndiPropertyVal)){
				if(!(firstColumnName.indexOf('?') === -1)){
					firstColumnName = firstColumnName.replace('?','').trim();
				}
				alert(firstColumnName+" must specified");
				$(jndiPropertyObj).focus();
				isValid = false;
				return false;
			}
			/* check DbField  Value */
			if(isNull(jndiPropertyValueVal)){
				if(!(secondColumnName.indexOf('?') === -1)){
					secondColumnName = secondColumnName.replace('?','').trim();
				}
				alert(secondColumnName+" must specified");
				$(jndiPropertyValueObj).focus();
				isValid = false;
				return false;
			}
			/* check Duplicate Value */
			if(!isNewDuplicateMapping(jndiPropertyArray, jndiPropertyValueArray, jndiPropertyVal, jndiPropertyValueVal)){
				jndiPropertyArray[arrIndex] =  jndiPropertyVal;
				jndiPropertyValueArray[arrIndex++] = jndiPropertyValueVal;
			}else{
				alert("Mapping with "+firstColumnName+" "+jndiPropertyVal+" and "+secondColumnName+" "+jndiPropertyValueVal+" exists multiple times");
				$(jndiPropertyValueObj).focus();
				isValid = false;
				return false;
			}
		}else{
			firstColumnName = $.trim($(this).find("td").eq(0).text());
			secondColumnName = $.trim($(this).find("td").eq(1).text());
		}
	});
	return isValid;
}
function isNewDuplicateMapping(logicalNameArray, dbFieldArray, logicalNameVal, dbFieldVal){
	var isDuplicate = false;
	$(logicalNameArray).each(function (index, element){
		if(element == logicalNameVal && dbFieldArray[index] == dbFieldVal){
			isDuplicate = true;
			return false;
		}			
	});
	return isDuplicate;
}
function addRow(templateTableId, tableId){
	var tableRowStr = $("#"+templateTableId).find("tr");
		$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
		if($("#"+tableId+" select").size() > 0){
	  		$("#"+tableId+" tr:last").find("select:first").focus();
		}else{
			$("#"+tableId+" tr:last").find("input:first").focus();
		}
		
}
function isValidAttributeMapping(tableId, jndiProperty){
	var isValid = true;
	var jndiPropertyArray = new Array();;
	var arrIndex = 0;
	var firstColumnName = "";
	$("#"+tableId+" tr").each(function(){
		var jndiPropertyObj = $(this).find("input[name='"+jndiProperty+"']");
		if($(jndiPropertyObj).size() == 1 ){
			var jndiPropertyVal =  $.trim($(jndiPropertyObj).val());
		
			/* check Property*/
			if(isNull(jndiPropertyVal)){
				if(!(firstColumnName.indexOf('?') === -1)){
					firstColumnName = firstColumnName.replace('?','').trim();
				}
				alert(firstColumnName+" must specified");
				$(jndiPropertyObj).focus();
				isValid = false;
				return false;
			}
			/* check Duplicate Value */
			if(!isDuplicateMapping(jndiPropertyArray,jndiPropertyVal)){
				jndiPropertyArray[arrIndex] =  jndiPropertyVal;
				arrIndex++;
			}else{
				alert("Mapping with "+firstColumnName+" "+jndiPropertyVal+" exists multiple times");
				$(jndiPropertyObj).focus();
				isValid = false;
				return false;
			}
		}else{
			firstColumnName = $.trim($(this).find("td").eq(0).text());
		}
	});
	return isValid;
}
function isDuplicateMapping(logicalNameArray,logicalNameVal){
	var isDuplicate = false;
	$(logicalNameArray).each(function (index,element){
		if(logicalNameArray[index] == logicalNameVal){
			isDuplicate = true;
			return false;
		}			
	});
	return isDuplicate;
}

function isValidAcctDriverMapping(tableId, logicalName, dbFieldName){
	var isValid = true;
	var logicalNameArray = new Array();;
	var dbFieldArray = new Array();
	var arrIndex = 0;
	var firstColumnName = "";
	var secondColumnName = "";
	$("#"+tableId+" tr").each(function(){
		var logicalNameObj = $(this).find("input[name='"+logicalName+"']");
		var dbFieldObj = $(this).find("input[name='"+dbFieldName+"']");
		if($(logicalNameObj).size() == 1 && $(dbFieldObj).size() == 1){
			var logicalNameVal =  $.trim($(logicalNameObj).val());
			var dbFieldVal = $.trim($(dbFieldObj).val());
			/* check Logical Name Value */
			if(logicalNameVal == "0"){
				if(!(firstColumnName.indexOf('?') === -1)){
					firstColumnName = firstColumnName.replace('?','').trim();
				}
				alert(firstColumnName+" must specified");
				$(logicalNameObj).focus();
				isValid = false;
				return false;
			}
			/* check DbField  Value */
			if(isNull(dbFieldVal)){
				if(!(secondColumnName.indexOf('?') === -1)){
					secondColumnName = secondColumnName.replace('?','').trim();
				}
				alert(secondColumnName+" must specified");
				$(dbFieldObj).focus();
				isValid = false;
				return false;
			}
			/* check Duplicate Value */
			if(!isNewDuplicateMapping(logicalNameArray, dbFieldArray, logicalNameVal, dbFieldVal)){
				logicalNameArray[arrIndex] =  logicalNameVal;
				dbFieldArray[arrIndex++] = dbFieldVal;
			}else{
				alert("Mapping with "+firstColumnName+" "+logicalNameVal+" and "+secondColumnName+" "+dbFieldVal+" exists multiple times");
				$(dbFieldObj).focus();
				isValid = false;
				return false;
			}
		}else{
			firstColumnName = $.trim($(this).find("td").eq(0).text());
			secondColumnName = $.trim($(this).find("td").eq(1).text());
		}
	});
	return isValid;
}
function isValidFieldMapping(tableId, dbField){
	var isValid = true;
	var dbFieldArray = new Array();;
	var arrIndex = 0;
	var firstColumnName = "";
	$("#"+tableId+" tr").each(function(){
		var dbFieldObj = $(this).find("input[name='"+dbField+"']");
		if($(dbFieldObj).size() == 1 ){
			var dbFieldVal =  $.trim($(dbFieldObj).val());
		
			/* check Property*/
			if(isNull(dbFieldVal)){
				if(!(firstColumnName.indexOf('?') === -1)){
					firstColumnName = firstColumnName.replace('?','').trim();
				}
				alert(firstColumnName+" must specified");
				$(dbFieldObj).focus();
				isValid = false;
				return false;
			}
			/* check Duplicate Value */
			if(!isDuplicateMapping(dbFieldArray,dbFieldVal)){
				dbFieldArray[arrIndex] =  dbFieldVal;
				arrIndex++;
			}else{
				alert("Mapping with "+firstColumnName+" "+dbFieldVal+" exists multiple times");
				$(dbFieldObj).focus();
				
				isValid = false;
				return false;
			}
		}else{
			firstColumnName = dbField;
		}
	});
	return isValid;
}