



var dictionaryType;
var jsVars;
function setExpressionData(dicType){
	dictionaryType=dicType;
	jsVars = {dictionaryType:dictionaryType}
	//createDivision();	
	createHandler();
}

function popupExpression(id) {	
	
$.fx.speeds._default = 1000;
document.getElementById("popupExpr").style.visibility = "visible";		
$( "#popupExpr" ).dialog({
	modal: true,
	autoOpen: false,		
	height: "auto",
	width: 850,		
	buttons:{					
      'Add': function() {
	           var val=document.getElementsByName("gwttextbox");					
	           if(val.length>0){
	           	$("#"+id).val(val[0].value);
	           	
	           }
	           $(this).dialog('close');    
      },                
	    Cancel: function() {
      	$(this).dialog('close');
  	}
  },
	open: function() {
  	
	},
	close: function() {
		var val=document.getElementsByName("gwttextbox");
		val[0].value="";
	}				
});
$( "#popupExpr" ).dialog("open");


}
/*    
 *    <div id="popupExpr" style="display: none;" title="ExpressionBuilder"> 
 *	   		<div id="expBuilderId" align="center" ></div>
 *    </div>
 *   
 *     
 */
function createDivision(){
	
	
	var parentdivTag = document.createElement("div");
    parentdivTag.id = "popupExpr";
    parentdivTag.style.display = "none";
    parentdivTag.title ="Expression Builder";
    
    var childdivTag=document.createElement("div");
    childdivTag.id="expBuilderId";
    childdivTag.setAttribute("align","center");
    
    document.body.appendChild(parentdivTag);
    document.getElementById("popupExpr").appendChild(childdivTag);
    
}
 
 function createHandler(){
		$("#popupExpr").bind('keydown', function(e) { 
		    if (e.which == 27) {
		    	 return false;
		    }
		});
	}
 
 
 