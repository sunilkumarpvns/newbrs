
var message = {
		mappingMessage : "Wrong mappings configured. Do you still want to continue?",
		serverMessage : "No Live Server Found or Timeout Occur while validating mapping's conditions, Do you still want to continue?",
		conditionMessage : "Wrong mapping's conditions configured. Do you still want to continue?",
};

/*
 * This Function validate mappings of PacketMapping module.
 */

function validateMapping(rowID,conversationType){
	var policyKey = encodeURIComponent($("tr[id='" + rowID + "'] td input[id=policyKey]").val());
	var attribute = encodeURIComponent($("tr[id='" + rowID + "'] td input[id=attribute]").val());
	var defaultValue = encodeURIComponent($("tr[id='" + rowID + "'] td input[id=defaultValue]").val());
	var valueMapping = encodeURIComponent($("tr[id='" + rowID + "'] td input[id=valueMapping]").val()); 
	
	$.ajax({url:validateUrl,
		type: 'POST',
		async: false,
		data: 'method=validatePacketMapping&policyKey='+policyKey+'&attribute='+attribute +'&defaultValue='+defaultValue+'&valueMapping='+valueMapping+'&conversationType='+conversationType,
		success: function(transport){
			if(transport != 'SUCCESS'){
				$("tr[id='" + rowID + "']").children().css("box-shadow","0px 0px 7px #FF2400");
				var tableRowStr = '<tr id="noteTR'+rowID+'" name="noteTR">'+
								  '<td name="noteTP" class="tblfirstcol" colspan="6" align="center" id="noteTD'+rowID+'"></td>'+
	   			                  '</tr>';
				$(tableRowStr).insertAfter('#'+rowID);
				$("tr[id='noteTR" + rowID + "']").children().css("color","red");
				$("#noteTD"+rowID).text(transport);
				
			}else{
				$("tr[id='" + rowID + "']").children().css("box-shadow","none");
			}	
		}
	});
}


/*
 * This Function validate ruleSet for ServicePolicy , SubsciberCriteria for Promotional offer and realm-based condition for Routing table  
 */
function validateCondition(fieldId){
	var data = encodeURIComponent($("#"+fieldId).val());
	if(data == null || data.trim().length<=0){
			$("#"+fieldId).removeClass();
			$("#validCond").text('');
			alert("condition is empty");
			return;
	}else{
			$.ajax({url:validateUrl,
				type: 'POST',
				async:false,
				data: 'method=validateCondition&condition='+data,
				success: function(transport){
					onSuccessValidateCondition(transport,fieldId);
				}
			});
	}
}
function onSuccessValidateCondition(data,fieldId){
	if(data != 'SUCCESS'){
		$("#validCond").css('color', 'red');
		$("#validCond").text(data);
		$("#"+fieldId).addClass("failure");
	}else{
		$("#validCond").css('color', 'green');
		$("#validCond").html("<img src='"+image+"'/> Valid Condition.");
		$("#"+fieldId).removeClass();
		$("#"+fieldId).addClass("success");
	}
}

/*
 * These following functions validate conditions of mapping in radius gateway profile and diameter gateway profile
 */

function callValidateMappingCondition() {
	$("tr[name=noteTR]").each(function(){
		$(this).remove();
	});
	validateMappingCondition('mappingRequest','reqCondition');
	validateMappingCondition('mappingResponse','resCondition');
}
function validateMappingCondition(name,id){
	$("tr[name='"+name+"']").each(function(){
		var data = encodeURIComponent($(this).find("#"+id).val());
		if(data == null || data.trim().length<=0){
			$("#"+id).css("box-shadow","0px 0px 7px #FFFFFF");
			return;
		}else{
			var temp = $(this);
			$.ajax({url:validateUrl,
				type: 'POST',
				async: false,
				data: 'method=validateCondition&condition='+data,
				success: function(transport){
					if(transport != 'SUCCESS'){
						console.log($(this));
						$(temp).find("#"+id).css("box-shadow","0px 0px 7px #FF2400");
						var noteRow ='<tr name="noteTR"><td class="tblfirstcol" colspan="5" align="center"></td></tr>';
						$(noteRow).insertAfter(temp);
						$(temp).closest('tr').next().children().css("color","red");
						$(temp).closest('tr').next().children().text(transport);
					}else{
						$(temp).find("#"+id).css("box-shadow","none");
					}	
				}
			});	
		}
		
	});
}

function checkForNote() {
	var noteValue = null;
	$("tr[name=noteTR]").each(function(){
		noteValue = $(this).children('td').eq(0).html();
		return;
	});
	return noteValue;
}


