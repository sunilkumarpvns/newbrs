<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script>
var i =0;
function splitData( val ) {
	return val.split( /[.,;(\'']\s*/ );
}
function extractLastData( term ) {
	return splitData( term ).pop();
} 
function setDiameterDictionaryReferAttributeFieldsData(txtField,myArray){
	$( ".autoSuggest").autocomplete({	
		source:function( request, response ) {
			response( $.ui.autocomplete.filter(
				myArray, extractLastData( request.term ) ) );
	},	
	
	focus: function( event, ui ) {
		return false;
	},
	select: function( event, ui ) {
		var val = this.value;
		var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
		var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
		var quoteIndex=val.lastIndexOf("\'") == -1 ? 0 : val.lastIndexOf("\'");
		var dotIndex=val.lastIndexOf(".") == -1 ? 0 :val.lastIndexOf(".");
		 if(commaIndex == semiColonIndex &&  commaIndex == dotIndex && quoteIndex == dotIndex) {
				val = "";
		} else if(commaIndex > semiColonIndex) {
				val = val.substring(0,commaIndex+1); 
			}  else if(dotIndex!=0 && dotIndex > quoteIndex){
				val = val.substring(0,dotIndex + 1); 
			} else if(quoteIndex!=0){
				val = val.substring(0,quoteIndex + 1);
			}   
		 
		this.value = val + ui.item.value ;
		return false;
	}
});	
};	
function retriveDiameterDictionaryDataAttributes() {
	
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	var searchNameOrAttributeId="";
	$.post("SearchDiameterAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#, ");
		var value;
		var label;
	
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0].trim();
			label = tmpArray[1];
			var item = new ListItem(value,label); 
			myArray.push(item);
		}	
		setDiameterDictionaryReferAttributeFieldsData("",myArray);
		return dbFieldArray;
	});
};


function retriveRadiusDictionaryAttributesForReferingAtrributes() {
var myArray = new Array();
var dbFieldStr;
var dbFieldArray;
var searchNameOrAttributeId="";
$.post("SearchRadiusAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
	dbFieldStr = data.substring(1,data.length-3);
	dbFieldArray = new Array();
	dbFieldArray = dbFieldStr.split("#, ");
	var value;
	var label;

	for(var i=0;i<dbFieldArray.length;i++) {
		tmpArray = dbFieldArray[i].split(",");
		value = tmpArray[0].trim();
		label = tmpArray[1];
		var item = new ListItem(value,label); 
		myArray.push(item);
	}	
	setRadiusDictionaryReferAttributeFields("",myArray);
	return dbFieldArray; 
	
});
}

 function setRadiusDictionaryReferAttributeFields(txtField,myArray) {
	 $( ".autoSuggest").autocomplete({	
			source:function( request, response ) {
				response( $.ui.autocomplete.filter(
					myArray, extractLastData( request.term ) ) );
		},	
		
		focus: function( event, ui ) {
			return false;
		},
		select: function( event, ui ) {
			var val = this.value;
			var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
			var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
			var quoteIndex=val.lastIndexOf("\'") == -1 ? 0 : val.lastIndexOf("\'");
			var dotIndex=val.lastIndexOf(".") == -1 ? 0 :val.lastIndexOf(".");
			 if(commaIndex == semiColonIndex &&  commaIndex == dotIndex && quoteIndex == dotIndex) {
					val = "";
			} else if(commaIndex > semiColonIndex) {
					val = val.substring(0,commaIndex+1); 
				}  else if(dotIndex!=0 && dotIndex > quoteIndex){
					val = val.substring(0,dotIndex + 1); 
				} else if(quoteIndex!=0){
					val = val.substring(0,quoteIndex + 1);
				}   
			 
			this.value = val + ui.item.value ;
			return false;
		}
	});		
} 

function addData(rowId) {

document.getElementById("txtCheckExpr" + rowId).value = document
	.getElementById("tempCheckExpr" + rowId).value;
document.getElementById("txtDestinationExpr" + rowId).value = document
	.getElementById("tempDestinationExpr" + rowId).value;
document.getElementById("txtSourceExpr" + rowId).value = document
	.getElementById("tempSourceExpr" + rowId).value;
document.getElementById("txtDefaultValue" + rowId).value = document
	.getElementById("tempDefaultValue" + rowId).value;
document.getElementById("txtValueMapping" + rowId).value = document
	.getElementById("tempValueMapping" + rowId).value;
$("#ddlOperation" + rowId).val(
	document.getElementById("tempDdlOperation" + rowId).value);
	
toggleMe(rowId);
};

function toggleMe(id) {
var imgElement = document.getElementById("imgExpand" + id);
if ($("#tempRow" + id).is(':hidden')) {
imgElement.src = "images/minus.jpg";
document.getElementById("tempCheckExpr" + id).value = document
.getElementById("txtCheckExpr" + id).value;
document.getElementById("tempDestinationExpr" + id).value = document
.getElementById("txtDestinationExpr" + id).value;
document.getElementById("tempSourceExpr" + id).value = document
.getElementById("txtSourceExpr" + id).value;
document.getElementById("tempDefaultValue" + id).value = document
.getElementById("txtDefaultValue" + id).value;
document.getElementById("tempValueMapping" + id).value = document
.getElementById("txtValueMapping" + id).value;
$("#tempDdlOperation" + id).val(
document.getElementById("ddlOperation" + id).value); 
 $("#tempRow" + id).show();

} else {
imgElement.src = "images/plus.jpg";

if(document.getElementById('btnCancel'+id).onclick ==true) {
	document.getElementById("txtCheckExpr" + id).value = document.getElementById("tempCheckExpr" + id).value;
	document.getElementById("txtDestinationExpr" + id).value = document.getElementById("tempDestinationExpr" + id).value;
	document.getElementById("txtSourceExpr" + id).value = document.getElementById("tempSourceExpr" + id).value;
	document.getElementById("txtDefaultValue" + id).value = document.getElementById("tempDefaultValue" + id).value;
	document.getElementById("txtValueMapping" + id).value = document.getElementById("tempValueMapping" + id).value;
	$("#ddlOperation" + id).val(document.getElementById("tempDdlOperation" + id).value);	  
	
}
$("#tempRow" + id).hide();

} 
};



function removeRow(id) {
	$("#tableRow" + id).remove();
	
};

 function handleMappingResponse(response){
var div = document.getElementById("messageTypeDiv");
$(div).append(response);
var tableId = "table"+mappingIndex;
var mappingNameId = "mappingName"+mappingIndex;
document.getElementById(tableId).scrollIntoView();
document.getElementById(mappingNameId).focus();
setMappingLabeIndex();
setCheckBox(mappingIndex);
}
function setMappingLabeIndex(){
$(".mappingLabel").each(function(index, item){
	var mapIndex = index+1;
	$(this).text("Mapping-"+mapIndex);
});
}

function setCheckBox(mappingIndex){
 var chkDefault = document.getElementById("defaultMappingChkBox"+mappingIndex);
 if(chkDefault.value == "true"){
	 chkDefault.checked = true;
 }else{
	 chkDefault.checked = false;
 }
 setDefaultMapping(mappingIndex);
}

function setDefaultMapping(componentIndex){
var checkBox = document.getElementById("defaultMappingChkBox"+componentIndex);
if(!checkBox.checked){
	$("#div"+componentIndex).show(); 
}else{
	$("#div"+componentIndex).hide();	
}
} 

function toggleMappingDiv(mappingDivIndex){
	  var imgElement = document.getElementById("toggleImageElement"+mappingDivIndex);
	  if ($("#toggleDivElement"+mappingDivIndex).is(':hidden')) {
          imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
     } else {
          imgElement.src="<%=request.getContextPath()%>/images/bottom-level.jpg";
     }
    $("#toggleDivElement"+mappingDivIndex).slideToggle("fast");
}

function toggleDummyParameters(){
	  var imgElement = document.getElementById("toggleDummyParamImageElement");
	  if ($("#toggleDummyParamDivElement").is(':hidden')) {
          imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
     } else {
          imgElement.src = "<%=request.getContextPath()%>/images/bottom-level.jpg";
	}
	$("#toggleDummyParamDivElement").slideToggle("fast");
}

</script>
<body>

</body>
</html>