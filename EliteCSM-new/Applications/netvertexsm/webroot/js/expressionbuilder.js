var iscontrolpressed = false;


var textAreaTag;
function setTextAreaTag(tempTextAreaTag){
	textAreaTag = tempTextAreaTag.id;
	
}

var textKeyTag;
function setTextKeyTag(tempTextKeyTag){
	textKeyTag = tempTextKeyTag.id;
	
}

function loadAll(){
	commonAutoCompleteSpace(textAreaTag  ,document.getElementById(textKeyTag));
	 
	
	$("#" + textAreaTag).keydown( function(e) {
		
		if(e.which == 17){
			iscontrolpressed = true;
		}
		
	    if (e.which == 32 && iscontrolpressed) {
	    	var lastChar = getValueBeforeCursour(document.getElementById(textAreaTag).value).substr(document.getElementById(textAreaTag).value.length - 1);
	    	if(lastChar == ' '){
	    		commonAutoCompleteSpace(textAreaTag  ,document.getElementById(textKeyTag));
	    		$("#"+textAreaTag).autocomplete( "search", "" );
	    	}else{
	    		commonAutoCompleteSpace(textAreaTag  ,document.getElementById(textKeyTag));
	    		$("#"+textAreaTag).autocomplete( "search", getValueBeforeCursour(document.getElementById(textAreaTag).value) );
	    	}
	    	return false;
	    } else if(e.which == 32) {
	    	commonAutoCompleteSpace(textAreaTag  ,null);
	    	return true;
		}
	    
	    return true;
	});
	
	$("#" + textAreaTag).keyup( function(e) {
		if(e.which == 17){
			iscontrolpressed = false;
		}
	});

}

var suggestionTag;
function setSuggestionTag(tempSuggestionTag){
	suggestionTag = tempSuggestionTag.id;
}

function getElementFromLast(value ){
	var array = value.split( /[\s,]+/ );
	if(array.length > 0){
		return array[array.length - 1];
	}
	return null;
}