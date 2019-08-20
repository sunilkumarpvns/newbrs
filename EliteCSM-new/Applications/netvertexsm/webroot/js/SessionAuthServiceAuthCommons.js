

function checkForDuplicateServiceType(serviceTypeName){
	var tblrows 	= document.getElementById("pccRulesTable").rows;
	var chkPCCRules = document.getElementsByName("select");
	
	var pccRuleServiceTypeIds = document.getElementsByName("pccRuleServiceTypeId");	
    
	if(chkPCCRules!=null && chkPCCRules.length>0){
		for(var i1=0; i1<chkPCCRules.length; i1++){
			for(var i2=i1+1; i2<chkPCCRules.length; i2++){
				if(chkPCCRules[i1].checked==true && chkPCCRules[i2].checked==true && tblrows[i1+1].style.display!="none"){
						if(pccRuleServiceTypeIds[i1].value==pccRuleServiceTypeIds[i2].value){						
							chkPCCRules[i2].checked=false;							
							alert("You cannot select two PCC Rules with '"+serviceTypeName+"' Service Type");							
							return;
						}
				}
			}
		}
	}
}
function checkAllForDuplicateServiceType(serviceTypeName){
	var tblrows 	= document.getElementById("pccRulesTable").rows;
	var chkPCCRules = document.getElementsByName("select");
	var chkPCCRuleLength = chkPCCRules.length;
	var pccRuleServiceTypeIds = document.getElementsByName("pccRuleServiceTypeId");	
    
	if(chkPCCRules != null && chkPCCRuleLength > 0){
		for(var i1 = 0; i1 < chkPCCRuleLength; i1++){
			for(var i2 = i1+1; i2 < chkPCCRuleLength; i2++){
				if(chkPCCRules[i1].checked == true && chkPCCRules[i2].checked == true && tblrows[i1+1].style.display != "none"){
						if(pccRuleServiceTypeIds[i1].value == pccRuleServiceTypeIds[i2].value){	
							alert("You cannot select two PCC Rules with Same Service Type");
							return false;
						}
				}
			}
		}
	}
}

function setRowColors(){
	var chkPCCRules = document.getElementsByName("select");
	if(chkPCCRules!=null && chkPCCRules.length>0){
		for(var i1=0; i1<chkPCCRules.length; i1++){
			onOffHighlightedRow(i1,chkPCCRules[i1]);
		}
	}
}

function checkForAlreadyMappedPccRuleServiceType(serviceTypeName){
	
	var tblrows 	= document.getElementById("pccRulesTable").rows;
	var chkPCCRules = document.getElementsByName("select");
	
	var pccRuleServiceTypeIds 		= document.getElementsByName("pccRuleServiceTypeId");
	var mappedPccRuleServiceTypeIds = document.getElementsByName("mappedPccRuleServiceTypeId");	
    
	if(chkPCCRules!=null && chkPCCRules.length>0){
		for(var i=0; i<chkPCCRules.length;i++){			
			if(chkPCCRules[i].checked==true && tblrows[i+1].style.display!="none"){
				for(var x=0;	x<mappedPccRuleServiceTypeIds.length;	x++){
					if(pccRuleServiceTypeIds[i].value==mappedPccRuleServiceTypeIds[x].value){														
							alert("PCC Rule with Service Type '"+serviceTypeName+"' is already added.");
							chkPCCRules[i].checked=false;
							return;
					}
				}
			}
		}
	}
}
function checkAllForAlreadyMappedPccRuleServiceType(serviceTypeName){
	
	var tblrows 	= document.getElementById("pccRulesTable").rows;
	var chkPCCRules = document.getElementsByName("select");
	var chkPCCRuleLength = chkPCCRules.length;
	var pccRuleServiceTypeIds 		= document.getElementsByName("pccRuleServiceTypeId");
	var mappedPccRuleServiceTypeIds = document.getElementsByName("mappedPccRuleServiceTypeId");	
    
	if(chkPCCRules!=null && chkPCCRuleLength>0){
		for(var i=0; i<chkPCCRuleLength;i++){			
			if(chkPCCRules[i].checked == true && tblrows[i+1].style.display!="none"){
				for(var x=0; x<mappedPccRuleServiceTypeIds.length;	x++){
					if(pccRuleServiceTypeIds[i].value == mappedPccRuleServiceTypeIds[x].value){														
							alert("PCC Rule with Same Service Type is already added.");
							return false;
					}
				}
			}
		}
	}
	
}

/*functions for checking validation*/
function isConditionChange(){
	conditionChange = true;
}
function validateCondition() {
	var condition = document.getElementById("condition").value;
	if(conditionChange == true){
		if(condition == null || condition.trim().length<=0){
			$("#condition").removeClass();
			$("#validCond").text('');
			alert("condition is empty");
			return;
		}else{
			condition = encodeURIComponent(condition);
			$.ajax({url:validateUrl,
				type: 'POST',
				timeout: 3000,
				data: 'method=validateCondition&condition='+condition,
				success: function(transport){
					onSuccessValidateCondition(transport);
				},
				error: function(){
					$("#validCond").css('color', 'red');
					$("#validCond").text('Validation request timeout occured');
				}
			}); 
		}
		conditionChange = false; 
	}
}
function onSuccessValidateCondition(data){
	if(data != 'SUCCESS'){
		$("#validCond").css('color', 'red');
		$("#validCond").text(data);
	}else{
		$("#validCond").text('');
	}
}

function validateTimePeriod(obj){
	var moyVal = $("tr[id='" + obj + "'] td:nth-child(1) input[id=moyVal]").val();
	var domVal = $("tr[id='" + obj + "'] td:nth-child(2) input[id=domVal]").val();
	var dowVal = $("tr[id='" + obj + "'] td:nth-child(3) input[id=dowVal]").val();
	var timePeriodVal = $("tr[id='" + obj + "'] td:nth-child(4) input[id=timePeriodVal]").val(); 
	
	if($.trim(moyVal) == '' && $.trim(domVal) == '' && $.trim(dowVal) == '' && $.trim(timePeriodVal) == ''){
		$("tr[id='" + obj + "'] td:nth-child(1)").css("box-shadow","0px 0px 7px #FFFFF");
		$("tr[id='" + obj + "'] td:nth-child(2)").css("box-shadow","0px 0px 7px #FFFFF");
		$("tr[id='" + obj + "'] td:nth-child(3)").css("box-shadow","0px 0px 7px #FFFFF");
		$("tr[id='" + obj + "'] td:nth-child(4)").css("box-shadow","0px 0px 7px #FFFFF");
		$("#noteTD"+obj).text('');
		return;
	}else{
		moyVal = encodeURIComponent(moyVal);
		domVal = encodeURIComponent(domVal);
		dowVal = encodeURIComponent(dowVal);
		timePeriodVal = encodeURIComponent(timePeriodVal);
		
		$.ajax({url:validateUrl,
			type: 'POST',
			timeout: 3000,
			data: 'method=validateTimeSlot&moyVal='+moyVal+'&domVal='+domVal +'&dowVal='+dowVal+'&timePeriodVal='+timePeriodVal,
			success: function(transport){
				onSuccessTimePeriod(transport,obj);
			},
			error: function(){
				$("tr[id='noteTR" + obj + "'] td:first").css("color","red");
				$("#noteTD"+obj).text('Validation request timeout occured');
			}
			
		}); 
	} 
}

function onSuccessTimePeriod(data,obj){
	if(data != 'SUCCESS'){
		$("tr[id='" + obj + "'] td:nth-child(1)").css("box-shadow","0px 0px 7px #FF2400");
		$("tr[id='" + obj + "'] td:nth-child(2)").css("box-shadow","0px 0px 7px #FF2400");
		$("tr[id='" + obj + "'] td:nth-child(3)").css("box-shadow","0px 0px 7px #FF2400");
		$("tr[id='" + obj + "'] td:nth-child(4)").css("box-shadow","0px 0px 7px #FF2400");
		$("tr[id='noteTR" + obj + "'] td:first").css("color","red");
		$("#noteTD"+obj).text(data);
		
	}else{
		$("tr[id='" + obj + "'] td:nth-child(1)").css("box-shadow","0px 0px 7px #FFFFF");
		$("tr[id='" + obj + "'] td:nth-child(2)").css("box-shadow","0px 0px 7px #FFFFF");
		$("tr[id='" + obj + "'] td:nth-child(3)").css("box-shadow","0px 0px 7px #FFFFF");
		$("tr[id='" + obj + "'] td:nth-child(4)").css("box-shadow","0px 0px 7px #FFFFF");
		$("#noteTD"+obj).text('');
	}
}

function addTimePeriodRow() {
	$('#addButton').click(function() {
		var orderNumArray = document.getElementsByName("addTP");
		var currentOrderNumber=0;
		if(orderNumArray!=null && orderNumArray.length>0){
			for(var i=0; i<orderNumArray.length; i++){
				currentOrderNumber = i;	
			}
			currentOrderNumber++;
		}
		var tableRowStr = '<tr name="addTP" id="'+currentOrderNumber+'">'+
								'<td class="tblfirstcol"><input tabindex="8" class="noborder" type="text" name="moyVal" id="moyVal" maxlength="20" size="28" style="width:100%" onchange="validateTimePeriod('+currentOrderNumber+');"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="domVal" id="domVal" maxlength="20" size="28" style="width:100%" onchange="validateTimePeriod('+currentOrderNumber+');"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="dowVal" id="dowVal" maxlength="20" size="28" style="width:100%" onchange="validateTimePeriod('+currentOrderNumber+');"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="timePeriodVal" id="timePeriodVal" maxlength="20" size="30" style="width:100%" onchange="validateTimePeriod('+currentOrderNumber+');"/></td>'+
								'<td class="tblrows" align="center" colspan="3"><img value="top" tabindex="8" src="'+basePath+'/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="removeRow('+currentOrderNumber+')"/></td>'+
							'</tr>'+
							'<tr id="noteTR'+currentOrderNumber+'">'+
								'<td name="noteTP" class="tblfirstcol" colspan="5" id="noteTD'+currentOrderNumber+'" align="center"></td>'+
					   		'</tr>';			
		$(tableRowStr).appendTo('#timePeriodtbl');
	});
	
}
function removeRow(id) {
	$("table#timePeriodtbl tr#"+id).remove(); 
	$("table#timePeriodtbl tr#noteTR"+id).remove();  
}

function checkNullInTimePeriod(){
	var tpflag = false;
	var timePeriodArray = document.getElementsByName("addTP");
	for(var i=0; i<timePeriodArray.length;i++){
		var moy = $("tr[id='" + timePeriodArray[i].id + "'] td:nth-child(1) input[id=moyVal]").val();
		var dom = $("tr[id='" + timePeriodArray[i].id + "'] td:nth-child(2) input[id=domVal]").val();
		var dow = $("tr[id='" + timePeriodArray[i].id + "'] td:nth-child(3) input[id=dowVal]").val();
		var timePeriod = $("tr[id='" + timePeriodArray[i].id + "'] td:nth-child(4) input[id=timePeriodVal]").val();
		if($.trim(moy) == '' && $.trim(dom) == '' && $.trim(dow) == '' && $.trim(timePeriod) == ''){
			tpflag = true;
			return tpflag;
		}
	} 
	return tpflag;
}
function checkTimePeriod(){
	var checkTimePeriodFlag = true;
	 $("td[name=noteTP]:not(:empty)").each(function(){
		var s = $(this).html().trim();
		if(s == 'No Live Server Found'){
			return checkTimePeriodFlag;
		}else{
			checkTimePeriodFlag = false;
		}
	}); 
	return checkTimePeriodFlag;
}
function checkCondition(){
	var checkConditionFlag = true;
	 $("td[name=validCond]:not(:empty)").each(function(){
		var s = $(this).html().trim();
		if(s == 'No Live Server Found'){
			return checkConditionFlag;
		}else{
			checkConditionFlag = false;
		}
	}); 
	 return checkConditionFlag;	 
}
