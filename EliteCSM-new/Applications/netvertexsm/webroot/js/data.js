var policyKey = ['PCCRule.Name','PCCRule.Precedence','PCCRule.ChargingKey','PCCRule.MonitoringKey',
    'PCCRule.SponsorID','PCCRule.AppServiceProviderID','PCCRule.GatewayStatus',
    'PCCRule.ServiceName','PCCRule.MBRDL','PCCRule.UsageMetering','PCCRule.QCI',
    'PCCRule.BearerID','PCCRule.GBRDL','PCCRule.GBRUL','PCCRule.ServiceDataFlow',
    'PCCRule.MBRUL','PCCRule.AAMBRDL','PCCRule.AAMBRUL','AccessNetwork',
    'DeviceType','SericeType','PackageName','SessionID','FramedIPAddress',
    'GatewayURL','LocationID','UsedInputOctets','UsedOutputOctets',
    'UsedTotalOctets','UsedTime','GrantedInputOctets','GrantedOutputOctets',
    'GrantedTotalOctets','GrantedTime'];

var deviceType = ['Mobile-Phone','Laptop','Desktop','Tablet'];

var accessNetwork = ['WiFi','LTE','WLAN','GERAN'];

var serviceType = ['Default Service','E-Mail','P2P','HTTP','FTP','Yahoo Messenger Video',
    'Google Talk','VOD','IPTV','StaticIP'];
var vendorIdArray = [         
            'Ericsson - 5' ,
            'Cisco - 9' ,
           	'3Com - 43' ,
           	'Merit - 61' ,
           	'Nokia - 94' ,
           	'Shiva - 166' ,
           	'Ericsson-ViG - 193' ,
           	'Cisco-VPN5000 - 255' ,
           	'Livingston - 307' ,
           	'Microsoft - 311' ,
           	'USR - 429' ,
           	'Ascend - 529' ,
           	'Alcatel-1430 - 637' ,
           	'Alcatel - 800' ,
           	'Xedia - 838' ,
           	'Alltel - 1049' ,
           	'Funk - 1411' ,
           	'CyberGuard - 1457' ,
           	'Bay - 1584' ,
           	'Orinoco - 1751' ,
           	'Foundry - 1991' ,
           	'Packeteer - 2334' ,
           	'Redback - 2352' ,
           	'Juniper - 2636' ,
           	'Nortel-CVX - 2637' ,
           	'Cisco-VPN3000 - 3076' ,
           	'Cosine - 3085' ,
           	'Nortel-Shasta - 3199' ,
           	'Nomadix - 3309' ,
           	'SpringTide - 3551' ,
           	'DSL - 3561' ,
           	'Lucent-VID - 3729' ,
           	'Lucent - 4846' ,
           	'Juniper-Unisphere - 4874' ,
           	'Cisco-BBSM - 5263' ,
           	'3GPP2 - 5535' ,
           	'Riverstone - 5567' ,
           	'Alcatel-Lucent-IPD - 6527' ,
           	'ALU-8610 - 7483' ,
           	'Azaire - 7751' ,
           	'SN1 - 8164' ,
           	'Colubris - 8744' ,
           	'Acme - 9148' ,
           	'3GPP - 10415' ,
           	'VzW - 12951' ,
           	'WISPr - 14122' ,
           	'Cisco-Airespace - 14179' ,
           	'Syniverse - 14369' ,
           	'Trapeze - 14525' ,
           	'Aruba - 14823' ,
           	'Lucent-VID-Access-Group - 19487' ,
           	'WiMAX - 24757'
];
var vendorId = [
        { name: "Ericsson", to: "5" },
    	{ name: "Cisco", to: "9" },
    	{ name: "3Com", to: "43" },
    	{ name: "Merit", to: "61" },
    	{ name: "Nokia", to: "94" },
    	{ name: "Shiva", to: "166" },
    	{ name: "Ericsson-ViG", to: "193" },
    	{ name: "Cisco-VPN5000", to: "255" },
    	{ name: "Livingston", to: "307" },
    	{ name: "Microsoft", to: "311" },
    	{ name: "USR", to: "429" },
    	{ name: "Ascend", to: "529" },
    	{ name: "Alcatel-1430", to: "637" },
    	{ name: "Alcatel", to: "800" },
    	{ name: "Xedia", to: "838" },
    	{ name: "Alltel", to: "1049" },
    	{ name: "Funk", to: "1411" },
    	{ name: "CyberGuard", to: "1457" },
    	{ name: "Bay", to: "1584" },
    	{ name: "Orinoco", to: "1751" },
    	{ name: "Foundry", to: "1991" },
    	{ name: "Packeteer", to: "2334" },
    	{ name: "Redback", to: "2352" },
    	{ name: "Juniper", to: "2636" },
    	{ name: "Nortel-CVX", to: "2637" },
    	{ name: "Cisco-VPN3000", to: "3076" },
    	{ name: "Cosine", to: "3085" },
    	{ name: "Nortel-Shasta", to: "3199" },
    	{ name: "Nomadix", to: "3309" },
    	{ name: "SpringTide", to: "3551" },
    	{ name: "DSL", to: "3561" },
    	{ name: "Lucent-VID", to: "3729" },
    	{ name: "Lucent", to: "4846" },
    	{ name: "Juniper-Unisphere", to: "4874" },
    	{ name: "Cisco-BBSM", to: "5263" },
    	{ name: "3GPP2", to: "5535" },
    	{ name: "Riverstone", to: "5567" },
    	{ name: "Alcatel-Lucent-IPD", to: "6527" },
    	{ name: "ALU-8610", to: "7483" },
    	{ name: "Azaire", to: "7751" },
    	{ name: "SN1", to: "8164" },
    	{ name: "Colubris", to: "8744" },
    	{ name: "Acme", to: "9148" },
    	{ name: "3GPP", to: "10415" },
    	{ name: "VzW", to: "12951" },
    	{ name: "WISPr", to: "14122" },
    	{ name: "Cisco-Airespace", to: "14179" },
    	{ name: "Syniverse", to: "14369" },
    	{ name: "Trapeze", to: "14525" },
    	{ name: "Aruba", to: "14823" },
    	{ name: "Lucent-VID-Access-Group", to: "19487" },
    	{ name: "WiMAX", to: "24757" }
   ];

function value(len) {
	var val = document.getElementById("ruleSet").value;
	if(val=="Device-Type") {
		while(len != 0) {
			$("#test"+len).autocomplete(deviceType, {
				minChars: 0,
				max: 100
			});
			len = len - 1;
		}
	}
	if(val=="Access-Network") {
		while(len != 0) {
			$("#test"+len).autocomplete(accessNetwork, {
				minChars: 0,
				max: 100
			});
			len = len - 1;
		}
	}
	if(val=="Serice-Type") {
		while(len != 0) {
			$("#test"+len).autocomplete(serviceType, {
				minChars: 0,
				max: 100
			});
			len = len - 1;
		}
	}
}

 
function supportedVendorList() {
	var vendorStr="";
	for(var i=0; i<vendorIdArray.length; i++){
		if(i!=vendorIdArray.length-1){
			vendorStr+=jQuery.trim(vendorIdArray[i])+",";
		}else{
			vendorStr+=jQuery.trim(vendorIdArray[i]);
		}
	}	
	var idArray = new Array();		
	idArray = vendorStr.split(",");

	commonAutoCompleteMultiple("supportedVendorList",idArray);
	
	/*$("#supportedVendorList").autocomplete(vendorId, {
		minChars: 0,
		width: 220,
		max: 100,
		matchContains: "word",
		autoFill: false,
		multiple: true,
		mustMatch: true,
		formatItem: function(row, i, max) {
			return row.name + " - " + row.to;
		},
		formatMatch: function(row, i, max) {
			return row.name + " " + row.to;
		},
		formatResult: function(row) {
			return row.to;
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
	});*/
}

	function accessNetworkList(an) {
		commonAutoComplete("accessNetworkName",an);
 	}

	function deviceTypeList(dt) {
		commonAutoComplete("deviceName",dt);
 	}

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
  
  function commonAutoCompleteMultiple(id,dataArray){
	  var maxlength = $("#"+id).attr("maxlength");
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
	          if(maxlength != 'undefined'){
		       	  if(this.value.length >= maxlength){
		       		  var lastSelectedDataLen = this.value.length - (ui.item.value.length +2);
		          	  var temp = this.value.slice(0,lastSelectedDataLen);
		          	  if($('#errorMsgDiv_'+id) !=  'undefined'){
		          		  $('#errorMsgDiv_'+id).html("<img src='images/cross.jpg'/> <font color='#FF0000'>Max Length for field is reached.</font>");
		          	  }
		          	  this.value = temp;
		          	  return false;
		       	  }else{
		       		  if($('#errorMsgDiv_'+id) !=  'undefined'){
		          		  $('#errorMsgDiv_'+id).html("");
		          	  }
		       	  }
		        }
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
  
    function getCursourPoisition(){
		var control = document.getElementById(textAreaTag);
	    var endPos = control.selectionEnd;
	    return endPos;
	}
    
    function replaceAll(originalStr, removeStr, replaceStr){
      var newStr = originalStr.replace(removeStr,replaceStr);
      var tempStr = originalStr;
      while(newStr != tempStr){
    	  newStr = tempStr;
    	  tempStr = newStr.replace(removeStr,replaceStr);
      }
  	  
  	  return newStr;
    }
    
    function getValueBeforeCursour(value){
    	value = replaceAll(value , '(' , ' ' );
    	value = replaceAll(value , ')' , ' ' );
    	var position = getCursourPoisition();
    	if(position > 0){
    		value = value.substring(0,position);
    	}

    	return extractLastBySpace(value);
    }
	
  function splitBySpace( val ) {
	    return val.split( /[\s,]+/ );
  }
  function extractLastBySpace( term ) {
	  var text = splitBySpace( term ).pop();
	   return text;
  }
	  
  function commonAutoCompleteSpace(id,dataArray){

	  $( "#"+id).autocomplete({minLength: 0,
		  delay: 0,
		  source: function( request, response ) {
			  response( $.ui.autocomplete.filter(
					  dataArray, getValueBeforeCursour( request.term ) ) );
		  },
		  focus: function() {
			  return false;
		  },
		  select: function( event, ui ) {

			  var stringBeforeCursour;
			  var position = getCursourPoisition();
			  if(position > 0){
				  stringBeforeCursour = this.value.substring(0 , position);
			  }else{
				  stringBeforeCursour = "";
			  }

			  var valueSearched = getValueBeforeCursour(this.value);
			  var indices = getIndicesOf(valueSearched, stringBeforeCursour, false);
			
			  var index = this.value.length - 1 ;
			  if(indices != null && indices.length > 0){
				  index = indices.pop();
			  }

			  var cursourPoisiton = getCursourPoisition();
			  var resultString = this.value;
				var stringAfterCursour = resultString.substring(cursourPoisiton);
			  if(cursourPoisiton == (this.value.length)){
					stringAfterCursour = " ";
			  }
			  
			  resultString = resultString.substring(0 , index);
			  var updatedCursourPoisition = resultString.length + ui.item.value.length;
			  resultString = resultString + ui.item.value + stringAfterCursour;
			  this.value = resultString;
			  setCaretPosition(id , updatedCursourPoisition);
			  dataArray = null;
			  return false;
		  }
	  }); 
  }
  
  function setCaretPosition(elemId, caretPos) {
	  var elem = document.getElementById(elemId);

	  if(elem != null) {
		  if(elem.createTextRange) {
			  var range = elem.createTextRange();
			  range.move('character', caretPos);
			  range.select();
		  }
		  else {
			  if(elem.selectionStart) {
				  elem.focus();
				  elem.setSelectionRange(caretPos, caretPos);
			  }
			  else{
				  elem.focus();
			  }
		  }
	  }
  }
  
  function getIndicesOf(searchStr, str, caseSensitive) {
	  var startIndex = 0, searchStrLen = searchStr.length;
	  var index, indices = [];
	  if (!caseSensitive) {
		  str = str.toLowerCase();
		  searchStr = searchStr.toLowerCase();
	  }
	  
	  if(searchStr.trim().length == 0){
		  indices.push(str.length);
		  return indices;
	  }
	  
	  while ((index = str.indexOf(searchStr, startIndex)) > -1) {
		  indices.push(index);
		  startIndex = index + searchStrLen;
	  }
	  return indices;
  }
  
