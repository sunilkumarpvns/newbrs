var cursorpos;  
var iscontrolpressed=false;
function splitspace( val ) {
      return val.split(' ');
    }
    
    function extractLastspace(term,cssclass ){
    	  $(cssclass).keydown(function () {
              cursorpos = $(this).getCursorPosition();
          });
          var value = term.substring(0, cursorpos);
          return splitspace(value).pop();
    }
  
     
 function pccruleautocomplete(cssClass,dataArray){
	    // don't navigate away from the field on tab when selecting an item
	 $(cssClass).bind("keydown", function (event) {
	        if (event.keyCode === $.ui.keyCode.TAB && $(this).data("ui-autocomplete").menu.active && event.keyCode === $.ui.keyCode.DOWN) {
	            event.preventDefault();
	        }
	    }).autocomplete({
	        minLength: 0,
	        source: function (request, response) {
	            // delegate back to autocomplete, but extract the last term
	            response($.ui.autocomplete.filter(
	            		dataArray, extractLastspace(request.term,cssClass)));
	        },
	        focus: function () {
	            // prevent value inserted on focus
	            return false;
	        },
	        select: function (event, ui) {
	            var cursorpos = $(this).getCursorPosition();
	            var afterCursor = this.value.substring(cursorpos, this.value.length);
	            var terms = splitspace(this.value.substring(0,cursorpos));

	            // remove the current input
	            terms.pop();
	            // add the selected item
	            terms.push(ui.item.value);
	            // add placeholder to get the comma-and-space at the end
	            terms.push("");
	            var updateString=terms.join(" ");
	            this.value = updateString+afterCursor;
	            $(this).setCursorPosition(updateString.length);
	            return false;
	        }
	    });
		 $(cssClass).keydown(function(e) {
			 if(e.which === 17){
				 iscontrolpressed = true;
			 }
			 if (e.which === 32 && iscontrolpressed) {
				 $(this).autocomplete( "search", "" );
			 }
		 });	 
		 $(cssClass).keyup( function(e) {
				if(e.which === 17){
					iscontrolpressed = false;
				}
			});
 }

 
 
 $.fn.getCursorPosition = function () {
	    var input = this.get(0);
	    if (!input) return; // No (input) element found
	    if ('selectionStart' in input) {
	        // Standard-compliant browsers
	        return input.selectionStart;
	    } else if (document.selection) {
	        // IE
	        input.focus();
	        var sel = document.selection.createRange();
	        var selLen = document.selection.createRange().text.length;
	        sel.moveStart('character', -input.value.length);
	        return sel.text.length - selLen;
	    }
	};
	$.fn.setCursorPosition = function(position){
	    if(this.length == 0) return this;
	    return $(this).setSelection(position, position);
	};

	$.fn.setSelection = function(selectionStart, selectionEnd) {
	    if(this.length == 0) return this;
	    input = this[0];

	    if (input.createTextRange) {
	        var range = input.createTextRange();
	        range.collapse(true);
	        range.moveEnd('character', selectionEnd);
	        range.moveStart('character', selectionStart);
	        range.select();
	    } else if (input.setSelectionRange) {
	        input.focus();
	        input.setSelectionRange(selectionStart, selectionEnd);
	    }

	    return this;
	};

	$.fn.focusEnd = function(){
	    this.setCursorPosition(this.val().length);
	            return this;
	};
	
function callvalidatePCCRuleMapping(){
	$("tr[name=noteTR]").each(function(){
			$(this).remove();
	});
	validatePCCRuleMapping("staticMapping");
	validatePCCRuleMapping("dynamicMapping");
}
function validatePCCRuleMapping(name) {
	$("tr[name='"+name+"']").each(function(){
		var attributeS = encodeURIComponent($(this).find("#attribute").val());
		var policyKeyS = encodeURIComponent($(this).find("#policyKey").val());
		var defaultValueS = encodeURIComponent($(this).find("#defaultValue").val());
		var valueMappingS = encodeURIComponent($(this).find("#valueMapping").val());
		var conversationType="";
		var temp = $(this);
		$.ajax({url:validateUrl,
			type: 'POST',
			async: false,
			data: 'method=validatePacketMapping&policyKey='+policyKeyS+'&attribute='+attributeS +'&defaultValue='+defaultValueS+'&valueMapping='+valueMappingS+'&conversationType='+conversationType,
			success: function(transport){
				if(transport != 'SUCCESS'){
					$(temp).children().css("box-shadow","0px 0px 7px #FF2400");
					var noteRow ='<tr name="noteTR"><td class="tblfirstcol" colspan="5" align="center"></td></tr>';
					$(noteRow).insertAfter(temp);
					$(temp).closest('tr').next().children().css("color","red");
					$(temp).closest('tr').next().children().text(transport);
				}else{
					$(temp).children().css("box-shadow","none");
				}	
			}
		});
	});
}

