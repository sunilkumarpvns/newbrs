/**
 * This method validates whether at least one handler is added to the flow or not, 
 * and if the handler is added then at least one handler should be enable 
 * @param tableId Id of table that contains handlers
 * @returns {Boolean} Return true if invalid else false
 */
function validateNoOfHandler(tableId){
	var cnt = 0; 
	var isHandlerEnabled = false;
	var tableTr = $("#"+tableId+" > tbody > tr");
	if(tableTr.length > 0){
		tableTr.each(function(){
			if(isEmpty($(this).text()) == false){
				cnt++;
			}
			if($(this).find('input[name="isHandlerEnabled"]').val() == 'true'){
				isHandlerEnabled = true;
				return false;
			}
		});
	} 
	
	if(cnt == 0 ){
		alert('Please add atleast one handler');
	}else{
		if(isHandlerEnabled == false){
			alert('Please enable atleast one handler');
		}
	}
	
	return !isHandlerEnabled;
}

/**
 * This method is used to validate CDR handler in Auth & Acct flow of radius service policy. Minimum one
 * mapping has to be added in each CDR handler.
 * @returns true if mapping is added else false
 */
function validateCDRHandler(){
	var valueToReturn = true;
	$('.form_cdrGeneretaion').each(function(){
		var cdrTable = $(this).find('.mappingtblcdr');
		var cdrTr = cdrTable.children("tbody").children("tr");
		var noOfRows = cdrTr.length;
		
		if(noOfRows < 2){
			valueToReturn = false;
			var handler = $(this).find('table[name="tblmAdditionalCDRGeneration"]');
			$(handler).css({'border':'1px solid red'});
			alert('At least one mapping is required in CDR handler.');
			var addMappingButton = $(this).find('.light-btn');
			$(addMappingButton).focus();
			return false;
		} else {
			var skipFirstStep= false;
			cdrTr.each(function(){
				if(!skipFirstStep){
		        	skipFirstStep=true;
		        } else {
		        	var primaryDriverSelect = $(this).find('.primaryDriverId');
		        	var primaryDriverVal = primaryDriverSelect.val();
		        	
		        	if(primaryDriverVal == 0){
		        		$(primaryDriverSelect).css({'border':'1px solid red'});
		        		alert('Please select at least one primary driver from drop down');
		        		valueToReturn = false;
		        		return false;
		        	}
		        }
			});
			
			if(valueToReturn == false){
				return false;
			}
		}
	});
	return valueToReturn;
} 

/**
 * This method provides Warning Message if In Radius Service policy Basic Detail session-Manager
 * Not selected Still Concurrency handler Added.
 */
function  sessionManagerWarning(){
	
	var sessionManagerIds = $('#sessionManagerId').val();
	var sessionManagerDiv = $('#sessionManagerDiv').val();
	 if(sessionManagerIds == 0 && typeof(sessionManagerDiv) != "undefined"){
		 alert('You should not add Concurrency Handler without configuring Session Manager in Basic Details.');
	 }
}

function changeValueOfRadiusPolicyGroup(obj){
    if($(obj).attr('checked')){
          $(obj).val('true');
     }else{
          $(obj).val('false');
     }
}

function validateStatefulProxySequentialHandler(){
    var noValueConfigured = false;
    var isValueConfigured = false;
    var valueToReturn = true;
    $('.form_statefulproxycommunication').each(function(){

        var proxyTableObj = $(this).find('.statefulProxyCommunicationTbl');
        var proxyMaintableObj = $(this).find('.tblmStatefulProxyCommunication');
        var idofTable = $(proxyTableObj).attr('id');
        var rowCount = $('#'+idofTable+' tr').length;
        if( rowCount == 1){
            noValueConfigured= true;
        }else if(rowCount > 1){
            isValueConfigured = true;
        }

        if(noValueConfigured){
            $(proxyMaintableObj).css({'border':'1px solid red'});
            alert('Atleast one Mapping is Required in Stateful Proxy Sequential Handler');
            var addMappingButton = $(this).find('.proxy-com-btn');
            $(addMappingButton).focus();
            valueToReturn=false;
            return false;
        }else if(isValueConfigured){
            proxyTableObj.find('tr').each(function(){
                if($(this).find("select[name='serverGroupName']").val() == 0) {
                    var innerTableObj = $(this).find('.corRadEsiClass');
                    $(innerTableObj).css({'border': '1px solid red'});
                    alert('Please Select at least one Server Group from drop down');
                    valueToReturn = false;
                    return false;
                }
            });
        }
    });
    return valueToReturn;
}

function validateStatefulProxyBroadcastHandler(){
    var noValueConfigured = false;
    var isValueConfigured = false;
    var valueToReturn = true;
    $('.form_statefulproxybroadcastcommunication').each(function(){

        var proxyTableObj = $(this).find('.statefulProxyBroadcastCommunicationTbl');
        var proxyMaintableObj = $(this).find('.tblmStatefulProxyBroadcastCommunication');
        var idofTable = $(proxyTableObj).attr('id');
        var rowCount = $('#'+idofTable+' tr').length;
        if( rowCount == 1){
            noValueConfigured= true;
        }else if(rowCount > 1){
            isValueConfigured = true;
        }

        if(noValueConfigured){
            $(proxyMaintableObj).css({'border':'1px solid red'});
            alert('Atleast one Mapping is Required in Stateful Proxy Broadcast Handler');
            var addMappingButton = $(this).find('.proxy-com-btn');
            $(addMappingButton).focus();
            valueToReturn=false;
            return false;
        }else if(isValueConfigured){
            proxyTableObj.find('tr').each(function(){
                if($(this).find("select[name='serverGroupName']").val() == 0) {
                    var innerTableObj = $(this).find('.corRadEsiClass');
                    $(innerTableObj).css({'border': '1px solid red'});
                    alert('Please Select at least one Server Group from drop down');
                    valueToReturn = false;
                    return false;
                }
            });
        }
    });
    return valueToReturn;
}
