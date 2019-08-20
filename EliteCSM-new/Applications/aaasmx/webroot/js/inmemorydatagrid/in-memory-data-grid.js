var instanceData;
var nodes = [];
var edges = [];
var network, container;
var data = {};
var options = {};
var zoomstep = 0.3;
var currentzoom;
var myRadiusAttributeArray;

function setInstanceNameData(instanceData){
	this.instanceData = instanceData;
}

$(document).ready(function(){
	
	$('.outbound-btn').click(function(){
		var tableRowStr = $("#port-mapping-template-table").find("tr");
		$('#port-mapping-table').append("<tr>"+$(tableRowStr).html()+"</tr>");
	});
	
	$('#add-group-btn').click(function(){
		var tableRowStr = $("#group-mapping").find("tr");
		$("#group-table").append("<tr>"+$(tableRowStr).html()+"</tr>");
	});
	
	$('.add-property').click(function(){
		var tableRowStr = $("#string-prop-mapping-template-table").find("tr");
		$('#string-property-table').append("<tr>"+$(tableRowStr).html()+"</tr>");
	});
	
	$('#topology-btn').click(function(){
		$('.topology-tr').show();
	});
	
	$('#radius-field-mapping-section').append($('#radius-field'));
	$('#diameter-field-mapping-section').append($('#diameter-field'));
	
	$('#common-field-mapping-table tr').each(function() {
	    $('#radius-field-mapping-table').append("<tr>"+$(this).html()+"</tr>");
	    $('#diameter-field-mapping-table').append("<tr>"+$(this).html()+"</tr>");
	});
	
	$('#sessionDataMapping tr').each(function() {
		$('#radsessionmappingtbl').append("<tr>"+$(this).html()+"</tr>");
	});
	
});

function loadAttributesForAutoCompletion(){
	loadAttributesFromDictionary(myRadiusAttributeArray,"attributeValue");
}


function retriveRadiusAttribute(searchNameOrAttributeId,txtFields) {
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

/**Autocomplete For AttribureId */
function loadAttributesFromDictionary(autoCompleteArray, mappingObj) {
	if (mappingObj != null) {
		$(".attributeValue").autocomplete({
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

function split( val ) {
	return val.split( /[,;]\s*/ );
}
function extractLast( term ) {
	return split( term ).pop();
} 

function setInsDropDown(){
	var proxyListIdNameArray = getSelectedInstance();
	$('#group-table').find(".instanceName").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value=''>--select--</option>");
		if(instanceData!=undefined){
			$.each(instanceData, function(index, item) {
				if( $.inArray(item.id,proxyListIdNameArray) < 0 ||  item.id == currentVal ){
					$(selectObj).append("<option value='" + item.name + "'>" + item.name + "</option>");
				}
			});
		}
		$(selectObj).val(currentVal);
	}); 
}

function setInstanceDropDown(object){
	var proxyListIdNameArray = getSelectedInstance();
	
	$('#group-table').find(".instanceName").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value=''>--select--</option>");
		if(instanceData!=undefined){
			$.each(instanceData, function(index, item) {
				if( $.inArray(item.id,proxyListIdNameArray) < 0 ||  item.id == currentVal ){
					$(selectObj).append("<option value='" + item.name + "'>" + item.name + "</option>");
				}
			});
		}
		$(selectObj).val(currentVal);
	}); 
}

function getSelectedInstance(){
	var proxyListIdNameArray = new Array();
	var arrIndex = 0;
	$('#group-table').find(".instanceName").each(function(){
		if($(this).val() != ""){
			proxyListIdNameArray[arrIndex++] = $(this).val();
		}				
	});
	return proxyListIdNameArray;
}

function addMapping(obj){
	var tableObject = $(obj).parent().parent().parent().find('.ip-table-mapping');
	var tableRowStr = $("#ipTableMappingTamplate").find("tr");
	$(tableObject).append("<tr>"+$(tableRowStr).html()+"</tr>");
	addServerInstanceDown(obj);	
}

function addServerInstanceDown(selectedObject){
	var proxyListIdNameArray = getSelectedInstance();
	var tableObj = $(selectedObject).parent().parent().parent();
	var selectBoxObj = $(tableObj).find( "tr:last").find(".instanceName");
	$(selectBoxObj).append("<option value=''>--Select--</option>");
	
	if(instanceData!=undefined){
		$.each(instanceData, function(index, item) {
			if($.inArray(item.id,proxyListIdNameArray) < 0 ){
				$(selectBoxObj).append("<option value='" + item.id + "'>" + item.name + "</option>");
			}
		});
	}
}

function removeData(obj){
	$(obj).parent().parent().remove();
	setInsDropDown();
}

function removeBlog(obj){
	$(obj).parent().parent().parent().parent().remove();
}

function addDefaultGroup(){
	var tableRowStr = $("#group-mapping").find("tr");
	$("#group-table").append("<tr>"+$(tableRowStr).html()+"</tr>");
}

function validateRadiusMapping(){

	if(validateRadiusIMDGMapping() && validateRadiusInMemorySessionFieldMapping()){
		var jsonData = createRadiusIMDGJSON();

		$('#imdgConfigRadiusMapping').val(JSON.stringify(jsonData));
		
		if(isPositiveNumber($('#batchsize').val()) == false || $('#batchsize').val() <= '0'){
			alert('Total Batch Count must be Numeric and Positive Integer');
			$('#batchsize').focus();
			return false;
		}else if(isPositiveNumber($('#sessiontimeout').val()) == false || $('#sessiontimeout').val() <= '0'){
			alert('Session Time out must be Numeric and Positive Integer');
			$('#sessiontimeout').focus();
			return false;
		}else if(isPositiveNumber($('#threadsleeptime').val()) == false || $('#threadsleeptime').val() <= '0') {
			alert('Thread sleep time must be Numeric and Positive Integer');
			$('#threadsleeptime').focus();
			return false;
		}
		else{
			$('#radiusSessionMappingId').submit();
		}
	}
}

function validateRadiusInMemorySessionFieldMapping() {
	return isDuplicateMapping('#radsessionmappingtbl tr', "Radius InMomory Session Field Mapping");	
}

function validateRadiusIMDGMapping(){
	
	return isDuplicateMapping('#radius-field-mapping-table tr',"Radius Session Index Mapping");
}

function createRadiusIMDGJSON(){
		return validateFiledMapping('#radius-field-mapping-table tr','radiusIMDGFieldMapping', '#radsessionmappingtbl tr', 'radiusSessionFieldMappingList');
}

function validateFiledMapping(tableName1,tableType1,tableName2,tableType2){
	var filedMapping = [];
	var indexCounter = 0;
	var indexPreString = "index";
	$(tableName1).each(function(){
		var key = $(this).find("input[name='key']").val();
		var value = $(this).find("input[name='value']").val();
		if( (key != undefined && value != undefined) && (key != "" || value != "") ){
			filedMapping.push({'imdgIndex':indexPreString+indexCounter,'imdgFieldValue':key,'imdgAttributeValue':value});
            indexCounter++;
        }
	});
	var item = {};
	
	item["active"] = $('#active').val();
	var radiusImdgJson = {};
	
	
	
	radiusImdgJson[tableType1] = filedMapping;
	
	indexCounter = 0;
	var radiusSessionFieldMapping = [];
	
	$(tableName2).each(function(){
		var key = $(this).find("input[name='key']").val();
		var value = $(this).find("input[name='value']").val();
		if( (key != undefined && value != undefined) && (key != "" || value != "") ){
			radiusSessionFieldMapping.push({'fieldName':key,'attributes':value});
            indexCounter++;
        }
	});

	radiusImdgJson[tableType2] = radiusSessionFieldMapping;
	
	// session closure json
	var radiusSessionClosureJson = {};
	
	var sessionClouseTable = '#closureproperties';
	radiusSessionClosureJson["sessionCloseBatchCount"] = $(sessionClouseTable).find("#batchsize").val();
	radiusSessionClosureJson["sessionTimeout"]=$(sessionClouseTable).find("#sessiontimeout").val();
	radiusSessionClosureJson["closureInterval"]=$(sessionClouseTable).find("#threadsleeptime").val();
	radiusSessionClosureJson["sessionOverrideField"]=$(sessionClouseTable).find("#overridefield").val();
	radiusSessionClosureJson["action"]=$(sessionClouseTable).find("#closeaction").val();
	
	radiusImdgJson["sessionClosureProperties"] = radiusSessionClosureJson;
	
	item["imdgRadiusConfig"] = radiusImdgJson;

	console.log("The Value to be submitted is : ");
	console.log(item);
	
	return item;
}


function validateDiameterMapping(){
	
	if(validateDiameterIMDGMapping()){
		var jsonData = createDiameterIMDGJSON();

		$('#imdgConfigDiameterMapping').val(JSON.stringify(jsonData));
		
		$('#diameterSessionMappingId').submit();
	}
}

function validateDiameterIMDGMapping(){
	return isDuplicateMapping('#diameter-field-mapping-table tr',"Diameter Session Index Mapping");
}

function createDiameterIMDGJSON(){
	return validateFiledMapping('#diameter-field-mapping-table tr','diameterIMDGFieldMapping');
}

function validateForm(){

	if (validateIMDGForm()) {
	
		var jsonData = createIMDGJSON();
		
		$('#imdgConfigBasicDetail').val(JSON.stringify(jsonData));
		
		$('#inMemoryDataGridId').submit();
	}
}

function validateIMDGForm(){
	var isValid = true;
	var instanceNameArray = new Array();
	var ipAddressArray = new Array();
	var arrIndex = 0;
	var groupNameArray = [];
	
	if($('#group-table').find('.div-container').length == 0){
		alert('At least one group configuration must be specified');
		$('#add-group-btn').focus();
		return false;
	}
	
	$('#group-table').find('.div-container').each(function(){
		var groupname = $(this).find("input[name='groupname']").val();
		var grouppassword = $(this).find(".grouppassword").val();
		
		if(groupname == ''){
			alert('Group Name must be specified');
			$(this).find("input[name='groupname']").focus();
			isValid = false;
			return false;
		}
		
		if(grouppassword == ''){
			alert('Group Password must be specified');
			$(this).find(".grouppassword").focus();
			isValid = false;
			return false;
		}
		
		$(this).find('.ip-table-mapping tr').each(function(){
			
			var rowObject = $(this);
			
			var instanceName = $(rowObject).find('.instanceName').val();
			var ipAddress = $(rowObject).find('.ip').val();
			
			if( instanceName != undefined && ipAddress != undefined){
				
				if(instanceName == ''){
					alert('Instance Name must be specified');
					$(rowObject).find('.instanceName').focus();
					isValid = false;
					return false;
				}
				
				if(!isNewDuplicateMapping(instanceNameArray, ipAddressArray, instanceName, ipAddress)){
					instanceNameArray[arrIndex] =  instanceName;
					ipAddressArray[arrIndex++] = ipAddress;
				}else{
					alert("Mapping with "+instanceName+" and "+ipAddress+" exists multiple times between groups");
					$(this).find('.instanceName').focus();
					isValid = false;
					return false;
				}
			}
		});
		
		groupNameArray.push(groupname);
		
	});
	
	if( isValid ){
		if( isDuplicate(groupNameArray) ){
			isValid = false;
		}else if($('#inMemoryFormat').val() == ''){
			alert('Please select In-Memory Format');
			$('#inMemoryFormat').focus();
			isValid = false;
		}else if($('#startPort').val() == ''){
			alert('Start Port must be specififed');
			$('#startPort').focus();
			isValid = false;
		}else if($('#startPortCount').val() == ''){
			alert('Start Port Count must be specififed');
			$('#startPortCount').focus();
			isValid = false;
		}else if(isPositiveNumber($('#startPort').val()) == false || $('#startPort').val() == '0'){
			alert('Start Port must be Numeric and Positive Integer');
			$('#startPort').focus();
			isValid = false;
		}else if(isPositiveNumber($('#startPortCount').val()) == false || $('#startPortCount').val() == '0'){
			alert('Start Port Count must be Numeric and Positive Integer');
			$('#startPortCount').focus();
			isValid = false;
		}
	}
	
	return isValid;
}

function isDuplicateMapping(table,tabletype){
	var isValid = true;
	var myArry = [];
	$(table).each(function(){
		
		var value = $(this).find("input[name='key']").val();
		if(value != undefined && value != ""){
			if($.inArray(value, myArry) === -1){
				myArry.push(value);
			}else {
				alert("Duplicate Key "+ value +" found in "+tabletype);
				isValid = false;
			}
		}
		
	});
	return isValid;
}

function isDuplicate(groupNameArray) {
	
	var i, j, n;
	n = groupNameArray.length;
                                             
	for (i=0; i<n; i++) {                       
		for (j=i+1; j<n; j++) {              
			if (groupNameArray[i]==groupNameArray[j]) {
				alert('Duplicate Group Name [ ' + groupNameArray[j] + ' ] found. It must be Unique');
				return true;
			}
	}	}//Convert into relevant POJO 
	
	return false;
}

function isNewDuplicateMapping(logicalNameArray, dbFieldArray, logicalNameVal, dbFieldVal){
	var isDuplicate = false;
	$(logicalNameArray).each(function (index, element){
		console.log(index);
		console.log(element);
		
		if(element == logicalNameVal && dbFieldArray[index] == dbFieldVal){
			isDuplicate = true;
			return false;
		}			
	});
	return isDuplicate;
}
function createIMDGJSON(){
	var clusterGroup = fetchClusterGroup();
	var stringProp = [];
	
	$('#string-property-table tr').each(function(){
		var key = $(this).find("input[name='key']").val();
		var value = $(this).find("input[name='value']").val();
		
		if( key != undefined && value != undefined ){
			stringProp.push({'key':key,'value':value});
		}
	});
	
	var jsonObject = {'active':$('#active').val(), 
					  'clusterGroups': clusterGroup,
					  'propertyList': stringProp,
					  'inMemoryFormat' : $('#inMemoryFormat').val(),
					  'startPort':$('#startPort').val(),
					  'startPortCount':$('#startPortCount').val(),
					  'outboundPorts':$('#outboundPorts').val(),
					  'mancenterUrl':$('#mancenterUrl').val()
					  };
	
	console.log("The Value to be submitted is : ");
	console.log(jsonObject);
	
	return jsonObject;
}

function fetchClusterGroup(){
	
	var clusterGroup = [];
	
	$('#group-table').find('.div-container').each(function(){
		
		var memberDatas = [];
		
		var groupname = $(this).find("input[name='groupname']").val();
		var grouppassword = $(this).find(".grouppassword").val();
		var groupdescription  = $(this).find(".groupdescription").val();
		
		$(this).find('.ip-table-mapping tr').each(function(){
			var name = $(this).find("select[name='name']").val();
			var ip = $(this).find("input[name='ip']").val();
			
			if( name != undefined && ip != undefined ){
				memberDatas.push({
					'name' : name,
					'ip' : ip
				});
			}
		});
		
		if( groupname != "" ) {
			
			clusterGroup.push({'name': groupname,
				   'passwd': grouppassword,
				   'description': groupdescription,
				   'memberDatas': memberDatas});
		}
		
	});
	
	return clusterGroup;
}


function addUpdatedValue(imdgConfigJSON){
 
	console.log('IMDG config JSON : ');
	console.log(imdgConfigJSON);
	var isBasicDetailsEmpty = true;
	
	$.each(imdgConfigJSON, function(imdgKey,imdgValue){
		  
	   if( imdgKey == 'propertyList'){
		  
		   $.each(imdgValue, function(propKey, propValue){
			  
			   var tableRowStr = $("#string-prop-mapping-template-table").find("tr");
			   $('#string-property-table').append("<tr>"+$(tableRowStr).html()+"</tr>");
			   
			   var propertyTableRow = $('#string-property-table tr:last').find("input[name='key']");
			   $(propertyTableRow).val(propValue.key);
			   
			   var propertyTableValue = $('#string-property-table tr:last').find("input[name='value']");
			   $(propertyTableValue).val(propValue.value);
		   });
		   
	   }else if( imdgKey == 'clusterGroups'){
		  
		   isBasicDetailsEmpty = false;
		   $.each(imdgValue, function(groupKey, groupValue){
			   
			   var tableRowStr = $("#group-mapping").find("tr");
			   $("#group-table").append("<tr>"+$(tableRowStr).html()+"</tr>");
			   
			   var groupNameTextBox = $('#group-table > tbody > tr:last').find(".groupname");
			   $(groupNameTextBox).val(groupValue.name);
			   
			   var groupNamePasswordTextBox = $('#group-table > tbody > tr:last').find(".grouppassword");
			   $(groupNamePasswordTextBox).val(groupValue.passwd);
			   
			   var groupNameDescTextBox = $('#group-table > tbody > tr:last').find(".groupdescription");
			   $(groupNameDescTextBox).val(groupValue.description);
			   
			   $.each(groupValue.memberDatas, function(memberKey, memberValue){
				   
				   var tableObject = $('#group-table > tbody > tr:last').find(".ip-table-mapping");
				   var tableRowStr = $("#ipTableMappingTamplate").find("tr");
				   $(tableObject).append("<tr>"+$(tableRowStr).html()+"</tr>");
				   
				   var instanceTextBox = $('#group-table > tbody > tr:last').find(".ip-table-mapping > tbody > tr:last" ).find('.instanceName');
				   
				   $(instanceTextBox).append($('<option>', {
					    value: '',
					    text: '--select--'
					}));
				   
				   $(instanceTextBox).append($('<option>', {
					    value: memberValue.name,
					    text: memberValue.name
					}));
				   
				   $(instanceTextBox).val(memberValue.name);
				   
				   var ipTextbox = $('#group-table > tbody > tr:last').find(".ip-table-mapping > tbody > tr:last" ).find('.ip');
				   $(ipTextbox).val(memberValue.ip);
			   });
		   });
	   }else if(imdgKey == 'imdgRadiusConfig'){
		   $.each(imdgValue, function(radKey, radValue){
			   var index = 1;
			   if(radKey == 'radiusIMDGFieldMapping'){
				   $.each(radValue, function(mappingField,mappingValue){
					   var propertyTableKey = $('#radius-field-mapping-table  tr:eq('+ index +')').find("input[name='key']");
					   var propertyTableValue = $('#radius-field-mapping-table  tr:eq('+ index +')').find("input[name='value']");
					   index++;
					   $(propertyTableKey).val(mappingValue.imdgFieldValue);
					   $(propertyTableValue).val(mappingValue.imdgAttributeValue); 
				   });
			   }else if(radKey == 'radiusSessionFieldMappingList'){
				   $('#radsessionmappingtbl').find("tr:gt(1)").remove();
				   $.each(radValue, function(mappingField,mappingValue){
					   addRow('dbMappingTable','radsessionmappingtbl');
					   var findTableObj = $("#radsessionmappingtbl tr:last");
					   
					   $(findTableObj).find("input[name='key']").val(mappingValue.fieldName);
					   $(findTableObj).find("input[name='value']").val(mappingValue.attributes);
				   });
			   }else if(radKey == 'sessionClosureProperties') {
				   var radiusSessionClosureJson = {};
				   var sessionClouseTable = '#closureproperties';
				   $(sessionClouseTable).find("#batchsize").val(radValue.sessionCloseBatchCount);
				   $(sessionClouseTable).find("#sessiontimeout").val(radValue.sessionTimeout);
				   $(sessionClouseTable).find("#threadsleeptime").val(radValue.closureInterval);
				   $(sessionClouseTable).find("#overridefield").val(radValue.sessionOverrideField);
				   $(sessionClouseTable).find("#closeaction").val(radValue.action);
				   
				   $('#'+ radKey).val(radiusSessionClosureJson);

			   }
		   });
	   }else if(imdgKey == 'imdgDiameterConfig'){
		   $.each(imdgValue, function(diaKey, diaValue){
			   var index = 1;
			   if(diaKey == 'diameterIMDGFieldMapping'){
				   $.each(diaValue, function(mappingField,mappingValue){
					   var propertyTableKey = $('#diameter-field-mapping-table  tr:eq('+ index +')').find("input[name='key']");
					   var propertyTableValue = $('#diameter-field-mapping-table  tr:eq('+ index +')').find("input[name='value']");
					   index++;

					   $(propertyTableKey).val(mappingValue.imdgFieldValue);
					   $(propertyTableValue).val(mappingValue.imdgAttributeValue);
				   });
			   }
		   });
	   }else{
		  if( imdgKey == 'active'){
			  
			  if(imdgValue.toString() == 'false'){
					$('#inMemoryDataGridEnabledDisabled').show();
			  }else{
					$('#inMemoryDataGridEnabledDisabled').hide();
			  }
		  } 
		  $('#'+ imdgKey).val(imdgValue.toString());
	   }
   });
	
	
	if(isBasicDetailsEmpty){
		var tableRowStr = $("#group-mapping").find("tr");
		$("#group-table").append("<tr>"+$(tableRowStr).html()+"</tr>");
		$('#startPort').val("5701");
		$('#startPortCount').val("500");
	}
}

function drawTopology() {
	
	container = document.getElementById('topology');
	
	data = {
	    nodes: nodes,
	    edges: edges
	};
	
	options = {
	    nodes: {
	        shape: 'icon',
	        size: 20,
	        font: {
	            size: 15,
	            color: 'black',
	            mono : 'calibri',
	            bold : 'true'
	        },
	        icon: {
	            face: 'FontAwesome',
	            code: '\uf0c2',
	            size: 50,
	            color: '#015198'
	        },
	        borderWidth: 2
	    },
	    edges: {
	        width: 2
	    },
	    groups: {
	        instances: {
	            shape: 'icon',
	            icon: {
	                face: 'FontAwesome',
	                code: '\uf1b2',
	                size: 50,
	                color: '#60A917'
	            }
	        },
	        source: {
	            color:{border:'white'}
	        }
	    },
	    interaction : {
	    	navigationButtons : true,
	    	keyboard : false
	    },
	    physics: { stabilization: false }
	};
	network = new vis.Network(container, data, options);
}


function zoomin() {
	// network.setScale(network.getScale() - zoomstep);
}

function zoomout() {
	// network.setScale(network.getScale() + zoomstep);
}

function initHandlers() {
    document.getElementById("zoomin").addEventListener("click", zoomin);
    document.getElementById("zoomout").addEventListener("click", zoomout);
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
