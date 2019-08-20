/**
 * This validation js file contains the functions for retrive driver list for CDR Mapping.
 * Created Date : 8th July 2014 
 * Author       : Nayana Rathod
 */

var driverDataJson = {};

function addNewCDRRow(templateTableId, tableId, isUpdate , isDiameter){
	var handlerTable;
	if(isDiameter){
		handlerTable = $('#'+tableId).closest('table[name="tblmCDRHandler"]');
	} else {
		handlerTable = $('#'+tableId).closest('table[name="tblmAdditionalCDRGeneration"]');
	}
	$(handlerTable).css({'border':'none'});
	
	var tableRowStr = $("#"+templateTableId).find("tr");
	
	$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	if($("#"+tableId+" select").size() > 0){
		if(isUpdate == false){
			addCDRDropDown(tableId);
		}
  		$("#"+tableId+" tr:last").find("select:first").focus();
	}else{
		$("#"+tableId+" tr:last").find("input:first").focus();
	}
	setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
}

function addNewPluginRow(templateTableId,tableId){
	
	var handlerTable = $('#'+tableId).closest('.tblmPlugin-container');
	$(handlerTable).css({'border':'none'});
	
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+tableId+" tr:last").find("input:first").focus();
}

function addPrePluginList(templateTableId,tableId){
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+tableId+" tr:last").find("input:first").focus();
}

function addPostPluginList(templateTableId,tableId){
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+tableId+" tr:last").find("input:first").focus();
}

function addCDRDropDown(tableId){
	var optionString = generateOptionFromJSON();
	
	var selectObj = $("#"+tableId+" tr:last").find("select:first");
	$(selectObj).append(optionString);
	
	var secondaryDriver = $("#"+tableId+" tr:last").find(".secondaryDriverId");
	$(secondaryDriver).append(optionString);
}

function setOtherDriverDropDown(formClass){
	$('.'+formClass).each(function(){
		$(".mappingtblcdr tr").each(function(){
			removeDriverFromListOfOther($(this).find('.primaryDriverId'),true);
			removeDriverFromListOfOther($(this).find('.secondaryDriverId'),false);
		}); 
	});
}

function removeDriverFromListOfOther(obj,isPrimaryDriver){
	/*Note : Class of select object of other driver. Example when primary driver is selected it will store class
	  value of secondary driver and vice versa */
	var otherDriverSelectClass;
	if(isPrimaryDriver){
		otherDriverSelectClass = 'secondaryDriverId';
	} else {
		otherDriverSelectClass = 'primaryDriverId';
	}
	
	var currentVal = $(obj).val();
	if(currentVal != null){
		var otherDriverObj = $(obj).closest('tr').find('.'+otherDriverSelectClass);
		var otherObjVal = $(otherDriverObj).val();
		$(otherDriverObj).empty();
		$(otherDriverObj).append(generateOptionFromJSON(currentVal));
		$(otherDriverObj).val(otherObjVal);
	}
}

/* CDR DM Generation */
function addAttributeMappingRow(templateTableId, tableId){
	var mainTable = $('#'+tableId).closest('.tblmCOADMGeneration');
	$(mainTable).css({'border':'none'});
	
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+tableId).append("<tr>"+$(tableRowStr).html()+"</tr>");
}
function addNasClientRow(templateTableId, tableId){
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+tableId).append("<tr>"+$(tableRowStr).html()+"</tr>");
}

function generateOptionFromJSON(selectedValue){
	
	var isValidValue = ((typeof(selectedValue) != 'undefined')
			&& selectedValue.trim() != ''
			&& selectedValue != '0');

	var optionStringForDriver = "<option value='0'>--Select--</option>";

	for(var key in driverDataJson){
		var optionArray = driverDataJson[key];
		var optionArrayLen = optionArray.length;

		optionStringForDriver += "<optgroup label='"+key+"'>";

		for(i=0; i<optionArrayLen; i++){
			var optionJson = optionArray[i];

			if(isValidValue){
				if(optionJson['id'] != selectedValue){
					optionStringForDriver += "<option value='"+optionJson['name']+"' class='captiontext'>"
												+ optionJson['name']+"</option>";
				}
			} else {
				optionStringForDriver += "<option value='"+optionJson['name']+"' class='captiontext'>"
											+ optionJson['name']+"</option>";
			}
		}

		optionStringForDriver += "</optgroup>";
	}
	optionStringForDriver += "<optgroup label='&nbsp;&nbsp;'></optgroup>";
	optionStringForDriver += "<option value='Add' style='background-color:#e9e9e9;color: #015198;cursor: pointer;" +
	"font-weight: bold;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+ Add New Driver</span></option>";

	return optionStringForDriver;
}

/* This function is required to Open an dialog for adding a new driver */
function openDriverWizard(index,obj,mappingTableIds){
	$(obj).css({'border':'none'});
	if($(obj).val() == 'Add'){
		$('#driverDiv').dialog({
			modal: true,
			autoOpen: true,
			minHeight: 200,
			height: 415,
			width: 795,
			position: 'top',
			close: function() {
				$(obj).val("0");
			}
		});
		$('#driverDiv').dialog("open");
	}
	var name = $(obj).attr('name');

	if(name == 'primaryDriverId'){
		removeDriverFromListOfOther(obj,true);
	} else {
		removeDriverFromListOfOther(obj,false);
	}
}