/** This function  is used for expand textbox while editing 
 * 	@param textbox denotes current textbox object 
 * */
function expand(textbox) {
    if (!textbox.startW) { textbox.startW = textbox.offsetWidth; }
   
    var style = textbox.style;
    style.width = 0;
    
    var desiredW = textbox.scrollWidth;
    desiredW += textbox.offsetHeight;
    
    style.width = Math.max(desiredW, textbox.startW) + 'px';
}

/** This function is used changing handler name
 *  @param obj  denotes current textbox object  */
function changeHandlerName(obj){
	var handlerNameTextbox = $(obj).parent().parent().find(".handler-name-txt");
	handlerNameTextbox.attr( "disabled", false );
	$(handlerNameTextbox).css({'border':'1px solid #CCC','background-color':'white'});
	
	var isHandlerisNew = $( handlerNameTextbox ).hasClass( "new-handler" );
	if(isHandlerisNew){
		$(handlerNameTextbox).css({'color':'black'});
	}
	
	$(obj).parent().parent().find(".save_handler_icon").css({"display":"inline"});
	$(obj).css({"display":"none"});
}

/** This function is used for changing handler name
 *  @param obj  denotes current textbox object
 * */
function saveHandlerName(obj){
	var handlerNameTextbox = $(obj).parent().parent().find(".handler-name-txt");
	if( handlerNameTextbox.val() <= 0 ){
		alert('Please Enter Handler Name');
		handlerNameTextbox.focus();
	}else{
		
		var currentVal = handlerNameTextbox.val();
		currentVal=currentVal.trim();
		var properFlowFound = true;
		var attribId = handlerNameTextbox.attr('id');
		
		
		$('.handler-name-txt').each(function(){
			if($(this).attr('id') != attribId ){
				if($(this).val() == currentVal){
					alert('System will not allow duplicate Handler name not allowed. Kindly change it');
					handlerNameTextbox.focus();
					properFlowFound = false;
					return false;
				}
			}
		});
		
		if(properFlowFound){
			var oldValue = getOldValue(handlerNameTextbox);
			var newVal = $(handlerNameTextbox).val();
			if(oldValue.trim() != newVal.trim()){
				$("#confirmDialog").data('obj',handlerNameTextbox).dialog("open");
			}else{
				disableTextBox(handlerNameTextbox);
			}
		}
	}
}

/** This function is used for on key pressing it will save data
 *  @param e denotes  javascript event
 *  @param obj  denotes current textbox object
 *  @return boolean for condition satisfied or not
 *  */
function keyPressedForHandler(e,obj){
	if(e.keyCode === 27){
		setOldValue(obj);
		disableTextBox(obj);
	}
	if(e.keyCode === 13){
		if($(obj).val().length <= 0){
			alert('Please Enter Handler Name');
			$(obj).focus();
		}else{
			var currentVal = $(obj).val();
			currentVal=currentVal.trim();
			var properFlowFound = true;
			var attribId = $(obj).attr('id');
			
			$('.handler-name-txt').each(function(){
				if($(this).attr('id') != attribId ){
					if($(this).val() == currentVal){
						alert('System will not allow duplicate Handler name not allowed. Kindly change it');
						$(obj).focus();
						properFlowFound = false;
						return false;
					}
				}
			});
			
			if ( properFlowFound ){
				var oldValue = getOldValue(obj);
				var newVal = $(obj).val();
				if(oldValue.trim() != newVal.trim()){
					$(obj).focus();
					$("#confirmDialog").data('obj',obj).dialog("open");
				}else{
					disableTextBox(obj);
				}
			}
			
			return false;
		}
    }
}

/** Checking duplicate from array
 *  @param A denotes array 
 *  @return Boolean return true if duplicate found else false */
function arrHasDupes( A ) {                         
	var i, j, n;
	n=A.length;
                                                     
	for (i=0; i<n; i++) {                      
		for (j=i+1; j<n; j++) {              
			if (A[i]==A[j]) return true;
	}	}
	return false;
}

/**
 * Set old value of the handler
 * @param obj of Text box
 */
function setOldValue(obj) {
	var oldVal = getOldValue(obj);
	$(obj).attr('disabled', true);
	
	$(obj).val(oldVal);
}


function getOldValue(obj) {
	return oldVal = $(obj).parent().find('.hidden-handler-name').val();
}



/**
 * This function disable text box for further editing
 * @param obj Text box that you want to disable
 */
function disableTextBox(obj){
	$(obj).parent().parent().find(".edit_handler_icon").css({"display":"inline"});
	$(obj).parent().parent().find(".save_handler_icon").css({"display":"none"});
	$(obj).css({'border':'none','background-color':'transparent','font-weight':'bold'});
	
	var isHandlerisNew = $( obj ).hasClass( "new-handler" );
	if(isHandlerisNew){
		$(obj).css({'color':'white'});
	}
	$(obj).attr( "disabled", true );
}

function loadConfirmDialog() {
	$("#confirmDialog").dialog({
		autoOpen: false,
		modal: true,
		position : 'top',
		open: function( event, ui ) {
			$('button').removeClass("ui-state-focus");
			var obj = $("#confirmDialog").data('obj');
			var oldValue = getOldValue(obj);
			var newVal = $(obj).val();
			$('#oldHandlerName').text(oldValue);
			$('#newHandlerName').text(newVal);
		},
		close : function (){
			var obj = $("#confirmDialog").data('obj');
			setOldValue(obj);
			disableTextBox(obj);
		},
		buttons : {
			"Keep it" : function() {
				var obj = $("#confirmDialog").data('obj');
				var newVal = $(obj).val();
				$(obj).parent().find('.hidden-handler-name').val(newVal);
				$(this).dialog("close");
				disableTextBox(obj);
			},
			"Discard" : function() {
				var obj = $("#confirmDialog").data('obj');
				setOldValue(obj);
				$(this).dialog("close");
				disableTextBox(obj);
			}
		}
	});
}

function validateHandlerName(){
	
	var handlerNameArray = [];
	$('.handler-name-txt').each(function(){
		handlerNameArray.push($(this).val());
	});
	var isDuplicatehandlerName = arrHasDupes(handlerNameArray);

    if( isDuplicatehandlerName ){
		alert('Duplicate handler name found. Kindly verify all Handlers name.');
		return false;
	}
	
	var savedFlag = true;
	$('.handler-name-txt').each(function(){
		if($(this).attr("disabled") == false || typeof $(this).attr("disabled") == 'undefined'){
			var oldValue = getOldValue(this);
			var newVal = $(this).val();
			if(oldValue.trim() != newVal.trim()){
				$(this).focus();
				$("#confirmDialog").data('obj',this).dialog("open");
				savedFlag = false;
				return false;
			}else{
				disableTextBox(this);
			}
		}
	});
	
	return savedFlag;
}

function addNewHandler(handlerName,handlerTable,isAuth,isForView){
	var htmlcontent = '';
	htmlcontent=getNewHandlerData(handlerName,handlerTable);
	var newRowId=$("#"+handlerTable+" > tbody > tr").length;
	newRowId++;
	$('#'+handlerTable).append('<tr id='+newRowId+'><td class="handler-css"> '+htmlcontent+'</td></tr>');
	
	if(isAuth){
		retriveRadiusDictionaryAttributesForUsernameResponse();
	}
	
	if(isForView == false){
		var textboxObj = $('#'+handlerTable + "> tbody > tr:last-child ").find('.handler-name-txt');
		var textboxVal = $(textboxObj).val();
		
		var handlerCounter = 0;
		var handlerNumbers = [];
		$("input[name='handlerType']").each( function() {
			if($(this).val() == handlerName){
				var name = $(this).parent().find("input[name='hiddenHandlerName']").val();
				
				var startIndex = name.lastIndexOf("(");
				var endIndex = name.lastIndexOf(")");
				
				if(startIndex > -1 && endIndex > -1 && startIndex+1 != endIndex){
					var number = name.substring(startIndex+1,endIndex);
	
					if(isNaN(number) == false){
						handlerNumbers.push(parseInt(number));
					} else {
						handlerNumbers.push(1);
					}
				} else if(name == textboxVal){
					handlerNumbers.push(1);
				}
			}
		});
		
		if(handlerNumbers.length == 1){
			handlerCounter = 1;
		}else{
			var maxNumber = Math.max.apply(Math, handlerNumbers);
			handlerCounter = ++maxNumber;
		}
		
		if( handlerCounter > 1 ){
			var hiddenObj = $('#'+handlerTable + " > tbody > tr:last-child ").find('.hidden-handler-name');
			var newValue = textboxVal + "("+handlerCounter+")";
			
			$(textboxObj).empty();
			$(hiddenObj).empty();
			
			$(textboxObj).val(newValue);
			$(hiddenObj).val(newValue);
			
			$(textboxObj).attr('size',newValue.length);
		}
		
		/* This code is commented because the transition was not working properly when we add a post handler.*/
		/*$('html, body').animate({
			scrollTop: $("tr#"+newRowId).offset().top
		}, 500);*/
		
		setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	}
}
function changeValueOfFlow(obj){
	if( $(obj).val() == 'true'){
		$(obj).val('false');
		var handlerObject=$(obj).closest('table[class^="handler-class"]');
		var flag = false;
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-toggle-class');
		});
	}else{
		var handlerEnabledFlag = false;
		var formObj = $(obj).closest('form');
		if($(formObj).hasClass("form_auth")){
			$(obj).closest('table.authenticationTbl').find('.form_auth').each(function (){
			if($(this).find("input[name='isHandlerEnabled']").val() == "true"){
					handlerEnabledFlag = true;
					return false;
				}
			});
		}
		if(handlerEnabledFlag){
			$(obj).attr('checked', false);
			$(obj).val('false');
			alert("You cannot have multiple enabled authentication handler");
			return false;
		}else{
			$(obj).attr('checked', true);
			$(obj).val('true');
			var handlerObject=$(obj).closest('table[class^="handler-class"]');
			$(handlerObject).find('tr').each(function(){
				$(this).removeClass('disable-class');
			});
		}
	}
}

