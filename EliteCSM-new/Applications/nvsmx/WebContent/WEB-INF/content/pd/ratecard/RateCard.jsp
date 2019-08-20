<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<script type="text/javascript">

var DOM_ADD_VERION_DETAIL = '#tableTemplate > tbody > tr';
var DOM_VALIDATE_VERION_DETAIL = '#versionDetailTbl tbody tr';
var UPDATE_EVENT = 'UPDATE';
var ADD_EVENT = 'ADD';
var VALIDATE_EVENT = 'VALIDATE';
var TEMPLATE_ID = 'templateId';
var VERSION_DETAIL_TBL = 'versionDetailTbl';
var VERSION_ROW = 'tr[name=versionRow]';
var VERSION = 'version';

var versionDetailClasses = {'.labelOne' : 'label1','.labelTwo' : 'label2','.slabOne' : 'slab1',
							'.slabTwo' : 'slab2','.slabThree' : 'slab3','.pulseOne' : 'pulse1',
							'.pulseTwo' : 'pulse2','.pulseThree' : 'pulse3','.rateOne' : 'rate1',
							'.rateTwo' : 'rate2','.rateThree' : 'rate3','.rateCardTierRateType' : 'tierRateType',
							'.fromDate' : 'fromDate'};

var skipValidation = ['slab2','slab3', 'rate2', 'rate3', 'pulse2', 'pulse3'];

var index = 0;
function addVersion(){
	index =  $('#'+VERSION_DETAIL_TBL).find(VERSION_ROW).length;
	var count;
	if(index > 0){
		count = $($('#'+VERSION_DETAIL_TBL).find(VERSION_ROW)[0]).children().find('h4')[0].innerText.split(" ")[1];
		count = Number(count)+1;
	}
	else{
		count = index+1;
	}
	
	$('#'+TEMPLATE_ID).children().find('h4').text("version "+count);
	$('#'+TEMPLATE_ID).children().find('.'+VERSION)[0].name = "rateCardVersionRelation["+index+"].versionName";
	
	$('#'+TEMPLATE_ID).children().find('.'+VERSION)[0].value = "version "+count;
	var templateData = $('#ratecardVersionHeader').html();
	
	var rowData = "<tr name='versionRow'><td style='border:0px !important'>"+ templateData +"</td></tr>";
	$('#'+VERSION_DETAIL_TBL).prepend(rowData);
	
	$('#'+VERSION_DETAIL_TBL).find(".labelKeyOne").text($("#ratecardLabelOne").val());
	$('#'+VERSION_DETAIL_TBL).find(".labelKeyTwo").text($("#ratecardLabelTwo").val());
	index++;
	
	var defaultRateCardVersionDetailObj = $('span#addRow');
	addVersionDetail(defaultRateCardVersionDetailObj[1]);
}

var indexUpdate =  $('#'+VERSION_DETAIL_TBL).find(VERSION_ROW).length;
function addVersionData(){
	indexUpdate =  $('#'+VERSION_DETAIL_TBL).find(VERSION_ROW).length;
	var count;
	if(indexUpdate > 0){
		count = $($('#'+VERSION_DETAIL_TBL).find(VERSION_ROW)[0]).children().find('h4')[0].innerText.split(" ")[1];
		count = Number(count)+1;
	}
	else{
		count = indexUpdate+1;
	}
	$('#'+TEMPLATE_ID).children().find('h4').text("version "+count);
	$('#'+TEMPLATE_ID).children().find('.'+VERSION)[0].name = "rateCardVersionRelation["+(count-1)+"].versionName";
	$('#'+TEMPLATE_ID).children().find('.'+VERSION)[0].value = "version "+count;
	var templateData = $('#ratecardVersionHeader').html();
	
	var rowData = "<tr name='versionRow'><td style='border:0px !important'>"+ templateData +"</td></tr>";
	$('#'+VERSION_DETAIL_TBL).prepend(rowData);
	
	$('#'+VERSION_DETAIL_TBL).find(".labelKeyOne").text($("#ratecardLabelOne").val());
	$('#'+VERSION_DETAIL_TBL).find(".labelKeyTwo").text($("#ratecardLabelTwo").val());
	
	
	var defaultRateCardVersionDetailObj = $('span#addRow');
	addVersionDetail(defaultRateCardVersionDetailObj[1]);
	
	initializeFromDate();
}

function addVersionDetail(currentObj){
	var versionIndex = $(currentObj).closest('tr').find('h4')[0].innerText.split(" ")[1]-1;
	var tableObj = $(currentObj).closest('table');
	var array = tableObj[0].children;
	var innerIndex = (array.length) -1;

	createTable("#tableTemplate > tbody > tr", ADD_EVENT, versionIndex, innerIndex);

	var trTemplateData = $('#tableTemplate').html();
	$(tableObj).append(trTemplateData);
	$('#'+VERSION_DETAIL_TBL).children().find(".fromDate").datepicker();
	initializeFromDate();
}

function generateRateCardTable(data){
	for(var index = 0 ;index < data.length; index++){
		$('#'+TEMPLATE_ID).children().find('h4').text(data[index].versionName);
		var outerIndex = $('#'+TEMPLATE_ID).children().find('h4')[0].innerText.split(" ")[1]-1;
		$('#'+TEMPLATE_ID).children().find('.'+VERSION)[0].name = "rateCardVersionRelation["+(outerIndex)+"].versionName";
		$('#'+TEMPLATE_ID).children().find('.'+VERSION)[0].value = "version "+ (outerIndex+1);
		var templateData = $('#ratecardVersionHeader').html();
		
		var object = $.parseHTML(templateData);
		var tableObj = $(object).closest('table');
		
		var trTemplateData = $('#tableTemplate').html();
		$(tableObj).append(trTemplateData);
	
		for(var rateCardVersion = 0 ; rateCardVersion < data[index].rateCardVersionDetail.length; rateCardVersion++){
			if(rateCardVersion !=0){
				var childDataRow = $(tableObj[0]).find('tbody:first > tr:last-child').html();
				$(tableObj[0]).find('tbody:first > tr:last-child').after("<tbody><tr name='upRow'>"+childDataRow+"</tr></tbody>");
			}
			createTable($(tableObj[0]).find("tbody:first > tr:last-child"), UPDATE_EVENT, outerIndex, rateCardVersion,data,index);
		}
		var b = $(tableObj).html();
		var rowData = "<tr name='versionRow'><td style='border:0px !important'>" +
				      "<table class='table table-blue table-bordered version-detail'>"+
		                b+"</table></td></tr>";
		$("#"+VERSION_DETAIL_TBL).append(rowData);
		$("#"+VERSION_DETAIL_TBL).children().find(".fromDate").datepicker();
		
		$("#"+VERSION_DETAIL_TBL).find(".labelKeyOne").text($("#ratecardLabelOne").val());
		$("#"+VERSION_DETAIL_TBL).find(".labelKeyTwo").text($("#ratecardLabelTwo").val());
	}
	
}

function initializeFromDate() {
	
	$.datepicker.setDefaults({
		changeMonth : 'true',
		changeYear : 'true',
		dateFormat: 'dd-M-yy' 
	});
}

function validate(category,id){
	var generalError = "generalError";
	var bgDanger  = "bg-danger";
	var isValid = true;
	if(category == 'editNew'){
		isValid =  verifyUniquenessOnSubmit('ratecardName','create','','com.elitecore.corenetvertex.pd.ratecard.RateCardData','','');
	}
	else{
		isValid =  verifyUniquenessOnSubmit('ratecardName','update',id,'com.elitecore.corenetvertex.pd.ratecard.RateCardData','','');
	}
	
	if (isValid == false) {
		return false;
	} else {
		var versionLength = $('#'+VERSION_DETAIL_TBL).find(VERSION_ROW).length;
		if (versionLength == 0) {
			$("#"+generalError).addClass(bgDanger);
			$("#"+generalError).text("<s:text name='ratecard.version.error'/>");
			return false;
		}
		
		else{
			var isValidBaseDn = true;
			$("#"+VERSION_DETAIL_TBL+" tbody "+VERSION_ROW).each(function(){
				
				var rowLength =  $(this).find(".version-detail tbody tr[name='upRow']").length;
				if( rowLength > 0){
					isValidBaseDn = createTable($(this).find(".version-detail tbody tr[name='upRow']"), VALIDATE_EVENT,0,0,0,0,isValidBaseDn);
				}
				else{
					$("#"+generalError).addClass(bgDanger);
					$("#"+generalError).text("<s:text name='ratecard.version.detail.error'/>");
					isValidBaseDn =  false;
				}
			});
		    return isValidBaseDn;           
		}
		
	}
}

function updateLabelsOnBlur(labelKey, rateCardLabel, outerIndex, innerIndex){
	$("#versionDetailTbl").find(labelKey).text($(rateCardLabel).val());
}

function createTable(domElement, eventName, outerIndex, innerIndex,parentData,valueIndex,isValidBaseDn){
	
	switch(eventName) {
    case 'UPDATE':
    	$(domElement).each(function () {
    		hideRows(this,'firstSection', true);
    		hideRows(this,'secondSection', true);
    		hideRows(this,'thirdSection', true);
    		createRateCardSubverionTable($(this), eventName, outerIndex, innerIndex,false,parentData,valueIndex);
    	});
        break;
    case 'ADD':
    	$(domElement).each(function () {
    		hideRows(this,'firstSection', true);
    		hideRows(this,'secondSection', true);
    		hideRows(this,'thirdSection', true);
    		createRateCardSubverionTable($(this), eventName, outerIndex, innerIndex);
    	});
        break;
    case 'VALIDATE':
    	$(domElement).each(function () {
    		isValidBaseDn = createRateCardSubverionTable($(this), eventName,outerIndex, innerIndex,isValidBaseDn);
    	});
        break;
	}
	return isValidBaseDn;
}


function createRateCardSubverionTable(obj, eventName, outerIndex, innerIndex, isValidBaseDnValue,parentData,valueIndex){
	$.each(versionDetailClasses, function(key,value) {
		var domObj = $(obj).children().find(key);
		if( key == '.rateCardTierRateType' && domObj != undefined && domObj.length > 0){
			if(eventName == ADD_EVENT){
            	$(domObj).attr('name',"rateCardVersionRelation["+outerIndex+"].rateCardVersionDetail["+innerIndex+"].tierRateType");
         	}
         	else if(eventName == UPDATE_EVENT){
         		$(domObj).attr('name',"rateCardVersionRelation["+outerIndex+"].rateCardVersionDetail["+innerIndex+"].tierRateType");
         		$(obj).find(".rateCardTierRateType option[value='" + parentData[valueIndex].rateCardVersionDetail[innerIndex].tierRateType+"']").attr("selected","selected");
         	}
        }
		else{
			if(domObj != undefined && domObj.length > 0 ){
				isValidBaseDnValue = setValueEventBase(outerIndex, innerIndex, valueIndex , domObj,parentData,value, eventName, isValidBaseDnValue);
	        }
		}
	});
    return isValidBaseDnValue;
}



function setValueEventBase(outerIndex, innerIndex, valueIndex, obj, parentData, value, event, isValidBaseDnValue) {
for (var index = 0; index < obj.length; index++) {
	if (event == ADD_EVENT) {
		obj[index].name = "rateCardVersionRelation[" + outerIndex
				+ "].rateCardVersionDetail[" + innerIndex + "]." + value;
	} else if (event == UPDATE_EVENT) {
		obj[index].name = "rateCardVersionRelation[" + outerIndex
				+ "].rateCardVersionDetail[" + innerIndex + "]." + value;
		if(value == 'fromDate'){
			var value = parentData[valueIndex].rateCardVersionDetail[innerIndex][value];
			var d = new Date(value);
			obj[index].attributes.getNamedItem("value").value = $.datepicker.formatDate('dd-M-yy', d);
		}
		else{
			obj[index].attributes.getNamedItem("value").value = parentData[valueIndex].rateCardVersionDetail[innerIndex][value];
		}
	} else {
		var skipCheck = true;
		   for(var skipValIndex = 0 ; skipValIndex < skipValidation.length ; skipValIndex++){
			   if(skipValidation[skipValIndex] == value){
				   skipCheck = false;
				   break;
			   }
		   }
		  if(skipCheck){
			  isValidBaseDnValue = isEmpty(obj, isValidBaseDnValue);
		  }
	}
}
return isValidBaseDnValue;
}


function isEmpty(datas, isValid) {
for (var index = 0; index < datas.length; index++) {
	if (typeof datas[index] == undefined
			|| typeof datas[index].value == undefined
			|| datas[index].value == ''
			|| datas[index].value == 'undefined') {
		setErrorOnElement(datas, "<s:text name='ratecard.version.value.required'/>");
		isValid = false;
		break;
	}
}

return isValid;
}

function hideRows(obj,  domId1,domId2,domId3, check){
	if(check){
		$(obj).find('#'+domId1+' tr:gt(0)').hide();
		$(obj).find('#'+domId2+' tr:gt(0)').hide();
		$(obj).find('#'+domId3+' tr:gt(0)').hide();
	}
	else{
		var domObj1 = $(obj).closest('table').children().find('#'+domId1);
		$(domObj1).find('tr:gt(0)').hide();
		
		var domObj2 = $(obj).closest('table').children().find('#'+domId2);
		$(domObj2).find('tr:gt(0)').hide();
		
		var domObj3 = $(obj).closest('table').children().find('#'+domId3);
		$(domObj3).find('tr:gt(0)').hide();
		
		$(obj).closest('table').children().find('#'+domId3+'Hide').hide();
		$(obj).closest('table').children().find('#'+domId3+'Show').show();
	}
}

function showRows(obj, domId1,domId2,domId3){
	var domObj1 = $(obj).closest('table').children().find('#'+domId1);
	$(domObj1).find('#'+domId1+'Hide').show();
	$(domObj1).find('tr').show();
	
	var domObj2 = $(obj).closest('table').children().find('#'+domId2);
	$(domObj2).find('#'+domId2+'Hide').show();
	$(domObj2).find('tr').show();
	
	var domObj3 = $(obj).closest('table').children().find('#'+domId3);
	$(domObj3).find('#'+domId3+'Hide').show();
	$(domObj3).find('tr').show();
	
	$(obj).closest('table').children().find('#'+domId3+'Show').hide();
	$(obj).closest('table').children().find('#'+domId3+'Hide').show();
}


function autoCompleteForLabelKey(){
	$('#ratecardLabelOne').autocomplete();
	$('#ratecardLabelTwo').autocomplete();
	
	var list = [ <s:iterator value="@com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants@values()" >
	'<s:property value="name"/>',
	</s:iterator> ];
	commonAutoComplete("ratecardLabelOne",list);
	commonAutoComplete("ratecardLabelTwo",list);
	}; 
</script>
