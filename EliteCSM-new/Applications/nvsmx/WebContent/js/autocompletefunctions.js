  function split( val ) {
    return val.split( /,\s*/ );
  }
  function extractLast( term ) {
    return split( term ).pop();
  }
 
  
function commonAutoComplete(id,dataArray){
		 
      $( "#"+id).autocomplete({minLength: 0,
        source: function( request, response ) {
          response( $.ui.autocomplete.filter(
        		  dataArray, extractLast( request.term ) ) );
        },		     	       
        focus: function() {
          return false;
        },
        select: function( event, ui ) {
	          var terms = split( this.value );
	          terms.pop();
	          terms.push( ui.item.value );
	          terms.push( "" );		          
	          return true;
	   }
      });
      $('#'+id).dblclick(function() {
    	  $("#"+id).autocomplete( "search", "" );
      });	      	      	     
}
  
  
  
 // 
  function commonAutoCompleteMultiple(id,dataArray){
	    $( "#"+id).autocomplete({minLength: 0,
	        source: function( request, response ) {
	          response( $.ui.autocomplete.filter(
	        		  dataArray, extractLast( request.term ) ) );
	        },
	        focus: function() {
	          return false;
	        },
	        select: function( event, ui ) {
	          var terms = split( this.value );
	          terms.pop();
	          terms.push( ui.item.value );
	          terms.push( "" );
	          this.value = terms.join( ", " );
	          return false;
	        }
	    }); 
	    $('#'+id).dblclick(function() {
	    	$("#"+id).autocomplete( "search", "" );
	    });	      	      	     	    
} 

function commonAutoCompleteMultipleCssClass(cssClass,dataArray){
	    $(cssClass).autocomplete({minLength: 0,
	        source: function( request, response ) {
	          response( $.ui.autocomplete.filter(
	        		  dataArray, extractLast( request.term ) ) );
	        },
	        focus: function() {
	          return false;
	        },
	        select: function( event, ui ) {
	          var terms = split( this.value );
	          terms.pop();
	          terms.push( ui.item.value );
	          terms.push( "" );
	          this.value = terms.join( ", " );
	          return false;
	        }
	    }); 
	    $(cssClass).dblclick(function() {
	    	$(this).autocomplete( "search", "" );
	    });	      	      	     	    
}

  function commonAutoCompleteUsingCssClass(cssClass,dataArray){
      $(cssClass).autocomplete({minLength: 0,
          source: function( request, response ) {
              response( $.ui.autocomplete.filter(
                  dataArray, extractLast( request.term ) ) );
          },
          focus: function() {
              return false;
          },
          select: function( event, ui ) {
              var terms = split( this.value );
              terms.pop();
              terms.push( ui.item.value );
              terms.push( "" );
              return true;
          }
      });
      $(cssClass).dblclick(function() {
          $(this).autocomplete( "search", "" );
      });
  }


  function commonAutoCompleteUsingCssClassBasedOnKeyValue(cssClass, dataArray) {
      $(cssClass).autocomplete({
          minLength: 0,
          source: function (request, response) {
              var list = Object.values(dataArray);
              response($.ui.autocomplete.filter(
                  list, extractLast(request.term)));
          },
          focus: function (event, ui) {
              return false;
          },
          select: function (event, ui) {
              var terms = split(this.value);
              terms.pop();
              terms.push("");
              $.each(dataArray, function (key, value) {
                  if (value == ui.item.value) {
                      ui.item.value = key;
                      return true;
                  }
              });
              return true;
          }
      });
      $(cssClass).dblclick(function () {
          $(this).autocomplete("search", "");
      });
  }

  function commonAutoCompleteBasedOnKeyValueForGivenId(id, dataArray) {
      $("#"+id).autocomplete({
          minLength: 0,
          source: function (request, response) {
              var list = Object.values(dataArray);
              response($.ui.autocomplete.filter(
                  list, extractLast(request.term)));
          },
          focus: function (event, ui) {
              return false;
          },
          select: function (event, ui) {
              var terms = split(this.value);
              terms.pop();
              terms.push("");
              $.each(dataArray, function (key, value) {
                  if (value == ui.item.value) {
                      ui.item.value = key;
                      return true;
                  }
              });
              return true;
          }
      });
      $("#"+id).dblclick(function () {
          $("#"+id).autocomplete("search", "");
      });
  }
