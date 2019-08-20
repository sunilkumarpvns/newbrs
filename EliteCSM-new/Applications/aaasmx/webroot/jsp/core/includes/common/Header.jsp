
<html>
<head>
<title>EliteCSM - Server Manager</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/font/font-awesome.css"/> 
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/popcalendar.css" >
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.ui.all.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.ui.autocomplete.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.collapsibleCheckboxTree.css" type="text/css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/popcalendar.js"></script>		
<script language="javascript" src="<%=request.getContextPath()%>/js/validation.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/cookie.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script>	
<script src="<%=request.getContextPath()%>/jquery/development/jquery-1.4.2.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/external/jquery.bgiframe-2.1.1.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.effects.core.js"></script> 
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.core.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.widget.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.mouse.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.draggable.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.position.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.resizable.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.dialog.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.effects.blind.js"></script> 
<script src="<%=request.getContextPath() %>/js/jquery.collapsibleCheckboxTree.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.position.js"></script>
<script src="<%=request.getContextPath()%>/jquery/development/ui/jquery.ui.autocomplete.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/viewpage/elitecsm.viewpage.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>
<style>
	.ui-autocomplete {
		max-height: 200px;
		overflow-y: auto;
		max-width: 250px;
	}
	html .ui-autocomplete {
		height: 200px;
		overflow-y: auto;
		max-width: 250px;
	}
	body {
	font-size: 62.5%;
	}	
	
	
	.elite-suggestion {
	    position: absolute;
		z-index: 99999;
	}
	
	
	
</style>

<script language="javascript">
var frameHeader = top.frames["topFrame"].document.getElementById('frameHeader');
$(frameHeader).show(); 

var title="";
function setTitle(titleVal){
	title=titleVal;
	var statusText = document.getElementById('headerTitle');
	$(statusText).empty();
	$(statusText).text(titleVal);
	
	 if(titleVal.length > 22){
		 $(statusText).css({lineHeight:'15px'});
	 }else{
		 $(statusText).css({lineHeight:'28px'});
	 } 
}
function getTitle(){
	return title;
}

function storeDOM()
{
	pagecontent=('<pre><html>' + (document.documentElement.innerHTML) + '</html></pre>');
	
}

setContextPathForImageDir('<%=request.getContextPath()%>');	


function parameterDescription(val,opt) { 		
	   document.getElementById("maindiv").style.visibility = "visible";
		var dailogContent  =  document.getElementById("maindiv");
		dailogContent.innerHTML = '<p class="labeltext">'+ val+'</p>';	   
		 $("#maindiv").dialog({
						modal: false,
						title:opt,
						resizable: false,
						width: 500,
						height: 250,  
	                    open: function () {
	                       
	                    }

	                });
	}

function retriveTableFields(dbId,tableName,txtField) {
	var dbFieldStr;
	var dbFieldArray;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		data=data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		
		$( "#"+ txtField).autocomplete({
			source: dbFieldArray
		});
	});	
}

function setFields(field,columnArray) {
		
		$( "#"+ field).autocomplete({
			source: columnArray
		});
}

function retriveRadiusDictionaryAttributes(searchNameOrAttributeId,txtFields) {
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	$.post("SearchRadiusAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#,");
		var value;
		var label;
		var desc;
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0].trim();
			label = tmpArray[1];
			var item = new ListItem(value,label); 
			myArray.push(item);
		}	
		setRadiusDictionaryFields(txtFields,myArray);
		return dbFieldArray;
	});
}

function retriveDiameterDictionaryAttributes(searchNameOrAttributeId,txtFields) {
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	$.post("SearchDiameterAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#,");
		var value;
		var label;
		var desc;
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0].trim();
			label = tmpArray[1];
			var item = new ListItem(value,label); 
			myArray.push(item);
		}	
		setRadiusDictionaryFields(txtFields,myArray);
		return dbFieldArray;
	});
}


function verifyInstanceName(instanceType,searchName,mode,id,divId) {

		if(searchName!='' && searchName.indexOf("%")< 0){
			var data  = $.ajax({
			   type: "POST",
			   url: "<%=request.getContextPath()%>/VerifyInstanceNameServlet",
			   async:false,
			   data: "instanceType="+instanceType+"&searchName="+searchName+"&mode="+mode+"&id="+id
			}).responseText;

			if(data.trim()=='true') {
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/tick.jpg'/> Valid Name.");
				return true;
			}else if(data.trim()=='false'){
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists.</font>");
				return false;
			}else if(data.trim()=='invalid'){
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Not a valid Name. Valid Characters : A-Z, a-z, 0-9, ., -, _ </font>");
				return false;
			}else{
				var temp = "<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>" +data.trim()+"</font>";
				$('#' + divId).html(temp);
			}
		}else if(searchName.indexOf("%") > -1){
			$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/><font color='#FF0000'>Not a valid Name. Valid Characters : A-Z, a-z, 0-9, ., -, _ </font>");
			return false;
		}else{
			$('#' + divId).html("&nbsp;");
		}
}

function verifyHostIdentityName(instanceType,searchName,mode,id,divId) {

	if(searchName!='' && searchName.indexOf("%")< 0){
		var data  = $.ajax({
		   type: "POST",
		   url: "<%=request.getContextPath()%>/VerifyInstanceNameServlet",
		   async:false,
		   data: "instanceType="+instanceType+"&searchName="+searchName+"&mode="+mode+"&id="+id
		}).responseText;

		if(data.trim()=='true') {
			$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/tick.jpg'/> Valid Host Identity.");
			return true;
		}else if(data.trim()=='false'){
			$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists.</font>");
			return false;
		}else if(data.trim()=='invalid'){
			$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Invalid Host Identity [Name should contain A-Z, a-z, 0-9, -,  _ and . is allowed.].</font>");
			return false;
		}else{
			var temp = "<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>" +data.trim()+"</font>";
			$('#' + divId).html(temp);
		}
	}else if(searchName.indexOf("%") > -1){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Invalid Host Identity [Name should contain A-Z, a-z, 0-9, - and _ is allowed.].</font>");
		return false;
	}else{
		$('#' + divId).html("&nbsp;");
	}
}

function setRadiusDictionaryFields(txtField,myArray) {			
	$("#"+ txtField).bind("keydown",function(event) {
		if (event.keyCode === $.ui.keyCode.TAB
				&& $(this).autocomplete("instance").menu.active) {
			event.preventDefault();
		}
	}).autocomplete(
	{
		minLength : 0,
		source:function( request, response ) {
			response( $.ui.autocomplete.filter(
					myArray, extractLast( request.term ) ) );
		},
		focus : function() {
			return false;
		},
		select : function(event, ui) {
			var terms = split( this.value );
			terms.pop();
			terms.push( ui.item.value );
			this.value = terms.join( "," );
			return false;
		}
	});
}


function ListItem(value,label) {
	this.value = value;
	this.label = label;
}

function split( val ) {
	return val.split( /,\s*/ );
}

function extractLast( term ) {
	return split( term ).pop();
}



function initializeData(listOfId,listOfdrNm,tableId,checkbxId,headerArr,isWeightage,count,startValue,endValue){		
	
	if(startValue==null && endValue==null){
		startValue=0;
		endValue=10;
	}
	
	var table = document.getElementById(tableId);		
	var rowid = table.insertRow(0);
	var tempWeightage;
	
	if(startValue > endValue) {
		tempWeightage = startValue;
		startValue = endValue;
		endValue = tempWeightage;
	}
	
	for(var z=0;z<headerArr.length;z++){
		var cells = rowid.insertCell(z);					
		cells.innerHTML = headerArr[z];			
		cells.className="tblheader-bold";						
	}
	var temp = 1;
	
	for(var i = 0;i<count;i++){
		
		var row = table.insertRow(temp);		
		row.id = "row" + checkbxId + listOfId[i];
		row.style.visibility = 'visible';
            
		    var cell1 = row.insertCell(0);
		    var element1 = document.createElement("input");
		    element1.type = "checkbox";
		    element1.name = checkbxId;
		    element1.id = checkbxId+listOfId[i];		    		    		    
		    element1.value = listOfdrNm[i][0];
		    
		    cell1.appendChild(element1);
		    cell1.className="tblfirstcol";
		    cell1.width="5%";
		    
			
			var cellNumber = 1;
			
				for(var m=0;m<listOfdrNm[i].length;m++){
					var cell = row.insertCell(cellNumber);	
					
					if( cellNumber == 1 ){
						cell.innerHTML = '<span class="view-details-css" onclick="openViewDetails(this,'+listOfId[i]+',\''+ listOfdrNm[i][m] +'\',\''+'DRIVERS\');">' + listOfdrNm[i][m] +"</span>";
					}else{
						cell.innerHTML = listOfdrNm[i][m];
					}
					
					cell.className="tblrows";
					cellNumber++;
				}

			if(isWeightage == 'true'){
				
				var cell3 = row.insertCell(cellNumber);
			    var element2 = document.createElement("select");
			    for(var e=startValue;e<=endValue;e++){
			    	var innerele2 = document.createElement("option");
			    	innerele2.text = e;
			    	innerele2.value = e;
			    	if(e==1){
			    		innerele2.selected="selected";
			    	}
			    	element2.options.add(innerele2, e);
				}
				
				element2.id = checkbxId+listOfId[i]+"w";		    	
				cell3.appendChild(element2);
				cell3.className="tblrows";
				cell3.width="15%";
					
			}
			
			temp++;
	}

   }
String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}
function openpopup(divId,checkBxId,listOfIds,listOfNms,componentId,isWeightage){
	var id = "#" + divId;
	var comid = "#" + componentId;
	
	document.getElementById(divId).style.visibility = "visible";

       <%-- Reset Popup Window--%>	
	   if(isWeightage==null || isWeightage=='true')
		{
	       var jItems=$("*[name='"+checkBxId+"']"); //it will give element by name in jquery
			 for(var i=0;i<jItems.length;i++)
		     {
		         $("#"+checkBxId+listOfIds[i]+"w").val("1");
		     }

	    }

			
	$(id).dialog({
		modal: true,
		autoOpen: false,		
		height: "auto",
		width: 700,
		buttons:{					
			        'Add': function() {
                   
                        var selectedItems=$("*[name='"+checkBxId+"']");
                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
                               
                        		if(isWeightage==null || isWeightage=='true'){	 
									var optionsval =$("#"+checkBxId+listOfIds[0]+"w").val();	
                                	var labelVal=$("#"+checkBxId+listOfIds[0]).val();     
                               		$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0] + "-" + optionsval +" class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
                               		
								}else if(isWeightage=='false'){
									
	                                var labelVal=$("#"+checkBxId+listOfIds[0]).val();   
	                               	$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0]  +" class=labeltext> "+labelVal +" </option>");
								}
                               	$("#row"+checkBxId+listOfIds[0]).hide();
                               	$("#cacherow"+"driverDataRadioId"+listOfIds[0]).hide();	
								selectedItems[0].checked=false;
								         
                        }else if(selectedItems.length>1 ){
	                        for(var i=0;i<selectedItems.length;i++)
	                        {
	                        	if(selectedItems[i].checked == true)
	                            {   	                         
									if(isWeightage==null || isWeightage=='true'){	 
										var optionsval =$("#"+checkBxId+listOfIds[i]+"w").val();								
	                                	var labelVal=$("#"+checkBxId+listOfIds[i]).val(); 
	                               		var validFlag=true;
	                                	if(optionsval == 0){
	                                		$(comid+" option").each(function(){
	                                			var optionVal = $(this).val();
	                                			var optionWeightage = optionVal.substring(optionVal.lastIndexOf("-") + 1);
	                                			if(optionWeightage == 0) {
	                                				validFlag=false;
	                                				var name=comid;
                                      				var index=name.indexOf('#', 0);
                                      				var fullname=name.substring(index+1, index.length);
	                                				alert("In "+fullname.capitalize()+" only one Radius ESI should allowed with Weight-age 0" );
	                                				return false;
	                                			}
	                                		});
	                                	}	
	                                	if(validFlag){
		                               		$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i] + "-" + optionsval +" class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
	                                	} else {
	                                		return ;
	                                	}
									}else if(isWeightage=='false'){
		                                var labelVal=$("#"+checkBxId+listOfIds[i]).val(); 
		                               	$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i]  +" class=labeltext> "+labelVal +" </option>");
									}
	                               	$("#row"+checkBxId+listOfIds[i]).hide();
	                               	$("#cacherow"+"driverDataRadioId"+listOfIds[i]).hide();	
									selectedItems[i].checked=false;         
	                            }                             
	                        }
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
			
		}				
		});
		$(id).dialog("open");
		
 }

function hideSelectedData(componentId,checkboxid){
	var id = "#" + componentId + " " +"option";
	$(id).each(function(){
		 var mainValue = $(this).val();
		 var strippedVal = mainValue.split('-');
	     var rowid="#row"+checkboxid+strippedVal[0];
	     var cacheId="#cacherow"+"driverDataRadioId"+strippedVal[0];
	     $(cacheId).hide();
	     $(rowid).hide();	     	     				      
    });
}

function removeData(componentId,checkboxid){
	 var id = "#" + componentId + " " +"option:selected";	
	 
	$(id).each(function(){
		 var mainValue = $(this).val();
		 var strippedVal = mainValue.split('-');
	     var rowid="#row"+checkboxid+strippedVal[0];
	     var cacheRowId="#cacherow"+"driverDataRadioId"+strippedVal[0];
	     $(rowid).show();
	     $(cacheRowId).show();	     
	     $(this).remove();				      
    });
	
	
 }

function popUpExprSuggestion(mylink){

	if (! window.focus)return true;
	var href;
	if (typeof(mylink) == 'string')
		href=mylink;
	else
		href=mylink.href;
					
	mypopupwindow = window.open(href, "ExpressionBuilder", 'width=900,height=300,left=150,top=100,scrollbars=yes');
	return false;
	
}
function hideMainFrame(){
	 var frameHeader = top.frames["topFrame"].document.getElementById('frameHeader');
	 $(frameHeader).hide();
	 var dashboardInitTime = top.frames["topFrame"].document.getElementById('dashboardInitTime');
	 $(dashboardInitTime).hide();
}

/* focus on the first element */
$(document).ready(function(){
	window.focus();
});

</script>

</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="MM_preloadImages('<%=request.getContextPath()%>/images/csv-hover.jpg','<%=request.getContextPath()%>/images/pdf-hover.jpg','<%=request.getContextPath()%>/images/html-hover.jpg','<%=request.getContextPath()%>/images/filter-hover.jpg','<%=request.getContextPath()%>/images/previous-hover.jpg','<%=request.getContextPath()%>/images/next-hover.jpg','<%=request.getContextPath()%>/images/dnarrow-y.jpg','<%=request.getContextPath()%>/images/sublinks-dnarrow.jpg','<%=request.getContextPath()%>/images/sublinks-uparrow.jpg'); collapseAllRows();" onhelp="openHelpPage();return false;" onBlur="storeDOM()">


 <div id="maindiv" style="display: none" title="header">					
</div>


